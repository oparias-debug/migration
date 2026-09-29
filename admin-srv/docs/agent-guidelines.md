# Lineamientos de Desarrollo para Agentes de IA

> **Documento de referencia** para agentes de IA (GitHub Copilot, Cursor, Aider, etc.) que trabajen en proyectos generados por este template Spring Boot del Ministerio de Hacienda de El Salvador.



## 1. Contexto del Proyecto

### Stack Tecnológico

| Componente | Tecnología | Versión |
|-----------|------------|---------|
| Framework | Spring Boot | 3.5.16 |
| Lenguaje | Java | 21 |
| Build | Maven | 3.9+ (wrapper incluido) |
| Base de Datos | Oracle Database | 23c |
| ORM | Spring Data JPA + Hibernate | - |
| Seguridad | Spring Security + OAuth2 Resource Server | - |
| API docs | SpringDoc OpenAPI (Swagger) | 2.9.0 |
| Tests unitarios | JUnit 5 + REST Assured | - |
| Tests integración | Karate | 1.5.x |
| Tests de carga | Karate en paralelo (perfil `stress-tests`) | — |
| CI/CD | Tekton + ArgoCD | - |
| Contenedores | Red Hat UBI 9 / OpenJDK 21 | - |
| Registry | Quay.io | - |
| Calidad | SonarQube + JaCoCo | - |
| Plataforma | OpenShift (GCP) | - |

### Arquitectura

El proyecto implementa **Arquitectura Hexagonal** + **Clean Architecture** + **DDD** + **CQRS**:

```
sv.gob.mh/
├── api/              # Capa de Presentación (Controllers, DTOs request/response)
├── application/      # Capa de Aplicación (Commands, Handlers, Queries)
├── domain/           # Capa de Dominio (Modelos POJO, Services, Repository interfaces)
├── infrastructure/   # Capa de Infraestructura (Config, External, Messaging,
│                     #   Persistence: entity + repository)
└── shared/           # Código compartido (Exceptions, Mappers, Enums, Helpers)
```

> **Esta es la estructura base.** Podrán agregarse subcarpetas según la estructura
> indicada por MH para las células. La estructura ampliada y la tabla de equivalencias
> están en [architecture.md](architecture.md#estructura-ampliada-recomendada-por-mh).

### Servicios Externos Obligatorios

- **AUTHZ_SERVICE_URL**: `authorization-service` del marco (`/api/v1/authz`)
- **CONFIG_SERVICE_URL**: config-server del marco (`inventario-service`, `/api/v1/config/service`)
- **Keycloak**: Autenticación OAuth2/OIDC
- **RemoteLogger**: Servicio de logging remoto (integrado en shared)



## 2. Reglas de Arquitectura (OBLIGATORIAS)

### 2.1 Reglas de Dependencia entre Capas

```
API → Application → Domain ← Infrastructure
                      ↑
                    Shared (usado por todas)
```

| Regla | Descripción |
|-------|-------------|
| **R1** | `domain/` NUNCA debe importar clases de `api/`, `application/` o `infrastructure/` |
| **R2** | `application/` NUNCA debe importar clases de `api/` o `infrastructure/` |
| **R3** | `shared/` NUNCA debe importar clases de ninguna otra capa |
| **R4** | `infrastructure/` implementa las interfaces definidas en `domain/` |
| **R5** | `api/` solo delega a handlers/queries de `application/` |
| **R6** | Las entidades JPA (`@Entity`) van en `infrastructure/persistence/entity`, **nunca** en `domain/model` |

### 2.2 Dónde Colocar el Código

| Tipo de Código | Capa | Paquete |
|---------------|------|---------|
| Controller REST | `api` | `sv.gob.mh.api.controller` |
| DTOs de request | `api` | `sv.gob.mh.api.dto.request` |
| DTOs de response | `api` | `sv.gob.mh.api.dto.response` |
| Commands (escritura) | `application` | `sv.gob.mh.application.command` |
| Handlers de commands | `application` | `sv.gob.mh.application.handler` |
| Queries (lectura) | `application` | `sv.gob.mh.application.query` |
| Modelos de dominio (POJO, sin JPA) | `domain` | `sv.gob.mh.domain.model` |
| Value Objects | `domain` | `sv.gob.mh.domain.model` |
| Interfaces de repositorios | `domain` | `sv.gob.mh.domain.repository` |
| Servicios de dominio | `domain` | `sv.gob.mh.domain.service` |
| **Entidades JPA (`@Entity`)** | `infrastructure` | `sv.gob.mh.infrastructure.persistence.entity` |
| Repositorios Spring Data y adaptadores | `infrastructure` | `sv.gob.mh.infrastructure.persistence.repository` |
| Configuraciones (OpenAPI, CORS, Security) | `infrastructure` | `sv.gob.mh.infrastructure.config` |
| REST Clients externos | `infrastructure` | `sv.gob.mh.infrastructure.external` |
| Kafka producers/consumers | `infrastructure` | `sv.gob.mh.infrastructure.messaging` |
| Excepciones | `shared` | `sv.gob.mh.shared.exception` |
| Mappers DTO ↔ Entity | `shared` | `sv.gob.mh.shared.mapper` |
| Enumeraciones | `shared` | `sv.gob.mh.shared.enum` |
| Constantes | `shared` | `sv.gob.mh.shared.constant` |
| Utilidades | `shared` | `sv.gob.mh.shared.util` |

### 2.3 Reglas de Nomenclatura

| Tipo | Patrón Obligatorio | Ejemplo |
|------|-------------------|---------|
| Entidad | Sustantivo singular | `Producto` |
| Command | `{Verbo}{Sustantivo}Command` | `CrearProductoCommand` |
| Handler | `{Verbo}{Sustantivo}Handler` | `CrearProductoHandler` |
| Query | `{Verbo}{Sustantivo}Query` | `ObtenerProductoQuery` |
| DTO Request | `{Sustantivo}RequestDTO` | `ProductoRequestDTO` |
| DTO Response | `{Sustantivo}ResponseDTO` | `ProductoResponseDTO` |
| Repository (interface) | `{Sustantivo}Repository` | `ProductoRepository` |
| Repository (impl) | `{Sustantivo}RepositoryImpl` | `ProductoRepositoryImpl` |
| Service (domain) | `{Sustantivo}Service` | `InventarioService` |
| Mapper | `{Sustantivo}Mapper` | `ProductoMapper` |
| Exception | `{Descriptor}Exception` | `BusinessException` |
| Configuration | `{Descriptor}Config` | `OpenApiConfig` |



## 3. Flujo para Implementar un Nuevo Caso de Uso

Cuando un agente reciba la solicitud de implementar una nueva funcionalidad, debe seguir este flujo ordenado:

### Paso 1: Crear/Actualizar Entidad de Dominio

```
Ubicación: src/main/java/sv/gob/mh/domain/model/
```

- Crear la entidad con **Factory Method** estático
- Incluir **reglas de negocio** como métodos de instancia
- Incluir **validaciones** como métodos privados estáticos
- No exponer setters públicos — usar métodos con nombre de negocio
- Implementar `equals()` y `hashCode()` basados en ID

### Paso 2: Crear Interface de Repositorio

```
Ubicación: src/main/java/sv/gob/mh/domain/repository/
```

- Solo definir la interface con los métodos necesarios
- La implementación va en `infrastructure/persistence/repository/`

### Paso 3: Crear Servicio de Dominio (si aplica)

```
Ubicación: src/main/java/sv/gob/mh/domain/service/
```

- Solo si la lógica involucra múltiples entidades
- Si es lógica de UNA sola entidad, va dentro de la entidad misma

### Paso 4: Crear Command + Handler

```
Ubicación: src/main/java/sv/gob/mh/application/command/
Ubicación: src/main/java/sv/gob/mh/application/handler/
```

- Command inmutable (campos `final`, solo getters)
- Handler con `@Service` y `@Transactional`
- Handler coordina: validación → dominio → persistencia

### Paso 5: Crear Query (si se necesita lectura)

```
Ubicación: src/main/java/sv/gob/mh/application/query/
```

- Solo lectura, nunca modifica estado
- Anotar con `@Service`

### Paso 6: Crear DTOs

```
Ubicación: src/main/java/sv/gob/mh/api/dto/request/
           src/main/java/sv/gob/mh/api/dto/response/
```

- RequestDTO con anotaciones Bean Validation (`@NotNull`, `@NotBlank`, `@Size`)
- ResponseDTO con anotaciones OpenAPI (`@Schema`)
- Request y Response SEPARADOS

### Paso 7: Crear Mapper

```
Ubicación: src/main/java/sv/gob/mh/shared/mapper/
```

- `@Component`
- Métodos: `toCommand(RequestDTO)`, `toResponseDTO(Entity)`, `toResponseDTOList(List<Entity>)`

### Paso 8: Crear Controller

```
Ubicación: src/main/java/sv/gob/mh/api/controller/
```

- Anotar con `@RestController`, `@RequestMapping("/api/v1/{recurso}")`, `@Tag`
- Cada endpoint anotado con `@Operation`
- Usar `@Valid` para validación de entrada
- Solo: recibir → mapear → delegar → mapear → responder

### Paso 9: Implementar Repositorio

```
Ubicación: src/main/java/sv/gob/mh/infrastructure/persistence/entity/
           src/main/java/sv/gob/mh/infrastructure/persistence/repository/
```

- Implementar la interface del dominio
- Usar Spring Data JPA: `JpaRepository<Entity, Long>`
- `@Repository`

### Paso 10: Crear Tests

```
Ubicaciones:
- src/test/java/sv/gob/mh/domain/     → Test de entidad
- src/test/java/sv/gob/mh/application/ → Test de handler
- src/test/java/sv/gob/mh/api/         → Test de controller (REST Assured / MockMvc)
```



## 4. Patrones Obligatorios

### 4.1 Manejo de Excepciones

```java
// Negocio → BusinessException
throw new BusinessException("CODIGO_ERROR", "Mensaje descriptivo");

// No encontrado → NotFoundException
throw new NotFoundException("Producto", id);

// Validación → ValidationException
throw new ValidationException("campo", "Descripción del error");

// Infraestructura → InfrastructureException
throw new InfrastructureException("CODIGO", "Mensaje", cause);
```

Los `@RestControllerAdvice` globales (en `shared/exception/`) convierten estas excepciones a respuestas HTTP automáticamente.

### 4.2 Respuestas HTTP

| Operación | Código | Ejemplo |
|-----------|--------|---------|
| Crear | `201 Created` | `ResponseEntity.status(HttpStatus.CREATED).body(dto)` |
| Consultar | `200 OK` | `ResponseEntity.ok(dto)` |
| Actualizar | `200 OK` | `ResponseEntity.ok(dto)` |
| Eliminar | `204 No Content` | `ResponseEntity.noContent().build()` |
| Error negocio | `422` | Automático vía `@RestControllerAdvice` |
| No encontrado | `404` | Automático vía NotFoundException |
| Validación | `400` | Automático vía Bean Validation |

### 4.3 Logging

```java
// Opción 1: SLF4J/Logback (estándar Spring Boot)
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

private static final Logger LOG = LoggerFactory.getLogger(MiClase.class);

// Opción 2: RemoteLogger (envía logs a servicio externo)
import sv.gob.mh.shared.exception.RemoteLogger;

private final RemoteLogger logger = RemoteLogger.getLogger(MiClase.class);

LOG.info("Mensaje informativo");
LOG.debug("Detalle: {}", variable);
LOG.error("Error al procesar: {}", id, exception);
```

**NUNCA** usar `System.out.println()`.

### 4.4 Inyección de Dependencias

```java
// Preferido: Inyección por constructor
@Service
public class MiHandler {
    private final ProductoRepository productoRepository;
    
    public MiHandler(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }
}

// Alternativa: Inyección por campo
@Service
public class MiHandler {
    @Autowired
    private ProductoRepository productoRepository;
}
```



## 5. Equivalencia Quarkus ↔ Spring Boot

| Concepto Quarkus | Equivalente Spring Boot |
|-----------------|------------------------|
| `@ApplicationScoped` | `@Service` / `@Component` |
| `@Inject` | `@Autowired` / Constructor injection |
| `@Path("/api/v1/...")` | `@RequestMapping("/api/v1/...")` |
| `@GET`, `@POST`, `@PUT`, `@DELETE` | `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` |
| `@Produces(APPLICATION_JSON)` | `produces = MediaType.APPLICATION_JSON_VALUE` |
| `Response.ok(dto).build()` | `ResponseEntity.ok(dto)` |
| `Response.status(201).entity(dto).build()` | `ResponseEntity.status(HttpStatus.CREATED).body(dto)` |
| `PanacheRepository<Entity>` | `JpaRepository<Entity, Long>` |
| `ExceptionMapper<T>` | `@RestControllerAdvice` + `@ExceptionHandler` |
| `application.properties` | `application.yml` |
| `@QuarkusTest` | `@SpringBootTest` |
| `@ConfigProperty` | `@Value` / `@ConfigurationProperties` |
| `quarkus:dev` | `spring-boot:run` |
| `/q/health` | `/actuator/health` |
| `/q/swagger-ui` | `/swagger-ui` |
| `/q/metrics` | `/actuator/metrics` |
| `org.jboss.logging.Logger` | `org.slf4j.Logger` / `RemoteLogger` |



## 6. Configuración del Proyecto

### Variables de Entorno Requeridas

| Variable | Descripción |
|----------|-------------|
| `SERVICE_NAME` | Nombre lógico del servicio |
| `AUTHZ_SERVICE_URL` | URL del servicio de autorización |
| `CONFIG_SERVICE_URL` | URL del servidor de configuración |
| `OIDC_ISSUER_URI` | URI del emisor OAuth2/OIDC (Keycloak) |

### Archivo de Configuración Principal

```
src/main/resources/application.yml
```

- Usar variables de entorno con `${VARIABLE:valor_default}`
- No hardcodear URLs o credenciales
- Perfiles: `spring.profiles.active=dev`, `test`, `prod`

### Ejecución Local

```bash
# Linux/Mac
./mvnw spring-boot:run

# Windows
.\mvnw.cmd spring-boot:run
```

### URLs de Desarrollo

| Recurso | URL |
|---------|-----|
| Aplicación | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| Health Check | http://localhost:8080/actuator/health |
| Metrics | http://localhost:8080/actuator/metrics |



## 7. Checklist para Agentes

Antes de considerar completa una tarea, el agente debe verificar:

### Al crear código nuevo

- [ ] El código está en la capa y paquete correctos según la tabla de la sección 2.2
- [ ] Se respetan las reglas de dependencia entre capas (sección 2.1)
- [ ] Se sigue la nomenclatura obligatoria (sección 2.3)
- [ ] Los Commands son inmutables (campos `final`)
- [ ] Las Entidades tienen Factory Methods y reglas de negocio (no modelo anémico)
- [ ] Los DTOs tienen anotaciones de validación y OpenAPI
- [ ] El Controller no tiene lógica de negocio — solo delega
- [ ] Se usan excepciones específicas (`BusinessException`, `NotFoundException`)
- [ ] Se usa SLF4J Logger o RemoteLogger, no `System.out`
- [ ] El endpoint está documentado con `@Operation`, `@Tag`

### Al modificar código existente

- [ ] No se rompen las reglas de dependencia entre capas
- [ ] Se mantiene la consistencia con los patrones existentes
- [ ] Se actualizan los tests si es necesario
- [ ] Se actualiza la documentación OpenAPI si cambian endpoints

### Al crear tests

- [ ] Test unitario para lógica de dominio (entidades, servicios)
- [ ] Test de handler para casos de uso
- [ ] Test de controller con MockMvc o REST Assured para endpoints
- [ ] Los tests siguen el patrón Arrange-Act-Assert



## 8. Errores Comunes a Evitar

| Error | Corrección |
|-------|-----------|
| Poner lógica de negocio en el Controller | Mover al Domain Service o Entidad |
| Modelo anémico (solo getters/setters) | Agregar métodos de negocio a la entidad |
| Importar clases de `infrastructure` en `domain` | Crear interface en `domain`, implementar en `infrastructure` |
| Hardcodear URLs de servicios | Usar variables de entorno en `application.yml` |
| Usar `System.out.println` | Usar `org.slf4j.Logger` o `RemoteLogger` |
| Retornar entidades directamente en endpoints | Usar DTOs separados (Request y Response) |
| Command con setters | Hacer campos `final`, solo getters |
| Test sin assertions | Añadir assertions significativas |
| No anotar endpoints con OpenAPI | Agregar `@Operation`, `@Schema`, `@Tag` |
| Mezclar excepciones genéricas | Usar `BusinessException`, `NotFoundException`, etc. |



## 9. Comandos Rápidos de Referencia

```bash
# Desarrollo
./mvnw spring-boot:run                # Iniciar en modo desarrollo
./mvnw test                            # Ejecutar tests unitarios
./mvnw clean verify jacoco:report      # Tests + coverage

# Compilación
./mvnw clean package                   # Compilar y empaquetar
./mvnw clean package -DskipTests       # Empaquetar sin tests

# Docker
docker build -f Dockerfile -t mi-servicio:latest .
docker run -i --rm -p 8080:8080 mi-servicio:latest

# Integración
mvn test -Dtest=IntegrationTestRunner -Dkarate.env=dev
mvn test -Dtest=SmokeTestRunner -Dkarate.env=dev

# SonarQube
./mvnw sonar:sonar -Dsonar.token=TOKEN
```



**Ministerio de Hacienda — El Salvador**



## Documentación Complementaria

Para información detallada sobre temas específicos, consultar:

| Tema | Documento |
|------|-----------|
| Seguridad OIDC/JWT y Autorización | [security.md](security.md) |
| Framework DINAFI (librería compartida) | [framework-library.md](framework-library.md) |
| Pipeline CI/CD (Tekton + ArgoCD) | [ci-cd-pipeline.md](ci-cd-pipeline.md) |
| Helm Chart y Configuración CD | [helm-configuration.md](helm-configuration.md) |
| Migración de Base de Datos | [database-migration.md](database-migration.md) |
| Auditoría y Logging Remoto | [audit-logging.md](audit-logging.md) |
| Contrato de Respuesta de Errores | [error-contract.md](error-contract.md) |
| Certificados TLS | [tls-certificates.md](tls-certificates.md) |
| Versionamiento de API y Paginación | [api-versioning-pagination.md](api-versioning-pagination.md) |
| Compilación Nativa (GraalVM) | [native-compilation.md](native-compilation.md) |
| Programación Asíncrona | [async-programming.md](async-programming.md) |
