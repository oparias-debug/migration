# Arquitectura

`front` organiza el código **por dominio de negocio** (`features/<dominio>/<caso-de-uso>/`),
con una capa fina compartida alrededor (API, auth, layout, componentes):

```
src/
├── main.tsx          # canjea el refresh token ANTES de montar React, luego monta <App/>
├── App.tsx           # BrowserRouter + rutas; todo menos /login va bajo RequireAuth + AppLayout
├── api/
│   ├── httpClient.ts # fábrica de axios: Authorization: Bearer + reintento único tras 401 (refresh)
│   ├── authApi.ts    # /auth/login y /auth/refresh (instancia aparte, sin interceptores)
│   ├── <dominio>Api.ts # instancia las clases generadas con createHttpClient('/back') y reexporta tipos
│   └── generated/    # cliente typescript-axios generado desde openapi/ — versionado, no se edita
├── auth/             # tokenStore (localStorage), AuthContext (useSyncExternalStore), RequireAuth
├── layout/           # AppLayout, Sidebar, Topbar, BandaRuta, navegacion.ts (árbol del menú)
├── pages/            # Login, Home, NotFound, placeholders de módulos sin implementar
├── features/
│   ├── preinversion/ # bandeja, captura, proyectos, etapas, pasos, identificación, análisis, ...
│   └── administracion/ # catálogos, calendario
├── components/       # form/ (FormRow, DatePickerInput), table/ (DataTable, Pagination), ConfirmDialog
├── i18n/             # i18next, un solo idioma (es)
└── styles/           # tokens.css (sistema de diseño, @font-face Roboto), base.css, estilos.css
```

## Llamadas al back

Todas las URLs son **relativas** (`/auth/...`, `/back/...`). Quien las resuelve
depende del entorno, pero el comportamiento es el mismo (same-origin, sin CORS):

| Entorno | Quién reenvía a `api-gateway` | Configuración |
|---|---|---|
| `npm run dev` | proxy de Vite | `vite.config.ts`, `VITE_API_PROXY_TARGET` |
| `Dockerfile` (entidad y docker-compose) | Apache HTTPD (`mod_proxy`) | `httpd.conf`, `GATEWAY_URL` |

El cliente de cada caso de uso lo genera `openapi-generator-cli` (`typescript-axios`)
desde una copia del contrato del back en `openapi/<dominio>/CU-XX.openapi.yaml`; un
script `generate:api:<cu>` por contrato en `package.json`. El wrapper
`api/<dominio>Api.ts` le pasa a cada clase generada una instancia de
`createHttpClient('/back')` para que comparta el interceptor de autenticación.

## Autenticación

El front **no** usa OIDC en el navegador (no hay `oidc-client-ts` ni redirección a
Keycloak). El login es un formulario propio que envía usuario/contraseña a
`/auth/login` de `api-gateway`, que obtiene los tokens de Keycloak y los devuelve:

1. `tokenStore` guarda access y refresh token en `localStorage` (`siip.auth`) y
   decodifica roles y usuario del JWT (`jwt-decode`).
2. Al cargar la página, `main.tsx` canjea el refresh token si el access venció,
   antes de montar React, para que el guardia de rutas no mande al login por un
   instante.
3. `httpClient` agrega `Authorization: Bearer` a cada llamada; ante un 401 intenta
   **una vez** `/auth/refresh` y reintenta; si falla, limpia la sesión y va a `/login`.
4. `RequireAuth` protege todas las rutas salvo `/login`.

Guardar tokens en `localStorage` es una decisión pragmática documentada en
`tokenStore.ts`: lo correcto sería una cookie `httpOnly` emitida por `api-gateway`.

## Estado

No hay librería de estado global (la plantilla usa Zustand). La sesión vive en
`tokenStore` + `AuthContext`; el resto es estado local de cada pantalla y
formularios con `react-hook-form` + `zod` (un `<algo>FormSchema.ts` por formulario).

## Mapeo frente a los temas de la plantilla

Para orientarse viniendo de la documentación de `react-simple`:

| Tema de la plantilla | Equivalente real en `front` |
|---|---|
| Estructura (Next.js App Router, `src/app/`) | Vite + React Router; rutas en `App.tsx`, pantallas en `features/` y `pages/` |
| Configuración runtime (`app-config.json`, `env-replace.sh`) | No existe: el front no necesita URLs por ambiente (todo es relativo); la única variable, `GATEWAY_URL`, la lee el servidor web, no la app |
| Componentes UI / `shared-lib-react` (frmk) | Componentes propios en `components/` y `layout/`; no depende de `shared-lib-react` |
| Estilos y temas (SCSS) | CSS plano: `styles/tokens.css` define el sistema de diseño; `stylelint.config.mjs` con el preset estándar de CSS |
| Estado (Zustand) | `AuthContext` + estado local (ver arriba) |
| Seguridad (OIDC con `oidc-client-ts`) | Login propio contra `/auth` de `api-gateway` (ver arriba) |
| Autorización por permisos (`withPermission`) | Solo roles del JWT; no hay integración con `authorization-service` |
| Integración con APIs | Cliente generado desde OpenAPI + `httpClient` |
| Internacionalización | i18next con un solo idioma (`es`) |
| Testing (Jest) | Vitest + React Testing Library (`*.test.ts(x)` junto al código), jsdom |
| Contenedor (`out/` en HTTPD) | `dist/` en HTTPD; en local lo compila el servicio `front-build` de `docker-compose.yml` |
| Pipeline, Helm, ambientes, promoción, Gerrit | Son del ambiente de la entidad, no del código; ver la documentación de la plantilla en el Developer Hub |
