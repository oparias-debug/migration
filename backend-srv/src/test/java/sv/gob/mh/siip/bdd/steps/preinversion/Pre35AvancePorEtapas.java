package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.bdd.support.SufijosPrueba;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
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
import sv.gob.mh.siip.model.preinversion.dto.EtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaEmergenciaDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaEmergenciaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoSeleccionadoDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.SeleccionYRegistroDeEtapasService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;
import sv.gob.mh.siip.security.AutenticacionDePrueba;

/**
 * CU-PRE-3.5-avance-por-etapas.feature (la agregó el front). La "etapa en curso" es la regla que
 * aplica la pantalla Registro de Etapas sobre lo que devuelve el servidor: la primera etapa, en
 * orden de ruta, habilitada y sin Opinión Técnica. Aquí se verifica esa regla contra la respuesta
 * real de {@code listarEtapas}.
 */
public class Pre35AvancePorEtapas implements PantallaFront {

    private static final String FEATURE = "CU-PRE-3.5-avance-por-etapas.feature";

    /** Se guardan desordenadas a propósito: el servicio debe devolverlas en orden de ruta. */
    private static final List<TipoEtapaPreinversion> ETAPAS_DESORDENADAS = List.of(TipoEtapaPreinversion.DISENO,
            TipoEtapaPreinversion.EJECUCION, TipoEtapaPreinversion.PERFIL, TipoEtapaPreinversion.FACTIBILIDAD,
            TipoEtapaPreinversion.PREFACTIBILIDAD);

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final EtapaPreinversionRepository etapaPreinversionRepository;
    private final SeleccionYRegistroDeEtapasService service;

    @Autowired
    private PantallasFrontComun comun;

    private Proyecto proyecto;
    private FichaEmergenciaRequestDto fichaDiligenciada;
    private FichaEmergenciaDto fichaGuardada;

    public Pre35AvancePorEtapas(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository, MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository, EjeTematicoRepository ejeTematicoRepository,
            EtapaPreinversionRepository etapaPreinversionRepository, SeleccionYRegistroDeEtapasService service) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.service = service;
    }

    @Before
    public void activar(Scenario scenario) {
        comun.activarSi(scenario, FEATURE, this);
    }

    @After
    public void limpiar(Scenario scenario) {
        if (PantallasFrontComun.esDeLaFeature(scenario, FEATURE)) {
            RequestContextHolder.resetRequestAttributes();
        }
    }

    @Dado("que el proyecto tiene etapas registradas en la pantalla {string}")
    public void el_proyecto_tiene_etapas_registradas(String pantalla) {
        assertThat(pantalla).isEqualTo("Registro de Etapas");
        proyecto = crearProyectoYAutenticar(false);
        for (TipoEtapaPreinversion tipoEtapa : ETAPAS_DESORDENADAS) {
            // RN09: PERFIL y EJECUCION nacen habilitadas; el resto espera la OT de la etapa anterior.
            boolean habilitada = tipoEtapa == TipoEtapaPreinversion.PERFIL
                    || tipoEtapa == TipoEtapaPreinversion.EJECUCION;
            etapaPreinversionRepository.save(EtapaPreinversion.builder().proyecto(proyecto).tipoEtapa(tipoEtapa)
                    .fechaSeleccion(LocalDateTime.now()).habilitadoParaRegistro(habilitada).build());
        }
    }

    @Entonces("el sistema lista las etapas en este orden: Perfil, Prefactibilidad, Factibilidad, Diseño, Ejecución")
    public void lista_las_etapas_en_orden() {
        assertThat(service.listarEtapas(proyecto.getId())).extracting(EtapaDto::getNombreEtapa).containsExactly(
                NombreEtapaDto.PERFIL, NombreEtapaDto.PREFACTIBILIDAD, NombreEtapaDto.FACTIBILIDAD,
                NombreEtapaDto.DISENO, NombreEtapaDto.EJECUCION);
    }

    @Entonces("las ordena así aunque el servicio las devuelva en otro orden")
    public void las_ordena_aunque_se_guarden_en_otro_orden() {
        // El repositorio las devuelve en el orden en que se guardaron; el servicio las reordena.
        List<TipoEtapaPreinversion> guardadas = etapaPreinversionRepository.findByProyectoId(proyecto.getId())
                .stream().map(EtapaPreinversion::getTipoEtapa).toList();
        assertThat(guardadas).containsExactlyElementsOf(ETAPAS_DESORDENADAS);
        lista_las_etapas_en_orden();
    }

    @Dado("que la etapa {string} está habilitada y no tiene opinión técnica")
    public void la_etapa_esta_habilitada_sin_opinion_tecnica(String etapa) {
        EtapaDto dto = etapa(etapa);
        assertThat(dto.getHabilitadoParaRegistro()).isTrue();
        assertThat(dto.getTieneOpinionTecnica()).isFalse();
    }

    @Dado("la etapa {string} todavía no corresponde")
    public void la_etapa_todavia_no_corresponde(String etapa) {
        assertThat(etapaEnCurso()).isNotEqualTo(nombreEtapa(etapa));
    }

    @Entonces("el sistema ofrece el botón {string} únicamente en la etapa {string}")
    public void ofrece_el_boton_unicamente_en(String boton, String etapa) {
        assertThat(boton).isEqualTo("Formular");
        assertThat(etapaEnCurso()).isEqualTo(nombreEtapa(etapa));
    }

    @Entonces("muestra {string} como habilitada")
    public void muestra_como_habilitada(String etapa) {
        assertThat(etapa(etapa).getHabilitadoParaRegistro()).isTrue();
    }

    @Entonces("en {string} indica que se habilita al aprobar la etapa anterior")
    public void indica_que_se_habilita_al_aprobar_la_anterior(String etapa) {
        EtapaDto dto = etapa(etapa);
        assertThat(dto.getTieneOpinionTecnica()).isFalse();
        assertThat(etapaEnCurso()).isNotEqualTo(dto.getNombreEtapa());
    }

    @Dado("que la etapa {string} ya tiene opinión técnica")
    public void la_etapa_ya_tiene_opinion_tecnica(String etapa) {
        EtapaPreinversion entidad = entidad(etapa);
        entidad.setTieneOpinionTecnica(true);
        etapaPreinversionRepository.save(entidad);
    }

    @Dado("la etapa {string} está habilitada")
    public void la_etapa_esta_habilitada(String etapa) {
        EtapaPreinversion entidad = entidad(etapa);
        entidad.setHabilitadoParaRegistro(true);
        etapaPreinversionRepository.save(entidad);
    }

    @Entonces("el sistema muestra {string} como etapa con opinión técnica, sin botón {string}")
    public void muestra_como_etapa_con_opinion_tecnica(String etapa, String boton) {
        assertThat(etapa(etapa).getTieneOpinionTecnica()).isTrue();
        assertThat(etapaEnCurso()).isNotEqualTo(nombreEtapa(etapa));
    }

    @Entonces("ofrece el botón {string} en {string}")
    public void ofrece_el_boton_en(String boton, String etapa) {
        ofrece_el_boton_unicamente_en(boton, etapa);
    }

    @Cuando("el Técnico URP hace clic en {string} en la etapa en curso")
    public void clic_en_la_etapa_en_curso(String boton) {
        assertThat(boton).isEqualTo("Formular");
        assertThat(etapaEnCurso()).isNotNull();
    }

    @Entonces("el sistema abre la formulación del proyecto, empezando por {string}")
    public void abre_la_formulacion(String capitulo) {
        // La navegación a "Identificación" (CU-PRE-04) es del front. Del lado del servidor, un proyecto
        // que no es de emergencia no tiene ficha de emergencia: Formular lleva a la formulación normal.
        assertThat(capitulo).isEqualTo("Identificación");
        Long idProyecto = proyecto.getId();
        assertThatThrownBy(() -> service.obtenerFichaEmergencia(idProyecto))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Dado("un proyecto de emergencia cuya etapa {string} está habilitada")
    public void un_proyecto_de_emergencia_con_etapa_habilitada(String etapa) {
        proyecto = crearProyectoYAutenticar(true);
        assertThat(etapa(etapa).getHabilitadoParaRegistro()).isTrue();
    }

    @Cuando("el Técnico URP hace clic en {string} en la etapa {string}")
    public void clic_en_la_etapa(String boton, String etapa) {
        ofrece_el_boton_unicamente_en(boton, etapa);
    }

    @Entonces("el sistema abre la {string}")
    public void abre_la_ficha(String pantalla) {
        assertThat(pantalla).isEqualTo("Ficha de proyectos de emergencia");
        assertThat(service.obtenerFichaEmergencia(proyecto.getId()).getCup()).isEqualTo(proyecto.getCup());
    }

    @Dado("que el Técnico URP diligenció la {string}")
    public void diligencio_la_ficha(String pantalla) {
        assertThat(pantalla).isEqualTo("Ficha de proyectos de emergencia");
        proyecto = crearProyectoYAutenticar(true);
        fichaDiligenciada = new FichaEmergenciaRequestDto()
                .planteamientoProblema("Las lluvias destruyeron el puente de acceso a la comunidad.")
                .productos(List.of(new ProductoSeleccionadoDto().codigoProducto("PROD-1")))
                .distrito("San Salvador")
                .poblacionObjetivo("1,200 familias");
    }

    @Override
    public void haceClic(String boton) {
        assertThat(boton).isEqualTo("Guardar");
        fichaGuardada = service.registrarFichaEmergencia(proyecto.getId(), fichaDiligenciada);
    }

    @Entonces("el sistema guarda la ficha y permanece en la pantalla")
    public void guarda_la_ficha() {
        assertThat(fichaGuardada).isNotNull();
        assertThat(service.obtenerFichaEmergencia(proyecto.getId()).getPlanteamientoProblema())
                .isEqualTo(fichaDiligenciada.getPlanteamientoProblema());
    }

    @Override
    public void muestraBoton(String boton) {
        // CU-PRE-03.5 no expone un envío a Viabilidad aparte: hoy guardar la ficha ya remite el proyecto
        // (FA-05, paso 5.5), así que el botón del front queda desactivado. Se verifica ese efecto.
        assertThat(boton).isEqualTo("Enviar a viabilidad");
        assertThat(proyectoRepository.findById(proyecto.getId()).orElseThrow().getEstado())
                .isEqualTo(EstadoProyecto.EN_VIABILIDAD);
    }

    /** La regla de la pantalla: primera etapa, en orden de ruta, habilitada y sin Opinión Técnica. */
    private NombreEtapaDto etapaEnCurso() {
        return service.listarEtapas(proyecto.getId()).stream()
                .filter((EtapaDto etapa) -> Boolean.TRUE.equals(etapa.getHabilitadoParaRegistro())
                        && !Boolean.TRUE.equals(etapa.getTieneOpinionTecnica()))
                .map(EtapaDto::getNombreEtapa)
                .findFirst()
                .orElse(null);
    }

    private EtapaDto etapa(String etiqueta) {
        NombreEtapaDto nombre = nombreEtapa(etiqueta);
        return service.listarEtapas(proyecto.getId()).stream()
                .filter((EtapaDto etapa) -> etapa.getNombreEtapa() == nombre)
                .findFirst()
                .orElseThrow();
    }

    private EtapaPreinversion entidad(String etiqueta) {
        TipoEtapaPreinversion tipoEtapa = TipoEtapaPreinversion.valueOf(nombreEtapa(etiqueta).name());
        return etapaPreinversionRepository.findByProyectoIdAndTipoEtapa(proyecto.getId(), tipoEtapa).orElseThrow();
    }

    private static NombreEtapaDto nombreEtapa(String etiqueta) {
        return switch (etiqueta) {
            case "Perfil" -> NombreEtapaDto.PERFIL;
            case "Prefactibilidad" -> NombreEtapaDto.PREFACTIBILIDAD;
            case "Factibilidad" -> NombreEtapaDto.FACTIBILIDAD;
            case "Diseño" -> NombreEtapaDto.DISENO;
            case "Ejecución" -> NombreEtapaDto.EJECUCION;
            default -> throw new IllegalArgumentException("Etapa no reconocida: " + etiqueta);
        };
    }

    private Proyecto crearProyectoYAutenticar(boolean emergencia) {
        String sufijo = SufijosPrueba.nuevo(8);
        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-35A-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.save(
                ProyectoFixtures.nuevaUnidadEjecutora("UE-35A-" + sufijo, "Unidad Ejecutora de prueba", institucion));
        String nombreUsuario = "tecnico.urp.bdd.35a." + sufijo;
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
                .save(ProyectoFixtures.nuevoMacrosector("M" + sufijo, "Macrosector de prueba"));
        SectorActividad sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-35A-" + sufijo, "Eje temático de prueba"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        AutenticacionDePrueba.autenticar(nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        Proyecto nuevo = ProyectoFixtures.nuevoProyecto("Proyecto CU-PRE-03.5 BDD avance", EstadoProyecto.CUP_ASIGNADO,
                unidadEjecutora, institucion, sector, ejeTematico);
        nuevo.setCup(ProyectoFixtures.nuevoCup(proyectoRepository));
        nuevo.setIniciativaInversion(IniciativaInversion.PROYECTO);
        nuevo.setEsProyectoEmergencia(emergencia);
        return proyectoRepository.save(nuevo);
    }
}
