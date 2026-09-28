package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;

import sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioFilaMetasFisicasDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralMetaFisicaRepository;

/**
 * CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión": armado de cada fila
 * del listado (Anexo A.1) y del reporte a partir de la meta física de una etapa.
 */
final class ProgramacionMetasFisicasPapListadoAssembler {

    private final ProgramacionMetasFisicasPapCalculos calculos;

    ProgramacionMetasFisicasPapListadoAssembler(EtapaMetaFisicaPapRepository etapaMetaRepository,
            ProgCuatrimestralMetaFisicaRepository progRepository) {
        this.calculos = new ProgramacionMetasFisicasPapCalculos(etapaMetaRepository, progRepository);
    }

    EstudioFilaMetasFisicasDto construirFilaListaDto(EtapaMetaFisicaPap etapaMeta, Integer anio,
            String comentariosReporteDgicp) {
        EtapaPreinversion etapa = etapaMeta.getEtapaPreinversion();
        Proyecto proyecto = etapa.getProyecto();
        BigDecimal ejecutadoAnterior = calculos.ejecutadoAniosAnteriores(etapaMeta.getId(), anio);
        BigDecimal total = calculos.totalDelAnio(etapaMeta.getId(), anio);
        BigDecimal aniosPosteriores = ProgramacionMetasFisicasPapCalculos.CIEN.subtract(ejecutadoAnterior)
                .subtract(total);

        return new EstudioFilaMetasFisicasDto(proyecto.getCup(), proyecto.getNombre(),
                NombreEtapaDto.valueOf(etapa.getTipoEtapa().name()))
                .metaTotal(ProgramacionMetasFisicasPapCalculos.META_TOTAL)
                .entregable(ProgramacionMetasFisicasPapCalculos.entregableDto(etapaMeta.getEntregable()))
                .ejecutadoAniosAnteriores(ProgramacionPapSoporte.positivoONulo(ejecutadoAnterior))
                .totalAnio(total.doubleValue())
                .aniosPosteriores(ProgramacionPapSoporte.positivoONulo(aniosPosteriores))
                .comentariosReporteDgicp(comentariosReporteDgicp);
    }
}
