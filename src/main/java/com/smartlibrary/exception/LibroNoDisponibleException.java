package com.smartlibrary.exception;

/**
 * Se lanza cuando se intenta prestar un libro que no está disponible (HTTP 409).
 */
public class LibroNoDisponibleException extends RuntimeException {

    public LibroNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
