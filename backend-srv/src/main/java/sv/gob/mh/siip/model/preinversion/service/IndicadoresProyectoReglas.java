package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;
import java.util.Objects;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.IndicadorProductoRequestDto;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.IndicadorResultadoRequestDto;

/** Validaciones y aritmética de metas de CU-PRE-23, sin estado. */
final class IndicadoresProyectoReglas {

    private static final double TOLERANCIA = 0.00001D;

    private IndicadoresProyectoReglas() {
    }

    static void validarResultado(IndicadorResultadoRequestDto request) {
        if (request == null || esBlanco(request.getNombreIndicador()) || invalido(request.getMetaGlobal())) {
            throw validacion("Nombre del indicador y meta global son obligatorios.");
        }
    }

    static void validarProducto(IndicadorProductoRequestDto request) {
        if (request == null || esBlanco(request.getNombreIndicador()) || invalido(request.getMetaGlobal())
                || request.getMetaEsAcumulativa() == null || request.getMetasPorPeriodo() == null
                || request.getMetasPorPeriodo().stream().allMatch(Objects::isNull)) {
            throw validacion("Los datos del indicador de producto son obligatorios.");
        }
        for (Double meta : request.getMetasPorPeriodo()) {
            if (invalido(meta)) {
                throw validacion("Las metas por período deben ser valores no negativos.");
            }
        }
    }

    static boolean invalido(Double valor) {
        return valor == null || !Double.isFinite(valor) || valor < 0D;
    }

    static boolean esBlanco(String valor) {
        return valor == null || valor.isBlank();
    }

    static double sumar(List<Double> valores) {
        return valores.stream().filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum();
    }

    static boolean mismaCantidad(Double primero, Double segundo) {
        return primero != null && segundo != null && Math.abs(primero - segundo) < TOLERANCIA;
    }

    static ValidacionNegocioException validacion(String mensaje) {
        return new ValidacionNegocioException(mensaje, List.of());
    }

    static RecursoNoEncontradoException noEncontrado(String mensaje) {
        return new RecursoNoEncontradoException(mensaje);
    }
}
