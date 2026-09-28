package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import sv.gob.mh.siip.model.preinversion.domain.AvanceFinancieroCuatrimestral;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.FuenteFinanciamientoEtapaPap;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralFinanciera;
import sv.gob.mh.siip.model.preinversion.dto.FuenteFinanciamientoDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.repository.AvanceFinancieroCuatrimestralRepository;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;

/**
 * CU-PRE-32 "Avance Financiero Cuatrimestral del PAP": lecturas de fuentes, programación y avances
 * ejecutados, y los cálculos sobre ellas (acumulados ejecutados, programado del cuatrimestre y al
 * período, porcentajes, etapa finalizada y alerta de exceso sobre el costo de la etapa).
 */
final class AvanceFinancieroPapCalculos {

    private static final long PORCENTAJE_TOTAL = 100L;
    private static final int DECIMALES_PORCENTAJE = 2;

    private final FuenteFinanciamientoEtapaPapRepository fuenteRepository;
    private final ProgCuatrimestralFinancieraRepository progRepository;
    private final AvanceFinancieroCuatrimestralRepository avanceRepository;

    AvanceFinancieroPapCalculos(FuenteFinanciamientoEtapaPapRepository fuenteRepository,
            ProgCuatrimestralFinancieraRepository progRepository,
            AvanceFinancieroCuatrimestralRepository avanceRepository) {
        this.fuenteRepository = fuenteRepository;
        this.progRepository = progRepository;
        this.avanceRepository = avanceRepository;
    }

    /**
     * RN-A.a/RN-B.a: estudios activos en la Programación Financiera del ejercicio "anio" (CU-PRE-30)
     * de la Unidad Ejecutora.
     */
    Page<FuenteFinanciamientoEtapaPap> buscarActivasEnAnio(Long idUnidadEjecutora, Integer anio,
            Pageable pageable) {
        return fuenteRepository.buscarActivasEnAnio(idUnidadEjecutora, anio, pageable);
    }

    List<FuenteFinanciamientoEtapaPap> fuentesDeEtapa(Long idEtapa) {
        return fuenteRepository.findByEtapaPreinversionId(idEtapa);
    }

    List<AvanceFinancieroCuatrimestral> avancesDeFuente(Long idFuente) {
        return avanceRepository.findByProgramacion_Fuente_Id(idFuente);
    }

    ProgCuatrimestralFinanciera programacionDelAnio(Long idFuente, Integer anio) {
        return progRepository.findByFuenteIdAndAnio(idFuente, anio).orElse(null);
    }

    AvanceFinancieroCuatrimestral avanceDelPeriodo(ProgCuatrimestralFinanciera prog, Cuatrimestre periodo) {
        return prog != null
                ? avanceRepository.findByProgramacionIdAndCuatrimestre(prog.getId(), periodo).orElse(null)
                : null;
    }

    /** RN-B.a: una etapa ya financiada al 100% de su costo en años anteriores al consultado deja de listarse. */
    boolean etapaFinalizada(EtapaPreinversion etapa, Integer anio) {
        if (etapa.getCosto() == null || etapa.getCosto() <= 0) {
            return false;
        }
        BigDecimal ejecutadoHistorico = BigDecimal.ZERO;
        for (FuenteFinanciamientoEtapaPap fuente : fuenteRepository.findByEtapaPreinversionId(etapa.getId())) {
            ejecutadoHistorico = ejecutadoHistorico.add(ejecutadoAniosAnteriores(fuente.getId(), anio));
        }
        return ejecutadoHistorico.compareTo(BigDecimal.valueOf(etapa.getCosto())) >= 0;
    }

    /** RN-E: "Ejecutado años anteriores" = suma de lo ejecutado en años previos al consultado. */
    BigDecimal ejecutadoAniosAnteriores(Long idFuente, Integer anio) {
        BigDecimal total = BigDecimal.ZERO;
        for (AvanceFinancieroCuatrimestral avance : avanceRepository.findByProgramacion_Fuente_Id(idFuente)) {
            if (avance.getProgramacion().getAnio() < anio) {
                total = total.add(avance.getMontoEjecutado());
            }
        }
        return total;
    }

    /** RN-E: "Ejecutado años anteriores" de la etapa = suma sobre todas sus fuentes de financiamiento. */
    BigDecimal ejecutadoAniosAnterioresEtapa(List<FuenteFinanciamientoEtapaPap> fuentesEtapa, Integer anio) {
        return CostoEtapaPapSupport.sumarPorEtapa(fuentesEtapa,
                fuente -> ejecutadoAniosAnteriores(fuente.getId(), anio));
    }

    /** RN-E: acumulado ejecutado del año "anio", desde Cuatrimestre I hasta "hastaPeriodo" (inclusive). */
    BigDecimal ejecutadoEnAnioHastaPeriodo(Long idFuente, Integer anio, Cuatrimestre hastaPeriodo) {
        BigDecimal total = BigDecimal.ZERO;
        for (AvanceFinancieroCuatrimestral avance : avanceRepository.findByProgramacion_Fuente_Id(idFuente)) {
            if (avance.getProgramacion().getAnio().equals(anio)
                    && avance.getCuatrimestre().ordinal() <= hastaPeriodo.ordinal()) {
                total = total.add(avance.getMontoEjecutado());
            }
        }
        return total;
    }

    /**
     * RN-B.b: el acumulado ejecutado (años anteriores + Avance Anual/al Cuatrimestre del año hasta el
     * período) no puede superar el "Costo de la Etapa" programado en CU-PRE-30. El costo es único por
     * etapa, así que se compara la SUMA de todas sus fuentes de financiamiento (mismo criterio que
     * CU-PRE-30 RN-B.c); la alerta se replica en cada fila de fuente de esa etapa.
     */
    boolean alertaExcesoEtapa(EtapaPreinversion etapa, Integer anio, Cuatrimestre periodo) {
        BigDecimal ejecutadoEtapa = CostoEtapaPapSupport.sumarPorEtapa(
                fuenteRepository.findByEtapaPreinversionId(etapa.getId()),
                fuente -> ejecutadoAniosAnteriores(fuente.getId(), anio)
                        .add(ejecutadoEnAnioHastaPeriodo(fuente.getId(), anio, periodo)));
        return CostoEtapaPapSupport.superaCostoEtapa(etapa.getCosto(), ejecutadoEtapa);
    }

    static BigDecimal montoProgramadoCuatrimestre(ProgCuatrimestralFinanciera prog, Cuatrimestre periodo) {
        if (prog == null) {
            return BigDecimal.ZERO;
        }
        return AvancePapSoporte.programadoDelCuatrimestre(periodo, prog.getMontoCuatrimestre1(),
                prog.getMontoCuatrimestre2(), prog.getMontoCuatrimestre3());
    }

    static BigDecimal montoProgramadoAlPeriodo(ProgCuatrimestralFinanciera prog, Cuatrimestre hastaPeriodo) {
        if (prog == null) {
            return BigDecimal.ZERO;
        }
        return AvancePapSoporte.programadoAlCuatrimestre(hastaPeriodo, prog.getMontoCuatrimestre1(),
                prog.getMontoCuatrimestre2(), prog.getMontoCuatrimestre3());
    }

    static BigDecimal montoProgramadoAnual(ProgCuatrimestralFinanciera prog) {
        return prog != null ? prog.totalProgramadoAnio() : BigDecimal.ZERO;
    }

    static BigDecimal montoEjecutado(AvanceFinancieroCuatrimestral avance) {
        return avance != null ? avance.getMontoEjecutado() : BigDecimal.ZERO;
    }

    static String observaciones(AvanceFinancieroCuatrimestral avance) {
        return avance != null ? avance.getObservaciones() : null;
    }

    static FuenteFinanciamientoDto fuenteFinanciamientoDto(FuenteFinanciamientoEtapaPap fuente) {
        return fuente.getFuenteFinanciamiento() != null
                ? FuenteFinanciamientoDto.valueOf(fuente.getFuenteFinanciamiento().name())
                : null;
    }

    /** Monto como Double si es mayor que cero; null en otro caso (celda vacía). */
    static Double positivoONulo(BigDecimal monto) {
        return monto.compareTo(BigDecimal.ZERO) > 0 ? monto.doubleValue() : null;
    }

    static double porcentaje(BigDecimal monto, BigDecimal total) {
        return monto.multiply(BigDecimal.valueOf(PORCENTAJE_TOTAL))
                .divide(total, DECIMALES_PORCENTAJE, RoundingMode.HALF_UP).doubleValue();
    }
}
