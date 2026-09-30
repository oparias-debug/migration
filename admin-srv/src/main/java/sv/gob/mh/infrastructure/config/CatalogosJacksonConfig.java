package sv.gob.mh.infrastructure.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import sv.gob.mh.api.dto.catalogo.CatalogDescriptorsUpdateRequestDto;
import sv.gob.mh.api.dto.catalogo.DescriptoresCatalogoInformados;

/**
 * Deserialización que exige el contrato de CU-ADM-01:
 * <ul>
 * <li>El cuerpo de {@code actualizarDescriptoresCatalogo} se lee como
 * {@link DescriptoresCatalogoInformados}, para distinguir {@code parent: null} (desvincular) de
 * {@code parent} ausente (sin cambios). Se hace con un mixin porque el DTO es generado.</li>
 * <li>Una propiedad no declarada en el contrato es un error: así se detecta el {@code code} que
 * intenta cambiar un código de catálogo inmutable (Regla 17, 422 CODIGO_CATALOGO_INMUTABLE).
 * Solo cambia la lectura de solicitudes: el resto del servicio solo serializa con este mapper.</li>
 * </ul>
 */
@Configuration
public class CatalogosJacksonConfig {

    @JsonDeserialize(as = DescriptoresCatalogoInformados.class)
    interface DescriptoresCatalogoMixin {
    }

    @Bean
    Jackson2ObjectMapperBuilderCustomizer catalogosJackson() {
        return builder -> builder
                .mixIn(CatalogDescriptorsUpdateRequestDto.class, DescriptoresCatalogoMixin.class)
                .failOnUnknownProperties(true);
    }
}
