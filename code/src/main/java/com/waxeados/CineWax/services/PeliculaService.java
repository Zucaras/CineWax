package com.waxeados.CineWax.services;

import com.waxeados.CineWax.dto.PeliculaDTO;
import com.waxeados.CineWax.entity.*;
import com.waxeados.CineWax.respositories.*;
import com.waxeados.CineWax.structures.ListaEnlazada;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

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
    public Pelicula modificarPelicula(int id, PeliculaDTO dto) {
        Pelicula p = peliculaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pelicula no encontrada con ID: " + id));

        // 1. VERIFICAR SI LA DURACIÓN CAMBIÓ
        if (!Objects.equals(p.getDuracionMin(), dto.getDuracionMin())) {

            // Traemos todos los horarios donde se proyecta esta película
            List<HorarioCartelera> horariosAfectados = horarioRepository.findByPelicula_IdPelicula(id);

            for (HorarioCartelera horario : horariosAfectados) {
                // Calculamos la nueva hora de fin (Duración nueva + 30 min de limpieza)
                LocalTime nuevaHoraFin = horario.getHoraInicio()
                        .plusMinutes(dto.getDuracionMin())
                        .plusMinutes(30);

                // Traemos todos los horarios de ESA SALA en ESA FECHA para revisar empalmes
                List<HorarioCartelera> horariosEnSala = horarioRepository
                        .findBySalaAndFecha(horario.getSala().getIdSala(), horario.getFechaProyeccion());

                for (HorarioCartelera otroHorario : horariosEnSala) {
                    // Omitimos compararlo contra sí mismo
                    if (otroHorario.getIdHorario().equals(horario.getIdHorario())) {
                        continue;
                    }

                    // Fórmula matemática para detectar empalmes de tiempo: (InicioA < FinB) y (FinA > InicioB)
                    boolean hayEmpalme = horario.getHoraInicio().isBefore(otroHorario.getHoraFinEstimada())
                            && nuevaHoraFin.isAfter(otroHorario.getHoraInicio());

                    if (hayEmpalme) {
                        throw new IllegalArgumentException(
                                "No se puede cambiar la duracion a " + dto.getDuracionMin() + " min. " +
                                        "Causa un empalme el dia " + horario.getFechaProyeccion() +
                                        " en la Sala " + horario.getSala().getNumeroSala() +
                                        " con la funcion '" + otroHorario.getPelicula().getNombre() + "'.");
                    }
                }

                // Si no chocó con ninguna otra película, le actualizamos su nueva hora de fin
                horario.setHoraFinEstimada(nuevaHoraFin);
            }

            // Guardamos todos los horarios actualizados de un jalón
            horarioRepository.saveAll(horariosAfectados);
        }

        // 2. ACTUALIZAR LOS DEMÁS DATOS DE LA PELÍCULA
        p.setNombre(dto.getNombre());
        p.setDirector(dto.getDirector());
        p.setProductor(dto.getProductor());
        p.setClasificacion(dto.getClasificacion());
        p.setDuracionMin(dto.getDuracionMin());

        // Buscar y asignar el nuevo género
        Genero g = generoRepository.findById(dto.getIdGenero())
                .orElseThrow(() -> new IllegalArgumentException("Genero no encontrado"));
        p.setGenero(g);

        return peliculaRepository.save(p);
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
