package sv.gob.mh.siip.model.administracion.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.administracion.domain.CampoDefinicion;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.dto.ActiveStatusDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogFieldDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogSummaryDto;
import sv.gob.mh.siip.model.administracion.dto.FieldQualifierDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoVigencia;

/** Pruebas unitarias de {@link CatalogoDtoAssembler} (CU-ADM-01). */
class CatalogoDtoAssemblerTest {

    @Test
    void aCatalogDto_mapeaDescriptoresYCamposOrdenadosPorPosicion() {
        Catalogo catalogo = Catalogo.builder().codigo("CAT").nombre("Catalogo").catalogoPadreCodigo("PADRE")
                .estado(EstadoVigencia.ACTIVE).fechaDesde(LocalDate.of(2026, 1, 1))
                .fechaHasta(LocalDate.of(2026, 12, 31)).build();
        catalogo.getCampos().add(CampoDefinicion.builder().nombre("nombre").esKey(false).posicion(1).build());
        catalogo.getCampos().add(CampoDefinicion.builder().nombre("codigo").esKey(true).posicion(0).build());

        CatalogDto dto = CatalogoDtoAssembler.aCatalogDto(catalogo);

        assertThat(dto.getCode()).isEqualTo("CAT");
        assertThat(dto.getName()).isEqualTo("Catalogo");
        assertThat(dto.getParent()).isEqualTo("PADRE");
        assertThat(dto.getActive()).isEqualTo(ActiveStatusDto.ACTIVE);
        assertThat(dto.getFromDate()).isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(dto.getToDate()).isEqualTo(LocalDate.of(2026, 12, 31));
        assertThat(dto.getFields()).extracting(CatalogFieldDto::getName).containsExactly("codigo", "nombre");
        assertThat(dto.getFields()).extracting(CatalogFieldDto::getQualifier)
                .containsExactly(FieldQualifierDto.KEY, FieldQualifierDto.FIELD);
        assertThat(dto.getFields()).extracting(CatalogFieldDto::getPosition).containsExactly(0, 1);
    }

    @Test
    void aCatalogSummaryDto_mapeaCodigoYNombre() {
        CatalogSummaryDto resumen = CatalogoDtoAssembler.aCatalogSummaryDto(
                Catalogo.builder().codigo("HIJO").nombre("Hijo").build());

        assertThat(resumen.getCode()).isEqualTo("HIJO");
        assertThat(resumen.getName()).isEqualTo("Hijo");
    }
}
