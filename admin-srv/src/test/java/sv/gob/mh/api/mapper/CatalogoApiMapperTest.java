package sv.gob.mh.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sv.gob.mh.api.dto.catalogo.ActiveStatusDto;
import sv.gob.mh.api.dto.catalogo.CatalogDescriptorsUpdateRequestDto;
import sv.gob.mh.api.dto.catalogo.DescriptoresCatalogoInformados;
import sv.gob.mh.api.dto.catalogo.InactivationRequestDto;
import sv.gob.mh.domain.model.catalogo.CambioDescriptores;
import sv.gob.mh.domain.model.catalogo.CambioDescriptores.Descriptor;
import sv.gob.mh.shared.enums.EstadoVigencia;

/** Traducción del contrato CU-ADM-01 en los casos que no cubren los escenarios BDD. */
class CatalogoApiMapperTest {

    @Test
    @DisplayName("Sin lista de campos no hay campos nuevos, y una inactivación sin cuerpo no trae fecha")
    void solicitudesSinDatosOpcionales() {
        LocalDate ayer = LocalDate.of(2026, 9, 29);

        assertThat(CatalogoApiMapper.aNuevosCampos(null)).isNull();
        assertThat(CatalogoApiMapper.fechaHasta(null)).isNull();
        assertThat(CatalogoApiMapper.fechaHasta(new InactivationRequestDto().toDate(ayer))).isEqualTo(ayer);
    }

    @Test
    @DisplayName("Un DTO armado en código informa solo sus propiedades no nulas")
    void dtoArmadoEnCodigoInformaLasNoNulas() {
        CatalogDescriptorsUpdateRequestDto request = new CatalogDescriptorsUpdateRequestDto()
                .name("Naciones")
                .active(ActiveStatusDto.INACTIVE);

        CambioDescriptores cambio = DescriptoresApiMapper.aCambioDescriptores(request);

        assertThat(DescriptoresCatalogoInformados.informadas(request))
                .containsExactlyInAnyOrder(DescriptoresCatalogoInformados.PROPIEDAD_NAME,
                        DescriptoresCatalogoInformados.PROPIEDAD_ACTIVE);
        assertThat(cambio.informados()).containsExactlyInAnyOrder(Descriptor.NOMBRE, Descriptor.ESTADO);
        assertThat(cambio.estado()).isEqualTo(EstadoVigencia.INACTIVE);
    }

    @Test
    @DisplayName("El DTO deserializado recuerda también las propiedades que vinieron nulas")
    void elDtoDeserializadoRecuerdaLasNulas() {
        DescriptoresCatalogoInformados informados = new DescriptoresCatalogoInformados();
        informados.setParent(null);
        informados.setFromDate(null);
        informados.setToDate(null);
        DescriptoresCatalogoInformados igual = new DescriptoresCatalogoInformados();

        assertThat(DescriptoresCatalogoInformados.informadas(informados))
                .containsExactlyInAnyOrder(DescriptoresCatalogoInformados.PROPIEDAD_PARENT,
                        DescriptoresCatalogoInformados.PROPIEDAD_FROM_DATE,
                        DescriptoresCatalogoInformados.PROPIEDAD_TO_DATE);
        assertThat(informados).isEqualTo(igual).hasSameHashCodeAs(igual);
    }
}
