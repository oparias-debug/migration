package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralMetaFisica;
import sv.gob.mh.siip.model.preinversion.dto.EntregableDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaMetaFisicaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.enums.Entregable;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;

class ProgramacionMetasFisicasPapRegistroTest {

    private static final int ANIO = 2028;
    private static final Long ID_PROYECTO = 1L;

    private final EtapaPreinversionRepository etapaPreinversionRepository = mock(EtapaPreinversionRepository.class);
    private final EtapaMetaFisicaPapRepository etapaMetaRepository = mock(EtapaMetaFisicaPapRepository.class);
    private final ProgCuatrimestralMetaFisicaRepository progRepository =
            mock(ProgCuatrimestralMetaFisicaRepository.class);

    private final ProgramacionMetasFisicasPapRegistro registro = new ProgramacionMetasFisicasPapRegistro(
            new ProgramacionPapConsultas(mock(ProyectoRepository.class), etapaPreinversionRepository),
            etapaMetaRepository, progRepository);

    @Test
    void guardar_soloPersisteLasEtapasIncluidasEnLaSolicitud() {
        EtapaPreinversion perfil = EtapaPreinversion.builder().id(10L).tipoEtapa(TipoEtapaPreinversion.PERFIL).build();
        EtapaPreinversion prefactibilidad = EtapaPreinversion.builder().id(11L)
                .tipoEtapa(TipoEtapaPreinversion.PREFACTIBILIDAD).build();
        when(etapaPreinversionRepository.findByProyectoId(ID_PROYECTO)).thenReturn(List.of(prefactibilidad, perfil));
        when(etapaMetaRepository.save(any(EtapaMetaFisicaPap.class))).then(returnsFirstArg());
        // Solo se programa el III Cuatrimestre: basta con que uno de los tres sea mayor que cero.
        EtapaMetaFisicaRequestDto soloTercerCuatrimestre = new EtapaMetaFisicaRequestDto(NombreEtapaDto.PERFIL)
                .entregable(EntregableDto.ESTUDIO_DE_PERFIL)
                .montoCuatrimestre1(0D).montoCuatrimestre2(0D).montoCuatrimestre3(50D);

        registro.guardar(ID_PROYECTO, ANIO, List.of(soloTercerCuatrimestre));

        ArgumentCaptor<EtapaMetaFisicaPap> etapaMeta = ArgumentCaptor.forClass(EtapaMetaFisicaPap.class);
        verify(etapaMetaRepository).save(etapaMeta.capture());
        assertThat(etapaMeta.getValue().getEtapaPreinversion()).isSameAs(perfil);
        assertThat(etapaMeta.getValue().getEntregable()).isEqualTo(Entregable.ESTUDIO_DE_PERFIL);
        assertThat(etapaMeta.getValue().getActivo()).isTrue();
        ArgumentCaptor<ProgCuatrimestralMetaFisica> prog = ArgumentCaptor.forClass(ProgCuatrimestralMetaFisica.class);
        verify(progRepository).save(prog.capture());
        assertThat(prog.getValue().getAnio()).isEqualTo(ANIO);
        assertThat(prog.getValue().getMontoCuatrimestre3()).isEqualByComparingTo("50");
    }

    @Test
    void esArrastreEtapa_metaFisicaSinPersistir_noEsDeArrastre() {
        ProgramacionMetasFisicasPapCalculos calculos =
                new ProgramacionMetasFisicasPapCalculos(etapaMetaRepository, progRepository);

        assertThat(calculos.esArrastreEtapa(EtapaMetaFisicaPap.builder().build(), ANIO)).isFalse();
        verifyNoInteractions(progRepository);
    }
}
