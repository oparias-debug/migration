package sv.gob.mh.siip.model.administracion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;

/** Pruebas unitarias de {@link CatalogoBusqueda} (CU-ADM-01). */
class CatalogoBusquedaTest {

    private final CatalogoRepository catalogoRepository = mock(CatalogoRepository.class);
    private final CatalogoBusqueda busqueda = new CatalogoBusqueda(catalogoRepository);

    @Test
    void obtenerPorCodigo_existente_devuelveElCatalogo() {
        Catalogo catalogo = Catalogo.builder().codigo("CAT").build();
        when(catalogoRepository.findByCodigo("CAT")).thenReturn(Optional.of(catalogo));

        assertThat(busqueda.obtenerPorCodigo("CAT")).isSameAs(catalogo);
    }

    @Test
    void obtenerPorCodigo_inexistente_lanzaRecursoNoEncontrado() {
        when(catalogoRepository.findByCodigo("CAT")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> busqueda.obtenerPorCodigo("CAT"))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("El catálogo indicado no existe.");
    }

    @Test
    void exigirCatalogoPadreExistente_sinPadreOEnBlanco_noConsulta() {
        assertThatCode(() -> busqueda.exigirCatalogoPadreExistente(null)).doesNotThrowAnyException();
        assertThatCode(() -> busqueda.exigirCatalogoPadreExistente("  ")).doesNotThrowAnyException();
        verify(catalogoRepository, never()).findByCodigo(any());
    }

    @Test
    void exigirCatalogoPadreExistente_padreExistente_noLanza() {
        when(catalogoRepository.findByCodigo("PADRE")).thenReturn(Optional.of(Catalogo.builder().build()));

        assertThatCode(() -> busqueda.exigirCatalogoPadreExistente("PADRE")).doesNotThrowAnyException();
    }

    @Test
    void exigirCatalogoPadreExistente_padreInexistente_lanzaRecursoNoEncontrado() {
        when(catalogoRepository.findByCodigo("PADRE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> busqueda.exigirCatalogoPadreExistente("PADRE"))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("El catálogo padre indicado no existe.");
    }
}
