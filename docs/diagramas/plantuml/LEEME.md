# Diagramas en PlantUML

Respaldo en PlantUML de los 4 diagramas del taller, con el mismo contenido que el proyecto
`../SmartLibrary-Bloque7.vpp`. Las imágenes oficiales (exportadas desde Visual Paradigm) están en
`docs/diagramas/`; las de este respaldo están en esta misma carpeta.

| Archivo | Diagrama |
|---|---|
| `01-modelo-inicial-anotado.puml` | Clases del modelo inicial, con notas O1–O10 |
| `02-clases-por-capas.puml` | Clases por capas (contrato de diseño), con notas N1–N5 |
| `03-secuencia-registrar-prestamo.puml` | Secuencia POST /api/prestamos (exitoso, 409 y 404) |
| `04-secuencia-consultar-prestamos.puml` | Secuencia GET /api/prestamos |

## Regenerar las imágenes

Requiere Java, `plantuml.jar` y Graphviz (los diagramas de clases usan `dot`).

```powershell
$env:GRAPHVIZ_DOT = "C:\ruta\a\Graphviz\bin\dot.exe"
java -DPLANTUML_LIMIT_SIZE=16384 -jar plantuml.jar -charset UTF-8 -tpng -o .. *.puml
java -DPLANTUML_LIMIT_SIZE=16384 -jar plantuml.jar -charset UTF-8 -tsvg -o .. *.puml
```

También se pueden pegar en https://www.plantuml.com/plantuml: los archivos son autocontenidos, sin `!include`.
