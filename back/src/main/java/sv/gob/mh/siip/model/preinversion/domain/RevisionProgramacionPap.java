package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import sv.gob.mh.siip.model.preinversion.enums.EstadoPap;

/**
 * Estado del ciclo de revisión/aprobación conjunta del PAP (RN-A.c, SF-3, SF-6), por Unidad
 * Ejecutora y año. CU-PRE-31: es el único recurso de aprobación tanto para la Programación
 * Financiera (CU-PRE-30) como para la de Metas Físicas (decisión funcional v1.2 del documento
 * fuente) — vive enteramente en el service de CU-PRE-31; CU-PRE-30 no lo lee ni lo escribe.
 */
@Entity
@Table(name = "REVISION_PROGRAMACION_PAP",
       uniqueConstraints = @UniqueConstraint(
               name = "UK_REVISION_PROGRAMACION_PAP",
               columnNames = {"ID_UNIDAD_EJECUTORA", "ANIO"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false, of = "id")
public class RevisionProgramacionPap extends RevisionPapBase {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "revision_prog_pap_seq")
    @SequenceGenerator(name = "revision_prog_pap_seq", sequenceName = "REVISION_PROG_PAP_SEQ", allocationSize = 1)
    @Column(name = "ID_REVISION_PROG_PAP")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "ESTADO_PAP", nullable = false, length = 30)
    private EstadoPap estadoPap = EstadoPap.EN_ELABORACION;

    @Column(name = "COMENTARIOS_REPORTE_FINANCIERO_DGICP", length = 2000)
    private String comentariosReporteFinancieroDgicp;

    @Column(name = "COMENTARIOS_REPORTE_METAS_FISICAS_DGICP", length = 2000)
    private String comentariosReporteMetasFisicasDgicp;
}
