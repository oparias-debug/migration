package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import sv.gob.mh.siip.model.preinversion.dto.EnviarProgramacionARevisionDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioProgramacionMetasDto;
import sv.gob.mh.siip.model.preinversion.dto.FinalizarRevisionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarProgramacionMetasEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ProgramacionMetasFisicasPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarObservacionesDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarRespuestaInstitucionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RevisionProgramacionPAPDto;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionMetasFisicasPapRevisionService;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionMetasFisicasPapService;

class ProgramacionMetasFisicasPapControllerTest {

    private static final String CUP = "08040";
    private static final MediaType EXCEL = MediaType
            .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private ProgramacionMetasFisicasPapService service;
    private ProgramacionMetasFisicasPapRevisionService revisionService;
    private ProgramacionMetasFisicasPapController controller;

    @BeforeEach
    void setUp() {
        service = mock(ProgramacionMetasFisicasPapService.class);
        revisionService = mock(ProgramacionMetasFisicasPapRevisionService.class);
        controller = new ProgramacionMetasFisicasPapController(service, revisionService);
    }

    @Test
    void obtenerProgramacionMetasFisicasPAP_delegaFiltrosYDevuelve200() {
        ProgramacionMetasFisicasPAPResponseDto esperado = new ProgramacionMetasFisicasPAPResponseDto();
        when(service.listar(25L, 2027, 1, 10)).thenReturn(esperado);

        ResponseEntity<ProgramacionMetasFisicasPAPResponseDto> respuesta = controller
                .obtenerProgramacionMetasFisicasPAP(25L, 2027, 1, 10);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void obtenerProgramacionMetasEstudio_delegaYDevuelve200() {
        EstudioProgramacionMetasDto esperado = new EstudioProgramacionMetasDto();
        when(service.obtenerProgramacionMetasEstudio(CUP, 2027)).thenReturn(esperado);

        ResponseEntity<EstudioProgramacionMetasDto> respuesta = controller.obtenerProgramacionMetasEstudio(CUP, 2027);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void guardarProgramacionMetasEstudio_delegaYDevuelve200() {
        GuardarProgramacionMetasEstudioRequestDto request = new GuardarProgramacionMetasEstudioRequestDto();
        EstudioProgramacionMetasDto esperado = new EstudioProgramacionMetasDto();
        when(service.guardarProgramacionMetasEstudio(CUP, 2027, request)).thenReturn(esperado);

        ResponseEntity<EstudioProgramacionMetasDto> respuesta = controller.guardarProgramacionMetasEstudio(CUP, 2027,
                request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void enviarProgramacionARevisionDgicp_delegaEnRevisionYDevuelve200() {
        EnviarProgramacionARevisionDgicpRequestDto request = new EnviarProgramacionARevisionDgicpRequestDto();
        RevisionProgramacionPAPDto esperado = new RevisionProgramacionPAPDto();
        when(revisionService.enviarProgramacionARevisionDgicp(request)).thenReturn(esperado);

        ResponseEntity<RevisionProgramacionPAPDto> respuesta = controller.enviarProgramacionARevisionDgicp(request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void registrarObservacionesDgicp_delegaEnRevisionYDevuelve200() {
        RegistrarObservacionesDgicpRequestDto request = new RegistrarObservacionesDgicpRequestDto();
        RevisionProgramacionPAPDto esperado = new RevisionProgramacionPAPDto();
        when(revisionService.registrarObservacionesDgicp(request)).thenReturn(esperado);

        ResponseEntity<RevisionProgramacionPAPDto> respuesta = controller.registrarObservacionesDgicp(request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void enviarObservacionesDgicp_delegaEnRevisionYDevuelve200() {
        EnviarProgramacionARevisionDgicpRequestDto request = new EnviarProgramacionARevisionDgicpRequestDto();
        RevisionProgramacionPAPDto esperado = new RevisionProgramacionPAPDto();
        when(revisionService.enviarObservacionesDgicp(request)).thenReturn(esperado);

        ResponseEntity<RevisionProgramacionPAPDto> respuesta = controller.enviarObservacionesDgicp(request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void registrarRespuestaInstitucion_delegaEnRevisionYDevuelve200() {
        RegistrarRespuestaInstitucionRequestDto request = new RegistrarRespuestaInstitucionRequestDto();
        RevisionProgramacionPAPDto esperado = new RevisionProgramacionPAPDto();
        when(revisionService.registrarRespuestaInstitucion(request)).thenReturn(esperado);

        ResponseEntity<RevisionProgramacionPAPDto> respuesta = controller.registrarRespuestaInstitucion(request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void enviarRespuestaInstitucion_delegaEnRevisionYDevuelve200() {
        EnviarProgramacionARevisionDgicpRequestDto request = new EnviarProgramacionARevisionDgicpRequestDto();
        RevisionProgramacionPAPDto esperado = new RevisionProgramacionPAPDto();
        when(revisionService.enviarRespuestaInstitucion(request)).thenReturn(esperado);

        ResponseEntity<RevisionProgramacionPAPDto> respuesta = controller.enviarRespuestaInstitucion(request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void finalizarRevision_delegaEnRevisionYDevuelve200() {
        FinalizarRevisionRequestDto request = new FinalizarRevisionRequestDto();
        RevisionProgramacionPAPDto esperado = new RevisionProgramacionPAPDto();
        when(revisionService.finalizarRevision(request)).thenReturn(esperado);

        ResponseEntity<RevisionProgramacionPAPDto> respuesta = controller.finalizarRevision(request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void generarReporteMetasFisicas_devuelveExcelCuandoNoEsPdf() {
        Resource recurso = new ByteArrayResource(new byte[] { 1, 2, 3 });
        when(service.generarReporteMetasFisicas(25L, 2027, "EXCEL")).thenReturn(recurso);

        ResponseEntity<Resource> respuesta = controller.generarReporteMetasFisicas(25L, 2027, "EXCEL");

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getHeaders().getContentType()).isEqualTo(EXCEL);
        assertThat(respuesta.getBody()).isSameAs(recurso);
    }

    @Test
    void generarReporteMetasFisicas_devuelvePdfSinImportarMayusculas() {
        Resource recurso = new ByteArrayResource(new byte[] { 1 });
        when(service.generarReporteMetasFisicas(25L, 2027, "pdf")).thenReturn(recurso);

        ResponseEntity<Resource> respuesta = controller.generarReporteMetasFisicas(25L, 2027, "pdf");

        assertThat(respuesta.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PDF);
        assertThat(respuesta.getBody()).isSameAs(recurso);
    }

    @Test
    void habilitarModificacionesMetasFueraPlazo_delegaYDevuelve200SinCuerpo() {
        EnviarProgramacionARevisionDgicpRequestDto request = new EnviarProgramacionARevisionDgicpRequestDto();

        ResponseEntity<Void> respuesta = controller.habilitarModificacionesMetasFueraPlazo(request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isNull();
        verify(service).habilitarModificacionesMetasFueraPlazo(request);
    }
}
