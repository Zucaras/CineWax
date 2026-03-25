package com.waxeados.CineWax.structures;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * Lista enlazada simple genérica.
 * JUSTIFICACIÓN: Se usa una lista enlazada para almacenar las películas
 * de cada municipio en memoria. Las listas enlazadas permiten inserción y
 * eliminación eficiente O(1) en el frente, lo cual es útil para agregar/quitar
 * películas dinámicamente sin necesidad de redimensionar arreglos.
 * También se usa para la búsqueda secuencial por nombre, clasificación y género.
 */
public class ListaEnlazada<T> implements Iterable<T> {

    private static class Nodo<T> {
        T dato;
        Nodo<T> siguiente;

        Nodo(T dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }

    private Nodo<T> cabeza;
    private int tamanio;

    public ListaEnlazada() {
        this.cabeza = null;
        this.tamanio = 0;
    }

    /** Agrega un elemento al inicio de la lista. O(1) */
    public void agregarInicio(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.siguiente = cabeza;
        cabeza = nuevo;
        tamanio++;
    }

    /** Agrega un elemento al final de la lista. O(n) */
    public void agregarFinal(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            Nodo<T> actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevo;
        }
        tamanio++;
    }

    /** Elimina el primer elemento que cumpla el predicado. Retorna true si se eliminó. */
    public boolean eliminar(Predicate<T> condicion) {
        if (cabeza == null) return false;

        if (condicion.test(cabeza.dato)) {
            cabeza = cabeza.siguiente;
            tamanio--;
            return true;
        }

        Nodo<T> actual = cabeza;
        while (actual.siguiente != null) {
            if (condicion.test(actual.siguiente.dato)) {
                actual.siguiente = actual.siguiente.siguiente;
                tamanio--;
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    /** Busca un elemento por predicado. Retorna null si no lo encuentra. */
    public T buscar(Predicate<T> condicion) {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (condicion.test(actual.dato)) return actual.dato;
            actual = actual.siguiente;
        }
        return null;
    }

    /**
     * Busca un elemento usando RECURSIVIDAD.
     */
    public T buscarRecursivo(Predicate<T> condicion) {
        return buscarRecursivoHelper(cabeza, condicion);
    }

    private T buscarRecursivoHelper(Nodo<T> nodo, Predicate<T> condicion) {
        if (nodo == null) return null;                              // caso base
        if (condicion.test(nodo.dato)) return nodo.dato;           // encontrado
        return buscarRecursivoHelper(nodo.siguiente, condicion);   // recursividad
    }

    /**
     * Cuenta elementos que cumplen un predicado usando RECURSIVIDAD.
     */
    public int contarRecursivo(Predicate<T> condicion) {
        return contarRecursivoHelper(cabeza, condicion);
    }

    private int contarRecursivoHelper(Nodo<T> nodo, Predicate<T> condicion) {
        if (nodo == null) return 0;
        int cuenta = condicion.test(nodo.dato) ? 1 : 0;
        return cuenta + contarRecursivoHelper(nodo.siguiente, condicion);
    }

    /** Retorna una nueva lista con los elementos que cumplen el predicado. */
    public ListaEnlazada<T> filtrar(Predicate<T> condicion) {
        ListaEnlazada<T> resultado = new ListaEnlazada<>();
        Nodo<T> actual = cabeza;
        while (actual != null) {
            if (condicion.test(actual.dato)) {
                resultado.agregarFinal(actual.dato);
            }
            actual = actual.siguiente;
        }
        return resultado;
    }

    public T obtener(int index) {
        if (index < 0 || index >= tamanio) throw new IndexOutOfBoundsException();
        Nodo<T> actual = cabeza;
        for (int i = 0; i < index; i++) actual = actual.siguiente;
        return actual.dato;
    }

    public boolean estaVacia() { return cabeza == null; }

    public int getTamanio() { return tamanio; }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Nodo<T> actual = cabeza;
            public boolean hasNext() { return actual != null; }
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                T dato = actual.dato;
                actual = actual.siguiente;
                return dato;
            }
        };
    }
}