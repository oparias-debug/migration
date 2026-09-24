package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import sv.gob.mh.siip.model.preinversion.dto.EstadoAvanceMetasDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioFilaAvanceMetasDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;

/** Genera el reporte del Avance Cuatrimestral por Metas Físicas del PAP (SF-4), en Excel o PDF. */
final class ReporteAvanceMetasFisicasPapGenerator {

    private static final String[] ENCABEZADOS = {
            "CUP", "Nombre del proyecto", "Etapa", "Meta", "Ejecutado años anteriores",
            "Avance Anual Programado", "Avance Anual Ejecutado", "Total Meta Ejecutada", "Estado"
    };

    private ReporteAvanceMetasFisicasPapGenerator() {
    }

    static byte[] generarExcel(Long idUnidadEjecutora, Integer anio, Cuatrimestre periodo,
            List<EstudioFilaAvanceMetasDto> filas) {
        String titulo = "AVANCE CUATRIMESTRAL POR METAS FÍSICAS DEL PAP - Unidad Ejecutora "
                + idUnidadEjecutora + " - Año " + anio + " - " + periodo;
        return ReportePapGeneratorSupport.generarExcel("Avance Metas Fisicas PAP", titulo, ENCABEZADOS, filas,
                (row, fila) -> {
                    row.createCell(0).setCellValue(fila.getCup());
                    row.createCell(1).setCellValue(fila.getNombreProyecto());
                    NombreEtapaDto etapa = fila.getEtapa();
                    row.createCell(2).setCellValue(etapa != null ? etapa.getValue() : "");
                    ReportePapGeneratorSupport.escribirMonto(row.createCell(3), fila.getMeta());
                    ReportePapGeneratorSupport.escribirMonto(row.createCell(4), fila.getEjecutadoAniosAnteriores());
                    ReportePapGeneratorSupport.escribirMonto(row.createCell(5), fila.getProgramadoEnElAnio());
                    ReportePapGeneratorSupport.escribirMonto(row.createCell(6), fila.getEjecutadoEnElAnio());
                    ReportePapGeneratorSupport.escribirMonto(row.createCell(7), fila.getTotalMetaEjecutada());
                    EstadoAvanceMetasDto estado = fila.getEstado();
                    row.createCell(8).setCellValue(estado != null ? estado.getValue() : "");
                },
                "No se pudo generar el reporte Excel del Avance Cuatrimestral por Metas Físicas del PAP.");
    }

    static byte[] generarPdf(Long idUnidadEjecutora, Integer anio, Cuatrimestre periodo,
            List<EstudioFilaAvanceMetasDto> filas) {
        String subtitulo = "Unidad Ejecutora: " + idUnidadEjecutora + "   Año: " + anio + "   Período: " + periodo;
        return ReportePapGeneratorSupport.generarPdf("AVANCE CUATRIMESTRAL POR METAS FÍSICAS DEL PAP", subtitulo, filas,
                fila -> {
                    NombreEtapaDto etapa = fila.getEtapa();
                    EstadoAvanceMetasDto estado = fila.getEstado();
                    return String.format("%s | %s | %s | %.2f | %.2f | %s",
                            fila.getCup(), fila.getNombreProyecto(),
                            etapa != null ? etapa.getValue() : "",
                            ReportePapGeneratorSupport.valorODefecto(fila.getProgramadoEnElAnio()),
                            ReportePapGeneratorSupport.valorODefecto(fila.getEjecutadoEnElAnio()),
                            estado != null ? estado.getValue() : "");
                },
                "No se pudo generar el reporte PDF del Avance Cuatrimestral por Metas Físicas del PAP.");
    }
}
