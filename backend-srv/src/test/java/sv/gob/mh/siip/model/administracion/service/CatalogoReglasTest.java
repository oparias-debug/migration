package sv.gob.mh.siip.model.administracion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.administracion.domain.CampoDefinicion;
import sv.gob.mh.siip.model.administracion.dto.ActiveStatusDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoVigencia;

/** Pruebas unitarias de {@link CatalogoReglas} (CU-ADM-01, Reglas 4, 5, 9b, 13 y 14). */
class CatalogoReglasTest {

    private static final LocalDate HOY = LocalDate.now(ZoneId.of("America/El_Salvador"));

    @Test
    void porPosicion_ordenaAscendenteConNulosAlFinal() {
        CampoDefinicion sinPosicion = CampoDefinicion.builder().nombre("sin").build();
        CampoDefinicion segundo = CampoDefinicion.builder().nombre("b").posicion(2).build();
        CampoDefinicion primero = CampoDefinicion.builder().nombre("a").posicion(1).build();
        List<CampoDefinicion> campos = new ArrayList<>(List.of(sinPosicion, segundo, primero));

        campos.sort(CatalogoReglas.POR_POSICION);

        assertThat(campos).extracting(CampoDefinicion::getNombre).containsExactly("a", "b", "sin");
    }

    @Test
    void resolverToDate_sinFecha_devuelveHoy() {
        assertThat(CatalogoReglas.resolverToDate(null)).isEqualTo(HOY);
    }

    @Test
    void resolverToDate_fechaPasada_laConserva() {
        LocalDate ayer = HOY.minusDays(1);

        assertThat(CatalogoReglas.resolverToDate(ayer)).isEqualTo(ayer);
    }

    @Test
    void resolverToDate_fechaFutura_lanzaValidacion() {
        LocalDate manana = HOY.plusDays(1);

        assertThatThrownBy(() -> CatalogoReglas.resolverToDate(manana))
                .isInstanceOfSatisfying(ValidacionNegocioException.class,
                        ex -> assertThat(ex.getCodigo()).isEqualTo("TO_DATE_FUTURA"));
    }

    @Test
    void estadoSolicitadoOCalculado_conActiveExplicito_loRespeta() {
        assertThat(CatalogoReglas.estadoSolicitadoOCalculado(ActiveStatusDto.INACTIVE, null))
                .isEqualTo(EstadoVigencia.INACTIVE);
    }

    @Test
    void estadoSolicitadoOCalculado_sinActive_loCalculaPorFechaHasta() {
        assertThat(CatalogoReglas.estadoSolicitadoOCalculado(null, HOY.minusDays(1)))
                .isEqualTo(EstadoVigencia.INACTIVE);
    }

    @Test
    void calcularEstadoVigencia_sinFechaHastaOFechaNoVencida_esActive() {
        assertThat(CatalogoReglas.calcularEstadoVigencia(null)).isEqualTo(EstadoVigencia.ACTIVE);
        assertThat(CatalogoReglas.calcularEstadoVigencia(HOY)).isEqualTo(EstadoVigencia.ACTIVE);
    }
}
