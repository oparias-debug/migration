package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.bdd.support.SufijosPrueba;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ComplejidadProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.CriteriosCalificacionDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.ModificarRutaPreinversionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoCapturaItemDto;
import sv.gob.mh.siip.model.preinversion.dto.RutaPreinversionDto;
import sv.gob.mh.siip.model.preinversion.dto.RutaPreinversionSugeridaDto;
import sv.gob.mh.siip.model.preinversion.dto.TamanioProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoCapitalDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.ProyectoCapturaFiltro;
import sv.gob.mh.siip.model.preinversion.service.ProyectoCapturaService;
import sv.gob.mh.siip.model.preinversion.service.SeleccionYRegistroDeEtapasService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;
import sv.gob.mh.siip.security.AutenticacionDePrueba;

/**
 * CU-PRE-3.5-caminos-de-la-ruta.feature (la agregó el front): qué camino ofrece la pantalla "Ruta de
 * Preinversión" según la iniciativa y si el proyecto es de emergencia. Cada camino se verifica contra
 * lo que el servidor admite: solo un PROYECTO que no es de emergencia puede generar ruta (RN07/RN08,
 * DN-03).
 */
public class Pre35CaminosDeLaRuta {

    private static final String FEATURE = "CU-PRE-3.5-caminos-de-la-ruta.feature";

    /** Anexo B.2: capital físico, tamaño pequeño y complejidad baja dan Perfil + Diseño + Ejecución. */
    private static final List<NombreEtapaDto> RUTA_SUGERIDA = List.of(NombreEtapaDto.PERFIL, NombreEtapaDto.DISENO,
            NombreEtapaDto.EJECUCION);

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final SeleccionYRegistroDeEtapasService service;
    private final ProyectoCapturaService proyectoCapturaService;

    private Proyecto proyecto;
    private List<ProyectoCapturaItemDto> listado;
    private RutaPreinversionDto ruta;
    private RutaPreinversionSugeridaDto sugerida;

    public Pre35CaminosDeLaRuta(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository, MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository, EjeTematicoRepository ejeTematicoRepository,
            SeleccionYRegistroDeEtapasService service, ProyectoCapturaService proyectoCapturaService) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.service = service;
        this.proyectoCapturaService = proyectoCapturaService;
    }

    @After
    public void limpiar(Scenario scenario) {
        if (PantallasFrontComun.esDeLaFeature(scenario, FEATURE)) {
            RequestContextHolder.resetRequestAttributes();
        }
    }

    @Dado("que el Técnico URP entra al proceso {string}")
    public void entra_al_proceso(String proceso) {
        assertThat(proceso).isEqualTo("Creación ruta de preinversión");
        proyecto = crearProyectoYAutenticar();
    }

    @Entonces("el sistema muestra el listado de proyectos con CUP")
    public void muestra_el_listado_de_proyectos_con_cup() {
        listado = proyectoCapturaService
                .listarProyectosCaptura(new ProyectoCapturaFiltro(null, null, null, null, null, null), 0, 200)
                .getContenido();
        assertThat(listado).isNotEmpty().allMatch((ProyectoCapturaItemDto item) -> item.getCup() != null);
        assertThat(listado).extracting(ProyectoCapturaItemDto::getIdProyecto).contains(proyecto.getId());
    }

    @Cuando("el Técnico URP elige un proyecto")
    public void elige_un_proyecto() {
        ruta = service.obtenerRutaPreinversion(proyecto.getId());
    }

    @Entonces("el sistema abre la pantalla {string} de ese proyecto")
    public void abre_la_pantalla_de_ese_proyecto(String pantalla) {
        assertThat(pantalla).isEqualTo("Ruta de Preinversión");
        assertThat(ruta.getIdProyecto()).isEqualTo(proyecto.getId());
    }

    @Entonces("lo primero que se ve es la matriz de criterios")
    public void lo_primero_es_la_matriz_de_criterios() {
        // Sin ruta aceptada todavía no hay criterios calificados: la pantalla abre pidiéndolos.
        assertThat(ruta.getCriterios()).isNull();
        assertThat(ruta.getFueModificada()).isFalse();
    }

    @Dado("un proyecto con iniciativa {string}, que no es de emergencia")
    public void un_proyecto_con_iniciativa_que_no_es_de_emergencia(String iniciativa) {
        assertThat(iniciativa(iniciativa)).isEqualTo(IniciativaInversion.PROYECTO);
        assertThat(proyecto.getEsProyectoEmergencia()).isFalse();
    }

    @Cuando("el Técnico URP califica los tres criterios y genera la Ruta de Preinversión")
    public void califica_los_criterios_y_genera_la_ruta() {
        sugerida = service.generarRutaPreinversion(proyecto.getId(), criterios());
    }

    @Entonces("el sistema sugiere las etapas del Anexo B.2")
    public void sugiere_las_etapas_del_anexo() {
        assertThat(sugerida.getEtapasSugeridas()).containsExactlyElementsOf(RUTA_SUGERIDA);
    }

    @Entonces("el Técnico URP puede aceptarlas o modificarlas")
    public void puede_aceptarlas_o_modificarlas() {
        assertThat(service.aceptarRutaPreinversion(proyecto.getId(), criterios()).getEtapasAceptadas())
                .containsExactlyElementsOf(RUTA_SUGERIDA);
        RutaPreinversionDto modificada = service.modificarRutaPreinversion(proyecto.getId(),
                new ModificarRutaPreinversionRequestDto().justificacion("Se requiere un estudio de prefactibilidad.")
                        .etapas(List.of(NombreEtapaDto.PERFIL, NombreEtapaDto.PREFACTIBILIDAD,
                                NombreEtapaDto.DISENO, NombreEtapaDto.EJECUCION)));
        assertThat(modificada.getFueModificada()).isTrue();
        assertThat(modificada.getEtapasAceptadas()).contains(NombreEtapaDto.PREFACTIBILIDAD);
    }

    @Dado("un proyecto marcado como de emergencia")
    public void un_proyecto_marcado_como_de_emergencia() {
        proyecto.setEsProyectoEmergencia(true);
        proyecto = proyectoRepository.save(proyecto);
    }

    @Dado("un proyecto con iniciativa {string}")
    public void un_proyecto_con_iniciativa(String iniciativa) {
        proyecto.setIniciativaInversion(iniciativa(iniciativa));
        proyecto = proyectoRepository.save(proyecto);
    }

    @Dado("un proyecto cuya ruta ya fue aceptada")
    public void un_proyecto_cuya_ruta_ya_fue_aceptada() {
        service.aceptarRutaPreinversion(proyecto.getId(), criterios());
    }

    @Cuando("el Técnico URP abre su {string}")
    public void abre_su_ruta(String pantalla) {
        assertThat(pantalla).isEqualTo("Ruta de Preinversión");
        ruta = service.obtenerRutaPreinversion(proyecto.getId());
    }

    @Entonces("el sistema advierte que un proyecto de emergencia no lleva ruta")
    public void advierte_que_emergencia_no_lleva_ruta() {
        Long idProyecto = proyecto.getId();
        CriteriosCalificacionDto criterios = criterios();
        assertThatThrownBy(() -> service.aceptarRutaPreinversion(idProyecto, criterios))
                .isInstanceOfSatisfying(ConflictoEstadoException.class, (ConflictoEstadoException ex) ->
                        assertThat(ex.getCodigo()).isEqualTo("PROYECTO_EMERGENCIA_SIN_RUTA"));
    }

    @Entonces("no pide calificar criterios")
    public void no_pide_calificar_criterios() {
        assertThat(ruta.getCriterios()).isNull();
        Long idProyecto = proyecto.getId();
        CriteriosCalificacionDto criterios = criterios();
        assertThatThrownBy(() -> service.generarRutaPreinversion(idProyecto, criterios))
                .isInstanceOf(ConflictoEstadoException.class);
    }

    @Entonces("ofrece el botón {string}")
    public void ofrece_el_boton(String boton) {
        assertThat(boton).isEqualTo("Ir a la Ficha de emergencia");
        assertThat(service.obtenerFichaEmergencia(proyecto.getId()).getCup()).isEqualTo(proyecto.getCup());
    }

    @Entonces("el sistema advierte que sus etapas ya están definidas: Perfil y Ejecución")
    public void advierte_que_sus_etapas_ya_estan_definidas() {
        assertThat(service.listarEtapas(proyecto.getId())).extracting(EtapaDto::getNombreEtapa)
                .containsExactly(NombreEtapaDto.PERFIL, NombreEtapaDto.EJECUCION);
    }

    @Entonces("sólo la etapa {string} queda habilitada para la formulación")
    public void solo_la_etapa_queda_habilitada(String etapa) {
        // Misma regla que la pantalla Registro de Etapas: la primera etapa habilitada y sin Opinión Técnica.
        NombreEtapaDto enCurso = service.listarEtapas(proyecto.getId()).stream()
                .filter((EtapaDto dto) -> Boolean.TRUE.equals(dto.getHabilitadoParaRegistro())
                        && !Boolean.TRUE.equals(dto.getTieneOpinionTecnica()))
                .map(EtapaDto::getNombreEtapa)
                .findFirst()
                .orElseThrow();
        assertThat(etapa).isEqualTo("Perfil");
        assertThat(enCurso).isEqualTo(NombreEtapaDto.PERFIL);
    }

    @Entonces("el sistema muestra las etapas vigentes")
    public void muestra_las_etapas_vigentes() {
        assertThat(ruta.getEtapasAceptadas()).containsExactlyElementsOf(RUTA_SUGERIDA);
        assertThat(ruta.getCriterios()).isNotNull();
    }

    @Entonces("ofrece el botón {string} para cambiarlas, indicando la justificación")
    public void ofrece_modificar_con_justificacion(String boton) {
        assertThat(boton).isEqualTo("Modificar");
        Long idProyecto = proyecto.getId();
        ModificarRutaPreinversionRequestDto sinJustificacion = new ModificarRutaPreinversionRequestDto()
                .etapas(RUTA_SUGERIDA);
        assertThatThrownBy(() -> service.modificarRutaPreinversion(idProyecto, sinJustificacion))
                .isInstanceOf(ValidacionNegocioException.class);
        RutaPreinversionDto modificada = service.modificarRutaPreinversion(idProyecto,
                new ModificarRutaPreinversionRequestDto().justificacion("Ajuste de la ruta.").etapas(RUTA_SUGERIDA));
        assertThat(modificada.getFueModificada()).isTrue();
        assertThat(modificada.getJustificacionUltimaModificacion()).isEqualTo("Ajuste de la ruta.");
    }

    private static CriteriosCalificacionDto criterios() {
        return new CriteriosCalificacionDto().tipoCapital(TipoCapitalDto.CAPITAL_FISICO)
                .tamanioProyecto(TamanioProyectoDto.PEQUENIO).complejidad(ComplejidadProyectoDto.BAJA);
    }

    private static IniciativaInversion iniciativa(String etiqueta) {
        return switch (etiqueta) {
            case "Proyecto" -> IniciativaInversion.PROYECTO;
            case "Programa" -> IniciativaInversion.PROGRAMA;
            case "Estudios Generales" -> IniciativaInversion.ESTUDIO_GENERAL;
            default -> throw new IllegalArgumentException("Iniciativa no reconocida: " + etiqueta);
        };
    }

    private Proyecto crearProyectoYAutenticar() {
        String sufijo = SufijosPrueba.nuevo(8);
        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-35C-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.save(
                ProyectoFixtures.nuevaUnidadEjecutora("UE-35C-" + sufijo, "Unidad Ejecutora de prueba", institucion));
        String nombreUsuario = "tecnico.urp.bdd.35c." + sufijo;
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
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-35C-" + sufijo, "Eje temático de prueba"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        AutenticacionDePrueba.autenticar(nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        Proyecto nuevo = ProyectoFixtures.nuevoProyecto("Proyecto CU-PRE-03.5 BDD caminos", EstadoProyecto.CUP_ASIGNADO,
                unidadEjecutora, institucion, sector, ejeTematico);
        nuevo.setCup(ProyectoFixtures.nuevoCup(proyectoRepository));
        nuevo.setIniciativaInversion(IniciativaInversion.PROYECTO);
        nuevo.setEsProyectoEmergencia(false);
        return proyectoRepository.save(nuevo);
    }
}
