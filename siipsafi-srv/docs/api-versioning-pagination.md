# Versionamiento de API y Paginación

## Parte 1: Estrategia de Versionamiento

### Convención de URLs

Todos los endpoints siguen el patrón de versionamiento por **URL path**:

```
/api/v{N}/{recurso}
```

| Versión | Estado | Ejemplo |
|---------|--------|---------|
| `v1` | **Actual** | `/api/v1/productos` |
| `v2` | Futura | `/api/v2/productos` |

### ¿Cuándo Crear una Nueva Versión?

| Cambio | ¿Nueva versión? | Ejemplo |
|--------|-----------------|---------|
| Agregar campo opcional a response | ❌ No | Agregar `descripcion` al producto |
| Agregar endpoint nuevo | ❌ No | `GET /api/v1/productos/exportar` |
| Agregar query parameter opcional | ❌ No | `?incluir_inactivos=true` |
| Cambiar tipo de campo existente | ✅ Sí | `precio: String` → `precio: Number` |
| Remover campo del response | ✅ Sí | Eliminar `codigoLegacy` |
| Cambiar estructura del response | ✅ Sí | Wrapping en objeto `data` |
| Cambiar semántica de un endpoint | ✅ Sí | `POST` que antes creaba, ahora actualiza |

### Implementación con Spring MVC

```java
// Versión 1 — vigente
@RestController
@RequestMapping("/api/v1/productos")
@Tag(name = "Productos v1")
public class ProductoControllerV1 {

    @GetMapping
    public ResponseEntity<PageResponseDTO<ProductoResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(listarQuery.ejecutar(page, size, null));
    }
}

// Versión 2 — nueva (cuando se requiera)
@RestController
@RequestMapping("/api/v2/productos")
@Tag(name = "Productos v2")
public class ProductoControllerV2 {

    @GetMapping
    public ResponseEntity<Map<String, Object>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var data = listarQuery.ejecutar(page, size, null);
        return ResponseEntity.ok(Map.of("data", data, "meta", buildMeta(data)));
    }
}
```

### Política de Deprecación

| Fase | Duración | Acción |
|------|----------|--------|
| **Activa** | Indefinida | Versión actual recibiendo nuevas features |
| **Deprecated** | 6 meses mínimo | Header `Deprecated: true` + documentación |
| **Sunset** | 3 meses aviso | Header `Sunset: {fecha}` |
| **Removida** | Tras sunset | Endpoint retorna 410 Gone |

### Headers de Deprecación

```java
@GetMapping
@Deprecated
@Operation(summary = "Listar productos", deprecated = true)
public ResponseEntity<PageResponseDTO<ProductoResponseDTO>> listarV1() {
    return ResponseEntity.ok(listarQuery.ejecutar(0, 20, null))
            .headers(headers -> {
                headers.add("Deprecated", "true");
                headers.add("Sunset", "2026-12-31");
                headers.add("Link", "</api/v2/productos>; rel=\"successor-version\"");
            });
}
```



## Parte 2: Patrón de Paginación

### Estructura de Respuesta Paginada

```json
{
  "content": [
    { "id": 1, "nombre": "Producto A", "precio": 99.99 },
    { "id": 2, "nombre": "Producto B", "precio": 149.99 }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 150,
  "totalPages": 8,
  "first": true,
  "last": false
}
```

### Query Parameters

| Parámetro | Default | Descripción | Ejemplo |
|-----------|---------|-------------|---------|
| `page` | `0` | Número de página (0-indexed) | `?page=2` |
| `size` | `20` | Elementos por página | `?size=50` |
| `sort` | - | Campo y dirección | `?sort=nombre,asc` |

### DTO de Respuesta Paginada

```java
package sv.gob.mh.api.dto;

import java.util.List;

public record PageResponseDTO<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean first,
    boolean last
) {
    public static <T> PageResponseDTO<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return new PageResponseDTO<>(content, page, size, totalElements, totalPages, page == 0, page >= totalPages - 1);
    }
}
```

### Query con Paginación (Spring Data)

```java
package sv.gob.mh.application.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ListarProductosQuery {

    private final ProductoRepository repository;
    private final ProductoMapper mapper;

    public ListarProductosQuery(ProductoRepository repository, ProductoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public PageResponseDTO<ProductoResponseDTO> ejecutar(int page, int size, String sort) {
        // Limitar tamaño máximo
        size = Math.min(size, AppConstants.PAGE_SIZE_MAX);

        // Parsear sort: "nombre,asc"
        Sort sorting = Sort.by("id").ascending();
        if (sort != null && sort.contains(",")) {
            String[] parts = sort.split(",");
            sorting = Sort.by(Sort.Direction.fromString(parts[1]), parts[0]);
        }

        // Spring Data maneja la paginación nativamente
        Page<Producto> resultado = repository.findAll(PageRequest.of(page, size, sorting));

        var content = resultado.getContent().stream()
                .map(mapper::toResponseDTO)
                .toList();

        return PageResponseDTO.of(content, page, size, resultado.getTotalElements());
    }
}
```

### Controller con Paginación

```java
@GetMapping
@Operation(summary = "Listar productos paginados")
public ResponseEntity<PageResponseDTO<ProductoResponseDTO>> listar(
        @Parameter(description = "Número de página (0-indexed)", example = "0")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Elementos por página", example = "20")
        @RequestParam(defaultValue = "20") int size,
        @Parameter(description = "Ordenamiento: campo,dirección", example = "nombre,asc")
        @RequestParam(required = false) String sort) {
    return ResponseEntity.ok(listarQuery.ejecutar(page, size, sort));
}
```



## Buenas Prácticas

| Práctica | Descripción |
|----------|-------------|
| **Paginación obligatoria** | Todo endpoint que retorne listas debe paginar |
| **Límite de tamaño** | Máximo 100 elementos por página |
| **Sort por defecto** | Siempre definir un ordenamiento por defecto (por `id`) |
| **Page 0-indexed** | La primera página es `0`, no `1` |
| **Versión en URL** | Usar `/api/v1/` consistentemente |
| **Backward compatible** | Agregar campos opcionales sin cambiar versión |



## Antipatrones

| Antipatrón | Corrección |
|------------|------------|
| ❌ Retornar listas sin paginar | ✅ Siempre usar `PageResponseDTO` |
| ❌ Sin límite de tamaño de página | ✅ Máximo `PAGE_SIZE_MAX = 100` |
| ❌ Versión en headers | ✅ Versión en URL (`/api/v2/`) |
| ❌ Cambiar response sin nueva versión | ✅ Evaluar si es breaking change |
| ❌ `page=1` como primera página | ✅ `page=0` (0-indexed, consistente con Spring Data) |
