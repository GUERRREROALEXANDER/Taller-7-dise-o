package com.smartlibrary.controller;

import com.smartlibrary.model.Estudiante;
import com.smartlibrary.model.Libro;
import com.smartlibrary.repository.EstudianteRepository;
import com.smartlibrary.repository.LibroRepository;
import com.smartlibrary.repository.PrestamoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración del endpoint /api/prestamos (Controller → Service → Repository → H2).
 * Cada prueba corre en una transacción que se revierte al final, por lo que son independientes.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PrestamoControllerIntegrationTest {

    private static final String URL = "/api/prestamos";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private PrestamoRepository prestamoRepository;

    @Test
    void prestamoLibroDisponible_retorna201() throws Exception {
        Long anaId = idEstudiante("Ana Torres");
        Long libroId = idLibro("Clean Code");
        long prestamosAntes = prestamoRepository.count();

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(json(anaId, libroId)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", matchesPattern("/api/prestamos/\\d+")))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.estudianteId").value(anaId))
                .andExpect(jsonPath("$.libroId").value(libroId))
                .andExpect(jsonPath("$.tituloLibro").value("Clean Code"))
                .andExpect(jsonPath("$.fechaPrestamo").value(matchesPattern("\\d{4}-\\d{2}-\\d{2}")));

        assertThat(libroRepository.findById(libroId).orElseThrow().isDisponible()).isFalse();
        assertThat(prestamoRepository.count()).isEqualTo(prestamosAntes + 1);
    }

    @Test
    void libroYaPrestado_retorna409SinNuevoPrestamo() throws Exception {
        long prestamosAntes = prestamoRepository.count();

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content(json(idEstudiante("Ana Torres"), idLibro("Patrones de Diseño"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.mensaje").value("El libro 'Patrones de Diseño' no está disponible"));

        assertThat(prestamoRepository.count()).isEqualTo(prestamosAntes);
    }

    @Test
    void estudianteInexistente_retorna404() throws Exception {
        long prestamosAntes = prestamoRepository.count();

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content(json(999L, idLibro("Diseño de Software"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("El estudiante con id 999 no existe"));

        assertThat(prestamoRepository.count()).isEqualTo(prestamosAntes);
    }

    @Test
    void libroInexistente_retorna404() throws Exception {
        long prestamosAntes = prestamoRepository.count();

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON)
                        .content(json(idEstudiante("Ana Torres"), 999L)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("El libro con id 999 no existe"));

        assertThat(prestamoRepository.count()).isEqualTo(prestamosAntes);
    }

    @Test
    void consultarPrestamos_retorna200() throws Exception {
        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$[0].nombreEstudiante").isNotEmpty());
    }

    @Test
    void solicitudInvalida_retorna400() throws Exception {
        long prestamosAntes = prestamoRepository.count();

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje").value(org.hamcrest.Matchers.containsString("estudianteId")))
                .andExpect(jsonPath("$.mensaje").value(org.hamcrest.Matchers.containsString("libroId")));

        assertThat(prestamoRepository.count()).isEqualTo(prestamosAntes);
    }

    @Test
    void mismoLibroDosVeces_segundoRetorna409() throws Exception {
        Long anaId = idEstudiante("Ana Torres");
        Long luisId = idEstudiante("Luis Pérez");
        Long libroId = idLibro("Diseño de Software");

        MvcResult primero = mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(json(anaId, libroId)))
                .andExpect(status().isCreated())
                .andReturn();
        assertThat(primero.getResponse().getHeader("Location")).startsWith("/api/prestamos/");
        long prestamosTrasPrimero = prestamoRepository.count();

        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(json(luisId, libroId)))
                .andExpect(status().isConflict());

        assertThat(prestamoRepository.count()).isEqualTo(prestamosTrasPrimero);
    }

    // ----- utilidades -----

    private Long idEstudiante(String nombre) {
        return estudianteRepository.findAll().stream()
                .filter(e -> e.getNombre().equals(nombre))
                .map(Estudiante::getId)
                .findFirst()
                .orElseThrow();
    }

    private Long idLibro(String titulo) {
        return libroRepository.findAll().stream()
                .filter(l -> l.getTitulo().equals(titulo))
                .map(Libro::getId)
                .findFirst()
                .orElseThrow();
    }

    private static String json(Long estudianteId, Long libroId) {
        return "{\"estudianteId\": " + estudianteId + ", \"libroId\": " + libroId + "}";
    }
}
