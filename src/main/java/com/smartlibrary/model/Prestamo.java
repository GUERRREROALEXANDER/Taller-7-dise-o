package com.smartlibrary.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;

/**
 * Entidad de dominio: préstamo de un libro a un estudiante (R2).
 * Relación unidireccional: Estudiante y Libro no conocen sus préstamos.
 */
@Entity
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fechaPrestamo;

    @Column(nullable = true)
    private LocalDate fechaDevolucion;

    @ManyToOne(optional = false)
    @JoinColumn(name = "estudiante_id")
    private Estudiante estudiante;

    @ManyToOne(optional = false)
    @JoinColumn(name = "libro_id")
    private Libro libro;

    /** Constructor requerido por JPA. */
    protected Prestamo() {
    }

    public Prestamo(Estudiante estudiante, Libro libro, LocalDate fechaPrestamo) {
        this.estudiante = estudiante;
        this.libro = libro;
        this.fechaPrestamo = fechaPrestamo;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public Libro getLibro() {
        return libro;
    }

    /** R5: un préstamo está activo mientras no tenga fecha de devolución. */
    public boolean estaActivo() {
        return fechaDevolucion == null;
    }

    /** Registra la devolución del libro (usado para cargar el historial de préstamos). */
    public void registrarDevolucion(LocalDate fecha) {
        this.fechaDevolucion = fecha;
    }
}
