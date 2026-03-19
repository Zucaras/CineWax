package com.waxeados.cartelera.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    public enum Rol {
        ADMINISTRADOR,
        CLIENTE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "username", length = 60, nullable = false, unique = true)
    private String username;

    // Guardar siempre hasheado con BCrypt desde la capa de servicio
    @Column(name = "password_hash", length = 255, nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol_usuario", length = 20, nullable = false)
    private Rol rolUsuario;

    // Municipio al que pertenece (solo relevante para ADMINISTRADOR)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_municipio")
    private Municipio municipio;
}