package sv.gob.mh.siip.model.preinversion.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.domain.AnalisisLegal;
import sv.gob.mh.siip.model.preinversion.domain.AnalsisGestionesLegalesRequeridas;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisLegalDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaAnalisisLegalRequestDto;

class AnalisisLegalMapperTest {

    private final AnalisisLegalMapper mapper = new AnalisisLegalMapper();

    private static AnalsisGestionesLegalesRequeridas fila(String gestion, String entregable, Double costo) {
        return AnalsisGestionesLegalesRequeridas.builder()
                .analisisGestionLegalRequerida(gestion).entregable(entregable).costoEntregable(costo).build();
    }

    @Test
    void toDto_entidadNula_devuelveNull() {
        assertThat(mapper.toDto(null)).isNull();
    }

    @Test
    void toDto_conProyectoYFilas_mapeaFilasYSumaCostosIgnorandoNulos() {
        AnalisisLegal entidad = AnalisisLegal.builder()
                .proyecto(Proyecto.builder().id(7L).build())
                .requiereAnalisisLegal(true)
                .filas(new ArrayList<>(List.of(fila("Permiso municipal", "Resolución", 150.5),
                        fila("Escritura", "Testimonio", null))))
                .build();

        AnalisisLegalDto dto = mapper.toDto(entidad);

        assertThat(dto.getIdProyecto()).isEqualTo(7L);
        assertThat(dto.getRequiereAnalisisLegal()).isTrue();
        assertThat(dto.getTotalCostoEntregables()).isEqualTo(150.5);
        assertThat(dto.getFilas()).extracting(FilaAnalisisLegalRequestDto::getEntregable)
                .containsExactly("Resolución", "Testimonio");
        assertThat(dto.getFilas().get(0).getAnalisisGestionLegalRequerida()).isEqualTo("Permiso municipal");
        assertThat(dto.getFilas().get(1).getCostoEntregable()).isNull();
    }

    @Test
    void toDto_sinProyectoNiFilas_dejaIdNuloYTotalCero() {
        AnalisisLegal entidad = AnalisisLegal.builder().requiereAnalisisLegal(false).build();

        AnalisisLegalDto dto = mapper.toDto(entidad);

        assertThat(dto.getIdProyecto()).isNull();
        assertThat(dto.getRequiereAnalisisLegal()).isFalse();
        assertThat(dto.getFilas()).isEmpty();
        assertThat(dto.getTotalCostoEntregables()).isZero();
    }

    @Test
    void toDto_filasNulas_devuelveListaVaciaYTotalCero() {
        AnalisisLegal entidad = AnalisisLegal.builder().requiereAnalisisLegal(true).build();
        entidad.setFilas(null);

        AnalisisLegalDto dto = mapper.toDto(entidad);

        assertThat(dto.getFilas()).isEmpty();
        assertThat(dto.getTotalCostoEntregables()).isZero();
    }

    @Test
    void toFilaDto_entidadNula_devuelveNull() {
        assertThat(mapper.toFilaDto(null)).isNull();
    }

    @Test
    void toEntity_dtoNulo_devuelveNull() {
        assertThat(mapper.toEntity(null, new AnalisisLegal())).isNull();
    }

    @Test
    void toEntity_conDto_construyeDetalleAsociadoAlPadre() {
        AnalisisLegal padre = new AnalisisLegal();
        FilaAnalisisLegalRequestDto dto = new FilaAnalisisLegalRequestDto()
                .analisisGestionLegalRequerida("Permiso ambiental").entregable("Resolución MARN")
                .costoEntregable(300d);

        AnalsisGestionesLegalesRequeridas entidad = mapper.toEntity(dto, padre);

        assertThat(entidad.getAnalisisLegal()).isSameAs(padre);
        assertThat(entidad.getAnalisisGestionLegalRequerida()).isEqualTo("Permiso ambiental");
        assertThat(entidad.getEntregable()).isEqualTo("Resolución MARN");
        assertThat(entidad.getCostoEntregable()).isEqualTo(300d);
    }
}
