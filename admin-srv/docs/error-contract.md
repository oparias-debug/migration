# Contrato de Errores (Error Contract)

## Principio

Toda respuesta de error del sistema sigue un **contrato uniforme** que permite al consumidor:

1. Identificar la naturaleza del error.
2. Mostrar un mensaje adecuado al usuario final.
3. Correlacionar el error con logs/trazas internas.



## Estructura de Error

```json
{
  "code": "PRODUCTO_NO_ENCONTRADO",
  "message": "No se encontró el producto con ID 42.",
  "status": 404,
  "timestamp": "2025-01-15T10:30:00Z",
  "path": "/api/v1/productos/42",
  "errors": []
}
```

### Campos

| Campo | Tipo | Obligatorio | Descripción |
|-------|------|-------------|-------------|
| `code` | `String` | ✅ | Código de error de negocio en UPPER_SNAKE_CASE |
| `message` | `String` | ✅ | Mensaje legible para el consumidor |
| `status` | `int` | ✅ | Código HTTP (`400`, `404`, `500`, etc.) |
| `timestamp` | `String` | ✅ | Fecha/hora en ISO-8601 (UTC) |
| `path` | `String` | ✅ | URI del recurso que generó el error |
| `errors` | `List<FieldError>` | Opcional | Detalles para errores de validación |

### Sub-estructura `FieldError`

```json
{
  "field": "nombre",
  "message": "El nombre es obligatorio.",
  "rejectedValue": null
}
```



## Implementación

### DTO de Error

```java
package sv.gob.mh.shared.exception;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
    String code,
    String message,
    int status,
    Instant timestamp,
    String path,
    List<FieldErrorDetail> errors
) {
    public record FieldErrorDetail(
        String field,
        String message,
        Object rejectedValue
    ) {}

    public static ErrorResponse of(String code, String message, int status, String path) {
        return new ErrorResponse(code, message, status, Instant.now(), path, List.of());
    }

    public static ErrorResponse withValidation(String code, String message, int status,
                                                String path, List<FieldErrorDetail> errors) {
        return new ErrorResponse(code, message, status, Instant.now(), path, errors);
    }
}
```

### Jerarquía de Excepciones

```
RuntimeException
└── BusinessException (base de negocio)
    ├── NotFoundException (404)
    └── ValidationException (422)
DomainException (reglas de dominio — 409)
InfrastructureException (errores técnicos — 502/503)
```

```java
package sv.gob.mh.shared.exception;

public class BusinessException extends RuntimeException {
    private final String code;
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
    public String getCode() { return code; }
}

public class NotFoundException extends BusinessException {
    public NotFoundException(String code, String message) {
        super(code, message);
    }
}

public class ValidationException extends BusinessException {
    public ValidationException(String code, String message) {
        super(code, message);
    }
}

public class DomainException extends RuntimeException {
    private final String code;
    public DomainException(String code, String message) {
        super(message);
        this.code = code;
    }
    public String getCode() { return code; }
}

public class InfrastructureException extends RuntimeException {
    private final String code;
    public InfrastructureException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
    public String getCode() { return code; }
}
```

### Global Exception Handler (`@RestControllerAdvice`)

```java
package sv.gob.mh.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 404 — Recurso no encontrado
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(ex.getCode(), ex.getMessage(), 404, request.getRequestURI()));
    }

    // 400 — Validación de Bean Validation (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ErrorResponse.FieldErrorDetail> fieldErrors = ex.getBindingResult()
                .getFieldErrors().stream()
                .map(fe -> new ErrorResponse.FieldErrorDetail(
                        fe.getField(), fe.getDefaultMessage(), fe.getRejectedValue()))
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.withValidation(
                        "VALIDATION_ERROR", "Error de validación en la solicitud.",
                        400, request.getRequestURI(), fieldErrors));
    }

    // 422 — Validación de negocio
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessValidation(
            ValidationException ex, HttpServletRequest request) {
        log.warn("Validación de negocio: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ErrorResponse.of(ex.getCode(), ex.getMessage(), 422, request.getRequestURI()));
    }

    // 409 — Regla de dominio violada
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomain(DomainException ex, HttpServletRequest request) {
        log.warn("Regla de dominio: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(ex.getCode(), ex.getMessage(), 409, request.getRequestURI()));
    }

    // 400 — Error genérico de negocio
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest request) {
        log.warn("Error de negocio: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(ex.getCode(), ex.getMessage(), 400, request.getRequestURI()));
    }

    // 502/503 — Error de infraestructura
    @ExceptionHandler(InfrastructureException.class)
    public ResponseEntity<ErrorResponse> handleInfra(
            InfrastructureException ex, HttpServletRequest request) {
        log.error("Error de infraestructura: {}", ex.getMessage(), ex.getCause());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ErrorResponse.of(ex.getCode(), "Error al comunicarse con un servicio externo.",
                        502, request.getRequestURI()));
    }

    // 500 — Error inesperado (catch-all)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of("INTERNAL_ERROR", "Error interno del servidor.",
                        500, request.getRequestURI()));
    }
}
```



## Catálogo de Códigos de Error

### Errores Generales

| Código | HTTP | Descripción |
|--------|------|-------------|
| `INTERNAL_ERROR` | 500 | Error inesperado en el servidor |
| `VALIDATION_ERROR` | 400 | Un o más campos no cumplen las restricciones |
| `UNAUTHORIZED` | 401 | Token JWT ausente o inválido |
| `FORBIDDEN` | 403 | Sin permisos para la operación |
| `NOT_FOUND` | 404 | Recurso no encontrado |

### Errores de Dominio (ejemplo)

| Código | HTTP | Descripción |
|--------|------|-------------|
| `PRODUCTO_NO_ENCONTRADO` | 404 | Producto con el ID dado no existe |
| `PRODUCTO_DUPLICADO` | 409 | Ya existe un producto con ese código |
| `PRODUCTO_INACTIVO` | 422 | No se puede operar sobre un producto inactivo |
| `PRECIO_INVALIDO` | 422 | El precio tiene un valor fuera de rango |

### Errores de Infraestructura

| Código | HTTP | Descripción |
|--------|------|-------------|
| `DATABASE_ERROR` | 502 | Error comunicándose con la base de datos |
| `EXTERNAL_SERVICE_ERROR` | 502 | Servicio externo no disponible |
| `TIMEOUT_ERROR` | 504 | Timeout al llamar servicio externo |



## Buenas Prácticas

| Práctica | Descripción |
|----------|-------------|
| **Códigos en UPPER_SNAKE_CASE** | `PRODUCTO_NO_ENCONTRADO`, no `productoNoEncontrado` |
| **Mensajes para el consumidor** | No exponer detalles de stack traces ni SQL |
| **Log del error real** | Registrar `ex.getMessage()` y `cause` en logs internos |
| **Un solo handler** | Todo error pasa por `GlobalExceptionHandler` |
| **Nunca retornar HTML** | Asegurar `produces = APPLICATION_JSON_VALUE` |
| **Timestamp en UTC** | Usar `Instant.now()` para zona horaria universal |



## Comparación Quarkus ↔ Spring Boot

| Aspecto | Quarkus | Spring Boot |
|---------|---------|-------------|
| Handler global | `ExceptionMapper<T>` | `@RestControllerAdvice` + `@ExceptionHandler` |
| Registro | `@Provider` automático | `@RestControllerAdvice` escaneado por Spring |
| Respuesta | `Response.status(code).entity(body).build()` | `ResponseEntity.status(code).body(body)` |
| Validación | `ConstraintViolationException` | `MethodArgumentNotValidException` |
| Request info | `@Context UriInfo` | `HttpServletRequest` inyectado |



## Antipatrones

| Antipatrón | Corrección |
|------------|------------|
| ❌ `throw new RuntimeException("error")` | ✅ `throw new BusinessException("CODIGO", "mensaje")` |
| ❌ Retornar stacktrace al cliente | ✅ Log interno + mensaje genérico al cliente |
| ❌ Códigos HTTP inconsistentes | ✅ Mapeo fijo por tipo de excepción |
| ❌ Catch genérico en controllers | ✅ Dejar que `GlobalExceptionHandler` maneje todo |
| ❌ Mensajes en inglés mezclados con español | ✅ Consistencia en el idioma de los mensajes |
