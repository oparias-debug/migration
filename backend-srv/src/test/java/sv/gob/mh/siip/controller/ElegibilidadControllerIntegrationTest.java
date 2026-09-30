package sv.gob.mh.siip.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.ComentarioOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.CriterioElegibilidad;
import sv.gob.mh.siip.model.preinversion.domain.EjePlanGobierno;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.EntradaCatalogoEspecificar;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.enums.TipoCatalogoEspecificar;
import sv.gob.mh.siip.model.preinversion.enums.TipoEspecificar;
import sv.gob.mh.siip.model.preinversion.repository.ComentarioOpinionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.CriterioElegibilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.EjePlanGobiernoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EntradaCatalogoEspecificarRepository;
import sv.gob.mh.siip.model.preinversion.repository.OpinionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * Pruebas de integración HTTP de CU-PRE-25 "Elegibilidad": recorren el contrato
 * {@code CU-PRE-25.openapi.yaml} de punta a punta (rutas, códigos de estado y códigos de error)
 * sobre el contexto completo de Spring Boot.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ElegibilidadControllerIntegrationTest {

  private static final String HEADER_USUARIO = "X-Usuario";
  private static final String BASE = "/proyectos/{proyectoId}/elegibilidad";
  private static final ZoneId ZONA = ZoneId.of("America/El_Salvador");

  @Autowired private MockMvc mockMvc;
  @Autowired private InstitucionRepository institucionRepository;
  @Autowired private UnidadEjecutoraRepository unidadEjecutoraRepository;
  @Autowired private UsuarioRepository usuarioRepository;
  @Autowired private MacroSectorRepository macroSectorRepository;
  @Autowired private SectorActividadRepository sectorActividadRepository;
  @Autowired private EjeTematicoRepository ejeTematicoRepository;
  @Autowired private ProyectoRepository proyectoRepository;
  @Autowired private CriterioElegibilidadRepository criterioRepository;
  @Autowired private EntradaCatalogoEspecificarRepository entradaRepository;
  @Autowired private EjePlanGobiernoRepository ejePlanGobiernoRepository;
  @Autowired private OpinionTecnicaRepository opinionTecnicaRepository;
  @Autowired private ComentarioOpinionTecnicaRepository comentarioOtRepository;

  private Proyecto proyecto;
  private Usuario usuarioViabilizador;
  private String viabilizador;
  private String tecnicoUrp;
  private String tecnicoPre;
  private String coordinadorPre;
  private CriterioElegibilidad ods;
  private CriterioElegibilidad planGobierno;
  private CriterioElegibilidad planesRegionales;
  private String sufijo;

  @BeforeEach
  void prepararDatos() {
    sufijo = UUID.randomUUID().toString().substring(0, 8);
    Institucion institucion = institucionRepository.save(ProyectoFixtures.nuevaInstitucion("MH-ELE-" + sufijo,
        "Institución Elegibilidad"));
    UnidadEjecutora unidad = unidadEjecutoraRepository.save(ProyectoFixtures.nuevaUnidadEjecutora("UEE-" + sufijo,
        "UE Elegibilidad", institucion));
    viabilizador = "viab.ele." + sufijo;
    tecnicoUrp = "urp.ele." + sufijo;
    tecnicoPre = "pre.ele." + sufijo;
    coordinadorPre = "coord.ele." + sufijo;
    usuarioViabilizador = usuarioRepository.save(usuario(viabilizador, RolUsuario.VIABILIZADOR, null));
    usuarioRepository.save(usuario(tecnicoUrp, RolUsuario.TECNICO_URP, unidad));
    usuarioRepository.save(usuario(tecnicoPre, RolUsuario.TECNICO_PRE, null));
    usuarioRepository.save(usuario(coordinadorPre, RolUsuario.COORDINADOR_PRE, null));
    MacroSector macro = macroSectorRepository.save(ProyectoFixtures.nuevoMacrosector("ME" + sufijo, "Macro"));
    SectorActividad sector = sectorActividadRepository.save(ProyectoFixtures.nuevoSector("SE" + sufijo, "Sector", macro));
    EjeTematico eje = ejeTematicoRepository.save(ProyectoFixtures.nuevoEjeTematico("EE" + sufijo, "Eje"));
    proyecto = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto Elegibilidad " + sufijo,
        EstadoProyecto.VIABLE, unidad, institucion, sector, eje));

    ods = criterioRepository.save(criterio("ODS", 1, "Contribución a metas ODS", TipoEspecificar.CATALOGO,
        TipoCatalogoEspecificar.ODS, true));
    planGobierno = criterioRepository.save(criterio("PG", 2, "Coherencia con Plan de Gobierno",
        TipoEspecificar.CATALOGO, TipoCatalogoEspecificar.EJE_PLAN_GOBIERNO, false));
    planesRegionales = criterioRepository.save(criterio("PR", 3, "Contribución a Planes Regionales",
        TipoEspecificar.TEXTO_LIBRE, null, false));
    criterioRepository.save(criterio("SN", 4, "Criterio Sí/No pendiente", TipoEspecificar.SI_NO, null, false));
    entradaRepository.save(EntradaCatalogoEspecificar.builder().tipo(TipoCatalogoEspecificar.ODS)
        .codigo("IT-ODS-1-" + sufijo).nombre("Fin de la pobreza").build());
    entradaRepository.save(EntradaCatalogoEspecificar.builder().tipo(TipoCatalogoEspecificar.ODS)
        .codigo("IT-ODS-2-" + sufijo).nombre("Hambre cero").build());
    ejePlanGobiernoRepository.save(EjePlanGobierno.builder().codigo("IT1-" + sufijo).nombre("Eje 1: Carreteras")
        .activo(true).build());
    ejePlanGobiernoRepository.save(EjePlanGobierno.builder().codigo("IT5-" + sufijo).nombre("Eje 5: Agua")
        .activo(true).build());
  }

  @Test
  @DisplayName("Calificar, guardar y emitir la Elegibilidad por primera vez")
  void flujoDeEmision() throws Exception {
    mockMvc.perform(get(BASE, proyecto.getId()).header(HEADER_USUARIO, viabilizador))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.estadoProyecto").value("Proyecto viable"))
        .andExpect(jsonPath("$.accionesDisponibles.guardarCalificacion").value(true))
        .andExpect(jsonPath("$.accionesDisponibles.emitirElegibilidad").value(true))
        .andExpect(jsonPath("$.dimensiones[?(@.nombre == 'Alineación estratégica')].numero").value(1))
        .andExpect(jsonPath("$.dimensiones[*].criterios[?(@.criterioId == " + ods.getId() + ")].codigoCatalogo")
            .value("C.1"))
        .andExpect(jsonPath("$.dimensiones[*].criterios[?(@.criterioId == " + planesRegionales.getId()
            + ")].tipoEspecificar").value("TEXTO_LIBRE"))
        .andExpect(jsonPath("$.dimensiones[*].criterios[?(@.nombre == 'Criterio Sí/No pendiente')]").isEmpty());

    guardar("{\"respuestas\":["
        + "{\"criterioId\":" + ods.getId() + ",\"aplica\":true,\"especificarCodigosOpcion\":[\"IT-ODS-1-" + sufijo
        + "\",\"IT-ODS-2-" + sufijo + "\"]},"
        + "{\"criterioId\":" + planesRegionales.getId() + ",\"aplica\":true,\"especificarTexto\":\" Plan oriental \"},"
        + "{\"criterioId\":" + planGobierno.getId() + ",\"aplica\":false,\"especificarCodigosOpcion\":[\"IT1-"
        + sufijo + "\"]}]}")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.respuestas.length()").value(3))
        .andExpect(jsonPath("$.respuestas[0].especificarOpciones[1].nombre").value("Hambre cero"))
        .andExpect(jsonPath("$.respuestas[1].especificarOpciones.length()").value(0))
        .andExpect(jsonPath("$.respuestas[2].especificarTexto").value("Plan oriental"))
        .andExpect(jsonPath("$.accionesDisponibles.emitirElegibilidad").value(true));

    // Un segundo guardado reemplaza la calificación: el criterio que no viene queda sin calificar.
    guardar("{\"respuestas\":[{\"criterioId\":" + ods.getId() + ",\"aplica\":true,\"especificarCodigosOpcion\":"
        + "[\"IT-ODS-1-" + sufijo + "\"]}]}")
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.respuestas.length()").value(1));

    mockMvc.perform(get(BASE, proyecto.getId()).header(HEADER_USUARIO, tecnicoUrp))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accionesDisponibles.guardarCalificacion").value(false))
        .andExpect(jsonPath("$.dimensiones[*].criterios[?(@.criterioId == " + ods.getId()
            + ")].respuesta.especificarOpciones[0].nombre").value("Fin de la pobreza"));

    mockMvc.perform(post(BASE + "/emisiones", proyecto.getId()).header(HEADER_USUARIO, viabilizador))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.proyectoId").value(proyecto.getId()))
        .andExpect(jsonPath("$.estadoProyecto").value("Proyecto elegible"));

    mockMvc.perform(post(BASE + "/emisiones", proyecto.getId()).header(HEADER_USUARIO, viabilizador))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.codigo").value("FICHA_ELEGIBILIDAD_DESHABILITADA"));
    guardar("{\"respuestas\":[]}")
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.codigo").value("FICHA_ELEGIBILIDAD_DESHABILITADA"));
  }

  @Test
  @DisplayName("La nueva emisión tras comentarios de OT exige responderlos (RN15)")
  void nuevaEmisionTrasComentariosOt() throws Exception {
    mockMvc.perform(post(BASE + "/emisiones", proyecto.getId()).header(HEADER_USUARIO, viabilizador))
        .andExpect(status().isOk());
    OpinionTecnica ot = opinionTecnicaRepository.save(OpinionTecnica.builder().proyecto(proyecto)
        .resultado(ResultadoOpinionTecnica.OBSERVADO).fechaEmision(LocalDateTime.now(ZONA).plusMinutes(1))
        .tecnicoResponsable(usuarioViabilizador).build());
    ComentarioOpinionTecnica comentario = comentarioOtRepository.save(ComentarioOpinionTecnica.builder()
        .opinionTecnica(ot).apartado(ComentarioOpinionTecnica.ELEGIBILIDAD).comentario("Precisar los ODS").build());

    mockMvc.perform(get(BASE, proyecto.getId()).header(HEADER_USUARIO, viabilizador))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accionesDisponibles.guardarCalificacion").value(true));
    mockMvc.perform(post(BASE + "/emisiones", proyecto.getId()).header(HEADER_USUARIO, viabilizador))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.codigo").value("COMENTARIOS_OPINION_TECNICA_SIN_RESPONDER"))
        .andExpect(jsonPath("$.mensaje")
            .value("Es necesario responder los comentarios de la Opinión Técnica previo a la emisión de elegibilidad"));

    comentario.setJustificacionInstitucion("Se precisaron los ODS");
    comentarioOtRepository.save(comentario);
    mockMvc.perform(post(BASE + "/emisiones", proyecto.getId()).header(HEADER_USUARIO, viabilizador))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.estadoProyecto").value("Proyecto elegible"));
  }

  @Test
  @DisplayName("RN03: criterios seleccionados sin \"Especificar\" responden 422 con el detalle de cada uno")
  void especificarIncompleto() throws Exception {
    guardar("{\"respuestas\":["
        + "{\"criterioId\":" + ods.getId() + ",\"aplica\":true,\"especificarCodigosOpcion\":[\" \"]},"
        + "{\"criterioId\":" + planesRegionales.getId() + ",\"aplica\":true,\"especificarTexto\":\"  \"}]}")
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.codigo").value("ESPECIFICAR_INCOMPLETO"))
        .andExpect(jsonPath("$.mensaje")
            .value("Se debe completar la información de la columna 'Especificar' para los criterios seleccionados"))
        .andExpect(jsonPath("$.detalles.length()").value(2))
        .andExpect(jsonPath("$.detalles[0].campo").value("respuestas[criterioId=" + ods.getId() + "]"));
  }

  @Test
  @DisplayName("Criterios u opciones que no son válidos responden 400 SOLICITUD_INVALIDA")
  void solicitudInvalida() throws Exception {
    String textoLargo = "x".repeat(2001);
    guardar("{\"respuestas\":["
        + "{\"criterioId\":999999999,\"aplica\":true},"
        + "{\"criterioId\":" + ods.getId() + ",\"aplica\":true,\"especificarCodigosOpcion\":[\"NO-EXISTE\"]},"
        + "{\"criterioId\":" + ods.getId() + ",\"aplica\":false},"
        + "{\"criterioId\":" + planGobierno.getId() + ",\"aplica\":true,\"especificarCodigosOpcion\":[\"IT1-"
        + sufijo + "\",\"IT5-" + sufijo + "\"]},"
        + "{\"criterioId\":" + planesRegionales.getId() + ",\"aplica\":true,\"especificarTexto\":\"" + textoLargo
        + "\"}]}")
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.codigo").value("SOLICITUD_INVALIDA"))
        .andExpect(jsonPath("$.detalles.length()").value(5));
  }

  @Test
  @DisplayName("Solo el Viabilizador guarda y emite; otros roles no acceden a la ficha")
  void permisos() throws Exception {
    guardarComo(tecnicoPre, "{\"respuestas\":[]}")
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.codigo").value("ACCESO_DENEGADO"));
    mockMvc.perform(post(BASE + "/emisiones", proyecto.getId()).header(HEADER_USUARIO, tecnicoUrp))
        .andExpect(status().isForbidden());
    mockMvc.perform(get(BASE, proyecto.getId()).header(HEADER_USUARIO, coordinadorPre))
        .andExpect(status().isForbidden());
    mockMvc.perform(get(BASE, proyecto.getId()).header(HEADER_USUARIO, tecnicoPre))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accionesDisponibles.emitirElegibilidad").value(false));
  }

  @Test
  @DisplayName("Proyecto inexistente o que aún no es viable")
  void proyectoNoDisponible() throws Exception {
    mockMvc.perform(get(BASE, 999_999_999L).header(HEADER_USUARIO, viabilizador))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.codigo").value("PROYECTO_NO_ENCONTRADO"));

    proyecto.setEstado(EstadoProyecto.EN_FORMULACION);
    proyectoRepository.save(proyecto);
    mockMvc.perform(get(BASE, proyecto.getId()).header(HEADER_USUARIO, viabilizador))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accionesDisponibles.guardarCalificacion").value(false));
    mockMvc.perform(post(BASE + "/emisiones", proyecto.getId()).header(HEADER_USUARIO, viabilizador))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.codigo").value("FICHA_ELEGIBILIDAD_DESHABILITADA"));
  }

  private ResultActions guardar(String json) throws Exception {
    return guardarComo(viabilizador, json);
  }

  private ResultActions guardarComo(String usuario, String json) throws Exception {
    return mockMvc.perform(put(BASE + "/calificacion", proyecto.getId()).header(HEADER_USUARIO, usuario)
        .contentType(MediaType.APPLICATION_JSON).content(json));
  }

  private CriterioElegibilidad criterio(String clave, int orden, String nombre, TipoEspecificar tipo,
      TipoCatalogoEspecificar catalogo, boolean multiple) {
    return CriterioElegibilidad.builder().codigo("IT-" + clave + "-" + sufijo).numeroDimension(1)
        .dimension("Alineación estratégica").orden(orden).criterio(nombre).tipoEspecificar(tipo)
        .catalogoEspecificar(catalogo).permiteSeleccionMultiple(multiple).build();
  }

  private static Usuario usuario(String nombreUsuario, RolUsuario rol, UnidadEjecutora unidad) {
    return Usuario.builder().nombreUsuario(nombreUsuario).nombreCompleto(nombreUsuario)
        .correo(nombreUsuario + "@example.com").rol(rol).unidadEjecutora(unidad)
        .institucion(unidad == null ? null : unidad.getInstitucion()).activo(true).build();
  }
}
