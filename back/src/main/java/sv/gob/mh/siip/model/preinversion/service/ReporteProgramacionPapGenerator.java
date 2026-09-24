package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

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

    /** Primera columna de montos (Costo de la etapa); las siguientes 7 columnas también son montos totalizables. */
    private static final int PRIMERA_COLUMNA_MONTO = 4;

    private ReporteProgramacionPapGenerator() {
    }

    static byte[] generarExcel(Long idUnidadEjecutora, Integer anio, List<EstudioFilaListaPAPDto> filas) {
        String titulo = "PROGRAMACIÓN FINANCIERA DE LA PREINVERSIÓN PÚBLICA - Unidad Ejecutora "
                + idUnidadEjecutora + " - Año " + anio;
        double[] totales = new double[ENCABEZADOS.length - PRIMERA_COLUMNA_MONTO];
        return ReportePapGeneratorSupport.generarExcel("Programacion Financiera PAP", titulo, ENCABEZADOS, filas,
                (row, fila) -> {
                    row.createCell(0).setCellValue(fila.getCup());
                    row.createCell(1).setCellValue(fila.getNombreProyecto());
                    row.createCell(2).setCellValue(fila.getEtapa() != null ? fila.getEtapa().getValue() : "");
                    FuenteFinanciamientoDto fuenteFinanciamiento = fila.getFuenteFinanciamiento();
                    row.createCell(3).setCellValue(fuenteFinanciamiento != null ? fuenteFinanciamiento.getValue() : "");
                    Double[] montos = montos(fila);
                    for (int i = 0; i < montos.length; i++) {
                        ReportePapGeneratorSupport.escribirMonto(row.createCell(PRIMERA_COLUMNA_MONTO + i), montos[i]);
                        totales[i] += ReportePapGeneratorSupport.valorODefecto(montos[i]);
                    }
                },
                (XSSFSheet hoja, int numeroFila) -> {
                    Row totalRow = hoja.createRow(numeroFila);
                    totalRow.createCell(1).setCellValue("TOTAL");
                    for (int i = 0; i < totales.length; i++) {
                        totalRow.createCell(PRIMERA_COLUMNA_MONTO + i).setCellValue(totales[i]);
                    }
                },
                "No se pudo generar el reporte Excel de la Programación Financiera del PAP.");
    }

    static byte[] generarPdf(Long idUnidadEjecutora, Integer anio, List<EstudioFilaListaPAPDto> filas) {
        String subtitulo = "Unidad Ejecutora: " + idUnidadEjecutora + "   Año: " + anio;
        return ReportePapGeneratorSupport.generarPdf("PROGRAMACIÓN FINANCIERA DE LA PREINVERSIÓN PÚBLICA", subtitulo,
                filas,
                fila -> {
                    NombreEtapaDto etapa = fila.getEtapa();
                    FuenteFinanciamientoDto fuenteFinanciamiento = fila.getFuenteFinanciamiento();
                    StringBuilder linea = new StringBuilder(String.format("%s | %s | %s | %s",
                            fila.getCup(), fila.getNombreProyecto(),
                            etapa != null ? etapa.getValue() : "",
                            fuenteFinanciamiento != null ? fuenteFinanciamiento.getValue() : ""));
                    for (Double monto : montos(fila)) {
                        linea.append(String.format(" | %.2f", ReportePapGeneratorSupport.valorODefecto(monto)));
                    }
                    return linea.toString();
                },
                "No se pudo generar el reporte PDF de la Programación Financiera del PAP.");
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
