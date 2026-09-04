package sv.gob.mh.siip.bdd.steps.preinversion;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoSolicitud;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.SolicitudPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.service.ProyectoService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * CU-PRE-01-eliminar-registro.feature. El CU original no describe el mecanismo de interfaz de
 * esta eliminacion (RN 4); se agrego DELETE /proyectos/{idProyecto} a preinversion.yaml
 * (soft-delete via activo=false, mismo patron que ya usa el scheduler de alerta de RN 4) para
 * poder verificar la regla de negocio contra el backend real.
 */
public class Pre01EliminarRegistro {

    private static final String HEADER_USUARIO = "X-Usuario";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final SolicitudPreinversionRepository solicitudRepository;
    private final ProyectoService proyectoService;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;

    private Proyecto proyecto;
    private RuntimeException excepcion;

    public Pre01EliminarRegistro(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository,
            UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository,
            SolicitudPreinversionRepository solicitudRepository,
            ProyectoService proyectoService,
            MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.solicitudRepository = solicitudRepository;
        this.proyectoService = proyectoService;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
    }

    @Dado("un proyecto en la pantalla {string} \\(Anexo A.{int}) que nunca ha solicitado CUP")
    public void un_proyecto_en_la_pantalla_anexo_a_que_nunca_ha_solicitado_cup(String pantalla, Integer anexo) {
        UnidadEjecutora unidadEjecutora = crearUnidadEjecutora("ELI1");
        crearYAutenticarTecnicoUrp("elim1", unidadEjecutora);
        // Nunca ha solicitado CUP: sin ninguna SolicitudPreinversion asociada.
        proyecto = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto sin CUP solicitado",
                EstadoProyecto.EN_REGISTRO, unidadEjecutora, unidadEjecutora.getInstitucion(), crearSector("ELI1"),
                crearEjeTematico("ELI1")));
    }

    @Cuando("el Técnico URP elimina el registro del proyecto")
    public void el_tecnico_urp_elimina_el_registro_del_proyecto() {
        proyectoService.eliminar(proyecto.getId());
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el registro deja de aparecer en la pantalla {string}")
    public void el_registro_deja_de_aparecer_en_la_pantalla(String pantalla) {
        assertThat(estaActivo()).isFalse();
        assertThat(visibleEnListado()).isFalse();
    }

    @Dado("el Técnico URP tiene un registro en estado {string} que nunca ha solicitado el CUP")
    public void el_tecnico_urp_tiene_un_registro_en_estado_que_nunca_ha_solicitado_el_cup(String estadoEtiqueta) {
        UnidadEjecutora unidadEjecutora = crearUnidadEjecutora("ELI2");
        crearYAutenticarTecnicoUrp("elim2", unidadEjecutora);
        proyecto = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto " + estadoEtiqueta,
                mapearEstado(estadoEtiqueta), unidadEjecutora, unidadEjecutora.getInstitucion(), crearSector("ELI2"),
                crearEjeTematico("ELI2")));
    }

    @Entonces("el Sistema elimina la información de la {string}")
    public void el_sistema_elimina_la_informacion_de_la(String bandeja) {
        assertThat(estaActivo()).isFalse();
        assertThat(visibleEnListado()).isFalse();
    }

    @Dado("un proyecto ya tuvo al menos una solicitud de CUP registrada")
    public void un_proyecto_ya_tuvo_al_menos_una_solicitud_de_cup_registrada() {
        UnidadEjecutora unidadEjecutora = crearUnidadEjecutora("ELI3");
        crearYAutenticarTecnicoUrp("elim3", unidadEjecutora);
        proyecto = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto con CUP solicitado",
                EstadoProyecto.ENVIADO_DGICP_REGISTRO, unidadEjecutora, unidadEjecutora.getInstitucion(),
                crearSector("ELI3"), crearEjeTematico("ELI3")));

        solicitudRepository.save(SolicitudPreinversion.builder()
                .proyecto(proyecto)
                .tipoSolicitud(TipoSolicitud.CUP)
                .estado(EstadoSolicitud.REGISTRADA)
                .fechaSolicitud(LocalDateTime.now())
                .build());
    }

    @Cuando("el Técnico URP intenta eliminar el registro")
    public void el_tecnico_urp_intenta_eliminar_el_registro() {
        excepcion = null;
        try {
            proyectoService.eliminar(proyecto.getId());
        } catch (ConflictoEstadoException ex) {
            excepcion = ex;
        }
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el Sistema deniega la eliminación")
    public void el_sistema_deniega_la_eliminacion() {
        assertThat(excepcion).isInstanceOf(ConflictoEstadoException.class);
        assertThat(estaActivo()).isTrue();
    }

    @Dado("un actor distinto de {string} consulta un proyecto")
    public void un_actor_distinto_de_consulta_un_proyecto(String rolExcluido) {
        UnidadEjecutora unidadEjecutora = crearUnidadEjecutora("ELI4");
        Institucion institucion = unidadEjecutora.getInstitucion();

        // Cualquier rol distinto de Tecnico URP; se usa Tecnico PRE como ejemplo.
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        String nombreUsuario = "actor.bdd.elim4." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Actor distinto de " + rolExcluido + " (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(RolUsuario.TECNICO_PRE)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());
        autenticarComo(nombreUsuario);

        proyecto = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto consultado", EstadoProyecto.EN_REGISTRO,
                unidadEjecutora, institucion, crearSector("ELI4"), crearEjeTematico("ELI4")));
        // "consulta un proyecto": el rol distinto de Tecnico URP si puede ver el registro.
        proyectoService.obtener(proyecto.getId());
    }

    @Entonces("no puede eliminar el registro")
    public void no_puede_eliminar_el_registro() {
        excepcion = null;
        try {
            proyectoService.eliminar(proyecto.getId());
        } catch (AccesoDenegadoException ex) {
            excepcion = ex;
        }
        assertThat(excepcion).isInstanceOf(AccesoDenegadoException.class);
        assertThat(estaActivo()).isTrue();
        RequestContextHolder.resetRequestAttributes();
    }

    private boolean estaActivo() {
        return proyectoRepository.findById(proyecto.getId()).orElseThrow().getActivo();
    }

    private boolean visibleEnListado() {
        return proyectoRepository
                .findByActivoTrueAndUnidadEjecutoraId(proyecto.getUnidadEjecutora().getId(), PageRequest.of(0, 20))
                .getContent()
                .stream()
                .anyMatch(p -> p.getId().equals(proyecto.getId()));
    }

    private UnidadEjecutora crearUnidadEjecutora(String prefijo) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-" + prefijo + "-" + sufijo, "Institucion de prueba"));
        return unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-" + prefijo + "-" + sufijo, "Unidad Ejecutora de prueba",
                        institucion));
    }

    private SectorActividad crearSector(String prefijo) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        // MacroSector/SectorActividad.codigo son VARCHAR(10) (esquema del modulo programacion):
        // sin margen para prefijo + sufijo de 8 caracteres, solo 1 letra + sufijo.
        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M" + sufijo, "Macrosector de prueba"));
        return sectorActividadRepository.save(
                ProyectoFixtures.nuevoSector("S" + sufijo, "Sector "+prefijo, macrosector));
    }

    private EjeTematico crearEjeTematico(String prefijo) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        return ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-" + prefijo + "-" + sufijo, "Eje temático de prueba"));
    }

    private void crearYAutenticarTecnicoUrp(String prefijo, UnidadEjecutora unidadEjecutora) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        String nombreUsuario = "tecnico.urp.bdd." + prefijo + "." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(unidadEjecutora.getInstitucion())
                .activo(true)
                .build());
        autenticarComo(nombreUsuario);
    }

    private EstadoProyecto mapearEstado(String etiqueta) {
        return switch (etiqueta) {
            case "En Elaboración" -> EstadoProyecto.EN_REGISTRO;
            case "Observado DGICP (Registro)" -> EstadoProyecto.OBSERVADO_DGICP_REGISTRO;
            default -> throw new IllegalArgumentException("Estado no reconocido: " + etiqueta);
        };
    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
