package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Pruebas unitarias de {@link DiasHabilesOpinionTecnica} (CU-PRE-26, RN08 y RN09). */
class DiasHabilesOpinionTecnicaTest {

    /** Lunes. */
    private static final LocalDate LUNES = LocalDate.of(2026, 9, 21);

    @Test
    void elPlazoDeCincoDiasHabilesSaltaElFinDeSemana() {
        assertThat(DiasHabilesOpinionTecnica.sumar(LUNES, 5)).isEqualTo(LUNES.plusDays(7));
        // Desde un viernes, el primer día hábil es el lunes siguiente.
        assertThat(DiasHabilesOpinionTecnica.sumar(LUNES.plusDays(4), 1)).isEqualTo(LUNES.plusDays(7));
    }

    @Test
    void cuentaLosDiasHabilesTranscurridosSinIncluirElDiaInicial() {
        assertThat(DiasHabilesOpinionTecnica.entre(LUNES, LUNES)).isZero();
        assertThat(DiasHabilesOpinionTecnica.entre(LUNES, LUNES.plusDays(3))).isEqualTo(3);
        assertThat(DiasHabilesOpinionTecnica.entre(LUNES.plusDays(4), LUNES.plusDays(6))).isZero();
        assertThat(DiasHabilesOpinionTecnica.entre(LUNES, DiasHabilesOpinionTecnica.sumar(LUNES, 5)))
                .isEqualTo(DiasHabilesOpinionTecnica.PLAZO);
    }

    @Test
    void laAdvertenciaSeEnviaDosDiasAntesDelVencimiento() {
        assertThat(DiasHabilesOpinionTecnica.ALERTA).isEqualTo(3);
    }
}
