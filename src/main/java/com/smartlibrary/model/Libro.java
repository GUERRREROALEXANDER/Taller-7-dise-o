package com.smartlibrary.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Entidad de dominio: libro de la biblioteca.
 * La disponibilidad solo cambia mediante la operación de dominio {@link #marcarPrestado()}.
 */
@Entity
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private boolean disponible;

    /** Constructor requerido por JPA. */
    protected Libro() {
    }

    public Libro(String titulo, boolean disponible) {
        this.titulo = titulo;
        this.disponible = disponible;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public boolean isDisponible() {
        return disponible;
    }

    /** R3: al prestarse, el libro deja de estar disponible. */
    public void marcarPrestado() {
        this.disponible = false;
    }
}
