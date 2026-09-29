package sv.gob.mh.infrastructure.config.audit;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 * Serializa entidades JPA a JSON para incluir en los eventos de auditoría.
 */
@Component
public class EntitySerializer {

    private static final Logger LOG = Logger.getLogger(EntitySerializer.class.getName());

    private final ObjectMapper objectMapper;

    public EntitySerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Convierte la entidad a JSON para el evento de auditoría.
     *
     * <p>Un fallo de serialización <b>no puede tumbar la operación de negocio</b>: se registra
     * con su traza y se devuelve el motivo como valor, para que en la auditoría quede constancia
     * de que ese campo no se pudo capturar.</p>
     *
     * @param entity entidad a serializar, o {@code null}
     * @return el JSON, {@code null} si no había entidad, o el motivo del fallo
     */
    public String serialize(Object entity) {
        if (entity == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(entity);
        } catch (Exception e) {
            LOG.log(Level.WARNING, e, () -> "No se pudo serializar la entidad para auditoría: "
                    + entity.getClass().getName());
            return "Serialization error: " + e.getMessage();
        }
    }
}
