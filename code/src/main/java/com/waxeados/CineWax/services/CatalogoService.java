package com.waxeados.CineWax.services;

import com.waxeados.CineWax.entity.Estado;
import com.waxeados.CineWax.entity.Genero;
import com.waxeados.CineWax.entity.Municipio;
import com.waxeados.CineWax.entity.Sala;
import com.waxeados.CineWax.respositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de catálogos: estados, municipios, géneros, salas.
 */
@Service
@RequiredArgsConstructor
public class CatalogoService {

    private final EstadoRepository estadoRepository;
    private final MunicipioRepository municipioRepository;
    private final GeneroRepository generoRepository;
    private final SalaRepository salaRepository;

    /** Listar todos los estados. */
    public List<Estado> listarEstados() {
        return estadoRepository.findAll();
    }

    /** Listar municipios de un estado. */
    public List<Municipio> listarMunicipios(String idEstado) {
        return municipioRepository.findByEstado_IdEstado(idEstado);
    }

    /** Listar todos los géneros. */
    public List<Genero> listarGeneros() {
        return generoRepository.findAll();
    }

    /** Listar salas de un municipio. */
    public List<Sala> listarSalas(String idMunicipio) {
        return salaRepository.findByMunicipio_IdMunicipio(idMunicipio);
    }

    /** Obtener un estado por ID. */
    public Estado obtenerEstado(String idEstado) {
        return estadoRepository.findById(idEstado)
                .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado: " + idEstado));
    }

    /** Obtener un municipio por ID. */
    public Municipio obtenerMunicipio(String idMunicipio) {
        return municipioRepository.findById(idMunicipio)
                .orElseThrow(() -> new IllegalArgumentException("Municipio no encontrado: " + idMunicipio));
    }
}