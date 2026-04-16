package com.waxeados.CineWax.services;

import com.waxeados.CineWax.dto.PeliculaDTO;
import com.waxeados.CineWax.entity.*;
import com.waxeados.CineWax.respositories.*;
import com.waxeados.CineWax.structures.ListaEnlazada;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio principal de peliculas
 * Usa ListaEnlazada para almacenamiento en memoria y busquedas recursivas
 */
@Service
@RequiredArgsConstructor
public class PeliculaService {

    private final PeliculaRepository peliculaRepository;
    private final GeneroRepository generoRepository;
    private final HorarioCarteleraRepository horarioRepository;

    // ALTA

    /**
     * Alta de película (Administrador opcion 1)
     * Valida que no exista otra con el mismo nombre
     */
    @Transactional
    public Pelicula altaPelicula(PeliculaDTO dto) {
        if (peliculaRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new IllegalArgumentException("** YA EXISTE PELICULA CON EL NOMBRE DE: " + dto.getNombre() + " **");
        }

        Genero genero = generoRepository.findById(dto.getIdGenero())
                .orElseThrow(() -> new IllegalArgumentException("** GENERO NO ENCONTRADO CON ID: " + dto.getIdGenero() + " **"));

        Pelicula pelicula = Pelicula.builder()
                .nombre(dto.getNombre())
                .director(dto.getDirector())
                .productor(dto.getProductor())
                .clasificacion(dto.getClasificacion().toUpperCase())
                .duracionMin(dto.getDuracionMin())
                .genero(genero)
                .build();

        return peliculaRepository.save(pelicula);
    }

    // BAJA

    /**
     * Baja de pelicula (Administrador opcion 3)
     * Elimina la pelicula y todos sus horarios asociados
     */
    @Transactional
    public void bajaPelicula(Integer idPelicula) {
        Pelicula pelicula = peliculaRepository.findById(idPelicula)
                .orElseThrow(() -> new IllegalArgumentException("** PELICULA NO ENCONTRADA CON ID: " + idPelicula + " **"));

        // Eliminar horarios asociados primero
        horarioRepository.deleteByPelicula_IdPelicula(idPelicula);
        peliculaRepository.delete(pelicula);
    }

    // MODIFICAR

    /**
     * Modificar pelicula (Administrador opcion 5).
     * Se pueden modificar todos los campos excepto los horarios.
     */
    @Transactional
    public Pelicula modificarPelicula(Integer idPelicula, PeliculaDTO dto) {
        Pelicula pelicula = peliculaRepository.findById(idPelicula)
                .orElseThrow(() -> new IllegalArgumentException("** PELICULA NO ENCONTRADA CON ID: " + idPelicula + " **"));

        // Verificar que el nuevo nombre no colisione con otra pelicula
        if (!pelicula.getNombre().equalsIgnoreCase(dto.getNombre())
                && peliculaRepository.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new IllegalArgumentException("** YA EXISTE UNA PELICULA CON EL NOMBRE: " + dto.getNombre()+ " **");
        }

        Genero genero = generoRepository.findById(dto.getIdGenero())
                .orElseThrow(() -> new IllegalArgumentException("** GENERO NO ENCONTRADO CON ID: " + dto.getIdGenero()+ " **"));

        pelicula.setNombre(dto.getNombre());
        pelicula.setDirector(dto.getDirector());
        pelicula.setProductor(dto.getProductor());
        pelicula.setClasificacion(dto.getClasificacion().toUpperCase());
        pelicula.setDuracionMin(dto.getDuracionMin());
        pelicula.setGenero(genero);

        return peliculaRepository.save(pelicula);
    }

    // CONSULTAR

    /**
     * Consultar pelicula (Administrador opcion 6 / Cliente opcion 5).
     */
    public Pelicula consultarPelicula(Integer idPelicula) {
        return peliculaRepository.findById(idPelicula)
                .orElseThrow(() -> new IllegalArgumentException("** PELICULA NO ENCONTRADA CON ID: " + idPelicula + " **"));
    }

    /**
     * Listar todas las peliculas
     */
    public List<Pelicula> listarPeliculas() {
        return peliculaRepository.findAll();
    }

    // BUSQUEDAS (Cliente)

    /**
     * Buscar pelicula por nombre (Cliente opcion 1)
     * Usa ListaEnlazada con busqueda recursiva
     */
    public List<Pelicula> buscarPorNombre(String nombre) {
        List<Pelicula> todas = peliculaRepository.findAll();

        // Cargar en lista enlazada para usar busqueda recursiva
        ListaEnlazada<Pelicula> lista = new ListaEnlazada<>();
        todas.forEach(lista::agregarFinal);

        // Filtrar usando la lista enlazada
        ListaEnlazada<Pelicula> resultado = lista.filtrar(
                p -> p.getNombre().toLowerCase().contains(nombre.toLowerCase())
        );

        List<Pelicula> resultadoList = new ArrayList<>();
        for (Pelicula p : resultado) {
            resultadoList.add(p);
        }
        return resultadoList;
    }

    /**
     * Buscar pelicula por clasificacion (Cliente opcion 2)
     * Usa ListaEnlazada con busqueda recursiva
     */
    public List<Pelicula> buscarPorClasificacion(String clasificacion) {
        List<Pelicula> todas = peliculaRepository.findAll();

        ListaEnlazada<Pelicula> lista = new ListaEnlazada<>();
        todas.forEach(lista::agregarFinal);

        // Usar método recursivo de la lista enlazada para contar coincidencias
        int count = lista.contarRecursivo(
                p -> p.getClasificacion().equalsIgnoreCase(clasificacion)
        );

        if (count == 0) {
            return new ArrayList<>();
        }

        ListaEnlazada<Pelicula> resultado = lista.filtrar(
                p -> p.getClasificacion().equalsIgnoreCase(clasificacion)
        );

        List<Pelicula> resultadoList = new ArrayList<>();
        for (Pelicula p : resultado) {
            resultadoList.add(p);
        }
        return resultadoList;
    }

    /**
     * Buscar pelicula por genero (Cliente opcion 3)
     * Usa ListaEnlazada con busqueda recursiva
     */
    public List<Pelicula> buscarPorGenero(Integer idGenero) {
        List<Pelicula> todas = peliculaRepository.findAll();

        ListaEnlazada<Pelicula> lista = new ListaEnlazada<>();
        todas.forEach(lista::agregarFinal);

        // Buscar recursivamente la primera coincidencia para validar que existe
        Pelicula primera = lista.buscarRecursivo(
                p -> p.getGenero().getIdGenero().equals(idGenero)
        );

        if (primera == null) {
            return new ArrayList<>();
        }

        ListaEnlazada<Pelicula> resultado = lista.filtrar(
                p -> p.getGenero().getIdGenero().equals(idGenero)
        );

        List<Pelicula> resultadoList = new ArrayList<>();
        for (Pelicula p : resultado) {
            resultadoList.add(p);
        }
        return resultadoList;
    }
}
