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
import sv.gob.mh.siip.model.preinversion.dto.GuardarProgramacionEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.HabilitarModificacionesFueraPlazoRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionFinancieraPapService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * CU-PRE-30-habilitar-modificaciones-fuera-plazo.feature (SF-4/SF-5). No existe en el sistema un
 * flujo/endpoint para la "solicitud" previa del Coordinador PRE (solo se menciona la nota remitida
 * por la Institución); el propio .feature lo marca como pendiente y no se modela aquí.
 */
public class Pre30HabilitarModificacionesFueraPlazo {

    private static final String HEADER_USUARIO = "X-Usuario";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final EtapaPreinversionRepository etapaPreinversionRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final CalendarioEventoRepository calendarioEventoRepository;
    private final ProgramacionFinancieraPapService service;

    private UnidadEjecutora unidadEjecutora;
    private Institucion institucion;
    private SectorActividad sector;
    private EjeTematico ejeTematico;
    private Proyecto proyecto;
    private EstudioProgramacionPAPDto estudio;

    public Pre30HabilitarModificacionesFueraPlazo(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository, EtapaPreinversionRepository etapaPreinversionRepository,
            MacroSectorRepository macroSectorRepository, SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository, CalendarioEventoRepository calendarioEventoRepository,
            ProgramacionFinancieraPapService service) {
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
    }

    @Dado("que el Coordinador PRE solicitó la modificación del PAP con nota de solicitud remitida por la Institución")
    public void que_el_coordinador_pre_solicito_la_modificacion() {
        crearInsumos();
    }

    @Cuando("el Administrador del Sistema habilita el sistema")
    public void el_administrador_habilita_el_sistema() {
        autenticarComo(crearUsuario(RolUsuario.ADMINISTRADOR, "admin.30h"));
        service.habilitarModificacionesFueraPlazo(
                new HabilitarModificacionesFueraPlazoRequestDto(unidadEjecutora.getId(), 2033));
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el Técnico URP puede agregar un nuevo estudio y registrar su programación, siguiendo los mismos pasos de SF-2")
    public void el_tecnico_urp_puede_agregar_un_nuevo_estudio_siguiendo_sf2() {
        autenticarComo(crearUsuario(RolUsuario.TECNICO_URP, "urp.30h.nuevo"));
        crearProyectoConEtapaSiNoExiste();

        estudio = service.agregarEstudio(new AgregarEstudioRequestDto(proyecto.getCup(), unidadEjecutora.getId(), 2033));
        assertThat(estudio.getEtapas()).isNotEmpty();
        RequestContextHolder.resetRequestAttributes();
    }

    @Y("el Técnico URP hace clic en el CUP del estudio a modificar")
    public void el_tecnico_urp_hace_clic_en_el_cup_del_estudio_a_modificar() {
        autenticarComo(crearUsuario(RolUsuario.TECNICO_URP, "urp.30h.mod"));
        crearProyectoConEtapaSiNoExiste();
    }

    private void crearProyectoConEtapaSiNoExiste() {
        if (proyecto != null) {
            return;
        }
        proyecto = Pre30Fixtures.nuevoEstudio(proyectoRepository, unidadEjecutora, institucion, sector, ejeTematico,
                proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc()
                        .map(p -> String.valueOf(Integer.parseInt(p.getCup()) + 1))
                        .orElse("10000"));
        Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, proyecto, TipoEtapaPreinversion.PERFIL, 10000.0);
    }

    @Entonces("el sistema muestra el Anexo A.2 para registrar los ajustes correspondientes")
    public void el_sistema_muestra_el_anexo_a2_para_ajustes() {
        estudio = service.obtenerProgramacionEstudio(proyecto.getCup(), 2033);
        assertThat(estudio.getCup()).isEqualTo(proyecto.getCup());
        RequestContextHolder.resetRequestAttributes();
    }

    @Y("aplica las mismas validaciones de suma cuatrimestral y cálculo de porcentajes que en SF-2")
    public void aplica_las_mismas_validaciones_que_en_sf2() {
        autenticarComo(crearUsuario(RolUsuario.TECNICO_URP, "urp.30h.guarda"));

        GuardarProgramacionEstudioRequestDto request = new GuardarProgramacionEstudioRequestDto()
                .addEtapasItem(new sv.gob.mh.siip.model.preinversion.dto.EtapaProgramacionRequestDto(
                        sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto.PERFIL)
                        .addFuentesItem(new sv.gob.mh.siip.model.preinversion.dto.FilaFuenteProgramacionRequestDto()
                                .montoCuatrimestre1(4000d).montoCuatrimestre2(3000d).montoCuatrimestre3(3000d)));

        EstudioProgramacionPAPDto resultado = service.guardarProgramacionEstudio(proyecto.getCup(), 2033, request);
        var fuente = resultado.getEtapas().get(0).getFuentes().get(0);
        assertThat(fuente.getTotalProgramadoAnio()).isEqualTo(10000d);
        assertThat(fuente.getPorcentajeCuatrimestre1()).isEqualTo(40d);
        RequestContextHolder.resetRequestAttributes();
    }

    private void crearInsumos() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        institucion = institucionRepository.save(ProyectoFixtures.nuevaInstitucion("INS-30H-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-30H-" + sufijo, "UE de prueba", institucion));
        MacroSector macrosector = macroSectorRepository.save(ProyectoFixtures.nuevoMacrosector("M30H" + sufijo, "Macrosector de prueba"));
        sector = sectorActividadRepository.save(ProyectoFixtures.nuevoSector("S30H" + sufijo, "Sector de prueba", macrosector));
        ejeTematico = ejeTematicoRepository.save(ProyectoFixtures.nuevoEjeTematico("EJE-30H-" + sufijo, "Eje tematico de prueba"));

        // Calendario de Eventos del PAP cerrado para 2033: sin esto, RN-A.b ya deja el periodo
        // abierto por defecto (sin calendario configurado) y la habilitacion de SF-4/SF-5 no
        // tendria nada que "saltarse" en este escenario.
        calendarioEventoRepository.save(CalendarioEvento.builder()
                .tipoEvento(TipoEventoCalendario.PROGRAMACION_PAP)
                .anio(2033)
                .estado(EstadoCalendarioEvento.CERRADO)
                .build());
    }

    private String crearUsuario(RolUsuario rol, String prefijo) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        String nombreUsuario = prefijo + "." + sufijo;
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

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
