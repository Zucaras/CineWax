package com.waxeados.CineWax.services;

import com.waxeados.CineWax.entity.Municipio;
import com.waxeados.CineWax.respositories.MunicipioRepository;
import com.waxeados.CineWax.structures.FloydWarshall;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Servicio que usa el algoritmo Floyd-Warshall para calcular distancias
 * entre municipios de un mismo estado.
 * JUSTIFICACIÓN: Permite sugerir al cliente en qué municipio cercano
 * hay funciones disponibles si en el suyo no hay, o encontrar la ruta
 * más corta entre municipios.
 */
@Service
@RequiredArgsConstructor
public class FloydService {

    private final MunicipioRepository municipioRepository;

    // Un grafo Floyd por cada estado
    private final Map<String, FloydWarshall> grafosPorEstado = new HashMap<>();

    /**
     * Inicializa los grafos Floyd al arrancar la aplicación.
     * Se asignan distancias simuladas entre municipios de cada estado
     * (en un proyecto real, se usarían distancias reales en km).
     */
    @PostConstruct
    public void inicializar() {
        // IDs de los 5 estados
        String[] estados = {"J11", "N11", "E11", "C11", "S11"};

        for (String idEstado : estados) {
            List<Municipio> municipios = municipioRepository.findByEstado_IdEstado(idEstado);
            if (municipios.isEmpty()) continue;

            List<String> nodos = new ArrayList<>();
            for (Municipio m : municipios) {
                nodos.add(m.getIdMunicipio());
            }

            // Generar distancias simuladas entre municipios adyacentes
            // (consecutivos en la lista tienen distancias menores)
            Map<String, Map<String, Integer>> distancias = new HashMap<>();
            for (int i = 0; i < nodos.size(); i++) {
                Map<String, Integer> vecinos = new HashMap<>();

                // Conectar con el siguiente (distancia corta)
                if (i + 1 < nodos.size()) {
                    vecinos.put(nodos.get(i + 1), 15 + (i * 5) % 30);
                }
                // Conectar con el anterior
                if (i - 1 >= 0) {
                    vecinos.put(nodos.get(i - 1), 15 + (i * 5) % 30);
                }
                // Conectar con uno dos posiciones adelante (distancia mayor)
                if (i + 2 < nodos.size()) {
                    vecinos.put(nodos.get(i + 2), 30 + (i * 7) % 40);
                }
                if (i - 2 >= 0) {
                    vecinos.put(nodos.get(i - 2), 30 + (i * 7) % 40);
                }

                distancias.put(nodos.get(i), vecinos);
            }

            FloydWarshall floyd = new FloydWarshall(nodos, distancias);
            grafosPorEstado.put(idEstado, floyd);
        }
    }

    /**
     * Obtener distancia entre dos municipios del mismo estado.
     */
    public int getDistancia(String idEstado, String origen, String destino) {
        FloydWarshall floyd = grafosPorEstado.get(idEstado);
        if (floyd == null) {
            throw new IllegalArgumentException("** NO SE ENCONTRO EL GRAFO PARA EL ESTADO: " + idEstado + " **");
        }
        return floyd.getDistancia(origen, destino);
    }

    /**
     * Obtener el camino más corto entre dos municipios.
     * Usa RECURSIVIDAD interna en FloydWarshall.getCamino().
     */
    public List<String> getCamino(String idEstado, String origen, String destino) {
        FloydWarshall floyd = grafosPorEstado.get(idEstado);
        if (floyd == null) {
            throw new IllegalArgumentException("** NO SE ENCONTRO EL GRAFO PARA EL ESTADO: " + idEstado + " **");
        }
        return floyd.getCamino(origen, destino);
    }

    /**
     * Obtener los municipios más cercanos a uno dado, ordenados por distancia.
     */
    public List<Map.Entry<String, Integer>> getMunicipiosCercanos(String idEstado, String idMunicipio) {
        FloydWarshall floyd = grafosPorEstado.get(idEstado);
        if (floyd == null) {
            throw new IllegalArgumentException("** NO SE ENCONTRO EL GRAFO PARA EL ESTADO: " + idEstado + " **");
        }
        return floyd.getMunicipiosCercanos(idMunicipio);
    }
}