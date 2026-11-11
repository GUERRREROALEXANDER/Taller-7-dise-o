package com.smartlibrary.dto;

import java.time.LocalDateTime;

/**
 * Cuerpo estándar de las respuestas de error de la API.
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String mensaje,
        String ruta
) {
}
