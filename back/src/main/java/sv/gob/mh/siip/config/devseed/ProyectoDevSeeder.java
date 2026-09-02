package sv.gob.mh.siip.config.devseed;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoSolicitud;
import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.SolicitudPreinversionRepository;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * Dos proyectos de ejemplo para probar CU-PRE-01/CU-PRE-01.5 sin tener que registrar uno a mano:
 * uno en "En Elaboración" (recién registrado, tarea Flowable UT_EnElaboracion pendiente) y otro
 * en "Enviado DGICP (Registro)" (ya solicitó CUP, tarea Flowable UT_RevisionCUP pendiente). Arranca
 * y avanza el proceso Flowable igual que {@code ProyectoServiceImpl.registrar}/{@code solicitarCup},
 * sin pasar por {@code ActorContexto} (no hay actor autenticado durante el arranque).
 */
@Component
@Profile("dev")
@Order(30)
public class ProyectoDevSeeder implements DevSeeder {

    private static final String PROCESS_DEFINITION_KEY = "proceso_ciclo_vida_proyecto_siip";
    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private final ProyectoRepository proyectoRepository;
    private final SolicitudPreinversionRepository solicitudRepository;
    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final RuntimeService runtimeService;
    private final TaskService taskService;

    public ProyectoDevSeeder(ProyectoRepository proyectoRepository,
            SolicitudPreinversionRepository solicitudRepository, InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository, RuntimeService runtimeService, TaskService taskService) {
        this.proyectoRepository = proyectoRepository;
        this.solicitudRepository = solicitudRepository;
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
    }

    @Override
    public void seed() {
        Institucion institucion = institucionRepository.findByCodigo("MH-DGICP").orElseThrow(
                () -> new IllegalStateException("Falta el seed de Institución MH-DGICP (UsuarioDevSeeder)."));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.findByCodigo("URP-01").orElseThrow(
                () -> new IllegalStateException("Falta el seed de Unidad Ejecutora URP-01 (UsuarioDevSeeder)."));
        SectorActividad sector = sectorActividadRepository.findByCodigo("Desarrollo Social::Educación y cultura")
                .orElseThrow(() -> new IllegalStateException(
                        "Falta el seed del sector 'Educación y cultura' (CatalogoProyectoDevSeeder)."));
        EjeTematico ejeTematico = ejeTematicoRepository
                .findByCodigo("Infraestructura Educativa (Construcción y Mejoramiento)")
                .orElseThrow(() -> new IllegalStateException(
                        "Falta el seed del eje temático 'Infraestructura Educativa' (CatalogoProyectoDevSeeder)."));

        crearProyectoEnElaboracion("Proyecto de prueba (En Elaboración)", unidadEjecutora, institucion, sector,
                ejeTematico);
        crearProyectoEnviadoDgicpRegistro("Proyecto de prueba (Enviado DGICP Registro)", unidadEjecutora, institucion,
                sector, ejeTematico);
    }

    private void crearProyectoEnElaboracion(String nombre, UnidadEjecutora unidadEjecutora, Institucion institucion,
            SectorActividad sector, EjeTematico ejeTematico) {
        if (!proyectoRepository.findByNombreContainingIgnoreCase(nombre).isEmpty()) {
            return;
        }
        Proyecto proyecto = nuevoProyectoBase(nombre, unidadEjecutora, institucion, sector, ejeTematico);
        proyecto = proyectoRepository.save(proyecto);
        runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY, String.valueOf(proyecto.getId()));
    }

    private void crearProyectoEnviadoDgicpRegistro(String nombre, UnidadEjecutora unidadEjecutora,
            Institucion institucion, SectorActividad sector, EjeTematico ejeTematico) {
        if (!proyectoRepository.findByNombreContainingIgnoreCase(nombre).isEmpty()) {
            return;
        }
        Proyecto proyecto = nuevoProyectoBase(nombre, unidadEjecutora, institucion, sector, ejeTematico);
        proyecto.setEstado(EstadoProyecto.ENVIADO_DGICP_REGISTRO);
        proyecto = proyectoRepository.save(proyecto);

        solicitudRepository.save(SolicitudPreinversion.builder()
                .proyecto(proyecto)
                .tipoSolicitud(TipoSolicitud.CUP)
                .estado(EstadoSolicitud.REGISTRADA)
                .fechaSolicitud(LocalDateTime.now(ZONA_EL_SALVADOR))
                .build());

        runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY, String.valueOf(proyecto.getId()));
        Task tareaEnElaboracion = taskService.createTaskQuery()
                .processInstanceBusinessKey(String.valueOf(proyecto.getId()))
                .singleResult();
        taskService.complete(tareaEnElaboracion.getId());
    }

    private Proyecto nuevoProyectoBase(String nombre, UnidadEjecutora unidadEjecutora, Institucion institucion,
            SectorActividad sector, EjeTematico ejeTematico) {
        return Proyecto.builder()
                .nombre(nombre)
                .iniciativaInversion(IniciativaInversion.PROYECTO)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .estado(EstadoProyecto.EN_REGISTRO)
                .fechaIngreso(LocalDateTime.now(ZONA_EL_SALVADOR))
                .activo(true)
                .montoEstimadoInversion(100000.0)
                .sector(sector)
                .ejeTematico(ejeTematico)
                .descripcionProyecto("Proyecto de prueba sembrado para pruebas locales de CU-PRE-01/CU-PRE-01.5.")
                .build();
    }
}
