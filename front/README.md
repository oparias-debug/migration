# dgicp-siip2-frontend-ui

componente para interfaz de usuario de sistema información de inversión publica

SPA del Sistema de Información de Inversión Pública (SIIP) sobre **React 18 + Vite 6 (TypeScript)**,
con React Router, `react-hook-form` + `zod` para formularios, i18next y CSS propio (`src/styles/`). En producción
se sirve como estáticos desde Apache HTTPD (UBI 9), que además reenvía `/auth/**` y `/back/**` hacia
`api-gateway`: el navegador habla con un único origen y el gateway no necesita CORS.

> Este front **no** está construido sobre la plantilla institucional Next.js (`react-simple`). De la
> plantilla se adoptaron el `Dockerfile` (HTTPD sobre UBI 9 fijado por digest), las cabeceras de
> seguridad de `httpd.conf`, `sonar-project.properties` y `stylelint.config.mjs`, adaptados al stack
> real. Ver [Pendientes](#pendientes--por-revisar).

## Estructura

```
├── openapi/             → copia de los contratos .openapi.yaml de back (fuente del cliente generado)
├── features/            → copia de los .feature (Gherkin) de back — especificación funcional
├── public/              → logos, íconos del menú y la fuente Roboto (se sirven tal cual)
├── scripts/
│   ├── comprobar-quality-gate.mjs → consulta el Quality Gate de Sonar tras `npm run sonar`
│   └── sembrar-demo.mjs           → carga datos de demostración contra el back
├── src/
│   ├── api/             → httpClient (axios + refresh-on-401), wrappers por dominio;
│   │                      generated/ lo crea `npm run generate:api` (se versiona, ver abajo)
│   ├── auth/            → AuthContext, RequireAuth, tokenStore (login contra /auth de api-gateway)
│   ├── layout/          → AppLayout, Sidebar, Topbar, navegación
│   ├── pages/           → Login, Home, placeholders
│   ├── features/        → pantallas por dominio (preinversion/, administracion/)
│   ├── components/      → form/, table/, diálogos y componentes reutilizables
│   ├── i18n/            → traducciones (es)
│   └── styles/          → CSS global y tokens del sistema de diseño
├── Dockerfile           → empaqueta dist/ (ya compilado) en ubi9/httpd-24; el mismo en local y en la entidad
└── httpd.conf           → SPA fallback + proxy a api-gateway + cabeceras de seguridad
```

## Desarrollo local

Requisitos: Node 22+. Java 11+ solo si vas a regenerar el cliente (`openapi-generator-cli` corre
sobre la JVM).

```bash
npm ci
npm run dev            # http://localhost:5173
```

### Cliente generado (`src/api/generated/`)

Lo genera `openapi-generator-cli` desde `openapi/**/*.openapi.yaml` y **se versiona** (solo los `.ts`):
la imagen Node del pipeline de la entidad no trae Java, así que el pipeline no puede regenerarlo y
compila con lo que está en el repo. Cada vez que cambia un `.yaml`:

```bash
npm run generate:api   # regenera src/api/generated/
git add src/api/generated openapi
```

Nunca se edita a mano: el próximo `generate:api` lo pisa.

Vite reenvía `/auth` y `/back` a `api-gateway` (por defecto `http://localhost:8080`, configurable con
`VITE_API_PROXY_TARGET` en `.env.development`), así que el back tiene que estar arriba. Para levantar el
stack completo (back, gateway, Keycloak, Postgres) ver `SETUP.md` en la raíz del monorepo `siip`.

## Build

```bash
npm run build          # tsc -b + vite build → dist/ (el echo inicial es el puente del pipeline, ver Pendientes)
```

## Pruebas y calidad

```bash
npm test               # Vitest (una corrida)
npm run test:coverage  # + cobertura en coverage/ (lcov.info para Sonar, index.html para leer)
npm run lint:css       # stylelint sobre src/**/*.css
npm run sonar          # generate:api + cobertura + análisis (@sonar/scan)
npm run gate           # consulta el Quality Gate del último análisis
```

`sonar-project.properties` apunta al servidor institucional. Para el SonarQube local del monorepo:

```powershell
$env:SONAR_HOST_URL = "http://localhost:9000"
$env:SONAR_TOKEN = "<token>"
npm run sonar
npm run gate
```

## Contenedor

Un solo `Dockerfile`, el mismo en la entidad y en local: copia `dist/` a `ubi9/httpd-24` (puerto 8080,
usuario 1001) con `httpd.conf`. **No compila**: `dist/` tiene que existir antes del `docker build`.

| Dónde | Quién compila `dist/` |
|---|---|
| Entidad | El pipeline Tekton (`npm ci && npm run build`) antes de construir la imagen. |
| Local (`docker-compose.yml`, `scripts/publish-images.ps1`) | El servicio `front-build`: un contenedor con Node 22 + Java 21 (UBI 9) que monta `front/` y corre `npm ci && npm run build`. No hace falta Node ni Java en el host, y las dos imágenes base son públicas: funciona fuera de la VPN del MH. |

Desde la raíz del monorepo:

```bash
docker compose run --rm front-build     # compila → front/dist/
docker compose up -d --build front      # empaqueta y levanta
```

Van en dos pasos porque `up --build` construye todas las imágenes antes de levantar cualquier
contenedor: `front-build` no llegaría a dejar `dist/` a tiempo para el `COPY`. Tiene profile `build`
para que `docker compose up` no lo levante solo.

| Variable | Default | Descripción |
|---|---|---|
| `GATEWAY_URL` | `http://api-gateway:8080` | URL interna de `api-gateway` a la que se reenvían `/auth/**` y `/back/**`. |

## Documentación

[`docs/`](docs/index.md) se publica como TechDocs en el Developer Hub: [arquitectura](docs/architecture.md)
y [desarrollo](docs/desarrollo.md) (cliente generado desde OpenAPI, pruebas, especificaciones Gherkin,
SonarQube). Los pasos del lado front para implementar un CU están en [CONTRIBUTING.md](CONTRIBUTING.md).
Lo que abarca el sistema completo (levantar el stack, convenciones compartidas con el back) vive en la
raíz del monorepo `siip` (`README.md`, `SETUP.md`, `CONTRIBUTING.md`).

## Pendientes / por revisar

- **Puente para el pipeline (`scripts.build`):** el script de build empieza con
  `echo pipeline-mh-rama-react-next-ver-README`. **No es decorativo, no lo quites.** El step
  `run-unit-tests` de la Task `nodejs-dependency-test` elige cómo correr los tests buscando `next`
  en el script de build: si lo encuentra entra en "Modalidad de React" (`npm run test`, que con
  Vitest funciona tal cual); si no, cae en la rama Jest de **Angular**, que exige `@angular/core`
  15-22 y falla con `exit 1` (migrar a Jest no lo evita). Se pidió a la plataforma que la rama
  React acepte también Vite; cuando lo hagan, el `echo` se quita y el build vuelve a ser
  `tsc -b && vite build`.
- **Cliente generado desincronizado:** como `src/api/generated/` se versiona, nada impide subir un `.yaml`
  cambiado sin regenerar. Si pasa seguido, vale la pena un chequeo en CR (regenerar y fallar si hay diff),
  que requeriría Java en el pipeline.
- **Salida del build:** la plantilla documenta `out/` (Next.js); este front deja `dist/`. Confirmar que el
  pipeline no copia ni publica una carpeta fija.
- **Resultados de pruebas en Sonar:** la plantilla publica `coverage/test-report.xml` con `jest-sonar`. Con
  Vitest haría falta un reporter equivalente (p. ej. `vitest-sonar-reporter`); hoy Sonar recibe la
  cobertura pero no el conteo de pruebas.
- **CSS vs. stylelint:** `npm run lint:css` marca ~290 hallazgos sobre el CSS actual (49 de
  `no-descending-specificity` = `css:S4664`; el resto es formato que se corrige solo con `--fix`).
- **ESLint:** `npm run lint` corre `eslint .`, pero `eslint` no está en `devDependencies` ni hay
  configuración: hoy falla.
- **Íconos desde CDN:** `index.html` carga Font Awesome y Bootstrap Icons de cdnjs/jsdelivr, por eso la
  CSP de `httpd.conf` abre esos dos hosts. Pasarlos a paquetes npm permitiría dejar la CSP en `'self'`.
