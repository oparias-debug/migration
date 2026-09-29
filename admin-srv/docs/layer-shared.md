# Shared Layer (Capa Compartida)

## Propósito

La capa Shared contiene **código reutilizable** por todas las demás capas. No tiene dependencias de ninguna otra capa de la aplicación.



## Estructura

> **Esta es la estructura base.** La plantilla crea únicamente lo indispensable
> para arrancar; **podrán agregarse subcarpetas según la estructura indicada por
> MH** para los proyectos de las células. Ver
> [Estructura ampliada recomendada por MH](architecture.md#estructura-ampliada-recomendada-por-mh).

```
shared/
├── constant/
│   └── AppConstants.java
├── enum/
│   ├── EstadoOrden.java
│   └── TipoMovimiento.java
├── exception/
│   ├── BusinessException.java
│   ├── NotFoundException.java
│   ├── ValidationException.java
│   ├── InfrastructureException.java
│   ├── GlobalExceptionHandler.java
│   ├── ErrorResponse.java
│   ├── LogData.java
│   └── RemoteLogger.java
├── mapper/
│   └── ProductoMapper.java
└── util/
    └── DateUtils.java
```



## Enumeraciones — Ejemplo

```java
package sv.gob.mh.shared.enums;

public enum EstadoOrden {
    PENDIENTE("Pendiente de procesamiento"),
    EN_PROCESO("En proceso de preparación"),
    COMPLETADA("Orden completada"),
    CANCELADA("Orden cancelada");

    private final String descripcion;

    EstadoOrden(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() { return descripcion; }
}
```



## Jerarquía de Excepciones

```
RuntimeException
├── BusinessException          # Error de lógica de negocio (HTTP 422)
├── NotFoundException          # Recurso no encontrado (HTTP 404)
├── ValidationException        # Error de validación (HTTP 400)
└── InfrastructureException    # Error de infraestructura (HTTP 500)
```

### BusinessException

```java
package sv.gob.mh.shared.exception;

public class BusinessException extends RuntimeException {
    private final String codigo;

    public BusinessException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public String getCodigo() { return codigo; }
}
```

### NotFoundException

```java
package sv.gob.mh.shared.exception;

public class NotFoundException extends RuntimeException {
    private final String recurso;
    private final Object id;

    public NotFoundException(String recurso, Object id) {
        super(recurso + " no encontrado con ID: " + id);
        this.recurso = recurso;
        this.id = id;
    }

    public String getRecurso() { return recurso; }
    public Object getId() { return id; }
}
```

### ValidationException

```java
package sv.gob.mh.shared.exception;

public class ValidationException extends RuntimeException {
    private final String campo;

    public ValidationException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }

    public String getCampo() { return campo; }
}
```

### InfrastructureException

```java
package sv.gob.mh.shared.exception;

public class InfrastructureException extends RuntimeException {
    private final String codigo;

    public InfrastructureException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public InfrastructureException(String codigo, String mensaje, Throwable cause) {
        super(mensaje, cause);
        this.codigo = codigo;
    }

    public String getCodigo() { return codigo; }
}
```



## Global Exception Handler

```java
package sv.gob.mh.shared.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        LOG.warn("Business exception: {} - {}", ex.getCodigo(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponse(ex.getCodigo(), ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("VALIDATION_ERROR", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBeanValidation(MethodArgumentNotValidException ex) {
        String detalles = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("VALIDATION_ERROR", detalles));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String detalles = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("VALIDATION_ERROR", detalles));
    }

    @ExceptionHandler(InfrastructureException.class)
    public ResponseEntity<ErrorResponse> handleInfrastructure(InfrastructureException ex) {
        LOG.error("Infrastructure exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(ex.getCodigo(), "Error interno del servidor"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        LOG.error("Unexpected exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "Error interno del servidor"));
    }
}
```



## ErrorResponse

```java
package sv.gob.mh.shared.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
    String codigo,
    String mensaje,
    LocalDateTime timestamp
) {
    public ErrorResponse(String codigo, String mensaje) {
        this(codigo, mensaje, LocalDateTime.now());
    }
}
```



## Mapper — Ejemplo

```java
package sv.gob.mh.shared.mapper;

import org.springframework.stereotype.Component;
import sv.gob.mh.api.dto.ProductoRequestDTO;
import sv.gob.mh.api.dto.ProductoResponseDTO;
import sv.gob.mh.application.command.CrearProductoCommand;
import sv.gob.mh.domain.model.Producto;

import java.util.List;

@Component
public class ProductoMapper {

    public CrearProductoCommand toCommand(ProductoRequestDTO dto) {
        return new CrearProductoCommand(
                dto.nombre(),
                dto.precio(),
                dto.stock(),
                dto.categoriaId()
        );
    }

    public ProductoResponseDTO toResponseDTO(Producto entity) {
        return new ProductoResponseDTO(
                entity.getId(),
                entity.getNombre(),
                entity.getPrecio(),
                entity.getStock(),
                entity.getFechaCreacion(),
                entity.getActivo()
        );
    }

    public List<ProductoResponseDTO> toResponseDTOList(List<Producto> entities) {
        return entities.stream().map(this::toResponseDTO).toList();
    }
}
```



## Constantes

```java
package sv.gob.mh.shared.constant;

public final class AppConstants {
    private AppConstants() {}

    public static final int PAGE_SIZE_DEFAULT = 20;
    public static final int PAGE_SIZE_MAX = 100;
    public static final String API_VERSION = "v1";
}
```



## Buenas Prácticas

| Práctica | Descripción |
|----------|-------------|
| **Excepciones específicas** | Usar jerarquía: Business, NotFound, Validation |
| **@RestControllerAdvice** | Manejar excepciones globalmente |
| **Mappers como @Component** | Inyectables por Spring IoC |
| **Sin dependencias de capas** | Shared no importa API, Application, Domain ni Infrastructure |
| **Records para DTOs** | Usar Java records para immutabilidad |



## Antipatrones

| Antipatrón | Corrección |
|------------|------------|
| ❌ `RuntimeException` genérico | ✅ Usar excepciones específicas |
| ❌ Catch genérico sin log | ✅ Loggear y retornar error apropiado |
| ❌ Mapper con lógica de negocio | ✅ Solo transformación de datos |
| ❌ Shared que importa clases de Domain | ✅ Mantener independencia total |
| ❌ Constantes dispersas en varias clases | ✅ Centralizar en `AppConstants` |
