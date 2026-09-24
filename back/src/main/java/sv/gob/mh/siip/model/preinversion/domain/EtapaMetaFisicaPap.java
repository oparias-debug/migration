package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import sv.gob.mh.siip.model.preinversion.enums.Entregable;

/**
 * Identidad de la meta física de una etapa registrada para la Programación Cuatrimestral de Metas
 * Físicas del PAP (Anexo A.4). CU-PRE-31. Es el análogo de {@link FuenteFinanciamientoEtapaPap}
 * para metas físicas: a diferencia de las fuentes de financiamiento (varias por etapa), solo hay
 * una meta física por etapa, por lo que la relación con {@link EtapaPreinversion} es única.
 * {@code entregable} persiste a través de los años (RN-B.a: en un estudio de arrastre se bloquea y
 * se muestra tal cual en el ejercicio siguiente); el porcentaje programado por cuatrimestre, que sí
 * cambia año a año, vive en {@link ProgCuatrimestralMetaFisica}.
 */
@Entity
@Table(name = "ETAPA_META_FISICA_PAP",
       uniqueConstraints = @UniqueConstraint(name = "UK_ETAPA_META_FISICA_PAP", columnNames = {"ID_ETAPA_PREINVERSION"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class EtapaMetaFisicaPap {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "etapa_meta_fisica_pap_seq")
    @SequenceGenerator(name = "etapa_meta_fisica_pap_seq", sequenceName = "ETAPA_META_FISICA_PAP_SEQ", allocationSize = 1)
    @Column(name = "ID_ETAPA_META_FISICA")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_ETAPA_PREINVERSION", nullable = false)
    private EtapaPreinversion etapaPreinversion;

    @Enumerated(EnumType.STRING)
    @Column(name = "ENTREGABLE", length = 30)
    private Entregable entregable;

    /**
     * SF-4/SF-5: "automáticamente se desactivará en la programación por Metas Físicas" cuando el
     * código se desactive o la etapa se elimine en la Programación Financiera (CU-PRE-30). Se
     * conserva el registro (y su histórico de {@link ProgCuatrimestralMetaFisica}) marcándolo como
     * inactivo, en lugar de borrarlo físicamente; se reactiva al volver a guardar su programación
     * (SF-8/SF-9).
     */
    @NotNull
    @Builder.Default
    @Column(name = "ACTIVO", nullable = false)
    private Boolean activo = Boolean.TRUE;
}
