package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaMetaFisicaRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;

/**
 * CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión": validaciones de la
 * programación física por etapa antes de guardarla (campos del Anexo A.4 y RN-B.a).
 */
final class ProgramacionMetasFisicasPapValidaciones {

    private static final String CAMPO_OBLIGATORIO = "*Campo obligatorio";

    private final EtapaMetaFisicaPapRepository etapaMetaRepository;
    private final ProgramacionMetasFisicasPapCalculos calculos;

    ProgramacionMetasFisicasPapValidaciones(EtapaMetaFisicaPapRepository etapaMetaRepository,
            ProgramacionMetasFisicasPapCalculos calculos) {
        this.etapaMetaRepository = etapaMetaRepository;
        this.calculos = calculos;
    }

    /** Valida la programación enviada ("porEtapa") para las etapas del estudio, ANTES de escribir nada. */
    void validar(List<EtapaPreinversion> etapas, Map<TipoEtapaPreinversion, EtapaMetaFisicaRequestDto> porEtapa,
            Integer anio) {
        validarCamposAnexoA4(etapas, porEtapa, anio);
        validarPorcentajesContraCien(etapas, porEtapa, anio);
    }

    /**
     * Anexo B.1 (sección "Registro de la programación física por etapa de preinversión", Anexo
     * A.4) — validaciones de campo previas a RN-B.a, reportadas todas juntas como
     * {@code VALIDACION_NEGOCIO} con un {@code ErrorDetalle} por campo (CU-ADM-03):
     * <ul>
     * <li>"Entregable Unidad de Medida": campo obligatorio. En una etapa de arrastre el campo está
     * deshabilitado y se toma el ya registrado en ejercicios anteriores (SF-1 paso 2), por lo que
     * se valida el valor efectivo, no el enviado.</li>
     * <li>I/II/III Cuatrimestre: "valores entre 0.00% y 100%", cada uno por separado.</li>
     * <li>"Debe registrar en al menos un cuatrimestre": como el contrato define 0 por defecto en
     * los tres campos, se interpreta como al menos un cuatrimestre con porcentaje mayor que 0.</li>
     * </ul>
     */
    private void validarCamposAnexoA4(List<EtapaPreinversion> etapas,
            Map<TipoEtapaPreinversion, EtapaMetaFisicaRequestDto> porEtapa, Integer anio) {
        List<ErrorDetalleDto> detalles = new ArrayList<>();
        for (EtapaPreinversion etapa : etapas) {
            EtapaMetaFisicaRequestDto item = porEtapa.get(etapa.getTipoEtapa());
            if (item == null) {
                continue;
            }
            String prefijo = etapa.getTipoEtapa().name() + ".";
            if (calculos.entregableEfectivo(etapa, item, anio) == null) {
                detalles.add(new ErrorDetalleDto().campo(prefijo + "entregable").mensaje(CAMPO_OBLIGATORIO));
            }
            validarRangoCuatrimestre(prefijo + "montoCuatrimestre1", item.getMontoCuatrimestre1(), detalles);
            validarRangoCuatrimestre(prefijo + "montoCuatrimestre2", item.getMontoCuatrimestre2(), detalles);
            validarRangoCuatrimestre(prefijo + "montoCuatrimestre3", item.getMontoCuatrimestre3(), detalles);
            boolean algunCuatrimestreRegistrado = ProgramacionPapSoporte.bd(item.getMontoCuatrimestre1()).signum() > 0
                    || ProgramacionPapSoporte.bd(item.getMontoCuatrimestre2()).signum() > 0
                    || ProgramacionPapSoporte.bd(item.getMontoCuatrimestre3()).signum() > 0;
            if (!algunCuatrimestreRegistrado) {
                detalles.add(new ErrorDetalleDto().campo(prefijo + "programacionCuatrimestral")
                        .mensaje("Debe registrar en al menos un cuatrimestre."));
            }
        }
        if (!detalles.isEmpty()) {
            throw new ValidacionNegocioException(
                    "Existen campos obligatorios o con valores inválidos en la programación física por etapa.",
                    detalles);
        }
    }

    private static void validarRangoCuatrimestre(String campo, Double valor, List<ErrorDetalleDto> detalles) {
        BigDecimal porcentaje = ProgramacionPapSoporte.bd(valor);
        if (porcentaje.signum() < 0 || porcentaje.compareTo(ProgramacionMetasFisicasPapCalculos.CIEN) > 0) {
            detalles.add(new ErrorDetalleDto().campo(campo).mensaje("El porcentaje debe estar entre 0.00% y 100%."));
        }
    }

    private void validarPorcentajesContraCien(List<EtapaPreinversion> etapas,
            Map<TipoEtapaPreinversion, EtapaMetaFisicaRequestDto> porEtapa, Integer anio) {
        for (EtapaPreinversion etapa : etapas) {
            EtapaMetaFisicaRequestDto item = porEtapa.get(etapa.getTipoEtapa());
            if (item == null) {
                continue;
            }
            validarPorcentajeEtapa(etapa, item, anio);
        }
    }

    /**
     * RN-B.a: el literal aplicable (a.1 arrastre / a.2 nuevo) se decide POR ETAPA, no por
     * proyecto — un mismo proyecto puede combinar una etapa de arrastre con una etapa nueva
     * (p.ej. Perfil ya programado en años anteriores y Prefactibilidad agregada este año, caso
     * mostrado en el mockup del Anexo A.1), y cada una debe recibir su propio mensaje.
     */
    private void validarPorcentajeEtapa(EtapaPreinversion etapa, EtapaMetaFisicaRequestDto item, Integer anio) {
        BigDecimal total = ProgramacionMetasFisicasPapCalculos.suma(item);
        Optional<EtapaMetaFisicaPap> etapaMetaOpt = etapaMetaRepository.findByEtapaPreinversionId(etapa.getId());
        BigDecimal ejecutadoAnterior = etapaMetaOpt
                .map(etapaMeta -> calculos.ejecutadoAniosAnteriores(etapaMeta.getId(), anio))
                .orElse(BigDecimal.ZERO);
        BigDecimal limite = ProgramacionMetasFisicasPapCalculos.CIEN.subtract(ejecutadoAnterior);
        if (total.compareTo(limite) <= 0) {
            return;
        }
        boolean esArrastreEtapa = etapaMetaOpt.map(etapaMeta -> calculos.esArrastreEtapa(etapaMeta, anio))
                .orElse(false);
        if (esArrastreEtapa) {
            throw new ValidacionNegocioException("PORCENTAJE_SUPERA_100_ARRASTRE",
                    "Porcentaje Programado supera el 100% de la etapa.", null);
        }
        throw new ValidacionNegocioException("MONTO_SUPERA_100_NUEVO", "Monto Programado supera el 100%.", null);
    }
}
