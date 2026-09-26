# dgicp-siip2-backend-srv

Backend único del Sistema de Información de Inversión Pública (SIIP) del
Ministerio de Hacienda: catálogos administrativos, calendario,
usuarios/roles/permisos, gestión de proyectos, procesos de preinversión y
motor de workflow (Flowable BPM).

`back` es un módulo del monorepo `siip`, junto con `api-gateway`, `front`,
`keycloak` y `postgresql`. No tiene seguridad propia: confía en que solo
`api-gateway` lo invoque, por eso no publica su puerto al host.

Esta página documenta la **arquitectura interna** del componente. Para todo
lo operativo, la documentación ya existe en la raíz del monorepo y no se
duplica acá:

| Documento | Para qué sirve |
|---|---|
| [../README.md](../README.md) | Qué es el sistema completo y cómo están armados los módulos. |
| [../SETUP.md](../SETUP.md) | Levantar el stack: requisitos, variables de entorno, build, despliegue, accesos. |
| [../REFERENCE.md](../REFERENCE.md) | Generación de código desde OpenAPI, estructura del front, organización de pruebas. |
| [../CONTRIBUTING.md](../CONTRIBUTING.md) | Cómo agregar un caso de uso (CU) nuevo, convenciones, checklist de entrega. |
| [../GLOSSARY.md](../GLOSSARY.md) | Glosario de términos de dominio. |
| [README.md § Pendientes](../README.md#pendientes--por-revisar) | Brechas conocidas frente a la plantilla institucional (pilares, Dockerfile, catalog-info, etc.). |

Ver [architecture.md](architecture.md) para el detalle de paquetes y el mapeo
frente a las capas de la plantilla `backend-srv`.
