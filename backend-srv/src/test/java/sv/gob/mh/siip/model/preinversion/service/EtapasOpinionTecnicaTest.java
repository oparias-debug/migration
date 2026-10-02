package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
    private CostoEtapaEjecucion costoEjecucion;
    private EtapasOpinionTecnica etapas;

    @BeforeEach
    void setUp() {
        repositorio = mock(EtapaPreinversionRepository.class);
        costoEjecucion = mock(CostoEtapaEjecucion.class);
        etapas = new EtapasOpinionTecnica(repositorio, costoEjecucion);
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
        EtapaPreinversion fuera = etapa(TipoEtapaPreinversion.PERFIL, false);
        fuera.setFueraDeRuta(true);
        ruta(fuera, etapa(TipoEtapaPreinversion.FACTIBILIDAD, false), etapa(TipoEtapaPreinversion.EJECUCION, false));

        EtapasOpinionTecnica.Etapas gestionadas = etapas.paraOpinionTecnica(ID_PROYECTO);

        assertThat(gestionadas.actual()).isEqualTo(TipoEtapaPreinversion.FACTIBILIDAD);
        assertThat(gestionadas.habilitaEjecucion()).isTrue();
    }

    @Test
    void unaEtapaBloqueadaPorRn13SigueEnLaRutaYEsperaSuTurno() {
        EtapaPreinversion diseno = etapa(TipoEtapaPreinversion.DISENO, false);
        diseno.setBloqueadaPorModificacion(true);
        ruta(etapa(TipoEtapaPreinversion.PERFIL, true), etapa(TipoEtapaPreinversion.PREFACTIBILIDAD, false), diseno);

        EtapasOpinionTecnica.Etapas gestionadas = etapas.paraOpinionTecnica(ID_PROYECTO);

        assertThat(gestionadas.actual()).isEqualTo(TipoEtapaPreinversion.PREFACTIBILIDAD);
        assertThat(gestionadas.futura()).isEqualTo(TipoEtapaPreinversion.DISENO);
    }

    @Test
    void recalcularBloqueos_etapaConOtDespuesDeUnaSinOt_pierdeLaOtYQuedaBloqueada() {
        EtapaPreinversion perfil = etapa(TipoEtapaPreinversion.PERFIL, true);
        EtapaPreinversion prefactibilidad = etapa(TipoEtapaPreinversion.PREFACTIBILIDAD, false);
        EtapaPreinversion diseno = etapa(TipoEtapaPreinversion.DISENO, true);
        EtapaPreinversion ejecucion = etapa(TipoEtapaPreinversion.EJECUCION, false);
        ruta(ejecucion, diseno, prefactibilidad, perfil);

        etapas.recalcularBloqueos(ID_PROYECTO);

        assertThat(diseno.getBloqueadaPorModificacion()).isTrue();
        assertThat(diseno.getTieneOpinionTecnica()).isFalse();
        assertThat(perfil.getBloqueadaPorModificacion()).isFalse();
        assertThat(perfil.getTieneOpinionTecnica()).isTrue();
        assertThat(ejecucion.getBloqueadaPorModificacion()).isFalse();
        verify(repositorio).save(diseno);
        verify(repositorio, times(1)).save(any());
    }

    @Test
    void recalcularBloqueos_sinEtapasAnterioresPendientes_desbloquea() {
        EtapaPreinversion diseno = etapa(TipoEtapaPreinversion.DISENO, false);
        diseno.setBloqueadaPorModificacion(true);
        ruta(etapa(TipoEtapaPreinversion.PERFIL, true), etapa(TipoEtapaPreinversion.PREFACTIBILIDAD, true), diseno);

        etapas.recalcularBloqueos(ID_PROYECTO);

        assertThat(diseno.getBloqueadaPorModificacion()).isFalse();
        assertThat(diseno.getTieneOpinionTecnica()).isFalse();
        verify(repositorio).save(diseno);
    }

    @Test
    void recalcularBloqueos_etapaNuncaEmitida_noSeBloquea() {
        ruta(etapa(TipoEtapaPreinversion.PERFIL, false), etapa(TipoEtapaPreinversion.DISENO, false));

        etapas.recalcularBloqueos(ID_PROYECTO);

        verify(repositorio, never()).save(any());
    }

    @Test
    void marcarEmitida_deLaEtapaAnteriorPendiente_desbloqueaLaSiguiente() {
        EtapaPreinversion prefactibilidad = etapa(TipoEtapaPreinversion.PREFACTIBILIDAD, false);
        EtapaPreinversion diseno = etapa(TipoEtapaPreinversion.DISENO, false);
        diseno.setBloqueadaPorModificacion(true);
        ruta(etapa(TipoEtapaPreinversion.PERFIL, true), prefactibilidad, diseno);
        when(repositorio.findByProyectoIdAndTipoEtapa(ID_PROYECTO, TipoEtapaPreinversion.PREFACTIBILIDAD))
                .thenReturn(Optional.of(prefactibilidad));

        etapas.marcarEmitida(ID_PROYECTO, TipoEtapaPreinversion.PREFACTIBILIDAD);

        assertThat(prefactibilidad.getTieneOpinionTecnica()).isTrue();
        assertThat(diseno.getBloqueadaPorModificacion()).isFalse();
        assertThat(etapas.paraOpinionTecnica(ID_PROYECTO).actual()).isEqualTo(TipoEtapaPreinversion.DISENO);
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
        // RN11 de CU-PRE-3.5: cada OT emitida recalcula el costo de Ejecución.
        verify(costoEjecucion, times(2)).recalcular(ID_PROYECTO);
        verify(repositorio, never()).findByProyectoIdAndTipoEtapa(any(), org.mockito.ArgumentMatchers.isNull());
    }
}
