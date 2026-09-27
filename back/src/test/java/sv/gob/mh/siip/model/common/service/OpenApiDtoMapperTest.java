package sv.gob.mh.siip.model.common.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;

class OpenApiDtoMapperTest {

    private final OpenApiDtoMapper mapper = new OpenApiDtoMapper(new ObjectMapper());

    @Test
    void aDto_convierteElMapaEnElDtoGenerado() {
        ErrorDetalleDto dto = mapper.aDto(Map.of("campo", "nombre", "mensaje", "Es obligatorio"),
                ErrorDetalleDto.class);

        assertThat(dto.getCampo()).isEqualTo("nombre");
        assertThat(dto.getMensaje()).isEqualTo("Es obligatorio");
    }

    @Test
    void aMapa_convierteElDtoEnMapaDeValores() {
        ErrorDetalleDto dto = new ErrorDetalleDto().campo("monto").mensaje("Debe ser positivo");

        Map<String, Object> valores = mapper.aMapa(dto);

        assertThat(valores).containsEntry("campo", "monto").containsEntry("mensaje", "Debe ser positivo");
    }
}
