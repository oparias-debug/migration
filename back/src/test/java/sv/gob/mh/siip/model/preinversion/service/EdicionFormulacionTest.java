package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;

/** Pruebas de la regla de bloqueo de la formulación (CU-PRE-24, RN04 y RN05). */
class EdicionFormulacionTest {

    @Test
    void soloLaSolicitudDeViabilidadEnCursoBloqueaLaFormulacion() {
        assertThat(Arrays.stream(EstadoProyecto.values()).filter(EstadoProyecto::bloqueaFormulacion))
                .containsExactly(EstadoProyecto.EN_VIABILIDAD);
    }

    @Test
    void rechazaLaEdicionEnViabilidadConElCodigoDeFormulacionBloqueada() {
        Proyecto proyecto = Proyecto.builder().estado(EstadoProyecto.EN_VIABILIDAD).build();

        assertThatThrownBy(() -> EdicionFormulacion.exigirEditable(proyecto))
                .isInstanceOf(ConflictoEstadoException.class)
                .hasMessageContaining("En viabilidad")
                .extracting(e -> ((ConflictoEstadoException) e).getCodigo())
                .isEqualTo(EdicionFormulacion.CODIGO_FORMULACION_BLOQUEADA);
    }

    @ParameterizedTest
    @EnumSource(value = EstadoProyecto.class, names = "EN_VIABILIDAD", mode = EnumSource.Mode.EXCLUDE)
    void permiteLaEdicionEnLosDemasEstados(EstadoProyecto estado) {
        Proyecto proyecto = Proyecto.builder().estado(estado).build();

        assertThatCode(() -> EdicionFormulacion.exigirEditable(proyecto)).doesNotThrowAnyException();
    }

    @Test
    void unProyectoSinEstadoNoSeBloquea() {
        assertThatCode(() -> EdicionFormulacion.exigirEditable(new Proyecto())).doesNotThrowAnyException();
    }
}
