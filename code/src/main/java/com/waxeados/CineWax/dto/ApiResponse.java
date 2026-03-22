package com.waxeados.CineWax.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * DTO de respuesta genérico para toda la API.
 * - code: código HTTP o código de aplicación (200, 400, 401, 403, 500...)
 * - message: mensaje descriptivo del resultado
 * - data: payload de datos (se omite del JSON si es null)
 *
 * @param <T> tipo del payload
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    private int code;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    // ==================== FACTORY METHODS ====================

    /** Respuesta exitosa con datos. */
    public static <T> ApiResponse<T> ok(String message, T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .build();
    }

    /** Respuesta exitosa sin datos. */
    public static ApiResponse<Void> ok(String message) {
        return ApiResponse.<Void>builder()
                .code(200)
                .message(message)
                .data(null)
                .build();
    }

    /** Error de validación o datos inválidos (400). */
    public static ApiResponse<Void> badRequest(String message) {
        return ApiResponse.<Void>builder()
                .code(400)
                .message(message)
                .data(null)
                .build();
    }

    /** No autenticado (401). */
    public static ApiResponse<Void> unauthorized(String message) {
        return ApiResponse.<Void>builder()
                .code(401)
                .message(message)
                .data(null)
                .build();
    }

    /** Sin permisos (403). */
    public static ApiResponse<Void> forbidden(String message) {
        return ApiResponse.<Void>builder()
                .code(403)
                .message(message)
                .data(null)
                .build();
    }

    /** Error interno (500). */
    public static ApiResponse<Void> error(String message) {
        return ApiResponse.<Void>builder()
                .code(500)
                .message(message)
                .data(null)
                .build();
    }
}