package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.preinversion.dto.ProbabilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.ImpactoRiesgoDto;
import sv.gob.mh.siip.model.preinversion.dto.CalificacionRiesgoDto;



/**
 * Detalle de Riesgos y Desastres Inminentes. CU-PRE-15.
 *
 * @author Luis Medrano
 * @version 1.0
 */
@Entity
@Table(name = "RIESGOS_DESASTRES_INMINENTES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class RiesgosDesastresInminentes {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "riesgos_desastres_seq")
    @SequenceGenerator(name = "riesgos_desastres_seq", sequenceName = "RIESGOS_DESASTRES_SEQ", allocationSize = 1)
    @Column(name = "ID_RIESGO_DESASTRE")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_ANALISIS_RIESGO", nullable = false)
    private AnalisisRiesgo analisisRiesgo;

    @Column(name = "DESCRIPCION_RIESGO", length = 500)
    private String descripcionRiesgo;

    @Enumerated(EnumType.STRING)
    @Column(name = "PROBABILIDAD", length = 30)
    private ProbabilidadDto probabilidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "IMPACTO_RIESGO", length = 30)
    private ImpactoRiesgoDto impactoRiesgo;

    @Enumerated(EnumType.STRING)
    @Column(name = "CALIFICACION_RIESGO", length = 30)
    private CalificacionRiesgoDto calificacionRiesgo;

    @Column(name = "ACCION_MITIGACION", length = 1000)
    private String accionMitigacion;

    @Column(name = "COSTO_ACCION_MITIGACION")
    private Double costoAccionMitigacion;
}
