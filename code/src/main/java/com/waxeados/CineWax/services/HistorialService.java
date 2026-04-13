package com.waxeados.CineWax.services;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Collections;

/**
 * Servicio de historial de navegación del cliente.
 * Usa Pila (Stack) para almacenar las acciones del usuario.
 * JUSTIFICACIÓN: La pila permite al cliente "regresar" a la acción anterior
 * siguiendo el principio LIFO.
 * Cada usuario tiene su propia pila de historial (por sesión en memoria).
 */
@Service
public class HistorialService {

    // ESTRUCTURA DE DATOS: PILA (Stack).
    // Usamos un mapa para darle una pila independiente a cada usuario.
    private final Map<String, Stack<String>> historiales = new HashMap<>();

    public void registrarAccion(String username, String accion, String detalle) {
        historiales.putIfAbsent(username, new Stack<>());
        historiales.get(username).push(accion + " - " + detalle); // Push (LIFO)
    }

    public List<String> obtenerHistorial(String username) {
        if (!historiales.containsKey(username)) return new ArrayList<>();

        // Convertimos la pila en lista para imprimirla (y la volteamos para ver lo mqs nuevo primero)
        List<String> lista = new ArrayList<>(historiales.get(username));
        Collections.reverse(lista);
        return lista;
    }

    public void deshacerUltimaAccion(String username) {
        if (historiales.containsKey(username) && !historiales.get(username).isEmpty()) {
            historiales.get(username).pop(); // Elimina la ultima accion (POP)
        }
    }
}