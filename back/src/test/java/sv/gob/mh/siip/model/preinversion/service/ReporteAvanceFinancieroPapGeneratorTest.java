package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.dto.EstudioFilaAvancePAPDto;
import sv.gob.mh.siip.model.preinversion.dto.FuenteFinanciamientoDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;

/** CU-PRE-32 Anexo A.6: columnas del Anexo A.1, fila TOTAL, Institución Ejecutora y comentarios DGICP. */
class ReporteAvanceFinancieroPapGeneratorTest {

    private static EstudioFilaAvancePAPDto filaCompleta() {
        return new EstudioFilaAvancePAPDto("08040", "Proyecto A", NombreEtapaDto.PERFIL)
                .fuenteFinanciamiento(FuenteFinanciamientoDto.FONDO_GENERAL)
                .costoEtapa(10000d)
                .ejecutadoAniosAnteriores(1000d)
                .avanceAnualProgramado(6000d)
                .avanceAnualEjecutadoMonto(1500d)
                .avanceAnualEjecutadoPorcentaje(25d)
                .avanceAlCuatrimestreProgramado(3000d)
                .avanceAlCuatrimestreEjecutadoMonto(1500d)
                .avanceAlCuatrimestreEjecutadoPorcentaje(50d)
                .avanceDelCuatrimestreProgramado(2000d)
                .avanceDelCuatrimestreEjecutadoMonto(1000d)
                .avanceDelCuatrimestrePorcentaje(50d)
                .observaciones("Observación BDD");
    }

    @Test
    void generarExcel_incluyeColumnasDelAnexoA1TotalesInstitucionYComentariosDgicp() throws IOException {
        var encabezado = new ReporteAvanceFinancieroPapGenerator.Encabezado("MINSAL", 2028,
                Cuatrimestre.CUATRIMESTRE_II, true, "Comentario DGICP");

        byte[] bytes = ReporteAvanceFinancieroPapGenerator.generarExcel(encabezado,
                List.of(filaCompleta(), new EstudioFilaAvancePAPDto("08041", "Proyecto B", null)));

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            XSSFSheet hoja = workbook.getSheetAt(0);
            assertThat(hoja.getRow(0).getCell(0).getStringCellValue())
                    .contains("CUATRIMESTRE II", "2028", "Institución Ejecutora: MINSAL");
            Row encabezados = hoja.getRow(2);
            assertThat(encabezados.getCell(8).getStringCellValue()).isEqualTo("Avance Anual %");
            assertThat(encabezados.getCell(9).getStringCellValue()).isEqualTo("Avance al Cuatrimestre Programado");
            assertThat(encabezados.getCell(10).getStringCellValue()).isEqualTo("Avance al Cuatrimestre Ejecutado");
            assertThat(encabezados.getCell(11).getStringCellValue()).isEqualTo("Avance al Cuatrimestre %");
            assertThat(encabezados.getCell(15).getStringCellValue()).isEqualTo("Observaciones del Cuatrimestre");

            Row fila = hoja.getRow(3);
            assertThat(fila.getCell(8).getNumericCellValue()).isEqualTo(25d);
            assertThat(fila.getCell(9).getNumericCellValue()).isEqualTo(3000d);
            assertThat(fila.getCell(11).getNumericCellValue()).isEqualTo(50d);
            assertThat(fila.getCell(15).getStringCellValue()).isEqualTo("Observación BDD");

            Row total = hoja.getRow(5);
            assertThat(total.getCell(1).getStringCellValue()).isEqualTo("TOTAL");
            assertThat(total.getCell(6).getNumericCellValue()).isEqualTo(6000d);
            assertThat(total.getCell(9).getNumericCellValue()).isEqualTo(3000d);
            // Los porcentajes no se totalizan (RN-E "Total General" solo aplica a montos).
            assertThat(total.getCell(8)).isNull();

            Row comentarios = hoja.getRow(7);
            assertThat(comentarios.getCell(0).getStringCellValue())
                    .isEqualTo(ReporteAvanceFinancieroPapGenerator.ETIQUETA_COMENTARIOS);
            assertThat(comentarios.getCell(1).getStringCellValue()).isEqualTo("Comentario DGICP");
        }
    }

    @Test
    void generarExcel_sinPermisoDgicp_noIncluyeElCampoDeComentarios() throws IOException {
        var encabezado = new ReporteAvanceFinancieroPapGenerator.Encabezado("MINSAL", 2028,
                Cuatrimestre.CUATRIMESTRE_I, false, null);

        byte[] bytes = ReporteAvanceFinancieroPapGenerator.generarExcel(encabezado, List.of(filaCompleta()));

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            XSSFSheet hoja = workbook.getSheetAt(0);
            assertThat(hoja.getRow(4).getCell(1).getStringCellValue()).isEqualTo("TOTAL");
            // Declarada como Row: XSSFRow es Iterable y Comparable a la vez y assertThat(...) quedaría ambiguo.
            Row filaSiguiente = hoja.getRow(6);
            assertThat(filaSiguiente).isNull();
        }
    }

    @Test
    void generarPdf_incluyeTotalYComentariosEnUnaLinea() throws IOException {
        var encabezado = new ReporteAvanceFinancieroPapGenerator.Encabezado("MINSAL", 2028,
                Cuatrimestre.CUATRIMESTRE_III, true, "Linea 1\nLinea 2");
        List<EstudioFilaAvancePAPDto> filas = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            filas.add(filaCompleta());
        }

        byte[] bytes = ReporteAvanceFinancieroPapGenerator.generarPdf(encabezado, filas);

        try (PDDocument documento = PDDocument.load(bytes)) {
            assertThat(documento.getNumberOfPages()).isGreaterThan(1);
            String texto = new PDFTextStripper().getText(documento);
            assertThat(texto).contains("Institución Ejecutora: MINSAL", "TOTAL | 600000",
                    "Comentarios al reporte financiero DGICP: Linea 1 Linea 2");
        }
    }
}
