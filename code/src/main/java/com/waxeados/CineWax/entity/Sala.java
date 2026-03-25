package com.waxeados.CineWax.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(
    name = "sala",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_sala_municipio",
        columnNames = {"numero_sala", "id_municipio"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sala")
    private Integer idSala;

    @Column(name = "numero_sala", nullable = false)
    private Integer numeroSala;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_municipio", nullable = false)
    private Municipio municipio;

    @OneToMany(mappedBy = "sala", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HorarioCartelera> horarios;
}