package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sv.gob.mh.siip.model.administracion.dto.BuscarListaRegistros200ResponseDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogRecordCreateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogRecordDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogRecordUpdateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.InactivationRequestDto;
import sv.gob.mh.siip.model.administracion.service.RegistroService;

class RegistroCatalogoControllerTest {

    private static final String CODIGO = "CAT_DEPTOS";
    private static final String CLAVE = "01";
    private static final List<String> CAMPOS = List.of("nombre");

    private RegistroService registroService;
    private RegistroCatalogoController controller;

    @BeforeEach
    void setUp() {
        registroService = mock(RegistroService.class);
        controller = new RegistroCatalogoController(registroService);
    }

    @Test
    void crearRegistroCatalogo_devuelve201ConElRegistroCreado() {
        CatalogRecordCreateRequestDto request = new CatalogRecordCreateRequestDto();
        CatalogRecordDto esperado = new CatalogRecordDto();
        when(registroService.crear(CODIGO, request)).thenReturn(esperado);

        ResponseEntity<CatalogRecordDto> respuesta = controller.crearRegistroCatalogo(CODIGO, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void buscarListaRegistros_mapeaLaPaginaAlDtoDeRespuesta() {
        CatalogRecordDto registro = new CatalogRecordDto();
        PageRequest pageable = PageRequest.of(0, 10);
        when(registroService.buscarLista(CODIGO, CAMPOS, pageable))
                .thenReturn(new PageImpl<>(List.of(registro), pageable, 1L));

        ResponseEntity<BuscarListaRegistros200ResponseDto> respuesta = controller.buscarListaRegistros(CODIGO, CAMPOS,
                0, 10);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        BuscarListaRegistros200ResponseDto cuerpo = respuesta.getBody();
        assertThat(cuerpo).isNotNull();
        assertThat(cuerpo.getContent()).containsExactly(registro);
        assertThat(cuerpo.getTotalElements()).isEqualTo(1L);
        assertThat(cuerpo.getTotalPages()).isEqualTo(1);
        assertThat(cuerpo.getNumber()).isZero();
        assertThat(cuerpo.getSize()).isEqualTo(10);
    }

    @Test
    void buscarRegistroPorClave_delegaYDevuelve200() {
        CatalogRecordDto esperado = new CatalogRecordDto();
        when(registroService.buscarPorClave(CODIGO, CLAVE, CAMPOS)).thenReturn(esperado);

        ResponseEntity<CatalogRecordDto> respuesta = controller.buscarRegistroPorClave(CODIGO, CLAVE, CAMPOS);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void actualizarRegistro_delegaYDevuelve200() {
        CatalogRecordUpdateRequestDto request = new CatalogRecordUpdateRequestDto();
        CatalogRecordDto esperado = new CatalogRecordDto();
        when(registroService.actualizar(CODIGO, CLAVE, request)).thenReturn(esperado);

        ResponseEntity<CatalogRecordDto> respuesta = controller.actualizarRegistro(CODIGO, CLAVE, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void eliminarRegistroCatalogo_delegaYDevuelve204() {
        ResponseEntity<Void> respuesta = controller.eliminarRegistroCatalogo(CODIGO, CLAVE);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(registroService).eliminar(CODIGO, CLAVE);
    }

    @Test
    void inactivarRegistroCatalogo_delegaYDevuelve200() {
        InactivationRequestDto request = new InactivationRequestDto();
        CatalogRecordDto esperado = new CatalogRecordDto();
        when(registroService.inactivar(CODIGO, CLAVE, request)).thenReturn(esperado);

        ResponseEntity<CatalogRecordDto> respuesta = controller.inactivarRegistroCatalogo(CODIGO, CLAVE, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }
}
