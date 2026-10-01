# admin-srv — Documentación técnica

!!! info "Información del servicio"
    **Proyecto**: dgicp-siip
    **Componente**: admin-srv
    **Dirección**: dgicp
    **Tecnología**: Spring Boot 3.5 / Java 21
    **Namespace de desarrollo**: `mh-dev-dgicp-siip`

## Descripción del servicio

Servicio de Administración

### Responsabilidades principales

<!-- Completar con las responsabilidades específicas del servicio -->
- [ ] Responsabilidad 1: _describir la función principal_
- [ ] Responsabilidad 2: _describir la función secundaria_

### Endpoints principales

| Endpoint | Descripción | Seguridad |
|----------|-------------|-----------|
| `/api/v1/catalogos/**` | CU-ADM-01, Administración de Catálogos | JWT + rol `ADMINISTRADOR` o `ADMINISTRADOR_DE_CATALOGOS` |
| `/api/v1/calendarios/**` | CU-ADM-04, Gestión de Calendario | JWT + rol `ADMINISTRADOR` o `ADMINISTRADOR_CALENDARIO` |

El detalle de cada operación está en el contrato, [`api/openapi.yaml`](../api/openapi.yaml).

---

## La ruta de adopción

Esta documentación está ordenada como la recorre quien toma el servicio por primera vez.
Cada página dice qué hace uno, qué hace la plataforma sola, cómo se comprueba y qué hacer
si falla.

| # | Paso | Página | Qué resuelve |
|---|------|--------|--------------|
| 1 | Creación de la aplicación | [creacion-aplicacion.md](creacion-aplicacion.md) | De dónde salen los nombres de repos, namespace, imagen y cliente OIDC |
| 2 | Arquitectura de la plantilla | [architecture.md](architecture.md) | Cómo está organizado el código que recibiste y dónde va cada cosa |
| 3 | Después de la plantilla | [post-plantilla.md](post-plantilla.md) | Qué quedó creado y lo que hay que hacer antes del primer cambio |
| 4 | Configuración de variables | [helm-configuration.md](helm-configuration.md) | ConfigMap, Secret, overlays por clúster y config-server |
| 5 | Pilares del marco | [framework-library.md](framework-library.md) · [security.md](security.md) · [autorizacion.md](autorizacion.md) · [autorizacion-adopcion.md](autorizacion-adopcion.md) · [audit-logging.md](audit-logging.md) | Configuración externa, logger remoto, autorización por permisos y auditoría |
| 6 | Desarrollo | [desarrollo.md](desarrollo.md) | El ciclo de trabajo con Gerrit y las revisiones automáticas que va a encontrar cada cambio |
| 7 | Ambiente local | [local-development.md](local-development.md) | Correr el servicio en la máquina, directo o en contenedor |
| 8 | Despliegue | [ci-cd-pipeline.md](ci-cd-pipeline.md) · [deployment.md](deployment.md) | Qué pasa desde que se fusiona un cambio hasta que el pod está arriba, y cómo operarlo |
| 9 | Creación de un ambiente | [ambientes.md](ambientes.md) | Cómo nace `qa`, `test`, `preprod` o `master` desde el portal |
| 10 | Pruebas funcionales y de seguridad | [testing.md](testing.md) | Unitarias, Karate, carga, y los controles de seguridad del ciclo |
| 11 | Paso entre ambientes | [promocion.md](promocion.md) | Cómo llega una versión nueva a un ambiente que ya existe |

---

## Arranque rápido

```bash
# 1. Espejo de Maven (una sola vez): ver local-development.md §1.1
# 2. Credenciales de base de datos
export DB_USER="<usuario>"
export DB_PASSWORD="<password>"
# 3. Arrancar con el perfil de desarrollo
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

| Recurso | URL local |
|---------|-----------|
| Aplicación | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui |
| OpenAPI | http://localhost:8080/v3/api-docs |
| Health | http://localhost:8080/actuator/health |

```bash
./mvnw test                          # unitarias
./mvnw verify -Pintegration-tests    # + Karate contra un servicio desplegado
./mvnw package                       # empaqueta target/app.jar
```

---

## Dónde está cada cosa

| Recurso | Ubicación |
|---------|-----------|
| Código | Gerrit, proyecto `dgicp-siip/admin-srv` |
| Chart de despliegue | Gerrit, proyecto `dgicp-siip/admin-srv-config`, carpeta `admin-srv/` |
| Ficha en el Developer Hub | Catálogo → componente `dgicp-siip-admin-srv`. Desde ahí se ven pipelines, ArgoCD, pods y esta documentación |
| Calidad | SonarQube, proyecto `dgicp-siip-admin-srv` |
| Imagen | Quay institucional, repositorio `mh-dgicp-siip/admin-srv` |
| Despliegue | ArgoCD, ApplicationSet `dev-dgicp-siip-admin-srv` |
| Proyecto | Tuleap, proyecto `dgicp-siip` |
