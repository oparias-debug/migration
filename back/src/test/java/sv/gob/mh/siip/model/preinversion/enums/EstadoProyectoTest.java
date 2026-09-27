package sv.gob.mh.siip.model.preinversion.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;

class EstadoProyectoTest {

    // ProyectoServiceImpl.listar() convierte EstadoProyectoDto -> EstadoProyecto vía
    // EstadoProyecto.valueOf(estadoFiltro.name()); si un valor existe de un lado y no del otro,
    // esa conversion falla en runtime (IllegalArgumentException) en vez de en compilacion.
    @Test
    void coincideUnoAUnoConElCatalogoOficialDelContratoOpenApi() {
        Set<String> dominio = Stream.of(EstadoProyecto.values()).map(Enum::name).collect(Collectors.toSet());
        Set<String> contrato = Stream.of(EstadoProyectoDto.values()).map(Enum::name).collect(Collectors.toSet());

        assertThat(dominio).containsExactlyInAnyOrderElementsOf(contrato);
    }

    @Test
    void fromEtiquetaUi_conEtiquetaConocida_devuelveElEstado() {
        assertThat(EstadoProyecto.fromEtiquetaUi("En Elaboración")).isEqualTo(EstadoProyecto.EN_REGISTRO);
        assertThat(EstadoProyecto.fromEtiquetaUi("Proyecto con Opinión Técnica"))
                .isEqualTo(EstadoProyecto.PROYECTO_CON_OT);
    }

    @Test
    void fromEtiquetaUi_esLaInversaDeGetEtiquetaUi() {
        for (EstadoProyecto estado : EstadoProyecto.values()) {
            assertThat(EstadoProyecto.fromEtiquetaUi(estado.getEtiquetaUi())).isEqualTo(estado);
        }
    }

    @Test
    void fromEtiquetaUi_conEtiquetaDesconocida_lanzaIllegalArgument() {
        assertThatThrownBy(() -> EstadoProyecto.fromEtiquetaUi("Inexistente"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Estado no reconocido: Inexistente");
    }

    @Test
    void bloqueaFormulacion_soloEnViabilidad() {
        Set<EstadoProyecto> bloqueantes = Stream.of(EstadoProyecto.values())
                .filter(EstadoProyecto::bloqueaFormulacion)
                .collect(Collectors.toSet());

        assertThat(bloqueantes).containsExactly(EstadoProyecto.EN_VIABILIDAD);
    }
}
