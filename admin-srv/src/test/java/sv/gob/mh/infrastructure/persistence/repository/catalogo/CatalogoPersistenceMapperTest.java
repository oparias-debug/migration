package sv.gob.mh.infrastructure.persistence.repository.catalogo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sv.gob.mh.domain.model.catalogo.CampoDefinicion;
import sv.gob.mh.domain.model.catalogo.Catalogo;
import sv.gob.mh.domain.model.catalogo.DefinicionTipo;
import sv.gob.mh.domain.model.catalogo.Periodo;
import sv.gob.mh.infrastructure.persistence.entity.catalogo.CampoDefinicionEntity;
import sv.gob.mh.infrastructure.persistence.entity.catalogo.CatalogoEntity;
import sv.gob.mh.infrastructure.persistence.entity.catalogo.RegistroEntity;
import sv.gob.mh.shared.enums.EstadoVigencia;

/** Sincronización del modelo de catálogo con sus entidades JPA. */
class CatalogoPersistenceMapperTest {

    @Test
    @DisplayName("Los campos se sincronizan por id: el conservado se actualiza y el desconocido se crea")
    void sincronizaLosCamposPorId() {
        CatalogoEntity entidad = new CatalogoEntity();
        CampoDefinicionEntity existente = new CampoDefinicionEntity();
        existente.setId(10L);
        existente.setNombre("codigo");
        entidad.agregarCampo(existente);
        Catalogo modelo = new Catalogo(1L, "PAISES", "Países", null, null, new Periodo(EstadoVigencia.ACTIVE, null, null),
                List.of(new CampoDefinicion(10L, "codigo", true, 1, DefinicionTipo.texto(3)),
                        new CampoDefinicion(99L, "nombre", false, 2,
                                DefinicionTipo.enumerado(List.of("A", "B")))));

        CatalogoPersistenceMapper.copiar(modelo, entidad, null);

        assertThat(entidad.getCampos()).hasSize(2);
        assertThat(entidad.getCampos().get(0)).isSameAs(existente);
        assertThat(entidad.getCampos().get(1).getId()).isNull();
        assertThat(entidad.getCampos().get(1).getNombre()).isEqualTo("nombre");
        assertThat(entidad.getCampos().get(1).getCatalogo()).isSameAs(entidad);
        assertThat(entidad.getCampos().get(0).getLongitudMaxima()).isEqualTo(3);
        assertThat(entidad.getCampos().get(1).getValoresEnum()).containsExactly("A", "B");
        assertThat(CatalogoPersistenceMapper.aModelo(entidad).buscarCampo("nombre").orElseThrow().getDefinicion())
                .isEqualTo(DefinicionTipo.enumerado(List.of("A", "B")));
    }

    @Test
    @DisplayName("RN-05: el padre se enlaza por su entidad (id) y el modelo lo recibe por código")
    void enlazaElCatalogoPadrePorId() {
        CatalogoEntity padre = new CatalogoEntity();
        padre.setId(5L);
        padre.setCodigo("REGION");
        CatalogoEntity entidad = new CatalogoEntity();
        Catalogo modelo = new Catalogo(null, "DEPARTAMENTO", "Departamento", "REGION", null,
                new Periodo(EstadoVigencia.ACTIVE, null, null),
                List.of(new CampoDefinicion(null, "codigo", true, 1, DefinicionTipo.texto(3))));

        CatalogoPersistenceMapper.copiar(modelo, entidad, padre);

        assertThat(entidad.getCatalogoPadre()).isSameAs(padre);
        assertThat(CatalogoPersistenceMapper.aModelo(entidad).getCatalogoPadreCodigo()).isEqualTo("REGION");
    }

    @Test
    @DisplayName("Las colecciones de las entidades solo cambian a través de sus métodos")
    void lasColeccionesNoSeModificanPorFuera() {
        CatalogoEntity catalogo = new CatalogoEntity();
        catalogo.setId(1L);
        RegistroEntity registro = new RegistroEntity();
        registro.setId(2L);
        registro.setCatalogo(catalogo);
        registro.reemplazarValores(Map.of("codigo", "SV"));
        CampoDefinicionEntity campo = new CampoDefinicionEntity();
        List<CampoDefinicionEntity> campos = catalogo.getCampos();
        Map<String, String> valores = registro.getValores();
        List<String> valoresEnum = campo.getValoresEnum();

        assertThatThrownBy(() -> campos.add(campo)).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> valores.put("otro", "x")).isInstanceOf(UnsupportedOperationException.class);
        assertThatThrownBy(() -> valoresEnum.add("A")).isInstanceOf(UnsupportedOperationException.class);
        catalogo.reemplazarCampos(List.of(campo));
        assertThat(catalogo.getCampos()).containsExactly(campo);
        assertThat(registro.getValores()).containsEntry("codigo", "SV");
        assertThat(registro.getCatalogo().getId()).isEqualTo(1L);
        assertThat(registro.getId()).isEqualTo(2L);
    }
}
