package com.waxeados.CineWax.controllers;

import com.waxeados.CineWax.dto.*;
import com.waxeados.CineWax.entity.Pelicula;
import com.waxeados.CineWax.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/cliente")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClienteController {

    private final MovieService movieService;
    private final HorarioService horarioService;
    private final FloydService floydService;
    private final HistorialService historialService;

    private String validarCliente(HttpSession session) {
        String rol = (String) session.getAttribute("rol");
        if (rol == null) {
            throw new SecurityException("No autenticado.");
        }
        return (String) session.getAttribute("username");
    }

    // ==================== 1. BUSCAR POR NOMBRE ====================

    @GetMapping("/buscar/nombre")
    public ResponseEntity<ApiResponse<?>> buscarPorNombre(@RequestParam String q,
                                                           HttpSession session) {
        try {
            String username = validarCliente(session);
            historialService.registrarAccion(username, "BUSCAR_NOMBRE", q);

            List<PeliculaDetalleDTO> data = movieService.buscarPorNombre(q).stream()
                    .map(this::toPeliculaDetalle).toList();

            return ResponseEntity.ok(ApiResponse.ok("Búsqueda por nombre: " + q, data));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(ApiResponse.unauthorized(e.getMessage()));
        }
    }

    // ==================== 2. BUSCAR POR CLASIFICACIÓN ====================

    @GetMapping("/buscar/clasificacion")
    public ResponseEntity<ApiResponse<?>> buscarPorClasificacion(@RequestParam String c,
                                                                   HttpSession session) {
        try {
            String username = validarCliente(session);
            historialService.registrarAccion(username, "BUSCAR_CLASIFICACION", c);

            List<PeliculaDetalleDTO> data = movieService.buscarPorClasificacion(c).stream()
                    .map(this::toPeliculaDetalle).toList();

            return ResponseEntity.ok(ApiResponse.ok("Búsqueda por clasificación: " + c, data));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(ApiResponse.unauthorized(e.getMessage()));
        }
    }

    // ==================== 3. BUSCAR POR GÉNERO ====================

    @GetMapping("/buscar/genero")
    public ResponseEntity<ApiResponse<?>> buscarPorGenero(@RequestParam Integer id,
                                                           HttpSession session) {
        try {
            String username = validarCliente(session);
            historialService.registrarAccion(username, "BUSCAR_GENERO", "ID:" + id);

            List<PeliculaDetalleDTO> data = movieService.buscarPorGenero(id).stream()
                    .map(this::toPeliculaDetalle).toList();

            return ResponseEntity.ok(ApiResponse.ok("Búsqueda por género ID: " + id, data));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(ApiResponse.unauthorized(e.getMessage()));
        }
    }

    // ==================== 4. ORDENAR CARTELERA (QuickSort A/D) ====================

    @GetMapping("/cartelera/ordenar")
    public ResponseEntity<ApiResponse<?>> ordenarCartelera(
            @RequestParam String municipio,
            @RequestParam(defaultValue = "asc") String orden,
            HttpSession session) {
        try {
            String username = validarCliente(session);
            historialService.registrarAccion(username, "ORDENAR_CARTELERA",
                    municipio + " - " + orden);

            boolean asc = orden.equalsIgnoreCase("asc");
            List<CarteleraDTO> data = horarioService.consultarCartelera(municipio, asc);
            String dir = asc ? "ascendente" : "descendente";
            return ResponseEntity.ok(
                    ApiResponse.ok("Cartelera ordenada " + dir + " (QuickSort)", data));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(ApiResponse.unauthorized(e.getMessage()));
        }
    }

    // ==================== 5. CONSULTAR PELÍCULA ====================

    @GetMapping("/peliculas/{id}")
    public ResponseEntity<ApiResponse<?>> consultarPelicula(@PathVariable Integer id,
                                                             HttpSession session) {
        try {
            String username = validarCliente(session);
            Pelicula p = movieService.consultarPelicula(id);
            historialService.registrarAccion(username, "CONSULTAR_PELICULA", p.getNombre());

            PeliculaDetalleDTO data = toPeliculaDetalle(p);
            return ResponseEntity.ok(ApiResponse.ok("Consulta exitosa", data));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(ApiResponse.unauthorized(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    // ==================== 6. CONSULTAR CARTELERA ====================

    @GetMapping("/cartelera")
    public ResponseEntity<ApiResponse<?>> consultarCartelera(
            @RequestParam String municipio,
            @RequestParam(defaultValue = "asc") String orden,
            HttpSession session) {
        try {
            String username = validarCliente(session);
            historialService.registrarAccion(username, "CONSULTAR_CARTELERA", municipio);

            boolean asc = orden.equalsIgnoreCase("asc");
            List<CarteleraDTO> data = horarioService.consultarCartelera(municipio, asc);
            return ResponseEntity.ok(ApiResponse.ok("Cartelera consultada", data));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(ApiResponse.unauthorized(e.getMessage()));
        }
    }

    // ==================== CARTELERA POR RANGO ====================

    @GetMapping("/cartelera/rango")
    public ResponseEntity<ApiResponse<?>> carteleraRango(
            @RequestParam String municipio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "asc") String orden,
            HttpSession session) {
        try {
            String username = validarCliente(session);
            historialService.registrarAccion(username, "CARTELERA_RANGO",
                    municipio + " " + desde + " - " + hasta);

            boolean asc = orden.equalsIgnoreCase("asc");
            List<CarteleraDTO> data = horarioService.consultarCarteleraRango(
                    municipio, desde, hasta, asc);
            return ResponseEntity.ok(ApiResponse.ok("Cartelera por rango de fechas", data));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(ApiResponse.unauthorized(e.getMessage()));
        }
    }

    // ==================== FLOYD - MUNICIPIOS CERCANOS ====================

    @GetMapping("/floyd/cercanos")
    public ResponseEntity<ApiResponse<?>> municipiosCercanos(
            @RequestParam String estado,
            @RequestParam String municipio,
            HttpSession session) {
        try {
            validarCliente(session);

            var cercanos = floydService.getMunicipiosCercanos(estado, municipio);
            List<FloydCercanosDTO.MunicipioCercanoDTO> lista = cercanos.stream()
                    .map(e -> FloydCercanosDTO.MunicipioCercanoDTO.builder()
                            .idMunicipio(e.getKey())
                            .distancia(e.getValue())
                            .build())
                    .toList();

            FloydCercanosDTO data = FloydCercanosDTO.builder()
                    .origen(municipio)
                    .estado(estado)
                    .municipiosCercanos(lista)
                    .build();

            return ResponseEntity.ok(
                    ApiResponse.ok("Municipios cercanos (Floyd-Warshall)", data));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(ApiResponse.unauthorized(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    /** Camino más corto entre dos municipios (recursividad). */
    @GetMapping("/floyd/camino")
    public ResponseEntity<ApiResponse<?>> caminoCorto(
            @RequestParam String estado,
            @RequestParam String origen,
            @RequestParam String destino,
            HttpSession session) {
        try {
            validarCliente(session);

            List<String> camino = floydService.getCamino(estado, origen, destino);
            int distancia = floydService.getDistancia(estado, origen, destino);

            FloydCaminoDTO data = FloydCaminoDTO.builder()
                    .origen(origen)
                    .destino(destino)
                    .distancia(distancia)
                    .camino(camino)
                    .build();

            return ResponseEntity.ok(
                    ApiResponse.ok("Camino más corto (Floyd + recursividad)", data));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(ApiResponse.unauthorized(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest(e.getMessage()));
        }
    }

    // ==================== HISTORIAL (Pila) ====================

    @GetMapping("/historial")
    public ResponseEntity<ApiResponse<?>> obtenerHistorial(HttpSession session) {
        try {
            String username = validarCliente(session);
            List<HistorialNavegacionDTO> data = historialService.obtenerHistorial(username);
            return ResponseEntity.ok(ApiResponse.ok("Historial de navegación (Pila)", data));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(ApiResponse.unauthorized(e.getMessage()));
        }
    }

    @PostMapping("/historial/regresar")
    public ResponseEntity<ApiResponse<?>> regresar(HttpSession session) {
        try {
            String username = validarCliente(session);
            HistorialNavegacionDTO anterior = historialService.regresar(username);
            if (anterior == null) {
                return ResponseEntity.ok(ApiResponse.ok("No hay acciones anteriores"));
            }
            return ResponseEntity.ok(ApiResponse.ok("Acción anterior recuperada", anterior));
        } catch (SecurityException e) {
            return ResponseEntity.status(401).body(ApiResponse.unauthorized(e.getMessage()));
        }
    }

    // ==================== HELPER ====================

    private PeliculaDetalleDTO toPeliculaDetalle(Pelicula p) {
        List<CarteleraDTO> horarios = horarioService.obtenerHorariosPelicula(p.getIdPelicula());
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
}