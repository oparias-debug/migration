package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.ToDoubleFunction;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import sv.gob.mh.siip.model.preinversion.dto.EstudioFilaAvancePAPDto;
import sv.gob.mh.siip.model.preinversion.dto.FuenteFinanciamientoDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;

/**
 * Genera el reporte del Avance Cuatrimestral Financiero del PAP (SF-3, Anexo A.6), en Excel o PDF:
 * "Institución Ejecutora", Año, Cuatrimestre, la misma tabla del Anexo A.1 (incluida la fila TOTAL)
 * y el campo "Comentarios al reporte financiero DGICP" (solo para actores internos DGICP).
 */
final class ReporteAvanceFinancieroPapGenerator {

    public static final String ETIQUETA_COMENTARIOS = "Comentarios al reporte financiero DGICP:";

    /** Columnas del Anexo A.1 (Avance Anual, Avance al Cuatrimestre y Avance del Cuatrimestre + Observaciones). */
    private static final String[] ENCABEZADOS = {
            "CUP", "Nombre del proyecto", "Etapa", "Fuente de Financiamiento", "Costo de la etapa",
            "Ejecutado años anteriores",
            "Avance Anual Programado", "Avance Anual Ejecutado", "Avance Anual %",
            "Avance al Cuatrimestre Programado", "Avance al Cuatrimestre Ejecutado", "Avance al Cuatrimestre %",
            "Avance del Cuatrimestre Programado", "Avance del Cuatrimestre Ejecutado", "Avance del Cuatrimestre %",
            "Observaciones del Cuatrimestre"
    };

    private static final int COL_CUP = 0;
    private static final int COL_NOMBRE_PROYECTO = 1;
    private static final int COL_ETAPA = 2;
    private static final int COL_FUENTE_FINANCIAMIENTO = 3;
    private static final int PRIMERA_COLUMNA_VALOR = 4;
    private static final int COLUMNA_OBSERVACIONES = ENCABEZADOS.length - 1;
    /** Columnas del renglón de "Comentarios al reporte financiero DGICP": etiqueta y texto. */
    private static final int COL_ETIQUETA_COMENTARIOS = 0;
    private static final int COL_TEXTO_COMENTARIOS = 1;
    /** Distancia (en filas) entre la fila TOTAL y la de comentarios DGICP (deja una fila en blanco). */
    private static final int FILAS_ENTRE_TOTAL_Y_COMENTARIOS = 2;

    /** Saltos de línea/tabuladores, no admitidos por PDFBox dentro de {@code showText}. */
    private static final Pattern SALTOS_DE_LINEA = Pattern.compile("[\\r\\n\\t]+");

    /**
     * Valores de las columnas numéricas (un {@code null} cuenta como 0), en el orden de
     * {@link #ENCABEZADOS} desde "Costo de la etapa".
     */
    private static final List<ToDoubleFunction<EstudioFilaAvancePAPDto>> VALORES = List.of(
            fila -> ReportePapGeneratorSupport.valorODefecto(fila.getCostoEtapa()),
            fila -> ReportePapGeneratorSupport.valorODefecto(fila.getEjecutadoAniosAnteriores()),
            fila -> ReportePapGeneratorSupport.valorODefecto(fila.getAvanceAnualProgramado()),
            fila -> ReportePapGeneratorSupport.valorODefecto(fila.getAvanceAnualEjecutadoMonto()),
            fila -> ReportePapGeneratorSupport.valorODefecto(fila.getAvanceAnualEjecutadoPorcentaje()),
            fila -> ReportePapGeneratorSupport.valorODefecto(fila.getAvanceAlCuatrimestreProgramado()),
            fila -> ReportePapGeneratorSupport.valorODefecto(fila.getAvanceAlCuatrimestreEjecutadoMonto()),
            fila -> ReportePapGeneratorSupport.valorODefecto(fila.getAvanceAlCuatrimestreEjecutadoPorcentaje()),
            fila -> ReportePapGeneratorSupport.valorODefecto(fila.getAvanceDelCuatrimestreProgramado()),
            fila -> ReportePapGeneratorSupport.valorODefecto(fila.getAvanceDelCuatrimestreEjecutadoMonto()),
            fila -> ReportePapGeneratorSupport.valorODefecto(fila.getAvanceDelCuatrimestrePorcentaje()));

    /** RN-E "Total General": solo se totalizan las columnas de montos, no los porcentajes. */
    private static final boolean[] ES_MONTO = {
            true, true, true, true, false, true, true, false, true, true, false
    };

    /** Datos de cabecera/pie del Anexo A.6. {@code comentariosDgicp} solo aplica si {@code mostrarComentarios}. */
    record Encabezado(String institucionEjecutora, Integer anio, Cuatrimestre periodo, boolean mostrarComentarios,
            String comentariosDgicp) {
    }

    private ReporteAvanceFinancieroPapGenerator() {
    }

    static byte[] generarExcel(Encabezado encabezado, List<EstudioFilaAvancePAPDto> filas) {
        String titulo = tituloPrincipal(encabezado) + " - Institución Ejecutora: "
                + ReportePapGeneratorSupport.valorODefectoTexto(encabezado.institucionEjecutora());
        double[] totales = new double[VALORES.size()];
        return ReportePapGeneratorSupport.generarExcel("Avance Financiero PAP", titulo, ENCABEZADOS, filas,
                (Row row, EstudioFilaAvancePAPDto fila) -> escribirFilaExcel(row, fila, totales),
                (XSSFSheet hoja, int numeroFila) -> escribirFilasFinalesExcel(hoja, numeroFila, totales, encabezado),
                "No se pudo generar el reporte Excel del Avance Financiero Cuatrimestral del PAP.");
    }

    /** Escribe una fila de datos y acumula en {@code totales} sus valores numéricos. */
    private static void escribirFilaExcel(Row row, EstudioFilaAvancePAPDto fila, double[] totales) {
        row.createCell(COL_CUP).setCellValue(fila.getCup());
        row.createCell(COL_NOMBRE_PROYECTO).setCellValue(fila.getNombreProyecto());
        NombreEtapaDto etapa = fila.getEtapa();
        row.createCell(COL_ETAPA).setCellValue(Optional.ofNullable(etapa).map(NombreEtapaDto::getValue).orElse(""));
        FuenteFinanciamientoDto fuenteFinanciamiento = fila.getFuenteFinanciamiento();
        row.createCell(COL_FUENTE_FINANCIAMIENTO)
                .setCellValue(fuenteFinanciamiento != null ? fuenteFinanciamiento.getValue() : "");
        for (int i = 0; i < VALORES.size(); i++) {
            double valor = VALORES.get(i).applyAsDouble(fila);
            ReportePapGeneratorSupport.escribirMonto(row.createCell(PRIMERA_COLUMNA_VALOR + i), valor);
            totales[i] += valor;
        }
        row.createCell(COLUMNA_OBSERVACIONES)
                .setCellValue(ReportePapGeneratorSupport.valorODefectoTexto(fila.getObservaciones()));
    }

    /** Fila TOTAL (solo columnas de montos) y, si aplica, la fila de comentarios DGICP. */
    private static void escribirFilasFinalesExcel(XSSFSheet hoja, int numeroFila, double[] totales,
            Encabezado encabezado) {
        Row totalRow = hoja.createRow(numeroFila);
        totalRow.createCell(COL_NOMBRE_PROYECTO).setCellValue("TOTAL");
        for (int i = 0; i < totales.length; i++) {
            if (ES_MONTO[i]) {
                totalRow.createCell(PRIMERA_COLUMNA_VALOR + i).setCellValue(totales[i]);
            }
        }
        if (encabezado.mostrarComentarios()) {
            Row comentariosRow = hoja.createRow(numeroFila + FILAS_ENTRE_TOTAL_Y_COMENTARIOS);
            comentariosRow.createCell(COL_ETIQUETA_COMENTARIOS).setCellValue(ETIQUETA_COMENTARIOS);
            comentariosRow.createCell(COL_TEXTO_COMENTARIOS).setCellValue(
                    ReportePapGeneratorSupport.valorODefectoTexto(encabezado.comentariosDgicp()));
        }
    }

    static byte[] generarPdf(Encabezado encabezado, List<EstudioFilaAvancePAPDto> filas) {
        String subtitulo = "Institución Ejecutora: "
                + ReportePapGeneratorSupport.valorODefectoTexto(encabezado.institucionEjecutora())
                + "   Año: " + encabezado.anio() + "   Cuatrimestre: "
                + encabezado.periodo().name().replace("CUATRIMESTRE_", "");
        List<String> lineasFinales = new ArrayList<>();
        StringBuilder total = new StringBuilder("TOTAL");
        for (int i = 0; i < VALORES.size(); i++) {
            double suma = filas.stream().mapToDouble(VALORES.get(i)).sum();
            total.append(ES_MONTO[i] ? String.format(Locale.ROOT, " | %.2f", suma) : " | ");
        }
        lineasFinales.add(total.toString());
        if (encabezado.mostrarComentarios()) {
            lineasFinales.add(ETIQUETA_COMENTARIOS + " " + textoEnUnaLinea(encabezado.comentariosDgicp()));
        }
        return ReportePapGeneratorSupport.generarPdf(tituloPrincipal(encabezado), subtitulo, filas,
                ReporteAvanceFinancieroPapGenerator::formatearLineaPdf,
                lineasFinales,
                "No se pudo generar el reporte PDF del Avance Financiero Cuatrimestral del PAP.");
    }

    private static String formatearLineaPdf(EstudioFilaAvancePAPDto fila) {
        NombreEtapaDto etapa = fila.getEtapa();
        FuenteFinanciamientoDto fuenteFinanciamiento = fila.getFuenteFinanciamiento();
        StringBuilder linea = new StringBuilder(String.format(Locale.ROOT, "%s | %s | %s | %s",
                fila.getCup(), fila.getNombreProyecto(),
                Optional.ofNullable(etapa).map(NombreEtapaDto::getValue).orElse(""),
                fuenteFinanciamiento != null ? fuenteFinanciamiento.getValue() : ""));
        for (int i = 0; i < VALORES.size(); i++) {
            double valor = VALORES.get(i).applyAsDouble(fila);
            linea.append(String.format(Locale.ROOT, ES_MONTO[i] ? " | %.2f" : " | %.2f%%", valor));
        }
        linea.append(" | ").append(textoEnUnaLinea(fila.getObservaciones()));
        return linea.toString();
    }

    private static String tituloPrincipal(Encabezado encabezado) {
        return "INFORME DE AVANCE AL CUATRIMESTRE " + encabezado.periodo().name().replace("CUATRIMESTRE_", "")
                + " FINANCIERO DEL PROGRAMA ANUAL DE PREINVERSION PUBLICA " + encabezado.anio();
    }

    /** PDFBox (Helvetica) no admite saltos de línea dentro de {@code showText}. */
    private static String textoEnUnaLinea(String texto) {
        return SALTOS_DE_LINEA.matcher(ReportePapGeneratorSupport.valorODefectoTexto(texto)).replaceAll(" ");
    }
}
