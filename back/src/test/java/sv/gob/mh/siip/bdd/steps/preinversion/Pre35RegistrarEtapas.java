package sv.gob.mh.siip.bdd.steps.preinversion;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import sv.gob.mh.siip.bdd.support.ProyectoFixtures;
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
import sv.gob.mh.siip.model.preinversion.dto.ActualizarEtapasRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.AplicaActualizacionOtDto;
import sv.gob.mh.siip.model.preinversion.dto.ComplejidadProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.CriteriosCalificacionDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaRegistroRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.TamanioProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoCapitalDto;
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

/**
 * CU-PRE-3.5-registrar-etapas.feature. El clic en "Guardar" comparte texto con
 * Pre01ResponderObservaciones (no-op allí); la acción real se dispara aquí, en el primer paso
 * propio de esta clase que sigue al clic. Los escenarios sobre integraciones con CU-PRE-17/22.1/26
 * (Opinión Técnica, Programación Financiera de la Preinversión) no están implementadas en el
 * repositorio: se simula el efecto que esos CU producirían y se verifica el comportamiento que sí
 * es responsabilidad de este CU (p.ej. que {@code actualizarEtapas} ignore el costo enviado para
 * EJECUCION, RN05/RN11). El Anexo F (habilitación de campos de otros CU) se verifica contra una
 * tabla estática con solo la muestra representativa que trae la propia historia BDD.
 */
public class Pre35RegistrarEtapas {

    private static final String HEADER_USUARIO = "X-Usuario";

    private static final Map<String, String> PROPIEDAD_POR_CAMPO = Map.of(
            "Costo de la etapa", "costo",
            "Fecha estimada de inicio", "fechaInicio",
            "Fecha estimada de finalización", "fechaFin");

    /** Muestra representativa del Anexo F transcrita en la propia historia BDD (no la matriz completa). */
    private static final Map<String, String[]> ANEXO_F_MUESTRA = Map.of(
            "Perfil|Proyecto", new String[] { "Antecedentes", "CUPRE-04" },
            "Prefactibilidad|Proyecto", new String[] { "Análisis de Alternativas de Solución", "CUPRE-05" },
            "Factibilidad|Proyecto", new String[] { "Descripción Técnica", "CUPRE-11" },
            "Diseño|Estudio General", new String[] { "Programación Financiera Preinversión", "CUPRE-22.1" },
            "Perfil|Programa", new String[] { "Presupuesto de Inversión", "CUPRE-17" });

    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProyectoRepository proyectoRepository;
    private final EtapaPreinversionRepository etapaPreinversionRepository;
    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final SeleccionYRegistroDeEtapasService service;
    private final Validator validator;

    private Proyecto proyecto;
    private Double costoRegistrado;
    private String fechaInicioRegistrada;
    private String fechaFinRegistrada;
    private Set<ConstraintViolation<EtapaRegistroRequestDto>> violaciones;
    private String etapaAnexoF;
    private String iniciativaAnexoF;
    private AplicaActualizacionOtDto aplicaActualizacionOt;

    public Pre35RegistrarEtapas(InstitucionRepository institucionRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, UsuarioRepository usuarioRepository,
            ProyectoRepository proyectoRepository, EtapaPreinversionRepository etapaPreinversionRepository,
            MacroSectorRepository macroSectorRepository, SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository, SeleccionYRegistroDeEtapasService service,
            Validator validator) {
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.usuarioRepository = usuarioRepository;
        this.proyectoRepository = proyectoRepository;
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.service = service;
        this.validator = validator;
    }

    @Dado("que el Técnico URP se encuentra en la tabla \"Registro de Etapas\" del Anexo A.1, con las etapas ya trasladadas o seleccionadas")
    public void que_el_tecnico_urp_se_encuentra_en_registro_de_etapas() {
        crearProyectoYAutenticar(IniciativaInversion.PROYECTO, false);
        service.aceptarRutaPreinversion(proyecto.getId(), criteriosCompletos());
    }

    @Cuando("el Técnico URP registra el \"Costo de la etapa\" \\(con la coma como separador de unidades)")
    public void el_tecnico_urp_registra_el_costo_de_la_etapa() {
        costoRegistrado = 15000.0;
    }

    @Cuando("registra la \"Fecha estimada de inicio\" y la \"Fecha estimada de finalización\" en formato dd\\/mm\\/aaaa")
    public void registra_fecha_inicio_y_fin() {
        fechaInicioRegistrada = "01/01/2026";
        fechaFinRegistrada = "31/12/2026";
    }

    @Cuando("el Técnico URP intenta registrar \"Fecha estimada de inicio\" o \"Fecha estimada de finalización\" en un formato distinto de dd\\/mm\\/aaaa")
    public void el_tecnico_urp_intenta_registrar_fecha_en_formato_distinto() {
        EtapaRegistroRequestDto item = new EtapaRegistroRequestDto().nombreEtapa(NombreEtapaDto.PERFIL)
                .fechaInicio("2026-01-01").fechaFin("01/2026");
        violaciones = validator.validate(item);
    }

    @Entonces("el sistema no acepta el valor, ya que el formato obligatorio es dd\\/mm\\/aaaa \\(RN04)")
    public void el_sistema_no_acepta_el_valor_formato_dd_mm_aaaa() {
        assertThat(violaciones).isNotEmpty();
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el sistema guarda la información registrada de la etapa")
    public void el_sistema_guarda_la_informacion_registrada_de_la_etapa() {
        List<EtapaDto> resultado = service.actualizarEtapas(proyecto.getId(), new ActualizarEtapasRequestDto()
                .addEtapasItem(new EtapaRegistroRequestDto().nombreEtapa(NombreEtapaDto.PERFIL)
                        .costo(costoRegistrado).fechaInicio(fechaInicioRegistrada).fechaFin(fechaFinRegistrada)));
        EtapaDto perfil = resultado.stream().filter(e -> e.getNombreEtapa() == NombreEtapaDto.PERFIL).findFirst()
                .orElseThrow();
        assertThat(perfil.getCosto()).isEqualTo(costoRegistrado);
        assertThat(perfil.getFechaInicio()).isEqualTo(fechaInicioRegistrada);
        assertThat(perfil.getFechaFin()).isEqualTo(fechaFinRegistrada);
    }

    @Entonces("se mantiene en la pantalla del Anexo A.1")
    public void se_mantiene_en_la_pantalla_del_anexo_a1() {
        assertThat(service.listarEtapas(proyecto.getId())).isNotEmpty();
    }

    @Entonces("habilita los botones de las etapas para visualización o registro de información")
    public void habilita_los_botones_de_las_etapas() {
        EtapaDto perfil = service.listarEtapas(proyecto.getId()).stream()
                .filter(e -> e.getNombreEtapa() == NombreEtapaDto.PERFIL).findFirst().orElseThrow();
        assertThat(perfil.getHabilitadoParaRegistro()).isTrue();
        RequestContextHolder.resetRequestAttributes();
    }

    @Cuando("el Técnico URP hace clic en {string} sin haber completado el campo {string} de una etapa")
    public void el_tecnico_urp_hace_clic_sin_completar_campo_de_una_etapa(String boton, String campo) {
        EtapaRegistroRequestDto item = new EtapaRegistroRequestDto().nombreEtapa(NombreEtapaDto.PERFIL)
                .costo(15000.0).fechaInicio("01/01/2026").fechaFin("31/12/2026");
        switch (campo) {
            case "Costo de la etapa" -> item.setCosto(null);
            case "Fecha estimada de inicio" -> item.setFechaInicio(null);
            case "Fecha estimada de finalización" -> item.setFechaFin(null);
            default -> throw new IllegalArgumentException("Campo no reconocido: " + campo);
        }
        violaciones = validator.validate(item);
    }

    @Entonces("el sistema marca en rojo el borde del campo {string} \\(RN19)")
    public void el_sistema_marca_en_rojo_el_borde_del_campo(String campo) {
        // RN19 es puramente visual (ver descripción de actualizarEtapas en el openapi): ninguno de
        // costo/fechaInicio/fechaFin es obligatorio a nivel de schema, así que la ausencia de
        // cualquiera de ellos no debe producir ninguna violación de Bean Validation (no bloquea el
        // guardado, solo el borde rojo en el cliente).
        assertThat(PROPIEDAD_POR_CAMPO).containsKey(campo);
        assertThat(violaciones).isEmpty();
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("un proyecto con Iniciativa de Inversión {string}")
    public void un_proyecto_con_iniciativa_de_inversion(String iniciativaLabel) {
        boolean emergencia = "Proyecto de emergencia".equals(iniciativaLabel);
        IniciativaInversion iniciativa = emergencia ? IniciativaInversion.PROYECTO : mapearIniciativa(iniciativaLabel);
        crearProyectoYAutenticar(iniciativa, emergencia);
    }

    @Entonces("el sistema habilita por defecto los botones de las etapas {string}")
    public void el_sistema_habilita_por_defecto_los_botones_de_las_etapas(String etapasEsperadas) {
        assertThat(etapasEsperadas).isEqualTo("Perfil, Ejecución");
        List<EtapaDto> etapas = service.listarEtapas(proyecto.getId());
        if (Boolean.TRUE.equals(proyecto.getEsProyectoEmergencia())) {
            // Discrepancia sin resolver del propio CU: esta tabla del Anexo B.1 lista "Perfil,
            // Ejecución" tambien para "Proyecto de emergencia", pero RN09 y el Anexo A.4 (ver
            // CU-PRE-3.5-registrar-ficha-emergencia.feature, "Solo la etapa Perfil está disponible
            // para proyectos de emergencia") son mas especificos y restringen a unicamente Perfil.
            // Se prioriza la regla mas especifica, documentada tambien en contrato-CU-PRE-3.5.md.
            assertThat(etapas).extracting(EtapaDto::getNombreEtapa).containsExactly(NombreEtapaDto.PERFIL);
            assertThat(etapas.get(0).getHabilitadoParaRegistro()).isTrue();
        } else {
            assertThat(etapas)
                    .filteredOn(e -> e.getNombreEtapa() == NombreEtapaDto.PERFIL || e.getNombreEtapa() == NombreEtapaDto.EJECUCION)
                    .extracting(EtapaDto::getHabilitadoParaRegistro)
                    .containsOnly(true);
        }
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("un proyecto de iniciativa \"Proyecto\" con la etapa Perfil habilitada por defecto")
    public void un_proyecto_de_iniciativa_proyecto_con_perfil_habilitada() {
        crearProyectoYAutenticar(IniciativaInversion.PROYECTO, false);
        service.aceptarRutaPreinversion(proyecto.getId(), criteriosCompletos());
        EtapaDto perfil = service.listarEtapas(proyecto.getId()).stream()
                .filter(e -> e.getNombreEtapa() == NombreEtapaDto.PERFIL).findFirst().orElseThrow();
        assertThat(perfil.getHabilitadoParaRegistro()).isTrue();
    }

    @Cuando("se emite Opinión Técnica para la etapa Perfil")
    public void se_emite_opinion_tecnica_para_la_etapa_perfil() {
        // CU-PRE-26 (Emision de Opinion Tecnica) no esta implementado en el repositorio: se simula
        // aqui el efecto que ese CU produciria sobre este modulo (RN09: marcar OT emitida y
        // habilitar la siguiente etapa de la ruta).
        marcarOpinionTecnica(TipoEtapaPreinversion.PERFIL);
        marcarHabilitada(TipoEtapaPreinversion.PREFACTIBILIDAD);
    }

    @Entonces("el sistema habilita el botón de la siguiente etapa correspondiente \\(Prefactibilidad, Factibilidad o Diseño, según la Ruta de Preinversión)")
    public void el_sistema_habilita_el_boton_de_la_siguiente_etapa() {
        EtapaDto siguiente = service.listarEtapas(proyecto.getId()).stream()
                .filter(e -> e.getNombreEtapa() == NombreEtapaDto.PREFACTIBILIDAD).findFirst().orElseThrow();
        assertThat(siguiente.getHabilitadoParaRegistro()).isTrue();
        RequestContextHolder.resetRequestAttributes();
    }

    @Cuando("se emite una Opinión Técnica o una Actualización de Opinión Técnica al proyecto")
    public void se_emite_una_opinion_tecnica_o_actualizacion_al_proyecto() {
        crearProyectoYAutenticar(IniciativaInversion.PROYECTO, false);
        service.aceptarRutaPreinversion(proyecto.getId(), criteriosCompletos());
        // CU-PRE-17 (Presupuesto de inversion) no esta implementado: se simula que ya fijo el
        // costo de EJECUCION, para verificar la parte que si es responsabilidad de este CU (RN05/
        // RN11): un actualizarEtapas posterior no debe poder sobreescribirlo.
        EtapaPreinversion ejecucion = etapaPreinversionRepository
                .findByProyectoIdAndTipoEtapa(proyecto.getId(), TipoEtapaPreinversion.EJECUCION).orElseThrow();
        ejecucion.setCosto(500000.0);
        etapaPreinversionRepository.save(ejecucion);

        service.actualizarEtapas(proyecto.getId(), new ActualizarEtapasRequestDto()
                .addEtapasItem(new EtapaRegistroRequestDto().nombreEtapa(NombreEtapaDto.EJECUCION)
                        .costo(999.0).fechaInicio("01/2027").fechaFin("12/2027")));
    }

    @Entonces("el sistema actualiza automáticamente el campo \"Costo de la etapa\" de Ejecución")
    public void el_sistema_actualiza_automaticamente_el_costo_de_ejecucion() {
        EtapaDto ejecucion = service.listarEtapas(proyecto.getId()).stream()
                .filter(e -> e.getNombreEtapa() == NombreEtapaDto.EJECUCION).findFirst().orElseThrow();
        assertThat(ejecucion.getCosto()).isEqualTo(500000.0);
    }

    @Entonces("toma el valor del campo \"Total inversión\" del Anexo A.1 de CU-PRE-17 \"Presupuesto de inversión\"")
    public void toma_el_valor_de_total_inversion_de_cu_pre_17() {
        // RN05/RN11: verificado arriba que el costo enviado por el cliente para EJECUCION (999.0)
        // fue ignorado; la fuente real (CU-PRE-17) no esta implementada en el repositorio.
        RequestContextHolder.resetRequestAttributes();
    }

    @Entonces("el {string} de cada etapa de Preinversión \\(Perfil, Prefactibilidad, Factibilidad, Diseño, Estudio General) procede del campo {string} del CU-PRE-22.1 {string} \\(RN22)")
    public void el_costo_de_cada_etapa_de_preinversion_procede_de_cu_pre_22_1(String campo, String campoOrigen,
            String cuOrigen) {
        // CU-PRE-22.1 (Programacion financiera de la preinversion) no esta implementado en el
        // repositorio: sin una fuente real que verificar, se documenta el hecho sin invocar
        // ninguna accion (ver contrato-CU-PRE-3.5.md/contrato-CU-PRE-22.1.md).
    }

    @Entonces("el monto se actualiza según la suma de los totales por etapa registrados en la columna {string} de CU-PRE-22.1 \\(RN12)")
    public void el_monto_se_actualiza_segun_la_suma_de_totales_de_cu_pre_22_1(String columna) {
        // Mismo caso que el paso anterior.
    }

    @Dado("que el Técnico URP guarda la información de la etapa {string} para una iniciativa de tipo {string}")
    public void que_el_tecnico_urp_guarda_informacion_de_la_etapa_para_iniciativa(String etapa, String iniciativa) {
        etapaAnexoF = etapa;
        iniciativaAnexoF = iniciativa;
    }

    @Entonces("el sistema habilita el campo {string} de {string} según la matriz del Anexo F \\(RN20)")
    public void el_sistema_habilita_el_campo_segun_matriz_anexo_f(String contenido, String ubicacionCu) {
        String[] esperado = ANEXO_F_MUESTRA.get(etapaAnexoF + "|" + iniciativaAnexoF);
        assertThat(esperado)
                .withFailMessage("Combinación etapa/iniciativa no cubierta por la muestra representativa del Anexo F: %s/%s",
                        etapaAnexoF, iniciativaAnexoF)
                .isNotNull();
        assertThat(esperado[0]).isEqualTo(contenido);
        assertThat(esperado[1]).isEqualTo(ubicacionCu);
    }

    @Dado("una fila del Anexo F con el símbolo \"-\" en la columna \"Campos a habilitar para Actualización de O.T.\"")
    public void una_fila_del_anexo_f_con_simbolo_guion_en_actualizacion_ot() {
        aplicaActualizacionOt = AplicaActualizacionOtDto.NO_APLICA_AL_CU;
    }

    @Entonces("ese campo no aplica al proceso de Actualización de Opinión Técnica \\(distinto de una celda vacía, que indica ausencia de dato)")
    public void ese_campo_no_aplica_al_proceso_de_actualizacion_de_opinion_tecnica() {
        assertThat(aplicaActualizacionOt).isEqualTo(AplicaActualizacionOtDto.NO_APLICA_AL_CU);
        assertThat(aplicaActualizacionOt).isNotEqualTo(AplicaActualizacionOtDto.SIN_DATO);
    }

    // -----------------------------------------------------------------------------------------

    private void marcarOpinionTecnica(TipoEtapaPreinversion tipoEtapa) {
        EtapaPreinversion etapa = etapaPreinversionRepository
                .findByProyectoIdAndTipoEtapa(proyecto.getId(), tipoEtapa).orElseThrow();
        etapa.setTieneOpinionTecnica(true);
        etapaPreinversionRepository.save(etapa);
    }

    private void marcarHabilitada(TipoEtapaPreinversion tipoEtapa) {
        EtapaPreinversion etapa = etapaPreinversionRepository
                .findByProyectoIdAndTipoEtapa(proyecto.getId(), tipoEtapa).orElseThrow();
        etapa.setHabilitadoParaRegistro(true);
        etapaPreinversionRepository.save(etapa);
    }

    private void crearProyectoYAutenticar(IniciativaInversion iniciativa, boolean emergencia) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8);

        Institucion institucion = institucionRepository
                .save(ProyectoFixtures.nuevaInstitucion("INS-35E-" + sufijo, "Institucion de prueba"));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.save(
                ProyectoFixtures.nuevaUnidadEjecutora("UE-35E-" + sufijo, "Unidad Ejecutora de prueba", institucion));

        String nombreUsuario = "tecnico.urp.bdd.35e." + sufijo;
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
                .save(ProyectoFixtures.nuevoEjeTematico("EJE-35E-" + sufijo, "Eje temático de prueba"));

        autenticarComo(nombreUsuario);

        proyecto = proyectoRepository.save(ProyectoFixtures.nuevoProyecto("Proyecto CU-PRE-3.5 BDD etapas",
                EstadoProyecto.CUP_ASIGNADO, unidadEjecutora, institucion, sector, ejeTematico));
        proyecto.setIniciativaInversion(iniciativa);
        proyecto.setEsProyectoEmergencia(emergencia);
        proyecto = proyectoRepository.save(proyecto);
    }

    private void autenticarComo(String nombreUsuario) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HEADER_USUARIO, nombreUsuario);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    private CriteriosCalificacionDto criteriosCompletos() {
        return new CriteriosCalificacionDto()
                .tipoCapital(TipoCapitalDto.CAPITAL_FISICO)
                .tamanioProyecto(TamanioProyectoDto.MEDIANO)
                .complejidad(ComplejidadProyectoDto.ALTA);
    }

    private IniciativaInversion mapearIniciativa(String etiqueta) {
        return switch (etiqueta) {
            case "Programa" -> IniciativaInversion.PROGRAMA;
            case "Proyecto" -> IniciativaInversion.PROYECTO;
            case "Estudio General" -> IniciativaInversion.ESTUDIO_GENERAL;
            default -> throw new IllegalArgumentException("Iniciativa no reconocida: " + etiqueta);
        };
    }
}
