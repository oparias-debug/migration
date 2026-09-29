# Infrastructure Layer (Capa de Infraestructura) - Spring Boot

Esta capa implementa los detalles técnicos y frameworks específicos.

Ver documentación completa de arquitectura en: [README principal](../README.md)

## Responsabilidades

- **Entidades JPA** (`@Entity`) en `persistence/entity` — el mapeo a las tablas
- Implementaciones de repositorios (Spring Data JPA) en `persistence/repository`
- Configuraciones (OpenAPI, Security, etc.)
- Integraciones con servicios externos (RestClient, WebClient)
- Mensajería (Kafka, RabbitMQ)

> **Esta es la estructura base.** **Podrán agregarse subcarpetas según la estructura
> indicada por MH** para los proyectos de las células; el árbol completo y las
> equivalencias están en el [README principal](../README.md#estructura-ampliada-recomendada-por-mh).

## Ejemplo

```java
// infrastructure/persistence/entity/ProductoEntity.java
@Entity
@Table(name = "PRODUCTOS")
public class ProductoEntity {
    @Id @GeneratedValue
    private Long id;
    private String nombre;
    private BigDecimal precio;
    // getters y setters
}
```

```java
// infrastructure/persistence/repository/ProductoJpaRepository.java
@Repository
public interface ProductoJpaRepository extends JpaRepository<ProductoEntity, Long> {
    List<ProductoEntity> findByActivo(Boolean activo);
    boolean existsByNombre(String nombre);
}
```

```java
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
```
