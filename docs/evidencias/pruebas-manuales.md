# Pruebas manuales – SmartLibrary

Ejecutadas con `curl -i` contra la aplicación levantada con `./mvnw spring-boot:run`
(puerto 8081, datos iniciales cargados por `DatosIniciales`), en el orden indicado.
Fecha de ejecución: 2026-11-11 17:02.

Datos iniciales: estudiante 1 = Ana Torres, 2 = Luis Pérez; libro 1 = Diseño de Software,
2 = Clean Code, 3 = Patrones de Diseño (prestado a Luis).

## Resumen

| # | Escenario | Esperado | Obtenido | ✔/✘ |
|---|-----------|----------|----------|-----|
| 1 | Préstamo de libro disponible (Ana, libro 2) | 201 Created | 201 | ✔ |
| 2 | Libro ya prestado (Ana, libro 3) | 409 Conflict | 409 | ✔ |
| 3 | Estudiante inexistente (999, libro 1) | 404 Not Found | 404 | ✔ |
| 4 | Libro inexistente (1, 999) | 404 Not Found | 404 | ✔ |
| 5 | Consulta de préstamos (GET) | 200 OK con registros | 200 | ✔ |
| 6 | Solicitud inválida ({}) | 400 Bad Request | 400 | ✔ |

## 1. Préstamo de libro disponible (Ana, libro 2)

**Esperado:** 201 Created

**Petición:**

```bash
curl -i -X POST http://localhost:8081/api/prestamos -H "Content-Type: application/json" -d '{"estudianteId": 1, "libroId": 2}'
```

**Respuesta:**

```http
HTTP/1.1 201 
Location: /api/prestamos/3
Content-Type: application/json
Transfer-Encoding: chunked
Date: Wed, 11 Nov 2026 22:02:47 GMT

{"id":3,"estudianteId":1,"nombreEstudiante":"Ana Torres","libroId":2,"tituloLibro":"Clean Code","fechaPrestamo":"2026-11-11","fechaDevolucion":null}
```

## 2. Libro ya prestado (Ana, libro 3)

**Esperado:** 409 Conflict

**Petición:**

```bash
curl -i -X POST http://localhost:8081/api/prestamos -H "Content-Type: application/json" -d '{"estudianteId": 1, "libroId": 3}'
```

**Respuesta:**

```http
HTTP/1.1 409 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Wed, 11 Nov 2026 22:02:48 GMT

{"timestamp":"2026-11-11T17:02:48.0125429","status":409,"error":"Conflict","mensaje":"El libro 'Patrones de Diseño' no está disponible","ruta":"/api/prestamos"}
```

## 3. Estudiante inexistente (999, libro 1)

**Esperado:** 404 Not Found

**Petición:**

```bash
curl -i -X POST http://localhost:8081/api/prestamos -H "Content-Type: application/json" -d '{"estudianteId": 999, "libroId": 1}'
```

**Respuesta:**

```http
HTTP/1.1 404 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Wed, 11 Nov 2026 22:02:48 GMT

{"timestamp":"2026-11-11T17:02:48.0730192","status":404,"error":"Not Found","mensaje":"El estudiante con id 999 no existe","ruta":"/api/prestamos"}
```

## 4. Libro inexistente (1, 999)

**Esperado:** 404 Not Found

**Petición:**

```bash
curl -i -X POST http://localhost:8081/api/prestamos -H "Content-Type: application/json" -d '{"estudianteId": 1, "libroId": 999}'
```

**Respuesta:**

```http
HTTP/1.1 404 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Wed, 11 Nov 2026 22:02:48 GMT

{"timestamp":"2026-11-11T17:02:48.128731","status":404,"error":"Not Found","mensaje":"El libro con id 999 no existe","ruta":"/api/prestamos"}
```

## 5. Consulta de préstamos (GET)

**Esperado:** 200 OK con registros

**Petición:**

```bash
curl -i http://localhost:8081/api/prestamos
```

**Respuesta:**

```http
HTTP/1.1 200 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Wed, 11 Nov 2026 22:02:48 GMT

[{"id":1,"estudianteId":2,"nombreEstudiante":"Luis Pérez","libroId":1,"tituloLibro":"Diseño de Software","fechaPrestamo":"2026-10-01","fechaDevolucion":"2026-10-15"},{"id":2,"estudianteId":2,"nombreEstudiante":"Luis Pérez","libroId":3,"tituloLibro":"Patrones de Diseño","fechaPrestamo":"2026-11-08","fechaDevolucion":null},{"id":3,"estudianteId":1,"nombreEstudiante":"Ana Torres","libroId":2,"tituloLibro":"Clean Code","fechaPrestamo":"2026-11-11","fechaDevolucion":null}]
```

## 6. Solicitud inválida ({})

**Esperado:** 400 Bad Request

**Petición:**

```bash
curl -i -X POST http://localhost:8081/api/prestamos -H "Content-Type: application/json" -d '{}'
```

**Respuesta:**

```http
HTTP/1.1 400 
Content-Type: application/json
Transfer-Encoding: chunked
Date: Wed, 11 Nov 2026 22:02:48 GMT
Connection: close

{"timestamp":"2026-11-11T17:02:48.2257427","status":400,"error":"Bad Request","mensaje":"Campos inválidos: estudianteId (El id del estudiante es obligatorio), libroId (El id del libro es obligatorio)","ruta":"/api/prestamos"}
```
