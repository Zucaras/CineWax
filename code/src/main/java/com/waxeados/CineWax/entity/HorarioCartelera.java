package com.waxeados.cartelera.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "horario_cartelera")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioCartelera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_horario")
    private Integer idHorario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pelicula", nullable = false)
    private Pelicula pelicula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sala", nullable = false)
    private Sala sala;

    @Column(name = "fecha_proyeccion", nullable = false)
    private LocalDate fechaProyeccion;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    // hora_inicio + duracion_min + 30 min de limpieza
    // Se calcula en la capa de servicio antes de persistir
    @Column(name = "hora_fin_estimada", nullable = false)
    private LocalTime horaFinEstimada;

    /**
     * Calcula y asigna hora_fin_estimada automáticamente antes de insertar o actualizar.
     * Suma la duración de la película + 30 minutos de buffer a hora_inicio.
     */
    @PrePersist
    @PreUpdate
    public void calcularHoraFin() {
        if (horaInicio != null && pelicula != null && pelicula.getDuracionMin() != null) {
            this.horaFinEstimada = horaInicio
                .plusMinutes(pelicula.getDuracionMin())
                .plusMinutes(30);
        }
    }
}