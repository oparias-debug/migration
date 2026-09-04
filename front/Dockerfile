# --- Stage 1: build ---
FROM node:22-alpine AS build
WORKDIR /app
# openapi-generator-cli (invocado por "npm run build" via el hook "prebuild")
# corre sobre la JVM - hace falta un JRE en la imagen de build para regenerar
# src/api/generated/ en cada build, igual que "mvn generate-sources" en back.
RUN apk add --no-cache openjdk21-jre-headless
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# --- Stage 2: serve ---
FROM nginx:1.27-alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf.template /etc/nginx/templates/nginx.conf.template
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh \
	&& chown -R nginx:nginx /usr/share/nginx/html /etc/nginx /var/cache/nginx /var/run \
	&& touch /var/run/nginx.pid \
	&& chown nginx:nginx /var/run/nginx.pid
EXPOSE 8080
USER nginx
ENTRYPOINT ["/docker-entrypoint.sh"]
