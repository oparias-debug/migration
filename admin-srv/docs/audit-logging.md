# 5. Pilares del marco — auditoría

La auditoría del marco, incluida en este proyecto como módulo propio, registra **quién cambió qué y cuándo** sobre las entidades que tú
marques, y lo envía a `auditoria-service`. No hay que escribir clientes ni eventos: el
módulo lo hace desde el ciclo de vida de JPA.

---

## 1. Qué hace la librería

Al arrancar, `AuditConfiguration` (paquete `sv.gob.mh.infrastructure.config.audit` del propio proyecto) registra `AuditEntityListener` en Hibernate. Para cada
entidad anotada con `@Auditable`:

| Evento JPA | Qué envía | Detalle |
|---|---|---|
| Insert | `INSERT` con los valores nuevos | |
| Update | `UPDATE` con valores anteriores y nuevos | El "antes" se captura al cargar la entidad (`POST_LOAD`) |
| Delete | `DELETE` con los valores anteriores | |

El evento (`AuditEvent`) lleva `userId` (del JWT en curso, vía `SecurityContext`), `action`, `resource`
(`esquema.tabla`), `application` (`service.name`), `oldValues`, `newValues` y `timestamp`, y
se envía de forma **asíncrona** a `AUDIT_SERVICE_URL`. Si el servicio de auditoría no
responde, la transacción de negocio no se ve afectada; el fallo queda en el log local.

Las lecturas no generan evento.

---

## 2. Qué haces tú

```java
package sv.gob.mh.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import sv.gob.mh.infrastructure.config.audit.Auditable;

@Entity
@Table(name = "EXPEDIENTE", schema = "SIIT")
@Auditable
public class Expediente {
    // ...
}
```

Eso es todo para las operaciones sobre la entidad. Requisitos:

- La entidad se persiste con JPA (Spring Data o `EntityManager`) dentro de una transacción
  (`@Transactional` en el servicio). Un `UPDATE` por consulta nativa no pasa por el listener y no se audita.
- Hay un request autenticado: el `userId` sale del `SecurityContext`. En procesos sin
  usuario (jobs, consumidores Kafka) el evento se registra sin usuario.
- `AUDIT_SERVICE_URL` está en el ConfigMap (viene de fábrica).

### Datos sensibles

`oldValues` y `newValues` serializan la entidad completa. Si una columna no debe salir del
servicio (contraseñas, datos personales que no hagan falta en auditoría), márcala
`@Transient` para la serialización o modélala en una entidad aparte sin `@Auditable`.

---

## 3. Acciones de negocio que no son una entidad

Para registrar una acción que no se traduce en un insert/update/delete (por ejemplo "emitió
el reporte X"), usa el logger remoto con nivel `INFO` o superior y un mensaje estructurado;
llega a `auditoria-service` por el canal de logs
([framework-library.md](framework-library.md) §2). Ajusta `LOG_LEVEL` a `INFO` en el
ConfigMap si quieres que ese nivel salga del pod.

---

## 4. Cómo comprobar

1. Ejecutar una operación de escritura sobre la entidad anotada con un usuario autenticado.
2. En `auditoria-ui` del ambiente, filtrar por servicio
   `dgicp-siip-admin-srv`; debe aparecer el
   evento con el usuario, la acción y la tabla.
3. En el log del pod no debe haber `Error enviando evento de auditoría`.

---

## 5. Si algo falla

| Síntoma | Causa | Qué hacer |
|---|---|---|
| No aparece ningún evento | La entidad no tiene `@Auditable`, o la escritura fue por SQL nativo | Anotar; usar `save`/dirty checking |
| Evento sin usuario | La operación corrió fuera de un request autenticado | Esperado en jobs; si es un endpoint, falta `@Authenticated` |
| `Error enviando evento` en el log | `AUDIT_SERVICE_URL` incorrecta o `auditoria-service` caído | Revisar el ConfigMap; el negocio no se afecta |
| El `UPDATE` llega sin `oldValues` | La entidad no se cargó antes de modificarse en la misma sesión | Cargar con `findById` y modificar la instancia gestionada |
