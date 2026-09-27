package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.dto.TipoSolicitudDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoSolicitud;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud;

class BandejaPreinversionFiltrosTest {

    private Root<SolicitudPreinversion> root;
    private CriteriaQuery<?> query;
    private CriteriaBuilder cb;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        root = mock(Root.class, RETURNS_DEEP_STUBS);
        query = mock(CriteriaQuery.class);
        cb = mock(CriteriaBuilder.class);
    }

    @Test
    void activas_combinaProyectoActivoEstadoDeSolicitudYEstadoDelProyecto() {
        Predicate combinado = mock(Predicate.class);
        when(cb.and(any(), any(), any())).thenReturn(combinado);

        assertThat(BandejaPreinversionFiltros.activas().toPredicate(root, query, cb)).isSameAs(combinado);
        verify(cb).isTrue(any());
    }

    @Test
    void archivadas_filtraPorEstadoArchivada() {
        Path<Object> estado = mock(Path.class);
        when(root.get("estado")).thenReturn(estado);

        BandejaPreinversionFiltros.archivadas().toPredicate(root, query, cb);

        verify(cb).equal(estado, EstadoSolicitud.ARCHIVADA);
    }

    @Test
    void asignadasA_filtraPorIdDelTecnicoAsignado() {
        BandejaPreinversionFiltros.asignadasA(5L).toPredicate(root, query, cb);

        verify(cb).equal(any(), eq(5L));
    }

    @Test
    void deTipo_sinTipo_noFiltra() {
        Predicate todos = mock(Predicate.class);
        when(cb.conjunction()).thenReturn(todos);

        assertThat(BandejaPreinversionFiltros.deTipo(null).toPredicate(root, query, cb)).isSameAs(todos);
        verify(cb, never()).equal(any(), any(Object.class));
    }

    @Test
    void deTipo_conTipo_filtraPorElTipoDeSolicitud() {
        Path<Object> tipo = mock(Path.class);
        when(root.get("tipoSolicitud")).thenReturn(tipo);

        BandejaPreinversionFiltros.deTipo(TipoSolicitudDto.OPINION_TECNICA).toPredicate(root, query, cb);

        verify(cb).equal(tipo, TipoSolicitud.OPINION_TECNICA);
    }

    @Test
    void pagina_sinParametros_usaPrimeraPaginaDeVeinteOrdenadaPorFechaDescendente() {
        assertThat(BandejaPreinversionFiltros.pagina(null, null))
                .isEqualTo(PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "fechaSolicitud", "id")));
    }

    @Test
    void pagina_limitesValidos_losRespeta() {
        assertThat(BandejaPreinversionFiltros.pagina(3, 200).getPageSize()).isEqualTo(200);
        assertThat(BandejaPreinversionFiltros.pagina(3, 1).getPageNumber()).isEqualTo(3);
    }

    @Test
    void pagina_fueraDeRango_lanzaValidacion() {
        assertThatThrownBy(() -> BandejaPreinversionFiltros.pagina(-1, 20))
                .isInstanceOf(ValidacionNegocioException.class);
        assertThatThrownBy(() -> BandejaPreinversionFiltros.pagina(0, 0))
                .isInstanceOf(ValidacionNegocioException.class);
        assertThatThrownBy(() -> BandejaPreinversionFiltros.pagina(0, 201))
                .isInstanceOf(ValidacionNegocioException.class);
    }
}
