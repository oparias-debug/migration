package sv.gob.mh.siip.model.preinversion.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.domain.Componente;
import sv.gob.mh.siip.model.preinversion.dto.TipoCostoResumenDto;

class DescripcionTecnicaMapperTest {

    private final DescripcionTecnicaMapper mapper = new DescripcionTecnicaMapperImpl();

    @Test
    void mapComponenteToTipoCosto_componenteNulo_devuelveNull() {
        assertThat(mapper.mapComponenteToTipoCosto(null)).isNull();
    }

    @Test
    void mapComponenteToTipoCosto_conId_usaElIdComoCodigo() {
        Componente componente = Componente.builder().id(12L).nombre("Obra civil").build();

        TipoCostoResumenDto dto = mapper.mapComponenteToTipoCosto(componente);

        assertThat(dto.getCodigo()).isEqualTo("12");
        assertThat(dto.getNombre()).isEqualTo("Obra civil");
    }

    @Test
    void mapComponenteToTipoCosto_sinId_dejaCodigoNulo() {
        Componente componente = Componente.builder().nombre("Equipamiento").build();

        TipoCostoResumenDto dto = mapper.mapComponenteToTipoCosto(componente);

        assertThat(dto.getCodigo()).isNull();
        assertThat(dto.getNombre()).isEqualTo("Equipamiento");
    }
}
