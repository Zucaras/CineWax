package com.waxeados.CineWax.console;

import com.waxeados.CineWax.dto.*;
import com.waxeados.CineWax.entity.*;
import com.waxeados.CineWax.mappers.HorarioMapper;
import com.waxeados.CineWax.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
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
 */
@Component
@Profile("console")
@Order(2)
@RequiredArgsConstructor
public class ConsoleRunner implements CommandLineRunner {

    private final UserService userService;
    private final PeliculaService peliculaService;
    private final HorarioService horarioService;
    private final CatalogoService catalogoService;
    private final HistorialService historialService;
    private final FloydService floydService;
    private final HorarioMapper horarioMapper;

    private final Scanner scanner = new Scanner(System.in);

    private Usuario usuarioActual = null;

    @Override
    public void run(String... args) {
        System.out.println(banner());
        menuPrincipal();
    }

    // ══════════════════════════════════════════════════════════
    //  MENU PRINCIPAL
    // ══════════════════════════════════════════════════════════

    private void menuPrincipal() {
        while (true) {
            System.out.println("\n╔══════════════════════════════════╗");
            System.out.println("║       CINEWAX - CARTELERA        ║");
            System.out.println("╠══════════════════════════════════╣");
            System.out.println("║  1. Iniciar sesion               ║");
            System.out.println("║  2. Registrarse                  ║");
            System.out.println("║  3. Salir del programa           ║");
            System.out.println("╚══════════════════════════════════╝");
            System.out.print("Opcion: ");

            int op = leerEntero();
            switch (op) {
                case 1 -> iniciarSesion();
                case 2 -> registrarse();
                case 3 -> {
                    System.out.println("\n** CERRANDO APLICACION. HASTA LUEGO **");
                    System.exit(0);
                }
                default -> System.out.println("** OPCION INVALIDA **");
            }
        }
    }

    // ══════════════════════════════════════════════════════════
    //  AUTH
    // ══════════════════════════════════════════════════════════

    private void iniciarSesion() {
        System.out.print("Usuarios: ");
        String username = scanner.nextLine().trim();
        System.out.print("Contraseña: ");
        String password = scanner.nextLine().trim();

        try {
            LoginDTO dto = new LoginDTO(username, password);
            usuarioActual = userService.login(dto);
            System.out.println("\nBIENVENIDO, " + usuarioActual.getUsername().toUpperCase()
                    + " [" + usuarioActual.getRolUsuario() + "]");

            if (usuarioActual.getRolUsuario() == Usuario.Rol.ADMINISTRADOR) {
                menuAdmin();
            } else {
                menuCliente();
            }
        } catch (Exception e) {
            System.out.println("\n** ERROR: " + e.getMessage().toUpperCase() + " **");
        }
    }

    private void registrarse() {
        System.out.println("\n(Escriba 'CANCELAR' en cualquier momento para abortar)");
        String username = leerStringNoVacio("Usuario: ");
        if (esCancelacionString(username)) return;

        String password = leerStringNoVacio("Contraseña: ");
        if (esCancelacionString(password)) return;

        String rol = leerRol();
        if (esCancelacionString(rol)) return;

        String idMunicipio = null;
        if (rol.equals("ADMINISTRADOR")) {
            String idEstado = seleccionarEstado();
            if (idEstado == null) return;

            idMunicipio = seleccionarMunicipio(idEstado);
            if (idMunicipio == null) return;
        }

        try {
            RegistroDTO dto = new RegistroDTO(username, password, rol, idMunicipio);
            Usuario u = userService.registrar(dto);
            System.out.println("\nUSUARIO REGISTRADO: " + u.getUsername() + " [" + u.getRolUsuario() + "]");
        } catch (Exception e) {
            System.out.println("\n** ERROR: " + e.getMessage().toUpperCase() + " **");
        }
    }

    // ══════════════════════════════════════════════════════════
    //  MENU ADMINISTRADOR
    // ══════════════════════════════════════════════════════════

    private void menuAdmin() {
        while (true) {
            System.out.println("\n╔══════════════════════════════════╗");
            System.out.println("║      MENU ADMINISTRADOR          ║");
            System.out.println("╠══════════════════════════════════╣");
            System.out.println("║  1. Alta de Pelicula             ║");
            System.out.println("║  2. Alta de Horario              ║");
            System.out.println("║  3. Baja de Pelicula             ║");
            System.out.println("║  4. Baja de Horario              ║");
            System.out.println("║  5. Modificar Pelicula           ║");
            System.out.println("║  6. Consultar Pelicula           ║");
            System.out.println("║  7. Consultar Cartelera          ║");
            System.out.println("║  8. Procesar Cola de Horarios    ║");
            System.out.println("║  9. Cerrar Sesion                ║");
            System.out.println("╚══════════════════════════════════╝");
            System.out.print("Opcion: ");

            int op = leerEntero();
            switch (op) {
                case 1 -> altaPelicula();
                case 2 -> altaHorario();
                case 3 -> bajaPelicula();
                case 4 -> bajaHorario();
                case 5 -> modificarPelicula();
                case 6 -> consultarPelicula();
                case 7 -> consultarCartelera();
                case 8 -> procesarColaHorarios();
                case 9 -> { usuarioActual = null; return; }
                default -> System.out.println("** OPCION INVALIDA **");
            }
        }
    }

    // ══════════════════════════════════════════════════════════
    //  MENU CLIENTE
    // ══════════════════════════════════════════════════════════

    private void menuCliente() {
        while (true) {
            System.out.println("\n╔══════════════════════════════════╗");
            System.out.println("║         MENU CLIENTE             ║");
            System.out.println("╠══════════════════════════════════╣");
            System.out.println("║  1. Buscar pelicula por nombre   ║");
            System.out.println("║  2. Buscar por clasificacion     ║");
            System.out.println("║  3. Buscar por genero            ║");
            System.out.println("║  4. Ordenar Cartelera            ║");
            System.out.println("║  5. Consultar Pelicula           ║");
            System.out.println("║  6. Consultar Cartelera          ║");
            System.out.println("║  7. Ver mi Historial             ║");
            System.out.println("║  8. Ruta entre Sucursales        ║");
            System.out.println("║  9. Cerrar Sesion                ║");
            System.out.println("╚══════════════════════════════════╝");
            System.out.print("Opcion: ");

            int op = leerEntero();
            switch (op) {
                case 1 -> buscarPorNombre();
                case 2 -> buscarPorClasificacion();
                case 3 -> buscarPorGenero();
                case 4 -> ordenarCartelera();
                case 5 -> consultarPelicula();
                case 6 -> consultarCartelera();
                case 7 -> verHistorial();
                case 8 -> calcularRutaCorta();
                case 9 -> { usuarioActual = null; return; }
                default -> System.out.println("** OPCION INVALIDA **");
            }
        }
    }

    // ══════════════════════════════════════════════════════════
    // ESTRUCTURAS DE DATOS
    // ══════════════════════════════════════════════════════════

    // --- COLA (QUEUE) ---
    private void procesarColaHorarios() {
        System.out.println("\n--- PROCESANDO COLA DE HORARIOS PENDIENTES ---");
        try {
            int procesados = horarioService.procesarColaPendientes();

            if (procesados == 0) {
                System.out.println("** LA COLA ESTA VACIA. NO HAY HORARIOS POR PROCESAR **");
            } else {
                System.out.println("** SE PROCESARON EXITOSAMENTE " + procesados + " HORARIOS DE LA COLA (FIFO) **");
            }
        } catch (Exception e) {
            System.out.println("\n** ERROR: " + e.getMessage().toUpperCase() + " **");
        }
    }

    // --- PILA (STACK) ---
    private void verHistorial() {
        System.out.println("\n--- MI HISTORIAL DE BUSQUEDAS (PILA / LIFO) ---");
        try {
            List<String> acciones = historialService.obtenerHistorial(usuarioActual.getUsername());

            if (acciones.isEmpty()) {
                System.out.println("** TU HISTORIAL ESTA VACIO **");
            } else {
                int i = 1;
                for (String accion : acciones) {
                    System.out.println("  " + i++ + ". " + accion);
                }

                System.out.print("\n¿Desea deshacer su ultima busqueda? (1=Si, 0=No): ");
                int pop = leerEntero();

                if (pop == 1) {
                    historialService.deshacerUltimaAccion(usuarioActual.getUsername());
                    System.out.println("** ULTIMA ACCION ELIMINADA DEL HISTORIAL (POP) **");
                }
            }
        } catch (Exception e) {
            System.out.println("\n** ERROR: " + e.getMessage().toUpperCase() + " **");
        }
    }

    // --- GRAFOS (FLOYDWARSHALL) ---
    private void calcularRutaCorta() {
        System.out.println("\n--- CALCULAR RUTA OPTIMA ENTRE SUCURSALES (GRAFOS) ---");
        System.out.println("Nota: El calculo se realiza entre sucursales del mismo estado.");

        System.out.println("\n[ SELECCIONE EL ESTADO ] (Escriba '0' para abortar)");
        String idEstado = seleccionarEstado();
        if (idEstado == null) return;

        System.out.println("\n[ SELECCIONE MUNICIPIO DE ORIGEN ] (Escriba '0' para abortar)");
        String idOrigen = seleccionarMunicipio(idEstado);
        if (idOrigen == null) return;

        System.out.println("\n[ SELECCIONE MUNICIPIO DE DESTINO ] (Escriba '0' para abortar)");
        String idDestino = seleccionarMunicipio(idEstado);
        if (idDestino == null) return;

        if (idOrigen.equalsIgnoreCase(idDestino)) {
            System.out.println("\n** EL ORIGEN Y DESTINO SON EL MISMO MUNICIPIO (" + idOrigen.toUpperCase() + ") **");
            return;
        }

        try {
            // Llamamos a los métodos reales de tu FloydService usando el ID del Estado
            int distancia = floydService.getDistancia(idEstado, idOrigen, idDestino);
            List<String> camino = floydService.getCamino(idEstado, idOrigen, idDestino);

            // Calculamos un tiempo estimado (ej. 1.5 minutos por km recorrido)
            int tiempoEstimado = (int) (distancia * 1.5);

            System.out.println("\n||- RUTA MAS CORTA CALCULADA -||");
            // Unimos la lista de nodos devueltos por tu algoritmo con una flechita visual
            System.out.println("  Ruta optima:     " + String.join(" -> ", camino).toUpperCase());
            System.out.println("  Distancia total: " + distancia + " km");
            System.out.println("  Tiempo estimado: " + tiempoEstimado + " minutos");

        } catch (Exception e) {
            System.out.println("\n** ERROR: " + e.getMessage().toUpperCase() + " **");
        }
    }


    // ══════════════════════════════════════════════════════════
    //  FUNCIONES ADMIN (ORIGINALES)
    // ══════════════════════════════════════════════════════════

    private void altaPelicula() {
        System.out.println("\n-|Introduzca los datos (Escriba 'CANCELAR' o '0' para abortar)|-");

        String nombre = leerStringNoVacio("Nombre: ");
        if (esCancelacionString(nombre)) return;

        String director = leerStringNoVacio("Director: ");
        if (esCancelacionString(director)) return;

        String productor = leerStringNoVacio("Productor: ");
        if (esCancelacionString(productor)) return;

        String clasificacion = leerClasificacion();
        if (esCancelacionString(clasificacion)) return;

        System.out.print("Duracion (minutos): ");
        int duracion = leerEnteroPositivoOCero();
        if (duracion == 0) return;

        int idGenero = seleccionarGenero();
        if (idGenero == 0) return;

        try {
            PeliculaDTO dto = PeliculaDTO.builder()
                    .nombre(nombre).director(director).productor(productor)
                    .clasificacion(clasificacion).duracionMin(duracion).idGenero(idGenero)
                    .build();
            Pelicula p = peliculaService.altaPelicula(dto);
            System.out.println("\nPELICULA CREADA: " + p.getNombre().toUpperCase() + " (ID: " + p.getIdPelicula() + ")");
        } catch (Exception e) {
            System.out.println("\n** ERROR: " + e.getMessage().toUpperCase() + " **");
        }
    }

    private void altaHorario() {
        System.out.println("\n(Escriba '0' en los IDs para cancelar)");

        int idPelicula = seleccionarPelicula();
        if (idPelicula == 0 || idPelicula == -1) return;

        String idEstado = seleccionarEstado();
        if (idEstado == null || esCancelacionString(idEstado)) return;

        String idMunicipio = seleccionarMunicipio(idEstado);
        if (idMunicipio == null || esCancelacionString(idMunicipio)) return;

        int idSala = seleccionarSala(idMunicipio);
        if (idSala == 0 || idSala == -1) return;

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
            System.out.println("\nHORARIO REGISTRADO:");
            System.out.printf("  Pelicula: %s | Sala: %d | Fecha: %s | %s - %s%n",
                    resp.getPelicula(), resp.getSala(), resp.getFecha(),
                    resp.getHoraInicio(), resp.getHoraFinEstimada());
        } catch (Exception e) {
            System.out.println("\n** ERROR: " + e.getMessage().toUpperCase() + " **");
        }
    }

    private void bajaPelicula() {
        System.out.println("\n(Escriba '0' para cancelar)");
        int id = seleccionarPelicula();
        if (id == 0 || id == -1) return;

        try {
            peliculaService.bajaPelicula(id);
            System.out.println("\n** PELICULA ELIMINADA **");
        } catch (Exception e) {
            System.out.println("\n** ERROR: " + e.getMessage().toUpperCase() + " **");
        }
    }

    private void bajaHorario() {
        System.out.println("\n--- BUSCAR HORARIO A ELIMINAR (Escriba '0' o 'CANCELAR' para abortar) ---");
        String idEstado = seleccionarEstado();
        if (idEstado == null || esCancelacionString(idEstado)) return;

        String idMunicipio = seleccionarMunicipio(idEstado);
        if (idMunicipio == null || esCancelacionString(idMunicipio)) return;

        try {
            List<CarteleraDTO> cartelera = horarioService.consultarCartelera(idMunicipio, true);
            if (cartelera.isEmpty()) {
                System.out.println("\n** NO HAY HORARIOS REGISTRADOS PARA ESTE MUNICIPIO **");
                return;
            }

            System.out.println("\n||-HORARIOS DISPONIBLES-||");
            imprimirCartelera(cartelera);

            System.out.print("\nIngrese el ID del horario a eliminar (o 0 para cancelar): ");
            int id = leerEntero();

            if (id == 0) {
                System.out.println("\n** OPERACION CANCELADA **");
                return;
            }

            horarioService.bajaHorario(id);
            System.out.println("\n** HORARIO ELIMINADO EXITOSAMENTE **");
        } catch (Exception e) {
            System.out.println("\n** ERROR: " + e.getMessage().toUpperCase() + " **");
        }
    }

    private void modificarPelicula() {
        System.out.println("\n(Escriba '0' para cancelar)");
        int id = seleccionarPelicula();
        if (id == 0 || id == -1) return;

        try {
            Pelicula actual = peliculaService.consultarPelicula(id);
            System.out.println("\nDatos actuales: " + actual.getNombre() + " | "
                    + actual.getDirector() + " | " + actual.getClasificacion()
                    + " | " + actual.getDuracionMin() + " min");

            System.out.println("(Presiona ENTER para mantener el valor actual)");

            System.out.print("Nombre [" + actual.getNombre() + "]: ");
            String nombre = scanner.nextLine().trim();
            if (esCancelacionString(nombre)) return;
            if (nombre.isEmpty()) nombre = actual.getNombre();

            System.out.print("Director [" + actual.getDirector() + "]: ");
            String director = scanner.nextLine().trim();
            if (esCancelacionString(director)) return;
            if (director.isEmpty()) director = actual.getDirector();

            System.out.print("Productor [" + actual.getProductor() + "]: ");
            String productor = scanner.nextLine().trim();
            if (esCancelacionString(productor)) return;
            if (productor.isEmpty()) productor = actual.getProductor();

            String clasif = leerClasificacionOpcional(actual.getClasificacion());
            if (esCancelacionString(clasif)) return;

            System.out.print("Duracion min [" + actual.getDuracionMin() + "]: ");
            int duracion = leerEnteroPositivoOpcional(actual.getDuracionMin());
            if (duracion == 0) return;

            int idGenero = seleccionarGeneroOpcional(actual.getGenero().getIdGenero());
            if (idGenero == 0) return;

            PeliculaDTO dto = PeliculaDTO.builder()
                    .nombre(nombre).director(director).productor(productor)
                    .clasificacion(clasif).duracionMin(duracion).idGenero(idGenero)
                    .build();
            Pelicula p = peliculaService.modificarPelicula(id, dto);
            System.out.println("\nPELICULA MODIFICADA: " + p.getNombre().toUpperCase());
        } catch (Exception e) {
            System.out.println("\n** ERROR: " + e.getMessage().toUpperCase() + " **");
        }
    }

    // ══════════════════════════════════════════════════════════
    //  FUNCIONES COMPARTIDAS (Admin y Cliente)
    // ══════════════════════════════════════════════════════════

    private void consultarPelicula() {
        System.out.println("\n(Escriba '0' para cancelar)");
        int id = seleccionarPelicula();
        if (id == 0 || id == -1) return;

        try {
            Pelicula p = peliculaService.consultarPelicula(id);
            List<CarteleraDTO> horarios = horarioService.obtenerHorariosPelicula(id);

            if (usuarioActual != null) {
                historialService.registrarAccion(usuarioActual.getUsername(), "CONSULTAR_PELICULA", p.getNombre());
            }

            System.out.println("\n══════════════════════════════════");
            System.out.println("  " + p.getNombre().toUpperCase());
            System.out.println("══════════════════════════════════");
            System.out.println("  Director:       " + p.getDirector());
            System.out.println("  Productor:      " + p.getProductor());
            System.out.println("  Clasificacion:  " + p.getClasificacion());
            System.out.println("  Duracion:       " + p.getDuracionMin() + " min");
            System.out.println("  Genero:         " + p.getGenero().getNombreGenero());

            if (!horarios.isEmpty()) {
                System.out.println("\n  Horarios:");
                imprimirCartelera(horarios);
            } else {
                System.out.println("\n** SIN HORARIOS REGISTRADOS **");
            }
        } catch (Exception e) {
            System.out.println("\n** ERROR: " + e.getMessage().toUpperCase() + " **");
        }
    }

    private void consultarCartelera() {
        System.out.println("\n(Escriba '0' o 'CANCELAR' para abortar)");
        String idEstado = seleccionarEstado();
        if (idEstado == null || esCancelacionString(idEstado)) return;

        String idMunicipio = seleccionarMunicipio(idEstado);
        if (idMunicipio == null || esCancelacionString(idMunicipio)) return;

        System.out.println("\nSeleccione el tipo de consulta:");
        System.out.println("  1. Cartelera Completa (ordenada)");
        System.out.println("  2. Buscar por Rango de Fechas");
        System.out.print("Opcion (0 para cancelar): ");
        int tipoBusqueda = leerEntero();
        if (tipoBusqueda == 0) return;

        int orden = leerOpcionOrden();
        if (orden == 0) return;
        boolean asc = (orden == 1);

        try {
            List<CarteleraDTO> cartelera;

            if (tipoBusqueda == 2) {
                System.out.print("Fecha de inicio (dd/MM/yyyy): ");
                LocalDate inicio = leerFecha();

                // VALIDACION LOGICA DE FECHAS
                LocalDate fin = null;
                while (true) {
                    System.out.print("Fecha de fin (dd/MM/yyyy): ");
                    fin = leerFecha();
                    if (fin.isBefore(inicio)) {
                        System.out.println("** ERROR LOGICO: LA FECHA DE FIN NO PUEDE SER ANTERIOR A LA DE INICIO. **");
                    } else {
                        break;
                    }
                }

                cartelera = horarioService.consultarCarteleraRango(idMunicipio, inicio, fin, asc);
                if (usuarioActual != null) {
                    historialService.registrarAccion(usuarioActual.getUsername(), "CARTELERA_RANGO", idMunicipio + " " + inicio + " a " + fin);
                }
            } else {
                cartelera = horarioService.consultarCartelera(idMunicipio, asc);
                if (usuarioActual != null) {
                    historialService.registrarAccion(usuarioActual.getUsername(), "CONSULTAR_CARTELERA", idMunicipio);
                }
            }

            if (cartelera.isEmpty()) {
                System.out.println("\n** NO HAY FUNCIONES REGISTRADAS PARA ESTA BUSQUEDA **");
            } else {
                System.out.println("\n||-CARTELERA-||");
                imprimirCartelera(cartelera);
            }
        } catch (Exception e) {
            System.out.println("\n** ERROR: " + e.getMessage().toUpperCase() + " **");
        }
    }

    // ══════════════════════════════════════════════════════════
    //  FUNCIONES CLIENTE (BUSQUEDAS ORIGINALES)
    // ══════════════════════════════════════════════════════════

    private void buscarPorNombre() {
        String q = leerStringNoVacio("Nombre de pelicula a buscar (0 para cancelar): ");
        if (esCancelacionString(q)) return;

        try {
            historialService.registrarAccion(usuarioActual.getUsername(), "BUSCAR_NOMBRE", q);
            List<Pelicula> peliculas = peliculaService.buscarPorNombre(q);

            if (peliculas.isEmpty()) {
                System.out.println("\n** NO SE ENCONTRARON PELICULAS CON: " + q.toUpperCase() + " **");
                return;
            }

            for (Pelicula p : peliculas) {
                List<CarteleraDTO> horarios = horarioService.obtenerHorariosPelicula(p.getIdPelicula());
                System.out.println("\n► " + p.getNombre().toUpperCase() + " [" + p.getClasificacion() + "] - "
                        + p.getGenero().getNombreGenero());
                if (!horarios.isEmpty()) {
                    imprimirCartelera(horarios);
                } else {
                    System.out.println("  ** SIN HORARIOS **");
                }
            }
        } catch (Exception e) {
            System.out.println("\n** ERROR: " + e.getMessage().toUpperCase() + " **");
        }
    }

    private void buscarPorClasificacion() {
        System.out.println("\n(Escriba '0' o 'CANCELAR' para abortar)");
        String c = leerClasificacion();
        if (esCancelacionString(c)) return;

        try {
            historialService.registrarAccion(usuarioActual.getUsername(), "BUSCAR_CLASIFICACION", c);
            List<Pelicula> peliculas = peliculaService.buscarPorClasificacion(c);

            if (peliculas.isEmpty()) {
                System.out.println("\n** NO SE ENCONTRARON PELICULAS CON CLASIFICACION: " + c.toUpperCase() + " **");
                return;
            }

            for (Pelicula p : peliculas) {
                List<CarteleraDTO> horarios = horarioService.obtenerHorariosPelicula(p.getIdPelicula());
                System.out.println("\n- " + p.getNombre().toUpperCase() + " [" + p.getClasificacion() + "]");
                if (!horarios.isEmpty()) imprimirCartelera(horarios);
                else System.out.println("  ** SIN HORARIOS **");
            }
        } catch (Exception e) {
            System.out.println("\n** ERROR: " + e.getMessage().toUpperCase() + " **");
        }
    }

    private void buscarPorGenero() {
        System.out.println("\n(Escriba '0' para cancelar)");
        int id = seleccionarGenero();
        if (id == 0) return;

        try {
            historialService.registrarAccion(usuarioActual.getUsername(), "BUSCAR_GENERO", "ID:" + id);
            List<Pelicula> peliculas = peliculaService.buscarPorGenero(id);

            if (peliculas.isEmpty()) {
                System.out.println("\n** NO SE ENCONTRARON PELICULAS CON ESE GENERO **");
                return;
            }

            for (Pelicula p : peliculas) {
                List<CarteleraDTO> horarios = horarioService.obtenerHorariosPelicula(p.getIdPelicula());
                System.out.println("\n► " + p.getNombre().toUpperCase() + " - " + p.getGenero().getNombreGenero());
                if (!horarios.isEmpty()) imprimirCartelera(horarios);
                else System.out.println("  ** SIN HORARIOS **");
            }
        } catch (Exception e) {
            System.out.println("\n** ERROR: " + e.getMessage().toUpperCase() + " **");
        }
    }

    private void ordenarCartelera() {
        System.out.println("\n(Escriba '0' o 'CANCELAR' para abortar)");
        String idEstado = seleccionarEstado();
        if (idEstado == null || esCancelacionString(idEstado)) return;

        String idMunicipio = seleccionarMunicipio(idEstado);
        if (idMunicipio == null || esCancelacionString(idMunicipio)) return;

        int orden = leerOpcionOrden();
        if (orden == 0) return;
        boolean asc = (orden == 1);

        try {
            historialService.registrarAccion(usuarioActual.getUsername(), "ORDENAR_CARTELERA",
                    idMunicipio + (asc ? " ASC" : " DESC"));
            List<CarteleraDTO> cartelera = horarioService.consultarCartelera(idMunicipio, asc); // QUICKSORT

            String dir = asc ? "ASCENDENTE" : "DESCENDENTE";
            System.out.println("\n||-CARTELERA ORDENADA " + dir + " (QUICKSORT)-||");

            if (cartelera.isEmpty()) {
                System.out.println("** NO HAY FUNCIONES REGISTRADAS **");
            } else {
                imprimirCartelera(cartelera);
            }
        } catch (Exception e) {
            System.out.println("\n** ERROR: " + e.getMessage().toUpperCase() + " **");
        }
    }

    // ══════════════════════════════════════════════════════════
    //  HELPERS DE CANCELACION GLOBAL
    // ══════════════════════════════════════════════════════════

    private boolean esCancelacionString(String input) {
        return input.equalsIgnoreCase("CANCELAR") || input.equals("0");
    }

    // ══════════════════════════════════════════════════════════
    //  HELPERS DE SELECCION Y VALIDACION
    // ══════════════════════════════════════════════════════════

    private String seleccionarEstado() {
        while (true) {
            System.out.println("\n--- ESTADOS DISPONIBLES ---");
            List<Estado> estados = catalogoService.listarEstados();
            for (Estado e : estados) {
                System.out.println("  " + e.getIdEstado() + " - " + e.getNombreEstado());
            }
            System.out.print("Ingrese ID Estado: ");
            String id = scanner.nextLine().trim().toUpperCase();

            if (esCancelacionString(id)) return null;

            if (estados.stream().anyMatch(e -> e.getIdEstado().equals(id))) {
                return id;
            }
            System.out.println("** ERROR: ESTADO NO RECONOCIDO. INTENTE DE NUEVO. **");
        }
    }

    private String seleccionarMunicipio(String idEstado) {
        while (true) {
            List<Municipio> municipios = catalogoService.listarMunicipios(idEstado);
            if (municipios.isEmpty()) {
                System.out.println("** NO HAY MUNICIPIOS REGISTRADOS PARA EL ESTADO " + idEstado.toUpperCase() + " **");
                return null;
            }
            System.out.println("\n--- MUNICIPIOS DISPONIBLES ---");
            for (Municipio m : municipios) {
                System.out.println("  " + m.getIdMunicipio() + " (" + m.getLetraMunicipio() + ") - " + m.getNombreMunicipio());
            }
            System.out.print("Ingrese ID Municipio: ");
            String id = scanner.nextLine().trim().toUpperCase();

            if (esCancelacionString(id)) return null;

            if (municipios.stream().anyMatch(m -> m.getIdMunicipio().equals(id))) {
                return id;
            }
            System.out.println("** ERROR: MUNICIPIO NO RECONOCIDO. INTENTE DE NUEVO. **");
        }
    }

    private int seleccionarSala(String idMunicipio) {
        while (true) {
            List<Sala> salas = catalogoService.listarSalas(idMunicipio);
            if (salas.isEmpty()) {
                System.out.println("** NO HAY SALAS REGISTRADAS PARA EL MUNICIPIO " + idMunicipio.toUpperCase() + " **");
                return -1;
            }
            System.out.println("\n--- SALAS DISPONIBLES ---");
            for (Sala s : salas) {
                System.out.println("  ID Sala: " + s.getIdSala() + " (Numero en sucursal: Sala " + s.getNumeroSala() + ")");
            }
            System.out.print("Ingrese ID Sala: ");
            int id = leerEntero();

            if (id == 0) return 0;

            if (salas.stream().anyMatch(s -> s.getIdSala().equals(id))) {
                return id;
            }
            System.out.println("** ERROR: SALA NO RECONOCIDA. INTENTE DE NUEVO. **");
        }
    }

    private int seleccionarPelicula() {
        while (true) {
            List<Pelicula> peliculas = peliculaService.listarPeliculas();
            if (peliculas.isEmpty()) {
                System.out.println("** NO HAY PELICULAS REGISTRADAS **");
                return -1;
            }
            System.out.println("\n--- PELICULAS DISPONIBLES ---");
            for (Pelicula p : peliculas) {
                System.out.printf("  ID: %d | %s [%s] - %d min - %s%n",
                        p.getIdPelicula(), p.getNombre(), p.getClasificacion(),
                        p.getDuracionMin(), p.getGenero().getNombreGenero());
            }
            System.out.print("Ingrese ID Pelicula: ");
            int id = leerEntero();

            if (id == 0) return 0;

            if (peliculas.stream().anyMatch(p -> p.getIdPelicula().equals(id))) {
                return id;
            }
            System.out.println("** ERROR: PELICULA NO RECONOCIDA. INTENTE DE NUEVO. **");
        }
    }

    private int seleccionarGenero() {
        while (true) {
            System.out.println("\n--- GENEROS DISPONIBLES ---");
            List<Genero> generos = catalogoService.listarGeneros();
            for (Genero g : generos) {
                System.out.println("  " + g.getIdGenero() + ". " + g.getNombreGenero());
            }
            System.out.print("Ingrese ID Genero: ");
            int id = leerEntero();

            if (id == 0) return 0;

            if (generos.stream().anyMatch(g -> g.getIdGenero().equals(id))) {
                return id;
            }
            System.out.println("** ERROR: GENERO NO RECONOCIDO. INTENTE DE NUEVO. **");
        }
    }

    private int seleccionarGeneroOpcional(int idActual) {
        while (true) {
            System.out.println("\n--- GENEROS DISPONIBLES ---");
            List<Genero> generos = catalogoService.listarGeneros();
            for (Genero g : generos) {
                System.out.println("  " + g.getIdGenero() + ". " + g.getNombreGenero());
            }
            System.out.print("Ingrese ID Genero [" + idActual + "]: ");
            String str = scanner.nextLine().trim();

            if (esCancelacionString(str)) return 0;
            if (str.isEmpty()) return idActual;

            try {
                int id = Integer.parseInt(str);
                if (generos.stream().anyMatch(g -> g.getIdGenero().equals(id))) {
                    return id;
                }
                System.out.println("** ERROR: GENERO NO RECONOCIDO. INTENTE DE NUEVO. **");
            } catch (NumberFormatException e) {
                System.out.println("** ERROR: INGRESE UN NUMERO VALIDO. **");
            }
        }
    }

    private String leerClasificacion() {
        List<String> validas = List.of("AA", "A", "B", "B15", "C", "D");
        while (true) {
            System.out.print("Clasificacion (AA, A, B, B15, C, D): ");
            String c = scanner.nextLine().trim().toUpperCase();

            if (esCancelacionString(c)) return c;
            if (validas.contains(c)) return c;

            System.out.println("** ERROR: CLASIFICACION NO VALIDA. USE UNA DE LAS OPCIONES. **");
        }
    }

    private String leerClasificacionOpcional(String actual) {
        List<String> validas = List.of("AA", "A", "B", "B15", "C", "D");
        while (true) {
            System.out.print("Clasificacion [" + actual + "]: ");
            String c = scanner.nextLine().trim().toUpperCase();

            if (esCancelacionString(c)) return c;
            if (c.isEmpty()) return actual;
            if (validas.contains(c)) return c;

            System.out.println("** ERROR: CLASIFICACION NO VALIDA. USE UNA DE LAS OPCIONES. **");
        }
    }

    private String leerRol() {
        while (true) {
            System.out.print("Rol (ADMINISTRADOR / CLIENTE): ");
            String rol = scanner.nextLine().trim().toUpperCase();

            if (esCancelacionString(rol)) return rol;
            if (rol.equals("ADMINISTRADOR") || rol.equals("CLIENTE")) return rol;

            System.out.println("** ERROR: ROL NO VALIDO. ESCRIBA ADMINISTRADOR O CLIENTE. **");
        }
    }

    private String leerStringNoVacio(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String val = scanner.nextLine().trim();

            if (esCancelacionString(val)) return val;
            if (!val.isEmpty()) return val;

            System.out.println("** ERROR: ESTE CAMPO NO PUEDE QUEDAR VACIO. **");
        }
    }

    private int leerOpcionOrden() {
        while (true) {
            System.out.print("Orden (1=Ascendente, 2=Descendente, 0=Cancelar): ");
            int op = leerEntero();

            if (op == 0) return 0;
            if (op == 1 || op == 2) return op;

            System.out.println("** ERROR: OPCION INVALIDA. ELIJA 1 O 2. **");
        }
    }

    // ══════════════════════════════════════════════════════════
    //  HELPERS DE LECTURA DE FORMATOS (Enteros, Fechas, Horas)
    // ══════════════════════════════════════════════════════════

    private int leerEntero() {
        while (true) {
            try {
                String linea = scanner.nextLine().trim();
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.print("** ERROR: INGRESE UN NUMERO VALIDO: ");
            }
        }
    }

    private int leerEnteroPositivoOCero() {
        while (true) {
            int num = leerEntero();
            if (num >= 0) return num;
            System.out.print("** ERROR: EL NUMERO DEBE SER MAYOR A CERO. INTENTE DE NUEVO: ");
        }
    }

    private int leerEnteroPositivoOpcional(int actual) {
        while (true) {
            String str = scanner.nextLine().trim();
            if (esCancelacionString(str)) return 0;
            if (str.isEmpty()) return actual;
            try {
                int num = Integer.parseInt(str);
                if (num > 0) return num;
                System.out.print("** ERROR: EL NUMERO DEBE SER MAYOR A CERO. INTENTE DE NUEVO: ");
            } catch (NumberFormatException e) {
                System.out.print("** ERROR: INGRESE UN NUMERO VALIDO O PRESIONE ENTER: ");
            }
        }
    }

    private LocalDate leerFecha() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while (true) {
            try {
                return LocalDate.parse(scanner.nextLine().trim(), fmt);
            } catch (DateTimeParseException e) {
                System.out.print("** FORMATO INVALIDO ** | USE DD/MM/YYYY: ");
            }
        }
    }

    private LocalTime leerHora() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        while (true) {
            try {
                return LocalTime.parse(scanner.nextLine().trim(), fmt);
            } catch (DateTimeParseException e) {
                System.out.print("** FORMATO INVALIDO ** | USE HH:MM: ");
            }
        }
    }

    // ══════════════════════════════════════════════════════════
    //  HELPERS DE IMPRESION GENERAL
    // ══════════════════════════════════════════════════════════

    private void imprimirCartelera(List<CarteleraDTO> cartelera) {
        System.out.printf("  %-5s %-20s %-15s %-5s %-6s %-6s %-12s%n",
                "ID", "Nombre", "Ubicacion", "Sala", "Hora", "Fin", "Fecha");
        System.out.println("  " + "─".repeat(80));

        for (CarteleraDTO c : cartelera) {

            String nombre = c.getNombrePelicula();
            if (nombre.length() > 20) nombre = nombre.substring(0, 17) + "...";

            String ubicacion = c.getMunicipio() + " (" + c.getEstado() + ")";
            if (ubicacion.length() > 15) ubicacion = ubicacion.substring(0, 12) + "...";

            System.out.printf("  %-5d %-20s %-15s %-5d %-6s %-6s %-12s%n",
                    c.getIdHorario(),
                    nombre,
                    ubicacion,
                    c.getNumeroSala(),
                    c.getHoraInicio(),
                    c.getHoraFinEstimada(),
                    c.getFecha());
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
                ║        ╚═════╝╚═╝╚═╝  ╚═══╝╚══════╝       ║
                ║                W   A   X                  ║
                ║                                           ║
                ║             Cartelera de Cines            ║
                ╚═══════════════════════════════════════════╝
                """;
    }
}