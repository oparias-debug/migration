package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.IniciativaInversionDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectosCapturaResponseDto;
import sv.gob.mh.siip.model.preinversion.service.ProyectoCapturaFiltro;
import sv.gob.mh.siip.model.preinversion.service.ProyectoCapturaService;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * Pruebas unitarias para {@link ProyectoCapturaController}.
 * Cobertura 1:1 para los 6 parametros de entrada del Controller mapeados hacia los 10 del Service.
 *
 * @author Luis Medrano
 */
class ProyectoCapturaControllerTest {

    private ProyectoCapturaService proyectoCapturaService;
    private ActorContexto actorContexto;
    private ProyectoCapturaController controller;

    private Usuario usuarioAdmin;

    @BeforeEach
    void setUp() {
        proyectoCapturaService = mock(ProyectoCapturaService.class);
        actorContexto = mock(ActorContexto.class);
        controller = new ProyectoCapturaController(proyectoCapturaService);

        UnidadEjecutora ue = new UnidadEjecutora();
        ue.setId(10L);

        usuarioAdmin = Usuario.builder()
                .rol(RolUsuario.ADMINISTRADOR)
                .unidadEjecutora(ue)
                .build();

        when(actorContexto.exigir()).thenReturn(usuarioAdmin);
        when(actorContexto.actual()).thenReturn(Optional.of(usuarioAdmin));
    }

    // =========================================================================
    // PRUEBAS DE UN PARÁMETRO INDIVIDUAL POR TEST (1 A 1)
    // =========================================================================

    @Test
    @DisplayName("Filtro 1/6: Evalua mapeo exclusivo de 'busqueda'")
    void listarProyectosCaptura_evaluaParametro_busqueda() {
        ProyectosCapturaResponseDto expectedResponse = new ProyectosCapturaResponseDto();
        ProyectoCapturaFiltro filtro = new ProyectoCapturaFiltro("Hospital", null, null, null, null, null);
        when(proyectoCapturaService.listarProyectosCaptura(filtro, 0, 10))
                .thenReturn(expectedResponse);

        ResponseEntity<ProyectosCapturaResponseDto> respuesta = controller.listarProyectosCaptura(
                "Hospital", null, null, null, null, null, 0, 10);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(expectedResponse);
        verify(proyectoCapturaService).listarProyectosCaptura(filtro, 0, 10);
    }

    @Test
    @DisplayName("Filtro 2/6: Evalua mapeo exclusivo de 'cup'")
    void listarProyectosCaptura_evaluaParametro_cup() {
        ProyectosCapturaResponseDto expectedResponse = new ProyectosCapturaResponseDto();
        ProyectoCapturaFiltro filtro = new ProyectoCapturaFiltro(null, "99996", null, null, null, null);
        when(proyectoCapturaService.listarProyectosCaptura(filtro, 0, 10))
                .thenReturn(expectedResponse);

        ResponseEntity<ProyectosCapturaResponseDto> respuesta = controller.listarProyectosCaptura(
                null, "99996", null, null, null, null, 0, 10);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(expectedResponse);
        verify(proyectoCapturaService).listarProyectosCaptura(filtro, 0, 10);
    }

    @Test
    @DisplayName("Filtro 3/6: Evalua mapeo exclusivo de 'nombreProyecto'")
    void listarProyectosCaptura_evaluaParametro_nombreProyecto() {
        ProyectosCapturaResponseDto expectedResponse = new ProyectosCapturaResponseDto();
        ProyectoCapturaFiltro filtro = new ProyectoCapturaFiltro(null, null, "Construccion de Escuela", null, null, null);
        when(proyectoCapturaService.listarProyectosCaptura(filtro, 0, 10))
                .thenReturn(expectedResponse);

        ResponseEntity<ProyectosCapturaResponseDto> respuesta = controller.listarProyectosCaptura(
                null, null, "Construccion de Escuela", null, null, null, 0, 10);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(expectedResponse);
        verify(proyectoCapturaService).listarProyectosCaptura(filtro, 0, 10);
    }

    @Test
    @DisplayName("Filtro 4/6: Evalua mapeo exclusivo de 'iniciativaInversion'")
    void listarProyectosCaptura_evaluaParametro_iniciativaInversion() {
        ProyectosCapturaResponseDto expectedResponse = new ProyectosCapturaResponseDto();
        ProyectoCapturaFiltro filtro = new ProyectoCapturaFiltro(null, null, null, IniciativaInversionDto.PROYECTO, null, null);
        when(proyectoCapturaService.listarProyectosCaptura(filtro, 0, 10))
                .thenReturn(expectedResponse);

        ResponseEntity<ProyectosCapturaResponseDto> respuesta = controller.listarProyectosCaptura(
                null, null, null, IniciativaInversionDto.PROYECTO, null, null, 0, 10);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(expectedResponse);
        verify(proyectoCapturaService).listarProyectosCaptura(filtro, 0, 10);
    }

    @Test
    @DisplayName("Filtro 5/6: Evalua mapeo exclusivo de 'estado'")
    void listarProyectosCaptura_evaluaParametro_estado() {
        ProyectosCapturaResponseDto expectedResponse = new ProyectosCapturaResponseDto();
        ProyectoCapturaFiltro filtro = new ProyectoCapturaFiltro(null, null, null, null, EstadoProyectoDto.EN_REGISTRO, null);
        when(proyectoCapturaService.listarProyectosCaptura(filtro, 0, 10))
                .thenReturn(expectedResponse);

        ResponseEntity<ProyectosCapturaResponseDto> respuesta = controller.listarProyectosCaptura(
                null, null, null, null, EstadoProyectoDto.EN_REGISTRO, null, 0, 10);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(expectedResponse);
        verify(proyectoCapturaService).listarProyectosCaptura(filtro, 0, 10);
    }

    @Test
    @DisplayName("Filtro 6/6: Evalua mapeo exclusivo de 'idUnidadEjecutora'")
    void listarProyectosCaptura_evaluaParametro_idUnidadEjecutora() {
        ProyectosCapturaResponseDto expectedResponse = new ProyectosCapturaResponseDto();
        ProyectoCapturaFiltro filtro = new ProyectoCapturaFiltro(null, null, null, null, null, 25L);
        when(proyectoCapturaService.listarProyectosCaptura(filtro, 0, 10))
                .thenReturn(expectedResponse);

        ResponseEntity<ProyectosCapturaResponseDto> respuesta = controller.listarProyectosCaptura(
                null, null, null, null, null, 25L, 0, 10);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(expectedResponse);
        verify(proyectoCapturaService).listarProyectosCaptura(filtro, 0, 10);
    }

    // =========================================================================
    // CASO COMBINADO (TODOS LOS FILTROS LLENOS)
    // =========================================================================

    @Test
    @DisplayName("Verificacion integradora: Todos los parametros de filtro con valores explicitos")
    void listarProyectosCaptura_todosLosFiltrosLlenos_delegaCorrectamente() {
        ProyectosCapturaResponseDto expectedResponse = new ProyectosCapturaResponseDto();
        ProyectoCapturaFiltro filtro = new ProyectoCapturaFiltro("Busqueda Global", "12345", "Paso a Desnivel",
                IniciativaInversionDto.PROGRAMA, EstadoProyectoDto.CUP_ASIGNADO, 50L);
        when(proyectoCapturaService.listarProyectosCaptura(filtro, 1, 20))
                .thenReturn(expectedResponse);

        ResponseEntity<ProyectosCapturaResponseDto> respuesta = controller.listarProyectosCaptura(
                "Busqueda Global", "12345", "Paso a Desnivel", IniciativaInversionDto.PROGRAMA,
                EstadoProyectoDto.CUP_ASIGNADO, 50L, 1, 20);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(expectedResponse);

        verify(proyectoCapturaService).listarProyectosCaptura(filtro, 1, 20);
    }
}