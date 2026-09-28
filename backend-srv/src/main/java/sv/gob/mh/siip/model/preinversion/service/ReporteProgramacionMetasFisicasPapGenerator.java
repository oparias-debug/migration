package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Row;

import sv.gob.mh.siip.model.preinversion.dto.EntregableDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioFilaMetasFisicasDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;

/** Genera el reporte del Anexo A.5 (SF-7, RN-E) en Excel o PDF, a partir de las filas del Anexo A.1. */
final class ReporteProgramacionMetasFisicasPapGenerator {

    private static final String[] ENCABEZADOS = {
            "CUP", "Nombre del proyecto", "Etapa", "Meta Total", "Entregable",
            "Ejecutado años anteriores (%)", "Total Año (%)", "Años posteriores (%)", "Comentarios al reporte DGICP"
    };

    /** Índices de columna del Excel, en el orden de {@link #ENCABEZADOS}. */
    private static final int COL_CUP = 0;
    private static final int COL_NOMBRE_PROYECTO = 1;
    private static final int COL_ETAPA = 2;
    private static final int COL_META_TOTAL = 3;
    private static final int COL_ENTREGABLE = 4;
    private static final int COL_EJECUTADO_ANIOS_ANTERIORES = 5;
    private static final int COL_TOTAL_ANIO = 6;
    private static final int COL_ANIOS_POSTERIORES = 7;
    private static final int COL_COMENTARIOS_REPORTE_DGICP = 8;

    private ReporteProgramacionMetasFisicasPapGenerator() {
    }

    static byte[] generarExcel(Long idUnidadEjecutora, Integer anio, List<EstudioFilaMetasFisicasDto> filas) {
        String titulo = "PROGRAMACIÓN CUATRIMESTRAL DE METAS FÍSICAS DEL PAP - Unidad Ejecutora "
                + idUnidadEjecutora + " - Año " + anio;
        return ReportePapGeneratorSupport.generarExcel("Programacion Metas Fisicas PAP", titulo, ENCABEZADOS, filas,
                ReporteProgramacionMetasFisicasPapGenerator::escribirFilaExcel,
                "No se pudo generar el reporte Excel de la Programación de Metas Físicas del PAP.");
    }

    private static void escribirFilaExcel(Row row, EstudioFilaMetasFisicasDto fila) {
        NombreEtapaDto etapa = fila.getEtapa();
        EntregableDto entregable = fila.getEntregable();
        row.createCell(COL_CUP).setCellValue(fila.getCup());
        row.createCell(COL_NOMBRE_PROYECTO).setCellValue(fila.getNombreProyecto());
        row.createCell(COL_ETAPA).setCellValue(Optional.ofNullable(etapa).map(NombreEtapaDto::getValue).orElse(""));
        ReportePapGeneratorSupport.escribirMonto(row.createCell(COL_META_TOTAL), fila.getMetaTotal());
        row.createCell(COL_ENTREGABLE).setCellValue(entregable != null ? entregable.getValue() : "");
        ReportePapGeneratorSupport.escribirMonto(row.createCell(COL_EJECUTADO_ANIOS_ANTERIORES),
                fila.getEjecutadoAniosAnteriores());
        ReportePapGeneratorSupport.escribirMonto(row.createCell(COL_TOTAL_ANIO), fila.getTotalAnio());
        ReportePapGeneratorSupport.escribirMonto(row.createCell(COL_ANIOS_POSTERIORES), fila.getAniosPosteriores());
        row.createCell(COL_COMENTARIOS_REPORTE_DGICP).setCellValue(
                ReportePapGeneratorSupport.valorODefectoTexto(fila.getComentariosReporteDgicp()));
    }

    static byte[] generarPdf(Long idUnidadEjecutora, Integer anio, List<EstudioFilaMetasFisicasDto> filas) {
        String subtitulo = "Unidad Ejecutora: " + idUnidadEjecutora + "   Año: " + anio;
        return ReportePapGeneratorSupport.generarPdf("PROGRAMACIÓN CUATRIMESTRAL DE METAS FÍSICAS DEL PAP", subtitulo,
                filas,
                ReporteProgramacionMetasFisicasPapGenerator::formatearLineaPdf,
                "No se pudo generar el reporte PDF de la Programación de Metas Físicas del PAP.");
    }

    private static String formatearLineaPdf(EstudioFilaMetasFisicasDto fila) {
        NombreEtapaDto etapa = fila.getEtapa();
        EntregableDto entregable = fila.getEntregable();
        return String.format(Locale.ROOT, "%s | %s | %s | %.2f | %s | %.2f | %.2f | %.2f",
                fila.getCup(), fila.getNombreProyecto(),
                Optional.ofNullable(etapa).map(NombreEtapaDto::getValue).orElse(""),
                ReportePapGeneratorSupport.valorODefecto(fila.getMetaTotal()),
                entregable != null ? entregable.getValue() : "",
                ReportePapGeneratorSupport.valorODefecto(fila.getEjecutadoAniosAnteriores()),
                ReportePapGeneratorSupport.valorODefecto(fila.getTotalAnio()),
                ReportePapGeneratorSupport.valorODefecto(fila.getAniosPosteriores()));
    }
}
