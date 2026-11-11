package com.smartlibrary.service;

import com.smartlibrary.dto.PrestamoRequest;
import com.smartlibrary.dto.PrestamoResponse;
import com.smartlibrary.exception.LibroNoDisponibleException;
import com.smartlibrary.exception.RecursoNoEncontradoException;
import com.smartlibrary.model.Estudiante;
import com.smartlibrary.model.Libro;
import com.smartlibrary.model.Prestamo;
import com.smartlibrary.repository.EstudianteRepository;
import com.smartlibrary.repository.LibroRepository;
import com.smartlibrary.repository.PrestamoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Lógica de negocio del caso de uso «Registrar préstamo» y de la consulta de préstamos.
 */
@Service
public class PrestamoService {

    private final EstudianteRepository estudianteRepository;
    private final LibroRepository libroRepository;
    private final PrestamoRepository prestamoRepository;

    public PrestamoService(EstudianteRepository estudianteRepository,
                           LibroRepository libroRepository,
                           PrestamoRepository prestamoRepository) {
        this.estudianteRepository = estudianteRepository;
        this.libroRepository = libroRepository;
        this.prestamoRepository = prestamoRepository;
    }

    /**
     * Registra un préstamo. Todo ocurre en una transacción: si algo falla
     * no se crea el préstamo ni se modifica el libro (R4).
     */
    @Transactional
    public PrestamoResponse registrarPrestamo(PrestamoRequest request) {
        Estudiante estudiante = estudianteRepository.findById(request.estudianteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El estudiante con id " + request.estudianteId() + " no existe"));

        Libro libro = libroRepository.findById(request.libroId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El libro con id " + request.libroId() + " no existe"));

        validarDisponibilidad(libro);                                          // R1, R5

        Prestamo prestamo = new Prestamo(estudiante, libro, LocalDate.now());  // R2

        libro.marcarPrestado();                                                // R3

        libroRepository.save(libro);
        Prestamo guardado = prestamoRepository.save(prestamo);

        return PrestamoResponse.desde(guardado);
    }

    /** Consulta todos los préstamos (activos e históricos). */
    @Transactional(readOnly = true)
    public List<PrestamoResponse> listarPrestamos() {
        return prestamoRepository.findAll().stream()
                .map(PrestamoResponse::desde)
                .toList();
    }

    /** R1 y R5: el libro debe estar disponible y no tener un préstamo activo. */
    private void validarDisponibilidad(Libro libro) {
        if (!libro.isDisponible() || prestamoRepository.existsByLibroIdAndFechaDevolucionIsNull(libro.getId())) {
            throw new LibroNoDisponibleException("El libro '" + libro.getTitulo() + "' no está disponible");
        }
    }
}
