# =============================================================================
# Dockerfile del front (React + Vite)
# Basado en la plantilla react-simple (Backstage)
#
# NOTA: la aplicacion ya viene compilada. En la entidad la compila el pipeline
#       (Tekton); en local, el servicio front-build de docker-compose.yml
#       (docker compose run --rm front-build). Este Dockerfile solo empaqueta el
#       dist/ resultante en Apache HTTPD.
# =============================================================================

# Base fijada por DIGEST, que es lo que exige el perfil SecDocker (docker:S7023): el
# build es reproducible y se sabe exactamente que imagen se desplego. Solo va el digest
# (tag + digest a la vez lo marca docker:S8431); el tag de origen queda aqui como
# referencia legible. Para recoger un parche de Red Hat hay que actualizar el digest a
# proposito (CR): es el mismo criterio que "subir de version" en imagenes-base/README.md.
# Digest tomado el 2026-09-10 del indice multi-arch de
# registry.access.redhat.com/ubi9/httpd-24:1 (tag inmutable 1-1769458844).
# Los frontends NO llevan imagen propia del marco: sirven estaticos y no abren TLS
# saliente (ver imagenes-base/README.md).
FROM registry.access.redhat.com/ubi9/httpd-24@sha256:cc496c2eb6d487c54cc48a0d304c02fba87460e1133c844bb2d33e9c3c65c076

# Etiquetas estandar OCI: permiten responder "que hay desplegado y de donde salio" con
# una consulta en vez de abriendo pods. "maintainer" la exige SecDocker (docker:S7028).
LABEL maintainer="Ministerio de Hacienda de El Salvador / dgicp" \
      org.opencontainers.image.title="frontend-ui" \
      org.opencontainers.image.description="componente para interfaz de usuario de sistema información de inversión publica" \
      org.opencontainers.image.vendor="Ministerio de Hacienda de El Salvador" \
      org.opencontainers.image.licenses="Proprietary" \
      org.opencontainers.image.source="ssh://almsrv2.mh.gob.sv:29419/dgicp-siip2/frontend-ui" \
      mh.direccion="dgicp" \
      mh.aplicacion="dgicp-siip2" \
      mh.stack="react"

# URL interna de api-gateway. httpd.conf reenvia /auth/** y /back/** ahi (same-origin,
# api-gateway no expone CORS). httpd la lee del entorno al arrancar: se sobrescribe
# desde el ConfigMap/Deployment o desde docker-compose.yml.
ENV GATEWAY_URL=http://api-gateway:8080

# Configuracion de HTTPD para la SPA: fallback a index.html, proxy al gateway,
# cabeceras de seguridad y cache.
COPY httpd.conf /etc/httpd/conf.d/react.conf

# autoindex.conf declara `Alias /icons/ /usr/share/httpd/icons/` (iconos de listados de
# directorio de Apache) y tapa public/icons/ del front: todo /icons/*.png daba 404.
# welcome.conf trae la pagina de bienvenida de Apache y sus propios Alias. Ninguno de
# los dos tiene uso en una SPA.
RUN rm -f /etc/httpd/conf.d/ssl.conf /etc/httpd/conf.d/autoindex.conf /etc/httpd/conf.d/welcome.conf

# Aplicacion precompilada (Vite deja la salida en dist/).
COPY dist/ /var/www/html

# Permisos compatibles con OpenShift (UID arbitrario, grupo 0).
USER root
RUN chown -R 1001:0 /var/www/html && \
    chmod -R g=u /var/www/html
USER 1001

# Puerto por defecto de HTTPD en UBI
EXPOSE 8080

CMD ["httpd", "-D", "FOREGROUND"]
