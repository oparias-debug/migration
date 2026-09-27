package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockConstruction;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.poi.ss.usermodel.Row;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

class ReportePapGeneratorSupportTest {

    private static final String MENSAJE_ERROR = "No se pudo generar el reporte.";
    private static final String[] ENCABEZADOS = {"Columna"};

    /** Relanza una excepción verificada sin declararla, para simular un fallo de E/S del escritor de filas. */
    @SuppressWarnings("unchecked")
    private static <E extends Exception> void lanzarSinDeclarar(Exception excepcion) throws E {
        throw (E) excepcion;
    }

    @Test
    void generarExcel_errorDeEscritura_seEnvuelveEnUncheckedIOException() {
        BiConsumer<Row, String> escritorQueFalla =
                (Row row, String fila) -> lanzarSinDeclarar(new IOException("disco lleno"));
        List<String> filas = List.of("fila");

        assertThatThrownBy(() -> ReportePapGeneratorSupport.generarExcel("Hoja", "Título", ENCABEZADOS, filas,
                escritorQueFalla, MENSAJE_ERROR))
                .isInstanceOf(UncheckedIOException.class)
                .hasMessage(MENSAJE_ERROR)
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    void generarPdf_errorDeEscritura_seEnvuelveEnUncheckedIOException() {
        List<String> filas = List.of("fila");
        Function<String, String> formateador = Function.identity();

        try (MockedConstruction<PDPageContentStream> contenido = mockConstruction(PDPageContentStream.class,
                (PDPageContentStream mock, MockedConstruction.Context contexto) ->
                        doThrow(new IOException("fuente no disponible")).when(mock).beginText())) {
            assertThatThrownBy(() -> ReportePapGeneratorSupport.generarPdf("Título", "Subtítulo", filas,
                    formateador, MENSAJE_ERROR))
                    .isInstanceOf(UncheckedIOException.class)
                    .hasMessage(MENSAJE_ERROR)
                    .hasCauseInstanceOf(IOException.class);
        }
    }
}
