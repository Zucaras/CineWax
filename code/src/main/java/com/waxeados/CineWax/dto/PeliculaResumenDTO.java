package com.waxeados.CineWax.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeliculaResumenDTO {
    private Integer idPelicula;
    private String nombre;
    private String clasificacion;
    private Integer duracionMin;
    private String genero;
}