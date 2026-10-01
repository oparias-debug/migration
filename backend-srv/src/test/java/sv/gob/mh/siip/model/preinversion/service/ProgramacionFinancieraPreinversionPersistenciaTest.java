package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.ProgramacionFinPreinversionDetalle;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.programacion.dto.FilaProgramacionEtapaRequestDto;
import sv.gob.mh.siip.model.preinversion.programacion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgramacionFinPreinversionDetalleRepository;

class ProgramacionFinancieraPreinversionPersistenciaTest {

    private static final Proyecto PROYECTO = Proyecto.builder().id(8L).build();

    private EtapaPreinversionRepository etapas;
    private ProgramacionFinPreinversionDetalleRepository detalles;
    private ProgramacionFinancieraPreinversionPersistencia persistencia;

    @BeforeEach
    void preparar() {
        etapas = mock(EtapaPreinversionRepository.class);
        detalles = mock(ProgramacionFinPreinversionDetalleRepository.class);
        persistencia = new ProgramacionFinancieraPreinversionPersistencia(etapas, detalles);
    }

    @Test
    void reemplazarGuardaDetallesAunqueLaEtapaYaNoExistaSinActualizarSuCosto() {
        EtapaPreinversion perfil = EtapaPreinversion.builder().tipoEtapa(TipoEtapaPreinversion.PERFIL).costo(40D)
                .build();
        when(etapas.findByProyectoIdAndTipoEtapa(8L, TipoEtapaPreinversion.PERFIL)).thenReturn(Optional.empty());
        List<FilaProgramacionEtapaRequestDto> filas = List.of(
                new FilaProgramacionEtapaRequestDto(NombreEtapaDto.PERFIL, List.of(7D)));

        persistencia.reemplazar(PROYECTO, List.of(perfil), filas);

        assertThat(perfil.getCosto()).isZero();
        verify(detalles).deleteByProyectoId(8L);
        verify(detalles).save(any(ProgramacionFinPreinversionDetalle.class));
        verify(etapas, times(1)).save(perfil);
    }

    @Test
    void depurarEliminaDetallesSinPeriodoOFueraDeEtapasVigentesYRecalculaCostos() {
        EtapaPreinversion perfil = EtapaPreinversion.builder().tipoEtapa(TipoEtapaPreinversion.PERFIL).build();
        EtapaPreinversion diseno = EtapaPreinversion.builder().tipoEtapa(TipoEtapaPreinversion.DISENO).costo(9D)
                .build();
        ProgramacionFinPreinversionDetalle valido = detalle(TipoEtapaPreinversion.PERFIL, 1, "12.345");
        ProgramacionFinPreinversionDetalle sinPeriodo = detalle(TipoEtapaPreinversion.PERFIL, null, "1.00");
        ProgramacionFinPreinversionDetalle ejecucion = detalle(TipoEtapaPreinversion.EJECUCION, 1, "2.00");
        when(detalles.findByProyectoId(8L)).thenReturn(List.of(valido, sinPeriodo, ejecucion), List.of(valido));

        persistencia.depurarYRecalcular(PROYECTO, List.of(perfil, diseno), 2);

        verify(detalles).deleteAll(List.of(sinPeriodo, ejecucion));
        assertThat(perfil.getCosto()).isEqualTo(12.35D);
        assertThat(diseno.getCosto()).isZero();
    }

    @Test
    void depurarSinDetallesInvalidosNoEliminaNada() {
        EtapaPreinversion perfil = EtapaPreinversion.builder().tipoEtapa(TipoEtapaPreinversion.PERFIL).build();
        List<ProgramacionFinPreinversionDetalle> vigentes = List.of(detalle(TipoEtapaPreinversion.PERFIL, 2, "3.00"));
        when(detalles.findByProyectoId(8L)).thenReturn(vigentes);

        persistencia.depurarYRecalcular(PROYECTO, List.of(perfil), 2);

        verify(detalles, never()).deleteAll(anyCollection());
        assertThat(perfil.getCosto()).isEqualTo(3D);
    }

    private static ProgramacionFinPreinversionDetalle detalle(TipoEtapaPreinversion etapa, Integer periodo,
            String monto) {
        return ProgramacionFinPreinversionDetalle.builder().proyecto(PROYECTO).etapa(etapa).periodo(periodo)
                .monto(new BigDecimal(monto)).build();
    }
}
