package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.Pre30Fixtures;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.AvanceFinancieroCuatrimestral;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.FuenteFinanciamientoEtapaPap;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralFinanciera;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceEstudioDto;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaAvanceFuenteDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.enums.FuenteFinanciamiento;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.AvanceFinancieroCuatrimestralRepository;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.AvanceFinancieroPapService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * CU-PRE-32-calcular-acumulados.feature (RN-E). Fuente con programado 1000/1000/1000 (AAP=3000) y
 * avances CI=500, CII=300; se consulta en Cuatrimestre II para verificar cada fórmula.
 */
public class Pre32CalcularAcumulados {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final ZoneId ZONA = ZoneId.of("America/El_Salvador");
    private static final int ANIO = 2028;

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final EtapaPreinversionRepository etapaPreinversionRepository;
    private final FuenteFinanciamientoEtapaPapRepository fuenteRepository;
    private final ProgCuatrimestralFinancieraRepository progRepository;
    private final AvanceFinancieroCuatrimestralRepository avanceRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final AvanceFinancieroPapService service;

    public Pre32CalcularAcumulados(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository, EtapaPreinversionRepository etapaPreinversionRepository,
            FuenteFinanciamientoEtapaPapRepository fuenteRepository,
            ProgCuatrimestralFinancieraRepository progRepository,
            AvanceFinancieroCuatrimestralRepository avanceRepository, MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository, EjeTematicoRepository ejeTematicoRepository,
            AvanceFinancieroPapService service) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.fuenteRepository = fuenteRepository;
        this.progRepository = progRepository;
        this.avanceRepository = avanceRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.service = service;
    }

    @Entonces("el sistema calcula \"AAP\" como la suma de lo Programado en los Cuatrimestres I, II y III \\(RN-E)")
    public void el_sistema_calcula_aap() {
        FilaAvanceFuenteDto fila = consultarFila();
        assertThat(fila.getAvanceAnualProgramado()).isEqualTo(3000d);
        terminar();
    }

    @Entonces("el sistema calcula el \"Avance Anual Ejecutado\" de cada cuatrimestre como la suma acumulada de los montos ejecutados hasta ese cuatrimestre \\(RN-E)")
    public void el_sistema_calcula_avance_anual_ejecutado_monto() {
        FilaAvanceFuenteDto fila = consultarFila();
        assertThat(fila.getAvanceAnualEjecutadoMonto()).isEqualTo(800d);
        terminar();
    }

    @Entonces("el sistema calcula el \"Avance Anual Ejecutado %\" de cada cuatrimestre dividiendo el monto ejecutado acumulado entre el Avance Anual Programado, multiplicado por 100 \\(RN-E)")
    public void el_sistema_calcula_avance_anual_ejecutado_porcentaje() {
        FilaAvanceFuenteDto fila = consultarFila();
        assertThat(fila.getAvanceAnualEjecutadoPorcentaje()).isCloseTo(26.67, within(0.01));
        terminar();
    }

    @Entonces("el sistema calcula el \"Avance al Cuatrimestre Ejecutado\" como el monto acumulado hasta el cuatrimestre en vigencia")
    public void el_sistema_calcula_avance_al_cuatrimestre_ejecutado() {
        FilaAvanceFuenteDto fila = consultarFila();
        assertThat(fila.getAvanceAlCuatrimestreEjecutadoMonto()).isEqualTo(800d);
    }

    @io.cucumber.java.es.Y("calcula su porcentaje dividiendo dicho monto entre lo programado acumulado hasta ese cuatrimestre, multiplicado por 100 \\(RN-E)")
    public void el_sistema_calcula_avance_al_cuatrimestre_porcentaje() {
        FilaAvanceFuenteDto fila = consultarFila();
        assertThat(fila.getAvanceAlCuatrimestreEjecutadoPorcentaje()).isCloseTo(40.0, within(0.01));
        terminar();
    }

    @Entonces("el sistema calcula el porcentaje del \"Avance del Cuatrimestre\" dividiendo el monto ejecutado del cuatrimestre entre el monto programado de ese mismo cuatrimestre, multiplicado por 100 \\(RN-E)")
    public void el_sistema_calcula_avance_del_cuatrimestre_porcentaje() {
        FilaAvanceFuenteDto fila = consultarFila();
        assertThat(fila.getPorcentajeEjecutadoCuatrimestre()).isCloseTo(30.0, within(0.01));
        terminar();
    }

    private FilaAvanceFuenteDto consultarFila() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-32C-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-32C-" + sufijo, "UE de prueba", institucion));

        String nombreUsuario = "urp.32c." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        MacroSector macrosector = macroSectorRepository.save(ProyectoFixtures.nuevoMacrosector("M32C" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository.save(ProyectoFixtures.nuevoSector("S32C" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository.save(ProyectoFixtures.nuevoEjeTematico("EJE-32C-" + sufijo, "Eje tematico de prueba"));
        String cup = proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc()
                .map(p -> String.format("%05d", Integer.parseInt(p.getCup()) + 1))
                .orElse("10000");
        Proyecto proyecto = Pre30Fixtures.nuevoEstudio(proyectoRepository, unidadEjecutora, institucion, sector, ejeTematico, cup);
        EtapaPreinversion etapa = Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, proyecto, TipoEtapaPreinversion.PERFIL, 100000.0);
        FuenteFinanciamientoEtapaPap fuente = fuenteRepository.save(FuenteFinanciamientoEtapaPap.builder()
                .etapaPreinversion(etapa).fuenteFinanciamiento(FuenteFinanciamiento.FONDO_GENERAL).build());
        ProgCuatrimestralFinanciera prog = progRepository.save(ProgCuatrimestralFinanciera.builder()
                .fuente(fuente).anio(ANIO)
                .montoCuatrimestre1(BigDecimal.valueOf(1000)).montoCuatrimestre2(BigDecimal.valueOf(1000))
                .montoCuatrimestre3(BigDecimal.valueOf(1000)).build());
        avanceRepository.save(AvanceFinancieroCuatrimestral.builder().programacion(prog)
                .cuatrimestre(Cuatrimestre.CUATRIMESTRE_I).montoEjecutado(BigDecimal.valueOf(500))
                .fechaRegistro(LocalDateTime.now(ZONA)).build());
        avanceRepository.save(AvanceFinancieroCuatrimestral.builder().programacion(prog)
                .cuatrimestre(Cuatrimestre.CUATRIMESTRE_II).montoEjecutado(BigDecimal.valueOf(300))
                .fechaRegistro(LocalDateTime.now(ZONA)).build());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        AvanceEstudioDto estudio = service.obtenerAvanceEstudio(proyecto.getCup(), ANIO, CuatrimestreDto.CUATRIMESTRE_II);
        return estudio.getEtapas().get(0).getFuentes().get(0);
    }

    private void terminar() {
        RequestContextHolder.resetRequestAttributes();
    }
}
