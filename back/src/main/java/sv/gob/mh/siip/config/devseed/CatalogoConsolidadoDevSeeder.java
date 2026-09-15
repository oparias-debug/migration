package sv.gob.mh.siip.config.devseed;

import java.util.LinkedHashMap;
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
 */
@Component
@Profile("dev")
@Order(26)
public class CatalogoConsolidadoDevSeeder implements DevSeeder {

    private static final Map<String, Double> PARAMETROS = new LinkedHashMap<>();
    static {
        // El propio contrato aclara que el catálogo real (no el mockup) lista 1.00 para todos.
        PARAMETROS.put("Valor social del tiempo", 1.00);
        PARAMETROS.put("Valor social de la mano de obra", 1.00);
        PARAMETROS.put("Tasa social de descuento", 1.00);
        PARAMETROS.put("Precio social de la divisa", 1.00);
    }

    private record FilaIndicador(String codigo, String nombre, String descripcion, String unidadMedida) {
    }

    private static final List<FilaIndicador> INDICADORES_RESULTADO = List.of(
            new FilaIndicador("IND-01", "Número de beneficiarios directos",
                    "Personas que reciben un beneficio directo del proyecto.", "Personas"),
            new FilaIndicador("IND-02", "Metros cuadrados construidos", null, "Metro cuadrado"),
            new FilaIndicador("IND-03", "Kilómetros de vía rehabilitados", null, "Kilómetro"),
            new FilaIndicador("IND-04", "Cobertura del servicio ampliada",
                    "Porcentaje de la población objetivo que queda cubierta por el servicio.", "Porcentaje"));

    private record FilaRango(String categoria, double puntajeMinimo, double puntajeMaximo, String implicacion) {
    }

    private static final List<FilaRango> RANGOS_INTERPRETACION = List.of(
            new FilaRango("Baja prioridad", 0, 25, "El proyecto requiere una revisión sustancial antes de continuar."),
            new FilaRango("Prioridad media", 26, 50, "El proyecto es viable pero requiere ajustes."),
            new FilaRango("Prioridad alta", 51, 75, "El proyecto está en buena posición para avanzar."),
            new FilaRango("Prioridad muy alta", 76, 100, "El proyecto es prioritario para su ejecución inmediata."));

    private record FilaSubcriterio(String codigo, String numero, String nombre, double ponderacion) {
    }

    private record FilaCriterio(String codigo, int numero, String nombre, double ponderacion,
            List<FilaSubcriterio> subcriterios) {
    }
    private static final String ALINEACION_ESTRATEGICA = "Alineación estratégica";

    private static final List<FilaCriterio> CRITERIOS_PRIORIZACION = List.of(
            new FilaCriterio("CRIT-1", 1, ALINEACION_ESTRATEGICA, 30.0, List.of(
                    new FilaSubcriterio("SUB-1.1", "1.1", "Contribución al Plan Cuscatlán", 15.0),
                    new FilaSubcriterio("SUB-1.2", "1.2", "Contribución a los ODS", 15.0))),
            new FilaCriterio("CRIT-2", 2, "Impacto social", 30.0, List.of(
                    new FilaSubcriterio("SUB-2.1", "2.1", "Población beneficiada", 15.0),
                    new FilaSubcriterio("SUB-2.2", "2.2", "Grupos vulnerables atendidos", 15.0))),
            new FilaCriterio("CRIT-3", 3, "Viabilidad técnica y financiera", 25.0, List.of(
                    new FilaSubcriterio("SUB-3.1", "3.1", "Madurez del estudio de preinversión", 15.0),
                    new FilaSubcriterio("SUB-3.2", "3.2", "Disponibilidad de financiamiento", 10.0))),
            new FilaCriterio("CRIT-4", 4, "Sostenibilidad ambiental", 15.0, List.of(
                    new FilaSubcriterio("SUB-4.1", "4.1", "Gestión de riesgo ambiental", 15.0))));

    private static final Map<ValorCalificacion, String> ESCALA_CALIFICACION = new LinkedHashMap<>();
    static {
        ESCALA_CALIFICACION.put(ValorCalificacion.NO_APLICA, "El subcriterio no aplica a este proyecto.");
        ESCALA_CALIFICACION.put(ValorCalificacion.CERO, "No cumple con el subcriterio.");
        ESCALA_CALIFICACION.put(ValorCalificacion.UNO, "Cumple mínimamente con el subcriterio.");
        ESCALA_CALIFICACION.put(ValorCalificacion.DOS, "Cumple parcialmente con el subcriterio.");
        ESCALA_CALIFICACION.put(ValorCalificacion.TRES, "Cumple aceptablemente con el subcriterio.");
        ESCALA_CALIFICACION.put(ValorCalificacion.CUATRO, "Cumple satisfactoriamente con el subcriterio.");
        ESCALA_CALIFICACION.put(ValorCalificacion.CINCO, "Cumple plenamente con el subcriterio.");
    }

    private record FilaCriterioElegibilidad(String codigo, String dimension, String criterio,
            TipoEspecificar tipoEspecificar, TipoCatalogoEspecificar catalogoEspecificar,
            boolean permiteSeleccionMultiple) {
    }

    private static final List<FilaCriterioElegibilidad> CRITERIOS_ELEGIBILIDAD = List.of(
            new FilaCriterioElegibilidad("ELEG-01", ALINEACION_ESTRATEGICA,
                    "¿El proyecto contribuye a algún Objetivo de Desarrollo Sostenible (ODS)?",
                    TipoEspecificar.CATALOGO, TipoCatalogoEspecificar.ODS, true),
            new FilaCriterioElegibilidad("ELEG-02", ALINEACION_ESTRATEGICA,
                    "¿El proyecto está alineado a un Eje del Plan de Gobierno?", TipoEspecificar.CATALOGO,
                    TipoCatalogoEspecificar.EJE_PLAN_GOBIERNO, false),
            new FilaCriterioElegibilidad("ELEG-03", "Impacto social",
                    "¿El proyecto beneficia a algún grupo poblacional vulnerable?", TipoEspecificar.CATALOGO,
                    TipoCatalogoEspecificar.GRUPO_POBLACIONAL_VULNERABLE, true),
            new FilaCriterioElegibilidad("ELEG-04", "Impacto ambiental",
                    "¿El proyecto incorpora medidas de Gestión de Riesgo de Desastres?", TipoEspecificar.CATALOGO,
                    TipoCatalogoEspecificar.MEDIDA_GRD, true),
            new FilaCriterioElegibilidad("ELEG-05", "Elegibilidad general",
                    "¿El proyecto cuenta con Ficha Técnica completa?", TipoEspecificar.SI_NO, null, false),
            new FilaCriterioElegibilidad("ELEG-06", "Elegibilidad general",
                    "Justificación adicional de elegibilidad (si aplica).", TipoEspecificar.TEXTO_LIBRE, null,
                    false));

    private record FilaEntradaEspecificar(TipoCatalogoEspecificar tipo, String codigo, String nombre) {
    }

    private static final List<FilaEntradaEspecificar> ENTRADAS_ESPECIFICAR = List.of(
            new FilaEntradaEspecificar(TipoCatalogoEspecificar.ODS, "ODS-01", "ODS 1: Fin de la pobreza"),
            new FilaEntradaEspecificar(TipoCatalogoEspecificar.ODS, "ODS-03", "ODS 3: Salud y bienestar"),
            new FilaEntradaEspecificar(TipoCatalogoEspecificar.ODS, "ODS-04", "ODS 4: Educación de calidad"),
            new FilaEntradaEspecificar(TipoCatalogoEspecificar.COMPONENTE_MEDIO_AMBIENTE, "CMA-01",
                    "Recurso hídrico"),
            new FilaEntradaEspecificar(TipoCatalogoEspecificar.COMPONENTE_MEDIO_AMBIENTE, "CMA-02",
                    "Calidad del aire"),
            new FilaEntradaEspecificar(TipoCatalogoEspecificar.GRUPO_POBLACIONAL_VULNERABLE, "GPV-01",
                    "Niñez y adolescencia"),
            new FilaEntradaEspecificar(TipoCatalogoEspecificar.GRUPO_POBLACIONAL_VULNERABLE, "GPV-02",
                    "Personas con discapacidad"),
            new FilaEntradaEspecificar(TipoCatalogoEspecificar.MEJORA_CALIDAD_VIDA, "MCV-01",
                    "Reducción del tiempo de traslado"));

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
        PARAMETROS.forEach((nombre, factorCorreccion) -> {
            if (parametroRepository.findByCodigo(nombre).isEmpty()) {
                parametroRepository.save(Parametro.builder().codigo(nombre).nombre(nombre)
                        .factorCorreccion(factorCorreccion).build());
            }
        });
    }

    private void sembrarIndicadoresResultado() {
        for (FilaIndicador fila : INDICADORES_RESULTADO) {
            if (indicadorResultadoRepository.findByCodigo(fila.codigo()).isEmpty()) {
                indicadorResultadoRepository.save(IndicadorResultado.builder().codigo(fila.codigo())
                        .nombre(fila.nombre()).descripcion(fila.descripcion()).unidadMedida(fila.unidadMedida())
                        .build());
            }
        }
    }

    private void sembrarRangosInterpretacion() {
        for (FilaRango fila : RANGOS_INTERPRETACION) {
            if (rangoInterpretacionPriorizacionRepository.findByCategoria(fila.categoria()).isEmpty()) {
                rangoInterpretacionPriorizacionRepository.save(RangoInterpretacionPriorizacion.builder()
                        .puntajeMinimo(fila.puntajeMinimo()).puntajeMaximo(fila.puntajeMaximo())
                        .categoria(fila.categoria()).implicacion(fila.implicacion()).build());
            }
        }
    }

    private void sembrarCriteriosYSubcriteriosPriorizacion() {
        for (FilaCriterio filaCriterio : CRITERIOS_PRIORIZACION) {
            if (criterioPriorizacionRepository.findByCodigo(filaCriterio.codigo()).isPresent()) {
                continue;
            }
            CriterioPriorizacion criterio = criterioPriorizacionRepository.save(CriterioPriorizacion.builder()
                    .codigo(filaCriterio.codigo()).numeroCriterio(filaCriterio.numero())
                    .nombreCriterio(filaCriterio.nombre()).ponderacionCriterio(filaCriterio.ponderacion()).build());

            for (FilaSubcriterio filaSubcriterio : filaCriterio.subcriterios()) {
                SubcriterioPriorizacion subcriterio = subcriterioPriorizacionRepository
                        .save(SubcriterioPriorizacion.builder().criterio(criterio)
                                .codigo(filaSubcriterio.codigo()).numero(filaSubcriterio.numero())
                                .nombre(filaSubcriterio.nombre())
                                .ponderacionSubcriterio(filaSubcriterio.ponderacion()).build());
                sembrarEscalaCalificacion(subcriterio.getCodigo());
            }
        }
    }

    private void sembrarEscalaCalificacion(String codigoSubcriterio) {
        ESCALA_CALIFICACION.forEach((valor, descripcion) -> {
            if (escalaCalificacionSubcriterioRepository.findByCodigoSubcriterioAndValor(codigoSubcriterio, valor)
                    .isEmpty()) {
                escalaCalificacionSubcriterioRepository.save(EscalaCalificacionSubcriterio.builder()
                        .codigoSubcriterio(codigoSubcriterio).valor(valor).descripcion(descripcion).build());
            }
        });
    }

    private void sembrarCriteriosElegibilidad() {
        for (FilaCriterioElegibilidad fila : CRITERIOS_ELEGIBILIDAD) {
            if (criterioElegibilidadRepository.findByCodigo(fila.codigo()).isEmpty()) {
                criterioElegibilidadRepository.save(CriterioElegibilidad.builder().codigo(fila.codigo())
                        .dimension(fila.dimension()).criterio(fila.criterio())
                        .tipoEspecificar(fila.tipoEspecificar()).catalogoEspecificar(fila.catalogoEspecificar())
                        .permiteSeleccionMultiple(fila.permiteSeleccionMultiple()).build());
            }
        }
    }

    private void sembrarEntradasEspecificar() {
        for (FilaEntradaEspecificar fila : ENTRADAS_ESPECIFICAR) {
            if (entradaCatalogoEspecificarRepository.findByTipoAndCodigo(fila.tipo(), fila.codigo()).isEmpty()) {
                entradaCatalogoEspecificarRepository.save(EntradaCatalogoEspecificar.builder().tipo(fila.tipo())
                        .codigo(fila.codigo()).nombre(fila.nombre()).build());
            }
        }
    }
}
