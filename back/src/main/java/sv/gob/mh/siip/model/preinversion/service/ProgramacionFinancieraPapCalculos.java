package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.util.List;

import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.FuenteFinanciamientoEtapaPap;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralFinanciera;
import sv.gob.mh.siip.model.preinversion.dto.FilaFuenteProgramacionRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;

/**
 * CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión": lecturas de la programación
 * histórica por fuente y etapa, y los cálculos sobre ella ("Ejecutado años anteriores", programado del
 * año, estudio de arrastre y etapa finalizada).
 */
final class ProgramacionFinancieraPapCalculos {

    private final FuenteFinanciamientoEtapaPapRepository fuenteRepository;
    private final ProgCuatrimestralFinancieraRepository progRepository;

    ProgramacionFinancieraPapCalculos(FuenteFinanciamientoEtapaPapRepository fuenteRepository,
            ProgCuatrimestralFinancieraRepository progRepository) {
        this.fuenteRepository = fuenteRepository;
        this.progRepository = progRepository;
    }

    /** RN-B.c: "Ejecutado años anteriores" = suma histórica de todos los años previos al consultado. */
    BigDecimal ejecutadoAniosAnteriores(Long idFuente, Integer anio) {
        BigDecimal total = BigDecimal.ZERO;
        for (ProgCuatrimestralFinanciera prog : progRepository.findByFuenteId(idFuente)) {
            if (prog.getAnio() < anio) {
                total = total.add(prog.totalProgramadoAnio());
            }
        }
        return total;
    }

    /** {@code true} si la fuente tiene "Ejecutado años anteriores" mayor que cero. */
    boolean tieneEjecucionAnterior(Long idFuente, Integer anio) {
        return ejecutadoAniosAnteriores(idFuente, anio).compareTo(BigDecimal.ZERO) > 0;
    }

    /** Monto ya guardado como "Total programado Año" de una fuente para el año indicado. */
    BigDecimal programadoEnAnio(Long idFuente, Integer anio) {
        return progRepository.findByFuenteIdAndAnio(idFuente, anio)
                .map(ProgCuatrimestralFinanciera::totalProgramadoAnio)
                .orElse(BigDecimal.ZERO);
    }

    /** Montos de la fuente para el año: "Ejecutado años anteriores" y programado de cada cuatrimestre. */
    ProgramacionFinancieraPapMontos montos(Long idFuente, Integer anio) {
        BigDecimal ejecutadoAnterior = ejecutadoAniosAnteriores(idFuente, anio);
        ProgCuatrimestralFinanciera actual = progRepository.findByFuenteIdAndAnio(idFuente, anio).orElse(null);
        if (actual == null) {
            return new ProgramacionFinancieraPapMontos(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    ejecutadoAnterior);
        }
        return new ProgramacionFinancieraPapMontos(actual.getMontoCuatrimestre1(), actual.getMontoCuatrimestre2(),
                actual.getMontoCuatrimestre3(), ejecutadoAnterior);
    }

    /**
     * RN-B.e: una etapa ya finalizada física y financieramente en años ANTERIORES no se muestra.
     * Solo cuenta programación de años previos al consultado/guardado: de lo contrario, una etapa
     * desaparecería de su propia respuesta en el mismo guardado que la completa (RN-B.c permite
     * financiar el 100% del costo de la etapa en un único año).
     */
    boolean estaFinalizada(EtapaPreinversion etapa, Integer anio) {
        if (etapa.getCosto() == null || etapa.getCosto() <= 0) {
            return false;
        }
        List<FuenteFinanciamientoEtapaPap> fuentes = fuenteRepository.findByEtapaPreinversionId(etapa.getId());
        if (fuentes.isEmpty()) {
            return false;
        }
        BigDecimal totalHistorico = BigDecimal.ZERO;
        for (FuenteFinanciamientoEtapaPap fuente : fuentes) {
            for (ProgCuatrimestralFinanciera prog : progRepository.findByFuenteId(fuente.getId())) {
                if (prog.getAnio() < anio) {
                    totalHistorico = totalHistorico.add(prog.totalProgramadoAnio());
                }
            }
        }
        return totalHistorico.compareTo(BigDecimal.valueOf(etapa.getCosto())) >= 0;
    }

    boolean tieneHistoricoPositivo(EtapaPreinversion etapa) {
        for (FuenteFinanciamientoEtapaPap fuente : fuenteRepository.findByEtapaPreinversionId(etapa.getId())) {
            for (ProgCuatrimestralFinanciera prog : progRepository.findByFuenteId(fuente.getId())) {
                if (prog.totalProgramadoAnio().compareTo(BigDecimal.ZERO) > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /** RN-A.a/FB paso 2: un estudio es "de arrastre" si ya tuvo programación en un año anterior al consultado. */
    boolean esArrastre(Long idProyecto, Integer anio) {
        List<FuenteFinanciamientoEtapaPap> fuentes = fuenteRepository.findByEtapaPreinversionProyectoId(idProyecto);
        if (fuentes.isEmpty()) {
            return false;
        }
        List<Long> idsFuente = fuentes.stream().map(FuenteFinanciamientoEtapaPap::getId).toList();
        return progRepository.findByFuenteIdIn(idsFuente).stream().anyMatch(prog -> prog.getAnio() < anio);
    }

    /** "Total programado Año" de una fila de la solicitud (suma de los tres cuatrimestres). */
    static BigDecimal suma(FilaFuenteProgramacionRequestDto fila) {
        return ProgramacionPapSoporte.bd(fila.getMontoCuatrimestre1())
                .add(ProgramacionPapSoporte.bd(fila.getMontoCuatrimestre2()))
                .add(ProgramacionPapSoporte.bd(fila.getMontoCuatrimestre3()));
    }
}
