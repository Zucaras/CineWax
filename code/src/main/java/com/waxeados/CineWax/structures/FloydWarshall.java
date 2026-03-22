package com.waxeados.CineWax.structures;

import java.util.*;

/**
 * Implementación del algoritmo Floyd-Warshall.
 * JUSTIFICACIÓN: Se usa Floyd para calcular las distancias más cortas entre
 * todos los municipios de un estado. Esto permite al sistema sugerir al cliente
 * en qué municipio cercano hay funciones disponibles si en el suyo no hay,
 * o para mostrar la ruta más corta entre municipios.
 * Floyd es ideal porque necesitamos las distancias entre TODOS los pares de nodos
 * (todos-a-todos), no solo de un origen a los demás.
 */
public class FloydWarshall {

    public static final int INF = 99999999;

    private final int n;
    private final int[][] dist;
    private final int[][] next; // para reconstruir el camino
    private final List<String> nodos; // IDs de municipios

    /**
     * @param nodos lista de IDs de municipios
     * @param distancias mapa de (origen, destino) -> peso. Se asume grafo no dirigido.
     */
    public FloydWarshall(List<String> nodos, Map<String, Map<String, Integer>> distancias) {
        this.nodos = new ArrayList<>(nodos);
        this.n = nodos.size();
        this.dist = new int[n][n];
        this.next = new int[n][n];

        // Inicializar matrices
        for (int i = 0; i < n; i++) {
            Arrays.fill(dist[i], INF);
            Arrays.fill(next[i], -1);
            dist[i][i] = 0;
            next[i][i] = i;
        }

        // Cargar pesos desde el mapa de distancias
        for (int i = 0; i < n; i++) {
            String origen = nodos.get(i);
            Map<String, Integer> vecinos = distancias.getOrDefault(origen, Collections.emptyMap());
            for (Map.Entry<String, Integer> entry : vecinos.entrySet()) {
                int j = nodos.indexOf(entry.getKey());
                if (j >= 0) {
                    dist[i][j] = entry.getValue();
                    next[i][j] = j;
                }
            }
        }

        // Algoritmo Floyd-Warshall
        ejecutarFloyd();
    }

    /**
     * Ejecuta el algoritmo Floyd-Warshall con triple bucle anidado.
     * Complejidad: O(n^3)
     */
    private void ejecutarFloyd() {
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        next[i][j] = next[i][k];
                    }
                }
            }
        }
    }

    /**
     * Obtiene la distancia más corta entre dos municipios.
     */
    public int getDistancia(String origen, String destino) {
        int i = nodos.indexOf(origen);
        int j = nodos.indexOf(destino);
        if (i < 0 || j < 0) return INF;
        return dist[i][j];
    }

    /**
     * Reconstruye el camino más corto entre dos municipios.
     * Usa RECURSIVIDAD para reconstruir el path.
     */
    public List<String> getCamino(String origen, String destino) {
        int i = nodos.indexOf(origen);
        int j = nodos.indexOf(destino);
        if (i < 0 || j < 0 || dist[i][j] == INF) return Collections.emptyList();

        List<String> camino = new ArrayList<>();
        reconstruirCamino(i, j, camino); // recursividad
        return camino;
    }

    /**
     * Método recursivo para reconstruir el camino.
     */
    private void reconstruirCamino(int i, int j, List<String> camino) {
        if (i == j) {
            camino.add(nodos.get(i));
            return;
        }
        if (next[i][j] == -1) return; // no hay camino
        camino.add(nodos.get(i));
        reconstruirCamino(next[i][j], j, camino); // llamada recursiva
    }

    /**
     * Devuelve los municipios más cercanos a uno dado, ordenados por distancia.
     */
    public List<Map.Entry<String, Integer>> getMunicipiosCercanos(String origen) {
        int i = nodos.indexOf(origen);
        if (i < 0) return Collections.emptyList();

        List<Map.Entry<String, Integer>> resultado = new ArrayList<>();
        for (int j = 0; j < n; j++) {
            if (j != i && dist[i][j] < INF) {
                resultado.add(Map.entry(nodos.get(j), dist[i][j]));
            }
        }
        resultado.sort(Comparator.comparingInt(Map.Entry::getValue));
        return resultado;
    }

    /**
     * Devuelve la matriz de distancias completa.
     */
    public int[][] getMatrizDistancias() {
        return dist;
    }
}