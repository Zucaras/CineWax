package com.waxeados.CineWax.controllers;

import com.waxeados.CineWax.dto.*;
import com.waxeados.CineWax.entity.HorarioCartelera;
import com.waxeados.CineWax.entity.Pelicula;
import com.waxeados.CineWax.services.CatalogoService;
import com.waxeados.CineWax.services.HorarioService;
import com.waxeados.CineWax.services.PeliculaService;
import com.waxeados.CineWax.mappers.PeliculaMapper;
import com.waxeados.CineWax.mappers.HorarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final PeliculaService peliculaService;
    private final HorarioService horarioService;
    private final CatalogoService catalogoService;

    // Inyectamos nuestros nuevos mappers
    private final PeliculaMapper peliculaMapper;
    private final HorarioMapper horarioMapper;

    private void validarAdmin(HttpSession session) {
        String rol = (String) session.getAttribute("rol");
        if (rol == null || !rol.equals("ADMINISTRADOR")) {
            throw new SecurityException("Acceso denegado. Se requiere rol ADMINISTRADOR.");
        }
    }

    // ==================== 1. ALTA DE PELÍCULA ====================

    @PostMapping("/peliculas")
    public ResponseEntity<ApiResponse<?>> altaPelicula(@RequestBody PeliculaDTO dto, HttpSession session) {
        validarAdmin(session);
        Pelicula p = peliculaService.altaPelicula(dto);
        return ResponseEntity.ok(ApiResponse.ok("Película creada exitosamente", peliculaMapper.toPeliculaResumen(p)));
    }

    // ==================== 2. ALTA DE HORARIO ====================

    @PostMapping("/horarios")
    public ResponseEntity<ApiResponse<?>> altaHorario(@RequestBody HorarioDTO dto, HttpSession session) {
        validarAdmin(session);
        HorarioCartelera h = horarioService.altaHorario(dto);
        return ResponseEntity.ok(ApiResponse.ok("Horario creado exitosamente", horarioMapper.toHorarioResponse(h)));
    }

    @PostMapping("/horarios/encolar")
    public ResponseEntity<ApiResponse<?>> encolarHorario(@RequestBody HorarioDTO dto, HttpSession session) {
        validarAdmin(session);
        horarioService.encolarSolicitudHorario(dto);
        int pendientes = horarioService.obtenerSolicitudesPendientes().size();
        return ResponseEntity.ok(ApiResponse.ok("Solicitud encolada. Pendientes: " + pendientes));
    }

    @PostMapping("/horarios/procesar-cola")
    public ResponseEntity<ApiResponse<?>> procesarCola(HttpSession session) {
        validarAdmin(session);
        List<String> resultados = horarioService.procesarTodasLasSolicitudes();
        return ResponseEntity.ok(ApiResponse.ok("Cola procesada", resultados));
    }

    // ==================== 3. BAJA DE PELÍCULA ====================

    @DeleteMapping("/peliculas/{id}")
    public ResponseEntity<ApiResponse<Void>> bajaPelicula(@PathVariable Integer id, HttpSession session) {
        validarAdmin(session);
        peliculaService.bajaPelicula(id);
        return ResponseEntity.ok(ApiResponse.ok("Película eliminada exitosamente"));
    }

    // ==================== 4. BAJA DE HORARIO ====================

    @DeleteMapping("/horarios/{id}")
    public ResponseEntity<ApiResponse<Void>> bajaHorario(@PathVariable Integer id, HttpSession session) {
        validarAdmin(session);
        horarioService.bajaHorario(id);
        return ResponseEntity.ok(ApiResponse.ok("Horario eliminado exitosamente"));
    }

    // ==================== 5. MODIFICAR PELÍCULA ====================

    @PutMapping("/peliculas/{id}")
    public ResponseEntity<ApiResponse<?>> modificarPelicula(@PathVariable Integer id, @RequestBody PeliculaDTO dto, HttpSession session) {
        validarAdmin(session);
        Pelicula p = peliculaService.modificarPelicula(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Película modificada exitosamente", peliculaMapper.toPeliculaResumen(p)));
    }

    // ==================== 6. CONSULTAR PELÍCULA ====================

    @GetMapping("/peliculas/{id}")
    public ResponseEntity<ApiResponse<?>> consultarPelicula(@PathVariable Integer id, HttpSession session) {
        validarAdmin(session);
        Pelicula p = peliculaService.consultarPelicula(id);
        List<CarteleraDTO> horarios = horarioService.obtenerHorariosPelicula(id);
        return ResponseEntity.ok(ApiResponse.ok("Consulta exitosa", peliculaMapper.toPeliculaDetalle(p, horarios)));
    }

    @GetMapping("/peliculas")
    public ResponseEntity<ApiResponse<?>> listarPeliculas(HttpSession session) {
        validarAdmin(session);
        List<PeliculaResumenDTO> data = peliculaService.listarPeliculas().stream()
                .map(peliculaMapper::toPeliculaResumen).toList();
        return ResponseEntity.ok(ApiResponse.ok("Listado de películas", data));
    }

    // ==================== 7. CONSULTAR CARTELERA ====================

    @GetMapping("/cartelera")
    public ResponseEntity<ApiResponse<?>> consultarCartelera(
            @RequestParam String municipio,
            @RequestParam(defaultValue = "asc") String orden,
            HttpSession session) {
        validarAdmin(session);
        boolean asc = orden.equalsIgnoreCase("asc");
        List<CarteleraDTO> data = horarioService.consultarCartelera(municipio, asc);
        return ResponseEntity.ok(ApiResponse.ok("Cartelera consultada", data));
    }

    // ==================== SALAS ====================

    @GetMapping("/salas/{idMunicipio}")
    public ResponseEntity<ApiResponse<?>> listarSalas(@PathVariable String idMunicipio, HttpSession session) {
        validarAdmin(session);
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