package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import sv.gob.mh.siip.bdd.support.Pre31Fixtures;
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
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.RevisionProgramacionPap;
import sv.gob.mh.siip.model.preinversion.dto.EnviarProgramacionARevisionDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EntregableDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoPAPDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioFilaMetasFisicasDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioProgramacionMetasDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaMetaFisicaDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaMetaFisicaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FinalizarRevisionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarProgramacionMetasEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.ProgramacionMetasFisicasPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarObservacionesDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarRespuestaInstitucionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RevisionProgramacionPAPDto;
import sv.gob.mh.siip.model.preinversion.enums.Entregable;
import sv.gob.mh.siip.model.preinversion.enums.EstadoPap;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.HabilitacionModificacionMetasPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.RevisionProgramacionPapRepository;
import sv.gob.mh.siip.model.preinversion.service.NotificacionService;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionMetasFisicasPapRevisionService;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionMetasFisicasPapRevisionServiceImpl;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionMetasFisicasPapService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;
import sv.gob.mh.siip.security.ActorContexto;
import sv.gob.mh.siip.security.AutenticacionDePrueba;

/**
 * CU-PRE-31-programacion-metas-fisicas-pap.feature (feature "de pantalla" del front). Cada paso de
 * UI se traduce en la llamada al servicio que la pantalla hace y se verifica lo que el servidor
 * devuelve o persiste. El ciclo de revisión DGICP se ejercita con el servicio real armado con un
 * {@link NotificacionService} simulado, para verificar a quién notifica (mismo criterio que
 * {@code Pre24Viabilidad}); toda la escena corre en la transacción del escenario.
 *
 * <p>Datos de la Unidad Ejecutora del Técnico URP, programados para el año vigente:
 * <ul>
 * <li>Estudio A: Perfil (30/20/10 %) y Prefactibilidad (10/0/0 %), ambas nuevas.</li>
 * <li>Estudio B: Perfil de arrastre (40 % ejecutado el año anterior, "Estudio de Perfil") con 20/0/0 %.</li>
 * </ul>
 */
public class Pre31ProgramacionMetasFisicasPap implements PantallaFront {

    private static final String FEATURE = "CU-PRE-31-programacion-metas-fisicas-pap.feature";
    private static final String PANTALLA = "Programación por Meta Física Cuatrimestral del PAP";
    private static final String OBSERVACIONES = "Revisar el porcentaje del I cuatrimestre del Perfil.";
    private static final String RESPUESTA = "Se ajustó el porcentaje según lo observado.";

    private final PantallasFrontComun comun;
    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final EtapaPreinversionRepository etapaPreinversionRepository;
    private final EtapaMetaFisicaPapRepository etapaMetaRepository;
    private final ProgCuatrimestralMetaFisicaRepository progRepository;
    private final RevisionProgramacionPapRepository revisionRepository;
    private final HabilitacionModificacionMetasPapRepository habilitacionRepository;
    private final CalendarioEventoRepository calendarioEventoRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final ActorContexto actorContexto;
    private final ProgramacionMetasFisicasPapService service;

    private final int anio = Year.now(ZoneId.of("America/El_Salvador")).getValue();

    private boolean activa;
    private String nombreEscenario;
    private NotificacionService notificaciones;
    private ProgramacionMetasFisicasPapRevisionService revisionService;
    private Institucion institucion;
    private UnidadEjecutora unidadEjecutora;
    private Usuario tecnicoUrp;
    private Usuario tecnicoPre;
    private Usuario coordinadorPre;
    private Proyecto estudioA;
    private Proyecto estudioB;
    private EtapaPreinversion perfilB;

    private ProgramacionMetasFisicasPAPResponseDto listado;
    private EstudioProgramacionMetasDto estudioAbierto;
    private GuardarProgramacionMetasEstudioRequestDto solicitud;
    private EstudioProgramacionMetasDto estudioGuardado;
    private RevisionProgramacionPAPDto revision;
    private Resource reporte;

    public Pre31ProgramacionMetasFisicasPap(PantallasFrontComun comun, InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository, EtapaPreinversionRepository etapaPreinversionRepository,
            EtapaMetaFisicaPapRepository etapaMetaRepository, ProgCuatrimestralMetaFisicaRepository progRepository,
            RevisionProgramacionPapRepository revisionRepository,
            HabilitacionModificacionMetasPapRepository habilitacionRepository,
            CalendarioEventoRepository calendarioEventoRepository, MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository, EjeTematicoRepository ejeTematicoRepository,
            ActorContexto actorContexto, ProgramacionMetasFisicasPapService service) {
        this.comun = comun;
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.etapaMetaRepository = etapaMetaRepository;
        this.progRepository = progRepository;
        this.revisionRepository = revisionRepository;
        this.habilitacionRepository = habilitacionRepository;
        this.calendarioEventoRepository = calendarioEventoRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.actorContexto = actorContexto;
        this.service = service;
    }

    @Before
    public void activar(Scenario scenario) {
        activa = comun.activarSi(scenario, FEATURE, this);
        if (activa) {
            nombreEscenario = scenario.getName();
            notificaciones = mock(NotificacionService.class);
            revisionService = new ProgramacionMetasFisicasPapRevisionServiceImpl(revisionRepository,
                    usuarioRepository, notificaciones, habilitacionRepository, calendarioEventoRepository,
                    actorContexto);
        }
    }

    @After
    public void limpiar() {
        if (activa) {
            RequestContextHolder.resetRequestAttributes();
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
    public void filaPorEstudioYEtapa() {
        listado = service.listar(null, null, 0, 50);
        assertThat(listado.getAnio()).isEqualTo(anio);
        assertThat(listado.getIdUnidadEjecutora()).isEqualTo(unidadEjecutora.getId());
        assertThat(listado.getContenido())
                .extracting(EstudioFilaMetasFisicasDto::getCup, EstudioFilaMetasFisicasDto::getEtapa)
                .containsExactlyInAnyOrder(
                        tuple(estudioA.getCup(), NombreEtapaDto.PERFIL),
                        tuple(estudioA.getCup(), NombreEtapaDto.PREFACTIBILIDAD),
                        tuple(estudioB.getCup(), NombreEtapaDto.PERFIL));
    }

    @Override
    public void clicCupDeEstudio() {
        estudioAbierto = service.obtenerProgramacionMetasEstudio(estudioA.getCup(), anio);
    }

    @Override
    public void haceClic(String boton) {
        EnviarProgramacionARevisionDgicpRequestDto pap = papDelAnio();
        switch (boton) {
            case "Guardar" -> estudioGuardado = service.guardarProgramacionMetasEstudio(estudioA.getCup(), anio,
                    solicitud);
            case "Enviar observaciones" -> revision = revisionService.enviarObservacionesDgicp(pap);
            case "Enviar respuesta" -> revision = revisionService.enviarRespuestaInstitucion(pap);
            case "Finalizar revisión" -> revision = revisionService.finalizarRevision(
                    new FinalizarRevisionRequestDto(unidadEjecutora.getId(), anio));
            default -> throw new IllegalArgumentException("Botón no contemplado en CU-PRE-31: " + boton);
        }
    }

    @Override
    public void tecnicoUrpHaceClic(String boton) {
        assertThat(boton).isEqualTo("Enviar a revisión DGICP");
        revision = revisionService.enviarProgramacionARevisionDgicp(papDelAnio());
    }

    @Override
    public void actorHaceClic(String boton) {
        assertThat(boton).isEqualTo("Generar reporte");
        reporte = service.generarReporteMetasFisicas(unidadEjecutora.getId(), anio, "EXCEL");
    }

    @Override
    public void guardaProgramacion() {
        EstudioProgramacionMetasDto leido = service.obtenerProgramacionMetasEstudio(estudioA.getCup(), anio);
        assertThat(leido.getEtapas())
                .extracting(EtapaMetaFisicaDto::getEtapa, EtapaMetaFisicaDto::getMontoCuatrimestre1,
                        EtapaMetaFisicaDto::getMontoCuatrimestre2, EtapaMetaFisicaDto::getMontoCuatrimestre3)
                .containsExactly(tuple(NombreEtapaDto.PERFIL, 25d, 25d, 25d),
                        tuple(NombreEtapaDto.PREFACTIBILIDAD, 10d, 10d, 0d));
    }

    @Override
    public void entraTecnicoPreOCoordinadorPre() {
        // Antes de que la DGICP revise, la institución envió el PAP a revisión (SF-2).
        revisionService.enviarProgramacionARevisionDgicp(papDelAnio());
        // El escenario de finalizar lo ejerce el Coordinador PRE; los demás, el Técnico PRE.
        autenticarComo(nombreEscenario.contains("finaliza") ? coordinadorPre : tecnicoPre);
    }

    @Override
    public void escribeObservacionesYClic(String boton) {
        assertThat(boton).isEqualTo("Guardar");
        RevisionProgramacionPAPDto guardada = revisionService.registrarObservacionesDgicp(
                new RegistrarObservacionesDgicpRequestDto(unidadEjecutora.getId(), anio, OBSERVACIONES));
        assertThat(guardada.getObservacionesDgicp()).isEqualTo(OBSERVACIONES);
    }

    // ---------------------------------------------------------------- Consultar

    @Entonces("muestra el entregable, lo ejecutado en años anteriores, el total del año y los años posteriores "
            + "en porcentaje")
    public void muestraEntregableYPorcentajes() {
        assertThat(listado.getContenido())
                .extracting(EstudioFilaMetasFisicasDto::getCup, EstudioFilaMetasFisicasDto::getEtapa,
                        EstudioFilaMetasFisicasDto::getEntregable,
                        EstudioFilaMetasFisicasDto::getEjecutadoAniosAnteriores,
                        EstudioFilaMetasFisicasDto::getTotalAnio, EstudioFilaMetasFisicasDto::getAniosPosteriores)
                .containsExactlyInAnyOrder(
                        tuple(estudioA.getCup(), NombreEtapaDto.PERFIL, EntregableDto.ESTUDIO_DE_PERFIL, null,
                                60d, 40d),
                        tuple(estudioA.getCup(), NombreEtapaDto.PREFACTIBILIDAD,
                                EntregableDto.ESTUDIO_DE_PREFACTIBILIDAD, null, 10d, 90d),
                        tuple(estudioB.getCup(), NombreEtapaDto.PERFIL, EntregableDto.ESTUDIO_DE_PERFIL, 40d,
                                20d, 40d));
    }

    // ---------------------------------------------------------------- Programar metas

    @Entonces("el sistema abre la programación de metas de ese estudio \\(Anexo A.2)")
    public void abreLaProgramacionDeMetas() {
        assertThat(estudioAbierto.getCup()).isEqualTo(estudioA.getCup());
        assertThat(estudioAbierto.getEsArrastre()).isFalse();
        assertThat(estudioAbierto.getEtapas())
                .extracting(EtapaMetaFisicaDto::getEtapa, EtapaMetaFisicaDto::getTotalAnio)
                .containsExactly(tuple(NombreEtapaDto.PERFIL, 60d), tuple(NombreEtapaDto.PREFACTIBILIDAD, 10d));
    }

    @Entonces("las etapas son las de la Ruta de Preinversión del proyecto, sin agregar ni quitar filas")
    public void lasEtapasSonLasDeLaRuta() {
        List<NombreEtapaDto> ruta = etapaPreinversionRepository.findByProyectoId(estudioA.getId()).stream()
                .map((EtapaPreinversion e) -> NombreEtapaDto.valueOf(e.getTipoEtapa().name()))
                .toList();
        assertThat(estudioAbierto.getEtapas()).extracting(EtapaMetaFisicaDto::getEtapa)
                .containsExactlyInAnyOrderElementsOf(ruta);

        // Una etapa fuera de la Ruta no agrega filas: el servidor solo programa las etapas del proyecto.
        EstudioProgramacionMetasDto conEtapaAjena = service.guardarProgramacionMetasEstudio(estudioA.getCup(), anio,
                new GuardarProgramacionMetasEstudioRequestDto().addEtapasItem(
                        meta(NombreEtapaDto.FACTIBILIDAD, EntregableDto.ESTUDIO_DE_FACTIBILIDAD, 50d, 0d, 0d)));
        assertThat(conEtapaAjena.getEtapas()).extracting(EtapaMetaFisicaDto::getEtapa)
                .containsExactlyInAnyOrderElementsOf(ruta);
        assertThat(conEtapaAjena.getEtapas()).extracting(EtapaMetaFisicaDto::getTotalAnio)
                .containsExactly(60d, 10d);
    }

    @Cuando("escribe el porcentaje de cada cuatrimestre")
    public void escribeElPorcentajeDeCadaCuatrimestre() {
        solicitud = new GuardarProgramacionMetasEstudioRequestDto()
                .addEtapasItem(meta(NombreEtapaDto.PERFIL, EntregableDto.ESTUDIO_DE_PERFIL, 25d, 25d, 25d))
                .addEtapasItem(meta(NombreEtapaDto.PREFACTIBILIDAD, EntregableDto.ESTUDIO_DE_PREFACTIBILIDAD,
                        10d, 10d, 0d));
    }

    @Entonces("muestra el total del año y los años posteriores que calcula el servidor")
    public void muestraTotalesCalculadosPorElServidor() {
        assertThat(estudioGuardado.getEtapas())
                .extracting(EtapaMetaFisicaDto::getEtapa, EtapaMetaFisicaDto::getTotalAnio,
                        EtapaMetaFisicaDto::getAniosPosteriores)
                .containsExactly(tuple(NombreEtapaDto.PERFIL, 75d, 25d),
                        tuple(NombreEtapaDto.PREFACTIBILIDAD, 20d, 80d));
    }

    // ---------------------------------------------------------------- Arrastre

    @Dado("que la etapa ya tuvo programación de metas en un año anterior")
    public void laEtapaYaTuvoProgramacionEnUnAnioAnterior() {
        EtapaMetaFisicaDto perfil = service.obtenerProgramacionMetasEstudio(estudioB.getCup(), anio).getEtapas()
                .get(0);
        assertThat(perfil.getEsArrastre()).isTrue();
        assertThat(perfil.getEjecutadoAniosAnteriores()).isEqualTo(40d);
    }

    @Entonces("su entregable no admite un valor distinto")
    public void suEntregableNoAdmiteUnValorDistinto() {
        EstudioProgramacionMetasDto guardado = service.guardarProgramacionMetasEstudio(estudioB.getCup(), anio,
                new GuardarProgramacionMetasEstudioRequestDto().addEtapasItem(
                        meta(NombreEtapaDto.PERFIL, EntregableDto.ESTUDIO_DE_DISENO, 30d, 0d, 0d)));
        EtapaMetaFisicaDto perfil = guardado.getEtapas().get(0);
        assertThat(perfil.getEntregable()).isEqualTo(EntregableDto.ESTUDIO_DE_PERFIL);
        assertThat(perfil.getTotalAnio()).isEqualTo(30d);
        assertThat(etapaMetaRepository.findByEtapaPreinversionId(perfilB.getId()).orElseThrow().getEntregable())
                .isEqualTo(Entregable.ESTUDIO_DE_PERFIL);
    }

    // ---------------------------------------------------------------- Enviar a revisión

    @Entonces("el sistema envía a revisión la programación financiera y la de metas físicas juntas")
    public void enviaARevisionAmbasProgramaciones() {
        assertThat(revision.getEstadoPap()).isEqualTo(EstadoPAPDto.ENVIADO_A_REVISION_DGICP);
        // Un único registro de revisión por Unidad Ejecutora y año cubre las dos programaciones
        // (lleva los comentarios DGICP del reporte financiero y del de metas físicas).
        RevisionProgramacionPap registro = revisionDelAnio();
        assertThat(registro.getEstadoPap()).isEqualTo(EstadoPap.ENVIADO_A_REVISION_DGICP);
    }

    @Entonces("notifica al Técnico PRE")
    public void notificaAlTecnicoPre() {
        verify(notificaciones).notificarProgramacionEnviadaARevision(eq(unidadEjecutora.getId()), eq(anio),
                argThat((List<Usuario> destinatarios) -> contiene(destinatarios, tecnicoPre)));
    }

    // ---------------------------------------------------------------- Observaciones DGICP

    @Entonces("el sistema registra las observaciones con su fecha")
    public void registraLasObservacionesConSuFecha() {
        assertThat(revision.getObservacionesDgicp()).isEqualTo(OBSERVACIONES);
        assertThat(revision.getFechaObservaciones()).isNotNull();
        assertThat(revisionDelAnio().getFechaObservaciones()).isNotNull();
    }

    @Entonces("habilita al Técnico URP el campo {string}")
    public void habilitaAlTecnicoUrpElCampo(String campo) {
        assertThat(campo).isEqualTo("Respuesta de la institución");
        assertThat(revision.getEstadoPap()).isEqualTo(EstadoPAPDto.OBSERVADO);
        verify(notificaciones).notificarObservacionesDgicp(eq(unidadEjecutora.getId()), eq(anio),
                argThat((List<Usuario> destinatarios) -> contiene(destinatarios, tecnicoUrp)));

        autenticarComo(tecnicoUrp);
        RevisionProgramacionPAPDto conRespuesta = revisionService.registrarRespuestaInstitucion(
                new RegistrarRespuestaInstitucionRequestDto(unidadEjecutora.getId(), anio, RESPUESTA));
        assertThat(conRespuesta.getRespuestaInstitucion()).isEqualTo(RESPUESTA);
    }

    // ---------------------------------------------------------------- Respuesta de la institución

    @Dado("que quien entra es Técnico URP")
    public void quienEntraEsTecnicoUrp() {
        // La institución envió el PAP y la DGICP lo observó (SF-2 y SF-3).
        revisionService.enviarProgramacionARevisionDgicp(papDelAnio());
        autenticarComo(tecnicoPre);
        revisionService.registrarObservacionesDgicp(
                new RegistrarObservacionesDgicpRequestDto(unidadEjecutora.getId(), anio, OBSERVACIONES));
        revisionService.enviarObservacionesDgicp(papDelAnio());
        autenticarComo(tecnicoUrp);
    }

    @Entonces("no puede escribir las observaciones de la DGICP")
    public void noPuedeEscribirLasObservacionesDeLaDgicp() {
        RegistrarObservacionesDgicpRequestDto request = new RegistrarObservacionesDgicpRequestDto(
                unidadEjecutora.getId(), anio, "Observación escrita por la institución");
        assertThatThrownBy(() -> revisionService.registrarObservacionesDgicp(request))
                .isInstanceOf(AccesoDenegadoException.class);
        assertThat(revisionDelAnio().getObservacionesDgicp()).isEqualTo(OBSERVACIONES);
    }

    @Cuando("escribe su respuesta y hace clic en {string}")
    public void escribeSuRespuestaYHaceClic(String boton) {
        assertThat(boton).isEqualTo("Guardar");
        RevisionProgramacionPAPDto guardada = revisionService.registrarRespuestaInstitucion(
                new RegistrarRespuestaInstitucionRequestDto(unidadEjecutora.getId(), anio, RESPUESTA));
        assertThat(guardada.getRespuestaInstitucion()).isEqualTo(RESPUESTA);
    }

    @Entonces("el sistema registra la respuesta con su fecha")
    public void registraLaRespuestaConSuFecha() {
        assertThat(revision.getRespuestaInstitucion()).isEqualTo(RESPUESTA);
        assertThat(revision.getFechaRespuesta()).isNotNull();
        assertThat(revision.getEstadoPap()).isEqualTo(EstadoPAPDto.ENVIADO_A_REVISION_DGICP);
        verify(notificaciones).notificarRespuestaInstitucion(eq(unidadEjecutora.getId()), eq(anio),
                argThat((List<Usuario> destinatarios) -> contiene(destinatarios, tecnicoPre)));
    }

    // ---------------------------------------------------------------- Finalizar revisión

    @Entonces("el PAP queda en estado {string}")
    public void elPapQuedaEnEstado(String estado) {
        String codigo = Normalizer.normalize(estado, Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT).replace(' ', '_');
        assertThat(revision.getEstadoPap()).isEqualTo(EstadoPAPDto.fromValue(codigo));
        assertThat(revisionDelAnio().getEstadoPap()).isEqualTo(EstadoPap.valueOf(codigo));
    }

    // ---------------------------------------------------------------- Reporte

    @Entonces("el sistema genera el reporte del año para la unidad ejecutora")
    public void generaElReporteDelAnioParaLaUnidad() throws IOException {
        List<String> celdas = leerCeldas(reporte);
        assertThat(celdas).anyMatch((String c) -> c.contains("Unidad Ejecutora " + unidadEjecutora.getId())
                && c.contains(String.valueOf(anio)));
        assertThat(celdas).contains(estudioA.getCup(), estudioB.getCup());
    }

    // ---------------------------------------------------------------- soporte

    private EnviarProgramacionARevisionDgicpRequestDto papDelAnio() {
        return new EnviarProgramacionARevisionDgicpRequestDto(unidadEjecutora.getId(), anio);
    }

    private RevisionProgramacionPap revisionDelAnio() {
        return revisionRepository.findByIdUnidadEjecutoraAndAnio(unidadEjecutora.getId(), anio).orElseThrow();
    }

    private static boolean contiene(List<Usuario> destinatarios, Usuario usuario) {
        return destinatarios.stream()
                .anyMatch((Usuario u) -> u.getNombreUsuario().equals(usuario.getNombreUsuario()));
    }

    private static EtapaMetaFisicaRequestDto meta(NombreEtapaDto etapa, EntregableDto entregable, double c1,
            double c2, double c3) {
        return new EtapaMetaFisicaRequestDto(etapa).entregable(entregable)
                .montoCuatrimestre1(c1).montoCuatrimestre2(c2).montoCuatrimestre3(c3);
    }

    private void crearDatos() {
        String sufijo = SufijosPrueba.nuevo(8);
        institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-31P-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-31P-" + sufijo, "UE de prueba", institucion));
        MacroSector macrosector = macroSectorRepository
                .save(ProyectoFixtures.nuevoMacrosector("M31P" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S31P" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-31P-" + sufijo, "Eje tematico de prueba"));
        tecnicoUrp = crearUsuario(RolUsuario.TECNICO_URP, "urp.31p");
        tecnicoPre = crearUsuario(RolUsuario.TECNICO_PRE, "pre.31p");
        coordinadorPre = crearUsuario(RolUsuario.COORDINADOR_PRE, "coord.31p");

        estudioA = Pre30Fixtures.nuevoEstudio(proyectoRepository, unidadEjecutora, institucion, sector, ejeTematico,
                ProyectoFixtures.nuevoCup(proyectoRepository));
        Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, estudioA, TipoEtapaPreinversion.PERFIL, 10000.0);
        Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, estudioA, TipoEtapaPreinversion.PREFACTIBILIDAD,
                20000.0);
        estudioB = Pre30Fixtures.nuevoEstudio(proyectoRepository, unidadEjecutora, institucion, sector, ejeTematico,
                ProyectoFixtures.nuevoCup(proyectoRepository));
        perfilB = Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, estudioB, TipoEtapaPreinversion.PERFIL,
                8000.0);
        Pre31Fixtures.nuevaEtapaMetaFisicaConHistorico(etapaMetaRepository, progRepository, perfilB,
                Entregable.ESTUDIO_DE_PERFIL, anio - 1, 40d);

        // La programación del año la registra el propio servicio (SF-1), como lo haría el Técnico URP.
        autenticarComo(tecnicoUrp);
        service.guardarProgramacionMetasEstudio(estudioA.getCup(), anio, new GuardarProgramacionMetasEstudioRequestDto()
                .addEtapasItem(meta(NombreEtapaDto.PERFIL, EntregableDto.ESTUDIO_DE_PERFIL, 30d, 20d, 10d))
                .addEtapasItem(meta(NombreEtapaDto.PREFACTIBILIDAD, EntregableDto.ESTUDIO_DE_PREFACTIBILIDAD,
                        10d, 0d, 0d)));
        service.guardarProgramacionMetasEstudio(estudioB.getCup(), anio, new GuardarProgramacionMetasEstudioRequestDto()
                .addEtapasItem(meta(NombreEtapaDto.PERFIL, EntregableDto.ESTUDIO_DE_PERFIL, 20d, 0d, 0d)));
    }

    private Usuario crearUsuario(RolUsuario rol, String prefijo) {
        String nombreUsuario = prefijo + "." + SufijosPrueba.nuevo(8);
        return usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Actor de prueba (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(rol)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());
    }

    private static void autenticarComo(Usuario usuario) {
        AutenticacionDePrueba.autenticar(usuario.getNombreUsuario());
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
