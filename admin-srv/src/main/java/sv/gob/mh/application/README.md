# Application Layer (Capa de Aplicación) - Spring Boot

Esta capa orquesta los casos de uso usando el patrón CQRS (Commands y Queries).

Ver documentación completa de arquitectura en: [README principal](../README.md)

## Responsabilidades

- Coordinar casos de uso
- Ejecutar transacciones con @Transactional
- Orquestar servicios de dominio
- No contiene lógica de negocio

> **Esta es la estructura base.** **Podrán agregarse subcarpetas según la estructura
> indicada por MH** para los proyectos de las células; el árbol completo y las
> equivalencias están en el [README principal](../README.md#estructura-ampliada-recomendada-por-mh).

## Ejemplo

```java
@Service
public class CrearProductoHandler {
    
    @Autowired
    private ProductoRepository repository;
    
    @Transactional
    public Producto handle(CrearProductoCommand command) {
        var producto = Producto.crear(command.getNombre(), command.getPrecio());
        return repository.save(producto);
    }
}
```
