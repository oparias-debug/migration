package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.EnumMap;
import java.util.Map;
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
import sv.gob.mh.siip.exception.ValidacionNegocioException;
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
import sv.gob.mh.siip.model.preinversion.dto.AvanceCuatrimestreAnteriorDto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceEstudioDto;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaAvanceRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaAvanceFuenteDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaAvanceFuenteRequestDto;
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

/** CU-PRE-32-registrar-avance-cuatrimestre.feature (SF-1, RN-D.a, RN-B.b, RN-F). */
public class Pre32RegistrarAvanceCuatrimestre {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final ZoneId ZONA = ZoneId.of("America/El_Salvador");
    private static final int ANIO = 2028;
    private static final String OBSERVACIONES = "Avance registrado durante la prueba BDD.";
    private static final String MENSAJE_LIMITE =
            "Error. El monto del avance del cuatrimestre no debe superar el monto anual programado";

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
    private Proyecto proyecto;
    private EtapaPreinversion etapa;
    private FuenteFinanciamientoEtapaPap fuente;
    private ProgCuatrimestralFinanciera programacion;
    private AvanceEstudioDto estudioGuardado;
    private Cuatrimestre periodoActual;
    private Map<Cuatrimestre, Double> avancesPrevios;
    private ValidacionNegocioException excepcionCapturada;

    public Pre32RegistrarAvanceCuatrimestre(InstitucionRepository institucionRepository,
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

    @Dado("que el Técnico URP hace clic en el código de un proyecto")
    public void que_hace_clic_en_el_codigo_de_un_proyecto() {
        crearEscenarioYAutenticar();
    }

    @Y("el sistema muestra la pantalla del Anexo A.5")
    public void el_sistema_muestra_la_pantalla_del_anexo_a5() {
        assertThat(service.obtenerAvanceEstudio(proyecto.getCup(), ANIO, CuatrimestreDto.CUATRIMESTRE_I)).isNotNull();
    }

    @Cuando("el Técnico URP registra, para cada etapa y fuente de financiamiento, el \"Avance del Cuatrimestre\" y las \"Observaciones del Cuatrimestre\"")
    public void el_tecnico_urp_registra_el_avance() {
        estudioGuardado = guardarAvance(Cuatrimestre.CUATRIMESTRE_I, 2000d, OBSERVACIONES);
    }

    @Y("hace clic en el botón \"Guardar\" avance-financiero")
    public void hace_clic_en_guardar() {
        assertThat(estudioGuardado).isNotNull();
    }

    @Entonces("el sistema valida los datos según RN-D.a")
    public void el_sistema_valida_los_datos_segun_rn_d_a() {
        FilaAvanceFuenteDto fila = estudioGuardado.getEtapas().get(0).getFuentes().get(0);
        assertThat(fila.getMontoEjecutadoCuatrimestre()).isEqualTo(2000d);
    }

    @Y("coloca el monto registrado en la columna \"Ejecutado\" del Avance del Cuatrimestre del Anexo A.1")
    public void coloca_el_monto_en_ejecutado_del_anexo_a1() {
        var contenido = service.listar(unidadEjecutora.getId(), ANIO, CuatrimestreDto.CUATRIMESTRE_I, 0, 20)
                .getContenido();
        assertThat(contenido).anySatisfy(fila -> {
            if (fila.getCup().equals(proyecto.getCup())) {
                assertThat(fila.getAvanceDelCuatrimestreEjecutadoMonto()).isEqualTo(2000d);
            }
        });
    }

    @Y("coloca la información de \"Observaciones del Cuatrimestre\" en el campo \"Observaciones\" del Anexo A.1")
    public void coloca_las_observaciones_en_el_anexo_a1() {
        var contenido = service.listar(unidadEjecutora.getId(), ANIO, CuatrimestreDto.CUATRIMESTRE_I, 0, 20)
                .getContenido();
        assertThat(contenido).filteredOn(fila -> fila.getCup().equals(proyecto.getCup())).first()
                .satisfies(fila -> assertThat(fila.getObservaciones()).isEqualTo(OBSERVACIONES));
        RequestContextHolder.resetRequestAttributes();
    }

    @Cuando("el Técnico URP hace clic en el botón \"Salir\" avance-financiero")
    public void el_tecnico_urp_hace_clic_en_salir() {
        // No dispara ninguna acción de guardado.
    }

    @Entonces("el sistema regresa a la pantalla del Anexo A.1 sin guardar los cambios avance-financiero")
    public void el_sistema_regresa_sin_guardar_los_cambios() {
        assertThat(avanceRepository.findByProgramacionId(programacion.getId())).isEmpty();
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("que el Técnico URP está registrando el avance del {string}")
    public void que_esta_registrando_el_avance_del(String cuatrimestreTexto) {
        periodoActual = mapearCuatrimestre(cuatrimestreTexto);
        avancesPrevios = new EnumMap<>(Cuatrimestre.class);
        if (periodoActual.ordinal() >= 1) {
            registrarAvancePrevio(Cuatrimestre.CUATRIMESTRE_I, 500d);
        }
        if (periodoActual.ordinal() >= 2) {
            registrarAvancePrevio(Cuatrimestre.CUATRIMESTRE_II, 700d);
        }
    }

    @Cuando("el monto registrado supera {string}")
    public void el_monto_registrado_supera(String limiteDescripcion) {
        double totalAnioProgramado = 3000d;
        double ejecutadoPrevio = avancesPrevios.values().stream().mapToDouble(Double::doubleValue).sum();
        double montoExcesivo = (totalAnioProgramado - ejecutadoPrevio) + 500d;
        Cuatrimestre periodo = periodoActual;
        excepcionCapturada = assertThrows(ValidacionNegocioException.class,
                () -> guardarAvance(periodo, montoExcesivo, "Excede el límite (BDD)"));
    }

    @Entonces("el sistema muestra el mensaje \"Error. El monto del avance del cuatrimestre no debe superar el monto anual programado\" \\(Anexo A.2, RN-D.a)")
    public void el_sistema_muestra_el_mensaje_de_limite() {
        assertThat(excepcionCapturada).isNotNull();
        assertThat(excepcionCapturada.getMessage()).isEqualTo(MENSAJE_LIMITE);
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("que el monto ejecutado acumulado supera el \"Costo de la Etapa\" programado en CU-PRE-30")
    public void que_el_monto_ejecutado_acumulado_supera_el_costo_de_la_etapa() {
        etapa.setCosto(1000.0);
        etapaPreinversionRepository.save(etapa);
        estudioGuardado = guardarAvance(Cuatrimestre.CUATRIMESTRE_I, 1500d, "Excede el costo de la etapa (BDD)");
    }

    @Entonces("el sistema genera una alerta para revisión y ajuste del PAP vigente \\(Anexo A.2, RN-B.b)")
    public void el_sistema_genera_una_alerta() {
        FilaAvanceFuenteDto fila = estudioGuardado.getEtapas().get(0).getFuentes().get(0);
        assertThat(fila.getAlertaExcesoProgramado()).isTrue();
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("que otra fuente de financiamiento de la misma etapa ya ejecutó en años anteriores parte del \"Costo de la Etapa\"")
    public void que_otra_fuente_ya_ejecuto_en_anios_anteriores() {
        etapa.setCosto(1000.0);
        etapaPreinversionRepository.save(etapa);
        FuenteFinanciamientoEtapaPap otraFuente = fuenteRepository.save(FuenteFinanciamientoEtapaPap.builder()
                .etapaPreinversion(etapa).fuenteFinanciamiento(FuenteFinanciamiento.PRESTAMOS_EXTERNOS).build());
        ProgCuatrimestralFinanciera progAnterior = progRepository.save(ProgCuatrimestralFinanciera.builder()
                .fuente(otraFuente).anio(ANIO - 1).montoCuatrimestre1(BigDecimal.valueOf(700)).build());
        avanceRepository.save(AvanceFinancieroCuatrimestral.builder()
                .programacion(progAnterior)
                .cuatrimestre(Cuatrimestre.CUATRIMESTRE_I)
                .montoEjecutado(BigDecimal.valueOf(700))
                .fechaRegistro(LocalDateTime.now(ZONA))
                .build());
    }

    @Cuando("el Técnico URP registra un avance que por sí solo no supera el \"Costo de la Etapa\" pero sumado a la otra fuente sí")
    public void registra_un_avance_que_solo_supera_el_costo_en_el_agregado() {
        // Fuente propia: 0 (años anteriores) + 500 <= 1000; otra fuente: 700 <= 1000; etapa: 1200 > 1000.
        estudioGuardado = guardarAvance(Cuatrimestre.CUATRIMESTRE_I, 500d, "Avance parcial (BDD)");
    }

    @Entonces("el sistema genera la alerta para revisión y ajuste del PAP vigente en todas las fuentes de la etapa \\(Anexo A.2, RN-B.b)")
    public void el_sistema_genera_la_alerta_en_todas_las_fuentes_de_la_etapa() {
        var fuentesEtapa = estudioGuardado.getEtapas().get(0).getFuentes();
        assertThat(fuentesEtapa).hasSize(2)
                .allSatisfy(fila -> assertThat(fila.getAlertaExcesoProgramado()).isTrue());

        var contenido = service.listar(unidadEjecutora.getId(), ANIO, CuatrimestreDto.CUATRIMESTRE_I, 0, 20)
                .getContenido();
        assertThat(contenido).filteredOn(fila -> fila.getCup().equals(proyecto.getCup())).isNotEmpty()
                .allSatisfy(fila -> assertThat(fila.getAlertaExcesoProgramado()).isTrue());
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("la sección \"Avances reportados en cuatrimestres anteriores\" muestra {string} \\(RN-F)")
    public void la_seccion_de_avances_anteriores_muestra(String contenidoDescripcion) {
        AvanceEstudioDto estudio = service.obtenerAvanceEstudio(proyecto.getCup(), ANIO, CuatrimestreDto.valueOf(periodoActual.name()));
        var anteriores = estudio.getEtapas().get(0).getFuentes().get(0).getAvancesCuatrimestresAnteriores();
        assertThat(anteriores).hasSize(avancesPrevios.size());
        for (AvanceCuatrimestreAnteriorDto fila : anteriores) {
            Cuatrimestre periodo = Cuatrimestre.valueOf(fila.getPeriodo().name());
            assertThat(fila.getMontoEjecutado()).isEqualTo(avancesPrevios.get(periodo));
        }
        RequestContextHolder.resetRequestAttributes();
    }

    private void registrarAvancePrevio(Cuatrimestre periodo, double monto) {
        avanceRepository.save(AvanceFinancieroCuatrimestral.builder()
                .programacion(programacion)
                .cuatrimestre(periodo)
                .montoEjecutado(BigDecimal.valueOf(monto))
                .fechaRegistro(LocalDateTime.now(ZONA))
                .build());
        avancesPrevios.put(periodo, monto);
    }

    private Cuatrimestre mapearCuatrimestre(String texto) {
        if (texto.contains("III")) {
            return Cuatrimestre.CUATRIMESTRE_III;
        }
        if (texto.contains("II")) {
            return Cuatrimestre.CUATRIMESTRE_II;
        }
        return Cuatrimestre.CUATRIMESTRE_I;
    }

    private AvanceEstudioDto guardarAvance(Cuatrimestre periodo, double monto, String observaciones) {
        GuardarAvanceEstudioRequestDto request = new GuardarAvanceEstudioRequestDto()
                .addEtapasItem(new EtapaAvanceRequestDto(NombreEtapaDto.PERFIL)
                        .addFuentesItem(new FilaAvanceFuenteRequestDto(fuente.getId(), monto)
                                .observacionesCuatrimestre(observaciones)));
        return service.guardarAvanceEstudio(proyecto.getCup(), ANIO, CuatrimestreDto.valueOf(periodo.name()), request);
    }

    private void crearEscenarioYAutenticar() {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-32G-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-32G-" + sufijo, "UE de prueba", institucion));

        String nombreUsuario = "urp.32g." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        MacroSector macrosector = macroSectorRepository.save(ProyectoFixtures.nuevoMacrosector("M32G" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository.save(ProyectoFixtures.nuevoSector("S32G" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository.save(ProyectoFixtures.nuevoEjeTematico("EJE-32G-" + sufijo, "Eje tematico de prueba"));
        String cup = proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc()
                .map(p -> String.format("%05d", Integer.parseInt(p.getCup()) + 1))
                .orElse("10000");
        proyecto = Pre30Fixtures.nuevoEstudio(proyectoRepository, unidadEjecutora, institucion, sector, ejeTematico, cup);
        etapa = Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, proyecto, TipoEtapaPreinversion.PERFIL, 100000.0);
        fuente = fuenteRepository.save(FuenteFinanciamientoEtapaPap.builder()
                .etapaPreinversion(etapa).fuenteFinanciamiento(FuenteFinanciamiento.FONDO_GENERAL).build());
        programacion = progRepository.save(ProgCuatrimestralFinanciera.builder()
                .fuente(fuente).anio(ANIO)
                .montoCuatrimestre1(BigDecimal.valueOf(1000)).montoCuatrimestre2(BigDecimal.valueOf(1000))
                .montoCuatrimestre3(BigDecimal.valueOf(1000)).build());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
