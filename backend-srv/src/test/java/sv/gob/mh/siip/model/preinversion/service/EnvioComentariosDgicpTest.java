package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.dto.ActorRetornoDto;
import sv.gob.mh.siip.model.preinversion.dto.EnvioComentariosDgicpResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.FiltroAprobacionDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;

/** Pruebas unitarias de {@link EnvioComentariosDgicp} (CU-PRE-26, RN14). */
class EnvioComentariosDgicpTest {

    private static final LocalDate ENVIO = LocalDate.of(2026, 9, 21);

    private static EnvioComentariosDgicp envio(boolean proyecto, boolean elegibilidad) {
        return new EnvioComentariosDgicp(proyecto, elegibilidad, ENVIO, ENVIO.plusDays(7));
    }

    @Test
    void conComentariosAlProyectoVuelveAlTecnicoUrpPorViabilidad() {
        EnvioComentariosDgicpResponseDto respuesta = envio(true, false).respuesta(9L, EstadoProyecto.OBSERVADO);

        assertThat(respuesta.getOpinionTecnicaId()).isEqualTo(9L);
        assertThat(respuesta.getEstadoProyecto()).isEqualTo(EstadoProyectoDto.OBSERVADO);
        assertThat(respuesta.getFechaFinPlazoObservaciones()).isEqualTo(ENVIO.plusDays(7));
        assertThat(respuesta.getRutaRetorno().getActorDestino()).isEqualTo(ActorRetornoDto.TECNICO_URP);
        assertThat(respuesta.getRutaRetorno().getFiltrosAprobacion())
                .containsExactly(FiltroAprobacionDto.VIABILIDAD, FiltroAprobacionDto.OPINION_TECNICA);
    }

    @Test
    void conComentariosAAmbosRecorreTodosLosFiltros() {
        assertThat(envio(true, true).rutaRetorno().getFiltrosAprobacion()).containsExactly(
                FiltroAprobacionDto.VIABILIDAD, FiltroAprobacionDto.ELEGIBILIDAD, FiltroAprobacionDto.OPINION_TECNICA);
    }

    @Test
    void conComentariosSoloAElegibilidadVuelveAlViabilizador() {
        assertThat(envio(false, true).rutaRetorno().getActorDestino()).isEqualTo(ActorRetornoDto.VIABILIZADOR);
        assertThat(envio(false, true).rutaRetorno().getFiltrosAprobacion())
                .containsExactly(FiltroAprobacionDto.ELEGIBILIDAD, FiltroAprobacionDto.OPINION_TECNICA);
    }
}
