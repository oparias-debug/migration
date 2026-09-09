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
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
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
 * Proyectos de ejemplo para probar CU-PRE-01/CU-PRE-01.5/CU-PRE-02 sin tener que registrar uno a
 * mano: uno en "En Elaboración" (recién registrado, tarea Flowable UT_EnElaboracion pendiente),
 * otro en "Enviado DGICP (Registro)" sin asignar, otro igual pero ya asignado al Técnico PRE de
 * prueba, otro con el CUP ya emitido (CUP_ASIGNADO), y uno más en la segunda Unidad Ejecutora de
 * prueba (para verificar el acotamiento por UE de CU-PRE-01 RN1). Arranca y avanza el proceso
 * Flowable igual que {@code ProyectoServiceImpl.registrar}/{@code solicitarCup}, sin pasar por
 * {@code ActorContexto} (no hay actor autenticado durante el arranque).
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
    private final UsuarioRepository usuarioRepository;
    private final RuntimeService runtimeService;
    private final TaskService taskService;

    public ProyectoDevSeeder(ProyectoRepository proyectoRepository,
            SolicitudPreinversionRepository solicitudRepository, InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository, UsuarioRepository usuarioRepository,
            RuntimeService runtimeService, TaskService taskService) {
        this.proyectoRepository = proyectoRepository;
        this.solicitudRepository = solicitudRepository;
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.usuarioRepository = usuarioRepository;
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
        crearProyectoAsignadoTecnicoPre("Proyecto de prueba (Asignado a Técnico PRE)", unidadEjecutora, institucion,
                sector, ejeTematico);
        crearProyectoConCupAsignado("Proyecto de prueba (CUP Asignado)", unidadEjecutora, institucion, sector,
                ejeTematico);

        // RN1 CU-PRE-01: el Técnico URP solo ve/registra proyectos de SU Unidad Ejecutora
        // (ProyectoServiceImpl.listar filtra por unidadEjecutoraId del actor). Este proyecto vive
        // en la segunda UE (URP-02/MINED, ver UsuarioDevSeeder) para que tecnico.urp2 sí lo vea
        // y tecnico.urp (URP-01) NO — sin este proyecto, tecnico.urp2 vería la bandeja vacía y no
        // habría nada concreto que confirmar que el acotamiento por UE realmente funciona.
        Institucion institucion2 = institucionRepository.findByCodigo("MINED").orElseThrow(
                () -> new IllegalStateException("Falta el seed de Institución MINED (UsuarioDevSeeder)."));
        UnidadEjecutora unidadEjecutora2 = unidadEjecutoraRepository.findByCodigo("URP-02").orElseThrow(
                () -> new IllegalStateException("Falta el seed de Unidad Ejecutora URP-02 (UsuarioDevSeeder)."));
        crearProyectoEnElaboracion("Proyecto de prueba (Otra Unidad Ejecutora)", unidadEjecutora2, institucion2,
                sector, ejeTematico);
    }

    /**
     * Proyecto ya visible en la Bandeja Preinversión (CU-PRE-02, "Solicitudes Activas") con su
     * solicitud de CUP ya asignada al Técnico PRE de prueba ("tecnico.pre", ver UsuarioDevSeeder) —
     * para probar sin pasos manuales tanto la vista de Coordinador PRE (columna "Asignado a" ya
     * completa, botón "Guardar" deshabilitado) como la de Técnico PRE (ve el caso, puede entrar a
     * FA-01/CU-PRE-01.5 vía el link del nombre del proyecto).
     */
    private void crearProyectoAsignadoTecnicoPre(String nombre, UnidadEjecutora unidadEjecutora,
            Institucion institucion, SectorActividad sector, EjeTematico ejeTematico) {
        if (!proyectoRepository.findByNombreContainingIgnoreCase(nombre).isEmpty()) {
            return;
        }
        Usuario tecnicoPre = usuarioRepository.findByNombreUsuario("tecnico.pre").orElseThrow(
                () -> new IllegalStateException("Falta el seed de Usuario tecnico.pre (UsuarioDevSeeder)."));

        Proyecto proyecto = nuevoProyectoBase(nombre, unidadEjecutora, institucion, sector, ejeTematico);
        proyecto.setEstado(EstadoProyecto.ENVIADO_DGICP_REGISTRO);
        proyecto = proyectoRepository.save(proyecto);

        solicitudRepository.save(SolicitudPreinversion.builder()
                .proyecto(proyecto)
                .tipoSolicitud(TipoSolicitud.CUP)
                .estado(EstadoSolicitud.REGISTRADA)
                .fechaSolicitud(LocalDateTime.now(ZONA_EL_SALVADOR))
                .tecnicoAsignado(tecnicoPre)
                .fechaAsignacion(LocalDateTime.now(ZONA_EL_SALVADOR))
                .build());

        runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY, String.valueOf(proyecto.getId()));
        Task tareaEnElaboracion = taskService.createTaskQuery()
                .processInstanceBusinessKey(String.valueOf(proyecto.getId()))
                .singleResult();
        taskService.complete(tareaEnElaboracion.getId());
    }

    /**
     * Proyecto que ya superó CU-PRE-01.5 (CUP emitido, estado CUP_ASIGNADO) — para probar sin
     * pasos manuales pantallas que dependen de un CUP ya asignado (p.ej. Formulación/CU-PRE-03)
     * sin tener que pasar a mano por "Solicitar CUP" → asignación → "Emitir CUP". Replica en la
     * base los mismos cambios que {@code ProyectoServiceImpl.emitirCup()} hace en Proyecto/
     * SolicitudPreinversion. Igual que ese método (y que el resto de la app hoy: es el único lugar
     * de todo el código que llama a {@code taskService.complete}, y solo para UT_EnElaboracion),
     * NO completa la tarea Flowable "UT_RevisionCUP" — queda pendiente en Flowable aunque el
     * proyecto ya esté en CUP_ASIGNADO en la base de datos; no es una inconsistencia introducida
     * acá, es el mismo estado en el que emitirCup() deja las cosas en producción.
     */
    private void crearProyectoConCupAsignado(String nombre, UnidadEjecutora unidadEjecutora, Institucion institucion,
            SectorActividad sector, EjeTematico ejeTematico) {
        if (!proyectoRepository.findByNombreContainingIgnoreCase(nombre).isEmpty()) {
            return;
        }
        Usuario tecnicoPre = usuarioRepository.findByNombreUsuario("tecnico.pre").orElseThrow(
                () -> new IllegalStateException("Falta el seed de Usuario tecnico.pre (UsuarioDevSeeder)."));

        Proyecto proyecto = nuevoProyectoBase(nombre, unidadEjecutora, institucion, sector, ejeTematico);
        proyecto.setEstado(EstadoProyecto.CUP_ASIGNADO);
        proyecto.setCup(siguienteCup());
        proyecto.setFechaCupAsignado(LocalDateTime.now(ZONA_EL_SALVADOR));
        proyecto = proyectoRepository.save(proyecto);

        solicitudRepository.save(SolicitudPreinversion.builder()
                .proyecto(proyecto)
                .tipoSolicitud(TipoSolicitud.CUP)
                .estado(EstadoSolicitud.APROBADA)
                .fechaSolicitud(LocalDateTime.now(ZONA_EL_SALVADOR))
                .tecnicoAsignado(tecnicoPre)
                .fechaAsignacion(LocalDateTime.now(ZONA_EL_SALVADOR))
                .build());

        runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY, String.valueOf(proyecto.getId()));
        Task tareaEnElaboracion = taskService.createTaskQuery()
                .processInstanceBusinessKey(String.valueOf(proyecto.getId()))
                .singleResult();
        taskService.complete(tareaEnElaboracion.getId());
    }

    /** CU-PRE-01.5, RN 2.8.c: siguiente CUP consecutivo de 5 dígitos, partiendo de 10000 — misma
     *  regla que {@code ProyectoServiceImpl.siguienteCup()}. */
    private String siguienteCup() {
        int siguiente = proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc()
                .map(p -> Integer.parseInt(p.getCup()) + 1)
                .orElse(10000);
        return String.format("%05d", siguiente);
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
