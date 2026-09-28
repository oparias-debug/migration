package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import sv.gob.mh.siip.model.preinversion.dto.CargarDocumentoViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.EmitirViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.EnviarComentariosViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarComentariosViabilidadRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarComentariosViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoDocumentoViabilidadDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoDocumentoViabilidad;
import sv.gob.mh.siip.model.preinversion.service.ViabilidadService;

/** Pruebas unitarias de {@link ViabilidadController}: delega 1:1 y responde con el código del contrato. */
class ViabilidadControllerTest {

    private ViabilidadService service;
    private ViabilidadController controller;

    @BeforeEach
    void setUp() {
        service = mock(ViabilidadService.class);
        controller = new ViabilidadController(service);
    }

    @Test
    void consultarFichaResponde200() {
        FichaViabilidadResponseDto ficha = new FichaViabilidadResponseDto();
        when(service.consultarFicha(1L)).thenReturn(ficha);

        ResponseEntity<FichaViabilidadResponseDto> respuesta = controller.consultarFichaViabilidad(1L);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(ficha);
    }

    @Test
    void cargarDocumentoResponde201ConvirtiendoElTipo() {
        MockMultipartFile archivo = new MockMultipartFile("archivo", "p.pdf", "application/pdf", new byte[] {1});
        CargarDocumentoViabilidadResponseDto cargado = new CargarDocumentoViabilidadResponseDto();
        when(service.cargarDocumento(1L, TipoDocumentoViabilidad.OTRO_DOCUMENTO, archivo)).thenReturn(cargado);

        ResponseEntity<CargarDocumentoViabilidadResponseDto> respuesta = controller.cargarDocumentoViabilidad(1L,
                TipoDocumentoViabilidadDto.OTRO_DOCUMENTO, archivo);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(respuesta.getBody()).isSameAs(cargado);
    }

    @Test
    void cargarDocumentoSinTipoDelegaLaValidacionAlServicio() {
        MockMultipartFile archivo = new MockMultipartFile("archivo", "p.pdf", "application/pdf", new byte[] {1});

        controller.cargarDocumentoViabilidad(1L, null, archivo);

        verify(service).cargarDocumento(1L, null, archivo);
    }

    @Test
    void guardarComentariosResponde200() {
        GuardarComentariosViabilidadRequestDto request = new GuardarComentariosViabilidadRequestDto();
        GuardarComentariosViabilidadResponseDto guardado = new GuardarComentariosViabilidadResponseDto();
        when(service.guardarComentarios(1L, request)).thenReturn(guardado);

        ResponseEntity<GuardarComentariosViabilidadResponseDto> respuesta = controller.guardarComentariosViabilidad(1L,
                request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(guardado);
    }

    @Test
    void solicitarViabilidadResponde204() {
        ResponseEntity<Void> respuesta = controller.solicitarViabilidad(1L);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service).solicitarViabilidad(1L);
    }

    @Test
    void enviarComentariosYEmitirResponden200() {
        EnviarComentariosViabilidadResponseDto devuelto = new EnviarComentariosViabilidadResponseDto(1L, "Observado");
        EmitirViabilidadResponseDto emitido = new EmitirViabilidadResponseDto(1L, "Proyecto viable", true);
        when(service.enviarComentarios(1L)).thenReturn(devuelto);
        when(service.emitirViabilidad(1L)).thenReturn(emitido);

        assertThat(controller.enviarComentariosViabilidad(1L).getBody()).isSameAs(devuelto);
        assertThat(controller.emitirViabilidad(1L).getBody()).isSameAs(emitido);
    }
}
