# API Layer (Capa de Presentación) - Spring Boot

Esta capa expone los endpoints REST usando Spring MVC (@RestController, @RequestMapping, @GetMapping, etc.).

Ver documentación completa de arquitectura en: [README principal](../README.md)

## Responsabilidades

- Exponer endpoints HTTP/REST
- Validar DTOs con Bean Validation (`dto/request/`) y devolver `dto/response/`
- Transformar entre DTOs y comandos/queries
- Manejar autenticación/autorización con Spring Security

> **Esta es la estructura base.** **Podrán agregarse subcarpetas según la estructura
> indicada por MH** para los proyectos de las células; el árbol completo y las
> equivalencias están en el [README principal](../README.md#estructura-ampliada-recomendada-por-mh).


## Ejemplo

```java
@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    private final CrearProductoHandler handler;
    private final ProductoMapper mapper;

    public ProductoController(CrearProductoHandler handler, ProductoMapper mapper) {
        this.handler = handler;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crear(@Valid @RequestBody ProductoRequestDTO request) {
        var command = mapper.toCommand(request);
        var producto = handler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDTO(producto));
    }
}
```
