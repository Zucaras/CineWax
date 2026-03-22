package com.waxeados.CineWax.controllers;

import com.waxeados.CineWax.dto.*;
import com.waxeados.CineWax.services.CatalogoService;
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

    /** GET /api/catalogo/estados */
    @GetMapping("/estados")
    public ResponseEntity<ApiResponse<?>> listarEstados() {
        List<EstadoDTO> data = catalogoService.listarEstados().stream()
                .map(e -> EstadoDTO.builder()
                        .idEstado(e.getIdEstado())
                        .nombreEstado(e.getNombreEstado())
                        .build())
                .toList();
        return ResponseEntity.ok(ApiResponse.ok("Estados disponibles", data));
    }

    /** GET /api/catalogo/municipios/{idEstado} */
    @GetMapping("/municipios/{idEstado}")
    public ResponseEntity<ApiResponse<?>> listarMunicipios(@PathVariable String idEstado) {
        List<MunicipioDTO> data = catalogoService.listarMunicipios(idEstado).stream()
                .map(m -> MunicipioDTO.builder()
                        .idMunicipio(m.getIdMunicipio())
                        .letraMunicipio(m.getLetraMunicipio().toString())
                        .nombreMunicipio(m.getNombreMunicipio())
                        .build())
                .toList();
        return ResponseEntity.ok(ApiResponse.ok("Municipios del estado " + idEstado, data));
    }

    /** GET /api/catalogo/generos */
    @GetMapping("/generos")
    public ResponseEntity<ApiResponse<?>> listarGeneros() {
        List<GeneroDTO> data = catalogoService.listarGeneros().stream()
                .map(g -> GeneroDTO.builder()
                        .idGenero(g.getIdGenero())
                        .nombreGenero(g.getNombreGenero())
                        .build())
                .toList();
        return ResponseEntity.ok(ApiResponse.ok("Géneros disponibles", data));
    }

    /** GET /api/catalogo/salas/{idMunicipio} */
    @GetMapping("/salas/{idMunicipio}")
    public ResponseEntity<ApiResponse<?>> listarSalas(@PathVariable String idMunicipio) {
        List<SalaDTO> data = catalogoService.listarSalas(idMunicipio).stream()
                .map(s -> SalaDTO.builder()
                        .idSala(s.getIdSala())
                        .numeroSala(s.getNumeroSala())
                        .municipio(s.getMunicipio().getNombreMunicipio())
                        .build())
                .toList();
        return ResponseEntity.ok(ApiResponse.ok("Salas del municipio", data));
    }
}