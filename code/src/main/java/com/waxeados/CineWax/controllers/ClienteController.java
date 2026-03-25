package com.waxeados.CineWax.controllers;

import com.waxeados.CineWax.dto.*;
import com.waxeados.CineWax.entity.Pelicula;
import com.waxeados.CineWax.services.*;
import com.waxeados.CineWax.mappers.PeliculaMapper;
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

    private final PeliculaService peliculaService;
    private final HorarioService horarioService;
    private final FloydService floydService;
    private final HistorialService historialService;
    private final PeliculaMapper peliculaMapper; // Mapper inyectado

    private String validarCliente(HttpSession session) {
        String rol = (String) session.getAttribute("rol");
        if (rol == null) {
            // El GlobalExceptionHandler atrapa esto y devuelve un 403 limpio
            throw new SecurityException("No autenticado.");
        }
        return (String) session.getAttribute("username");
    }

    // ==================== 1, 2 y 3. BÚSQUEDAS ====================

    @GetMapping("/buscar/nombre")
    public ResponseEntity<ApiResponse<?>> buscarPorNombre(@RequestParam String q, HttpSession session) {
        String username = validarCliente(session);
        historialService.registrarAccion(username, "BUSCAR_NOMBRE", q);

        List<PeliculaDetalleDTO> data = peliculaService.buscarPorNombre(q).stream()
                .map(p -> peliculaMapper.toPeliculaDetalle(p, horarioService.obtenerHorariosPelicula(p.getIdPelicula())))
                .toList();

        return ResponseEntity.ok(ApiResponse.ok("Búsqueda por nombre: " + q, data));
    }

    @GetMapping("/buscar/clasificacion")
    public ResponseEntity<ApiResponse<?>> buscarPorClasificacion(@RequestParam String c, HttpSession session) {
        String username = validarCliente(session);
        historialService.registrarAccion(username, "BUSCAR_CLASIFICACION", c);

        List<PeliculaDetalleDTO> data = peliculaService.buscarPorClasificacion(c).stream()
                .map(p -> peliculaMapper.toPeliculaDetalle(p, horarioService.obtenerHorariosPelicula(p.getIdPelicula())))
                .toList();

        return ResponseEntity.ok(ApiResponse.ok("Búsqueda por clasificación: " + c, data));
    }

    @GetMapping("/buscar/genero")
    public ResponseEntity<ApiResponse<?>> buscarPorGenero(@RequestParam Integer id, HttpSession session) {
        String username = validarCliente(session);
        historialService.registrarAccion(username, "BUSCAR_GENERO", "ID:" + id);

        List<PeliculaDetalleDTO> data = peliculaService.buscarPorGenero(id).stream()
                .map(p -> peliculaMapper.toPeliculaDetalle(p, horarioService.obtenerHorariosPelicula(p.getIdPelicula())))
                .toList();

        return ResponseEntity.ok(ApiResponse.ok("Búsqueda por género ID: " + id, data));
    }

    // ==================== 4. ORDENAR CARTELERA (QuickSort A/D) ====================

    @GetMapping("/cartelera/ordenar")
    public ResponseEntity<ApiResponse<?>> ordenarCartelera(
            @RequestParam String municipio,
            @RequestParam(defaultValue = "asc") String orden,
            HttpSession session) {

        String username = validarCliente(session);
        historialService.registrarAccion(username, "ORDENAR_CARTELERA", municipio + " - " + orden);

        boolean asc = orden.equalsIgnoreCase("asc");
        List<CarteleraDTO> data = horarioService.consultarCartelera(municipio, asc);
        String dir = asc ? "ascendente" : "descendente";

        return ResponseEntity.ok(ApiResponse.ok("Cartelera ordenada " + dir + " (QuickSort)", data));
    }

    // ==================== 5. CONSULTAR PELÍCULA ====================

    @GetMapping("/peliculas/{id}")
    public ResponseEntity<ApiResponse<?>> consultarPelicula(@PathVariable Integer id, HttpSession session) {
        String username = validarCliente(session);
        Pelicula p = peliculaService.consultarPelicula(id);
        historialService.registrarAccion(username, "CONSULTAR_PELICULA", p.getNombre());

        List<CarteleraDTO> horarios = horarioService.obtenerHorariosPelicula(p.getIdPelicula());
        return ResponseEntity.ok(ApiResponse.ok("Consulta exitosa", peliculaMapper.toPeliculaDetalle(p, horarios)));
    }

    // ==================== 6. CONSULTAR CARTELERA ====================

    @GetMapping("/cartelera")
    public ResponseEntity<ApiResponse<?>> consultarCartelera(
            @RequestParam String municipio,
            @RequestParam(defaultValue = "asc") String orden,
            HttpSession session) {

        String username = validarCliente(session);
        historialService.registrarAccion(username, "CONSULTAR_CARTELERA", municipio);

        boolean asc = orden.equalsIgnoreCase("asc");
        List<CarteleraDTO> data = horarioService.consultarCartelera(municipio, asc);
        return ResponseEntity.ok(ApiResponse.ok("Cartelera consultada", data));
    }

    @GetMapping("/cartelera/rango")
    public ResponseEntity<ApiResponse<?>> carteleraRango(
            @RequestParam String municipio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "asc") String orden,
            HttpSession session) {

        String username = validarCliente(session);
        historialService.registrarAccion(username, "CARTELERA_RANGO", municipio + " " + desde + " - " + hasta);

        boolean asc = orden.equalsIgnoreCase("asc");
        List<CarteleraDTO> data = horarioService.consultarCarteleraRango(municipio, desde, hasta, asc);
        return ResponseEntity.ok(ApiResponse.ok("Cartelera por rango de fechas", data));
    }

    // ==================== FLOYD - MUNICIPIOS CERCANOS ====================

    @GetMapping("/floyd/cercanos")
    public ResponseEntity<ApiResponse<?>> municipiosCercanos(
            @RequestParam String estado,
            @RequestParam String municipio,
            HttpSession session) {

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

        return ResponseEntity.ok(ApiResponse.ok("Municipios cercanos (Floyd-Warshall)", data));
    }

    @GetMapping("/floyd/camino")
    public ResponseEntity<ApiResponse<?>> caminoCorto(
            @RequestParam String estado,
            @RequestParam String origen,
            @RequestParam String destino,
            HttpSession session) {

        validarCliente(session);

        List<String> camino = floydService.getCamino(estado, origen, destino);
        int distancia = floydService.getDistancia(estado, origen, destino);

        FloydCaminoDTO data = FloydCaminoDTO.builder()
                .origen(origen)
                .destino(destino)
                .distancia(distancia)
                .camino(camino)
                .build();

        return ResponseEntity.ok(ApiResponse.ok("Camino más corto (Floyd + recursividad)", data));
    }

    // ==================== HISTORIAL (Pila) ====================

    @GetMapping("/historial")
    public ResponseEntity<ApiResponse<?>> obtenerHistorial(HttpSession session) {
        String username = validarCliente(session);
        List<HistorialNavegacionDTO> data = historialService.obtenerHistorial(username);
        return ResponseEntity.ok(ApiResponse.ok("Historial de navegación (Pila)", data));
    }

    @PostMapping("/historial/regresar")
    public ResponseEntity<ApiResponse<?>> regresar(HttpSession session) {
        String username = validarCliente(session);
        HistorialNavegacionDTO anterior = historialService.regresar(username);

        if (anterior == null) {
            return ResponseEntity.ok(ApiResponse.ok("No hay acciones anteriores"));
        }
        return ResponseEntity.ok(ApiResponse.ok("Acción anterior recuperada", anterior));
    }
}