# 7. Ambiente local — ejecución directa y con Podman

Cómo levantar este servicio en la máquina del desarrollador, de dos formas: **directa**
(`spring-boot:run` con el perfil `dev`) y **en contenedor** (Podman, el mismo artefacto que
se despliega en OpenShift).

En ejecución directa con `--spring.profiles.active=dev` el `application.yml` aporta la base
de datos de desarrollo y CORS abierto. En contenedor no hay perfil activo y toda variable
sin valor por defecto tiene que venir del entorno. Casi todos los "en mi máquina sí
corría" salen de ahí.

---

## 1. Requisitos previos

| Herramienta | Versión | Nota |
|---|---|---|
| JDK | 21 | Igual que `java.version` del `pom.xml` y que la imagen del CI |
| Maven | No se instala | Se usa el wrapper del proyecto: `./mvnw` / `mvnw.cmd` |
| Podman | 4.x o superior | Sólo para el Modo B. En Windows requiere `podman machine` |
| Red MH | VPN o red interna | Los defaults apuntan a los servicios del marco y a la Oracle de desarrollo |

### 1.1 Espejo de Maven

El CI resuelve dependencias contra el Nexus institucional. Usar el mismo espejo en local
evita diferencias y funciona sin salida a internet. Crear `~/.m2/settings.xml` (en
Windows, `C:\Users\<usuario>\.m2\settings.xml`):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0">
  <mirrors>
    <mirror>
      <id>alcm-nexus</id>
      <mirrorOf>*</mirrorOf>
      <url>http://alcm.mh.gob.sv/e/nexus/repository/public</url>
    </mirror>
  </mirrors>
</settings>
```

---

## 2. Modo A — Ejecución directa

### 2.1 Arranque

```bash
export DB_USER="<usuario>"
export DB_PASSWORD="<password>"
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

```powershell
$env:DB_USER = "<usuario>"; $env:DB_PASSWORD = "<password>"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

| Recurso | URL |
|---|---|
| Aplicación | `http://localhost:8080` |
| Health | `http://localhost:8080/actuator/health` |
| OpenAPI | `http://localhost:8080/v3/api-docs` |
| Swagger UI | `http://localhost:8080/swagger-ui` |
| Endpoint de ejemplo, público | `http://localhost:8080/api/v1/demo/security/publico` |
| Endpoint de ejemplo, autenticado | `http://localhost:8080/api/v1/demo/security/autenticado` |

Spring Boot DevTools no está incluido: un cambio de código requiere reiniciar.

### 2.2 Qué resuelve el `application.yml` por sí solo

| Propiedad | Valor por defecto |
|---|---|
| `config.service.url`, `authz.service.url`, `audit.service.url`, `remote.logger.url` | Servicios del marco de DEV (`gcp-op-desa`) |
| `spring.security.oauth2.resourceserver.jwt.issuer-uri` | Keycloak DEV, realm `MHINTERNO` |
| `spring.datasource.url` (perfil `dev`) | Oracle de desarrollo |
| `cors.*` (perfil `dev`) | Abierto (`*`) |

Sin VPN el arranque falla al resolver el `issuer-uri` contra Keycloak. Ver §2.4.

### 2.3 Credenciales de base de datos

El perfil `dev` toma `DB_USER` y `DB_PASSWORD` del entorno; si faltan usa `changeme` y
Oracle responde `ORA-01017`. Nunca versionarlas ni escribirlas en el `application.yml`.

### 2.4 Arrancar sin Oracle y sin Keycloak

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev \
  -Dspring-boot.run.arguments="--spring.datasource.url=jdbc:h2:mem:local --spring.datasource.driver-class-name=org.h2.Driver --spring.datasource.username=sa --spring.datasource.password= --spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:0/mock"
```

Con eso todo endpoint no público responde `401` y nada que persista en Oracle funciona. Es
un modo para iterar sobre lógica pura, no para validar un flujo completo. (Es la misma
configuración que usan las pruebas unitarias en `src/test/resources/application.yml`.)

### 2.5 Cambiar el puerto

```bash
HTTP_PORT=8085 ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### 2.6 Pruebas

```bash
./mvnw test                          # unitarias (H2, sin Keycloak)
./mvnw verify -Pintegration-tests    # Karate contra el servicio levantado
```

---

## 3. Modo B — Ejecución con Podman

### 3.1 El `Dockerfile` no compila la aplicación

Copia `target/app.jar`, que hay que producir antes con `./mvnw package`.

### 3.2 Construir y correr

```bash
./mvnw package -DskipTests
podman build -f Dockerfile -t servicio:local .
podman run --rm --name servicio-local -p 8080:8080 --env-file .env.local servicio:local
```

### 3.3 Variables de entorno en contenedor

Sin perfil `dev`, las tres de base de datos no tienen valor por defecto y el arranque falla
con `Could not resolve placeholder` si faltan. Las demás caen a los valores de dev del
`application.yml`.

| Variable | ¿Obligatoria? | Descripción |
|---|---|---|
| `DB_URL` | Sí | Cadena JDBC de Oracle |
| `DB_USER`, `DB_PASSWORD` | Sí | Credenciales |
| `CONFIG_SERVICE_URL`, `AUTHZ_SERVICE_URL`, `AUDIT_SERVICE_URL`, `LOG_SERVICE_URL` | No (dev) | URL del marco, con sus paths |
| `SECURITY_URL_KEYCLOAK`, `SECURITY_REALM` | No (dev, `MHINTERNO`) | Host del Keycloak (sin esquema) y realm |
| `HTTP_PORT` | No (`8080`) | Puerto |
| `CORS_ALLOWED_ORIGINS`, `CORS_ALLOWED_METHODS`, `CORS_ALLOWED_HEADERS` | No | CORS |

### 3.4 Archivo `.env.local`

```properties
# .env.local — NO versionar. Anadir a .gitignore.
DB_URL=jdbc:oracle:thin:@10.0.128.56:3115/desadevhpdb
DB_USER=mi_usuario
DB_PASSWORD=mi_password
SECURITY_URL_KEYCLOAK=keycloak-mh-dev.apps.gcp-op-desa.cloud.mh.gob.sv
SECURITY_REALM=MHINTERNO
CONFIG_SERVICE_URL=https://inventario-service-mh-dev-dinafi-usi-frmk.apps.gcp-op-desa.cloud.mh.gob.sv/api/v1/config/service
```

### 3.5 El contrato de `CONFIG_SERVICE_URL`

Es una **URL base** a la que `ExternalConfigSource` concatena `/` + `service.name`; el path
`/api/v1/config/service` es parte del valor. Sin el path, el inventario responde 404 y el
servicio arranca con la configuración vacía. Prueba rápida:

```bash
curl -s -w " [%{http_code}]" "$CONFIG_SERVICE_URL/PRUEBA"
# esperado: {"error":"Componente no encontrado"} [404]
```

### 3.6 Podman en Windows

```powershell
podman machine init          # sólo la primera vez
podman machine start
```

---

## 4. Problemas frecuentes

| Síntoma | Causa | Solución |
|---|---|---|
| `Could not resolve placeholder 'DB_URL'` en contenedor | Falta la variable | Completar `.env.local` (§3.3) |
| `ORA-01017: invalid username/password` | `DB_USER`/`DB_PASSWORD` en `changeme` | Exportar credenciales reales (§2.3) |
| El arranque falla al resolver el issuer | Keycloak inalcanzable (sin VPN) | VPN, o §2.4 |
| `401` con token válido | `SECURITY_URL_KEYCLOAK` con `https://`, o realm distinto | Sólo el host; comparar `iss` |
| `podman build` falla con `target/app.jar: no such file` | No se ejecutó `./mvnw package` | §3.1 |
| Puerto 8080 ocupado | Otro servicio local | `HTTP_PORT=8085`, o `-p 8085:8080` en Podman |

---

## 5. Checklist antes de pedir ayuda

- [ ] `~/.m2/settings.xml` con el espejo `alcm-nexus`.
- [ ] `java -version` reporta 21.
- [ ] Hay red hacia `apps.gcp-op-desa.cloud.mh.gob.sv` (VPN).
- [ ] `DB_USER` y `DB_PASSWORD` exportados; perfil `dev` activo.
- [ ] Para Podman: `./mvnw package` ejecutado y `.env.local` con `DB_URL`, `DB_USER`, `DB_PASSWORD`.

---

## Documentos relacionados

- [Configuración de variables](helm-configuration.md) — ConfigMap y Secret en OpenShift.
- [El ciclo de CI/CD](ci-cd-pipeline.md) — cómo construye Tekton esta misma imagen.
- [Pruebas](testing.md) — unitarias, Karate, carga.
- [Seguridad](security.md) — obtención de tokens para probar endpoints protegidos.
