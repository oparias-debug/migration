package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.IniciativaInversionDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoCapturaItemDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectosCapturaResponseDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;

class ProyectoCapturaEnsambladorTest {

    private EtapaPreinversionRepository etapaPreinversionRepository;
    private ProyectoCapturaEnsamblador ensamblador;

    @BeforeEach
    void setUp() {
        etapaPreinversionRepository = mock(EtapaPreinversionRepository.class);
        ensamblador = new ProyectoCapturaEnsamblador(etapaPreinversionRepository);
    }

    @Test
    void construirRespuesta_mapeaCadaProyectoConSuEtapaMasAvanzadaYLaPaginacion() {
        Proyecto completo = Proyecto.builder().id(1L).cup("10000").nombre("Completo")
                .unidadEjecutora(UnidadEjecutora.builder().id(4L).nombre("UE 4").build())
                .iniciativaInversion(IniciativaInversion.PROYECTO).estado(EstadoProyecto.CUP_ASIGNADO).build();
        Proyecto incompleto = Proyecto.builder().id(2L).nombre("Incompleto").build();
        when(etapaPreinversionRepository.findByProyectoIdIn(List.of(1L, 2L))).thenReturn(List.of(
                etapaDe(completo, TipoEtapaPreinversion.FACTIBILIDAD),
                etapaDe(completo, TipoEtapaPreinversion.PERFIL)));

        ProyectosCapturaResponseDto respuesta = ensamblador.construirRespuesta(
                new PageImpl<>(List.of(completo, incompleto), PageRequest.of(0, 2), 3));

        assertThat(respuesta.getContenido()).hasSize(2);
        ProyectoCapturaItemDto primero = respuesta.getContenido().get(0);
        assertThat(primero.getCup()).isEqualTo("10000");
        assertThat(primero.getNombreProyecto()).isEqualTo("Completo");
        assertThat(primero.getUnidadEjecutora().getIdUnidadEjecutora()).isEqualTo(4L);
        assertThat(primero.getIniciativaInversion()).isEqualTo(IniciativaInversionDto.PROYECTO);
        assertThat(primero.getEstado()).isEqualTo(EstadoProyectoDto.CUP_ASIGNADO);
        assertThat(primero.getEtapaActual()).isEqualTo(NombreEtapaDto.FACTIBILIDAD);
        ProyectoCapturaItemDto segundo = respuesta.getContenido().get(1);
        assertThat(segundo.getUnidadEjecutora()).isNull();
        assertThat(segundo.getIniciativaInversion()).isNull();
        assertThat(segundo.getEstado()).isNull();
        assertThat(segundo.getEtapaActual()).isNull();
        assertThat(respuesta.getPaginacion().getTotalElementos()).isEqualTo(3L);
        assertThat(respuesta.getPaginacion().getTotalPaginas()).isEqualTo(2);
    }

    private static EtapaPreinversion etapaDe(Proyecto proyecto, TipoEtapaPreinversion tipoEtapa) {
        return EtapaPreinversion.builder()
                .proyecto(proyecto)
                .tipoEtapa(tipoEtapa)
                .fechaSeleccion(LocalDateTime.now())
                .build();
    }
}
