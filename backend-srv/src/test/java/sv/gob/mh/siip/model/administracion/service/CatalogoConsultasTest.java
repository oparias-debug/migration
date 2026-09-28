package sv.gob.mh.siip.model.administracion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.dto.CatalogDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogExistenceResponseDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogSummaryDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoVigencia;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.security.ActorContexto;

/** Pruebas unitarias de {@link CatalogoConsultas} (CU-ADM-01). */
class CatalogoConsultasTest {

    private final ActorContexto actorContexto = mock(ActorContexto.class);
    private final CatalogoRepository catalogoRepository = mock(CatalogoRepository.class);
    private final CatalogoConsultas consultas = new CatalogoConsultas(actorContexto, catalogoRepository,
            new CatalogoBusqueda(catalogoRepository));

    private final Catalogo catalogo = Catalogo.builder().codigo("CAT").nombre("Catalogo")
            .estado(EstadoVigencia.ACTIVE).build();

    @Test
    void listar_mapeaLaPaginaDeCatalogos() {
        PageRequest pagina = PageRequest.of(0, 10);
        when(catalogoRepository.findAll(pagina)).thenReturn(new PageImpl<>(List.of(catalogo), pagina, 1));

        Page<CatalogDto> resultado = consultas.listar(pagina);

        assertThat(resultado.getContent()).extracting(CatalogDto::getCode).containsExactly("CAT");
        verify(actorContexto).exigirRol(RolUsuario.ADMINISTRADOR_DE_CATALOGOS);
    }

    @Test
    void verificarExistencia_devuelveNombreYResultado() {
        when(catalogoRepository.existsByNombreIgnoreCase("Catalogo")).thenReturn(true);

        CatalogExistenceResponseDto respuesta = consultas.verificarExistencia("Catalogo");

        assertThat(respuesta.getName()).isEqualTo("Catalogo");
        assertThat(respuesta.getExists()).isTrue();
    }

    @Test
    void consultar_existente_devuelveElCatalogo() {
        when(catalogoRepository.findByCodigo("CAT")).thenReturn(Optional.of(catalogo));

        assertThat(consultas.consultar("CAT").getName()).isEqualTo("Catalogo");
    }

    @Test
    void consultarHijos_devuelveResumenDeCadaHijo() {
        when(catalogoRepository.findByCodigo("CAT")).thenReturn(Optional.of(catalogo));
        when(catalogoRepository.findByCatalogoPadreCodigo("CAT"))
                .thenReturn(List.of(Catalogo.builder().codigo("HIJO").nombre("Hijo").build()));

        List<CatalogSummaryDto> hijos = consultas.consultarHijos("CAT");

        assertThat(hijos).extracting(CatalogSummaryDto::getCode).containsExactly("HIJO");
    }

    @Test
    void consultarHijos_catalogoInexistente_lanzaRecursoNoEncontrado() {
        when(catalogoRepository.findByCodigo("CAT")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> consultas.consultarHijos("CAT")).isInstanceOf(RecursoNoEncontradoException.class);
        verify(catalogoRepository, never()).findByCatalogoPadreCodigo(any());
    }
}
