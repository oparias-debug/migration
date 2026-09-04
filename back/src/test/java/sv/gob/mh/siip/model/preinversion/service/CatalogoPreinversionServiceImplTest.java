package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.EjePlanGobierno;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.PlanSectorialRegional;
import sv.gob.mh.siip.model.preinversion.mapper.ProyectoMapper;
import sv.gob.mh.siip.model.preinversion.repository.EjePlanGobiernoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.PlanSectorialRegionalRepository;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;
import sv.gob.mh.siip.security.ActorContexto;

class CatalogoPreinversionServiceImplTest {

    private final SectorActividadRepository sectorActividadRepository = mock(SectorActividadRepository.class);
    private final EjeTematicoRepository ejeTematicoRepository = mock(EjeTematicoRepository.class);
    private final EjePlanGobiernoRepository ejePlanGobiernoRepository = mock(EjePlanGobiernoRepository.class);
    private final PlanSectorialRegionalRepository planSectorialRegionalRepository = mock(
            PlanSectorialRegionalRepository.class);
    private final ProyectoMapper mapper = Mappers.getMapper(ProyectoMapper.class);
    private final ActorContexto actorContexto = mock(ActorContexto.class);

    private final CatalogoPreinversionServiceImpl service = new CatalogoPreinversionServiceImpl(
            sectorActividadRepository, ejeTematicoRepository, ejePlanGobiernoRepository,
            planSectorialRegionalRepository, mapper, actorContexto);

    {
        when(actorContexto.exigir()).thenReturn(Usuario.builder().build());
    }

    @Test
    void listarSectores_devuelveLosSectoresOrdenadosPorNombre() {
        when(sectorActividadRepository.findAllByOrderByNombreAsc())
                .thenReturn(List.of(SectorActividad.builder().id(1L).codigo("SEC-1").nombre("Salud").build()));

        var resultado = service.listarSectores();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCodigo()).isEqualTo("SEC-1");
        assertThat(resultado.get(0).getNombre()).isEqualTo("Salud");
    }

    @Test
    void listarEjesTematicos_devuelveSoloLosActivos() {
        when(ejeTematicoRepository.findByActivoTrueOrderByNombre()).thenReturn(
                List.of(EjeTematico.builder().id(1L).codigo("ET-1").nombre("Educacion").activo(true).build()));

        var resultado = service.listarEjesTematicos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCodigo()).isEqualTo("ET-1");
        assertThat(resultado.get(0).getNombre()).isEqualTo("Educacion");
    }

    @Test
    void listarEjesPlanGobierno_devuelveSoloLosActivos() {
        when(ejePlanGobiernoRepository.findByActivoTrueOrderByNombre()).thenReturn(List.of(
                EjePlanGobierno.builder().id(1L).codigo("EPG-1").nombre("Eje 1").activo(true).build()));

        var resultado = service.listarEjesPlanGobierno();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCodigo()).isEqualTo("EPG-1");
        assertThat(resultado.get(0).getNombre()).isEqualTo("Eje 1");
    }

    @Test
    void listarPlanesSectoriales_devuelveSoloLosActivos() {
        when(planSectorialRegionalRepository.findByActivoTrueOrderByNombre()).thenReturn(List.of(
                PlanSectorialRegional.builder().id(1L).codigo("PSR-1").nombre("Plan 1").activo(true).build()));

        var resultado = service.listarPlanesSectoriales();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCodigo()).isEqualTo("PSR-1");
        assertThat(resultado.get(0).getNombre()).isEqualTo("Plan 1");
    }
}
