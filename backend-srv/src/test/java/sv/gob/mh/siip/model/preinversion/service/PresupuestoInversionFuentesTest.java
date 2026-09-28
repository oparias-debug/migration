package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.PresupuestoProyecto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.FuenteFinanciamientoDto;
import sv.gob.mh.siip.model.preinversion.dto.FuentesFinanciamientoRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.FuenteFinanciamiento;

class PresupuestoInversionFuentesTest {

    @Test
    void validarRechazaFuentesNulasOVaciasAntesQueLaFuenteDeRecursos() {
        FuentesFinanciamientoRequestDto nulas = new FuentesFinanciamientoRequestDto().fuentesFinanciamiento(null);
        FuentesFinanciamientoRequestDto vacias = new FuentesFinanciamientoRequestDto();

        assertCampoInvalido(nulas, "fuentesFinanciamiento");
        assertCampoInvalido(vacias, "fuentesFinanciamiento");
    }

    @Test
    void validarRechazaFuenteDeRecursosNulaOEnBlanco() {
        FuentesFinanciamientoRequestDto sinRecursos = new FuentesFinanciamientoRequestDto()
                .fuentesFinanciamiento(List.of(FuenteFinanciamientoDto.OTROS));
        FuentesFinanciamientoRequestDto recursosEnBlanco = new FuentesFinanciamientoRequestDto()
                .fuentesFinanciamiento(List.of(FuenteFinanciamientoDto.OTROS)).fuenteRecursos("   ");

        assertCampoInvalido(sinRecursos, "fuenteRecursos");
        assertCampoInvalido(recursosEnBlanco, "fuenteRecursos");
    }

    @Test
    void validarAceptaSolicitudCompleta() {
        FuentesFinanciamientoRequestDto req = new FuentesFinanciamientoRequestDto()
                .fuentesFinanciamiento(List.of(FuenteFinanciamientoDto.DONACIONES)).fuenteRecursos("nacional");

        assertThatCode(() -> PresupuestoInversionFuentes.validar(req)).doesNotThrowAnyException();
    }

    @Test
    void aplicarCopiaFuentesYRecortaLaFuenteDeRecursos() {
        PresupuestoProyecto p = PresupuestoProyecto.builder().id(2L).build();
        FuentesFinanciamientoRequestDto req = new FuentesFinanciamientoRequestDto()
                .fuentesFinanciamiento(List.of(FuenteFinanciamientoDto.DONACIONES, FuenteFinanciamientoDto.FONDO_GENERAL))
                .fuenteRecursos("  presupuesto nacional  ");

        PresupuestoInversionFuentes.aplicar(p, req);

        assertThat(p.getFuentesFinanciamiento())
                .containsExactly(FuenteFinanciamiento.DONACIONES, FuenteFinanciamiento.FONDO_GENERAL);
        assertThat(p.getFuenteRecursos()).isEqualTo("presupuesto nacional");
    }

    @Test
    void dtoConvierteLasFuentesDelPresupuesto() {
        PresupuestoProyecto p = PresupuestoProyecto.builder().id(2L).build();
        p.setFuentesFinanciamiento(List.of(FuenteFinanciamiento.PRESTAMOS_EXTERNOS));
        p.setFuenteRecursos("BID");

        FuentesFinanciamientoRequestDto dto = PresupuestoInversionFuentes.dto(p);

        assertThat(dto.getFuenteRecursos()).isEqualTo("BID");
        assertThat(dto.getFuentesFinanciamiento()).containsExactly(FuenteFinanciamientoDto.PRESTAMOS_EXTERNOS);
    }

    private static void assertCampoInvalido(FuentesFinanciamientoRequestDto req, String campo) {
        assertThatThrownBy(() -> PresupuestoInversionFuentes.validar(req))
                .isInstanceOfSatisfying(ValidacionNegocioException.class, e -> {
                    assertThat(e).hasMessage(PresupuestoInversionValidaciones.TITULO);
                    assertThat(e.getDetalles()).extracting(ErrorDetalleDto::getCampo, ErrorDetalleDto::getMensaje)
                            .containsExactly(tuple(campo, PresupuestoInversionValidaciones.CAMPO_OBLIGATORIO));
                });
    }
}
