package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import sv.gob.mh.siip.model.preinversion.dto.AvanceMetasEstudioDto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceMetasFisicasPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.EnviarObservacionesAvanceDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FinalizarRevisionAvanceRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarAvanceMetasEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarObservacionesAvanceDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistrarRespuestaInstitucionAvanceRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RevisionAvancePAPDto;
import sv.gob.mh.siip.model.preinversion.service.AvanceMetasFisicasPapRevisionService;
import sv.gob.mh.siip.model.preinversion.service.AvanceMetasFisicasPapService;

class AvanceMetasFisicasPapControllerTest {

    private static final String CUP = "08040";
    private static final CuatrimestreDto PERIODO = CuatrimestreDto.CUATRIMESTRE_II;

    private AvanceMetasFisicasPapService service;
    private AvanceMetasFisicasPapRevisionService revisionService;
    private AvanceMetasFisicasPapController controller;

    @BeforeEach
    void setUp() {
        service = mock(AvanceMetasFisicasPapService.class);
        revisionService = mock(AvanceMetasFisicasPapRevisionService.class);
        controller = new AvanceMetasFisicasPapController(service, revisionService);
    }

    @Test
    void obtenerAvanceMetasFisicasPAP_delegaFiltrosYDevuelve200() {
        AvanceMetasFisicasPAPResponseDto esperado = new AvanceMetasFisicasPAPResponseDto();
        when(service.listar(25L, 2027, PERIODO, 1, 10)).thenReturn(esperado);

        ResponseEntity<AvanceMetasFisicasPAPResponseDto> respuesta = controller.obtenerAvanceMetasFisicasPAP(25L,
                2027, PERIODO, 1, 10);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void obtenerAvanceMetasEstudio_delegaYDevuelve200() {
        AvanceMetasEstudioDto esperado = new AvanceMetasEstudioDto();
        when(service.obtenerAvanceMetasEstudio(CUP, 2027, PERIODO)).thenReturn(esperado);

        ResponseEntity<AvanceMetasEstudioDto> respuesta = controller.obtenerAvanceMetasEstudio(CUP, 2027, PERIODO);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void guardarAvanceMetasEstudio_delegaYDevuelve200() {
        GuardarAvanceMetasEstudioRequestDto request = new GuardarAvanceMetasEstudioRequestDto();
        AvanceMetasEstudioDto esperado = new AvanceMetasEstudioDto();
        when(service.guardarAvanceMetasEstudio(CUP, 2027, PERIODO, request)).thenReturn(esperado);

        ResponseEntity<AvanceMetasEstudioDto> respuesta = controller.guardarAvanceMetasEstudio(CUP, 2027, PERIODO,
                request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void registrarObservacionesAvanceDgicp_delegaEnRevisionYDevuelve200() {
        RegistrarObservacionesAvanceDgicpRequestDto request = new RegistrarObservacionesAvanceDgicpRequestDto();
        RevisionAvancePAPDto esperado = new RevisionAvancePAPDto();
        when(revisionService.registrarObservacionesAvanceDgicp(request)).thenReturn(esperado);

        ResponseEntity<RevisionAvancePAPDto> respuesta = controller.registrarObservacionesAvanceDgicp(request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void enviarObservacionesAvanceDgicp_delegaEnRevisionYDevuelve200() {
        EnviarObservacionesAvanceDgicpRequestDto request = new EnviarObservacionesAvanceDgicpRequestDto();
        RevisionAvancePAPDto esperado = new RevisionAvancePAPDto();
        when(revisionService.enviarObservacionesAvanceDgicp(request)).thenReturn(esperado);

        ResponseEntity<RevisionAvancePAPDto> respuesta = controller.enviarObservacionesAvanceDgicp(request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void registrarRespuestaInstitucionAvance_delegaEnRevisionYDevuelve200() {
        RegistrarRespuestaInstitucionAvanceRequestDto request = new RegistrarRespuestaInstitucionAvanceRequestDto();
        RevisionAvancePAPDto esperado = new RevisionAvancePAPDto();
        when(revisionService.registrarRespuestaInstitucionAvance(request)).thenReturn(esperado);

        ResponseEntity<RevisionAvancePAPDto> respuesta = controller.registrarRespuestaInstitucionAvance(request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void enviarRespuestaInstitucionAvance_delegaEnRevisionYDevuelve200() {
        EnviarObservacionesAvanceDgicpRequestDto request = new EnviarObservacionesAvanceDgicpRequestDto();
        RevisionAvancePAPDto esperado = new RevisionAvancePAPDto();
        when(revisionService.enviarRespuestaInstitucionAvance(request)).thenReturn(esperado);

        ResponseEntity<RevisionAvancePAPDto> respuesta = controller.enviarRespuestaInstitucionAvance(request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void finalizarRevisionAvance_delegaEnRevisionYDevuelve200() {
        FinalizarRevisionAvanceRequestDto request = new FinalizarRevisionAvanceRequestDto();
        RevisionAvancePAPDto esperado = new RevisionAvancePAPDto();
        when(revisionService.finalizarRevisionAvance(request)).thenReturn(esperado);

        ResponseEntity<RevisionAvancePAPDto> respuesta = controller.finalizarRevisionAvance(request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void generarReporteAvanceMetas_devuelveExcelCuandoNoEsPdf() {
        Resource recurso = new ByteArrayResource(new byte[] { 1, 2, 3 });
        when(service.generarReporteAvanceMetas(25L, 2027, PERIODO, "EXCEL")).thenReturn(recurso);

        ResponseEntity<Resource> respuesta = controller.generarReporteAvanceMetas(25L, 2027, PERIODO, "EXCEL");

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getHeaders().getContentType()).isEqualTo(MediaType
                .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        assertThat(respuesta.getBody()).isSameAs(recurso);
    }

    @Test
    void generarReporteAvanceMetas_devuelvePdfCuandoSeSolicita() {
        Resource recurso = new ByteArrayResource(new byte[] { 1 });
        when(service.generarReporteAvanceMetas(25L, 2027, PERIODO, "PDF")).thenReturn(recurso);

        ResponseEntity<Resource> respuesta = controller.generarReporteAvanceMetas(25L, 2027, PERIODO, "PDF");

        assertThat(respuesta.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PDF);
        assertThat(respuesta.getBody()).isSameAs(recurso);
    }
}
