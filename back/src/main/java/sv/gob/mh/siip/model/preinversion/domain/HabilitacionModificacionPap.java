package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Habilitación, por Unidad Ejecutora y año, de modificaciones al PAP fuera del período de
 * elaboración (SF-4/SF-5, CU-PRE-30). La ejecuta el Administrador del Sistema a solicitud del
 * Coordinador PRE (nota de solicitud remitida por la Institución, no modelada como flujo propio).
 * Mientras exista un registro para (unidadEjecutora, año), el Técnico URP puede agregar/modificar
 * estudios de ese año aunque el Calendario de Eventos del PAP (RN-A.b) esté cerrado.
 */
@Entity
@Table(name = "HABILITACION_MODIFICACION_PAP",
       uniqueConstraints = @UniqueConstraint(name = "UK_HABILITACION_MOD_PAP", columnNames = {"ID_UNIDAD_EJECUTORA", "ANIO"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false, of = "id")
public class HabilitacionModificacionPap extends HabilitacionModificacionPapBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "habilitacion_mod_pap_seq")
    @SequenceGenerator(name = "habilitacion_mod_pap_seq", sequenceName = "HABILITACION_MOD_PAP_SEQ", allocationSize = 1)
    @Column(name = "ID_HABILITACION_MOD_PAP")
    private Long id;
}
