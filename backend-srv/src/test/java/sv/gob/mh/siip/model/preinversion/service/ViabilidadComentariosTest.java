package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.ComentarioCampoViabilidad;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.RevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.dto.CampoFichaViabilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.ComentarioCampoViabilidadDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarComentariosViabilidadRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarComentariosViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.enums.CampoFichaViabilidad;
import sv.gob.mh.siip.model.preinversion.enums.EstadoRevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.repository.RevisionViabilidadRepository;

/** Pruebas unitarias de {@link ViabilidadComentarios} (CU-PRE-24). */
class ViabilidadComentariosTest {

    private RevisionViabilidadRepository revisiones;
    private ViabilidadComentarios comentarios;

    private Usuario viabilizador;
    private RevisionViabilidad revision;

    @BeforeEach
    void setUp() {
        revisiones = mock(RevisionViabilidadRepository.class);
        comentarios = new ViabilidadComentarios(revisiones);

        viabilizador = Usuario.builder().id(20L).rol(RolUsuario.VIABILIZADOR).build();
        revision = RevisionViabilidad.builder().numero(1).estado(EstadoRevisionViabilidad.EN_CURSO)
                .comentarios(new ArrayList<>()).build();
    }

    private ViabilidadContexto contexto(RevisionViabilidad ultima) {
        return new ViabilidadContexto(viabilizador, Proyecto.builder().id(7L).build(), ultima, false, true);
    }

    private static GuardarComentariosViabilidadRequestDto request(String observaciones,
            ComentarioCampoViabilidadDto... recibidos) {
        GuardarComentariosViabilidadRequestDto request = new GuardarComentariosViabilidadRequestDto(
                new ArrayList<>(Arrays.asList(recibidos)));
        request.setObservacionesGeneralesJustificacion(observaciones);
        return request;
    }

    private static ComentarioCampoViabilidadDto comentario(CampoFichaViabilidadDto campo, String texto) {
        return new ComentarioCampoViabilidadDto(campo, texto);
    }

    @Test
    void reemplazaLosComentariosRecortaLosTextosYHabilitaEmitir() {
        revision.getComentarios().add(new ComentarioCampoViabilidad(CampoFichaViabilidad.DESCRIPCION, "Anterior"));

        GuardarComentariosViabilidadResponseDto respuesta = comentarios.guardar(contexto(revision),
                request("  Justificación  ", comentario(CampoFichaViabilidadDto.PRODUCTOS, "  Precisar  "),
                        comentario(CampoFichaViabilidadDto.OBJETIVO_GENERAL, "")));

        assertThat(revision.getComentarios()).singleElement()
                .satisfies(c -> assertThat(c.getCampo()).isEqualTo(CampoFichaViabilidad.PRODUCTOS));
        assertThat(revision.getObservacionesGenerales()).isEqualTo("Justificación");
        assertThat(respuesta.getComentariosViabilizador())
                .containsExactly(comentario(CampoFichaViabilidadDto.PRODUCTOS, "Precisar"));
        assertThat(respuesta.getObservacionesGeneralesJustificacion()).isEqualTo("Justificación");
        assertThat(respuesta.getAccionesDisponibles().getEmitirViabilidad()).isTrue();
        verify(revisiones).save(revision);
    }

    @Test
    void sinComentariosNiObservacionesDejaLaRevisionVacia() {
        GuardarComentariosViabilidadRequestDto vacio = request(null);
        vacio.setComentariosViabilizador(null);

        GuardarComentariosViabilidadResponseDto respuesta = comentarios.guardar(contexto(revision), vacio);

        assertThat(respuesta.getComentariosViabilizador()).isEmpty();
        assertThat(respuesta.getObservacionesGeneralesJustificacion()).isNull();
        assertThat(respuesta.getAccionesDisponibles().getEmitirViabilidad()).isFalse();
    }

    @Test
    void unComentarioNuloEsSolicitudInvalida() {
        GuardarComentariosViabilidadRequestDto conNulo = request(null, (ComentarioCampoViabilidadDto) null);
        ViabilidadContexto contexto = contexto(revision);

        assertThatThrownBy(() -> comentarios.guardar(contexto, conNulo))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("debe indicar el campo");
        verifyNoInteractions(revisiones);
    }

    @Test
    void campoRepetidoOTextosLargosSonSolicitudInvalida() {
        String largo = "x".repeat(ViabilidadComentarios.LONGITUD_MAXIMA_TEXTO + 1);
        String alLimite = "x".repeat(ViabilidadComentarios.LONGITUD_MAXIMA_TEXTO);
        GuardarComentariosViabilidadRequestDto repetido = request(null,
                comentario(CampoFichaViabilidadDto.DESCRIPCION, "a"),
                comentario(CampoFichaViabilidadDto.DESCRIPCION, "b"));
        GuardarComentariosViabilidadRequestDto comentarioLargo = request(null,
                comentario(CampoFichaViabilidadDto.DESCRIPCION, largo));
        GuardarComentariosViabilidadRequestDto observacionesLargas = request(largo);
        ViabilidadContexto contexto = contexto(revision);

        assertThatThrownBy(() -> comentarios.guardar(contexto, repetido))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("más de un comentario");
        assertThatThrownBy(() -> comentarios.guardar(contexto, comentarioLargo))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("supera");
        assertThatThrownBy(() -> comentarios.guardar(contexto, observacionesLargas))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("observaciones generales");
        assertThat(comentarios.guardar(contexto, request(alLimite)).getObservacionesGeneralesJustificacion())
                .hasSize(ViabilidadComentarios.LONGITUD_MAXIMA_TEXTO);
    }

    @Test
    void sinRevisionEnCursoNoHayBorradorQueGuardar() {
        ViabilidadContexto sinRevision = contexto(null);
        GuardarComentariosViabilidadRequestDto borrador = request("Obs");

        assertThatThrownBy(() -> comentarios.guardar(sinRevision, borrador))
                .isInstanceOf(ConflictoEstadoException.class)
                .extracting(e -> ((ConflictoEstadoException) e).getCodigo())
                .isEqualTo(ViabilidadContexto.SOLICITUD_VIABILIDAD_NO_VIGENTE);
    }
}
