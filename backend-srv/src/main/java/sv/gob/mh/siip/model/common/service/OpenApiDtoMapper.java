package sv.gob.mh.siip.model.common.service;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

/**
 * Conversor de borde entre las estructuras internas y los DTO generados desde
 * OpenAPI. Mantiene esa traducci\u00f3n fuera de los controladores HTTP.
 */
@Service
public class OpenApiDtoMapper {

    private final ObjectMapper objectMapper;

    public OpenApiDtoMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T aDto(Map<String, Object> valores, Class<T> tipoDto) {
        return objectMapper.convertValue(valores, tipoDto);
    }

    public Map<String, Object> aMapa(Object dto) {
        return objectMapper.convertValue(dto, new TypeReference<Map<String, Object>>() {
        });
    }
}
