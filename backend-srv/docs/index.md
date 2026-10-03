# dgicp-siip2-backend-srv

Backend único del Sistema de Información de Inversión Pública (SIIP) del
Ministerio de Hacienda: catálogos administrativos, calendario,
usuarios/roles/permisos, gestión de proyectos y procesos de preinversión.

Lo invoca `api-gateway`, que autentica al usuario contra Keycloak y le reenvía
el token. `backend-srv` valida ese JWT por su cuenta (`security/SecurityConfig`)
y toma el usuario de su `preferred_username`; el rol de negocio sale de
`USUARIO.ROL`.

Su configuración por ambiente (base de datos, esquemas, logging, recursos) no
vive en este repositorio sino en `dgicp-siip2/backend-srv-config`.

| Página | Para qué sirve |
|---|---|
| [Arquitectura](architecture.md) | Paquetes por dominio y su mapeo frente a las capas de la plantilla. |
| [Desarrollo](desarrollo.md) | Contratos OpenAPI, pruebas (unitarias y BDD), ciclo de vida del proyecto, auditoría y SonarQube. |
| [Configuración y ambientes](configuracion.md) | Variables de entorno, perfiles y cómo se configura cada ambiente desde `backend-srv-config`. |
