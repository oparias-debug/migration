package sv.gob.mh.siip.model.preinversion.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.common.enums.RolUsuario;

/** Pruebas de los enums de CU-PRE-26.5 "Priorización". */
class PriorizacionEnumsTest {

    @Test
    void categoriaPorNombre_ignoraTildesMayusculasYEspacios() {
        assertThat(CategoriaPriorizacion.porNombre("  Priorizado para  programación "))
                .contains(CategoriaPriorizacion.PRIORIZADO_PARA_PROGRAMACION);
        assertThat(CategoriaPriorizacion.porNombre("No priorizable en estado actual"))
                .contains(CategoriaPriorizacion.NO_PRIORIZABLE_EN_ESTADO_ACTUAL);
        assertThat(CategoriaPriorizacion.porNombre("Otra categoría")).isEmpty();
    }

    @Test
    void tramos_separanLosCriteriosYSusActores() {
        assertThat(TramoPriorizacion.PRE.incluye(4)).isTrue();
        assertThat(TramoPriorizacion.PRE.incluye(TramoPriorizacion.CRITERIO_SYMP)).isFalse();
        assertThat(TramoPriorizacion.SYMP.incluye(TramoPriorizacion.CRITERIO_SYMP)).isTrue();
        assertThat(TramoPriorizacion.SYMP.incluye(1)).isFalse();
        assertThat(TramoPriorizacion.SYMP.getTecnico()).isEqualTo(RolUsuario.TECNICO_SYMP);
        assertThat(TramoPriorizacion.PRE.getCoordinador()).isEqualTo(RolUsuario.COORDINADOR_PRE);
        assertThat(TramoPriorizacion.SYMP.getDescripcion()).isEqualTo("criterio 5");
    }

    @Test
    void valorCalificacion_puntosDeCeroACincoYNuloParaNoAplica() {
        assertThat(ValorCalificacion.NO_APLICA.puntos()).isNull();
        assertThat(ValorCalificacion.CERO.puntos()).isZero();
        assertThat(ValorCalificacion.CINCO.puntos()).isEqualTo(5);
    }
}
