package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.CambioUnidadEjecutoraRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoDto;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.ProyectoService;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * CU-PRE-01-cambiar-unidad-ejecutora.feature. Simula la peticion autenticada agregando el header
 * X-Usuario directamente al contexto de request (en producto lo agrega api-gateway tras validar
 * el JWT; ver ActorContexto), y ejecuta el caso a traves del ProyectoService real.
 */
public class Pre01CambiarUnidadEjecutora {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final String NOMBRE_USUARIO_ADMIN = "admin.sistema.bdd";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final ProyectoService proyectoService;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;

    private Proyecto proyecto;
    private UnidadEjecutora unidadEjecutoraNueva;
    private ProyectoDto proyectoActualizado;

    public Pre01CambiarUnidadEjecutora(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository,
            UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository,
            ProyectoService proyectoService,
            MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.proyectoService = proyectoService;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
    }

    @Dado("un proyecto registrado en cualquier etapa")
    public void un_proyecto_registrado_en_cualquier_etapa() {
        autenticarComoAdministradorDelSistema();

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-BDD", "Institucion de prueba"));
        UnidadEjecutora unidadEjecutoraOriginal = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-ORIGEN", "Unidad Ejecutora origen", institucion));
        unidadEjecutoraNueva = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-DESTINO", "Unidad Ejecutora destino", institucion));

        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(NOMBRE_USUARIO_ADMIN)
                .nombreCompleto("Administrador del Sistema (BDD)")
                .correo("admin.sistema.bdd@example.com")
                .rol(RolUsuario.ADMINISTRADOR)
                .unidadEjecutora(unidadEjecutoraOriginal)
                .institucion(institucion)
                .activo(true)
                .build());

        var macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("MACS-BDD", "Macrosector de prueba"));
        var sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("SEC-BDD", "Sector de prueba", macrosector));
        var ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-BDD", "Eje temático de prueba"));

        // "en cualquier etapa": un estado avanzado del ciclo de vida, no solo EN_REGISTRO,
        // para reflejar que RN 4 permite este cambio en cualquier momento.
        proyecto = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto en cualquier etapa",
                EstadoProyecto.EN_VIABILIDAD, unidadEjecutoraOriginal, institucion, sector, ejeTematico));
    }

    @Cuando("el Administrador del Sistema cambia la Unidad Ejecutora del proyecto")
    public void el_administrador_del_sistema_cambia_la_unidad_ejecutora_del_proyecto() {
        proyectoActualizado = proyectoService.cambiarUnidadEjecutora(proyecto.getId(),
                new CambioUnidadEjecutoraRequestDto().idUnidadEjecutora(unidadEjecutoraNueva.getId()));
    }

    @Entonces("el proyecto queda asociado a la nueva Unidad Ejecutora")
    public void el_proyecto_queda_asociado_a_la_nueva_unidad_ejecutora() {
        assertThat(proyectoActualizado.getUnidadEjecutora().getIdUnidadEjecutora())
                .isEqualTo(unidadEjecutoraNueva.getId());

        Proyecto recargado = proyectoRepository.findById(proyecto.getId()).orElseThrow();
        assertThat(recargado.getUnidadEjecutora().getId()).isEqualTo(unidadEjecutoraNueva.getId());
        assertThat(recargado.getInstitucion().getId()).isEqualTo(unidadEjecutoraNueva.getInstitucion().getId());

        RequestContextHolder.resetRequestAttributes();
    }

    private void autenticarComoAdministradorDelSistema() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, NOMBRE_USUARIO_ADMIN);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
