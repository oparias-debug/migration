package sv.gob.mh.siip.bdd.steps.preinversion;

import java.util.List;
import java.util.Set;
import java.util.UUID;

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
import sv.gob.mh.siip.exception.ConflictoEstadoException;
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
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.RutaPreinversionDto;
import sv.gob.mh.siip.model.preinversion.dto.RutaPreinversionSugeridaDto;
import sv.gob.mh.siip.model.preinversion.dto.TamanioProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoCapitalDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.SeleccionYRegistroDeEtapasService;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * CU-PRE-3.5-generar-aceptar-ruta.feature. Los clics en botones genéricos ("Generar Ruta de
 * Preinversión", "Aceptar") comparten texto con pasos ya definidos en Pre01ResponderObservaciones/
 * Pre01RegistrarNuevoProyecto (Cucumber exige una única definición por texto) y son no-op allí; la
 * acción real de negocio se dispara aquí, en el primer paso propio de esta clase que sigue a cada
 * clic (mismo patrón que Pre015EmitirCup).
 */
public class Pre35GenerarAceptarRuta {

    private static final String HEADER_USUARIO = "X-Usuario";

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final SeleccionYRegistroDeEtapasService service;
    private final Validator validator;

    private Proyecto proyecto;
    private final CriteriosCalificacionDto criterios = new CriteriosCalificacionDto();
    private RutaPreinversionSugeridaDto sugerida;
    private RutaPreinversionDto rutaAceptada;
    private Set<ConstraintViolation<CriteriosCalificacionDto>> violaciones;

    public Pre35GenerarAceptarRuta(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository, MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository, EjeTematicoRepository ejeTematicoRepository,
            SeleccionYRegistroDeEtapasService service, Validator validator) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.service = service;
        this.validator = validator;
    }

    @Dado("que el Técnico URP ingresa a \"Captura de Proyectos\" \\(UC-PRE-03) y hace clic sobre el CUP del proyecto")
    public void ingresa_a_captura_de_proyectos_y_hace_clic_sobre_el_cup() {
        crearProyectoYAutenticar(IniciativaInversion.PROYECTO);
    }

    @Cuando("hace clic en el botón {string} de la pantalla del Anexo A.1")
    public void hace_clic_en_el_boton_de_la_pantalla_del_anexo_a1(String boton) {
        // Navegacion de UI pura hacia la pantalla de Ruta de Preinversion (Anexo A.2).
    }

    @Dado("que es la primera vez que se ingresa al proyecto o aún no se ha generado una Ruta de Preinversión")
    public void es_la_primera_vez_que_se_ingresa_al_proyecto() {
        assertThat(service.obtenerRutaPreinversion(proyecto.getId()).getCriterios()).isNull();
    }

    @Cuando("el sistema muestra únicamente los campos \"Criterios\", \"Calificación\" y el botón \"Generar Ruta de Preinversión\" \\(Anexo A.2)")
    public void el_sistema_muestra_unicamente_los_campos_criterios() {
        // UI pura: sin estado de backend que verificar en este paso.
    }

    @Cuando("el Técnico URP selecciona una calificación para el criterio {string} con valor {string}")
    public void el_tecnico_urp_selecciona_una_calificacion_para_el_criterio(String criterio, String valor) {
        aplicarCriterio(criterios, criterio, valor);
    }

    @Cuando("selecciona una calificación para el criterio {string} con valor {string}")
    public void selecciona_una_calificacion_para_el_criterio(String criterio, String valor) {
        aplicarCriterio(criterios, criterio, valor);
    }

    @Cuando("el Técnico URP selecciona una calificación para cada criterio \\(Tipo de capital, Tamaño del proyecto, Complejidad del proyecto)")
    public void el_tecnico_urp_selecciona_una_calificacion_para_cada_criterio() {
        criterios.setTipoCapital(TipoCapitalDto.CAPITAL_FISICO);
        criterios.setTamanioProyecto(TamanioProyectoDto.MEDIANO);
        criterios.setComplejidad(ComplejidadProyectoDto.ALTA);
    }

    @Entonces("el sistema muestra la Ruta de Preinversión sugerida según la combinación seleccionada \\(Anexo B.2)")
    public void el_sistema_muestra_la_ruta_sugerida_segun_la_combinacion_seleccionada() {
        sugerida = service.generarRutaPreinversion(proyecto.getId(), criterios);
        assertThat(sugerida.getEtapasSugeridas()).containsExactlyInAnyOrder(NombreEtapaDto.PERFIL,
                NombreEtapaDto.PREFACTIBILIDAD, NombreEtapaDto.FACTIBILIDAD, NombreEtapaDto.DISENO,
                NombreEtapaDto.EJECUCION);
    }

    @Cuando("el Técnico URP selecciona {string}, {string} y {string}")
    public void el_tecnico_urp_selecciona_tipo_capital_tamano_y_complejidad(String tipoCapital, String tamano,
            String complejidadValor) {
        criterios.setTipoCapital(mapearTipoCapital(tipoCapital));
        criterios.setTamanioProyecto(mapearTamanio(tamano));
        criterios.setComplejidad(mapearComplejidad(complejidadValor));
    }

    @Entonces("el sistema sugiere la ruta {string}")
    public void el_sistema_sugiere_la_ruta(String rutaSugerida) {
        sugerida = service.generarRutaPreinversion(proyecto.getId(), criterios);
        assertThat(sugerida.getEtapasSugeridas()).containsExactlyInAnyOrderElementsOf(mapearRutaSugerida(rutaSugerida));
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el sistema traslada las etapas de Preinversión a la pantalla del Anexo A.1")
    public void el_sistema_traslada_las_etapas_a_registro_de_etapas() {
        rutaAceptada = service.aceptarRutaPreinversion(proyecto.getId(), criterios);
        assertThat(rutaAceptada.getEtapasAceptadas()).containsExactlyInAnyOrder(sugerida.getEtapasSugeridas().toArray(new NombreEtapaDto[0]));
        List<EtapaDto> etapas = service.listarEtapas(proyecto.getId());
        assertThat(etapas).hasSize(5);
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("las etapas {string} y {string} aparecen seleccionadas por defecto en la Ruta de Preinversión")
    public void las_etapas_aparecen_seleccionadas_por_defecto(String etapa1, String etapa2) {
        RutaPreinversionSugeridaDto resultado = service.generarRutaPreinversion(proyecto.getId(), new CriteriosCalificacionDto());
        assertThat(resultado.getEtapasSugeridas()).contains(mapearEtapa(etapa1), mapearEtapa(etapa2));
        RequestContextHolder.resetRequestAttributes();
    }

    @Cuando("el Técnico URP hace clic en {string} sin haber calificado el criterio {string}")
    public void el_tecnico_urp_hace_clic_sin_haber_calificado_el_criterio(String boton, String criterioFaltante) {
        CriteriosCalificacionDto casiCompletos = new CriteriosCalificacionDto()
                .tipoCapital(TipoCapitalDto.CAPITAL_FISICO)
                .tamanioProyecto(TamanioProyectoDto.MEDIANO)
                .complejidad(ComplejidadProyectoDto.ALTA);
        quitarCriterio(casiCompletos, criterioFaltante);
        violaciones = validator.validate(casiCompletos);
    }

    @Entonces("el sistema no genera la Ruta de Preinversión, ya que la calificación de cada criterio es obligatoria \\(RN01)")
    public void el_sistema_no_genera_la_ruta_por_criterio_faltante() {
        assertThat(violaciones).isNotEmpty();
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("que el proyecto tiene como Iniciativa de Inversión {string} \\(registrada en CU-PRE-01)")
    public void que_el_proyecto_tiene_como_iniciativa_de_inversion(String iniciativaLabel) {
        crearProyectoYAutenticar(mapearIniciativa(iniciativaLabel));
    }

    @Entonces("el sistema desactiva el botón {string}")
    public void el_sistema_desactiva_el_boton(String boton) {
        CriteriosCalificacionDto criterio = new CriteriosCalificacionDto().tipoCapital(TipoCapitalDto.CAPITAL_FISICO)
                        .tamanioProyecto(TamanioProyectoDto.MEDIANO).complejidad(ComplejidadProyectoDto.ALTA);
        Long idProyecto = proyecto.getId();
        assertThatThrownBy(() -> service.generarRutaPreinversion(idProyecto, criterio))
                .isInstanceOf(ConflictoEstadoException.class);
    }

    @Entonces("muestra por defecto en Registro de Etapas las etapas PERFIL y EJECUCIÓN")
    public void muestra_por_defecto_en_registro_de_etapas_perfil_y_ejecucion() {
        List<EtapaDto> etapas = service.listarEtapas(proyecto.getId());
        assertThat(etapas).extracting(EtapaDto::getNombreEtapa)
                .containsExactlyInAnyOrder(NombreEtapaDto.PERFIL, NombreEtapaDto.EJECUCION);
        RequestContextHolder.resetRequestAttributes();
    }

    // -----------------------------------------------------------------------------------------

    private void crearProyectoYAutenticar(IniciativaInversion iniciativa) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-35-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository
                .save(ProyectoFixtures.nuevaUnidadEjecutora("UE-35-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        String nombreUsuario = "tecnico.urp.bdd.35." + sufijo;
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
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-35-" + sufijo, "Eje temático de prueba"));

        autenticarComo(nombreUsuario);

        proyecto = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto CU-PRE-3.5 BDD",
                EstadoProyecto.CUP_ASIGNADO, unidadEjecutora, institucion, sector, ejeTematico));
        proyecto.setIniciativaInversion(iniciativa);
        proyecto = proyectoRepository.save(proyecto);
    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private void aplicarCriterio(CriteriosCalificacionDto criterios, String criterio, String valor) {
        switch (criterio) {
            case "Tipo de capital que genera" -> criterios.setTipoCapital(mapearTipoCapital(valor));
            case "Tamaño del proyecto según monto" -> criterios.setTamanioProyecto(mapearTamanio(valor));
            case "Complejidad del proyecto" -> criterios.setComplejidad(mapearComplejidad(valor));
            default -> throw new IllegalArgumentException("Criterio no reconocido: " + criterio);
        }
    }

    private void quitarCriterio(CriteriosCalificacionDto criterios, String criterio) {
        switch (criterio) {
            case "Tipo de capital que genera" -> criterios.setTipoCapital(null);
            case "Tamaño del proyecto según monto" -> criterios.setTamanioProyecto(null);
            case "Complejidad del proyecto" -> criterios.setComplejidad(null);
            default -> throw new IllegalArgumentException("Criterio no reconocido: " + criterio);
        }
    }

    private TipoCapitalDto mapearTipoCapital(String valor) {
        return switch (valor) {
            case "Capital Físico" -> TipoCapitalDto.CAPITAL_FISICO;
            case "Capital Humano" -> TipoCapitalDto.CAPITAL_HUMANO;
            case "Capital Institucional" -> TipoCapitalDto.CAPITAL_INSTITUCIONAL;
            case "Otros Capitales", "Otros capitales" -> TipoCapitalDto.OTROS_CAPITALES;
            default -> throw new IllegalArgumentException("Tipo de capital no reconocido: " + valor);
        };
    }

    /**
     * Traduce la etiqueta de "ruta_sugerida" de la matriz del Anexo B.2 (p. ej. "Perfil con Diseño
     * Básico", "Perfil + Prefactibilidad + Factibilidad + Diseño") a las etapas esperadas. Ejecución
     * no aparece en ninguna etiqueta de la matriz, pero se agrega siempre (RN09: habilitada por
     * defecto junto con Perfil, ver comentario de calcularEtapasSugeridas en el servicio).
     */
    private List<NombreEtapaDto> mapearRutaSugerida(String etiqueta) {
        List<NombreEtapaDto> etapas = new java.util.ArrayList<>();
        etapas.add(NombreEtapaDto.PERFIL);
        if (etiqueta.contains("Prefactibilidad")) {
            etapas.add(NombreEtapaDto.PREFACTIBILIDAD);
        }
        if (etiqueta.contains("Factibilidad")) {
            etapas.add(NombreEtapaDto.FACTIBILIDAD);
        }
        if (etiqueta.contains("Diseño")) {
            etapas.add(NombreEtapaDto.DISENO);
        }
        etapas.add(NombreEtapaDto.EJECUCION);
        return etapas;
    }

    private TamanioProyectoDto mapearTamanio(String valor) {
        return switch (valor) {
            case "Pequeño" -> TamanioProyectoDto.PEQUENIO;
            case "Mediano" -> TamanioProyectoDto.MEDIANO;
            case "Grande" -> TamanioProyectoDto.GRANDE;
            default -> throw new IllegalArgumentException("Tamaño de proyecto no reconocido: " + valor);
        };
    }

    private ComplejidadProyectoDto mapearComplejidad(String valor) {
        return switch (valor) {
            case "Complejidad Baja" -> ComplejidadProyectoDto.BAJA;
            case "Complejidad Media" -> ComplejidadProyectoDto.MEDIA;
            case "Complejidad Alta" -> ComplejidadProyectoDto.ALTA;
            case "Todas las complejidades" -> ComplejidadProyectoDto.TODAS_LAS_COMPLEJIDADES;
            default -> throw new IllegalArgumentException("Complejidad no reconocida: " + valor);
        };
    }

    private IniciativaInversion mapearIniciativa(String etiqueta) {
        return switch (etiqueta) {
            case "Programa" -> IniciativaInversion.PROGRAMA;
            case "Proyecto" -> IniciativaInversion.PROYECTO;
            case "Estudios Generales" -> IniciativaInversion.ESTUDIO_GENERAL;
            default -> throw new IllegalArgumentException("Iniciativa no reconocida: " + etiqueta);
        };
    }

    private NombreEtapaDto mapearEtapa(String etiqueta) {
        return switch (etiqueta) {
            case "Perfil" -> NombreEtapaDto.PERFIL;
            case "Prefactibilidad" -> NombreEtapaDto.PREFACTIBILIDAD;
            case "Factibilidad" -> NombreEtapaDto.FACTIBILIDAD;
            case "Diseño" -> NombreEtapaDto.DISENO;
            case "Ejecución" -> NombreEtapaDto.EJECUCION;
            default -> throw new IllegalArgumentException("Etapa no reconocida: " + etiqueta);
        };
    }
}
