# dgicp-siip2-frontend-ui

Interfaz de usuario del Sistema de Información de Inversión Pública (SIIP) del
Ministerio de Hacienda: login, bandeja y registro de proyectos de preinversión,
programación (PAP), banco de proyectos y catálogos administrativos.

Es una SPA en **React 18 + Vite (TypeScript)**. No habla con ningún servicio
directamente: todas sus llamadas van a `/auth/**` y `/back/**` de su propio
origen, y el Apache HTTPD que la sirve (el mismo en la entidad y en local)
las reenvía a `api-gateway`, que a su vez autentica contra Keycloak y enruta a
`backend-srv`.

**No** se construyó sobre la plantilla institucional Next.js (`react-simple`): de
ella se adoptaron el `Dockerfile`, las cabeceras de seguridad de `httpd.conf`, la
configuración de Sonar y de stylelint, adaptados al stack real.

| Página | Para qué sirve |
|---|---|
| [Arquitectura](architecture.md) | Carpetas, autenticación, llamadas al back y mapeo frente a la plantilla `react-simple`. |
| [Desarrollo](desarrollo.md) | Cliente generado desde OpenAPI, pruebas, especificaciones Gherkin y SonarQube. |
| [README](../README.md) | Comandos: desarrollo, build, pruebas, contenedor. |
| [CONTRIBUTING.md](../CONTRIBUTING.md) | Pasos del lado front para implementar un caso de uso (CU). |
| [README § Pendientes](../README.md#pendientes--por-revisar) | Brechas conocidas frente a la plantilla institucional (pipeline, Sonar, lint, CSP). |

Levantar el stack completo (back, gateway, Keycloak, Postgres) y la visión del
sistema entero están en el monorepo `siip` (`SETUP.md` y `README.md` de su raíz).
