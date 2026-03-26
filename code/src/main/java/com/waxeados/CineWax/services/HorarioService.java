package com.waxeados.CineWax.services;

import com.waxeados.CineWax.dto.CarteleraDTO;
import com.waxeados.CineWax.dto.HorarioDTO;
import com.waxeados.CineWax.entity.*;
import com.waxeados.CineWax.exceptions.ScheduleOverlapException;
import com.waxeados.CineWax.mappers.HorarioMapper;
import com.waxeados.CineWax.respositories.*;
import com.waxeados.CineWax.structures.Cola;
import com.waxeados.CineWax.structures.QuickSort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de horarios de cartelera.
 * Usa Cola para procesar solicitudes de alta de horarios en orden FIFO.
 * Usa QuickSort para ordenar la cartelera.
 */
@Service
@RequiredArgsConstructor
public class HorarioService {

    private final HorarioCarteleraRepository horarioRepository;
    private final PeliculaRepository peliculaRepository;
    private final SalaRepository salaRepository;
    private final HorarioMapper horarioMapper;

    // Cola para procesar solicitudes de horarios en orden
    private final Cola<HorarioDTO> colaSolicitudes = new Cola<>();

    // ==================== ALTA HORARIO ====================

    /**
     * Encola una solicitud de horario para procesarla en orden FIFO.
     * (Administrador opción 2)
     */
    public void encolarSolicitudHorario(HorarioDTO dto) {
        colaSolicitudes.enqueue(dto);
    }

    /**
     * Procesa la siguiente solicitud de la cola.
     * Retorna el horario creado o lanza excepción si hay conflicto.
     */
    @Transactional
    public HorarioCartelera procesarSiguienteSolicitud() {
        if (colaSolicitudes.estaVacia()) {
            throw new IllegalStateException("No hay solicitudes pendientes en la cola.");
        }
        HorarioDTO dto = colaSolicitudes.dequeue();
        return altaHorario(dto);
    }

    /**
     * Alta de horario directa (sin cola).
     * Valida que no se empalmen salas, días y horarios.
     * Máximo 10 horarios por película por día.
     */
    @Transactional
    public HorarioCartelera altaHorario(HorarioDTO dto) {
        Pelicula pelicula = peliculaRepository.findById(dto.getIdPelicula())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Película no encontrada con ID: " + dto.getIdPelicula()));

        Sala sala = salaRepository.findById(dto.getIdSala())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Sala no encontrada con ID: " + dto.getIdSala()));

        // Validar máximo 10 horarios por película por día
        List<HorarioCartelera> horariosPelicula = horarioRepository.findByPelicula_IdPelicula(dto.getIdPelicula());
        long countPorDia = horariosPelicula.stream()
                .filter(h -> h.getFechaProyeccion().equals(dto.getFechaProyeccion()))
                .count();

        if (countPorDia >= 10) {
            throw new IllegalArgumentException(
                    "La película ya tiene 10 horarios para la fecha: " + dto.getFechaProyeccion()
                            + ". Máximo permitido: 10.");
        }

        // Calcular hora fin estimada: hora inicio + duración + 30 min buffer
        LocalTime horaFinNueva = dto.getHoraInicio()
                .plusMinutes(pelicula.getDuracionMin())
                .plusMinutes(30);

        // Validar empalme con horarios existentes en la MISMA sala y MISMA fecha
        validarEmpalme(dto.getIdSala(), dto.getFechaProyeccion(), dto.getHoraInicio(), horaFinNueva, null);

        HorarioCartelera horario = HorarioCartelera.builder()
                .pelicula(pelicula)
                .sala(sala)
                .fechaProyeccion(dto.getFechaProyeccion())
                .horaInicio(dto.getHoraInicio())
                .horaFinEstimada(horaFinNueva)
                .build();

        return horarioRepository.save(horario);
    }

    // ==================== BAJA HORARIO ====================

    /**
     * Baja de horario (Administrador opción 4).
     */
    @Transactional
    public void bajaHorario(Integer idHorario) {
        HorarioCartelera horario = horarioRepository.findById(idHorario)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Horario no encontrado con ID: " + idHorario));
        horarioRepository.delete(horario);
    }

    // ==================== VALIDACIÓN DE EMPALME ====================

    /**
     * Valida que el nuevo horario no se empalme con los existentes en la misma sala y fecha.
     * Se requieren 30 min de buffer entre películas.
     *
     * @param idSala          ID de la sala
     * @param fecha           fecha de proyección
     * @param nuevaHoraInicio hora de inicio propuesta
     * @param nuevaHoraFin    hora fin estimada (duración + 30 min)
     * @param excludeId       ID de horario a excluir (para modificaciones), puede ser null
     */
    public void validarEmpalme(Integer idSala, java.time.LocalDate fecha,
                               LocalTime nuevaHoraInicio, LocalTime nuevaHoraFin,
                               Integer excludeId) {

        List<HorarioCartelera> existentes = horarioRepository.findBySalaAndFecha(idSala, fecha);

        for (HorarioCartelera existente : existentes) {
            // Excluir el horario que se está modificando
            if (existente.getIdHorario().equals(excludeId)) {
                continue;
            }

            LocalTime iniExistente = existente.getHoraInicio();
            LocalTime finExistente = existente.getHoraFinEstimada();

            // Verificar solapamiento:
            // Hay empalme si: nuevaInicio < finExistente AND nuevaFin > inicioExistente
            boolean hayEmpalme = nuevaHoraInicio.isBefore(finExistente)
                    && nuevaHoraFin.isAfter(iniExistente);

            if (hayEmpalme) {
                throw new ScheduleOverlapException(
                        String.format("Empalme detectado en sala %d, fecha %s. " +
                                        "El horario %s-%s se cruza con '%s' (%s-%s). " +
                                        "Recuerda que se necesitan 30 min entre películas.",
                                existente.getSala().getNumeroSala(),
                                fecha,
                                nuevaHoraInicio, nuevaHoraFin,
                                existente.getPelicula().getNombre(),
                                iniExistente, finExistente));
            }
        }
    }

    // ==================== CONSULTAR CARTELERA ====================

    /**
     * Consultar cartelera de un municipio (Administrador opción 7 / Cliente opción 6).
     * Usa QuickSort para ordenar por fecha y hora.
     *
     * @param idMunicipio ID del municipio
     * @param ascendente  true = menor a mayor, false = mayor a menor
     */
    public List<CarteleraDTO> consultarCartelera(String idMunicipio, boolean ascendente) {
        List<HorarioCartelera> horarios = horarioRepository.findByMunicipio(idMunicipio);

        // Ordenar usando QuickSort (estructura obligatoria)
        QuickSort.ordenar(horarios, ascendente);

        return horarios.stream()
                .map(horarioMapper::toCarteleraDTO)
                .collect(Collectors.toList());
    }

    /**
     * Consultar cartelera de un municipio por rango de fechas.
     */
    public List<CarteleraDTO> consultarCarteleraRango(String idMunicipio,
                                                       java.time.LocalDate fechaInicio,
                                                       java.time.LocalDate fechaFin,
                                                       boolean ascendente) {
        List<HorarioCartelera> horarios = horarioRepository.findByMunicipio(idMunicipio);

        // Filtrar por rango de fechas
        List<HorarioCartelera> filtrados = horarios.stream()
                .filter(h -> !h.getFechaProyeccion().isBefore(fechaInicio)
                        && !h.getFechaProyeccion().isAfter(fechaFin))
                .collect(Collectors.toList());

        // Ordenar con QuickSort
        QuickSort.ordenar(filtrados, ascendente);

        return filtrados.stream()
                .map(horarioMapper::toCarteleraDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener horarios de una película específica.
     */
    public List<CarteleraDTO> obtenerHorariosPelicula(Integer idPelicula) {
        List<HorarioCartelera> horarios = horarioRepository.findByPelicula_IdPelicula(idPelicula);

        QuickSort.ordenar(horarios, true);

        return horarios.stream()
                .map(horarioMapper::toCarteleraDTO)
                .collect(Collectors.toList());
    }

    // ==================== COLA ====================

    /**
     * Obtener solicitudes pendientes en la cola.
     */
    public List<HorarioDTO> obtenerSolicitudesPendientes() {
        return colaSolicitudes.toList();
    }

    /**
     * Procesar todas las solicitudes pendientes.
     * Retorna los horarios creados exitosamente y los errores.
     */
    @Transactional
    public List<String> procesarTodasLasSolicitudes() {
        List<String> resultados = new ArrayList<>();

        while (!colaSolicitudes.estaVacia()) {
            HorarioDTO dto = colaSolicitudes.dequeue();
            try {
                HorarioCartelera h = altaHorario(dto);
                resultados.add("OK: Horario creado para '" + h.getPelicula().getNombre()
                        + "' en sala " + h.getSala().getNumeroSala()
                        + " a las " + h.getHoraInicio());
            } catch (Exception e) {
                resultados.add("** ERROR: " + e.getMessage() + " **");
            }
        }

        return resultados;
    }
}