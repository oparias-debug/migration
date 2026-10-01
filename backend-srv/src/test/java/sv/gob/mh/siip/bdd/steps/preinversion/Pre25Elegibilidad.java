package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
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
import sv.gob.mh.siip.model.preinversion.domain.CalificacionCriterioElegibilidad;
import sv.gob.mh.siip.model.preinversion.domain.ComentarioOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.CriterioElegibilidad;
import sv.gob.mh.siip.model.preinversion.domain.EjePlanGobierno;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.EntradaCatalogoEspecificar;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.CriterioElegibilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.EmitirElegibilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaElegibilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarCalificacionElegibilidadRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarCalificacionElegibilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.OpcionCatalogoDto;
import sv.gob.mh.siip.model.preinversion.dto.RespuestaCriterioElegibilidadRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoEspecificarDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.enums.TipoCatalogoEspecificar;
import sv.gob.mh.siip.model.preinversion.enums.TipoEspecificar;
import sv.gob.mh.siip.model.preinversion.repository.CalificacionCriterioElegibilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.ComentarioOpinionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.CriterioElegibilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.EjePlanGobiernoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ElegibilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.EntradaCatalogoEspecificarRepository;
import sv.gob.mh.siip.model.preinversion.repository.OpinionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.CriteriosFichaElegibilidad;
import sv.gob.mh.siip.model.preinversion.service.ElegibilidadAcceso;
import sv.gob.mh.siip.model.preinversion.service.ElegibilidadCalificacion;
import sv.gob.mh.siip.model.preinversion.service.ElegibilidadContexto;
import sv.gob.mh.siip.model.preinversion.service.ElegibilidadEmision;
import sv.gob.mh.siip.model.preinversion.service.ElegibilidadService;
import sv.gob.mh.siip.model.preinversion.service.ElegibilidadServiceImpl;
import sv.gob.mh.siip.model.preinversion.service.FiltrosPosterioresViabilidad;
import sv.gob.mh.siip.model.preinversion.service.NotificacionService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;
import sv.gob.mh.siip.security.ActorContexto;
import sv.gob.mh.siip.security.AutenticacionDePrueba;

/**
 * Steps BDD de CU-PRE-25 "Elegibilidad" (HU-PRE-25-01 calificar criterios, HU-PRE-25-02 emitir,
 * HU-PRE-25-03 atender comentarios de OT).
 *
 * <p>Se ejercita {@link ElegibilidadServiceImpl} con los repositorios y colaboradores reales del
 * contexto Spring de pruebas; solo la notificación se sustituye por un mock para poder verificar a
 * quién se envía. Los pasos que describen presentación (mensajes emergentes, links, navegación a
 * CU-PRE-26) verifican el dato del backend que la pantalla necesita para mostrarlos.
 *
 * <p>Cuatro textos de estos escenarios coinciden con pasos de CU-PRE-24 y Cucumber admite una sola
 * definición por texto: los define {@link Pre24Viabilidad}, que delega aquí cuando {@link #activo()}.
 * Las acciones de CU-PRE-26 "Opinión Técnica" se simulan registrando la OT "Observado" y sus
 * comentarios directamente en los repositorios; los escenarios de CU-PRE-26 las ejercitan de verdad
 * en {@link Pre26OpinionTecnica}.
 */
public class Pre25Elegibilidad {

  private static final ZoneId ZONA = ZoneId.of("America/El_Salvador");

  private static final String BOTON_GUARDAR = "Guardar";
  private static final String BOTON_EMITIR = "Emitir Elegibilidad";
  private static final String BOTON_CONSULTA_ODS = "Consulta ODS";
  private static final String BOTON_VER_COMENTARIOS_OT = "Ver comentarios OT";
  private static final String COLUMNA_APLICA = "¿Aplica?";
  private static final String COLUMNA_ESPECIFICAR = "Especificar";
  private static final String OPINION_TECNICA = "Opinión Técnica";
  private static final String RESPUESTA_INSTITUCION = "Respuesta Institución";

  /** RN06: link del botón "Consulta ODS". */
  private static final String LINK_ODS =
      "https://www.un.org/sustainabledevelopment/es/objetivos-de-desarrollo-sostenible/";

  /** Textos de los mensajes que el cliente muestra según la respuesta del backend (Anexos A.2 y A.3). */
  private static final String MENSAJE_GUARDADO = "¡Guardado!";
  private static final String TEXTO_GUARDADO = "Sus datos han sido guardados exitosamente.";

  // Criterios de la dimensión "1. Alineación estratégica" (Anexo A.1 del PDF de AGOSTO 2026).
  private static final String DIMENSION = "Alineación estratégica";
  private static final String CRITERIO_ODS = "Contribución a metas Objetivos de Desarrollo Sostenible";
  private static final String CRITERIO_PLAN_GOBIERNO = "Coherencia con Plan de Gobierno";
  private static final String CRITERIO_PLANES_REGIONALES = "Contribución a Planes Regionales";
  private static final String CRITERIO_PLANES_SECTORIALES = "Contribución a Planes Sectoriales o Institucionales";
  private static final String ODS_FIN_POBREZA = "Fin de la pobreza";

  private final InstitucionRepository instituciones;
  private final UnidadEjecutoraRepository unidades;
  private final UsuarioRepository usuarios;
  private final ProyectoRepository proyectos;
  private final MacroSectorRepository macrosectores;
  private final SectorActividadRepository sectores;
  private final EjeTematicoRepository ejes;
  private final CriterioElegibilidadRepository criterios;
  private final EntradaCatalogoEspecificarRepository entradas;
  private final EjePlanGobiernoRepository ejesPlanGobierno;
  private final CalificacionCriterioElegibilidadRepository calificaciones;
  private final ElegibilidadRepository elegibilidades;
  private final OpinionTecnicaRepository opinionesTecnicas;
  private final ComentarioOpinionTecnicaRepository comentariosOt;
  private final FiltrosPosterioresViabilidad filtros;
  private final CriteriosFichaElegibilidad criteriosFicha;
  private final ActorContexto actorContexto;
  private final TransactionTemplate transacciones;

  private boolean activo;
  /** Pasos compartidos con CU-PRE-26.5: delegan cuando {@link Pre265Priorizacion#activo()}. */
  @Autowired
  private Pre265Priorizacion priorizacion;
  private NotificacionService notificaciones;
  private ElegibilidadService service;
  private Proyecto proyecto;
  private Usuario viabilizador;
  private Usuario tecnicoUrp;
  private Usuario tecnicoPre;
  private Usuario tecnicoSymp;
  private FichaElegibilidadResponseDto ficha;
  private Usuario actorFicha;
  /** Lo que el Viabilizador tiene diligenciado en el formulario, por criterio (aún sin guardar). */
  private final Map<Long, RespuestaCriterioElegibilidadRequestDto> borrador = new LinkedHashMap<>();
  private Long criterioSeleccionado;
  private GuardarCalificacionElegibilidadResponseDto guardado;
  private EmitirElegibilidadResponseDto emision;
  private RuntimeException error;
  private String botonPulsado;
  private int minutosOt;
  private OpinionTecnica otAnterior;
  private OpinionTecnica otActual;

  public Pre25Elegibilidad(InstitucionRepository instituciones, UnidadEjecutoraRepository unidades,
      UsuarioRepository usuarios, ProyectoRepository proyectos, MacroSectorRepository macrosectores,
      SectorActividadRepository sectores, EjeTematicoRepository ejes, CriterioElegibilidadRepository criterios,
      EntradaCatalogoEspecificarRepository entradas, EjePlanGobiernoRepository ejesPlanGobierno,
      CalificacionCriterioElegibilidadRepository calificaciones, ElegibilidadRepository elegibilidades,
      OpinionTecnicaRepository opinionesTecnicas, ComentarioOpinionTecnicaRepository comentariosOt,
      FiltrosPosterioresViabilidad filtros, CriteriosFichaElegibilidad criteriosFicha, ActorContexto actorContexto,
      PlatformTransactionManager transactionManager) {
    this.instituciones = instituciones;
    this.unidades = unidades;
    this.usuarios = usuarios;
    this.proyectos = proyectos;
    this.macrosectores = macrosectores;
    this.sectores = sectores;
    this.ejes = ejes;
    this.criterios = criterios;
    this.entradas = entradas;
    this.ejesPlanGobierno = ejesPlanGobierno;
    this.calificaciones = calificaciones;
    this.elegibilidades = elegibilidades;
    this.opinionesTecnicas = opinionesTecnicas;
    this.comentariosOt = comentariosOt;
    this.filtros = filtros;
    this.criteriosFicha = criteriosFicha;
    this.actorContexto = actorContexto;
    this.transacciones = new TransactionTemplate(transactionManager);
  }

  @Before("@CU-PRE-25")
  public void prepararEscenario() {
    activo = true;
    String sufijo = SufijosPrueba.nuevo(8);
    Institucion institucion = instituciones.save(ProyectoFixtures.nuevaInstitucion("MH-CU25-" + sufijo,
        "Ministerio de Hacienda CU25"));
    UnidadEjecutora unidad = unidades.save(ProyectoFixtures.nuevaUnidadEjecutora("UE25-" + sufijo,
        "UE CU25", institucion));
    viabilizador = usuarios.save(usuario("viabilizador.pre25." + sufijo, RolUsuario.VIABILIZADOR, null, null));
    tecnicoUrp = usuarios.save(usuario("tecnico.urp.pre25." + sufijo, RolUsuario.TECNICO_URP, unidad, institucion));
    tecnicoPre = usuarios.save(usuario("tecnico.pre.pre25." + sufijo, RolUsuario.TECNICO_PRE, null, null));
    tecnicoSymp = usuarios.save(usuario("tecnico.symp.pre25." + sufijo, RolUsuario.TECNICO_SYMP, null, null));
    MacroSector macrosector = macrosectores.save(ProyectoFixtures.nuevoMacrosector("M25" + sufijo,
        "Macrosector CU25"));
    SectorActividad sector = sectores.save(ProyectoFixtures.nuevoSector("S25" + sufijo, "Sector CU25",
        macrosector));
    EjeTematico eje = ejes.save(ProyectoFixtures.nuevoEjeTematico("E25" + sufijo, "Eje CU25"));
    // Precondición: CU-PRE-24 ya emitió la Viabilidad por primera vez.
    proyecto = proyectos.save(ProyectoFixtures.nuevoProyecto("Proyecto CU25 " + sufijo, EstadoProyecto.VIABLE,
        unidad, institucion, sector, eje));
    sembrarCatalogos();

    notificaciones = mock(NotificacionService.class);
    service = new ElegibilidadServiceImpl(
        new ElegibilidadAcceso(actorContexto, proyectos, elegibilidades, filtros),
        criteriosFicha,
        calificaciones,
        new ElegibilidadCalificacion(calificaciones, criteriosFicha),
        new ElegibilidadEmision(proyectos, elegibilidades, calificaciones, usuarios, notificaciones, filtros));
  }

  @After("@CU-PRE-25")
  public void limpiarContexto() {
    RequestContextHolder.resetRequestAttributes();
  }

  /** @return si el escenario en curso es de CU-PRE-25 (ver pasos compartidos en {@link Pre24Viabilidad}) */
  boolean activo() {
    return activo;
  }

  // =============================================================================================
  // HU-PRE-25-01: Calificación de criterios

  @Dado("que el Viabilizador está en la pantalla {string}")
  public void viabilizadorEnPantalla(String pantalla) {
    assertThat(pantalla).isEqualTo("Captura de proyectos");
  }

  @Dado("la pantalla lista los proyectos de la entidad en el estado {string}")
  public void pantallaListaProyectosEnEstado(String estado) {
    assertThat(proyectoActual().getEstado().getEtiquetaUi()).isEqualToIgnoringCase(estado);
  }

  @Cuando("el Viabilizador da clic en el proyecto sobre el cual va a emitir elegibilidad")
  public void viabilizadorDaClicEnProyecto() {
    ficha = fichaComo(viabilizador);
  }

  @Entonces("el Sistema muestra el formulario del Anexo A.1")
  public void sistemaMuestraFormulario() {
    assertThat(ficha.getProyectoId()).isEqualTo(proyecto.getId());
    assertThat(ficha.getDimensiones()).anySatisfy(d -> {
      assertThat(d.getNombre()).isEqualTo(DIMENSION);
      assertThat(d.getCriterios()).extracting(CriterioElegibilidadDto::getNombre)
          .contains(CRITERIO_ODS, CRITERIO_PLAN_GOBIERNO, CRITERIO_PLANES_REGIONALES, CRITERIO_PLANES_SECTORIALES);
    });
    assertThat(ficha.getAccionesDisponibles().getGuardarCalificacion()).isTrue();
  }

  @Dado("que el Viabilizador está en el formulario del Anexo A.1 de un proyecto")
  public void viabilizadorEnFormulario() {
    ficha = fichaComo(viabilizador);
    assertThat(ficha.getAccionesDisponibles().getGuardarCalificacion()).isTrue();
  }

  @Cuando("el Viabilizador selecciona en la columna {string} el criterio {string}")
  public void viabilizadorSeleccionaCriterio(String columna, String criterio) {
    assertThat(columna).isEqualTo(COLUMNA_APLICA);
    seleccionar(criterio);
  }

  @Dado("seleccionó en la columna {string} el criterio {string}")
  public void viabilizadorSeleccionoCriterio(String columna, String criterio) {
    viabilizadorSeleccionaCriterio(columna, criterio);
  }

  @Entonces("el Viabilizador puede responder en la columna {string} la pregunta {string} mediante {}")
  public void viabilizadorPuedeResponder(String columna, String pregunta, String tipo) {
    assertThat(columna).isEqualTo(COLUMNA_ESPECIFICAR);
    CriterioElegibilidadDto criterio = criterioDeFicha(criterioSeleccionado);
    assertThat(criterio.getPregunta()).isEqualTo(pregunta);
    RespuestaCriterioElegibilidadRequestDto respuesta = borrador.get(criterioSeleccionado);
    if (tipo.startsWith("selección del listado del catálogo ")) {
      assertThat(criterio.getTipoEspecificar()).isEqualTo(TipoEspecificarDto.LISTADO_CATALOGO);
      assertThat(criterio.getCodigoCatalogo()).isEqualTo(tipo.substring("selección del listado del catálogo ".length()));
      assertThat(criterio.getOpciones()).isNotEmpty();
      respuesta.setEspecificarCodigosOpcion(new ArrayList<>(List.of(criterio.getOpciones().get(0).getCodigo())));
    } else {
      assertThat(tipo).isEqualTo("texto libre");
      assertThat(criterio.getTipoEspecificar()).isEqualTo(TipoEspecificarDto.TEXTO_LIBRE);
      respuesta.setEspecificarTexto("Plan de desarrollo de la región oriental");
    }
    // La respuesta así diligenciada se acepta al guardar.
    capturar(this::guardar);
    assertThat(error).isNull();
  }

  @Cuando("el Viabilizador selecciona {string} y {string} en el listado de la columna {string}")
  public void viabilizadorSeleccionaElementos(String primero, String segundo, String columna) {
    assertThat(columna).isEqualTo(COLUMNA_ESPECIFICAR);
    CriterioElegibilidadDto criterio = criterioDeFicha(criterioSeleccionado);
    assertThat(criterio.getSeleccionMultiple()).isTrue();
    borrador.get(criterioSeleccionado).setEspecificarCodigosOpcion(
        new ArrayList<>(List.of(codigoOpcion(criterio, primero), codigoOpcion(criterio, segundo))));
    capturar(this::guardar);
  }

  @Entonces("el Sistema mantiene ambos elementos seleccionados para el criterio")
  public void sistemaMantieneAmbosElementos() {
    assertThat(error).isNull();
    CriterioElegibilidadDto criterio = criterioDeFicha(fichaComo(viabilizador), criterioSeleccionado);
    assertThat(criterio.getRespuesta()).isNotNull();
    assertThat(criterio.getRespuesta().getEspecificarOpciones()).hasSize(2);
  }

  @Entonces("el Sistema lo lleva al link {string}")
  public void sistemaLlevaAlLink(String link) {
    assertThat(botonPulsado).isEqualTo(BOTON_CONSULTA_ODS);
    assertThat(link).isEqualTo(LINK_ODS);
    // El botón acompaña al criterio que se responde con el catálogo C.1 (ODS).
    assertThat(ficha.getDimensiones()).flatExtracting(d -> d.getCriterios())
        .extracting(CriterioElegibilidadDto::getCodigoCatalogo).contains("C.1");
  }

  @Dado("completó la columna {string} de todos los criterios seleccionados en {string}")
  public void completoEspecificar(String columna, String columnaAplica) {
    assertThat(columna).isEqualTo(COLUMNA_ESPECIFICAR);
    assertThat(columnaAplica).isEqualTo(COLUMNA_APLICA);
    diligenciarCompleto();
  }

  @Entonces("el Sistema muestra el mensaje emergente {string} con el texto {string}")
  public void sistemaMuestraMensajeGuardado(String mensaje, String texto) {
    assertThat(error).isNull();
    assertThat(guardado).isNotNull();
    assertThat(mensaje).isEqualTo(MENSAJE_GUARDADO);
    assertThat(texto).isEqualTo(TEXTO_GUARDADO);
  }

  @Dado("que el Sistema muestra el mensaje emergente {string} del Anexo A.2")
  public void sistemaMostroMensajeGuardado(String mensaje) {
    viabilizadorEnFormulario();
    diligenciarCompleto();
    viabilizadorDaClic(BOTON_GUARDAR);
    sistemaMuestraMensajeGuardado(mensaje, TEXTO_GUARDADO);
  }

  @Cuando("el Viabilizador da clic en {string}")
  public void viabilizadorDaClicEnMensaje(String boton) {
    // "Aceptar" solo cierra el mensaje del Anexo A.2: la calificación ya se guardó al pulsar "Guardar".
    assertThat(boton).isEqualTo("Aceptar");
  }

  @Entonces("el Sistema guarda la información registrada")
  public void sistemaGuardaInformacion() {
    if (priorizacion.activo()) {
      priorizacion.sistemaGuardaInformacion();
      return;
    }
    List<CalificacionCriterioElegibilidad> guardadas = calificacionesDelProyecto();
    assertThat(guardadas).extracting(c -> c.getCriterio().getId())
        .containsExactlyInAnyOrderElementsOf(borrador.keySet());
    assertThat(guardadas).allSatisfy(c -> assertThat(c.getAplica()).isTrue());
  }

  @Entonces("el Sistema se mantiene en la pantalla {string}")
  public void sistemaSeMantieneEnPantalla(String pantalla) {
    assertThat(pantalla).isEqualTo("Elegibilidad");
    assertThat(guardado.getAccionesDisponibles().getGuardarCalificacion()).isTrue();
    assertThat(guardado.getAccionesDisponibles().getEmitirElegibilidad()).isTrue();
  }

  @Dado("no completó la columna {string} de ese criterio")
  public void noCompletoEspecificar(String columna) {
    assertThat(columna).isEqualTo(COLUMNA_ESPECIFICAR);
    RespuestaCriterioElegibilidadRequestDto respuesta = borrador.get(criterioSeleccionado);
    assertThat(respuesta.getEspecificarTexto()).isNull();
    assertThat(respuesta.getEspecificarCodigosOpcion()).isEmpty();
  }

  @Dado("un proyecto con el formulario del Anexo A.1 disponible")
  public void proyectoConFormularioDisponible() {
    assertThat(fichaComo(viabilizador).getAccionesDisponibles().getGuardarCalificacion()).isTrue();
  }

  @Cuando("el {} accede al formulario del Anexo A.1 del proyecto")
  public void rolAccedeAlFormulario(String rol) {
    actorFicha = usuarioDelRol(rol);
    ficha = fichaComo(actorFicha);
  }

  @Entonces("el botón {string} no es visible para el {}")
  public void botonNoVisible(String boton, String rol) {
    assertThat(boton).isEqualTo(BOTON_GUARDAR);
    assertThat(actorFicha).isEqualTo(usuarioDelRol(rol));
    assertThat(ficha.getAccionesDisponibles().getGuardarCalificacion()).isFalse();
    assertThat(ficha.getAccionesDisponibles().getEmitirElegibilidad()).isFalse();
  }

  @Entonces("el {} no puede registrar información en las columnas {string} y {string}")
  public void rolNoPuedeRegistrar(String rol, String columnaAplica, String columnaEspecificar) {
    assertThat(columnaAplica).isEqualTo(COLUMNA_APLICA);
    assertThat(columnaEspecificar).isEqualTo(COLUMNA_ESPECIFICAR);
    diligenciarCompleto();
    Usuario usuario = usuarioDelRol(rol);
    capturar(() -> {
      autenticar(usuario);
      enTransaccion(() -> service.guardarCalificacion(proyecto.getId(), solicitud()));
    });
    assertThat(error).isInstanceOf(AccesoDenegadoException.class);
    assertThat(calificacionesDelProyecto()).isEmpty();
  }

  // =============================================================================================
  // HU-PRE-25-02: Emisión de Elegibilidad

  @Dado("que el Viabilizador está en el formulario del Anexo A.1 de un proyecto en estado {string}")
  public void viabilizadorEnFormularioConEstado(String estado) {
    viabilizadorEnFormulario();
    assertThat(ficha.getEstadoProyecto()).isEqualToIgnoringCase(estado);
  }

  @Dado("registró la información de los criterios a los que contribuye el proyecto")
  public void registroInformacionCriterios() {
    diligenciarCompleto();
    capturar(this::guardar);
    assertThat(error).isNull();
  }

  @Entonces("el Sistema muestra el mensaje de emisión de Elegibilidad del Anexo A.3")
  public void sistemaMuestraMensajeEmision() {
    assertThat(error).isNull();
    assertThat(emision).isNotNull();
    assertThat(emision.getProyectoId()).isEqualTo(proyecto.getId());
  }

  @Entonces("el mensaje muestra los botones {string} e {string}")
  public void mensajeMuestraBotones(String primero, String segundo) {
    assertThat(List.of(primero, segundo)).containsExactly("ACEPTAR", "IR A OPINIÓN TÉCNICA");
    assertThat(emision).isNotNull();
  }

  @Entonces("el Sistema notifica al Técnico URP y al Técnico PRE")
  public void sistemaNotificaUrpYPre() {
    verify(notificaciones).notificarEmisionElegibilidad(
        argThat(p -> p.getId().equals(proyecto.getId())),
        argThat(destinatarios -> contiene(destinatarios, tecnicoUrp) && contiene(destinatarios, tecnicoPre)));
  }

  @Entonces("el Sistema habilita el formulario para emitir OT")
  public void sistemaHabilitaFormularioOt() {
    // La Elegibilidad emitida es la que CU-PRE-24/26 consultan para seguir a la OT (RN03 de CU-PRE-24).
    assertThat(filtros.yaPasoPorElegibilidad(proyecto.getId())).isTrue();
    // "la pantalla 'Elegibilidad' queda deshabilitada" (FB1 paso 6).
    FichaElegibilidadResponseDto despues = fichaComo(viabilizador);
    assertThat(despues.getAccionesDisponibles().getGuardarCalificacion()).isFalse();
    assertThat(despues.getAccionesDisponibles().getEmitirElegibilidad()).isFalse();
  }

  // =============================================================================================
  // HU-PRE-25-03: Atención de comentarios de la OT

  @Dado("que el proyecto se envió a CU-PRE-26 {string} por primera vez")
  public void proyectoEnviadoAOtPorPrimeraVez(String cu) {
    assertThat(cu).isEqualTo(OPINION_TECNICA);
    emitirPorPrimeraVez();
  }

  @Dado("el Técnico PRE no ha dado clic en el botón {string} desde CU-PRE-26 {string}")
  public void tecnicoPreNoHaEnviadoComentarios(String boton, String cu) {
    assertThat(boton).isEqualTo("Enviar comentarios");
    assertThat(cu).isEqualTo(OPINION_TECNICA);
    assertThat(opinionesTecnicas.findFirstByProyectoIdOrderByFechaEmisionDesc(proyecto.getId())).isEmpty();
  }

  @Entonces("la pantalla del Anexo A.1 está bloqueada")
  public void pantallaBloqueada() {
    assertThat(ficha.getAccionesDisponibles().getGuardarCalificacion()).isFalse();
    assertThat(ficha.getAccionesDisponibles().getEmitirElegibilidad()).isFalse();
    capturar(this::guardar);
    assertThat(error).isInstanceOf(ConflictoEstadoException.class);
    assertThat(((ConflictoEstadoException) error).getCodigo())
        .isEqualTo(ElegibilidadContexto.FICHA_ELEGIBILIDAD_DESHABILITADA);
  }

  @Cuando("el Técnico PRE envía comentarios a la información registrada en la dimensión de la elegibilidad")
  public void tecnicoPreEnviaComentarios() {
    otActual = registrarOtObservada("Precisar a cuáles metas ODS contribuye el proyecto");
  }

  @Cuando("da clic en el botón {string} desde CU-PRE-26 {string}")
  public void daClicDesdeOpinionTecnica(String boton, String cu) {
    assertThat(boton).isEqualTo("Enviar comentarios");
    assertThat(cu).isEqualTo(OPINION_TECNICA);
    avisarComentariosOt();
  }

  /** Aviso del Anexo A2 h que CU-PRE-26 envía al Viabilizador del proyecto al enviar los comentarios. */
  private void avisarComentariosOt() {
    notificaciones.notificarComentariosOtElegibilidad(proyecto, List.of(viabilizador));
  }

  @Entonces("el Sistema notifica al Viabilizador que se emitieron comentarios")
  public void sistemaNotificaAlViabilizador() {
    verify(notificaciones).notificarComentariosOtElegibilidad(
        argThat(p -> p.getId().equals(proyecto.getId())),
        argThat(destinatarios -> contiene(destinatarios, viabilizador)));
  }

  @Entonces("el Sistema habilita los campos de la pantalla {string}")
  public void sistemaHabilitaCampos(String pantalla) {
    assertThat(pantalla).isEqualTo("Criterios de Elegibilidad del Proyecto");
    FichaElegibilidadResponseDto despues = fichaComo(viabilizador);
    assertThat(despues.getAccionesDisponibles().getGuardarCalificacion()).isTrue();
    assertThat(despues.getAccionesDisponibles().getEmitirElegibilidad()).isTrue();
  }

  @Dado("que el Técnico PRE envió comentarios a la Elegibilidad desde CU-PRE-26 {string}")
  public void tecnicoPreEnvioComentarios(String cu) {
    proyectoEnviadoAOtPorPrimeraVez(cu);
    tecnicoPreEnviaComentarios();
    daClicDesdeOpinionTecnica("Enviar comentarios", cu);
  }

  @Entonces("el Sistema presenta el formulario de CU-PRE-26 {string} para diligenciar el campo {string} y oprimir el botón {string}")
  public void sistemaPresentaFormularioOt(String cu, String campo, String boton) {
    assertThat(botonPulsado).isEqualTo(BOTON_VER_COMENTARIOS_OT);
    assertThat(cu).isEqualTo(OPINION_TECNICA);
    assertThat(campo).isEqualTo(RESPUESTA_INSTITUCION);
    assertThat(boton).isEqualTo("Guardar Ajustes");
    // Hay comentarios de la OT pendientes de responder en ese formulario (RN15).
    assertThat(filtros.tieneComentariosElegibilidadSinResponder(proyecto.getId())).isTrue();
  }

  @Dado("el Viabilizador ajustó la información en el formulario del Anexo A.1")
  public void viabilizadorAjustoInformacion() {
    seleccionar(CRITERIO_PLANES_SECTORIALES);
    borrador.get(criterioSeleccionado).setEspecificarTexto("Plan Institucional 2025-2029");
    capturar(this::guardar);
    assertThat(error).isNull();
  }

  @Dado("el Viabilizador respondió todos los comentarios en el campo {string} de CU-PRE-26 {string} y oprimió {string}")
  public void viabilizadorRespondioComentarios(String campo, String cu, String boton) {
    assertThat(campo).isEqualTo(RESPUESTA_INSTITUCION);
    assertThat(cu).isEqualTo(OPINION_TECNICA);
    assertThat(boton).isEqualTo("Guardar Ajustes");
    responderComentarios(otActual);
    assertThat(filtros.tieneComentariosElegibilidadSinResponder(proyecto.getId())).isFalse();
  }

  @Entonces("el Sistema permite emitir la Elegibilidad nuevamente")
  public void sistemaPermiteEmitirNuevamente() {
    assertThat(error).isNull();
    assertThat(emision).isNotNull();
    assertThat(cantidadEmisiones()).isEqualTo(2);
  }

  @Entonces("el Sistema notifica al Técnico PRE y al Técnico SYMP que fueron atendidas las observaciones a la Elegibilidad")
  public void sistemaNotificaPreYSymp() {
    verify(notificaciones).notificarObservacionesElegibilidadAtendidas(
        argThat(p -> p.getId().equals(proyecto.getId())),
        argThat(destinatarios -> contiene(destinatarios, tecnicoPre) && contiene(destinatarios, tecnicoSymp)));
  }

  @Dado("el Viabilizador no ha respondido todos los comentarios en el campo {string} de CU-PRE-26 {string}")
  public void viabilizadorNoRespondioComentarios(String campo, String cu) {
    assertThat(campo).isEqualTo(RESPUESTA_INSTITUCION);
    assertThat(cu).isEqualTo(OPINION_TECNICA);
    assertThat(filtros.tieneComentariosElegibilidadSinResponder(proyecto.getId())).isTrue();
  }

  @Entonces("el Sistema no permite generar la Elegibilidad nuevamente")
  public void sistemaNoPermiteEmitirNuevamente() {
    assertThat(error).isInstanceOf(ReglaNegocioException.class);
    assertThat(((ReglaNegocioException) error).getCodigo())
        .isEqualTo(ElegibilidadEmision.COMENTARIOS_OPINION_TECNICA_SIN_RESPONDER);
    assertThat(cantidadEmisiones()).isEqualTo(1);
  }

  @Dado("que el proyecto ya fue devuelto a elegibilidad previamente con comentarios de OT")
  public void proyectoDevueltoPreviamente() {
    tecnicoPreEnvioComentarios(OPINION_TECNICA);
    responderComentarios(otActual);
    viabilizadorDaClic(BOTON_EMITIR);
    assertThat(error).isNull();
    otAnterior = otActual;
  }

  @Cuando("el Técnico PRE envía nuevamente comentarios a la Elegibilidad desde CU-PRE-26 {string}")
  public void tecnicoPreEnviaNuevamenteComentarios(String cu) {
    assertThat(cu).isEqualTo(OPINION_TECNICA);
    otActual = registrarOtObservada("Justificar la coherencia con el Plan de Gobierno");
    avisarComentariosOt();
  }

  @Entonces("el Sistema guarda los comentarios emitidos en OT de esta devolución")
  public void sistemaGuardaComentariosDevolucion() {
    assertThat(comentariosDe(otActual)).containsExactly("Justificar la coherencia con el Plan de Gobierno");
    // Cada devolución vuelve a habilitar la ficha para ajustes (RN09, RN10).
    assertThat(fichaComo(viabilizador).getAccionesDisponibles().getGuardarCalificacion()).isTrue();
  }

  @Entonces("el Sistema conserva los comentarios de las devoluciones anteriores")
  public void sistemaConservaComentariosAnteriores() {
    assertThat(comentariosDe(otAnterior)).containsExactly("Precisar a cuáles metas ODS contribuye el proyecto");
  }

  // =============================================================================================
  // Pasos compartidos con CU-PRE-24 (definidos en Pre24Viabilidad)

  /** "el Viabilizador da clic en el botón {string}". */
  void viabilizadorDaClic(String boton) {
    botonPulsado = boton;
    switch (boton) {
      case BOTON_GUARDAR -> capturar(this::guardar);
      case BOTON_EMITIR -> capturar(() -> {
        autenticar(viabilizador);
        emision = enTransaccion(() -> service.emitirElegibilidad(proyecto.getId()));
      });
      // Solo navegan: "Consulta ODS" a la página de la ONU (RN06) y "Ver comentarios OT" a CU-PRE-26 (RN15).
      case BOTON_CONSULTA_ODS, BOTON_VER_COMENTARIOS_OT -> error = null;
      default -> throw new IllegalArgumentException("Botón no reconocido: " + boton);
    }
  }

  /** "el Sistema muestra el mensaje {string}": en CU-PRE-25 solo lo usan los rechazos (RN03, RN15). */
  void sistemaMuestraMensaje(String mensaje) {
    assertThat(error).isNotNull();
    assertThat(error.getMessage()).isEqualTo(mensaje);
    if (BOTON_GUARDAR.equals(botonPulsado)) {
      assertThat(error).isInstanceOf(ReglaNegocioException.class);
      assertThat(((ReglaNegocioException) error).getCodigo()).isEqualTo(ElegibilidadCalificacion.ESPECIFICAR_INCOMPLETO);
      assertThat(calificacionesDelProyecto()).isEmpty();
    }
  }

  /** "el Sistema cambia el estado del proyecto a {string}". */
  void sistemaCambiaEstado(String estado) {
    assertThat(error).isNull();
    assertThat(proyectoActual().getEstado().getEtiquetaUi()).isEqualToIgnoringCase(estado);
    assertThat(emision.getEstadoProyecto()).isEqualToIgnoringCase(estado);
  }

  /** "el Viabilizador accede a la pantalla del Anexo A.1 del proyecto". */
  void viabilizadorAccedeAAnexoA1() {
    ficha = fichaComo(viabilizador);
  }

  // =============================================================================================
  // Operaciones

  private void seleccionar(String nombreCriterio) {
    criterioSeleccionado = criterioDeFicha(nombreCriterio).getCriterioId();
    borrador.computeIfAbsent(criterioSeleccionado, id -> new RespuestaCriterioElegibilidadRequestDto(id, true));
  }

  /** Marca "¿Aplica?" en el ODS y en Planes Regionales y completa su "Especificar". */
  private void diligenciarCompleto() {
    if (ficha == null) {
      ficha = fichaComo(viabilizador);
    }
    seleccionar(CRITERIO_ODS);
    borrador.get(criterioSeleccionado).setEspecificarCodigosOpcion(
        new ArrayList<>(List.of(codigoOpcion(criterioDeFicha(CRITERIO_ODS), ODS_FIN_POBREZA))));
    seleccionar(CRITERIO_PLANES_REGIONALES);
    borrador.get(criterioSeleccionado).setEspecificarTexto("Plan de desarrollo de la región oriental");
  }

  private void emitirPorPrimeraVez() {
    viabilizadorEnFormulario();
    registroInformacionCriterios();
    viabilizadorDaClic(BOTON_EMITIR);
    assertThat(error).isNull();
  }

  private void guardar() {
    autenticar(viabilizador);
    guardado = enTransaccion(() -> service.guardarCalificacion(proyecto.getId(), solicitud()));
  }

  private GuardarCalificacionElegibilidadRequestDto solicitud() {
    return new GuardarCalificacionElegibilidadRequestDto(new ArrayList<>(borrador.values()));
  }

  private FichaElegibilidadResponseDto fichaComo(Usuario usuario) {
    autenticar(usuario);
    return enTransaccion(() -> service.consultarFicha(proyecto.getId()));
  }

  /**
   * OT "Observado" con un comentario sin responder. Cada una queda fechada después de la anterior y
   * de la última emisión, como ocurre al devolver el proyecto desde CU-PRE-26.
   */
  private OpinionTecnica registrarOtObservada(String comentario) {
    minutosOt++;
    OpinionTecnica ot = opinionesTecnicas.save(OpinionTecnica.builder()
        .proyecto(proyecto)
        .resultado(ResultadoOpinionTecnica.OBSERVADO)
        .fechaEmision(LocalDateTime.now(ZONA).plusMinutes(minutosOt))
        .observaciones("Comentarios a la Elegibilidad")
        .tecnicoResponsable(tecnicoPre)
        .build());
    comentariosOt.save(ComentarioOpinionTecnica.builder()
        .opinionTecnica(ot)
        .apartado(ComentarioOpinionTecnica.ELEGIBILIDAD)
        .comentario(comentario)
        .build());
    return ot;
  }

  private void responderComentarios(OpinionTecnica ot) {
    transacciones.executeWithoutResult(estado -> comentariosOt.findAll().stream()
        .filter(c -> c.getOpinionTecnica().getId().equals(ot.getId()))
        .forEach(c -> {
          c.setJustificacionInstitucion("Se precisaron las metas en la calificación de criterios");
          comentariosOt.save(c);
        }));
  }

  private List<String> comentariosDe(OpinionTecnica ot) {
    return enTransaccion(() -> comentariosOt.findAll().stream()
        .filter(c -> c.getOpinionTecnica().getId().equals(ot.getId()))
        .map(ComentarioOpinionTecnica::getComentario)
        .toList());
  }

  private List<CalificacionCriterioElegibilidad> calificacionesDelProyecto() {
    return enTransaccion(() -> {
      List<CalificacionCriterioElegibilidad> lista = calificaciones.findByProyectoId(proyecto.getId());
      lista.forEach(c -> c.getCriterio().getId());
      return lista;
    });
  }

  private long cantidadEmisiones() {
    return enTransaccion(() -> elegibilidades.findAll().stream()
        .filter(e -> e.getProyecto().getId().equals(proyecto.getId()))
        .count());
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

  /**
   * Criterios de la dimensión "1. Alineación estratégica" y opciones de los catálogos C.1 y C.2 con
   * los valores de los ejemplos del .feature. La base de pruebas es compartida entre escenarios: solo
   * se siembran la primera vez.
   */
  private void sembrarCatalogos() {
    criterio("BDD25-ELEG-01", 1, CRITERIO_ODS, "¿A cuáles ODS contribuye?", TipoEspecificar.CATALOGO,
        TipoCatalogoEspecificar.ODS);
    criterio("BDD25-ELEG-02", 2, CRITERIO_PLAN_GOBIERNO, "¿A cuál eje o pilar del Plan de Gobierno contribuye?",
        TipoEspecificar.CATALOGO, TipoCatalogoEspecificar.EJE_PLAN_GOBIERNO);
    criterio("BDD25-ELEG-03", 3, CRITERIO_PLANES_REGIONALES, "¿A cuál Plan Regional contribuye? Especifique",
        TipoEspecificar.TEXTO_LIBRE, null);
    criterio("BDD25-ELEG-04", 4, CRITERIO_PLANES_SECTORIALES,
        "¿A cuál Plan Institucional o Sectorial contribuye? Especifique", TipoEspecificar.TEXTO_LIBRE, null);
    ods("BDD25-ODS-01", ODS_FIN_POBREZA);
    ods("BDD25-ODS-02", "Hambre cero");
    ejePlanGobierno("B25-EJE-1", "Eje 1: Carreteras");
    ejePlanGobierno("B25-EJE-5", "Eje 5: Agua potable y saneamiento");
  }

  private void criterio(String codigo, int orden, String nombre, String pregunta, TipoEspecificar tipo,
      TipoCatalogoEspecificar catalogo) {
    if (criterios.findByCodigo(codigo).isEmpty()) {
      criterios.save(CriterioElegibilidad.builder().codigo(codigo).numeroDimension(1).dimension(DIMENSION)
          .orden(orden).criterio(nombre).pregunta(pregunta).tipoEspecificar(tipo).catalogoEspecificar(catalogo)
          .permiteSeleccionMultiple(catalogo != null).build());
    }
  }

  private void ods(String codigo, String nombre) {
    if (entradas.findByTipoAndCodigo(TipoCatalogoEspecificar.ODS, codigo).isEmpty()) {
      entradas.save(EntradaCatalogoEspecificar.builder().tipo(TipoCatalogoEspecificar.ODS).codigo(codigo)
          .nombre(nombre).build());
    }
  }

  private void ejePlanGobierno(String codigo, String nombre) {
    if (ejesPlanGobierno.findByCodigo(codigo).isEmpty()) {
      ejesPlanGobierno.save(EjePlanGobierno.builder().codigo(codigo).nombre(nombre).activo(true).build());
    }
  }

  private CriterioElegibilidadDto criterioDeFicha(String nombre) {
    if (ficha == null) {
      ficha = fichaComo(viabilizador);
    }
    return ficha.getDimensiones().stream()
        .flatMap(d -> d.getCriterios().stream())
        .filter(c -> c.getNombre().equals(nombre))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Criterio no reconocido: " + nombre));
  }

  private CriterioElegibilidadDto criterioDeFicha(Long criterioId) {
    return criterioDeFicha(ficha, criterioId);
  }

  private static CriterioElegibilidadDto criterioDeFicha(FichaElegibilidadResponseDto vista, Long criterioId) {
    return vista.getDimensiones().stream()
        .flatMap(d -> d.getCriterios().stream())
        .filter(c -> c.getCriterioId().equals(criterioId))
        .findFirst()
        .orElseThrow();
  }

  private static String codigoOpcion(CriterioElegibilidadDto criterio, String nombre) {
    return criterio.getOpciones().stream()
        .filter(o -> o.getNombre().equals(nombre))
        .map(OpcionCatalogoDto::getCodigo)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Opción no reconocida: " + nombre));
  }

  private Usuario usuarioDelRol(String rol) {
    return switch (rol) {
      case "Técnico URP" -> tecnicoUrp;
      case "Técnico PRE" -> tecnicoPre;
      case "Técnico SYMP" -> tecnicoSymp;
      default -> throw new IllegalArgumentException("Rol no reconocido: " + rol);
    };
  }

  private Proyecto proyectoActual() {
    return proyectos.findById(proyecto.getId()).orElseThrow();
  }

  private static boolean contiene(List<Usuario> destinatarios, Usuario usuario) {
    return destinatarios.stream().anyMatch(u -> u.getId().equals(usuario.getId()));
  }

  private static Usuario usuario(String nombreUsuario, RolUsuario rol, UnidadEjecutora unidad,
      Institucion institucion) {
    return Usuario.builder().nombreUsuario(nombreUsuario).nombreCompleto(nombreUsuario)
        .correo(nombreUsuario + "@example.com").rol(rol).unidadEjecutora(unidad).institucion(institucion)
        .activo(true).build();
  }

  private static void autenticar(Usuario usuario) {
    MockHttpServletRequest request = new MockHttpServletRequest();
    AutenticacionDePrueba.autenticar(usuario.getNombreUsuario());
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
  }
}
