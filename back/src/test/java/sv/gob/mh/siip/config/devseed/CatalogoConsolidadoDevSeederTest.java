package sv.gob.mh.siip.config.devseed;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.domain.CriterioElegibilidad;
import sv.gob.mh.siip.model.preinversion.domain.CriterioPriorizacion;
import sv.gob.mh.siip.model.preinversion.domain.EntradaCatalogoEspecificar;
import sv.gob.mh.siip.model.preinversion.domain.EscalaCalificacionSubcriterio;
import sv.gob.mh.siip.model.preinversion.domain.IndicadorResultado;
import sv.gob.mh.siip.model.preinversion.domain.Parametro;
import sv.gob.mh.siip.model.preinversion.domain.RangoInterpretacionPriorizacion;
import sv.gob.mh.siip.model.preinversion.domain.SubcriterioPriorizacion;
import sv.gob.mh.siip.model.preinversion.repository.CriterioElegibilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.CriterioPriorizacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.EntradaCatalogoEspecificarRepository;
import sv.gob.mh.siip.model.preinversion.repository.EscalaCalificacionSubcriterioRepository;
import sv.gob.mh.siip.model.preinversion.repository.IndicadorResultadoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ParametroRepository;
import sv.gob.mh.siip.model.preinversion.repository.RangoInterpretacionPriorizacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.SubcriterioPriorizacionRepository;

class CatalogoConsolidadoDevSeederTest {

    private ParametroRepository parametroRepository;
    private IndicadorResultadoRepository indicadorResultadoRepository;
    private RangoInterpretacionPriorizacionRepository rangoInterpretacionPriorizacionRepository;
    private CriterioPriorizacionRepository criterioPriorizacionRepository;
    private SubcriterioPriorizacionRepository subcriterioPriorizacionRepository;
    private EscalaCalificacionSubcriterioRepository escalaCalificacionSubcriterioRepository;
    private CriterioElegibilidadRepository criterioElegibilidadRepository;
    private EntradaCatalogoEspecificarRepository entradaCatalogoEspecificarRepository;
    private CatalogoConsolidadoDevSeeder seeder;

    @BeforeEach
    void setUp() {
        parametroRepository = mock(ParametroRepository.class);
        indicadorResultadoRepository = mock(IndicadorResultadoRepository.class);
        rangoInterpretacionPriorizacionRepository = mock(RangoInterpretacionPriorizacionRepository.class);
        criterioPriorizacionRepository = mock(CriterioPriorizacionRepository.class);
        subcriterioPriorizacionRepository = mock(SubcriterioPriorizacionRepository.class);
        escalaCalificacionSubcriterioRepository = mock(EscalaCalificacionSubcriterioRepository.class);
        criterioElegibilidadRepository = mock(CriterioElegibilidadRepository.class);
        entradaCatalogoEspecificarRepository = mock(EntradaCatalogoEspecificarRepository.class);
        seeder = new CatalogoConsolidadoDevSeeder(parametroRepository, indicadorResultadoRepository,
                rangoInterpretacionPriorizacionRepository, criterioPriorizacionRepository,
                subcriterioPriorizacionRepository, escalaCalificacionSubcriterioRepository,
                criterioElegibilidadRepository, entradaCatalogoEspecificarRepository);
    }

    @Test
    void seed_creaTodosLosCatalogos_cuandoNoExistenAun() {
        when(parametroRepository.findByCodigo(anyString())).thenReturn(Optional.empty());
        when(indicadorResultadoRepository.findByCodigo(anyString())).thenReturn(Optional.empty());
        when(rangoInterpretacionPriorizacionRepository.findByCategoria(anyString())).thenReturn(Optional.empty());
        when(criterioPriorizacionRepository.findByCodigo(anyString())).thenReturn(Optional.empty());
        when(criterioPriorizacionRepository.save(any())).thenAnswer(inv -> {
            CriterioPriorizacion criterio = inv.getArgument(0);
            criterio.setId(1L);
            return criterio;
        });
        when(subcriterioPriorizacionRepository.save(any())).thenAnswer(inv -> {
            SubcriterioPriorizacion subcriterio = inv.getArgument(0);
            subcriterio.setId(1L);
            return subcriterio;
        });
        when(escalaCalificacionSubcriterioRepository.findByCodigoSubcriterioAndValor(anyString(), any()))
                .thenReturn(Optional.empty());
        when(criterioElegibilidadRepository.findByCodigo(anyString())).thenReturn(Optional.empty());
        when(entradaCatalogoEspecificarRepository.findByTipoAndCodigo(any(), anyString()))
                .thenReturn(Optional.empty());

        seeder.seed();

        verify(parametroRepository, times(4)).save(any(Parametro.class));
        verify(indicadorResultadoRepository, times(4)).save(any(IndicadorResultado.class));
        verify(rangoInterpretacionPriorizacionRepository, times(4))
                .save(any(RangoInterpretacionPriorizacion.class));
        verify(criterioPriorizacionRepository, times(4)).save(any(CriterioPriorizacion.class));
        verify(subcriterioPriorizacionRepository, times(7)).save(any(SubcriterioPriorizacion.class));
        verify(escalaCalificacionSubcriterioRepository, times(49)).save(any(EscalaCalificacionSubcriterio.class));
        verify(criterioElegibilidadRepository, times(6)).save(any(CriterioElegibilidad.class));
        verify(entradaCatalogoEspecificarRepository, times(8)).save(any(EntradaCatalogoEspecificar.class));
    }

    @Test
    void seed_esIdempotente_cuandoYaExisteElCriterioDePriorizacion() {
        when(parametroRepository.findByCodigo(anyString())).thenReturn(Optional.of(mock(Parametro.class)));
        when(indicadorResultadoRepository.findByCodigo(anyString()))
                .thenReturn(Optional.of(mock(IndicadorResultado.class)));
        when(rangoInterpretacionPriorizacionRepository.findByCategoria(anyString()))
                .thenReturn(Optional.of(mock(RangoInterpretacionPriorizacion.class)));
        when(criterioPriorizacionRepository.findByCodigo(anyString()))
                .thenReturn(Optional.of(mock(CriterioPriorizacion.class)));
        when(criterioElegibilidadRepository.findByCodigo(anyString()))
                .thenReturn(Optional.of(mock(CriterioElegibilidad.class)));
        when(entradaCatalogoEspecificarRepository.findByTipoAndCodigo(any(), anyString()))
                .thenReturn(Optional.of(mock(EntradaCatalogoEspecificar.class)));

        seeder.seed();

        verify(parametroRepository, never()).save(any());
        verify(indicadorResultadoRepository, never()).save(any());
        verify(rangoInterpretacionPriorizacionRepository, never()).save(any());
        verify(criterioPriorizacionRepository, never()).save(any());
        verify(subcriterioPriorizacionRepository, never()).save(any());
        verify(escalaCalificacionSubcriterioRepository, never()).save(any());
        verify(criterioElegibilidadRepository, never()).save(any());
        verify(entradaCatalogoEspecificarRepository, never()).save(any());
    }
}
