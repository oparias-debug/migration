package sv.gob.mh.siip.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class ReferencedWarningTest {

    @Test
    void toMessage_sinParametros_devuelveSoloLaClave() {
        ReferencedWarning warning = new ReferencedWarning();
        warning.setKey("proyecto.referenciado");

        assertThat(warning.toMessage()).isEqualTo("proyecto.referenciado");
    }

    @Test
    void toMessage_conParametros_losConcatenaSeparadosPorComa() {
        ReferencedWarning warning = new ReferencedWarning();
        warning.setKey("proyecto.referenciado");
        warning.addParam("solicitud");
        warning.addParam(42L);

        assertThat(warning.toMessage()).isEqualTo("proyecto.referenciado,solicitud,42");
        assertThat(warning.getParams()).containsExactly("solicitud", 42L);
    }

    @Test
    void valoresIniciales_claveNulaYSinParametros() {
        ReferencedWarning warning = new ReferencedWarning();

        assertThat(warning.getKey()).isNull();
        assertThat(warning.getParams()).isEmpty();
        assertThat(warning.toMessage()).isNull();
    }

    @Test
    void setParams_reemplazaLaListaDeParametros() {
        ReferencedWarning warning = new ReferencedWarning();
        warning.setKey("clave");
        warning.setParams(new ArrayList<>(List.of("a", "b")));

        assertThat(warning.toMessage()).isEqualTo("clave,a,b");
    }
}
