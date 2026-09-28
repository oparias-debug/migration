package sv.gob.mh.siip.config.devseed;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import sv.gob.mh.siip.model.preinversion.domain.CriterioElegibilidad;
import sv.gob.mh.siip.model.preinversion.domain.CriterioPriorizacion;
import sv.gob.mh.siip.model.preinversion.domain.EntradaCatalogoEspecificar;
import sv.gob.mh.siip.model.preinversion.domain.EscalaCalificacionSubcriterio;
import sv.gob.mh.siip.model.preinversion.domain.IndicadorResultado;
import sv.gob.mh.siip.model.preinversion.domain.Parametro;
import sv.gob.mh.siip.model.preinversion.domain.RangoInterpretacionPriorizacion;
import sv.gob.mh.siip.model.preinversion.domain.SubcriterioPriorizacion;
import sv.gob.mh.siip.model.preinversion.enums.TipoCatalogoEspecificar;
import sv.gob.mh.siip.model.preinversion.enums.TipoEspecificar;
import sv.gob.mh.siip.model.preinversion.enums.ValorCalificacion;
import sv.gob.mh.siip.model.preinversion.repository.CriterioElegibilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.CriterioPriorizacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.EntradaCatalogoEspecificarRepository;
import sv.gob.mh.siip.model.preinversion.repository.EscalaCalificacionSubcriterioRepository;
import sv.gob.mh.siip.model.preinversion.repository.IndicadorResultadoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ParametroRepository;
import sv.gob.mh.siip.model.preinversion.repository.RangoInterpretacionPriorizacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.SubcriterioPriorizacionRepository;

/**
 * Catálogos de CU-ADM-02-catalogos.openapi.yaml sin implementación previa: "Parámetros" (CU-PRE-20),
 * "Indicadores de Resultado" (CU-PRE-23), y los de elegibilidad/priorización (CU-PRE-25/26.5).
 * <p>
 * Ninguno de estos 4 CU de origen tiene su documento de análisis en este repositorio
 * (docs/casos-de-uso/1 - Preinversion/), y para Elegibilidad/Priorización el propio contrato
 * OpenAPI marca ⚠️ contenido bloqueado por ambigüedad de versión o pertenencia no confirmada al
 * CU — así que lo sembrado aquí es dato de prueba razonable, no el catálogo oficial ni
 * contenido "confirmado" (ni siquiera los Rangos de Interpretación, que el contrato describe como
 * confirmados: no se tuvo acceso al documento fuente real para transcribirlos).
 * <p>
 * Los valores viven en los CSV de {@code data/seed/} (ver las constantes {@code CSV_*} y
 * {@link CsvSeed}); esta clase solo los carga.
 */
@Component
@Profile("dev")
@Order(26)
public class CatalogoConsolidadoDevSeeder implements DevSeeder {

    public static final String CSV_PARAMETROS = "parametros.csv";
    public static final String CSV_INDICADORES_RESULTADO = "indicadores-resultado.csv";
    public static final String CSV_RANGOS_INTERPRETACION = "rangos-interpretacion.csv";
    public static final String CSV_CRITERIOS_PRIORIZACION = "criterios-priorizacion.csv";
    public static final String CSV_SUBCRITERIOS_PRIORIZACION = "subcriterios-priorizacion.csv";
    public static final String CSV_ESCALA_CALIFICACION = "escala-calificacion.csv";
    public static final String CSV_CRITERIOS_ELEGIBILIDAD = "criterios-elegibilidad.csv";
    public static final String CSV_ENTRADAS_ESPECIFICAR = "entradas-especificar.csv";

    private static final String COLUMNA_NOMBRE = "nombre";
    private static final String COLUMNA_CODIGO = "codigo";

    private final ParametroRepository parametroRepository;
    private final IndicadorResultadoRepository indicadorResultadoRepository;
    private final RangoInterpretacionPriorizacionRepository rangoInterpretacionPriorizacionRepository;
    private final CriterioPriorizacionRepository criterioPriorizacionRepository;
    private final SubcriterioPriorizacionRepository subcriterioPriorizacionRepository;
    private final EscalaCalificacionSubcriterioRepository escalaCalificacionSubcriterioRepository;
    private final CriterioElegibilidadRepository criterioElegibilidadRepository;
    private final EntradaCatalogoEspecificarRepository entradaCatalogoEspecificarRepository;

    public CatalogoConsolidadoDevSeeder(ParametroRepository parametroRepository,
            IndicadorResultadoRepository indicadorResultadoRepository,
            RangoInterpretacionPriorizacionRepository rangoInterpretacionPriorizacionRepository,
            CriterioPriorizacionRepository criterioPriorizacionRepository,
            SubcriterioPriorizacionRepository subcriterioPriorizacionRepository,
            EscalaCalificacionSubcriterioRepository escalaCalificacionSubcriterioRepository,
            CriterioElegibilidadRepository criterioElegibilidadRepository,
            EntradaCatalogoEspecificarRepository entradaCatalogoEspecificarRepository) {
        this.parametroRepository = parametroRepository;
        this.indicadorResultadoRepository = indicadorResultadoRepository;
        this.rangoInterpretacionPriorizacionRepository = rangoInterpretacionPriorizacionRepository;
        this.criterioPriorizacionRepository = criterioPriorizacionRepository;
        this.subcriterioPriorizacionRepository = subcriterioPriorizacionRepository;
        this.escalaCalificacionSubcriterioRepository = escalaCalificacionSubcriterioRepository;
        this.criterioElegibilidadRepository = criterioElegibilidadRepository;
        this.entradaCatalogoEspecificarRepository = entradaCatalogoEspecificarRepository;
    }

    @Override
    public void seed() {
        sembrarParametros();
        sembrarIndicadoresResultado();
        sembrarRangosInterpretacion();
        sembrarCriteriosYSubcriteriosPriorizacion();
        sembrarCriteriosElegibilidad();
        sembrarEntradasEspecificar();
    }

    private void sembrarParametros() {
        for (Map<String, String> fila : CsvSeed.leer(CSV_PARAMETROS)) {
            String nombre = fila.get(COLUMNA_NOMBRE);
            if (parametroRepository.findByCodigo(nombre).isEmpty()) {
                parametroRepository.save(Parametro.builder().codigo(nombre).nombre(nombre)
                        .factorCorreccion(Double.valueOf(fila.get("factor_correccion"))).build());
            }
        }
    }

    private void sembrarIndicadoresResultado() {
        for (Map<String, String> fila : CsvSeed.leer(CSV_INDICADORES_RESULTADO)) {
            String codigo = fila.get(COLUMNA_CODIGO);
            if (indicadorResultadoRepository.findByCodigo(codigo).isEmpty()) {
                indicadorResultadoRepository.save(IndicadorResultado.builder().codigo(codigo)
                        .nombre(fila.get(COLUMNA_NOMBRE)).descripcion(fila.get("descripcion"))
                        .unidadMedida(fila.get("unidad_medida")).build());
            }
        }
    }

    private void sembrarRangosInterpretacion() {
        for (Map<String, String> fila : CsvSeed.leer(CSV_RANGOS_INTERPRETACION)) {
            String categoria = fila.get("categoria");
            if (rangoInterpretacionPriorizacionRepository.findByCategoria(categoria).isEmpty()) {
                rangoInterpretacionPriorizacionRepository.save(RangoInterpretacionPriorizacion.builder()
                        .puntajeMinimo(Double.valueOf(fila.get("puntaje_minimo")))
                        .puntajeMaximo(Double.valueOf(fila.get("puntaje_maximo")))
                        .categoria(categoria).implicacion(fila.get("implicacion")).build());
            }
        }
    }

    private void sembrarCriteriosYSubcriteriosPriorizacion() {
        List<Map<String, String>> subcriterios = CsvSeed.leer(CSV_SUBCRITERIOS_PRIORIZACION);
        List<Map<String, String>> escala = CsvSeed.leer(CSV_ESCALA_CALIFICACION);
        for (Map<String, String> filaCriterio : CsvSeed.leer(CSV_CRITERIOS_PRIORIZACION)) {
            String codigoCriterio = filaCriterio.get(COLUMNA_CODIGO);
            if (criterioPriorizacionRepository.findByCodigo(codigoCriterio).isPresent()) {
                continue;
            }
            CriterioPriorizacion criterio = criterioPriorizacionRepository.save(CriterioPriorizacion.builder()
                    .codigo(codigoCriterio).numeroCriterio(Integer.valueOf(filaCriterio.get("numero")))
                    .nombreCriterio(filaCriterio.get(COLUMNA_NOMBRE))
                    .ponderacionCriterio(Double.valueOf(filaCriterio.get("ponderacion"))).build());

            for (Map<String, String> filaSubcriterio : subcriterios) {
                if (!codigoCriterio.equals(filaSubcriterio.get("codigo_criterio"))) {
                    continue;
                }
                SubcriterioPriorizacion subcriterio = subcriterioPriorizacionRepository
                        .save(SubcriterioPriorizacion.builder().criterio(criterio)
                                .codigo(filaSubcriterio.get(COLUMNA_CODIGO)).numero(filaSubcriterio.get("numero"))
                                .nombre(filaSubcriterio.get(COLUMNA_NOMBRE))
                                .ponderacionSubcriterio(Double.valueOf(filaSubcriterio.get("ponderacion")))
                                .build());
                sembrarEscalaCalificacion(subcriterio.getCodigo(), escala);
            }
        }
    }

    private void sembrarEscalaCalificacion(String codigoSubcriterio, List<Map<String, String>> escala) {
        for (Map<String, String> fila : escala) {
            ValorCalificacion valor = ValorCalificacion.valueOf(fila.get("valor"));
            if (escalaCalificacionSubcriterioRepository.findByCodigoSubcriterioAndValor(codigoSubcriterio, valor)
                    .isEmpty()) {
                escalaCalificacionSubcriterioRepository.save(EscalaCalificacionSubcriterio.builder()
                        .codigoSubcriterio(codigoSubcriterio).valor(valor).descripcion(fila.get("descripcion"))
                        .build());
            }
        }
    }

    private void sembrarCriteriosElegibilidad() {
        for (Map<String, String> fila : CsvSeed.leer(CSV_CRITERIOS_ELEGIBILIDAD)) {
            String codigo = fila.get(COLUMNA_CODIGO);
            if (criterioElegibilidadRepository.findByCodigo(codigo).isEmpty()) {
                String catalogo = fila.get("catalogo_especificar");
                criterioElegibilidadRepository.save(CriterioElegibilidad.builder().codigo(codigo)
                        .dimension(fila.get("dimension")).criterio(fila.get("criterio"))
                        .tipoEspecificar(TipoEspecificar.valueOf(fila.get("tipo_especificar")))
                        .catalogoEspecificar(catalogo == null ? null : TipoCatalogoEspecificar.valueOf(catalogo))
                        .permiteSeleccionMultiple(Boolean.parseBoolean(fila.get("permite_seleccion_multiple")))
                        .build());
            }
        }
    }

    private void sembrarEntradasEspecificar() {
        for (Map<String, String> fila : CsvSeed.leer(CSV_ENTRADAS_ESPECIFICAR)) {
            TipoCatalogoEspecificar tipo = TipoCatalogoEspecificar.valueOf(fila.get("tipo"));
            String codigo = fila.get(COLUMNA_CODIGO);
            if (entradaCatalogoEspecificarRepository.findByTipoAndCodigo(tipo, codigo).isEmpty()) {
                entradaCatalogoEspecificarRepository.save(EntradaCatalogoEspecificar.builder().tipo(tipo)
                        .codigo(codigo).nombre(fila.get(COLUMNA_NOMBRE)).build());
            }
        }
    }
}
