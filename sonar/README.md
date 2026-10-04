# SonarQube local

Cómo levantar el SonarQube local, cargarle las reglas de la entidad (MH) y analizar
`backend-srv`, `api-gateway` y `front` contra él antes de subir un cambio.

| Archivo | Qué es |
|---|---|
| `reglas-entidad.json` | Perfil de reglas y quality gate "Entidad MH" (Java, XML, TS, CSS). |
| `aplicar-reglas-entidad.py` | Aplica el JSON al Sonar local y verifica regla por regla. Lo corre el servicio `sonarqube-init`. |

Todos los comandos son de PowerShell y se corren desde la raíz del monorepo, salvo que se
indique otra carpeta.

## 1. Levantar el servidor

```powershell
docker compose up -d sonarqube
```

Levanta también su base `sonarqube-db`. La primera vez tarda un par de minutos
(Elasticsearch embebido). Queda en http://localhost:9000.

Solo la primera vez:

1. Entrar con `admin` / `admin`. Pide cambiar la clave.
2. Poner esa clave nueva en el `.env` de la raíz: `SONAR_ADMIN_PASSWORD=...`. La usa
   `sonarqube-init` para administrar perfiles.
3. Crear un token (**My Account → Security → Generate Token**, tipo *User Token*) y ponerlo
   en el `.env`: `SONAR_TOKEN=sqa_...`. Lo usan los análisis. Un token de otro servidor no sirve.

Si el contenedor muere con `vm.max_map_count [...] is too low`:

```powershell
wsl -d docker-desktop sysctl -w vm.max_map_count=262144
```

## 2. Actualizar el Sonar local con las reglas de la entidad

```powershell
docker compose up sonarqube-init -d
```

Espera a que Sonar esté `UP` y después, por cada lenguaje del JSON:

- crea el perfil "Entidad MH" (hereda de *Sonar way*) si no existe;
- activa cada regla con sus parámetros y desactiva las que ya no están en el JSON;
- lo deja como perfil por defecto del lenguaje.

También crea el quality gate "Entidad MH" (cobertura ≥ 95 %) y lo deja por defecto. Termina con
`Perfil y quality gate de la entidad aplicados y verificados.`, o con la lista de lo que no
coincide y código de salida 1. Es idempotente: se puede correr cuantas veces haga falta.

Las reglas nuevas se aplican en el **próximo análisis**. Los resultados que ya están en el
servidor no cambian hasta volver a analizar (paso 3).

### Cuando la entidad reporta una regla que no tenemos

No hay backup del perfil de la entidad: el JSON se reconstruye a partir de las incidencias que
reporta su pipeline.

1. En el Sonar de la entidad, abrir el detalle de la incidencia y copiar la clave de la regla
   (`java:S1234`, `typescript:S1234`, `xml:S1234`, `css:S1234`).
2. Agregarla en `reglas-entidad.json`, en el perfil de su lenguaje (`perfil` para Java,
   `perfiles_adicionales` para el resto):

   ```json
   { "regla": "java:S103", "params": { "maximumLineLength": "120" }, "nota": "Split this N characters long line" }
   ```

   `params` solo hace falta si el mensaje de la entidad muestra un umbral distinto del default.
   La `nota` es el mensaje de la incidencia, para reconocerla después.
3. Volver a correr `docker compose up sonarqube-init` y reanalizar el proyecto.

Las reglas de `.sql` (analizador PL/SQL) solo existen en las ediciones comerciales de
SonarQube. La imagen local es `sonarqube:community`, así que esas reglas quedan documentadas en
`no_disponibles_en_community` y no se pueden probar en local.

## 3. Analizar cada proyecto

Primero, en la misma terminal, apuntar al Sonar local y cargar el token del `.env`:

```powershell
$env:SONAR_HOST_URL = "http://localhost:9000"
$env:SONAR_TOKEN = "$((Get-Content .env | Select-String '^SONAR_TOKEN=').ToString().Split('=')[1])"
```

`backend-srv` y `front` apuntan por defecto al servidor institucional (`alcm.mh.gob.sv`). Sin
`SONAR_HOST_URL`, el análisis se manda allá. `api-gateway` ya apunta a `localhost:9000`.

| Proyecto | Clave en Sonar |
|---|---|
| `backend-srv` | `dgicp-siip2-backend-srv` |
| `api-gateway` | `siip-api-gateway` |
| `front` | `dgicp-siip2-frontend-ui` |

### Backend (`backend-srv`)

```powershell
cd backend-srv
mvn clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:5.1.0.4751:sonar
cd ..
```

`verify` corre las pruebas y genera la cobertura JaCoCo que lee Sonar. El `clean` hace falta
porque MapStruct no regenera bien sus mapeadores en compilaciones incrementales.

### API Gateway (`api-gateway`)

```powershell
cd api-gateway
mvn clean verify org.sonarsource.scanner.maven:sonar-maven-plugin:5.1.0.4751:sonar
cd ..
```

### Front (`front`)

```powershell
cd front
npm run sonar   # generate:api + test:coverage (Vitest) + @sonar/scan
npm run gate    # falla si el quality gate quedó en rojo
cd ..
```

`npm run verificar` hace todo seguido: `tsc -b`, `sonar` y `gate`.

El primer análisis de cada proyecto puede tardar varios minutos sin imprimir nada (JVM en frío,
scanner por descargar). No está colgado.

## 4. Revisar el resultado

Al final, el análisis imprime `ANALYSIS SUCCESSFUL, you can find the results at: ...`. Revisar
que esa URL sea `localhost:9000` y la clave del proyecto correcta.

En http://localhost:9000, abrir el proyecto y revisar:

- **Quality Gate**: debe estar en verde. La condición de la entidad es cobertura total ≥ 95 %.
- **New Code**: sin bugs, vulnerabilidades ni code smells nuevos en lo que se agregó. La deuda
  que ya existía no bloquea el PR.

Para consultar el quality gate de `backend-srv` o `api-gateway` sin abrir el navegador:

```powershell
$auth = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("$($env:SONAR_TOKEN):"))
(Invoke-RestMethod "$env:SONAR_HOST_URL/api/qualitygates/project_status?projectKey=dgicp-siip2-backend-srv" `
  -Headers @{ Authorization = "Basic $auth" }).projectStatus.status   # OK o ERROR
```

Para confirmar que el proyecto usa las reglas de la entidad: **Project Settings → Quality
Profiles** debe mostrar "Entidad MH" en cada lenguaje, y **Project Settings → Quality Gate**
también "Entidad MH". Si un proyecto tiene otro perfil asignado a mano, el default no le aplica:
cambiarlo ahí a "Entidad MH".

## Problemas comunes

| Síntoma | Causa y solución |
|---|---|
| `sonarqube-init` termina con `Falta SONAR_ADMIN_TOKEN o SONAR_ADMIN_PASSWORD` | Falta la clave de `admin` en el `.env` (paso 1). |
| `sonarqube-init` da `HTTP 401` o `403` | La clave de `SONAR_ADMIN_PASSWORD` no es la actual de `admin`, o se usó el `SONAR_TOKEN` de un usuario común, que no puede administrar perfiles. |
| El análisis da `401` | `SONAR_TOKEN` vacío o de otro servidor. Revisar `$env:SONAR_TOKEN`. |
| El análisis sube a `alcm.mh.gob.sv` | Falta `$env:SONAR_HOST_URL` en esa terminal. |
| `@sonar/scan` falla con `403` contra SonarCloud | No encontró `sonar.host.url`. Correr `npm run sonar` desde `front/`. |
| Cobertura en 0 % | El análisis se corrió sin `verify` (Java) o sin `test:coverage` (front), y no se generó el reporte. |
| Una regla de la entidad no aparece en local | Correr `docker compose up sonarqube-init` y reanalizar. Si es de `.sql`, no existe en Community (ver paso 2). |

Más detalle en [REFERENCE.md § SonarQube local](../REFERENCE.md#sonarqube-local),
[backend-srv/docs/desarrollo.md](../backend-srv/docs/desarrollo.md#análisis-estático-sonarqube)
y [front/docs/desarrollo.md](../front/docs/desarrollo.md#análisis-estático-sonarqube).
