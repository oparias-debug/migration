package sv.gob.mh.siip.model.preinversion.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/** Boilerplate común (título, encabezados, paginación) de los generadores de reportes Excel/PDF del PAP. */
final class ReportePapGeneratorSupport {

    private ReportePapGeneratorSupport() {
    }

    static double valorODefecto(Double valor) {
        return valor != null ? valor : 0d;
    }

    static String valorODefectoTexto(String valor) {
        return valor != null ? valor : "";
    }

    static void escribirMonto(Cell celda, Double valor) {
        celda.setCellValue(valorODefecto(valor));
    }

    static <T> byte[] generarExcel(String nombreHoja, String titulo, String[] encabezados, List<T> filas,
            BiConsumer<Row, T> escritorFila, String mensajeError) {
        return generarExcel(nombreHoja, titulo, encabezados, filas, escritorFila, null, mensajeError);
    }

    static <T> byte[] generarExcel(String nombreHoja, String titulo, String[] encabezados, List<T> filas,
            BiConsumer<Row, T> escritorFila, ObjIntConsumer<XSSFSheet> filaFinal, String mensajeError) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            XSSFSheet hoja = workbook.createSheet(nombreHoja);
            Row filaTitulo = hoja.createRow(0);
            filaTitulo.createCell(0).setCellValue(titulo);

            Row encabezado = hoja.createRow(2);
            for (int i = 0; i < encabezados.length; i++) {
                encabezado.createCell(i).setCellValue(encabezados[i]);
            }

            int numeroFila = 3;
            for (T fila : filas) {
                Row row = hoja.createRow(numeroFila++);
                escritorFila.accept(row, fila);
            }
            if (filaFinal != null) {
                filaFinal.accept(hoja, numeroFila);
            }

            for (int i = 0; i < encabezados.length; i++) {
                hoja.autoSizeColumn(i);
            }

            workbook.write(salida);
            return salida.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(mensajeError, e);
        }
    }

    static <T> byte[] generarPdf(String tituloPrincipal, String subtitulo, List<T> filas,
            Function<T, String> formateadorLinea, String mensajeError) {
        return generarPdf(tituloPrincipal, subtitulo, filas, formateadorLinea, List.of(), mensajeError);
    }

    /** Igual que el anterior, agregando al final {@code lineasFinales} (p. ej. fila TOTAL o comentarios DGICP). */
    static <T> byte[] generarPdf(String tituloPrincipal, String subtitulo, List<T> filas,
            Function<T, String> formateadorLinea, List<String> lineasFinales, String mensajeError) {
        List<String> lineas = new ArrayList<>(filas.size() + lineasFinales.size());
        for (T fila : filas) {
            lineas.add(formateadorLinea.apply(fila));
        }
        lineas.addAll(lineasFinales);
        return generarPdfLineas(tituloPrincipal, subtitulo, lineas, mensajeError);
    }

    private static byte[] generarPdfLineas(String tituloPrincipal, String subtitulo, List<String> filas,
            String mensajeError) {
        try (PDDocument documento = new PDDocument(); ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            PDType1Font fuenteNegrita = PDType1Font.HELVETICA_BOLD;
            PDType1Font fuenteNormal = PDType1Font.HELVETICA;

            PDPage pagina = new PDPage(PDRectangle.A4);
            documento.addPage(pagina);
            PDPageContentStream contenido = new PDPageContentStream(documento, pagina);
            float y = 780;
            contenido.beginText();
            contenido.setFont(fuenteNegrita, 12);
            contenido.newLineAtOffset(40, y);
            contenido.showText(tituloPrincipal);
            contenido.endText();
            y -= 18;
            contenido.beginText();
            contenido.setFont(fuenteNormal, 10);
            contenido.newLineAtOffset(40, y);
            contenido.showText(subtitulo);
            contenido.endText();
            y -= 24;

            for (String fila : filas) {
                if (y < 60) {
                    contenido.close();
                    pagina = new PDPage(PDRectangle.A4);
                    documento.addPage(pagina);
                    contenido = new PDPageContentStream(documento, pagina);
                    y = 780;
                }
                contenido.beginText();
                contenido.setFont(fuenteNormal, 8);
                contenido.newLineAtOffset(40, y);
                contenido.showText(fila);
                contenido.endText();
                y -= 14;
            }
            contenido.close();

            documento.save(salida);
            return salida.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(mensajeError, e);
        }
    }
}
