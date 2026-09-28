package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.preinversion.domain.IndicadorEvaluacion;
import sv.gob.mh.siip.model.preinversion.dto.IndicadorEvaluacionDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoIndicador;
import sv.gob.mh.siip.model.preinversion.repository.IndicadorEvaluacionRepository;

/**
 * Campo "Indicadores de evaluación" de la ficha de Viabilidad (CU-PRE-24, Anexo B.1), con los
 * cálculos registrados en CU-PRE-21.
 */
@Component
@Transactional(readOnly = true)
public class FichaViabilidadIndicadores {

    /** Etiquetas de los indicadores que muestra el mockup del Anexo A.1, en su orden. */
    private static final String INDICADOR_VAN = "VAN";
    private static final String INDICADOR_TIR = "TIR";
    private static final String INDICADOR_RBC = "R B/C";

    private final IndicadorEvaluacionRepository indicadores;

    public FichaViabilidadIndicadores(IndicadorEvaluacionRepository indicadores) {
        this.indicadores = indicadores;
    }

    /**
     * "Indicadores de evaluación" (CU-PRE-21): siempre incluye VAN, TIR y R B/C, como en el mockup
     * del Anexo A.1, con valor nulo si todavía no se calcularon; cualquier otro indicador calculado
     * se agrega después. De cada tipo se toma el cálculo más reciente.
     *
     * @param idProyecto identificador del proyecto
     * @return los indicadores a mostrar, en su orden
     */
    public List<IndicadorEvaluacionDto> indicadoresEvaluacion(Long idProyecto) {
        Map<TipoIndicador, IndicadorEvaluacion> ultimos = indicadores.findByProyectoId(idProyecto).stream()
                .filter(i -> i.getTipoIndicador() != null)
                .collect(Collectors.toMap(IndicadorEvaluacion::getTipoIndicador, Function.identity(),
                        FichaViabilidadIndicadores::masReciente, () -> new EnumMap<>(TipoIndicador.class)));

        List<IndicadorEvaluacionDto> resultado = new ArrayList<>();
        resultado.add(indicador(INDICADOR_VAN, ultimos.remove(TipoIndicador.VAN)));
        resultado.add(indicador(INDICADOR_TIR, ultimos.remove(TipoIndicador.TIR)));
        resultado.add(indicador(INDICADOR_RBC, ultimos.remove(TipoIndicador.RELACION_BENEFICIO_COSTO)));
        ultimos.forEach((tipo, calculo) -> resultado.add(indicador(tipo.name(), calculo)));
        return resultado;
    }

    private static IndicadorEvaluacion masReciente(IndicadorEvaluacion a, IndicadorEvaluacion b) {
        Comparator<IndicadorEvaluacion> porFecha = Comparator.comparing(IndicadorEvaluacion::getFechaCalculo,
                Comparator.nullsFirst(Comparator.naturalOrder()));
        return porFecha.compare(a, b) >= 0 ? a : b;
    }

    private static IndicadorEvaluacionDto indicador(String nombre, IndicadorEvaluacion calculo) {
        return new IndicadorEvaluacionDto(nombre).valor(calculo == null ? null : calculo.getValor());
    }
}
