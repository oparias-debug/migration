# Guía para el frontend de un CU

Esta guía cubre la parte de `front` al implementar un caso de uso (CU). Las rutas son relativas a la raíz de este repositorio (`frontend-ui`).

Asume que ya leíste el **`CONTRIBUTING.md` del monorepo `siip`** — ahí están las convenciones compartidas con el back (qué podés tocar, branches/commits, reglas de Gherkin, Definition of Done) y el flujo completo de punta a punta. Esta guía solo detalla los pasos específicos de `front`. Para levantar el stack en tu máquina, ver `SETUP.md` del monorepo; para la mecánica de generación de código (OpenAPI → TypeScript) y cómo están organizadas las pruebas, ver [docs/desarrollo.md](./docs/desarrollo.md). Los comandos propios del front (desarrollo, build, pruebas, Sonar, contenedor) están en [README.md](./README.md).

Mirá `preinversion`/CU-PRE-01 como referencia completa: `src/api/preinversionApi.ts`, `src/features/preinversion/proyectos/`.

## Pasos

1. **Copiá el mismo `.feature`** (sin modificarlo) a `features/`. Es la misma especificación que ya cumplió el back — le sirve al front como guía de qué pantallas/mensajes/validaciones implementar.
2. **Copiá el `.openapi.yaml`** actualizado del back a `openapi/<dominio>/CU-XX.openapi.yaml` (idéntico al de `src/main/resources/openapi/` del repo del back).
3. Generá el cliente TypeScript:
   ```
   npm run generate:api
   ```
   (para un dominio nuevo, agregá antes un script `generate:api:<dominio>` en `package.json`, análogo al existente, apuntando a `openapi/<dominio>/CU-XX.openapi.yaml` y `-o src/api/generated/<dominio>`). **Commiteá lo generado** junto con el `.yaml`: el pipeline de la entidad no tiene Java y compila con lo que está en el repo.
4. Creá (o extendé) el wrapper `src/api/<dominio>Api.ts`: instanciá las clases generadas (una por `tag` del yaml) pasándoles `createHttpClient('/back')`. Reexportá ahí los tipos (`Dto`s) que la UI necesite.
5. Implementá la pantalla/componente en `src/features/<dominio>/<caso-de-uso>/`, siguiendo el patrón de `src/features/preinversion/proyectos/`: `react-hook-form` + un schema `zod` en `<algo>FormSchema.ts`, reutilizando los componentes genéricos de `src/components/form/` (`FormRow`, `DatePickerInput`) y `src/components/table/` (`DataTable`, `Pagination`) donde aplique.
6. Conectá la ruta/menú si hace falta (reemplazando el placeholder "🚧 Página en Construcción" del módulo correspondiente en el sidebar/routing).
7. Escribí los tests junto al componente (`*.test.tsx`, Vitest + React Testing Library), cubriendo al menos el camino feliz y las validaciones descritas en el `.feature`. Si el equipo decide automatizar el `.feature` con `cucumber-js`, los steps van en `features/step_definitions/<dominio>/`.
8. Validá todo:
   ```
   npm run lint
   npm run test
   npm run build
   ```

## Qué podés tocar y qué no (específico de front)

✅ Podés tocar:
- Tu wrapper `src/api/<dominio>Api.ts` y tu pantalla en `src/features/<dominio>/`.
- Tu `.feature`/`.openapi.yaml` (copia idéntica de la del back — ver el `CONTRIBUTING.md` del monorepo).

🚫 No toques:
- Código generado: `src/api/generated/`. Si lo editás a mano, se pierde en el próximo `npm run generate:api`.
- El `src/api/httpClient.ts` genérico — cada wrapper de dominio instancia el cliente generado con `createHttpClient('/back')` propio (ver nota en `preinversionApi.ts`); no reutilices el `httpClient` genérico, porque el cliente generado ignora su `basePath` si el axios que recibe ya trae `baseURL` distinto.

## Puntos que suelen confundir a alguien nuevo

- El `.feature` y el `.openapi.yaml` están **duplicados a propósito** en el repo del back y en este — no hay generación cruzada entre repos ni symlinks. Si editás uno, editá el otro a mano.
- El código generado (cliente TS en `src/api/generated/`) **nunca se edita a mano**, pero **sí se versiona** (solo los `.ts`): el pipeline de la entidad no tiene Java para regenerarlo. Se regenera con `npm run generate:api` y se commitea junto con el `.yaml` que lo originó.

## Definition of Done (front)

Antes de abrir el PR, confirmá:

```
[ ] npm run lint pasa
[ ] npm run test pasa
[ ] npm run build pasa
[ ] .feature idéntico en features/ (este repo) y src/test/resources/features/ (repo del back)
[ ] .openapi.yaml idéntico en openapi/ (este repo) y src/main/resources/openapi/ (repo del back)
[ ] branch/PR nombrados con el código del CU (ver CONTRIBUTING.md del monorepo)
[ ] Escaneo de SonarQube corrido sobre el front sin issues nuevos bloqueantes ni caída del Quality Gate
```

### Actualizar el escaneo de SonarQube (front)

El servidor de SonarQube (servicio `sonarqube` del `docker-compose.yml` del monorepo `siip`) debe estar arriba — ver la sección "SonarQube local" de `REFERENCE.md` del monorepo para el arranque y el token. Los detalles del análisis del front están en [docs/desarrollo.md § Análisis estático](./docs/desarrollo.md#análisis-estático-sonarqube).

Desde la raíz de este repositorio:

```
npm run generate:api
$env:SONAR_HOST_URL = "http://localhost:9000"   # sin esto el análisis va al servidor institucional
npm run sonar
```

Entrá a http://localhost:9000 y revisá el dashboard del proyecto `dgicp-siip2-frontend-ui`: si el Quality Gate queda en rojo o aparecen issues **New Code** (bugs, vulnerabilidades, code smells bloqueantes) en las líneas que agregaste, resolvelos antes de pedir revisión — no hace falta salir a cero en deuda técnica preexistente, solo en lo que tu PR introduce. La configuración del análisis está en [sonar-project.properties](./sonar-project.properties).
