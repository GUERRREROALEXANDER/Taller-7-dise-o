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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de PrestamoService con Mockito (sin contexto de Spring).
 */
@ExtendWith(MockitoExtension.class)
class PrestamoServiceTest {

    @Mock
    private EstudianteRepository estudianteRepository;

    @Mock
    private LibroRepository libroRepository;

    @Mock
    private PrestamoRepository prestamoRepository;

    private PrestamoService prestamoService;

    @BeforeEach
    void setUp() {
        prestamoService = new PrestamoService(estudianteRepository, libroRepository, prestamoRepository);
    }

    @Test
    void registrarPrestamo_libroDisponible_marcaPrestadoYGuardaEnOrden() {
        Estudiante ana = new Estudiante("Ana Torres", "ana.torres@ucc.edu.co");
        Libro libro = new Libro("Clean Code", true);
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(ana));
        when(libroRepository.findById(2L)).thenReturn(Optional.of(libro));
        when(prestamoRepository.save(any(Prestamo.class))).thenAnswer(inv -> inv.getArgument(0));

        PrestamoResponse respuesta = prestamoService.registrarPrestamo(new PrestamoRequest(1L, 2L));

        assertThat(libro.isDisponible()).isFalse();                       // marcarPrestado() se ejecutó (R3)
        assertThat(respuesta.nombreEstudiante()).isEqualTo("Ana Torres"); // R2
        assertThat(respuesta.tituloLibro()).isEqualTo("Clean Code");
        assertThat(respuesta.fechaPrestamo()).isEqualTo(LocalDate.now());
        assertThat(respuesta.fechaDevolucion()).isNull();

        InOrder orden = inOrder(libroRepository, prestamoRepository);
        orden.verify(libroRepository).save(libro);
        orden.verify(prestamoRepository).save(any(Prestamo.class));
    }

    @Test
    void registrarPrestamo_libroNoDisponible_lanzaExcepcionYNoGuarda() {
        Estudiante ana = new Estudiante("Ana Torres", "ana.torres@ucc.edu.co");
        Libro libro = new Libro("Patrones de Diseño", false);
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(ana));
        when(libroRepository.findById(3L)).thenReturn(Optional.of(libro));

        assertThatThrownBy(() -> prestamoService.registrarPrestamo(new PrestamoRequest(1L, 3L)))
                .isInstanceOf(LibroNoDisponibleException.class)
                .hasMessage("El libro 'Patrones de Diseño' no está disponible");

        verify(prestamoRepository, never()).save(any());
        verify(libroRepository, never()).save(any());
    }

    @Test
    void registrarPrestamo_estudianteInexistente_lanzaExcepcionYNoConsultaLibro() {
        when(estudianteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> prestamoService.registrarPrestamo(new PrestamoRequest(999L, 1L)))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("El estudiante con id 999 no existe");

        verifyNoInteractions(libroRepository);
        verify(prestamoRepository, never()).save(any());
    }
}
