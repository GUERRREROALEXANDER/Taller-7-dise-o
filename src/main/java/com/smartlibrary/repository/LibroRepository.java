package com.smartlibrary.repository;

import com.smartlibrary.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acceso a datos de libros.
 */
public interface LibroRepository extends JpaRepository<Libro, Long> {
}
