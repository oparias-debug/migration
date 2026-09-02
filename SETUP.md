# Puesta en marcha

Cómo levantar el stack completo o un módulo puntual. Para entender qué es cada pieza, ver [README.md](./README.md); para la mecánica de generación de código y testing, ver [REFERENCE.md](./REFERENCE.md).

## Índice

- [Primer día: levantar todo en 10 minutos](#primer-día-levantar-todo-en-10-minutos)
- [Requisitos](#requisitos)
- [Variables de entorno](#variables-de-entorno)
- [Base de datos: Postgres en local, Oracle en producción](#base-de-datos-postgres-en-local-oracle-en-producción)
- [Compilación y despliegue](#compilación-y-despliegue)
- [Herramienta externa: Flowable UI (opcional)](#herramienta-externa-flowable-ui-opcional)
- [Accesos una vez levantado el stack](#accesos-una-vez-levantado-el-stack)

## Primer día: levantar todo en 10 minutos

Versión corta para tener el sistema andando y confirmar que todo quedó bien conectado. El detalle de cada paso está más abajo.

1. Instalá los [requisitos](#requisitos) (Java 25, Docker y Docker Compose alcanzan para esta parte — Node solo hace falta si vas a tocar `front` fuera de Docker).
2. Copiá el bloque de [variables de entorno](#variables-de-entorno) a un archivo `.env` en la raíz del proyecto y completá los valores vacíos (`DB_USER`, `DB_PASSWORD`, `DB_DATABASE`, `KEYCLOAK_REALM`, `KEYCLOAK_CLIENT_ID`, `KEYCLOAK_CLIENT_SECRET`) — para desarrollo local podés poner cualquier valor propio, no necesitan ser reales.
3. Compilá y levantá todo:
   ```
   mvn clean package install -DskipTests
   docker-compose up --build -d
   ```
   La primera vez tarda varios minutos (descarga imágenes, compila `front`, arranca Keycloak). `back` espera a que `postgres` esté *healthy* antes de arrancar.
4. Confirmá que los tres puntos de entrada responden (ver [tabla completa de accesos](#accesos-una-vez-levantado-el-stack)):
   - http://localhost → pantalla de login de la SPA.
   - http://localhost:8080/swagger-ui.html → Swagger UI del API Gateway.
   - http://localhost:8085 → consola de Keycloak.
5. **Smoke test de login:** en http://localhost, entrá con un usuario ya sembrado en `keycloak/realm-export.json` (realm `siip-api`) — por ejemplo `user` / `user123`, o `tecnico.pre` / `tecnicoPre123` si querés ver el módulo de Preinversión con ese rol. Si el login funciona y llegás al home, el stack completo (front → gateway → Keycloak → back → postgres) está bien conectado.

Si algo de esto falla, revisá `docker-compose logs -f <servicio>` antes que nada; y si seguís trabado, escribile a david@magnaperitia.com.

## Requisitos

1. Java 25 (OpenJDK) https://download.oracle.com/java/25/latest/jdk-25_windows-x64_bin.exe 
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

`SONAR_TOKEN` se genera desde la propia consola de SonarQube (http://localhost:9000, **My Account → Security**) una vez que el servicio está arriba — no hace falta completarlo antes del primer `docker-compose up` (ver [Análisis estático](./REFERENCE.md#análisis-estático-sonarqube)).

> No versionar el `.env` con credenciales reales; usarlo solo como plantilla local.

## Base de datos: Postgres en local, Oracle en producción

`back` no tiene el driver ni la URL de base de datos hardcodeados — todo sale de `DB_URL`/`DB_DRIVER_CLASS_NAME`/`DB_SCHEMA`, así que el mismo jar sirve para cualquiera de los dos motores. En local (docker-compose) apunta a Postgres con los valores del `.env` de arriba. En producción, el `.env`/secreto real del servidor debe usar algo como:

```
DB_URL=jdbc:oracle:thin:@//<host>:<puerto>/<service_name_o_SID>
DB_DRIVER_CLASS_NAME=oracle.jdbc.OracleDriver
DB_SCHEMA=<usuario_oracle_de_la_app>
```

Prerrequisitos operativos (no son cambios de código):
- Oracle debe ser **12c o superior** (la entidad `Proyecto` usa `GenerationType.IDENTITY`, soportado desde esa versión).
- El esquema `flowable` (separado del esquema de negocio, ver [Motor de procesos](./README.md#motor-de-procesos-flowable)) debe provisionarlo el DBA como un segundo usuario/esquema Oracle con los grants cruzados correspondientes hacia el usuario de la app — en Postgres esto lo hace automáticamente `postgresql/init.sql`, pero ese mecanismo no aplica a Oracle.

### Configuración de esquema por perfil

El perfil `dev` (`back/src/main/resources/application-dev.yml`) desactiva `ddl-auto` (`none`) para dejar la gestión del esquema fuera del arranque de la aplicación; el perfil por defecto (`application.yml`) usa `create-drop`.

> **Estado actual — sin herramienta de migraciones.** Mientras el proyecto usa `create-drop` en el perfil por defecto, agregar una columna o tabla nueva a una entidad JPA no requiere ningún paso extra: el esquema se recrea solo al levantar `back`. No hace falta escribir scripts de migración (Flyway/Liquibase) por ahora. Esto es una decisión temporal: el día que el proyecto pase a un esquema estable (pensando sobre todo en Oracle en producción, donde un `create-drop` borraría datos reales), va a hacer falta introducir una herramienta de migraciones — pero eso todavía no está resuelto.

## Compilación y despliegue

### Todo el sistema

```
mvn clean package install -DskipTests
docker-compose up --build -d
```

`mvn` compila `api-gateway` y `back` (`front` ya no es un módulo Maven — ver más abajo — y `siip-comun` se fusionó dentro de `back`). `docker-compose up --build` construye la imagen de `front` por separado, ejecutando `npm ci && npm run build` dentro de su propio `Dockerfile` y empaquetando el resultado en una imagen Nginx — no requiere el paso de `mvn` para nada relacionado con el frontend.

### Un solo módulo (por ejemplo, `back`)

```
mvn clean package install -DskipTests -pl back
docker compose up -d --build back
```

> `back` y `api-gateway` ya no tienen dependencias internas entre sí ni con otro módulo Java (`siip-comun` se fusionó en `back`), así que compilar uno con `-pl` no requiere `-am`.

### Frontend en desarrollo local (sin Docker)

```
cd front
npm install
npm run dev
```

El servidor de Vite (`http://localhost:5173`) proxya `/auth/**` y `/back/**` hacia `api-gateway` (por defecto `http://localhost:8080`, configurable con `VITE_API_PROXY_TARGET` en `front/.env.development`) — así el código de la app siempre usa rutas relativas y se comporta igual en desarrollo que en producción (donde ese mismo rol lo cumple Nginx).

`npm run build` compila con TypeScript y genera el bundle de producción en `front/dist/` (lo que empaqueta el `Dockerfile`). Para la estructura de carpetas del front, ver [REFERENCE.md](./REFERENCE.md).

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
