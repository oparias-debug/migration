# Domain Layer (Capa de Dominio) - Spring Boot

Esta es la capa más importante - contiene TODA la lógica de negocio.

Ver documentación completa de arquitectura en: [README principal](../README.md)

## Responsabilidades

- **Modelos de dominio**: POJOs o clases Java puras que representan el dominio de negocio,
  con comportamiento rico (no modelos anémicos). **Sin anotaciones JPA**
- Value Objects inmutables
- Servicios de dominio
- Interfaces de repositorios (contratos)
- Independiente de frameworks

> **Esta es la estructura base.** **Podrán agregarse subcarpetas según la estructura
> indicada por MH** para los proyectos de las células; el árbol completo y las
> equivalencias están en el [README principal](../README.md#estructura-ampliada-recomendada-por-mh).

> Las **entidades JPA** (`@Entity`) no van aquí: viven en
> `infrastructure/persistence/entity`, porque son el mapeo a la tabla y no el negocio.

## Ejemplo

```java
// domain/model/Producto.java — clase Java pura, sin JPA
public class Producto {
    private Long id;
    private String nombre;
    private BigDecimal precio;
    
    public static Producto crear(String nombre, BigDecimal precio) {
        validarPrecio(precio);
        Producto p = new Producto();
        p.nombre = nombre;
        p.precio = precio;
        return p;
    }
    
    private static void validarPrecio(BigDecimal precio) {
        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("PRECIO_INVALIDO", "...");
        }
    }
}
```
