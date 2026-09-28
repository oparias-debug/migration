package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;
import sv.gob.mh.siip.model.preinversion.domain.AvanceCuatriMetaFisica;
import sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralMetaFisica;
import sv.gob.mh.siip.model.preinversion.dto.EtapaAvanceMetasRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.AvanceCuatriMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;

class AvanceMetasFisicasPapRegistroTest {

    private static final int ANIO = 2028;
    private static final Long ID_PROYECTO = 1L;

    private final EtapaPreinversionRepository etapaPreinversionRepository = mock(EtapaPreinversionRepository.class);
    private final EtapaMetaFisicaPapRepository etapaMetaRepository = mock(EtapaMetaFisicaPapRepository.class);
    private final ProgCuatrimestralMetaFisicaRepository progRepository =
            mock(ProgCuatrimestralMetaFisicaRepository.class);
    private final AvanceCuatriMetaFisicaRepository avanceRepository = mock(AvanceCuatriMetaFisicaRepository.class);

    private final AvanceMetasFisicasPapRegistro registro = new AvanceMetasFisicasPapRegistro(
            new AvancePapConsultas(mock(ProyectoRepository.class), etapaPreinversionRepository,
                    mock(CalendarioEventoRepository.class)),
            new AvanceMetasFisicasPapCalculos(progRepository, avanceRepository),
            etapaMetaRepository, progRepository, avanceRepository);

    @Test
    void guardar_sinProgramacionDelAnio_creaLaProgramacionYRegistraElAvanceExentoDelTopeAnual() {
        EtapaPreinversion etapa = EtapaPreinversion.builder().id(10L).tipoEtapa(TipoEtapaPreinversion.PERFIL).build();
        when(etapaPreinversionRepository.findByProyectoIdAndTipoEtapa(ID_PROYECTO, TipoEtapaPreinversion.PERFIL))
                .thenReturn(Optional.of(etapa));
        EtapaMetaFisicaPap etapaMeta = EtapaMetaFisicaPap.builder().id(40L).etapaPreinversion(etapa).build();
        when(etapaMetaRepository.findByEtapaPreinversionId(10L)).thenReturn(Optional.of(etapaMeta));
        when(progRepository.save(any(ProgCuatrimestralMetaFisica.class))).then(returnsFirstArg());
        List<EtapaAvanceMetasRequestDto> etapas = List.of(
                new EtapaAvanceMetasRequestDto(NombreEtapaDto.PERFIL, 10D).observacionesCuatrimestre("Avance"));

        registro.guardar(ID_PROYECTO, etapas, ANIO, Cuatrimestre.CUATRIMESTRE_II);

        ArgumentCaptor<ProgCuatrimestralMetaFisica> prog = ArgumentCaptor.forClass(ProgCuatrimestralMetaFisica.class);
        verify(progRepository).save(prog.capture());
        assertThat(prog.getValue().getEtapaMetaFisica()).isSameAs(etapaMeta);
        assertThat(prog.getValue().getAnio()).isEqualTo(ANIO);
        ArgumentCaptor<AvanceCuatriMetaFisica> avance = ArgumentCaptor.forClass(AvanceCuatriMetaFisica.class);
        verify(avanceRepository).save(avance.capture());
        assertThat(avance.getValue().getAvanceCuatrimestre()).isEqualByComparingTo("10");
        assertThat(avance.getValue().getCuatrimestre()).isEqualTo(Cuatrimestre.CUATRIMESTRE_II);
    }
}
