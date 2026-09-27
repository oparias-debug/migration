package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.FuenteFinanciamientoEtapaPap;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralFinanciera;
import sv.gob.mh.siip.model.preinversion.dto.EtapaProgramacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaFuenteProgramacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;

class ProgramacionFinancieraPapRegistroTest {

    private static final int ANIO = 2028;
    private static final Long ID_PROYECTO = 1L;

    private final ProyectoRepository proyectoRepository = mock(ProyectoRepository.class);
    private final EtapaPreinversionRepository etapaPreinversionRepository = mock(EtapaPreinversionRepository.class);
    private final FuenteFinanciamientoEtapaPapRepository fuenteRepository =
            mock(FuenteFinanciamientoEtapaPapRepository.class);
    private final ProgCuatrimestralFinancieraRepository progRepository =
            mock(ProgCuatrimestralFinancieraRepository.class);

    private final ProgramacionFinancieraPapRegistro registro = new ProgramacionFinancieraPapRegistro(
            new ProgramacionPapConsultas(proyectoRepository, etapaPreinversionRepository),
            etapaPreinversionRepository, fuenteRepository, progRepository);

    @Test
    void guardar_omiteEtapasFinalizadasYSinSolicitudYActualizaElCostoEnviado() {
        EtapaPreinversion perfilFinalizado = EtapaPreinversion.builder().id(11L)
                .tipoEtapa(TipoEtapaPreinversion.PERFIL).costo(100D).build();
        EtapaPreinversion prefactibilidad = EtapaPreinversion.builder().id(12L)
                .tipoEtapa(TipoEtapaPreinversion.PREFACTIBILIDAD).build();
        EtapaPreinversion factibilidad = EtapaPreinversion.builder().id(13L)
                .tipoEtapa(TipoEtapaPreinversion.FACTIBILIDAD).build();
        when(etapaPreinversionRepository.findByProyectoId(ID_PROYECTO))
                .thenReturn(List.of(factibilidad, prefactibilidad, perfilFinalizado));
        // Perfil ya se financió al 100% de su costo en años anteriores (RN-B.e).
        when(fuenteRepository.findByEtapaPreinversionId(11L))
                .thenReturn(List.of(FuenteFinanciamientoEtapaPap.builder().id(20L).build()));
        when(progRepository.findByFuenteId(20L)).thenReturn(List.of(ProgCuatrimestralFinanciera.builder()
                .anio(ANIO - 1).montoCuatrimestre1(new BigDecimal("100")).build()));
        when(fuenteRepository.save(any(FuenteFinanciamientoEtapaPap.class))).then(returnsFirstArg());

        FilaFuenteProgramacionRequestDto filaNueva = new FilaFuenteProgramacionRequestDto()
                .montoCuatrimestre1(300D).fuenteRecursos("Préstamo");
        filaNueva.setConvenios(null);
        EtapaProgramacionRequestDto solicitud = new EtapaProgramacionRequestDto(NombreEtapaDto.PREFACTIBILIDAD)
                .costoEtapa(500D).fuentes(List.of(filaNueva));

        registro.guardar(ID_PROYECTO, ANIO, List.of(solicitud));

        assertThat(prefactibilidad.getCosto()).isEqualTo(500D);
        verify(etapaPreinversionRepository).save(prefactibilidad);
        verify(etapaPreinversionRepository, never()).save(perfilFinalizado);
        verify(etapaPreinversionRepository, never()).save(factibilidad);

        ArgumentCaptor<FuenteFinanciamientoEtapaPap> fuente =
                ArgumentCaptor.forClass(FuenteFinanciamientoEtapaPap.class);
        verify(fuenteRepository).save(fuente.capture());
        assertThat(fuente.getValue().getEtapaPreinversion()).isSameAs(prefactibilidad);
        assertThat(fuente.getValue().getConvenios()).isEmpty();

        ArgumentCaptor<ProgCuatrimestralFinanciera> prog = ArgumentCaptor.forClass(ProgCuatrimestralFinanciera.class);
        verify(progRepository).save(prog.capture());
        assertThat(prog.getValue().getAnio()).isEqualTo(ANIO);
        assertThat(prog.getValue().totalProgramadoAnio()).isEqualByComparingTo("300");
    }

    @Test
    void guardar_solicitudSinCostoDeEtapa_noModificaElCostoRegistrado() {
        EtapaPreinversion perfil = EtapaPreinversion.builder().id(11L)
                .tipoEtapa(TipoEtapaPreinversion.PERFIL).costo(1000D).build();
        when(etapaPreinversionRepository.findByProyectoId(ID_PROYECTO)).thenReturn(List.of(perfil));
        when(fuenteRepository.save(any(FuenteFinanciamientoEtapaPap.class))).then(returnsFirstArg());
        EtapaProgramacionRequestDto solicitud = new EtapaProgramacionRequestDto(NombreEtapaDto.PERFIL)
                .fuentes(List.of(new FilaFuenteProgramacionRequestDto().montoCuatrimestre2(200D)));

        registro.guardar(ID_PROYECTO, ANIO, List.of(solicitud));

        assertThat(perfil.getCosto()).isEqualTo(1000D);
        verify(etapaPreinversionRepository, never()).save(any(EtapaPreinversion.class));
        verify(progRepository).save(any(ProgCuatrimestralFinanciera.class));
    }
}
