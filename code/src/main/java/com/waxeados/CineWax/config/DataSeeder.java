package com.waxeados.CineWax.config;

import com.waxeados.CineWax.respositories.EstadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Componente encargado de poblar la base de datos la primera vez que se ejecuta el proyecto.
 */
@Component
@Order(1) // Prioridad 1: Le decimos a Spring que ejecute esto ANTES que la consola
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final EstadoRepository estadoRepository;
    private final DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        // Verificamos si la tabla "estado" está vacía
        if (estadoRepository.count() == 0) {
            System.out.println("\n======================================================");
            System.out.println("DATA SEEDER: Inicializando base de datos por primera vez...");
            System.out.println("======================================================");

            try (Connection connection = dataSource.getConnection()) {
                // Ejecuta el script data.sql ubicado de forma segura
                ScriptUtils.executeSqlScript(
                        connection,
                        new ClassPathResource("static/data.sql")
                );
                System.out.println("Base de datos poblada exitosamente.\n");
            } catch (Exception e) {
                System.out.println("Error al inicializar los datos: " + e.getMessage());
            }
        } else {
            System.out.println("\nDatos ya existentes. Saltando DataSeeder.\n");
        }
    }
}