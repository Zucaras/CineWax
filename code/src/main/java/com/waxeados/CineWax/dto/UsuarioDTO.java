package com.waxeados.CineWax.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {
    private Integer idUsuario;
    private String username;
    private String rol;
    private String idMunicipio;
    private String nombreMunicipio;
}