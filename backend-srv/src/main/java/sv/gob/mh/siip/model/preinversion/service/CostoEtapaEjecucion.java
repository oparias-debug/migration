package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.preinversion.dto.PresupuestoDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.PresupuestoProyectoRepository;

/**
 * "Costo de la etapa" de EJECUCION en Registro de Etapas (CU-PRE-03.5, RN05/RN11/RN22): es la
 * "Inversión estimada (precios de mercado)" del presupuesto de inversión (CU-PRE-17). Se actualiza
 * cada vez que cambia ese presupuesto y cada vez que se emite una Opinión Técnica. El cliente no
 * puede fijarlo (ver {@code SeleccionEtapasRegistro#actualizar}).
 */
@Component
@Transactional
public class CostoEtapaEjecucion {

    private final EtapaPreinversionRepository etapas;
    private final PresupuestoProyectoRepository presupuestos;
    private final PresupuestoInversionEnsamblador ensamblador;

    public CostoEtapaEjecucion(EtapaPreinversionRepository etapas, PresupuestoProyectoRepository presupuestos,
            PresupuestoInversionEnsamblador ensamblador) {
        this.etapas = etapas;
        this.presupuestos = presupuestos;
        this.ensamblador = ensamblador;
    }

    /**
     * Copia el total del presupuesto ya calculado al costo de EJECUCION. No hace nada si el
     * proyecto no tiene esa etapa (p.ej. un proyecto de emergencia).
     *
     * @param idProyecto identificador del proyecto
     * @param presupuesto presupuesto de inversión calculado (CU-PRE-17)
     */
    public void actualizar(Long idProyecto, PresupuestoDto presupuesto) {
        double total = presupuesto.getInversionEstimadaPreciosMercado().getTotal();
        etapas.findByProyectoIdAndTipoEtapa(idProyecto, TipoEtapaPreinversion.EJECUCION).ifPresent(etapa -> {
            etapa.setCosto(BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP).doubleValue());
            etapas.save(etapa);
        });
    }

    /**
     * Recalcula el costo de EJECUCION a partir del presupuesto guardado (RN11: al emitir una
     * Opinión Técnica). Sin presupuesto registrado, deja el costo como está.
     *
     * @param idProyecto identificador del proyecto
     */
    public void recalcular(Long idProyecto) {
        presupuestos.findByProyectoId(idProyecto)
                .ifPresent(p -> actualizar(idProyecto, ensamblador.dto(p.getProyecto(), p)));
    }
}
