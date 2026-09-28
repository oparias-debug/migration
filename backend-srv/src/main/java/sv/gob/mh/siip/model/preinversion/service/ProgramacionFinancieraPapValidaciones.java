package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.FuenteFinanciamientoEtapaPap;
import sv.gob.mh.siip.model.preinversion.dto.EtapaProgramacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaFuenteProgramacionRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;

/**
 * CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión": validaciones de la
 * programación de un estudio antes de guardarla (al menos una etapa programada, RN-B.b y RN-B.c).
 */
final class ProgramacionFinancieraPapValidaciones {

    private final FuenteFinanciamientoEtapaPapRepository fuenteRepository;
    private final ProgramacionFinancieraPapCalculos calculos;

    ProgramacionFinancieraPapValidaciones(FuenteFinanciamientoEtapaPapRepository fuenteRepository,
            ProgramacionFinancieraPapCalculos calculos) {
        this.fuenteRepository = fuenteRepository;
        this.calculos = calculos;
    }

    /**
     * Valida la programación enviada ("porEtapa") para las etapas programables del estudio, ANTES de
     * escribir nada.
     */
    void validar(List<EtapaPreinversion> etapasProgramables,
            Map<TipoEtapaPreinversion, EtapaProgramacionRequestDto> porEtapa, Integer anio) {
        Set<TipoEtapaPreinversion> conProgramacion = determinarEtapasConProgramacion(etapasProgramables, porEtapa);

        if (conProgramacion.isEmpty()) {
            throw new ValidacionNegocioException("SIN_NINGUNA_ETAPA_PROGRAMADA",
                    "Es obligatorio registrar la programación de por lo menos una etapa.", null);
        }
        validarRutaCompleta(etapasProgramables, conProgramacion);

        // Validar montos contra el costo/monto pendiente ANTES de escribir nada (RN-B.c).
        validarMontosContraCosto(etapasProgramables, porEtapa, anio);
    }

    private Set<TipoEtapaPreinversion> determinarEtapasConProgramacion(List<EtapaPreinversion> etapasProgramables,
            Map<TipoEtapaPreinversion, EtapaProgramacionRequestDto> porEtapa) {
        Set<TipoEtapaPreinversion> conProgramacion = EnumSet.noneOf(TipoEtapaPreinversion.class);
        for (EtapaPreinversion etapa : etapasProgramables) {
            boolean enEstaLlamada = tieneMontoPositivo(porEtapa.get(etapa.getTipoEtapa()));
            boolean yaTeniaHistorico = calculos.tieneHistoricoPositivo(etapa);
            if (enEstaLlamada || yaTeniaHistorico) {
                conProgramacion.add(etapa.getTipoEtapa());
            }
        }
        return conProgramacion;
    }

    /** RN-B.b: no se permite registrar una etapa posterior sin haber programado una etapa anterior de la Ruta. */
    private static void validarRutaCompleta(List<EtapaPreinversion> etapasProgramables,
            Set<TipoEtapaPreinversion> conProgramacion) {
        boolean etapaFaltante = false;
        for (EtapaPreinversion etapa : etapasProgramables) {
            if (!conProgramacion.contains(etapa.getTipoEtapa())) {
                etapaFaltante = true;
            } else if (etapaFaltante) {
                throw new ValidacionNegocioException("RUTA_PREINVERSION_SALTEADA",
                        "Las etapas no coinciden con las registradas en la Ruta de Preinversión."
                                + " Revisar y ajustar según corresponda.",
                        null);
            } else {
                // Etapa programada y sin etapas faltantes previas: la ruta sigue siendo válida.
            }
        }
    }

    /**
     * RN-B.c (c.1 estudios nuevos, c.2 estudios de arrastre): el "Total programado Año" no puede
     * superar el "Costo de la etapa" menos lo "Ejecutado años anteriores". El costo es un único valor
     * por etapa, por lo que se valida el AGREGADO de todas sus fuentes de financiamiento (botón "+" del
     * Anexo A.2), no cada fuente por separado: las filas enviadas reemplazan lo ya guardado para este
     * año en su propia fuente (no se cuenta dos veces) y las fuentes de la etapa que no vienen en la
     * solicitud aportan lo que ya tienen programado para el año.
     */
    private void validarMontosContraCosto(List<EtapaPreinversion> etapasProgramables,
            Map<TipoEtapaPreinversion, EtapaProgramacionRequestDto> porEtapa, Integer anio) {
        for (EtapaPreinversion etapa : etapasProgramables) {
            EtapaProgramacionRequestDto item = porEtapa.get(etapa.getTipoEtapa());
            if (item == null) {
                continue;
            }
            Double costoEtapa = item.getCostoEtapa() != null ? item.getCostoEtapa() : etapa.getCosto();
            List<FilaFuenteProgramacionRequestDto> filas = ProgramacionPapSoporte.nullSafe(item.getFuentes());
            List<FuenteFinanciamientoEtapaPap> fuentesEtapa = fuenteRepository.findByEtapaPreinversionId(etapa.getId());
            Set<Long> idsEnSolicitud = new HashSet<>();
            for (FilaFuenteProgramacionRequestDto fila : filas) {
                if (fila.getIdFuente() != null) {
                    idsEnSolicitud.add(fila.getIdFuente());
                }
            }
            List<FuenteFinanciamientoEtapaPap> fuentesNoEnviadas = fuentesEtapa.stream()
                    .filter(fuente -> !idsEnSolicitud.contains(fuente.getId()))
                    .toList();

            BigDecimal programadoAnioEtapa = CostoEtapaPapSupport
                    .sumarPorEtapa(filas, ProgramacionFinancieraPapCalculos::suma)
                    .add(CostoEtapaPapSupport.sumarPorEtapa(fuentesNoEnviadas,
                            fuente -> calculos.programadoEnAnio(fuente.getId(), anio)));
            BigDecimal ejecutadoAnteriorEtapa = CostoEtapaPapSupport.sumarPorEtapa(fuentesEtapa,
                    fuente -> calculos.ejecutadoAniosAnteriores(fuente.getId(), anio));

            if (CostoEtapaPapSupport.superaCostoEtapa(costoEtapa, programadoAnioEtapa.add(ejecutadoAnteriorEtapa))) {
                throw new ValidacionNegocioException("MONTO_SUPERA_COSTO_ETAPA",
                        "Monto Programado supera el costo de la etapa.", null);
            }
        }
    }

    private static boolean tieneMontoPositivo(EtapaProgramacionRequestDto item) {
        if (item == null) {
            return false;
        }
        for (FilaFuenteProgramacionRequestDto fila : ProgramacionPapSoporte.nullSafe(item.getFuentes())) {
            if (ProgramacionFinancieraPapCalculos.suma(fila).compareTo(BigDecimal.ZERO) > 0) {
                return true;
            }
        }
        return false;
    }
}
