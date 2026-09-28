package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Row;

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

    /** Índices de columna del Excel, en el orden de {@link #ENCABEZADOS}. */
    private static final int COL_CUP = 0;
    private static final int COL_NOMBRE_PROYECTO = 1;
    private static final int COL_ETAPA = 2;
    private static final int COL_META = 3;
    private static final int COL_EJECUTADO_ANIOS_ANTERIORES = 4;
    private static final int COL_AVANCE_ANUAL_PROGRAMADO = 5;
    private static final int COL_AVANCE_ANUAL_EJECUTADO = 6;
    private static final int COL_TOTAL_META_EJECUTADA = 7;
    private static final int COL_ESTADO = 8;

    private ReporteAvanceMetasFisicasPapGenerator() {
    }

    static byte[] generarExcel(Long idUnidadEjecutora, Integer anio, Cuatrimestre periodo,
            List<EstudioFilaAvanceMetasDto> filas) {
        String titulo = "AVANCE CUATRIMESTRAL POR METAS FÍSICAS DEL PAP - Unidad Ejecutora "
                + idUnidadEjecutora + " - Año " + anio + " - " + periodo;
        return ReportePapGeneratorSupport.generarExcel("Avance Metas Fisicas PAP", titulo, ENCABEZADOS, filas,
                ReporteAvanceMetasFisicasPapGenerator::escribirFilaExcel,
                "No se pudo generar el reporte Excel del Avance Cuatrimestral por Metas Físicas del PAP.");
    }

    private static void escribirFilaExcel(Row row, EstudioFilaAvanceMetasDto fila) {
        row.createCell(COL_CUP).setCellValue(fila.getCup());
        row.createCell(COL_NOMBRE_PROYECTO).setCellValue(fila.getNombreProyecto());
        NombreEtapaDto etapa = fila.getEtapa();
        row.createCell(COL_ETAPA).setCellValue(Optional.ofNullable(etapa).map(NombreEtapaDto::getValue).orElse(""));
        ReportePapGeneratorSupport.escribirMonto(row.createCell(COL_META), fila.getMeta());
        ReportePapGeneratorSupport.escribirMonto(row.createCell(COL_EJECUTADO_ANIOS_ANTERIORES),
                fila.getEjecutadoAniosAnteriores());
        ReportePapGeneratorSupport.escribirMonto(row.createCell(COL_AVANCE_ANUAL_PROGRAMADO),
                fila.getProgramadoEnElAnio());
        ReportePapGeneratorSupport.escribirMonto(row.createCell(COL_AVANCE_ANUAL_EJECUTADO),
                fila.getEjecutadoEnElAnio());
        ReportePapGeneratorSupport.escribirMonto(row.createCell(COL_TOTAL_META_EJECUTADA),
                fila.getTotalMetaEjecutada());
        EstadoAvanceMetasDto estado = fila.getEstado();
        row.createCell(COL_ESTADO).setCellValue(estado != null ? estado.getValue() : "");
    }

    static byte[] generarPdf(Long idUnidadEjecutora, Integer anio, Cuatrimestre periodo,
            List<EstudioFilaAvanceMetasDto> filas) {
        String subtitulo = "Unidad Ejecutora: " + idUnidadEjecutora + "   Año: " + anio + "   Período: " + periodo;
        return ReportePapGeneratorSupport.generarPdf("AVANCE CUATRIMESTRAL POR METAS FÍSICAS DEL PAP", subtitulo,
                filas,
                (EstudioFilaAvanceMetasDto fila) -> {
                    NombreEtapaDto etapa = fila.getEtapa();
                    EstadoAvanceMetasDto estado = fila.getEstado();
                    return String.format(Locale.ROOT, "%s | %s | %s | %.2f | %.2f | %s",
                            fila.getCup(), fila.getNombreProyecto(),
                            Optional.ofNullable(etapa).map(NombreEtapaDto::getValue).orElse(""),
                            ReportePapGeneratorSupport.valorODefecto(fila.getProgramadoEnElAnio()),
                            ReportePapGeneratorSupport.valorODefecto(fila.getEjecutadoEnElAnio()),
                            estado != null ? estado.getValue() : "");
                },
                "No se pudo generar el reporte PDF del Avance Cuatrimestral por Metas Físicas del PAP.");
    }
}
