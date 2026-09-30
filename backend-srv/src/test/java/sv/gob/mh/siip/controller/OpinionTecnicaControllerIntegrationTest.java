package sv.gob.mh.siip.controller;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.bdd.support.SufijosPrueba;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.Elegibilidad;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoElegibilidad;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ElegibilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * Pruebas de integración HTTP de CU-PRE-26 "Opinión Técnica": recorren el contrato
 * {@code CU-PRE-26.openapi.yaml} de punta a punta (rutas, multipart, códigos de estado y códigos de
 * error) sobre el contexto completo de Spring Boot.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OpinionTecnicaControllerIntegrationTest {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final String BASE = "/proyectos/{proyectoId}/opiniones-tecnicas";
    private static final String GESTION = BASE + "/{opinionTecnicaId}";
    private static final ZoneId ZONA = ZoneId.of("America/El_Salvador");
    private static final String NUMERO_NOTA = "MH.DGICP.DGI/001.070/2026";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private InstitucionRepository institucionRepository;
    @Autowired private UnidadEjecutoraRepository unidadEjecutoraRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private MacroSectorRepository macroSectorRepository;
    @Autowired private SectorActividadRepository sectorActividadRepository;
    @Autowired private EjeTematicoRepository ejeTematicoRepository;
    @Autowired private ProyectoRepository proyectoRepository;
    @Autowired private ElegibilidadRepository elegibilidadRepository;
    @Autowired private EtapaPreinversionRepository etapaRepository;

    private Proyecto proyecto;
    private String tecnicoUrp;
    private String tecnicoPre;
    private String coordinadorPre;
    private String viabilizador;
    private String otroTecnicoPre;
    private Long idTecnicoPre;
    /** Gestión abierta por el último {@link #solicitar()} exitoso. */
    private long idGestionCreada;

    @BeforeEach
    void prepararDatos() {
        String sufijo = SufijosPrueba.nuevo(8);
        Institucion institucion = institucionRepository.save(ProyectoFixtures.nuevaInstitucion("MH-OT-" + sufijo,
                "Institución OT"));
        UnidadEjecutora unidad = unidadEjecutoraRepository.save(ProyectoFixtures.nuevaUnidadEjecutora("UEOT-" + sufijo,
                "UE Opinión Técnica", institucion));
        tecnicoUrp = "urp.ot." + sufijo;
        tecnicoPre = "pre.ot." + sufijo;
        coordinadorPre = "coord.ot." + sufijo;
        viabilizador = "viab.ot." + sufijo;
        otroTecnicoPre = "pre2.ot." + sufijo;
        usuarioRepository.save(usuario(otroTecnicoPre, RolUsuario.TECNICO_PRE, null));
        usuarioRepository.save(usuario(viabilizador, RolUsuario.VIABILIZADOR, null));
        usuarioRepository.save(usuario(tecnicoUrp, RolUsuario.TECNICO_URP, unidad));
        idTecnicoPre = usuarioRepository.save(usuario(tecnicoPre, RolUsuario.TECNICO_PRE, null)).getId();
        usuarioRepository.save(usuario(coordinadorPre, RolUsuario.COORDINADOR_PRE, null));
        MacroSector macro = macroSectorRepository.save(ProyectoFixtures.nuevoMacrosector("MO" + sufijo, "Macro"));
        SectorActividad sector = sectorActividadRepository.save(ProyectoFixtures.nuevoSector("SO" + sufijo, "Sector",
                macro));
        EjeTematico eje = ejeTematicoRepository.save(ProyectoFixtures.nuevoEjeTematico("EO" + sufijo, "Eje"));
        Proyecto nuevo = ProyectoFixtures.nuevoProyecto("Proyecto OT " + sufijo, EstadoProyecto.ELEGIBLE, unidad,
                institucion, sector, eje);
        nuevo.setCup(ProyectoFixtures.nuevoCup(proyectoRepository));
        proyecto = proyectoRepository.save(nuevo);
        // Precondición (FB paso 1): la Elegibilidad ya se emitió.
        elegibilidadRepository.save(Elegibilidad.builder().proyecto(proyecto).resultado(ResultadoElegibilidad.ELEGIBLE)
                .fechaEvaluacion(LocalDateTime.now(ZONA).minusHours(1)).build());
        etapa(TipoEtapaPreinversion.PERFIL);
        etapa(TipoEtapaPreinversion.PREFACTIBILIDAD);
    }

    @Test
    @DisplayName("Solicitar OT, revisar y enviar comentarios DGICP, y justificar (RN04, FA02, FA03, RN14)")
    void solicitudRevisionYComentarios() throws Exception {
        mockMvc.perform(get("/proyectos/{proyectoId}/tipos-solicitud-opinion-tecnica", proyecto.getId())
                        .header(HEADER_USUARIO, tecnicoUrp))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tiposSolicitud[0].tipoSolicitud").value("OPINION_TECNICA"))
                .andExpect(jsonPath("$.tiposSolicitud[0].habilitado").value(true))
                .andExpect(jsonPath("$.tiposSolicitud[1].tipoSolicitud").value("ACTUALIZACION_OT"))
                .andExpect(jsonPath("$.tiposSolicitud[1].habilitado").value(false));

        mockMvc.perform(multipart(BASE, proyecto.getId())
                        .file(new MockMultipartFile("notaSolicitudOt", "vacia.pdf", "application/pdf", new byte[0]))
                        .header(HEADER_USUARIO, tecnicoUrp))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("NOTA_SOLICITUD_OT_REQUERIDA"))
                .andExpect(jsonPath("$.detalles[0].campo").value("notaSolicitudOt"));

        ResultActions solicitud = solicitar();
        long idGestion = idGestionCreada;
        solicitud.andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/opiniones-tecnicas/" + idGestion)))
                .andExpect(jsonPath("$.tipoSolicitud").value("OPINION_TECNICA"))
                .andExpect(jsonPath("$.fechaSolicitud").isNotEmpty());

        solicitar()
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("OPINION_TECNICA_EN_CURSO"));

        mockMvc.perform(get(BASE, proyecto.getId()).header(HEADER_USUARIO, tecnicoPre))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.opinionesTecnicas[0].opinionTecnicaId").value(idGestion))
                .andExpect(jsonPath("$.opinionesTecnicas[0].estadoGestion").value("EN_CURSO"));

        mockMvc.perform(get(GESTION, proyecto.getId(), idGestion).header(HEADER_USUARIO, tecnicoPre))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.encabezado.cup").value(proyecto.getCup()))
                .andExpect(jsonPath("$.encabezado.etapaActual").value("Perfil"))
                .andExpect(jsonPath("$.encabezado.etapaFutura").value("Prefactibilidad"))
                .andExpect(jsonPath("$.tipoFormulario").value("ESTANDAR"))
                .andExpect(jsonPath("$.esPrimeraGestion").value(true))
                .andExpect(jsonPath("$.apartados[0].apartadoCodigo").value("1.1"))
                .andExpect(jsonPath("$.apartados[0].pantallaOrigen.casoUso").value("CU-PRE-04"))
                .andExpect(jsonPath("$.documentosAnexos.documentos[0].tipoDocumento").value("NOTA_SOLICITUD_OT"))
                .andExpect(jsonPath("$.comentariosElegibilidad").exists())
                .andExpect(jsonPath("$.accionesDisponibles.enviarComentarios.visible").value(true))
                .andExpect(jsonPath("$.accionesDisponibles.enviarComentarios.habilitada").value(true))
                .andExpect(jsonPath("$.accionesDisponibles.enviarAjustes.visible").value(false))
                .andExpect(jsonPath("$.camposEditables.comentarioDgicp").value(true))
                .andExpect(jsonPath("$.camposEditables.justificacionInstitucion").value(false));

        mockMvc.perform(post(GESTION + "/asignacion", proyecto.getId(), idGestion)
                        .header(HEADER_USUARIO, coordinadorPre)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tecnicoPreId\": " + idTecnicoPre + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tecnicoPreId").value(idTecnicoPre))
                .andExpect(jsonPath("$.fechaAsignacion").isNotEmpty());

        // RN07 b: asignada la gestión, otro Técnico PRE no la revisa.
        mockMvc.perform(put(GESTION + "/comentarios-dgicp", proyecto.getId(), idGestion)
                        .header(HEADER_USUARIO, otroTecnicoPre)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(comentario("1.1")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get(GESTION, proyecto.getId(), idGestion).header(HEADER_USUARIO, otroTecnicoPre))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accionesDisponibles.enviarComentarios.habilitada").value(false));

        mockMvc.perform(post(GESTION + "/envio-comentarios", proyecto.getId(), idGestion)
                        .header(HEADER_USUARIO, tecnicoUrp)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(comentario("1.2")))
                .andExpect(status().isForbidden());

        mockMvc.perform(put(GESTION + "/comentarios-dgicp", proyecto.getId(), idGestion)
                        .header(HEADER_USUARIO, tecnicoPre)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(comentario("9.9")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("SOLICITUD_INVALIDA"));

        mockMvc.perform(put(GESTION + "/comentarios-dgicp", proyecto.getId(), idGestion)
                        .header(HEADER_USUARIO, tecnicoPre)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(comentario("1.1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apartados[0].comentarioDgicp").value("Precisar el problema"))
                .andExpect(jsonPath("$.estadoGestion").value("EN_CURSO"));

        mockMvc.perform(post(GESTION + "/envio-comentarios", proyecto.getId(), idGestion)
                        .header(HEADER_USUARIO, tecnicoPre)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"comentarioDgicpDocumentosAnexos\": \"  \"}"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("COMENTARIOS_DGICP_REQUERIDOS"));

        mockMvc.perform(post(GESTION + "/envio-comentarios", proyecto.getId(), idGestion)
                        .header(HEADER_USUARIO, tecnicoPre)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(comentario("1.2")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoProyecto").value("OBSERVADO"))
                .andExpect(jsonPath("$.fechaFinPlazoObservaciones").isNotEmpty())
                .andExpect(jsonPath("$.rutaRetorno.actorDestino").value("TECNICO_URP"))
                .andExpect(jsonPath("$.rutaRetorno.filtrosAprobacion[0]").value("VIABILIDAD"))
                .andExpect(jsonPath("$.rutaRetorno.filtrosAprobacion[1]").value("OPINION_TECNICA"));

        mockMvc.perform(put(GESTION + "/justificaciones-institucion", proyecto.getId(), idGestion)
                        .header(HEADER_USUARIO, tecnicoUrp)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"justificacionesApartados\": [{\"apartadoCodigo\": \"1.2\", "
                                + "\"justificacionInstitucion\": \"Se amplió el análisis\"}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoGestion").value("OBSERVADA"))
                .andExpect(jsonPath("$.apartados[1].comentarioDgicp").value("Precisar el problema"))
                .andExpect(jsonPath("$.apartados[1].justificacionInstitucion").value("Se amplió el análisis"))
                .andExpect(jsonPath("$.accionesDisponibles.enviarAjustes.habilitada").value(true));

        mockMvc.perform(put(GESTION + "/justificaciones-institucion", proyecto.getId(), idGestion)
                        .header(HEADER_USUARIO, tecnicoUrp)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"justificacionInstitucionElegibilidad\": \"La responde el Viabilizador\"}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(put(GESTION + "/justificaciones-institucion", proyecto.getId(), idGestion)
                        .header(HEADER_USUARIO, viabilizador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"justificacionInstitucionElegibilidad\": \"Sin comentario que responder\"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(GESTION, proyecto.getId(), idGestion).header(HEADER_USUARIO, viabilizador))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numeroDevoluciones").value(1))
                .andExpect(jsonPath("$.camposEditables.justificacionInstitucion").value(false))
                .andExpect(jsonPath("$.camposEditables.justificacionInstitucionElegibilidad").value(true));
    }

    @Test
    @DisplayName("Visto bueno, OT favorable y Actualización de OT (FA01, RN10, FA04)")
    void emisionFavorableYActualizacion() throws Exception {
        solicitar().andExpect(status().isCreated());
        long idGestion = idGestionCreada;

        mockMvc.perform(post(GESTION + "/visto-bueno", proyecto.getId(), idGestion)
                        .header(HEADER_USUARIO, coordinadorPre))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("CONCLUSIONES_NO_REGISTRADAS"));

        mockMvc.perform(put(GESTION + "/conclusiones", proyecto.getId(), idGestion)
                        .header(HEADER_USUARIO, tecnicoPre)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"conclusiones\": \"   \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("SOLICITUD_INVALIDA"));

        mockMvc.perform(put(GESTION + "/conclusiones", proyecto.getId(), idGestion)
                        .header(HEADER_USUARIO, tecnicoPre)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"conclusiones\": \"Cumple los requisitos de la etapa\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conclusiones").value("Cumple los requisitos de la etapa"))
                .andExpect(jsonPath("$.accionesDisponibles.otFavorable.habilitada").value(false));

        mockMvc.perform(emitir(idGestion, tecnicoPre, bytes(), NUMERO_NOTA))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("VISTO_BUENO_OT_PENDIENTE"));
        mockMvc.perform(get(GESTION + "/informe", proyecto.getId(), idGestion).header(HEADER_USUARIO, tecnicoPre))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("OPINION_TECNICA_NO_EMITIDA"));

        mockMvc.perform(post(GESTION + "/visto-bueno", proyecto.getId(), idGestion)
                        .header(HEADER_USUARIO, coordinadorPre))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vistoBuenoOt").value(true));

        mockMvc.perform(emitir(idGestion, tecnicoPre, new byte[0], NUMERO_NOTA))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("NOTA_OT_REQUERIDA"));
        mockMvc.perform(emitir(idGestion, tecnicoPre, bytes(), "  "))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("NUMERO_NOTA_OT_REQUERIDO"));

        mockMvc.perform(emitir(idGestion, tecnicoPre, bytes(), NUMERO_NOTA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoProyecto").value("PROYECTO_CON_OT"))
                .andExpect(jsonPath("$.fechaEmisionOt").isNotEmpty())
                .andExpect(jsonPath("$.disponibleEnCapturaProyectos").value(false));

        mockMvc.perform(put(GESTION + "/comentarios-dgicp", proyecto.getId(), idGestion)
                        .header(HEADER_USUARIO, tecnicoPre)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(comentario("1.2")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("OPINION_TECNICA_YA_EMITIDA"));

        mockMvc.perform(get(GESTION, proyecto.getId(), idGestion).header(HEADER_USUARIO, tecnicoPre))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoGestion").value("FAVORABLE"))
                .andExpect(jsonPath("$.notaOt.nombreArchivo").value("nota-ot.pdf"))
                .andExpect(jsonPath("$.numeroNotaOt").value(NUMERO_NOTA))
                .andExpect(jsonPath("$.accionesDisponibles.guardar.habilitada").value(false));

        mockMvc.perform(get(BASE, proyecto.getId()).header(HEADER_USUARIO, tecnicoUrp))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.opinionesTecnicas[0].numeroNotaOt").value(NUMERO_NOTA));
        mockMvc.perform(get(GESTION + "/informe", proyecto.getId(), idGestion).header(HEADER_USUARIO, tecnicoUrp))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.opinionTecnica").value("FAVORABLE"))
                .andExpect(jsonPath("$.numeroNotaOt").value(NUMERO_NOTA))
                .andExpect(jsonPath("$.encabezado.cup").value(proyecto.getCup()))
                .andExpect(jsonPath("$.conclusiones").value("Cumple los requisitos de la etapa"));

        mockMvc.perform(post("/proyectos/{proyectoId}/actualizaciones-opinion-tecnica", proyecto.getId())
                        .header(HEADER_USUARIO, tecnicoUrp))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoSolicitud").value("ACTUALIZACION_OT"))
                .andExpect(jsonPath("$.camposHabilitados", hasItem("CU-PRE-04")));

        mockMvc.perform(get("/proyectos/{proyectoId}/tipos-solicitud-opinion-tecnica", proyecto.getId())
                        .header(HEADER_USUARIO, tecnicoUrp))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tiposSolicitud[0].habilitado").value(false))
                .andExpect(jsonPath("$.tiposSolicitud[1].habilitado").value(false));

        mockMvc.perform(post("/proyectos/{proyectoId}/actualizaciones-opinion-tecnica", proyecto.getId())
                        .header(HEADER_USUARIO, tecnicoUrp))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("ACTUALIZACION_OT_NO_DISPONIBLE"));
    }

    @Test
    @DisplayName("La gestión debe pertenecer al proyecto y el Coordinador PRE no emite la OT")
    void accesoALaGestion() throws Exception {
        solicitar().andExpect(status().isCreated());

        mockMvc.perform(get(GESTION, proyecto.getId(), idGestionCreada + 1000).header(HEADER_USUARIO, tecnicoPre))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("OPINION_TECNICA_NO_ENCONTRADA"));
        mockMvc.perform(emitir(idGestionCreada, coordinadorPre, bytes(), NUMERO_NOTA))
                .andExpect(status().isForbidden());
    }

    /** "OT favorable" con la "Nota de OT" y el "N° de nota de OT" (FA01 paso 1.5). */
    private MockHttpServletRequestBuilder emitir(long idGestion, String usuario, byte[] nota, String numero) {
        return multipart(GESTION + "/emision-favorable", proyecto.getId(), idGestion)
                .file(new MockMultipartFile("notaOt", "nota-ot.pdf", "application/pdf", nota))
                .param("numeroNotaOt", numero)
                .header(HEADER_USUARIO, usuario);
    }

    /** "Solicitar OT" como Técnico URP; guarda el identificador de la gestión creada. */
    private ResultActions solicitar() throws Exception {
        ResultActions resultado = mockMvc.perform(multipart(BASE, proyecto.getId())
                .file(new MockMultipartFile("notaSolicitudOt", "nota-solicitud.pdf", "application/pdf", bytes()))
                .header(HEADER_USUARIO, tecnicoUrp));
        String cuerpo = resultado.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode json = objectMapper.readTree(cuerpo);
        if (json.has("opinionTecnicaId")) {
            idGestionCreada = json.get("opinionTecnicaId").asLong();
        }
        return resultado;
    }

    private static String comentario(String apartado) {
        return "{\"comentariosApartados\": [{\"apartadoCodigo\": \"" + apartado
                + "\", \"comentarioDgicp\": \"Precisar el problema\"}]}";
    }

    private void etapa(TipoEtapaPreinversion tipo) {
        etapaRepository.save(EtapaPreinversion.builder().proyecto(proyecto).tipoEtapa(tipo)
                .fechaSeleccion(LocalDateTime.now(ZONA)).build());
    }

    private static byte[] bytes() {
        return "contenido".getBytes(StandardCharsets.UTF_8);
    }

    private static Usuario usuario(String nombreUsuario, RolUsuario rol, UnidadEjecutora unidad) {
        return Usuario.builder().nombreUsuario(nombreUsuario).nombreCompleto(nombreUsuario)
                .correo(nombreUsuario + "@example.com").rol(rol).unidadEjecutora(unidad)
                .institucion(unidad == null ? null : unidad.getInstitucion()).activo(true).build();
    }
}
