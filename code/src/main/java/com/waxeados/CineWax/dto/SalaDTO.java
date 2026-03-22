package com.waxeados.CineWax.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaDTO {
    private Integer idSala;
    private Integer numeroSala;
    private String municipio;
}