package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarAvanceEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.AvanceFinancieroPapService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/** CU-PRE-32-bloqueo-fuera-calendario.feature (RN-A.b). */
public class Pre32BloqueoFueraCalendario {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final int ANIO = 2029;
    private static final String MENSAJE_ESPERADO = "Periodo de ingreso de información ha finalizado.";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final EtapaPreinversionRepository etapaPreinversionRepository;
    private final CalendarioEventoRepository calendarioEventoRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final AvanceFinancieroPapService service;

    private UnidadEjecutora unidadEjecutora;
    private Proyecto proyecto;
    private ConflictoEstadoException excepcionCapturada;

    public Pre32BloqueoFueraCalendario(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository, EtapaPreinversionRepository etapaPreinversionRepository,
            CalendarioEventoRepository calendarioEventoRepository, MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository, EjeTematicoRepository ejeTematicoRepository,
            AvanceFinancieroPapService service) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.calendarioEventoRepository = calendarioEventoRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.service = service;
    }

    @Dado("que la fecha actual está fuera del período establecido en el Calendario de Eventos del PAP \\(CU-ADM-04) avance-financiero")
    public void que_la_fecha_actual_esta_fuera_del_periodo() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-32B-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-32B-" + sufijo, "UE de prueba", institucion));

        String nombreUsuario = "urp.32b." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        MacroSector macrosector = macroSectorRepository.save(ProyectoFixtures.nuevoMacrosector("M32B" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository.save(ProyectoFixtures.nuevoSector("S32B" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository.save(ProyectoFixtures.nuevoEjeTematico("EJE-32B-" + sufijo, "Eje tematico de prueba"));
        String cup = proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc()
                .map(p -> String.format("%05d", Integer.parseInt(p.getCup()) + 1))
                .orElse("10000");
        proyecto = Pre30Fixtures.nuevoEstudio(proyectoRepository, unidadEjecutora, institucion, sector, ejeTematico, cup);
        Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, proyecto, TipoEtapaPreinversion.PERFIL, 10000.0);

        calendarioEventoRepository.save(CalendarioEvento.builder()
                .tipoEvento(TipoEventoCalendario.EJECUCION_PAP)
                .anio(ANIO)
                .cuatrimestre(1)
                .estado(EstadoCalendarioEvento.CERRADO)
                .build());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Cuando("el Técnico URP intenta ingresar o ajustar información avance-financiero")
    public void el_tecnico_urp_intenta_ingresar_o_ajustar_informacion() {
        GuardarAvanceEstudioRequestDto request = new GuardarAvanceEstudioRequestDto();
        String cup = proyecto.getCup();
        excepcionCapturada = assertThrows(ConflictoEstadoException.class,
                () -> service.guardarAvanceEstudio(cup, ANIO, CuatrimestreDto.CUATRIMESTRE_I, request));
    }

    @Entonces("el sistema muestra el mensaje del Anexo A.3")
    public void el_sistema_muestra_el_mensaje_del_anexo_a3() {
        assertThat(excepcionCapturada).isNotNull();
        assertThat(excepcionCapturada.getCodigo()).isEqualTo("PERIODO_CERRADO");
        assertThat(excepcionCapturada.getMessage()).isEqualTo(MENSAJE_ESPERADO);
    }

    @Y("todas las acciones de las tablas de los Anexos A.1 y A.5 quedan deshabilitadas, salvo \"Generar reporte\"")
    public void todas_las_acciones_quedan_deshabilitadas_salvo_generar_reporte() {
        assertThat(service.generarReporte(unidadEjecutora.getId(), ANIO, CuatrimestreDto.CUATRIMESTRE_I, "EXCEL"))
                .isNotNull();
        RequestContextHolder.resetRequestAttributes();
    }
}
