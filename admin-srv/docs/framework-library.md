# 5. Pilares del marco — configuración externa y logger remoto

En Spring Boot los pilares del marco no vienen como dependencia Maven: están **incluidos en
el proyecto**, en el paquete `sv.gob.mh.infrastructure.config` (y `shared.exception` para el
logger). Son los mismos contratos que usa el resto del marco.

| Pilar | Dónde está | Qué te da | Qué tienes que hacer |
|---|---|---|---|
| Configuración externa | `infrastructure.config.ExternalConfigSource` | Propiedades por ambiente sin redesplegar | Nada: viene activa. Poner las propiedades en el inventario |
| Logger remoto | `shared.exception.RemoteLogger` | Tus logs de `WARNING` para arriba llegan a `auditoria-service` | Usar `RemoteLogger` en vez de `Logger` |
| Autorización | `infrastructure.config.authz` | `@PermissionsAllowed` evalúa permisos contra `authorization-service` | Anotar los endpoints; sembrar los permisos ([security.md](security.md)) |
| Auditoría | `infrastructure.config.audit` | Cada insert/update/delete de una entidad anotada se registra en `auditoria-service` | `@Auditable` sobre la entidad ([audit-logging.md](audit-logging.md)) |

Estos paquetes se mantienen desde la plantilla: no los modifiques en el proyecto; lo que
les falte se pide al equipo del marco.

---

## 1. Configuración externa (`ExternalConfigSource`)

### Qué hace

Está registrado como `EnvironmentPostProcessor` en `META-INF/spring.factories`. Antes de
que arranque el contexto, lee `CONFIG_SERVICE_URL` del entorno (o `config.service.url` del
`application.yml` si no existe), llama a `GET {url}/{service.name}` con 10 s de timeout y
añade lo recibido como la fuente de propiedades de **mayor prioridad**. Si el config-server
no responde, el servicio arranca con lo que tenga en el `application.yml` y lo deja escrito
en el log.

`service.name` es `dgicp-siip-admin-srv` y es la
clave del componente en el inventario. No lo cambies: también identifica al servicio en
autorización y auditoría.

### Cómo se usa

En el código no hay nada específico del marco: las propiedades del config-server se leen
igual que las del `application.yml`.

```java
@Value("${notificaciones.max-reintentos:3}")
private int maxReintentos;
```

Para que un valor cambie por ambiente, se registra en el inventario del componente de ese
ambiente y se reinicia el pod. El detalle está en
[Configuración de variables](helm-configuration.md) §5.

### Cómo comprobar

```bash
oc logs deploy/admin-srv | grep -i "config"
```

Debe verse la lectura del config-server sin error; un `404` significa que el componente no
existe en el inventario de ese ambiente con ese nombre.

---

## 2. Logger remoto (`RemoteLogger`)

### Qué hace

`RemoteLogger` extiende `java.util.logging.Logger`: escribe en el log local del pod **y**,
si el nivel es igual o superior al mínimo (`WARNING`), envía el registro de forma asíncrona
a `LOG_SERVICE_URL` (`auditoria-service`). `RemoteLoggerConfiguration` lo configura al
arrancar con `remote.logger.url`, `remote.logger.enabled` y `remote.logger.level` del
`application.yml`. Si el servicio de logs no responde, la aplicación sigue; el fallo de
envío queda en el log local.

### Cómo se usa

```java
import sv.gob.mh.shared.exception.RemoteLogger;

@Service
public class CrearPedidoService {

    private static final RemoteLogger LOG = RemoteLogger.getLogger(CrearPedidoService.class);

    public PedidoDto ejecutar(CrearPedidoCommand cmd) {
        LOG.info("Creando pedido para " + cmd.clienteId());      // sólo local
        try {
            // ...
        } catch (RestClientException e) {
            LOG.severe("Fallo al consultar inventario: " + e.getMessage()); // local + remoto
            throw e;
        }
    }
}
```

| Propiedad (`application.yml`) | Valor | Efecto |
|---|---|---|
| `remote.logger.enabled` | `true` | En `false` sólo escribe en local |
| `remote.logger.url` | `${LOG_SERVICE_URL:…}` | Destino de los envíos |
| `remote.logger.level` | `WARNING` | Nivel mínimo que se envía. `SEVERE` > `WARNING` > `INFO` > `CONFIG` > `FINE` |

Nunca escribir tokens, contraseñas ni datos personales en el mensaje: el registro sale del
pod.

### Cómo comprobar

En `auditoria-ui`, pestaña de logs, filtrar por servicio
`dgicp-siip-admin-srv`. En local se ve el envío
en el log de la aplicación al forzar un `LOG.warning(...)`.

---

## 3. Llamar a otros servicios con el JWT del usuario

El proyecto no trae un cliente REST preconfigurado. Para llamar a otro servicio del marco
propagando el token del usuario, usar `RestClient` con la cabecera del request en curso:

```java
@Bean
RestClient catalogosClient(@Value("${catalogos.service.url}") String url) {
    return RestClient.builder()
        .baseUrl(url)
        .requestInterceptor((req, body, exec) -> {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth instanceof JwtAuthenticationToken jwtAuth) {
                req.getHeaders().setBearerAuth(jwtAuth.getToken().getTokenValue());
            }
            return exec.execute(req, body);
        })
        .build();
}
```

La URL llega por el ConfigMap (`CATALOGOS_SERVICE_URL`) y se declara en el `application.yml`
como `catalogos.service.url: ${CATALOGOS_SERVICE_URL}`. El destino debe validar contra el
**mismo realm**; un `401` con token válido casi siempre es un realm distinto.

---

## 4. Si algo falla

| Síntoma | Causa | Qué hacer |
|---|---|---|
| Una propiedad del inventario no se aplica | El pod no se reinició, o el nombre no coincide con el del `@Value` | Reiniciar; comparar nombres |
| El log dice que `CONFIG_SERVICE_URL` no está definida | El ConfigMap no la trae y se usó el default de dev | Revisar `values.yaml` |
| Logs no llegan a auditoría | Nivel por debajo de `WARNING`, o `LOG_SERVICE_URL` incorrecta | Revisar el ConfigMap y el `application.yml` |
| `401` en la llamada saliente | Realm distinto o llamada sin request autenticado | Ver §3 |
