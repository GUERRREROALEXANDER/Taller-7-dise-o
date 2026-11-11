package com.smartlibrary.config;

import com.smartlibrary.model.Estudiante;
import com.smartlibrary.model.Libro;
import com.smartlibrary.model.Prestamo;
import com.smartlibrary.repository.EstudianteRepository;
import com.smartlibrary.repository.LibroRepository;
import com.smartlibrary.repository.PrestamoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Carga los datos iniciales al arrancar la aplicación (solo si las tablas están vacías).
 * Ids resultantes: estudiante 1 = Ana, 2 = Luis; libro 1 = Diseño de Software,
 * 2 = Clean Code, 3 = Patrones de Diseño.
 */
@Component
public class DatosIniciales implements CommandLineRunner {

    private final EstudianteRepository estudianteRepository;
    private final LibroRepository libroRepository;
    private final PrestamoRepository prestamoRepository;

    public DatosIniciales(EstudianteRepository estudianteRepository,
                          LibroRepository libroRepository,
                          PrestamoRepository prestamoRepository) {
        this.estudianteRepository = estudianteRepository;
        this.libroRepository = libroRepository;
        this.prestamoRepository = prestamoRepository;
    }

    @Override
    public void run(String... args) {
        if (estudianteRepository.count() > 0 || libroRepository.count() > 0 || prestamoRepository.count() > 0) {
            return;
        }

        estudianteRepository.save(new Estudiante("Ana Torres", "ana.torres@ucc.edu.co"));
        Estudiante luis = estudianteRepository.save(new Estudiante("Luis Pérez", "luis.perez@ucc.edu.co"));

        Libro disenoSoftware = libroRepository.save(new Libro("Diseño de Software", true));
        libroRepository.save(new Libro("Clean Code", true));
        Libro patrones = libroRepository.save(new Libro("Patrones de Diseño", false));

        // Préstamo histórico ya devuelto (R5: un libro puede tener varios préstamos en su historial)
        Prestamo historico = new Prestamo(luis, disenoSoftware, LocalDate.of(2026, 10, 1));
        historico.registrarDevolucion(LocalDate.of(2026, 10, 15));
        prestamoRepository.save(historico);

        // Préstamo activo: "Patrones de Diseño" está prestado a Luis
        prestamoRepository.save(new Prestamo(luis, patrones, LocalDate.now().minusDays(3)));
    }
}
