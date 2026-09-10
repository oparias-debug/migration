# Guía para el frontend de un CU

Esta guía cubre la parte de `front` al implementar un caso de uso (CU). Asume que ya leíste **[CONTRIBUTING.md](./CONTRIBUTING.md)** — ahí están las convenciones compartidas con el back (qué podés tocar, branches/commits, reglas de Gherkin, Definition of Done) y el flujo completo de punta a punta. Esta guía solo detalla los pasos específicos de `front`.

Para levantar el stack en tu máquina, ver **[SETUP.md](./SETUP.md)**. Para la mecánica de generación de código (OpenAPI → TypeScript) y cómo están organizadas las pruebas, ver **[REFERENCE.md](./REFERENCE.md)**.

Mirá `preinversion`/CU-PRE-01 como referencia completa: `front/src/api/preinversionApi.ts`, `front/src/features/preinversion/proyectos/`.

## Pasos

1. **Copiá el mismo `.feature`** (sin modificarlo) a `front/features/`. Es la misma especificación que ya cumplió el back — le sirve al front como guía de qué pantallas/mensajes/validaciones implementar.
2. **Copiá el `.openapi.yaml`** actualizado del back a `front/openapi/<dominio>/CU-XX.openapi.yaml` (idéntico al de `back/src/main/resources/openapi/`).
3. Generá el cliente TypeScript:
   ```
   npm run generate:api
   ```
   (para un dominio nuevo, agregá antes un script `generate:api:<dominio>` en `front/package.json`, análogo al existente, apuntando a `openapi/<dominio>/CU-XX.openapi.yaml` y `-o src/api/generated/<dominio>`).
4. Creá (o extendé) el wrapper `front/src/api/<dominio>Api.ts`: instanciá las clases generadas (una por `tag` del yaml) pasándoles `createHttpClient('/back')`. Reexportá ahí los tipos (`Dto`s) que la UI necesite.
5. Implementá la pantalla/componente en `front/src/features/<dominio>/<caso-de-uso>/`, siguiendo el patrón de `features/preinversion/proyectos/`: `react-hook-form` + un schema `zod` en `<algo>FormSchema.ts`, reutilizando los componentes genéricos de `src/components/form/` (`FormRow`, `DatePickerInput`) y `src/components/table/` (`DataTable`, `Pagination`) donde aplique.
6. Conectá la ruta/menú si hace falta (reemplazando el placeholder "🚧 Página en Construcción" del módulo correspondiente en el sidebar/routing).
7. Escribí los tests junto al componente (`*.test.tsx`, Vitest + React Testing Library), cubriendo al menos el camino feliz y las validaciones descritas en el `.feature`. Si el equipo decide automatizar el `.feature` con `cucumber-js`, los steps van en `front/features/step_definitions/<dominio>/`.
8. Validá todo:
   ```
   npm run lint
   npm run test
   npm run build
   ```

## Qué podés tocar y qué no (específico de front)

✅ Podés tocar:
- Tu wrapper `<dominio>Api.ts` y tu pantalla en `front/src/features/<dominio>/`.
- Tu `.feature`/`.openapi.yaml` (copia idéntica de la del back — ver [CONTRIBUTING.md](./CONTRIBUTING.md)).

🚫 No toques:
- Código generado: `front/src/api/generated/`. Se regenera solo; si lo editás a mano, se pierde en el próximo build.
- El `httpClient.ts` genérico del front — cada wrapper de dominio instancia el cliente generado con `createHttpClient('/back')` propio (ver nota en `preinversionApi.ts`); no reutilices el `httpClient` genérico, porque el cliente generado ignora su `basePath` si el axios que recibe ya trae `baseURL` distinto.

## Puntos que suelen confundir a alguien nuevo

- El `.feature` y el `.openapi.yaml` están **duplicados a propósito** en `back` y `front` — no hay generación cruzada entre módulos ni symlinks. Si editás uno, editá el otro a mano.
- El código generado (cliente TS en `front/src/api/generated/`) **nunca se edita a mano** y **nunca se versiona** — se regenera en cada `npm run generate:api`.

## Definition of Done (front)

Antes de abrir el PR, confirmá:

```
[ ] front: npm run lint pasa
[ ] front: npm run test pasa
[ ] front: npm run build pasa
[ ] .feature idéntico en back/src/test/resources/features/ y front/features/
[ ] .openapi.yaml idéntico en back/src/main/resources/openapi/ y front/openapi/
[ ] branch/PR nombrados con el código del CU (ver CONTRIBUTING.md)
[ ] Escaneo de SonarQube corrido sobre front sin issues nuevos bloqueantes ni caída del Quality Gate
```

### Actualizar el escaneo de SonarQube (front)

El servidor de SonarQube (servicio `sonarqube` en `docker-compose.yml`) debe estar arriba — ver [arranque y token](./REFERENCE.md#análisis-estático-sonarqube) en REFERENCE.md si todavía no lo tenés levantado.

```
cd front
npm run generate:api
npm run sonar
```

Entrá a http://localhost:9000 y revisá el dashboard del proyecto `siip-front`: si el Quality Gate queda en rojo o aparecen issues **New Code** (bugs, vulnerabilidades, code smells bloqueantes) en las líneas que agregaste, resolvelos antes de pedir revisión — no hace falta salir a cero en deuda técnica preexistente, solo en lo que tu PR introduce. Ver [REFERENCE.md](./REFERENCE.md#análisis-estático-sonarqube) para detalles de configuración.
