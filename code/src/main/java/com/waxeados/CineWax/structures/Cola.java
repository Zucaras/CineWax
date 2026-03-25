package com.waxeados.CineWax.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de Cola (Queue) genérica.
 * JUSTIFICACIÓN: Se usa una cola para procesar las solicitudes pendientes
 * de alta de horarios en orden FIFO (First In, First Out).
 * Cuando múltiples administradores intentan agregar horarios simultáneamente,
 * la cola garantiza que se procesen en el orden en que llegaron, evitando
 * conflictos de empalme.
 */
public class Cola<T> {

    private final ListaEnlazada<T> datos;

    public Cola() {
        this.datos = new ListaEnlazada<>();
    }

    /** Encola un elemento al final. O(n) */
    public void enqueue(T elemento) {
        datos.agregarFinal(elemento);
    }

    /** Desencola y retorna el primer elemento. O(1) */
    public T dequeue() {
        if (estaVacia()) throw new IllegalStateException("** LA COLA ESTA VACIA **");
        T frente = datos.obtener(0);
        datos.eliminar(e -> e.equals(frente));
        return frente;
    }

    /** Consulta el primer elemento sin desencolar. O(1) */
    public T peek() {
        if (estaVacia()) throw new IllegalStateException("** LA COLA ESTA VACIA **");
        return datos.obtener(0);
    }

    public boolean estaVacia() {
        return datos.estaVacia();
    }

    public int getTamanio() {
        return datos.getTamanio();
    }

    /** Retorna todos los elementos como lista (del frente al final). */
    public List<T> toList() {
        List<T> lista = new ArrayList<>();
        for (T elem : datos) {
            lista.add(elem);
        }
        return lista;
    }
}