package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.List;

import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.FuenteFinanciamientoEtapaPap;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioProgramacionPAPDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaProgramacionDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaFuenteProgramacionDto;
import sv.gob.mh.siip.model.preinversion.dto.FuenteFinanciamientoDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;

/**
 * CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión": armado de la programación de
 * un estudio (Anexo A.2) por etapa y fuente de financiamiento, omitiendo las etapas ya finalizadas
 * (RN-B.e).
 */
final class ProgramacionFinancieraPapEstudioAssembler {

    private final ProgramacionPapConsultas consultas;
    private final FuenteFinanciamientoEtapaPapRepository fuenteRepository;
    private final ProgramacionFinancieraPapCalculos calculos;

    ProgramacionFinancieraPapEstudioAssembler(ProgramacionPapConsultas consultas,
            FuenteFinanciamientoEtapaPapRepository fuenteRepository,
            ProgCuatrimestralFinancieraRepository progRepository) {
        this.consultas = consultas;
        this.fuenteRepository = fuenteRepository;
        this.calculos = new ProgramacionFinancieraPapCalculos(fuenteRepository, progRepository);
    }

    EstudioProgramacionPAPDto construirEstudioDto(Proyecto proyecto, Integer anio) {
        boolean esArrastreEstudio = calculos.esArrastre(proyecto.getId(), anio);
        List<EtapaProgramacionDto> etapas = consultas.etapasOrdenadas(proyecto.getId()).stream()
                .filter(etapa -> !calculos.estaFinalizada(etapa, anio))
                .map(etapa -> construirEtapaDto(etapa, anio))
                .toList();
        return new EstudioProgramacionPAPDto(proyecto.getCup(), proyecto.getNombre(), esArrastreEstudio, etapas);
    }

    /** Fuente de financiamiento de la fila como DTO; {@code null} si no está registrada. */
    static FuenteFinanciamientoDto fuenteFinanciamientoDto(FuenteFinanciamientoEtapaPap fuente) {
        return fuente.getFuenteFinanciamiento() != null
                ? FuenteFinanciamientoDto.valueOf(fuente.getFuenteFinanciamiento().name())
                : null;
    }

    private EtapaProgramacionDto construirEtapaDto(EtapaPreinversion etapa, Integer anio) {
        List<FilaFuenteProgramacionDto> fuentes = fuenteRepository.findByEtapaPreinversionId(etapa.getId()).stream()
                .map(fuente -> construirFilaDto(fuente, etapa.getCosto(), anio))
                .toList();
        return new EtapaProgramacionDto(NombreEtapaDto.valueOf(etapa.getTipoEtapa().name()), fuentes)
                .costoEtapa(etapa.getCosto());
    }

    private FilaFuenteProgramacionDto construirFilaDto(FuenteFinanciamientoEtapaPap fuente, Double costoEtapa,
            Integer anio) {
        ProgramacionFinancieraPapMontos montos = calculos.montos(fuente.getId(), anio);
        return new FilaFuenteProgramacionDto(fuente.getId(), montos.cuatrimestre1().doubleValue(),
                montos.cuatrimestre2().doubleValue(), montos.cuatrimestre3().doubleValue(),
                montos.total().doubleValue())
                .fuenteFinanciamiento(fuenteFinanciamientoDto(fuente))
                .fuenteRecursos(fuente.getFuenteRecursos())
                .convenios(new ArrayList<>(fuente.getConvenios()))
                .ejecutadoAniosAnteriores(ProgramacionPapSoporte.positivoONulo(montos.ejecutadoAniosAnteriores()))
                .aniosPosteriores(ProgramacionPapSoporte.positivoONulo(montos.aniosPosteriores(costoEtapa)))
                .porcentajeCuatrimestre1(montos.porcentaje(montos.cuatrimestre1()))
                .porcentajeCuatrimestre2(montos.porcentaje(montos.cuatrimestre2()))
                .porcentajeCuatrimestre3(montos.porcentaje(montos.cuatrimestre3()));
    }
}
