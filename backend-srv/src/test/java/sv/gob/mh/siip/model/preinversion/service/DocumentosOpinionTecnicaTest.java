package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.DocumentoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.TipoDocumentoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.repository.DocumentoOpinionTecnicaRepository;

/** Pruebas unitarias de {@link DocumentosOpinionTecnica} (CU-PRE-26, RN04 y FA01 paso 1.5). */
class DocumentosOpinionTecnicaTest {

    @TempDir
    Path directorio;

    private DocumentoOpinionTecnicaRepository repositorio;
    private DocumentosOpinionTecnica documentos;
    private OpinionTecnica gestion;
    private final Usuario usuario = Usuario.builder().id(1L).build();

    @BeforeEach
    void setUp() {
        repositorio = mock(DocumentoOpinionTecnicaRepository.class);
        when(repositorio.save(any())).thenAnswer(i -> i.getArgument(0));
        documentos = new DocumentosOpinionTecnica(repositorio, directorio.toString());
        gestion = OpinionTecnica.builder().id(8L).proyecto(Proyecto.builder().id(2L).build()).build();
    }

    private static MockMultipartFile archivo(String nombre) {
        return new MockMultipartFile("nota", nombre, "application/pdf", "contenido".getBytes(StandardCharsets.UTF_8));
    }

    /** Extensión con la que queda el archivo en disco. */
    private String extensionEnDisco(String nombre) {
        String enDisco = Path.of(documentos.cargar(gestion, TipoDocumentoOpinionTecnica.NOTA_OT, archivo(nombre),
                usuario).getRutaArchivo()).getFileName().toString();
        int punto = enDisco.lastIndexOf('.');
        return punto < 0 ? "" : enDisco.substring(punto);
    }

    @Test
    void soloConservaEnDiscoExtensionesAsciiCortas() {
        assertThat(extensionEnDisco("nota.Pdf")).isEqualTo(".pdf");
        assertThat(extensionEnDisco("nota.7z")).isEqualTo(".7z");
        assertThat(extensionEnDisco("nota")).isEmpty();
        assertThat(extensionEnDisco("nota.")).isEmpty();
        assertThat(extensionEnDisco("nota.pd f")).isEmpty();
        assertThat(extensionEnDisco("nota.pdé")).isEmpty();
        assertThat(extensionEnDisco("nota.extensionlarga")).isEmpty();
    }

    @Test
    void siNoSePuedeEliminarLaNotaReemplazadaSeInformaElFallo() throws IOException {
        // Un directorio con contenido en la ruta de la nota vigente impide eliminarla.
        Path anterior = Files.createDirectories(directorio.resolve("anterior"));
        Files.createFile(anterior.resolve("contenido.txt"));
        DocumentoOpinionTecnica vigente = DocumentoOpinionTecnica.builder().id(4L).rutaArchivo(anterior.toString())
                .build();
        when(repositorio.findFirstByOpinionTecnicaIdAndTipoDocumento(8L, TipoDocumentoOpinionTecnica.NOTA_OT))
                .thenReturn(Optional.of(vigente));
        MultipartFile nueva = archivo("nota.pdf");

        assertThatThrownBy(() -> documentos.cargar(gestion, TipoDocumentoOpinionTecnica.NOTA_OT, nueva, usuario))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("eliminar");
        verify(repositorio, never()).delete(vigente);
    }

    @Test
    void siNoSePuedeEscribirLaNotaSeInformaElFallo() throws IOException {
        // Un archivo común en el lugar del directorio del proyecto impide crear la carpeta.
        Files.createDirectories(directorio.resolve("opinion-tecnica"));
        Files.createFile(directorio.resolve("opinion-tecnica").resolve("2"));
        MultipartFile nota = archivo("nota.pdf");

        assertThatThrownBy(() -> documentos.cargar(gestion, TipoDocumentoOpinionTecnica.NOTA_OT, nota, usuario))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("almacenar");
    }

    @Test
    void guardaLaNotaEnDiscoConSuNombreOriginalSinRutas() {
        DocumentoOpinionTecnica nota = documentos.cargar(gestion, TipoDocumentoOpinionTecnica.NOTA_SOLICITUD_OT,
                archivo("C:\\Users\\urp\\nota solicitud.PDF"), usuario);

        assertThat(nota.getNombreArchivo()).isEqualTo("nota solicitud.PDF");
        assertThat(nota.getRutaArchivo()).endsWith(".pdf");
        assertThat(Files.exists(Path.of(nota.getRutaArchivo()))).isTrue();
    }

    @Test
    void cadaNotaEsUnicaPorGestionYReemplazaALaAnterior() throws Exception {
        Path anterior = Files.createFile(directorio.resolve("anterior.pdf"));
        DocumentoOpinionTecnica vigente = DocumentoOpinionTecnica.builder().id(3L).rutaArchivo(anterior.toString())
                .build();
        when(repositorio.findFirstByOpinionTecnicaIdAndTipoDocumento(8L, TipoDocumentoOpinionTecnica.NOTA_OT))
                .thenReturn(Optional.of(vigente));

        DocumentoOpinionTecnica nueva = documentos.cargar(gestion, TipoDocumentoOpinionTecnica.NOTA_OT,
                archivo("nota-ot.extension-demasiado-larga"), usuario);

        verify(repositorio).delete(vigente);
        assertThat(Files.exists(anterior)).isFalse();
        // Una extensión que no es segura no se conserva en disco.
        assertThat(nueva.getRutaArchivo()).doesNotContain(".extension");
    }

    @Test
    void rechazaUnArchivoSinNombre() {
        MockMultipartFile sinNombre = archivo("");
        MockMultipartFile nombreNulo = new MockMultipartFile("nota", null, "application/pdf", new byte[] {1});
        assertThatThrownBy(() -> documentos.cargar(gestion, TipoDocumentoOpinionTecnica.NOTA_OT, sinNombre, usuario))
                .isInstanceOf(ValidacionNegocioException.class)
                .satisfies(e -> assertThat(((ValidacionNegocioException) e).getDetalles().get(0).getCampo())
                        .isEqualTo("notaOt"));
        assertThatThrownBy(() -> documentos.cargar(gestion, TipoDocumentoOpinionTecnica.NOTA_SOLICITUD_OT, nombreNulo,
                usuario))
                .isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    void distingueArchivosSinContenidoYListaLasNotas() {
        assertThat(DocumentosOpinionTecnica.tieneContenido(null)).isFalse();
        assertThat(DocumentosOpinionTecnica.tieneContenido(new MockMultipartFile("nota", new byte[0]))).isFalse();
        assertThat(DocumentosOpinionTecnica.tieneContenido(archivo("nota.pdf"))).isTrue();

        when(repositorio.findByOpinionTecnicaIdOrderByIdAsc(8L)).thenReturn(List.of());
        assertThat(documentos.listar(8L)).isEmpty();
    }
}
