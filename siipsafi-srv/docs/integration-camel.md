# Integración con Apache Camel

## Visión General

| Aspecto | Detalle |
|---------|---------|
| **Framework** | Apache Camel 4.x con Spring Boot Starter |
| **Propósito** | Integración entre sistemas, ruteo, transformación |
| **Patrón** | Enterprise Integration Patterns (EIP) |
| **Alternativa** | Spring Integration (nativo de Spring) |

> **Nota**: El esqueleto base no incluye Apache Camel. Esta guía documenta cómo integrarlo cuando se requiera comunicación compleja entre sistemas (transformación de datos, protocolos múltiples, rutas condicionales).



## Dependencias Maven

```xml
<!-- Camel Spring Boot Starter -->
<dependency>
    <groupId>org.apache.camel.springboot</groupId>
    <artifactId>camel-spring-boot-starter</artifactId>
    <version>4.4.0</version>
</dependency>

<!-- Componentes adicionales según necesidad -->
<dependency>
    <groupId>org.apache.camel.springboot</groupId>
    <artifactId>camel-http-starter</artifactId>
    <version>4.4.0</version>
</dependency>
<dependency>
    <groupId>org.apache.camel.springboot</groupId>
    <artifactId>camel-jackson-starter</artifactId>
    <version>4.4.0</version>
</dependency>
<dependency>
    <groupId>org.apache.camel.springboot</groupId>
    <artifactId>camel-kafka-starter</artifactId>
    <version>4.4.0</version>
</dependency>

<!-- Testing -->
<dependency>
    <groupId>org.apache.camel</groupId>
    <artifactId>camel-test-spring-junit5</artifactId>
    <version>4.4.0</version>
    <scope>test</scope>
</dependency>
```



## Configuración

### application.yml

```yaml
camel:
  springboot:
    name: mi-aplicacion-camel
    main-run-controller: true
  component:
    http:
      connect-timeout: 5000
      socket-timeout: 10000
```



## Rutas Camel

### Ruta REST → Transformación → Servicio

```java
package sv.gob.mh.infrastructure.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class ProductoRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // Manejo global de errores
        onException(Exception.class)
                .handled(true)
                .log("Error en ruta: ${exception.message}")
                .setHeader("CamelHttpResponseCode", constant(500))
                .setBody(constant("{\"error\": \"Error interno en integración\"}"));

        // Ruta: Recibir evento de Kafka → Transformar → Enviar a servicio externo
        from("kafka:producto-eventos?brokers={{kafka.bootstrap.servers}}")
                .routeId("producto-evento-route")
                .log("Evento recibido: ${body}")
                .unmarshal().json(ProductoCreadoEvent.class)
                .process(exchange -> {
                    var event = exchange.getIn().getBody(ProductoCreadoEvent.class);
                    // Transformar a formato del servicio externo
                    var payload = Map.of(
                            "id", event.productoId(),
                            "name", event.nombre(),
                            "timestamp", event.timestamp().toString()
                    );
                    exchange.getIn().setBody(payload);
                })
                .marshal().json()
                .setHeader("Content-Type", constant("application/json"))
                .to("http://inventario-service:8080/api/v1/sync")
                .log("Sincronización completada: ${body}");
    }
}
```

### Ruta con Content-Based Router

```java
@Component
public class NotificacionRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:notificar")
                .routeId("notificacion-route")
                .choice()
                    .when(header("tipoNotificacion").isEqualTo("EMAIL"))
                        .to("direct:enviarEmail")
                    .when(header("tipoNotificacion").isEqualTo("SMS"))
                        .to("direct:enviarSms")
                    .otherwise()
                        .log("Tipo de notificación desconocido: ${header.tipoNotificacion}")
                .end();

        from("direct:enviarEmail")
                .routeId("email-route")
                .marshal().json()
                .setHeader("Content-Type", constant("application/json"))
                .to("http://email-service:8080/api/v1/send")
                .log("Email enviado: ${body}");

        from("direct:enviarSms")
                .routeId("sms-route")
                .marshal().json()
                .setHeader("Content-Type", constant("application/json"))
                .to("http://sms-service:8080/api/v1/send")
                .log("SMS enviado: ${body}");
    }
}
```

### Ruta con Timer (Polling)

```java
@Component
public class SincronizacionRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // Cada 5 minutos sincronizar catálogos
        from("timer:sync-catalogos?period=300000")
                .routeId("sync-catalogos-route")
                .log("Iniciando sincronización de catálogos")
                .to("http://catalogo-service:8080/api/v1/catalogos?httpMethod=GET")
                .unmarshal().json()
                .split(body())
                    .to("direct:procesarCatalogo")
                .end()
                .log("Sincronización completada");
    }
}
```



## Invocar Rutas desde Servicios Spring

### Usar ProducerTemplate

```java
@Service
public class IntegracionService {

    private final ProducerTemplate producerTemplate;

    public IntegracionService(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    public void notificar(String tipo, NotificacionDTO notificacion) {
        producerTemplate.sendBodyAndHeader(
                "direct:notificar",
                notificacion,
                "tipoNotificacion", tipo);
    }

    @SuppressWarnings("unchecked")
    public <T> T llamarServicio(String endpoint, Object body, Class<T> responseType) {
        return producerTemplate.requestBody(
                "http://" + endpoint,
                body,
                responseType);
    }
}
```



## Patrones EIP Comunes

| Patrón | Uso | Ejemplo Camel |
|--------|-----|---------------|
| **Content-Based Router** | Dirigir mensajes según contenido | `.choice().when(...)` |
| **Splitter** | Dividir mensaje en partes | `.split(body())` |
| **Aggregator** | Combinar mensajes | `.aggregate(header("id"))` |
| **Wire Tap** | Copiar mensaje a otro destino | `.wireTap("direct:audit")` |
| **Dead Letter Channel** | Manejo de errores | `.errorHandler(deadLetterChannel(...))` |
| **Throttle** | Limitar throughput | `.throttle(10).timePeriodMillis(1000)` |



## Testing

```java
@CamelSpringBootTest
@SpringBootTest
@ActiveProfiles("test")
class ProductoRouteTest {

    @Autowired
    private ProducerTemplate producerTemplate;

    @EndpointInject("mock:result")
    private MockEndpoint mockEndpoint;

    @Test
    @DisplayName("debe procesar evento de producto")
    void debeProcesarEvento() throws Exception {
        mockEndpoint.expectedMessageCount(1);

        var event = new ProductoCreadoEvent(1L, "Test", 99.99, "admin", Instant.now());
        producerTemplate.sendBody("direct:test-producto", event);

        mockEndpoint.assertIsSatisfied();
    }
}
```



## ¿Cuándo Usar Camel vs HttpClient?

| Escenario | Recomendación |
|-----------|---------------|
| Llamada REST simple | `HttpClient` JDK o `RestClient` |
| Transformación + ruteo | Apache Camel |
| Múltiples protocolos (FTP, JMS, AMQP) | Apache Camel |
| Polling periódico | Apache Camel (Timer) |
| Event-driven simple | Spring Kafka directamente |
| Integración compleja multi-sistema | Apache Camel |



## Comparación Quarkus ↔ Spring Boot

| Aspecto | Quarkus | Spring Boot |
|---------|---------|-------------|
| Extension | `quarkus-camel-*` | `camel-spring-boot-starter` |
| Config | `camel.component.*` en application.properties | `camel.springboot.*` en application.yml |
| Route class | `RouteBuilder` (CDI bean) | `RouteBuilder` (`@Component`) |
| Testing | `@QuarkusTest` + `CamelQuarkusTestSupport` | `@CamelSpringBootTest` |
| Reactive | Camel + Mutiny | Camel standard (blocking) |
| Native | Full support Quarkus-Camel | Limitado (GraalVM hints) |



## Buenas Prácticas

| Práctica | Descripción |
|----------|-------------|
| **Route IDs** | Asignar `routeId()` descriptivo a cada ruta |
| **Error handler** | `onException()` global + DLC para errores |
| **Idempotencia** | Usar `idempotentConsumer` para evitar duplicados |
| **Log en cada paso** | `.log()` para trazabilidad |
| **Componentes separados** | Una clase `RouteBuilder` por dominio |
| **No mezclar** | No usar Camel para llamadas REST simples |
