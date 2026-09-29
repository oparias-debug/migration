# Domain Layer (Capa de Dominio)

## Propósito

La capa Domain es el **corazón** de la aplicación. Contiene **toda la lógica de negocio**, las reglas, las validaciones y las entidades. Es completamente **independiente de frameworks** — no debe conocer Spring Boot, JPA annotations de persistencia (idealmente), ni detalles de infraestructura.



## Principios DDD

| Principio | Descripción |
|-----------|-------------|
| **Lenguaje Ubicuo** | Nombres de clases y métodos reflejan el lenguaje del negocio |
| **Entidades Ricas** | Las entidades contienen comportamiento, no son modelos anémicos |
| **Value Objects** | Objetos inmutables que describen atributos sin identidad propia |
| **Aggregate Roots** | Entidades raíz que protegen la consistencia del agregado |
| **Repository Interfaces** | Contratos de persistencia definidos en el dominio |
| **Domain Services** | Lógica que involucra múltiples entidades |



## Estructura

> **Esta es la estructura base.** La plantilla crea únicamente lo indispensable
> para arrancar; **podrán agregarse subcarpetas según la estructura indicada por
> MH** para los proyectos de las células. Ver
> [Estructura ampliada recomendada por MH](architecture.md#estructura-ampliada-recomendada-por-mh).

```
domain/
├── model/
│   ├── Producto.java          # Modelo de dominio (Aggregate Root) — POJO puro
│   ├── Categoria.java         # Modelo de dominio
│   ├── Dinero.java            # Value Object
│   └── DireccionEntrega.java  # Value Object
├── repository/
│   ├── RepositoryInterface.java    # Interface genérica base
│   ├── ProductoRepository.java     # Interface específica
│   └── CategoriaRepository.java    # Interface específica
└── service/
    └── InventarioService.java      # Servicio de dominio
```

### Dónde van las entidades

| Clase | Ubicación | Anotaciones |
|-------|-----------|-------------|
| Modelo de dominio (`Producto`) | `domain/model` | **Ninguna de framework.** Clase Java pura |
| Entidad de persistencia (`ProductoEntity`) | `infrastructure/persistence/entity` | `@Entity`, `@Table`, `@Column` |
| Contrato de repositorio (`ProductoRepository`) | `domain/repository` | `interface`, sin Spring ni JPA |
| Repositorio Spring Data (`ProductoJpaRepository`) | `infrastructure/persistence/repository` | `@Repository`, `JpaRepository<ProductoEntity, Long>` |
| Adaptador (`ProductoRepositoryImpl`) | `infrastructure/persistence/repository` | Implementa el contrato y mapea entidad ↔ modelo |
| Mapper (`ProductoMapper`) | `shared/mapper` | Convierte DTO ↔ modelo ↔ entidad |

El dominio **no conoce JPA ni Spring**. La entidad es un detalle de la base de datos y vive
en infraestructura; el modelo de dominio lleva las reglas de negocio y se prueba sin
levantar contexto de Spring.

Ver [Infrastructure Layer](layer-infrastructure.md) para el ejemplo de la entidad y del
repositorio Spring Data.



## Modelo de Dominio (Aggregate Root) — Ejemplo

Clase Java pura: **sin `@Entity`, sin `@Table`, sin `@Column`**.

```java
package sv.gob.mh.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Producto {

    private Long id;
    private String nombre;
    private BigDecimal precio;
    private Integer stock;
    private Boolean activo;

    private LocalDateTime fechaCreacion;
    private Categoria categoria;

    // ===== Constructor protegido: se crea con el factory method =====
    protected Producto() {}

    // ===== Factory Method =====
    public static Producto crear(String nombre, BigDecimal precio, Integer stock, Categoria categoria) {
        validarNombre(nombre);
        validarPrecio(precio);

        Producto producto = new Producto();
        producto.nombre = nombre;
        producto.precio = precio;
        producto.stock = stock != null ? stock : 0;
        producto.activo = true;
        producto.fechaCreacion = LocalDateTime.now();
        producto.categoria = categoria;
        return producto;
    }

    // ===== Métodos de Negocio =====
    public void actualizar(String nuevoNombre, BigDecimal nuevoPrecio) {
        validarNombre(nuevoNombre);
        validarPrecio(nuevoPrecio);
        this.nombre = nuevoNombre;
        this.precio = nuevoPrecio;
    }

    public void descontarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }
        if (cantidad > this.stock) {
            throw new sv.gob.mh.shared.exception.BusinessException(
                    "STOCK_INSUFICIENTE",
                    "Stock insuficiente. Disponible: " + this.stock + ", solicitado: " + cantidad);
        }
        this.stock -= cantidad;
    }

    public void agregarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva");
        }
        this.stock += cantidad;
    }

    public void desactivar() {
        this.activo = false;
    }

    public void activar() {
        this.activo = true;
    }

    // ===== Validaciones Privadas =====
    private static void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new sv.gob.mh.shared.exception.BusinessException(
                    "NOMBRE_REQUERIDO", "El nombre del producto es obligatorio");
        }
        if (nombre.length() > 100) {
            throw new sv.gob.mh.shared.exception.BusinessException(
                    "NOMBRE_MUY_LARGO", "El nombre no puede exceder 100 caracteres");
        }
    }

    private static void validarPrecio(BigDecimal precio) {
        if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new sv.gob.mh.shared.exception.BusinessException(
                    "PRECIO_INVALIDO", "El precio debe ser mayor a cero");
        }
    }

    // ===== Getters (sin setters públicos) =====
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public BigDecimal getPrecio() { return precio; }
    public Integer getStock() { return stock; }
    public Boolean getActivo() { return activo; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public Categoria getCategoria() { return categoria; }

    // ===== equals y hashCode basados en ID =====
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Producto that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
```



## Value Object — Ejemplo

```java
package sv.gob.mh.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Dinero {

    private final BigDecimal monto;
    private final String moneda;

    public Dinero(BigDecimal monto, String moneda) {
        if (monto == null || monto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El monto no puede ser negativo");
        }
        if (moneda == null || moneda.isBlank()) {
            throw new IllegalArgumentException("La moneda es obligatoria");
        }
        this.monto = monto;
        this.moneda = moneda;
    }

    public Dinero sumar(Dinero otro) {
        if (!this.moneda.equals(otro.moneda)) {
            throw new IllegalArgumentException("No se pueden sumar monedas diferentes");
        }
        return new Dinero(this.monto.add(otro.monto), this.moneda);
    }

    public BigDecimal getMonto() { return monto; }
    public String getMoneda() { return moneda; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dinero dinero)) return false;
        return Objects.equals(monto, dinero.monto) && Objects.equals(moneda, dinero.moneda);
    }

    @Override
    public int hashCode() {
        return Objects.hash(monto, moneda);
    }
}
```



## Repository Interface — Ejemplo

```java
package sv.gob.mh.domain.repository;

import sv.gob.mh.domain.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Contrato de persistencia para Producto.
 * Extiende JpaRepository de Spring Data JPA para operaciones CRUD + paginación.
 */
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivo(Boolean activo);

    boolean existsByNombre(String nombre);

    List<Producto> findByCategoriaId(Long categoriaId);
}
```

!!! note "Sobre la independencia de frameworks"
    Idealmente, las interfaces de repositorio no deberían extender `JpaRepository` directamente
    (eso acopla Domain a Spring Data). Sin embargo, por pragmatismo en este proyecto, se permite
    usar `JpaRepository` en Domain, ya que simplifica significativamente el código de Infrastructure.
    Para máxima pureza, se puede usar la interface genérica `RepositoryInterface<E, I>` incluida
    en el template y luego implementar con Spring Data en Infrastructure.



## Domain Service — Ejemplo

```java
package sv.gob.mh.domain.service;

import org.springframework.stereotype.Service;
import sv.gob.mh.domain.model.Producto;
import sv.gob.mh.domain.repository.ProductoRepository;
import sv.gob.mh.shared.exception.BusinessException;

/**
 * Servicio de dominio para lógica que involucra múltiples entidades.
 * Se usa cuando la lógica NO pertenece a una sola entidad.
 */
@Service
public class InventarioService {

    private final ProductoRepository productoRepository;

    public InventarioService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    /**
     * Transfiere stock entre dos productos.
     * Esta lógica involucra DOS entidades, por eso va en un Domain Service.
     */
    public void transferirStock(Long origenId, Long destinoId, int cantidad) {
        Producto origen = productoRepository.findById(origenId)
                .orElseThrow(() -> new BusinessException("PRODUCTO_NOT_FOUND", "Producto origen no encontrado"));
        Producto destino = productoRepository.findById(destinoId)
                .orElseThrow(() -> new BusinessException("PRODUCTO_NOT_FOUND", "Producto destino no encontrado"));

        origen.descontarStock(cantidad);  // Lógica en la entidad
        destino.agregarStock(cantidad);   // Lógica en la entidad

        productoRepository.save(origen);
        productoRepository.save(destino);
    }
}
```



## ¿Entidad o Domain Service?

| Si la lógica... | Va en... | Ejemplo |
|-----------------|----------|---------|
| Involucra UNA sola entidad | **Entidad** | `producto.descontarStock(5)` |
| Involucra MÚLTIPLES entidades | **Domain Service** | `inventarioService.transferirStock(...)` |
| Es una validación del propio objeto | **Entidad** (privado) | `validarPrecio(precio)` |
| Requiere datos de otro repositorio | **Domain Service** | Verificar stock global |



## Buenas Prácticas

| Práctica | Descripción |
|----------|-------------|
| **Factory Methods** | Crear entidades con métodos estáticos, no constructor público |
| **Entidades ricas** | Comportamiento de negocio dentro de la entidad |
| **No setters públicos** | Usar métodos de negocio (`descontarStock`, `activar`) |
| **Validaciones en creación** | Validar invariantes en Factory Method |
| **equals/hashCode por ID** | Solo basados en identificador de negocio |
| **Constructor protegido** | `protected` para JPA, no `public` |



## Antipatrones

| Antipatrón | Corrección |
|------------|------------|
| ❌ Modelo anémico (solo getters/setters) | ✅ Agregar métodos de negocio |
| ❌ Validación fuera de la entidad | ✅ Validar dentro del factory method |
| ❌ Setters públicos | ✅ Métodos de negocio con nombre descriptivo |
| ❌ Dependencias de infraestructura | ✅ Domain no importa Spring/JPA (idealmente) |
| ❌ Lógica de negocio en Handler | ✅ Mover a Entidad o Domain Service |
| ❌ equals basado en todos los campos | ✅ equals basado solo en ID |
