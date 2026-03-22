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
            throw new IllegalArgumentException("El username ya está en uso: " + dto.getUsername());
        }

        Usuario.Rol rol;
        try {
            rol = Usuario.Rol.valueOf(dto.getRol().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rol inválido. Use ADMINISTRADOR o CLIENTE.");
        }

        Municipio municipio = null;
        if (rol == Usuario.Rol.ADMINISTRADOR) {
            if (dto.getIdMunicipio() == null || dto.getIdMunicipio().isBlank()) {
                throw new IllegalArgumentException("El administrador debe tener un municipio asignado.");
            }
            municipio = municipioRepository.findById(dto.getIdMunicipio())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Municipio no encontrado: " + dto.getIdMunicipio()));
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
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPasswordHash())) {
            throw new IllegalArgumentException("Contraseña incorrecta.");
        }

        return usuario;
    }

    /**
     * Obtener usuario por ID.
     */
    public Usuario obtenerPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
    }

    /**
     * Obtener usuario por username.
     */
    public Usuario obtenerPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + username));
    }
}