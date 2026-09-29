# Certificados TLS

## Visión General

| Aspecto | Detalle |
|---------|---------|
| **Protocolo** | TLS 1.2+ obligatorio en producción |
| **Certificados** | Emitidos por CA interna del MH |
| **Gestión JVM** | Java KeyStore (JKS) o PKCS12 |
| **Runtime** | OpenShift Route (edge TLS) + backend trust |



## Arquitectura TLS

```
┌──────────┐  HTTPS  ┌───────────┐  HTTPS  ┌────────────┐
│  Cliente  │────────>│ OpenShift │────────>│ Spring Boot│
│  (Browser/│  TLS    │  Route    │  TLS    │  Pod       │
│   API)    │         │  (Edge)   │  (opt)  │            │
└──────────┘         └───────────┘         └────────────┘
                          │                      │
                          │                      ▼
                          │               ┌────────────┐
                          │               │ Oracle DB  │
                          │               │ (TLS opt)  │
                          │               └────────────┘
                          ▼
                    ┌────────────┐
                    │ Keycloak   │
                    │ (HTTPS)    │
                    └────────────┘
```



## TrustStore para Servicios Externos

Cuando Spring Boot necesita conectarse a servicios con certificados de CA interna:

### Opción 1: TrustStore en application.yml

```yaml
# Para conexiones HTTPS genéricas (HttpClient)
server:
  ssl:
    trust-store: classpath:truststore.p12
    trust-store-password: ${TRUSTSTORE_PASSWORD:changeit}
    trust-store-type: PKCS12
```

### Opción 2: System Properties (JVM)

```bash
java -jar app.jar \
  -Djavax.net.ssl.trustStore=/certs/truststore.p12 \
  -Djavax.net.ssl.trustStorePassword=changeit \
  -Djavax.net.ssl.trustStoreType=PKCS12
```

### Opción 3: Programático (HttpClient)

```java
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;

@Configuration
public class HttpClientConfig {

    @Bean
    public HttpClient httpClient(
            @Value("${app.tls.truststore-path:}") String truststorePath,
            @Value("${app.tls.truststore-password:changeit}") String truststorePassword)
            throws Exception {

        if (truststorePath == null || truststorePath.isBlank()) {
            return HttpClient.newBuilder().build();
        }

        KeyStore trustStore = KeyStore.getInstance("PKCS12");
        try (var is = new FileInputStream(truststorePath)) {
            trustStore.load(is, truststorePassword.toCharArray());
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        return HttpClient.newBuilder()
                .sslContext(sslContext)
                .build();
    }
}
```



## Crear TrustStore con `keytool`

### Importar Certificado de CA

```bash
# Importar certificado de CA interna
keytool -importcert \
  -alias ca-interna-mh \
  -file ca-interna.pem \
  -keystore truststore.p12 \
  -storetype PKCS12 \
  -storepass changeit \
  -noprompt

# Importar certificado de Keycloak
keytool -importcert \
  -alias keycloak-cert \
  -file keycloak.pem \
  -keystore truststore.p12 \
  -storetype PKCS12 \
  -storepass changeit \
  -noprompt

# Verificar contenido
keytool -list -keystore truststore.p12 -storetype PKCS12 -storepass changeit
```

### Convertir PEM a DER (si necesario)

```bash
openssl x509 -in certificate.pem -outform DER -out certificate.der
```



## TLS en Dockerfile

### Importar Certificados al Build

```dockerfile
FROM registry.access.redhat.com/ubi9/openjdk-21-runtime:latest

USER root

# Copiar certificados de CA
COPY certs/ca-interna.pem /etc/pki/ca-trust/source/anchors/
COPY certs/keycloak.pem /etc/pki/ca-trust/source/anchors/

# Actualizar trust store del sistema
RUN update-ca-trust

# Importar al truststore de Java
RUN keytool -importcert \
    -alias ca-interna \
    -file /etc/pki/ca-trust/source/anchors/ca-interna.pem \
    -cacerts \
    -storepass changeit \
    -noprompt

USER 1001
WORKDIR /deployments
COPY --from=builder /build/target/*.jar app.jar

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### Montar Certificados como Volumen

```yaml
# En Helm values o Deployment
spec:
  containers:
    - name: app
      volumeMounts:
        - name: certs
          mountPath: /certs
          readOnly: true
      env:
        - name: JAVA_OPTS
          value: >-
            -Djavax.net.ssl.trustStore=/certs/truststore.p12
            -Djavax.net.ssl.trustStorePassword=changeit
  volumes:
    - name: certs
      secret:
        secretName: app-truststore
```

### Crear Secret con TrustStore

```bash
# Crear secret con el truststore
oc create secret generic app-truststore \
  --from-file=truststore.p12=./truststore.p12 \
  -n mi-namespace
```



## Oracle DB con TLS

### Configuración JDBC con TLS

```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCPS)(HOST=oracle-prd)(PORT=2484))(CONNECT_DATA=(SERVICE_NAME=PRDDB)))
    properties:
      oracle.net.ssl_version: "1.2"
      oracle.net.ssl_cipher_suites: "(TLS_RSA_WITH_AES_256_CBC_SHA256)"
      javax.net.ssl.trustStore: /certs/truststore.p12
      javax.net.ssl.trustStorePassword: changeit
      javax.net.ssl.trustStoreType: PKCS12
```



## OpenShift Route TLS

### Edge Termination (Recomendado)

```yaml
apiVersion: route.openshift.io/v1
kind: Route
metadata:
  name: mi-aplicacion
spec:
  host: mi-aplicacion.apps.ocp.mh.gob.sv
  to:
    kind: Service
    name: mi-aplicacion
  port:
    targetPort: 8080
  tls:
    termination: edge
    insecureEdgeTerminationPolicy: Redirect
    certificate: |
      -----BEGIN CERTIFICATE-----
      ...
      -----END CERTIFICATE-----
    key: |
      -----BEGIN PRIVATE KEY-----
      ...
      -----END PRIVATE KEY-----
    caCertificate: |
      -----BEGIN CERTIFICATE-----
      ...
      -----END CERTIFICATE-----
```

| Terminación | Descripción | TLS Backend |
|-------------|-------------|-------------|
| **Edge** | TLS termina en el router | HTTP al pod |
| **Passthrough** | TLS pasa directo al pod | HTTPS al pod |
| **Re-encrypt** | TLS termina y re-encripta | HTTPS al pod |



## Variables de Entorno TLS

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `TRUSTSTORE_PATH` | Ruta al truststore | `/certs/truststore.p12` |
| `TRUSTSTORE_PASSWORD` | Contraseña del truststore | `(desde Secret)` |
| `JAVAX_NET_SSL_TRUSTSTORE` | JVM system property | `/certs/truststore.p12` |



## Comparación Quarkus ↔ Spring Boot

| Aspecto | Quarkus | Spring Boot |
|---------|---------|-------------|
| TLS config | `quarkus.http.ssl.*` | `server.ssl.*` |
| TrustStore | `quarkus.tls.trust-store.*` | `javax.net.ssl.trustStore` |
| REST Client TLS | `quarkus.rest-client.*.trust-store` | Programático via `SSLContext` |
| Cert import | `keytool` / Dockerfile | `keytool` / Dockerfile |
| Oracle TLS | JDBC URL `TCPS` + wallet | JDBC URL `TCPS` + system properties |



## Buenas Prácticas

| Práctica | Descripción |
|----------|-------------|
| **TLS 1.2+** | Mínimo TLS 1.2 en todas las conexiones |
| **PKCS12** | Preferir PKCS12 sobre JKS (estándar moderno) |
| **Secrets** | TrustStore password en Kubernetes Secrets |
| **Rotación** | Plan de rotación de certificados antes de expiración |
| **Edge TLS** | Usar route edge para simplificar — TLS en el router |
| **No self-signed** | Certificados de CA interna, nunca auto-firmados en prod |
| **Refresh sin redeploy** | Montar certs como volumen para actualizar sin rebuild |
