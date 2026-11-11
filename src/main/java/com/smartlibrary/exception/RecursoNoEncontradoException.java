package com.smartlibrary.exception;

/**
 * Se lanza cuando un estudiante o libro solicitado no existe (HTTP 404).
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
