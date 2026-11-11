# Trazabilidad requisito → código → prueba

| Requisito | Clase | Operación | Prueba(s) |
|-----------|-------|-----------|-----------|
| **R1** Solo un libro disponible puede prestarse | `PrestamoService`, `Libro`, `LibroNoDisponibleException`, `GlobalExceptionHandler` | `validarDisponibilidad(Libro)` → `Libro.isDisponible()`; `manejarNoDisponible(...)` → 409 | `PrestamoControllerIntegrationTest.libroYaPrestado_retorna409SinNuevoPrestamo`, `PrestamoServiceTest.registrarPrestamo_libroNoDisponible_lanzaExcepcionYNoGuarda` |
| **R2** El préstamo registra estudiante, libro y fecha | `Prestamo`, `PrestamoService`, `PrestamoResponse` | `new Prestamo(estudiante, libro, LocalDate.now())`; `PrestamoResponse.desde(Prestamo)` | `PrestamoControllerIntegrationTest.prestamoLibroDisponible_retorna201`, `PrestamoServiceTest.registrarPrestamo_libroDisponible_marcaPrestadoYGuardaEnOrden` |
| **R3** Al prestarlo, el libro deja de estar disponible | `Libro`, `PrestamoService`, `LibroRepository` | `Libro.marcarPrestado()`; `libroRepository.save(libro)` | `PrestamoControllerIntegrationTest.prestamoLibroDisponible_retorna201` (verifica `disponible=false` en BD), `PrestamoServiceTest.registrarPrestamo_libroDisponible_marcaPrestadoYGuardaEnOrden` (InOrder) |
| **R4** Una solicitud inválida no crea préstamo | `PrestamoRequest` (`@NotNull`), `PrestamoController` (`@Valid`), `PrestamoService` (`@Transactional`), `RecursoNoEncontradoException`, `GlobalExceptionHandler` | `registrarPrestamo(...)`; `manejarValidacion(...)` → 400; `manejarNoEncontrado(...)` → 404 | `PrestamoControllerIntegrationTest.solicitudInvalida_retorna400`, `estudianteInexistente_retorna404`, `libroInexistente_retorna404`, `PrestamoServiceTest.registrarPrestamo_estudianteInexistente_lanzaExcepcionYNoConsultaLibro` |
| **R5** Varios préstamos históricos, solo uno activo | `PrestamoRepository`, `PrestamoService`, `Prestamo`, `DatosIniciales` | `existsByLibroIdAndFechaDevolucionIsNull(Long)`; `validarDisponibilidad(Libro)`; `Prestamo.estaActivo()`; `Prestamo.registrarDevolucion(LocalDate)` | `PrestamoControllerIntegrationTest.mismoLibroDosVeces_segundoRetorna409`, `consultarPrestamos_retorna200` (lista incluye el préstamo histórico devuelto) |

Consulta de préstamos (`GET /api/prestamos`): `PrestamoController.listarPrestamos()` →
`PrestamoService.listarPrestamos()` → `PrestamoRepository.findAll()`; prueba
`PrestamoControllerIntegrationTest.consultarPrestamos_retorna200`.

Evidencias: `docs/evidencias/resultado-mvn-test.txt`, `docs/evidencias/surefire/`,
`docs/evidencias/pruebas-manuales.md`.
