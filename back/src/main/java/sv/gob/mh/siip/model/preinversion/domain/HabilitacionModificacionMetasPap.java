package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Habilitación, por Unidad Ejecutora y año, de modificaciones a la Programación de Metas Físicas
 * fuera del período de elaboración (SF-8/SF-9, CU-PRE-31). Separada de
 * {@link HabilitacionModificacionPap} (CU-PRE-30): el propio contrato documenta la habilitación de
 * CU-PRE-31 como una precondición que exige que ya se haya habilitado antes CU-PRE-30, lo que
 * implica que son dos registros/decisiones administrativas independientes, no el mismo.
 */
@Entity
@Table(name = "HABILITACION_MODIF_METAS_PAP",
       uniqueConstraints = @UniqueConstraint(
               name = "UK_HABILITACION_MOD_METAS_PAP",
               columnNames = {"ID_UNIDAD_EJECUTORA", "ANIO"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false, of = "id")
public class HabilitacionModificacionMetasPap extends HabilitacionModificacionPapBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "habilitacion_mod_metas_pap_seq")
    @SequenceGenerator(
            name = "habilitacion_mod_metas_pap_seq",
            sequenceName = "HABILITACION_MOD_METAS_PAP_SEQ",
            allocationSize = 1)
    @Column(name = "ID_HABILITACION_MOD_METAS_PAP")
    private Long id;
}
