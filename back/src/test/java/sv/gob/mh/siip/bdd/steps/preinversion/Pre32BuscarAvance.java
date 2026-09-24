package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
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
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.FuenteFinanciamientoEtapaPap;
import sv.gob.mh.siip.model.preinversion.domain.AvanceFinancieroCuatrimestral;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralFinanciera;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceFinancieroPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
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

/** CU-PRE-32-buscar-avance.feature (RN-A.a, RN-B.a). */
public class Pre32BuscarAvance {

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

    private UnidadEjecutora unidadEjecutora;
    private Institucion institucion;
    private SectorActividad sector;
    private EjeTematico ejeTematico;
    private Proyecto proyectoActivo;
    private Proyecto proyectoFinalizado;
    private Proyecto proyectoOtroAnio;
    private AvanceFinancieroPAPResponseDto respuesta;
    private AvanceFinancieroPAPResponseDto respuestaOtroAnio;

    public Pre32BuscarAvance(InstitucionRepository institucionRepository,
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

    @Cuando("el Técnico URP selecciona la \"Unidad Ejecutora\", el \"Año\" y el \"Período\"")
    public void el_tecnico_urp_selecciona_los_filtros() {
        crearEscenarioYAutenticar();
    }

    @Y("hace clic en el botón \"Buscar\" avance-financiero")
    public void hace_clic_en_buscar() {
        respuesta = service.listar(unidadEjecutora.getId(), ANIO, CuatrimestreDto.CUATRIMESTRE_I, 0, 20);
    }

    @Entonces("el sistema muestra la tabla \"Ejecución Cuatrimestral del PAP\" filtrada según lo seleccionado")
    public void el_sistema_muestra_la_tabla_filtrada() {
        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getIdUnidadEjecutora()).isEqualTo(unidadEjecutora.getId());
        assertThat(respuesta.getContenido()).isNotEmpty();
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el campo \"Unidad Ejecutora\" muestra por defecto la del Técnico URP según credenciales")
    public void el_campo_unidad_ejecutora_muestra_la_del_tecnico_urp() {
        crearEscenarioYAutenticar();
        respuesta = service.listar(null, null, null, 0, 20);
        assertThat(respuesta.getIdUnidadEjecutora()).isEqualTo(unidadEjecutora.getId());
    }

    @Y("el campo \"Año\" muestra por defecto el año vigente")
    public void el_campo_anio_muestra_el_vigente() {
        assertThat(respuesta.getAnio()).isEqualTo(Year.now(ZONA).getValue());
    }

    @Y("el campo \"Período\" muestra por defecto el cuatrimestre vigente \\(RN-A.a)")
    public void el_campo_periodo_muestra_el_vigente() {
        assertThat(respuesta.getPeriodo()).isNotNull();
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("la tabla muestra únicamente los estudios activos para el ejercicio fiscal vigente al cuatrimestre del período de seguimiento, según CU-PRE-30 \\(RN-B.a)")
    public void la_tabla_muestra_unicamente_estudios_activos() {
        crearEscenarioYAutenticar();
        respuesta = service.listar(unidadEjecutora.getId(), ANIO, CuatrimestreDto.CUATRIMESTRE_I, 0, 200);
        assertThat(respuesta.getContenido()).anySatisfy(fila -> assertThat(fila.getCup()).isEqualTo(proyectoActivo.getCup()));
        assertThat(respuesta.getContenido()).noneSatisfy(fila -> assertThat(fila.getCup()).isEqualTo(proyectoFinalizado.getCup()));
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("que otro estudio de la Unidad Ejecutora tiene Programación Financiera únicamente en el año siguiente")
    public void que_otro_estudio_tiene_programacion_solo_en_el_anio_siguiente() {
        crearEscenarioYAutenticar();
        proyectoOtroAnio = Pre30Fixtures.nuevoEstudio(proyectoRepository, unidadEjecutora, institucion, sector,
                ejeTematico, siguienteCup());
        EtapaPreinversion etapa = Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, proyectoOtroAnio,
                TipoEtapaPreinversion.PREFACTIBILIDAD, 100000.0);
        FuenteFinanciamientoEtapaPap fuente = fuenteRepository.save(FuenteFinanciamientoEtapaPap.builder()
                .etapaPreinversion(etapa).fuenteFinanciamiento(FuenteFinanciamiento.DONACIONES).build());
        progRepository.save(ProgCuatrimestralFinanciera.builder().fuente(fuente).anio(ANIO + 1)
                .montoCuatrimestre1(BigDecimal.valueOf(100)).montoCuatrimestre2(BigDecimal.valueOf(200))
                .montoCuatrimestre3(BigDecimal.valueOf(300)).build());
    }

    @Cuando("el Técnico URP cambia el \"Año\" y el \"Período\" y hace clic en \"Buscar\" avance-financiero")
    public void el_tecnico_urp_cambia_anio_y_periodo() {
        respuesta = service.listar(unidadEjecutora.getId(), ANIO, CuatrimestreDto.CUATRIMESTRE_I, 0, 20);
        respuestaOtroAnio = service.listar(unidadEjecutora.getId(), ANIO + 1, CuatrimestreDto.CUATRIMESTRE_II, 0, 20);
    }

    @Entonces("la tabla y su paginación solo contienen los estudios programados en el año seleccionado \\(RN-A.a, RN-B.a)")
    public void la_tabla_y_su_paginacion_solo_contienen_el_anio_seleccionado() {
        // ANIO: solo la fuente con programación en ese año (ni la del año siguiente ni la del estudio
        // finalizado, que solo tiene programación en el año anterior).
        assertThat(respuesta.getAnio()).isEqualTo(ANIO);
        assertThat(respuesta.getContenido()).singleElement()
                .satisfies(fila -> assertThat(fila.getCup()).isEqualTo(proyectoActivo.getCup()));
        assertThat(respuesta.getPaginacion().getTotalElementos()).isEqualTo(1L);

        assertThat(respuestaOtroAnio.getAnio()).isEqualTo(ANIO + 1);
        assertThat(respuestaOtroAnio.getContenido()).singleElement()
                .satisfies(fila -> assertThat(fila.getCup()).isEqualTo(proyectoOtroAnio.getCup()));
        assertThat(respuestaOtroAnio.getPaginacion().getTotalElementos()).isEqualTo(1L);
    }

    @Y("las columnas del avance se calculan para el \"Período\" seleccionado \\(RN-E)")
    public void las_columnas_se_calculan_para_el_periodo_seleccionado() {
        assertThat(respuestaOtroAnio.getPeriodo()).isEqualTo(CuatrimestreDto.CUATRIMESTRE_II);
        var fila = respuestaOtroAnio.getContenido().get(0);
        assertThat(fila.getAvanceAnualProgramado()).isEqualTo(600d);
        assertThat(fila.getAvanceAlCuatrimestreProgramado()).isEqualTo(300d);
        assertThat(fila.getAvanceDelCuatrimestreProgramado()).isEqualTo(200d);
        RequestContextHolder.resetRequestAttributes();
    }

    private void crearEscenarioYAutenticar() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-32A-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-32A-" + sufijo, "UE de prueba", institucion));

        String nombreUsuario = "urp.32a." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        MacroSector macrosector = macroSectorRepository.save(ProyectoFixtures.nuevoMacrosector("M32A" + sufijo, "Macrosector de prueba"));
        sector = sectorActividadRepository.save(ProyectoFixtures.nuevoSector("S32A" + sufijo, "Sector de prueba", macrosector));
        ejeTematico = ejeTematicoRepository.save(ProyectoFixtures.nuevoEjeTematico("EJE-32A-" + sufijo, "Eje tematico de prueba"));

        String cup1 = siguienteCup();
        proyectoActivo = Pre30Fixtures.nuevoEstudio(proyectoRepository, unidadEjecutora, institucion, sector, ejeTematico, cup1);
        EtapaPreinversion etapaActiva = Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, proyectoActivo,
                TipoEtapaPreinversion.PERFIL, 100000.0);
        FuenteFinanciamientoEtapaPap fuenteActiva = fuenteRepository.save(FuenteFinanciamientoEtapaPap.builder()
                .etapaPreinversion(etapaActiva).fuenteFinanciamiento(FuenteFinanciamiento.FONDO_GENERAL).build());
        progRepository.save(ProgCuatrimestralFinanciera.builder().fuente(fuenteActiva).anio(ANIO)
                .montoCuatrimestre1(BigDecimal.valueOf(1000)).montoCuatrimestre2(BigDecimal.valueOf(1000))
                .montoCuatrimestre3(BigDecimal.valueOf(1000)).build());

        String cup2 = siguienteCup();
        proyectoFinalizado = Pre30Fixtures.nuevoEstudio(proyectoRepository, unidadEjecutora, institucion, sector, ejeTematico, cup2);
        EtapaPreinversion etapaFinalizada = Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, proyectoFinalizado,
                TipoEtapaPreinversion.PERFIL, 1000.0);
        FuenteFinanciamientoEtapaPap fuenteFinalizada = fuenteRepository.save(FuenteFinanciamientoEtapaPap.builder()
                .etapaPreinversion(etapaFinalizada).fuenteFinanciamiento(FuenteFinanciamiento.FONDO_GENERAL).build());
        ProgCuatrimestralFinanciera progAnioAnterior = progRepository.save(ProgCuatrimestralFinanciera.builder()
                .fuente(fuenteFinalizada).anio(ANIO - 1).montoCuatrimestre1(BigDecimal.valueOf(1000)).build());
        avanceRepository.save(AvanceFinancieroCuatrimestral.builder()
                .programacion(progAnioAnterior).cuatrimestre(Cuatrimestre.CUATRIMESTRE_I)
                .montoEjecutado(BigDecimal.valueOf(1000)).fechaRegistro(LocalDateTime.now(ZONA)).build());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private String siguienteCup() {
        return proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc()
                .map(p -> String.format("%05d", Integer.parseInt(p.getCup()) + 1))
                .orElse("10000");
    }
}
