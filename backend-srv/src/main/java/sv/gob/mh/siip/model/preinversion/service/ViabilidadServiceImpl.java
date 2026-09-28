package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import sv.gob.mh.siip.model.preinversion.dto.CargarDocumentoViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.EmitirViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.EnviarComentariosViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarComentariosViabilidadRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarComentariosViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoDocumentoViabilidad;

/**
 * Implementación de CU-PRE-24 "Viabilidad".
 *
 * <p>El estado de la gestión se deriva de la última revisión de Viabilidad del proyecto:
 * <ul>
 * <li>sin revisiones, o con la última devuelta: el Técnico URP puede solicitar Viabilidad;</li>
 * <li>con una revisión en curso: el Viabilizador puede guardar comentarios, devolver o emitir, y la
 * formulación queda bloqueada porque el proyecto pasa a "En viabilidad" (RN04);</li>
 * <li>con la última emitida: la ficha queda deshabilitada (FA02 paso 2.5), salvo que después la OT
 * haya devuelto el proyecto para ajustes, caso en que se puede volver a solicitar (RN03, RN11).</li>
 * </ul>
 * Cada cierre de revisión queda además registrado como un resultado de Viabilidad (resultado de la
 * evaluación).
 *
 * <p>Orquesta los colaboradores del caso de uso: {@link ViabilidadAcceso} abre cada operación,
 * {@link ViabilidadSolicitud}, {@link ViabilidadComentarios} y {@link ViabilidadCierre} aplican sus
 * reglas, y {@link FichaViabilidadEnsamblador} completa los campos de consulta de la ficha.
 */
@Service
@Transactional
public class ViabilidadServiceImpl implements ViabilidadService {

    public static final String PROYECTO_NO_ENCONTRADO = ViabilidadAcceso.PROYECTO_NO_ENCONTRADO;
    public static final String SOLICITUD_VIABILIDAD_EN_CURSO = ViabilidadContexto.SOLICITUD_VIABILIDAD_EN_CURSO;
    public static final String SOLICITUD_VIABILIDAD_NO_VIGENTE = ViabilidadContexto.SOLICITUD_VIABILIDAD_NO_VIGENTE;
    public static final String FICHA_VIABILIDAD_DESHABILITADA = ViabilidadContexto.FICHA_VIABILIDAD_DESHABILITADA;
    public static final String DOCUMENTO_PREINVERSION_REQUERIDO = ViabilidadSolicitud.DOCUMENTO_PREINVERSION_REQUERIDO;
    public static final String COMENTARIOS_OPINION_TECNICA_SIN_RESPONDER =
            ViabilidadSolicitud.COMENTARIOS_OPINION_TECNICA_SIN_RESPONDER;
    public static final String JUSTIFICACION_VIABILIDAD_REQUERIDA = ViabilidadCierre.JUSTIFICACION_VIABILIDAD_REQUERIDA;

    /** Mensaje literal de RN11. */
    public static final String MENSAJE_COMENTARIOS_OT_SIN_RESPONDER =
            ViabilidadSolicitud.MENSAJE_COMENTARIOS_OT_SIN_RESPONDER;

    /** Longitud máxima de cada comentario y de las observaciones generales (columnas de 2000). */
    public static final int LONGITUD_MAXIMA_TEXTO = ViabilidadComentarios.LONGITUD_MAXIMA_TEXTO;

    private final ViabilidadAcceso acceso;
    private final DocumentosViabilidad documentos;
    private final ViabilidadSolicitud solicitud;
    private final ViabilidadComentarios comentarios;
    private final ViabilidadCierre cierre;
    private final FichaViabilidadEnsamblador ensamblador;

    public ViabilidadServiceImpl(ViabilidadAcceso acceso,
            DocumentosViabilidad documentos,
            ViabilidadSolicitud solicitud,
            ViabilidadComentarios comentarios,
            ViabilidadCierre cierre,
            FichaViabilidadEnsamblador ensamblador) {
        this.acceso = acceso;
        this.documentos = documentos;
        this.solicitud = solicitud;
        this.comentarios = comentarios;
        this.cierre = cierre;
        this.ensamblador = ensamblador;
    }

    // ---------------------------------------------------------------------------------------------
    // Consulta

    @Override
    @Transactional(readOnly = true)
    public FichaViabilidadResponseDto consultarFicha(Long idProyecto) {
        ViabilidadContexto contexto = acceso.paraConsulta(idProyecto);
        FichaViabilidadResponseDto ficha = ViabilidadRespuestas.ficha(contexto, documentos.listar(idProyecto));
        ensamblador.completarCamposDeConsulta(ficha, idProyecto);
        return ficha;
    }

    // ---------------------------------------------------------------------------------------------
    // Técnico URP

    @Override
    public CargarDocumentoViabilidadResponseDto cargarDocumento(Long idProyecto, TipoDocumentoViabilidad tipoDocumento,
            MultipartFile archivo) {
        ViabilidadContexto contexto = acceso.paraTecnicoUrp(idProyecto);
        // Mientras el Viabilizador revisa (o tras la emisión) los documentos no cambian (RN04).
        contexto.exigirHabilitadaParaSolicitud();

        return new CargarDocumentoViabilidadResponseDto(
                ViabilidadRespuestas.documento(
                        documentos.cargar(contexto.proyecto(), tipoDocumento, archivo, contexto.actor())),
                acceso.actualizar(contexto).acciones());
    }

    @Override
    public void solicitarViabilidad(Long idProyecto) {
        solicitud.solicitar(acceso.paraTecnicoUrp(idProyecto));
    }

    // ---------------------------------------------------------------------------------------------
    // Viabilizador

    @Override
    public GuardarComentariosViabilidadResponseDto guardarComentarios(Long idProyecto,
            GuardarComentariosViabilidadRequestDto request) {
        return comentarios.guardar(acceso.paraViabilizador(idProyecto), request);
    }

    @Override
    public EnviarComentariosViabilidadResponseDto enviarComentarios(Long idProyecto) {
        ViabilidadContexto contexto = acceso.paraViabilizador(idProyecto);
        cierre.devolver(contexto);
        return new EnviarComentariosViabilidadResponseDto(contexto.proyecto().getId(),
                contexto.proyecto().getEstado().getEtiquetaUi());
    }

    @Override
    public EmitirViabilidadResponseDto emitirViabilidad(Long idProyecto) {
        ViabilidadContexto contexto = acceso.paraViabilizador(idProyecto);
        boolean primeraVez = cierre.emitir(contexto);
        return new EmitirViabilidadResponseDto(contexto.proyecto().getId(),
                contexto.proyecto().getEstado().getEtiquetaUi(), primeraVez);
    }
}
