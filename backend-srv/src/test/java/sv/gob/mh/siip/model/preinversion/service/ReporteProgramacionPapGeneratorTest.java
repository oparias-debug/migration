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

import sv.gob.mh.siip.model.preinversion.dto.EstudioFilaListaPAPDto;
import sv.gob.mh.siip.model.preinversion.dto.FuenteFinanciamientoDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;

class ReporteProgramacionPapGeneratorTest {

    @Test
    void generarExcel_escribeFilasTotalesYUsaDefectosParaCamposNulos() throws IOException {
        EstudioFilaListaPAPDto filaCompleta = new EstudioFilaListaPAPDto("08040", "Proyecto A", NombreEtapaDto.PERFIL)
                .fuenteFinanciamiento(FuenteFinanciamientoDto.FONDO_GENERAL)
                .costoEtapa(10000d)
                .ejecutadoAniosAnteriores(4000d)
                .montoCuatrimestre1(1000d)
                .montoCuatrimestre2(2000d)
                .montoCuatrimestre3(3000d)
                .totalProgramadoAnio(6000d)
                .aniosPosteriores(0d);
        EstudioFilaListaPAPDto filaSinDatosOpcionales = new EstudioFilaListaPAPDto("08041", "Proyecto B", null);

        byte[] bytes = ReporteProgramacionPapGenerator.generarExcel(25L, 2027,
                List.of(filaCompleta, filaSinDatosOpcionales));

        assertThat(bytes).isNotEmpty();
        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            XSSFSheet hoja = workbook.getSheetAt(0);
            assertThat(hoja.getRow(0).getCell(0).getStringCellValue())
                    .contains("Unidad Ejecutora 25", "Año 2027");
            assertThat(hoja.getRow(2).getCell(0).getStringCellValue()).isEqualTo("CUP");
            // Anexo A.1/A.8: columnas "Programación I, II y III Cuatrimestre".
            assertThat(hoja.getRow(2).getCell(6).getStringCellValue()).isEqualTo("I Cuatrimestre");
            assertThat(hoja.getRow(2).getCell(7).getStringCellValue()).isEqualTo("II Cuatrimestre");
            assertThat(hoja.getRow(2).getCell(8).getStringCellValue()).isEqualTo("III Cuatrimestre");
            assertThat(hoja.getRow(2).getCell(9).getStringCellValue()).isEqualTo("Total Año");

            Row filaCompletaXls = hoja.getRow(3);
            assertThat(filaCompletaXls.getCell(0).getStringCellValue()).isEqualTo("08040");
            assertThat(filaCompletaXls.getCell(2).getStringCellValue()).isEqualTo(NombreEtapaDto.PERFIL.getValue());
            assertThat(filaCompletaXls.getCell(3).getStringCellValue())
                    .isEqualTo(FuenteFinanciamientoDto.FONDO_GENERAL.getValue());
            assertThat(filaCompletaXls.getCell(4).getNumericCellValue()).isEqualTo(10000d);
            assertThat(filaCompletaXls.getCell(6).getNumericCellValue()).isEqualTo(1000d);
            assertThat(filaCompletaXls.getCell(7).getNumericCellValue()).isEqualTo(2000d);
            assertThat(filaCompletaXls.getCell(8).getNumericCellValue()).isEqualTo(3000d);

            Row filaSinDatosXls = hoja.getRow(4);
            assertThat(filaSinDatosXls.getCell(2).getStringCellValue()).isEmpty();
            assertThat(filaSinDatosXls.getCell(3).getStringCellValue()).isEmpty();
            assertThat(filaSinDatosXls.getCell(4).getNumericCellValue()).isZero();

            Row totalXls = hoja.getRow(5);
            assertThat(totalXls.getCell(1).getStringCellValue()).isEqualTo("TOTAL");
            assertThat(totalXls.getCell(4).getNumericCellValue()).isEqualTo(10000d);
            assertThat(totalXls.getCell(5).getNumericCellValue()).isEqualTo(4000d);
            assertThat(totalXls.getCell(6).getNumericCellValue()).isEqualTo(1000d);
            assertThat(totalXls.getCell(7).getNumericCellValue()).isEqualTo(2000d);
            assertThat(totalXls.getCell(8).getNumericCellValue()).isEqualTo(3000d);
            assertThat(totalXls.getCell(9).getNumericCellValue()).isEqualTo(6000d);
        }
    }

    @Test
    void generarExcel_conListaVacia_generaSoloEncabezadosYTotalesEnCero() throws IOException {
        byte[] bytes = ReporteProgramacionPapGenerator.generarExcel(25L, 2027, List.of());

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            Row totalXls = workbook.getSheetAt(0).getRow(3);
            assertThat(totalXls.getCell(4).getNumericCellValue()).isZero();
        }
    }

    @Test
    void generarPdf_conPocasFilas_generaUnaSolaPagina() throws IOException {
        EstudioFilaListaPAPDto fila = new EstudioFilaListaPAPDto("08040", "Proyecto A", NombreEtapaDto.PERFIL)
                .fuenteFinanciamiento(FuenteFinanciamientoDto.FONDO_GENERAL)
                .costoEtapa(10000d);

        byte[] bytes = ReporteProgramacionPapGenerator.generarPdf(25L, 2027, List.of(fila));

        assertThat(bytes).isNotEmpty();
        try (PDDocument documento = PDDocument.load(bytes)) {
            assertThat(documento.getNumberOfPages()).isEqualTo(1);
        }
    }

    @Test
    void generarPdf_conMuchasFilasYCamposNulos_generaMultiplesPaginas() throws IOException {
        List<EstudioFilaListaPAPDto> filas = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            filas.add(new EstudioFilaListaPAPDto("0804" + i, "Proyecto " + i, null));
        }

        byte[] bytes = ReporteProgramacionPapGenerator.generarPdf(25L, 2027, filas);

        assertThat(bytes).isNotEmpty();
        try (PDDocument documento = PDDocument.load(bytes)) {
            assertThat(documento.getNumberOfPages()).isGreaterThan(1);
        }
    }
}
