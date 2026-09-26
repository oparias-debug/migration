package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import sv.gob.mh.siip.model.preinversion.dto.EstudioFilaListaPAPDto;
import sv.gob.mh.siip.model.preinversion.dto.FuenteFinanciamientoDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;

/** Genera el reporte del Anexo A.8 (SF-3, RN-E) en Excel o PDF, a partir de las filas del Anexo A.1. */
final class ReporteProgramacionPapGenerator {

    /** Anexo A.8: "la misma tabla de datos del Anexo A.1", incluidas las columnas I/II/III Cuatrimestre. */
    private static final String[] ENCABEZADOS = {
            "CUP", "Nombre del proyecto", "Etapa", "Fuente de Financiamiento", "Costo de la etapa",
            "Ejecutado años anteriores", "I Cuatrimestre", "II Cuatrimestre", "III Cuatrimestre", "Total Año",
            "Años posteriores"
    };

    private static final int COL_CUP = 0;
    private static final int COL_NOMBRE_PROYECTO = 1;
    private static final int COL_ETAPA = 2;
    private static final int COL_FUENTE_FINANCIAMIENTO = 3;
    /** Primera columna de montos (Costo de la etapa); las siguientes 7 columnas también son montos totalizables. */
    private static final int PRIMERA_COLUMNA_MONTO = 4;

    private ReporteProgramacionPapGenerator() {
    }

    static byte[] generarExcel(Long idUnidadEjecutora, Integer anio, List<EstudioFilaListaPAPDto> filas) {
        String titulo = "PROGRAMACIÓN FINANCIERA DE LA PREINVERSIÓN PÚBLICA - Unidad Ejecutora "
                + idUnidadEjecutora + " - Año " + anio;
        double[] totales = new double[ENCABEZADOS.length - PRIMERA_COLUMNA_MONTO];
        return ReportePapGeneratorSupport.generarExcel("Programacion Financiera PAP", titulo, ENCABEZADOS, filas,
                (Row row, EstudioFilaListaPAPDto fila) -> escribirFilaExcel(row, fila, totales),
                (XSSFSheet hoja, int numeroFila) -> {
                    Row totalRow = hoja.createRow(numeroFila);
                    totalRow.createCell(COL_NOMBRE_PROYECTO).setCellValue("TOTAL");
                    for (int i = 0; i < totales.length; i++) {
                        totalRow.createCell(PRIMERA_COLUMNA_MONTO + i).setCellValue(totales[i]);
                    }
                },
                "No se pudo generar el reporte Excel de la Programación Financiera del PAP.");
    }

    /** Escribe una fila de datos y acumula en {@code totales} sus montos. */
    private static void escribirFilaExcel(Row row, EstudioFilaListaPAPDto fila, double[] totales) {
        row.createCell(COL_CUP).setCellValue(fila.getCup());
        row.createCell(COL_NOMBRE_PROYECTO).setCellValue(fila.getNombreProyecto());
        row.createCell(COL_ETAPA)
                .setCellValue(Optional.ofNullable(fila.getEtapa()).map(NombreEtapaDto::getValue).orElse(""));
        FuenteFinanciamientoDto fuenteFinanciamiento = fila.getFuenteFinanciamiento();
        row.createCell(COL_FUENTE_FINANCIAMIENTO)
                .setCellValue(fuenteFinanciamiento != null ? fuenteFinanciamiento.getValue() : "");
        Double[] montos = montos(fila);
        for (int i = 0; i < montos.length; i++) {
            ReportePapGeneratorSupport.escribirMonto(row.createCell(PRIMERA_COLUMNA_MONTO + i), montos[i]);
            totales[i] += ReportePapGeneratorSupport.valorODefecto(montos[i]);
        }
    }

    static byte[] generarPdf(Long idUnidadEjecutora, Integer anio, List<EstudioFilaListaPAPDto> filas) {
        String subtitulo = "Unidad Ejecutora: " + idUnidadEjecutora + "   Año: " + anio;
        return ReportePapGeneratorSupport.generarPdf("PROGRAMACIÓN FINANCIERA DE LA PREINVERSIÓN PÚBLICA", subtitulo,
                filas,
                ReporteProgramacionPapGenerator::formatearLineaPdf,
                "No se pudo generar el reporte PDF de la Programación Financiera del PAP.");
    }

    private static String formatearLineaPdf(EstudioFilaListaPAPDto fila) {
        NombreEtapaDto etapa = fila.getEtapa();
        FuenteFinanciamientoDto fuenteFinanciamiento = fila.getFuenteFinanciamiento();
        StringBuilder linea = new StringBuilder(String.format(Locale.ROOT, "%s | %s | %s | %s",
                fila.getCup(), fila.getNombreProyecto(),
                Optional.ofNullable(etapa).map(NombreEtapaDto::getValue).orElse(""),
                fuenteFinanciamiento != null ? fuenteFinanciamiento.getValue() : ""));
        for (Double monto : montos(fila)) {
            linea.append(String.format(Locale.ROOT, " | %.2f", ReportePapGeneratorSupport.valorODefecto(monto)));
        }
        return linea.toString();
    }

    /** Columnas de monto en el orden de {@link #ENCABEZADOS} (desde "Costo de la etapa"). */
    private static Double[] montos(EstudioFilaListaPAPDto fila) {
        return new Double[] {
                fila.getCostoEtapa(), fila.getEjecutadoAniosAnteriores(), fila.getMontoCuatrimestre1(),
                fila.getMontoCuatrimestre2(), fila.getMontoCuatrimestre3(), fila.getTotalProgramadoAnio(),
                fila.getAniosPosteriores()
        };
    }
}
