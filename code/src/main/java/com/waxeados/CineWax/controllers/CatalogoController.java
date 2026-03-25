package com.waxeados.CineWax.controllers;

import com.waxeados.CineWax.dto.*;
import com.waxeados.CineWax.services.CatalogoService;
import com.waxeados.CineWax.mappers.CatalogoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CatalogoController {

    private final CatalogoService catalogoService;
    private final CatalogoMapper catalogoMapper; // Mapper inyectado

    /** GET /api/catalogo/estados */
    @GetMapping("/estados")
    public ResponseEntity<ApiResponse<?>> listarEstados() {
        List<EstadoDTO> data = catalogoService.listarEstados().stream()
                .map(catalogoMapper::toEstadoDTO)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok("Estados disponibles", data));
    }

    /** GET /api/catalogo/municipios/{idEstado} */
    @GetMapping("/municipios/{idEstado}")
    public ResponseEntity<ApiResponse<?>> listarMunicipios(@PathVariable String idEstado) {
        List<MunicipioDTO> data = catalogoService.listarMunicipios(idEstado).stream()
                .map(catalogoMapper::toMunicipioDTO)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok("Municipios del estado " + idEstado, data));
    }

    /** GET /api/catalogo/generos */
    @GetMapping("/generos")
    public ResponseEntity<ApiResponse<?>> listarGeneros() {
        List<GeneroDTO> data = catalogoService.listarGeneros().stream()
                .map(catalogoMapper::toGeneroDTO)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok("Generos disponibles", data));
    }

    /** GET /api/catalogo/salas/{idMunicipio} */
    @GetMapping("/salas/{idMunicipio}")
    public ResponseEntity<ApiResponse<?>> listarSalas(@PathVariable String idMunicipio) {
        List<SalaDTO> data = catalogoService.listarSalas(idMunicipio).stream()
                .map(catalogoMapper::toSalaDTO)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok("Salas del municipio", data));
    }
}