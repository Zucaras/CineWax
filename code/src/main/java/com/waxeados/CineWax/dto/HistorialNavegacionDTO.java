package com.waxeados.CineWax.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialNavegacionDTO {
    private String accion; // ej: "CONSULTAR_PELICULA", "CONSULTAR_CARTELERA", "BUSCAR_NOMBRE"
    private String detalle; // ej: nombre de la película o parámetro de búsqueda
    private LocalDateTime timestamp;
}