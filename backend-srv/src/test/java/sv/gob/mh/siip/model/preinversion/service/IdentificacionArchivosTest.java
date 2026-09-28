package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import sv.gob.mh.siip.exception.FormatoArchivoNoSoportadoException;

/** Pruebas unitarias de {@link IdentificacionArchivos} (CU-PRE-04, RNB-1/RNB-2). */
class IdentificacionArchivosTest {

    @TempDir
    Path directorio;

    private IdentificacionArchivos archivos;

    @BeforeEach
    void setUp() {
        archivos = new IdentificacionArchivos(directorio.toString());
    }

    private static MockMultipartFile archivo(String nombre, String contentType) {
        return new MockMultipartFile("archivo", nombre, contentType, new byte[] {1, 2, 3});
    }

    @Test
    void validarFormatoPdf_aceptaPorTipoDeContenido() {
        MockMultipartFile pdf = archivo("arbol", "APPLICATION/PDF");

        assertThatCode(() -> archivos.validarFormatoPdf(pdf)).doesNotThrowAnyException();
    }

    @Test
    void validarFormatoPdf_aceptaPorExtension() {
        MockMultipartFile pdf = archivo("ARBOL.PDF", "application/octet-stream");

        assertThatCode(() -> archivos.validarFormatoPdf(pdf)).doesNotThrowAnyException();
    }

    @Test
    void validarFormatoPdf_rechazaOtroFormato() {
        MockMultipartFile word = archivo("arbol.docx", "application/octet-stream");

        assertThatThrownBy(() -> archivos.validarFormatoPdf(word))
                .isInstanceOf(FormatoArchivoNoSoportadoException.class)
                .hasMessage("El archivo debe estar en formato PDF/A.");
    }

    @Test
    void validarFormatoPdf_sinNombreNiTipoPdf_rechaza() {
        MockMultipartFile sinNombre = new MockMultipartFile("archivo", null, null, new byte[] {1});

        assertThatThrownBy(() -> archivos.validarFormatoPdf(sinNombre))
                .isInstanceOf(FormatoArchivoNoSoportadoException.class);
    }

    @Test
    void guardar_escribeElArchivoConNombreFijoYReemplazaElAnterior() throws IOException {
        archivos.guardar(7L, "arbol-problemas.pdf", archivo("viejo.pdf", "application/pdf"));
        MockMultipartFile nuevo = new MockMultipartFile("archivo", "nuevo.pdf", "application/pdf",
                new byte[] {9, 9});

        String ruta = archivos.guardar(7L, "arbol-problemas.pdf", nuevo);

        Path esperada = directorio.resolve("identificacion").resolve("7").resolve("arbol-problemas.pdf");
        assertThat(ruta).isEqualTo(esperada.toString());
        assertThat(Files.readAllBytes(esperada)).containsExactly(9, 9);
    }

    @Test
    void guardar_directorioNoCreable_lanzaIllegalState() throws IOException {
        // Un archivo común en el lugar del directorio del proyecto impide crear la carpeta.
        Files.createDirectories(directorio.resolve("identificacion"));
        Files.createFile(directorio.resolve("identificacion").resolve("7"));
        MockMultipartFile pdf = archivo("arbol.pdf", "application/pdf");

        assertThatThrownBy(() -> archivos.guardar(7L, "arbol-problemas.pdf", pdf))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No se pudo almacenar el archivo cargado.");
    }

    @Test
    void abrir_archivoExistente_devuelveRecurso() throws IOException {
        Path ruta = Files.createFile(directorio.resolve("arbol.pdf"));

        Optional<Resource> recurso = archivos.abrir(ruta.toString());

        assertThat(recurso).hasValueSatisfying(contenido -> assertThat(contenido.exists()).isTrue());
    }

    @Test
    void abrir_archivoInexistente_devuelveVacio() {
        assertThat(archivos.abrir(directorio.resolve("no-existe.pdf").toString())).isEmpty();
    }

    @Test
    void eliminar_borraElArchivoYToleraQueYaNoExista() throws IOException {
        Path ruta = Files.createFile(directorio.resolve("arbol.pdf"));

        archivos.eliminar(ruta.toString());

        assertThat(ruta).doesNotExist();
        assertThatCode(() -> archivos.eliminar(ruta.toString())).doesNotThrowAnyException();
    }

    @Test
    void eliminar_directorioNoVacio_lanzaIllegalState() throws IOException {
        Path carpeta = Files.createDirectories(directorio.resolve("carpeta"));
        Files.createFile(carpeta.resolve("contenido.pdf"));
        String ruta = carpeta.toString();

        assertThatThrownBy(() -> archivos.eliminar(ruta))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No se pudo eliminar el archivo almacenado.");
    }
}
