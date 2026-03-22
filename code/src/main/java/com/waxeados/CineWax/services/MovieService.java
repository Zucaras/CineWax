package com.waxeados.CineWax.services;

import com.waxeados.CineWax.dto.CarteleraDTO;
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
 * Servicio principal de películas.
 * Usa ListaEnlazada para almacenamiento en memoria y búsquedas recursivas.
 */
@Service
@RequiredArgsConstructor
public class MovieService {

    private final PeliculaRepository peliculaRepository;
    private final GeneroRepository generoRepository;
    private final HorarioCarteleraRepository horarioRepository;

    // ==================== ALTA ====================

    /**
     * Alta de película (Administrador opción 1).
     * Valida que no exista otra con el mismo nombre.
     */
    @Transactional
    public Pelicula altaPelicula(PeliculaDTO dto) {
        if (peliculaRepository.existsByNameIgnoreCase(dto.getNombre())) {
            throw new IllegalArgumentException("Ya existe una película con el nombre: " + dto.getNombre());
        }

        Genero genero = generoRepository.findById(dto.getIdGenero())
                .orElseThrow(() -> new IllegalArgumentException("Género no encontrado con ID: " + dto.getIdGenero()));

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

    // ==================== BAJA ====================

    /**
     * Baja de película (Administrador opción 3).
     * Elimina la película y todos sus horarios asociados.
     */
    @Transactional
    public void bajaPelicula(Integer idPelicula) {
        Pelicula pelicula = peliculaRepository.findById(idPelicula)
                .orElseThrow(() -> new IllegalArgumentException("Película no encontrada con ID: " + idPelicula));

        // Eliminar horarios asociados primero
        horarioRepository.deleteByPelicula_IdPelicula(idPelicula);
        peliculaRepository.delete(pelicula);
    }

    // ==================== MODIFICAR ====================

    /**
     * Modificar película (Administrador opción 5).
     * Se pueden modificar todos los campos excepto los horarios.
     */
    @Transactional
    public Pelicula modificarPelicula(Integer idPelicula, PeliculaDTO dto) {
        Pelicula pelicula = peliculaRepository.findById(idPelicula)
                .orElseThrow(() -> new IllegalArgumentException("Película no encontrada con ID: " + idPelicula));

        // Verificar que el nuevo nombre no colisione con otra película
        if (!pelicula.getNombre().equalsIgnoreCase(dto.getNombre())
                && peliculaRepository.existsByNameIgnoreCase(dto.getNombre())) {
            throw new IllegalArgumentException("Ya existe otra película con el nombre: " + dto.getNombre());
        }

        Genero genero = generoRepository.findById(dto.getIdGenero())
                .orElseThrow(() -> new IllegalArgumentException("Género no encontrado con ID: " + dto.getIdGenero()));

        pelicula.setNombre(dto.getNombre());
        pelicula.setDirector(dto.getDirector());
        pelicula.setProductor(dto.getProductor());
        pelicula.setClasificacion(dto.getClasificacion().toUpperCase());
        pelicula.setDuracionMin(dto.getDuracionMin());
        pelicula.setGenero(genero);

        return peliculaRepository.save(pelicula);
    }

    // ==================== CONSULTAR ====================

    /**
     * Consultar película (Administrador opción 6 / Cliente opción 5).
     */
    public Pelicula consultarPelicula(Integer idPelicula) {
        return peliculaRepository.findById(idPelicula)
                .orElseThrow(() -> new IllegalArgumentException("Película no encontrada con ID: " + idPelicula));
    }

    /**
     * Listar todas las películas.
     */
    public List<Pelicula> listarPeliculas() {
        return peliculaRepository.findAll();
    }

    // ==================== BÚSQUEDAS (Cliente) ====================

    /**
     * Buscar película por nombre (Cliente opción 1).
     * Usa ListaEnlazada con búsqueda recursiva.
     */
    public List<Pelicula> buscarPorNombre(String nombre) {
        List<Pelicula> todas = peliculaRepository.findAll();

        // Cargar en lista enlazada para usar búsqueda recursiva
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
     * Buscar película por clasificación (Cliente opción 2).
     * Usa ListaEnlazada con búsqueda recursiva.
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
     * Buscar película por género (Cliente opción 3).
     * Usa ListaEnlazada con búsqueda recursiva.
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

    // ==================== HELPERS ====================

    /**
     * Convierte un HorarioCartelera a CarteleraDTO para la respuesta.
     */
    public CarteleraDTO toCarteleraDTO(HorarioCartelera h) {
        return CarteleraDTO.builder()
                .idHorario(h.getIdHorario())
                .nombrePelicula(h.getPelicula().getNombre())
                .director(h.getPelicula().getDirector())
                .clasificacion(h.getPelicula().getClasificacion())
                .duracionMin(h.getPelicula().getDuracionMin())
                .genero(h.getPelicula().getGenero().getNombreGenero())
                .numeroSala(h.getSala().getNumeroSala())
                .fecha(h.getFechaProyeccion())
                .horaInicio(h.getHoraInicio())
                .horaFinEstimada(h.getHoraFinEstimada())
                .municipio(h.getSala().getMunicipio().getNombreMunicipio())
                .estado(h.getSala().getMunicipio().getEstado().getNombreEstado())
                .build();
    }
}