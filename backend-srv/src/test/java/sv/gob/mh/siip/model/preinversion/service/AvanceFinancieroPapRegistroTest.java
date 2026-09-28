package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;
import sv.gob.mh.siip.model.preinversion.domain.AvanceFinancieroCuatrimestral;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.FuenteFinanciamientoEtapaPap;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralFinanciera;
import sv.gob.mh.siip.model.preinversion.dto.EtapaAvanceRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaAvanceFuenteRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.AvanceFinancieroCuatrimestralRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;

class AvanceFinancieroPapRegistroTest {

    private static final int ANIO = 2028;
    private static final Long ID_PROYECTO = 1L;
    private static final Long ID_FUENTE = 30L;

    private final EtapaPreinversionRepository etapaPreinversionRepository = mock(EtapaPreinversionRepository.class);
    private final FuenteFinanciamientoEtapaPapRepository fuenteRepository =
            mock(FuenteFinanciamientoEtapaPapRepository.class);
    private final ProgCuatrimestralFinancieraRepository progRepository =
            mock(ProgCuatrimestralFinancieraRepository.class);
    private final AvanceFinancieroCuatrimestralRepository avanceRepository =
            mock(AvanceFinancieroCuatrimestralRepository.class);

    private final AvanceFinancieroPapRegistro registro = new AvanceFinancieroPapRegistro(
            new AvancePapConsultas(mock(ProyectoRepository.class), etapaPreinversionRepository,
                    mock(CalendarioEventoRepository.class)),
            new AvanceFinancieroPapCalculos(fuenteRepository, progRepository, avanceRepository),
            fuenteRepository, progRepository, avanceRepository);

    @BeforeEach
    void fuenteSinProgramacionDelAnio() {
        EtapaPreinversion etapa = EtapaPreinversion.builder().id(10L).tipoEtapa(TipoEtapaPreinversion.PERFIL).build();
        when(etapaPreinversionRepository.findByProyectoIdAndTipoEtapa(ID_PROYECTO, TipoEtapaPreinversion.PERFIL))
                .thenReturn(Optional.of(etapa));
        when(fuenteRepository.findById(ID_FUENTE))
                .thenReturn(Optional.of(FuenteFinanciamientoEtapaPap.builder().id(ID_FUENTE).etapaPreinversion(etapa)
                        .build()));
        when(progRepository.save(any(ProgCuatrimestralFinanciera.class))).then(returnsFirstArg());
    }

    private static List<EtapaAvanceRequestDto> solicitud(double monto) {
        return List.of(new EtapaAvanceRequestDto(NombreEtapaDto.PERFIL)
                .fuentes(List.of(new FilaAvanceFuenteRequestDto(ID_FUENTE, monto).observacionesCuatrimestre("Obs"))));
    }

    @Test
    void guardar_sinProgramacionDelAnio_creaLaProgramacionYRegistraElAvance() {
        registro.guardar(ID_PROYECTO, solicitud(0D), ANIO, Cuatrimestre.CUATRIMESTRE_I);

        ArgumentCaptor<ProgCuatrimestralFinanciera> prog = ArgumentCaptor.forClass(ProgCuatrimestralFinanciera.class);
        verify(progRepository).save(prog.capture());
        assertThat(prog.getValue().getAnio()).isEqualTo(ANIO);
        assertThat(prog.getValue().getFuente().getId()).isEqualTo(ID_FUENTE);
        ArgumentCaptor<AvanceFinancieroCuatrimestral> avance =
                ArgumentCaptor.forClass(AvanceFinancieroCuatrimestral.class);
        verify(avanceRepository).save(avance.capture());
        assertThat(avance.getValue().getProgramacion()).isSameAs(prog.getValue());
        assertThat(avance.getValue().getObservaciones()).isEqualTo("Obs");
    }

    @Test
    void guardar_sinProgramacionDelAnio_rechazaCualquierMontoPositivo() {
        List<EtapaAvanceRequestDto> etapas = solicitud(1D);

        assertThatThrownBy(() -> registro.guardar(ID_PROYECTO, etapas, ANIO, Cuatrimestre.CUATRIMESTRE_I))
                .isInstanceOf(ValidacionNegocioException.class)
                .extracting("codigo").isEqualTo("MONTO_SUPERA_PROGRAMADO_ANUAL");
        verify(avanceRepository, never()).save(any(AvanceFinancieroCuatrimestral.class));
    }
}
