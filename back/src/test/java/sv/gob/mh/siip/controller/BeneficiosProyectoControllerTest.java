package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sv.gob.mh.siip.model.preinversion.beneficios.dto.BeneficioDto;
import sv.gob.mh.siip.model.preinversion.beneficios.dto.BeneficioRequestDto;
import sv.gob.mh.siip.model.preinversion.beneficios.dto.BeneficiosDelProyectoDto;
import sv.gob.mh.siip.model.preinversion.beneficios.dto.GuardarBeneficiosProyectoRequestDto;
import sv.gob.mh.siip.model.preinversion.service.BeneficiosProyectoApiService;

class BeneficiosProyectoControllerTest {

    private BeneficiosProyectoApiService service;
    private BeneficiosProyectoController controller;

    @BeforeEach
    void preparar() {
        service = mock(BeneficiosProyectoApiService.class);
        controller = new BeneficiosProyectoController(service);
    }

    @Test
    void obtenerBeneficiosDelegaAlServicio() {
        BeneficiosDelProyectoDto esperado = new BeneficiosDelProyectoDto();
        esperado.setIdProyecto(7L);
        when(service.obtener(7L)).thenReturn(esperado);

        var respuesta = controller.obtenerBeneficiosProyecto(7L);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody().getIdProyecto()).isEqualTo(7L);
        verify(service).obtener(7L);
    }

    @Test
    void registrarBeneficioDevuelveCreado() {
        BeneficioRequestDto request = new BeneficioRequestDto();
        BeneficioDto esperado = new BeneficioDto();
        esperado.setIdBeneficio(12L);
        when(service.registrar(7L, request)).thenReturn(esperado);

        ResponseEntity<BeneficioDto> respuesta = controller.registrarBeneficio(7L, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(respuesta.getBody().getIdBeneficio()).isEqualTo(12L);
        verify(service).registrar(7L, request);
    }

    @Test
    void eliminarBeneficioDevuelveSinContenido() {
        var respuesta = controller.eliminarBeneficio(7L, 12L);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service).eliminar(7L, 12L);
    }

    @Test
    void guardarBeneficiosDelegaAlServicio() {
        GuardarBeneficiosProyectoRequestDto request = new GuardarBeneficiosProyectoRequestDto();
        BeneficiosDelProyectoDto esperado = new BeneficiosDelProyectoDto();
        esperado.setIdProyecto(7L);
        when(service.guardar(7L, request)).thenReturn(esperado);

        ResponseEntity<BeneficiosDelProyectoDto> respuesta = controller.guardarBeneficiosProyecto(7L, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody().getIdProyecto()).isEqualTo(7L);
        verify(service).guardar(7L, request);
    }
}
