# dgicp-siip2-backend-srv

componente para sistemas de inversion publica

Servicio de backend sobre Spring Boot 3.5 / Java 21, creado desde la plantilla Spring Boot
del Developer Hub del Ministerio de Hacienda.

## Documentación

Está en [`docs/`](docs/index.md) y se publica como TechDocs en la ficha del componente en el
Developer Hub:

- [Arquitectura](docs/architecture.md)
- [Desarrollo](docs/desarrollo.md): contratos OpenAPI, pruebas, Flowable y SonarQube.
- [Configuración y ambientes](docs/configuracion.md): variables de entorno, perfiles y cómo
  se configura cada ambiente.

## Configuración por ambiente

Este repo no guarda valores de ningún ambiente. La configuración de dev, pruebas,
preproducción y producción vive en el repo **`dgicp-siip2/backend-srv-config`** (chart Helm
que despliega ArgoCD): ConfigMap `backend-srv-cmp` para lo no sensible y Secret
`backend-srv-secret` para las credenciales. Detalle en
[docs/configuracion.md](docs/configuracion.md).

## Arranque rápido (local)

Requisitos: JDK 21 y una base de datos PostgreSQL con dos esquemas: el de negocio (`public`) y
`flowable`. Con las variables mínimas:

```bash
export DB_URL="jdbc:postgresql://localhost:5432/preinversiondb"
export DB_DRIVER_CLASS_NAME="org.postgresql.Driver"
export DB_USER="<usuario>"
export DB_PASSWORD="<password>"
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

El perfil `dev` recrea el esquema y siembra datos de prueba en cada arranque; no usarlo
contra una base compartida.

- Aplicación: http://localhost:8081
- Swagger UI: http://localhost:8081/swagger-ui.html
- Health: http://localhost:8081/actuator/health

```bash
./mvnw test       # pruebas unitarias y BDD (Cucumber, H2 en memoria)
./mvnw package    # empaquetar (target/app.jar)
```

## Contribuir

El código entra siempre por la rama `dev` mediante una revisión en Gerrit
(`git push origin HEAD:refs/for/dev`). Cada revisión pasa por compilación, pruebas,
SonarQube, búsqueda de secretos y análisis de dependencias antes de poder fusionarse.
