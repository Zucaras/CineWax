package com.waxeados.CineWax.mappers;

import com.waxeados.CineWax.dto.CarteleraDTO;
import com.waxeados.CineWax.dto.HorarioResponseDTO;
import com.waxeados.CineWax.entity.HorarioCartelera;
import org.springframework.stereotype.Component;

/**
 * Componente para convertir entre entidades HorarioCartelera y DTOs.
 */
@Component
public class HorarioMapper {

    public HorarioResponseDTO toHorarioResponse(HorarioCartelera horario) {
        return HorarioResponseDTO.builder()
                .idHorario(horario.getIdHorario())
                .pelicula(horario.getPelicula().getNombre())
                .sala(horario.getSala().getNumeroSala())
                .fecha(horario.getFechaProyeccion().toString())
                .horaInicio(horario.getHoraInicio().toString())
                .horaFinEstimada(horario.getHoraFinEstimada().toString())
                .build();
    }

    public CarteleraDTO toCarteleraDTO(HorarioCartelera horario) {
        return CarteleraDTO.builder()
                .idHorario(horario.getIdHorario())
                .nombrePelicula(horario.getPelicula().getNombre())
                .director(horario.getPelicula().getDirector())
                .clasificacion(horario.getPelicula().getClasificacion())
                .duracionMin(horario.getPelicula().getDuracionMin())
                .genero(horario.getPelicula().getGenero().getNombreGenero())
                .numeroSala(horario.getSala().getNumeroSala())
                .fecha(horario.getFechaProyeccion())
                .horaInicio(horario.getHoraInicio())
                .horaFinEstimada(horario.getHoraFinEstimada())
                .municipio(horario.getSala().getMunicipio().getNombreMunicipio())
                .estado(horario.getSala().getMunicipio().getEstado().getNombreEstado())
                .build();
    }
}