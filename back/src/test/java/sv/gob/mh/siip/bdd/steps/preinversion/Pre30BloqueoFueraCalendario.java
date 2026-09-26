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
import sv.gob.mh.siip.model.preinversion.dto.AgregarEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioProgramacionPAPDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionFinancieraPapService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/** CU-PRE-30-bloqueo-fuera-calendario.feature (RN-A.b). */
public class Pre30BloqueoFueraCalendario {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final String MENSAJE_ESPERADO = "Periodo de ingreso de información ha finalizado.";
    /** Año sin evento PROGRAMACION_PAP configurado en ningún escenario BDD. */
    private static final int ANIO_SIN_EVENTO = 2045;

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final CalendarioEventoRepository calendarioEventoRepository;
    private final ProyectoRepository proyectoRepository;
    private final EtapaPreinversionRepository etapaPreinversionRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final ProgramacionFinancieraPapService service;

    private UnidadEjecutora unidadEjecutora;
    private Proyecto proyecto;
    private EstudioProgramacionPAPDto estudioAgregado;
    private ConflictoEstadoException excepcionCapturada;

    public Pre30BloqueoFueraCalendario(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            CalendarioEventoRepository calendarioEventoRepository, ProyectoRepository proyectoRepository,
            EtapaPreinversionRepository etapaPreinversionRepository, MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository, EjeTematicoRepository ejeTematicoRepository,
            ProgramacionFinancieraPapService service) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.calendarioEventoRepository = calendarioEventoRepository;
        this.proyectoRepository = proyectoRepository;
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.service = service;
    }

    @Dado("que la fecha actual está fuera del período establecido en el Calendario de Eventos del PAP \\(CU-ADM XX \"Gestión de Eventos de Calendario\")")
    public void que_la_fecha_actual_esta_fuera_del_periodo() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-30B-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-30B-" + sufijo, "UE de prueba", institucion));

        String nombreUsuario = "urp.30b." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        calendarioEventoRepository.save(CalendarioEvento.builder()
                .tipoEvento(TipoEventoCalendario.PROGRAMACION_PAP)
                .anio(2031)
                .estado(EstadoCalendarioEvento.CERRADO)
                .build());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Cuando("el Técnico URP intenta ingresar o ajustar información")
    public void el_tecnico_urp_intenta_ingresar_o_ajustar_informacion() {
        AgregarEstudioRequestDto request = new AgregarEstudioRequestDto("08040", unidadEjecutora.getId(), 2031);
        excepcionCapturada = org.junit.jupiter.api.Assertions.assertThrows(ConflictoEstadoException.class,
                () -> service.agregarEstudio(request));
    }

    @Dado("que no existe un evento de \"Programación PAP\" en el Calendario de Eventos del PAP para el año a programar")
    public void que_no_existe_evento_de_calendario_para_el_anio() {
        que_la_fecha_actual_esta_fuera_del_periodo();
        assertThat(calendarioEventoRepository
                .findByTipoEventoAndAnioAndCuatrimestreIsNull(TipoEventoCalendario.PROGRAMACION_PAP, ANIO_SIN_EVENTO))
                .isEmpty();
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        MacroSector macrosector = macroSectorRepository.save(ProyectoFixtures.nuevoMacrosector("M30S" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository.save(ProyectoFixtures.nuevoSector("S30S" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository.save(ProyectoFixtures.nuevoEjeTematico("EJE-30S-" + sufijo, "Eje tematico de prueba"));
        String cup = proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc()
                .map(p -> String.format("%05d", Integer.parseInt(p.getCup()) + 1))
                .orElse("10000");
        proyecto = Pre30Fixtures.nuevoEstudio(proyectoRepository, unidadEjecutora, unidadEjecutora.getInstitucion(),
                sector, ejeTematico, cup);
        Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, proyecto, TipoEtapaPreinversion.PERFIL, 1000.0);
    }

    @Cuando("el Técnico URP agrega un estudio para ese año")
    public void el_tecnico_urp_agrega_un_estudio_para_ese_anio() {
        estudioAgregado = service.agregarEstudio(
                new AgregarEstudioRequestDto(proyecto.getCup(), unidadEjecutora.getId(), ANIO_SIN_EVENTO));
    }

    @Entonces("el sistema permite el ingreso de información, asumiendo el período abierto \\(RN-A.b)")
    public void el_sistema_permite_el_ingreso_asumiendo_periodo_abierto() {
        // [SUPUESTO] documentado en ProgramacionPapCalendario#verificarPeriodoAbierto.
        assertThat(estudioAgregado).isNotNull();
        assertThat(estudioAgregado.getCup()).isEqualTo(proyecto.getCup());
        RequestContextHolder.resetRequestAttributes();
    }

    @Y("todas las acciones de la tabla \"Programación Financiera Cuatrimestral del PAP\" permanecen deshabilitadas")
    public void todas_las_acciones_permanecen_deshabilitadas() {
        assertThat(excepcionCapturada).isNotNull();
        assertThat(excepcionCapturada.getCodigo()).isEqualTo("PERIODO_CERRADO");
        assertThat(excepcionCapturada.getMessage()).isEqualTo(MENSAJE_ESPERADO);
        RequestContextHolder.resetRequestAttributes();
    }
}
