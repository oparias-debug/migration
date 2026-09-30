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

import sv.gob.mh.siip.model.preinversion.domain.Elegibilidad;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitudOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.repository.ComentarioOpinionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ElegibilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.OpinionTecnicaRepository;

/** Pruebas unitarias de {@link FiltrosPosterioresViabilidad} (CU-PRE-24 RN03 y RN11; CU-PRE-26 RN14 y FA04). */
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
    void soloCuentaLaOtObservadaConComentariosAlProyectoPosteriorALaEmision() {
        assertThat(filtros.otReabrioViabilidadDespuesDe(ID_PROYECTO, EMISION)).isFalse();

        ultimaOt(ResultadoOpinionTecnica.OBSERVADO, EMISION.plusDays(1));
        when(comentarios.tieneComentariosProyecto(30L)).thenReturn(true);
        assertThat(filtros.otReabrioViabilidadDespuesDe(ID_PROYECTO, EMISION)).isTrue();
        assertThat(filtros.otReabrioViabilidadDespuesDe(ID_PROYECTO, null)).isTrue();

        ultimaOt(ResultadoOpinionTecnica.OBSERVADO, EMISION.minusDays(1));
        assertThat(filtros.otReabrioViabilidadDespuesDe(ID_PROYECTO, EMISION)).isFalse();

        ultimaOt(ResultadoOpinionTecnica.OBSERVADO, null);
        assertThat(filtros.otReabrioViabilidadDespuesDe(ID_PROYECTO, EMISION)).isFalse();

        ultimaOt(ResultadoOpinionTecnica.FAVORABLE, EMISION.plusDays(1));
        assertThat(filtros.otReabrioViabilidadDespuesDe(ID_PROYECTO, EMISION)).isFalse();
    }

    @Test
    void rn14LosComentariosSoloAElegibilidadNoReabrenLaViabilidad() {
        ultimaOt(ResultadoOpinionTecnica.OBSERVADO, EMISION.plusDays(1));
        when(comentarios.tieneComentariosElegibilidad(30L)).thenReturn(true);

        assertThat(filtros.otReabrioViabilidadDespuesDe(ID_PROYECTO, EMISION)).isFalse();
        assertThat(filtros.otReabrioElegibilidadDespuesDe(ID_PROYECTO, EMISION)).isTrue();
    }

    @Test
    void rn14LosComentariosSoloAlProyectoNoReabrenLaElegibilidad() {
        ultimaOt(ResultadoOpinionTecnica.OBSERVADO, EMISION.plusDays(1));
        when(comentarios.tieneComentariosProyecto(30L)).thenReturn(true);

        assertThat(filtros.otReabrioElegibilidadDespuesDe(ID_PROYECTO, EMISION)).isFalse();
        assertThat(filtros.otPideElegibilidad(ID_PROYECTO)).isFalse();
    }

    @Test
    void laViabilidadReemitidaVuelveAElegibilidadMientrasNoSeReemita() {
        ultimaOt(ResultadoOpinionTecnica.OBSERVADO, EMISION.plusDays(1));
        when(comentarios.tieneComentariosElegibilidad(30L)).thenReturn(true);
        when(elegibilidades.findFirstByProyectoIdOrderByFechaEvaluacionDescIdDesc(ID_PROYECTO))
                .thenReturn(Optional.of(Elegibilidad.builder().fechaEvaluacion(EMISION).build()));
        assertThat(filtros.otPideElegibilidad(ID_PROYECTO)).isTrue();

        when(elegibilidades.findFirstByProyectoIdOrderByFechaEvaluacionDescIdDesc(ID_PROYECTO))
                .thenReturn(Optional.of(Elegibilidad.builder().fechaEvaluacion(EMISION.plusDays(2)).build()));
        assertThat(filtros.otPideElegibilidad(ID_PROYECTO)).isFalse();
    }

    @Test
    void laActualizacionDeOtSolicitadaDespuesDeLaEmisionReabreLaViabilidad() {
        when(opiniones.existsByProyectoIdAndTipoSolicitudAndResultadoIsNullAndFechaArchivoIsNullAndFechaSolicitudAfter(
                ID_PROYECTO, TipoSolicitudOpinionTecnica.ACTUALIZACION_OT, EMISION)).thenReturn(true);
        assertThat(filtros.otReabrioViabilidadDespuesDe(ID_PROYECTO, EMISION)).isTrue();

        when(opiniones.existsByProyectoIdAndTipoSolicitudAndResultadoIsNullAndFechaArchivoIsNull(
                ID_PROYECTO, TipoSolicitudOpinionTecnica.ACTUALIZACION_OT)).thenReturn(true);
        assertThat(filtros.otReabrioViabilidadDespuesDe(ID_PROYECTO, null)).isTrue();
    }
    @Test
    void exigeRespuestaSoloALosComentariosDeUnaOtObservada() {
        assertThat(filtros.tieneComentariosProyectoSinResponder(ID_PROYECTO)).isFalse();

        ultimaOt(ResultadoOpinionTecnica.FAVORABLE, EMISION);
        assertThat(filtros.tieneComentariosProyectoSinResponder(ID_PROYECTO)).isFalse();
        verify(comentarios, never()).contarProyectoSinResponder(30L);

        ultimaOt(ResultadoOpinionTecnica.OBSERVADO, EMISION);
        when(comentarios.contarProyectoSinResponder(30L)).thenReturn(2L);
        assertThat(filtros.tieneComentariosProyectoSinResponder(ID_PROYECTO)).isTrue();

        when(comentarios.contarProyectoSinResponder(30L)).thenReturn(0L);
        assertThat(filtros.tieneComentariosProyectoSinResponder(ID_PROYECTO)).isFalse();
    }

    @Test
    void cadaSeccionExigeSoloSusPropiosComentariosRespondidos() {
        ultimaOt(ResultadoOpinionTecnica.OBSERVADO, EMISION);
        // Un comentario a la Elegibilidad sin responder no impide volver a solicitar la Viabilidad, ni uno
        // al proyecto impide reemitir la Elegibilidad.
        when(comentarios.contarProyectoSinResponder(30L)).thenReturn(0L);
        when(comentarios.contarElegibilidadSinResponder(30L)).thenReturn(1L);
        assertThat(filtros.tieneComentariosProyectoSinResponder(ID_PROYECTO)).isFalse();
        assertThat(filtros.tieneComentariosElegibilidadSinResponder(ID_PROYECTO)).isTrue();

        when(comentarios.contarProyectoSinResponder(30L)).thenReturn(3L);
        when(comentarios.contarElegibilidadSinResponder(30L)).thenReturn(0L);
        assertThat(filtros.tieneComentariosProyectoSinResponder(ID_PROYECTO)).isTrue();
        assertThat(filtros.tieneComentariosElegibilidadSinResponder(ID_PROYECTO)).isFalse();
    }

    @Test
    void unaOtArchivadaPorVencimientoNoExigeResponderSusComentarios() {
        when(opiniones.findFirstByProyectoIdOrderByFechaEmisionDesc(ID_PROYECTO))
                .thenReturn(Optional.of(OpinionTecnica.builder().id(30L).resultado(ResultadoOpinionTecnica.OBSERVADO)
                        .fechaEmision(EMISION).fechaArchivo(EMISION.plusDays(8)).build()));
        when(comentarios.contarProyectoSinResponder(30L)).thenReturn(2L);
        when(comentarios.contarElegibilidadSinResponder(30L)).thenReturn(2L);

        assertThat(filtros.tieneComentariosProyectoSinResponder(ID_PROYECTO)).isFalse();
        assertThat(filtros.tieneComentariosElegibilidadSinResponder(ID_PROYECTO)).isFalse();
    }
}
