# Puesta en marcha

Cómo levantar el stack completo o un módulo puntual. Para entender qué es cada pieza, ver [README.md](./README.md); para la mecánica de generación de código y testing, ver [REFERENCE.md](./REFERENCE.md).

## Índice

- [Primer día: levantar todo en 10 minutos](#primer-día-levantar-todo-en-10-minutos)
- [Requisitos](#requisitos)
- [Variables de entorno](#variables-de-entorno)
- [Base de datos: Postgres en local, Oracle en producción](#base-de-datos-postgres-en-local-oracle-en-producción)
- [Configuración por ambiente en la entidad (backend-srv-config)](#configuración-por-ambiente-en-la-entidad-backend-srv-config)
- [Compilación y despliegue](#compilación-y-despliegue)
- [Herramienta externa: Flowable UI (opcional)](#herramienta-externa-flowable-ui-opcional)
- [Accesos una vez levantado el stack](#accesos-una-vez-levantado-el-stack)

## Primer día: levantar todo en 10 minutos

Versión corta para tener el sistema andando y confirmar que todo quedó bien conectado. El detalle de cada paso está más abajo.

1. Instalá los [requisitos](#requisitos) (Java 21, Docker y Docker Compose alcanzan para esta parte — Node solo hace falta si vas a tocar `front` fuera de Docker).
2. Copiá el bloque de [variables de entorno](#variables-de-entorno) a un archivo `.env` en la raíz del proyecto y completá los valores vacíos (`DB_USER`, `DB_PASSWORD`, `DB_DATABASE`, `KEYCLOAK_REALM`, `KEYCLOAK_CLIENT_ID`, `KEYCLOAK_CLIENT_SECRET`) — para desarrollo local podés poner cualquier valor propio, no necesitan ser reales.
3. Compilá y levantá todo:
   ```
   cd backend-srv; mvn clean package -DskipTests; cd ..
   cd api-gateway; mvn clean package -DskipTests; cd ..
   docker compose run --rm front-build
   docker-compose up --build -d
   ```
   La primera vez tarda varios minutos (descarga imágenes, compila `front`, arranca Keycloak). `backend-srv` espera a que `postgres` esté *healthy* antes de arrancar.
4. Confirmá que los tres puntos de entrada responden (ver [tabla completa de accesos](#accesos-una-vez-levantado-el-stack)):
   - http://localhost → pantalla de login de la SPA.
   - http://localhost:8080/swagger-ui.html → Swagger UI del API Gateway.
   - http://localhost:8085 → consola de Keycloak.
5. **Smoke test de login:** en http://localhost, entrá con un usuario ya sembrado en `keycloak/realm-export.json` (realm `siip-api`) — por ejemplo `user` / `user123`, o `tecnico.pre` / `tecnicoPre123` si querés ver el módulo de Preinversión con ese rol. Si el login funciona y llegás al home, el stack completo (front → gateway → Keycloak → backend-srv → postgres) está bien conectado.

Si algo de esto falla, revisá `docker-compose logs -f <servicio>` antes que nada; y si seguís trabado, escribile a david@magnaperitia.com.

## Requisitos

1. Java 21 (OpenJDK) https://download.oracle.com/java/21/latest/jdk-21_windows-x64_bin.exe 
2. Maven (o usar el wrapper `mvnw` incluido en cada módulo) https://maven.apache.org/download.cgi
3. Node.js 22+ y npm (solo para trabajar en `front` fuera de Docker) — instalar vía [nvm](https://github.com/nvm-sh/nvm) ([nvm-windows](https://github.com/coreybutler/nvm-windows) en Windows) y correr `nvm install 22 && nvm use 22`
4. Docker y Docker Compose https://www.docker.com/products/docker-desktop/

> Tras instalar Java y Maven, agregá sus carpetas `bin` al `PATH` (y configurá `JAVA_HOME` apuntando al JDK y, si instalaste Maven manualmente, `M2_HOME`/`MAVEN_HOME` apuntando a esa carpeta) — sin esto, `java` y `mvn` no se reconocen desde la terminal. Verificá con `java -version` y `mvn -version`.

## Variables de entorno

Los servicios se configuran mediante un archivo `.env` en la raíz del proyecto (usado por `docker-compose.yml`). Variables requeridas:

```
DB_USER=
DB_PASSWORD=
DB_DATABASE=
DB_URL=jdbc:postgresql://postgres:5432/preinversiondb
DB_DRIVER_CLASS_NAME=org.postgresql.Driver
DB_SCHEMA=public
KEYCLOAK_REALM=
KEYCLOAK_INTERNAL_URL=http://keycloak:8080
KEYCLOAK_EXTERNAL_URL=http://localhost:8085
KEYCLOAK_CLIENT_ID=
KEYCLOAK_CLIENT_SECRET=
GATEWAY_INTERNAL_URL=http://api-gateway:8080
GATEWAY_URL=http://localhost:8080
SONAR_HOST_URL=http://localhost:9000
SONAR_TOKEN=
SONARQUBE_DB_USER=
SONARQUBE_DB_PASSWORD=
SONARQUBE_DB_DATABASE=
```

`SONAR_TOKEN` se genera desde la propia consola de SonarQube (http://localhost:9000, **My Account → Security**) una vez que el servicio está arriba — no hace falta completarlo antes del primer `docker-compose up` (ver [Análisis estático](./REFERENCE.md#sonarqube-local)).

> No versionar el `.env` con credenciales reales; usarlo solo como plantilla local.

## Base de datos: Postgres en local, Oracle en producción

`backend-srv` no tiene el driver ni la URL de base de datos hardcodeados — todo sale de `DB_URL`/`DB_DRIVER_CLASS_NAME`/`DB_SCHEMA`, así que el mismo jar sirve para cualquiera de los dos motores. En local (docker-compose) apunta a Postgres con los valores del `.env` de arriba. En los ambientes de la entidad estos valores salen del chart de `backend-srv-config` (ver [siguiente sección](#configuración-por-ambiente-en-la-entidad-backend-srv-config)) y tienen esta forma:

```
DB_URL=jdbc:oracle:thin:@//<host>:<puerto>/<service_name_o_SID>
DB_DRIVER_CLASS_NAME=oracle.jdbc.OracleDriver
DB_SCHEMA=<esquema_dueño_de_las_tablas>
```

Prerrequisitos operativos (no son cambios de código):
- Oracle debe ser **12c o superior** (la entidad `Proyecto` usa `GenerationType.IDENTITY`, soportado desde esa versión).
- El esquema `flowable` (separado del esquema de negocio, ver [Motor de procesos](./README.md#motor-de-procesos-flowable)) debe provisionarlo el DBA como un segundo usuario/esquema Oracle con los grants cruzados correspondientes hacia el usuario de la app — en Postgres esto lo hace automáticamente `postgresql/init.sql`, pero ese mecanismo no aplica a Oracle.

### Configuración de esquema por perfil

| Perfil | Dónde se usa | `JPA_DDL_AUTO` | `FLOWABLE_DB_SCHEMA_UPDATE` | Datos de prueba |
|---|---|---|---|---|
| `dev` (`application-dev.yml`) | local: `docker-compose.yml`, `dist-tester` | `create-drop` | `drop-create` | Sí (`DevSeeder`) |
| `test` (`src/test/resources/application-test.yml`) | `mvn test` (H2 en memoria) | `create-drop` | `drop-create` | No (cada prueba arma lo suyo) |
| `prod` (por defecto, `application.yml`) | ambientes de la entidad | `validate` | `false` | No |

Los dos valores se pueden sobreescribir con esas variables de entorno, pero el valor por defecto de `application.yml` es a propósito el seguro: si a un ambiente compartido le falta la variable, la app **no** crea ni borra tablas; si el esquema no existe, el arranque falla y se ve.

> **Estado actual — sin herramienta de migraciones.** En local (`dev`) agregar una columna o tabla nueva a una entidad JPA no requiere ningún paso extra: el esquema se recrea solo al levantar `backend-srv`. En la entidad eso no alcanza: con `validate`, cada cambio de entidad necesita que el esquema de Oracle ya lo tenga. Falta decidir cómo se entrega ese DDL (scripts para el DBA o Flyway/Liquibase) — está anotado en los pendientes de `backend-srv-config/README.md`.

## Configuración por ambiente en la entidad (backend-srv-config)

`backend-srv` sigue la convención de la entidad de separar código y configuración en dos repositorios de Gerrit:

| Repo | Contenido |
|---|---|
| `dgicp-siip2/backend-srv` (carpeta `backend-srv/`) | Código, `Dockerfile`, pipeline. La imagen es la misma en todos los ambientes. |
| `dgicp-siip2/backend-srv-config` (carpeta `backend-srv-config/`) | Chart Helm que despliega ArgoCD: todo lo que distingue dev, pruebas, preproducción y producción. |

Cómo se arma la configuración de un ambiente:

- **La rama de `backend-srv-config` es el ambiente**; dentro de ella, `values.yaml` tiene los valores base y `envs/values-<cluster>.yaml` sobreescribe lo que depende del cluster (base de datos, esquemas, dominios, registry).
- Lo no sensible va al **ConfigMap `backend-srv-cmp`** (`configMap.data` del chart) y lo sensible (`DB_USER`, `DB_PASSWORD`) al **Secret `backend-srv-secret`**, que el chart crea vacío y el owner completa directamente en el cluster. Nada sensible va a git.
- Ambos llegan al contenedor como variables de entorno (`envFrom`), con los mismos nombres que usa `docker-compose.yml` en local. La lista completa de variables y su valor en cada ambiente está en [`backend-srv/docs/configuracion.md`](./backend-srv/docs/configuracion.md) y en [`backend-srv-config/README.md`](./backend-srv-config/README.md).

Regla práctica: **si agregás una propiedad a `application.yml` que cambia por ambiente**, leela de una variable de entorno (con un valor por defecto seguro), agregala al `.env`/`docker-compose.yml` para local y a `configMap.data` de `backend-srv-config` (o al Secret, si es sensible) en el mismo cambio.

## Compilación y despliegue

### Todo el sistema

```
cd backend-srv; mvn clean package -DskipTests; cd ..
cd api-gateway; mvn clean package -DskipTests; cd ..
docker compose run --rm front-build
docker-compose up --build -d
```

`backend-srv` y `api-gateway` son proyectos Maven independientes (no hay `pom.xml` agregador en la raíz: cada uno va a su propio repositorio en la entidad), así que se compilan por separado (`front` no es un proyecto Maven — ver más abajo — y `siip-comun` se fusionó dentro de `backend-srv`). `front` sigue el mismo patrón: su `Dockerfile` (el mismo que usa el pipeline de la entidad) no compila, solo empaqueta `front/dist/` en Apache HTTPD. `docker compose run --rm front-build` hace esa compilación (`npm ci && npm run build`) en un contenedor con Node y Java, así que no hace falta tenerlos en el host; va antes de `up` porque Compose construye todas las imágenes antes de levantar cualquier contenedor. Las dos imágenes base del front son públicas (`registry.access.redhat.com`), así que esto funciona fuera de la VPN del MH.

### Un solo módulo (por ejemplo, `backend-srv`)

```
cd backend-srv
mvn clean package -DskipTests
cd ..
docker compose up -d --build backend-srv
```

> `backend-srv` y `api-gateway` no tienen dependencias entre sí ni con otro módulo Java (`siip-comun` se fusionó en `backend-srv`): cada uno compila solo.

### Frontend en desarrollo local (sin Docker)

```
cd front
npm install
npm run dev
```

El servidor de Vite (`http://localhost:5173`) proxya `/auth/**` y `/back/**` hacia `api-gateway` (por defecto `http://localhost:8080`, configurable con `VITE_API_PROXY_TARGET` en `front/.env.development`) — así el código de la app siempre usa rutas relativas y se comporta igual en desarrollo que en producción (donde ese mismo rol lo cumple Apache HTTPD, ver `front/httpd.conf`).

`npm run build` compila con TypeScript y genera el bundle de producción en `front/dist/` (lo que empaqueta el `Dockerfile`). Para la estructura de carpetas del front, ver [front/docs/architecture.md](./front/docs/architecture.md).

### Herramienta externa: Flowable UI (opcional)

Para inspeccionar procesos/tareas con la consola oficial de Flowable:

```
docker run -p 8090:8080 flowable/flowable-ui
```

> Nota: el puerto interno del contenedor sigue siendo 8080 (el mismo que `api-gateway`), por eso se remapea al host como 8090 con `-p 8090:8080` — así puede levantarse junto con el resto del stack sin conflicto. La consola queda accesible en http://localhost:8090.

## Accesos una vez levantado el stack

| Servicio | URL |
|---|---|
| Frontend | http://localhost |
| API Gateway / Swagger UI | http://localhost:8080/swagger-ui.html |
| Keycloak | http://localhost:8085 |
| PostgreSQL | localhost:5432 |
| SonarQube | http://localhost:9000 |
| Flowable UI (opcional, ver arriba) | http://localhost:8090 |
