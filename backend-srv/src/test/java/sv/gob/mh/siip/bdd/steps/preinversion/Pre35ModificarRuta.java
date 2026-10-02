package sv.gob.mh.siip.bdd.steps.preinversion;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
import sv.gob.mh.siip.bdd.support.SufijosPrueba;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
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
import sv.gob.mh.siip.model.preinversion.dto.ComplejidadProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.CriteriosCalificacionDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.ModificarRutaPreinversionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.RutaPreinversionDto;
import sv.gob.mh.siip.model.preinversion.dto.TamanioProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoCapitalDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.EtapasOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.service.SeleccionYRegistroDeEtapasService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;
import sv.gob.mh.siip.security.AutenticacionDePrueba;

/**
 * CU-PRE-03.5-modificar-ruta.feature. El clic en "Modificar"/"Guardar" comparte texto con pasos ya
 * definidos en Pre01RegistrarNuevoProyecto/Pre01ResponderObservaciones (no-op allí); la acción real
 * se dispara en el primer paso propio de esta clase que sigue a cada clic.
 */
public class Pre35ModificarRuta {

    private static final Double COSTO_DISENO = 250000.0;
    private static final LocalDate INICIO_DISENO = LocalDate.of(2026, 3, 1);

    private static final List<NombreEtapaDto> RUTA_COMPLETA = List.of(NombreEtapaDto.PERFIL,
            NombreEtapaDto.PREFACTIBILIDAD, NombreEtapaDto.FACTIBILIDAD, NombreEtapaDto.DISENO,
            NombreEtapaDto.EJECUCION);

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final EtapaPreinversionRepository etapaPreinversionRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final SeleccionYRegistroDeEtapasService service;
    private final EtapasOpinionTecnica etapasOpinionTecnica;
    private final Validator validator;

    private Proyecto proyecto;
    private String justificacion;
    private List<NombreEtapaDto> nuevaSeleccion;
    private RutaPreinversionDto rutaModificada;
    private Set<ConstraintViolation<ModificarRutaPreinversionRequestDto>> violaciones;

    public Pre35ModificarRuta(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository, EtapaPreinversionRepository etapaPreinversionRepository,
            MacroSectorRepository macroSectorRepository, SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository, SeleccionYRegistroDeEtapasService service,
            EtapasOpinionTecnica etapasOpinionTecnica, Validator validator) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.service = service;
        this.etapasOpinionTecnica = etapasOpinionTecnica;
        this.validator = validator;
    }

    @Dado("que el proyecto ya cuenta con una Ruta de Preinversión generada")
    public void que_el_proyecto_ya_cuenta_con_una_ruta_de_preinversion_generada() {
        crearProyectoYAutenticar();
        service.aceptarRutaPreinversion(proyecto.getId(), criteriosCompletos());
    }

    @Dado("el Técnico URP se encuentra en la pantalla del Anexo A.2")
    public void el_tecnico_urp_se_encuentra_en_la_pantalla_del_anexo_a2() {
        // Navegacion de UI pura.
    }

    @Entonces("el sistema muestra el campo \"Justifique Modificación\", el campo \"Identifique nueva Ruta de Preinversión\" y el botón \"Guardar\"")
    public void el_sistema_muestra_los_campos_de_modificacion() {
        // UI pura: sin estado de backend que verificar en este paso.
    }

    @Cuando("el Técnico URP diligencia el campo \"Justifique Modificación\" con la justificación del cambio")
    public void el_tecnico_urp_diligencia_el_campo_justifique_modificacion() {
        justificacion = "Se ajusta la ruta por cambio en el alcance del proyecto.";
    }

    @Cuando("selecciona la nueva ruta de preinversión marcando cada una de las etapas que desarrollará")
    public void selecciona_la_nueva_ruta_de_preinversion_marcando_cada_etapa() {
        nuevaSeleccion = RUTA_COMPLETA;
    }

    @Entonces("el sistema guarda la información")
    public void el_sistema_guarda_la_informacion() {
        rutaModificada = service.modificarRutaPreinversion(proyecto.getId(),
                new ModificarRutaPreinversionRequestDto().justificacion(justificacion).etapas(nuevaSeleccion));
        assertThat(rutaModificada.getFueModificada()).isTrue();
        assertThat(rutaModificada.getJustificacionUltimaModificacion()).isEqualTo(justificacion);
    }

    @Entonces("se dirige a la pantalla del Anexo A.1 mostrando las etapas según la nueva selección para completar costos y fechas")
    public void se_dirige_a_la_pantalla_del_anexo_a1_con_la_nueva_seleccion() {
        List<EtapaDto> etapas = service.listarEtapas(proyecto.getId());
        assertThat(etapas).extracting(EtapaDto::getNombreEtapa).containsExactlyInAnyOrderElementsOf(nuevaSeleccion);
        RequestContextHolder.resetRequestAttributes();
    }

    @Cuando("el Técnico URP intenta seleccionar la nueva ruta de preinversión sin haber registrado el campo \"Justifique Modificación\"")
    public void intenta_seleccionar_la_nueva_ruta_sin_justificar() {
        ModificarRutaPreinversionRequestDto request = new ModificarRutaPreinversionRequestDto()
                .justificacion(null)
                .etapas(RUTA_COMPLETA);
        violaciones = validator.validate(request);
    }

    @Entonces("el sistema no habilita la selección de la nueva ruta de preinversión, ya que el campo es obligatorio \\(RN03)")
    public void el_sistema_no_habilita_la_seleccion_sin_justificacion() {
        assertThat(violaciones).isNotEmpty();
        // El contrato solo exige que el campo exista; una justificación en blanco la rechaza el servicio.
        ModificarRutaPreinversionRequestDto enBlanco = new ModificarRutaPreinversionRequestDto()
                .justificacion("   ").etapas(RUTA_COMPLETA);
        assertThatThrownBy(() -> service.modificarRutaPreinversion(proyecto.getId(), enBlanco))
                .isInstanceOf(ValidacionNegocioException.class);
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("un proyecto de iniciativa \"Proyecto\" en formulación en alguna de las etapas PERFIL, PREFACTIBILIDAD, FACTIBILIDAD o DISEÑO")
    public void un_proyecto_de_iniciativa_proyecto_en_formulacion() {
        crearProyectoYAutenticar();
        proyecto.setEstado(EstadoProyecto.EN_FORMULACION);
        proyecto = proyectoRepository.save(proyecto);
        // Ruta Perfil + Diseño + Ejecución, para poder agregar después Prefactibilidad antes de Diseño.
        service.aceptarRutaPreinversion(proyecto.getId(), new CriteriosCalificacionDto()
                .tipoCapital(TipoCapitalDto.CAPITAL_FISICO)
                .tamanioProyecto(TamanioProyectoDto.PEQUENIO)
                .complejidad(ComplejidadProyectoDto.BAJA));
    }

    @Dado("una de esas etapas ya cuenta con Opinión Técnica emitida")
    public void una_de_esas_etapas_ya_cuenta_con_opinion_tecnica_emitida() {
        etapasOpinionTecnica.marcarEmitida(proyecto.getId(), TipoEtapaPreinversion.PERFIL);
        EtapaPreinversion diseno = diseno();
        diseno.setCosto(COSTO_DISENO);
        diseno.setFechaInicio(INICIO_DISENO);
        etapaPreinversionRepository.save(diseno);
        etapasOpinionTecnica.marcarEmitida(proyecto.getId(), TipoEtapaPreinversion.DISENO);
    }

    @Cuando("el Técnico URP modifica la Ruta de Preinversión seleccionando una etapa anterior a la ya emitida")
    public void el_tecnico_urp_modifica_la_ruta_seleccionando_una_etapa_anterior() {
        // Se agrega PREFACTIBILIDAD, anterior a DISENO, que ya tenia Opinion Tecnica.
        rutaModificada = service.modificarRutaPreinversion(proyecto.getId(),
                new ModificarRutaPreinversionRequestDto()
                        .justificacion("Se requiere un estudio de prefactibilidad tras revision del alcance.")
                        .etapas(List.of(NombreEtapaDto.PERFIL, NombreEtapaDto.PREFACTIBILIDAD,
                                NombreEtapaDto.DISENO, NombreEtapaDto.EJECUCION)));
    }

    @Entonces("el sistema bloquea la etapa que ya contaba con Opinión Técnica")
    public void el_sistema_bloquea_la_etapa_que_ya_contaba_con_opinion_tecnica() {
        assertThat(diseno().getBloqueadaPorModificacion()).isTrue();
        assertThat(rutaModificada.getEtapasAceptadas()).contains(NombreEtapaDto.DISENO);
    }

    @Entonces("no se pierde la información previamente registrada en esa etapa")
    public void no_se_pierde_la_informacion_previamente_registrada() {
        EtapaPreinversion diseno = diseno();
        assertThat(diseno.getCosto()).isEqualTo(COSTO_DISENO);
        assertThat(diseno.getFechaInicio()).isEqualTo(INICIO_DISENO);
    }

    @Entonces("el Técnico URP deberá volver a pasar por el proceso de aprobación hasta obtener la Opinión Técnica nuevamente si actualiza dicha etapa")
    public void debera_volver_a_pasar_por_el_proceso_de_aprobacion() {
        // Su OT anterior deja de valer: la siguiente OT es la de Prefactibilidad y después la de Diseño.
        assertThat(diseno().getTieneOpinionTecnica()).isFalse();
        assertThat(etapasOpinionTecnica.paraOpinionTecnica(proyecto.getId()).actual())
                .isEqualTo(TipoEtapaPreinversion.PREFACTIBILIDAD);
        RequestContextHolder.resetRequestAttributes();
    }

    @Cuando("se emite la Opinión Técnica de la etapa anterior que se agregó a la ruta")
    public void se_emite_la_opinion_tecnica_de_la_etapa_anterior_agregada() {
        etapasOpinionTecnica.marcarEmitida(proyecto.getId(), TipoEtapaPreinversion.PREFACTIBILIDAD);
    }

    @Entonces("la etapa bloqueada se desbloquea y es la siguiente en gestionar su Opinión Técnica")
    public void la_etapa_bloqueada_se_desbloquea() {
        assertThat(diseno().getBloqueadaPorModificacion()).isFalse();
        assertThat(etapasOpinionTecnica.paraOpinionTecnica(proyecto.getId()).actual())
                .isEqualTo(TipoEtapaPreinversion.DISENO);
        RequestContextHolder.resetRequestAttributes();
    }

    private EtapaPreinversion diseno() {
        return etapaPreinversionRepository
                .findByProyectoIdAndTipoEtapa(proyecto.getId(), TipoEtapaPreinversion.DISENO).orElseThrow();
    }

    // -----------------------------------------------------------------------------------------

    private void crearProyectoYAutenticar() {
        String sufijo = SufijosPrueba.nuevo(8);
        Institucion institucion = ProyectoFixtures.nuevaInstitucion("INS-35M-" + sufijo, "Institucion de prueba");
        if(institucion!=null)
            institucion = institucionRepository.save(institucion);
        UnidadEjecutora unidadEjecutora = ProyectoFixtures.nuevaUnidadEjecutora("UE-35M-" + sufijo, "Unidad Ejecutora de prueba", institucion);
        if(unidadEjecutora!=null)
            unidadEjecutora = unidadEjecutoraRepository.save(unidadEjecutora);

        String nombreUsuario = "tecnico.urp.bdd.35m." + sufijo;
        Usuario usuario = Usuario.builder()
                .nombreUsuario(nombreUsuario)
                .nombreCompleto("Tecnico URP (BDD)")
                .correo(nombreUsuario + "@example.com")
                .rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .activo(true)
                .build();
        if(usuario!=null)
            usuarioRepository.save(usuario);

        MacroSector macrosector = ProyectoFixtures.nuevoMacrosector("M" + sufijo, "Macrosector de prueba");
        if(macrosector!=null)
            macrosector = macroSectorRepository.save(macrosector);
        SectorActividad sector = sectorActividadRepository
                .save(ProyectoFixtures.nuevoSector("S" + sufijo, "Sector de prueba", macrosector));
        EjeTematico ejeTematico = ejeTematicoRepository
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-35M-" + sufijo, "Eje temático de prueba"));

        autenticarComo(nombreUsuario);

        proyecto = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto CU-PRE-03.5 BDD modificar ruta",
                EstadoProyecto.CUP_ASIGNADO, unidadEjecutora, institucion, sector, ejeTematico));
        proyecto.setIniciativaInversion(IniciativaInversion.PROYECTO);
        proyecto = proyectoRepository.save(proyecto);
    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        AutenticacionDePrueba.autenticar(nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private CriteriosCalificacionDto criteriosCompletos() {
        return new CriteriosCalificacionDto()
                .tipoCapital(TipoCapitalDto.CAPITAL_FISICO)
                .tamanioProyecto(TamanioProyectoDto.MEDIANO)
                .complejidad(ComplejidadProyectoDto.ALTA);
    }
}
