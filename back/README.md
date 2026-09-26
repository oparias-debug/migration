# dgicp-siip2-backend-srv

componente para sistemas de inversion publica

Servicio de backend sobre Spring Boot 3.5 / Java 21, creado desde la plantilla Spring Boot
del Developer Hub del Ministerio de Hacienda. Incluye los pilares del marco DINAFI como
módulos del proyecto: configuración externa, autorización por permisos, auditoría y logger
remoto.

## Documentación

Toda la documentación está en [`docs/`](docs/index.md) y se publica como TechDocs en la ficha
del componente en el Developer Hub. Está ordenada como la ruta que sigue quien toma el
servicio: creación, arquitectura, qué hacer después de la plantilla, variables, pilares,
desarrollo, ambiente local, despliegue, creación de ambientes, pruebas y promoción.

## Arranque rápido

Requisitos: JDK 21 y, recomendado, el espejo de Maven del Nexus institucional en
`~/.m2/settings.xml` (ver [`docs/local-development.md`](docs/local-development.md) §1.1).

```bash
export DB_USER="<usuario>"
export DB_PASSWORD="<password>"
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

- Aplicación: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui
- Health: http://localhost:8080/actuator/health

```bash
./mvnw test                          # pruebas unitarias
./mvnw verify -Pintegration-tests    # pruebas Karate contra un servicio desplegado
./mvnw package                       # empaquetar (target/app.jar)
```

## Contribuir

El código entra siempre por la rama `dev` mediante una revisión en Gerrit
(`git push origin HEAD:refs/for/dev`). Cada revisión pasa por compilación, pruebas,
SonarQube, búsqueda de secretos y análisis de dependencias antes de poder fusionarse.
El detalle está en [`docs/desarrollo.md`](docs/desarrollo.md).
