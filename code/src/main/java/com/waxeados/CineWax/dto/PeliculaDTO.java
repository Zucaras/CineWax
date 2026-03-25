package com.waxeados.CineWax.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PeliculaDTO {
    private String nombre;
    private String director;
    private String productor;
    private String clasificacion; // AA, A, B, B15, C, D
    private Integer duracionMin;
    private Integer idGenero;
}