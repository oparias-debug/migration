package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import sv.gob.mh.siip.exception.ReglaNegocioException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoDocumentoOpinionTecnica;

/**
 * Datos que el Técnico PRE registra al emitir la OT favorable (FA01 paso 1.5): la "Nota de OT" firmada
 * por el Director DGICP y su "N° de nota de OT" (Anexo B.1). Guarda también la "Inversión estimada" del
 * momento, que muestra el Histórico de OT (Anexo A1.5).
 */
@Component
@Transactional
public class NotaEmisionOpinionTecnica {

    public static final String NOTA_OT_REQUERIDA = "NOTA_OT_REQUERIDA";
    public static final String NUMERO_NOTA_OT_REQUERIDO = "NUMERO_NOTA_OT_REQUERIDO";

    /** Longitud de la columna {@code NUMERO_NOTA_OT}. */
    public static final int LONGITUD_NUMERO = 50;

    private final DocumentosOpinionTecnica documentos;
    private final FichaViabilidadPresupuesto presupuesto;

    public NotaEmisionOpinionTecnica(DocumentosOpinionTecnica documentos, FichaViabilidadPresupuesto presupuesto) {
        this.documentos = documentos;
        this.presupuesto = presupuesto;
    }

    /**
     * Valida la nota y su número y los registra en la gestión.
     *
     * @param gestion gestión que se emite
     * @param notaOt archivo "Nota de OT"
     * @param numeroNotaOt "N° de nota de OT"
     * @param actor Técnico PRE que emite
     */
    public void registrar(OpinionTecnica gestion, MultipartFile notaOt, String numeroNotaOt, Usuario actor) {
        if (!DocumentosOpinionTecnica.tieneContenido(notaOt)) {
            throw new ReglaNegocioException(NOTA_OT_REQUERIDA,
                    "Debe cargar la Nota de OT firmada por el Director DGICP para emitir la Opinión Técnica.",
                    List.of(new ErrorDetalleDto().campo("notaOt").mensaje("Archivo requerido.")));
        }
        String numero = numeroNotaOt == null ? "" : numeroNotaOt.strip();
        if (numero.isEmpty() || numero.length() > LONGITUD_NUMERO) {
            throw new ReglaNegocioException(NUMERO_NOTA_OT_REQUERIDO,
                    "Debe registrar el N° de nota de OT, de hasta " + LONGITUD_NUMERO + " caracteres.",
                    List.of(new ErrorDetalleDto().campo("numeroNotaOt").mensaje("Número requerido.")));
        }
        documentos.cargar(gestion, TipoDocumentoOpinionTecnica.NOTA_OT, notaOt, actor);
        gestion.setNumeroNotaOt(numero);
        gestion.setInversionEstimada(presupuesto.inversionEstimada(gestion.getProyecto().getId()));
    }
}
