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

    /** Fila (0-based) de la hoja Excel con los encabezados de columna; la fila 1 queda en blanco. */
    private static final int FILA_ENCABEZADOS = 2;
    /** Primera fila (0-based) de datos en la hoja Excel. */
    private static final int PRIMERA_FILA_DATOS = 3;

    /** Coordenada vertical inicial (puntos PDF) del contenido de cada página A4. */
    private static final float Y_INICIAL_PAGINA = 780F;
    /** Margen izquierdo (puntos PDF) de todas las líneas. */
    private static final float MARGEN_IZQUIERDO = 40F;
    /** Por debajo de esta coordenada vertical se inicia una página nueva. */
    private static final float Y_MINIMO_PAGINA = 60F;
    private static final float TAMANIO_FUENTE_TITULO = 12F;
    private static final float TAMANIO_FUENTE_SUBTITULO = 10F;
    private static final float TAMANIO_FUENTE_FILA = 8F;
    private static final float ESPACIO_TRAS_TITULO = 18F;
    private static final float ESPACIO_TRAS_SUBTITULO = 24F;
    private static final float INTERLINEADO_FILA = 14F;

    private ReportePapGeneratorSupport() {
    }

    static double valorODefecto(Double valor) {
        return valor != null ? valor : 0D;
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

            Row encabezado = hoja.createRow(FILA_ENCABEZADOS);
            for (int i = 0; i < encabezados.length; i++) {
                encabezado.createCell(i).setCellValue(encabezados[i]);
            }

            int numeroFila = PRIMERA_FILA_DATOS;
            for (T fila : filas) {
                Row row = hoja.createRow(numeroFila);
                numeroFila++;
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
            float y = Y_INICIAL_PAGINA;
            contenido.beginText();
            contenido.setFont(fuenteNegrita, TAMANIO_FUENTE_TITULO);
            contenido.newLineAtOffset(MARGEN_IZQUIERDO, y);
            contenido.showText(tituloPrincipal);
            contenido.endText();
            y -= ESPACIO_TRAS_TITULO;
            contenido.beginText();
            contenido.setFont(fuenteNormal, TAMANIO_FUENTE_SUBTITULO);
            contenido.newLineAtOffset(MARGEN_IZQUIERDO, y);
            contenido.showText(subtitulo);
            contenido.endText();
            y -= ESPACIO_TRAS_SUBTITULO;

            for (String fila : filas) {
                if (y < Y_MINIMO_PAGINA) {
                    contenido.close();
                    pagina = new PDPage(PDRectangle.A4);
                    documento.addPage(pagina);
                    contenido = new PDPageContentStream(documento, pagina);
                    y = Y_INICIAL_PAGINA;
                }
                contenido.beginText();
                contenido.setFont(fuenteNormal, TAMANIO_FUENTE_FILA);
                contenido.newLineAtOffset(MARGEN_IZQUIERDO, y);
                contenido.showText(fila);
                contenido.endText();
                y -= INTERLINEADO_FILA;
            }
            contenido.close();

            documento.save(salida);
            return salida.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(mensajeError, e);
        }
    }
}
