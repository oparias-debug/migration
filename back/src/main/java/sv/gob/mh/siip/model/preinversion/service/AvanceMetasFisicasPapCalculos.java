package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.util.List;

import sv.gob.mh.siip.model.preinversion.domain.AvanceCuatriMetaFisica;
import sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralMetaFisica;
import sv.gob.mh.siip.model.preinversion.dto.EstadoAvanceMetasDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.repository.AvanceCuatriMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralMetaFisicaRepository;

/**
 * CU-PRE-33 "Avance Cuatrimestral por Metas Físicas del PAP": lecturas de programación y avances
 * de metas físicas y los cálculos sobre ellas (acumulados ejecutados, programado del cuatrimestre y
 * al período, total meta ejecutada y Estado del estudio, RN-B.c).
 */
final class AvanceMetasFisicasPapCalculos {

    /** RN-B.d: la meta del estudio es el 100% (Ejecutado años anteriores + Ejecutado en el año). */
    public static final BigDecimal CIEN = BigDecimal.valueOf(100);

    private final ProgCuatrimestralMetaFisicaRepository progRepository;
    private final AvanceCuatriMetaFisicaRepository avanceRepository;

    AvanceMetasFisicasPapCalculos(ProgCuatrimestralMetaFisicaRepository progRepository,
            AvanceCuatriMetaFisicaRepository avanceRepository) {
        this.progRepository = progRepository;
        this.avanceRepository = avanceRepository;
    }

    ProgCuatrimestralMetaFisica programacionDelAnio(Long idEtapaMetaFisica, Integer anio) {
        return progRepository.findByEtapaMetaFisicaIdAndAnio(idEtapaMetaFisica, anio).orElse(null);
    }

    AvanceCuatriMetaFisica avanceDelPeriodo(ProgCuatrimestralMetaFisica prog, Cuatrimestre periodo) {
        return prog != null
                ? avanceRepository.findByProgramacionMetaIdAndCuatrimestre(prog.getId(), periodo).orElse(null)
                : null;
    }

    List<AvanceCuatriMetaFisica> avancesDeEtapaMeta(Long idEtapaMetaFisica) {
        return avanceRepository.findByProgramacionMeta_EtapaMetaFisica_Id(idEtapaMetaFisica);
    }

    /** RN-F: "Ejecutado años anteriores" = suma de lo ejecutado en años previos al consultado. */
    BigDecimal ejecutadoAniosAnteriores(Long idEtapaMetaFisica, Integer anio) {
        BigDecimal total = BigDecimal.ZERO;
        for (AvanceCuatriMetaFisica avance
                : avanceRepository.findByProgramacionMeta_EtapaMetaFisica_Id(idEtapaMetaFisica)) {
            if (avance.getProgramacionMeta().getAnio() < anio) {
                total = total.add(avance.getAvanceCuatrimestre());
            }
        }
        return total;
    }

    /** RN-F: acumulado ejecutado del año "anio", desde Cuatrimestre I hasta "hastaPeriodo" (inclusive). */
    BigDecimal ejecutadoEnAnioHastaPeriodo(Long idEtapaMetaFisica, Integer anio, Cuatrimestre hastaPeriodo) {
        BigDecimal total = BigDecimal.ZERO;
        for (AvanceCuatriMetaFisica avance
                : avanceRepository.findByProgramacionMeta_EtapaMetaFisica_Id(idEtapaMetaFisica)) {
            if (avance.getProgramacionMeta().getAnio().equals(anio)
                    && avance.getCuatrimestre().ordinal() <= hastaPeriodo.ordinal()) {
                total = total.add(avance.getAvanceCuatrimestre());
            }
        }
        return total;
    }

    static BigDecimal programadoCuatrimestre(ProgCuatrimestralMetaFisica prog, Cuatrimestre periodo) {
        if (prog == null) {
            return BigDecimal.ZERO;
        }
        return AvancePapSoporte.programadoDelCuatrimestre(periodo, prog.getMontoCuatrimestre1(),
                prog.getMontoCuatrimestre2(), prog.getMontoCuatrimestre3());
    }

    static BigDecimal programadoAlPeriodo(ProgCuatrimestralMetaFisica prog, Cuatrimestre hastaPeriodo) {
        if (prog == null) {
            return BigDecimal.ZERO;
        }
        return AvancePapSoporte.programadoAlCuatrimestre(hastaPeriodo, prog.getMontoCuatrimestre1(),
                prog.getMontoCuatrimestre2(), prog.getMontoCuatrimestre3());
    }

    Datos calcularDatos(EtapaMetaFisicaPap etapaMeta, Integer anio, Cuatrimestre periodo) {
        if (etapaMeta == null) {
            return new Datos(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO, null);
        }
        ProgCuatrimestralMetaFisica prog = programacionDelAnio(etapaMeta.getId(), anio);
        BigDecimal programadoDelPeriodo = programadoCuatrimestre(prog, periodo);
        BigDecimal programadoAnual = prog != null ? prog.totalProgramadoAnio() : BigDecimal.ZERO;
        BigDecimal programadoAlPeriodo = programadoAlPeriodo(prog, periodo);

        BigDecimal ejecutadoAnterior = ejecutadoAniosAnteriores(etapaMeta.getId(), anio);
        BigDecimal ejecutadoAlPeriodo = ejecutadoEnAnioHastaPeriodo(etapaMeta.getId(), anio, periodo);
        AvanceCuatriMetaFisica avanceDelPeriodo = avanceDelPeriodo(prog, periodo);
        BigDecimal ejecutadoDelPeriodo = avanceDelPeriodo != null
                ? avanceDelPeriodo.getAvanceCuatrimestre()
                : BigDecimal.ZERO;
        BigDecimal totalMetaEjecutada = ejecutadoAnterior.add(ejecutadoAlPeriodo);

        EstadoAvanceMetas estado = calcularEstado(totalMetaEjecutada, ejecutadoAlPeriodo, programadoAlPeriodo,
                programadoAnual);

        return new Datos(programadoDelPeriodo, programadoAnual, programadoAlPeriodo, ejecutadoAnterior,
                ejecutadoAlPeriodo, ejecutadoDelPeriodo, totalMetaEjecutada, estado);
    }

    /**
     * RN-B.c, calculado al cuatrimestre:
     * <ul>
     * <li>Finalizado: estudio concluido conforme a la ejecución de metas físicas, es decir, el "Total
     * meta ejecutada" alcanzó la meta del estudio (100%, RN-B.d). No se liga al "Programado en el Año":
     * en estudios plurianuales cumplir lo programado del año no concluye el estudio, y en el
     * Cuatrimestre III lo programado al cuatrimestre coincide con el del año, por lo que ese caso es
     * "A tiempo".</li>
     * <li>A tiempo: ejecutado al cuatrimestre igual a lo programado al cuatrimestre.</li>
     * <li>Atrasado: ejecutado al cuatrimestre menor a lo programado al cuatrimestre.</li>
     * <li>Adelantado: ejecutado al cuatrimestre mayor a lo programado al cuatrimestre, sin sobrepasar
     * el "Programado en el Año".</li>
     * </ul>
     * Si lo ejecutado en el año sobrepasa el "Programado en el Año" (solo posible por la excepción de
     * RN-B.b o por una reprogramación posterior en CU-PRE-31), el estudio no encaja en ningún estado
     * del catálogo y se devuelve {@code null} (el campo "estado" es nullable en el contrato).
     */
    private static EstadoAvanceMetas calcularEstado(BigDecimal totalMetaEjecutada, BigDecimal ejecutadoAlPeriodo,
            BigDecimal programadoAlPeriodo, BigDecimal programadoAnual) {
        EstadoAvanceMetas estado;
        int comparacion = ejecutadoAlPeriodo.compareTo(programadoAlPeriodo);
        if (totalMetaEjecutada.compareTo(CIEN) >= 0) {
            estado = EstadoAvanceMetas.FINALIZADO;
        } else if (comparacion == 0) {
            estado = EstadoAvanceMetas.A_TIEMPO;
        } else if (comparacion < 0) {
            estado = EstadoAvanceMetas.ATRASADO;
        } else {
            estado = ejecutadoAlPeriodo.compareTo(programadoAnual) <= 0 ? EstadoAvanceMetas.ADELANTADO : null;
        }
        return estado;
    }

    /**
     * Catálogo de Estados del estudio (RN-B.c), interno — se traduce a {@link EstadoAvanceMetasDto}
     * al construir el DTO.
     */
    enum EstadoAvanceMetas {
        A_TIEMPO, ATRASADO, ADELANTADO, FINALIZADO
    }

    /** Programado y ejecutado de una etapa al cuatrimestre consultado, con su Estado (RN-B.c). */
    record Datos(BigDecimal programadoDelPeriodo, BigDecimal programadoAnual, BigDecimal programadoAlPeriodo,
            BigDecimal ejecutadoAnterior, BigDecimal ejecutadoAlPeriodo, BigDecimal ejecutadoDelPeriodo,
            BigDecimal totalMetaEjecutada, EstadoAvanceMetas estado) {

        /** "Ejecutado años anteriores" como Double si es mayor que cero; null en otro caso (celda vacía). */
        Double ejecutadoAnteriorONulo() {
            return ejecutadoAnterior.compareTo(BigDecimal.ZERO) > 0 ? ejecutadoAnterior.doubleValue() : null;
        }

        EstadoAvanceMetasDto estadoDto() {
            return estado != null ? EstadoAvanceMetasDto.valueOf(estado.name()) : null;
        }
    }
}
