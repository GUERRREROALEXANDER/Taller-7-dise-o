package com.smartlibrary.repository;

import com.smartlibrary.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acceso a datos de préstamos.
 */
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    /** R5: indica si el libro ya tiene un préstamo activo (sin fecha de devolución). */
    boolean existsByLibroIdAndFechaDevolucionIsNull(Long libroId);
}
