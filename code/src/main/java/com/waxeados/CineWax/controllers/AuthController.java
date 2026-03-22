package com.waxeados.CineWax.controllers;

import com.waxeados.CineWax.dto.*;
import com.waxeados.CineWax.entity.Usuario;
import com.waxeados.CineWax.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    /**
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody RegistroDTO dto) {
        try {
            Usuario usuario = userService.registrar(dto);
            return ResponseEntity.ok(
                    ApiResponse.ok("Usuario registrado exitosamente", toDTO(usuario)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /**
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginDTO dto, HttpSession session) {
        try {
            Usuario usuario = userService.login(dto);
            session.setAttribute("userId", usuario.getIdUsuario());
            session.setAttribute("username", usuario.getUsername());
            session.setAttribute("rol", usuario.getRolUsuario().name());
            if (usuario.getMunicipio() != null) {
                session.setAttribute("idMunicipio", usuario.getMunicipio().getIdMunicipio());
            }
            return ResponseEntity.ok(
                    ApiResponse.ok("Login exitoso", toDTO(usuario)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).body(ApiResponse.unauthorized(e.getMessage()));
        }
    }

    /**
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> me(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(ApiResponse.unauthorized("No autenticado"));
        }
        UsuarioDTO data = UsuarioDTO.builder()
                .idUsuario(userId)
                .username((String) session.getAttribute("username"))
                .rol((String) session.getAttribute("rol"))
                .build();
        return ResponseEntity.ok(ApiResponse.ok("Sesión activa", data));
    }

    /**
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(ApiResponse.ok("Sesión cerrada exitosamente"));
    }

    private UsuarioDTO toDTO(Usuario u) {
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