# Arquitectura de la Aplicación Spring Boot

## Visión General

Esta aplicación sigue una **Arquitectura Hexagonal (Ports & Adapters)** combinada con principios de **Clean Architecture** y **Domain-Driven Design (DDD)**. El objetivo es crear un sistema mantenible, testeable y desacoplado de frameworks específicos.

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
| `infrastructure/repository` | `infrastructure/persistence/repository` | Repositorio Spring Data + adaptador |
| — | `infrastructure/persistence/entity` | **Entidades JPA (`@Entity`)** |
| — | `infrastructure/persistence/dto` | Proyecciones de consulta |
| `infrastructure/messaging` | `infrastructure/messaging/{publisher,serialization}` | Se separa el envío de la serialización |
| `application/{command,handler,query}` | `application/handler` | CQRS opcional: las células agrupan en `handler` |
| `api/dto` | `api/dto/{request,response}` | Se separa la entrada de la salida |
| `shared/{enum,exception,mapper}` | `shared/{dto/response,exception/enums,helper,mapper}` | Mismo propósito, más granular |

Ninguna de estas carpetas es obligatoria desde el día uno: se crean cuando el caso de uso
las pide. Lo que sí es obligatorio es **respetar la capa**: una entidad JPA nunca vive en
`domain/`, y una regla de negocio nunca vive en `infrastructure/`.

## Descripción de Capas

### API Layer (api)
**Responsabilidad**: Exponer endpoints REST y manejar HTTP

- **Controladores**: Reciben peticiones HTTP, validan DTOs, orquestan handlers
- **DTOs**: Objetos para request/response, con validaciones
- **Sin lógica de negocio**: Solo transformación y delegación

[Ver documentación detallada](./api/README.md)



### Application Layer (application)
**Responsabilidad**: Coordinar casos de uso del negocio (CQRS)

- **Commands**: Intención de modificar estado (crear, actualizar, eliminar)
- **Handlers**: Ejecutan commands, coordinan servicios de dominio
- **Queries**: Consultas de solo lectura
- **Sin lógica de negocio**: Solo orquestación y transacciones

[Ver documentación detallada](./application/README.md)



### Domain Layer (domain)
**Responsabilidad**: Contener TODA la lógica de negocio

- **Modelos de dominio**: POJOs o clases Java puras que representan el dominio de
  negocio, con identidad y comportamiento (Aggregate Roots). **Sin anotaciones JPA**
- **Value Objects**: Objetos inmutables sin identidad
- **Servicios de Dominio**: Lógica que involucra múltiples entidades
- **Repositorios (interfaces)**: Contratos de persistencia
- **Independiente de frameworks**: No debe depender de tecnologías específicas

[Ver documentación detallada](./domain/README.md)



### Infrastructure Layer (infrastructure)
**Responsabilidad**: Implementar detalles técnicos y frameworks

- **Repository Implementations**: JPA/Hibernate, Spring Data
- **Configuraciones**: OpenAPI, Security, Jackson
- **Integraciones**: REST clients, servicios externos
- **Mensajería**: Kafka producers/consumers
- **Frameworks específicos**: Spring Boot, JPA

[Ver documentación detallada](./infrastructure/README.md)



### Shared Layer (shared)
**Responsabilidad**: Código reutilizable por todas las capas

- **Enumeraciones**: Tipos compartidos (Estados, Roles, etc.)
- **Excepciones**: Jerarquía de excepciones de negocio y técnicas
- **Mappers**: Conversión entre DTOs, modelos de dominio y entidades JPA
- **Utilidades**: Helpers, validadores, constantes
- **Sin dependencias de capas**: No conoce API, Application, Domain o Infrastructure

[Ver documentación detallada](./shared/README.md)



## Flujo de una Petición

```
1. Cliente HTTP
   ↓
2. API Layer (Controller)
   - Recibe ProductoRequestDTO
   - Valida con Bean Validation
   ↓
3. Mapper (Shared)
   - Convierte DTO → CrearProductoCommand
   ↓
4. Application Layer (Handler)
   - Inicia transacción (@Transactional)
   - Obtiene Categoria del repositorio
   - Valida reglas de negocio
   ↓
5. Domain Layer (Modelo de dominio)
   - Producto.crear() - Factory method
   - Validaciones de dominio
   - Lógica de negocio
   ↓
6. Infrastructure Layer (Repository)
   - save(producto) - Spring Data JPA
   - Guarda en base de datos
   ↓
7. Application Layer (Handler)
   - Commit de transacción
   - Retorna Producto al Controller
   ↓
8. Mapper (Shared)
   - Convierte Producto → ProductoResponseDTO
   ↓
9. API Layer (Controller)
   - ResponseEntity HTTP 201 Created
   ↓
10. Cliente HTTP
```

## Principios de la Arquitectura

### 1. Separación de Responsabilidades (SRP)
Cada capa tiene una responsabilidad única y bien definida.

### 2. Inversión de Dependencias (DIP)
- Domain define interfaces de repositorios
- Infrastructure implementa esas interfaces
- Domain no depende de Infrastructure

### 3. CQRS (Command Query Responsibility Segregation)
- **Commands**: Modifican estado (void o ID)
- **Queries**: Consultan datos (retornan objetos)

### 4. Domain-Driven Design (DDD)
- Lenguaje ubicuo del negocio en el código
- Modelos de dominio ricos con comportamiento
- Agregados con root que garantiza consistencia

### 5. Independencia de Frameworks
- Domain no debe conocer Spring Boot, JPA, etc.
- Fácil cambio de tecnologías sin afectar el negocio

## Dependencias entre Capas

```
┌─────────────────────────────────────────────┐
│              API Layer                      │
│  (Controllers, DTOs)                        │
└──────────────┬──────────────────────────────┘
               │ depende de ↓
┌──────────────▼──────────────────────────────┐
│         Application Layer                   │
│  (Commands, Handlers, Queries)              │
└──────────────┬──────────────────────────────┘
               │ depende de ↓
┌──────────────▼──────────────────────────────┐
│           Domain Layer                      │
│  (Entities, Services, Repository interfaces)│
└──────────────▲──────────────────────────────┘
               │ implementa ↑
┌──────────────┴──────────────────────────────┐
│        Infrastructure Layer                 │
│  (Repository Impl, Config, External)        │
└─────────────────────────────────────────────┘

         ┌────────────────────┐
         │   Shared Layer     │
         │ (usado por todos)  │
         └────────────────────┘
```

**Reglas de dependencia:**
- API puede depender de: Application, Domain, Shared
- Application puede depender de: Domain, Shared
- Domain puede depender de: Shared (mínimo)
- Infrastructure puede depender de: Domain, Shared
- Domain NO puede depender de: API, Application, Infrastructure
- Shared NO puede depender de: ninguna otra capa



**Última actualización**: Noviembre 2025  
**Versión de Spring Boot**: 3.5.16  
**Java**: 21  
**Ministerio de Hacienda - El Salvador**
