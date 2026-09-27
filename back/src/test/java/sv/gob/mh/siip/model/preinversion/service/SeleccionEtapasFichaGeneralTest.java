package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjePlanGobierno;
import sv.gob.mh.siip.model.preinversion.domain.PlanSectorialRegional;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.EjePlanGobiernoResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaInformacionGeneralDto;
import sv.gob.mh.siip.model.preinversion.dto.IniciativaInversionDto;
import sv.gob.mh.siip.model.preinversion.dto.PlanSectorialRegionalResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.SeleccionCoEjecutorRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.UnidadEjecutoraResumenDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.mapper.ProyectoMapper;

class SeleccionEtapasFichaGeneralTest {

    private static final Long ID_PROYECTO = 30L;

    private final SeleccionEtapasProyectos proyectos = mock(SeleccionEtapasProyectos.class);
    private final UnidadEjecutoraRepository unidadRepository = mock(UnidadEjecutoraRepository.class);
    private final ProyectoMapper proyectoMapper = mock(ProyectoMapper.class);
    private final SeleccionEtapasFichaGeneral fichaGeneral =
            new SeleccionEtapasFichaGeneral(proyectos, unidadRepository, proyectoMapper);

    private final UnidadEjecutora unidad = UnidadEjecutora.builder().id(1L).nombre("Unidad").build();
    private final Proyecto proyecto = Proyecto.builder().id(ID_PROYECTO).nombre("Puente")
            .iniciativaInversion(IniciativaInversion.PROYECTO).unidadEjecutora(unidad)
            .montoEstimadoInversion(1500d).estado(EstadoProyecto.EN_REGISTRO).build();

    @BeforeEach
    void setUp() {
        when(proyectos.buscar(ID_PROYECTO)).thenReturn(proyecto);
    }

    @Test
    void obtener_proyectoSinDatosOpcionales_dejaVaciosLosCamposOpcionales() {
        UnidadEjecutoraResumenDto resumenUnidad = new UnidadEjecutoraResumenDto().nombre("Unidad");
        when(proyectoMapper.toResumen(unidad)).thenReturn(resumenUnidad);

        FichaInformacionGeneralDto dto = fichaGeneral.obtener(ID_PROYECTO);

        assertThat(dto.getIdProyecto()).isEqualTo(ID_PROYECTO);
        assertThat(dto.getNombreProyecto()).isEqualTo("Puente");
        assertThat(dto.getIniciativaInversion()).isEqualTo(IniciativaInversionDto.PROYECTO);
        assertThat(dto.getUnidadEjecutora()).isSameAs(resumenUnidad);
        assertThat(dto.getEsProyectoGrdGrcAcc()).isFalse();
        assertThat(dto.getEsProyectoEmergencia()).isFalse();
        assertThat(dto.getCoEjecutor()).isNull();
        assertThat(dto.getEjePlanGobierno()).isNull();
        assertThat(dto.getPlanSectorialRegional()).isNull();
        assertThat(dto.getMontoAjustadoEjecucion()).isNull();
    }

    @Test
    void obtener_proyectoEnEjecucionConDatosOpcionales_losIncluye() {
        EjePlanGobierno eje = new EjePlanGobierno();
        PlanSectorialRegional plan = new PlanSectorialRegional();
        EjePlanGobiernoResumenDto resumenEje = new EjePlanGobiernoResumenDto();
        PlanSectorialRegionalResumenDto resumenPlan = new PlanSectorialRegionalResumenDto();
        when(proyectoMapper.toResumen(eje)).thenReturn(resumenEje);
        when(proyectoMapper.toResumen(plan)).thenReturn(resumenPlan);
        proyecto.setEjePlanGobierno(eje);
        proyecto.setPlanSectorialRegional(plan);
        proyecto.setEstado(EstadoProyecto.EN_EJECUCION);
        proyecto.setEsProyectoEmergencia(true);
        proyecto.setMedidasAcc(List.of("ACC-1"));

        FichaInformacionGeneralDto dto = fichaGeneral.obtener(ID_PROYECTO);

        assertThat(dto.getEjePlanGobierno()).isSameAs(resumenEje);
        assertThat(dto.getPlanSectorialRegional()).isSameAs(resumenPlan);
        assertThat(dto.getMontoAjustadoEjecucion()).isEqualTo(1500d);
        assertThat(dto.getEsProyectoEmergencia()).isTrue();
        assertThat(dto.getEsProyectoGrdGrcAcc()).isTrue();
    }

    @Test
    void obtener_proyectoConMedidasGrdOGrc_esGrdGrcAcc() {
        proyecto.setMedidasGrd(List.of("GRD-1"));
        assertThat(fichaGeneral.obtener(ID_PROYECTO).getEsProyectoGrdGrcAcc()).isTrue();

        proyecto.setMedidasGrd(List.of());
        proyecto.setMedidasGrc(List.of("GRC-1"));
        assertThat(fichaGeneral.obtener(ID_PROYECTO).getEsProyectoGrdGrcAcc()).isTrue();
    }

    @Test
    void seleccionarCoEjecutor_unidadExistente_laAsignaYGuardaElProyecto() {
        UnidadEjecutora coEjecutor = UnidadEjecutora.builder().id(2L).nombre("Co-ejecutora").build();
        UnidadEjecutoraResumenDto resumenCoEjecutor = new UnidadEjecutoraResumenDto().nombre("Co-ejecutora");
        when(unidadRepository.findById(2L)).thenReturn(Optional.of(coEjecutor));
        when(proyectoMapper.toResumen(coEjecutor)).thenReturn(resumenCoEjecutor);

        FichaInformacionGeneralDto dto = fichaGeneral.seleccionarCoEjecutor(ID_PROYECTO,
                new SeleccionCoEjecutorRequestDto().idUnidadEjecutoraCoEjecutora(2L));

        assertThat(proyecto.getUnidadEjecutoraCoEjecutora()).isSameAs(coEjecutor);
        assertThat(dto.getCoEjecutor()).isSameAs(resumenCoEjecutor);
        verify(proyectos).guardar(proyecto);
    }

    @Test
    void seleccionarCoEjecutor_unidadInexistente_lanzaRecursoNoEncontradoSinGuardar() {
        when(unidadRepository.findById(9L)).thenReturn(Optional.empty());
        SeleccionCoEjecutorRequestDto request = new SeleccionCoEjecutorRequestDto().idUnidadEjecutoraCoEjecutora(9L);

        assertThatThrownBy(() -> fichaGeneral.seleccionarCoEjecutor(ID_PROYECTO, request))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("La Unidad Ejecutora 9 no existe.");
        verify(proyectos, never()).guardar(any());
    }
}
