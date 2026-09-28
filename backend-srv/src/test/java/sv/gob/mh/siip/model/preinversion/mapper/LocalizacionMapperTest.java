package sv.gob.mh.siip.model.preinversion.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.domain.Localizacion;
import sv.gob.mh.siip.model.preinversion.dto.CoordenadasDto;

class LocalizacionMapperTest {

    private final LocalizacionMapper mapper = new LocalizacionMapperImpl();

    @Test
    void mapToCoordenadas_conLatitudYLongitud_devuelveCoordenadas() {
        Localizacion entidad = Localizacion.builder()
                .latitud(BigDecimal.valueOf(13.69)).longitud(BigDecimal.valueOf(-89.19)).build();

        CoordenadasDto dto = mapper.mapToCoordenadasDto(entidad);

        assertThat(dto.getLatitud()).isEqualTo(13.69);
        assertThat(dto.getLongitud()).isEqualTo(-89.19);
    }

    @Test
    void mapToCoordenadas_sinLatitud_devuelveNull() {
        Localizacion entidad = Localizacion.builder().longitud(BigDecimal.ONE).build();

        assertThat(mapper.mapToCoordenadasDto(entidad)).isNull();
    }

    @Test
    void mapToCoordenadas_sinLongitud_devuelveNull() {
        Localizacion entidad = Localizacion.builder().latitud(BigDecimal.ONE).build();

        assertThat(mapper.mapToCoordenadasDto(entidad)).isNull();
    }
}
