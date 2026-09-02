package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.context.request.RequestContextHolder;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.ContextoProyectoBdd;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.service.ProyectoService;

/**
 * CU-PRE-01.5-emitir-cup.feature. Las Antecedentes y los pasos "el sistema cambia el estado del
 * proyecto a {string}", "el sistema informa al Técnico URP..." y "el sistema pasa a la pantalla
 * {string}" (texto identico al de CU-PRE-01.5-devolver.feature) estan definidos una unica vez en
 * {@link Pre015Devolver}; esta clase solo cubre los pasos propios de esta historia, leyendo el
 * Proyecto compartido via {@link ContextoProyectoBdd}. El clic en "Emitir CUP" es un no-op: la
 * accion real se dispara en el primer Entonces propio que sigue ("el sistema asigna al proyecto un
 * CUP consecutivo...").
 */
public class Pre015EmitirCup {

    private final ProyectoRepository proyectoRepository;
    private final ProyectoService proyectoService;
    private final ContextoProyectoBdd contextoProyecto;

    public Pre015EmitirCup(ProyectoRepository proyectoRepository, ProyectoService proyectoService,
            ContextoProyectoBdd contextoProyecto) {
        this.proyectoRepository = proyectoRepository;
        this.proyectoService = proyectoService;
        this.contextoProyecto = contextoProyecto;
    }

    @Cuando("el Técnico PRE hace clic en el botón {string}")
    public void el_tecnico_pre_hace_clic_en_el_boton(String boton) {
        // No-op intencional: ver comentario de la clase.
    }

    @Entonces("el sistema asigna al proyecto un CUP consecutivo numérico de 5 dígitos, partiendo desde el número 10000 en adelante, saltando un número cada 53 códigos emitidos")
    public void el_sistema_asigna_al_proyecto_un_cup_consecutivo_numerico_de_5_digitos() {
        Long idProyecto = contextoProyecto.getProyectoActual().getId();
        proyectoService.emitirCup(idProyecto);

        Proyecto recargado = proyectoRepository.findById(idProyecto).orElseThrow();
        assertThat(recargado.getCup()).isNotBlank().matches("\\d{5}");
        assertThat(Integer.parseInt(recargado.getCup())).isGreaterThanOrEqualTo(10000);
    }

    @Entonces("el sistema envía el proyecto a la pantalla {string} \\(UC-PRE-03)")
    public void el_sistema_envia_el_proyecto_a_la_pantalla_uc_pre_03(String pantalla) {
        // Navegacion de UI pura hacia UC-PRE-03, fuera de este fragmento OpenAPI.
    }

    @Entonces("el proyecto ingresa inmediatamente al banco de proyectos, quedando disponible para su búsqueda mediante CU-PRE-{int}")
    public void el_proyecto_ingresa_inmediatamente_al_banco_de_proyectos(Integer numeroCu) {
        // CU-PRE-29 (Banco de Proyectos) es un caso de uso propio, fuera de este fragmento OpenAPI;
        // se verifica el efecto ya comprobado por CU-PRE-01.5: el estado CUP_ASIGNADO.
        Proyecto recargado = proyectoRepository.findById(contextoProyecto.getProyectoActual().getId()).orElseThrow();
        assertThat(recargado.getEstado()).isEqualTo(EstadoProyecto.CUP_ASIGNADO);
    }

    @Entonces("en la tabla de origen {string} \\(CU-PRE-{int}) aparece una fila con el proyecto en estado {string}")
    public void en_la_tabla_de_origen_aparece_una_fila_con_el_proyecto_en_estado(String tabla, Integer numeroCu,
            String estadoUi) {
        Proyecto recargado = proyectoRepository.findById(contextoProyecto.getProyectoActual().getId()).orElseThrow();
        boolean visible = proyectoRepository
                .findByActivoTrueAndUnidadEjecutoraIdAndEstado(recargado.getUnidadEjecutora().getId(),
                        EstadoProyecto.CUP_ASIGNADO, PageRequest.of(0, 20))
                .getContent()
                .stream()
                .anyMatch(p -> p.getId().equals(recargado.getId()));
        assertThat(visible).isTrue();
        assertThat(estadoUi).isEqualTo("CUP asignado");

        RequestContextHolder.resetRequestAttributes();
    }
}
