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
import org.springframework.http.ResponseEntity;

import sv.gob.mh.siip.model.preinversion.dto.AgregarEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioProgramacionPAPDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarProgramacionEstudioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.HabilitarModificacionesFueraPlazoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.ProgramacionFinancieraPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionFinancieraPapAjusteService;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionFinancieraPapService;

class ProgramacionFinancieraPapControllerTest {

    private ProgramacionFinancieraPapService service;
    private ProgramacionFinancieraPapAjusteService ajusteService;
    private ProgramacionFinancieraPapController controller;

    @BeforeEach
    void setUp() {
        service = mock(ProgramacionFinancieraPapService.class);
        ajusteService = mock(ProgramacionFinancieraPapAjusteService.class);
        controller = new ProgramacionFinancieraPapController(service, ajusteService);
    }

    @Test
    void obtenerProgramacionFinancieraPAP_delegaFiltrosYDevuelveRespuesta() {
        ProgramacionFinancieraPAPResponseDto esperado = new ProgramacionFinancieraPAPResponseDto();
        when(service.listar(25L, 2027, "8040", 1, 10)).thenReturn(esperado);

        ResponseEntity<ProgramacionFinancieraPAPResponseDto> respuesta = controller
                .obtenerProgramacionFinancieraPAP(25L, 2027, "8040", 1, 10);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
        verify(service).listar(25L, 2027, "8040", 1, 10);
    }

    @Test
    void agregarEstudio_devuelve201ConElEstudioCreado() {
        AgregarEstudioRequestDto request = new AgregarEstudioRequestDto("08040", 25L, 2027);
        EstudioProgramacionPAPDto esperado = new EstudioProgramacionPAPDto();
        when(service.agregarEstudio(request)).thenReturn(esperado);

        ResponseEntity<EstudioProgramacionPAPDto> respuesta = controller.agregarEstudio(request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void obtenerProgramacionEstudio_delegaYDevuelveRespuesta() {
        EstudioProgramacionPAPDto esperado = new EstudioProgramacionPAPDto();
        when(service.obtenerProgramacionEstudio("08040", 2027)).thenReturn(esperado);

        ResponseEntity<EstudioProgramacionPAPDto> respuesta = controller.obtenerProgramacionEstudio("08040", 2027);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void guardarProgramacionEstudio_delegaYDevuelveRespuesta() {
        GuardarProgramacionEstudioRequestDto request = new GuardarProgramacionEstudioRequestDto();
        EstudioProgramacionPAPDto esperado = new EstudioProgramacionPAPDto();
        when(service.guardarProgramacionEstudio("08040", 2027, request)).thenReturn(esperado);

        ResponseEntity<EstudioProgramacionPAPDto> respuesta = controller.guardarProgramacionEstudio("08040", 2027,
                request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void desactivarEstudio_delegaYDevuelve204() {
        ResponseEntity<Void> respuesta = controller.desactivarEstudio("08040", 2027);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(ajusteService).desactivarEstudio("08040", 2027);
    }

    @Test
    void eliminarEtapaProgramacion_delegaYDevuelve204() {
        ResponseEntity<Void> respuesta = controller.eliminarEtapaProgramacion("08040", NombreEtapaDto.PERFIL, 2027);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(ajusteService).eliminarEtapaProgramacion("08040", NombreEtapaDto.PERFIL, 2027);
    }

    @Test
    void eliminarFuenteFinanciamiento_delegaYDevuelve204() {
        ResponseEntity<Void> respuesta = controller.eliminarFuenteFinanciamiento("08040", NombreEtapaDto.PERFIL, 5L,
                2027);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(ajusteService).eliminarFuenteFinanciamiento("08040", NombreEtapaDto.PERFIL, 5L, 2027);
    }

    @Test
    void habilitarModificacionesFueraPlazo_delegaYDevuelve200() {
        HabilitarModificacionesFueraPlazoRequestDto request = new HabilitarModificacionesFueraPlazoRequestDto(25L,
                2027);

        ResponseEntity<Void> respuesta = controller.habilitarModificacionesFueraPlazo(request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(ajusteService).habilitarModificacionesFueraPlazo(request);
    }

    @Test
    void generarReporteProgramacionPAP_devuelveExcelPorDefecto() {
        Resource recurso = new ByteArrayResource(new byte[] { 1, 2, 3 });
        when(service.generarReporte(25L, 2027, "EXCEL")).thenReturn(recurso);

        ResponseEntity<Resource> respuesta = controller.generarReporteProgramacionPAP(25L, 2027, "EXCEL");

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getHeaders().getContentType())
                .isEqualTo(org.springframework.http.MediaType
                        .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        assertThat(respuesta.getBody()).isSameAs(recurso);
    }

    @Test
    void generarReporteProgramacionPAP_devuelvePdfCuandoSeSolicita() {
        Resource recurso = new ByteArrayResource(new byte[] { 1, 2, 3 });
        when(service.generarReporte(25L, 2027, "PDF")).thenReturn(recurso);

        ResponseEntity<Resource> respuesta = controller.generarReporteProgramacionPAP(25L, 2027, "PDF");

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getHeaders().getContentType()).isEqualTo(org.springframework.http.MediaType.APPLICATION_PDF);
    }
}
