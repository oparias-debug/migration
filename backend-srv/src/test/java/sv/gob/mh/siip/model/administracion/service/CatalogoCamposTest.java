package sv.gob.mh.siip.model.administracion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.administracion.domain.CampoDefinicion;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.dto.CatalogFieldDto;
import sv.gob.mh.siip.model.administracion.dto.FieldQualifierDto;
import sv.gob.mh.siip.model.administracion.enums.TipoCampo;

/** Pruebas unitarias de {@link CatalogoCampos} (CU-ADM-01, Reglas 2 y 3). */
class CatalogoCamposTest {

    private static CatalogFieldDto campo(String nombre, FieldQualifierDto calificador, Integer posicion) {
        return new CatalogFieldDto().name(nombre).qualifier(calificador).position(posicion);
    }

    @Test
    void validar_conKeyYNombresUnicos_noLanza() {
        List<CatalogFieldDto> campos = List.of(campo("codigo", FieldQualifierDto.KEY, null),
                campo("nombre", FieldQualifierDto.FIELD, null));

        assertThatCode(() -> CatalogoCampos.validarCampoKeyYNombresUnicos(campos)).doesNotThrowAnyException();
    }

    @Test
    void validar_sinKey_lanzaCampoKeyRequerido() {
        List<CatalogFieldDto> campos = List.of(campo("nombre", FieldQualifierDto.FIELD, null));

        assertThatThrownBy(() -> CatalogoCampos.validarCampoKeyYNombresUnicos(campos))
                .isInstanceOfSatisfying(ValidacionNegocioException.class,
                        ex -> assertThat(ex.getCodigo()).isEqualTo("CAMPO_KEY_REQUERIDO"));
    }

    @Test
    void validar_nombresRepetidos_lanzaNombresCampoRepetidos() {
        List<CatalogFieldDto> campos = List.of(campo("codigo", FieldQualifierDto.KEY, null),
                campo("codigo", FieldQualifierDto.FIELD, null));

        assertThatThrownBy(() -> CatalogoCampos.validarCampoKeyYNombresUnicos(campos))
                .isInstanceOfSatisfying(ValidacionNegocioException.class,
                        ex -> assertThat(ex.getCodigo()).isEqualTo("NOMBRES_CAMPO_REPETIDOS"));
    }

    @Test
    void reemplazarCampos_sustituyeLaDefinicionConPosicionExplicitaOPorDefecto() {
        Catalogo catalogo = Catalogo.builder().codigo("CAT").build();
        catalogo.getCampos().add(CampoDefinicion.builder().nombre("viejo").build());

        CatalogoCampos.reemplazarCampos(catalogo, List.of(campo("codigo", FieldQualifierDto.KEY, 7),
                campo("nombre", FieldQualifierDto.FIELD, null)));

        assertThat(catalogo.getCampos()).hasSize(2);
        CampoDefinicion primero = catalogo.getCampos().get(0);
        assertThat(primero.getNombre()).isEqualTo("codigo");
        assertThat(primero.isEsKey()).isTrue();
        assertThat(primero.getPosicion()).isEqualTo(7);
        assertThat(primero.getTipo()).isEqualTo(TipoCampo.STRING);
        assertThat(primero.getCatalogo()).isSameAs(catalogo);
        CampoDefinicion segundo = catalogo.getCampos().get(1);
        assertThat(segundo.isEsKey()).isFalse();
        assertThat(segundo.getPosicion()).isEqualTo(1);
    }
}
