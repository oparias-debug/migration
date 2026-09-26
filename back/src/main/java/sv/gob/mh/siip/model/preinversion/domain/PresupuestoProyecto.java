package sv.gob.mh.siip.model.preinversion.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.common.domain.Auditable;
import sv.gob.mh.siip.model.preinversion.enums.FuenteFinanciamiento;

@Entity
@Table(name = "PRESUPUESTO_PROYECTO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(callSuper = false, of = "id")
public class PresupuestoProyecto extends Auditable {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "presupuesto_proyecto_seq")
    @SequenceGenerator(name = "presupuesto_proyecto_seq", sequenceName = "PRESUPUESTO_PROYECTO_SEQ", allocationSize = 1)
    @Column(name = "ID_PRESUPUESTO_PROYECTO") private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", unique = true, nullable = false)
    private Proyecto proyecto;
    @Column(name = "PERIODOS_ESTIMADOS") private Integer periodosEstimados;

    /**
     * "Fuente de financiamiento y de recursos" (Anexo A.5, RN14) — propias de CU-PRE-17, para
     * cualquier proyecto. No reutiliza los campos homónimos de {@link FichaEmergencia} (CU-PRE-03.5):
     * esa entidad solo existe para proyectos de emergencia, y acoplar ambos CUs dejaba
     * "/presupuesto/fuentes-financiamiento" en 404 para el resto de los proyectos.
     */
    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "PRESUPUESTO_FUENTE_FINANC", joinColumns = @JoinColumn(name = "ID_PRESUPUESTO_PROYECTO"))
    @Enumerated(EnumType.STRING)
    @Column(name = "CODIGO_FUENTE", length = 20)
    private List<FuenteFinanciamiento> fuentesFinanciamiento = new ArrayList<>();

    @Column(name = "FUENTE_RECURSOS", length = 200)
    private String fuenteRecursos;
}
