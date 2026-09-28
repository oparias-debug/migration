package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sv.gob.mh.siip.model.preinversion.dto.BancoProyectosResponseDto;
import sv.gob.mh.siip.model.preinversion.service.BancoProyectosService;

class BancoProyectosControllerTest {

    private BancoProyectosService bancoProyectosService;
    private BancoProyectosController controller;

    @BeforeEach
    void setUp() {
        bancoProyectosService = mock(BancoProyectosService.class);
        controller = new BancoProyectosController(bancoProyectosService);
    }

    @Test
    void listarBancoProyectos_delegaFiltrosYDevuelveRespuesta() {
        BancoProyectosResponseDto expected = new BancoProyectosResponseDto();
        when(bancoProyectosService.listar(25L, "9010", 1, 10)).thenReturn(expected);

        ResponseEntity<BancoProyectosResponseDto> response = controller.listarBancoProyectos(25L, "9010", 1, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(expected);
        verify(bancoProyectosService).listar(25L, "9010", 1, 10);
    }
}
