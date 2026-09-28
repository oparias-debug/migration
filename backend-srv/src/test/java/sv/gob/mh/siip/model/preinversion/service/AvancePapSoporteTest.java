package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.data.domain.Pageable;

import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;

class AvancePapSoporteTest {

    private static Cuatrimestre cuatrimestreVigenteEnMes(int mes) {
        LocalDateTime fecha = LocalDateTime.of(2028, mes, 15, 10, 0);
        try (MockedStatic<LocalDateTime> reloj = mockStatic(LocalDateTime.class, CALLS_REAL_METHODS)) {
            reloj.when(() -> LocalDateTime.now(any(ZoneId.class))).thenReturn(fecha);
            return AvancePapSoporte.cuatrimestreVigente();
        }
    }

    @Test
    void cuatrimestreVigente_eneroAAbril_esCuatrimestreI() {
        assertThat(cuatrimestreVigenteEnMes(4)).isEqualTo(Cuatrimestre.CUATRIMESTRE_I);
    }

    @Test
    void cuatrimestreVigente_mayoAAgosto_esCuatrimestreII() {
        assertThat(cuatrimestreVigenteEnMes(8)).isEqualTo(Cuatrimestre.CUATRIMESTRE_II);
    }

    @Test
    void cuatrimestreVigente_septiembreADiciembre_esCuatrimestreIII() {
        assertThat(cuatrimestreVigenteEnMes(9)).isEqualTo(Cuatrimestre.CUATRIMESTRE_III);
    }

    @Test
    void paginaSolicitada_valoresNulos_usaPrimeraPaginaYTamanioPorDefecto() {
        Pageable pagina = AvancePapSoporte.paginaSolicitada(null, null);

        assertThat(pagina.getPageNumber()).isZero();
        assertThat(pagina.getPageSize()).isEqualTo(20);
    }

    @Test
    void paginaSolicitada_valoresInvalidos_usaPrimeraPaginaYTamanioPorDefecto() {
        Pageable pagina = AvancePapSoporte.paginaSolicitada(-1, 0);

        assertThat(pagina.getPageNumber()).isZero();
        assertThat(pagina.getPageSize()).isEqualTo(20);
    }

    @Test
    void paginaSolicitada_valoresValidos_respetaLaSolicitud() {
        Pageable pagina = AvancePapSoporte.paginaSolicitada(3, 5);

        assertThat(pagina.getPageNumber()).isEqualTo(3);
        assertThat(pagina.getPageSize()).isEqualTo(5);
    }

    @Test
    void nullSafe_listaNula_devuelveListaVacia() {
        assertThat(AvancePapSoporte.nullSafe(null)).isEmpty();
        assertThat(AvancePapSoporte.nullSafe(List.of("a"))).containsExactly("a");
    }
}
