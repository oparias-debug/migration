package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.preinversion.domain.ComentarioOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.DocumentoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.DocumentoViabilidad;
import sv.gob.mh.siip.model.preinversion.dto.DocumentoAnexoDto;
import sv.gob.mh.siip.model.preinversion.dto.DocumentosAnexosOpinionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoDocumentoAnexoDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoDocumentoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.enums.TipoDocumentoViabilidad;

/**
 * Sección "Documentos anexos" de la pantalla de Opinión Técnica (CU-PRE-26, Anexo A.1): la "Nota de
 * solicitud de OT" de la gestión y el "Documento de Preinversión" y "Otros documentos anexos" que el
 * Técnico URP cargó en CU-PRE-24, con su comentario DGICP y su justificación.
 */
@Component
@Transactional(readOnly = true)
public class DocumentosAnexosOpinionTecnica {

    private final DocumentosViabilidad documentosViabilidad;

    public DocumentosAnexosOpinionTecnica(DocumentosViabilidad documentosViabilidad) {
        this.documentosViabilidad = documentosViabilidad;
    }

    /**
     * @param idProyecto identificador del proyecto
     * @param notas notas de la gestión
     * @param comentario comentario DGICP a la documentación anexa, o {@code null}
     * @return la sección de documentos anexos
     */
    public DocumentosAnexosOpinionTecnicaDto seccion(Long idProyecto, Collection<DocumentoOpinionTecnica> notas,
            ComentarioOpinionTecnica comentario) {
        Stream<DocumentoAnexoDto> notaSolicitud = notas.stream()
                .filter((DocumentoOpinionTecnica n) ->
                        n.getTipoDocumento() == TipoDocumentoOpinionTecnica.NOTA_SOLICITUD_OT)
                .map(DocumentosAnexosOpinionTecnica::documento);
        Stream<DocumentoAnexoDto> deViabilidad = documentosViabilidad.listar(idProyecto).stream()
                .map(DocumentosAnexosOpinionTecnica::documento);
        List<DocumentoAnexoDto> documentos = new ArrayList<>(Stream.concat(notaSolicitud, deViabilidad).toList());
        DocumentosAnexosOpinionTecnicaDto dto = new DocumentosAnexosOpinionTecnicaDto(documentos);
        if (comentario != null) {
            dto.setComentarioDgicpDocumentosAnexos(comentario.getComentario());
            dto.setJustificacionInstitucionDocumentosAnexos(comentario.getJustificacionInstitucion());
        }
        return dto;
    }

    /**
     * @param notas notas de la gestión
     * @return la "Nota de OT" firmada por el Director DGICP, si se cargó (FA01 paso 1.5)
     */
    public static Optional<DocumentoAnexoDto> notaOt(Collection<DocumentoOpinionTecnica> notas) {
        return notas.stream()
                .filter((DocumentoOpinionTecnica n) -> n.getTipoDocumento() == TipoDocumentoOpinionTecnica.NOTA_OT)
                .findFirst()
                .map(DocumentosAnexosOpinionTecnica::documento);
    }

    private static DocumentoAnexoDto documento(DocumentoOpinionTecnica nota) {
        return new DocumentoAnexoDto(nota.getId(), TipoDocumentoAnexoDto.valueOf(nota.getTipoDocumento().name()),
                nota.getNombreArchivo());
    }

    private static DocumentoAnexoDto documento(DocumentoViabilidad documento) {
        TipoDocumentoAnexoDto tipo = documento.getTipoDocumento() == TipoDocumentoViabilidad.DOCUMENTO_PREINVERSION
                ? TipoDocumentoAnexoDto.DOCUMENTO_PREINVERSION : TipoDocumentoAnexoDto.OTROS_DOCUMENTOS_ANEXOS;
        return new DocumentoAnexoDto(documento.getId(), tipo, documento.getNombreArchivo());
    }
}
