package sv.gob.mh.siip.model.preinversion.enums;

import static org.assertj.core.api.Assertions.assertThat;

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
}
