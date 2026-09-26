package sv.gob.mh.siip.config.devseed;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.domain.EjePlanGobierno;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.MedidaCatalogo;
import sv.gob.mh.siip.model.preinversion.domain.PlanSectorialRegional;
import sv.gob.mh.siip.model.preinversion.enums.TipoMedidaCatalogo;
import sv.gob.mh.siip.model.preinversion.repository.EjePlanGobiernoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.MedidaCatalogoRepository;
import sv.gob.mh.siip.model.preinversion.repository.PlanSectorialRegionalRepository;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

class CatalogoProyectoDevSeederTest {

    private MacroSectorRepository macroSectorRepository;
    private SectorActividadRepository sectorActividadRepository;
    private EjeTematicoRepository ejeTematicoRepository;
    private EjePlanGobiernoRepository ejePlanGobiernoRepository;
    private PlanSectorialRegionalRepository planSectorialRegionalRepository;
    private MedidaCatalogoRepository medidaCatalogoRepository;
    private CatalogoProyectoDevSeeder seeder;

    @BeforeEach
    void setUp() {
        macroSectorRepository = mock(MacroSectorRepository.class);
        sectorActividadRepository = mock(SectorActividadRepository.class);
        ejeTematicoRepository = mock(EjeTematicoRepository.class);
        ejePlanGobiernoRepository = mock(EjePlanGobiernoRepository.class);
        planSectorialRegionalRepository = mock(PlanSectorialRegionalRepository.class);
        medidaCatalogoRepository = mock(MedidaCatalogoRepository.class);
        seeder = new CatalogoProyectoDevSeeder(macroSectorRepository, sectorActividadRepository,
                ejeTematicoRepository, ejePlanGobiernoRepository, planSectorialRegionalRepository,
                medidaCatalogoRepository);
    }

    @Test
    void seed_creaTodosLosCatalogos_cuandoNoExistenAun() {
        when(macroSectorRepository.findByCodigo(anyString())).thenReturn(Optional.empty());
        when(macroSectorRepository.save(any(MacroSector.class))).thenAnswer(inv -> inv.getArgument(0));
        when(sectorActividadRepository.findByCodigo(anyString())).thenReturn(Optional.empty());
        when(ejeTematicoRepository.findByCodigo(anyString())).thenReturn(Optional.empty());
        when(ejePlanGobiernoRepository.findByCodigo(anyString())).thenReturn(Optional.empty());
        when(planSectorialRegionalRepository.findByCodigo(anyString())).thenReturn(Optional.empty());
        when(medidaCatalogoRepository.findByTipoOrderByCodigo(any())).thenReturn(List.of());

        seeder.seed();

        verify(macroSectorRepository, times(3)).save(any(MacroSector.class));
        verify(sectorActividadRepository, times(17)).save(any(SectorActividad.class));
        verify(ejeTematicoRepository, times(22)).save(any(EjeTematico.class));
        verify(ejePlanGobiernoRepository, times(9)).save(any(EjePlanGobierno.class));
        verify(planSectorialRegionalRepository, times(7)).save(any(PlanSectorialRegional.class));
        verify(medidaCatalogoRepository, times(9)).save(any(MedidaCatalogo.class));
        // Una sola consulta de medidas existentes por tipo, no una por fila del CSV.
        verify(medidaCatalogoRepository).findByTipoOrderByCodigo(TipoMedidaCatalogo.GRD);
        verify(medidaCatalogoRepository).findByTipoOrderByCodigo(TipoMedidaCatalogo.GRC);
        verify(medidaCatalogoRepository).findByTipoOrderByCodigo(TipoMedidaCatalogo.ACC);
    }

    @Test
    void seed_esIdempotente_cuandoYaExisten() {
        when(macroSectorRepository.findByCodigo(anyString())).thenReturn(Optional.of(mock(MacroSector.class)));
        when(sectorActividadRepository.findByCodigo(anyString()))
                .thenReturn(Optional.of(mock(SectorActividad.class)));
        when(ejeTematicoRepository.findByCodigo(anyString())).thenReturn(Optional.of(mock(EjeTematico.class)));
        when(ejePlanGobiernoRepository.findByCodigo(anyString()))
                .thenReturn(Optional.of(mock(EjePlanGobierno.class)));
        when(planSectorialRegionalRepository.findByCodigo(anyString()))
                .thenReturn(Optional.of(mock(PlanSectorialRegional.class)));
        when(medidaCatalogoRepository.findByTipoOrderByCodigo(any())).thenAnswer(inv -> List.of(
                MedidaCatalogo.builder().codigo("1").build(), MedidaCatalogo.builder().codigo("2").build(),
                MedidaCatalogo.builder().codigo("3").build(), MedidaCatalogo.builder().codigo("4").build()));

        seeder.seed();

        verify(macroSectorRepository, never()).save(any());
        verify(sectorActividadRepository, never()).save(any());
        verify(ejeTematicoRepository, never()).save(any());
        verify(ejePlanGobiernoRepository, never()).save(any());
        verify(planSectorialRegionalRepository, never()).save(any());
        verify(medidaCatalogoRepository, never()).save(any());
    }
}
