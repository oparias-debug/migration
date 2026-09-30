package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;

/** Pruebas unitarias de {@link EtapasOpinionTecnica} (CU-PRE-26, Anexo B.1 "Etapa actual/futura", RN11). */
class EtapasOpinionTecnicaTest {

    private static final Long ID_PROYECTO = 1L;

    private EtapaPreinversionRepository repositorio;
    private EtapasOpinionTecnica etapas;

    @BeforeEach
    void setUp() {
        repositorio = mock(EtapaPreinversionRepository.class);
        etapas = new EtapasOpinionTecnica(repositorio);
    }

    private static EtapaPreinversion etapa(TipoEtapaPreinversion tipo, boolean conOt) {
        return EtapaPreinversion.builder().tipoEtapa(tipo).tieneOpinionTecnica(conOt).build();
    }

    private void ruta(EtapaPreinversion... ruta) {
        when(repositorio.findByProyectoId(ID_PROYECTO)).thenReturn(List.of(ruta));
    }

    @Test
    void laEtapaActualEsLaPrimeraSinOtYLaFuturaLaSiguienteDeLaRuta() {
        ruta(etapa(TipoEtapaPreinversion.DISENO, false), etapa(TipoEtapaPreinversion.PERFIL, true),
                etapa(TipoEtapaPreinversion.PREFACTIBILIDAD, false));

        EtapasOpinionTecnica.Etapas gestionadas = etapas.paraOpinionTecnica(ID_PROYECTO);

        assertThat(gestionadas.actual()).isEqualTo(TipoEtapaPreinversion.PREFACTIBILIDAD);
        assertThat(gestionadas.futura()).isEqualTo(TipoEtapaPreinversion.DISENO);
        assertThat(gestionadas.habilitaEjecucion()).isFalse();
    }

    @Test
    void sinEtapasNoHayEtapaActualYConTodasEmitidasSeGestionaLaUltima() {
        ruta();
        assertThat(etapas.paraOpinionTecnica(ID_PROYECTO)).isEqualTo(new EtapasOpinionTecnica.Etapas(null, null));

        ruta(etapa(TipoEtapaPreinversion.PERFIL, true), etapa(TipoEtapaPreinversion.EJECUCION, true));
        EtapasOpinionTecnica.Etapas ultima = etapas.paraOpinionTecnica(ID_PROYECTO);
        assertThat(ultima.actual()).isEqualTo(TipoEtapaPreinversion.EJECUCION);
        assertThat(ultima.futura()).isNull();
        assertThat(ultima.habilitaEjecucion()).isTrue();
    }

    @Test
    void lasEtapasQueUnaModificacionDejoFueraNoCuentan() {
        EtapaPreinversion bloqueada = etapa(TipoEtapaPreinversion.PERFIL, false);
        bloqueada.setBloqueadaPorModificacion(true);
        ruta(bloqueada, etapa(TipoEtapaPreinversion.FACTIBILIDAD, false), etapa(TipoEtapaPreinversion.EJECUCION, false));

        EtapasOpinionTecnica.Etapas gestionadas = etapas.paraOpinionTecnica(ID_PROYECTO);

        assertThat(gestionadas.actual()).isEqualTo(TipoEtapaPreinversion.FACTIBILIDAD);
        assertThat(gestionadas.habilitaEjecucion()).isTrue();
    }

    @Test
    void laActualizacionGestionaLaEtapaMasAvanzadaConOt() {
        ruta(etapa(TipoEtapaPreinversion.PERFIL, true), etapa(TipoEtapaPreinversion.PREFACTIBILIDAD, true),
                etapa(TipoEtapaPreinversion.DISENO, false));
        assertThat(etapas.paraActualizacion(ID_PROYECTO)).contains(new EtapasOpinionTecnica.Etapas(
                TipoEtapaPreinversion.PREFACTIBILIDAD, TipoEtapaPreinversion.DISENO));

        ruta(etapa(TipoEtapaPreinversion.PERFIL, false));
        assertThat(etapas.paraActualizacion(ID_PROYECTO)).isEmpty();
    }

    @Test
    void marcaLaEtapaEmitida() {
        EtapaPreinversion perfil = etapa(TipoEtapaPreinversion.PERFIL, false);
        when(repositorio.findByProyectoIdAndTipoEtapa(ID_PROYECTO, TipoEtapaPreinversion.PERFIL))
                .thenReturn(Optional.of(perfil));

        etapas.marcarEmitida(ID_PROYECTO, TipoEtapaPreinversion.PERFIL);
        etapas.marcarEmitida(ID_PROYECTO, TipoEtapaPreinversion.DISENO);
        etapas.marcarEmitida(ID_PROYECTO, null);

        assertThat(perfil.getTieneOpinionTecnica()).isTrue();
        verify(repositorio).save(perfil);
        verify(repositorio, never()).findByProyectoIdAndTipoEtapa(any(), org.mockito.ArgumentMatchers.isNull());
    }
}
