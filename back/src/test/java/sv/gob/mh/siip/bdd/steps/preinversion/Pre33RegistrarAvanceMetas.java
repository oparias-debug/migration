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
import sv.gob.mh.siip.model.preinversion.domain.AvanceCuatriMetaFisica;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralMetaFisica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceCuatrimestreMetaAnteriorDto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceMetasEstudioDto;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaAvanceMetasRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarAvanceMetasEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.AvanceCuatriMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.AvanceMetasFisicasPapService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/** CU-PRE-33-registrar-avance-metas.feature (SF-1, RN-C.b, RN-B.b, RN-G). */
public class Pre33RegistrarAvanceMetas {

    private static final String HEADER_USUARIO = "X-Usuario";
    private static final ZoneId ZONA = ZoneId.of("America/El_Salvador");
    private static final int ANIO = 2030;
    private static final String OBSERVACIONES = "Avance de metas registrado durante la prueba BDD.";
    private static final String MENSAJE_LIMITE = "El porcentaje total registrado supera el 100%";

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

    private UnidadEjecutora unidadEjecutora;
    private Proyecto proyecto;
    private EtapaMetaFisicaPap etapaMeta;
    private ProgCuatrimestralMetaFisica programacion;
    private AvanceMetasEstudioDto estudioGuardado;
    private Cuatrimestre periodoActual;
    private Map<Cuatrimestre, Double> avancesPrevios;
    private ValidacionNegocioException excepcionCapturada;

    public Pre33RegistrarAvanceMetas(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository, EtapaPreinversionRepository etapaPreinversionRepository,
            EtapaMetaFisicaPapRepository etapaMetaRepository, ProgCuatrimestralMetaFisicaRepository progRepository,
            AvanceCuatriMetaFisicaRepository avanceRepository, MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository, EjeTematicoRepository ejeTematicoRepository,
            AvanceMetasFisicasPapService service) {
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
    }

    @Dado("que el Técnico URP hace clic en el código de un proyecto avance-metas")
    public void que_hace_clic_en_el_codigo_de_un_proyecto() {
        crearEscenarioYAutenticar(25, 25, 25);
    }

    @Y("el sistema muestra la pantalla del Anexo A.4")
    public void el_sistema_muestra_la_pantalla_del_anexo_a4() {
        assertThat(service.obtenerAvanceMetasEstudio(proyecto.getCup(), ANIO, CuatrimestreDto.CUATRIMESTRE_I)).isNotNull();
    }

    @Cuando("el Técnico URP registra, para cada etapa, el \"Avance del Cuatrimestre\" y las \"Observaciones del Cuatrimestre\"")
    public void el_tecnico_urp_registra_el_avance() {
        estudioGuardado = guardarAvance(Cuatrimestre.CUATRIMESTRE_I, 20d, OBSERVACIONES);
    }

    @Y("hace clic en el botón \"Guardar\" avance-metas")
    public void hace_clic_en_guardar() {
        assertThat(estudioGuardado).isNotNull();
    }

    @Entonces("el sistema valida los datos según RN-C.b")
    public void el_sistema_valida_los_datos_segun_rn_c_b() {
        assertThat(estudioGuardado.getEtapas().get(0).getAvanceCuatrimestre()).isEqualTo(20d);
    }

    @Y("calcula el \"Total meta acumulada\" como Ejecutado años anteriores más Avance acumulado de cuatrimestres anteriores más Avance del cuatrimestre")
    public void calcula_el_total_meta_acumulada() {
        assertThat(estudioGuardado.getEtapas().get(0).getTotalMetaEjecutada()).isEqualTo(20d);
    }

    @Y("coloca el porcentaje registrado en \"Ejecutado del Cuatrimestre\" del Anexo A.1")
    public void coloca_el_porcentaje_registrado_en_ejecutado_del_cuatrimestre() {
        var contenido = service.listar(unidadEjecutora.getId(), ANIO, CuatrimestreDto.CUATRIMESTRE_I, 0, 20)
                .getContenido();
        assertThat(contenido).filteredOn(fila -> fila.getCup().equals(proyecto.getCup())).first()
                .satisfies(fila -> assertThat(fila.getEjecutadoDelCuatrimestre()).isEqualTo(20d));
    }

    @Y("coloca el \"Total meta ejecutada\" calculado en el campo del mismo nombre del Anexo A.1")
    public void coloca_el_total_meta_ejecutada_en_el_anexo_a1() {
        var contenido = service.listar(unidadEjecutora.getId(), ANIO, CuatrimestreDto.CUATRIMESTRE_I, 0, 20)
                .getContenido();
        assertThat(contenido).filteredOn(fila -> fila.getCup().equals(proyecto.getCup())).first()
                .satisfies(fila -> assertThat(fila.getTotalMetaEjecutada()).isEqualTo(20d));
    }

    @Y("coloca las \"Observaciones del Cuatrimestre\" en el campo \"Observaciones del cuatrimestre\" del Anexo A.1")
    public void coloca_las_observaciones_del_cuatrimestre() {
        var contenido = service.listar(unidadEjecutora.getId(), ANIO, CuatrimestreDto.CUATRIMESTRE_I, 0, 20)
                .getContenido();
        assertThat(contenido).filteredOn(fila -> fila.getCup().equals(proyecto.getCup())).first()
                .satisfies(fila -> assertThat(fila.getObservaciones()).isEqualTo(OBSERVACIONES));
        RequestContextHolder.resetRequestAttributes();
    }

    @Cuando("el Técnico URP hace clic en el botón \"Salir\" avance-metas")
    public void el_tecnico_urp_hace_clic_en_salir() {
        // No dispara ninguna acción de guardado.
    }

    @Entonces("el sistema regresa a la pantalla del Anexo A.1 sin guardar los cambios avance-metas")
    public void el_sistema_regresa_sin_guardar_los_cambios() {
        assertThat(avanceRepository.findByProgramacionMetaId(programacion.getId())).isEmpty();
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("que el avance registrado supera el porcentaje \"programado anual\" del estudio")
    public void que_el_avance_registrado_supera_el_porcentaje_programado_anual() {
        // Reutiliza el escenario base (programado 25/25/25, "Programado en el Año" = 75). Un avance de
        // 80% no llega al 100% de RN-B.d, pero sí supera lo programado anual, y RN-C.b debe bloquearlo.
    }

    @Cuando("el Técnico URP hace clic en el botón \"Guardar\" avance-metas-limite")
    public void el_tecnico_urp_hace_clic_en_guardar_sin_sufijo() {
        excepcionCapturada = assertThrows(ValidacionNegocioException.class,
                () -> guardarAvance(Cuatrimestre.CUATRIMESTRE_I, 80d, "Supera el programado anual (BDD)"));
    }

    @Entonces("el sistema muestra el mensaje \"El porcentaje total registrado supera el 100%\" \\(RN-C.b)")
    public void el_sistema_muestra_el_mensaje_de_limite() {
        assertThat(excepcionCapturada).isNotNull();
        assertThat(excepcionCapturada.getMessage()).isEqualTo(MENSAJE_LIMITE);
        assertThat(excepcionCapturada.getCodigo()).isEqualTo("PORCENTAJE_SUPERA_PROGRAMADO_ANUAL");
        assertThat(avanceRepository.findByProgramacionMetaId(programacion.getId())).isEmpty();
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("un estudio con \"programado del cuatrimestre\" igual a 0 en el cuatrimestre que se está registrando")
    public void un_estudio_con_programado_del_cuatrimestre_igual_a_cero() {
        crearEscenarioYAutenticar(0, 0, 0);
    }

    @Entonces("el Técnico URP puede reportar el avance correspondiente desde el Anexo A.4 \\(RN-B.b)")
    public void el_tecnico_urp_puede_reportar_el_avance() {
        AvanceMetasEstudioDto estudio = guardarAvance(Cuatrimestre.CUATRIMESTRE_I, 15d, "Avance sin programación (BDD)");
        assertThat(estudio.getEtapas().get(0).getAvanceCuatrimestre()).isEqualTo(15d);
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("que el Técnico URP está registrando el avance del {string} avance-metas")
    public void que_esta_registrando_el_avance_del(String cuatrimestreTexto) {
        crearEscenarioYAutenticar(25, 25, 25);
        periodoActual = mapearCuatrimestre(cuatrimestreTexto);
        avancesPrevios = new EnumMap<>(Cuatrimestre.class);
        if (periodoActual.ordinal() >= 1) {
            registrarAvancePrevio(Cuatrimestre.CUATRIMESTRE_I, 15d);
        }
        if (periodoActual.ordinal() >= 2) {
            registrarAvancePrevio(Cuatrimestre.CUATRIMESTRE_II, 20d);
        }
    }

    @Entonces("la sección \"Avances reportados en cuatrimestres anteriores\" muestra {string} \\(RN-G)")
    public void la_seccion_de_avances_anteriores_muestra(String contenidoDescripcion) {
        AvanceMetasEstudioDto estudio = service.obtenerAvanceMetasEstudio(proyecto.getCup(), ANIO,
                CuatrimestreDto.valueOf(periodoActual.name()));
        var anteriores = estudio.getEtapas().get(0).getAvancesCuatrimestresAnteriores();
        assertThat(anteriores).hasSize(avancesPrevios.size());
        for (AvanceCuatrimestreMetaAnteriorDto fila : anteriores) {
            Cuatrimestre periodo = Cuatrimestre.valueOf(fila.getPeriodo().name());
            assertThat(fila.getPorcentajeEjecutado()).isEqualTo(avancesPrevios.get(periodo));
        }
        RequestContextHolder.resetRequestAttributes();
    }

    private void registrarAvancePrevio(Cuatrimestre periodo, double monto) {
        avanceRepository.save(AvanceCuatriMetaFisica.builder()
                .programacionMeta(programacion)
                .cuatrimestre(periodo)
                .avanceCuatrimestre(BigDecimal.valueOf(monto))
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

    private AvanceMetasEstudioDto guardarAvance(Cuatrimestre periodo, double avance, String observaciones) {
        GuardarAvanceMetasEstudioRequestDto request = new GuardarAvanceMetasEstudioRequestDto()
                .addEtapasItem(new EtapaAvanceMetasRequestDto(NombreEtapaDto.PERFIL, avance)
                        .observacionesCuatrimestre(observaciones));
        return service.guardarAvanceMetasEstudio(proyecto.getCup(), ANIO, CuatrimestreDto.valueOf(periodo.name()), request);
    }

    private void crearEscenarioYAutenticar(double c1, double c2, double c3) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);
        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-33G-" + sufijo, "Institucion de prueba"));
        unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-33G-" + sufijo, "UE de prueba", institucion));

        String nombreUsuario = "urp.33g." + sufijo;
        usuarioRepository.save(Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build());

        MacroSector macrosector = macroSectorRepository.save(ProyectoFixtures.nuevoMacrosector("M33G" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository.save(ProyectoFixtures.nuevoSector("S33G" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository.save(ProyectoFixtures.nuevoEjeTematico("EJE-33G-" + sufijo, "Eje tematico de prueba"));
        String cup = proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc()
                .map(p -> String.format("%05d", Integer.parseInt(p.getCup()) + 1))
                .orElse("10000");
        proyecto = Pre30Fixtures.nuevoEstudio(proyectoRepository, unidadEjecutora, institucion, sector, ejeTematico, cup);
        EtapaPreinversion etapa = Pre30Fixtures.nuevaEtapa(etapaPreinversionRepository, proyecto, TipoEtapaPreinversion.PERFIL, 100000.0);
        etapaMeta = etapaMetaRepository.save(EtapaMetaFisicaPap.builder().etapaPreinversion(etapa).build());
        programacion = progRepository.save(ProgCuatrimestralMetaFisica.builder()
                .etapaMetaFisica(etapaMeta).anio(ANIO)
                .montoCuatrimestre1(BigDecimal.valueOf(c1)).montoCuatrimestre2(BigDecimal.valueOf(c2))
                .montoCuatrimestre3(BigDecimal.valueOf(c3)).build());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }
}
