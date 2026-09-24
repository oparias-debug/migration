package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.enums.EstadoRevisionAvancePap;

/**
 * Estado del ciclo de revisión/aprobación del avance cuatrimestral del PAP (RN-D.b, SF-3), por
 * Unidad Ejecutora, año y período. CU-PRE-33: es el único recurso de aprobación tanto para el avance
 * financiero (CU-PRE-32) como para el de metas físicas (decisión funcional v1.2 del documento
 * fuente) — vive en el service de CU-PRE-33; CU-PRE-32 no lo escribe y solo lee
 * {@code comentarioReporteFinancieroDgicp} para su reporte (Anexo A.6). Es un
 * recurso **distinto** de {@link RevisionProgramacionPap} (CU-PRE-30/31): aquel aprueba la
 * programación anual, este aprueba el avance/ejecución de cada cuatrimestre.
 */
@Entity
@Table(name = "REVISION_AVANCE_PAP",
       uniqueConstraints = @UniqueConstraint(name = "UK_REVISION_AVANCE_PAP_PRE", columnNames = {"ID_UNIDAD_EJECUTORA", "ANIO", "PERIODO"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false, of = "id")
public class RevisionAvancePap extends RevisionPapBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "revision_avance_pap_seq")
    @SequenceGenerator(name = "revision_avance_pap_seq", sequenceName = "REVISION_AVANCE_PAP_SEQ", allocationSize = 1)
    @Column(name = "ID_REVISION_AVANCE_PAP")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "PERIODO", nullable = false, length = 20)
    private Cuatrimestre periodo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoRevisionAvancePap estado = EstadoRevisionAvancePap.EN_ELABORACION;

    @Column(name = "COMENTARIO_REPORTE_FINANCIERO_DGICP", length = 2000)
    private String comentarioReporteFinancieroDgicp;

    @Column(name = "COMENTARIO_REPORTE_METAS_FISICAS_DGICP", length = 2000)
    private String comentarioReporteMetasFisicasDgicp;
}
