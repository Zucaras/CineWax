package com.waxeados.CineWax.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "genero")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Genero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_genero")
    private Integer idGenero;

    @Column(name = "nombre_genero", length = 40, nullable = false)
    private String nombreGenero;

    @OneToMany(mappedBy = "genero", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pelicula> peliculas;
}