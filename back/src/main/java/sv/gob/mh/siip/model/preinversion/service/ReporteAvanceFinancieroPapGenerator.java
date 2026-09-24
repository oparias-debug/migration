package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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

    static final String ETIQUETA_COMENTARIOS = "Comentarios al reporte financiero DGICP:";

    /** Columnas del Anexo A.1 (Avance Anual, Avance al Cuatrimestre y Avance del Cuatrimestre + Observaciones). */
    private static final String[] ENCABEZADOS = {
            "CUP", "Nombre del proyecto", "Etapa", "Fuente de Financiamiento", "Costo de la etapa",
            "Ejecutado años anteriores",
            "Avance Anual Programado", "Avance Anual Ejecutado", "Avance Anual %",
            "Avance al Cuatrimestre Programado", "Avance al Cuatrimestre Ejecutado", "Avance al Cuatrimestre %",
            "Avance del Cuatrimestre Programado", "Avance del Cuatrimestre Ejecutado", "Avance del Cuatrimestre %",
            "Observaciones del Cuatrimestre"
    };

    private static final int PRIMERA_COLUMNA_VALOR = 4;
    private static final int COLUMNA_OBSERVACIONES = ENCABEZADOS.length - 1;

    /** Getters de las columnas numéricas, en el orden de {@link #ENCABEZADOS} desde "Costo de la etapa". */
    private static final List<Function<EstudioFilaAvancePAPDto, Double>> VALORES = List.of(
            EstudioFilaAvancePAPDto::getCostoEtapa,
            EstudioFilaAvancePAPDto::getEjecutadoAniosAnteriores,
            EstudioFilaAvancePAPDto::getAvanceAnualProgramado,
            EstudioFilaAvancePAPDto::getAvanceAnualEjecutadoMonto,
            EstudioFilaAvancePAPDto::getAvanceAnualEjecutadoPorcentaje,
            EstudioFilaAvancePAPDto::getAvanceAlCuatrimestreProgramado,
            EstudioFilaAvancePAPDto::getAvanceAlCuatrimestreEjecutadoMonto,
            EstudioFilaAvancePAPDto::getAvanceAlCuatrimestreEjecutadoPorcentaje,
            EstudioFilaAvancePAPDto::getAvanceDelCuatrimestreProgramado,
            EstudioFilaAvancePAPDto::getAvanceDelCuatrimestreEjecutadoMonto,
            EstudioFilaAvancePAPDto::getAvanceDelCuatrimestrePorcentaje);

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
                (row, fila) -> {
                    row.createCell(0).setCellValue(fila.getCup());
                    row.createCell(1).setCellValue(fila.getNombreProyecto());
                    NombreEtapaDto etapa = fila.getEtapa();
                    row.createCell(2).setCellValue(etapa != null ? etapa.getValue() : "");
                    FuenteFinanciamientoDto fuenteFinanciamiento = fila.getFuenteFinanciamiento();
                    row.createCell(3).setCellValue(fuenteFinanciamiento != null ? fuenteFinanciamiento.getValue() : "");
                    for (int i = 0; i < VALORES.size(); i++) {
                        Double valor = VALORES.get(i).apply(fila);
                        ReportePapGeneratorSupport.escribirMonto(row.createCell(PRIMERA_COLUMNA_VALOR + i), valor);
                        totales[i] += ReportePapGeneratorSupport.valorODefecto(valor);
                    }
                    row.createCell(COLUMNA_OBSERVACIONES)
                            .setCellValue(ReportePapGeneratorSupport.valorODefectoTexto(fila.getObservaciones()));
                },
                (XSSFSheet hoja, int numeroFila) -> {
                    Row totalRow = hoja.createRow(numeroFila);
                    totalRow.createCell(1).setCellValue("TOTAL");
                    for (int i = 0; i < totales.length; i++) {
                        if (ES_MONTO[i]) {
                            totalRow.createCell(PRIMERA_COLUMNA_VALOR + i).setCellValue(totales[i]);
                        }
                    }
                    if (encabezado.mostrarComentarios()) {
                        Row comentariosRow = hoja.createRow(numeroFila + 2);
                        comentariosRow.createCell(0).setCellValue(ETIQUETA_COMENTARIOS);
                        comentariosRow.createCell(1).setCellValue(
                                ReportePapGeneratorSupport.valorODefectoTexto(encabezado.comentariosDgicp()));
                    }
                },
                "No se pudo generar el reporte Excel del Avance Financiero Cuatrimestral del PAP.");
    }

    static byte[] generarPdf(Encabezado encabezado, List<EstudioFilaAvancePAPDto> filas) {
        String subtitulo = "Institución Ejecutora: "
                + ReportePapGeneratorSupport.valorODefectoTexto(encabezado.institucionEjecutora())
                + "   Año: " + encabezado.anio() + "   Cuatrimestre: "
                + encabezado.periodo().name().replace("CUATRIMESTRE_", "");
        List<String> lineasFinales = new ArrayList<>();
        StringBuilder total = new StringBuilder("TOTAL");
        for (int i = 0; i < VALORES.size(); i++) {
            Function<EstudioFilaAvancePAPDto, Double> valor = VALORES.get(i);
            double suma = filas.stream().mapToDouble(fila -> ReportePapGeneratorSupport.valorODefecto(valor.apply(fila)))
                    .sum();
            total.append(ES_MONTO[i] ? String.format(" | %.2f", suma) : " | ");
        }
        lineasFinales.add(total.toString());
        if (encabezado.mostrarComentarios()) {
            lineasFinales.add(ETIQUETA_COMENTARIOS + " " + textoEnUnaLinea(encabezado.comentariosDgicp()));
        }
        return ReportePapGeneratorSupport.generarPdf(tituloPrincipal(encabezado), subtitulo, filas,
                fila -> {
                    NombreEtapaDto etapa = fila.getEtapa();
                    FuenteFinanciamientoDto fuenteFinanciamiento = fila.getFuenteFinanciamiento();
                    StringBuilder linea = new StringBuilder(String.format("%s | %s | %s | %s",
                            fila.getCup(), fila.getNombreProyecto(),
                            etapa != null ? etapa.getValue() : "",
                            fuenteFinanciamiento != null ? fuenteFinanciamiento.getValue() : ""));
                    for (int i = 0; i < VALORES.size(); i++) {
                        double valor = ReportePapGeneratorSupport.valorODefecto(VALORES.get(i).apply(fila));
                        linea.append(String.format(ES_MONTO[i] ? " | %.2f" : " | %.2f%%", valor));
                    }
                    linea.append(" | ").append(textoEnUnaLinea(fila.getObservaciones()));
                    return linea.toString();
                },
                lineasFinales,
                "No se pudo generar el reporte PDF del Avance Financiero Cuatrimestral del PAP.");
    }

    private static String tituloPrincipal(Encabezado encabezado) {
        return "INFORME DE AVANCE AL CUATRIMESTRE " + encabezado.periodo().name().replace("CUATRIMESTRE_", "")
                + " FINANCIERO DEL PROGRAMA ANUAL DE PREINVERSION PUBLICA " + encabezado.anio();
    }

    /** PDFBox (Helvetica) no admite saltos de línea dentro de {@code showText}. */
    private static String textoEnUnaLinea(String texto) {
        return ReportePapGeneratorSupport.valorODefectoTexto(texto).replaceAll("[\\r\\n\\t]+", " ");
    }
}
