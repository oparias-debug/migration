package sv.gob.mh.siip.model.preinversion.service;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import sv.gob.mh.siip.model.preinversion.domain.DocumentoViabilidad;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.RevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.dto.CampoFichaViabilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.ComentarioCampoViabilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.DocumentoViabilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoDocumentoViabilidadDto;

/**
 * Conversión a DTO de los datos propios de la ficha de Viabilidad (CU-PRE-24): encabezado del
 * proyecto, documentos cargados y comentarios del Viabilizador.
 */
final class ViabilidadRespuestas {

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private ViabilidadRespuestas() {
    }

    /**
     * Arma la ficha con los datos propios de CU-PRE-24; los campos de consulta los completa
     * {@link FichaViabilidadEnsamblador}.
     */
    static FichaViabilidadResponseDto ficha(ViabilidadContexto contexto, List<DocumentoViabilidad> documentos) {
        var proyecto = contexto.proyecto();
        List<DocumentoViabilidadDto> documentosDto = documentos.stream()
                .map(ViabilidadRespuestas::documento)
                .toList();
        var ficha = new FichaViabilidadResponseDto(proyecto.getId(), proyecto.getCup(),
                proyecto.getNombre(), proyecto.getEstado().getEtiquetaUi(), documentosDto,
                comentarios(contexto.ultima()), contexto.acciones());
        ficha.setObservacionesGeneralesJustificacion(contexto.observacionesGenerales());
        return ficha;
    }

    static DocumentoViabilidadDto documento(DocumentoViabilidad documento) {
        return new DocumentoViabilidadDto(documento.getId(),
                TipoDocumentoViabilidadDto.valueOf(documento.getTipoDocumento().name()),
                documento.getNombreArchivo(),
                documento.getFechaCarga().atZone(ZONA_EL_SALVADOR).toOffsetDateTime());
    }

    static List<ComentarioCampoViabilidadDto> comentarios(RevisionViabilidad revision) {
        if (revision == null) {
            return new ArrayList<>();
        }
        return revision.getComentarios().stream()
                .map(c -> new ComentarioCampoViabilidadDto(CampoFichaViabilidadDto.valueOf(c.getCampo().name()),
                        c.getComentario()))
                .toList();
    }
}
