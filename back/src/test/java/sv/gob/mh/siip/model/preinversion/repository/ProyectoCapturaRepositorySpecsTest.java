package sv.gob.mh.siip.model.preinversion.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoCapturaRepository.Specs;

class ProyectoCapturaRepositorySpecsTest {

    private Root<Proyecto> root;
    private CriteriaQuery<?> query;
    private CriteriaBuilder cb;
    private Predicate predicado;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        root = mock(Root.class, RETURNS_DEEP_STUBS);
        query = mock(CriteriaQuery.class);
        cb = mock(CriteriaBuilder.class);
        predicado = mock(Predicate.class);
    }

    @Test
    void fetchUnidadEjecutora_consultaDeEntidades_haceFetchDeLaUnidadEjecutora() {
        doReturn(Proyecto.class).when(query).getResultType();

        assertThat(Specs.fetchUnidadEjecutora().toPredicate(root, query, cb)).isNull();
        verify(root).fetch(Specs.FIELD_UNIDAD_EJECUTORA, JoinType.LEFT);
    }

    @Test
    void fetchUnidadEjecutora_consultaDeConteo_noHaceFetch() {
        doReturn(Long.class).when(query).getResultType();
        assertThat(Specs.fetchUnidadEjecutora().toPredicate(root, query, cb)).isNull();

        doReturn(boolean.class).when(query).getResultType();
        assertThat(Specs.fetchUnidadEjecutora().toPredicate(root, query, cb)).isNull();

        verify(root, never()).fetch(anyString(), any(JoinType.class));
    }

    @Test
    void esValidoParaCaptura_exigeCupNoNuloYProyectoActivo() {
        when(cb.and(any(), any())).thenReturn(predicado);

        assertThat(Specs.esValidoParaCaptura().toPredicate(root, query, cb)).isSameAs(predicado);
        verify(cb).isNotNull(any());
        verify(cb).isTrue(any());
    }

    @Test
    void byUnidadEjecutora_sinId_noFiltra() {
        assertThat(Specs.byUnidadEjecutora(null).toPredicate(root, query, cb)).isNull();
        verifyNoInteractions(cb);
    }

    @Test
    void byUnidadEjecutora_conId_filtraPorLaUnidad() {
        when(cb.equal(any(), any(Object.class))).thenReturn(predicado);

        assertThat(Specs.byUnidadEjecutora(4L).toPredicate(root, query, cb)).isSameAs(predicado);
    }

    @Test
    void byBusquedaGeneral_sinTermino_noFiltra() {
        assertThat(Specs.byBusquedaGeneral(null).toPredicate(root, query, cb)).isNull();
        assertThat(Specs.byBusquedaGeneral("  ").toPredicate(root, query, cb)).isNull();
        verifyNoInteractions(cb);
    }

    @Test
    void byBusquedaGeneral_conTermino_buscaEnCupNombreYUnidad() {
        when(cb.or(any(), any(), any())).thenReturn(predicado);

        assertThat(Specs.byBusquedaGeneral(" PuEnte ").toPredicate(root, query, cb)).isSameAs(predicado);
        verify(cb, times(3)).like(any(), eq("%puente%"));
    }

    @Test
    void byCup_sinValor_noFiltra() {
        assertThat(Specs.byCup(null).toPredicate(root, query, cb)).isNull();
        assertThat(Specs.byCup("").toPredicate(root, query, cb)).isNull();
        verifyNoInteractions(cb);
    }

    @Test
    void byCup_conValor_buscaCoincidenciaParcialSinMayusculas() {
        Expression<String> cupEnMinusculas = expresion();
        when(cb.lower(any())).thenReturn(cupEnMinusculas);
        when(cb.like(cupEnMinusculas, "%cup-1%")).thenReturn(predicado);

        assertThat(Specs.byCup(" CUP-1 ").toPredicate(root, query, cb)).isSameAs(predicado);
    }

    @Test
    void byNombre_sinValor_noFiltra() {
        assertThat(Specs.byNombre(null).toPredicate(root, query, cb)).isNull();
        assertThat(Specs.byNombre(" ").toPredicate(root, query, cb)).isNull();
        verifyNoInteractions(cb);
    }

    @Test
    void byNombre_conValor_buscaCoincidenciaParcialSinMayusculas() {
        Expression<String> nombreEnMinusculas = expresion();
        when(cb.lower(any())).thenReturn(nombreEnMinusculas);
        when(cb.like(nombreEnMinusculas, "%escuela%")).thenReturn(predicado);

        assertThat(Specs.byNombre("Escuela").toPredicate(root, query, cb)).isSameAs(predicado);
    }

    @Test
    void byIniciativa_sinValor_noFiltra() {
        assertThat(Specs.byIniciativa(null).toPredicate(root, query, cb)).isNull();
        assertThat(Specs.byIniciativa("").toPredicate(root, query, cb)).isNull();
        verifyNoInteractions(cb);
    }

    @Test
    void byIniciativa_valorValido_filtraPorElEnumerado() {
        Path<Object> iniciativa = camino(Specs.FIELD_INICIATIVA);
        when(cb.equal(iniciativa, IniciativaInversion.PROGRAMA)).thenReturn(predicado);

        assertThat(Specs.byIniciativa("PROGRAMA").toPredicate(root, query, cb)).isSameAs(predicado);
    }

    @Test
    void byIniciativa_valorInvalido_fuerzaResultadoVacio() {
        when(cb.disjunction()).thenReturn(predicado);

        assertThat(Specs.byIniciativa("NO_EXISTE").toPredicate(root, query, cb)).isSameAs(predicado);
    }

    @Test
    void byEstado_sinValor_noFiltra() {
        assertThat(Specs.byEstado(null).toPredicate(root, query, cb)).isNull();
        assertThat(Specs.byEstado(" ").toPredicate(root, query, cb)).isNull();
        verifyNoInteractions(cb);
    }

    @Test
    void byEstado_valorValido_filtraPorElEnumerado() {
        Path<Object> estado = camino(Specs.FIELD_ESTADO);
        when(cb.equal(estado, EstadoProyecto.EN_REGISTRO)).thenReturn(predicado);

        assertThat(Specs.byEstado("EN_REGISTRO").toPredicate(root, query, cb)).isSameAs(predicado);
    }

    @Test
    void byEstado_valorInvalido_fuerzaResultadoVacio() {
        when(cb.disjunction()).thenReturn(predicado);

        assertThat(Specs.byEstado("NO_EXISTE").toPredicate(root, query, cb)).isSameAs(predicado);
    }

    @SuppressWarnings("unchecked")
    private static Expression<String> expresion() {
        return mock(Expression.class);
    }

    @SuppressWarnings("unchecked")
    private Path<Object> camino(String campo) {
        Path<Object> path = mock(Path.class);
        when(root.get(campo)).thenReturn(path);
        return path;
    }
}
