# SmartLibrary – Taller práctico Bloque 7 (Diseño de Software)

Implementación del caso de uso **«Registrar préstamo»** (`POST /api/prestamos`) y de la consulta de
préstamos (`GET /api/prestamos`), organizada en tres capas **Controller → Service → Repository**
con entidades de dominio aparte.

## Requisitos

- JDK 21 o superior (`java -version`)
- No hace falta instalar Maven: el proyecto trae Maven Wrapper (`mvnw` / `mvnw.cmd`)

Stack: Java 21, Spring Boot 3.5.16, Spring Web, Spring Data JPA, Validation, H2, JUnit 5 + Mockito.

## Cómo correr

```bash
./mvnw spring-boot:run          # Linux / macOS / Git Bash
mvnw.cmd spring-boot:run        # Windows (cmd / PowerShell)
```

La aplicación queda en **http://localhost:8081** (se usa el 8081 porque en el equipo de desarrollo el
8080 está ocupado; se cambia con `server.port` en `application.properties`).

## Cómo probar

```bash
./mvnw test
```

Corre 10 pruebas: 7 de integración (`PrestamoControllerIntegrationTest`) y 3 unitarias con Mockito
(`PrestamoServiceTest`). También hay peticiones listas en `requests.http` y en
`docs/evidencias/SmartLibrary.postman_collection.json`.

## Consola H2

- URL: http://localhost:8081/h2-console
- JDBC URL: `jdbc:h2:mem:smartlibrary`
- Usuario: `sa`
- Contraseña: *(vacía)*

## Endpoints

| Método | Ruta | Cuerpo | Respuestas |
|--------|------|--------|------------|
| POST | `/api/prestamos` | `{"estudianteId": 1, "libroId": 2}` | 201 Created (+ header `Location`), 400, 404, 409 |
| GET | `/api/prestamos` | – | 200 OK con la lista de préstamos |

Errores con cuerpo `ErrorResponse`:

```json
{"timestamp":"2026-11-11T17:02:48","status":409,"error":"Conflict","mensaje":"El libro 'Patrones de Diseño' no está disponible","ruta":"/api/prestamos"}
```

| Código | Cuándo |
|--------|--------|
| 400 | Faltan `estudianteId` o `libroId`, o el JSON está mal formado |
| 404 | El estudiante o el libro no existen |
| 409 | El libro no está disponible o ya tiene un préstamo activo |

## Datos iniciales

Los carga `DatosIniciales` (un `CommandLineRunner`) al arrancar, solo si las tablas están vacías.

| Tipo | Id | Datos |
|------|----|-------|
| Estudiante | 1 | Ana Torres – ana.torres@ucc.edu.co |
| Estudiante | 2 | Luis Pérez – luis.perez@ucc.edu.co |
| Libro | 1 | Diseño de Software (disponible) |
| Libro | 2 | Clean Code (disponible) |
| Libro | 3 | Patrones de Diseño (no disponible) |
| Préstamo | 1 | Luis + Diseño de Software, 2026-10-01 → devuelto 2026-10-15 (histórico) |
| Préstamo | 2 | Luis + Patrones de Diseño, hace 3 días, sin devolución (activo) |

## Reglas de negocio

- **R1** Solo un libro disponible puede prestarse.
- **R2** El préstamo registra estudiante, libro y fecha.
- **R3** Al prestarlo, el libro deja de estar disponible.
- **R4** Una solicitud inválida no crea préstamo.
- **R5** Un libro puede tener varios préstamos históricos, pero solo uno activo a la vez.

Ver la trazabilidad en [docs/trazabilidad.md](docs/trazabilidad.md).

## Estructura de paquetes

```
src/main/java/com/smartlibrary/
├── SmartlibraryApplication.java
├── controller/   PrestamoController            (HTTP ↔ Service)
├── service/      PrestamoService               (reglas de negocio, transacción)
├── repository/   EstudianteRepository, LibroRepository, PrestamoRepository
├── model/        Estudiante, Libro, Prestamo   (entidades JPA)
├── dto/          PrestamoRequest, PrestamoResponse, ErrorResponse
├── exception/    RecursoNoEncontradoException, LibroNoDisponibleException, GlobalExceptionHandler
└── config/       DatosIniciales                (datos de prueba)

src/test/java/com/smartlibrary/
├── controller/   PrestamoControllerIntegrationTest
└── service/      PrestamoServiceTest

docs/
├── trazabilidad.md
├── cambios-para-uml.md
└── evidencias/   resultado-mvn-test.txt, surefire/, pruebas-manuales.md, colección Postman
```
