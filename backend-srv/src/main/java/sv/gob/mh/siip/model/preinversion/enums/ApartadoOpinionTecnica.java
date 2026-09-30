package sv.gob.mh.siip.model.preinversion.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Filas de la tabla "APARTADOS / COMENTARIOS DGICP / JUSTIFICACIÓN INSTITUCIÓN" de la pantalla de
 * Opinión Técnica (CU-PRE-26, Anexo A.1), con la pantalla donde se registró cada apartado (ícono de
 * lápiz, RN16).
 *
 * <p>El formulario estándar sigue la lista "Campos requeridos" del CU (1.1 a 4.2); el mockup extendido
 * del archivo anexo agrega apartados que no están en esa lista y la definitiva sigue pendiente
 * (Observaciones ítem 42). Los proyectos de emergencia usan los campos del Anexo A.4 de CU-PRE-3.5
 * (RN13). La ruta es el segmento de la pantalla en el cliente, bajo {@code /preinversion/proyectos/{id}/};
 * {@code null} si esa pantalla todavía no tiene ruta propia.
 */
public enum ApartadoOpinionTecnica {

    ANTECEDENTES("1.1", "Antecedentes", Seccion.IDENTIFICACION, Pantalla.CU04, Pantalla.IDENTIFICACION),
    PROBLEMA_CENTRAL("1.2", "Problema Central", Seccion.IDENTIFICACION, Pantalla.CU04, Pantalla.IDENTIFICACION),
    OBJETIVO_GENERAL("1.3", "Objetivo General", Seccion.IDENTIFICACION, Pantalla.CU04, Pantalla.IDENTIFICACION),
    OBJETIVOS_ESPECIFICOS("1.4", "Objetivos Específicos", Seccion.IDENTIFICACION, Pantalla.CU04,
            Pantalla.IDENTIFICACION),
    ANALISIS_INTERESADOS("2.1", "Análisis de Interesados", Seccion.FORMULACION, "CU-PRE-06", Pantalla.DIAGNOSTICO),
    ANALISIS_POBLACION("2.2", "Análisis de la Población", Seccion.FORMULACION, "CU-PRE-07", Pantalla.DIAGNOSTICO),
    AREA_INFLUENCIA("2.3", "Área de Influencia", Seccion.FORMULACION, "CU-PRE-08", Pantalla.DIAGNOSTICO),
    ANALISIS_MERCADO("2.4", "Análisis de Mercado", Seccion.FORMULACION, "CU-PRE-09", Pantalla.DIAGNOSTICO),
    DESCRIPCION_TECNICA("2.5", "Descripción Técnica", Seccion.FORMULACION, "CU-PRE-11", Pantalla.ESTUDIO_TECNICO),
    LOCALIZACION("2.6", "Localización", Seccion.FORMULACION, "CU-PRE-12", Pantalla.ESTUDIO_TECNICO),
    ANALISIS_AMBIENTAL("2.7", "Análisis Ambiental", Seccion.FORMULACION, "CU-PRE-14", "analisis-ambiental"),
    ANALISIS_RIESGOS("2.8", "Análisis de Riesgos", Seccion.FORMULACION, "CU-PRE-15", "analisis-riesgo"),
    ANALISIS_LEGAL("2.9", "Análisis Legal", Seccion.FORMULACION, "CU-PRE-16", "analisis-legal"),
    PRESUPUESTO_INVERSION("2.10", "Presupuesto de Inversión", Seccion.FORMULACION, "CU-PRE-17", "presupuesto"),
    PRESUPUESTO_OM("2.11", "Presupuesto de O&M", Seccion.FORMULACION, "CU-PRE-18", "presupuesto-om"),
    FLUJO_BENEFICIOS("3.1", "Flujo de Beneficios", Seccion.EVALUACION, "CU-PRE-20", "beneficios"),
    FLUJO_CAJA_INDICADORES("3.2", "Flujo de Caja e Indicadores", Seccion.EVALUACION, "CU-PRE-21", null),
    INDICADORES_PROYECTO("4.1", "Indicadores del proyecto", Seccion.PROGRAMACION, "CU-PRE-23", null),
    PROGRAMACION_FINANCIERA_PREINVERSION("4.2", "Programación Financiera Preinversión", Seccion.PROGRAMACION,
            "CU-PRE-22.1", null),

    EMERGENCIA_PROBLEMA("E.1", "Planteamiento del problema"),
    EMERGENCIA_OBJETIVO_GENERAL("E.2", "Objetivo General"),
    EMERGENCIA_DESCRIPCION("E.3", "Descripción del proyecto"),
    EMERGENCIA_PRODUCTOS("E.4", "Productos"),
    EMERGENCIA_LOCALIZACION("E.5", "Localización"),
    EMERGENCIA_POBLACION("E.6", "Población Objetivo"),
    EMERGENCIA_PRESUPUESTO("E.7", "Inversión estimada y presupuesto"),
    EMERGENCIA_COSTOS_OM("E.8", "Costos de operación y mantenimiento"),
    EMERGENCIA_FINANCIAMIENTO("E.9", "Fuentes de financiamiento y recursos");

    /** Sección agrupadora de la tabla (desplegables I a IV del Anexo A.1). */
    public enum Seccion {
        IDENTIFICACION("Identificación"),
        FORMULACION("Formulación del proyecto"),
        EVALUACION("Evaluación del proyecto"),
        PROGRAMACION("Programación del proyecto"),
        EMERGENCIA("Ficha de emergencia");

        private final String etiquetaUi;

        Seccion(String etiquetaUi) {
            this.etiquetaUi = etiquetaUi;
        }

        public String getEtiquetaUi() {
            return etiquetaUi;
        }
    }

    /** Pantallas de origen que comparten varios apartados. */
    private static final class Pantalla {
        private static final String CU04 = "CU-PRE-04";
        private static final String IDENTIFICACION = "identificacion";
        private static final String DIAGNOSTICO = "diagnostico";
        private static final String ESTUDIO_TECNICO = "estudio-tecnico";
        private static final String CU_EMERGENCIA = "CU-PRE-03.5";
        private static final String EMERGENCIA = "ficha-emergencia";

        private Pantalla() {
        }
    }

    private final String codigo;
    private final String nombre;
    private final Seccion seccion;
    private final String casoUso;
    private final String ruta;

    ApartadoOpinionTecnica(String codigo, String nombre, Seccion seccion, String casoUso, String ruta) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.seccion = seccion;
        this.casoUso = casoUso;
        this.ruta = ruta;
    }

    /** Apartado de la ficha de emergencia de CU-PRE-3.5 (RN13). */
    ApartadoOpinionTecnica(String codigo, String nombre) {
        this(codigo, nombre, Seccion.EMERGENCIA, Pantalla.CU_EMERGENCIA, Pantalla.EMERGENCIA);
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public Seccion getSeccion() {
        return seccion;
    }

    public String getCasoUso() {
        return casoUso;
    }

    /** @return segmento de ruta del cliente, o {@code null} si la pantalla aún no tiene ruta propia */
    public String getRuta() {
        return ruta;
    }

    /**
     * @param emergencia si el proyecto está categorizado como "Proyecto de emergencia" (RN13)
     * @return los apartados del formulario, en el orden de la pantalla
     */
    public static List<ApartadoOpinionTecnica> delFormulario(boolean emergencia) {
        return Arrays.stream(values())
                .filter(a -> (a.seccion == Seccion.EMERGENCIA) == emergencia)
                .toList();
    }

    /**
     * Campos que la Actualización de OT habilita (CU-PRE-26, FA04 paso 4.4). La hoja "Campos a habilitar
     * para Actualización de O.T." de CU-PRE-3.5 no está incluida en el CU: mientras tanto se habilitan
     * las pantallas de formulación que revisa la OT, identificadas por su caso de uso.
     *
     * @param emergencia si el proyecto está categorizado como "Proyecto de emergencia" (RN13)
     * @return los casos de uso de las pantallas del formulario, sin repetir
     */
    public static List<String> casosDeUso(boolean emergencia) {
        return delFormulario(emergencia).stream()
                .map(ApartadoOpinionTecnica::getCasoUso)
                .distinct()
                .toList();
    }
}
