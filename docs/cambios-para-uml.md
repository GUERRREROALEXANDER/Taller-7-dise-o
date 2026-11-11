# Cambios respecto al contrato de diseño (para reflejar en el UML)

Ningún nombre del contrato fue cambiado. Esto es lo que se **agregó**:

## Diagrama de clases

1. **`Prestamo.registrarDevolucion(fecha: LocalDate): void`** (public)
   Asigna `fechaDevolucion`. Se usa en `DatosIniciales` para crear el préstamo histórico devuelto (R5).

2. **Clase `config.DatosIniciales`** — `@Component`, `implements CommandLineRunner`
   - Atributos: `- estudianteRepository: EstudianteRepository {final}`, `- libroRepository: LibroRepository {final}`, `- prestamoRepository: PrestamoRepository {final}`
   - Operaciones: `+ DatosIniciales(EstudianteRepository, LibroRepository, PrestamoRepository)`, `+ run(args: String...): void`
   - Dependencias hacia los tres repositorios y hacia `Estudiante`, `Libro`, `Prestamo` (los crea).

3. **`GlobalExceptionHandler.manejarJsonInvalido(ex: HttpMessageNotReadableException, req: HttpServletRequest): ResponseEntity<ErrorResponse>`** → 400 (JSON mal formado).

4. **`GlobalExceptionHandler.construir(status: HttpStatus, mensaje: String, req: HttpServletRequest): ResponseEntity<ErrorResponse>`** (private)
   Método auxiliar que arma el `ErrorResponse`; lo usan los cuatro manejadores.

5. **Constructores de las excepciones**: `RecursoNoEncontradoException(mensaje: String)` y `LibroNoDisponibleException(mensaje: String)`.

6. **Clase `SmartlibraryApplication`** (`@SpringBootApplication`, `main(String[])`), punto de entrada. Normalmente no se dibuja en el diagrama de dominio.

## Configuración (no afecta los diagramas)

- `server.port=8081` en `application.properties`: en el equipo de desarrollo el puerto 8080 estaba ocupado por otro servicio.
- `maven-surefire-plugin` con `-Dnet.bytebuddy.experimental=true` para que Mockito funcione cuando el JDK instalado es más nuevo que 21 (en el equipo hay JDK 26). El código compila con `java.version=21`.
- Spring Initializr ya no ofrece Spring Boot 3.x, así que el `pom.xml` se escribió a mano con `spring-boot-starter-parent` **3.5.16** (última 3.x estable).

## Diagrama de secuencia

Sin cambios: `registrarPrestamo` sigue el orden exacto del contrato
(`findById` estudiante → `findById` libro → `validarDisponibilidad` → `new Prestamo` → `marcarPrestado` → `libroRepository.save` → `prestamoRepository.save` → `PrestamoResponse.desde`).
