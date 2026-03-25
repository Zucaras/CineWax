package com.waxeados.CineWax.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FloydCaminoDTO {
    private String origen;
    private String destino;
    private int distancia;
    private List<String> camino;
}