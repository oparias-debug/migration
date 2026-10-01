package sv.gob.mh.siip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import sv.gob.mh.siip.model.preinversion.programacion.dto.ConfigurarPeriodosProgramacionPreinversionRequestDto;
import sv.gob.mh.siip.model.preinversion.programacion.dto.ProgramacionFinancieraPreinversionDto;
import sv.gob.mh.siip.model.preinversion.programacion.dto.ProgramacionFinancieraPreinversionRequestDto;
import sv.gob.mh.siip.model.preinversion.service.ProgramacionFinancieraPreinversionService;

class ProgramacionFinancieraPreinversionControllerTest {
    private ProgramacionFinancieraPreinversionService service;
    private ProgramacionFinancieraPreinversionController controller;

    @BeforeEach
    void preparar() {
        service = mock(ProgramacionFinancieraPreinversionService.class);
        controller = new ProgramacionFinancieraPreinversionController(service);
    }

    @Test
    void obtenerDelegaEnElServicio() {
        when(service.obtener(7L)).thenReturn(new ProgramacionFinancieraPreinversionDto().idProyecto(7L));
        var respuesta = controller.obtenerProgramacionFinancieraPreinversion(7L);
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(service).obtener(7L);
    }

    @Test
    void configurarPeriodosDelegaEnElServicio() {
        var request = new ConfigurarPeriodosProgramacionPreinversionRequestDto().periodosAProgramar(3);
        when(service.configurarPeriodos(7L, request)).thenReturn(new ProgramacionFinancieraPreinversionDto());
        assertThat(controller.configurarPeriodosProgramacionPreinversion(7L, request).getStatusCode())
                .isEqualTo(HttpStatus.OK);
        verify(service).configurarPeriodos(7L, request);
    }

    @Test
    void guardarDelegaEnElServicio() {
        var request = new ProgramacionFinancieraPreinversionRequestDto();
        when(service.guardar(7L, request)).thenReturn(new ProgramacionFinancieraPreinversionDto());
        assertThat(controller.guardarProgramacionFinancieraPreinversion(7L, request).getStatusCode())
                .isEqualTo(HttpStatus.OK);
        verify(service).guardar(7L, request);
    }
}
