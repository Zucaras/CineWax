package com.waxeados.CineWax.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "municipio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Municipio {

    @Id
    @Column(name = "id_municipio", length = 10, nullable = false)
    private String idMunicipio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado", nullable = false)
    private Estado estado;

    @Column(name = "letra_municipio", length = 1, nullable = false)
    private Character letraMunicipio;

    @Column(name = "nombre_municipio", length = 80, nullable = false)
    private String nombreMunicipio;

    @OneToMany(mappedBy = "municipio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Sala> salas;
}