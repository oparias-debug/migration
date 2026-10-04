package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.List;

import sv.gob.mh.siip.model.preinversion.domain.FilaAnalisisMercado;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisMercadoDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaAnalisisMercadoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoSeleccionadoDto;

/** Cálculos de CU-PRE-09 "Análisis de Mercado": déficit y promedios, siempre en el servidor (Anexo B.1). */
final class CalculoAnalisisMercado {

    private CalculoAnalisisMercado() {
    }

    /**
     * @param idProyecto identificador del proyecto
     * @param filas filas guardadas
     * @return el análisis con déficit y promedios calculados (Anexo B.1)
     */
    static AnalisisMercadoDto respuesta(Long idProyecto, List<FilaAnalisisMercado> filas) {
        List<FilaAnalisisMercadoDto> resultado = new ArrayList<>();
        for (FilaAnalisisMercado fila : filas) {
            ProductoSeleccionadoDto producto = new ProductoSeleccionadoDto()
                    .codigoProducto(fila.getCodigoProducto()).producto(fila.getProducto());
            Double deficit = calcularDeficit(fila.getDemanda(), fila.getOferta());
            Double promedioDemanda = promedioProyectado(fila.getDemanda(), fila.getTasaDemanda(),
                    fila.getAniosAProyectar());
            Double promedioOferta = promedioProyectado(fila.getOferta(), fila.getTasaOferta(),
                    fila.getAniosAProyectar());
            resultado.add(new FilaAnalisisMercadoDto()
                    .producto(producto)
                    .unidadMedida(fila.getUnidadMedida())
                    .demanda(fila.getDemanda())
                    .oferta(fila.getOferta())
                    .deficit(deficit)
                    .aniosAProyectar(fila.getAniosAProyectar())
                    .tasaDemanda(fila.getTasaDemanda())
                    .tasaOferta(fila.getTasaOferta())
                    .promedioDemanda(promedioDemanda)
                    .promedioOferta(promedioOferta)
                    .promedioDeficit(calcularDeficit(promedioDemanda, promedioOferta)));
        }
        return new AnalisisMercadoDto().idProyecto(idProyecto).filas(resultado);
    }

    private static Double calcularDeficit(Double demanda, Double oferta) {
        return (demanda == null || oferta == null) ? null : (demanda - oferta);
    }

    /**
     * Anexo B.1: promedio del año base y de los años proyectados,
     * {@code (V₀ + V₀(1+r)¹ + … + V₀(1+r)ᵗ) / (t + 1)}, con {@code r} en porcentaje y {@code t} los años a
     * proyectar. Es el promedio, no el valor del último año ({@code V₀(1+r)ᵗ}).
     */
    static Double promedioProyectado(Double base, Double tasa, Integer anios) {
        if (base == null || tasa == null || anios == null || anios < 0) {
            return null;
        }
        double factor = 1.0 + (tasa / ValidacionAnalisisMercado.PORCENTAJE_TOTAL);
        double suma = 0;
        for (var n = 0; n <= anios; n++) {
            suma += base * Math.pow(factor, n);
        }
        return suma / (anios + 1);
    }
}
