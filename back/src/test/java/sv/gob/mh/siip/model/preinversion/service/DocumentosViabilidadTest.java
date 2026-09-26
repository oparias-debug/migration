package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.DocumentoViabilidad;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.TipoDocumentoViabilidad;
import sv.gob.mh.siip.model.preinversion.repository.DocumentoViabilidadRepository;

/** Pruebas unitarias de {@link DocumentosViabilidad} (CU-PRE-24, RN02; Anexo B.1). */
class DocumentosViabilidadTest {

    @TempDir
    Path directorio;

    private DocumentoViabilidadRepository repositorio;
    private DocumentosViabilidad documentos;
    private final Proyecto proyecto = Proyecto.builder().id(5L).build();
    private final Usuario tecnico = Usuario.builder().id(1L).nombreUsuario("urp").build();

    @BeforeEach
    void setUp() {
        repositorio = mock(DocumentoViabilidadRepository.class);
        when(repositorio.save(any())).thenAnswer(invocacion -> invocacion.getArgument(0));
        documentos = new DocumentosViabilidad(repositorio, directorio.toString());
    }

    private static MockMultipartFile archivo(String nombre) {
        return new MockMultipartFile("archivo", nombre, "application/octet-stream", new byte[] {1, 2, 3});
    }

    @ParameterizedTest
    @ValueSource(strings = {"preinversion.pdf", "PREINVERSION.DOC", "preinversion.docx"})
    void aceptaElDocumentoDePreinversionEnPdfOWord(String nombre) throws IOException {
        DocumentoViabilidad documento = documentos.cargar(proyecto, TipoDocumentoViabilidad.DOCUMENTO_PREINVERSION,
                archivo(nombre), tecnico);

        assertThat(documento.getNombreArchivo()).isEqualTo(nombre);
        assertThat(documento.getUsuarioCarga()).isEqualTo(tecnico);
        assertThat(documento.getFechaCarga()).isNotNull();
        assertThat(Files.readAllBytes(Path.of(documento.getRutaArchivo()))).containsExactly(1, 2, 3);
    }

    @Test
    void elNuevoDocumentoDePreinversionReemplazaAlVigente() throws IOException {
        Path anterior = Files.createFile(directorio.resolve("anterior.pdf"));
        DocumentoViabilidad vigente = DocumentoViabilidad.builder().id(9L).rutaArchivo(anterior.toString()).build();
        when(repositorio.findFirstByProyectoIdAndTipoDocumento(5L, TipoDocumentoViabilidad.DOCUMENTO_PREINVERSION))
                .thenReturn(Optional.of(vigente));

        documentos.cargar(proyecto, TipoDocumentoViabilidad.DOCUMENTO_PREINVERSION, archivo("nuevo.pdf"), tecnico);

        assertThat(anterior).doesNotExist();
        verify(repositorio).delete(vigente);
    }

    @Test
    void losOtrosDocumentosSeAcumulanYAdmitenCualquierFormato() {
        DocumentoViabilidad documento = documentos.cargar(proyecto, TipoDocumentoViabilidad.OTRO_DOCUMENTO,
                archivo("carpeta\\anexo técnico.x_y"), tecnico);

        assertThat(documento.getNombreArchivo()).isEqualTo("anexo técnico.x_y");
        // La extensión no alfanumérica no pasa al nombre en disco.
        assertThat(Path.of(documento.getRutaArchivo()).getFileName().toString()).doesNotContain(".x_y");
        verify(repositorio, never()).findFirstByProyectoIdAndTipoDocumento(any(), any());
    }

    @Test
    void rechazaUnDocumentoDePreinversionEnOtroFormato() {
        MultipartFile texto = archivo("preinversion.txt");

        assertThatThrownBy(() -> documentos.cargar(proyecto, TipoDocumentoViabilidad.DOCUMENTO_PREINVERSION, texto, tecnico))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("PDF o Word");
        verify(repositorio, never()).save(any());
    }

    @Test
    void rechazaTipoOArchivoAusentes() {
        MultipartFile vacio = new MockMultipartFile("archivo", "vacio.pdf", "application/pdf", new byte[0]);
        MultipartFile sinNombre = new MockMultipartFile("archivo", "", "application/pdf", new byte[] {1});
        MultipartFile soloRuta = new MockMultipartFile("archivo", "/", "application/pdf", new byte[] {1});
        MultipartFile valido = archivo("p.pdf");

        assertThatThrownBy(() -> documentos.cargar(proyecto, null, valido, tecnico))
                .isInstanceOf(ValidacionNegocioException.class).hasMessageContaining("tipo de documento");
        assertThatThrownBy(() -> documentos.cargar(proyecto, TipoDocumentoViabilidad.OTRO_DOCUMENTO, null, tecnico))
                .isInstanceOf(ValidacionNegocioException.class).hasMessageContaining("con contenido");
        assertThatThrownBy(() -> documentos.cargar(proyecto, TipoDocumentoViabilidad.OTRO_DOCUMENTO, vacio, tecnico))
                .isInstanceOf(ValidacionNegocioException.class).hasMessageContaining("con contenido");
        assertThatThrownBy(() -> documentos.cargar(proyecto, TipoDocumentoViabilidad.OTRO_DOCUMENTO, sinNombre, tecnico))
                .isInstanceOf(ValidacionNegocioException.class).hasMessageContaining("nombre");
        assertThatThrownBy(() -> documentos.cargar(proyecto, TipoDocumentoViabilidad.OTRO_DOCUMENTO, soloRuta, tecnico))
                .isInstanceOf(ValidacionNegocioException.class).hasMessageContaining("nombre");
    }

    @Test
    void siNoSePuedeEscribirElArchivoSeInformaElFallo() throws IOException {
        // Un archivo común en el lugar del directorio del proyecto impide crear la carpeta.
        Files.createDirectories(directorio.resolve("viabilidad"));
        Files.createFile(directorio.resolve("viabilidad").resolve("5"));
        MultipartFile valido = archivo("p.pdf");

        assertThatThrownBy(() -> documentos.cargar(proyecto, TipoDocumentoViabilidad.OTRO_DOCUMENTO, valido, tecnico))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("almacenar");
    }

    @Test
    void listaYConsultaElDocumentoDePreinversion() {
        DocumentoViabilidad documento = DocumentoViabilidad.builder().id(1L).build();
        when(repositorio.findByProyectoIdOrderByFechaCargaAscIdAsc(5L)).thenReturn(List.of(documento));
        when(repositorio.existsByProyectoIdAndTipoDocumento(5L, TipoDocumentoViabilidad.DOCUMENTO_PREINVERSION))
                .thenReturn(true);

        assertThat(documentos.listar(5L)).containsExactly(documento);
        assertThat(documentos.tieneDocumentoPreinversion(5L)).isTrue();
    }
}
