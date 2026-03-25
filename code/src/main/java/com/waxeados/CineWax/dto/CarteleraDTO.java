package com.waxeados.CineWax.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarteleraDTO {
    private Integer idHorario;
    private String nombrePelicula;
    private String director;
    private String clasificacion;
    private Integer duracionMin;
    private String genero;
    private Integer numeroSala;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFinEstimada;
    private String municipio;
    private String estado;
}