package sv.gob.mh.siip.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

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
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * Pruebas de integración HTTP de CU-PRE-24 "Viabilidad": recorren el contrato
 * {@code CU-PRE-24.openapi.yaml} de punta a punta (rutas, códigos de estado y códigos de error)
 * sobre el contexto completo de Spring Boot.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ViabilidadControllerIntegrationTest {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final String BASE = "/proyectos/{proyectoId}/viabilidad";

    @Autowired private MockMvc mockMvc;
    @Autowired private InstitucionRepository institucionRepository;
    @Autowired private UnidadEjecutoraRepository unidadEjecutoraRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private MacroSectorRepository macroSectorRepository;
    @Autowired private SectorActividadRepository sectorActividadRepository;
    @Autowired private EjeTematicoRepository ejeTematicoRepository;
    @Autowired private ProyectoRepository proyectoRepository;

    private Proyecto proyecto;
    private String tecnicoUrp;
    private String viabilizador;

    @BeforeEach
    void prepararDatos() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = institucionRepository.save(ProyectoFixtures.nuevaInstitucion("MH-VIA-" + sufijo,
                "Institución Viabilidad"));
        UnidadEjecutora unidad = unidadEjecutoraRepository.save(ProyectoFixtures.nuevaUnidadEjecutora("UEV-" + sufijo,
                "UE Viabilidad", institucion));
        tecnicoUrp = "urp.via." + sufijo;
        viabilizador = "viab.via." + sufijo;
        usuarioRepository.save(usuario(tecnicoUrp, RolUsuario.TECNICO_URP, unidad));
        usuarioRepository.save(usuario(viabilizador, RolUsuario.VIABILIZADOR, null));
        MacroSector macro = macroSectorRepository.save(ProyectoFixtures.nuevoMacrosector("MV" + sufijo, "Macro"));
        SectorActividad sector = sectorActividadRepository.save(ProyectoFixtures.nuevoSector("SV" + sufijo, "Sector", macro));
        EjeTematico eje = ejeTematicoRepository.save(ProyectoFixtures.nuevoEjeTematico("EV" + sufijo, "Eje"));
        proyecto = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto Viabilidad " + sufijo,
                EstadoProyecto.EN_FORMULACION, unidad, institucion, sector, eje));
    }

    @Test
    @DisplayName("Solicitar, guardar la justificación y emitir la Viabilidad por primera vez")
    void flujoDeEmision() throws Exception {
        cargarDocumento("DOCUMENTO_PREINVERSION", "preinversion.pdf")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.documento.tipoDocumento").value("DOCUMENTO_PREINVERSION"))
                .andExpect(jsonPath("$.documento.nombreArchivo").value("preinversion.pdf"))
                .andExpect(jsonPath("$.accionesDisponibles.solicitarViabilidad").value(true));

        mockMvc.perform(post(BASE + "/solicitudes", proyecto.getId()).header(HEADER_USUARIO, tecnicoUrp))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(BASE, proyecto.getId()).header(HEADER_USUARIO, viabilizador))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoProyecto").value("En viabilidad"))
                .andExpect(jsonPath("$.documentos.length()").value(1))
                .andExpect(jsonPath("$.indicadoresEvaluacion[0].nombre").value("VAN"))
                .andExpect(jsonPath("$.accionesDisponibles.enviarComentarios").value(true))
                .andExpect(jsonPath("$.accionesDisponibles.emitirViabilidad").value(false));

        guardarComentarios("{\"comentariosViabilizador\":[{\"campo\":\"OBJETIVO_GENERAL\",\"comentario\":\"Correcto\"}],"
                + "\"observacionesGeneralesJustificacion\":\"Cumple los criterios.\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comentariosViabilizador[0].campo").value("OBJETIVO_GENERAL"))
                .andExpect(jsonPath("$.accionesDisponibles.emitirViabilidad").value(true));

        mockMvc.perform(post(BASE + "/emisiones", proyecto.getId()).header(HEADER_USUARIO, viabilizador))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoProyecto").value("Proyecto viable"))
                .andExpect(jsonPath("$.elegibilidadHabilitada").value(true));

        mockMvc.perform(post(BASE + "/emisiones", proyecto.getId()).header(HEADER_USUARIO, viabilizador))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("FICHA_VIABILIDAD_DESHABILITADA"));
    }

    @Test
    @DisplayName("Enviar comentarios devuelve el proyecto en estado Observado")
    void flujoDeDevolucion() throws Exception {
        cargarDocumento("DOCUMENTO_PREINVERSION", "preinversion.docx").andExpect(status().isCreated());
        mockMvc.perform(post(BASE + "/solicitudes", proyecto.getId()).header(HEADER_USUARIO, tecnicoUrp))
                .andExpect(status().isNoContent());
        mockMvc.perform(post(BASE + "/solicitudes", proyecto.getId()).header(HEADER_USUARIO, tecnicoUrp))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("SOLICITUD_VIABILIDAD_EN_CURSO"));

        mockMvc.perform(post(BASE + "/devoluciones", proyecto.getId()).header(HEADER_USUARIO, viabilizador))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.proyectoId").value(proyecto.getId()))
                .andExpect(jsonPath("$.estadoProyecto").value("Observado"));

        mockMvc.perform(get(BASE, proyecto.getId()).header(HEADER_USUARIO, tecnicoUrp))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accionesDisponibles.solicitarViabilidad").value(true));
    }

    @Test
    @DisplayName("Los errores de negocio usan los códigos del contrato")
    void erroresDelContrato() throws Exception {
        mockMvc.perform(get(BASE, 999_999_999L).header(HEADER_USUARIO, viabilizador))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value("PROYECTO_NO_ENCONTRADO"));

        mockMvc.perform(get(BASE, proyecto.getId()))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post(BASE + "/solicitudes", proyecto.getId()).header(HEADER_USUARIO, viabilizador))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.codigo").value("ACCESO_DENEGADO"));

        mockMvc.perform(post(BASE + "/solicitudes", proyecto.getId()).header(HEADER_USUARIO, tecnicoUrp))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("DOCUMENTO_PREINVERSION_REQUERIDO"));

        cargarDocumento("DOCUMENTO_PREINVERSION", "preinversion.txt")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value("SOLICITUD_INVALIDA"));

        mockMvc.perform(post(BASE + "/emisiones", proyecto.getId()).header(HEADER_USUARIO, viabilizador))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("SOLICITUD_VIABILIDAD_NO_VIGENTE"));

        guardarComentarios("{\"comentariosViabilizador\":[]}")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("SOLICITUD_VIABILIDAD_NO_VIGENTE"));
    }

    @Test
    @DisplayName("Emitir sin la justificación guardada responde 422 y la formulación queda bloqueada mientras tanto")
    void emitirSinJustificacion() throws Exception {
        cargarDocumento("OTRO_DOCUMENTO", "anexo.xlsx").andExpect(status().isCreated());
        cargarDocumento("DOCUMENTO_PREINVERSION", "preinversion.pdf").andExpect(status().isCreated());
        mockMvc.perform(post(BASE + "/solicitudes", proyecto.getId()).header(HEADER_USUARIO, tecnicoUrp))
                .andExpect(status().isNoContent());

        cargarDocumento("OTRO_DOCUMENTO", "otro.pdf")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("SOLICITUD_VIABILIDAD_EN_CURSO"));

        mockMvc.perform(post(BASE + "/emisiones", proyecto.getId()).header(HEADER_USUARIO, viabilizador))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value("JUSTIFICACION_VIABILIDAD_REQUERIDA"));

        mockMvc.perform(put("/proyectos/{idProyecto}/identificacion", proyecto.getId())
                        .header(HEADER_USUARIO, tecnicoUrp)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"objetivoGeneral\":\"Nuevo objetivo\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value("FORMULACION_BLOQUEADA"));
    }

    private ResultActions cargarDocumento(String tipo, String nombreArchivo) throws Exception {
        MockMultipartFile archivo = new MockMultipartFile("archivo", nombreArchivo, MediaType.APPLICATION_OCTET_STREAM_VALUE,
                "contenido".getBytes(StandardCharsets.UTF_8));
        return mockMvc.perform(multipart(BASE + "/documentos", proyecto.getId())
                .file(archivo)
                .param("tipoDocumento", tipo)
                .header(HEADER_USUARIO, tecnicoUrp));
    }

    private ResultActions guardarComentarios(String json) throws Exception {
        return mockMvc.perform(put(BASE + "/comentarios", proyecto.getId())
                .header(HEADER_USUARIO, viabilizador)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    private static Usuario usuario(String nombre, RolUsuario rol, UnidadEjecutora unidad) {
        return Usuario.builder().nombreUsuario(nombre).nombreCompleto(nombre).correo(nombre + "@example.com")
                .rol(rol).unidadEjecutora(unidad).activo(true).build();
    }
}
