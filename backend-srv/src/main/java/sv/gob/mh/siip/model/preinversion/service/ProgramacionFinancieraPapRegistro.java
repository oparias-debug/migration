package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;
import java.util.Map;

import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.dto.EtapaProgramacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaFuenteProgramacionRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;

/**
 * CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión": registro de la programación
 * de las etapas programables de un estudio (validación previa y persistencia del costo de la etapa y
 * de sus fuentes de financiamiento).
 */
final class ProgramacionFinancieraPapRegistro {

    private final EtapaPreinversionRepository etapaPreinversionRepository;
    private final ProgramacionPapConsultas consultas;
    private final ProgramacionFinancieraPapCalculos calculos;
    private final ProgramacionFinancieraPapValidaciones validaciones;
    private final ProgramacionFinancieraPapFuentes fuentes;

    ProgramacionFinancieraPapRegistro(ProgramacionPapConsultas consultas,
            EtapaPreinversionRepository etapaPreinversionRepository,
            FuenteFinanciamientoEtapaPapRepository fuenteRepository,
            ProgCuatrimestralFinancieraRepository progRepository) {
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.consultas = consultas;
        this.calculos = new ProgramacionFinancieraPapCalculos(fuenteRepository, progRepository);
        this.validaciones = new ProgramacionFinancieraPapValidaciones(fuenteRepository, calculos);
        this.fuentes = new ProgramacionFinancieraPapFuentes(fuenteRepository, progRepository);
    }

    /** Valida y guarda la programación "etapas" del estudio "idProyecto" para el año. */
    void guardar(Long idProyecto, Integer anio, List<EtapaProgramacionRequestDto> etapas) {
        List<EtapaPreinversion> etapasProgramables = consultas.etapasOrdenadas(idProyecto).stream()
                .filter(etapa -> !calculos.estaFinalizada(etapa, anio))
                .toList();

        Map<TipoEtapaPreinversion, EtapaProgramacionRequestDto> porEtapa = ProgramacionPapSoporte
                .construirMapaPorEtapa(etapas, EtapaProgramacionRequestDto::getEtapa);
        validaciones.validar(etapasProgramables, porEtapa, anio);

        // Persistir.
        persistirProgramacion(etapasProgramables, porEtapa, anio);
    }

    private void persistirProgramacion(List<EtapaPreinversion> etapasProgramables,
            Map<TipoEtapaPreinversion, EtapaProgramacionRequestDto> porEtapa, Integer anio) {
        for (EtapaPreinversion etapa : etapasProgramables) {
            EtapaProgramacionRequestDto item = porEtapa.get(etapa.getTipoEtapa());
            if (item == null) {
                continue;
            }
            if (item.getCostoEtapa() != null) {
                etapa.setCosto(item.getCostoEtapa());
                etapaPreinversionRepository.save(etapa);
            }
            for (FilaFuenteProgramacionRequestDto fila : ProgramacionPapSoporte.nullSafe(item.getFuentes())) {
                fuentes.guardarFila(etapa, fila, anio);
            }
        }
    }
}
