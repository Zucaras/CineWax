package com.waxeados.CineWax.services;

import com.waxeados.CineWax.dto.HistorialNavegacionDTO;
import com.waxeados.CineWax.structures.Pila;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio de historial de navegación del cliente.
 * Usa Pila (Stack) para almacenar las acciones del usuario.
 * JUSTIFICACIÓN: La pila permite al cliente "regresar" a la acción anterior
 * siguiendo el principio LIFO.
 * Cada usuario tiene su propia pila de historial (por sesión en memoria).
 */
@Service
public class HistorialService {

    // Mapa de username -> Pila de historial
    private final Map<String, Pila<HistorialNavegacionDTO>> historialesPorUsuario =
            new ConcurrentHashMap<>();

    /**
     * Registra una acción en el historial del usuario.
     */
    public void registrarAccion(String username, String accion, String detalle) {
        Pila<HistorialNavegacionDTO> pila = historialesPorUsuario
                .computeIfAbsent(username, k -> new Pila<>());

        HistorialNavegacionDTO entrada = HistorialNavegacionDTO.builder()
                .accion(accion)
                .detalle(detalle)
                .timestamp(LocalDateTime.now())
                .build();

        pila.push(entrada);
    }

    /**
     * Obtiene la última acción del usuario (peek).
     */
    public HistorialNavegacionDTO obtenerUltimaAccion(String username) {
        Pila<HistorialNavegacionDTO> pila = historialesPorUsuario.get(username);
        if (pila == null || pila.estaVacia()) {
            return null;
        }
        return pila.peek();
    }

    /**
     * Regresa a la acción anterior (pop).
     */
    public HistorialNavegacionDTO regresar(String username) {
        Pila<HistorialNavegacionDTO> pila = historialesPorUsuario.get(username);
        if (pila == null || pila.estaVacia()) {
            return null;
        }
        return pila.pop();
    }

    /**
     * Obtiene todo el historial del usuario (del más reciente al más antiguo).
     */
    public List<HistorialNavegacionDTO> obtenerHistorial(String username) {
        Pila<HistorialNavegacionDTO> pila = historialesPorUsuario.get(username);
        if (pila == null) {
            return List.of();
        }
        return pila.toList();
    }

    /**
     * Limpia el historial de un usuario.
     */
    public void limpiarHistorial(String username) {
        historialesPorUsuario.remove(username);
    }
}