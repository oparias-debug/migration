package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.repository.ComentarioOpinionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ElegibilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.OpinionTecnicaRepository;

/** Pruebas unitarias de {@link FiltrosPosterioresViabilidad} (CU-PRE-24, RN03 y RN11). */
class FiltrosPosterioresViabilidadTest {

    private static final Long ID_PROYECTO = 4L;
    private static final LocalDateTime EMISION = LocalDateTime.of(2026, 9, 1, 10, 0);

    private ElegibilidadRepository elegibilidades;
    private OpinionTecnicaRepository opiniones;
    private ComentarioOpinionTecnicaRepository comentarios;
    private FiltrosPosterioresViabilidad filtros;

    @BeforeEach
    void setUp() {
        elegibilidades = mock(ElegibilidadRepository.class);
        opiniones = mock(OpinionTecnicaRepository.class);
        comentarios = mock(ComentarioOpinionTecnicaRepository.class);
        filtros = new FiltrosPosterioresViabilidad(elegibilidades, opiniones, comentarios);
    }

    private void ultimaOt(ResultadoOpinionTecnica resultado, LocalDateTime fecha) {
        when(opiniones.findFirstByProyectoIdOrderByFechaEmisionDesc(ID_PROYECTO))
                .thenReturn(Optional.of(OpinionTecnica.builder().id(30L).resultado(resultado).fechaEmision(fecha).build()));
    }

    @Test
    void consultaSiElProyectoYaPasoPorElegibilidad() {
        when(elegibilidades.existsByProyectoId(ID_PROYECTO)).thenReturn(true);

        assertThat(filtros.yaPasoPorElegibilidad(ID_PROYECTO)).isTrue();
    }

    @Test
    void soloCuentaLaOtObservadaPosteriorALaEmision() {
        assertThat(filtros.otDevolvioDespuesDe(ID_PROYECTO, EMISION)).isFalse();

        ultimaOt(ResultadoOpinionTecnica.OBSERVADO, EMISION.plusDays(1));
        assertThat(filtros.otDevolvioDespuesDe(ID_PROYECTO, EMISION)).isTrue();
        assertThat(filtros.otDevolvioDespuesDe(ID_PROYECTO, null)).isTrue();

        ultimaOt(ResultadoOpinionTecnica.OBSERVADO, EMISION.minusDays(1));
        assertThat(filtros.otDevolvioDespuesDe(ID_PROYECTO, EMISION)).isFalse();

        ultimaOt(ResultadoOpinionTecnica.OBSERVADO, null);
        assertThat(filtros.otDevolvioDespuesDe(ID_PROYECTO, EMISION)).isFalse();

        ultimaOt(ResultadoOpinionTecnica.FAVORABLE, EMISION.plusDays(1));
        assertThat(filtros.otDevolvioDespuesDe(ID_PROYECTO, EMISION)).isFalse();
    }

    @Test
    void exigeRespuestaSoloALosComentariosDeUnaOtObservada() {
        assertThat(filtros.tieneComentariosOtSinResponder(ID_PROYECTO)).isFalse();

        ultimaOt(ResultadoOpinionTecnica.FAVORABLE, EMISION);
        assertThat(filtros.tieneComentariosOtSinResponder(ID_PROYECTO)).isFalse();
        verify(comentarios, never()).contarSinResponder(30L);

        ultimaOt(ResultadoOpinionTecnica.OBSERVADO, EMISION);
        when(comentarios.contarSinResponder(30L)).thenReturn(2L);
        assertThat(filtros.tieneComentariosOtSinResponder(ID_PROYECTO)).isTrue();

        when(comentarios.contarSinResponder(30L)).thenReturn(0L);
        assertThat(filtros.tieneComentariosOtSinResponder(ID_PROYECTO)).isFalse();
    }
}
