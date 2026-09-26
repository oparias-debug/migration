package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralMetaFisica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioProgramacionMetasDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaMetaFisicaDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.enums.Entregable;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralMetaFisicaRepository;

/**
 * CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión": armado de la
 * programación física de un estudio (Anexo A.4), una meta física por etapa de preinversión.
 */
final class ProgramacionMetasFisicasPapEstudioAssembler {

    private final ProgramacionPapConsultas consultas;
    private final EtapaMetaFisicaPapRepository etapaMetaRepository;
    private final ProgramacionMetasFisicasPapCalculos calculos;

    ProgramacionMetasFisicasPapEstudioAssembler(ProgramacionPapConsultas consultas,
            EtapaMetaFisicaPapRepository etapaMetaRepository, ProgCuatrimestralMetaFisicaRepository progRepository) {
        this.consultas = consultas;
        this.etapaMetaRepository = etapaMetaRepository;
        this.calculos = new ProgramacionMetasFisicasPapCalculos(etapaMetaRepository, progRepository);
    }

    EstudioProgramacionMetasDto construirEstudioDto(Proyecto proyecto, Integer anio) {
        boolean esArrastreEstudio = calculos.esArrastre(proyecto.getId(), anio);
        List<EtapaMetaFisicaDto> etapas = consultas.etapasOrdenadas(proyecto.getId()).stream()
                .map(etapa -> construirEtapaDto(etapa, anio))
                .toList();
        return new EstudioProgramacionMetasDto(proyecto.getCup(), proyecto.getNombre(), esArrastreEstudio, etapas);
    }

    private EtapaMetaFisicaDto construirEtapaDto(EtapaPreinversion etapa, Integer anio) {
        Optional<EtapaMetaFisicaPap> etapaMetaOpt = etapaMetaRepository.findByEtapaPreinversionId(etapa.getId());
        BigDecimal ejecutadoAnterior = etapaMetaOpt
                .map(etapaMeta -> calculos.ejecutadoAniosAnteriores(etapaMeta.getId(), anio))
                .orElse(BigDecimal.ZERO);
        ProgCuatrimestralMetaFisica actual = etapaMetaOpt
                .map(etapaMeta -> calculos.programacionDelAnio(etapaMeta.getId(), anio))
                .orElse(null);
        BigDecimal m1 = actual != null ? actual.getMontoCuatrimestre1() : BigDecimal.ZERO;
        BigDecimal m2 = actual != null ? actual.getMontoCuatrimestre2() : BigDecimal.ZERO;
        BigDecimal m3 = actual != null ? actual.getMontoCuatrimestre3() : BigDecimal.ZERO;
        BigDecimal total = m1.add(m2).add(m3);
        BigDecimal aniosPosteriores = ProgramacionMetasFisicasPapCalculos.CIEN.subtract(ejecutadoAnterior)
                .subtract(total);
        Entregable entregable = etapaMetaOpt.map(EtapaMetaFisicaPap::getEntregable).orElse(null);
        boolean esArrastreEtapa = etapaMetaOpt.map(etapaMeta -> calculos.esArrastreEtapa(etapaMeta, anio))
                .orElse(false);

        return new EtapaMetaFisicaDto(NombreEtapaDto.valueOf(etapa.getTipoEtapa().name()),
                m1.doubleValue(), m2.doubleValue(), m3.doubleValue(), total.doubleValue())
                .esArrastre(esArrastreEtapa)
                .meta(ProgramacionMetasFisicasPapCalculos.META_TOTAL)
                .entregable(ProgramacionMetasFisicasPapCalculos.entregableDto(entregable))
                .ejecutadoAniosAnteriores(ProgramacionPapSoporte.positivoONulo(ejecutadoAnterior))
                .aniosPosteriores(ProgramacionPapSoporte.positivoONulo(aniosPosteriores));
    }
}
