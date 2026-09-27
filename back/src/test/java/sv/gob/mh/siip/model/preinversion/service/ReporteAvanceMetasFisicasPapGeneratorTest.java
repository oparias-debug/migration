package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.dto.EstadoAvanceMetasDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioFilaAvanceMetasDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;

class ReporteAvanceMetasFisicasPapGeneratorTest {

    private static final int COL_ETAPA = 2;
    private static final int COL_META = 3;
    private static final int COL_ESTADO = 8;

    private static EstudioFilaAvanceMetasDto filaCompleta() {
        return new EstudioFilaAvanceMetasDto("08040", "Proyecto A", NombreEtapaDto.PERFIL)
                .meta(1d).ejecutadoAniosAnteriores(0.2).programadoEnElAnio(0.5).ejecutadoEnElAnio(0.4)
                .totalMetaEjecutada(0.6).estado(EstadoAvanceMetasDto.ATRASADO);
    }

    @Test
    void generarExcel_escribeFilasYUsaDefectosParaEtapaYEstadoNulos() throws IOException {
        EstudioFilaAvanceMetasDto sinOpcionales = new EstudioFilaAvanceMetasDto("08041", "Proyecto B", null);

        byte[] bytes = ReporteAvanceMetasFisicasPapGenerator.generarExcel(25L, 2027, Cuatrimestre.CUATRIMESTRE_II,
                List.of(filaCompleta(), sinOpcionales));

        try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            XSSFSheet hoja = workbook.getSheetAt(0);
            assertThat(hoja.getRow(0).getCell(0).getStringCellValue())
                    .contains("Unidad Ejecutora 25", "Año 2027", "CUATRIMESTRE_II");
            Row completa = hoja.getRow(3);
            assertThat(completa.getCell(COL_ETAPA).getStringCellValue()).isEqualTo(NombreEtapaDto.PERFIL.getValue());
            assertThat(completa.getCell(COL_META).getNumericCellValue()).isEqualTo(1d);
            assertThat(completa.getCell(COL_ESTADO).getStringCellValue())
                    .isEqualTo(EstadoAvanceMetasDto.ATRASADO.getValue());
            Row incompleta = hoja.getRow(4);
            assertThat(incompleta.getCell(COL_ETAPA).getStringCellValue()).isEmpty();
            assertThat(incompleta.getCell(COL_ESTADO).getStringCellValue()).isEmpty();
        }
    }

    @Test
    void generarPdf_incluyeCadaFilaYToleraCamposNulos() throws IOException {
        EstudioFilaAvanceMetasDto sinOpcionales = new EstudioFilaAvanceMetasDto("08041", "Proyecto B", null);

        byte[] bytes = ReporteAvanceMetasFisicasPapGenerator.generarPdf(25L, 2027, Cuatrimestre.CUATRIMESTRE_I,
                List.of(filaCompleta(), sinOpcionales));

        try (PDDocument documento = PDDocument.load(bytes)) {
            String texto = new PDFTextStripper().getText(documento);
            assertThat(texto).contains("08040 | Proyecto A", "0.50 | 0.40 | ATRASADO", "08041 | Proyecto B");
        }
    }
}
