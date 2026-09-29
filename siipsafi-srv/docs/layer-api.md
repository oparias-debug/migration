# API Layer (Capa de Presentación)

## Propósito

La capa API es el **punto de entrada** de la aplicación. Recibe peticiones HTTP, valida datos de entrada, delega a la capa de aplicación y retorna respuestas HTTP apropiadas. Esta capa utiliza **Spring MVC** con `@RestController`.



## Responsabilidades

| Responsabilidad | Descripción |
|----------------|-------------|
| Exposición REST | Definir endpoints HTTP con `@RestController`, `@RequestMapping` |
| Validación de entrada | Usar Bean Validation (`@Valid`, `@NotNull`, `@Size`) |
| Documentación OpenAPI | Anotar con SpringDoc (`@Operation`, `@Tag`, `@Schema`) |
| Transformación | Convertir DTOs → Commands y Entities → ResponseDTOs |
| Delegación | Llamar handlers y queries de la capa Application |
| Manejo de HTTP | Retornar códigos HTTP correctos (`ResponseEntity`) |

## Lo que NO debe hacer

| Prohibición | Razón |
|-------------|-------|
| ❌ Lógica de negocio | Va en Domain |
| ❌ Acceder a repositorios directamente | Va a través de Application |
| ❌ Transacciones (`@Transactional`) | Responsabilidad de Application |
| ❌ Queries directas a BD | Va en Infrastructure |



## Estructura

> **Esta es la estructura base.** La plantilla crea únicamente lo indispensable
> para arrancar; **podrán agregarse subcarpetas según la estructura indicada por
> MH** para los proyectos de las células. Ver
> [Estructura ampliada recomendada por MH](architecture.md#estructura-ampliada-recomendada-por-mh).

```
api/
├── controller/
│   ├── ProductoController.java
│   ├── CategoriaController.java
│   └── SecurityController.java
└── dto/
    ├── ProductoRequestDTO.java
    ├── ProductoResponseDTO.java
    ├── PageResponseDTO.java
    └── ErrorResponseDTO.java
```



## Controller — Ejemplo Completo

```java
package sv.gob.mh.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv.gob.mh.api.dto.ProductoRequestDTO;
import sv.gob.mh.api.dto.ProductoResponseDTO;
import sv.gob.mh.api.dto.PageResponseDTO;
import sv.gob.mh.application.handler.CrearProductoHandler;
import sv.gob.mh.application.handler.ActualizarProductoHandler;
import sv.gob.mh.application.handler.EliminarProductoHandler;
import sv.gob.mh.application.query.ObtenerProductoQuery;
import sv.gob.mh.application.query.ListarProductosQuery;
import sv.gob.mh.shared.mapper.ProductoMapper;

@RestController
@RequestMapping(value = "/api/v1/productos", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Productos", description = "Gestión de productos")
public class ProductoController {

    private final CrearProductoHandler crearHandler;
    private final ActualizarProductoHandler actualizarHandler;
    private final EliminarProductoHandler eliminarHandler;
    private final ObtenerProductoQuery obtenerQuery;
    private final ListarProductosQuery listarQuery;
    private final ProductoMapper mapper;

    public ProductoController(
            CrearProductoHandler crearHandler,
            ActualizarProductoHandler actualizarHandler,
            EliminarProductoHandler eliminarHandler,
            ObtenerProductoQuery obtenerQuery,
            ListarProductosQuery listarQuery,
            ProductoMapper mapper) {
        this.crearHandler = crearHandler;
        this.actualizarHandler = actualizarHandler;
        this.eliminarHandler = eliminarHandler;
        this.obtenerQuery = obtenerQuery;
        this.listarQuery = listarQuery;
        this.mapper = mapper;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Crear nuevo producto")
    @ApiResponse(responseCode = "201", description = "Producto creado exitosamente")
    public ResponseEntity<ProductoResponseDTO> crear(
            @Valid @RequestBody ProductoRequestDTO request) {
        var command = mapper.toCommand(request);
        var producto = crearHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponseDTO(producto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    @ApiResponse(responseCode = "200", description = "Producto encontrado")
    @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    public ResponseEntity<ProductoResponseDTO> obtener(
            @Parameter(description = "ID del producto") @PathVariable Long id) {
        var producto = obtenerQuery.ejecutar(id);
        return ResponseEntity.ok(mapper.toResponseDTO(producto));
    }

    @GetMapping
    @Operation(summary = "Listar productos paginados")
    public ResponseEntity<PageResponseDTO<ProductoResponseDTO>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(listarQuery.ejecutar(page, size, sort));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Actualizar producto existente")
    public ResponseEntity<ProductoResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoRequestDTO request) {
        var command = mapper.toUpdateCommand(id, request);
        var producto = actualizarHandler.handle(command);
        return ResponseEntity.ok(mapper.toResponseDTO(producto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto")
    @ApiResponse(responseCode = "204", description = "Producto eliminado")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        eliminarHandler.handle(id);
        return ResponseEntity.noContent().build();
    }
}
```



## Request DTO — Ejemplo

```java
package sv.gob.mh.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos para crear o actualizar un producto")
public record ProductoRequestDTO(

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Schema(description = "Nombre del producto", example = "Laptop Dell XPS 15")
    String nombre,

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Schema(description = "Precio del producto", example = "1299.99")
    java.math.BigDecimal precio,

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Schema(description = "Cantidad en stock", example = "50")
    Integer stock,

    @Schema(description = "ID de la categoría", example = "1")
    Long categoriaId
) {}
```



## Response DTO — Ejemplo

```java
package sv.gob.mh.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Datos de un producto")
public record ProductoResponseDTO(

    @Schema(description = "ID del producto", example = "1")
    Long id,

    @Schema(description = "Nombre del producto", example = "Laptop Dell XPS 15")
    String nombre,

    @Schema(description = "Precio del producto", example = "1299.99")
    BigDecimal precio,

    @Schema(description = "Cantidad en stock", example = "50")
    Integer stock,

    @Schema(description = "Fecha de creación")
    LocalDateTime fechaCreacion,

    @Schema(description = "Estado activo")
    Boolean activo
) {}
```



## Buenas Prácticas

| Práctica | Descripción |
|----------|-------------|
| **DTOs separados** | Request y Response separados, nunca reutilizar |
| **@Valid siempre** | Validar entrada con Bean Validation |
| **Sin lógica** | Controller solo recibe → mapea → delega → mapea → responde |
| **@Operation** | Documentar cada endpoint con OpenAPI |
| **Códigos HTTP correctos** | 201 para crear, 204 para eliminar, etc. |
| **Constructor injection** | Preferir inyección por constructor |
| **MediaType explícito** | Declarar `produces` y `consumes` |



## Antipatrones

| Antipatrón | Corrección |
|------------|------------|
| ❌ Lógica de negocio en controller | ✅ Mover a Domain Service o Entidad |
| ❌ `@Transactional` en controller | ✅ Mover a Handler en Application |
| ❌ Service / Repository inyectado directamente | ✅ Usar Handler/Query como intermediario |
| ❌ Retornar entidad JPA en response | ✅ Usar ResponseDTO |
| ❌ Endpoint sin documentación OpenAPI | ✅ `@Operation`, `@Tag`, `@Schema` |
| ❌ Catch genérico de excepciones | ✅ Usar `@RestControllerAdvice` global |
