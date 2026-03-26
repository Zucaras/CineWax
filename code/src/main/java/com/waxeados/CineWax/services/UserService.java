package com.waxeados.CineWax.services;

import com.waxeados.CineWax.dto.LoginDTO;
import com.waxeados.CineWax.dto.RegistroDTO;
import com.waxeados.CineWax.entity.Municipio;
import com.waxeados.CineWax.entity.Usuario;
import com.waxeados.CineWax.respositories.MunicipioRepository;
import com.waxeados.CineWax.respositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UsuarioRepository usuarioRepository;
    private final MunicipioRepository municipioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Registro de nuevo usuario.
     */
    @Transactional
    public Usuario registrar(RegistroDTO dto) {
        if (usuarioRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("** EL USUARIO YA ESTA EN USO **");
        }

        Usuario.Rol rol;
        try {
            rol = Usuario.Rol.valueOf(dto.getRol().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("** ROL INVALIDO **");
        }

        Municipio municipio = null;
        if (rol == Usuario.Rol.ADMINISTRADOR) {
            if (dto.getIdMunicipio() == null || dto.getIdMunicipio().isBlank()) {
                throw new IllegalArgumentException("** EL ADMINISTRADOR DEBE TENER UN MUNICIPIO ASIGNADO **");
            }
            municipio = municipioRepository.findById(dto.getIdMunicipio())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "** MUNICIPIO NO ENCONTRADO: " + dto.getIdMunicipio() + " **"));
        }

        Usuario usuario = Usuario.builder()
                .username(dto.getUsername())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .rolUsuario(rol)
                .municipio(municipio)
                .build();

        return usuarioRepository.save(usuario);
    }

    /**
     * Login: verifica credenciales y retorna el usuario.
     */
    public Usuario login(LoginDTO dto) {
        Usuario usuario = usuarioRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("** USUARIO NO ENCONTRADO **"));

        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPasswordHash())) {
            throw new IllegalArgumentException("** CONTRASEÑA INCORRECTA **");
        }

        return usuario;
    }

    /**
     * Obtener usuario por ID.
     */
    public Usuario obtenerPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("** USUARIO NO ENCONTRADO CON ID: " + id+ " **"));
    }

    /**
     * Obtener usuario por username.
     */
    public Usuario obtenerPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("** USUARIO NO ENCONTRADO: " + username+ " **"));
    }
}