package com.waxeados.CineWax.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeliculaDetalleDTO {
    private Integer idPelicula;
    private String nombre;
    private String director;
    private String productor;
    private String clasificacion;
    private Integer duracionMin;
    private String genero;
    private List<CarteleraDTO> horarios;
}