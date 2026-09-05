# Referencia técnica

Cómo funciona internamente la generación de código desde OpenAPI (back y front), la estructura del front, y cómo están organizadas las pruebas. Para levantar el stack, ver [SETUP.md](./SETUP.md). Para los pasos de "cómo agregar un CU", ver [CONTRIBUTING.md](./CONTRIBUTING.md).

## Índice

- [Contratos OpenAPI (back) — generación de código](#contratos-openapi-back--cómo-funciona-la-generación-de-código)
- [Estructura del front](#estructura-del-front)
- [Contratos OpenAPI (front) — generación del cliente](#contratos-openapi-front--cómo-funciona-la-generación-del-cliente)
- [Pruebas y calidad](#pruebas-y-calidad)
  - [Cobertura de pruebas](#cobertura-de-pruebas)
  - [Análisis estático (SonarQube)](#análisis-estático-sonarqube)
  - [Pruebas BDD (Gherkin/Cucumber)](#pruebas-bdd-gherkincucumber--solo-en-back)

## Contratos OpenAPI (back) — cómo funciona la generación de código

`back` usa **contrato primero** (API-first) para los servicios REST: se escribe un `.yaml` OpenAPI describiendo el/los endpoint(s) de un caso de uso, y `openapi-generator-maven-plugin` (configurado en `back/pom.xml`) genera la interfaz Java (con `@RequestMapping`, validación, docs Swagger) y los DTOs correspondientes. Vos solo escribís un `@RestController` que implementa esa interfaz — nunca se edita el código generado a mano.

**Estructura actual** (un `.yaml` por caso de uso, agrupado en una carpeta por dominio de negocio — coincide con `back/src/main/java/sv/gob/mh/siip/model/<dominio>/` y su `TRAZABILIDAD-<DOMINIO>.md`):

```
back/src/main/resources/openapi/
└── preinversion/
    └── CU-PRE-01.openapi.yaml   # Registro y Solicitud de CUP (CU-PRE-01)
```

Hoy solo existe el contrato de `preinversion` (CU-PRE-01); el resto de dominios (`administracion`, `convenios`, `ejecucion`, `oym`, `programacion`) todavía no tienen `.yaml` ni `<execution>` en el plugin.

**Generar el código:**

```
mvn generate-sources -pl back
```

(también se dispara solo con `mvn compile`/`package`/`install`, ya que está enlazado a la fase `generate-sources`). Genera, por dominio, algo como:

```
back/target/generated-sources/openapi/src/main/java/sv/gob/mh/siip/<dominio>/
├── api/<Tag>Api.java          # interfaz, una por tag del yaml — NO editar, se regenera en cada build
└── dto/<Modelo>Dto.java       # DTOs de request/response — NO editar
```

Esa carpeta es 100% generada (vive bajo `target/`, ignorada por git) — se borra y se reconstruye en cada `mvn clean`.

**Configuración clave** (en `back/pom.xml`, aplicada a todas las ejecuciones): `interfaceOnly=true` (no genera controllers ni clase `Application`), `skipDefaultInterface=true` (fuerza a implementar cada método), `useSpringBoot3=true` (imports `jakarta.*`, no `javax.*`). El Swagger UI existente (`springdoc-openapi-starter-webmvc-ui`) sigue funcionando igual: documenta en runtime cualquier `@RestController` activo, generado o no.

> Los pasos concretos para **agregar** un endpoint o un dominio nuevo están en [CONTRIBUTING.md](./CONTRIBUTING.md).

## Estructura del front

```
front/
├── openapi/
│   └── preinversion/CU-PRE-01.openapi.yaml   # copia del contrato del back, ver abajo
├── features/                              # .feature (Gherkin) espejo de back/src/test/resources/features
├── src/
│   ├── api/
│   │   ├── generated/    # cliente typescript-axios generado — NO editar, no se versiona (ver .gitignore)
│   │   ├── httpClient.ts # axios base (refresh-on-401)
│   │   └── <dominio>Api.ts  # wrapper por dominio: instancia el cliente generado + reexporta tipos
│   ├── auth/        # AuthContext, guard de rutas (RequireAuth)
│   ├── layout/       # AppLayout, Sidebar, Topbar
│   ├── pages/         # LoginPage, HomePage, placeholders (ver nota abajo)
│   ├── features/       # pantallas/lógica por dominio de negocio (ej. features/preinversion/proyectos/)
│   ├── components/     # form/ (FormRow, DatePickerInput), table/ (DataTable, Pagination), ConfirmDialog
│   └── i18n/            # traducciones (es)
├── Dockerfile              # build Node -> imagen Nginx
└── nginx.conf.template      # reverse-proxy /auth, /back + SPA fallback
```

> **Estado actual del front:** login, home y el módulo `preinversion` (CU-PRE-01, "Registro y Solicitud de CUP" — ver `src/features/preinversion/proyectos/`) están implementados contra el back real. El resto de módulos del sidebar (Catálogos Generales, Tablas de Rangos, y los dominios `administracion`/`convenios`/`ejecucion`/`oym`/`programacion`) siguen como placeholders ("🚧 Página en Construcción"): tuvieron una implementación CRUD completa en un primer momento, pero se retiraron cuando `back` reemplazó ese layer de controllers/servicios por el nuevo modelo de dominio, que todavía no expone endpoints REST para esos módulos. Los componentes genéricos (`FormRow`, `DatePickerInput`, `DataTable`, `Pagination`, `ConfirmDialog`) se conservaron porque no dependen de esos DTOs — son la base para conectar cada módulo nuevo en cuanto `back` publique su contrato, siguiendo el patrón descrito en [CONTRIBUTING.md](./CONTRIBUTING.md).

## Contratos OpenAPI (front) — cómo funciona la generación del cliente

Igual que `back`, `front` genera su cliente HTTP a partir de un `.yaml` OpenAPI — pero acá el `.yaml` es una **copia manual** del que vive en `back/src/main/resources/openapi/` (no hay symlink ni script de sync: al editar el contrato hay que copiarlo a los dos lados y mantenerlos idénticos).

```
front/openapi/preinversion/CU-PRE-01.openapi.yaml   # idéntico a back/src/main/resources/openapi/preinversion/CU-PRE-01.openapi.yaml
```

**Generar el cliente:**

```
cd front
npm run generate:api
```

Esto corre `openapi-generator-cli generate -i openapi/preinversion/CU-PRE-01.openapi.yaml -g typescript-axios -o src/api/generated/preinversion ...` (ver `package.json`) y regenera `src/api/generated/preinversion/` (API clients + tipos, uno por `tag` del yaml). Esa carpeta **no se versiona** (`front/.gitignore`) — se regenera localmente cada vez que cambia el contrato, igual que `back/target/generated-sources`.

`npm run build` corre `generate:api` solo antes de compilar (hook `prebuild` en `package.json`), así que no hace falta acordarse de correrlo a mano antes de un build — pasa igual en local que dentro del `Dockerfile` (que por eso instala un JRE en el stage de build: `openapi-generator-cli` corre sobre la JVM). Sí sigue haciendo falta correrlo a mano durante desarrollo si querés que el editor/TypeScript vean los tipos nuevos antes de buildear (por ejemplo, apenas cambiás el `.yaml`).

> Los pasos para agregar un script `generate:api:<dominio>` nuevo y el wrapper correspondiente están en [CONTRIBUTING.md](./CONTRIBUTING.md).

## Pruebas y calidad

- Los tests unitarios se ubican por módulo en `src/test/java` (JUnit 5 + Mockito).
- Cobertura con JaCoCo configurado en el `pom.xml` raíz (`prepare-agent` + `report` en fase `verify`).
- Análisis estático con SonarQube, corriendo como servicio `sonarqube` en `docker-compose.yml` (junto a su propia base `sonarqube-db`, separada de `siip-db`), autenticado con `SONAR_TOKEN` (variable de entorno).

### Cobertura de pruebas

**Backend (`back`, `api-gateway`) — JaCoCo:**

```
mvn clean verify
```

Genera un reporte HTML por módulo en `<módulo>/target/site/jacoco/index.html` (por ejemplo `back/target/site/jacoco/index.html`) — abrirlo directamente en el navegador. `mvn clean package`/`install` también ejecuta los tests, pero el reporte HTML solo se genera en la fase `verify` (donde está enlazado el goal `report` de JaCoCo).

**Frontend (`front`) — Vitest + `@vitest/coverage-v8`:**

```
cd front
npm run test           # corre los tests una vez
npm run test:watch      # modo watch, para desarrollo
npm run test:coverage    # corre los tests y genera el reporte de cobertura
```

`test:coverage` imprime la tabla de cobertura en la terminal y además genera un reporte HTML en `front/coverage/index.html`. Los tests viven junto al código que prueban (`*.test.ts`/`*.test.tsx`, p. ej. `src/auth/tokenStore.test.ts`), usando Vitest + React Testing Library (`jsdom` como entorno DOM). Hoy la cobertura es baja porque recién se sentó la base de testing al migrar el front a React — hay que ir sumando tests a medida que se toca cada módulo.

### Análisis estático (SonarQube)

El servidor corre local vía Docker Compose (servicio `sonarqube`, imagen `sonarqube:community`, con su propia Postgres `sonarqube-db` — separada de `siip-db` porque Sonar necesita su propio usuario/esquema y no debe compartir ciclo de vida con la base de negocio):

```
docker compose up -d sonarqube
```

La primera vez tarda un par de minutos en arrancar (Elasticsearch embebido). Si el contenedor muere con `max virtual memory areas vm.max_map_count [...] is too low`, hay que subir ese límite en el host/VM de Docker (en Docker Desktop con WSL2: `wsl -d docker-desktop sysctl -w vm.max_map_count=262144`, o agregarlo a `.wslconfig` para que sobreviva un reinicio).

Una vez arriba, entrar a http://localhost:9000 (usuario/clave por defecto `admin`/`admin`, pide cambiarla al primer login), crear un token de usuario (**My Account → Security**, tipo *User Token*) y ponerlo en `SONAR_TOKEN` en el `.env` de la raíz (reemplaza cualquier valor previo — un token de otro servidor/organización no sirve acá).

> **Sobre la versión y la autenticación**: `sonarqube:community` (Community Build, release rolling) acepta autenticación Bearer con `sonar.token`/`SONAR_TOKEN` sin workarounds — los comandos de abajo ya lo usan así. Ojo con el scanner de `front` (`@sonar/scan`): si no encuentra `sonar.host.url` apunta por defecto a **SonarCloud** y falla con `403` — por eso `front/sonar-project.properties` lo fija explícitamente a `http://localhost:9000`; si alguna vez corrés esto contra otro servidor, hay que cambiarlo ahí (no alcanza con `SONAR_HOST_URL`, ese env var no lo lee ninguno de los dos scanners).
>
> El análisis, sobre todo la primera vez (JVM en frío, sin caché de análisis, `@sonar/scan` sin el scanner-cli descargado), puede tardar varios minutos reales — no está colgado, `mvn`/`npx` simplemente no imprimen nada mientras el analizador Java/TS procesa los archivos. Dejalo correr en background si vas a hacer otra cosa mientras tanto.

**Backend (`back` + `api-gateway`) — un solo proyecto Sonar (`siip-back`) para todo el reactor Maven, corrido desde la raíz:**

```
$env:SONAR_TOKEN = "$((Get-Content .env | Select-String '^SONAR_TOKEN=').ToString().Split('=')[1])"
mvn clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:5.1.0.4751:sonar 
```

(`$SONAR_TOKEN` en bash/mac/Linux). `sonar.projectKey`/`sonar.projectName`/`sonar.host.url` ya están en el `pom.xml` raíz — no hace falta repetirlos. El plugin no está declarado como dependencia fija del build (convención recomendada por Sonar: se invoca por coordenadas completas) así que `clean verify` corre antes para que JaCoCo genere `target/site/jacoco/jacoco.xml` por módulo, que el scanner detecta solo. El `argLine` de Surefire en el `pom.xml` raíz también lleva `-XX:+EnableDynamicAgentLoading -Djdk.attach.allowAttachSelf=true`: sin eso, en JDK 21+ (JEP 451) el "inline mock maker" de Mockito falla al auto-adjuntarse y los tests con `@Mock`/`MockitoExtension` truenan.

**Frontend (`front`) — proyecto Sonar separado (`siip-front`, TypeScript/JS no es parte del reactor Maven):**

```
cd front
$env:SONAR_TOKEN = "$((Get-Content ..\.env | Select-String '^SONAR_TOKEN=').ToString().Split('=')[1])"  # o export SONAR_TOKEN=... en bash
npm run sonar
```

`npm run sonar` corre `test:coverage` (genera `coverage/lcov.info`, que lee `sonar.javascript.lcov.reportPaths` en `front/sonar-project.properties`) y después `@sonar/scan` (scanner oficial de Sonar, se descarga on-demand vía `npx`, no queda como dependencia instalada), que lee `SONAR_TOKEN`/`sonar.host.url` solo. Excluye `src/api/generated/**` del análisis y de cobertura — es código generado por `openapi-generator-cli`, no se versiona y no tiene sentido auditarlo (mismo criterio que las exclusiones de JaCoCo en el `pom.xml` de `back` para `api/`/`dto/` generados).

### Pruebas BDD (Gherkin/Cucumber) — solo en `back`

`back` tiene Cucumber (`io.cucumber:cucumber-java`, `cucumber-spring`, `cucumber-junit-platform-engine`) integrado sobre JUnit 5 Platform, corriendo junto a los tests normales con `mvn test`. Estructura:

```
back/src/test/resources/features/      # archivos .feature (Gherkin)
back/src/test/java/sv/gob/mh/siip/bdd/
├── RunCucumberTest.java                # runner @Suite — no se toca al agregar features
├── CucumberSpringConfiguration.java     # @SpringBootTest + @AutoConfigureTestDatabase (H2) + @Transactional
└── steps/                              # step definitions (@Given/@When/@Then), un archivo por feature/dominio
```

**Ejecutar solo la suite BDD:**

```
mvn test -pl back -Dtest=RunCucumberTest
```

`front/features/` tiene una **copia idéntica** de cada `.feature` de `back/src/test/resources/features/` (mismo Gherkin, mismos tags). `front` trae `@cucumber/cucumber` como dependencia y un script `npm run test:bdd` (`cucumber-js`), pero hoy **no existen step definitions del lado front** — no hay carpeta `step_definitions`. En la práctica, estos `.feature` en `front` funcionan como la especificación funcional en español que la UI debe cumplir (base para tests manuales o para los `*.test.tsx` de Vitest), no como una suite automatizada activa.

> Las reglas para escribir un `.feature` nuevo (una sola `Feature:` por archivo, `@wip`, idioma, etc.) y el flujo para implementarlo paso a paso están en [CONTRIBUTING.md](./CONTRIBUTING.md).