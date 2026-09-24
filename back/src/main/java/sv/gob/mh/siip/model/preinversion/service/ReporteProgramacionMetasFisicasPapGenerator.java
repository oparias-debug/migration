package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import sv.gob.mh.siip.model.preinversion.dto.EntregableDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioFilaMetasFisicasDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;

/** Genera el reporte del Anexo A.5 (SF-7, RN-E) en Excel o PDF, a partir de las filas del Anexo A.1. */
final class ReporteProgramacionMetasFisicasPapGenerator {

    private static final String[] ENCABEZADOS = {
            "CUP", "Nombre del proyecto", "Etapa", "Meta Total", "Entregable",
            "Ejecutado años anteriores (%)", "Total Año (%)", "Años posteriores (%)", "Comentarios al reporte DGICP"
    };

    private ReporteProgramacionMetasFisicasPapGenerator() {
    }

    static byte[] generarExcel(Long idUnidadEjecutora, Integer anio, List<EstudioFilaMetasFisicasDto> filas) {
        String titulo = "PROGRAMACIÓN CUATRIMESTRAL DE METAS FÍSICAS DEL PAP - Unidad Ejecutora "
                + idUnidadEjecutora + " - Año " + anio;
        return ReportePapGeneratorSupport.generarExcel("Programacion Metas Fisicas PAP", titulo, ENCABEZADOS, filas,
                (row, fila) -> {
                    NombreEtapaDto etapa = fila.getEtapa();
                    EntregableDto entregable = fila.getEntregable();
                    row.createCell(0).setCellValue(fila.getCup());
                    row.createCell(1).setCellValue(fila.getNombreProyecto());
                    row.createCell(2).setCellValue(etapa != null ? etapa.getValue() : "");
                    ReportePapGeneratorSupport.escribirMonto(row.createCell(3), fila.getMetaTotal());
                    row.createCell(4).setCellValue(entregable != null ? entregable.getValue() : "");
                    ReportePapGeneratorSupport.escribirMonto(row.createCell(5), fila.getEjecutadoAniosAnteriores());
                    ReportePapGeneratorSupport.escribirMonto(row.createCell(6), fila.getTotalAnio());
                    ReportePapGeneratorSupport.escribirMonto(row.createCell(7), fila.getAniosPosteriores());
                    row.createCell(8).setCellValue(ReportePapGeneratorSupport.valorODefectoTexto(fila.getComentariosReporteDgicp()));
                },
                "No se pudo generar el reporte Excel de la Programación de Metas Físicas del PAP.");
    }

    static byte[] generarPdf(Long idUnidadEjecutora, Integer anio, List<EstudioFilaMetasFisicasDto> filas) {
        String subtitulo = "Unidad Ejecutora: " + idUnidadEjecutora + "   Año: " + anio;
        return ReportePapGeneratorSupport.generarPdf("PROGRAMACIÓN CUATRIMESTRAL DE METAS FÍSICAS DEL PAP", subtitulo,
                filas,
                fila -> {
                    NombreEtapaDto etapa = fila.getEtapa();
                    EntregableDto entregable = fila.getEntregable();
                    return String.format("%s | %s | %s | %.2f | %s | %.2f | %.2f | %.2f",
                            fila.getCup(), fila.getNombreProyecto(),
                            etapa != null ? etapa.getValue() : "",
                            ReportePapGeneratorSupport.valorODefecto(fila.getMetaTotal()),
                            entregable != null ? entregable.getValue() : "",
                            ReportePapGeneratorSupport.valorODefecto(fila.getEjecutadoAniosAnteriores()),
                            ReportePapGeneratorSupport.valorODefecto(fila.getTotalAnio()),
                            ReportePapGeneratorSupport.valorODefecto(fila.getAniosPosteriores()));
                },
                "No se pudo generar el reporte PDF de la Programación de Metas Físicas del PAP.");
    }
}
