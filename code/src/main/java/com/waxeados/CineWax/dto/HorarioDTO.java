package com.waxeados.CineWax.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioDTO {
    private Integer idPelicula;
    private Integer idSala;
    private LocalDate fechaProyeccion;
    private LocalTime horaInicio;
}