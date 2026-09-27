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

import sv.gob.mh.siip.model.preinversion.dto.AvanceEstudioDto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceFinancieroPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarAvanceEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.service.AvanceFinancieroPapService;

class AvanceFinancieroPapControllerTest {

    private static final String CUP = "08040";
    private static final CuatrimestreDto PERIODO = CuatrimestreDto.CUATRIMESTRE_I;

    private AvanceFinancieroPapService service;
    private AvanceFinancieroPapController controller;

    @BeforeEach
    void setUp() {
        service = mock(AvanceFinancieroPapService.class);
        controller = new AvanceFinancieroPapController(service);
    }

    @Test
    void obtenerAvanceFinancieroPAP_delegaFiltrosYDevuelve200() {
        AvanceFinancieroPAPResponseDto esperado = new AvanceFinancieroPAPResponseDto();
        when(service.listar(25L, 2027, PERIODO, 1, 10)).thenReturn(esperado);

        ResponseEntity<AvanceFinancieroPAPResponseDto> respuesta = controller.obtenerAvanceFinancieroPAP(25L, 2027,
                PERIODO, 1, 10);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void obtenerAvanceEstudio_delegaYDevuelve200() {
        AvanceEstudioDto esperado = new AvanceEstudioDto();
        when(service.obtenerAvanceEstudio(CUP, 2027, PERIODO)).thenReturn(esperado);

        ResponseEntity<AvanceEstudioDto> respuesta = controller.obtenerAvanceEstudio(CUP, 2027, PERIODO);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void guardarAvanceEstudio_delegaYDevuelve200() {
        GuardarAvanceEstudioRequestDto request = new GuardarAvanceEstudioRequestDto();
        AvanceEstudioDto esperado = new AvanceEstudioDto();
        when(service.guardarAvanceEstudio(CUP, 2027, PERIODO, request)).thenReturn(esperado);

        ResponseEntity<AvanceEstudioDto> respuesta = controller.guardarAvanceEstudio(CUP, 2027, PERIODO, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void generarReporteAvanceFinanciero_devuelveExcelCuandoNoEsPdf() {
        Resource recurso = new ByteArrayResource(new byte[] { 1, 2, 3 });
        when(service.generarReporte(25L, 2027, PERIODO, "EXCEL")).thenReturn(recurso);

        ResponseEntity<Resource> respuesta = controller.generarReporteAvanceFinanciero(25L, 2027, PERIODO, "EXCEL");

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getHeaders().getContentType()).isEqualTo(MediaType
                .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        assertThat(respuesta.getBody()).isSameAs(recurso);
    }

    @Test
    void generarReporteAvanceFinanciero_devuelvePdfCuandoSeSolicita() {
        Resource recurso = new ByteArrayResource(new byte[] { 1 });
        when(service.generarReporte(25L, 2027, PERIODO, "PDF")).thenReturn(recurso);

        ResponseEntity<Resource> respuesta = controller.generarReporteAvanceFinanciero(25L, 2027, PERIODO, "PDF");

        assertThat(respuesta.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PDF);
        assertThat(respuesta.getBody()).isSameAs(recurso);
    }
}
