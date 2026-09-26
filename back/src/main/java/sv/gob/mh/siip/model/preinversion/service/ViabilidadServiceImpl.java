package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ReglaNegocioException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.ComentarioCampoViabilidad;
import sv.gob.mh.siip.model.preinversion.domain.DocumentoViabilidad;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.RevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.domain.Viabilidad;
import sv.gob.mh.siip.model.preinversion.dto.AccionesDisponiblesViabilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.CampoFichaViabilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.CargarDocumentoViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.ComentarioCampoViabilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.DocumentoViabilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.EmitirViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.EnviarComentariosViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarComentariosViabilidadRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarComentariosViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoDocumentoViabilidadDto;
import sv.gob.mh.siip.model.preinversion.enums.CampoFichaViabilidad;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoRevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoViabilidad;
import sv.gob.mh.siip.model.preinversion.enums.TipoDocumentoViabilidad;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.RevisionViabilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.ViabilidadRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * Implementación de CU-PRE-24 "Viabilidad".
 *
 * <p>El estado de la gestión se deriva de la última {@link RevisionViabilidad} del proyecto:
 * <ul>
 * <li>sin revisiones, o con la última devuelta: el Técnico URP puede solicitar Viabilidad;</li>
 * <li>con una revisión en curso: el Viabilizador puede guardar comentarios, devolver o emitir, y la
 * formulación queda bloqueada porque el proyecto pasa a "En viabilidad" (RN04);</li>
 * <li>con la última emitida: la ficha queda deshabilitada (FA02 paso 2.5), salvo que después la OT
 * haya devuelto el proyecto para ajustes, caso en que se puede volver a solicitar (RN03, RN11).</li>
 * </ul>
 * Cada cierre de revisión queda además registrado como un {@link Viabilidad} (resultado de la
 * evaluación).
 */
@Service
@Transactional
public class ViabilidadServiceImpl implements ViabilidadService {

    static final String PROYECTO_NO_ENCONTRADO = "PROYECTO_NO_ENCONTRADO";
    static final String SOLICITUD_VIABILIDAD_EN_CURSO = "SOLICITUD_VIABILIDAD_EN_CURSO";
    static final String SOLICITUD_VIABILIDAD_NO_VIGENTE = "SOLICITUD_VIABILIDAD_NO_VIGENTE";
    static final String FICHA_VIABILIDAD_DESHABILITADA = "FICHA_VIABILIDAD_DESHABILITADA";
    static final String DOCUMENTO_PREINVERSION_REQUERIDO = "DOCUMENTO_PREINVERSION_REQUERIDO";
    static final String COMENTARIOS_OPINION_TECNICA_SIN_RESPONDER = "COMENTARIOS_OPINION_TECNICA_SIN_RESPONDER";
    static final String JUSTIFICACION_VIABILIDAD_REQUERIDA = "JUSTIFICACION_VIABILIDAD_REQUERIDA";

    /** Mensaje literal de RN11. */
    static final String MENSAJE_COMENTARIOS_OT_SIN_RESPONDER =
            "Es necesario responder los comentarios de la Opinión Técnica previo a la solicitud de viabilidad";

    /** Longitud máxima de cada comentario y de las observaciones generales (columnas de 2000). */
    static final int LONGITUD_MAXIMA_TEXTO = 2000;

    private static final String CAMPO_COMENTARIOS = "comentariosViabilizador";
    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private final ProyectoRepository proyectoRepository;
    private final RevisionViabilidadRepository revisionRepository;
    private final ViabilidadRepository viabilidadRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;
    private final DocumentosViabilidad documentos;
    private final FiltrosPosterioresViabilidad filtros;
    private final FichaViabilidadEnsamblador ensamblador;
    private final ActorContexto actorContexto;

    public ViabilidadServiceImpl(ProyectoRepository proyectoRepository,
            RevisionViabilidadRepository revisionRepository,
            ViabilidadRepository viabilidadRepository,
            UsuarioRepository usuarioRepository,
            NotificacionService notificacionService,
            DocumentosViabilidad documentos,
            FiltrosPosterioresViabilidad filtros,
            FichaViabilidadEnsamblador ensamblador,
            ActorContexto actorContexto) {
        this.proyectoRepository = proyectoRepository;
        this.revisionRepository = revisionRepository;
        this.viabilidadRepository = viabilidadRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificacionService = notificacionService;
        this.documentos = documentos;
        this.filtros = filtros;
        this.ensamblador = ensamblador;
        this.actorContexto = actorContexto;
    }

    // ---------------------------------------------------------------------------------------------
    // Consulta

    @Override
    @Transactional(readOnly = true)
    public FichaViabilidadResponseDto consultarFicha(Long idProyecto) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.VIABILIZADOR);
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);

        Gestion gestion = gestion(proyecto);
        RevisionViabilidad revision = gestion.ultima();
        List<DocumentoViabilidadDto> documentosDto = documentos.listar(idProyecto).stream()
                .map(ViabilidadServiceImpl::documentoDto)
                .toList();
        FichaViabilidadResponseDto ficha = new FichaViabilidadResponseDto(proyecto.getId(), proyecto.getCup(),
                proyecto.getNombre(), proyecto.getEstado().getEtiquetaUi(), documentosDto,
                comentarios(revision), acciones(actor, gestion));
        ficha.setObservacionesGeneralesJustificacion(revision == null ? null : revision.getObservacionesGenerales());
        ensamblador.completarCamposDeConsulta(ficha, idProyecto);
        return ficha;
    }

    // ---------------------------------------------------------------------------------------------
    // Técnico URP

    @Override
    public CargarDocumentoViabilidadResponseDto cargarDocumento(Long idProyecto, TipoDocumentoViabilidad tipoDocumento,
            MultipartFile archivo) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);
        // Mientras el Viabilizador revisa (o tras la emisión) los documentos no cambian (RN04).
        exigirHabilitadaParaSolicitud(gestion(proyecto));

        DocumentoViabilidad documento = documentos.cargar(proyecto, tipoDocumento, archivo, actor);
        return new CargarDocumentoViabilidadResponseDto(documentoDto(documento), acciones(actor, gestion(proyecto)));
    }

    @Override
    public void solicitarViabilidad(Long idProyecto) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);
        Gestion gestion = gestion(proyecto);
        exigirHabilitadaParaSolicitud(gestion);
        if (!gestion.documentoPreinversionCargado()) {
            throw new ReglaNegocioException(DOCUMENTO_PREINVERSION_REQUERIDO,
                    "Debe cargar el Documento de Preinversión antes de solicitar Viabilidad.");
        }
        if (filtros.tieneComentariosOtSinResponder(idProyecto)) {
            throw new ReglaNegocioException(COMENTARIOS_OPINION_TECNICA_SIN_RESPONDER,
                    MENSAJE_COMENTARIOS_OT_SIN_RESPONDER);
        }

        RevisionViabilidad ultima = gestion.ultima();
        revisionRepository.save(RevisionViabilidad.builder()
                .proyecto(proyecto)
                .numero(ultima == null ? 1 : ultima.getNumero() + 1)
                .estado(EstadoRevisionViabilidad.EN_CURSO)
                .solicitante(actor)
                .fechaSolicitud(ahora())
                .build());
        // RN04: "En viabilidad" bloquea la formulación (EstadoProyecto#bloqueaFormulacion).
        proyecto.setEstado(EstadoProyecto.EN_VIABILIDAD);
        proyectoRepository.save(proyecto);

        notificacionService.notificarSolicitudViabilidad(proyecto,
                usuarioRepository.findByRolAndActivoTrue(RolUsuario.VIABILIZADOR));
    }

    // ---------------------------------------------------------------------------------------------
    // Viabilizador

    @Override
    public GuardarComentariosViabilidadResponseDto guardarComentarios(Long idProyecto,
            GuardarComentariosViabilidadRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.VIABILIZADOR);
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);
        Gestion gestion = gestion(proyecto);
        RevisionViabilidad revision = exigirRevisionEnCurso(gestion);

        List<ComentarioCampoViabilidad> comentarios = validarComentarios(request.getComentariosViabilizador());
        String observaciones = validarObservaciones(request.getObservacionesGeneralesJustificacion());
        revision.getComentarios().clear();
        revision.getComentarios().addAll(comentarios);
        revision.setObservacionesGenerales(observaciones);
        revisionRepository.save(revision);

        GuardarComentariosViabilidadResponseDto respuesta = new GuardarComentariosViabilidadResponseDto(
                comentarios(revision), acciones(actor, gestion));
        respuesta.setObservacionesGeneralesJustificacion(revision.getObservacionesGenerales());
        return respuesta;
    }

    @Override
    public EnviarComentariosViabilidadResponseDto enviarComentarios(Long idProyecto) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.VIABILIZADOR);
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);
        RevisionViabilidad revision = exigirRevisionEnCurso(gestion(proyecto));

        // RN10: la revisión devuelta conserva sus comentarios; la próxima solicitud abre otra.
        cerrarRevision(revision, EstadoRevisionViabilidad.DEVUELTA, actor);
        registrarResultado(proyecto, ResultadoViabilidad.OBSERVADO, revision, actor);
        // RN05: "Observado" vuelve a habilitar la formulación y "Solicitar Viabilidad".
        proyecto.setEstado(EstadoProyecto.OBSERVADO);
        proyectoRepository.save(proyecto);

        notificacionService.notificarComentariosViabilidad(proyecto, revision.getSolicitante());
        return new EnviarComentariosViabilidadResponseDto(proyecto.getId(), proyecto.getEstado().getEtiquetaUi());
    }

    @Override
    public EmitirViabilidadResponseDto emitirViabilidad(Long idProyecto) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.VIABILIZADOR);
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);
        RevisionViabilidad revision = exigirRevisionEnCurso(gestion(proyecto));
        if (esVacio(revision.getObservacionesGenerales())) {
            throw new ReglaNegocioException(JUSTIFICACION_VIABILIDAD_REQUERIDA,
                    "Debe registrar y guardar las Observaciones Generales/Justificación de la Viabilidad antes de emitirla.");
        }

        // RN03: Elegibilidad solo se gestiona la primera vez; después se salta a la OT.
        boolean primeraVez = !filtros.yaPasoPorElegibilidad(idProyecto);
        revision.setHabilitaElegibilidad(primeraVez);
        cerrarRevision(revision, EstadoRevisionViabilidad.EMITIDA, actor);
        registrarResultado(proyecto, ResultadoViabilidad.VIABLE, revision, actor);
        proyecto.setEstado(EstadoProyecto.VIABLE);
        proyectoRepository.save(proyecto);

        notificacionService.notificarEmisionViabilidad(proyecto, revision.getSolicitante());
        return new EmitirViabilidadResponseDto(proyecto.getId(), proyecto.getEstado().getEtiquetaUi(), primeraVez);
    }

    // ---------------------------------------------------------------------------------------------
    // Estado de la gestión

    /**
     * Foto del estado de la gestión de Viabilidad del proyecto.
     *
     * @param ultima última revisión del proyecto, o {@code null} si nunca solicitó Viabilidad
     * @param deshabilitada si la ficha está deshabilitada por una emisión vigente (FA02 paso 2.5)
     * @param documentoPreinversionCargado si ya se cargó el Documento de Preinversión (RN02)
     */
    private record Gestion(RevisionViabilidad ultima, boolean deshabilitada, boolean documentoPreinversionCargado) {

        boolean enCurso() {
            return ultima != null && ultima.getEstado() == EstadoRevisionViabilidad.EN_CURSO;
        }

        String observacionesGenerales() {
            return ultima == null ? null : ultima.getObservacionesGenerales();
        }

        boolean habilitoElegibilidad() {
            return ultima != null && Boolean.TRUE.equals(ultima.getHabilitaElegibilidad());
        }
    }

    private Gestion gestion(Proyecto proyecto) {
        RevisionViabilidad ultima = revisionRepository.findFirstByProyectoIdOrderByNumeroDesc(proyecto.getId())
                .orElse(null);
        boolean deshabilitada = ultima != null && ultima.getEstado() == EstadoRevisionViabilidad.EMITIDA
                && !filtros.otDevolvioDespuesDe(proyecto.getId(), ultima.getFechaCierre());
        return new Gestion(ultima, deshabilitada, documentos.tieneDocumentoPreinversion(proyecto.getId()));
    }

    /**
     * Acciones del Anexo A.1 habilitadas para el actor (RN02, RN04, RN06, RN07; Anexo B.1). El
     * backend vuelve a validar cada una al ejecutarla.
     */
    private static AccionesDisponiblesViabilidadDto acciones(Usuario actor, Gestion gestion) {
        boolean esTecnicoUrp = actor.getRol() == RolUsuario.TECNICO_URP;
        boolean esViabilizador = actor.getRol() == RolUsuario.VIABILIZADOR;
        boolean revisa = esViabilizador && gestion.enCurso();
        boolean solicita = esTecnicoUrp && gestion.documentoPreinversionCargado() && !gestion.enCurso()
                && !gestion.deshabilitada();
        return new AccionesDisponiblesViabilidadDto(
                solicita,
                revisa,
                revisa,
                revisa && !esVacio(gestion.observacionesGenerales()),
                esViabilizador && gestion.deshabilitada() && gestion.habilitoElegibilidad());
    }

    private static void exigirHabilitadaParaSolicitud(Gestion gestion) {
        if (gestion.deshabilitada()) {
            throw fichaDeshabilitada();
        }
        if (gestion.enCurso()) {
            throw new ConflictoEstadoException(SOLICITUD_VIABILIDAD_EN_CURSO,
                    "El proyecto ya cuenta con una solicitud de Viabilidad en curso.");
        }
    }

    private static RevisionViabilidad exigirRevisionEnCurso(Gestion gestion) {
        if (gestion.deshabilitada()) {
            throw fichaDeshabilitada();
        }
        if (!gestion.enCurso()) {
            throw new ConflictoEstadoException(SOLICITUD_VIABILIDAD_NO_VIGENTE,
                    "El proyecto no cuenta con una solicitud de Viabilidad vigente.");
        }
        return gestion.ultima();
    }

    private static ConflictoEstadoException fichaDeshabilitada() {
        return new ConflictoEstadoException(FICHA_VIABILIDAD_DESHABILITADA,
                "La Viabilidad del proyecto ya fue emitida; la ficha no admite cambios.");
    }

    private void cerrarRevision(RevisionViabilidad revision, EstadoRevisionViabilidad estado, Usuario viabilizador) {
        revision.setEstado(estado);
        revision.setViabilizador(viabilizador);
        revision.setFechaCierre(ahora());
        revisionRepository.save(revision);
    }

    private void registrarResultado(Proyecto proyecto, ResultadoViabilidad resultado, RevisionViabilidad revision,
            Usuario viabilizador) {
        viabilidadRepository.save(Viabilidad.builder()
                .proyecto(proyecto)
                .resultado(resultado)
                .fechaEvaluacion(revision.getFechaCierre())
                .observaciones(revision.getObservacionesGenerales())
                .evaluador(viabilizador)
                .build());
    }

    // ---------------------------------------------------------------------------------------------
    // Validaciones de entrada

    /**
     * Convierte los comentarios recibidos: descarta las celdas vacías y rechaza campos repetidos o
     * textos demasiado largos.
     */
    private static List<ComentarioCampoViabilidad> validarComentarios(List<ComentarioCampoViabilidadDto> recibidos) {
        List<ComentarioCampoViabilidad> resultado = new ArrayList<>();
        if (recibidos == null) {
            return resultado;
        }
        Set<CampoFichaViabilidad> vistos = EnumSet.noneOf(CampoFichaViabilidad.class);
        for (ComentarioCampoViabilidadDto recibido : recibidos) {
            CampoFichaViabilidad campo = campoDe(recibido);
            if (!vistos.add(campo)) {
                throw invalido(CAMPO_COMENTARIOS, "El campo " + campo + " tiene más de un comentario.");
            }
            String texto = recibido.getComentario() == null ? "" : recibido.getComentario().trim();
            if (texto.length() > LONGITUD_MAXIMA_TEXTO) {
                throw invalido(CAMPO_COMENTARIOS,
                        "El comentario del campo " + campo + " supera los " + LONGITUD_MAXIMA_TEXTO + " caracteres.");
            }
            if (!texto.isEmpty()) {
                resultado.add(new ComentarioCampoViabilidad(campo, texto));
            }
        }
        return resultado;
    }

    private static CampoFichaViabilidad campoDe(ComentarioCampoViabilidadDto recibido) {
        if (recibido == null || recibido.getCampo() == null) {
            throw invalido(CAMPO_COMENTARIOS, "Cada comentario debe indicar el campo de la ficha.");
        }
        return CampoFichaViabilidad.valueOf(recibido.getCampo().name());
    }

    private static String validarObservaciones(String observaciones) {
        if (esVacio(observaciones)) {
            return null;
        }
        String texto = observaciones.trim();
        if (texto.length() > LONGITUD_MAXIMA_TEXTO) {
            throw invalido("observacionesGeneralesJustificacion",
                    "Las observaciones generales superan los " + LONGITUD_MAXIMA_TEXTO + " caracteres.");
        }
        return texto;
    }

    private static ValidacionNegocioException invalido(String campo, String mensaje) {
        return new ValidacionNegocioException(DocumentosViabilidad.SOLICITUD_INVALIDA, mensaje,
                List.of(new ErrorDetalleDto().campo(campo).mensaje(mensaje)));
    }

    // ---------------------------------------------------------------------------------------------
    // Auxiliares

    private Proyecto buscarProyecto(Long idProyecto) {
        return proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException(PROYECTO_NO_ENCONTRADO,
                        "El proyecto " + idProyecto + " no existe."));
    }

    /**
     * El actor solo accede a proyectos de su Unidad Ejecutora cuando tiene una asignada (RN01, igual
     * que el resto de CU de Preinversión).
     */
    private static void exigirAlcanceUnidadEjecutora(Usuario actor, Proyecto proyecto) {
        if (actor.getUnidadEjecutora() != null && (proyecto.getUnidadEjecutora() == null
                || !actor.getUnidadEjecutora().getId().equals(proyecto.getUnidadEjecutora().getId()))) {
            throw new AccesoDenegadoException(
                    "El proyecto no pertenece a una Unidad Ejecutora dentro de las credenciales del actor.");
        }
    }

    private static DocumentoViabilidadDto documentoDto(DocumentoViabilidad documento) {
        return new DocumentoViabilidadDto(documento.getId(),
                TipoDocumentoViabilidadDto.valueOf(documento.getTipoDocumento().name()),
                documento.getNombreArchivo(),
                documento.getFechaCarga().atZone(ZONA_EL_SALVADOR).toOffsetDateTime());
    }

    private static List<ComentarioCampoViabilidadDto> comentarios(RevisionViabilidad revision) {
        if (revision == null) {
            return new ArrayList<>();
        }
        return revision.getComentarios().stream()
                .map(c -> new ComentarioCampoViabilidadDto(CampoFichaViabilidadDto.valueOf(c.getCampo().name()),
                        c.getComentario()))
                .toList();
    }

    private static boolean esVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    private static LocalDateTime ahora() {
        return LocalDateTime.now(ZONA_EL_SALVADOR);
    }
}
