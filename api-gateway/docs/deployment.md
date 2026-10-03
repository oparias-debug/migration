# Despliegue

## Imagen

`Dockerfile` usa la imagen base de la entidad
(`quay-quay-quay.apps.ocp-hub-3t.mh.gob.sv/mh-usi-ubi/openjdk-21-runtime-mh:21`), corre como el
usuario `185` y copia `target/app.jar`: el ciclo compila con Maven antes de construir la imagen.
La imagen se publica en `mh-dgicp-siip2/api-gateway`.

## Chart

`api-gateway-config` (derivado de `admin-srv-config`). A diferencia de los servicios, tiene la
Route habilitada: el gateway es el único punto de entrada público. Los probes usan
`/actuator/health/liveness` y `/actuator/health/readiness`.

## Pendientes

- Crear los repositorios Gerrit `api-gateway` y `api-gateway-config` y su ApplicationSet (P-05).
- El gateway vive en `dgicp-siip2`, con backend-srv, siipsafi-srv y frontend-ui; admin-srv está
  en `dgicp-siip`, otro namespace: confirmar con infra la URL y la NetworkPolicy entre ambos.
- Cliente confidencial del gateway en el realm `MHINTERNO` (P-06).
- NetworkPolicy de los servicios que admita solo al gateway (P-03).
