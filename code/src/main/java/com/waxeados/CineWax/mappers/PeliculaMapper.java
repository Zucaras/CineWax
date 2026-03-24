package com.waxeados.CineWax.mappers;

import com.waxeados.CineWax.dto.CarteleraDTO;
import com.waxeados.CineWax.dto.PeliculaDetalleDTO;
import com.waxeados.CineWax.dto.PeliculaResumenDTO;
import com.waxeados.CineWax.entity.Pelicula;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Componente para convertir entre entidades Pelicula y sus respectivos DTOs.
 */
@Component
public class PeliculaMapper {

    public PeliculaResumenDTO toPeliculaResumen(Pelicula pelicula) {
        return PeliculaResumenDTO.builder()
                .idPelicula(pelicula.getIdPelicula())
                .nombre(pelicula.getNombre())
                .clasificacion(pelicula.getClasificacion())
                .duracionMin(pelicula.getDuracionMin())
                .genero(pelicula.getGenero().getNombreGenero())
                .build();
    }

    public PeliculaDetalleDTO toPeliculaDetalle(Pelicula pelicula, List<CarteleraDTO> horarios) {
        return PeliculaDetalleDTO.builder()
                .idPelicula(pelicula.getIdPelicula())
                .nombre(pelicula.getNombre())
                .director(pelicula.getDirector())
                .productor(pelicula.getProductor())
                .clasificacion(pelicula.getClasificacion())
                .duracionMin(pelicula.getDuracionMin())
                .genero(pelicula.getGenero().getNombreGenero())
                .horarios(horarios)
                .build();
    }
}