package com.waxeados.CineWax.controllers;

import com.waxeados.CineWax.dto.*;
import com.waxeados.CineWax.entity.Usuario;
import com.waxeados.CineWax.services.UserService;
import com.waxeados.CineWax.mappers.UsuarioMapper;
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
    private final UsuarioMapper usuarioMapper; // Inyectamos el mapper

    /**
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody RegistroDTO dto) {
        // Si hay error (ej. username repetido), el GlobalExceptionHandler lo atrapa
        Usuario usuario = userService.registrar(dto);
        return ResponseEntity.ok(
                ApiResponse.ok("Usuario registrado exitosamente", usuarioMapper.toUsuarioDTO(usuario)));
    }

    /**
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginDTO dto, HttpSession session) {
        Usuario usuario = userService.login(dto);

        session.setAttribute("userId", usuario.getIdUsuario());
        session.setAttribute("username", usuario.getUsername());
        session.setAttribute("rol", usuario.getRolUsuario().name());

        if (usuario.getMunicipio() != null) {
            session.setAttribute("idMunicipio", usuario.getMunicipio().getIdMunicipio());
        }

        return ResponseEntity.ok(
                ApiResponse.ok("Login exitoso", usuarioMapper.toUsuarioDTO(usuario)));
    }

    /**
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> me(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            // Lanzamos la excepción para que el manejador global devuelva el error estructurado
            throw new SecurityException("No autenticado");
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
}