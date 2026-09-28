# Referencia técnica

La referencia técnica de cada componente vive **dentro de su propio repositorio**, en `docs/`
(se publica como TechDocs en el Developer Hub). Así se mantiene al día junto al código y no
depende del monorepo. Esta página solo reúne lo que cruza componentes: la duplicación de
contratos y `.feature` entre back y front, `api-gateway` (que no tiene `docs/` propio) y el
servidor de SonarQube local.

Para levantar el stack, ver [SETUP.md](./SETUP.md). Para los pasos de "cómo agregar un CU", ver
[CONTRIBUTING.md](./CONTRIBUTING.md).

## Dónde está cada tema

| Tema | `backend-srv` | `front` |
|---|---|---|
| Estructura del código | [docs/architecture.md](./backend-srv/docs/architecture.md) | [docs/architecture.md](./front/docs/architecture.md) |
| Contratos OpenAPI y código generado | [docs/desarrollo.md § Contratos](./backend-srv/docs/desarrollo.md#contratos-openapi-api-first) | [docs/desarrollo.md § Cliente generado](./front/docs/desarrollo.md#cliente-generado-desde-openapi) |
| Pruebas y cobertura | [docs/desarrollo.md § Pruebas](./backend-srv/docs/desarrollo.md#pruebas) | [docs/desarrollo.md § Pruebas](./front/docs/desarrollo.md#pruebas) |
| `.feature` (Gherkin) | [§ BDD (Cucumber)](./backend-srv/docs/desarrollo.md#bdd-cucumber): suite automatizada | [§ Especificaciones Gherkin](./front/docs/desarrollo.md#especificaciones-gherkin-features): copias sin steps |
| Motor de procesos (Flowable) | [docs/desarrollo.md § Flowable](./backend-srv/docs/desarrollo.md#motor-de-procesos-flowable) | — |
| Análisis estático | [docs/desarrollo.md § SonarQube](./backend-srv/docs/desarrollo.md#análisis-estático-sonarqube) | [docs/desarrollo.md § SonarQube](./front/docs/desarrollo.md#análisis-estático-sonarqube) |
| Variables de entorno y perfiles | [docs/configuracion.md](./backend-srv/docs/configuracion.md) | [README § Contenedor](./front/README.md#contenedor) |

## Contratos y `.feature` duplicados entre back y front

Cada CU tiene un `.openapi.yaml` y uno o más `.feature`. El original vive en `backend-srv` y
`front` guarda una **copia manual**: no hay symlink ni script de sincronización, porque en la
entidad son repositorios separados.

| Archivo | Original (`backend-srv`) | Copia (`front`) |
|---|---|---|
| Contrato OpenAPI | `src/main/resources/openapi/<dominio>/CU-XX.openapi.yaml` | `openapi/<dominio>/CU-XX.openapi.yaml` |
| Especificación Gherkin | `src/test/resources/features/<dominio>/*.feature` | `features/*.feature` (solo los CU que el front implementa) |

Al cambiar un contrato en el back hay que copiarlo al front, correr `npm run generate:api` y
commitear lo generado (el cliente de `front/src/api/generated/` se versiona). Nada lo valida
automáticamente: revisar que las dos copias sean idénticas es parte de la Definition of Done de
[CONTRIBUTING.md](./CONTRIBUTING.md).

## `api-gateway`: pruebas y cobertura

`api-gateway` no tiene `docs/`. Se prueba igual que `backend-srv`, dentro de su carpeta (no hay
`pom.xml` agregador en la raíz):

```
cd api-gateway
mvn clean verify    # JUnit 5 + Mockito; reporte JaCoCo en target/site/jacoco/index.html
```

`.\scripts\run-tests.ps1` corre las pruebas de `backend-srv`, `api-gateway` y `front` seguidas.

Su proyecto Sonar es `siip-api-gateway`, y su `pom.xml` ya apunta a `http://localhost:9000`.

## SonarQube local

El servidor corre con Docker Compose (servicio `sonarqube`, imagen `sonarqube:community`), con
su propia Postgres `sonarqube-db`. Está separada de `siip-db` porque Sonar necesita su propio
usuario y esquema, y no debe compartir ciclo de vida con la base de negocio.

```
docker compose up -d sonarqube
```

La primera vez tarda un par de minutos en arrancar (Elasticsearch embebido). Si el contenedor
muere con `max virtual memory areas vm.max_map_count [...] is too low`, hay que subir ese límite
en la VM de Docker (Docker Desktop con WSL2: `wsl -d docker-desktop sysctl -w
vm.max_map_count=262144`, o agregarlo a `.wslconfig` para que sobreviva un reinicio).

Una vez arriba, entrar a http://localhost:9000 (`admin`/`admin`, pide cambiarla al primer
login), crear un token de usuario (**My Account → Security**, tipo *User Token*) y ponerlo en
`SONAR_TOKEN` en el `.env` de la raíz. Un token de otro servidor no sirve.

### Mismas reglas que la entidad

El Sonar de la entidad usa un perfil Java más estricto que *Sonar way* (líneas ≤ 120, sin
imports con `*`, `package-info.java` por paquete, etc.) y un quality gate con cobertura ≥ 95 %.
Como no hay backup de ese perfil, las reglas están reconstruidas a partir de sus incidencias en
`sonar/reglas-entidad.json`. El servicio `sonarqube-init` las aplica al Sonar local (perfil y
quality gate "Entidad MH", ambos por defecto) y verifica regla por regla que quedaron activas:

1. Poner en el `.env` la clave del usuario `admin` del Sonar local (`SONAR_ADMIN_PASSWORD=...`)
   o un token de ese usuario (`SONAR_ADMIN_TOKEN=...`). El `SONAR_TOKEN` de un usuario común no
   puede administrar perfiles.
2. `docker compose up sonarqube-init`. Termina con "aplicados y verificados", o con la lista de
   lo que no coincide.
3. Cuando la entidad reporte una regla nueva, agregar su clave (`java:Sxxx`, visible en el
   detalle de la incidencia) a `sonar/reglas-entidad.json` y volver a correr el paso 2.

### Analizar cada proyecto contra el Sonar local

`backend-srv` y `front` apuntan por defecto al servidor institucional (`alcm.mh.gob.sv`), así que
hay que definir `SONAR_HOST_URL`. En `api-gateway` es redundante.

```powershell
$env:SONAR_HOST_URL = "http://localhost:9000"
$env:SONAR_TOKEN = "$((Get-Content .env | Select-String '^SONAR_TOKEN=').ToString().Split('=')[1])"

cd backend-srv    # o cd api-gateway
mvn clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:5.1.0.4751:sonar

cd front
npm run sonar
```

| Proyecto | Clave Sonar | Detalle |
|---|---|---|
| `backend-srv` | `dgicp-siip2-backend-srv` | [docs/desarrollo.md § SonarQube](./backend-srv/docs/desarrollo.md#análisis-estático-sonarqube) |
| `api-gateway` | `siip-api-gateway` | [arriba](#api-gateway-pruebas-y-cobertura) |
| `front` | `dgicp-siip2-frontend-ui` | [docs/desarrollo.md § SonarQube](./front/docs/desarrollo.md#análisis-estático-sonarqube) |

El análisis, sobre todo el primero (JVM en frío, sin caché, scanner por descargar), puede tardar
varios minutos sin imprimir nada. No está colgado.
