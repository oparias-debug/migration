# Shared Layer (Capa Compartida) - Spring Boot

Esta capa contiene código reutilizable por todas las demás capas.

Ver documentación completa de arquitectura en: [README principal](../README.md)

## Responsabilidades

- Enumeraciones compartidas
- Excepciones personalizadas (BusinessException, etc.)
- Mappers entre DTOs y entidades
- Utilidades y constantes

> **Esta es la estructura base.** **Podrán agregarse subcarpetas según la estructura
> indicada por MH** para los proyectos de las células; el árbol completo y las
> equivalencias están en el [README principal](../README.md#estructura-ampliada-recomendada-por-mh).

## Ejemplo

```java
public class BusinessException extends RuntimeException {
    private final String codigo;
    
    public BusinessException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }
}
```

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handle(BusinessException ex) {
        return ResponseEntity.status(422).body(new ErrorResponse(ex.getCodigo(), ex.getMessage()));
    }
}
```
