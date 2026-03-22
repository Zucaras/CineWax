package com.waxeados.CineWax.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de Pila (Stack) genérica.
 * JUSTIFICACIÓN: Se usa una pila para almacenar el historial de navegación
 * del cliente. Cada vez que el cliente consulta una película o cartelera,
 * se apila la acción. El cliente puede "regresar" desapilando.
 * La pila sigue el principio LIFO (Last In, First Out) ideal para historial.
 */
public class Pila<T> {

    private final ListaEnlazada<T> datos;

    public Pila() {
        this.datos = new ListaEnlazada<>();
    }

    /** Apila un elemento. O(1) */
    public void push(T elemento) {
        datos.agregarInicio(elemento);
    }

    /** Desapila y retorna el elemento del tope. O(1) */
    public T pop() {
        if (estaVacia()) throw new IllegalStateException("La pila está vacía");
        T tope = datos.obtener(0);
        datos.eliminar(e -> e.equals(tope));
        return tope;
    }

    /** Consulta el elemento del tope sin desapilar. O(1) */
    public T peek() {
        if (estaVacia()) throw new IllegalStateException("La pila está vacía");
        return datos.obtener(0);
    }

    public boolean estaVacia() {
        return datos.estaVacia();
    }

    public int getTamanio() {
        return datos.getTamanio();
    }

    /** Retorna todos los elementos como lista (del tope a la base). */
    public List<T> toList() {
        List<T> lista = new ArrayList<>();
        for (T elem : datos) {
            lista.add(elem);
        }
        return lista;
    }
}