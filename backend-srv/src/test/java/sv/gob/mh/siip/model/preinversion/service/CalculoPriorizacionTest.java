package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.enums.ValorCalificacion;

/** Pruebas unitarias de {@link CalculoPriorizacion} (CU-PRE-26.5, RN12 a RN14). */
class CalculoPriorizacionTest {

    @Test
    void unSubcriterioSinCalificarConservaSuPonderacionYNoSumaPuntaje() {
        CalculoPriorizacion.Resultado resultado = CalculoPriorizacion.calcular(List.of(
                new CalculoPriorizacion.Criterio(1, 50, List.of(
                        new CalculoPriorizacion.Subcriterio("1.1", 60, ValorCalificacion.CINCO),
                        new CalculoPriorizacion.Subcriterio("1.2", 40, null))),
                new CalculoPriorizacion.Criterio(2, 50, List.of(
                        new CalculoPriorizacion.Subcriterio("2.1", 100, null)))));

        assertThat(resultado.subcriterio("1.1").puntaje()).isEqualByComparingTo("30.00");
        assertThat(resultado.subcriterio("1.2").ponderacionAplicada()).isEqualTo(40.0);
        assertThat(resultado.subcriterio("1.2").puntaje()).isNull();
        assertThat(resultado.criterio(2).puntaje()).isNull();
        assertThat(resultado.prioridad()).isEqualByComparingTo("30.00");
    }

    @Test
    void siTodosLosCriteriosSonNoAplicaLaPrioridadEsCero() {
        CalculoPriorizacion.Resultado resultado = CalculoPriorizacion.calcular(List.of(
                new CalculoPriorizacion.Criterio(1, 100, List.of(
                        new CalculoPriorizacion.Subcriterio("1.1", 100, ValorCalificacion.NO_APLICA)))));

        assertThat(resultado.criterio(1).ponderacionAplicada()).isZero();
        assertThat(resultado.subcriterio("1.1").ponderacionAplicada()).isZero();
        assertThat(resultado.prioridad()).isEqualByComparingTo("0");
    }

    @Test
    void unCriterioSinSubcriteriosNoSeTrataComoNoAplica() {
        CalculoPriorizacion.Resultado resultado = CalculoPriorizacion.calcular(List.of(
                new CalculoPriorizacion.Criterio(1, 100, List.of())));

        assertThat(resultado.criterio(1).ponderacionAplicada()).isEqualTo(100.0);
        assertThat(resultado.criterio(1).puntaje()).isNull();
        assertThatThrownBy(() -> resultado.subcriterio("9.9")).isInstanceOf(NoSuchElementException.class);
    }
}
