package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
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
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import sv.gob.mh.siip.bdd.support.PeriodoEjecucionPapBdd;
import sv.gob.mh.siip.bdd.support.Pre30Fixtures;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.bdd.support.SufijosPrueba;
import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.AvanceCuatriMetaFisica;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralMetaFisica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceMetasEstudioDto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceMetasFisicasPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.EnviarObservacionesAvanceDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoAvanceMetasDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoRevisionAvancePAPDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioFilaAvanceMetasDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaAvanceMetasDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaAvanceMetasRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FinalizarRevisionAvanceRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarAvanceMetasEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarObservacionesAvanceDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarRespuestaInstitucionAvanceRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RevisionAvancePAPDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.AvanceCuatriMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.AvanceMetasFisicasPapRevisionService;
import sv.gob.mh.siip.model.preinversion.service.AvanceMetasFisicasPapService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;
import sv.gob.mh.siip.security.AutenticacionDePrueba;

/**
 * CU-PRE-33-avance-metas-fisicas-pap.feature (pantalla del front, Anexos A.1/A.2): consulta del
 * avance de metas por estudio y etapa con su estado (RN-B.c), registro del avance de un estudio,
 * ciclo de revisión DGICP (SF-2) y reporte. Los pasos con el mismo texto en otras features llegan
 * por {@link PantallasFrontComun}.
 *
 * <p>Los datos se crean en el año y cuatrimestre vigentes (la pantalla abre con esos filtros). El
 * estudio tiene dos etapas programadas al 25% por cuatrimestre: Perfil, con 25% ejecutado en cada
 * cuatrimestre hasta el vigente ("A tiempo"), y Factibilidad, con 10% solo en el Cuatrimestre I
 * ("Atrasado").
 */
public class Pre33AvanceMetasFisicasPap implements PantallaFront {

    private static final String FEATURE = "CU-PRE-33-avance-metas-fisicas-pap.feature";
    private static final String PANTALLA = "Informe de avance cuatrimestral de metas del PAP";
    private static final ZoneId ZONA = ZoneId.of("America/El_Salvador");
    private static final int MESES_POR_CUATRIMESTRE = 4;
    private static final double PROGRAMADO_CUATRIMESTRE = 25d;
    private static final double PROGRAMADO_ANUAL = 75d;
    private static final double EJECUTADO_FACTIBILIDAD = 10d;
    private static final double AVANCE_NUEVO = 20d;
    private static final String OBSERVACION = "Avance de metas registrado desde la pantalla (BDD).";
    private static final String OBSERVACIONES_DGICP = "Revisar el avance reportado de la etapa Perfil (BDD).";
    private static final String RESPUESTA = "Se ajustó el avance según lo observado (BDD).";
    private static final String BOTON_FINALIZAR = "Finalizar revisión";

    private final PantallasFrontComun comun;
    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final EtapaPreinversionRepository etapaPreinversionRepository;
    private final EtapaMetaFisicaPapRepository etapaMetaRepository;
    private final ProgCuatrimestralMetaFisicaRepository progRepository;
    private final AvanceCuatriMetaFisicaRepository avanceRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final AvanceMetasFisicasPapService service;
    private final AvanceMetasFisicasPapRevisionService revisionService;
    private final CalendarioEventoRepository calendarioEventoRepository;

    private int anio;
    private Cuatrimestre vigente;
    private PeriodoEjecucionPapBdd periodoVigente;
    private Institucion institucion;
    private UnidadEjecutora unidadEjecutora;
    private String nombreUsuarioUrp;
    private Proyecto proyecto;
    private ProgCuatrimestralMetaFisica progPerfil;
    private AvanceMetasFisicasPAPResponseDto listado;
    private AvanceMetasEstudioDto estudio;
    private GuardarAvanceMetasEstudioRequestDto requestGuardar;
    private RevisionAvancePAPDto revision;
    private Resource reporte;

    public Pre33AvanceMetasFisicasPap(PantallasFrontComun comun, InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository, EtapaPreinversionRepository etapaPreinversionRepository,
            EtapaMetaFisicaPapRepository etapaMetaRepository, ProgCuatrimestralMetaFisicaRepository progRepository,
            AvanceCuatriMetaFisicaRepository avanceRepository, MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository, EjeTematicoRepository ejeTematicoRepository,
            AvanceMetasFisicasPapService service, AvanceMetasFisicasPapRevisionService revisionService,
            CalendarioEventoRepository calendarioEventoRepository) {
        this.comun = comun;
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.etapaMetaRepository = etapaMetaRepository;
        this.progRepository = progRepository;
        this.avanceRepository = avanceRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.service = service;
        this.revisionService = revisionService;
        this.calendarioEventoRepository = calendarioEventoRepository;
    }

    @Before
    public void activar(Scenario scenario) {
        if (comun.activarSi(scenario, FEATURE, this)) {
            anio = Year.now(ZONA).getValue();
            vigente = Cuatrimestre.values()[(LocalDate.now(ZONA).getMonthValue() - 1) / MESES_POR_CUATRIMESTRE];
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
        assertThat(listado.getAnio()).isEqualTo(anio);
        assertThat(listado.getPeriodo()).isEqualTo(dto(vigente));
    }

    @Override
    public void filaPorEstudioYEtapa() {
        assertThat(filasDelEstudio()).extracting(EstudioFilaAvanceMetasDto::getEtapa)
                .containsExactlyInAnyOrder(NombreEtapaDto.PERFIL, NombreEtapaDto.FACTIBILIDAD);
    }

    @Override
    public void clicCupDeEstudio() {
        estudio = service.obtenerAvanceMetasEstudio(proyecto.getCup(), anio, dto(vigente));
    }

    @Override
    public void haceClic(String boton) {
        assertThat(boton).isEqualTo("Guardar");
        estudio = service.guardarAvanceMetasEstudio(proyecto.getCup(), anio, dto(vigente), requestGuardar);
    }

    @Override
    public void guardaAvance() {
        AvanceCuatriMetaFisica avance = avanceRepository
                .findByProgramacionMetaIdAndCuatrimestre(progPerfil.getId(), vigente).orElseThrow();
        assertThat(avance.getAvanceCuatrimestre()).isEqualByComparingTo(BigDecimal.valueOf(AVANCE_NUEVO));
        assertThat(avance.getObservaciones()).isEqualTo(OBSERVACION);

        listado = service.listar(null, anio, dto(vigente), 0, 20);
        EstudioFilaAvanceMetasDto fila = filaDe(NombreEtapaDto.PERFIL);
        assertThat(fila.getEjecutadoDelCuatrimestre()).isEqualTo(AVANCE_NUEVO);
        assertThat(fila.getObservaciones()).isEqualTo(OBSERVACION);
    }

    @Override
    public void entraTecnicoPreOCoordinadorPre() {
        autenticarComo(crearUsuario(RolUsuario.TECNICO_PRE, "pre.33p"));
    }

    @Override
    public void escribeObservacionesYClic(String boton) {
        assertThat(boton).isEqualTo("Enviar observaciones");
        revisionService.registrarObservacionesAvanceDgicp(new RegistrarObservacionesAvanceDgicpRequestDto(
                unidadEjecutora.getId(), anio, dto(vigente), OBSERVACIONES_DGICP));
        revision = revisionService.enviarObservacionesAvanceDgicp(
                new EnviarObservacionesAvanceDgicpRequestDto(unidadEjecutora.getId(), anio, dto(vigente)));
        assertThat(revision.getEstado()).isEqualTo(EstadoRevisionAvancePAPDto.OBSERVADO);
        assertThat(revision.getObservacionesDgicp()).isEqualTo(OBSERVACIONES_DGICP);
        assertThat(revision.getFechaObservaciones()).isNotNull();
    }

    @Override
    public void actorHaceClic(String boton) {
        assertThat(boton).isEqualTo("Generar reporte");
        reporte = service.generarReporteAvanceMetas(unidadEjecutora.getId(), anio, dto(vigente), "EXCEL");
    }

    @Override
    public void reporteDelAnioYCuatrimestre() {
        List<List<String>> celdas = leerCeldas(reporte);
        assertThat(celdas.get(0).get(0)).contains("Año " + anio).endsWith(vigente.name());
        assertThat(celdas).filteredOn((List<String> fila) -> !fila.isEmpty() && proyecto.getCup().equals(fila.get(0)))
                .hasSize(2);
    }

    // ---- Pasos propios de la feature ----

    @Y("muestra lo programado y lo ejecutado del año y del cuatrimestre, y el total de la meta ejecutada")
    public void muestra_programado_ejecutado_y_total_meta() {
        int cuatrimestresHastaVigente = vigente.numero();
        EstudioFilaAvanceMetasDto perfil = filaDe(NombreEtapaDto.PERFIL);
        double ejecutadoPerfil = PROGRAMADO_CUATRIMESTRE * cuatrimestresHastaVigente;
        assertThat(perfil.getProgramadoEnElAnio()).isEqualTo(PROGRAMADO_ANUAL);
        assertThat(perfil.getEjecutadoEnElAnio()).isEqualTo(ejecutadoPerfil);
        assertThat(perfil.getProgramadoAlCuatrimestre()).isEqualTo(ejecutadoPerfil);
        assertThat(perfil.getProgramadoDelCuatrimestre()).isEqualTo(PROGRAMADO_CUATRIMESTRE);
        assertThat(perfil.getEjecutadoDelCuatrimestre()).isEqualTo(PROGRAMADO_CUATRIMESTRE);
        assertThat(perfil.getTotalMetaEjecutada()).isEqualTo(ejecutadoPerfil);

        EstudioFilaAvanceMetasDto factibilidad = filaDe(NombreEtapaDto.FACTIBILIDAD);
        assertThat(factibilidad.getProgramadoEnElAnio()).isEqualTo(PROGRAMADO_ANUAL);
        assertThat(factibilidad.getEjecutadoEnElAnio()).isEqualTo(EJECUTADO_FACTIBILIDAD);
        assertThat(factibilidad.getTotalMetaEjecutada()).isEqualTo(EJECUTADO_FACTIBILIDAD);
    }

    @Y("muestra el estado del avance de cada etapa")
    public void muestra_el_estado_del_avance_de_cada_etapa() {
        assertThat(filaDe(NombreEtapaDto.PERFIL).getEstado()).isEqualTo(EstadoAvanceMetasDto.A_TIEMPO);
        assertThat(filaDe(NombreEtapaDto.FACTIBILIDAD).getEstado()).isEqualTo(EstadoAvanceMetasDto.ATRASADO);
    }

    @Entonces("el sistema abre el avance de metas de ese estudio \\(Anexo A.2)")
    public void abre_el_avance_de_metas_del_estudio() {
        assertThat(estudio.getCup()).isEqualTo(proyecto.getCup());
        assertThat(estudio.getEtapas()).extracting(EtapaAvanceMetasDto::getEtapa)
                .containsExactly(NombreEtapaDto.PERFIL, NombreEtapaDto.FACTIBILIDAD);
        assertThat(etapaDto(NombreEtapaDto.PERFIL).getAvanceCuatrimestre()).isEqualTo(PROGRAMADO_CUATRIMESTRE);
    }

    @Cuando("escribe el avance del cuatrimestre y su observación")
    public void escribe_el_avance_y_su_observacion() {
        requestGuardar = new GuardarAvanceMetasEstudioRequestDto()
                .addEtapasItem(new EtapaAvanceMetasRequestDto(NombreEtapaDto.PERFIL, AVANCE_NUEVO)
                        .observacionesCuatrimestre(OBSERVACION));
    }

    @Y("muestra el total de la meta ejecutada y el estado que calcula el servidor")
    public void muestra_total_meta_y_estado_calculados() {
        EtapaAvanceMetasDto perfil = etapaDto(NombreEtapaDto.PERFIL);
        double esperado = PROGRAMADO_CUATRIMESTRE * (vigente.numero() - 1) + AVANCE_NUEVO;
        assertThat(perfil.getAvanceCuatrimestre()).isEqualTo(AVANCE_NUEVO);
        assertThat(perfil.getTotalMetaEjecutada()).isEqualTo(esperado);
        // Lo ejecutado al cuatrimestre queda 5 puntos por debajo de lo programado al cuatrimestre.
        assertThat(perfil.getEstado()).isEqualTo(EstadoAvanceMetasDto.ATRASADO);
    }

    @Entonces("el Técnico URP puede escribir y enviar su respuesta")
    public void el_tecnico_urp_puede_escribir_y_enviar_su_respuesta() {
        autenticarComo(nombreUsuarioUrp);
        RevisionAvancePAPDto registrada = revisionService.registrarRespuestaInstitucionAvance(
                new RegistrarRespuestaInstitucionAvanceRequestDto(unidadEjecutora.getId(), anio, dto(vigente),
                        RESPUESTA));
        assertThat(registrada.getObservacionesDgicp()).isEqualTo(OBSERVACIONES_DGICP);
        revision = revisionService.enviarRespuestaInstitucionAvance(
                new EnviarObservacionesAvanceDgicpRequestDto(unidadEjecutora.getId(), anio, dto(vigente)));
        assertThat(revision.getRespuestaInstitucion()).isEqualTo(RESPUESTA);
        assertThat(revision.getFechaRespuesta()).isNotNull();
    }

    @Y("solo la DGICP puede hacer clic en {string}")
    public void solo_la_dgicp_puede_hacer_clic_en(String boton) {
        assertThat(boton).isEqualTo(BOTON_FINALIZAR);
        FinalizarRevisionAvanceRequestDto request =
                new FinalizarRevisionAvanceRequestDto(unidadEjecutora.getId(), anio, dto(vigente));

        autenticarComo(nombreUsuarioUrp);
        assertThatThrownBy(() -> revisionService.finalizarRevisionAvance(request))
                .isInstanceOf(AccesoDenegadoException.class);

        autenticarComo(crearUsuario(RolUsuario.TECNICO_PRE, "pre.33q"));
        assertThat(revisionService.finalizarRevisionAvance(request).getEstado())
                .isEqualTo(EstadoRevisionAvancePAPDto.REVISADO);

        autenticarComo(crearUsuario(RolUsuario.COORDINADOR_PRE, "coord.33q"));
        assertThat(revisionService.finalizarRevisionAvance(request).getEstado())
                .isEqualTo(EstadoRevisionAvancePAPDto.REVISADO);
    }

    // ---- Apoyo ----

    private void crearEscenarioYAutenticar() {
        String sufijo = SufijosPrueba.nuevo(8);
        institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-33P-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-33P-" + sufijo, "UE de prueba", institucion));
        nombreUsuarioUrp = crearUsuario(RolUsuario.TECNICO_URP, "urp.33p");

        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M33P" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S33P" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-33P-" + sufijo, "Eje tematico de prueba"));
        String cup = proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc()
                .map((Proyecto p) -> String.format("%05d", Integer.parseInt(p.getCup()) + 1))
                .orElse("10000");
        proyecto = Pre30Fixtures.nuevoEstudio(proyectoRepository, unidadEjecutora, institucion, sector,
                ejeTematico, cup);

        progPerfil = nuevaProgramacion(TipoEtapaPreinversion.PERFIL);
        for (Cuatrimestre periodo : Cuatrimestre.values()) {
            if (periodo.ordinal() <= vigente.ordinal()) {
                registrarAvance(progPerfil, periodo, PROGRAMADO_CUATRIMESTRE);
            }
        }
        registrarAvance(nuevaProgramacion(TipoEtapaPreinversion.FACTIBILIDAD), Cuatrimestre.CUATRIMESTRE_I,
                EJECUTADO_FACTIBILIDAD);

        autenticarComo(nombreUsuarioUrp);
    }

    private ProgCuatrimestralMetaFisica nuevaProgramacion(TipoEtapaPreinversion tipoEtapa) {
        EtapaPreinversion etapa = Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, proyecto, tipoEtapa,
                100000.0);
        EtapaMetaFisicaPap etapaMeta = etapaMetaRepository
                .save(EtapaMetaFisicaPap.builder().etapaPreinversion(etapa).build());
        BigDecimal programado = BigDecimal.valueOf(PROGRAMADO_CUATRIMESTRE);
        return progRepository.save(ProgCuatrimestralMetaFisica.builder()
                .etapaMetaFisica(etapaMeta).anio(anio)
                .montoCuatrimestre1(programado).montoCuatrimestre2(programado).montoCuatrimestre3(programado)
                .build());
    }

    private void registrarAvance(ProgCuatrimestralMetaFisica programacion, Cuatrimestre periodo, double avance) {
        avanceRepository.save(AvanceCuatriMetaFisica.builder()
                .programacionMeta(programacion)
                .cuatrimestre(periodo)
                .avanceCuatrimestre(BigDecimal.valueOf(avance))
                .fechaRegistro(LocalDateTime.now(ZONA))
                .build());
    }

    private String crearUsuario(RolUsuario rol, String prefijo) {
        String nombreUsuario = prefijo + "." + SufijosPrueba.nuevo(8);
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
        AutenticacionDePrueba.autenticar(nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    private List<EstudioFilaAvanceMetasDto> filasDelEstudio() {
        return listado.getContenido().stream()
                .filter((EstudioFilaAvanceMetasDto f) -> proyecto.getCup().equals(f.getCup()))
                .toList();
    }

    private EstudioFilaAvanceMetasDto filaDe(NombreEtapaDto etapa) {
        return filasDelEstudio().stream()
                .filter((EstudioFilaAvanceMetasDto f) -> f.getEtapa() == etapa)
                .findFirst()
                .orElseThrow();
    }

    private EtapaAvanceMetasDto etapaDto(NombreEtapaDto nombre) {
        return estudio.getEtapas().stream()
                .filter((EtapaAvanceMetasDto e) -> e.getEtapa() == nombre)
                .findFirst()
                .orElseThrow();
    }

    private static CuatrimestreDto dto(Cuatrimestre periodo) {
        return CuatrimestreDto.valueOf(periodo.name());
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
