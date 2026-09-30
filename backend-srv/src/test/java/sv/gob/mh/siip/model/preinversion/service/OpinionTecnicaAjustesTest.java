package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.ComentarioOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.JustificacionApartadoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.JustificacionesInstitucionRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.repository.OpinionTecnicaRepository;

/** Pruebas unitarias de {@link OpinionTecnicaAjustes} (CU-PRE-26, FA02, FA03.1, RN03, RN07 d). */
class OpinionTecnicaAjustesTest {

    private OpinionTecnicaRepository opiniones;
    private ComentariosDgicpOpinionTecnica comentarios;
    private DestinatariosOpinionTecnica destinatarios;
    private NotificacionService notificaciones;
    private OpinionTecnicaAjustes ajustes;

    private final Usuario viabilizador = Usuario.builder().id(4L).rol(RolUsuario.VIABILIZADOR).build();
    private Proyecto proyecto;
    private OpinionTecnica gestion;
    private OpinionTecnicaContexto contexto;
    private final Map<String, ComentarioOpinionTecnica> porApartado = new LinkedHashMap<>();

    @BeforeEach
    void setUp() {
        opiniones = mock(OpinionTecnicaRepository.class);
        comentarios = mock(ComentariosDgicpOpinionTecnica.class);
        destinatarios = mock(DestinatariosOpinionTecnica.class);
        notificaciones = mock(NotificacionService.class);
        ajustes = new OpinionTecnicaAjustes(opiniones, comentarios, destinatarios, notificaciones);
        proyecto = Proyecto.builder().id(1L).estado(EstadoProyecto.OBSERVADO).build();
        gestion = OpinionTecnica.builder().id(2L).resultado(ResultadoOpinionTecnica.OBSERVADO)
                .fechaEmision(LocalDateTime.now()).build();
        contexto = new OpinionTecnicaContexto(Usuario.builder().id(3L).rol(RolUsuario.TECNICO_URP).build(), proyecto,
                gestion, false);
        for (String apartado : List.of("1.2", ComentarioOpinionTecnica.DOCUMENTOS_ANEXOS,
                ComentarioOpinionTecnica.ELEGIBILIDAD)) {
            porApartado.put(apartado, ComentarioOpinionTecnica.builder().apartado(apartado).comentario("c").build());
        }
        when(comentarios.porApartado(gestion)).thenReturn(porApartado);
    }

    @Test
    void elTecnicoUrpJustificaElProyectoYElViabilizadorLaElegibilidad() {
        JustificacionesInstitucionRequestDto delProyecto = new JustificacionesInstitucionRequestDto()
                .addJustificacionesApartadosItem(new JustificacionApartadoRequestDto("1.2", "Ajustado"))
                .justificacionInstitucionDocumentosAnexos("Se adjuntó");
        JustificacionesInstitucionRequestDto deElegibilidad = new JustificacionesInstitucionRequestDto()
                .justificacionInstitucionElegibilidad("Se precisó");

        ajustes.guardarJustificaciones(contexto, delProyecto);
        ajustes.guardarJustificaciones(contexto(viabilizador), deElegibilidad);

        verify(comentarios).justificar(porApartado.get("1.2"), "Ajustado");
        verify(comentarios).justificar(porApartado.get(ComentarioOpinionTecnica.DOCUMENTOS_ANEXOS), "Se adjuntó");
        verify(comentarios).justificar(porApartado.get(ComentarioOpinionTecnica.ELEGIBILIDAD), "Se precisó");
    }

    @Test
    void cadaActorSoloJustificaSuSeccion() {
        JustificacionesInstitucionRequestDto deElegibilidad = new JustificacionesInstitucionRequestDto()
                .justificacionInstitucionElegibilidad("Se precisó");
        JustificacionesInstitucionRequestDto delProyecto = new JustificacionesInstitucionRequestDto()
                .justificacionInstitucionDocumentosAnexos("Se adjuntó");
        OpinionTecnicaContexto delViabilizador = contexto(viabilizador);

        assertThatThrownBy(() -> ajustes.guardarJustificaciones(contexto, deElegibilidad))
                .isInstanceOf(AccesoDenegadoException.class);
        assertThatThrownBy(() -> ajustes.guardarJustificaciones(delViabilizador, delProyecto))
                .isInstanceOf(AccesoDenegadoException.class);
        verify(comentarios, never()).justificar(any(), any());
    }

    @Test
    void elViabilizadorRespondeLaElegibilidadHastaReemitirla() {
        // Con la Viabilidad reemitida (comentarios también al proyecto) todavía puede responder.
        proyecto.setEstado(EstadoProyecto.VIABLE);
        ajustes.guardarJustificaciones(contexto(viabilizador), new JustificacionesInstitucionRequestDto()
                .justificacionInstitucionElegibilidad("Se precisó"));
        verify(comentarios).justificar(porApartado.get(ComentarioOpinionTecnica.ELEGIBILIDAD), "Se precisó");

        proyecto.setEstado(EstadoProyecto.ELEGIBLE);
        OpinionTecnicaContexto trasReemitir = contexto(viabilizador);
        JustificacionesInstitucionRequestDto tarde = new JustificacionesInstitucionRequestDto();
        assertThatThrownBy(() -> ajustes.guardarJustificaciones(trasReemitir, tarde))
                .isInstanceOf(ConflictoEstadoException.class);
    }

    private OpinionTecnicaContexto contexto(Usuario actor) {
        return new OpinionTecnicaContexto(actor, proyecto, gestion, false);
    }

    @Test
    void soloSeJustificanApartadosComentadosYSinSolicitudNoCambiaNada() {
        porApartado.remove(ComentarioOpinionTecnica.ELEGIBILIDAD);

        JustificacionesInstitucionRequestDto sinComentario = new JustificacionesInstitucionRequestDto()
                .addJustificacionesApartadosItem(new JustificacionApartadoRequestDto("2.1", "x"));
        assertThatThrownBy(() -> ajustes.guardarJustificaciones(contexto, sinComentario))
                .isInstanceOf(ValidacionNegocioException.class);

        ajustes.guardarJustificaciones(contexto, null);
        ajustes.guardarJustificaciones(contexto, new JustificacionesInstitucionRequestDto());
        verify(comentarios, never()).justificar(any(), any());
    }

    @Test
    void elEnvioDeAjustesRegistraLaFechaYAvisaALaDgicpSoloSiLaOtEstaObservada() {
        when(opiniones.findFirstByProyectoIdOrderByFechaEmisionDesc(1L)).thenReturn(Optional.of(gestion));

        ajustes.registrarEnvio(proyecto);

        assertThat(gestion.getFechaAjustes()).isNotNull();
        verify(notificaciones).notificarAjustesOpinionTecnica(any(), any());

        OpinionTecnica favorable = OpinionTecnica.builder().id(5L).resultado(ResultadoOpinionTecnica.FAVORABLE).build();
        when(opiniones.findFirstByProyectoIdOrderByFechaEmisionDesc(1L)).thenReturn(Optional.of(favorable));
        ajustes.registrarEnvio(proyecto);
        assertThat(favorable.getFechaAjustes()).isNull();
    }
}
