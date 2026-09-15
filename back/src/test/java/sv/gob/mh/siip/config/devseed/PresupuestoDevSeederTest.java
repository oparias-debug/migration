package sv.gob.mh.siip.config.devseed;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.argThat;

import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.FichaEmergencia;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.FichaEmergenciaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

class PresupuestoDevSeederTest {

    private ProyectoRepository proyectoRepository;
    private FichaEmergenciaRepository fichaEmergenciaRepository;
    private InstitucionRepository institucionRepository;
    private UnidadEjecutoraRepository unidadEjecutoraRepository;
    private SectorActividadRepository sectorActividadRepository;
    private EjeTematicoRepository ejeTematicoRepository;
    private PresupuestoDevSeeder seeder;

    @BeforeEach
    void setUp() {
        proyectoRepository = mock(ProyectoRepository.class);
        fichaEmergenciaRepository = mock(FichaEmergenciaRepository.class);
        institucionRepository = mock(InstitucionRepository.class);
        unidadEjecutoraRepository = mock(UnidadEjecutoraRepository.class);
        sectorActividadRepository = mock(SectorActividadRepository.class);
        ejeTematicoRepository = mock(EjeTematicoRepository.class);
        seeder = new PresupuestoDevSeeder(proyectoRepository, fichaEmergenciaRepository, institucionRepository,
                unidadEjecutoraRepository, sectorActividadRepository, ejeTematicoRepository);

        Institucion institucion = Institucion.builder().id(1L).codigo("MH-DGICP").build();
        UnidadEjecutora unidadEjecutora = UnidadEjecutora.builder().id(2L).codigo("URP-01").build();
        SectorActividad sector = SectorActividad.builder().id(3L)
                .codigo("Desarrollo Social::Educación y cultura").build();
        EjeTematico ejeTematico = EjeTematico.builder().id(4L)
                .codigo("Infraestructura Educativa (Construcción y Mejoramiento)").build();

        when(institucionRepository.findByCodigo("MH-DGICP")).thenReturn(Optional.of(institucion));
        when(unidadEjecutoraRepository.findByCodigo("URP-01")).thenReturn(Optional.of(unidadEjecutora));
        when(sectorActividadRepository.findByCodigo("Desarrollo Social::Educación y cultura"))
                .thenReturn(Optional.of(sector));
        when(ejeTematicoRepository.findByCodigo("Infraestructura Educativa (Construcción y Mejoramiento)"))
                .thenReturn(Optional.of(ejeTematico));
        when(proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc()).thenReturn(Optional.empty());
        when(proyectoRepository.save(any())).thenAnswer(inv -> {
            Proyecto proyecto = inv.getArgument(0);
            proyecto.setId(99L);
            return proyecto;
        });
    }

    @Test
    void seed_creaElProyectoDeEmergenciaConSuFichaEmergencia_cuandoNoExisteAun() {
        when(proyectoRepository.findByNombreContainingIgnoreCase(anyString())).thenReturn(List.of());

        seeder.seed();

        verify(proyectoRepository).save(argThat((Proyecto p) -> Boolean.TRUE.equals(p.getEsProyectoEmergencia())
                && p.getEstado() == EstadoProyecto.CUP_ASIGNADO && "10000".equals(p.getCup())));
        verify(fichaEmergenciaRepository).save(argThat((FichaEmergencia f) -> f.getProyecto() != null
                && f.getProyecto().getId().equals(99L) && !f.getProductos().isEmpty()));
    }

    @Test
    void seed_esIdempotente_cuandoElProyectoYaExiste() {
        when(proyectoRepository.findByNombreContainingIgnoreCase(anyString()))
                .thenReturn(List.of(mock(Proyecto.class)));

        seeder.seed();

        verify(proyectoRepository, never()).save(any());
        verify(fichaEmergenciaRepository, never()).save(any());
    }
}
