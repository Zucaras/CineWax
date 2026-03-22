package com.waxeados.CineWax.controllers;

import com.waxeados.CineWax.dto.*;
import com.waxeados.CineWax.entity.HorarioCartelera;
import com.waxeados.CineWax.entity.Pelicula;
import com.waxeados.CineWax.services.CatalogoService;
import com.waxeados.CineWax.services.HorarioService;
import com.waxeados.CineWax.services.MovieService;
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

    private final MovieService movieService;
    private final HorarioService horarioService;
    private final CatalogoService catalogoService;

    private void validarAdmin(HttpSession session) {
        String rol = (String) session.getAttribute("rol");
        if (rol == null || !rol.equals("ADMINISTRADOR")) {
            throw new SecurityException("Acceso denegado. Se requiere rol ADMINISTRADOR.");
        }
    }

    // ==================== 1. ALTA DE PELÍCULA ====================

    @PostMapping("/peliculas")
    public ResponseEntity<ApiResponse<?>> altaPelicula(@RequestBody PeliculaDTO dto,
                                                        HttpSession session) {
        try {
            validarAdmin(session);
            Pelicula p = movieService.altaPelicula(dto);
            PeliculaResumenDTO data = toPeliculaResumen(p);
            return ResponseEntity.ok(ApiResponse.ok("Película creada exitosamente", data));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(ApiResponse.forbidden(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    // ==================== 2. ALTA DE HORARIO ====================

    @PostMapping("/horarios")
    public ResponseEntity<ApiResponse<?>> altaHorario(@RequestBody HorarioDTO dto,
                                                       HttpSession session) {
        try {
            validarAdmin(session);
            HorarioCartelera h = horarioService.altaHorario(dto);
            HorarioResponseDTO data = toHorarioResponse(h);
            return ResponseEntity.ok(ApiResponse.ok("Horario creado exitosamente", data));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(ApiResponse.forbidden(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /** Encola una solicitud de horario (Cola FIFO). */
    @PostMapping("/horarios/encolar")
    public ResponseEntity<ApiResponse<?>> encolarHorario(@RequestBody HorarioDTO dto,
                                                          HttpSession session) {
        try {
            validarAdmin(session);
            horarioService.encolarSolicitudHorario(dto);
            int pendientes = horarioService.obtenerSolicitudesPendientes().size();
            return ResponseEntity.ok(
                    ApiResponse.ok("Solicitud encolada. Pendientes: " + pendientes));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(ApiResponse.forbidden(e.getMessage()));
        }
    }

    /** Procesa toda la cola de solicitudes. */
    @PostMapping("/horarios/procesar-cola")
    public ResponseEntity<ApiResponse<?>> procesarCola(HttpSession session) {
        try {
            validarAdmin(session);
            List<String> resultados = horarioService.procesarTodasLasSolicitudes();
            return ResponseEntity.ok(ApiResponse.ok("Cola procesada", resultados));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(ApiResponse.forbidden(e.getMessage()));
        }
    }

    // ==================== 3. BAJA DE PELÍCULA ====================

    @DeleteMapping("/peliculas/{id}")
    public ResponseEntity<ApiResponse<Void>> bajaPelicula(@PathVariable Integer id,
                                                           HttpSession session) {
        try {
            validarAdmin(session);
            movieService.bajaPelicula(id);
            return ResponseEntity.ok(ApiResponse.ok("Película eliminada exitosamente"));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(ApiResponse.forbidden(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    // ==================== 4. BAJA DE HORARIO ====================

    @DeleteMapping("/horarios/{id}")
    public ResponseEntity<ApiResponse<Void>> bajaHorario(@PathVariable Integer id,
                                                          HttpSession session) {
        try {
            validarAdmin(session);
            horarioService.bajaHorario(id);
            return ResponseEntity.ok(ApiResponse.ok("Horario eliminado exitosamente"));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(ApiResponse.forbidden(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    // ==================== 5. MODIFICAR PELÍCULA ====================

    @PutMapping("/peliculas/{id}")
    public ResponseEntity<ApiResponse<?>> modificarPelicula(@PathVariable Integer id,
                                                             @RequestBody PeliculaDTO dto,
                                                             HttpSession session) {
        try {
            validarAdmin(session);
            Pelicula p = movieService.modificarPelicula(id, dto);
            PeliculaResumenDTO data = toPeliculaResumen(p);
            return ResponseEntity.ok(ApiResponse.ok("Película modificada exitosamente", data));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(ApiResponse.forbidden(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    // ==================== 6. CONSULTAR PELÍCULA ====================

    @GetMapping("/peliculas/{id}")
    public ResponseEntity<ApiResponse<?>> consultarPelicula(@PathVariable Integer id,
                                                             HttpSession session) {
        try {
            validarAdmin(session);
            Pelicula p = movieService.consultarPelicula(id);
            List<CarteleraDTO> horarios = horarioService.obtenerHorariosPelicula(id);
            PeliculaDetalleDTO data = toPeliculaDetalle(p, horarios);
            return ResponseEntity.ok(ApiResponse.ok("Consulta exitosa", data));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(ApiResponse.forbidden(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /** Listar todas las películas. */
    @GetMapping("/peliculas")
    public ResponseEntity<ApiResponse<?>> listarPeliculas(HttpSession session) {
        try {
            validarAdmin(session);
            List<PeliculaResumenDTO> data = movieService.listarPeliculas().stream()
                    .map(this::toPeliculaResumen).toList();
            return ResponseEntity.ok(ApiResponse.ok("Listado de películas", data));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(ApiResponse.forbidden(e.getMessage()));
        }
    }

    // ==================== 7. CONSULTAR CARTELERA ====================

    @GetMapping("/cartelera")
    public ResponseEntity<ApiResponse<?>> consultarCartelera(
            @RequestParam String municipio,
            @RequestParam(defaultValue = "asc") String orden,
            HttpSession session) {
        try {
            validarAdmin(session);
            boolean asc = orden.equalsIgnoreCase("asc");
            List<CarteleraDTO> data = horarioService.consultarCartelera(municipio, asc);
            return ResponseEntity.ok(ApiResponse.ok("Cartelera consultada", data));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(ApiResponse.forbidden(e.getMessage()));
        }
    }

    // ==================== SALAS ====================

    @GetMapping("/salas/{idMunicipio}")
    public ResponseEntity<ApiResponse<?>> listarSalas(@PathVariable String idMunicipio,
                                                       HttpSession session) {
        try {
            validarAdmin(session);
            List<SalaDTO> data = catalogoService.listarSalas(idMunicipio).stream()
                    .map(s -> SalaDTO.builder()
                            .idSala(s.getIdSala())
                            .numeroSala(s.getNumeroSala())
                            .municipio(s.getMunicipio().getNombreMunicipio())
                            .build())
                    .toList();
            return ResponseEntity.ok(ApiResponse.ok("Salas del municipio", data));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(ApiResponse.forbidden(e.getMessage()));
        }
    }

    // ==================== HELPERS ====================

    private PeliculaResumenDTO toPeliculaResumen(Pelicula p) {
        return PeliculaResumenDTO.builder()
                .idPelicula(p.getIdPelicula())
                .nombre(p.getNombre())
                .clasificacion(p.getClasificacion())
                .duracionMin(p.getDuracionMin())
                .genero(p.getGenero().getNombreGenero())
                .build();
    }

    private PeliculaDetalleDTO toPeliculaDetalle(Pelicula p, List<CarteleraDTO> horarios) {
        return PeliculaDetalleDTO.builder()
                .idPelicula(p.getIdPelicula())
                .nombre(p.getNombre())
                .director(p.getDirector())
                .productor(p.getProductor())
                .clasificacion(p.getClasificacion())
                .duracionMin(p.getDuracionMin())
                .genero(p.getGenero().getNombreGenero())
                .horarios(horarios)
                .build();
    }

    private HorarioResponseDTO toHorarioResponse(HorarioCartelera h) {
        return HorarioResponseDTO.builder()
                .idHorario(h.getIdHorario())
                .pelicula(h.getPelicula().getNombre())
                .sala(h.getSala().getNumeroSala())
                .fecha(h.getFechaProyeccion().toString())
                .horaInicio(h.getHoraInicio().toString())
                .horaFinEstimada(h.getHoraFinEstimada().toString())
                .build();
    }
}