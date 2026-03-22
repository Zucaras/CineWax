package com.waxeados.CineWax.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioResponseDTO {
    private Integer idHorario;
    private String pelicula;
    private Integer sala;
    private String fecha;
    private String horaInicio;
    private String horaFinEstimada;
}