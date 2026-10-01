package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import sv.gob.mh.siip.exception.ReglaNegocioException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.ComentarioOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.dto.ComentarioApartadoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ComentariosDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.ComentarioOpinionTecnicaRepository;

/** Pruebas unitarias de {@link ComentariosDgicpOpinionTecnica} (CU-PRE-26, FA02, FA03, RN 12, RN13). */
class ComentariosDgicpOpinionTecnicaTest {

    private ComentarioOpinionTecnicaRepository repositorio;
    private ComentariosDgicpOpinionTecnica comentarios;
    private OpinionTecnica gestion;

    @BeforeEach
    void setUp() {
        repositorio = mock(ComentarioOpinionTecnicaRepository.class);
        comentarios = new ComentariosDgicpOpinionTecnica(repositorio);
        gestion = OpinionTecnica.builder().id(5L).build();
        when(repositorio.findByOpinionTecnicaIdOrderByIdAsc(5L)).thenReturn(new ArrayList<>());
    }

    private static ComentariosDgicpRequestDto request(String... codigos) {
        ComentariosDgicpRequestDto request = new ComentariosDgicpRequestDto();
        for (String codigo : codigos) {
            request.addComentariosApartadosItem(new ComentarioApartadoRequestDto(codigo, " Comentario " + codigo + " "));
        }
        return request;
    }

    @Test
    void registraLosComentariosConTextoYDistingueProyectoDeElegibilidad() {
        ComentariosDgicpRequestDto request = request("1.2");
        request.setComentarioDgicpDocumentosAnexos("   ");
        request.setComentarioDgicpElegibilidad("Precisar el Plan Regional");

        ComentariosDgicpOpinionTecnica.Registrados registrados = comentarios.registrar(gestion, false, request);

        assertThat(registrados.proyecto()).isTrue();
        assertThat(registrados.elegibilidad()).isTrue();
        ArgumentCaptor<ComentarioOpinionTecnica> guardado = ArgumentCaptor.forClass(ComentarioOpinionTecnica.class);
        verify(repositorio, times(2)).save(guardado.capture());
        assertThat(guardado.getAllValues()).extracting(ComentarioOpinionTecnica::getApartado)
                .containsExactly("1.2", ComentarioOpinionTecnica.ELEGIBILIDAD);
        assertThat(guardado.getAllValues().get(0).getComentario()).isEqualTo("Comentario 1.2");
    }

    @Test
    void elFormularioSeGuardaCompletoYBorraLosComentariosQueYaNoEstan() {
        ComentarioOpinionTecnica anterior = ComentarioOpinionTecnica.builder().id(1L).apartado("1.1").comentario("x")
                .build();
        ComentarioOpinionTecnica vigente = ComentarioOpinionTecnica.builder().id(2L).apartado("1.2").comentario("y")
                .build();
        when(repositorio.findByOpinionTecnicaIdOrderByIdAsc(5L)).thenReturn(List.of(anterior, vigente));

        ComentariosDgicpOpinionTecnica.Registrados registrados = comentarios.registrar(gestion, false, request("1.2"));

        verify(repositorio).delete(anterior);
        verify(repositorio).save(vigente);
        assertThat(vigente.getComentario()).isEqualTo("Comentario 1.2");
        assertThat(registrados.elegibilidad()).isFalse();
    }

    @Test
    void sinSolicitudNiApartadosNoQuedaNadaQueEnviar() {
        ComentariosDgicpOpinionTecnica.Registrados vacio = comentarios.registrar(gestion, false, null);
        assertThat(vacio.proyecto()).isFalse();
        assertThatThrownBy(vacio::exigirAlguno)
                .isInstanceOf(ReglaNegocioException.class)
                .extracting(e -> ((ReglaNegocioException) e).getCodigo())
                .isEqualTo(ComentariosDgicpOpinionTecnica.COMENTARIOS_DGICP_REQUERIDOS);

        ComentariosDgicpOpinionTecnica.Registrados soloDocumentos = comentarios.registrar(gestion, false,
                new ComentariosDgicpRequestDto().comentarioDgicpDocumentosAnexos("Falta el estudio de suelos"));
        assertThat(soloDocumentos.proyecto()).isTrue();
        assertThatCode(soloDocumentos::exigirAlguno).doesNotThrowAnyException();
    }

    @Test
    void sinListaDeApartadosSoloRegistraLosComentariosGenerales() {
        ComentariosDgicpRequestDto request = new ComentariosDgicpRequestDto().comentariosApartados(null)
                .comentarioDgicpDocumentosAnexos(" Revisar anexos ");

        ComentariosDgicpOpinionTecnica.Registrados registrados = comentarios.registrar(gestion, false, request);

        assertThat(registrados.proyecto()).isTrue();
        ArgumentCaptor<ComentarioOpinionTecnica> guardado = ArgumentCaptor.forClass(ComentarioOpinionTecnica.class);
        verify(repositorio).save(guardado.capture());
        assertThat(guardado.getValue().getApartado()).isEqualTo(ComentarioOpinionTecnica.DOCUMENTOS_ANEXOS);
    }

    @Test
    void rechazaApartadosAjenosAlFormularioORepetidos() {
        ComentariosDgicpRequestDto ajeno = request("9.9");
        ComentariosDgicpRequestDto deAnexoA1 = request("1.2");
        ComentariosDgicpRequestDto repetido = request("1.2", "1.2");
        assertThatThrownBy(() -> comentarios.registrar(gestion, false, ajeno))
                .isInstanceOf(ValidacionNegocioException.class);
        // RN13: el formulario de emergencia no tiene los apartados del Anexo A.1.
        assertThatThrownBy(() -> comentarios.registrar(gestion, true, deAnexoA1))
                .isInstanceOf(ValidacionNegocioException.class);
        assertThatThrownBy(() -> comentarios.registrar(gestion, false, repetido))
                .isInstanceOf(ValidacionNegocioException.class);
        verify(repositorio, never()).save(any());
    }

    @Test
    void laSeccionElegibilidadSoloExisteEnLaPrimeraGestion() {
        gestion.setPrimeraGestion(false);

        ComentariosDgicpRequestDto conElegibilidad = new ComentariosDgicpRequestDto()
                .comentarioDgicpElegibilidad("Comentario");
        assertThatThrownBy(() -> comentarios.registrar(gestion, false, conElegibilidad))
                .isInstanceOf(ReglaNegocioException.class)
                .extracting(e -> ((ReglaNegocioException) e).getCodigo())
                .isEqualTo(ComentariosDgicpOpinionTecnica.SECCION_ELEGIBILIDAD_NO_DISPONIBLE);
    }

    @Test
    void rechazaTextosMasLargosQueLaColumna() {
        String largo = "x".repeat(ComentariosDgicpOpinionTecnica.LONGITUD_TEXTO + 1);

        ComentariosDgicpRequestDto demasiadoLargo = new ComentariosDgicpRequestDto()
                .comentarioDgicpDocumentosAnexos(largo);
        assertThatThrownBy(() -> comentarios.registrar(gestion, false, demasiadoLargo))
                .isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    void laJustificacionEnBlancoSeBorra() {
        ComentarioOpinionTecnica comentario = ComentarioOpinionTecnica.builder().apartado("1.2").comentario("c")
                .justificacionInstitucion("antes").build();

        comentarios.justificar(comentario, " Respuesta ");
        assertThat(comentario.getJustificacionInstitucion()).isEqualTo("Respuesta");

        comentarios.justificar(comentario, "  ");
        assertThat(comentario.getJustificacionInstitucion()).isNull();
        verify(repositorio, times(2)).save(comentario);
    }
}
