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

import sv.gob.mh.siip.model.administracion.dto.CatalogCreateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogDescriptorsUpdateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogExistenceResponseDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogFieldsUpdateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogSummaryDto;
import sv.gob.mh.siip.model.administracion.dto.InactivationRequestDto;
import sv.gob.mh.siip.model.administracion.dto.ListarCatalogos200ResponseDto;
import sv.gob.mh.siip.model.administracion.service.CatalogoService;

class CatalogosAdministracionControllerTest {

    private static final String CODIGO = "CAT_DEPTOS";

    private CatalogoService catalogoService;
    private CatalogosAdministracionController controller;

    @BeforeEach
    void setUp() {
        catalogoService = mock(CatalogoService.class);
        controller = new CatalogosAdministracionController(catalogoService);
    }

    @Test
    void crearCatalogo_devuelve201ConElCatalogoCreado() {
        CatalogCreateRequestDto request = new CatalogCreateRequestDto();
        CatalogDto esperado = new CatalogDto();
        when(catalogoService.crear(request)).thenReturn(esperado);

        ResponseEntity<CatalogDto> respuesta = controller.crearCatalogo(request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void listarCatalogos_mapeaLaPaginaAlDtoDeRespuesta() {
        CatalogDto primero = new CatalogDto();
        CatalogDto segundo = new CatalogDto();
        PageRequest pageable = PageRequest.of(1, 2);
        when(catalogoService.listar(pageable)).thenReturn(new PageImpl<>(List.of(primero, segundo), pageable, 5L));

        ResponseEntity<ListarCatalogos200ResponseDto> respuesta = controller.listarCatalogos(1, 2);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        ListarCatalogos200ResponseDto cuerpo = respuesta.getBody();
        assertThat(cuerpo).isNotNull();
        assertThat(cuerpo.getContent()).containsExactly(primero, segundo);
        assertThat(cuerpo.getTotalElements()).isEqualTo(5L);
        assertThat(cuerpo.getTotalPages()).isEqualTo(3);
        assertThat(cuerpo.getNumber()).isEqualTo(1);
        assertThat(cuerpo.getSize()).isEqualTo(2);
    }

    @Test
    void verificarExistenciaCatalogo_delegaYDevuelve200() {
        CatalogExistenceResponseDto esperado = new CatalogExistenceResponseDto();
        when(catalogoService.verificarExistencia("Departamentos")).thenReturn(esperado);

        ResponseEntity<CatalogExistenceResponseDto> respuesta = controller.verificarExistenciaCatalogo("Departamentos");

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void consultarCatalogo_delegaYDevuelve200() {
        CatalogDto esperado = new CatalogDto();
        when(catalogoService.consultar(CODIGO)).thenReturn(esperado);

        ResponseEntity<CatalogDto> respuesta = controller.consultarCatalogo(CODIGO);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void actualizarDescriptoresCatalogo_delegaYDevuelve200() {
        CatalogDescriptorsUpdateRequestDto request = new CatalogDescriptorsUpdateRequestDto();
        CatalogDto esperado = new CatalogDto();
        when(catalogoService.actualizarDescriptores(CODIGO, request)).thenReturn(esperado);

        ResponseEntity<CatalogDto> respuesta = controller.actualizarDescriptoresCatalogo(CODIGO, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void eliminarCatalogo_delegaYDevuelve204() {
        ResponseEntity<Void> respuesta = controller.eliminarCatalogo(CODIGO);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(catalogoService).eliminar(CODIGO);
    }

    @Test
    void actualizarCamposCatalogo_delegaYDevuelve200() {
        CatalogFieldsUpdateRequestDto request = new CatalogFieldsUpdateRequestDto();
        CatalogDto esperado = new CatalogDto();
        when(catalogoService.actualizarCampos(CODIGO, request)).thenReturn(esperado);

        ResponseEntity<CatalogDto> respuesta = controller.actualizarCamposCatalogo(CODIGO, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void inactivarCatalogo_delegaYDevuelve200() {
        InactivationRequestDto request = new InactivationRequestDto();
        CatalogDto esperado = new CatalogDto();
        when(catalogoService.inactivar(CODIGO, request)).thenReturn(esperado);

        ResponseEntity<CatalogDto> respuesta = controller.inactivarCatalogo(CODIGO, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void consultarCatalogosHijos_delegaYDevuelve200() {
        List<CatalogSummaryDto> esperado = List.of(new CatalogSummaryDto());
        when(catalogoService.consultarHijos(CODIGO)).thenReturn(esperado);

        ResponseEntity<List<CatalogSummaryDto>> respuesta = controller.consultarCatalogosHijos(CODIGO);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }
}
