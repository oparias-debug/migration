# 1. Creación de la aplicación

Este servicio nació desde el Developer Hub (**Create → Spring Boot Golden Path Template**). Esta
página explica de dónde salen los nombres que vas a ver en todas partes, para que sepas
buscar tu servicio en Gerrit, ArgoCD, Quay, SonarQube y Keycloak sin adivinar.

---

## 1. Lo que se pidió en el formulario

| Campo | Valor de este servicio | Para qué se usa |
|---|---|---|
| Dirección | `dgicp` | Dueña del componente; prefijo de todos los nombres y portafolio de SonarQube |
| Nombre funcional | `siipsafi-srv` | Título en el catálogo y nombre del cliente OIDC |
| Descripción | `componente para integracion de siip con safi ` | Catálogo, Quay, TechDocs |
| Proyecto en Tuleap | forma `dgicp-siip2` = `<dirección>-<proyecto>` | Agrupa los repositorios de Gerrit y define el namespace |
| Repositorio en Gerrit | `siipsafi-srv` | Nombre del servicio en el clúster |
| Repositorio de Nexus | `DGICP-release` | Dónde publica el CI el artefacto Maven |
| Realm de Keycloak | `MHINTERNO` | Dónde vive el cliente OIDC y contra qué se validan los JWT |

Reglas de nombre que aplica el formulario: minúsculas, números y guion, empieza por letra;
el proyecto Tuleap no repite la dirección al inicio y tiene 24 caracteres como máximo; el
repositorio Gerrit tiene 27 como máximo. El límite existe porque esos nombres se concatenan
en hosts de Route y nombres de release de Helm.

---

## 2. Lo que se derivó de esos valores

Nada de esto se escribe a mano; el scaffolder lo calcula y lo deja en los archivos del
repositorio.

| Recurso | Nombre | Dónde se usa |
|---|---|---|
| Repositorio de código | `dgicp-siip2/siipsafi-srv` | Gerrit |
| Repositorio de despliegue | `dgicp-siip2/siipsafi-srv-config` | Gerrit; lo lee ArgoCD |
| Namespace | `mh-<ambiente>-dgicp-siip2` (en dev: `mh-dev-dgicp-siip2`) | OpenShift |
| Nombre del servicio (`service.name`) | `dgicp-siip2-siipsafi-srv` | `application.properties`; clave en el config-server, en autorización y en auditoría |
| Nombre de los recursos en el clúster | `siipsafi-srv` | Deployment, Service, Route, ConfigMap `-cmp`, Secret `-secret` |
| Host de la Route en dev | `siipsafi-srv-mh-dev-dgicp-siip2.apps.<dominio del clúster>` | Navegador, CORS, pruebas |
| Imagen | `mh-dgicp-siip2/siipsafi-srv` | Quay institucional |
| Proyecto SonarQube | `dgicp-siip2-siipsafi-srv` | Portafolio de la dirección, en mayúsculas |
| Cliente OIDC | `dgicp-siip2` (uno por proyecto Tuleap, compartido por sus servicios) | Realm `MHINTERNO` |
| ApplicationSet de ArgoCD | `<ambiente>-dgicp-siip2-siipsafi-srv` | ArgoCD del hub |
| Componente en el catálogo | `dgicp-siip2-siipsafi-srv` | Developer Hub |
| Componente en el inventario | `dgicp-siip2-siipsafi-srv` | `inventario-service` (config-server) |

---

## 3. Lo que hizo el scaffolder, en orden

1. Creó el proyecto en Tuleap y los dos repositorios en Gerrit.
2. Creó el proyecto en SonarQube dentro del portafolio de la dirección.
3. Creó el repositorio de imágenes en Quay.
4. Clonó este esqueleto, sustituyó los marcadores por los valores del formulario y lo subió a los dos repositorios; añadió los disparadores de CI del servicio al repositorio `tekton`.
5. Creó el ApplicationSet de `dev` en ArgoCD y lo versionó en git.
6. Creó el cliente OIDC en Keycloak con la Route de dev como `redirect_uri`.
7. Registró el componente en el inventario del marco, con su realm y su cliente.
8. Registró el webhook de Gerrit hacia el ciclo de CI de la dirección.
9. Registró `catalog-info.yaml` en el catálogo del Developer Hub.

Si alguno de esos pasos falló, el asistente lo muestra en rojo en su registro. El paso de
crear el volumen de trabajo puede aparecer con aviso y continuar; los demás detienen la
creación.

---

## 4. Cómo comprobar que todo quedó creado

Desde la ficha del componente en el Developer Hub deben verse, sin configurar nada:

- La pestaña de **CI/CD** con el `EventListener` del proyecto y sin PipelineRuns todavía.
- La pestaña de **ArgoCD** con la Application de `dev`.
- La pestaña de **Kubernetes** con el namespace `mh-dev-dgicp-siip2`.
- Esta documentación en **Docs**.

Lo que falta para que el pod arranque está en la página siguiente:
[Después de la plantilla](post-plantilla.md).
