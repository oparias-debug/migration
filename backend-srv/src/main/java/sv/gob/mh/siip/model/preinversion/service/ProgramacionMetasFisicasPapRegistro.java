package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;
import java.util.Map;

import sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralMetaFisica;
import sv.gob.mh.siip.model.preinversion.dto.EtapaMetaFisicaRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralMetaFisicaRepository;

/**
 * CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión": registro de la
 * programación física por etapa de un estudio (validación previa y persistencia de la meta física
 * de cada etapa y de su programación cuatrimestral del año).
 */
final class ProgramacionMetasFisicasPapRegistro {

    private final ProgramacionPapConsultas consultas;
    private final EtapaMetaFisicaPapRepository etapaMetaRepository;
    private final ProgCuatrimestralMetaFisicaRepository progRepository;
    private final ProgramacionMetasFisicasPapCalculos calculos;
    private final ProgramacionMetasFisicasPapValidaciones validaciones;

    ProgramacionMetasFisicasPapRegistro(ProgramacionPapConsultas consultas,
            EtapaMetaFisicaPapRepository etapaMetaRepository, ProgCuatrimestralMetaFisicaRepository progRepository) {
        this.consultas = consultas;
        this.etapaMetaRepository = etapaMetaRepository;
        this.progRepository = progRepository;
        this.calculos = new ProgramacionMetasFisicasPapCalculos(etapaMetaRepository, progRepository);
        this.validaciones = new ProgramacionMetasFisicasPapValidaciones(etapaMetaRepository, calculos);
    }

    /** Valida y guarda la programación "etapasSolicitud" del estudio "idProyecto" para el año. */
    void guardar(Long idProyecto, Integer anio, List<EtapaMetaFisicaRequestDto> etapasSolicitud) {
        List<EtapaPreinversion> etapas = consultas.etapasOrdenadas(idProyecto);
        Map<TipoEtapaPreinversion, EtapaMetaFisicaRequestDto> porEtapa = ProgramacionPapSoporte
                .construirMapaPorEtapa(etapasSolicitud, EtapaMetaFisicaRequestDto::getEtapa);

        validaciones.validar(etapas, porEtapa, anio);
        persistirProgramacion(etapas, porEtapa, anio);
    }

    private void persistirProgramacion(List<EtapaPreinversion> etapas,
            Map<TipoEtapaPreinversion, EtapaMetaFisicaRequestDto> porEtapa, Integer anio) {
        for (EtapaPreinversion etapa : etapas) {
            EtapaMetaFisicaRequestDto item = porEtapa.get(etapa.getTipoEtapa());
            if (item == null) {
                continue;
            }
            guardarFila(etapa, item, anio);
        }
    }

    private void guardarFila(EtapaPreinversion etapa, EtapaMetaFisicaRequestDto item, Integer anio) {
        EtapaMetaFisicaPap etapaMeta = etapaMetaRepository.findByEtapaPreinversionId(etapa.getId())
                .orElseGet(() -> EtapaMetaFisicaPap.builder().etapaPreinversion(etapa).build());
        // SF-1 paso 2: en una etapa de arrastre "Entregable" está deshabilitado; se ignora el enviado.
        etapaMeta.setEntregable(calculos.entregableEfectivo(etapa, item, anio));
        // SF-4/SF-5: si la meta había sido desactivada desde CU-PRE-30, volver a registrar su
        // programación (SF-8/SF-9) la reactiva, reutilizando el mismo registro y su histórico.
        etapaMeta.setActivo(Boolean.TRUE);
        EtapaMetaFisicaPap etapaMetaGuardada = etapaMetaRepository.save(etapaMeta);

        ProgCuatrimestralMetaFisica prog = progRepository
                .findByEtapaMetaFisicaIdAndAnio(etapaMetaGuardada.getId(), anio)
                .orElseGet(() -> ProgCuatrimestralMetaFisica.builder()
                        .etapaMetaFisica(etapaMetaGuardada)
                        .anio(anio)
                        .build());
        prog.setMontoCuatrimestre1(ProgramacionPapSoporte.bd(item.getMontoCuatrimestre1()));
        prog.setMontoCuatrimestre2(ProgramacionPapSoporte.bd(item.getMontoCuatrimestre2()));
        prog.setMontoCuatrimestre3(ProgramacionPapSoporte.bd(item.getMontoCuatrimestre3()));
        progRepository.save(prog);
    }
}
