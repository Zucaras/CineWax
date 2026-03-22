package com.waxeados.CineWax.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configuración general de la aplicación.
 *
 * Se usa spring-security-crypto (sin spring-boot-starter-security)
 * para tener acceso a BCryptPasswordEncoder sin activar los filtros
 * de seguridad automáticos de Spring Security.
 *
 * Dependencia en build.gradle:
 *   implementation 'org.springframework.security:spring-security-crypto'
 *
 * Si en el futuro se quiere migrar a Spring Security completo:
 *   1. Cambiar la dependencia a 'org.springframework.boot:spring-boot-starter-security'
 *   2. Agregar un SecurityFilterChain bean aquí mismo para configurar
 *      qué rutas son públicas y cuáles requieren autenticación.
 *      Ejemplo:
 *
 *      @Bean
 *      public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
 *          http
 *              .csrf(csrf -> csrf.disable())
 *              .authorizeHttpRequests(auth -> auth
 *                  .requestMatchers("/api/auth/**", "/api/catalogo/**").permitAll()
 *                  .requestMatchers("/api/admin/**").hasRole("ADMINISTRADOR")
 *                  .requestMatchers("/api/cliente/**").authenticated()
 *                  .anyRequest().authenticated()
 *              )
 *              .sessionManagement(session -> session
 *                  .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
 *              );
 *          return http.build();
 *      }
 */
@Configuration
public class AppConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}