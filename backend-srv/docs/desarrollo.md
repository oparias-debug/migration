# Desarrollo

Cómo se trabaja en el código de `backend-srv`: contratos OpenAPI, pruebas, ciclo de vida del proyecto y
análisis estático. Para las variables de entorno y los perfiles, ver
[Configuración y ambientes](configuracion.md).

## Contratos OpenAPI (API first)

Los servicios REST se escriben **contrato primero**: un `.yaml` OpenAPI por caso de uso
describe los endpoints, y `openapi-generator-maven-plugin` genera a partir de él la interfaz
Java (con `@RequestMapping`, validación y documentación Swagger) y los DTOs. El código propio
es solo un `@RestController` que implementa esa interfaz. El código generado nunca se edita a
mano.

```
src/main/resources/openapi/
├── administracion/        # CU-ADM-02 y CU-ADM-03 (CU-ADM-01 y CU-ADM-04 viven en admin-srv)
└── preinversion/          # CU-PRE-01 ... CU-PRE-33, un .yaml por CU
```

El código se genera en la fase `generate-sources`, así que corre solo con `compile`,
`test` o `package`:

```
target/generated-sources/openapi/src/main/java/sv/gob/mh/siip/model/<dominio>/
├── api/<Tag>Api.java      # una interfaz por tag del yaml
└── dto/<Modelo>Dto.java   # DTOs de request/response
```

Esa carpeta vive en `target/` (no se versiona) y se reconstruye con cada `mvn clean`.

Configuración común a todas las ejecuciones (`pom.xml`):

| Opción | Efecto |
|---|---|
| `interfaceOnly=true` | No genera controllers ni clase `Application`. |
| `skipDefaultInterface=true` | Obliga a implementar cada método (si falta uno, no compila). |
| `useSpringBoot3=true` | Imports `jakarta.*`. |
| `useTags=true` | Una interfaz por `tag` del yaml. |
| `modelNameSuffix=Dto` | Los modelos se llaman `<Nombre>Dto`. |
| `cleanupOutput=false` | Varias ejecuciones escriben en la misma salida y conservan lo de las anteriores. |

### Agregar un contrato

1. Crear `src/main/resources/openapi/<dominio>/CU-XX.openapi.yaml` con sus `paths`,
   `operationId`, `tags` (el tag da el nombre de la interfaz) y `components/schemas`.
2. Agregar una `<execution>` al `openapi-generator-maven-plugin` del `pom.xml`. El plugin
   genera **un archivo por ejecución**, no una carpeta entera: todo `.yaml` nuevo necesita la
   suya. Copiar una existente y cambiar `id`, `inputSpec`, `apiPackage` y `modelPackage`.
3. `mvn clean compile` y crear el `@RestController` que implementa la interfaz generada,
   delegando en un `Service` de `model/<dominio>/service/`.

Si se mueve un `tag` o un schema de un `.yaml` a otro, cambia el paquete del código generado:
hay que actualizar los imports de controllers, servicios y mappers, y diagnosticar siempre
con `mvn clean compile` (con `cleanupOutput=false` las clases viejas siguen en `target/` hasta
el `clean`).

Cada dominio documenta en `model/<dominio>/TRAZABILIDAD-<DOMINIO>.md` qué entidades JPA
corresponden a cada CU y de dónde salen.

## Pruebas

```
./mvnw clean test                       # unitarias + BDD
./mvnw clean test -Dtest=RunCucumberTest   # solo BDD
./mvnw clean verify                     # + reporte de cobertura JaCoCo (target/site/jacoco/index.html)
```

Usar siempre `clean`: sin él, MapStruct a veces deja un mapper generado corrupto en
`target/generated-sources/annotations` y la compilación falla con errores de sintaxis que no
existen en el código.

Las pruebas corren con el perfil `test` (`src/test/resources/application-test.yml`): H2 en
memoria, esquema recreado en cada ejecución. No hace falta base de datos.

### Unitarias

JUnit 5 + Mockito en `src/test/java`, junto al paquete que prueban. El `argLine` de Surefire
lleva `-XX:+EnableDynamicAgentLoading -Djdk.attach.allowAttachSelf=true`: sin eso, en JDK 21
el *inline mock maker* de Mockito no puede adjuntarse y fallan las pruebas con `@Mock`.

### BDD (Cucumber)

Los `.feature` (Gherkin en español) son la especificación funcional de cada CU:

```
src/test/resources/features/
└── pre/      # CU-PRE-* (los de CU-ADM-01 y CU-ADM-04 están en admin-srv)
src/test/java/sv/gob/mh/siip/bdd/
├── RunCucumberTest.java            # runner; no se toca al agregar features
├── CucumberSpringConfiguration.java # @SpringBootTest sobre H2
└── steps/<dominio>/                 # step definitions
```

- Un solo `Característica:` por archivo, con el código del CU como prefijo del nombre
  (`CU-PRE-01-solicitar-cup.feature`).
- Un paso sin implementar hace fallar el build. Para escribir el `.feature` antes de
  implementarlo, etiquetar la característica o el escenario con `@wip`: el runner los excluye
  (`FILTER_TAGS_PROPERTY_NAME = "not @wip"` en `RunCucumberTest`; Cucumber no lee
  `cucumber.properties`).
- Para obtener el esqueleto de un paso nuevo, quitar `@wip` y correr la suite: Cucumber
  imprime el *snippet* Java en consola.
- Si aparecen pasos "undefined" en escenarios que antes pasaban, buscar primero un paso
  duplicado o ambiguo en `steps/`: un solo conflicto hace que Cucumber deje de registrar el
  resto de los pasos.

## Ciclo de vida del proyecto

No hay motor de procesos. El avance del proyecto (registro → CUP → formulación → viabilidad →
elegibilidad → opinión técnica → cierre) se guarda en `Proyecto.estado` y
`SolicitudPreinversion.estado`. Cada servicio valida desde qué estados admite su operación y
responde 409 (`ConflictoEstadoException`) si el proyecto no está en uno de ellos. Las bandejas
leen esos mismos estados.

El diagrama BPMN del proceso (`Proceso_SIIF.bpmn20.xml`) se conserva como referencia de diseño en
la documentación de casos de uso del proyecto, no en este repositorio. Hasta el 2026-10-02
`backend-srv` embebía Flowable, pero solo para el tramo de registro y solicitud de CUP, y nada
leía sus tareas. Se quitó porque la base de la entidad no admite su esquema propio.

## Auditoría

`backend-srv` no usa el pilar de auditoría remota del marco. Tiene auditoría local:
`AuditoriaAspect` (AOP) registra en la entidad `LogAuditoria`, y `AuditorAwareImpl` alimenta
`@EnableJpaAuditing` con el usuario del JWT (ver `ActorContexto`). `AuditoriaAspect` registra los
headers de cada petición con `Authorization` y `Cookie` enmascarados.

En las pruebas, el usuario se simula con `AutenticacionDePrueba`: `.with(AutenticacionDePrueba.como(usuario))`
en MockMvc, o `AutenticacionDePrueba.autenticar(usuario)` en los steps que llaman a los servicios
directamente (un hook de Cucumber limpia el contexto al terminar cada escenario).

## Análisis estático (SonarQube)

`pom.xml` fija `sonar.projectKey=dgicp-siip2-backend-srv` y el servidor institucional
(`alcm.mh.gob.sv`). El perfil `sonar-host-desde-env` lo cambia si existe la variable
`SONAR_HOST_URL`, para analizar contra otro servidor:

```
export SONAR_HOST_URL=<url>
export SONAR_TOKEN=<token>
./mvnw clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:5.1.0.4751:sonar
```

El perfil de reglas de la entidad es más estricto que *Sonar way*: indentación de 4 espacios
(`.editorconfig`), líneas de 120 caracteres como máximo, sin imports con `*`, `package-info.java`
en cada paquete, y cobertura ≥ 95 % en el quality gate. Las clases generadas (`api/`, `dto/`)
están excluidas del análisis y de la cobertura (`sonar-project.properties`).
