package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import sv.gob.mh.siip.bdd.support.PeriodoEjecucionPapBdd;
import sv.gob.mh.siip.bdd.support.Pre30Fixtures;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.bdd.support.SufijosPrueba;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;
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
import sv.gob.mh.siip.model.preinversion.dto.AvanceFinancieroPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioFilaAvancePAPDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaAvanceDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaAvanceRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaAvanceFuenteDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaAvanceFuenteRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FuenteFinanciamientoDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarAvanceEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
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
import sv.gob.mh.siip.security.AutenticacionDePrueba;

/**
 * CU-PRE-32-avance-financiero-pap.feature (pantalla del front, Anexos A.1/A.2): consulta del avance
 * por etapa y fuente con los filtros por defecto (RN-A.a), cambio de cuatrimestre, registro del
 * avance de un estudio, alerta de exceso sobre el costo de la etapa (RN-B.b) y reporte (SF-3,
 * RN-A.b). Los pasos con el mismo texto en otras features llegan por {@link PantallasFrontComun}.
 *
 * <p>Los datos se crean en el año y cuatrimestre vigentes, porque la pantalla abre con esos filtros.
 * El estudio tiene dos etapas: Perfil con dos fuentes (Fondo General y Préstamos Externos) y
 * Factibilidad con una (Fondo General), es decir, tres filas en el Anexo A.1.
 */
public class Pre32AvanceFinancieroPap implements PantallaFront {

    private static final String FEATURE = "CU-PRE-32-avance-financiero-pap.feature";
    private static final String PANTALLA = "Avance Cuatrimestral Financiero del PAP";
    private static final ZoneId ZONA = ZoneId.of("America/El_Salvador");
    private static final int MESES_POR_CUATRIMESTRE = 4;
    private static final int DECIMALES_PORCENTAJE = 2;
    private static final double CIEN = 100d;
    /** Programado por cuatrimestre (I, II, III) de Perfil / Fondo General: distinto en cada uno. */
    private static final double[] PROGRAMADO_PERFIL_FG = {1000d, 2000d, 4000d};
    private static final double PROGRAMADO_ANUAL_PERFIL_FG = 7000d;
    private static final double EJECUTADO_VIGENTE = 500d;
    private static final double EJECUTADO_OTRO = 200d;
    private static final double EJECUTADO_NUEVO = 800d;
    private static final double EJECUTADO_EXCESO = 1500d;
    private static final double COSTO_ETAPA_REDUCIDO = 1000d;
    private static final String OBSERVACION = "Avance del cuatrimestre registrado desde la pantalla (BDD).";

    private final PantallasFrontComun comun;
    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final EtapaPreinversionRepository etapaPreinversionRepository;
    private final FuenteFinanciamientoEtapaPapRepository fuenteRepository;
    private final ProgCuatrimestralFinancieraRepository progRepository;
    private final AvanceFinancieroCuatrimestralRepository avanceRepository;
    private final CalendarioEventoRepository calendarioEventoRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final AvanceFinancieroPapService service;

    private int anio;
    private Cuatrimestre vigente;
    private Cuatrimestre otro;
    private PeriodoEjecucionPapBdd periodoVigente;
    private UnidadEjecutora unidadEjecutora;
    private Proyecto proyecto;
    private EtapaPreinversion etapaPerfil;
    private FuenteFinanciamientoEtapaPap fuentePerfilFg;
    private ProgCuatrimestralFinanciera progPerfilFg;
    private AvanceFinancieroPAPResponseDto listado;
    private AvanceEstudioDto estudio;
    private GuardarAvanceEstudioRequestDto requestGuardar;
    private Resource reporte;

    public Pre32AvanceFinancieroPap(PantallasFrontComun comun, InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository, EtapaPreinversionRepository etapaPreinversionRepository,
            FuenteFinanciamientoEtapaPapRepository fuenteRepository,
            ProgCuatrimestralFinancieraRepository progRepository,
            AvanceFinancieroCuatrimestralRepository avanceRepository,
            CalendarioEventoRepository calendarioEventoRepository, MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository, EjeTematicoRepository ejeTematicoRepository,
            AvanceFinancieroPapService service) {
        this.comun = comun;
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.fuenteRepository = fuenteRepository;
        this.progRepository = progRepository;
        this.avanceRepository = avanceRepository;
        this.calendarioEventoRepository = calendarioEventoRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.service = service;
    }

    @Before
    public void activar(Scenario scenario) {
        if (comun.activarSi(scenario, FEATURE, this)) {
            anio = Year.now(ZONA).getValue();
            vigente = cuatrimestreVigente();
            otro = vigente == Cuatrimestre.CUATRIMESTRE_I ? Cuatrimestre.CUATRIMESTRE_II : Cuatrimestre.CUATRIMESTRE_I;
            periodoVigente = new PeriodoEjecucionPapBdd(calendarioEventoRepository, anio, vigente.numero());
            periodoVigente.asegurarAbierto();
        }
    }

    @After
    public void limpiar(Scenario scenario) {
        if (PantallasFrontComun.esDeLaFeature(scenario, FEATURE)) {
            periodoVigente.restaurar();
            RequestContextHolder.resetRequestAttributes();
        }
    }

    // ---- Pasos compartidos (llegan por PantallasFrontComun) ----

    @Override
    public void tecnicoUrpEnPantalla(String pantalla) {
        assertThat(pantalla).isEqualTo(PANTALLA);
        crearEscenarioYAutenticar();
        // La pantalla abre sin filtros: el servidor aplica el año y el cuatrimestre vigentes (RN-A.a).
        listado = service.listar(null, null, null, 0, 20);
    }

    @Override
    public void filaPorEtapaYFuente() {
        List<EstudioFilaAvancePAPDto> filas = filasDelEstudio(listado);
        assertThat(filas).hasSize(3);
        assertThat(filas).extracting((EstudioFilaAvancePAPDto f) -> f.getEtapa() + "/" + f.getFuenteFinanciamiento())
                .containsExactlyInAnyOrder(
                        NombreEtapaDto.PERFIL + "/" + FuenteFinanciamientoDto.FONDO_GENERAL,
                        NombreEtapaDto.PERFIL + "/" + FuenteFinanciamientoDto.PRESTAMOS_EXTERNOS,
                        NombreEtapaDto.FACTIBILIDAD + "/" + FuenteFinanciamientoDto.FONDO_GENERAL);
    }

    @Override
    public void clicCupDeEstudio() {
        estudio = service.obtenerAvanceEstudio(proyecto.getCup(), anio, dto(vigente));
    }

    @Override
    public void haceClic(String boton) {
        assertThat(boton).isEqualTo("Guardar");
        estudio = service.guardarAvanceEstudio(proyecto.getCup(), anio, dto(vigente), requestGuardar);
    }

    @Override
    public void guardaAvance() {
        AvanceFinancieroCuatrimestral avance = avanceRepository
                .findByProgramacionIdAndCuatrimestre(progPerfilFg.getId(), vigente).orElseThrow();
        assertThat(avance.getMontoEjecutado()).isEqualByComparingTo(BigDecimal.valueOf(EJECUTADO_NUEVO));
        assertThat(avance.getObservaciones()).isEqualTo(OBSERVACION);

        EstudioFilaAvancePAPDto fila = filaPerfilFondoGeneral(service.listar(null, anio, dto(vigente), 0, 20));
        assertThat(fila.getAvanceDelCuatrimestreEjecutadoMonto()).isEqualTo(EJECUTADO_NUEVO);
        assertThat(fila.getObservaciones()).isEqualTo(OBSERVACION);
    }

    @Override
    public void actorHaceClic(String boton) {
        assertThat(boton).isEqualTo("Generar reporte");
        reporte = service.generarReporte(unidadEjecutora.getId(), anio, dto(vigente), "EXCEL");
    }

    @Override
    public void reporteDelAnioYCuatrimestre() {
        List<List<String>> celdas = leerCeldas(reporte);
        String romano = vigente.name().replace("CUATRIMESTRE_", "");
        assertThat(celdas.get(0).get(0))
                .contains("AL CUATRIMESTRE " + romano + " ")
                .contains(String.valueOf(anio));
        assertThat(celdas).filteredOn((List<String> fila) -> !fila.isEmpty() && proyecto.getCup().equals(fila.get(0)))
                .hasSize(3);
    }

    // ---- Pasos propios de la feature ----

    @Y("muestra lo programado y lo ejecutado del año y del cuatrimestre, con su porcentaje")
    public void muestra_programado_y_ejecutado_con_porcentaje() {
        EstudioFilaAvancePAPDto fila = filaPerfilFondoGeneral(listado);
        double programadoDelCuatrimestre = PROGRAMADO_PERFIL_FG[vigente.ordinal()];
        double ejecutadoEnElAnio = EJECUTADO_VIGENTE + (otro.ordinal() < vigente.ordinal() ? EJECUTADO_OTRO : 0d);
        assertThat(fila.getAvanceAnualProgramado()).isEqualTo(PROGRAMADO_ANUAL_PERFIL_FG);
        assertThat(fila.getAvanceAnualEjecutadoMonto()).isEqualTo(ejecutadoEnElAnio);
        assertThat(fila.getAvanceAnualEjecutadoPorcentaje())
                .isEqualTo(porcentaje(ejecutadoEnElAnio, PROGRAMADO_ANUAL_PERFIL_FG));
        assertThat(fila.getAvanceDelCuatrimestreProgramado()).isEqualTo(programadoDelCuatrimestre);
        assertThat(fila.getAvanceDelCuatrimestreEjecutadoMonto()).isEqualTo(EJECUTADO_VIGENTE);
        assertThat(fila.getAvanceDelCuatrimestrePorcentaje())
                .isEqualTo(porcentaje(EJECUTADO_VIGENTE, programadoDelCuatrimestre));
    }

    @Y("toma por defecto el año y el cuatrimestre vigentes \\(RN-A.a)")
    public void toma_por_defecto_anio_y_cuatrimestre_vigentes() {
        assertThat(listado.getAnio()).isEqualTo(anio);
        assertThat(listado.getPeriodo()).isEqualTo(dto(vigente));
        // El Técnico URP queda en su propia unidad ejecutora aunque no la envíe.
        assertThat(listado.getIdUnidadEjecutora()).isEqualTo(unidadEjecutora.getId());
    }

    @Cuando("el Técnico URP elige otro cuatrimestre")
    public void elige_otro_cuatrimestre() {
        listado = service.listar(null, anio, dto(otro), 0, 20);
    }

    @Entonces("el sistema muestra el avance de ese cuatrimestre")
    public void muestra_el_avance_de_ese_cuatrimestre() {
        assertThat(listado.getPeriodo()).isEqualTo(dto(otro));
        EstudioFilaAvancePAPDto fila = filaPerfilFondoGeneral(listado);
        assertThat(fila.getAvanceDelCuatrimestreProgramado()).isEqualTo(PROGRAMADO_PERFIL_FG[otro.ordinal()]);
        assertThat(fila.getAvanceDelCuatrimestreEjecutadoMonto()).isEqualTo(EJECUTADO_OTRO);
    }

    @Entonces("el sistema abre el avance de ese estudio con sus fuentes \\(Anexo A.2)")
    public void abre_el_avance_del_estudio_con_sus_fuentes() {
        assertThat(estudio.getCup()).isEqualTo(proyecto.getCup());
        assertThat(estudio.getEtapas()).extracting(EtapaAvanceDto::getEtapa)
                .containsExactly(NombreEtapaDto.PERFIL, NombreEtapaDto.FACTIBILIDAD);
        assertThat(etapaDto(NombreEtapaDto.PERFIL).getFuentes())
                .extracting(FilaAvanceFuenteDto::getFuenteFinanciamiento)
                .containsExactlyInAnyOrder(FuenteFinanciamientoDto.FONDO_GENERAL,
                        FuenteFinanciamientoDto.PRESTAMOS_EXTERNOS);
        assertThat(filaDetallePerfilFg().getMontoEjecutadoCuatrimestre()).isEqualTo(EJECUTADO_VIGENTE);
    }

    @Cuando("escribe el monto ejecutado del cuatrimestre y su observación")
    public void escribe_monto_ejecutado_y_observacion() {
        requestGuardar = solicitud(EJECUTADO_NUEVO, OBSERVACION);
    }

    @Y("muestra el porcentaje ejecutado que calcula el servidor")
    public void muestra_el_porcentaje_ejecutado_calculado() {
        FilaAvanceFuenteDto fila = filaDetallePerfilFg();
        assertThat(fila.getMontoEjecutadoCuatrimestre()).isEqualTo(EJECUTADO_NUEVO);
        assertThat(fila.getPorcentajeEjecutadoCuatrimestre())
                .isEqualTo(porcentaje(EJECUTADO_NUEVO, PROGRAMADO_PERFIL_FG[vigente.ordinal()]));
    }

    @Dado("que una fuente ejecutó más de lo programado")
    public void que_una_fuente_ejecuto_mas_de_lo_programado() {
        // RN-B.b: el acumulado ejecutado de la etapa supera el "Costo de la Etapa" programado en CU-PRE-30.
        etapaPerfil.setCosto(COSTO_ETAPA_REDUCIDO);
        etapaPreinversionRepository.save(etapaPerfil);
        service.guardarAvanceEstudio(proyecto.getCup(), anio, dto(vigente),
                solicitud(EJECUTADO_EXCESO, "Ejecutado por encima del costo de la etapa (BDD)"));
        listado = service.listar(null, anio, dto(vigente), 0, 20);
    }

    @Entonces("el sistema señala esa fila con la alerta de exceso")
    public void senala_la_fila_con_la_alerta_de_exceso() {
        List<EstudioFilaAvancePAPDto> filas = filasDelEstudio(listado);
        assertThat(filas).filteredOn((EstudioFilaAvancePAPDto f) -> f.getEtapa() == NombreEtapaDto.PERFIL)
                .hasSize(2)
                .allSatisfy((EstudioFilaAvancePAPDto f) -> assertThat(f.getAlertaExcesoProgramado()).isTrue());
        assertThat(filas).filteredOn((EstudioFilaAvancePAPDto f) -> f.getEtapa() == NombreEtapaDto.FACTIBILIDAD)
                .singleElement()
                .satisfies((EstudioFilaAvancePAPDto f) -> assertThat(f.getAlertaExcesoProgramado()).isFalse());
    }

    @Y("lo hace aun fuera del período del Calendario de Eventos del PAP \\(RN-A.b)")
    public void lo_hace_aun_fuera_del_periodo_del_calendario() {
        periodoVigente.cerrar();
        String cup = proyecto.getCup();
        CuatrimestreDto periodo = dto(vigente);
        GuardarAvanceEstudioRequestDto request = solicitud(EJECUTADO_NUEVO, OBSERVACION);
        assertThatThrownBy(() -> service.guardarAvanceEstudio(cup, anio, periodo, request))
                .isInstanceOf(ConflictoEstadoException.class);

        reporte = service.generarReporte(unidadEjecutora.getId(), anio, periodo, "EXCEL");
        reporteDelAnioYCuatrimestre();
    }

    // ---- Apoyo ----

    private void crearEscenarioYAutenticar() {
        String sufijo = SufijosPrueba.nuevo(8);
        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-32P-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-32P-" + sufijo, "UE de prueba", institucion));

        String nombreUsuario = "urp.32p." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M32P" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S32P" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-32P-" + sufijo, "Eje tematico de prueba"));
        String cup = proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc()
                .map((Proyecto p) -> String.format("%05d", Integer.parseInt(p.getCup()) + 1))
                .orElse("10000");
        proyecto = Pre30Fixtures.nuevoEstudio(proyectoRepository, unidadEjecutora, institucion, sector,
                ejeTematico, cup);

        etapaPerfil = Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, proyecto,
                TipoEtapaPreinversion.PERFIL, 100000.0);
        fuentePerfilFg = nuevaFuente(etapaPerfil, FuenteFinanciamiento.FONDO_GENERAL);
        progPerfilFg = nuevaProgramacion(fuentePerfilFg, PROGRAMADO_PERFIL_FG);
        nuevaProgramacion(nuevaFuente(etapaPerfil, FuenteFinanciamiento.PRESTAMOS_EXTERNOS),
                new double[] {500d, 500d, 500d});
        EtapaPreinversion etapaFactibilidad = Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, proyecto,
                TipoEtapaPreinversion.FACTIBILIDAD, 100000.0);
        nuevaProgramacion(nuevaFuente(etapaFactibilidad, FuenteFinanciamiento.FONDO_GENERAL),
                new double[] {1000d, 1000d, 1000d});

        registrarAvance(vigente, EJECUTADO_VIGENTE);
        registrarAvance(otro, EJECUTADO_OTRO);

        MockHttpServletRequest request = new MockHttpServletRequest();
        AutenticacionDePrueba.autenticar(nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private FuenteFinanciamientoEtapaPap nuevaFuente(EtapaPreinversion etapa, FuenteFinanciamiento fuente) {
        return fuenteRepository.save(FuenteFinanciamientoEtapaPap.builder()
                .etapaPreinversion(etapa).fuenteFinanciamiento(fuente).build());
    }

    private ProgCuatrimestralFinanciera nuevaProgramacion(FuenteFinanciamientoEtapaPap fuente, double[] montos) {
        return progRepository.save(ProgCuatrimestralFinanciera.builder()
                .fuente(fuente).anio(anio)
                .montoCuatrimestre1(BigDecimal.valueOf(montos[0]))
                .montoCuatrimestre2(BigDecimal.valueOf(montos[1]))
                .montoCuatrimestre3(BigDecimal.valueOf(montos[2]))
                .build());
    }

    private void registrarAvance(Cuatrimestre periodo, double monto) {
        avanceRepository.save(AvanceFinancieroCuatrimestral.builder()
                .programacion(progPerfilFg)
                .cuatrimestre(periodo)
                .montoEjecutado(BigDecimal.valueOf(monto))
                .fechaRegistro(LocalDateTime.now(ZONA))
                .build());
    }

    private GuardarAvanceEstudioRequestDto solicitud(double monto, String observacion) {
        return new GuardarAvanceEstudioRequestDto()
                .addEtapasItem(new EtapaAvanceRequestDto(NombreEtapaDto.PERFIL)
                        .addFuentesItem(new FilaAvanceFuenteRequestDto(fuentePerfilFg.getId(), monto)
                                .observacionesCuatrimestre(observacion)));
    }

    private List<EstudioFilaAvancePAPDto> filasDelEstudio(AvanceFinancieroPAPResponseDto respuesta) {
        return respuesta.getContenido().stream()
                .filter((EstudioFilaAvancePAPDto f) -> proyecto.getCup().equals(f.getCup()))
                .toList();
    }

    private EstudioFilaAvancePAPDto filaPerfilFondoGeneral(AvanceFinancieroPAPResponseDto respuesta) {
        return filasDelEstudio(respuesta).stream()
                .filter((EstudioFilaAvancePAPDto f) -> f.getEtapa() == NombreEtapaDto.PERFIL
                        && f.getFuenteFinanciamiento() == FuenteFinanciamientoDto.FONDO_GENERAL)
                .findFirst()
                .orElseThrow();
    }

    private EtapaAvanceDto etapaDto(NombreEtapaDto nombre) {
        return estudio.getEtapas().stream()
                .filter((EtapaAvanceDto e) -> e.getEtapa() == nombre)
                .findFirst()
                .orElseThrow();
    }

    private FilaAvanceFuenteDto filaDetallePerfilFg() {
        return etapaDto(NombreEtapaDto.PERFIL).getFuentes().stream()
                .filter((FilaAvanceFuenteDto f) -> fuentePerfilFg.getId().equals(f.getIdFuente()))
                .findFirst()
                .orElseThrow();
    }

    private static CuatrimestreDto dto(Cuatrimestre periodo) {
        return CuatrimestreDto.valueOf(periodo.name());
    }

    /** RN-A.a: cuatrimestre vigente según el mes actual (I: ene-abr, II: may-ago, III: sep-dic). */
    private static Cuatrimestre cuatrimestreVigente() {
        return Cuatrimestre.values()[(LocalDate.now(ZONA).getMonthValue() - 1) / MESES_POR_CUATRIMESTRE];
    }

    private static double porcentaje(double monto, double total) {
        return BigDecimal.valueOf(monto * CIEN)
                .divide(BigDecimal.valueOf(total), DECIMALES_PORCENTAJE, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private static List<List<String>> leerCeldas(Resource recurso) {
        List<List<String>> resultado = new ArrayList<>();
        try (InputStream entrada = recurso.getInputStream(); XSSFWorkbook workbook = new XSSFWorkbook(entrada)) {
            XSSFSheet hoja = workbook.getSheetAt(0);
            for (int i = 0; i <= hoja.getLastRowNum(); i++) {
                resultado.add(valoresDeFila(hoja.getRow(i)));
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer el reporte Excel generado.", e);
        }
        return resultado;
    }

    private static List<String> valoresDeFila(Row row) {
        List<String> valores = new ArrayList<>();
        if (row != null) {
            for (int j = 0; j < row.getLastCellNum(); j++) {
                Cell celda = row.getCell(j);
                if (celda == null) {
                    valores.add("");
                } else if (celda.getCellType() == CellType.STRING) {
                    valores.add(celda.getStringCellValue());
                } else {
                    valores.add(String.valueOf(celda.getNumericCellValue()));
                }
            }
        }
        return valores;
    }
}
