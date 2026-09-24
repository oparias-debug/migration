package sv.gob.mh.siip.bdd.support;

import java.math.BigDecimal;

import sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralMetaFisica;
import sv.gob.mh.siip.model.preinversion.enums.Entregable;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralMetaFisicaRepository;

/**
 * Builders reutilizados por los steps BDD de CU-PRE-31 (Programación Cuatrimestral de Metas
 * Físicas del PAP). Delega en {@link Pre30Fixtures} para el grafo común
 * Institucion/UnidadEjecutora/Proyecto/EtapaPreinversion; aquí solo se agregan los builders
 * propios del dominio de metas físicas.
 */
public final class Pre31Fixtures {

    private Pre31Fixtures() {
    }

    /** Registra una meta física con histórico positivo en {@code anioAnterior} (estudio de arrastre). */
    public static EtapaMetaFisicaPap nuevaEtapaMetaFisicaConHistorico(EtapaMetaFisicaPapRepository etapaMetaRepository,
            ProgCuatrimestralMetaFisicaRepository progRepository, EtapaPreinversion etapa, Entregable entregable,
            Integer anioAnterior, double porcentajeEjecutado) {
        EtapaMetaFisicaPap etapaMeta = etapaMetaRepository
                .save(EtapaMetaFisicaPap.builder().etapaPreinversion(etapa).entregable(entregable).build());
        progRepository.save(ProgCuatrimestralMetaFisica.builder()
                .etapaMetaFisica(etapaMeta)
                .anio(anioAnterior)
                .montoCuatrimestre1(BigDecimal.valueOf(porcentajeEjecutado))
                .montoCuatrimestre2(BigDecimal.ZERO)
                .montoCuatrimestre3(BigDecimal.ZERO)
                .build());
        return etapaMeta;
    }
}
