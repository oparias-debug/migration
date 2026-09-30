package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.domain.ComentarioOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.DocumentoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.DocumentoViabilidad;
import sv.gob.mh.siip.model.preinversion.dto.DocumentoAnexoDto;
import sv.gob.mh.siip.model.preinversion.dto.DocumentosAnexosOpinionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoDocumentoAnexoDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoDocumentoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.enums.TipoDocumentoViabilidad;

/** Pruebas unitarias de {@link DocumentosAnexosOpinionTecnica} (CU-PRE-26, Anexo A.1 "Documentos anexos"). */
class DocumentosAnexosOpinionTecnicaTest {

    private final DocumentosViabilidad documentosViabilidad = mock(DocumentosViabilidad.class);
    private final DocumentosAnexosOpinionTecnica anexos = new DocumentosAnexosOpinionTecnica(documentosViabilidad);

    private static DocumentoOpinionTecnica nota(long id, TipoDocumentoOpinionTecnica tipo) {
        return DocumentoOpinionTecnica.builder().id(id).tipoDocumento(tipo).nombreArchivo(tipo.name() + ".pdf").build();
    }

    @Test
    void reuneLaNotaDeSolicitudYLosDocumentosDeViabilidad() {
        when(documentosViabilidad.listar(1L)).thenReturn(List.of(
                DocumentoViabilidad.builder().id(5L).tipoDocumento(TipoDocumentoViabilidad.DOCUMENTO_PREINVERSION)
                        .nombreArchivo("preinversion.pdf").build(),
                DocumentoViabilidad.builder().id(6L).tipoDocumento(TipoDocumentoViabilidad.OTRO_DOCUMENTO)
                        .nombreArchivo("anexo.pdf").build()));
        ComentarioOpinionTecnica comentario = ComentarioOpinionTecnica.builder().comentario("Falta firma")
                .justificacionInstitucion("Firmado").build();

        DocumentosAnexosOpinionTecnicaDto seccion = anexos.seccion(1L, List.of(
                nota(1L, TipoDocumentoOpinionTecnica.NOTA_SOLICITUD_OT), nota(2L, TipoDocumentoOpinionTecnica.NOTA_OT)),
                comentario);

        assertThat(seccion.getDocumentos()).extracting(DocumentoAnexoDto::getTipoDocumento).containsExactly(
                TipoDocumentoAnexoDto.NOTA_SOLICITUD_OT, TipoDocumentoAnexoDto.DOCUMENTO_PREINVERSION,
                TipoDocumentoAnexoDto.OTROS_DOCUMENTOS_ANEXOS);
        assertThat(seccion.getComentarioDgicpDocumentosAnexos()).isEqualTo("Falta firma");
        assertThat(seccion.getJustificacionInstitucionDocumentosAnexos()).isEqualTo("Firmado");
    }

    @Test
    void sinComentarioLaSeccionNoLlevaTextos() {
        when(documentosViabilidad.listar(1L)).thenReturn(List.of());

        DocumentosAnexosOpinionTecnicaDto seccion = anexos.seccion(1L, List.of(), null);

        assertThat(seccion.getDocumentos()).isEmpty();
        assertThat(seccion.getComentarioDgicpDocumentosAnexos()).isNull();
    }

    @Test
    void laNotaDeOtSoloApareceSiSeCargo() {
        assertThat(DocumentosAnexosOpinionTecnica.notaOt(List.of(nota(1L, TipoDocumentoOpinionTecnica.NOTA_SOLICITUD_OT))))
                .isEmpty();
        assertThat(DocumentosAnexosOpinionTecnica.notaOt(List.of(nota(2L, TipoDocumentoOpinionTecnica.NOTA_OT))))
                .hasValueSatisfying(d -> assertThat(d.getTipoDocumento()).isEqualTo(TipoDocumentoAnexoDto.NOTA_OT));
    }
}
