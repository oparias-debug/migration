package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.bdd.support.SufijosPrueba;
import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.ReglaNegocioException;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.CriterioElegibilidad;
import sv.gob.mh.siip.model.preinversion.domain.Elegibilidad;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.RevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Viabilidad;
import sv.gob.mh.siip.model.preinversion.dto.ActorRetornoDto;
import sv.gob.mh.siip.model.preinversion.dto.ActualizacionOpinionTecnicaResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.ApartadoOpinionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.AsignarOpinionTecnicaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ComentarioApartadoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ComentariosDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ConclusionesOpinionTecnicaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EmisionOpinionTecnicaFavorableResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.EmitirViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.EnvioComentariosDgicpResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoAccionDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoGestionOpinionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.FiltroAprobacionDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarCalificacionElegibilidadRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarComentariosViabilidadRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.JustificacionApartadoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.JustificacionesInstitucionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.OpinionTecnicaResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectosCapturaResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.RespuestaCriterioElegibilidadRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.SolicitudActivaItemDto;
import sv.gob.mh.siip.model.preinversion.dto.SolicitudOpinionTecnicaResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoDocumentoAnexoDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoFormularioOpinionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoSolicitudDisponibleDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoSolicitudDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoSolicitudOpinionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.TiposSolicitudOpinionTecnicaResponseDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoRevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.enums.EstadoSolicitud;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoElegibilidad;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoViabilidad;
import sv.gob.mh.siip.model.preinversion.enums.TipoDocumentoViabilidad;
import sv.gob.mh.siip.model.preinversion.enums.TipoEspecificar;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.CalificacionCriterioElegibilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.ComentarioOpinionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.CriterioElegibilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.ElegibilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.OpinionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.RevisionViabilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.SolicitudPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ViabilidadRepository;
import sv.gob.mh.siip.model.preinversion.service.BandejaOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.service.BandejaPreinversionService;
import sv.gob.mh.siip.model.preinversion.service.ComentariosDgicpOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.service.CriteriosFichaElegibilidad;
import sv.gob.mh.siip.model.preinversion.service.DestinatariosOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.service.DocumentosOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.service.DocumentosViabilidad;
import sv.gob.mh.siip.model.preinversion.service.ElegibilidadAcceso;
import sv.gob.mh.siip.model.preinversion.service.ElegibilidadCalificacion;
import sv.gob.mh.siip.model.preinversion.service.ElegibilidadEmision;
import sv.gob.mh.siip.model.preinversion.service.ElegibilidadService;
import sv.gob.mh.siip.model.preinversion.service.ElegibilidadServiceImpl;
import sv.gob.mh.siip.model.preinversion.service.EtapasOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.service.FichaViabilidadEnsamblador;
import sv.gob.mh.siip.model.preinversion.service.FichaViabilidadPresupuesto;
import sv.gob.mh.siip.model.preinversion.service.FiltrosPosterioresViabilidad;
import sv.gob.mh.siip.model.preinversion.service.InformeOpinionTecnicaEnsamblador;
import sv.gob.mh.siip.model.preinversion.service.NotaEmisionOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.service.NotificacionService;
import sv.gob.mh.siip.model.preinversion.service.OpinionTecnicaAcceso;
import sv.gob.mh.siip.model.preinversion.service.OpinionTecnicaAjustes;
import sv.gob.mh.siip.model.preinversion.service.OpinionTecnicaContexto;
import sv.gob.mh.siip.model.preinversion.service.OpinionTecnicaEmision;
import sv.gob.mh.siip.model.preinversion.service.OpinionTecnicaEnsamblador;
import sv.gob.mh.siip.model.preinversion.service.OpinionTecnicaRevision;
import sv.gob.mh.siip.model.preinversion.service.OpinionTecnicaService;
import sv.gob.mh.siip.model.preinversion.service.OpinionTecnicaServiceImpl;
import sv.gob.mh.siip.model.preinversion.service.OpinionTecnicaSolicitud;
import sv.gob.mh.siip.model.preinversion.service.PlazoObservacionesOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.service.SolicitudOpinionTecnicaService;
import sv.gob.mh.siip.model.preinversion.service.SolicitudOpinionTecnicaServiceImpl;
import sv.gob.mh.siip.model.preinversion.service.ProyectoCapturaFiltro;
import sv.gob.mh.siip.model.preinversion.service.ProyectoCapturaService;
import sv.gob.mh.siip.model.preinversion.service.ViabilidadAcceso;
import sv.gob.mh.siip.model.preinversion.service.ViabilidadCierre;
import sv.gob.mh.siip.model.preinversion.service.ViabilidadComentarios;
import sv.gob.mh.siip.model.preinversion.service.ViabilidadService;
import sv.gob.mh.siip.model.preinversion.service.ViabilidadServiceImpl;
import sv.gob.mh.siip.model.preinversion.service.ViabilidadSolicitud;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * Steps BDD de CU-PRE-26 "Opinión Técnica" (HU-PRE-26-01 a HU-PRE-26-10).
 *
 * <p>Se ejercita {@link OpinionTecnicaServiceImpl} con los repositorios y colaboradores reales del
 * contexto Spring de pruebas; solo la notificación se sustituye por un mock para poder verificar a
 * quién se envía cada correo del Anexo A2. Los flujos que atraviesan CU-PRE-24 "Viabilidad" y CU-PRE-25
 * "Elegibilidad" usan sus servicios reales con el mismo mock. Los pasos que describen presentación
 * (mensajes emergentes, links, navegación) verifican el dato del backend que la pantalla necesita.
 *
 * <p>Tres textos de estos escenarios coinciden con pasos de CU-PRE-24 y Cucumber admite una sola
 * definición por texto: los define {@link Pre24Viabilidad}, que delega aquí cuando {@link #activo()}.
 *
 * <p>Precondición común (FB paso 1): el proyecto ya tiene la Viabilidad y la Elegibilidad emitidas y
 * una ruta PERFIL → PREFACTIBILIDAD.
 */
public class Pre26OpinionTecnica {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final ZoneId ZONA = ZoneId.of("America/El_Salvador");

    private static final String OPINION_TECNICA = "Opinión Técnica";
    private static final String CAPTURA = "Captura de proyectos";
    private static final String VIABILIDAD = "Viabilidad";
    private static final String NOTA_SOLICITUD = "Nota de solicitud de OT";
    private static final String COMENTARIOS_DGICP = "Comentarios DGICP";
    private static final String COMENTARIOS_ELEGIBILIDAD = "Comentarios DGICP a Elegibilidad";
    private static final String COMENTARIOS_DOCUMENTOS = "Comentarios DGICP a documentos anexos";
    private static final String CONCLUSIONES = "Conclusiones";
    private static final String JUSTIFICACION = "Justificación Institución";

    private static final String BOTON_GUARDAR = "Guardar";
    private static final String BOTON_ENVIAR_COMENTARIOS = "Enviar comentarios";
    private static final String BOTON_ENVIAR_AJUSTES = "Enviar ajustes";
    private static final String BOTON_SOLICITAR_OT = "Solicitar OT";
    private static final String BOTON_OT_FAVORABLE = "OT favorable";
    private static final String BOTON_VISTO_BUENO = "Visto bueno OT";
    private static final String BOTON_SOLICITAR_VIABILIDAD = "Solicitar Viabilidad";
    private static final String BOTON_SI = "SÍ";

    /** Textos que el cliente muestra según la respuesta del backend (Anexos A.3, A.4 y A.5). */
    private static final String MENSAJE_A3 = "¿Está seguro de gestionar una actualización de Opinión Técnica?";
    private static final String MENSAJE_A4 =
            "Se han habilitado los campos correspondientes de F&E, para el ingreso de información actualizada.";
    private static final String TITULO_A5 = "¡Opinión Técnica emitida!";
    private static final String BOTON_A5 = "IR A PRIORIZACIÓN";

    /** Apartado "1.2 Problema Central" (Anexo A.1), usado para los comentarios a los campos del proyecto. */
    private static final String APARTADO = "1.2";

    private static final String JUSTIFICACION_ELEGIBILIDAD = "Se precisó el Plan Regional en la calificación.";
    private static final String NUMERO_NOTA_OT = "MH.DGICP.DGI/001.070/2026";

    private final InstitucionRepository instituciones;
    private final UnidadEjecutoraRepository unidades;
    private final UsuarioRepository usuarios;
    private final ProyectoRepository proyectos;
    private final MacroSectorRepository macrosectores;
    private final SectorActividadRepository sectores;
    private final EjeTematicoRepository ejes;
    private final EtapaPreinversionRepository etapas;
    private final RevisionViabilidadRepository revisiones;
    private final ViabilidadRepository viabilidades;
    private final ElegibilidadRepository elegibilidades;
    private final CriterioElegibilidadRepository criterios;
    private final CalificacionCriterioElegibilidadRepository calificaciones;
    private final OpinionTecnicaRepository opinionesTecnicas;
    private final ComentarioOpinionTecnicaRepository comentariosOt;
    private final SolicitudPreinversionRepository solicitudes;
    private final DocumentosViabilidad documentosViabilidad;
    private final DocumentosOpinionTecnica documentosOt;
    private final FiltrosPosterioresViabilidad filtros;
    private final FichaViabilidadEnsamblador fichaViabilidad;
    private final CriteriosFichaElegibilidad criteriosFicha;
    private final OpinionTecnicaAcceso acceso;
    private final EtapasOpinionTecnica etapasOt;
    private final DestinatariosOpinionTecnica destinatarios;
    private final OpinionTecnicaEnsamblador ensamblador;
    private final PlatformTransactionManager transactionManager;
    @Autowired
    private FichaViabilidadPresupuesto presupuestoFicha;
    @Autowired
    private InformeOpinionTecnicaEnsamblador informe;
    private final ProyectoCapturaService captura;
    private final BandejaPreinversionService bandeja;
    private final ActorContexto actorContexto;
    private final TransactionTemplate transacciones;

    private boolean activo;
    /** Pasos compartidos con CU-PRE-26.5: delegan cuando {@link Pre265Priorizacion#activo()}. */
    @Autowired
    private Pre265Priorizacion priorizacion;
    private NotificacionService notificaciones;
    private OpinionTecnicaService service;
    private SolicitudOpinionTecnicaService solicitudService;
    private ViabilidadService viabilidad;
    private ElegibilidadService elegibilidad;
    private PlazoObservacionesOpinionTecnica plazo;

    private Proyecto proyecto;
    private Usuario tecnicoUrp;
    private Usuario viabilizador;
    private Usuario tecnicoPre;
    private Usuario coordinadorPre;
    private CriterioElegibilidad criterio;

    private Usuario actor;
    private Long idGestion;
    private String pantalla;
    private OpinionTecnicaResponseDto vista;
    private ProyectosCapturaResponseDto listado;
    private TiposSolicitudOpinionTecnicaResponseDto tipos;
    private MultipartFile notaSolicitud;
    private MultipartFile notaOt;
    private final Map<String, String> borradorComentarios = new LinkedHashMap<>();
    private String borradorElegibilidad;
    private String borradorConclusiones;
    private String borradorJustificacion;
    private String campoRegistrado;
    private boolean conComentariosElegibilidad;
    private SolicitudOpinionTecnicaResponseDto solicitud;
    private ActualizacionOpinionTecnicaResponseDto actualizacion;
    private EnvioComentariosDgicpResponseDto envio;
    private EmisionOpinionTecnicaFavorableResponseDto emision;
    private EmitirViabilidadResponseDto emisionViabilidad;
    private LocalDate hoy;
    private String botonPulsado;
    private RuntimeException error;

    public Pre26OpinionTecnica(InstitucionRepository instituciones, UnidadEjecutoraRepository unidades,
            UsuarioRepository usuarios, ProyectoRepository proyectos, MacroSectorRepository macrosectores,
            SectorActividadRepository sectores, EjeTematicoRepository ejes, EtapaPreinversionRepository etapas,
            RevisionViabilidadRepository revisiones, ViabilidadRepository viabilidades,
            ElegibilidadRepository elegibilidades, CriterioElegibilidadRepository criterios,
            CalificacionCriterioElegibilidadRepository calificaciones, OpinionTecnicaRepository opinionesTecnicas,
            ComentarioOpinionTecnicaRepository comentariosOt, SolicitudPreinversionRepository solicitudes,
            DocumentosViabilidad documentosViabilidad, DocumentosOpinionTecnica documentosOt,
            FiltrosPosterioresViabilidad filtros, FichaViabilidadEnsamblador fichaViabilidad,
            CriteriosFichaElegibilidad criteriosFicha, OpinionTecnicaAcceso acceso, EtapasOpinionTecnica etapasOt,
            DestinatariosOpinionTecnica destinatarios, OpinionTecnicaEnsamblador ensamblador,
            ProyectoCapturaService captura, BandejaPreinversionService bandeja, ActorContexto actorContexto,
            PlatformTransactionManager transactionManager) {
        this.instituciones = instituciones;
        this.unidades = unidades;
        this.usuarios = usuarios;
        this.proyectos = proyectos;
        this.macrosectores = macrosectores;
        this.sectores = sectores;
        this.ejes = ejes;
        this.etapas = etapas;
        this.revisiones = revisiones;
        this.viabilidades = viabilidades;
        this.elegibilidades = elegibilidades;
        this.criterios = criterios;
        this.calificaciones = calificaciones;
        this.opinionesTecnicas = opinionesTecnicas;
        this.comentariosOt = comentariosOt;
        this.solicitudes = solicitudes;
        this.documentosViabilidad = documentosViabilidad;
        this.documentosOt = documentosOt;
        this.filtros = filtros;
        this.fichaViabilidad = fichaViabilidad;
        this.criteriosFicha = criteriosFicha;
        this.acceso = acceso;
        this.etapasOt = etapasOt;
        this.destinatarios = destinatarios;
        this.ensamblador = ensamblador;
        this.captura = captura;
        this.bandeja = bandeja;
        this.actorContexto = actorContexto;
        this.transactionManager = transactionManager;
        this.transacciones = new TransactionTemplate(transactionManager);
    }

    @Before("@CU-PRE-26")
    public void prepararEscenario() {
        activo = true;
        String sufijo = SufijosPrueba.nuevo(8);
        Institucion institucion = instituciones.save(ProyectoFixtures.nuevaInstitucion("MH-CU26-" + sufijo,
                "Ministerio de Hacienda CU26"));
        UnidadEjecutora unidad = unidades.save(ProyectoFixtures.nuevaUnidadEjecutora("UE26-" + sufijo,
                "UE CU26", institucion));
        tecnicoUrp = usuarios.save(usuario("tecnico.urp.pre26." + sufijo, RolUsuario.TECNICO_URP, unidad, institucion));
        viabilizador = usuarios.save(usuario("viabilizador.pre26." + sufijo, RolUsuario.VIABILIZADOR, null, null));
        tecnicoPre = usuarios.save(usuario("tecnico.pre.pre26." + sufijo, RolUsuario.TECNICO_PRE, null, null));
        coordinadorPre = usuarios.save(usuario("coordinador.pre.pre26." + sufijo, RolUsuario.COORDINADOR_PRE, null,
                null));
        MacroSector macrosector = macrosectores.save(ProyectoFixtures.nuevoMacrosector("M26" + sufijo,
                "Macrosector CU26"));
        SectorActividad sector = sectores.save(ProyectoFixtures.nuevoSector("S26" + sufijo, "Sector CU26",
                macrosector));
        EjeTematico eje = ejes.save(ProyectoFixtures.nuevoEjeTematico("E26" + sufijo, "Eje CU26"));
        Proyecto nuevo = ProyectoFixtures.nuevoProyecto("Proyecto CU26 " + sufijo, EstadoProyecto.ELEGIBLE, unidad,
                institucion, sector, eje);
        nuevo.setCup(ProyectoFixtures.nuevoCup(proyectos));
        proyecto = proyectos.save(nuevo);
        sembrarPrecondiciones();
        criterio = criterios.findByCodigo("BDD26-ELEG-01").orElseGet(() -> criterios.save(CriterioElegibilidad
                .builder().codigo("BDD26-ELEG-01").numeroDimension(1).dimension("Alineación estratégica").orden(99)
                .criterio("Contribución a Planes Regionales (CU26)").pregunta("¿A cuál Plan Regional contribuye?")
                .tipoEspecificar(TipoEspecificar.TEXTO_LIBRE).permiteSeleccionMultiple(false).build()));

        notificaciones = mock(NotificacionService.class);
        BandejaOpinionTecnica bandejaOt = new BandejaOpinionTecnica(solicitudes);
        ComentariosDgicpOpinionTecnica comentariosDgicp = new ComentariosDgicpOpinionTecnica(comentariosOt);
        OpinionTecnicaAjustes ajustes = new OpinionTecnicaAjustes(opinionesTecnicas, comentariosDgicp, destinatarios,
                notificaciones);
        solicitudService = new SolicitudOpinionTecnicaServiceImpl(acceso, opinionesTecnicas,
                new OpinionTecnicaSolicitud(opinionesTecnicas, bandejaOt, acceso, etapasOt, documentosOt,
                        destinatarios, notificaciones),
                ensamblador);
        service = new OpinionTecnicaServiceImpl(acceso,
                new OpinionTecnicaRevision(opinionesTecnicas, proyectos, comentariosDgicp, bandejaOt, destinatarios,
                        notificaciones),
                ajustes,
                new OpinionTecnicaEmision(opinionesTecnicas, proyectos, bandejaOt, etapasOt,
                        new NotaEmisionOpinionTecnica(documentosOt, presupuestoFicha), destinatarios, notificaciones),
                ensamblador, informe);
        viabilidad = new ViabilidadServiceImpl(
                new ViabilidadAcceso(actorContexto, proyectos, revisiones, documentosViabilidad, filtros),
                documentosViabilidad,
                new ViabilidadSolicitud(proyectos, revisiones, usuarios, notificaciones, filtros, ajustes),
                new ViabilidadComentarios(revisiones),
                new ViabilidadCierre(proyectos, revisiones, viabilidades, notificaciones, filtros),
                fichaViabilidad);
        elegibilidad = new ElegibilidadServiceImpl(
                new ElegibilidadAcceso(actorContexto, proyectos, elegibilidades, filtros),
                criteriosFicha,
                calificaciones,
                new ElegibilidadCalificacion(calificaciones, criteriosFicha),
                new ElegibilidadEmision(proyectos, elegibilidades, calificaciones, usuarios, notificaciones, filtros));
        plazo = new PlazoObservacionesOpinionTecnica(opinionesTecnicas, proyectos, bandejaOt, destinatarios,
                notificaciones, transactionManager);
        actor = tecnicoPre;
    }

    @After("@CU-PRE-26")
    public void limpiarContexto() {
        RequestContextHolder.resetRequestAttributes();
    }

    /** @return si el escenario en curso es de CU-PRE-26 (ver pasos compartidos en {@link Pre24Viabilidad}) */
    boolean activo() {
        return activo;
    }

    // =============================================================================================
    // Actor y pantallas

    @Dado("que el usuario autenticado tiene el rol {string}")
    public void usuarioAutenticadoConRol(String rol) {
        if (priorizacion.activo()) {
            priorizacion.usuarioAutenticadoConRol(rol);
            return;
        }
        actor = usuarioDelRol(rol);
    }

    @Dado("el usuario autenticado tiene el rol {string}")
    public void usuarioAutenticadoTieneRol(String rol) {
        usuarioAutenticadoConRol(rol);
    }

    @Dado("está en la pantalla {string} de un proyecto")
    public void estaEnPantallaDeUnProyecto(String nombre) {
        assertThat(nombre).isEqualTo(OPINION_TECNICA);
        pantalla = OPINION_TECNICA;
        if (esSolicitante(actor) && idGestion == null) {
            // Antes de "Solicitar OT" la pantalla aún no tiene gestión: el menú ofrece "Opinión Técnica".
            tipos = tiposComo(actor);
            assertThat(opcion(TipoSolicitudOpinionTecnicaDto.OPINION_TECNICA).getHabilitado()).isTrue();
            return;
        }
        asegurarGestion();
        vista = pantallaComo(actor);
    }

    @Cuando("ingresa a la pantalla {string} de un proyecto")
    public void ingresaAPantallaDeUnProyecto(String nombre) {
        ingresaAPantallaDelProyecto(nombre);
    }

    @Cuando("ingresa a la pantalla {string} del proyecto")
    public void ingresaAPantallaDelProyecto(String nombre) {
        assertThat(nombre).isEqualTo(OPINION_TECNICA);
        pantalla = OPINION_TECNICA;
        asegurarGestion();
        vista = pantallaComo(actor);
    }

    @Cuando("el Técnico PRE ingresa a la pantalla {string} del proyecto")
    public void tecnicoPreIngresaAPantalla(String nombre) {
        actor = tecnicoPre;
        ingresaAPantallaDelProyecto(nombre);
    }

    @Cuando("se muestra la pantalla {string} del proyecto")
    public void seMuestraLaPantalla(String nombre) {
        ingresaAPantallaDelProyecto(nombre);
    }

    @Cuando("ingresa a la pantalla CU-PRE-{int} {string} de un proyecto en estado {string}")
    public void ingresaAPantallaViabilidad(int cu, String nombre, String estado) {
        assertThat(cu).isEqualTo(24);
        assertThat(nombre).isEqualTo(VIABILIDAD);
        proyectoEnEstado(estado);
        pantalla = VIABILIDAD;
        capturar(() -> {
            autenticar(actor);
            enTransaccion(() -> viabilidad.consultarFicha(proyecto.getId()));
        });
    }

    @Dado("está en la pantalla {string} \\(CU-PRE-{int})")
    public void estaEnCaptura(String nombre, int cu) {
        asegurarGestion();
        ingresaACaptura(nombre, cu);
    }

    @Cuando("ingresa a la pantalla {string} \\(CU-PRE-{int})")
    public void ingresaACaptura(String nombre, int cu) {
        assertThat(nombre).isEqualTo(CAPTURA);
        assertThat(cu).isEqualTo(3);
        pantalla = CAPTURA;
        EstadoProyectoDto estado = EstadoProyectoDto.valueOf(proyectoActual().getEstado().name());
        autenticar(actor);
        listado = enTransaccion(() -> captura.listarProyectosCaptura(
                new ProyectoCapturaFiltro(null, proyecto.getCup(), null, null, estado, null), 0, 50));
    }

    @Cuando("un usuario ingresa a la pantalla {string} \\(CU-PRE-{int})")
    public void usuarioIngresaACaptura(String nombre, int cu) {
        actor = tecnicoUrp;
        ingresaACaptura(nombre, cu);
    }

    @Entonces("el proyecto aparece en la lista")
    public void proyectoApareceEnLaLista() {
        assertThat(listado.getContenido()).anyMatch(p -> p.getIdProyecto().equals(proyecto.getId()));
    }

    @Cuando("da clic en un proyecto")
    public void daClicEnUnProyecto() {
        proyectoApareceEnLaLista();
    }

    @Cuando("ingresa a la pestaña {string} en la sección {string}")
    public void ingresaAPestana(String pestana, String seccion) {
        assertThat(pestana).isEqualTo("Gestión de Proyectos");
        assertThat(seccion).isEqualTo(OPINION_TECNICA);
        // El cliente abre la gestión vigente: la primera del listado del proyecto.
        autenticar(actor);
        Long vigente = enTransaccion(() -> solicitudService.listar(proyecto.getId())).getOpinionesTecnicas().get(0)
                .getOpinionTecnicaId();
        assertThat(vigente).isEqualTo(idGestion);
        pantalla = OPINION_TECNICA;
        vista = pantallaComo(actor);
    }

    @Entonces("se muestra la pantalla del Anexo A.{int}")
    public void seMuestraAnexo(int anexo) {
        assertThat(anexo).isEqualTo(1);
        assertThat(vista.getOpinionTecnicaId()).isEqualTo(idGestion);
        assertThat(vista.getProyectoId()).isEqualTo(proyecto.getId());
        assertThat(vista.getApartados()).isNotEmpty();
    }

    @Entonces("los campos {string}, {string}, {string}, {string} y {string} no son editables")
    public void camposNoEditables(String cup, String nombre, String unidad, String etapaActual, String etapaFutura) {
        assertThat(List.of(cup, nombre, unidad, etapaActual, etapaFutura))
                .containsExactly("CUP", "Nombre del proyecto", "Unidad Ejecutora", "Etapa actual", "Etapa futura");
        assertThat(vista.getEncabezado().getCup()).isEqualTo(proyecto.getCup());
        assertThat(vista.getEncabezado().getNombreProyecto()).isEqualTo(proyecto.getNombre());
        assertThat(vista.getEncabezado().getUnidadEjecutora()).isEqualTo("UE CU26");
        assertThat(vista.getEncabezado().getEtapaActual()).isEqualTo("Perfil");
        assertThat(vista.getEncabezado().getEtapaFutura()).isEqualTo("Prefactibilidad");
        assertThat(camposDeEscritura()).doesNotContain("cup", "nombreProyecto", "unidadEjecutora", "etapaActual",
                "etapaFutura");
    }

    @Entonces("el campo {string} está habilitado")
    public void campoHabilitado(String campo) {
        campoHabilitadoParaRegistro(campo);
    }

    @Entonces("la columna {string} no permite editar ningún campo")
    public void columnaNoEditable(String columna) {
        assertThat(columna).isEqualTo("Apartados");
        // Visor (Anexo B.1): ningún request de la pantalla lleva el contenido de un apartado.
        assertThat(camposDeEscritura()).doesNotContain("contenido", "apartados");
        assertThat(vista.getApartados()).extracting(ApartadoOpinionTecnicaDto::getApartadoCodigo).contains(APARTADO);
    }

    @Cuando("da clic en el ícono de lápiz de un apartado")
    public void daClicEnLapiz() {
        botonPulsado = "lápiz";
    }

    @Entonces("el Sistema lo direcciona a la pantalla donde se registró la información de ese apartado")
    public void sistemaDireccionaAPantalla() {
        ApartadoOpinionTecnicaDto antecedentes = apartado("1.1");
        assertThat(antecedentes.getPantallaOrigen().getCasoUso()).isEqualTo("CU-PRE-04");
        assertThat(antecedentes.getPantallaOrigen().getRuta())
                .isEqualTo("/preinversion/proyectos/" + proyecto.getId() + "/identificacion");
    }

    // =============================================================================================
    // HU-PRE-26-01: Solicitud de OT

    @Dado("no se ha anexado ningún archivo en el campo {string}")
    public void noSeHaAnexadoNota(String campo) {
        assertThat(campo).isEqualTo(NOTA_SOLICITUD);
        notaSolicitud = null;
    }

    @Entonces("el botón {string} se muestra inactivo")
    public void botonInactivo(String boton) {
        assertThat(boton).isEqualTo(BOTON_SOLICITAR_OT);
        capturar(this::solicitar);
        assertThat(error).isInstanceOf(ReglaNegocioException.class);
        assertThat(((ReglaNegocioException) error).getCodigo())
                .isEqualTo(OpinionTecnicaSolicitud.NOTA_SOLICITUD_OT_REQUERIDA);
        assertThat(opinionesTecnicas.existsByProyectoId(proyecto.getId())).isFalse();
    }

    @Cuando("anexa la Nota de Solicitud OT en el campo {string}")
    public void anexaNotaSolicitud(String campo) {
        assertThat(campo).isEqualTo(NOTA_SOLICITUD);
        notaSolicitud = archivo("notaSolicitudOt", "nota-solicitud-ot.pdf");
    }

    @Dado("en el proyecto se anexó la Nota de Solicitud OT en el campo {string}")
    public void seAnexoNotaSolicitud(String campo) {
        anexaNotaSolicitud(campo);
    }

    @Entonces("el botón {string} se activa")
    public void botonSeActiva(String boton) {
        assertThat(boton).isEqualTo(BOTON_SOLICITAR_OT);
        capturar(this::solicitar);
        assertThat(error).isNull();
        assertThat(solicitud.getOpinionTecnicaId()).isNotNull();
    }

    @Entonces("el campo {string} muestra la fecha del clic en formato DD\\/MM\\/AAAA")
    public void campoMuestraFecha(String campo) {
        assertThat(campo).isEqualTo("Fecha de solicitud");
        assertThat(error).isNull();
        LocalDate fecha = solicitud.getFechaSolicitud();
        assertThat(fecha).isEqualTo(LocalDate.now(ZONA));
        // El backend envía la fecha ISO; el formato DD/MM/AAAA es de presentación (Anexo B.1).
        assertThat(fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).matches("\\d{2}/\\d{2}/\\d{4}");
    }

    @Entonces("el Sistema envía al Coordinador PRE el correo electrónico del Anexo A2 a")
    public void correoA2a() {
        verify(notificaciones).notificarSolicitudOpinionTecnica(esProyecto(), contiene(coordinadorPre));
    }

    @Dado("que la Opinión Técnica del proyecto se está solicitando por primera vez")
    public void otPorPrimeraVez() {
        assertThat(opinionesTecnicas.existsByProyectoId(proyecto.getId())).isFalse();
        actor = tecnicoUrp;
    }

    @Cuando("se despliega el {string} del menú {string} en {string}")
    public void seDespliegaMenu(String lista, String menu, String grupo) {
        assertThat(List.of(lista, menu, grupo)).containsExactly("Tipo de solicitud", "OT", "GESTIÓN");
        tipos = tiposComo(esSolicitante(actor) ? actor : tecnicoUrp);
    }

    @Entonces("la opción {string} está desactivada")
    public void opcionDesactivada(String nombre) {
        TipoSolicitudOpinionTecnicaDto tipo = switch (nombre) {
            case "1. Opinión Técnica" -> TipoSolicitudOpinionTecnicaDto.OPINION_TECNICA;
            case "2. Actualización de OT" -> TipoSolicitudOpinionTecnicaDto.ACTUALIZACION_OT;
            default -> throw new IllegalArgumentException("Opción no reconocida: " + nombre);
        };
        assertThat(opcion(tipo).getHabilitado()).isFalse();
    }

    // =============================================================================================
    // HU-PRE-26-02: Asignación del caso

    @Dado("que existe una solicitud de Opinión Técnica para un proyecto")
    public void existeSolicitudOt() {
        asegurarGestion();
    }

    @Cuando("asigna el caso a un Técnico PRE")
    public void asignaCaso() {
        capturar(() -> {
            autenticar(actor);
            enTransaccion(() -> solicitudService.asignar(proyecto.getId(), idGestion,
                    new AsignarOpinionTecnicaRequestDto(tecnicoPre.getId())));
        });
        assertThat(error).isNull();
    }

    @Entonces("el Sistema envía al Técnico PRE asignado el correo electrónico del Anexo A2 b")
    public void correoA2b() {
        verify(notificaciones).notificarAsignacionOpinionTecnica(esProyecto(),
                argThat(u -> u.getId().equals(tecnicoPre.getId())));
        assertThat(gestionActual().getTecnicoResponsable().getId()).isEqualTo(tecnicoPre.getId());
        // La solicitud de la Bandeja de Preinversión (CU-PRE-02) queda asignada al mismo técnico.
        SolicitudPreinversion enBandeja = solicitudDeLaGestion();
        assertThat(enBandeja.getTecnicoAsignado().getId()).isEqualTo(tecnicoPre.getId());
        assertThat(enBandeja.getEstado()).isEqualTo(EstadoSolicitud.ASIGNADA);
    }

    // =============================================================================================
    // HU-PRE-26-03: Revisión y comentarios DGICP

    @Dado("existe un proyecto en el estado {string} enviado a Opinión Técnica")
    public void existeProyectoEnviadoAOt(String estado) {
        proyectoEnEstado(estado);
    }

    // Literales y no {string}: "registra información en el campo \"Respuesta Institución\"" es de CU-PRE-31.
    @Cuando("registra información en el campo \"Comentarios DGICP\"")
    public void registraComentariosDgicp() {
        registraInformacionEnCampo(COMENTARIOS_DGICP);
    }

    @Cuando("registra información en el campo \"Conclusiones\"")
    public void registraConclusiones() {
        registraInformacionEnCampo(CONCLUSIONES);
    }

    private void registraInformacionEnCampo(String campo) {
        campoRegistrado = campo;
        switch (campo) {
            case COMENTARIOS_DGICP -> borradorComentarios.put(APARTADO, "Precisar la magnitud del problema central");
            case CONCLUSIONES -> borradorConclusiones = "El proyecto cumple los requisitos de la etapa de Perfil.";
            default -> throw new IllegalArgumentException("Campo no reconocido: " + campo);
        }
    }

    @Entonces("el Sistema guarda la información registrada en {string}")
    public void sistemaGuardaEn(String campo) {
        assertThat(error).isNull();
        OpinionTecnicaResponseDto guardada = pantallaComo(actor);
        switch (campo) {
            case COMENTARIOS_DGICP -> assertThat(apartado(guardada, APARTADO).getComentarioDgicp())
                    .isEqualTo(borradorComentarios.get(APARTADO));
            case CONCLUSIONES -> assertThat(guardada.getConclusiones()).isEqualTo(borradorConclusiones);
            case JUSTIFICACION -> assertThat(apartado(guardada, APARTADO).getJustificacionInstitucion())
                    .isEqualTo(borradorJustificacion);
            default -> throw new IllegalArgumentException("Campo no reconocido: " + campo);
        }
    }

    @Entonces("el campo {string} está habilitado para registro")
    public void campoHabilitadoParaRegistro(String campo) {
        assertThat(campoEditable(campo)).as(campo).isTrue();
    }

    @Entonces("el campo {string} no está habilitado para registro")
    public void campoNoHabilitadoParaRegistro(String campo) {
        assertThat(campoEditable(campo)).as(campo).isFalse();
        // El backend también rechaza la escritura (RN02, RN03).
        capturar(() -> escribirComo(actor, campo));
        assertThat(error).as(campo).isInstanceOf(AccesoDenegadoException.class);
    }

    @Dado("que la gestión de OT del proyecto es {string}")
    public void gestionDeOtEs(String gestion) {
        asegurarGestion();
        if ("posterior a la primera".equals(gestion)) {
            completarRondaYSolicitarDeNuevo();
        } else {
            assertThat(gestion).isEqualTo("la primera");
        }
    }

    @Entonces("la sección {string} se muestra")
    public void seccionSeMuestra(String seccion) {
        assertThat(seccion).isEqualTo("Comentarios Elegibilidad");
        assertThat(vista.getEsPrimeraGestion()).isTrue();
        assertThat(vista.getComentariosElegibilidad()).isNotNull();
    }

    @Entonces("la sección {string} no se muestra")
    public void seccionNoSeMuestra(String seccion) {
        assertThat(seccion).isEqualTo("Comentarios Elegibilidad");
        assertThat(vista.getEsPrimeraGestion()).isFalse();
        assertThat(vista.getComentariosElegibilidad()).isNull();
        assertThat(vista.getCamposEditables().getComentarioDgicpElegibilidad()).isFalse();
    }

    @Dado("un proyecto categorizado como {string} en el CU-PRE-{int}")
    public void proyectoCategorizado(String categoria, int cu) {
        assertThat(categoria).isEqualTo("Proyecto de emergencia");
        assertThat(cu).isEqualTo(1);
        proyecto.setEsProyectoEmergencia(true);
        proyectos.save(proyecto);
    }

    @Entonces("el formulario muestra los campos del Anexo A.{int} del CU-PRE-{int}.{int} en lugar de los del Anexo A.{int}")
    public void formularioDeEmergencia(int anexoEmergencia, int cu, int subCu, int anexoEstandar) {
        assertThat(List.of(anexoEmergencia, cu, subCu, anexoEstandar)).containsExactly(4, 3, 5, 1);
        assertThat(vista.getTipoFormulario()).isEqualTo(TipoFormularioOpinionTecnicaDto.EMERGENCIA);
        assertThat(vista.getApartados()).isNotEmpty()
                .allSatisfy(a -> assertThat(a.getPantallaOrigen().getCasoUso()).isEqualTo("CU-PRE-03.5"));
    }

    // =============================================================================================
    // HU-PRE-26-04: Envío de comentarios

    @Dado("el proyecto está en el estado {string}")
    public void proyectoEnEstado(String estado) {
        asegurarGestion();
        EstadoProyecto destino = estado(estado);
        if (destino == EstadoProyecto.OBSERVADO) {
            if (!gestionActual().estaObservada()) {
                enviarComentariosAlProyecto();
            }
        } else {
            Proyecto actual = proyectoActual();
            actual.setEstado(destino);
            proyectos.save(actual);
        }
        assertThat(proyectoActual().getEstado()).isEqualTo(destino);
    }

    @Entonces("el botón {string} está activo")
    public void botonActivo(String boton) {
        assertThat(accion(boton, vista).getHabilitada()).as(boton).isTrue();
    }

    @Dado("registró comentarios en {string} a los campos del proyecto")
    public void registroComentariosEn(String campo) {
        asegurarGestion();
        registraInformacionEnCampo(campo);
    }

    @Dado("registró comentarios a los campos del proyecto sin criterios de elegibilidad")
    public void registroComentariosSoloProyecto() {
        asegurarGestion();
        borradorComentarios.put(APARTADO, "Precisar la magnitud del problema central");
    }

    @Dado("registró comentarios a los campos del proyecto y a los criterios de elegibilidad")
    public void registroComentariosProyectoYElegibilidad() {
        registroComentariosSoloProyecto();
        borradorElegibilidad = "Justificar la contribución a los Planes Regionales";
    }

    @Dado("registró comentarios a solamente los criterios de elegibilidad")
    public void registroComentariosSoloElegibilidad() {
        asegurarGestion();
        borradorElegibilidad = "Justificar la contribución a los Planes Regionales";
    }

    @Entonces("habilita al Técnico URP la edición de todos los campos del CU-PRE-{int} {string} al CU-PRE-{int} {string}")
    public void habilitaFormulacion(int desde, String inicio, int hasta, String fin) {
        assertThat(List.of(desde, hasta)).containsExactly(4, 23);
        assertThat(List.of(inicio, fin)).containsExactly("Identificación", "Indicadores del Proyecto");
        assertThat(proyectoActual().getEstado().bloqueaFormulacion()).isFalse();
    }

    @Entonces("habilita al Técnico URP el campo {string} de la pantalla del Anexo A.{int}")
    public void habilitaJustificacion(String campo, int anexo) {
        assertThat(campo).isEqualTo(JUSTIFICACION);
        assertThat(anexo).isEqualTo(1);
        assertThat(pantallaComo(tecnicoUrp).getCamposEditables().getJustificacionInstitucion()).isTrue();
    }

    @Entonces("habilita al Viabilizador las pantallas CU-PRE-{int} {string} y CU-PRE-{int} {string}")
    public void habilitaViabilidadYElegibilidad(int cuViabilidad, String viabilidadNombre, int cuElegibilidad,
            String elegibilidadNombre) {
        assertThat(List.of(cuViabilidad, cuElegibilidad)).containsExactly(24, 25);
        assertThat(List.of(viabilidadNombre, elegibilidadNombre)).containsExactly(VIABILIDAD, "Elegibilidad");
        // Con comentarios al proyecto la Viabilidad se reabre para una nueva solicitud del Técnico URP.
        assertThat(fichaViabilidadComo(tecnicoUrp).getAccionesDisponibles().getSolicitarViabilidad()).isTrue();
        // La ficha de Elegibilidad queda a la vista del Viabilizador; solo admite cambios si también hubo
        // comentarios a los criterios de elegibilidad (RN14, ver el esquema de ruta de retorno).
        autenticar(viabilizador);
        boolean elegibilidadEditable = enTransaccion(() -> elegibilidad.consultarFicha(proyecto.getId()))
                .getAccionesDisponibles().getGuardarCalificacion();
        assertThat(elegibilidadEditable).isEqualTo(borradorElegibilidad != null);
    }

    @Entonces("envía al Técnico URP y al Viabilizador el correo electrónico del Anexo A2 c")
    public void correoA2c() {
        verify(notificaciones).notificarComentariosOpinionTecnica(esProyecto(),
                argThat(d -> tiene(d, tecnicoUrp) && tiene(d, viabilizador)), any());
    }

    @Entonces("cambia el estado del proyecto a {string}")
    public void cambiaEstado(String estado) {
        elEstadoCambiaA(estado);
    }

    @Entonces("se activa el botón {string} para el Técnico URP")
    public void seActivaParaUrp(String boton) {
        assertThat(boton).isEqualTo(BOTON_ENVIAR_AJUSTES);
        assertThat(pantallaComo(tecnicoUrp).getAccionesDisponibles().getEnviarAjustes().getHabilitada()).isTrue();
    }

    @Entonces("el proyecto vuelve al {string}")
    public void proyectoVuelveAl(String actorDestino) {
        assertThat(error).isNull();
        ActorRetornoDto esperado = "Técnico URP".equals(actorDestino) ? ActorRetornoDto.TECNICO_URP
                : ActorRetornoDto.VIABILIZADOR;
        assertThat(envio.getRutaRetorno().getActorDestino()).isEqualTo(esperado);
    }

    @Entonces("el proceso de aprobación involucra los filtros {string}")
    public void procesoInvolucraFiltros(String lista) {
        List<FiltroAprobacionDto> esperados = Arrays.stream(lista.split(","))
                .map(String::strip)
                .map(f -> switch (f) {
                    case VIABILIDAD -> FiltroAprobacionDto.VIABILIDAD;
                    case "Elegibilidad" -> FiltroAprobacionDto.ELEGIBILIDAD;
                    case "OT" -> FiltroAprobacionDto.OPINION_TECNICA;
                    default -> throw new IllegalArgumentException("Filtro no reconocido: " + f);
                })
                .toList();
        assertThat(envio.getRutaRetorno().getFiltrosAprobacion()).containsExactlyElementsOf(esperados);
        // La ruta se refleja en qué filtros quedan habilitados.
        LocalDateTime cierreViabilidad = revisiones.findFirstByProyectoIdOrderByNumeroDesc(proyecto.getId())
                .orElseThrow().getFechaCierre();
        LocalDateTime emisionElegibilidad = elegibilidades
                .findFirstByProyectoIdOrderByFechaEvaluacionDescIdDesc(proyecto.getId()).orElseThrow()
                .getFechaEvaluacion();
        assertThat(filtros.otReabrioViabilidadDespuesDe(proyecto.getId(), cierreViabilidad))
                .isEqualTo(esperados.contains(FiltroAprobacionDto.VIABILIDAD));
        assertThat(filtros.otReabrioElegibilidadDespuesDe(proyecto.getId(), emisionElegibilidad))
                .isEqualTo(esperados.contains(FiltroAprobacionDto.ELEGIBILIDAD));
    }

    // =============================================================================================
    // HU-PRE-26-05 y 06: Ajustes de la institución y del Viabilizador

    @Cuando("registra información en el campo {string} de un apartado comentado")
    public void registraJustificacion(String campo) {
        assertThat(campo).isEqualTo(JUSTIFICACION);
        campoRegistrado = campo;
        borradorJustificacion = "Se amplió el análisis del problema central con datos de 2025.";
    }

    @Cuando("ajustó la información de las pantallas del CU-PRE-{int} {string} al CU-PRE-{int} {string}")
    public void ajustoFormulacion(int desde, String inicio, int hasta, String fin) {
        habilitaFormulacion(desde, inicio, hasta, fin);
    }

    @Dado("respondió los comentarios en la columna {string} de la pantalla del Anexo A.{int}")
    public void respondioComentarios(String columna, int anexo) {
        assertThat(columna).isEqualTo(JUSTIFICACION);
        assertThat(anexo).isEqualTo(1);
        responderComentarios();
    }

    @Dado("subió la documentación ajustada en el CU-PRE-{int} {string}")
    public void subioDocumentacion(int cu, String nombre) {
        assertThat(cu).isEqualTo(24);
        assertThat(nombre).isEqualTo(VIABILIDAD);
        autenticar(tecnicoUrp);
        enTransaccion(() -> viabilidad.cargarDocumento(proyecto.getId(), TipoDocumentoViabilidad.OTRO_DOCUMENTO,
                archivo("archivo", "documentacion-ajustada.pdf")));
    }

    @Cuando("da clic en el botón {string} del CU-PRE-{int} {string}")
    public void daClicEnBotonDeViabilidad(String boton, int cu, String nombre) {
        assertThat(boton).isEqualTo(BOTON_SOLICITAR_VIABILIDAD);
        assertThat(cu).isEqualTo(24);
        assertThat(nombre).isEqualTo(VIABILIDAD);
        capturar(this::solicitarViabilidad);
    }

    @Entonces("el Sistema guarda la información de todos los campos ajustados y la nueva documentación")
    public void sistemaGuardaAjustes() {
        assertThat(error).isNull();
        OpinionTecnicaResponseDto guardada = pantallaComo(tecnicoPre);
        assertThat(apartado(guardada, APARTADO).getJustificacionInstitucion()).isNotBlank();
        assertThat(guardada.getDocumentosAnexos().getDocumentos())
                .anyMatch(d -> d.getTipoDocumento() == TipoDocumentoAnexoDto.OTROS_DOCUMENTOS_ANEXOS
                        && d.getNombreArchivo().equals("documentacion-ajustada.pdf"));
        assertThat(guardada.getFechaAjustes()).isEqualTo(LocalDate.now(ZONA));
    }

    @Entonces("envía al Técnico PRE y al Coordinador PRE el correo electrónico del Anexo A2 d")
    public void correoA2d() {
        verify(notificaciones).notificarAjustesOpinionTecnica(esProyecto(),
                argThat(d -> tiene(d, tecnicoPre) && tiene(d, coordinadorPre)));
    }

    @Entonces("el proyecto vuelve al proceso de aprobación")
    public void vuelveAlProcesoDeAprobacion() {
        assertThat(proyectoActual().getEstado()).isEqualTo(EstadoProyecto.EN_VIABILIDAD);
    }

    @Dado("el Técnico URP registró información en {string}")
    public void urpRegistroJustificacion(String campo) {
        assertThat(campo).isEqualTo(JUSTIFICACION);
        proyectoEnEstado("Observado");
        responderComentarios();
    }

    @Entonces("puede ver la información registrada por el Técnico URP")
    public void puedeVerLaJustificacion() {
        assertThat(apartado(vista, APARTADO).getJustificacionInstitucion()).isEqualTo(borradorJustificacion);
    }

    @Entonces("no puede editarla")
    public void noPuedeEditarla() {
        campoNoHabilitadoParaRegistro(JUSTIFICACION);
    }

    @Dado("el Técnico PRE no emitió comentarios a los criterios de elegibilidad")
    public void preNoComentoElegibilidad() {
        conComentariosElegibilidad = false;
    }

    @Dado("el Técnico PRE emitió comentarios a los criterios de elegibilidad")
    public void preComentoElegibilidad() {
        conComentariosElegibilidad = true;
        asegurarGestion();
        borradorElegibilidad = "Justificar la contribución a los Planes Regionales";
        enviar(tecnicoPre);
        assertThat(error).isNull();
        // RN15 de CU-PRE-25: la institución responde el comentario antes de reemitir la Elegibilidad.
        responderComentarios();
    }

    @Dado("el Técnico URP envió los ajustes del proyecto")
    public void urpEnvioAjustes() {
        assertThat(conComentariosElegibilidad).isFalse();
        asegurarGestion();
        enviarComentariosAlProyecto();
        responderComentarios();
        solicitarViabilidad();
        assertThat(error).isNull();
    }

    @Cuando("emite Viabilidad en el CU-PRE-{int} {string} sobre la información ajustada")
    public void emiteViabilidad(int cu, String nombre) {
        assertThat(cu).isEqualTo(24);
        assertThat(nombre).isEqualTo(VIABILIDAD);
        capturar(this::emitirViabilidad);
    }

    @Entonces("el estado del proyecto cambia a {string}")
    public void elEstadoCambiaA(String estado) {
        assertThat(error).isNull();
        assertThat(proyectoActual().getEstado()).isEqualTo(estado(estado));
    }

    @Entonces("el proyecto vuelve a Opinión Técnica sin pasar por Elegibilidad")
    public void vuelveAOtSinElegibilidad() {
        assertThat(emisionViabilidad.getElegibilidadHabilitada()).isFalse();
        autenticar(viabilizador);
        assertThat(enTransaccion(() -> elegibilidad.consultarFicha(proyecto.getId())).getAccionesDisponibles()
                .getGuardarCalificacion()).isFalse();
        vuelveAOt();
    }

    @Cuando("ajusta la selección de criterios en el CU-PRE-{int} {string}")
    public void ajustaCriterios(int cu, String nombre) {
        assertThat(cu).isEqualTo(25);
        assertThat(nombre).isEqualTo("Elegibilidad");
        RespuestaCriterioElegibilidadRequestDto respuesta = new RespuestaCriterioElegibilidadRequestDto(
                criterio.getId(), true);
        respuesta.setEspecificarTexto("Plan de desarrollo de la región oriental");
        capturar(() -> {
            autenticar(viabilizador);
            enTransaccion(() -> elegibilidad.guardarCalificacion(proyecto.getId(),
                    new GuardarCalificacionElegibilidadRequestDto(new ArrayList<>(List.of(respuesta)))));
        });
        assertThat(error).isNull();
    }

    @Cuando("emite nuevamente Elegibilidad")
    public void emiteNuevamenteElegibilidad() {
        capturar(() -> {
            autenticar(viabilizador);
            enTransaccion(() -> elegibilidad.emitirElegibilidad(proyecto.getId()));
        });
    }

    @Entonces("el proyecto vuelve a Opinión Técnica")
    public void vuelveAOt() {
        // La institución puede volver a solicitar la OT: la nueva gestión ya no es la primera (RN 12).
        anexaNotaSolicitud(NOTA_SOLICITUD);
        Long anterior = idGestion;
        capturar(() -> solicitarComo(tecnicoUrp));
        assertThat(error).isNull();
        assertThat(idGestion).isNotEqualTo(anterior);
        assertThat(gestionActual().getPrimeraGestion()).isFalse();
    }

    // =============================================================================================
    // HU-PRE-26-07 y 08: Visto bueno y emisión de la OT favorable

    @Dado("revisó la información registrada y los documentos anexos del proyecto")
    public void revisoInformacion() {
        asegurarGestion();
        vista = pantallaComo(actor);
        assertThat(vista.getDocumentosAnexos().getDocumentos())
                .extracting(d -> d.getTipoDocumento())
                .contains(TipoDocumentoAnexoDto.NOTA_SOLICITUD_OT, TipoDocumentoAnexoDto.DOCUMENTO_PREINVERSION);
    }

    @Entonces("habilita el {string}")
    public void habilitaElVistoBueno(String accion) {
        assertThat(accion).isEqualTo(BOTON_VISTO_BUENO);
        assertThat(pantallaComo(coordinadorPre).getAccionesDisponibles().getVistoBuenoOt().getHabilitada()).isTrue();
    }

    @Dado("el campo {string} no ha sido registrado y guardado")
    public void campoNoRegistrado(String campo) {
        assertThat(campo).isEqualTo(CONCLUSIONES);
        asegurarGestion();
        assertThat(gestionActual().getRevisionConclusiones().getTexto()).isNull();
    }

    @Entonces("el {string} no está habilitado")
    public void vistoBuenoNoHabilitado(String accion) {
        assertThat(accion).isEqualTo(BOTON_VISTO_BUENO);
        assertThat(pantallaComo(coordinadorPre).getAccionesDisponibles().getVistoBuenoOt().getHabilitada()).isFalse();
        capturar(() -> vistoBuenoComo(coordinadorPre));
        assertThat(error).isInstanceOf(ConflictoEstadoException.class);
        assertThat(((ConflictoEstadoException) error).getCodigo())
                .isEqualTo(OpinionTecnicaEmision.CONCLUSIONES_NO_REGISTRADAS);
    }

    @Dado("el Técnico PRE registró y guardó las {string} del proyecto")
    public void preRegistroConclusiones(String campo) {
        assertThat(campo).isEqualTo(CONCLUSIONES);
        asegurarGestion();
        registraInformacionEnCampo(CONCLUSIONES);
        guardarComo(tecnicoPre);
        assertThat(error).isNull();
    }

    @Cuando("revisa la información y da el {string}")
    public void daElVistoBueno(String accion) {
        assertThat(accion).isEqualTo(BOTON_VISTO_BUENO);
        capturar(() -> vistoBuenoComo(actor));
    }

    @Entonces("el Sistema notifica al Técnico PRE el visto bueno de la OT")
    public void notificaVistoBueno() {
        assertThat(error).isNull();
        verify(notificaciones).notificarVistoBuenoOpinionTecnica(esProyecto(),
                argThat(u -> u.getId().equals(tecnicoPre.getId())));
    }

    @Entonces("habilita el botón {string}")
    public void habilitaElBoton(String boton) {
        assertThat(boton).isEqualTo(BOTON_OT_FAVORABLE);
        assertThat(pantallaComo(tecnicoPre).getAccionesDisponibles().getOtFavorable().getHabilitada()).isTrue();
    }

    @Dado("que el Coordinador PRE no ha dado el {string} del proyecto")
    public void coordinadorNoDioVistoBueno(String accion) {
        assertThat(accion).isEqualTo(BOTON_VISTO_BUENO);
        asegurarGestion();
        assertThat(gestionActual().getRevisionConclusiones().tieneVistoBueno()).isFalse();
    }

    @Dado("el Coordinador PRE dio el visto bueno de la OT")
    public void coordinadorDioVistoBueno() {
        preRegistroConclusiones(CONCLUSIONES);
        vistoBuenoComo(coordinadorPre);
    }

    @Dado("cargó el archivo {string} firmado por el Director DGICP")
    public void cargoNotaOt(String nombre) {
        assertThat(nombre).isEqualTo("Nota de OT");
        notaOt = archivo("notaOt", "nota-ot-firmada.pdf");
    }

    @Entonces("el Sistema envía a todos los actores el correo electrónico del Anexo A2 e")
    public void correoA2e() {
        verify(notificaciones).notificarEmisionOpinionTecnica(esProyecto(), argThat(d -> tiene(d, tecnicoUrp)
                && tiene(d, viabilizador) && tiene(d, tecnicoPre) && tiene(d, coordinadorPre)));
    }

    @Entonces("muestra al Técnico PRE el aviso del Anexo A.{int} con el título {string} y el botón {string}")
    public void muestraAvisoA5(int anexo, String titulo, String boton) {
        assertThat(anexo).isEqualTo(5);
        assertThat(titulo).isEqualTo(TITULO_A5);
        assertThat(boton).isEqualTo(BOTON_A5);
        assertThat(emision.getEstadoProyecto()).isEqualTo(EstadoProyectoDto.PROYECTO_CON_OT);
        assertThat(pantallaComo(tecnicoPre).getNotaOt().getNombreArchivo()).isEqualTo("nota-ot-firmada.pdf");
    }

    @Dado("que se muestra el aviso del Anexo A.{int}")
    public void seMuestraAvisoA5(int anexo) {
        assertThat(anexo).isEqualTo(5);
        emitirOtFavorable();
    }

    @Cuando("el Técnico PRE da clic en el botón {string}")
    public void preDaClicEnAviso(String boton) {
        assertThat(boton).isEqualTo(BOTON_A5);
        botonPulsado = boton;
    }

    @Entonces("el Sistema le permite continuar con la Priorización \\(CU-PRE-{int}.{int})")
    public void continuaConPriorizacion(int cu, int subCu) {
        assertThat(List.of(cu, subCu)).containsExactly(26, 5);
        assertThat(botonPulsado).isEqualTo(BOTON_A5);
        // RN17: el proceso de preinversión culmina con la OT; la Priorización parte de "Proyecto con OT".
        assertThat(proyectoActual().getEstado()).isEqualTo(EstadoProyecto.PROYECTO_CON_OT);
    }

    @Dado("que el Técnico PRE dio clic en el botón {string} del proyecto")
    public void preDioClicEnOtFavorable(String boton) {
        assertThat(boton).isEqualTo(BOTON_OT_FAVORABLE);
        emitirOtFavorable();
    }

    @Entonces("el botón {string} está inhabilitado")
    public void botonInhabilitado(String boton) {
        // RN10: ningún rol que ve el botón lo tiene habilitado, y el backend rechaza la acción.
        for (Usuario usuario : List.of(tecnicoPre, coordinadorPre, tecnicoUrp)) {
            EstadoAccionDto estado = accion(boton, pantallaComo(usuario));
            assertThat(estado.getHabilitada()).as(boton + " / " + usuario.getRol()).isFalse();
        }
        if (!BOTON_ENVIAR_AJUSTES.equalsIgnoreCase(boton)) {
            capturar(() -> guardarComentariosComo(tecnicoPre));
            assertThat(error).isInstanceOf(ConflictoEstadoException.class);
            assertThat(((ConflictoEstadoException) error).getCodigo())
                    .isEqualTo(OpinionTecnicaContexto.OPINION_TECNICA_YA_EMITIDA);
        }
    }

    @Dado("que se emitió la Opinión Técnica del proyecto para la etapa de Ejecución")
    public void otParaEjecucion() {
        // Ruta PERFIL → PREFACTIBILIDAD → DISEÑO → EJECUCIÓN con OT ya emitida hasta Prefactibilidad.
        etapas.findByProyectoId(proyecto.getId()).forEach(e -> {
            e.setTieneOpinionTecnica(true);
            etapas.save(e);
        });
        etapa(TipoEtapaPreinversion.DISENO);
        etapa(TipoEtapaPreinversion.EJECUCION);
        emitirOtFavorable();
        assertThat(gestionActual().getEtapaFutura()).isEqualTo(TipoEtapaPreinversion.EJECUCION);
    }

    @Entonces("el proyecto está disponible para visualización y\\/o actualización")
    public void proyectoDisponibleEnCaptura() {
        assertThat(emision.getDisponibleEnCapturaProyectos()).isTrue();
        proyectoApareceEnLaLista();
    }

    // =============================================================================================
    // HU-PRE-26-09: Control del plazo de atención

    @Dado("que el Técnico PRE envió comentarios al proyecto")
    public void preEnvioComentarios() {
        asegurarGestion();
        enviarComentariosAlProyecto();
    }

    @Dado("han transcurrido {int} días hábiles desde el envío de comentarios")
    public void hanTranscurridoDias(int dias) {
        hoy = sumarDiasHabiles(envio.getFechaEnvioComentarios(), dias);
    }

    @Dado("venció el plazo de {int} días hábiles de atención de observaciones")
    public void vencioPlazo(int dias) {
        assertThat(envio.getFechaFinPlazoObservaciones())
                .isEqualTo(sumarDiasHabiles(envio.getFechaEnvioComentarios(), dias));
        hoy = envio.getFechaFinPlazoObservaciones();
    }

    @Dado("el Técnico URP no ha dado clic en el botón {string}")
    public void urpNoEnvioAjustes(String boton) {
        assertThat(boton).isEqualToIgnoringCase(BOTON_ENVIAR_AJUSTES);
        assertThat(gestionActual().getFechaAjustes()).isNull();
    }

    @Dado("el Técnico URP dio clic en el botón {string}")
    public void urpEnvioAjustesConBoton(String boton) {
        assertThat(boton).isEqualToIgnoringCase(BOTON_ENVIAR_AJUSTES);
        // FA03.1: los ajustes se envían con "Solicitar Viabilidad" de CU-PRE-24.
        responderComentarios();
        solicitarViabilidad();
        assertThat(error).isNull();
        assertThat(gestionActual().getFechaAjustes()).isNotNull();
    }

    @Cuando("el Sistema evalúa el plazo de atención de observaciones")
    public void sistemaEvaluaPlazo() {
        enTransaccion(() -> {
            plazo.evaluar(hoy);
            return null;
        });
    }

    @Entonces("envía al Técnico URP y al Viabilizador el correo electrónico del Anexo A2 f")
    public void correoA2f() {
        verify(notificaciones).notificarAlertaPlazoObservaciones(esProyecto(),
                argThat(d -> tiene(d, tecnicoUrp) && tiene(d, viabilizador)),
                argThat(fin -> fin.equals(envio.getFechaFinPlazoObservaciones())));
    }

    @Entonces("no envía el correo electrónico del Anexo A2 f")
    public void noEnviaCorreoA2f() {
        verify(notificaciones, never()).notificarAlertaPlazoObservaciones(esProyecto(), any(), any());
    }

    @Entonces("envía al Técnico URP y al Viabilizador el correo electrónico del Anexo A2 g")
    public void correoA2g() {
        verify(notificaciones).notificarVencimientoPlazoObservaciones(esProyecto(),
                argThat(d -> tiene(d, tecnicoUrp) && tiene(d, viabilizador)));
    }

    @Entonces("elimina las solicitudes del módulo de gestión del proyecto \\(Viabilidad, soportes y OT observado)")
    public void eliminaSolicitudesDeGestion() {
        // La OT observada queda archivada y ya no bloquea: la institución tramita de nuevo desde la Viabilidad.
        autenticar(tecnicoUrp);
        assertThat(enTransaccion(() -> solicitudService.listar(proyecto.getId())).getOpinionesTecnicas().get(0)
                .getEstadoGestion()).isEqualTo(EstadoGestionOpinionTecnicaDto.ARCHIVADA);
        assertThat(filtros.tieneComentariosProyectoSinResponder(proyecto.getId())).isFalse();
        assertThat(proyectoActual().getEstado()).isEqualTo(EstadoProyecto.EN_FORMULACION);
        assertThat(fichaViabilidadComo(tecnicoUrp).getAccionesDisponibles().getSolicitarViabilidad()).isTrue();
        capturar(() -> justificarComo(tecnicoUrp));
        assertThat(((ConflictoEstadoException) error).getCodigo())
                .isEqualTo(OpinionTecnicaContexto.SOLICITUD_OT_ARCHIVADA);
    }

    @Entonces("archiva la solicitud")
    public void archivaLaSolicitud() {
        SolicitudPreinversion archivada = solicitudDeLaGestion();
        assertThat(archivada.getEstado()).isEqualTo(EstadoSolicitud.ARCHIVADA);
        assertThat(archivada.getFechaArchivo()).isNotNull();
    }

    @Entonces("la solicitud deja de visualizarse en la Bandeja de Preinversión \\(CU-PRE-{int})")
    public void solicitudFueraDeLaBandeja(int cu) {
        assertThat(cu).isEqualTo(3);
        Long idSolicitud = solicitudDeLaGestion().getId();
        autenticar(coordinadorPre);
        for (int pagina = 0;; pagina++) {
            int numero = pagina;
            List<SolicitudActivaItemDto> activas = enTransaccion(
                    () -> bandeja.activas(TipoSolicitudDto.OPINION_TECNICA, numero, 200)).getContenido();
            if (activas.isEmpty()) {
                break;
            }
            assertThat(activas).noneMatch(s -> s.getIdSolicitud().equals(idSolicitud));
        }
    }

    // =============================================================================================
    // HU-PRE-26-10: Actualización de OT

    @Dado("que se está solicitando una Actualización de Opinión Técnica para el proyecto")
    public void seEstaSolicitandoActualizacion() {
        emitirOtFavorable();
        actor = tecnicoUrp;
        solicitarActualizacion();
        assertThat(error).isNull();
    }

    @Cuando("ingresa al Módulo de Gestión")
    public void ingresaAlModuloDeGestion() {
        pantalla = "Gestión";
    }

    @Cuando("selecciona en el menú de OT la opción {string}")
    public void seleccionaOpcion(String opcion) {
        assertThat(opcion).isEqualTo("Actualización de OT");
        tipos = tiposComo(actor);
        botonPulsado = opcion;
    }

    @Entonces("el Sistema muestra el mensaje del Anexo A.{int} {string} con los botones {string} y {string}")
    public void muestraMensajeA3(int anexo, String mensaje, String si, String no) {
        assertThat(anexo).isEqualTo(3);
        assertThat(mensaje).isEqualTo(MENSAJE_A3);
        assertThat(List.of(si, no)).containsExactly(BOTON_SI, "NO");
        // La confirmación es solo de la interfaz: seleccionar la opción todavía no abre la gestión.
        assertThat(tipos.getTiposSolicitud()).extracting(TipoSolicitudDisponibleDto::getTipoSolicitud)
                .contains(TipoSolicitudOpinionTecnicaDto.ACTUALIZACION_OT);
        assertThat(opinionesTecnicas.existsByProyectoId(proyecto.getId())).isFalse();
    }

    @Dado("existe una OT previa para la etapa que se está gestionando")
    public void existeOtPrevia() {
        emitirOtFavorable();
        actor = tecnicoUrp;
    }

    @Dado("se muestra el mensaje del Anexo A.{int}")
    public void seMuestraMensaje(int anexo) {
        assertThat(anexo).isEqualTo(3);
        tipos = tiposComo(actor);
        assertThat(opcion(TipoSolicitudOpinionTecnicaDto.ACTUALIZACION_OT).getHabilitado()).isTrue();
    }

    @Entonces("el Sistema muestra el mensaje del Anexo A.{int} {string}")
    public void muestraMensajeA4(int anexo, String mensaje) {
        assertThat(anexo).isEqualTo(4);
        assertThat(mensaje).isEqualTo(MENSAJE_A4);
        assertThat(error).isNull();
        assertThat(actualizacion.getTipoSolicitud()).isEqualTo(TipoSolicitudOpinionTecnicaDto.ACTUALIZACION_OT);
    }

    @Entonces("habilita los campos indicados en la hoja de cálculo CU-PRE-{int}.{int} {string} ANEXO según el {string} y {string}")
    public void habilitaCamposDeActualizacion(int cu, int subCu, String hoja, String iniciativa, String campos) {
        assertThat(List.of(cu, subCu)).containsExactly(3, 5);
        assertThat(hoja).isEqualTo("Selección y registro de etapas");
        assertThat(List.of(iniciativa, campos))
                .containsExactly("Tipo de Iniciativa", "Campos a habilitar para Actualización de O.T.");
        assertThat(actualizacion.getCamposHabilitados()).contains("CU-PRE-04", "CU-PRE-17");
        assertThat(gestionActual().getEtapaActual()).isEqualTo(TipoEtapaPreinversion.PERFIL);
    }

    @Dado("el Sistema habilitó los campos para la Actualización de OT")
    public void sistemaHabilitoCampos() {
        existeOtPrevia();
        solicitarActualizacion();
        assertThat(error).isNull();
    }

    @Cuando("realiza ajustes en los campos habilitados")
    public void realizaAjustes() {
        assertThat(proyectoActual().getEstado().bloqueaFormulacion()).isFalse();
    }

    @Entonces("continúa con el proceso de Viabilidad \\(CU-PRE-{int} {string})")
    public void continuaConViabilidad(int cu, String nombre) {
        assertThat(cu).isEqualTo(24);
        assertThat(nombre).isEqualTo(VIABILIDAD);
        assertThat(fichaViabilidadComo(tecnicoUrp).getAccionesDisponibles().getSolicitarViabilidad()).isTrue();
        solicitarViabilidad();
        assertThat(error).isNull();
        assertThat(proyectoActual().getEstado()).isEqualTo(EstadoProyecto.EN_VIABILIDAD);
    }

    // =============================================================================================
    // Pasos compartidos con CU-PRE-24 (definidos en Pre24Viabilidad)

    /** "da clic en el botón {string}". */
    void daClicEnBoton(String boton) {
        botonPulsado = boton;
        switch (boton) {
            case BOTON_GUARDAR -> guardarComo(actor);
            case BOTON_ENVIAR_COMENTARIOS -> enviar(actor);
            case BOTON_OT_FAVORABLE -> capturar(() -> emitirComo(actor));
            case BOTON_SOLICITAR_OT -> capturar(() -> solicitarComo(actor));
            case BOTON_SI -> solicitarActualizacion();
            default -> throw new IllegalArgumentException("Botón no reconocido: " + boton);
        }
    }

    /** "el botón {string} no está habilitado". */
    void botonNoHabilitado(String boton) {
        assertThat(boton).isEqualTo(BOTON_OT_FAVORABLE);
        assertThat(vista.getAccionesDisponibles().getOtFavorable().getHabilitada()).isFalse();
        capturar(() -> emitirComo(tecnicoPre));
        assertThat(error).isInstanceOf(ConflictoEstadoException.class);
        assertThat(((ConflictoEstadoException) error).getCodigo())
                .isEqualTo(OpinionTecnicaEmision.VISTO_BUENO_OT_PENDIENTE);
    }

    /** "el campo {string} no es editable". */
    void campoNoEditable(String campo) {
        assertThat(campo).isEqualTo("Fecha de solicitud");
        // Ninguna operación de escritura de la pantalla recibe la fecha: la asigna el Sistema.
        assertThat(camposDeEscritura()).doesNotContain("fechaSolicitud");
    }

    // =============================================================================================
    // Visibilidad de botones

    @Entonces("el botón {string} es visible")
    public void botonVisible(String boton) {
        assertThat(visible(boton)).as(boton).isTrue();
    }

    @Entonces("el botón {string} no es visible")
    public void botonNoVisible(String boton) {
        assertThat(visible(boton)).as(boton).isFalse();
    }

    private boolean visible(String boton) {
        if (VIABILIDAD.equals(pantalla)) {
            assertThat(boton).isEqualTo(BOTON_SOLICITAR_VIABILIDAD);
            // Solo el Técnico URP ve el botón (FA03.1); la DGICP ni siquiera accede a la ficha de CU-PRE-24.
            if (error != null) {
                assertThat(error).isInstanceOf(AccesoDenegadoException.class);
                return false;
            }
            return actor.getRol() == RolUsuario.TECNICO_URP
                    && fichaViabilidadComo(actor).getAccionesDisponibles().getSolicitarViabilidad();
        }
        return accion(boton, vista).getVisible();
    }

    // =============================================================================================
    // Operaciones

    /** Abre la gestión de OT del escenario si todavía no existe (el Técnico URP la solicita). */
    private void asegurarGestion() {
        if (idGestion != null) {
            return;
        }
        if (notaSolicitud == null) {
            anexaNotaSolicitud(NOTA_SOLICITUD);
        }
        solicitarComo(tecnicoUrp);
    }

    private void solicitar() {
        solicitarComo(actor);
    }

    private void solicitarComo(Usuario usuario) {
        autenticar(usuario);
        solicitud = enTransaccion(() -> solicitudService.solicitar(proyecto.getId(), notaSolicitud));
        idGestion = solicitud.getOpinionTecnicaId();
    }

    private void solicitarActualizacion() {
        capturar(() -> {
            autenticar(tecnicoUrp);
            actualizacion = enTransaccion(() -> solicitudService.solicitarActualizacion(proyecto.getId()));
            idGestion = actualizacion.getOpinionTecnicaId();
        });
    }

    /** "Guardar" según el campo que el actor registró. */
    private void guardarComo(Usuario usuario) {
        capturar(() -> {
            if (CONCLUSIONES.equals(campoRegistrado)) {
                autenticar(usuario);
                enTransaccion(() -> service.guardarConclusiones(proyecto.getId(), idGestion,
                        new ConclusionesOpinionTecnicaRequestDto(borradorConclusiones)));
            } else if (JUSTIFICACION.equals(campoRegistrado)) {
                justificarComo(usuario);
            } else {
                guardarComentariosComo(usuario);
            }
        });
    }

    private void guardarComentariosComo(Usuario usuario) {
        autenticar(usuario);
        enTransaccion(() -> service.guardarComentarios(proyecto.getId(), idGestion, comentarios()));
    }

    private void enviar(Usuario usuario) {
        capturar(() -> {
            autenticar(usuario);
            envio = enTransaccion(() -> service.enviarComentarios(proyecto.getId(), idGestion, comentarios()));
        });
    }

    /** FA03 con un comentario al apartado "Problema Central". */
    private void enviarComentariosAlProyecto() {
        if (borradorComentarios.isEmpty() && borradorElegibilidad == null) {
            borradorComentarios.put(APARTADO, "Precisar la magnitud del problema central");
        }
        enviar(tecnicoPre);
        assertThat(error).isNull();
    }

    /** El Técnico URP responde todos los comentarios DGICP de la gestión observada. */
    private void responderComentarios() {
        if (borradorJustificacion == null) {
            borradorJustificacion = "Se amplió el análisis del problema central con datos de 2025.";
        }
        justificarComo(tecnicoUrp);
    }

    private void justificarComo(Usuario usuario) {
        JustificacionesInstitucionRequestDto request = new JustificacionesInstitucionRequestDto();
        if (!borradorComentarios.isEmpty()) {
            request.setJustificacionesApartados(new ArrayList<>(List.of(
                    new JustificacionApartadoRequestDto(APARTADO, borradorJustificacion))));
        }
        // El Técnico URP responde el proyecto y el Viabilizador la Elegibilidad (Anexo B.1); otro actor
        // intenta el formulario completo.
        boolean respondeViabilizador = usuario.getRol() == RolUsuario.TECNICO_URP;
        if (borradorElegibilidad != null && !respondeViabilizador) {
            request.setJustificacionInstitucionElegibilidad(JUSTIFICACION_ELEGIBILIDAD);
        }
        autenticar(usuario);
        enTransaccion(() -> service.guardarJustificaciones(proyecto.getId(), idGestion, request));
        if (borradorElegibilidad != null && respondeViabilizador) {
            JustificacionesInstitucionRequestDto soloElegibilidad = new JustificacionesInstitucionRequestDto();
            soloElegibilidad.setJustificacionInstitucionElegibilidad(JUSTIFICACION_ELEGIBILIDAD);
            autenticar(viabilizador);
            enTransaccion(() -> service.guardarJustificaciones(proyecto.getId(), idGestion, soloElegibilidad));
        }
    }

    private void vistoBuenoComo(Usuario usuario) {
        autenticar(usuario);
        enTransaccion(() -> service.darVistoBueno(proyecto.getId(), idGestion));
    }

    private void emitirComo(Usuario usuario) {
        autenticar(usuario);
        emision = enTransaccion(() -> service.emitirFavorable(proyecto.getId(), idGestion, notaOt, NUMERO_NOTA_OT));
    }

    /** FA01 completo: conclusiones, visto bueno y OT favorable. */
    private void emitirOtFavorable() {
        coordinadorDioVistoBueno();
        cargoNotaOt("Nota de OT");
        emitirComo(tecnicoPre);
        assertThat(proyectoActual().getEstado()).isEqualTo(EstadoProyecto.PROYECTO_CON_OT);
    }

    private void solicitarViabilidad() {
        capturar(() -> {
            autenticar(tecnicoUrp);
            enTransaccion(() -> {
                viabilidad.solicitarViabilidad(proyecto.getId());
                return null;
            });
        });
    }

    private void emitirViabilidad() {
        autenticar(viabilizador);
        GuardarComentariosViabilidadRequestDto observaciones = new GuardarComentariosViabilidadRequestDto(
                new ArrayList<>());
        observaciones.setObservacionesGeneralesJustificacion("Se atendieron los comentarios de la OT.");
        enTransaccion(() -> viabilidad.guardarComentarios(proyecto.getId(), observaciones));
        emisionViabilidad = enTransaccion(() -> viabilidad.emitirViabilidad(proyecto.getId()));
    }

    /** Segunda ronda (RN 12): comentarios al proyecto, ajustes, nueva Viabilidad y nueva solicitud de OT. */
    private void completarRondaYSolicitarDeNuevo() {
        enviarComentariosAlProyecto();
        responderComentarios();
        solicitarViabilidad();
        assertThat(error).isNull();
        emitirViabilidad();
        borradorComentarios.clear();
        vuelveAOt();
    }

    /** Intenta escribir en un campo de la pantalla con el rol del actor. */
    private void escribirComo(Usuario usuario, String campo) {
        if (JUSTIFICACION.equals(campo)) {
            justificarComo(usuario);
        } else if (CONCLUSIONES.equals(campo)) {
            autenticar(usuario);
            enTransaccion(() -> service.guardarConclusiones(proyecto.getId(), idGestion,
                    new ConclusionesOpinionTecnicaRequestDto("Conclusiones")));
        } else {
            borradorComentarios.putIfAbsent(APARTADO, "Comentario");
            guardarComentariosComo(usuario);
        }
    }

    private ComentariosDgicpRequestDto comentarios() {
        ComentariosDgicpRequestDto request = new ComentariosDgicpRequestDto();
        borradorComentarios.forEach((codigo, texto) -> request.addComentariosApartadosItem(
                new ComentarioApartadoRequestDto(codigo, texto)));
        request.setComentarioDgicpElegibilidad(borradorElegibilidad);
        return request;
    }

    private OpinionTecnicaResponseDto pantallaComo(Usuario usuario) {
        autenticar(usuario);
        return enTransaccion(() -> service.obtener(proyecto.getId(), idGestion));
    }

    private TiposSolicitudOpinionTecnicaResponseDto tiposComo(Usuario usuario) {
        autenticar(usuario);
        return enTransaccion(() -> solicitudService.consultarTiposSolicitud(proyecto.getId()));
    }

    private sv.gob.mh.siip.model.preinversion.dto.FichaViabilidadResponseDto fichaViabilidadComo(Usuario usuario) {
        autenticar(usuario);
        return enTransaccion(() -> viabilidad.consultarFicha(proyecto.getId()));
    }

    /**
     * Los servicios se construyen a mano (para inyectarles el mock de notificaciones), así que no tienen el
     * proxy transaccional de Spring: cada operación corre aquí en su propia transacción, como en producción.
     */
    private <T> T enTransaccion(Supplier<T> operacion) {
        return transacciones.execute(estado -> operacion.get());
    }

    private void capturar(Runnable accion) {
        error = null;
        try {
            accion.run();
        } catch (RuntimeException ex) {
            error = ex;
        }
    }

    // =============================================================================================
    // Datos

    /** Viabilidad y Elegibilidad emitidas hace una hora, Documento de Preinversión y ruta de etapas. */
    private void sembrarPrecondiciones() {
        LocalDateTime antes = LocalDateTime.now(ZONA).minusHours(1);
        revisiones.save(RevisionViabilidad.builder().proyecto(proyecto).numero(1)
                .estado(EstadoRevisionViabilidad.EMITIDA).solicitante(tecnicoUrp).viabilizador(viabilizador)
                .fechaSolicitud(antes.minusMinutes(10)).fechaCierre(antes)
                .observacionesGenerales("Proyecto viable.").habilitaElegibilidad(true).build());
        viabilidades.save(Viabilidad.builder().proyecto(proyecto).resultado(ResultadoViabilidad.VIABLE)
                .fechaEvaluacion(antes).evaluador(viabilizador).build());
        elegibilidades.save(Elegibilidad.builder().proyecto(proyecto).resultado(ResultadoElegibilidad.ELEGIBLE)
                .fechaEvaluacion(antes.plusMinutes(5)).build());
        documentosViabilidad.cargar(proyecto, TipoDocumentoViabilidad.DOCUMENTO_PREINVERSION,
                archivo("archivo", "documento-preinversion.pdf"), tecnicoUrp);
        etapa(TipoEtapaPreinversion.PERFIL);
        etapa(TipoEtapaPreinversion.PREFACTIBILIDAD);
    }

    private void etapa(TipoEtapaPreinversion tipo) {
        etapas.save(EtapaPreinversion.builder().proyecto(proyecto).tipoEtapa(tipo)
                .fechaSeleccion(LocalDateTime.now(ZONA)).build());
    }

    private OpinionTecnica gestionActual() {
        return opinionesTecnicas.findById(idGestion).orElseThrow();
    }

    /** La solicitud de la Bandeja de Preinversión de la gestión, con su técnico ya cargado. */
    private SolicitudPreinversion solicitudDeLaGestion() {
        return enTransaccion(() -> {
            SolicitudPreinversion enBandeja = gestionActual().getSolicitud();
            if (enBandeja.getTecnicoAsignado() != null) {
                enBandeja.getTecnicoAsignado().getId();
            }
            enBandeja.getEstado();
            return enBandeja;
        });
    }

    private Proyecto proyectoActual() {
        return proyectos.findById(proyecto.getId()).orElseThrow();
    }

    private TipoSolicitudDisponibleDto opcion(TipoSolicitudOpinionTecnicaDto tipo) {
        return tipos.getTiposSolicitud().stream()
                .filter(t -> t.getTipoSolicitud() == tipo)
                .findFirst()
                .orElseThrow();
    }

    private ApartadoOpinionTecnicaDto apartado(String codigo) {
        return apartado(vista, codigo);
    }

    private static ApartadoOpinionTecnicaDto apartado(OpinionTecnicaResponseDto pantalla, String codigo) {
        return pantalla.getApartados().stream()
                .filter(a -> a.getApartadoCodigo().equals(codigo))
                .findFirst()
                .orElseThrow();
    }

    private static EstadoAccionDto accion(String boton, OpinionTecnicaResponseDto pantalla) {
        return switch (boton.toLowerCase(java.util.Locale.ROOT)) {
            case "solicitar ot" -> pantalla.getAccionesDisponibles().getSolicitarOt();
            case "guardar" -> pantalla.getAccionesDisponibles().getGuardar();
            case "enviar comentarios" -> pantalla.getAccionesDisponibles().getEnviarComentarios();
            case "enviar ajustes" -> pantalla.getAccionesDisponibles().getEnviarAjustes();
            case "visto bueno ot" -> pantalla.getAccionesDisponibles().getVistoBuenoOt();
            case "ot favorable" -> pantalla.getAccionesDisponibles().getOtFavorable();
            default -> throw new IllegalArgumentException("Botón no reconocido: " + boton);
        };
    }

    private boolean campoEditable(String campo) {
        return switch (campo) {
            case COMENTARIOS_DGICP -> vista.getCamposEditables().getComentarioDgicp();
            case COMENTARIOS_ELEGIBILIDAD -> vista.getCamposEditables().getComentarioDgicpElegibilidad();
            case COMENTARIOS_DOCUMENTOS -> vista.getCamposEditables().getComentarioDgicpDocumentosAnexos();
            case CONCLUSIONES -> vista.getCamposEditables().getConclusiones();
            case JUSTIFICACION -> vista.getCamposEditables().getJustificacionInstitucion();
            default -> throw new IllegalArgumentException("Campo no reconocido: " + campo);
        };
    }

    /** Propiedades que reciben las operaciones de escritura de la pantalla de OT. */
    private static List<String> camposDeEscritura() {
        return Stream.of(ComentariosDgicpRequestDto.class, ComentarioApartadoRequestDto.class,
                        JustificacionesInstitucionRequestDto.class, JustificacionApartadoRequestDto.class,
                        ConclusionesOpinionTecnicaRequestDto.class, AsignarOpinionTecnicaRequestDto.class)
                .flatMap(c -> Arrays.stream(c.getDeclaredFields()))
                .map(Field::getName)
                .toList();
    }

    private static EstadoProyecto estado(String etiqueta) {
        return switch (etiqueta.toLowerCase(java.util.Locale.ROOT)) {
            case "proyecto viable" -> EstadoProyecto.VIABLE;
            case "proyecto elegible" -> EstadoProyecto.ELEGIBLE;
            case "observado" -> EstadoProyecto.OBSERVADO;
            case "proyecto con ot" -> EstadoProyecto.PROYECTO_CON_OT;
            default -> throw new IllegalArgumentException("Estado no reconocido: " + etiqueta);
        };
    }

    private Usuario usuarioDelRol(String rol) {
        return switch (rol) {
            case "Técnico URP" -> tecnicoUrp;
            case "Viabilizador" -> viabilizador;
            case "Técnico PRE" -> tecnicoPre;
            case "Coordinador PRE" -> coordinadorPre;
            default -> throw new IllegalArgumentException("Rol no reconocido: " + rol);
        };
    }

    private static boolean esSolicitante(Usuario usuario) {
        return usuario.getRol() == RolUsuario.TECNICO_URP || usuario.getRol() == RolUsuario.VIABILIZADOR;
    }

    private Proyecto esProyecto() {
        return argThat(p -> p.getId().equals(proyecto.getId()));
    }

    private static List<Usuario> contiene(Usuario usuario) {
        return argThat(d -> tiene(d, usuario));
    }

    private static boolean tiene(List<Usuario> destinatarios, Usuario usuario) {
        return destinatarios.stream().anyMatch(u -> u.getId().equals(usuario.getId()));
    }

    private static LocalDate sumarDiasHabiles(LocalDate desde, int dias) {
        LocalDate fecha = desde;
        int contados = 0;
        while (contados < dias) {
            fecha = fecha.plusDays(1);
            if (fecha.getDayOfWeek().getValue() < 6) {
                contados++;
            }
        }
        return fecha;
    }

    private static MultipartFile archivo(String parte, String nombre) {
        return new MockMultipartFile(parte, nombre, "application/pdf", "contenido".getBytes(StandardCharsets.UTF_8));
    }

    private static Usuario usuario(String nombreUsuario, RolUsuario rol, UnidadEjecutora unidad,
            Institucion institucion) {
        return Usuario.builder().nombreUsuario(nombreUsuario).nombreCompleto(nombreUsuario)
                .correo(nombreUsuario + "@example.com").rol(rol).unidadEjecutora(unidad).institucion(institucion)
                .activo(true).build();
    }

    private static void autenticar(Usuario usuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, usuario.getNombreUsuario());
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
