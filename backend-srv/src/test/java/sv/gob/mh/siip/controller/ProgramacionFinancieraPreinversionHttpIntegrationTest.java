package sv.gob.mh.siip.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
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
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;
import sv.gob.mh.siip.security.AutenticacionDePrueba;

/**
 * Pruebas HTTP de CU-PRE-22.1. Complementan los steps BDD, que ejercen el
 * servicio directamente, verificando el mapeo del contrato y de errores.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProgramacionFinancieraPreinversionHttpIntegrationTest {

    private static final String RUTA = "/proyectos/{idProyecto}/programacion-financiera-preinversion";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private InstitucionRepository instituciones;
    @Autowired
    private UnidadEjecutoraRepository unidades;
    @Autowired
    private UsuarioRepository usuarios;
    @Autowired
    private ProyectoRepository proyectos;
    @Autowired
    private MacroSectorRepository macrosectores;
    @Autowired
    private SectorActividadRepository sectores;
    @Autowired
    private EjeTematicoRepository ejes;
    @Autowired
    private EtapaPreinversionRepository etapas;

    private Proyecto proyecto;
    private String usuarioPropietario;
    private String usuarioDeOtraUnidad;

    @BeforeEach
    void prepararDatos() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = instituciones.save(ProyectoFixtures.nuevaInstitucion("MH-221-" + sufijo,
                "Institución HTTP CU-PRE-22.1"));
        UnidadEjecutora unidadPropietaria = unidades.save(ProyectoFixtures.nuevaUnidadEjecutora("UE221-" + sufijo,
                "Unidad propietaria HTTP", institucion));
        usuarioPropietario = "urp.http.221." + sufijo;
        usuarios.save(usuario(usuarioPropietario, institucion, unidadPropietaria));

        Institucion otraInstitucion = instituciones.save(ProyectoFixtures.nuevaInstitucion("OT-221-" + sufijo,
                "Otra institución HTTP"));
        UnidadEjecutora otraUnidad = unidades.save(ProyectoFixtures.nuevaUnidadEjecutora("UEOT-" + sufijo,
                "Otra unidad HTTP", otraInstitucion));
        usuarioDeOtraUnidad = "urp.otra.221." + sufijo;
        usuarios.save(usuario(usuarioDeOtraUnidad, otraInstitucion, otraUnidad));

        MacroSector macrosector = macrosectores.save(ProyectoFixtures.nuevoMacrosector("M" + sufijo,
                "Macrosector HTTP"));
        SectorActividad sector = sectores.save(ProyectoFixtures.nuevoSector("S" + sufijo, "Sector HTTP", macrosector));
        EjeTematico eje = ejes.save(ProyectoFixtures.nuevoEjeTematico("E221-" + sufijo, "Eje HTTP"));
        proyecto = proyectos.save(ProyectoFixtures.nuevoProyecto("Proyecto HTTP CU-PRE-22.1",
                EstadoProyecto.EN_REGISTRO, unidadPropietaria, institucion, sector, eje));
        etapas.save(EtapaPreinversion.builder().proyecto(proyecto).tipoEtapa(TipoEtapaPreinversion.PERFIL)
                .fechaSeleccion(LocalDateTime.now()).costo(0D).build());
    }

    @Test
    void rechazaPeriodosCeroComoErrorHttp400() throws Exception {
        mockMvc.perform(put(RUTA + "/periodos", proyecto.getId()).with(AutenticacionDePrueba.como(usuarioPropietario))
                .contentType(MediaType.APPLICATION_JSON).content("{\"periodosAProgramar\":0}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.codigo").value("VALIDACION_NEGOCIO"));
    }

    @Test
    void rechazaEtapaNulaYMontoNegativoComoErrorHttp400() throws Exception {
        configurarDosPeriodos();

        mockMvc.perform(put(RUTA, proyecto.getId()).with(AutenticacionDePrueba.como(usuarioPropietario))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"filas\":[{\"etapa\":null,\"programacionPorPeriodo\":[10,20]}]}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.codigo").value("VALIDACION_NEGOCIO"));

        mockMvc.perform(put(RUTA, proyecto.getId()).with(AutenticacionDePrueba.como(usuarioPropietario))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"filas\":[{\"etapa\":\"PERFIL\",\"programacionPorPeriodo\":[10,-20]}]}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.codigo").value("VALIDACION_NEGOCIO"));
    }

    @Test
    void rechazaConsultaDeTecnicoUrpDeOtraUnidadCon403() throws Exception {
        mockMvc.perform(get(RUTA, proyecto.getId()).with(AutenticacionDePrueba.como(usuarioDeOtraUnidad)))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.codigo").value("ACCESO_DENEGADO"));
    }

    @Test
    void responde404CuandoElProyectoNoExiste() throws Exception {
        mockMvc.perform(get(RUTA, 999999999L).with(AutenticacionDePrueba.como(usuarioPropietario)))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.codigo").value("RECURSO_NO_ENCONTRADO"));
    }

    private void configurarDosPeriodos() throws Exception {
        mockMvc.perform(put(RUTA + "/periodos", proyecto.getId()).with(AutenticacionDePrueba.como(usuarioPropietario))
                .contentType(MediaType.APPLICATION_JSON).content("{\"periodosAProgramar\":2}"))
                .andExpect(status().isOk());
    }

    private static Usuario usuario(String nombreUsuario, Institucion institucion, UnidadEjecutora unidad) {
        return Usuario.builder().nombreUsuario(nombreUsuario).nombreCompleto("Técnico URP HTTP")
                .correo(nombreUsuario + "@example.com").rol(RolUsuario.TECNICO_URP).institucion(institucion)
                .unidadEjecutora(unidad).activo(true).build();
    }
}
