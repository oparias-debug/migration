package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.dto.EntregableDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioFilaMetasFisicasDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;

class ReporteProgramacionMetasFisicasPapGeneratorTest {

    @Test
    void generarExcel_escribeFilasYUsaDefectosParaCamposNulos() throws IOException {
        EstudioFilaMetasFisicasDto filaCompleta = new EstudioFilaMetasFisicasDto("08040", "Proyecto A", NombreEtapaDto.PERFIL)
                .metaTotal(1d)
                .entregable(EntregableDto.ESTUDIO_DE_PERFIL)
                .ejecutadoAniosAnteriores(40d)
                .totalAnio(60d)
                .aniosPosteriores(0d)
                .comentariosReporteDgicp("Revisar Total Año.");
        EstudioFilaMetasFisicasDto filaSinDatosOpcionales = new EstudioFilaMetasFisicasDto("08041", "Proyecto B", null);

        byte[] bytes = ReporteProgramacionMetasFisicasPapGenerator.generarExcel(25L, 2027,
                List.of(filaCompleta, filaSinDatosOpcionales));

        assertThat(bytes).isNotEmpty();
        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            XSSFSheet hoja = workbook.getSheetAt(0);
            assertThat(hoja.getRow(0).getCell(0).getStringCellValue())
                    .contains("Unidad Ejecutora 25", "Año 2027");
            assertThat(hoja.getRow(2).getCell(0).getStringCellValue()).isEqualTo("CUP");

            Row filaCompletaXls = hoja.getRow(3);
            assertThat(filaCompletaXls.getCell(0).getStringCellValue()).isEqualTo("08040");
            assertThat(filaCompletaXls.getCell(2).getStringCellValue()).isEqualTo(NombreEtapaDto.PERFIL.getValue());
            assertThat(filaCompletaXls.getCell(3).getNumericCellValue()).isEqualTo(1d);
            assertThat(filaCompletaXls.getCell(4).getStringCellValue())
                    .isEqualTo(EntregableDto.ESTUDIO_DE_PERFIL.getValue());
            assertThat(filaCompletaXls.getCell(5).getNumericCellValue()).isEqualTo(40d);
            assertThat(filaCompletaXls.getCell(6).getNumericCellValue()).isEqualTo(60d);
            assertThat(filaCompletaXls.getCell(8).getStringCellValue()).isEqualTo("Revisar Total Año.");

            Row filaSinDatosXls = hoja.getRow(4);
            assertThat(filaSinDatosXls.getCell(2).getStringCellValue()).isEmpty();
            assertThat(filaSinDatosXls.getCell(3).getNumericCellValue()).isZero();
            assertThat(filaSinDatosXls.getCell(4).getStringCellValue()).isEmpty();
            assertThat(filaSinDatosXls.getCell(8).getStringCellValue()).isEmpty();
        }
    }

    @Test
    void generarPdf_conPocasFilas_generaUnaSolaPagina() throws IOException {
        EstudioFilaMetasFisicasDto fila = new EstudioFilaMetasFisicasDto("08040", "Proyecto A", NombreEtapaDto.PERFIL)
                .metaTotal(1d)
                .entregable(EntregableDto.ESTUDIO_DE_PERFIL)
                .totalAnio(100d);

        byte[] bytes = ReporteProgramacionMetasFisicasPapGenerator.generarPdf(25L, 2027, List.of(fila));

        assertThat(bytes).isNotEmpty();
        try (PDDocument documento = PDDocument.load(bytes)) {
            assertThat(documento.getNumberOfPages()).isEqualTo(1);
        }
    }

    @Test
    void generarPdf_conMuchasFilasYCamposNulos_generaMultiplesPaginas() throws IOException {
        List<EstudioFilaMetasFisicasDto> filas = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            filas.add(new EstudioFilaMetasFisicasDto("0804" + i, "Proyecto " + i, null));
        }

        byte[] bytes = ReporteProgramacionMetasFisicasPapGenerator.generarPdf(25L, 2027, filas);

        assertThat(bytes).isNotEmpty();
        try (PDDocument documento = PDDocument.load(bytes)) {
            assertThat(documento.getNumberOfPages()).isGreaterThan(1);
        }
    }
}
