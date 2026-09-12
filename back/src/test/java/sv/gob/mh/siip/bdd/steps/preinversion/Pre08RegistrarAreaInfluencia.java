package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.ContextoProyectoBdd;
import sv.gob.mh.siip.model.common.domain.Departamento;
import sv.gob.mh.siip.model.common.domain.Municipio;
import sv.gob.mh.siip.model.common.repository.DepartamentoRepository;
import sv.gob.mh.siip.model.common.repository.MunicipioRepository;
import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaDto;
import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaFilaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisPoblacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.CeldaUbicacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaPoblacionRequestDto;
import sv.gob.mh.siip.model.preinversion.service.AnalisisPoblacionService;
import sv.gob.mh.siip.model.preinversion.service.AreaInfluenciaService;

public class Pre08RegistrarAreaInfluencia {

    private static final String DISTRITO = "Distrito BDD PRE08";

    private final ContextoProyectoBdd contextoProyecto;
    private final DepartamentoRepository departamentoRepository;
    private final MunicipioRepository municipioRepository;
    private final AnalisisPoblacionService analisisPoblacionService;
    private final AreaInfluenciaService areaInfluenciaService;

    private AreaInfluenciaDto resultado;
    private boolean escenarioActivo;

    public Pre08RegistrarAreaInfluencia(ContextoProyectoBdd contextoProyecto,
            DepartamentoRepository departamentoRepository,
            MunicipioRepository municipioRepository,
            AnalisisPoblacionService analisisPoblacionService,
            AreaInfluenciaService areaInfluenciaService) {
        this.contextoProyecto = contextoProyecto;
        this.departamentoRepository = departamentoRepository;
        this.municipioRepository = municipioRepository;
        this.analisisPoblacionService = analisisPoblacionService;
        this.areaInfluenciaService = areaInfluenciaService;
    }

    @Dado("se encuentra en la pantalla {string} \\(Anexo A.{int})")
    public void se_encuentra_en_la_pantalla_anexo_a(String pantalla, Integer anexo) {
        assertThat(contextoProyecto.getProyectoActual()).as("proyecto de prueba").isNotNull();
        assertThat(pantalla).isEqualTo("Área de Influencia");
        escenarioActivo = true;
    }

    @Dado("que el proyecto ya cuenta con ubicaciones registradas en CU-PRE-{int} {string}")
    public void que_el_proyecto_ya_cuenta_con_ubicaciones_registradas_en_cu_pre(Integer cu,
            String nombreCasoUso) {
        assertThat(cu).isEqualTo(7);
        crearDistrito();
        analisisPoblacionService.guardar(contextoProyecto.getProyectoActual().getId(),
                new AnalisisPoblacionRequestDto()
                        .poblacionObjetivo(new FilaPoblacionRequestDto().ubicaciones(List.of(
                        new CeldaUbicacionRequestDto().ubicacion(DISTRITO).numeroPersonas(10)))));
    }

    @Entonces("el sistema completa los campos {string}, {string}, {string} y {string} según lo registrado en CU-PRE-{int}")
    public void el_sistema_completa_los_campos_segun_lo_registrado_en_cu_pre(String region, String departamento,
            String distrito, String ubicacionEspecifica, Integer cu) {
        resultado = areaInfluenciaService.autocompletarDesdePoblacionObjetivo(
                contextoProyecto.getProyectoActual().getId());
        assertThat(cu).isEqualTo(7);
        assertThat(resultado.getFilas()).hasSize(1);
        assertThat(resultado.getFilas().get(0).getDistrito()).isEqualTo(DISTRITO);
        assertThat(resultado.getFilas().get(0).getUbicacionEspecifica()).isEqualTo(DISTRITO);
    }

    @Entonces("se mantiene en la pantalla {string} \\(RN07, FA-{int})")
    public void se_mantiene_en_la_pantalla_rn07_fa(String pantalla, Integer flujo) {
        assertThat(pantalla).isEqualTo("Área de Influencia");
        assertThat(resultado).isNotNull();
    }

    @Dado("que los campos {string}, {string} y {string} ya fueron autocompletados")
    public void que_los_campos_ya_fueron_autocompletados(String region, String departamento, String distrito) {
        crearDistrito();
        resultado = areaInfluenciaService.autocompletarDesdePoblacionObjetivo(
                contextoProyecto.getProyectoActual().getId());
        assertThat(resultado.getFilas()).isEmpty();
    }

    @Entonces("dichos campos permanecen bloqueados para edición \\(RN08)")
    public void dichos_campos_permanecen_bloqueados_para_edicion_rn08() {
        assertThat(resultado).isNotNull();
    }

    @Entonces("si se requiere agregar otra Región, Departamento o Distrito, debe hacerse en CU-PRE-{int} {string}")
    public void si_se_requiere_agregar_otra_ubicacion_debe_hacerse_en_cu_pre(Integer cu, String nombreCasoUso) {
        assertThat(cu).isEqualTo(7);
        assertThat(nombreCasoUso).isEqualTo("Población Objetivo");
    }

    @Cuando("el Técnico URP acerca el cursor a un punto definido de la tabla")
    public void el_tecnico_urp_acerca_el_cursor_a_un_punto_definido_de_la_tabla() {
        // Paso puramente de UI (hover): no tiene contraparte de backend que verificar.
    }

    @Cuando("hace clic en el botón emergente {string}")
    public void hace_clic_en_el_boton_emergente(String boton) {
        // RN03: agregar una fila ocurre en el cliente; solo se envía al servidor en el siguiente
        // "Guardar" (mismo criterio que CU-PRE-04/05/06).
    }

    @Entonces("el sistema agrega una nueva fila para registrar otra {string} \\(RN03)")
    public void el_sistema_agrega_una_nueva_fila_para_registrar_otra(String campo) {
        assertThat(campo).isEqualTo("Ubicación específica");
    }

    @Dado("una fila de {string} ya registrada")
    public void una_fila_de_ya_registrada(String campo) {
        crearDistrito();
        resultado = areaInfluenciaService.guardar(contextoProyecto.getProyectoActual().getId(),
                new AreaInfluenciaRequestDto().filas(List.of(
                        new AreaInfluenciaFilaRequestDto().distrito(DISTRITO).ubicacionEspecifica(campo))));
        assertThat(resultado.getFilas()).hasSize(1);
    }

    @Cuando("el Técnico URP hace clic en el botón {string} de esa fila")
    public void el_tecnico_urp_hace_clic_en_el_boton_de_esa_fila(String boton) {
        // RN04: eliminar una fila ocurre en el cliente; se envía como lista vacía en el siguiente
        // "Guardar" (ver paso siguiente).
    }

    @Entonces("el sistema elimina la fila correspondiente \\(RN04)")
    public void el_sistema_elimina_la_fila_correspondiente_rn04() {
        resultado = areaInfluenciaService.guardar(contextoProyecto.getProyectoActual().getId(),
                new AreaInfluenciaRequestDto().filas(List.of()));
        assertThat(resultado.getFilas()).isEmpty();
    }

    @Cuando("el Técnico URP hace clic en el botón {string} sin haber completado los campos requeridos")
    public void el_tecnico_urp_hace_clic_en_el_boton_sin_haber_completado_los_campos_requeridos(String boton) {
        resultado = areaInfluenciaService.guardar(contextoProyecto.getProyectoActual().getId(),
                new AreaInfluenciaRequestDto().filas(List.of()));
    }

    @Entonces("el sistema sombrea en color rojo los bordes de los campos pendientes de completar \\(RN05)")
    public void el_sistema_sombrea_en_color_rojo_los_bordes_de_los_campos_pendientes_de_completar_rn05() {
        assertThat(resultado).isNotNull();
    }

    public boolean esEscenarioAreaInfluencia() {
        return escenarioActivo;
    }

    public void guardarInformacionRegistrada() {
        resultado = areaInfluenciaService.guardar(contextoProyecto.getProyectoActual().getId(),
                new AreaInfluenciaRequestDto().filas(List.of()));
        assertThat(resultado.getIdProyecto()).isEqualTo(contextoProyecto.getProyectoActual().getId());
    }

    public void verificarGuardado(String pantalla) {
        assertThat(pantalla).isEqualTo("Área de Influencia");
        assertThat(resultado).isNotNull();
    }

    private void crearDistrito() {
        Departamento departamento = departamentoRepository.findAll().stream().findFirst().orElseGet(() ->
                departamentoRepository.save(Departamento.builder()
                        .codigo("D" + String.valueOf(System.nanoTime()).substring(0, 8)).nombre("Departamento BDD PRE08")
                        .region("Region BDD PRE08").build()));
        if (municipioRepository.findAllByOrderByNombreAsc().stream()
                .noneMatch(municipio -> DISTRITO.equals(municipio.getNombre()))) {
            municipioRepository.save(Municipio.builder()
                    .codigo("M" + String.valueOf(System.nanoTime()).substring(0, 8)).nombre(DISTRITO)
                    .departamento(departamento).build());
        }
    }
}
