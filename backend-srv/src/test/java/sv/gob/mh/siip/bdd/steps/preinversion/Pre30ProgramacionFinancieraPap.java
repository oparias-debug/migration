package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import java.io.IOException;
import java.io.InputStream;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
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
import sv.gob.mh.siip.bdd.support.Pre30Fixtures;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.bdd.support.SufijosPrueba;
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
import sv.gob.mh.siip.model.preinversion.dto.EstudioFilaListaPAPDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioProgramacionPAPDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaProgramacionDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaProgramacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaFuenteProgramacionDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaFuenteProgramacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FuenteFinanciamientoDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarProgramacionEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.HabilitarModificacionesFueraPlazoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.ProgramacionFinancieraPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionFinancieraPapAjusteService;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionFinancieraPapService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;
import sv.gob.mh.siip.security.AutenticacionDePrueba;

/**
 * CU-PRE-30-programacion-financiera-pap.feature (feature "de pantalla" del front). Cada paso de UI se
 * traduce en la llamada al servicio que la pantalla hace (listar, consultar/guardar un estudio,
 * generar el reporte, habilitar fuera de plazo) y se verifica lo que el servidor devuelve o persiste.
 *
 * <p>Datos de la Unidad Ejecutora del Técnico URP, programados para el año vigente:
 * <ul>
 * <li>Estudio A: Perfil (costo 10,000) con dos fuentes (Fondo General, de arrastre con 1,000 del año
 * anterior, y Donaciones) y Prefactibilidad (costo 20,000) con una fuente (Recursos Propios).</li>
 * <li>Estudio B ("Puente ..."): Perfil (costo 8,000) con una fuente (Fondo General).</li>
 * </ul>
 * Además, un estudio C de otra Unidad Ejecutora que el Técnico URP no debe ver (RN-A.a).
 */
public class Pre30ProgramacionFinancieraPap implements PantallaFront {

    private static final String FEATURE = "CU-PRE-30-programacion-financiera-pap.feature";
    private static final String PANTALLA = "Programación Financiera Cuatrimestral del PAP";
    private static final String NOMBRE_ESTUDIO_B = "Puente sobre el Rio Lempa (BDD)";
    private static final String PARTE_NOMBRE_B = "rio lempa";
    /** Año cuyo período de elaboración del PAP se cierra en el escenario "fuera del plazo". */
    private static final int ANIO_FUERA_PLAZO = 2047;

    private final PantallasFrontComun comun;
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
    private final ProgramacionFinancieraPapAjusteService ajusteService;

    private final int anio = Year.now(ZoneId.of("America/El_Salvador")).getValue();

    private boolean activa;
    private Institucion institucion;
    private UnidadEjecutora unidadEjecutora;
    private SectorActividad sector;
    private EjeTematico ejeTematico;
    private String tecnicoUrp;
    private Proyecto estudioA;
    private Proyecto estudioB;
    private Proyecto estudioAjeno;

    private ProgramacionFinancieraPAPResponseDto listado;
    private String busqueda;
    private ProgramacionFinancieraPAPResponseDto resultadoPorNombre;
    private ProgramacionFinancieraPAPResponseDto resultadoPorCup;
    private Proyecto estudioSeleccionado;
    private EstudioProgramacionPAPDto estudioAbierto;
    private GuardarProgramacionEstudioRequestDto solicitud;
    private EstudioProgramacionPAPDto estudioGuardado;
    private Resource reporte;
    private CalendarioEvento eventoCerrado;

    public Pre30ProgramacionFinancieraPap(PantallasFrontComun comun, InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository, EtapaPreinversionRepository etapaPreinversionRepository,
            MacroSectorRepository macroSectorRepository, SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository, CalendarioEventoRepository calendarioEventoRepository,
            ProgramacionFinancieraPapService service, ProgramacionFinancieraPapAjusteService ajusteService) {
        this.comun = comun;
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
        this.ajusteService = ajusteService;
    }

    @Before
    public void activar(Scenario scenario) {
        activa = comun.activarSi(scenario, FEATURE, this);
    }

    @After
    public void limpiar() {
        if (activa) {
            RequestContextHolder.resetRequestAttributes();
        }
        if (eventoCerrado != null) {
            calendarioEventoRepository.delete(eventoCerrado);
        }
    }

    // ---------------------------------------------------------------- pasos compartidos (PantallaFront)

    @Override
    public void tecnicoUrpEnPantalla(String pantalla) {
        assertThat(pantalla).isEqualTo(PANTALLA);
        crearDatos();
        autenticarComo(tecnicoUrp);
    }

    @Override
    public void filaPorEtapaYFuente() {
        listado = service.listar(null, null, null, 0, 50);
        assertThat(listado.getContenido())
                .extracting(EstudioFilaListaPAPDto::getCup, EstudioFilaListaPAPDto::getEtapa,
                        EstudioFilaListaPAPDto::getFuenteFinanciamiento)
                .containsExactlyInAnyOrder(
                        tuple(estudioA.getCup(), NombreEtapaDto.PERFIL, FuenteFinanciamientoDto.FONDO_GENERAL),
                        tuple(estudioA.getCup(), NombreEtapaDto.PERFIL, FuenteFinanciamientoDto.DONACIONES),
                        tuple(estudioA.getCup(), NombreEtapaDto.PREFACTIBILIDAD,
                                FuenteFinanciamientoDto.RECURSOS_PROPIOS),
                        tuple(estudioB.getCup(), NombreEtapaDto.PERFIL, FuenteFinanciamientoDto.FONDO_GENERAL));
    }

    @Override
    public void haceClic(String boton) {
        switch (boton) {
            case "Buscar" -> {
                resultadoPorNombre = service.listar(null, null, busqueda, 0, 50);
                resultadoPorCup = service.listar(null, null, estudioB.getCup(), 0, 50);
            }
            case "Agregar fila" -> {
                // UI pura: la fila nueva solo existe en pantalla hasta que se guarda.
            }
            case "Guardar" -> estudioGuardado = service.guardarProgramacionEstudio(estudioSeleccionado.getCup(),
                    anio, solicitud);
            default -> throw new IllegalArgumentException("Botón no contemplado en CU-PRE-30: " + boton);
        }
    }

    @Override
    public void tecnicoUrpHaceClic(String boton) {
        assertThat(boton).isEqualTo("Generar reporte");
        reporte = service.generarReporte(unidadEjecutora.getId(), anio, "EXCEL");
    }

    @Override
    public void clicCupDeEstudio() {
        estudioSeleccionado = estudioB;
        estudioAbierto = service.obtenerProgramacionEstudio(estudioB.getCup(), anio);
    }

    @Override
    public void guardaProgramacion() {
        FilaFuenteProgramacionDto fila = unicaFuente(service.obtenerProgramacionEstudio(estudioB.getCup(), anio));
        assertThat(fila.getMontoCuatrimestre1()).isEqualTo(3000d);
        assertThat(fila.getMontoCuatrimestre2()).isEqualTo(2000d);
        assertThat(fila.getMontoCuatrimestre3()).isEqualTo(1000d);
    }

    // ---------------------------------------------------------------- Consultar

    @Entonces("muestra el costo de la etapa, lo ejecutado en años anteriores, los tres cuatrimestres, "
            + "el total del año y los años posteriores")
    public void muestraColumnasDeMontos() {
        EstudioFilaListaPAPDto fila = listado.getContenido().stream()
                .filter((EstudioFilaListaPAPDto f) -> f.getCup().equals(estudioA.getCup())
                        && f.getEtapa() == NombreEtapaDto.PERFIL
                        && f.getFuenteFinanciamiento() == FuenteFinanciamientoDto.FONDO_GENERAL)
                .findFirst()
                .orElseThrow();
        assertThat(fila.getCostoEtapa()).isEqualTo(10000d);
        assertThat(fila.getEjecutadoAniosAnteriores()).isEqualTo(1000d);
        assertThat(fila.getMontoCuatrimestre1()).isEqualTo(2000d);
        assertThat(fila.getMontoCuatrimestre2()).isEqualTo(2000d);
        assertThat(fila.getMontoCuatrimestre3()).isEqualTo(1000d);
        assertThat(fila.getTotalProgramadoAnio()).isEqualTo(5000d);
        assertThat(fila.getAniosPosteriores()).isEqualTo(4000d);
    }

    @Entonces("toma por defecto el año vigente y la unidad ejecutora del Técnico URP \\(RN-A.a)")
    public void tomaPorDefectoAnioYUnidad() {
        assertThat(listado.getAnio()).isEqualTo(anio);
        assertThat(listado.getIdUnidadEjecutora()).isEqualTo(unidadEjecutora.getId());

        // Aunque pida otra Unidad Ejecutora, el Técnico URP queda en la suya.
        ProgramacionFinancieraPAPResponseDto otra = service.listar(estudioAjeno.getUnidadEjecutora().getId(), null,
                null, 0, 50);
        assertThat(otra.getIdUnidadEjecutora()).isEqualTo(unidadEjecutora.getId());
        assertThat(otra.getContenido()).extracting(EstudioFilaListaPAPDto::getCup)
                .doesNotContain(estudioAjeno.getCup());
    }

    // ---------------------------------------------------------------- Buscar

    @Cuando("el Técnico URP escribe un CUP o parte del nombre del proyecto")
    public void escribeCupOParteDelNombre() {
        busqueda = PARTE_NOMBRE_B;
    }

    @Entonces("el sistema muestra únicamente los estudios que coinciden")
    public void muestraUnicamenteLosQueCoinciden() {
        assertThat(resultadoPorNombre.getContenido()).isNotEmpty()
                .extracting(EstudioFilaListaPAPDto::getCup).containsOnly(estudioB.getCup());
        assertThat(resultadoPorCup.getContenido()).isNotEmpty()
                .extracting(EstudioFilaListaPAPDto::getCup).containsOnly(estudioB.getCup());
    }

    // ---------------------------------------------------------------- Programar los cuatrimestres

    @Entonces("el sistema abre la programación de ese estudio con sus etapas y fuentes \\(Anexo A.2)")
    public void abreLaProgramacionDelEstudio() {
        assertThat(estudioAbierto.getCup()).isEqualTo(estudioB.getCup());
        assertThat(estudioAbierto.getEtapas()).extracting(EtapaProgramacionDto::getEtapa)
                .containsExactly(NombreEtapaDto.PERFIL);
        FilaFuenteProgramacionDto fila = unicaFuente(estudioAbierto);
        assertThat(fila.getFuenteFinanciamiento()).isEqualTo(FuenteFinanciamientoDto.FONDO_GENERAL);
        assertThat(fila.getTotalProgramadoAnio()).isEqualTo(1000d);
    }

    @Cuando("escribe el monto de cada cuatrimestre")
    public void escribeElMontoDeCadaCuatrimestre() {
        FilaFuenteProgramacionDto existente = unicaFuente(estudioAbierto);
        solicitud = new GuardarProgramacionEstudioRequestDto()
                .addEtapasItem(new EtapaProgramacionRequestDto(NombreEtapaDto.PERFIL)
                        .addFuentesItem(filaExistente(existente).montoCuatrimestre1(3000d)
                                .montoCuatrimestre2(2000d).montoCuatrimestre3(1000d)));
    }

    @Entonces("muestra el total del año y los años posteriores que calcula el servidor \\(RN-B.c)")
    public void muestraTotalesCalculadosPorElServidor() {
        FilaFuenteProgramacionDto fila = unicaFuente(estudioGuardado);
        assertThat(fila.getTotalProgramadoAnio()).isEqualTo(6000d);
        assertThat(fila.getAniosPosteriores()).isEqualTo(2000d);
        assertThat(fila.getPorcentajeCuatrimestre1()).isEqualTo(50d);

        EstudioFilaListaPAPDto filaListado = service.listar(null, null, estudioB.getCup(), 0, 50).getContenido()
                .get(0);
        assertThat(filaListado.getTotalProgramadoAnio()).isEqualTo(6000d);
        assertThat(filaListado.getAniosPosteriores()).isEqualTo(2000d);
    }

    // ---------------------------------------------------------------- Agregar fuente

    @Dado("que el Técnico URP está en la programación de un estudio")
    public void estaEnLaProgramacionDeUnEstudio() {
        clicCupDeEstudio();
        assertThat(unicaFuente(estudioAbierto)).isNotNull();
    }

    @Cuando("elige la fuente de financiamiento y escribe sus montos")
    public void eligeLaFuenteYEscribeSusMontos() {
        FilaFuenteProgramacionDto existente = unicaFuente(estudioAbierto);
        solicitud = new GuardarProgramacionEstudioRequestDto()
                .addEtapasItem(new EtapaProgramacionRequestDto(NombreEtapaDto.PERFIL)
                        .addFuentesItem(filaExistente(existente))
                        .addFuentesItem(new FilaFuenteProgramacionRequestDto()
                                .fuenteFinanciamiento(FuenteFinanciamientoDto.DONACIONES)
                                .montoCuatrimestre1(500d).montoCuatrimestre2(500d).montoCuatrimestre3(0d)));
    }

    @Entonces("la etapa queda con la nueva fuente")
    public void laEtapaQuedaConLaNuevaFuente() {
        List<FilaFuenteProgramacionDto> fuentes = service.obtenerProgramacionEstudio(estudioB.getCup(), anio)
                .getEtapas().get(0).getFuentes();
        assertThat(fuentes).extracting(FilaFuenteProgramacionDto::getFuenteFinanciamiento,
                FilaFuenteProgramacionDto::getTotalProgramadoAnio)
                .containsExactlyInAnyOrder(tuple(FuenteFinanciamientoDto.FONDO_GENERAL, 1000d),
                        tuple(FuenteFinanciamientoDto.DONACIONES, 1000d));
        assertThat(fuentes).allSatisfy((FilaFuenteProgramacionDto f) -> assertThat(f.getIdFuente()).isNotNull());
    }

    // ---------------------------------------------------------------- Reporte

    @Entonces("el sistema genera el reporte del año para su unidad ejecutora")
    public void generaElReporteDelAnioParaSuUnidad() throws IOException {
        List<String> celdas = leerCeldas(reporte);
        assertThat(celdas).anyMatch((String c) -> c.contains("Unidad Ejecutora " + unidadEjecutora.getId())
                && c.contains("Año " + anio));
        assertThat(celdas).contains(estudioA.getCup(), estudioB.getCup()).doesNotContain(estudioAjeno.getCup());
    }

    // ---------------------------------------------------------------- Fuera de plazo

    @Dado("que el período de elaboración del PAP ya cerró")
    public void elPeriodoDeElaboracionYaCerro() {
        // Los datos de los escenarios BDD no se revierten entre escenarios: el período se cierra en un
        // año propio (no en el vigente, que usan otras features) y el evento se borra en @After.
        if (calendarioEventoRepository.findByTipoEventoAndAnioAndCuatrimestreIsNull(
                TipoEventoCalendario.PROGRAMACION_PAP, ANIO_FUERA_PLAZO).isEmpty()) {
            eventoCerrado = calendarioEventoRepository.save(CalendarioEvento.builder()
                    .tipoEvento(TipoEventoCalendario.PROGRAMACION_PAP)
                    .anio(ANIO_FUERA_PLAZO)
                    .estado(EstadoCalendarioEvento.CERRADO)
                    .build());
        }
        String cup = estudioB.getCup();
        GuardarProgramacionEstudioRequestDto request = solicitudEstudioB(ANIO_FUERA_PLAZO, 4000d);
        assertThatThrownBy(() -> service.guardarProgramacionEstudio(cup, ANIO_FUERA_PLAZO, request))
                .isInstanceOf(ConflictoEstadoException.class)
                .hasFieldOrPropertyWithValue("codigo", "PERIODO_CERRADO")
                .hasMessageContaining("Periodo de ingreso de información ha finalizado");
    }

    @Cuando("la DGICP habilita las modificaciones fuera de plazo")
    public void laDgicpHabilitaModificacionesFueraDePlazo() {
        // El servicio reserva la habilitación (SF-4/SF-5) al Administrador del Sistema.
        autenticarComo(crearUsuario(RolUsuario.ADMINISTRADOR, "admin.30p"));
        ajusteService.habilitarModificacionesFueraPlazo(
                new HabilitarModificacionesFueraPlazoRequestDto(unidadEjecutora.getId(), ANIO_FUERA_PLAZO));
        autenticarComo(tecnicoUrp);
    }

    @Entonces("el Técnico URP puede volver a modificar la programación")
    public void puedeVolverAModificarLaProgramacion() {
        EstudioProgramacionPAPDto resultado = service.guardarProgramacionEstudio(estudioB.getCup(),
                ANIO_FUERA_PLAZO, solicitudEstudioB(ANIO_FUERA_PLAZO, 4000d));
        assertThat(unicaFuente(resultado).getTotalProgramadoAnio()).isEqualTo(4000d);
    }

    // ---------------------------------------------------------------- soporte

    private GuardarProgramacionEstudioRequestDto solicitudEstudioB(int anioProgramacion, double cuatrimestre1) {
        FilaFuenteProgramacionDto existente = unicaFuente(
                service.obtenerProgramacionEstudio(estudioB.getCup(), anioProgramacion));
        return new GuardarProgramacionEstudioRequestDto()
                .addEtapasItem(new EtapaProgramacionRequestDto(NombreEtapaDto.PERFIL)
                        .addFuentesItem(filaExistente(existente).montoCuatrimestre1(cuatrimestre1)));
    }

    private static FilaFuenteProgramacionRequestDto filaExistente(FilaFuenteProgramacionDto fila) {
        return new FilaFuenteProgramacionRequestDto()
                .idFuente(fila.getIdFuente())
                .fuenteFinanciamiento(fila.getFuenteFinanciamiento())
                .montoCuatrimestre1(fila.getMontoCuatrimestre1())
                .montoCuatrimestre2(fila.getMontoCuatrimestre2())
                .montoCuatrimestre3(fila.getMontoCuatrimestre3());
    }

    private static FilaFuenteProgramacionDto unicaFuente(EstudioProgramacionPAPDto estudio) {
        assertThat(estudio.getEtapas()).hasSize(1);
        assertThat(estudio.getEtapas().get(0).getFuentes()).hasSize(1);
        return estudio.getEtapas().get(0).getFuentes().get(0);
    }

    private static FilaFuenteProgramacionRequestDto fila(FuenteFinanciamientoDto fuente, double c1, double c2,
            double c3) {
        return new FilaFuenteProgramacionRequestDto().fuenteFinanciamiento(fuente)
                .montoCuatrimestre1(c1).montoCuatrimestre2(c2).montoCuatrimestre3(c3);
    }

    private void crearDatos() {
        String sufijo = SufijosPrueba.nuevo(8);
        institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-30P-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-30P-" + sufijo, "UE propia", institucion));
        UnidadEjecutora otraUnidad = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-30Q-" + sufijo, "UE ajena", institucion));
        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M30P" + sufijo, "Macrosector de prueba"));
        sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S30P" + sufijo, "Sector de prueba", macrosector));
        ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-30P-" + sufijo, "Eje tematico de prueba"));
        tecnicoUrp = crearUsuario(RolUsuario.TECNICO_URP, "urp.30p");
        String tecnicoUrpAjeno = crearUsuario(RolUsuario.TECNICO_URP, "urp.30q", otraUnidad);

        estudioA = nuevoEstudio(unidadEjecutora);
        Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, estudioA, TipoEtapaPreinversion.PERFIL, 10000.0);
        Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, estudioA, TipoEtapaPreinversion.PREFACTIBILIDAD,
                20000.0);
        estudioB = nuevoEstudio(unidadEjecutora);
        estudioB.setNombre(NOMBRE_ESTUDIO_B);
        estudioB = proyectoRepository.save(estudioB);
        Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, estudioB, TipoEtapaPreinversion.PERFIL, 8000.0);
        estudioAjeno = nuevoEstudio(otraUnidad);
        Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, estudioAjeno, TipoEtapaPreinversion.PERFIL, 5000.0);

        // La programación la registra el propio servicio (SF-2), como lo haría el Técnico URP.
        autenticarComo(tecnicoUrp);
        // Año anterior: el Fondo General del Perfil de A queda "de arrastre" con 1,000 ejecutado.
        service.guardarProgramacionEstudio(estudioA.getCup(), anio - 1, new GuardarProgramacionEstudioRequestDto()
                .addEtapasItem(new EtapaProgramacionRequestDto(NombreEtapaDto.PERFIL)
                        .addFuentesItem(fila(FuenteFinanciamientoDto.FONDO_GENERAL, 1000d, 0d, 0d))));
        Long idFondoGeneralA = service.obtenerProgramacionEstudio(estudioA.getCup(), anio).getEtapas().get(0)
                .getFuentes().get(0).getIdFuente();
        service.guardarProgramacionEstudio(estudioA.getCup(), anio, new GuardarProgramacionEstudioRequestDto()
                .addEtapasItem(new EtapaProgramacionRequestDto(NombreEtapaDto.PERFIL)
                        .addFuentesItem(fila(FuenteFinanciamientoDto.FONDO_GENERAL, 2000d, 2000d, 1000d)
                                .idFuente(idFondoGeneralA))
                        .addFuentesItem(fila(FuenteFinanciamientoDto.DONACIONES, 1000d, 0d, 0d)))
                .addEtapasItem(new EtapaProgramacionRequestDto(NombreEtapaDto.PREFACTIBILIDAD)
                        .addFuentesItem(fila(FuenteFinanciamientoDto.RECURSOS_PROPIOS, 3000d, 0d, 0d))));
        service.guardarProgramacionEstudio(estudioB.getCup(), anio, new GuardarProgramacionEstudioRequestDto()
                .addEtapasItem(new EtapaProgramacionRequestDto(NombreEtapaDto.PERFIL)
                        .addFuentesItem(fila(FuenteFinanciamientoDto.FONDO_GENERAL, 1000d, 0d, 0d))));
        autenticarComo(tecnicoUrpAjeno);
        service.guardarProgramacionEstudio(estudioAjeno.getCup(), anio, new GuardarProgramacionEstudioRequestDto()
                .addEtapasItem(new EtapaProgramacionRequestDto(NombreEtapaDto.PERFIL)
                        .addFuentesItem(fila(FuenteFinanciamientoDto.FONDO_GENERAL, 500d, 0d, 0d))));
    }

    private Proyecto nuevoEstudio(UnidadEjecutora unidad) {
        return Pre30Fixtures.nuevoEstudio(proyectoRepository, unidad, institucion, sector, ejeTematico,
                ProyectoFixtures.nuevoCup(proyectoRepository));
    }

    private String crearUsuario(RolUsuario rol, String prefijo) {
        return crearUsuario(rol, prefijo, unidadEjecutora);
    }

    private String crearUsuario(RolUsuario rol, String prefijo, UnidadEjecutora unidad) {
        String nombreUsuario = prefijo + "." + SufijosPrueba.nuevo(8);
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Actor de prueba (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(rol)
                .unidadEjecutora(unidad)
                .institucion(institucion)
                .activo(true)
                .build());
        return nombreUsuario;
    }

    private static void autenticarComo(String nombreUsuario) {
        AutenticacionDePrueba.autenticar(nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    private static List<String> leerCeldas(Resource recurso) throws IOException {
        List<String> valores = new ArrayList<>();
        try (InputStream entrada = recurso.getInputStream(); XSSFWorkbook libro = new XSSFWorkbook(entrada)) {
            Sheet hoja = libro.getSheetAt(0);
            for (Row row : hoja) {
                for (Cell celda : row) {
                    if (celda.getCellType() == CellType.STRING) {
                        valores.add(celda.getStringCellValue());
                    }
                }
            }
        }
        return valores;
    }
}
