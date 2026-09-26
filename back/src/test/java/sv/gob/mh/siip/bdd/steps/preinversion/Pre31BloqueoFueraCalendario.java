package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import sv.gob.mh.siip.bdd.support.Pre30Fixtures;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.administracion.domain.CalendarioEvento;
import sv.gob.mh.siip.model.administracion.enums.EstadoCalendarioEvento;
import sv.gob.mh.siip.model.administracion.enums.TipoEventoCalendario;
import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.EnviarProgramacionARevisionDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoPAPDto;
import sv.gob.mh.siip.model.preinversion.dto.FinalizarRevisionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarProgramacionMetasEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarRespuestaInstitucionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RevisionProgramacionPAPDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionMetasFisicasPapRevisionService;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionMetasFisicasPapService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * CU-PRE-31-bloqueo-fuera-calendario.feature (RN-A.b, RN-E). Además del guardado de la
 * programación, verifica que "Respuesta Institución" (SF-3 pasos 3-4) y "Revisión Finalizada"
 * (SF-6) también quedan bloqueadas fuera del Calendario de Eventos del PAP, salvo habilitación de
 * modificaciones fuera de plazo (SF-8/SF-9).
 */
public class Pre31BloqueoFueraCalendario {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final String MENSAJE_ESPERADO = "Periodo de ingreso de información ha finalizado.";
    private static final int ANIO = 2041;

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final EtapaPreinversionRepository etapaPreinversionRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final CalendarioEventoRepository calendarioEventoRepository;
    private final ProgramacionMetasFisicasPapService service;
    private final ProgramacionMetasFisicasPapRevisionService revisionService;

    private Institucion institucion;
    private UnidadEjecutora unidadEjecutora;
    private String nombreUsuarioUrp;
    private Proyecto proyecto;
    private ConflictoEstadoException excepcionCapturada;
    private ConflictoEstadoException excepcionEnvioRespuesta;
    private RevisionProgramacionPAPDto revisionFinal;

    public Pre31BloqueoFueraCalendario(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository, EtapaPreinversionRepository etapaPreinversionRepository,
            MacroSectorRepository macroSectorRepository, SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository, CalendarioEventoRepository calendarioEventoRepository,
            ProgramacionMetasFisicasPapService service,
            ProgramacionMetasFisicasPapRevisionService revisionService) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.calendarioEventoRepository = calendarioEventoRepository;
        this.service = service;
        this.revisionService = revisionService;
    }

    @Dado("que la fecha actual está fuera del período establecido en el Calendario de Eventos del PAP \\(CU-ADM-04)")
    public void que_la_fecha_actual_esta_fuera_del_periodo() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-31C-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-31C-" + sufijo, "UE de prueba", institucion));
        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M31C" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S31C" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-31C-" + sufijo, "Eje tematico de prueba"));

        nombreUsuarioUrp = crearUsuario(RolUsuario.TECNICO_URP, "urp.31c");

        String cup = proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc()
                .map(p -> String.format("%05d", Integer.parseInt(p.getCup()) + 1))
                .orElse("10000");
        proyecto = Pre30Fixtures.nuevoEstudio(proyectoRepository, unidadEjecutora, institucion, sector, ejeTematico, cup);
        Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, proyecto, TipoEtapaPreinversion.PERFIL, 10000.0);

        // El evento de calendario es único por tipo+año (sin cuatrimestre): varios escenarios de
        // este .feature comparten el mismo año, por lo que solo se crea la primera vez.
        if (calendarioEventoRepository
                .findByTipoEventoAndAnioAndCuatrimestreIsNull(TipoEventoCalendario.PROGRAMACION_PAP, ANIO).isEmpty()) {
            calendarioEventoRepository.save(CalendarioEvento.builder()
                    .tipoEvento(TipoEventoCalendario.PROGRAMACION_PAP)
                    .anio(ANIO)
                    .estado(EstadoCalendarioEvento.CERRADO)
                    .build());
        }

        autenticarComo(nombreUsuarioUrp);
    }

    @Cuando("el Técnico URP intenta ingresar o ajustar información metas-fisicas")
    public void el_tecnico_urp_intenta_ingresar_o_ajustar_informacion() {
        String cup = proyecto.getCup();
        GuardarProgramacionMetasEstudioRequestDto request = new GuardarProgramacionMetasEstudioRequestDto();
        excepcionCapturada = org.junit.jupiter.api.Assertions.assertThrows(ConflictoEstadoException.class,
                () -> service.guardarProgramacionMetasEstudio(cup, ANIO, request));
    }

    @Y("todas las acciones de la tabla \"Programación Física Cuatrimestral del PAP\" permanecen deshabilitadas")
    public void todas_las_acciones_permanecen_deshabilitadas() {
        verificarPeriodoCerrado(excepcionCapturada);
        RequestContextHolder.resetRequestAttributes();
    }

    @Cuando("el Técnico URP intenta registrar o enviar la \"Respuesta Institución\" fuera del período metas-fisicas")
    public void el_tecnico_urp_intenta_registrar_o_enviar_la_respuesta() {
        RegistrarRespuestaInstitucionRequestDto respuesta = new RegistrarRespuestaInstitucionRequestDto(
                unidadEjecutora.getId(), ANIO, "Ajustes realizados.");
        excepcionCapturada = org.junit.jupiter.api.Assertions.assertThrows(ConflictoEstadoException.class,
                () -> revisionService.registrarRespuestaInstitucion(respuesta));
        EnviarProgramacionARevisionDgicpRequestDto envio =
                new EnviarProgramacionARevisionDgicpRequestDto(unidadEjecutora.getId(), ANIO);
        excepcionEnvioRespuesta = org.junit.jupiter.api.Assertions.assertThrows(ConflictoEstadoException.class,
                () -> revisionService.enviarRespuestaInstitucion(envio));
    }

    @Entonces("el sistema rechaza ambas acciones con el mensaje \"Periodo de ingreso de información ha finalizado\" \\(Anexo A.3)")
    public void el_sistema_rechaza_ambas_acciones() {
        verificarPeriodoCerrado(excepcionCapturada);
        verificarPeriodoCerrado(excepcionEnvioRespuesta);
        RequestContextHolder.resetRequestAttributes();
    }

    @Cuando("el Técnico PRE intenta finalizar la revisión fuera del período metas-fisicas")
    public void el_tecnico_pre_intenta_finalizar_la_revision() {
        autenticarComo(crearUsuario(RolUsuario.TECNICO_PRE, "pre.31c"));
        FinalizarRevisionRequestDto request = new FinalizarRevisionRequestDto(unidadEjecutora.getId(), ANIO);
        excepcionCapturada = org.junit.jupiter.api.Assertions.assertThrows(ConflictoEstadoException.class,
                () -> revisionService.finalizarRevision(request));
    }

    @Entonces("el sistema rechaza la finalización con el mensaje \"Periodo de ingreso de información ha finalizado\" \\(Anexo A.3)")
    public void el_sistema_rechaza_la_finalizacion() {
        // Misma verificación que "todas las acciones ... permanecen deshabilitadas".
        todas_las_acciones_permanecen_deshabilitadas();
    }

    @Y("el Administrador del Sistema habilitó modificaciones fuera de plazo para la Unidad Ejecutora metas-fisicas")
    public void el_administrador_habilito_modificaciones_fuera_de_plazo() {
        autenticarComo(crearUsuario(RolUsuario.ADMINISTRADOR, "admin.31c"));
        service.habilitarModificacionesMetasFueraPlazo(
                new EnviarProgramacionARevisionDgicpRequestDto(unidadEjecutora.getId(), ANIO));
    }

    @Cuando("el Técnico URP envía la \"Respuesta Institución\" y el Técnico PRE finaliza la revisión metas-fisicas")
    public void el_tecnico_urp_envia_respuesta_y_el_tecnico_pre_finaliza() {
        autenticarComo(nombreUsuarioUrp);
        revisionService.registrarRespuestaInstitucion(
                new RegistrarRespuestaInstitucionRequestDto(unidadEjecutora.getId(), ANIO, "Ajustes realizados."));
        revisionService.enviarRespuestaInstitucion(new EnviarProgramacionARevisionDgicpRequestDto(unidadEjecutora.getId(), ANIO));

        autenticarComo(crearUsuario(RolUsuario.TECNICO_PRE, "pre.31c"));
        revisionFinal = revisionService.finalizarRevision(new FinalizarRevisionRequestDto(unidadEjecutora.getId(), ANIO));
    }

    @Entonces("el sistema permite ambas acciones y el estado queda en \"PAP Revisado\"")
    public void el_sistema_permite_ambas_acciones() {
        assertThat(revisionFinal.getRespuestaInstitucion()).isEqualTo("Ajustes realizados.");
        assertThat(revisionFinal.getFechaRespuesta()).isNotNull();
        assertThat(revisionFinal.getEstadoPap()).isEqualTo(EstadoPAPDto.PAP_REVISADO);
        RequestContextHolder.resetRequestAttributes();
    }

    private static void verificarPeriodoCerrado(ConflictoEstadoException excepcion) {
        assertThat(excepcion).isNotNull();
        assertThat(excepcion.getCodigo()).isEqualTo("PERIODO_CERRADO");
        assertThat(excepcion.getMessage()).isEqualTo(MENSAJE_ESPERADO);
    }

    private String crearUsuario(RolUsuario rol, String prefijo) {
        String nombreUsuario = prefijo + "." + UUID.randomUUID().toString().substring(0, 8);
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Actor de prueba (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(rol)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());
        return nombreUsuario;
    }

    private static void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
