package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sv.gob.mh.siip.model.administracion.dto.UsuarioResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.AsignacionTecnicoPreRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.SolicitudActivaItemDto;
import sv.gob.mh.siip.model.preinversion.dto.SolicitudArchivadaItemDto;
import sv.gob.mh.siip.model.preinversion.dto.SolicitudesActivasResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.SolicitudesArchivadasResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoSolicitudDto;
import sv.gob.mh.siip.model.preinversion.service.BandejaPreinversionService;

class BandejaPreinversionControllerTest {

    private static final Long ID_SOLICITUD = 21L;

    private BandejaPreinversionService service;
    private BandejaPreinversionController controller;

    @BeforeEach
    void setUp() {
        service = mock(BandejaPreinversionService.class);
        controller = new BandejaPreinversionController(service);
    }

    @Test
    void listarSolicitudesActivas_delegaYDevuelve200() {
        SolicitudesActivasResponseDto esperado = new SolicitudesActivasResponseDto();
        when(service.activas(TipoSolicitudDto.CUP, 0, 20)).thenReturn(esperado);

        ResponseEntity<SolicitudesActivasResponseDto> respuesta = controller
                .listarSolicitudesActivas(TipoSolicitudDto.CUP, 0, 20);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void listarSolicitudesArchivadas_delegaYDevuelve200() {
        SolicitudesArchivadasResponseDto esperado = new SolicitudesArchivadasResponseDto();
        when(service.archivadas(TipoSolicitudDto.OPINION_TECNICA, 1, 5)).thenReturn(esperado);

        ResponseEntity<SolicitudesArchivadasResponseDto> respuesta = controller
                .listarSolicitudesArchivadas(TipoSolicitudDto.OPINION_TECNICA, 1, 5);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void asignarTecnicoPre_delegaYDevuelve200() {
        AsignacionTecnicoPreRequestDto request = new AsignacionTecnicoPreRequestDto();
        SolicitudActivaItemDto esperado = new SolicitudActivaItemDto();
        when(service.asignar(ID_SOLICITUD, request)).thenReturn(esperado);

        ResponseEntity<SolicitudActivaItemDto> respuesta = controller.asignarTecnicoPre(ID_SOLICITUD, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void archivarSolicitud_delegaYDevuelve200() {
        SolicitudArchivadaItemDto esperado = new SolicitudArchivadaItemDto();
        when(service.archivar(ID_SOLICITUD)).thenReturn(esperado);

        ResponseEntity<SolicitudArchivadaItemDto> respuesta = controller.archivarSolicitud(ID_SOLICITUD);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void desarchivarSolicitud_delegaYDevuelve200() {
        SolicitudActivaItemDto esperado = new SolicitudActivaItemDto();
        when(service.desarchivar(ID_SOLICITUD)).thenReturn(esperado);

        ResponseEntity<SolicitudActivaItemDto> respuesta = controller.desarchivarSolicitud(ID_SOLICITUD);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void listarTecnicosPre_delegaYDevuelve200() {
        List<UsuarioResumenDto> esperado = List.of(new UsuarioResumenDto());
        when(service.tecnicos()).thenReturn(esperado);

        ResponseEntity<List<UsuarioResumenDto>> respuesta = controller.listarTecnicosPre();

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }
}
