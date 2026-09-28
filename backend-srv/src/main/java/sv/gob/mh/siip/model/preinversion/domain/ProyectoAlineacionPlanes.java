package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Alineación del proyecto con el Plan de Gobierno y los planes sectoriales. Condicional. */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class ProyectoAlineacionPlanes {

    /** Eje del Plan Cuscatlán (catálogo Anexo C.3). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_EJE_PLAN_GOBIERNO")
    private EjePlanGobierno ejePlanGobierno;

    /** Plan Sectorial/Regional (catálogo Anexo C.4). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PLAN_SECTORIAL_REGIONAL")
    private PlanSectorialRegional planSectorialRegional;
}
