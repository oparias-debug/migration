# Arquitectura de la Aplicación

## Visión General

Esta aplicación sigue una **Arquitectura Hexagonal (Ports & Adapters)** combinada con principios de **Clean Architecture** y **Domain-Driven Design (DDD)**. El objetivo es crear un sistema mantenible, testeable y desacoplado de frameworks específicos.



## Stack Tecnológico

| Componente | Quarkus (referencia) | Spring Boot (este proyecto) |
|-----------|---------------------|----------------------------|
| Framework | Quarkus 3.24.x | Spring Boot 3.5.16 |
| REST | JAX-RS (`@Path`, `@GET`) | Spring MVC (`@RestController`, `@GetMapping`) |
| DI | CDI (`@Inject`, `@ApplicationScoped`) | Spring IoC (`@Autowired`, `@Service`) |
| ORM | Hibernate + Panache | Spring Data JPA + Hibernate |
| Seguridad | SmallRye JWT / OIDC | Spring Security + OAuth2 Resource Server |
| API Docs | SmallRye OpenAPI | SpringDoc OpenAPI 2.9.0 |
| Health | SmallRye Health (`/q/health`) | Spring Actuator (`/actuator/health`) |
| Config | `application.properties` | `application.yml` |
| Build | Maven + Quarkus Plugin | Maven + Spring Boot Plugin |
| Tests | JUnit 5 + REST Assured | JUnit 5 + REST Assured / MockMvc |



## Estructura de Capas

> **Esta es la estructura base.** La plantilla crea únicamente lo indispensable
> para arrancar; **podrán agregarse subcarpetas según la estructura indicada por
> MH** para los proyectos de las células. Ver
> [Estructura ampliada recomendada por MH](#estructura-ampliada-recomendada-por-mh).

```
sv.gob.mh/
├── api/                    # API Layer (Capa de Presentación)
│   ├── controller/         # Controladores REST (@RestController)
│   └── dto/                # Data Transfer Objects
│       ├── request/        # DTOs de entrada (lo que recibe el endpoint)
│       └── response/       # DTOs de salida (lo que devuelve el endpoint)
│
├── application/            # Application Layer (Casos de Uso)
│   ├── command/            # Comandos (escritura - CQRS)
│   ├── handler/            # Handlers de comandos y queries
│   └── query/              # Queries (lectura - CQRS)
│
├── domain/                 # Domain Layer (Núcleo del Negocio)
│   ├── model/              # POJOs o clases Java puras que representan el dominio de negocio
│   ├── repository/         # Interfaces de repositorios (contratos)
│   └── service/            # Servicios de dominio
│
├── infrastructure/         # Infrastructure Layer (Detalles Técnicos)
│   ├── config/             # Configuraciones (OpenAPI, Security, Audit, etc.)
│   ├── exception/          # Manejo técnico de errores (@RestControllerAdvice)
│   ├── external/           # Integraciones con servicios externos
│   ├── messaging/          # Mensajería (Kafka, eventos)
│   └── persistence/        # Persistencia
│       ├── dto/            # Proyecciones de consulta
│       ├── entity/         # Entidades JPA (@Entity) — el mapeo a la tabla
│       └── repository/     # Repositorios Spring Data JPA y adaptadores
│
└── shared/                 # Shared Layer (Código Compartido)
    ├── dto/                # DTOs transversales
    │   └── response/       # Respuesta estándar
    ├── enum/               # Enumeraciones
    ├── exception/          # Excepciones personalizadas + RemoteLogger
    ├── helper/             # Utilidades y validadores
    └── mapper/             # Mappers DTO ↔ modelo ↔ entidad
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



## Estructura Ampliada Recomendada por MH

La estructura base de arriba es el punto de partida. Los proyectos de las células siguen
la organización recomendada por Red Hat y adoptada por el Ministerio de Hacienda, que
**agrega subcarpetas dentro de las mismas cinco capas**. No se cambia de arquitectura:
se agregan carpetas a medida que el proyecto las necesita.

```
sv.gob.mh/
├── api/
│   ├── controller/
│   └── dto/
│       ├── request/
│       └── response/
├── application/
│   └── handler/
├── domain/
│   ├── model/                  # POJOs o clases Java puras del dominio de negocio
│   │   ├── dto/
│   │   └── enums/
│   ├── publisher/              # Contratos de publicación de eventos
│   ├── repository/             # Contratos de persistencia (interfaces)
│   └── usecase/                # Casos de uso del negocio
│       └── bulk/               # Casos de uso masivos
├── infrastructure/
│   ├── config/
│   ├── exception/
│   ├── messaging/
│   │   ├── publisher/
│   │   └── serialization/
│   └── persistence/
│       ├── dto/                # Proyecciones de consulta
│       ├── entity/             # Entidades JPA (@Entity) — AQUÍ van las entidades
│       └── repository/         # Implementaciones de los contratos del dominio
└── shared/
    ├── dto/
    │   └── response/
    ├── exception/
    │   └── enums/
    ├── helper/
    └── mapper/
```

### Equivalencias entre la estructura base y la de MH

| Estructura base (plantilla) | Estructura MH (células) | Nota |
|-----------------------------|-------------------------|------|
| `domain/model` | `domain/model` (+ `dto/`, `enums/`) | Clases Java puras. **Sin anotaciones JPA** |
| `domain/service` | `domain/usecase` (+ `bulk/`) | Misma responsabilidad, distinta nomenclatura |
| `domain/repository` | `domain/repository` | Interfaces (contratos), sin JPA |
| — | `domain/publisher` | Contrato de publicación de eventos |
| `infrastructure/repository` | `infrastructure/persistence/repository` | Implementación JPA / Panache |
| — | `infrastructure/persistence/entity` | **Entidades JPA (`@Entity`)** |
| — | `infrastructure/persistence/dto` | Proyecciones de consulta |
| `infrastructure/messaging` | `infrastructure/messaging/{publisher,serialization}` | Se separa el envío de la serialización |
| `application/{command,handler,query}` | `application/handler` | CQRS opcional: las células agrupan en `handler` |
| `api/dto` | `api/dto/{request,response}` | Se separa la entrada de la salida |
| `shared/{enum,exception,mapper}` | `shared/{dto/response,exception/enums,helper,mapper}` | Mismo propósito, más granular |

Ninguna de estas carpetas es obligatoria desde el día uno: se crean cuando el caso de uso
las pide. Lo que sí es obligatorio es **respetar la capa**: una entidad JPA nunca vive en
`domain/`, y una regla de negocio nunca vive en `infrastructure/`.



## Flujo de Datos

```
1. Cliente HTTP
   ↓
2. API Layer (Controller — @RestController)
   - Recibe DTO de request
   - Valida con Bean Validation (@Valid)
   ↓
3. Mapper (Shared — @Component)
   - Convierte DTO → Command
   ↓
4. Application Layer (Handler — @Service)
   - Inicia transacción (@Transactional)
   - Valida reglas de negocio
   - Coordina servicios de dominio
   ↓
5. Domain Layer (Entidad/Service)
   - Factory methods
   - Validaciones de dominio
   - Lógica de negocio
   ↓
6. Infrastructure Layer (Repository — @Repository / JpaRepository)
   - Persistencia JPA/Hibernate
   - Guarda en base de datos Oracle
   ↓
7. Application Layer (Handler)
   - Commit de transacción
   - Retorna resultado al Controller
   ↓
8. Mapper (Shared)
   - Convierte Entity → ResponseDTO
   ↓
9. API Layer (Controller)
   - ResponseEntity HTTP con código apropiado
   ↓
10. Cliente HTTP
```



## Principios Arquitectónicos

### 1. Separación de Responsabilidades (SRP)
Cada capa tiene una responsabilidad única y bien definida.

### 2. Inversión de Dependencias (DIP)
- Domain define interfaces de repositorios
- Infrastructure implementa esas interfaces
- Domain **NO** depende de Infrastructure

### 3. CQRS (Command Query Responsibility Segregation)
- **Commands**: Modifican estado (crear, actualizar, eliminar)
- **Queries**: Consultan datos (retornan objetos, no modifican estado)

### 4. Domain-Driven Design (DDD)
- Lenguaje ubicuo del negocio en el código
- Entidades ricas con comportamiento
- Agregados con root que garantiza consistencia

### 5. Independencia de Frameworks
- Domain no debe conocer Spring Boot, JPA, etc.
- Fácil cambio de tecnologías sin afectar el negocio



## Dependencias entre Capas

```
┌─────────────────────────────────────────────┐
│              API Layer                      │
│  (@RestController, DTOs)                    │
└──────────────┬──────────────────────────────┘
               │ depende de ↓
┌──────────────▼──────────────────────────────┐
│         Application Layer                   │
│  (@Service — Commands, Handlers, Queries)   │
└──────────────┬──────────────────────────────┘
               │ depende de ↓
┌──────────────▼──────────────────────────────┐
│           Domain Layer                      │
│  (Entities, Services, Repository interfaces)│
└──────────────▲──────────────────────────────┘
               │ implementa ↑
┌──────────────┴──────────────────────────────┐
│        Infrastructure Layer                 │
│  (JpaRepository, @Configuration, External)  │
└─────────────────────────────────────────────┘

         ┌────────────────────┐
         │   Shared Layer     │
         │ (usado por todos)  │
         └────────────────────┘
```

**Reglas de dependencia:**

| Capa | Puede depender de | NO puede depender de |
|------|-------------------|---------------------|
| API | Application, Domain, Shared | Infrastructure |
| Application | Domain, Shared | API, Infrastructure |
| Domain | Shared (mínimo) | API, Application, Infrastructure |
| Infrastructure | Domain, Shared | API, Application |
| Shared | Ninguna capa | API, Application, Domain, Infrastructure |



## Convenciones de Nomenclatura

| Tipo | Patrón | Ejemplo |
|------|--------|---------|
| Entidades | Sustantivo singular | `Producto`, `Categoria` |
| Commands | Verbo + Sustantivo + "Command" | `CrearProductoCommand` |
| Handlers | Nombre del Command + "Handler" | `CrearProductoHandler` |
| Queries | Verbo + Sustantivo + "Query" | `ObtenerProductoQuery` |
| DTOs Request | Sustantivo + "RequestDTO" | `ProductoRequestDTO` |
| DTOs Response | Sustantivo + "ResponseDTO" | `ProductoResponseDTO` |
| Repositories (interface) | Sustantivo + "Repository" | `ProductoRepository` |
| Repositories (impl) | Sustantivo + "RepositoryImpl" | `ProductoRepositoryImpl` |
| Services | Sustantivo + "Service" | `InventarioService` |
| Exceptions | Descriptor + "Exception" | `BusinessException` |
| Mappers | Sustantivo + "Mapper" | `ProductoMapper` |
| Enums | Descriptor sin sufijo | `EstadoOrden`, `TipoMovimiento` |



## Ventajas de esta Arquitectura

- **Mantenibilidad**: Cambios localizados en capas específicas
- **Testabilidad**: Fácil crear tests unitarios sin frameworks
- **Escalabilidad**: Queries y Commands pueden optimizarse independientemente
- **Independencia de frameworks**: Domain no depende de Spring Boot
- **Reutilización**: Código compartido en Shared Layer
- **Claridad**: Responsabilidades bien definidas
- **Evolución**: Fácil agregar nuevos casos de uso



## Recursos Adicionales

- [Clean Architecture - Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Hexagonal Architecture - Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- [Domain-Driven Design - Eric Evans](https://www.domainlanguage.com/ddd/)
- [CQRS Pattern - Martin Fowler](https://martinfowler.com/bliki/CQRS.html)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
