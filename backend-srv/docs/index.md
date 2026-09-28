# dgicp-siip2-backend-srv

Backend único del Sistema de Información de Inversión Pública (SIIP) del
Ministerio de Hacienda: catálogos administrativos, calendario,
usuarios/roles/permisos, gestión de proyectos, procesos de preinversión y
motor de workflow (Flowable BPM).

Lo invoca solo `api-gateway`, que autentica al usuario contra Keycloak y le
pasa su identidad en el header `X-Usuario`. `backend-srv` no tiene seguridad
propia: no valida tokens y no debe exponerse directamente.

Su configuración por ambiente (base de datos, esquemas, logging, recursos) no
vive en este repositorio sino en `dgicp-siip2/backend-srv-config`.

| Página | Para qué sirve |
|---|---|
| [Arquitectura](architecture.md) | Paquetes por dominio y su mapeo frente a las capas de la plantilla. |
| [Desarrollo](desarrollo.md) | Contratos OpenAPI, pruebas (unitarias y BDD), Flowable, auditoría y SonarQube. |
| [Configuración y ambientes](configuracion.md) | Variables de entorno, perfiles y cómo se configura cada ambiente desde `backend-srv-config`. |
