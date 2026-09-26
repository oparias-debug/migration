package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.ReglaNegocioException;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.ActividadOm;
import sv.gob.mh.siip.model.preinversion.domain.AnalisisPoblacion;
import sv.gob.mh.siip.model.preinversion.domain.CeldaUbicacionPoblacion;
import sv.gob.mh.siip.model.preinversion.domain.ComentarioOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Componente;
import sv.gob.mh.siip.model.preinversion.domain.DescripcionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.Elegibilidad;
import sv.gob.mh.siip.model.preinversion.domain.Identificacion;
import sv.gob.mh.siip.model.preinversion.domain.IndicadorEvaluacion;
import sv.gob.mh.siip.model.preinversion.domain.InsumoActividad;
import sv.gob.mh.siip.model.preinversion.domain.MacroactividadPresupuesto;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.PresupuestoProyecto;
import sv.gob.mh.siip.model.preinversion.domain.PresupuestoOmConfiguracion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.RevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.dto.AccionesDisponiblesViabilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.CampoFichaViabilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.ComentarioCampoViabilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.EmitirViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarComentariosViabilidadRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.IdentificacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.IndicadorEvaluacionDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoRevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.enums.FuenteFinanciamiento;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoElegibilidad;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.enums.TipoDocumentoViabilidad;
import sv.gob.mh.siip.model.preinversion.enums.TipoIndicador;
import sv.gob.mh.siip.model.preinversion.repository.ActividadOmRepository;
import sv.gob.mh.siip.model.preinversion.repository.AnalisisPoblacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ComentarioOpinionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ComponenteRepository;
import sv.gob.mh.siip.model.preinversion.repository.DescripcionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ElegibilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.IdentificacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.IndicadorEvaluacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.MacroactividadPresupuestoRepository;
import sv.gob.mh.siip.model.preinversion.repository.OpinionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.PresupuestoOmConfiguracionRepository;
import sv.gob.mh.siip.model.preinversion.repository.PresupuestoProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.RevisionViabilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.ViabilidadRepository;
import sv.gob.mh.siip.model.preinversion.service.DocumentosViabilidad;
import sv.gob.mh.siip.model.preinversion.service.EdicionFormulacion;
import sv.gob.mh.siip.model.preinversion.service.FichaViabilidadEnsamblador;
import sv.gob.mh.siip.model.preinversion.service.FiltrosPosterioresViabilidad;
import sv.gob.mh.siip.model.preinversion.service.IdentificacionService;
import sv.gob.mh.siip.model.preinversion.service.NotificacionService;
import sv.gob.mh.siip.model.preinversion.service.ViabilidadService;
import sv.gob.mh.siip.model.preinversion.service.ViabilidadServiceImpl;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * Steps BDD de CU-PRE-24 "Viabilidad" (HU-PRE-24-01 solicitar, HU-PRE-24-02 enviar comentarios,
 * HU-PRE-24-03 emitir).
 *
 * <p>Se ejercita {@link ViabilidadServiceImpl} con los repositorios y colaboradores reales del
 * contexto Spring de pruebas; solo la notificación se sustituye por un mock para poder verificar a
 * quién se envía. Los pasos que describen presentación (links, tooltips, separador de miles,
 * navegación entre pantallas) verifican el dato del backend que la pantalla necesita para
 * mostrarlos: la presentación en sí corresponde al front.
 */
public class Pre24Viabilidad {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final ZoneId ZONA = ZoneId.of("America/El_Salvador");

    private static final String BOTON_SOLICITAR = "Solicitar Viabilidad";
    private static final String BOTON_GUARDAR = "Guardar";
    private static final String BOTON_ENVIAR = "Enviar comentarios";
    private static final String BOTON_EMITIR = "Emitir Viabilidad";
    private static final String BOTON_IR_ELEGIBILIDAD = "Ir a Elegibilidad";
    private static final String BOTON_VER_COMENTARIOS_OT = "Ver comentarios OT";
    private static final String DOCUMENTO_PREINVERSION = "Documento de Preinversión";
    private static final String JUSTIFICACION = "Observaciones Generales/Justificación de la Viabilidad";

    /** Textos de los mensajes que el cliente muestra según la respuesta del backend (FB1 paso 5; Anexo A.2). */
    private static final String MENSAJE_SOLICITUD =
            "la solicitud del proyecto ha sido enviada al proceso de viabilidad exitosamente";
    private static final String MENSAJE_EMISION = "La viabilidad del proyecto ha sido emitida con éxito";
    private static final String TEXTO_ELEGIBILIDAD = "Es necesario continuar con la gestión de Elegibilidad";

    // Datos de origen sembrados en la ficha (Anexo B.1).
    private static final String OBJETIVO_GENERAL = "Mejorar la conectividad vial del municipio";
    private static final String DESCRIPCION = "Ampliación de 12 km de carretera";
    private static final String PRODUCTO = "Carretera ampliada";
    private static final long PERSONAS = 12_500L;
    private static final double INVERSION = 1_250_000.75D;
    private static final double COSTO_OPERACION = 800D;
    private static final double VAN = 150_000.5D;

    private final InstitucionRepository instituciones;
    private final UnidadEjecutoraRepository unidades;
    private final UsuarioRepository usuarios;
    private final ProyectoRepository proyectos;
    private final MacroSectorRepository macrosectores;
    private final SectorActividadRepository sectores;
    private final EjeTematicoRepository ejes;
    private final RevisionViabilidadRepository revisiones;
    private final ViabilidadRepository viabilidades;
    private final ElegibilidadRepository elegibilidades;
    private final OpinionTecnicaRepository opinionesTecnicas;
    private final ComentarioOpinionTecnicaRepository comentariosOt;
    private final IdentificacionRepository identificaciones;
    private final DescripcionTecnicaRepository descripciones;
    private final ComponenteRepository componentes;
    private final AnalisisPoblacionRepository poblaciones;
    private final PresupuestoProyectoRepository presupuestos;
    private final IndicadorEvaluacionRepository indicadores;
    private final MacroactividadPresupuestoRepository macroactividades;
    private final PresupuestoOmConfiguracionRepository configuracionesOm;
    private final ActividadOmRepository actividadesOm;
    private final DocumentosViabilidad documentos;
    private final FiltrosPosterioresViabilidad filtros;
    private final FichaViabilidadEnsamblador ensamblador;
    private final ActorContexto actorContexto;
    private final IdentificacionService identificacionService;
    private final TransactionTemplate transacciones;

    private NotificacionService notificaciones;
    private ViabilidadService service;
    private Proyecto proyecto;
    private Usuario tecnicoUrp;
    private Usuario viabilizador;
    private FichaViabilidadResponseDto ficha;
    private GuardarComentariosViabilidadRequestDto borrador;
    private EmitirViabilidadResponseDto emision;
    private RuntimeException error;
    private String opcionSeleccionada;
    private String botonPulsado;

    public Pre24Viabilidad(InstitucionRepository instituciones, UnidadEjecutoraRepository unidades,
            UsuarioRepository usuarios, ProyectoRepository proyectos, MacroSectorRepository macrosectores,
            SectorActividadRepository sectores, EjeTematicoRepository ejes, RevisionViabilidadRepository revisiones,
            ViabilidadRepository viabilidades, ElegibilidadRepository elegibilidades,
            OpinionTecnicaRepository opinionesTecnicas, ComentarioOpinionTecnicaRepository comentariosOt,
            IdentificacionRepository identificaciones, DescripcionTecnicaRepository descripciones,
            ComponenteRepository componentes, AnalisisPoblacionRepository poblaciones,
            PresupuestoProyectoRepository presupuestos, IndicadorEvaluacionRepository indicadores,
            MacroactividadPresupuestoRepository macroactividades, PresupuestoOmConfiguracionRepository configuracionesOm,
            ActividadOmRepository actividadesOm,
            DocumentosViabilidad documentos, FiltrosPosterioresViabilidad filtros,
            FichaViabilidadEnsamblador ensamblador, ActorContexto actorContexto,
            IdentificacionService identificacionService, PlatformTransactionManager transactionManager) {
        this.instituciones = instituciones;
        this.unidades = unidades;
        this.usuarios = usuarios;
        this.proyectos = proyectos;
        this.macrosectores = macrosectores;
        this.sectores = sectores;
        this.ejes = ejes;
        this.revisiones = revisiones;
        this.viabilidades = viabilidades;
        this.elegibilidades = elegibilidades;
        this.opinionesTecnicas = opinionesTecnicas;
        this.comentariosOt = comentariosOt;
        this.identificaciones = identificaciones;
        this.descripciones = descripciones;
        this.componentes = componentes;
        this.poblaciones = poblaciones;
        this.presupuestos = presupuestos;
        this.indicadores = indicadores;
        this.macroactividades = macroactividades;
        this.configuracionesOm = configuracionesOm;
        this.actividadesOm = actividadesOm;
        this.documentos = documentos;
        this.filtros = filtros;
        this.ensamblador = ensamblador;
        this.actorContexto = actorContexto;
        this.identificacionService = identificacionService;
        this.transacciones = new TransactionTemplate(transactionManager);
    }

    @Before("@CU-PRE-24")
    public void prepararEscenario() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = instituciones.save(ProyectoFixtures.nuevaInstitucion("MH-CU24-" + sufijo,
                "Ministerio de Hacienda CU24"));
        UnidadEjecutora unidad = unidades.save(ProyectoFixtures.nuevaUnidadEjecutora("UE24-" + sufijo,
                "UE CU24", institucion));
        tecnicoUrp = usuarios.save(usuario("tecnico.urp.pre24." + sufijo, RolUsuario.TECNICO_URP, unidad, institucion));
        viabilizador = usuarios.save(usuario("viabilizador.pre24." + sufijo, RolUsuario.VIABILIZADOR, null, null));
        MacroSector macrosector = macrosectores.save(ProyectoFixtures.nuevoMacrosector("M24" + sufijo,
                "Macrosector CU24"));
        SectorActividad sector = sectores.save(ProyectoFixtures.nuevoSector("S24" + sufijo, "Sector CU24",
                macrosector));
        EjeTematico eje = ejes.save(ProyectoFixtures.nuevoEjeTematico("E24" + sufijo, "Eje CU24"));
        Proyecto nuevo = ProyectoFixtures.nuevoProyecto("Proyecto CU24 " + sufijo, EstadoProyecto.EN_FORMULACION,
                unidad, institucion, sector, eje);
        nuevo.setCup(ProyectoFixtures.nuevoCup(proyectos));
        proyecto = proyectos.save(nuevo);

        notificaciones = mock(NotificacionService.class);
        service = new ViabilidadServiceImpl(proyectos, revisiones, viabilidades, usuarios, notificaciones,
                documentos, filtros, ensamblador, actorContexto);
    }

    @After("@CU-PRE-24")
    public void limpiarContexto() {
        RequestContextHolder.resetRequestAttributes();
    }

    // =============================================================================================
    // HU-PRE-24-01: Solicitud de Viabilidad

    @Dado("que el Técnico URP está en la pestaña {string}, sección {string} de un proyecto")
    public void tecnicoEnSeccionViabilidad(String pestana, String seccion) {
        assertThat(pestana).isEqualTo("Gestión del Proyecto");
        assertThat(seccion).isEqualTo("Viabilidad");
        ficha = fichaComo(tecnicoUrp);
    }

    @Dado("el botón {string} no está habilitado")
    public void botonNoHabilitado(String boton) {
        assertThat(accion(boton, fichaComo(actorDelBoton(boton)))).as(boton).isFalse();
    }

    @Cuando("el Técnico URP carga el {string}")
    public void tecnicoCargaDocumento(String documento) {
        assertThat(documento).isEqualTo(DOCUMENTO_PREINVERSION);
        capturar(this::cargarDocumentoPreinversion);
        assertThat(error).isNull();
    }

    @Entonces("el Sistema habilita el botón {string}")
    public void sistemaHabilitaBoton(String boton) {
        assertThat(accion(boton, fichaComo(actorDelBoton(boton)))).as(boton).isTrue();
    }

    @Cuando("el {string} no ha sido cargado")
    public void documentoNoCargado(String documento) {
        assertThat(documento).isEqualTo(DOCUMENTO_PREINVERSION);
        assertThat(documentos.tieneDocumentoPreinversion(proyecto.getId())).isFalse();
    }

    @Dado("que la solicitud corresponde a {}")
    public void solicitudCorrespondeA(String situacion) {
        if (situacion.startsWith("una nueva solicitud tras comentarios del Viabilizador")) {
            devolverProyecto("Revisar el objetivo general");
            assertThat(proyectoActual().getEstado()).isEqualTo(EstadoProyecto.OBSERVADO);
        } else if (situacion.startsWith("una respuesta a una observación de la OT")) {
            emitirViabilidadCompleta();
            registrarOtObservada(true);
        } else {
            assertThat(situacion).isEqualTo("la primera solicitud de Viabilidad del proyecto");
            assertThat(revisiones.findFirstByProyectoIdOrderByNumeroDesc(proyecto.getId())).isEmpty();
        }
    }

    @Dado("el registro de la información desde CU-PRE-04 {string} hasta CU-PRE-23 {string} está completo, según corresponda")
    public void registroFormulacionCompleto(String desde, String hasta) {
        assertThat(desde).isEqualTo("Identificación");
        assertThat(hasta).isEqualTo("Indicadores del Proyecto");
        sembrarFicha();
    }

    @Dado("el Técnico URP ha cargado el {string}")
    public void tecnicoHaCargado(String documento) {
        assertThat(documento).isEqualTo(DOCUMENTO_PREINVERSION);
        cargarDocumentoPreinversion();
    }

    @Cuando("el Técnico URP da clic en el botón {string}")
    public void tecnicoDaClic(String boton) {
        botonPulsado = boton;
        if (BOTON_SOLICITAR.equals(boton)) {
            capturar(this::solicitar);
        } else {
            // "Ver comentarios OT" solo navega a CU-PRE-26 (RN11): no invoca ninguna operación de CU-PRE-24.
            assertThat(boton).isEqualTo(BOTON_VER_COMENTARIOS_OT);
        }
    }

    @Entonces("el Sistema muestra el mensaje emergente {string}")
    public void sistemaMuestraMensajeEmergente(String mensaje) {
        assertThat(error).isNull();
        assertThat(mensaje).isEqualTo(MENSAJE_SOLICITUD);
        assertThat(revisionActual().getEstado()).isEqualTo(EstadoRevisionViabilidad.EN_CURSO);
    }

    @Entonces("el Sistema envía una notificación al Viabilizador informando que le ha llegado la solicitud")
    public void sistemaNotificaAlViabilizador() {
        verify(notificaciones, atLeastOnce()).notificarSolicitudViabilidad(
                argThat(p -> p.getId().equals(proyecto.getId())),
                argThat(destinatarios -> destinatarios.stream().anyMatch(u -> u.getId().equals(viabilizador.getId()))));
    }

    @Entonces("el Sistema bloquea para edición los campos de CU-PRE-04 {string} hasta CU-PRE-23 {string}")
    public void sistemaBloqueaFormulacion(String desde, String hasta) {
        assertThat(desde).isEqualTo("Identificación");
        assertThat(hasta).isEqualTo("Indicadores del Proyecto");
        assertThat(proyectoActual().getEstado().bloqueaFormulacion()).isTrue();
        RuntimeException rechazo = intentarEditarIdentificacion();
        assertThat(rechazo).isInstanceOf(ConflictoEstadoException.class);
        assertThat(((ConflictoEstadoException) rechazo).getCodigo())
                .isEqualTo(EdicionFormulacion.CODIGO_FORMULACION_BLOQUEADA);
    }

    @Entonces("el Sistema deshabilita el botón {string}")
    public void sistemaDeshabilitaBoton(String boton) {
        assertThat(accion(boton, fichaComo(tecnicoUrp))).as(boton).isFalse();
    }

    @Entonces("el Sistema activa el botón {string} para el Viabilizador")
    public void sistemaActivaBotonViabilizador(String boton) {
        assertThat(accion(boton, fichaComo(viabilizador))).as(boton).isTrue();
    }

    @Dado("que el Técnico URP está en la pantalla del Anexo A.1 de un proyecto con observaciones de OT")
    public void tecnicoConObservacionesOt() {
        emitirViabilidadCompleta();
        registrarOtObservada(false);
        ficha = fichaComo(tecnicoUrp);
    }

    @Entonces("el Sistema lo remite a los comentarios de CU-PRE-26 {string} para diligenciar la columna {string}")
    public void sistemaRemiteAComentariosOt(String cu, String columna) {
        assertThat(botonPulsado).isEqualTo(BOTON_VER_COMENTARIOS_OT);
        assertThat(cu).isEqualTo("Opinión técnica");
        assertThat(columna).isEqualTo("Justificación Institución");
        // Tras la devolución de la OT la ficha vuelve a admitir solicitud, pero quedan comentarios por responder.
        assertThat(filtros.tieneComentariosOtSinResponder(proyecto.getId())).isTrue();
    }

    @Dado("que la solicitud de Viabilidad responde a una observación de la OT")
    public void solicitudRespondeAObservacionOt() {
        emitirViabilidadCompleta();
    }

    @Dado("el Técnico URP no ha respondido todos los comentarios emitidos por OT en la columna {string} de CU-PRE-26 {string}")
    public void comentariosOtSinResponder(String columna, String cu) {
        assertThat(columna).isEqualTo("Justificación Institución");
        assertThat(cu).isEqualTo("Opinión técnica");
        registrarOtObservada(false);
    }

    @Entonces("el Sistema muestra el mensaje {string}")
    public void sistemaMuestraMensaje(String mensaje) {
        if (emision != null) {
            assertThat(mensaje).isEqualTo(MENSAJE_EMISION);
        } else {
            assertThat(error).isNotNull();
            assertThat(error.getMessage()).isEqualTo(mensaje);
        }
    }

    @Entonces("el Sistema no permite solicitar Viabilidad")
    public void sistemaNoPermiteSolicitar() {
        assertThat(error).isInstanceOf(ReglaNegocioException.class);
        assertThat(((ReglaNegocioException) error).getCodigo()).isEqualTo("COMENTARIOS_OPINION_TECNICA_SIN_RESPONDER");
        assertThat(revisionActual().getEstado()).isEqualTo(EstadoRevisionViabilidad.EMITIDA);
        assertThat(proyectoActual().getEstado()).isEqualTo(EstadoProyecto.VIABLE);
    }

    @Dado("que el {string} del proyecto ha sido cargado")
    public void documentoDelProyectoCargado(String documento) {
        tecnicoHaCargado(documento);
    }

    @Cuando("el Viabilizador accede a la pestaña {string}, sección {string} del proyecto")
    public void viabilizadorAccedeASeccion(String pestana, String seccion) {
        assertThat(pestana).isEqualTo("Gestión del Proyecto");
        assertThat(seccion).isEqualTo("Viabilidad");
        ficha = fichaComo(viabilizador);
    }

    @Entonces("el botón {string} no está habilitado para el Viabilizador")
    public void botonNoHabilitadoParaViabilizador(String boton) {
        assertThat(accion(boton, ficha)).as(boton).isFalse();
    }

    // =============================================================================================
    // HU-PRE-24-02: Revisión y envío de comentarios

    @Dado("que el Técnico URP solicitó Viabilidad para un proyecto")
    public void tecnicoSolicitoViabilidad() {
        cargarDocumentoPreinversion();
        solicitar();
    }

    @Dado("el Sistema notificó al Viabilizador que hay un proyecto en bandeja con un link al formulario del Anexo A.1")
    public void sistemaNotificoConLink() {
        sistemaNotificaAlViabilizador();
    }

    @Cuando("el Viabilizador da clic en el link de la notificación")
    public void viabilizadorAbreLink() {
        ficha = fichaComo(viabilizador);
    }

    @Entonces("el Sistema despliega la pantalla del Anexo A.1")
    public void sistemaDespliegaAnexoA1() {
        assertThat(ficha.getProyectoId()).isEqualTo(proyecto.getId());
        assertThat(ficha.getCup()).isEqualTo(proyecto.getCup());
        assertThat(ficha.getAccionesDisponibles().getEnviarComentarios()).isTrue();
    }

    @Dado("que el Viabilizador está en la pantalla del Anexo A.1 de un proyecto")
    public void viabilizadorEnAnexoA1() {
        sembrarFicha();
        ficha = fichaComo(viabilizador);
    }

    @Cuando("el Viabilizador consulta la ficha del proyecto")
    public void viabilizadorConsultaFicha() {
        ficha = fichaComo(viabilizador);
    }

    @Entonces("el campo {string} muestra la información registrada en {}")
    public void campoMuestraInformacion(String campo, String origen) {
        assertThat(origen).startsWith(codigoOrigen(campo));
        Object esperado = valorEsperado(campo);
        if (esperado instanceof BigDecimal monto) {
            assertThat((BigDecimal) valorDe(campo)).as(campo).isEqualByComparingTo(monto);
        } else {
            assertThat(valorDe(campo)).as(campo).isEqualTo(esperado);
        }
    }

    @Entonces("el campo {string} no es editable")
    public void campoNoEditable(String campo) {
        // La única escritura del Viabilizador es "Guardar": solo lleva comentarios y observaciones.
        String propiedad = propiedadDe(campo);
        assertThat(Arrays.stream(GuardarComentariosViabilidadRequestDto.class.getDeclaredFields())
                .map(Field::getName))
                .containsExactlyInAnyOrder("comentariosViabilizador", "observacionesGeneralesJustificacion")
                .doesNotContain(propiedad);
    }

    @Cuando("el Viabilizador da clic en el link del campo {string}")
    public void viabilizadorDaClicEnLink(String campo) {
        ficha = fichaComo(viabilizador);
        assertThat(valorDe(campo)).as(campo).isNotNull();
    }

    @Entonces("el Sistema lo direcciona a la tabla de {}")
    public void sistemaDireccionaATabla(String origen) {
        // La navegación la hace el front con el id del proyecto; el backend expone ese id.
        assertThat(origen).matches("CU-PRE-(07|17|18|21|23) \".+\"");
        assertThat(ficha.getProyectoId()).isEqualTo(proyecto.getId());
    }

    @Cuando("el Viabilizador consulta el campo {string}")
    public void viabilizadorConsultaCampo(String campo) {
        assertThat(campo).isEqualTo("Inversión estimada");
        ficha = fichaComo(viabilizador);
    }

    @Entonces("el Sistema muestra el valor con el separador de miles \\(,)")
    public void sistemaMuestraSeparadorMiles() {
        BigDecimal inversion = ficha.getInversionEstimada();
        assertThat(inversion).isNotNull();
        // El backend envía el número sin formato; el separador es de presentación (Anexo B.1).
        assertThat(NumberFormat.getNumberInstance(Locale.US).format(inversion)).contains(",");
    }

    @Cuando("el Viabilizador acerca el cursor al signo de pregunta de un campo de la {string}")
    public void viabilizadorAcercaCursor(String tabla) {
        assertThat(tabla).isEqualTo("FICHA DEL PROYECTO");
        ficha = fichaComo(viabilizador);
    }

    @Entonces("el Sistema muestra la información de apoyo sobre los criterios a revisar en ese campo")
    public void sistemaMuestraInformacionApoyo() {
        // RN08: cada fila de la ficha se identifica por su campo; el texto de apoyo lo define el front.
        assertThat(CampoFichaViabilidadDto.values()).hasSize(10);
    }

    @Dado("que el Viabilizador está en la pantalla del Anexo A.1 de un proyecto con solicitud de Viabilidad")
    public void viabilizadorConSolicitud() {
        sembrarFicha();
        tecnicoSolicitoViabilidad();
        ficha = fichaComo(viabilizador);
    }

    @Cuando("el Viabilizador registra comentarios en la columna {string} y en el campo {string}")
    public void viabilizadorRegistraComentarios(String columna, String campo) {
        assertThat(columna).isEqualTo("Comentarios del Viabilizador");
        assertThat(campo).isEqualTo(JUSTIFICACION);
        borrador = borrador("Precisar el objetivo general", "Falta detalle de los productos",
                "Se requieren ajustes antes de emitir.");
    }

    @Cuando("da clic en el botón {string}")
    public void daClicEnBoton(String boton) {
        if (BOTON_GUARDAR.equals(boton)) {
            capturar(() -> guardar(borrador));
        } else {
            assertThat(boton).isEqualTo(BOTON_ENVIAR);
            capturar(this::enviar);
        }
    }

    @Entonces("el Sistema guarda cada uno de los comentarios registrados por el Viabilizador")
    public void sistemaGuardaComentarios() {
        assertThat(error).isNull();
        FichaViabilidadResponseDto guardada = fichaComo(viabilizador);
        assertThat(guardada.getComentariosViabilizador()).containsExactlyElementsOf(borrador.getComentariosViabilizador());
        assertThat(guardada.getObservacionesGeneralesJustificacion())
                .isEqualTo(borrador.getObservacionesGeneralesJustificacion());
    }

    @Dado("que el Viabilizador guardó sus comentarios en la pantalla del Anexo A.1 de un proyecto con solicitud de Viabilidad")
    public void viabilizadorGuardoComentarios() {
        viabilizadorConSolicitud();
        borrador = borrador("Precisar el objetivo general", null, "Devolver para ajustes.");
        guardar(borrador);
    }

    @Cuando("el Viabilizador da clic en el botón {string}")
    public void viabilizadorDaClic(String boton) {
        if (BOTON_EMITIR.equals(boton)) {
            capturar(() -> emision = emitir());
        } else {
            assertThat(boton).isEqualTo(BOTON_ENVIAR);
            capturar(this::enviar);
        }
    }

    @Entonces("el Sistema cambia el estado del proyecto a {string}")
    public void sistemaCambiaEstado(String estado) {
        assertThat(error).isNull();
        assertThat(proyectoActual().getEstado().getEtiquetaUi()).isEqualToIgnoringCase(estado);
    }

    @Entonces("el Sistema habilita para edición los campos de CU-PRE-04 {string} hasta CU-PRE-23 {string}")
    public void sistemaHabilitaFormulacion(String desde, String hasta) {
        assertThat(desde).isEqualTo("Identificación");
        assertThat(hasta).isEqualTo("Indicadores del Proyecto");
        assertThat(proyectoActual().getEstado().bloqueaFormulacion()).isFalse();
        assertThat(intentarEditarIdentificacion()).isNull();
    }

    @Entonces("el Sistema notifica al Técnico URP que el Viabilizador ha enviado comentarios a la información registrada, para su ajuste")
    public void sistemaNotificaComentarios() {
        verify(notificaciones).notificarComentariosViabilidad(
                argThat(p -> p.getId().equals(proyecto.getId())),
                argThat(u -> u.getId().equals(tecnicoUrp.getId())));
    }

    @Dado("que el proyecto ya fue devuelto previamente con comentarios del Viabilizador")
    public void proyectoDevueltoPreviamente() {
        devolverProyecto("Comentario de la primera devolución");
    }

    @Dado("el Técnico URP volvió a solicitar Viabilidad")
    public void tecnicoVolvioASolicitar() {
        solicitar();
        assertThat(revisionActual().getNumero()).isEqualTo(2);
    }

    @Cuando("el Viabilizador registra y guarda nuevos comentarios")
    public void viabilizadorGuardaNuevosComentarios() {
        borrador = borrador("Comentario de la segunda devolución", null, "Segunda devolución.");
        guardar(borrador);
    }

    @Entonces("el Sistema conserva guardados los comentarios del Viabilizador de cada devolución")
    public void sistemaConservaComentariosDeCadaDevolucion() {
        List<List<String>> comentariosPorRevision = transacciones.execute(estado ->
                revisiones.findByProyectoIdOrderByNumeroAsc(proyecto.getId()).stream()
                        .map(r -> r.getComentarios().stream().map(c -> c.getComentario()).toList())
                        .toList());
        assertThat(comentariosPorRevision).containsExactly(
                List.of("Comentario de la primera devolución"),
                List.of("Comentario de la segunda devolución"));
        assertThat(revisiones.countByProyectoIdAndEstado(proyecto.getId(), EstadoRevisionViabilidad.DEVUELTA))
                .isEqualTo(2);
    }

    @Dado("que el Técnico URP aún no ha dado clic en el botón {string}")
    public void tecnicoAunNoSolicita(String boton) {
        assertThat(boton).isEqualTo(BOTON_SOLICITAR);
        cargarDocumentoPreinversion();
    }

    @Cuando("el Viabilizador accede a la pantalla del Anexo A.1 del proyecto")
    public void viabilizadorAccedeAAnexoA1() {
        ficha = fichaComo(viabilizador);
    }

    @Entonces("el botón {string} no está activo")
    public void botonNoActivo(String boton) {
        assertThat(accion(boton, ficha)).as(boton).isFalse();
    }

    @Cuando("el Técnico URP accede a la pestaña {string}, sección {string} del proyecto")
    public void tecnicoAccedeASeccion(String pestana, String seccion) {
        tecnicoEnSeccionViabilidad(pestana, seccion);
    }

    @Entonces("el botón {string} no está habilitado para el Técnico URP")
    public void botonNoHabilitadoParaTecnico(String boton) {
        assertThat(accion(boton, ficha)).as(boton).isFalse();
    }

    // =============================================================================================
    // HU-PRE-24-03: Emisión de Viabilidad

    @Cuando("el campo {string} no ha sido registrado")
    public void campoNoRegistrado(String campo) {
        assertThat(campo).isEqualTo(JUSTIFICACION);
        assertThat(fichaComo(viabilizador).getObservacionesGeneralesJustificacion()).isNull();
    }

    @Cuando("el Viabilizador registra el campo {string}")
    public void viabilizadorRegistraJustificacion(String campo) {
        assertThat(campo).isEqualTo(JUSTIFICACION);
        borrador = borrador(null, null, "El proyecto cumple los criterios de viabilidad.");
    }

    @Cuando("el Viabilizador aún no ha dado clic en el botón {string}")
    public void viabilizadorAunNoEmite(String boton) {
        assertThat(boton).isEqualTo(BOTON_EMITIR);
        assertThat(revisionActual().getEstado()).isEqualTo(EstadoRevisionViabilidad.EN_CURSO);
    }

    @Dado("que es la primera vez que se gestiona la Viabilidad del proyecto")
    public void primeraGestion() {
        assertThat(elegibilidades.existsByProyectoId(proyecto.getId())).isFalse();
        tecnicoSolicitoViabilidad();
    }

    @Dado("el Viabilizador registró y guardó el campo {string}")
    public void viabilizadorRegistroYGuardoJustificacion(String campo) {
        assertThat(campo).isEqualTo(JUSTIFICACION);
        guardar(borrador(null, null, "El proyecto cumple los criterios de viabilidad."));
    }

    @Entonces("el Sistema muestra el mensaje {string} con el texto {string}")
    public void sistemaMuestraMensajeConTexto(String mensaje, String texto) {
        assertThat(mensaje).isEqualTo(MENSAJE_EMISION);
        assertThat(texto).isEqualTo(TEXTO_ELEGIBILIDAD);
        assertThat(emision.getElegibilidadHabilitada()).isTrue();
    }

    @Entonces("el mensaje muestra los botones {string} y {string}")
    public void mensajeMuestraBotones(String primero, String segundo) {
        assertThat(List.of(primero, segundo)).containsExactly(BOTON_IR_ELEGIBILIDAD, "Salir");
        assertThat(emision.getElegibilidadHabilitada()).isTrue();
    }

    @Entonces("el Sistema notifica al Técnico URP que se ha emitido Viabilidad al proyecto")
    public void sistemaNotificaEmision() {
        verify(notificaciones).notificarEmisionViabilidad(
                argThat(p -> p.getId().equals(proyecto.getId())),
                argThat(u -> u.getId().equals(tecnicoUrp.getId())));
    }

    @Entonces("el Sistema deshabilita la pantalla del Anexo A.1")
    public void sistemaDeshabilitaAnexoA1() {
        AccionesDisponiblesViabilidadDto acciones = fichaComo(viabilizador).getAccionesDisponibles();
        assertThat(acciones.getGuardarComentarios()).isFalse();
        assertThat(acciones.getEnviarComentarios()).isFalse();
        assertThat(acciones.getEmitirViabilidad()).isFalse();
        assertThat(fichaComo(tecnicoUrp).getAccionesDisponibles().getSolicitarViabilidad()).isFalse();
        capturar(() -> guardar(borrador(null, null, "Cambio posterior")));
        assertThat(error).isInstanceOf(ConflictoEstadoException.class);
        assertThat(((ConflictoEstadoException) error).getCodigo()).isEqualTo("FICHA_VIABILIDAD_DESHABILITADA");
    }

    @Entonces("el Sistema permite la edición de la pantalla {string}")
    public void sistemaPermiteEdicionElegibilidad(String pantalla) {
        assertThat(pantalla).isEqualTo("Elegibilidad");
        assertThat(revisionActual().getHabilitaElegibilidad()).isTrue();
    }

    @Dado("que el Viabilizador emitió la Viabilidad del proyecto por primera vez")
    public void viabilizadorEmitioPorPrimeraVez() {
        emision = emitirViabilidadCompleta();
    }

    @Dado("el Sistema muestra el mensaje del Anexo A.2 con los botones {string} y {string}")
    public void sistemaMuestraAnexoA2(String primero, String segundo) {
        mensajeMuestraBotones(primero, segundo);
    }

    @Cuando("el Viabilizador selecciona el botón {string}")
    public void viabilizadorSeleccionaBoton(String opcion) {
        opcionSeleccionada = opcion;
        ficha = fichaComo(viabilizador);
    }

    @Entonces("el Sistema envía al usuario a CU-PRE-25 {string}")
    public void sistemaEnviaAElegibilidad(String pantalla) {
        assertThat(pantalla).isEqualTo("Elegibilidad");
        assertThat(opcionSeleccionada).isEqualTo(BOTON_IR_ELEGIBILIDAD);
        assertThat(ficha.getAccionesDisponibles().getIrAElegibilidad()).isTrue();
    }

    @Entonces("el Sistema queda en la pantalla del Anexo A.1")
    public void sistemaQuedaEnAnexoA1() {
        assertThat(opcionSeleccionada).isEqualTo("Salir");
        assertThat(ficha.getProyectoId()).isEqualTo(proyecto.getId());
        assertThat(ficha.getAccionesDisponibles().getGuardarComentarios()).isFalse();
    }

    @Dado("que el proyecto ya pasó por un proceso de Elegibilidad")
    public void proyectoYaPasoPorElegibilidad() {
        elegibilidades.save(Elegibilidad.builder()
                .proyecto(proyecto)
                .resultado(ResultadoElegibilidad.ELEGIBLE)
                .fechaEvaluacion(LocalDateTime.now(ZONA).minusMonths(1))
                .build());
        tecnicoSolicitoViabilidad();
    }

    @Entonces("el Sistema no habilita CU-PRE-25 {string}")
    public void sistemaNoHabilitaElegibilidad(String pantalla) {
        assertThat(pantalla).isEqualTo("Elegibilidad");
        assertThat(emision.getElegibilidadHabilitada()).isFalse();
        assertThat(fichaComo(viabilizador).getAccionesDisponibles().getIrAElegibilidad()).isFalse();
    }

    @Dado("que la Viabilidad de un proyecto está en gestión en la pantalla del Anexo A.1")
    public void viabilidadEnGestion() {
        tecnicoSolicitoViabilidad();
        guardar(borrador(null, null, "Justificación registrada."));
        // Para el Viabilizador ambos botones de emisión ya estarían disponibles o se habilitarían.
        assertThat(fichaComo(viabilizador).getAccionesDisponibles().getEmitirViabilidad()).isTrue();
    }

    // =============================================================================================
    // Acciones del flujo

    private void cargarDocumentoPreinversion() {
        autenticar(tecnicoUrp);
        MockMultipartFile archivo = new MockMultipartFile("archivo", "preinversion.pdf", "application/pdf",
                "%PDF-1.4 documento".getBytes(StandardCharsets.UTF_8));
        enTransaccion(() -> service.cargarDocumento(proyecto.getId(), TipoDocumentoViabilidad.DOCUMENTO_PREINVERSION,
                archivo));
    }

    private void solicitar() {
        autenticar(tecnicoUrp);
        transacciones.executeWithoutResult(estado -> service.solicitarViabilidad(proyecto.getId()));
    }

    private void guardar(GuardarComentariosViabilidadRequestDto request) {
        autenticar(viabilizador);
        enTransaccion(() -> service.guardarComentarios(proyecto.getId(), request));
    }

    private void enviar() {
        autenticar(viabilizador);
        enTransaccion(() -> service.enviarComentarios(proyecto.getId()));
    }

    private EmitirViabilidadResponseDto emitir() {
        autenticar(viabilizador);
        return enTransaccion(() -> service.emitirViabilidad(proyecto.getId()));
    }

    private void devolverProyecto(String comentario) {
        cargarDocumentoPreinversion();
        solicitar();
        guardar(borrador(comentario, null, "Devolución para ajustes."));
        enviar();
    }

    private EmitirViabilidadResponseDto emitirViabilidadCompleta() {
        cargarDocumentoPreinversion();
        solicitar();
        guardar(borrador(null, null, "El proyecto cumple los criterios de viabilidad."));
        return emitir();
    }

    /** OT observada posterior a la emisión, con un comentario respondido o pendiente (RN11). */
    private void registrarOtObservada(boolean respondida) {
        OpinionTecnica ot = opinionesTecnicas.save(OpinionTecnica.builder()
                .proyecto(proyecto)
                .resultado(ResultadoOpinionTecnica.OBSERVADO)
                .fechaEmision(LocalDateTime.now(ZONA).plusMinutes(1))
                .observaciones("Ajustar el presupuesto")
                .tecnicoResponsable(viabilizador)
                .build());
        comentariosOt.save(ComentarioOpinionTecnica.builder()
                .opinionTecnica(ot)
                .comentario("Revisar el costo del producto 1")
                .justificacionInstitucion(respondida ? "Se ajustó el costo según cotizaciones" : null)
                .build());
    }

    private RuntimeException intentarEditarIdentificacion() {
        autenticar(tecnicoUrp);
        try {
            identificacionService.guardar(proyecto.getId(), new IdentificacionRequestDto().objetivoGeneral(OBJETIVO_GENERAL));
            return null;
        } catch (RuntimeException ex) {
            return ex;
        }
    }

    private FichaViabilidadResponseDto fichaComo(Usuario usuario) {
        autenticar(usuario);
        return enTransaccion(() -> service.consultarFicha(proyecto.getId()));
    }

    /**
     * El servicio se construye a mano (para inyectarle el mock de notificaciones), así que no tiene el
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

    private void sembrarFicha() {
        if (identificaciones.findByProyectoId(proyecto.getId()).isPresent()) {
            return;
        }
        identificaciones.save(Identificacion.builder().proyecto(proyecto).objetivoGeneral(OBJETIVO_GENERAL).build());
        descripciones.save(DescripcionTecnica.builder().proyecto(proyecto).descripcion(DESCRIPCION).build());
        componentes.save(Componente.builder().proyecto(proyecto).nombre(PRODUCTO).build());
        AnalisisPoblacion poblacion = AnalisisPoblacion.builder().proyecto(proyecto).build();
        poblacion.getUbicacionesObjetivo().add(new CeldaUbicacionPoblacion("San Salvador", 10_000));
        poblacion.getUbicacionesObjetivo().add(new CeldaUbicacionPoblacion("Soyapango", 2_500));
        poblaciones.save(poblacion);
        PresupuestoProyecto presupuesto = PresupuestoProyecto.builder().proyecto(proyecto)
                .fuenteRecursos("Fondo General de la Nación").build();
        presupuesto.getFuentesFinanciamiento().add(FuenteFinanciamiento.FONDO_GENERAL);
        presupuesto = presupuestos.save(presupuesto);
        macroactividades.save(MacroactividadPresupuesto.builder().presupuesto(presupuesto).numeroProducto(1)
                .nombre("Movimiento de tierras")
                .insumosJson("[{\"tipoInsumo\":\"MO\",\"costosPorPeriodo\":[" + INVERSION + "]}]").build());
        configuracionesOm.save(PresupuestoOmConfiguracion.builder().proyecto(proyecto).tipoCosto("OPERACION")
                .vidaUtil(3).tasaCrecimientoCostos(0D).build());
        ActividadOm actividad = ActividadOm.builder().proyecto(proyecto).tipoCostoTabla("OPERACION")
                .nombreActividad("Vigilancia").build();
        actividad.getInsumos().add(new InsumoActividad("MO", "Mano de obra", 1D, COSTO_OPERACION));
        actividadesOm.save(actividad);
        indicadores.save(IndicadorEvaluacion.builder().proyecto(proyecto).tipoIndicador(TipoIndicador.VAN)
                .valor(BigDecimal.valueOf(VAN)).fechaCalculo(LocalDateTime.now(ZONA)).build());
    }

    private static String codigoOrigen(String campo) {
        return switch (campo) {
            case "CUP", "Nombre del proyecto" -> "CU-PRE-01";
            case "Objetivo General" -> "CU-PRE-04";
            case "Descripción" -> "CU-PRE-11";
            case "Productos" -> "CU-PRE-23";
            case "Población objetivo" -> "CU-PRE-07";
            case "Inversión estimada", "Resumen del presupuesto", "Fuente de financiamiento" -> "CU-PRE-17";
            case "Costo de operación", "Costo de mantenimiento" -> "CU-PRE-18";
            case "Indicadores de evaluación" -> "CU-PRE-21";
            default -> throw new IllegalArgumentException("Campo no reconocido: " + campo);
        };
    }

    private static String propiedadDe(String campo) {
        return switch (campo) {
            case "CUP" -> "cup";
            case "Nombre del proyecto" -> "nombreProyecto";
            case "Objetivo General" -> "objetivoGeneral";
            case "Descripción" -> "descripcion";
            case "Productos" -> "productos";
            case "Población objetivo" -> "poblacionObjetivo";
            case "Inversión estimada" -> "inversionEstimada";
            case "Resumen del presupuesto" -> "resumenPresupuesto";
            case "Costo de operación" -> "costoOperacion";
            case "Costo de mantenimiento" -> "costoMantenimiento";
            case "Fuente de financiamiento" -> "fuenteFinanciamiento";
            case "Indicadores de evaluación" -> "indicadoresEvaluacion";
            default -> throw new IllegalArgumentException("Campo no reconocido: " + campo);
        };
    }

    private Object valorDe(String campo) {
        return switch (campo) {
            case "CUP" -> ficha.getCup();
            case "Nombre del proyecto" -> ficha.getNombreProyecto();
            case "Objetivo General" -> ficha.getObjetivoGeneral();
            case "Descripción" -> ficha.getDescripcion();
            case "Productos" -> ficha.getProductos();
            case "Población objetivo" -> ficha.getPoblacionObjetivo();
            case "Inversión estimada" -> ficha.getInversionEstimada();
            case "Resumen del presupuesto" -> ficha.getResumenPresupuesto().get("total");
            case "Costo de operación" -> ficha.getCostoOperacion();
            case "Costo de mantenimiento" -> ficha.getCostoMantenimiento();
            case "Fuente de financiamiento" -> ficha.getFuenteFinanciamiento();
            case "Indicadores de evaluación" -> ficha.getIndicadoresEvaluacion().stream()
                    .filter(i -> "VAN".equals(i.getNombre())).map(IndicadorEvaluacionDto::getValor).findFirst().orElse(null);
            default -> throw new IllegalArgumentException("Campo no reconocido: " + campo);
        };
    }

    /**
     * Valor que debe mostrar la ficha con los datos sembrados. CU-PRE-18 solo tiene costos de
     * operación, así que el Año 1 de mantenimiento llega en cero.
     */
    private Object valorEsperado(String campo) {
        return switch (campo) {
            case "CUP" -> proyecto.getCup();
            case "Nombre del proyecto" -> proyecto.getNombre();
            case "Objetivo General" -> OBJETIVO_GENERAL;
            case "Descripción" -> DESCRIPCION;
            case "Productos" -> List.of(PRODUCTO);
            case "Población objetivo" -> PERSONAS;
            case "Inversión estimada", "Resumen del presupuesto" -> BigDecimal.valueOf(INVERSION);
            case "Costo de operación" -> BigDecimal.valueOf(COSTO_OPERACION);
            case "Costo de mantenimiento" -> BigDecimal.valueOf(0D);
            case "Fuente de financiamiento" -> Map.of("fuentesFinanciamiento", List.of("FONDO_GENERAL"),
                    "fuenteRecursos", "Fondo General de la Nación");
            case "Indicadores de evaluación" -> BigDecimal.valueOf(VAN);
            default -> throw new IllegalArgumentException("Campo no reconocido: " + campo);
        };
    }

    private static Boolean accion(String boton, FichaViabilidadResponseDto vista) {
        AccionesDisponiblesViabilidadDto acciones = vista.getAccionesDisponibles();
        return switch (boton) {
            case BOTON_SOLICITAR -> acciones.getSolicitarViabilidad();
            case BOTON_GUARDAR -> acciones.getGuardarComentarios();
            case BOTON_ENVIAR -> acciones.getEnviarComentarios();
            case BOTON_EMITIR -> acciones.getEmitirViabilidad();
            case BOTON_IR_ELEGIBILIDAD -> acciones.getIrAElegibilidad();
            default -> throw new IllegalArgumentException("Botón no reconocido: " + boton);
        };
    }

    /** RN06/RN07: "Solicitar Viabilidad" es del Técnico URP; el resto de botones, del Viabilizador. */
    private Usuario actorDelBoton(String boton) {
        return BOTON_SOLICITAR.equals(boton) ? tecnicoUrp : viabilizador;
    }

    private RevisionViabilidad revisionActual() {
        return revisiones.findFirstByProyectoIdOrderByNumeroDesc(proyecto.getId()).orElseThrow();
    }

    private Proyecto proyectoActual() {
        return proyectos.findById(proyecto.getId()).orElseThrow();
    }

    private static GuardarComentariosViabilidadRequestDto borrador(String objetivo, String productos,
            String observaciones) {
        GuardarComentariosViabilidadRequestDto request = new GuardarComentariosViabilidadRequestDto();
        if (objetivo != null) {
            request.addComentariosViabilizadorItem(
                    new ComentarioCampoViabilidadDto(CampoFichaViabilidadDto.OBJETIVO_GENERAL, objetivo));
        }
        if (productos != null) {
            request.addComentariosViabilizadorItem(
                    new ComentarioCampoViabilidadDto(CampoFichaViabilidadDto.PRODUCTOS, productos));
        }
        request.setObservacionesGeneralesJustificacion(observaciones);
        return request;
    }

    private static Usuario usuario(String nombreUsuario, RolUsuario rol, UnidadEjecutora unidad, Institucion institucion) {
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
