package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjePlanGobierno;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.IniciativaInversionDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.repository.EjePlanGobiernoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.PlanSectorialRegionalRepository;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

class ProyectoDatosRegistroTest {

    private SectorActividadRepository sectorActividadRepository;
    private EjeTematicoRepository ejeTematicoRepository;
    private EjePlanGobiernoRepository ejePlanGobiernoRepository;
    private PlanSectorialRegionalRepository planSectorialRegionalRepository;
    private UnidadEjecutoraRepository unidadEjecutoraRepository;
    private ProyectoDatosRegistro datosRegistro;

    private SectorActividad sector;
    private EjeTematico ejeTematico;

    @BeforeEach
    void setUp() {
        sectorActividadRepository = mock(SectorActividadRepository.class);
        ejeTematicoRepository = mock(EjeTematicoRepository.class);
        ejePlanGobiernoRepository = mock(EjePlanGobiernoRepository.class);
        planSectorialRegionalRepository = mock(PlanSectorialRegionalRepository.class);
        unidadEjecutoraRepository = mock(UnidadEjecutoraRepository.class);
        datosRegistro = new ProyectoDatosRegistro(sectorActividadRepository, ejeTematicoRepository,
                ejePlanGobiernoRepository, planSectorialRegionalRepository, unidadEjecutoraRepository);

        sector = SectorActividad.builder().id(1L).codigo("SEC-1").nombre("Sector 1").build();
        ejeTematico = EjeTematico.builder().id(1L).codigo("EJE-1").nombre("Eje 1").activo(true).build();
    }

    @Test
    void nuevo_copiaLaPeticionYResuelveLosCatalogos() {
        when(sectorActividadRepository.findById(1L)).thenReturn(Optional.of(sector));
        when(ejeTematicoRepository.findById(1L)).thenReturn(Optional.of(ejeTematico));
        EjePlanGobierno ejePlanGobierno = EjePlanGobierno.builder().id(2L).codigo("EPG-2").nombre("Eje 2")
                .activo(true).build();
        when(ejePlanGobiernoRepository.findById(2L)).thenReturn(Optional.of(ejePlanGobierno));
        UnidadEjecutora unidad = UnidadEjecutora.builder().id(10L).build();
        Usuario actor = Usuario.builder().id(100L).unidadEjecutora(unidad).build();

        Proyecto entidad = datosRegistro.nuevo(actor, request().idEjePlanGobierno(2L).medidasGrd(List.of("GRD-1")));

        assertThat(entidad.getEstado()).isEqualTo(EstadoProyecto.EN_REGISTRO);
        assertThat(entidad.getUnidadEjecutora()).isSameAs(unidad);
        assertThat(entidad.getIniciativaInversion()).isEqualTo(IniciativaInversion.PROYECTO);
        assertThat(entidad.getSector()).isSameAs(sector);
        assertThat(entidad.getEjeTematico()).isSameAs(ejeTematico);
        assertThat(entidad.getEjePlanGobierno()).isSameAs(ejePlanGobierno);
        assertThat(entidad.getPlanSectorialRegional()).isNull();
        assertThat(entidad.getMedidasGrd()).containsExactly("GRD-1");
        assertThat(entidad.getMedidasGrc()).isEmpty();
        verifyNoInteractions(planSectorialRegionalRepository);
    }

    @Test
    void nuevo_acumulaUnDetallePorCadaCatalogoInexistente() {
        when(sectorActividadRepository.findById(1L)).thenReturn(Optional.empty());
        when(ejeTematicoRepository.findById(1L)).thenReturn(Optional.of(ejeTematico));
        when(planSectorialRegionalRepository.findById(3L)).thenReturn(Optional.empty());
        Usuario actor = Usuario.builder().id(100L).build();
        ProyectoRequestDto request = request().idPlanSectorialRegional(3L);

        assertThatThrownBy(() -> datosRegistro.nuevo(actor, request))
                .isInstanceOf(ValidacionNegocioException.class)
                .satisfies(ex -> assertThat(((ValidacionNegocioException) ex).getDetalles())
                        .extracting(ErrorDetalleDto::getCampo)
                        .containsExactly("idSector", "idPlanSectorialRegional"));
    }

    @Test
    void actualizar_rechazaProyectoNoEditable_sinResolverCatalogos() {
        Proyecto entidad = Proyecto.builder().id(1L).estado(EstadoProyecto.CUP_ASIGNADO).build();
        ProyectoRequestDto request = request();

        assertThatThrownBy(() -> datosRegistro.actualizar(entidad, request))
                .isInstanceOf(ConflictoEstadoException.class);
        verifyNoInteractions(sectorActividadRepository, ejeTematicoRepository);
    }

    @Test
    void actualizar_aplicaLaPeticion_cuandoElProyectoEsEditable() {
        when(sectorActividadRepository.findById(1L)).thenReturn(Optional.of(sector));
        when(ejeTematicoRepository.findById(1L)).thenReturn(Optional.of(ejeTematico));
        Proyecto entidad = Proyecto.builder().id(1L).estado(EstadoProyecto.OBSERVADO_DGICP_REGISTRO).build();

        datosRegistro.actualizar(entidad, request().nombre("Nombre nuevo"));

        assertThat(entidad.getNombre()).isEqualTo("Nombre nuevo");
    }

    @Test
    void asignarUnidadEjecutora_reasignaUnidadEInstitucion() {
        Institucion institucion = Institucion.builder().id(2L).build();
        UnidadEjecutora unidad = UnidadEjecutora.builder().id(20L).institucion(institucion).build();
        when(unidadEjecutoraRepository.findById(20L)).thenReturn(Optional.of(unidad));
        Proyecto entidad = Proyecto.builder().id(1L).build();

        datosRegistro.asignarUnidadEjecutora(entidad, 20L);

        assertThat(entidad.getUnidadEjecutora()).isSameAs(unidad);
        assertThat(entidad.getInstitucion()).isSameAs(institucion);
    }

    @Test
    void asignarUnidadEjecutora_rechazaUnidadInexistente() {
        when(unidadEjecutoraRepository.findById(99L)).thenReturn(Optional.empty());
        Proyecto entidad = Proyecto.builder().id(1L).build();

        assertThatThrownBy(() -> datosRegistro.asignarUnidadEjecutora(entidad, 99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    private static ProyectoRequestDto request() {
        return new ProyectoRequestDto()
                .iniciativaInversion(IniciativaInversionDto.PROYECTO)
                .nombre("Proyecto de prueba")
                .montoEstimadoInversion(1000.0)
                .idSector(1L)
                .idEjeTematico(1L)
                .descripcionProyecto("Descripcion de prueba");
    }
}
