package com.waxeados.CineWax.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FloydCercanosDTO {
    private String origen;
    private String estado;
    private List<MunicipioCercanoDTO> municipiosCercanos;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MunicipioCercanoDTO {
        private String idMunicipio;
        private int distancia;
    }
}