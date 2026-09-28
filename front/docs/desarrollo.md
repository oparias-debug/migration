# Desarrollo

Cómo se trabaja en el código de `front`: cliente generado desde OpenAPI, pruebas,
especificaciones Gherkin y análisis estático. Los comandos básicos (desarrollo, build,
contenedor) están en el [README](../README.md); la organización de carpetas, en
[Arquitectura](architecture.md).

## Cliente generado desde OpenAPI

El back escribe sus servicios **contrato primero**: un `.yaml` OpenAPI por caso de uso. El
front genera su cliente HTTP a partir de esos mismos contratos con `openapi-generator-cli`
(generador `typescript-axios`).

Los `.yaml` son una **copia manual** de los que viven en
`src/main/resources/openapi/` del repositorio de `backend-srv`. No hay symlink ni script de
sincronización: al cambiar un contrato hay que copiarlo a los dos repositorios y mantenerlos
idénticos.

```
openapi/
├── administracion/   # CU-ADM-01, CU-ADM-02 (catalogo/), CU-ADM-04 (calendario/)
└── preinversion/     # CU-PRE-01 ... CU-PRE-33, un .yaml por CU
```

Cada contrato tiene su propio script en `package.json`, y `generate:api` los corre todos:

```
npm run generate:api                          # regenera todo src/api/generated/
npm run generate:api:preinversion-cu-pre-20   # solo un contrato
```

Cada script escribe en su propia carpeta, `src/api/generated/<dominio>-<nombre>/` (por
ejemplo `preinversion-beneficios/` para CU-PRE-20), con
`supportsES6=true,withInterfaces=true,useSingleRequestParameter=true`. Genera una clase de API
por `tag` del yaml más los tipos de request y response.

### Por qué el cliente generado se versiona

`src/api/generated/` **se versiona** (solo los `.ts`; `.gitignore` excluye `docs/`,
`git_push.sh` y los metadatos del generador). El generador corre sobre la JVM y la imagen
Node del pipeline de la entidad no trae Java, así que el pipeline no puede regenerarlo: compila
con lo que está en el repositorio.

- `npm run build` **no** regenera el cliente (no hay hook `prebuild`).
- `generate:api` se corre a mano (necesita Java 11+) cada vez que cambia un `.yaml`, y lo
  generado se commitea junto con el contrato.
- Nunca se edita a mano: el próximo `generate:api` lo pisa.

### Agregar un contrato

1. Copiar el `.yaml` del back a `openapi/<dominio>/CU-XX.openapi.yaml`, sin modificarlo.
2. Agregar un script `generate:api:<dominio>-cu-xx` en `package.json`, copiando uno existente
   y cambiando `-i` (el `.yaml`) y `-o` (una carpeta nueva bajo `src/api/generated/`).
   Sumarlo a la cadena de `generate:api`.
3. Correr el script y crear o extender el wrapper `src/api/<dominio>Api.ts`: instancia las
   clases generadas con `createHttpClient('/back')` y reexporta los tipos que use la UI (ver
   [Arquitectura § Llamadas al back](architecture.md#llamadas-al-back)).
4. Commitear juntos el `.yaml`, lo generado y el wrapper.

Los pasos completos para implementar la pantalla de un CU están en
[CONTRIBUTING.md](../CONTRIBUTING.md).

## Pruebas

```
npm test                # Vitest, una corrida
npm run test:watch      # modo watch
npm run test:coverage   # + cobertura (coverage/index.html y coverage/lcov.info)
```

Vitest + React Testing Library, con `jsdom` como entorno DOM. Los tests viven junto al código
que prueban (`*.test.ts` / `*.test.tsx`, por ejemplo `src/auth/tokenStore.test.ts`).

La cobertura usa `@vitest/coverage-v8` (configurada en `vite.config.ts`) e incluye
`src/**/*.{ts,tsx}` salvo el código generado. `lcov.info` es el formato que lee SonarQube.

## Especificaciones Gherkin (`features/`)

`features/` guarda copias de los `.feature` de `src/test/resources/features/` de
`backend-srv`, solo de los CU que el front implementa. Son la especificación funcional en
español que la UI debe cumplir: pantallas, mensajes y validaciones.

No son una suite automatizada. `@cucumber/cucumber` está en las dependencias y existe
`npm run test:bdd`, pero no hay step definitions del lado front. En la práctica sirven de base
para los `*.test.tsx` y para las pruebas manuales. Si el equipo decide automatizarlas, los
steps van en `features/step_definitions/<dominio>/`.

Igual que los contratos, son copias manuales: si el back cambia un `.feature`, hay que
actualizar la copia.

## Análisis estático (SonarQube)

`sonar-project.properties` fija el proyecto `dgicp-siip2-frontend-ui` y el servidor
institucional (`alcm.mh.gob.sv`). Para analizar contra otro servidor (por ejemplo el
SonarQube local del monorepo `siip`), `SONAR_HOST_URL` tiene prioridad sobre el archivo:

```powershell
$env:SONAR_HOST_URL = "http://localhost:9000"   # sin esto va al servidor institucional
$env:SONAR_TOKEN = "<token>"
npm run sonar   # generate:api + test:coverage + @sonar/scan
npm run gate    # consulta el Quality Gate del último análisis
```

`npm run verificar` hace todo seguido: `tsc -b`, `sonar` y `gate`.

- `@sonar/scan` se descarga bajo demanda con `npx`: no es una dependencia instalada. Si no
  encuentra `sonar.host.url` apunta a SonarCloud y falla con `403`.
- Analiza `src` y también el `Dockerfile` (`sonar.sources=src,Dockerfile`), para que se le
  apliquen las reglas del perfil SecDocker de la entidad.
- `src/api/generated/**` queda excluido del análisis y de la cobertura, igual que `main.tsx`
  y `vite-env.d.ts` en la cobertura.
- Sonar recibe la cobertura pero no el conteo de pruebas: con Vitest haría falta un reporter
  equivalente a `jest-sonar` (ver [README § Pendientes](../README.md#pendientes--por-revisar)).
- El primer análisis puede tardar varios minutos sin imprimir nada; no está colgado.
