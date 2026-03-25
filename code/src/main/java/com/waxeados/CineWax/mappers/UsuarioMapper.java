package com.waxeados.CineWax.mappers;

import com.waxeados.CineWax.dto.UsuarioDTO;
import com.waxeados.CineWax.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioDTO toUsuarioDTO(Usuario u) {
        UsuarioDTO.UsuarioDTOBuilder b = UsuarioDTO.builder()
                .idUsuario(u.getIdUsuario())
                .username(u.getUsername())
                .rol(u.getRolUsuario().name());

        if (u.getMunicipio() != null) {
            b.idMunicipio(u.getMunicipio().getIdMunicipio())
                    .nombreMunicipio(u.getMunicipio().getNombreMunicipio());
        }
        return b.build();
    }
}