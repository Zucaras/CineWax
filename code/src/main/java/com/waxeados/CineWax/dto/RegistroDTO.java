package com.waxeados.CineWax.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistroDTO {
    private String username;
    private String password;
    private String rol; // ADMINISTRADOR o CLIENTE
    private String idMunicipio; // solo relevante para admin
}