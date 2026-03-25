package com.waxeados.CineWax.structures;

import com.waxeados.CineWax.entity.HorarioCartelera;

import java.util.List;

/**
 * Implementación de QuickSort para ordenar la cartelera.
 * JUSTIFICACIÓN: Se usa QuickSort para ordenar la cartelera por fecha y hora
 * de forma ascendente o descendente, cumpliendo con el requerimiento de
 * "Ordenar Cartelera (A y D)". QuickSort es eficiente con O(n log n) promedio
 * y es ideal para ordenar listas medianas-grandes de horarios en memoria.
 */
public class QuickSort {

    /**
     * Ordena una lista de HorarioCartelera usando QuickSort.
     * @param lista lista a ordenar
     * @param ascendente true = menor a mayor, false = mayor a menor
     */
    public static void ordenar(List<HorarioCartelera> lista, boolean ascendente) {
        if (lista == null || lista.size() <= 1) return;
        quickSort(lista, 0, lista.size() - 1, ascendente);
    }

    private static void quickSort(List<HorarioCartelera> lista, int low, int high, boolean asc) {
        if (low < high) {
            int pivotIndex = partition(lista, low, high, asc);
            quickSort(lista, low, pivotIndex - 1, asc);   // recursividad
            quickSort(lista, pivotIndex + 1, high, asc);   // recursividad
        }
    }

    private static int partition(List<HorarioCartelera> lista, int low, int high, boolean asc) {
        HorarioCartelera pivot = lista.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            int cmp = comparar(lista.get(j), pivot);
            boolean shouldSwap = asc ? (cmp < 0) : (cmp > 0);
            if (shouldSwap) {
                i++;
                swap(lista, i, j);
            }
        }
        swap(lista, i + 1, high);
        return i + 1;
    }

    /**
     * Compara dos horarios primero por fecha, luego por hora de inicio.
     */
    private static int comparar(HorarioCartelera a, HorarioCartelera b) {
        int cmpFecha = a.getFechaProyeccion().compareTo(b.getFechaProyeccion());
        if (cmpFecha != 0) return cmpFecha;
        return a.getHoraInicio().compareTo(b.getHoraInicio());
    }

    private static void swap(List<HorarioCartelera> lista, int i, int j) {
        HorarioCartelera tmp = lista.get(i);
        lista.set(i, lista.get(j));
        lista.set(j, tmp);
    }
}