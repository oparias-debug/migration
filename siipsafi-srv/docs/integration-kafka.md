# Integración con Apache Kafka

## Visión General

| Aspecto | Detalle |
|---------|---------|
| **Plataforma** | Red Hat AMQ Streams (Apache Kafka en OpenShift) |
| **Librería** | `spring-kafka` (Spring for Apache Kafka) |
| **Serialización** | JSON con `JsonSerializer` / `JsonDeserializer` |
| **Patrón** | Event-Driven Architecture + CQRS |



## Dependencia Maven

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka-test</artifactId>
    <scope>test</scope>
</dependency>
```



## Configuración

### application.yml

```yaml
spring:
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:kafka-cluster-kafka-bootstrap:9092}
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      acks: all
      retries: 3
      properties:
        enable.idempotence: true
        max.in.flight.requests.per.connection: 1
    consumer:
      group-id: ${KAFKA_CONSUMER_GROUP:mi-aplicacion-group}
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring.json.trusted.packages: "sv.gob.mh.domain.event"
    # TLS (si AMQ Streams está configurado con TLS)
    ssl:
      trust-store-location: ${KAFKA_TRUSTSTORE_LOCATION:}
      trust-store-password: ${KAFKA_TRUSTSTORE_PASSWORD:}
      protocol: TLSv1.2
```



## Productor (Publisher)

### Evento de Dominio

```java
package sv.gob.mh.domain.event;

import java.time.Instant;

public record ProductoCreadoEvent(
    Long productoId,
    String nombre,
    double precio,
    String creadoPor,
    Instant timestamp
) {
    public static ProductoCreadoEvent of(Long id, String nombre, double precio, String usuario) {
        return new ProductoCreadoEvent(id, nombre, precio, usuario, Instant.now());
    }
}
```

### Servicio Productor

```java
package sv.gob.mh.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import sv.gob.mh.domain.event.ProductoCreadoEvent;

import java.util.concurrent.CompletableFuture;

@Service
public class ProductoEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ProductoEventPublisher.class);
    private static final String TOPIC = "producto-eventos";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ProductoEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicarProductoCreado(ProductoCreadoEvent event) {
        String key = String.valueOf(event.productoId());

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(TOPIC, key, event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Error publicando evento para producto {}: {}",
                        event.productoId(), ex.getMessage());
            } else {
                log.info("Evento publicado: topic={}, partition={}, offset={}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
```

### Integrar en Handler

```java
@Service
@Transactional
public class CrearProductoHandler {

    private final ProductoRepository repository;
    private final ProductoMapper mapper;
    private final ProductoEventPublisher eventPublisher;

    public CrearProductoHandler(ProductoRepository repository,
                                 ProductoMapper mapper,
                                 ProductoEventPublisher eventPublisher) {
        this.repository = repository;
        this.mapper = mapper;
        this.eventPublisher = eventPublisher;
    }

    public ProductoResponseDTO ejecutar(CrearProductoRequestDTO request, String usuario) {
        var producto = Producto.crear(request.nombre(), request.precio(), usuario);
        var saved = repository.save(producto);

        // Publicar evento después de persistir
        eventPublisher.publicarProductoCreado(
                ProductoCreadoEvent.of(saved.getId(), saved.getNombre(),
                        saved.getPrecio(), usuario));

        return mapper.toResponseDTO(saved);
    }
}
```



## Consumidor (Subscriber)

### Listener

```java
package sv.gob.mh.infrastructure.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import sv.gob.mh.domain.event.ProductoCreadoEvent;

@Component
public class ProductoEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProductoEventConsumer.class);

    @KafkaListener(
        topics = "producto-eventos",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void onProductoCreado(
            @Payload ProductoCreadoEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        log.info("Evento recibido: topic={}, partition={}, offset={}, productoId={}",
                topic, partition, offset, event.productoId());

        try {
            // Procesar evento
            procesarEvento(event);
        } catch (Exception e) {
            log.error("Error procesando evento: {}", e.getMessage(), e);
            // DLT (Dead Letter Topic) se maneja automáticamente si está configurado
            throw e;
        }
    }

    private void procesarEvento(ProductoCreadoEvent event) {
        // Lógica de procesamiento: notificaciones, índices, etc.
        log.info("Procesando producto creado: {} - {}", event.productoId(), event.nombre());
    }
}
```

### Configuración Avanzada del Consumer

```java
package sv.gob.mh.infrastructure.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object>
            kafkaListenerContainerFactory(ConsumerFactory<String, Object> consumerFactory) {

        var factory = new ConcurrentKafkaListenerContainerFactory<String, Object>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(3);  // Particiones paralelas
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        // Reintentar 3 veces con 1 segundo entre intentos
        factory.setCommonErrorHandler(
                new DefaultErrorHandler(new FixedBackOff(1000L, 3)));

        return factory;
    }
}
```



## Tópicos

### Convención de Nombres

```
{dominio}-{recurso}-{accion}
```

| Tópico | Propósito | Particiones |
|--------|-----------|-------------|
| `producto-eventos` | Eventos CRUD de productos | 3 |
| `producto-notificaciones` | Notificaciones a usuarios | 3 |
| `auditoria-eventos` | Eventos de auditoría | 6 |

### Crear Tópicos (AMQ Streams)

```yaml
apiVersion: kafka.strimzi.io/v1beta2
kind: KafkaTopic
metadata:
  name: producto-eventos
  labels:
    strimzi.io/cluster: kafka-cluster
spec:
  partitions: 3
  replicas: 3
  config:
    retention.ms: 604800000      # 7 días
    cleanup.policy: delete
    min.insync.replicas: 2
```



## Dead Letter Topic (DLT)

### Configuración Automática

```java
@Bean
public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> template) {
    var recoverer = new DeadLetterPublishingRecoverer(template,
            (record, ex) -> new TopicPartition(record.topic() + ".DLT", record.partition()));

    return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3));
}
```

Los mensajes que fallan después de 3 reintentos se envían a `{topic}.DLT`.



## Testing

### Test con `@EmbeddedKafka`

```java
@SpringBootTest
@EmbeddedKafka(
    partitions = 1,
    topics = {"producto-eventos"},
    brokerProperties = {"listeners=PLAINTEXT://localhost:9092"})
@ActiveProfiles("test")
class ProductoEventPublisherTest {

    @Autowired
    private ProductoEventPublisher publisher;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    @DisplayName("debe publicar evento de producto creado")
    void debePublicarEvento() {
        var event = ProductoCreadoEvent.of(1L, "Test", 99.99, "admin");

        assertDoesNotThrow(() -> publisher.publicarProductoCreado(event));
    }
}
```



## Comparación Quarkus ↔ Spring Boot

| Aspecto | Quarkus | Spring Boot |
|---------|---------|-------------|
| Librería | SmallRye Reactive Messaging | `spring-kafka` |
| Anotación productor | `@Outgoing("channel")` | `KafkaTemplate.send()` |
| Anotación consumidor | `@Incoming("channel")` | `@KafkaListener` |
| Configuración | `mp.messaging.outgoing.*` | `spring.kafka.*` |
| Serialización | SmallRye Serializer | `JsonSerializer`/`JsonDeserializer` |
| Reactive | Mutiny `Uni<T>` / `Multi<T>` | `CompletableFuture` |
| Testing | `@QuarkusTest` + SmallRye InMemory | `@EmbeddedKafka` |
| DLT | Manual channel config | `DeadLetterPublishingRecoverer` |



## Buenas Prácticas

| Práctica | Descripción |
|----------|-------------|
| **Idempotencia** | `enable.idempotence=true` en productor |
| **Key por entidad** | Usar ID de la entidad como key del mensaje |
| **JSON events** | Usar records como DTOs de eventos |
| **DLT** | Configurar Dead Letter Topic para errores |
| **Retry limitado** | Máximo 3 reintentos antes de DLT |
| **Consumer group** | Nombre único por microservicio |
| **Particiones = Concurrency** | Concurrencia del consumer ≤ particiones |
