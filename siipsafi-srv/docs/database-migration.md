# Migración y Gestión de Base de Datos

## Visión General

| Aspecto | Detalle |
|---------|---------|
| **Motor** | Oracle Database 19c / 21c |
| **Driver** | `ojdbc11` (23.7.0.25.01) |
| **ORM** | Spring Data JPA + Hibernate 6 |
| **Pool** | HikariCP (incluido en Spring Boot) |
| **DDL** | `hibernate.ddl-auto=none` (producción) |
| **Migración** | Scripts SQL manuales o Flyway (opcional) |



## Configuración de DataSource

### application.yml

```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:oracle:thin:@//localhost:1521/XEPDB1}
    username: ${SPRING_DATASOURCE_USERNAME:APP_USER}
    password: ${SPRING_DATASOURCE_PASSWORD:secret}
    driver-class-name: oracle.jdbc.OracleDriver
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      idle-timeout: 300000        # 5 minutos
      max-lifetime: 1200000       # 20 minutos
      connection-timeout: 20000   # 20 segundos
      pool-name: HikariPool-App
      validation-timeout: 5000
      leak-detection-threshold: 60000  # 1 minuto
  jpa:
    hibernate:
      ddl-auto: none              # NUNCA auto-crear en producción
    database-platform: org.hibernate.dialect.OracleDialect
    show-sql: false
    properties:
      hibernate:
        format_sql: true
        default_schema: APP_SCHEMA
        jdbc:
          batch_size: 25
        order_inserts: true
        order_updates: true
```

### Pool por Ambiente

| Ambiente | min-idle | max-pool-size | Justificación |
|----------|----------|---------------|---------------|
| DEV | 2 | 5 | Uso bajo |
| TEST | 2 | 10 | Tests paralelos |
| QA | 5 | 15 | Simular carga |
| PRD | 5 | 20 | Producción |



## Estrategia DDL

### Regla de Oro

> **`hibernate.ddl-auto=none`** en QA y PRD. Hibernate **nunca** debe modificar el esquema.

| Valor | Uso | Ambiente |
|-------|-----|----------|
| `none` | Sin cambios al esquema | **QA, PRD** |
| `validate` | Valida que entidades coincidan con BD | TEST |
| `create-drop` | Crea y destruye (tests) | Test con H2 |
| `update` | ⚠️ Peligroso — no usar | Nunca |



## Scripts SQL Manuales

### Estructura de Scripts

```
sql/
├── V001__crear_tabla_producto.sql
├── V002__agregar_campo_estado.sql
├── V003__crear_indice_nombre.sql
├── V004__insertar_datos_base.sql
└── rollback/
    ├── R001__rollback_tabla_producto.sql
    └── R002__rollback_campo_estado.sql
```

### Convención de Nombres

```
V{NNN}__{descripcion_en_snake_case}.sql
R{NNN}__rollback_{descripcion}.sql
```

### Ejemplo: Script de Creación

```sql
-- V001__crear_tabla_producto.sql
-- Autor: equipo-backend
-- Fecha: 2025-01-15
-- Descripción: Tabla principal de productos

CREATE TABLE APP_SCHEMA.PRODUCTO (
    ID           NUMBER(19)    GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    NOMBRE       VARCHAR2(200) NOT NULL,
    DESCRIPCION  VARCHAR2(1000),
    PRECIO       NUMBER(12,2)  NOT NULL,
    ACTIVO       NUMBER(1)     DEFAULT 1 NOT NULL,
    CREADO_POR   VARCHAR2(100) NOT NULL,
    FECHA_CREACION TIMESTAMP   DEFAULT SYSTIMESTAMP NOT NULL,
    MODIFICADO_POR VARCHAR2(100),
    FECHA_MODIFICACION TIMESTAMP,
    CONSTRAINT CK_PRODUCTO_PRECIO CHECK (PRECIO >= 0),
    CONSTRAINT CK_PRODUCTO_ACTIVO CHECK (ACTIVO IN (0, 1))
);

CREATE INDEX IDX_PRODUCTO_NOMBRE ON APP_SCHEMA.PRODUCTO(NOMBRE);
CREATE INDEX IDX_PRODUCTO_ACTIVO ON APP_SCHEMA.PRODUCTO(ACTIVO);

COMMENT ON TABLE APP_SCHEMA.PRODUCTO IS 'Catálogo de productos';
COMMENT ON COLUMN APP_SCHEMA.PRODUCTO.ID IS 'Identificador único';
COMMENT ON COLUMN APP_SCHEMA.PRODUCTO.ACTIVO IS '1=Activo, 0=Inactivo';
```



## Flyway (Opcional)

### Dependencia Maven

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-oracle</artifactId>
</dependency>
```

### Configuración

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    schemas: APP_SCHEMA
    baseline-on-migrate: true
    baseline-version: '0'
    validate-on-migrate: true
    out-of-order: false
```

### Estructura Flyway

```
src/main/resources/db/migration/
├── V1__crear_tabla_producto.sql
├── V2__agregar_campo_estado.sql
└── V3__datos_iniciales.sql
```



## Entidades JPA ↔ Oracle

### Mapeo de Tipos

| Java / JPA | Oracle | Notas |
|-------------|--------|-------|
| `Long` + `@GeneratedValue(IDENTITY)` | `NUMBER(19) GENERATED AS IDENTITY` | PK auto-increment |
| `String` | `VARCHAR2(N)` | Especificar length |
| `BigDecimal` | `NUMBER(P,S)` | Precision y scale |
| `Boolean` | `NUMBER(1)` | 0/1 |
| `LocalDateTime` | `TIMESTAMP` | Sin zona horaria |
| `Instant` | `TIMESTAMP WITH TIME ZONE` | Con zona horaria |
| `@Lob String` | `CLOB` | Texto largo |
| `byte[]` | `BLOB` | Binario |

### Entidad Ejemplo

```java
@Entity
@Table(name = "PRODUCTO", schema = "APP_SCHEMA")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 200)
    private String nombre;

    @Column(name = "PRECIO", nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @Column(name = "ACTIVO", nullable = false)
    private Boolean activo;

    @Column(name = "CREADO_POR", nullable = false, length = 100)
    private String creadoPor;

    @Column(name = "FECHA_CREACION", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "MODIFICADO_POR", length = 100)
    private String modificadoPor;

    @Column(name = "FECHA_MODIFICACION")
    private LocalDateTime fechaModificacion;

    // Constructor, getters, métodos de dominio...
}
```



## Repository Spring Data

```java
package sv.gob.mh.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoJpaRepository extends JpaRepository<Producto, Long> {

    boolean existsByNombre(String nombre);

    @Query("SELECT p FROM Producto p WHERE p.activo = true AND p.nombre LIKE %:filtro%")
    Page<Producto> buscarActivos(@Param("filtro") String filtro, Pageable pageable);

    @Query(value = "SELECT * FROM APP_SCHEMA.PRODUCTO WHERE ROWNUM <= :limit",
           nativeQuery = true)
    List<Producto> findTopN(@Param("limit") int limit);
}
```



## Auditoría Automática con JPA

```java
@MappedSuperclass
public abstract class BaseEntity {

    @Column(name = "CREADO_POR", updatable = false)
    private String creadoPor;

    @Column(name = "FECHA_CREACION", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "MODIFICADO_POR")
    private String modificadoPor;

    @Column(name = "FECHA_MODIFICACION")
    private LocalDateTime fechaModificacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaModificacion = LocalDateTime.now();
    }
}
```



## Comparación Quarkus ↔ Spring Boot

| Aspecto | Quarkus | Spring Boot |
|---------|---------|-------------|
| ORM | Hibernate + Panache | Spring Data JPA + Hibernate |
| Repository | `PanacheRepository<T>` | `JpaRepository<T, ID>` |
| Config DS | `quarkus.datasource.*` | `spring.datasource.*` |
| Pool | Agroal | HikariCP |
| DDL | `quarkus.hibernate-orm.database.generation` | `spring.jpa.hibernate.ddl-auto` |
| Migración | Flyway (`quarkus-flyway`) | Flyway (`flyway-database-oracle`) |
| Active Record | `entity.persist()` (Panache) | `repository.save(entity)` |
| Named query | `Producto.find("nombre", name)` | `findByNombre(String name)` |
| Native query | `PanacheEntityBase.find(nativeQuery)` | `@Query(nativeQuery = true)` |



## Buenas Prácticas

| Práctica | Descripción |
|----------|-------------|
| **`ddl-auto=none`** | Producción nunca auto-genera esquema |
| **Scripts versionados** | Cada cambio es un script SQL numerado |
| **Rollback scripts** | Siempre preparar script de reversión |
| **Pool sizing** | No exceder conexiones máximas de Oracle |
| **Índices** | Crear índices para campos de búsqueda frecuente |
| **Batch operations** | Configurar `batch_size` para inserts masivos |
| **`validate` en TEST** | Detectar desincronización entidad ↔ tabla |
| **Nombres en UPPER_CASE** | Tablas y columnas Oracle en mayúsculas |



## Antipatrones

| Antipatrón | Corrección |
|------------|------------|
| ❌ `ddl-auto=update` en producción | ✅ `none` + scripts SQL manuales |
| ❌ Oracle driver hardcodeado en pom | ✅ Ya incluido via `ojdbc11` dependency |
| ❌ HikariCP sin límites | ✅ Configurar `maximum-pool-size` |
| ❌ Credenciales en `application.yml` | ✅ Variables de entorno / Secrets |
| ❌ `SELECT *` en consultas | ✅ Proyección específica con `@Query` |
| ❌ Sin índices en campos de filtro | ✅ Crear índices en campos WHERE frecuentes |
