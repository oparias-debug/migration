package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sv.gob.mh.siip.model.preinversion.dto.DevolucionSolicitudRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoDto;
import sv.gob.mh.siip.model.preinversion.service.ProyectoService;

class RevisionYEmisionCupControllerTest {

    private static final Long ID_PROYECTO = 13L;

    private ProyectoService proyectoService;
    private RevisionYEmisionCupController controller;

    @BeforeEach
    void setUp() {
        proyectoService = mock(ProyectoService.class);
        controller = new RevisionYEmisionCupController(proyectoService);
    }

    @Test
    void devolverSolicitudCup_delegaYDevuelve200() {
        DevolucionSolicitudRequestDto request = new DevolucionSolicitudRequestDto();
        ProyectoDto esperado = new ProyectoDto();
        when(proyectoService.devolverSolicitudCup(ID_PROYECTO, request)).thenReturn(esperado);

        ResponseEntity<ProyectoDto> respuesta = controller.devolverSolicitudCup(ID_PROYECTO, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void emitirCup_delegaYDevuelve200() {
        ProyectoDto esperado = new ProyectoDto();
        when(proyectoService.emitirCup(ID_PROYECTO)).thenReturn(esperado);

        ResponseEntity<ProyectoDto> respuesta = controller.emitirCup(ID_PROYECTO);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }
}
