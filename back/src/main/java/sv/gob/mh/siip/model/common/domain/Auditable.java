package sv.gob.mh.siip.model.common.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Superclase de auditoria para todas las entidades del SIIP.
 * Requiere habilitar Spring Data JPA Auditing en la configuracion:
 * @EnableJpaAuditing en la clase principal o una @Configuration.
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {

    @CreatedDate
    @Column(name = "FECHA_CREACION", updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "FECHA_MODIFICACION")
    private LocalDateTime fechaModificacion;

    @CreatedBy
    @Column(name = "USUARIO_CREACION", updatable = false, length = 100)
    private String usuarioCreacion;

    @LastModifiedBy
    @Column(name = "USUARIO_MODIFICACION", length = 100)
    private String usuarioModificacion;
}
