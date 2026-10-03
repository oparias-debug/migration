# Arquitectura

`backend-srv` no sigue la organización por capas técnicas (`api/application/domain/
infrastructure/shared`) que trae la plantilla Spring Boot del Developer Hub — en su lugar
organiza el código **por dominio de negocio**, porque cubre siete dominios
distintos y esa estructura evita que cada capa técnica termine con
subcarpetas por dominio de todos modos:

```
sv.gob.mh.siip
├── SiipApplication.java        # @SpringBootApplication, @EnableJpaAuditing, @EnableScheduling
├── config/                      # Beans, OpenAPI (springdoc), manejo global de errores, seed de datos dev
├── controller/                  # Controladores REST (uno por caso de uso / recurso)
├── exception/                   # Excepciones de dominio
├── security/                    # SecurityConfig (valida el JWT), ActorContexto, AuditorAwareImpl
└── model/
    ├── administracion/          # domain, enums, mapper, repository, service
    ├── common/                  # domain, enums, repository (p.ej. Auditable)
    ├── convenios/
    ├── ejecucion/
    ├── oym/
    ├── preinversion/
    └── programacion/
        └── {domain,dto*,enums,mapper,repository,service,sql}
```

`*` — `dto/` y las clases `api` quedan vacías en el repositorio: las genera
`openapi-generator-maven-plugin` en `target/generated-sources/openapi` a
partir de los contratos OpenAPI en `src/main/resources/openapi/<dominio>/`
(uno o más por caso de uso). Ver
[Desarrollo § Contratos OpenAPI](desarrollo.md#contratos-openapi-api-first)
para el detalle de esa generación.

## Mapeo frente a las capas de la plantilla

Para orientarse viniendo de la plantilla Spring Boot, esta es la
correspondencia conceptual — **no hay una reestructuración física** de
paquetes, por la razón explicada al inicio de esta página:

| Capa de la plantilla | Equivalente real en `backend-srv` |
|---|---|
| `layer-api` | `controller/` + clases `api`/`dto` generadas por dominio (`model/<dominio>/`) |
| `layer-application` | `model/<dominio>/service/` |
| `layer-domain` | `model/<dominio>/domain/`, `model/<dominio>/enums/` |
| `layer-infrastructure` | `model/<dominio>/repository/`, `config/` |
| `layer-shared` | `exception/`, `model/common/`, `security/` |

## Ciclo de vida del proyecto

No hay motor de procesos: el ciclo de vida se modela con los estados de las entidades. Ver
[Desarrollo § Ciclo de vida del proyecto](desarrollo.md#ciclo-de-vida-del-proyecto).

## Auditoría

`backend-srv` no usa el pilar de auditoría remota de la plantilla (que envía
eventos a un microservicio externo de Audit). En su lugar tiene su propia
auditoría local: `AuditoriaAspect` (AOP) + entidad/repositorio
`LogAuditoria` + `AuditorAwareImpl` (alimenta `@EnableJpaAuditing`),
persistida en la misma base de datos de negocio.

## Pruebas

Suite BDD con Cucumber (`src/test/resources/features/{adm,pre}/*.feature`,
134 features) más pruebas unitarias de servicios y controladores. Detalle
completo en [Desarrollo § Pruebas](desarrollo.md#pruebas).
