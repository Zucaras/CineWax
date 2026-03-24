package com.waxeados.CineWax.console;

import com.waxeados.CineWax.dto.*;
import com.waxeados.CineWax.entity.*;
import com.waxeados.CineWax.mappers.HorarioMapper;
import com.waxeados.CineWax.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Interfaz de terminal para CineWax.
 * Se activa con el perfil "console": spring.profiles.active=console
 *
 * Llama directamente a los Services (NO a los Controllers),
 * reutilizando toda la lógica de negocio, validaciones y estructuras de datos.
 */
@Component
@Profile("console")
@RequiredArgsConstructor
public class ConsoleRunner implements CommandLineRunner {

    private final UserService userService;
    private final PeliculaService peliculaService;
    private final HorarioService horarioService;
    private final CatalogoService catalogoService;
    private final HistorialService historialService;
    private final HorarioMapper horarioMapper;

    private final Scanner scanner = new Scanner(System.in);

    // Usuario logueado actualmente
    private Usuario usuarioActual = null;

    @Override
    public void run(String... args) {
        System.out.println(banner());
        menuPrincipal();
    }

    // ══════════════════════════════════════════════════════════
    //  MENÚ PRINCIPAL
    // ══════════════════════════════════════════════════════════

    private void menuPrincipal() {
        while (true) {
            System.out.println("\n╔══════════════════════════════════╗");
            System.out.println("║       CINEWAX - CARTELERA        ║");
            System.out.println("╠══════════════════════════════════╣");
            System.out.println("║  1. Iniciar sesión               ║");
            System.out.println("║  2. Registrarse                  ║");
            System.out.println("║  3. Salir                        ║");
            System.out.println("╚══════════════════════════════════╝");
            System.out.print("Opción: ");

            int op = leerEntero();
            switch (op) {
                case 1 -> iniciarSesion();
                case 2 -> registrarse();
                case 3 -> { System.out.println("¡Hasta luego!"); return; }
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    // ══════════════════════════════════════════════════════════
    //  AUTH
    // ══════════════════════════════════════════════════════════

    private void iniciarSesion() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        try {
            LoginDTO dto = new LoginDTO(username, password);
            usuarioActual = userService.login(dto);
            System.out.println("✓ Bienvenido, " + usuarioActual.getUsername()
                    + " [" + usuarioActual.getRolUsuario() + "]");

            if (usuarioActual.getRolUsuario() == Usuario.Rol.ADMINISTRADOR) {
                menuAdmin();
            } else {
                menuCliente();
            }
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private void registrarse() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Rol (ADMINISTRADOR / CLIENTE): ");
        String rol = scanner.nextLine().trim().toUpperCase();

        String idMunicipio = null;
        if (rol.equals("ADMINISTRADOR")) {
            mostrarEstadosYMunicipios();
            System.out.print("ID Municipio (ej: J11A): ");
            idMunicipio = scanner.nextLine().trim();
        }

        try {
            RegistroDTO dto = new RegistroDTO(username, password, rol, idMunicipio);
            Usuario u = userService.registrar(dto);
            System.out.println("✓ Usuario registrado: " + u.getUsername() + " [" + u.getRolUsuario() + "]");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    //  MENÚ ADMINISTRADOR
    // ══════════════════════════════════════════════════════════

    private void menuAdmin() {
        while (true) {
            System.out.println("\n╔══════════════════════════════════╗");
            System.out.println("║      MENÚ ADMINISTRADOR          ║");
            System.out.println("╠══════════════════════════════════╣");
            System.out.println("║  1. Alta de Película             ║");
            System.out.println("║  2. Alta de Horario              ║");
            System.out.println("║  3. Baja de Película             ║");
            System.out.println("║  4. Baja de Horario              ║");
            System.out.println("║  5. Modificar Película           ║");
            System.out.println("║  6. Consultar Película           ║");
            System.out.println("║  7. Consultar Cartelera          ║");
            System.out.println("║  8. Salir                        ║");
            System.out.println("╚══════════════════════════════════╝");
            System.out.print("Opción: ");

            int op = leerEntero();
            switch (op) {
                case 1 -> altaPelicula();
                case 2 -> altaHorario();
                case 3 -> bajaPelicula();
                case 4 -> bajaHorario();
                case 5 -> modificarPelicula();
                case 6 -> consultarPelicula();
                case 7 -> consultarCartelera();
                case 8 -> { usuarioActual = null; return; }
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    // ══════════════════════════════════════════════════════════
    //  MENÚ CLIENTE
    // ══════════════════════════════════════════════════════════

    private void menuCliente() {
        while (true) {
            System.out.println("\n╔══════════════════════════════════╗");
            System.out.println("║         MENÚ CLIENTE             ║");
            System.out.println("╠══════════════════════════════════╣");
            System.out.println("║  1. Buscar película por nombre   ║");
            System.out.println("║  2. Buscar por clasificación     ║");
            System.out.println("║  3. Buscar por género            ║");
            System.out.println("║  4. Ordenar Cartelera (A y D)    ║");
            System.out.println("║  5. Consultar Película           ║");
            System.out.println("║  6. Consultar Cartelera          ║");
            System.out.println("║  7. Salir                        ║");
            System.out.println("╚══════════════════════════════════╝");
            System.out.print("Opción: ");

            int op = leerEntero();
            switch (op) {
                case 1 -> buscarPorNombre();
                case 2 -> buscarPorClasificacion();
                case 3 -> buscarPorGenero();
                case 4 -> ordenarCartelera();
                case 5 -> consultarPelicula();
                case 6 -> consultarCartelera();
                case 7 -> { usuarioActual = null; return; }
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    // ══════════════════════════════════════════════════════════
    //  FUNCIONES ADMIN
    // ══════════════════════════════════════════════════════════

    private void altaPelicula() {
        System.out.println("\n-|Introduzca los datos|-");
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine().trim();
        System.out.print("Director: ");
        String director = scanner.nextLine().trim();
        System.out.print("Productor: ");
        String productor = scanner.nextLine().trim();
        System.out.print("Clasificación (AA, A, B, B15, C, D): ");
        String clasificacion = scanner.nextLine().trim();
        System.out.print("Duración (minutos): ");
        int duracion = leerEntero();

        mostrarGeneros();
        System.out.print("Género (Elija una opción): ");
        int idGenero = leerEntero();

        try {
            PeliculaDTO dto = PeliculaDTO.builder()
                    .nombre(nombre).director(director).productor(productor)
                    .clasificacion(clasificacion).duracionMin(duracion).idGenero(idGenero)
                    .build();
            Pelicula p = peliculaService.altaPelicula(dto);
            System.out.println("✓ Película creada: " + p.getNombre() + " (ID: " + p.getIdPelicula() + ")");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private void altaHorario() {
        listarPeliculasResumen();
        System.out.print("ID Película: ");
        int idPelicula = leerEntero();

        seleccionarMunicipio();
        System.out.print("ID Sala: ");
        int idSala = leerEntero();

        System.out.print("Fecha (dd/MM/yyyy): ");
        LocalDate fecha = leerFecha();
        System.out.print("Hora inicio (HH:mm): ");
        LocalTime hora = leerHora();

        try {
            HorarioDTO dto = HorarioDTO.builder()
                    .idPelicula(idPelicula).idSala(idSala)
                    .fechaProyeccion(fecha).horaInicio(hora)
                    .build();
            HorarioCartelera h = horarioService.altaHorario(dto);
            HorarioResponseDTO resp = horarioMapper.toHorarioResponse(h);
            System.out.println("✓ Horario creado:");
            System.out.printf("  Película: %s | Sala: %d | Fecha: %s | %s - %s%n",
                    resp.getPelicula(), resp.getSala(), resp.getFecha(),
                    resp.getHoraInicio(), resp.getHoraFinEstimada());
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private void bajaPelicula() {
        listarPeliculasResumen();
        System.out.print("ID de la película a eliminar: ");
        int id = leerEntero();

        try {
            peliculaService.bajaPelicula(id);
            System.out.println("✓ Película eliminada.");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private void bajaHorario() {
        System.out.print("ID del horario a eliminar: ");
        int id = leerEntero();

        try {
            horarioService.bajaHorario(id);
            System.out.println("✓ Horario eliminado.");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private void modificarPelicula() {
        listarPeliculasResumen();
        System.out.print("ID de la película a modificar: ");
        int id = leerEntero();

        try {
            Pelicula actual = peliculaService.consultarPelicula(id);
            System.out.println("Datos actuales: " + actual.getNombre() + " | "
                    + actual.getDirector() + " | " + actual.getClasificacion()
                    + " | " + actual.getDuracionMin() + " min");

            System.out.println("(Presiona ENTER para mantener el valor actual)");

            System.out.print("Nombre [" + actual.getNombre() + "]: ");
            String nombre = scanner.nextLine().trim();
            if (nombre.isEmpty()) nombre = actual.getNombre();

            System.out.print("Director [" + actual.getDirector() + "]: ");
            String director = scanner.nextLine().trim();
            if (director.isEmpty()) director = actual.getDirector();

            System.out.print("Productor [" + actual.getProductor() + "]: ");
            String productor = scanner.nextLine().trim();
            if (productor.isEmpty()) productor = actual.getProductor();

            System.out.print("Clasificación [" + actual.getClasificacion() + "]: ");
            String clasif = scanner.nextLine().trim();
            if (clasif.isEmpty()) clasif = actual.getClasificacion();

            System.out.print("Duración min [" + actual.getDuracionMin() + "]: ");
            String durStr = scanner.nextLine().trim();
            int duracion = durStr.isEmpty() ? actual.getDuracionMin() : Integer.parseInt(durStr);

            mostrarGeneros();
            System.out.print("Género [" + actual.getGenero().getIdGenero() + "]: ");
            String genStr = scanner.nextLine().trim();
            int idGenero = genStr.isEmpty() ? actual.getGenero().getIdGenero() : Integer.parseInt(genStr);

            PeliculaDTO dto = PeliculaDTO.builder()
                    .nombre(nombre).director(director).productor(productor)
                    .clasificacion(clasif).duracionMin(duracion).idGenero(idGenero)
                    .build();
            Pelicula p = peliculaService.modificarPelicula(id, dto);
            System.out.println("✓ Película modificada: " + p.getNombre());
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    //  FUNCIONES COMPARTIDAS (Admin y Cliente)
    // ══════════════════════════════════════════════════════════

    private void consultarPelicula() {
        listarPeliculasResumen();
        System.out.print("ID de la película: ");
        int id = leerEntero();

        try {
            Pelicula p = peliculaService.consultarPelicula(id);
            List<CarteleraDTO> horarios = horarioService.obtenerHorariosPelicula(id);

            if (usuarioActual != null) {
                historialService.registrarAccion(usuarioActual.getUsername(), "CONSULTAR_PELICULA", p.getNombre());
            }

            System.out.println("\n══════════════════════════════════");
            System.out.println("  " + p.getNombre());
            System.out.println("══════════════════════════════════");
            System.out.println("  Director:       " + p.getDirector());
            System.out.println("  Productor:      " + p.getProductor());
            System.out.println("  Clasificación:  " + p.getClasificacion());
            System.out.println("  Duración:       " + p.getDuracionMin() + " min");
            System.out.println("  Género:         " + p.getGenero().getNombreGenero());

            if (!horarios.isEmpty()) {
                System.out.println("\n  Horarios:");
                imprimirCartelera(horarios);
            } else {
                System.out.println("\n  Sin horarios registrados.");
            }
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private void consultarCartelera() {
        String idMunicipio = seleccionarMunicipio();
        System.out.print("Orden (1=Ascendente, 2=Descendente): ");
        int orden = leerEntero();
        boolean asc = (orden != 2);

        try {
            List<CarteleraDTO> cartelera = horarioService.consultarCartelera(idMunicipio, asc);

            if (usuarioActual != null) {
                historialService.registrarAccion(usuarioActual.getUsername(), "CONSULTAR_CARTELERA", idMunicipio);
            }

            if (cartelera.isEmpty()) {
                System.out.println("No hay funciones registradas en este municipio.");
            } else {
                System.out.println("\n||-CARTELERA-||");
                imprimirCartelera(cartelera);
            }
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    //  FUNCIONES CLIENTE
    // ══════════════════════════════════════════════════════════

    private void buscarPorNombre() {
        System.out.print("Nombre de película: ");
        String q = scanner.nextLine().trim();

        try {
            historialService.registrarAccion(usuarioActual.getUsername(), "BUSCAR_NOMBRE", q);
            List<Pelicula> peliculas = peliculaService.buscarPorNombre(q);

            if (peliculas.isEmpty()) {
                System.out.println("No se encontraron películas con: " + q);
                return;
            }

            for (Pelicula p : peliculas) {
                List<CarteleraDTO> horarios = horarioService.obtenerHorariosPelicula(p.getIdPelicula());
                System.out.println("\n► " + p.getNombre() + " [" + p.getClasificacion() + "] - "
                        + p.getGenero().getNombreGenero());
                if (!horarios.isEmpty()) {
                    imprimirCartelera(horarios);
                } else {
                    System.out.println("  Sin horarios.");
                }
            }
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private void buscarPorClasificacion() {
        System.out.print("Clasificación (AA, A, B, B15, C, D): ");
        String c = scanner.nextLine().trim();

        try {
            historialService.registrarAccion(usuarioActual.getUsername(), "BUSCAR_CLASIFICACION", c);
            List<Pelicula> peliculas = peliculaService.buscarPorClasificacion(c);

            if (peliculas.isEmpty()) {
                System.out.println("No se encontraron películas con clasificación: " + c);
                return;
            }

            for (Pelicula p : peliculas) {
                List<CarteleraDTO> horarios = horarioService.obtenerHorariosPelicula(p.getIdPelicula());
                System.out.println("\n► " + p.getNombre() + " [" + p.getClasificacion() + "]");
                if (!horarios.isEmpty()) imprimirCartelera(horarios);
                else System.out.println("  Sin horarios.");
            }
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private void buscarPorGenero() {
        mostrarGeneros();
        System.out.print("ID Género: ");
        int id = leerEntero();

        try {
            historialService.registrarAccion(usuarioActual.getUsername(), "BUSCAR_GENERO", "ID:" + id);
            List<Pelicula> peliculas = peliculaService.buscarPorGenero(id);

            if (peliculas.isEmpty()) {
                System.out.println("No se encontraron películas con ese género.");
                return;
            }

            for (Pelicula p : peliculas) {
                List<CarteleraDTO> horarios = horarioService.obtenerHorariosPelicula(p.getIdPelicula());
                System.out.println("\n► " + p.getNombre() + " - " + p.getGenero().getNombreGenero());
                if (!horarios.isEmpty()) imprimirCartelera(horarios);
                else System.out.println("  Sin horarios.");
            }
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private void ordenarCartelera() {
        String idMunicipio = seleccionarMunicipio();
        System.out.print("Orden (1=Ascendente, 2=Descendente): ");
        int orden = leerEntero();
        boolean asc = (orden != 2);

        try {
            historialService.registrarAccion(usuarioActual.getUsername(), "ORDENAR_CARTELERA",
                    idMunicipio + (asc ? " ASC" : " DESC"));
            List<CarteleraDTO> cartelera = horarioService.consultarCartelera(idMunicipio, asc);

            String dir = asc ? "ascendente" : "descendente";
            System.out.println("\n||-CARTELERA ORDENADA " + dir.toUpperCase() + " (QuickSort)-||");

            if (cartelera.isEmpty()) {
                System.out.println("No hay funciones registradas.");
            } else {
                imprimirCartelera(cartelera);
            }
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════
    //  HELPERS DE IMPRESIÓN
    // ══════════════════════════════════════════════════════════

    private void imprimirCartelera(List<CarteleraDTO> cartelera) {
        System.out.printf("  %-4s %-25s %-6s %-8s %-8s %-12s%n",
                "#", "Nombre", "Sala", "Hora", "Fin", "Fecha");
        System.out.println("  " + "─".repeat(70));

        int i = 1;
        for (CarteleraDTO c : cartelera) {
            System.out.printf("  %-4d %-25s %-6d %-8s %-8s %-12s%n",
                    i++,
                    c.getNombrePelicula().length() > 25
                            ? c.getNombrePelicula().substring(0, 22) + "..."
                            : c.getNombrePelicula(),
                    c.getNumeroSala(),
                    c.getHoraInicio(),
                    c.getHoraFinEstimada(),
                    c.getFecha());
        }
    }

    private void listarPeliculasResumen() {
        List<Pelicula> peliculas = peliculaService.listarPeliculas();
        if (peliculas.isEmpty()) {
            System.out.println("No hay películas registradas.");
            return;
        }
        System.out.println("\n  Películas disponibles:");
        for (Pelicula p : peliculas) {
            System.out.printf("    %d. %s [%s] - %d min - %s%n",
                    p.getIdPelicula(), p.getNombre(), p.getClasificacion(),
                    p.getDuracionMin(), p.getGenero().getNombreGenero());
        }
    }

    private void mostrarGeneros() {
        System.out.println("\n  GÉNERO");
        List<Genero> generos = catalogoService.listarGeneros();
        for (Genero g : generos) {
            System.out.println("    " + g.getIdGenero() + ". " + g.getNombreGenero());
        }
    }

    private void mostrarEstadosYMunicipios() {
        List<Estado> estados = catalogoService.listarEstados();
        for (Estado e : estados) {
            System.out.println("\n  " + e.getIdEstado() + " - " + e.getNombreEstado());
            List<Municipio> municipios = catalogoService.listarMunicipios(e.getIdEstado());
            for (Municipio m : municipios) {
                System.out.println("    " + m.getIdMunicipio() + " (" + m.getLetraMunicipio()
                        + ") " + m.getNombreMunicipio());
            }
        }
    }

    private String seleccionarMunicipio() {
        System.out.println("\nEstados disponibles:");
        List<Estado> estados = catalogoService.listarEstados();
        for (Estado e : estados) {
            System.out.println("  " + e.getIdEstado() + " - " + e.getNombreEstado());
        }
        System.out.print("ID Estado: ");
        String idEstado = scanner.nextLine().trim();

        List<Municipio> municipios = catalogoService.listarMunicipios(idEstado);
        System.out.println("\nMunicipios:");
        for (Municipio m : municipios) {
            System.out.println("  " + m.getIdMunicipio() + " - " + m.getNombreMunicipio());
        }
        System.out.print("ID Municipio: ");
        return scanner.nextLine().trim();
    }

    // ══════════════════════════════════════════════════════════
    //  HELPERS DE LECTURA
    // ══════════════════════════════════════════════════════════

    private int leerEntero() {
        while (true) {
            try {
                String linea = scanner.nextLine().trim();
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.print("Ingrese un número válido: ");
            }
        }
    }

    private LocalDate leerFecha() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while (true) {
            try {
                return LocalDate.parse(scanner.nextLine().trim(), fmt);
            } catch (DateTimeParseException e) {
                System.out.print("Formato inválido. Use dd/MM/yyyy: ");
            }
        }
    }

    private LocalTime leerHora() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        while (true) {
            try {
                return LocalTime.parse(scanner.nextLine().trim(), fmt);
            } catch (DateTimeParseException e) {
                System.out.print("Formato inválido. Use HH:mm: ");
            }
        }
    }

    private String banner() {
        return """
                
                ╔═══════════════════════════════════════════╗
                ║                                           ║
                ║        ██████╗██╗███╗   ██╗███████╗       ║
                ║       ██╔════╝██║████╗  ██║██╔════╝       ║
                ║       ██║     ██║██╔██╗ ██║█████╗         ║
                ║       ██║     ██║██║╚██╗██║██╔══╝         ║
                ║       ╚██████╗██║██║ ╚████║███████╗       ║
                ║        ╚═════╝╚═╝╚═╝  ╚═══╝╚══════╝      ║
                ║              W   A   X                    ║
                ║                                           ║
                ║       Cartelera de Cines - Terminal        ║
                ╚═══════════════════════════════════════════╝
                """;
    }
}
