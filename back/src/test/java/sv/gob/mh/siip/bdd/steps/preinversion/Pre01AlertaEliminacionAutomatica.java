package sv.gob.mh.siip.bdd.steps.preinversion;

import java.time.LocalDateTime;
import java.util.UUID;

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
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoSolicitud;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.SolicitudPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.service.AlertaEliminacionAutomaticaScheduler;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * CU-PRE-01-alerta-eliminacion-automatica.feature. Actor "Sistema": no hay endpoint REST, se
 * ejecuta directamente {@link AlertaEliminacionAutomaticaScheduler#ejecutar()} (lo mismo que
 * dispara el cron en produccion). No hay infraestructura de correo real (ver
 * LoggingNotificacionService); "el sistema envia... un mensaje" se verifica por el efecto
 * observable en el backend: se marca fechaAlertaEliminacion en la solicitud.
 */
public class Pre01AlertaEliminacionAutomatica {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final int DIAS_HABILES_ESPERADOS = 5;

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final SolicitudPreinversionRepository solicitudRepository;
    private final AlertaEliminacionAutomaticaScheduler scheduler;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;

    private Proyecto proyecto;
    private SolicitudPreinversion solicitud;

    public Pre01AlertaEliminacionAutomatica(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository,
            UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository,
            SolicitudPreinversionRepository solicitudRepository,
            AlertaEliminacionAutomaticaScheduler scheduler,
            MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.solicitudRepository = solicitudRepository;
        this.scheduler = scheduler;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
    }

    private SectorActividad nuevoSectorDePrueba(String sufijo) {
        // MacroSector/SectorActividad.codigo son VARCHAR(10) (esquema del modulo programacion):
        // sin margen para un prefijo legible + sufijo de 8 caracteres, solo 1 letra + sufijo.
        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M" + sufijo, "Macrosector de prueba"));
        return sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S" + sufijo, "Sector de prueba", macrosector));
    }

    private EjeTematico nuevoEjeTematicoDePrueba(String sufijo) {
        return ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-" + sufijo, "Eje temático de prueba"));
    }

    @Dado("que han transcurrido tres meses desde que el Técnico URP realizó un nuevo registro mediante el botón {string}")
    public void que_han_transcurrido_tres_meses_desde_que_el_tecnico_urp_realizo_un_nuevo_registro_mediante_el_boton(
            String boton) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-ALE-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.save(
                ProyectoFixtures.nuevaUnidadEjecutora("UE-ALE-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        String nombreUsuarioTecnico = "tecnico.urp.bdd.alerta." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuarioTecnico)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuarioTecnico + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        // El registro se guarda "autenticado" como el Tecnico URP para que la auditoria
        // (@CreatedBy) deje usuarioCreacion poblado: es lo que el scheduler usa para saber a
        // quien notificar la alerta.
        autenticarComo(nombreUsuarioTecnico);
        proyecto = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto sin CUP solicitado",
                EstadoProyecto.EN_REGISTRO, unidadEjecutora, institucion, nuevoSectorDePrueba(sufijo),
                nuevoEjeTematicoDePrueba(sufijo)));
        RequestContextHolder.resetRequestAttributes();

        solicitud = solicitudRepository.save(SolicitudPreinversion.builder()
                .proyecto(proyecto)
                .tipoSolicitud(TipoSolicitud.CUP)
                .estado(EstadoSolicitud.REGISTRADA)
                .fechaSolicitud(LocalDateTime.now().minusMonths(4))
                .build());
    }

    @Dado("el Técnico URP no ha solicitado el CUP de ese registro")
    public void el_tecnico_urp_no_ha_solicitado_el_cup_de_ese_registro() {
        assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.REGISTRADA);
        assertThat(solicitud.getFechaAlertaEliminacion()).isNull();
    }

    @Cuando("se cumple el plazo de tres meses")
    public void se_cumple_el_plazo_de_tres_meses() {
        scheduler.ejecutar();
    }

    @Entonces("el sistema envía al Técnico URP un mensaje de alerta por correo electrónico")
    public void el_sistema_envia_al_tecnico_urp_un_mensaje_de_alerta_por_correo_electronico() {
        solicitud = solicitudRepository.findById(solicitud.getId()).orElseThrow();
        assertThat(solicitud.getFechaAlertaEliminacion()).isNotNull();
    }

    @Entonces("el mensaje indica que la información será eliminada de la Bandeja de Registro de Proyectos {int} días hábiles después del envío, si no se solicita el CUP")
    public void el_mensaje_indica_que_la_informacion_sera_eliminada_de_la_bandeja_de_registro_de_proyectos_dias_habiles_despues_del_envio_si_no_se_solicita_el_cup(
            Integer diasHabiles) {
        assertThat(diasHabiles).isEqualTo(DIAS_HABILES_ESPERADOS);
        // Solo se envio la alerta: el registro todavia debe estar activo (no se elimina en esta
        // misma pasada; ver la segunda escena de esta feature para el archivo tras el plazo).
        Proyecto recargado = proyectoRepository.findById(proyecto.getId()).orElseThrow();
        assertThat(recargado.getActivo()).isTrue();
    }

    @Dado("que el sistema envió la alerta de posible eliminación al Técnico URP")
    public void que_el_sistema_envio_la_alerta_de_posible_eliminacion_al_tecnico_urp() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-ALE2-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.save(
                ProyectoFixtures.nuevaUnidadEjecutora("UE-ALE2-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        String nombreUsuarioTecnico = "tecnico.urp.bdd.alerta2." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuarioTecnico)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuarioTecnico + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        autenticarComo(nombreUsuarioTecnico);
        proyecto = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto alertado sin respuesta",
                EstadoProyecto.EN_REGISTRO, unidadEjecutora, institucion, nuevoSectorDePrueba(sufijo),
                nuevoEjeTematicoDePrueba(sufijo)));
        RequestContextHolder.resetRequestAttributes();

        solicitud = solicitudRepository.save(SolicitudPreinversion.builder()
                .proyecto(proyecto)
                .tipoSolicitud(TipoSolicitud.CUP)
                .estado(EstadoSolicitud.REGISTRADA)
                .fechaSolicitud(LocalDateTime.now().minusMonths(4))
                .fechaAlertaEliminacion(LocalDateTime.now())
                .build());
    }

    @Dado("transcurrieron {int} días hábiles posteriores al envío del mensaje sin que se solicitara el CUP")
    public void transcurrieron_dias_habiles_posteriores_al_envio_del_mensaje_sin_que_se_solicitara_el_cup(
            Integer diasHabiles) {
        // Margen generoso sobre dias calendario para garantizar que, sin importar el dia de la
        // semana de partida, ya transcurrieron al menos esa cantidad de dias habiles reales.
        solicitud.setFechaAlertaEliminacion(LocalDateTime.now().minusDays(diasHabiles + 9));
        solicitud = solicitudRepository.save(solicitud);
    }

    @Cuando("se cumple dicho plazo")
    public void se_cumple_dicho_plazo() {
        scheduler.ejecutar();
    }

    @Entonces("el sistema elimina la información del registro de la Bandeja de Registro de Proyectos")
    public void el_sistema_elimina_la_informacion_del_registro_de_la_bandeja_de_registro_de_proyectos() {
        Proyecto recargado = proyectoRepository.findById(proyecto.getId()).orElseThrow();
        assertThat(recargado.getActivo()).isFalse();

        SolicitudPreinversion solicitudRecargada = solicitudRepository.findById(solicitud.getId()).orElseThrow();
        assertThat(solicitudRecargada.getEstado()).isEqualTo(EstadoSolicitud.ARCHIVADA);
        assertThat(solicitudRecargada.getFechaArchivo()).isNotNull();
    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
