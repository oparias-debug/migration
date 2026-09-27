package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sv.gob.mh.siip.model.preinversion.dto.ActualizarEtapasRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.CriteriosCalificacionDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaEmergenciaDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaEmergenciaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaInformacionGeneralDto;
import sv.gob.mh.siip.model.preinversion.dto.ModificarRutaPreinversionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.RutaPreinversionDto;
import sv.gob.mh.siip.model.preinversion.dto.RutaPreinversionSugeridaDto;
import sv.gob.mh.siip.model.preinversion.dto.SeleccionCoEjecutorRequestDto;
import sv.gob.mh.siip.model.preinversion.service.SeleccionYRegistroDeEtapasService;

class SeleccionYRegistroDeEtapasControllerTest {

    private static final Long ID_PROYECTO = 11L;

    private SeleccionYRegistroDeEtapasService service;
    private SeleccionYRegistroDeEtapasController controller;

    @BeforeEach
    void setUp() {
        service = mock(SeleccionYRegistroDeEtapasService.class);
        controller = new SeleccionYRegistroDeEtapasController(service);
    }

    @Test
    void obtenerRutaPreinversion_delegaYDevuelve200() {
        RutaPreinversionDto esperado = new RutaPreinversionDto();
        when(service.obtenerRutaPreinversion(ID_PROYECTO)).thenReturn(esperado);

        ResponseEntity<RutaPreinversionDto> respuesta = controller.obtenerRutaPreinversion(ID_PROYECTO);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void generarRutaPreinversion_delegaYDevuelve200() {
        CriteriosCalificacionDto criterios = new CriteriosCalificacionDto();
        RutaPreinversionSugeridaDto esperado = new RutaPreinversionSugeridaDto();
        when(service.generarRutaPreinversion(ID_PROYECTO, criterios)).thenReturn(esperado);

        ResponseEntity<RutaPreinversionSugeridaDto> respuesta = controller.generarRutaPreinversion(ID_PROYECTO,
                criterios);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void aceptarRutaPreinversion_delegaYDevuelve200() {
        CriteriosCalificacionDto criterios = new CriteriosCalificacionDto();
        RutaPreinversionDto esperado = new RutaPreinversionDto();
        when(service.aceptarRutaPreinversion(ID_PROYECTO, criterios)).thenReturn(esperado);

        ResponseEntity<RutaPreinversionDto> respuesta = controller.aceptarRutaPreinversion(ID_PROYECTO, criterios);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void modificarRutaPreinversion_delegaYDevuelve200() {
        ModificarRutaPreinversionRequestDto request = new ModificarRutaPreinversionRequestDto();
        RutaPreinversionDto esperado = new RutaPreinversionDto();
        when(service.modificarRutaPreinversion(ID_PROYECTO, request)).thenReturn(esperado);

        ResponseEntity<RutaPreinversionDto> respuesta = controller.modificarRutaPreinversion(ID_PROYECTO, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void listarEtapas_delegaYDevuelve200() {
        List<EtapaDto> esperado = List.of(new EtapaDto());
        when(service.listarEtapas(ID_PROYECTO)).thenReturn(esperado);

        ResponseEntity<List<EtapaDto>> respuesta = controller.listarEtapas(ID_PROYECTO);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void actualizarEtapas_delegaYDevuelve200() {
        ActualizarEtapasRequestDto request = new ActualizarEtapasRequestDto();
        List<EtapaDto> esperado = List.of(new EtapaDto());
        when(service.actualizarEtapas(ID_PROYECTO, request)).thenReturn(esperado);

        ResponseEntity<List<EtapaDto>> respuesta = controller.actualizarEtapas(ID_PROYECTO, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void obtenerFichaInformacionGeneral_delegaYDevuelve200() {
        FichaInformacionGeneralDto esperado = new FichaInformacionGeneralDto();
        when(service.obtenerFichaInformacionGeneral(ID_PROYECTO)).thenReturn(esperado);

        ResponseEntity<FichaInformacionGeneralDto> respuesta = controller.obtenerFichaInformacionGeneral(ID_PROYECTO);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void seleccionarCoEjecutor_delegaYDevuelve200() {
        SeleccionCoEjecutorRequestDto request = new SeleccionCoEjecutorRequestDto();
        FichaInformacionGeneralDto esperado = new FichaInformacionGeneralDto();
        when(service.seleccionarCoEjecutor(ID_PROYECTO, request)).thenReturn(esperado);

        ResponseEntity<FichaInformacionGeneralDto> respuesta = controller.seleccionarCoEjecutor(ID_PROYECTO, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void obtenerFichaEmergencia_delegaYDevuelve200() {
        FichaEmergenciaDto esperado = new FichaEmergenciaDto();
        when(service.obtenerFichaEmergencia(ID_PROYECTO)).thenReturn(esperado);

        ResponseEntity<FichaEmergenciaDto> respuesta = controller.obtenerFichaEmergencia(ID_PROYECTO);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }

    @Test
    void registrarFichaEmergencia_delegaYDevuelve200() {
        FichaEmergenciaRequestDto request = new FichaEmergenciaRequestDto();
        FichaEmergenciaDto esperado = new FichaEmergenciaDto();
        when(service.registrarFichaEmergencia(ID_PROYECTO, request)).thenReturn(esperado);

        ResponseEntity<FichaEmergenciaDto> respuesta = controller.registrarFichaEmergencia(ID_PROYECTO, request);

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta.getBody()).isSameAs(esperado);
    }
}
