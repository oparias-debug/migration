package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

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
