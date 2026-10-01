package sv.gob.mh.siip.controller;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import sv.gob.mh.siip.bdd.support.PriorizacionFixtures;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.bdd.support.SufijosPrueba;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.repository.CriterioPriorizacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EscalaCalificacionSubcriterioRepository;
import sv.gob.mh.siip.model.preinversion.repository.OpinionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.RangoInterpretacionPriorizacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.SubcriterioPriorizacionRepository;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;
import sv.gob.mh.siip.security.AutenticacionDePrueba;

/**
 * Pruebas de integración HTTP de CU-PRE-26.5 "Priorización": recorren el contrato
 * {@code CU-PRE-26.5.openapi.yaml} de punta a punta (rutas, códigos de estado y códigos de error) sobre el
 * contexto completo de Spring Boot, con el catálogo oficial del CU.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PriorizacionControllerIntegrationTest {

    private static final String BASE = "/proyectos/{proyectoId}/priorizacion";
    private static final String PRE = BASE + "/calificacion-pre";
    private static final String SYMP = BASE + "/calificacion-symp";
    private static final String ENVIO = "/envio";
    private static final String REVISION = "/revision";
    private static final String AJUSTES = "/habilitacion-ajustes";
    private static final String CODIGO = "$.codigo";
    private static final String ESTADO_PRE = "$.calificacionPre.estado";
    private static final String ESTADO_SYMP = "$.calificacionSymp.estado";
    private static final String CALIFICABLES = "$.criteriosCalificables";
    private static final String REVISADA = "REVISADA";
    private static final String ENVIADA = "ENVIADA_A_REVISION";
    private static final List<String> SUBCRITERIOS_PRE = List.of("1.1", "1.2", "1.3", "1.4", "2.1", "2.2", "2.3",
            "2.4", "3.1", "3.2", "3.3", "4.1", "4.2", "4.3");
    private static final List<String> SUBCRITERIOS_SYMP = List.of("5.1", "5.2", "5.3");

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private InstitucionRepository institucionRepository;
    @Autowired private UnidadEjecutoraRepository unidadEjecutoraRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private MacroSectorRepository macroSectorRepository;
    @Autowired private SectorActividadRepository sectorActividadRepository;
    @Autowired private EjeTematicoRepository ejeTematicoRepository;
    @Autowired private ProyectoRepository proyectoRepository;
    @Autowired private OpinionTecnicaRepository opinionTecnicaRepository;
    @Autowired private CriterioPriorizacionRepository criterioRepository;
    @Autowired private SubcriterioPriorizacionRepository subcriterioRepository;
    @Autowired private EscalaCalificacionSubcriterioRepository escalaRepository;
    @Autowired private RangoInterpretacionPriorizacionRepository rangoRepository;

    private Proyecto proyecto;
    private String tecnicoUrp;
    private String tecnicoPre;
    private String coordinadorPre;
    private String tecnicoSymp;
    private String coordinadorSymp;
    private String jefeDgi;

    @BeforeEach
    void prepararDatos() {
        PriorizacionFixtures.sembrarCatalogo(criterioRepository, subcriterioRepository, escalaRepository,
                rangoRepository);
        String sufijo = SufijosPrueba.nuevo(8);
        Institucion institucion = institucionRepository.save(ProyectoFixtures.nuevaInstitucion("MH-PRZ-" + sufijo,
                "Institución Priorización"));
        UnidadEjecutora unidad = unidadEjecutoraRepository.save(ProyectoFixtures.nuevaUnidadEjecutora(
                "UEPRZ-" + sufijo, "UE Priorización", institucion));
        tecnicoUrp = usuario("urp.prz." + sufijo, RolUsuario.TECNICO_URP, unidad);
        tecnicoPre = usuario("pre.prz." + sufijo, RolUsuario.TECNICO_PRE, null);
        coordinadorPre = usuario("coordpre.prz." + sufijo, RolUsuario.COORDINADOR_PRE, null);
        tecnicoSymp = usuario("symp.prz." + sufijo, RolUsuario.TECNICO_SYMP, null);
        coordinadorSymp = usuario("coordsymp.prz." + sufijo, RolUsuario.COORDINADOR_SYMP, null);
        jefeDgi = usuario("jefe.prz." + sufijo, RolUsuario.JEFE_DGI, null);
        MacroSector macro = macroSectorRepository.save(ProyectoFixtures.nuevoMacrosector("MP" + sufijo, "Macro"));
        SectorActividad sector = sectorActividadRepository.save(ProyectoFixtures.nuevoSector("SP" + sufijo, "Sector",
                macro));
        EjeTematico eje = ejeTematicoRepository.save(ProyectoFixtures.nuevoEjeTematico("EP" + sufijo, "Eje"));
        Proyecto nuevo = ProyectoFixtures.nuevoProyecto("Proyecto Priorización " + sufijo,
                EstadoProyecto.PROYECTO_CON_OT, unidad, institucion, sector, eje);
        nuevo.setCup(ProyectoFixtures.nuevoCup(proyectoRepository));
        proyecto = proyectoRepository.save(nuevo);
    }

    @Test
    @DisplayName("Sin OT favorable, proyecto inexistente o rol sin acceso: 409, 404 y 403")
    void rechazaElAccesoSinFiltroHabilitanteOSinRol() throws Exception {
        mockMvc.perform(get(BASE, proyecto.getId()).with(AutenticacionDePrueba.como(tecnicoPre)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath(CODIGO).value("FILTRO_HABILITANTE_INCOMPLETO"));
        mockMvc.perform(get(BASE, Long.MAX_VALUE).with(AutenticacionDePrueba.como(tecnicoPre)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath(CODIGO).value("PROYECTO_NO_ENCONTRADO"));
        mockMvc.perform(get(BASE, proyecto.getId()).with(AutenticacionDePrueba.como(tecnicoUrp)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Técnico PRE: guardar, validaciones de la matriz, calificar, bloqueo y habilitación de ajustes")
    void calificacionDeLosCriteriosUnoACuatro() throws Exception {
        emitirOtFavorable();
        mockMvc.perform(get(BASE, proyecto.getId()).with(AutenticacionDePrueba.como(tecnicoPre)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.criterios", hasSize(5)))
                .andExpect(jsonPath("$.criterios[0].subcriterios", hasSize(4)))
                .andExpect(jsonPath(CALIFICABLES, contains(1, 2, 3, 4)))
                .andExpect(jsonPath("$.accionesDisponibles.guardar.habilitada").value(true))
                .andExpect(jsonPath("$.accionesDisponibles.priorizacionRevisada.visible").value(false))
                .andExpect(jsonPath("$.resultado").doesNotExist());

        calificar(put(PRE, proyecto.getId()), tecnicoPre, Map.of("1.1", "5", "1.3", "N/A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.criterios[0].subcriterios[0].calificacion").value("5"))
                .andExpect(jsonPath("$.criterios[0].subcriterios[0].puntaje").value(5.88))
                .andExpect(jsonPath("$.criterios[0].subcriterios[2].ponderacionSubcriterioAplicada").value(0.0))
                .andExpect(jsonPath(ESTADO_PRE).value("PENDIENTE"));
        calificar(put(PRE, proyecto.getId()), tecnicoPre, Map.of("4.9", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(CODIGO).value("SOLICITUD_INVALIDA"));
        calificar(put(PRE, proyecto.getId()), tecnicoPre, Map.of("5.1", "1"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath(CODIGO).value("SUBCRITERIO_FUERA_DE_ALCANCE"));
        calificar(post(PRE + ENVIO, proyecto.getId()), tecnicoPre, Map.of("1.2", "4"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath(CODIGO).value("CALIFICACION_INCOMPLETA"))
                .andExpect(jsonPath("$.mensaje").value("Error. Debe seleccionar un puntaje para cada subcriterio"));
        mockMvc.perform(post(PRE + REVISION, proyecto.getId()).with(AutenticacionDePrueba.como(coordinadorPre)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath(CODIGO).value("CALIFICACION_NO_ENVIADA"));
        calificar(put(SYMP, proyecto.getId()), tecnicoSymp, Map.of("5.1", "3"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath(CODIGO).value("CALIFICACION_PRE_PENDIENTE"));
        // Sin enviar, la habilitación de ajustes no cambia nada.
        mockMvc.perform(post(PRE + AJUSTES, proyecto.getId()).with(AutenticacionDePrueba.como(coordinadorPre)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(ESTADO_PRE).value("PENDIENTE"));

        calificar(post(PRE + ENVIO, proyecto.getId()), tecnicoPre, todos(SUBCRITERIOS_PRE, "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(ESTADO_PRE).value(ENVIADA))
                .andExpect(jsonPath(CALIFICABLES, empty()));
        calificar(put(PRE, proyecto.getId()), tecnicoPre, Map.of("1.1", "2"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath(CODIGO).value("CALIFICACION_BLOQUEADA"));
        mockMvc.perform(get(BASE, proyecto.getId()).with(AutenticacionDePrueba.como(coordinadorPre)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accionesDisponibles.priorizacionRevisada.habilitada").value(true))
                .andExpect(jsonPath("$.accionesDisponibles.habilitarCalificacionPrioridad.habilitada").value(true));
        mockMvc.perform(post(PRE + AJUSTES, proyecto.getId()).with(AutenticacionDePrueba.como(coordinadorPre)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accionesDisponibles.habilitarCalificacionPrioridad.habilitada").value(false));
        calificar(put(PRE, proyecto.getId()), tecnicoPre, Map.of("1.1", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calificacionPre.edicionHabilitada").value(true));
    }

    @Test
    @DisplayName("Flujo completo PRE → SYMP, ajustes cruzados y resultado del Anexo A.2 (RN07, RN09, RN12)")
    void flujoCompletoHastaLaPrioridadDelProyecto() throws Exception {
        emitirOtFavorable();
        enviarYRevisar(PRE, tecnicoPre, coordinadorPre, todos(SUBCRITERIOS_PRE, "5"))
                .andExpect(jsonPath(ESTADO_PRE).value(REVISADA))
                .andExpect(jsonPath("$.resultado").doesNotExist());
        mockMvc.perform(get(BASE, proyecto.getId()).with(AutenticacionDePrueba.como(tecnicoSymp)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(CALIFICABLES, contains(5)));
        calificar(put(SYMP, proyecto.getId()), tecnicoSymp, Map.of("5.1", "4"))
                .andExpect(status().isOk());
        calificar(post(SYMP + ENVIO, proyecto.getId()), tecnicoSymp, todos(SUBCRITERIOS_SYMP, "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(ESTADO_SYMP).value(ENVIADA));

        // El Coordinador PRE reabre los criterios 1 a 4 antes de la revisión del criterio 5: la priorización
        // se completa cuando vuelva a revisarlos.
        mockMvc.perform(post(PRE + AJUSTES, proyecto.getId()).with(AutenticacionDePrueba.como(coordinadorPre)))
                .andExpect(status().isOk());
        calificar(post(PRE + ENVIO, proyecto.getId()), tecnicoPre, todos(SUBCRITERIOS_PRE, "5"))
                .andExpect(status().isOk());
        mockMvc.perform(post(SYMP + REVISION, proyecto.getId()).with(AutenticacionDePrueba.como(coordinadorSymp)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(ESTADO_SYMP).value(REVISADA))
                .andExpect(jsonPath("$.resultado").doesNotExist());
        mockMvc.perform(post(PRE + REVISION, proyecto.getId()).with(AutenticacionDePrueba.como(coordinadorPre)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultado.prioridadProyecto").value(100.0))
                .andExpect(jsonPath("$.resultado.categoriaPriorizacion").value("PRIORIZADO_PARA_PROGRAMACION"))
                .andExpect(jsonPath("$.resultado.puntajesCriterios", hasSize(5)))
                .andExpect(jsonPath("$.resultado.puntajesCriterios[3].puntaje").value(25.0))
                .andExpect(jsonPath("$.resultado.completa").value(true));

        mockMvc.perform(get(BASE, proyecto.getId()).with(AutenticacionDePrueba.como(jefeDgi)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(CALIFICABLES, empty()))
                .andExpect(jsonPath("$.accionesDisponibles.guardar.visible").value(false))
                .andExpect(jsonPath("$.resultado.prioridadProyecto").value(100.0));
        calificar(put(SYMP, proyecto.getId()), jefeDgi, Map.of("5.1", "1"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post(SYMP + AJUSTES, proyecto.getId()).with(AutenticacionDePrueba.como(coordinadorSymp)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calificacionSymp.edicionHabilitada").value(true));
        enviarYRevisar(SYMP, tecnicoSymp, coordinadorSymp, todos(SUBCRITERIOS_SYMP, "0"))
                .andExpect(jsonPath("$.resultado.prioridadProyecto").value(75.0))
                .andExpect(jsonPath("$.resultado.categoriaPriorizacion").value("PRIORIZADO_CONDICIONAL"));
    }

    private ResultActions enviarYRevisar(String tramo, String tecnico, String coordinador,
            Map<String, String> calificaciones) throws Exception {
        calificar(post(tramo + ENVIO, proyecto.getId()), tecnico, calificaciones).andExpect(status().isOk());
        return mockMvc.perform(post(tramo + REVISION, proyecto.getId()).with(AutenticacionDePrueba.como(coordinador)))
                .andExpect(status().isOk());
    }

    private ResultActions calificar(
            org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder peticion, String usuario,
            Map<String, String> calificaciones) throws Exception {
        List<Map<String, String>> items = new ArrayList<>();
        calificaciones.forEach((numero, valor) -> items.add(Map.of("subcriterioNumero", numero, "calificacion",
                valor)));
        return mockMvc.perform(peticion.with(AutenticacionDePrueba.como(usuario))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("calificaciones", items))));
    }

    private static Map<String, String> todos(List<String> subcriterios, String valor) {
        Map<String, String> calificaciones = new LinkedHashMap<>();
        subcriterios.forEach(s -> calificaciones.put(s, valor));
        return calificaciones;
    }

    private void emitirOtFavorable() {
        opinionTecnicaRepository.save(OpinionTecnica.builder().proyecto(proyecto)
                .resultado(ResultadoOpinionTecnica.FAVORABLE)
                .fechaEmision(LocalDateTime.now(ZoneId.of("America/El_Salvador")).minusHours(1)).build());
    }

    private String usuario(String nombreUsuario, RolUsuario rol, UnidadEjecutora unidad) {
        usuarioRepository.save(Usuario.builder().nombreUsuario(nombreUsuario).nombreCompleto(nombreUsuario)
                .correo(nombreUsuario + "@example.com").rol(rol).unidadEjecutora(unidad)
                .institucion(unidad == null ? null : unidad.getInstitucion()).activo(true).build());
        return nombreUsuario;
    }
}
