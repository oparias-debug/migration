# Compilación Nativa y Optimización

## Visión General

| Aspecto | Detalle |
|---------|---------|
| **JVM** | OpenJDK 21 (default) |
| **Native** | Spring AOT + GraalVM (opcional) |
| **AOT** | Ahead-of-Time Processing en Spring Boot 3.x |
| **Base Image** | UBI9 OpenJDK 21 Runtime |

> Spring Boot 3.x soporta compilación nativa con GraalVM, pero en producción se recomienda **JVM** por estabilidad y compatibilidad con las bibliotecas del esqueleto (Hibernate, Spring Security, etc.).



## Optimización JVM (Recomendado)

### Opciones JVM para Producción

```bash
JAVA_OPTS="\
  -Xms512m \
  -Xmx1536m \
  -XX:+UseG1GC \
  -XX:+ExitOnOutOfMemoryError \
  -XX:MaxGCPauseMillis=200 \
  -XX:+UseStringDeduplication \
  -XX:+OptimizeStringConcat \
  -Djava.security.egd=file:/dev/urandom \
  -Dfile.encoding=UTF-8 \
  -Duser.timezone=America/El_Salvador"
```

### Opciones por Ambiente

| Ambiente | JAVA_OPTS |
|----------|-----------|
| DEV | `-Xms128m -Xmx384m -XX:+UseG1GC` |
| TEST | `-Xms256m -Xmx768m -XX:+UseG1GC` |
| QA | `-Xms256m -Xmx768m -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError` |
| PRD | `-Xms512m -Xmx1536m -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError -XX:MaxGCPauseMillis=200` |



## Spring AOT (Ahead-of-Time)

### Qué es Spring AOT

Spring AOT genera código optimizado en tiempo de compilación:
- Pre-computa la configuración del ApplicationContext
- Genera proxies en tiempo de build (no reflexión)
- Reduce tiempo de arranque

### Configuración Maven

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <!-- Habilitar procesamiento AOT -->
        <jvmArguments>-Dspring.aot.enabled=true</jvmArguments>
    </configuration>
    <executions>
        <execution>
            <id>process-aot</id>
            <goals>
                <goal>process-aot</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Build con AOT

```bash
# Build con procesamiento AOT
./mvnw clean package -DskipTests -Dspring.aot.enabled=true

# Ejecutar con AOT
java -Dspring.aot.enabled=true -jar target/mi-aplicacion.jar
```



## Compilación Nativa con GraalVM

### Pre-requisitos

- GraalVM JDK 21
- `native-image` instalado (`gu install native-image`)
- Tiempo de compilación: ~5-15 minutos

### Dependencia

```xml
<dependency>
    <groupId>org.graalvm.buildtools</groupId>
    <artifactId>native-maven-plugin</artifactId>
</dependency>
```

### Build Nativo

```bash
# Build imagen nativa
./mvnw -Pnative native:compile -DskipTests

# Ejecutar binario nativo
./target/mi-aplicacion
```

### Dockerfile Multi-Stage (Nativo)

```dockerfile
# ── Stage 1: Build nativo ───────────────────────────────────
FROM ghcr.io/graalvm/native-image-community:21 AS builder

WORKDIR /build
COPY pom.xml mvnw ./
COPY .mvn .mvn
COPY src src

RUN chmod +x mvnw && \
    ./mvnw -Pnative native:compile -DskipTests -B

# ── Stage 2: Runtime mínimo ─────────────────────────────────
FROM registry.access.redhat.com/ubi9/ubi-minimal:latest

WORKDIR /app
COPY --from=builder /build/target/mi-aplicacion ./app

EXPOSE 8080

ENTRYPOINT ["./app"]
```

### Beneficios vs Limitaciones

| Beneficio | Detalle |
|-----------|---------|
| ✅ Startup ~100ms | vs ~3-8s en JVM |
| ✅ Memoria ~50-100MB RSS | vs ~200-350MB en JVM |
| ✅ Imagen más pequeña | Sin JVM runtime |

| Limitación | Detalle |
|------------|---------|
| ❌ Reflexión limitada | Requiere hints para Hibernate, JPA |
| ❌ Build lento | ~5-15 min vs ~30s en JVM |
| ❌ Proxies dinámicos | Incompatible con algunos patterns |
| ❌ Throughput menor | JIT compiler no disponible |
| ❌ Debugging difícil | Sin herramientas JVM |



## GraalVM Hints (Reflexión)

Para que la compilación nativa funcione con Hibernate y Spring Security:

```java
@RegisterReflectionForBinding({
    Producto.class,
    ProductoResponseDTO.class,
    CrearProductoRequestDTO.class,
    ErrorResponse.class
})
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### `reflect-config.json`

```json
[
  {
    "name": "sv.gob.mh.infrastructure.persistence.entity.ProductoEntity",
    "allDeclaredFields": true,
    "allDeclaredMethods": true,
    "allDeclaredConstructors": true
  }
]
```



## Optimización Docker Multi-Stage (JVM)

### Layers Extraction (más rápido rebuild)

```dockerfile
# ── Stage 1: Build ──────────────────────────────────────────
FROM registry.access.redhat.com/ubi9/openjdk-21:latest AS builder
WORKDIR /build
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B
COPY src src
RUN ./mvnw package -DskipTests -B && \
    java -Djarmode=tools -jar target/*.jar extract --layers --launcher --destination extracted

# ── Stage 2: Runtime (layered) ──────────────────────────────
FROM registry.access.redhat.com/ubi9/openjdk-21-runtime:latest
WORKDIR /deployments

# Copiar por capas (Docker cache friendly)
COPY --from=builder /build/extracted/dependencies/ ./
COPY --from=builder /build/extracted/spring-boot-loader/ ./
COPY --from=builder /build/extracted/snapshot-dependencies/ ./
COPY --from=builder /build/extracted/application/ ./

USER 1001
EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
```

### Spring Boot CDS (Class Data Sharing)

```bash
# Generar CDS archive (reduce startup ~20-30%)
java -Dspring.context.exit=onRefresh -XX:ArchiveClassesAtExit=app.jsa -jar app.jar
java -XX:SharedArchiveFile=app.jsa -jar app.jar
```



## Comparación JVM vs Native

| Aspecto | JVM | Native |
|---------|-----|--------|
| Startup | 3-8s | ~100ms |
| Peak throughput | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ |
| RSS Memory | 200-350MB | 50-100MB |
| Build time | ~30s | ~5-15min |
| Debugging | ⭐⭐⭐⭐⭐ | ⭐⭐ |
| Reflection | Completo | Limitado (hints) |
| Hibernate | Full | Con hints |
| Spring Security | Full | Con hints |
| **Recomendado** | ✅ Producción | Serverless / edge |



## Comparación Quarkus ↔ Spring Boot

| Aspecto | Quarkus | Spring Boot |
|---------|---------|-------------|
| Native support | Nativo de la plataforma | Spring AOT + GraalVM |
| Build nativo | `./mvnw package -Dnative` | `./mvnw -Pnative native:compile` |
| Docker nativo | `quarkus-micro-image` | `ubi-minimal` |
| Dev mode | `quarkus:dev` (hot reload) | `spring-boot:run` + DevTools |
| AOT | Build-time CDI | Spring AOT Processing |
| CDS | N/A | `AppCDS` para JVM |
| Startup (JVM) | ~1-2s | ~3-8s |
| Startup (native) | ~50ms | ~100ms |



## Recomendación

Para el contexto de MH con Oracle DB + Spring Security + Hibernate:

> **Usar JVM (OpenJDK 21)** con optimización de JVM args y Docker multi-stage layered.
>
> La compilación nativa es una opción futura para microservicios simples (sin Hibernate / JPA complejos).

```
┌─────────────────────────────────────────────┐
│  Recomendado: JVM + G1GC + Docker layers     │
│  ✅ Estabilidad completa                      │
│  ✅ Debugging y profiling                     │
│  ✅ Compatible con todas las librerías        │
│  ✅ Build rápido (~30s)                       │
└─────────────────────────────────────────────┘
```
