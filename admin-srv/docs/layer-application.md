# Application Layer (Capa de Aplicación)

## Propósito

La capa Application **orquesta los casos de uso** del negocio siguiendo el patrón **CQRS** (Command Query Responsibility Segregation). Coordina servicios de dominio, maneja transacciones y NO contiene lógica de negocio.



## Responsabilidades

| Responsabilidad | Descripción |
|----------------|-------------|
| Orquestar casos de uso | Coordinar entidades, servicios de dominio y repositorios |
| Transacciones | Manejar `@Transactional` (iniciar, commit, rollback) |
| Comandos (escritura) | Definir y ejecutar operaciones que modifican estado |
| Queries (lectura) | Definir y ejecutar consultas de solo lectura |
| Delegación | Delegar lógica de negocio al Domain |

## Lo que NO debe hacer

| Prohibición | Razón |
|-------------|-------|
| ❌ Lógica de negocio | Va en Domain |
| ❌ Acceso HTTP directo | Va en API |
| ❌ SQL / JPA queries | Va en Infrastructure |
| ❌ Validación de entrada HTTP | Va en API (Bean Validation) |



## Estructura

> **Esta es la estructura base.** La plantilla crea únicamente lo indispensable
> para arrancar; **podrán agregarse subcarpetas según la estructura indicada por
> MH** para los proyectos de las células. Ver
> [Estructura ampliada recomendada por MH](architecture.md#estructura-ampliada-recomendada-por-mh).

```
application/
├── command/
│   ├── CrearProductoCommand.java
│   ├── ActualizarProductoCommand.java
│   └── EliminarProductoCommand.java
├── handler/
│   ├── CrearProductoHandler.java
│   ├── ActualizarProductoHandler.java
│   └── EliminarProductoHandler.java
└── query/
    ├── ObtenerProductoQuery.java
    └── ListarProductosQuery.java
```



## Command — Ejemplo

```java
package sv.gob.mh.application.command;

import java.math.BigDecimal;

/**
 * Command inmutable para crear un producto.
 * Los campos son final — no se modifican después de la creación.
 */
public class CrearProductoCommand {

    private final String nombre;
    private final BigDecimal precio;
    private final Integer stock;
    private final Long categoriaId;

    public CrearProductoCommand(String nombre, BigDecimal precio, Integer stock, Long categoriaId) {
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.categoriaId = categoriaId;
    }

    public String getNombre() { return nombre; }
    public BigDecimal getPrecio() { return precio; }
    public Integer getStock() { return stock; }
    public Long getCategoriaId() { return categoriaId; }
}
```



## Handler — Ejemplo

```java
package sv.gob.mh.application.handler;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.gob.mh.application.command.CrearProductoCommand;
import sv.gob.mh.domain.model.Categoria;
import sv.gob.mh.domain.model.Producto;
import sv.gob.mh.domain.repository.CategoriaRepository;
import sv.gob.mh.domain.repository.ProductoRepository;
import sv.gob.mh.shared.exception.BusinessException;
import sv.gob.mh.shared.exception.NotFoundException;

@Service
public class CrearProductoHandler {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public CrearProductoHandler(ProductoRepository productoRepository,
                                CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional
    public Producto handle(CrearProductoCommand command) {
        // 1. Validar que no exista producto duplicado
        if (productoRepository.existsByNombre(command.getNombre())) {
            throw new BusinessException("PRODUCTO_DUPLICADO",
                    "Ya existe un producto con el nombre: " + command.getNombre());
        }

        // 2. Obtener categoría (si se especificó)
        Categoria categoria = null;
        if (command.getCategoriaId() != null) {
            categoria = categoriaRepository.findById(command.getCategoriaId())
                    .orElseThrow(() -> new NotFoundException("Categoria", command.getCategoriaId()));
        }

        // 3. Crear producto (lógica de dominio en la entidad)
        Producto producto = Producto.crear(
                command.getNombre(),
                command.getPrecio(),
                command.getStock(),
                categoria
        );

        // 4. Persistir
        return productoRepository.save(producto);
    }
}
```



## Query — Ejemplo

```java
package sv.gob.mh.application.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import sv.gob.mh.api.dto.PageResponseDTO;
import sv.gob.mh.api.dto.ProductoResponseDTO;
import sv.gob.mh.domain.model.Producto;
import sv.gob.mh.domain.repository.ProductoRepository;
import sv.gob.mh.shared.mapper.ProductoMapper;

@Service
public class ListarProductosQuery {

    private final ProductoRepository repository;
    private final ProductoMapper mapper;

    public ListarProductosQuery(ProductoRepository repository, ProductoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public PageResponseDTO<ProductoResponseDTO> ejecutar(int page, int size, String sort) {
        Sort sorting = Sort.by("id").ascending();
        if (sort != null && sort.contains(",")) {
            String[] parts = sort.split(",");
            sorting = Sort.by(Sort.Direction.fromString(parts[1]), parts[0]);
        }

        Page<Producto> resultado = repository.findAll(PageRequest.of(page, Math.min(size, 100), sorting));

        var content = resultado.getContent().stream()
                .map(mapper::toResponseDTO)
                .toList();

        return PageResponseDTO.of(content, page, size, resultado.getTotalElements());
    }
}
```



## Flujo de Uso desde Controller

```java
// Controller → Handler → Domain → Repository
@PostMapping
public ResponseEntity<ProductoResponseDTO> crear(@Valid @RequestBody ProductoRequestDTO request) {
    // 1. DTO → Command (Mapper)
    var command = mapper.toCommand(request);

    // 2. Ejecutar caso de uso (Handler)
    var producto = crearHandler.handle(command);

    // 3. Entity → DTO (Mapper)
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(mapper.toResponseDTO(producto));
}
```



## Buenas Prácticas

| Práctica | Descripción |
|----------|-------------|
| **Commands inmutables** | Campos `final`, solo getters, sin setters |
| **@Transactional en Handler** | Transacción controlada en la capa de aplicación |
| **Sin lógica de negocio** | El Handler coordina, el Domain decide |
| **Un Handler por caso de uso** | Cada caso de uso tiene su propio Handler |
| **Queries separados** | Lectura y escritura separados (CQRS) |
| **Constructor injection** | Inyectar dependencias por constructor |



## Antipatrones

| Antipatrón | Corrección |
|------------|------------|
| ❌ Reglas de negocio en el Handler | ✅ Mover a Entidad o Domain Service |
| ❌ Command con setters | ✅ Campos `final`, solo constructor |
| ❌ Handler que retorna DTO | ✅ Retornar Entidad, mapear en Controller |
| ❌ Múltiples responsabilidades en un Handler | ✅ Un Handler = un caso de uso |
| ❌ Query que modifica estado | ✅ Queries solo lectura |
