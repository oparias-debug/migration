package sv.gob.mh.siip.security;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

/**
 * Alimenta @CreatedBy/@LastModifiedBy (Auditable) con el nombre de usuario resuelto por
 * {@link ActorContexto#nombreUsuarioActual()}. Usa a proposito la variante que NO consulta el
 * repositorio: este metodo se invoca dentro de un callback de auditoria de Hibernate
 * (@PrePersist/@PreUpdate), y una consulta ahi dispara un auto-flush que reentra en el mismo
 * callback (StackOverflowError).
 */
@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    private static final String USUARIO_SISTEMA = "sistema";

    private final ActorContexto actorContexto;

    public AuditorAwareImpl(ActorContexto actorContexto) {
        this.actorContexto = actorContexto;
    }

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(actorContexto.nombreUsuarioActual().orElse(USUARIO_SISTEMA));
    }
}
