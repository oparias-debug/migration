# dgicp-siip2-frontend-ui

Interfaz de usuario del Sistema de Información de Inversión Pública (SIIP) del
Ministerio de Hacienda: login, bandeja y registro de proyectos de preinversión,
programación (PAP), banco de proyectos y catálogos administrativos.

Es una SPA en **React 18 + Vite (TypeScript)**. No habla con ningún servicio
directamente: todas sus llamadas van a `/auth/**` y `/back/**` de su propio
origen, y el Apache HTTPD que la sirve (el mismo en la entidad y en local)
las reenvía a `api-gateway`, que a su vez autentica contra Keycloak y enruta a
`back`.

`front` es un módulo del monorepo `siip`, junto con `back`, `api-gateway`,
`keycloak` y `postgresql`. **No** se construyó sobre la plantilla institucional
Next.js (`react-simple`): de ella se adoptaron el `Dockerfile`, las cabeceras de
seguridad de `httpd.conf`, la configuración de Sonar y de stylelint, adaptados al
stack real.

Esta página documenta la **arquitectura interna** del componente. Para todo lo
operativo, la documentación ya existe en la raíz del monorepo y no se duplica acá:

| Documento | Para qué sirve |
|---|---|
| [../README.md](../README.md) | Comandos del front: desarrollo, build, pruebas, Sonar, contenedor. |
| [../../README.md](../../README.md) | Qué es el sistema completo y cómo están armados los módulos. |
| [../../SETUP.md](../../SETUP.md) | Levantar el stack: requisitos, variables de entorno, build, despliegue, accesos. |
| [../../REFERENCE.md](../../REFERENCE.md) | Generación del cliente desde OpenAPI, estructura del front, organización de pruebas. |
| [../../CONTRIBUTING-front.md](../../CONTRIBUTING-front.md) | Pasos del lado front para implementar un caso de uso (CU). |
| [README.md § Pendientes](../README.md#pendientes--por-revisar) | Brechas conocidas frente a la plantilla institucional (pipeline, Sonar, lint, CSP). |

Ver [architecture.md](architecture.md) para el detalle de carpetas, autenticación,
llamadas al back y el mapeo frente a los temas de la plantilla `react-simple`.
