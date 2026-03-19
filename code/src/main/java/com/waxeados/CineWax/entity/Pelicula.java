package com.waxeados.cartelera.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "pelicula")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pelicula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pelicula")
    private Integer idPelicula;

    @Column(name = "nombre", length = 120, nullable = false)
    private String nombre;

    @Column(name = "director", length = 100, nullable = false)
    private String director;

    @Column(name = "productor", length = 100, nullable = false)
    private String productor;

    // AA, A, B, B15, C, D
    @Column(name = "clasificacion", length = 10, nullable = false)
    private String clasificacion;

    @Column(name = "duracion_min", nullable = false)
    private Integer duracionMin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_genero", nullable = false)
    private Genero genero;

    @OneToMany(mappedBy = "pelicula", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HorarioCartelera> horarios;
}