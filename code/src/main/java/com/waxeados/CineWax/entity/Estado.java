package com.waxeados.cartelera.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "estado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Estado {

    @Id
    @Column(name = "id_estado", length = 3, nullable = false)
    private String idEstado;

    @Column(name = "nombre_estado", length = 60, nullable = false)
    private String nombreEstado;

    @OneToMany(mappedBy = "estado", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Municipio> municipios;
}