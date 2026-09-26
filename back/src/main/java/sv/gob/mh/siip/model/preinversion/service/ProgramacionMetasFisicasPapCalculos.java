package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralMetaFisica;
import sv.gob.mh.siip.model.preinversion.dto.EntregableDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaMetaFisicaRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.Entregable;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralMetaFisicaRepository;

/**
 * CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión": lecturas de la
 * programación histórica de metas físicas y los cálculos sobre ella ("Ejecutado años anteriores",
 * programado del año, etapa o estudio de arrastre y entregable efectivo, RN-B.a).
 */
final class ProgramacionMetasFisicasPapCalculos {

    /** "Meta"/"Meta Total": siempre 1.00, tanto para estudios de arrastre como nuevos (Anexo A.4). */
    public static final double META_TOTAL = 1.0D;
    public static final BigDecimal CIEN = BigDecimal.valueOf(100);

    private final EtapaMetaFisicaPapRepository etapaMetaRepository;
    private final ProgCuatrimestralMetaFisicaRepository progRepository;

    ProgramacionMetasFisicasPapCalculos(EtapaMetaFisicaPapRepository etapaMetaRepository,
            ProgCuatrimestralMetaFisicaRepository progRepository) {
        this.etapaMetaRepository = etapaMetaRepository;
        this.progRepository = progRepository;
    }

    /**
     * Resumen a nivel de estudio para {@code EstudioProgramacionMetas.esArrastre}: {@code true} si
     * al menos una de sus etapas es de arrastre. La validación de RN-B.a NO usa este valor, sino
     * {@link #esArrastreEtapa} (un proyecto puede mezclar etapas de arrastre y nuevas).
     */
    boolean esArrastre(Long idProyecto, Integer anio) {
        List<EtapaMetaFisicaPap> etapasMeta = etapaMetaRepository.findByEtapaPreinversionProyectoId(idProyecto);
        if (etapasMeta.isEmpty()) {
            return false;
        }
        List<Long> idsEtapaMetaFisica = etapasMeta.stream().map(EtapaMetaFisicaPap::getId).toList();
        return progRepository.findByEtapaMetaFisicaIdIn(idsEtapaMetaFisica).stream()
                .anyMatch(prog -> prog.getAnio() < anio);
    }

    /**
     * RN-B.a: una etapa es "de arrastre" si su meta física ya tuvo programación en un año anterior
     * al consultado.
     */
    boolean esArrastreEtapa(EtapaMetaFisicaPap etapaMeta, Integer anio) {
        if (etapaMeta.getId() == null) {
            return false;
        }
        return progRepository.findByEtapaMetaFisicaId(etapaMeta.getId()).stream()
                .anyMatch(prog -> prog.getAnio() < anio);
    }

    /** RN-B.a: "Ejecutado años anteriores" = suma histórica de todos los años previos al consultado. */
    BigDecimal ejecutadoAniosAnteriores(Long idEtapaMetaFisica, Integer anio) {
        BigDecimal total = BigDecimal.ZERO;
        for (ProgCuatrimestralMetaFisica prog : progRepository.findByEtapaMetaFisicaId(idEtapaMetaFisica)) {
            if (prog.getAnio() < anio) {
                total = total.add(prog.totalProgramadoAnio());
            }
        }
        return total;
    }

    /** Programación de la meta física para el año; {@code null} si no tiene. */
    ProgCuatrimestralMetaFisica programacionDelAnio(Long idEtapaMetaFisica, Integer anio) {
        return progRepository.findByEtapaMetaFisicaIdAndAnio(idEtapaMetaFisica, anio).orElse(null);
    }

    /** "Total Año" programado de la meta física; cero si no tiene programación para el año. */
    BigDecimal totalDelAnio(Long idEtapaMetaFisica, Integer anio) {
        ProgCuatrimestralMetaFisica actual = programacionDelAnio(idEtapaMetaFisica, anio);
        return actual != null ? actual.totalProgramadoAnio() : BigDecimal.ZERO;
    }

    /** SF-1 paso 2: en una etapa de arrastre "Entregable" está deshabilitado y se conserva el ya registrado. */
    Entregable entregableEfectivo(EtapaPreinversion etapa, EtapaMetaFisicaRequestDto item, Integer anio) {
        Optional<EtapaMetaFisicaPap> etapaMetaOpt = etapaMetaRepository.findByEtapaPreinversionId(etapa.getId());
        if (etapaMetaOpt.isPresent() && esArrastreEtapa(etapaMetaOpt.get(), anio)) {
            return etapaMetaOpt.get().getEntregable();
        }
        EntregableDto entregable = item.getEntregable();
        return entregable != null ? Entregable.valueOf(entregable.name()) : null;
    }

    /** Entregable como DTO; {@code null} si no está registrado. */
    static EntregableDto entregableDto(Entregable entregable) {
        return entregable != null ? EntregableDto.valueOf(entregable.name()) : null;
    }

    /** "Total programado Año" de una etapa de la solicitud (suma de los tres cuatrimestres). */
    static BigDecimal suma(EtapaMetaFisicaRequestDto item) {
        return ProgramacionPapSoporte.bd(item.getMontoCuatrimestre1())
                .add(ProgramacionPapSoporte.bd(item.getMontoCuatrimestre2()))
                .add(ProgramacionPapSoporte.bd(item.getMontoCuatrimestre3()));
    }
}
