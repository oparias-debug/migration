package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ActualizarEtapasRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaRegistroRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.mapper.SeleccionYRegistroDeEtapasMapper;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;

class SeleccionEtapasRegistroTest {

    private static final Long ID_PROYECTO = 10L;

    private final EtapaPreinversionRepository etapaRepository = mock(EtapaPreinversionRepository.class);
    private final SeleccionEtapasProyectos proyectos = mock(SeleccionEtapasProyectos.class);
    private final SeleccionYRegistroDeEtapasMapper mapper = mock(SeleccionYRegistroDeEtapasMapper.class);
    private final SeleccionEtapasRegistro registro = new SeleccionEtapasRegistro(etapaRepository, proyectos, mapper);

    private final Proyecto proyecto = Proyecto.builder().id(ID_PROYECTO).build();
    private final List<EtapaDto> dtos = List.of(new EtapaDto());

    @BeforeEach
    void setUp() {
        when(proyectos.buscar(ID_PROYECTO)).thenReturn(proyecto);
        when(mapper.toDtoList(any())).thenReturn(dtos);
        when(etapaRepository.findByProyectoIdAndTipoEtapa(any(), any())).thenReturn(Optional.empty());
    }

    @Test
    void listar_conEtapasExistentes_lasDevuelveEnOrdenDeRutaSinCrearNinguna() {
        EtapaPreinversion ejecucion = etapa(TipoEtapaPreinversion.EJECUCION);
        EtapaPreinversion perfil = etapa(TipoEtapaPreinversion.PERFIL);
        when(etapaRepository.findByProyectoId(ID_PROYECTO)).thenReturn(List.of(ejecucion, perfil));

        List<EtapaDto> resultado = registro.listar(ID_PROYECTO);

        assertThat(resultado).isSameAs(dtos);
        verify(mapper).toDtoList(List.of(perfil, ejecucion));
        verify(etapaRepository, never()).save(any());
    }

    @Test
    void listar_sinEtapas_creaPerfilYEjecucionHabilitadas() {
        EtapaPreinversion perfil = etapa(TipoEtapaPreinversion.PERFIL);
        when(etapaRepository.findByProyectoId(ID_PROYECTO)).thenReturn(List.of(), List.of(perfil));

        registro.listar(ID_PROYECTO);

        ArgumentCaptor<EtapaPreinversion> guardadas = ArgumentCaptor.forClass(EtapaPreinversion.class);
        verify(etapaRepository, times(2)).save(guardadas.capture());
        assertThat(guardadas.getAllValues()).extracting(EtapaPreinversion::getTipoEtapa)
                .containsExactly(TipoEtapaPreinversion.PERFIL, TipoEtapaPreinversion.EJECUCION);
        assertThat(guardadas.getAllValues()).allSatisfy((EtapaPreinversion etapa) -> {
            assertThat(etapa.getHabilitadoParaRegistro()).isTrue();
            assertThat(etapa.getProyecto()).isSameAs(proyecto);
            assertThat(etapa.getFechaSeleccion()).isNotNull();
        });
        verify(mapper).toDtoList(List.of(perfil));
    }

    @Test
    void listar_proyectoDeEmergenciaSinEtapas_creaSoloPerfil() {
        proyecto.setEsProyectoEmergencia(true);
        when(etapaRepository.findByProyectoId(ID_PROYECTO)).thenReturn(List.of());

        registro.listar(ID_PROYECTO);

        ArgumentCaptor<EtapaPreinversion> guardada = ArgumentCaptor.forClass(EtapaPreinversion.class);
        verify(etapaRepository).save(guardada.capture());
        assertThat(guardada.getValue().getTipoEtapa()).isEqualTo(TipoEtapaPreinversion.PERFIL);
    }

    @Test
    void actualizar_fechasValidas_guardaCostoFechasYHabilitaLaEtapa() {
        EtapaPreinversion ejecucion = etapa(TipoEtapaPreinversion.EJECUCION);
        ejecucion.setCosto(500d);
        when(etapaRepository.findByProyectoIdAndTipoEtapa(ID_PROYECTO, TipoEtapaPreinversion.EJECUCION))
                .thenReturn(Optional.of(ejecucion));
        when(etapaRepository.findByProyectoId(ID_PROYECTO)).thenReturn(List.of());

        List<EtapaDto> resultado = registro.actualizar(ID_PROYECTO, new ActualizarEtapasRequestDto()
                .addEtapasItem(item(NombreEtapaDto.DISENO, 100d, "01/02/2025", "28/02/2025"))
                .addEtapasItem(item(NombreEtapaDto.EJECUCION, 999d, "01/03/2025", null))
                .addEtapasItem(item(NombreEtapaDto.PREFACTIBILIDAD, 50d, null, "31/01/2025")));

        assertThat(resultado).isSameAs(dtos);
        ArgumentCaptor<EtapaPreinversion> guardadas = ArgumentCaptor.forClass(EtapaPreinversion.class);
        verify(etapaRepository, times(3)).save(guardadas.capture());
        EtapaPreinversion diseno = guardadas.getAllValues().get(0);
        assertThat(diseno.getTipoEtapa()).isEqualTo(TipoEtapaPreinversion.DISENO);
        assertThat(diseno.getCosto()).isEqualTo(100d);
        assertThat(diseno.getFechaInicio()).isEqualTo(LocalDate.of(2025, 2, 1));
        assertThat(diseno.getFechaFin()).isEqualTo(LocalDate.of(2025, 2, 28));
        assertThat(diseno.getHabilitadoParaRegistro()).isTrue();
        // RN05/RN11: el costo de EJECUCION enviado por el cliente se ignora.
        assertThat(ejecucion.getCosto()).isEqualTo(500d);
        assertThat(ejecucion.getFechaInicio()).isEqualTo(LocalDate.of(2025, 3, 1));
        assertThat(ejecucion.getFechaFin()).isNull();
        assertThat(ejecucion.getHabilitadoParaRegistro()).isFalse();
        EtapaPreinversion prefactibilidad = guardadas.getAllValues().get(2);
        assertThat(prefactibilidad.getFechaInicio()).isNull();
        assertThat(prefactibilidad.getFechaFin()).isEqualTo(LocalDate.of(2025, 1, 31));
        assertThat(prefactibilidad.getHabilitadoParaRegistro()).isFalse();
    }

    @Test
    void actualizar_fechaNoCalendario_lanzaFechaInvalidaSinGuardar() {
        ActualizarEtapasRequestDto request = new ActualizarEtapasRequestDto()
                .addEtapasItem(item(NombreEtapaDto.PERFIL, 1d, "32/01/2025", "15/03/2025"));

        assertThatThrownBy(() -> registro.actualizar(ID_PROYECTO, request))
                .isInstanceOfSatisfying(ValidacionNegocioException.class, (ValidacionNegocioException ex) -> {
                    assertThat(ex.getCodigo()).isEqualTo("FECHA_INVALIDA");
                    assertThat(ex.getDetalles()).extracting(ErrorDetalleDto::getCampo).containsExactly("PERFIL");
                });
        verify(etapaRepository, never()).save(any());
    }

    @Test
    void actualizar_etapaQueTerminaAntesDeIniciar_lanzaFechasInconsistentes() {
        EtapaPreinversion perfil = etapaConFechas(TipoEtapaPreinversion.PERFIL, "2025-03-10", "2025-03-01");
        when(etapaRepository.findByProyectoId(ID_PROYECTO)).thenReturn(List.of(perfil));
        ActualizarEtapasRequestDto request = new ActualizarEtapasRequestDto();

        assertThatThrownBy(() -> registro.actualizar(ID_PROYECTO, request))
                .isInstanceOfSatisfying(ValidacionNegocioException.class, (ValidacionNegocioException ex) -> {
                    assertThat(ex.getCodigo()).isEqualTo("FECHAS_ETAPAS_INCONSISTENTES");
                    assertThat(ex.getDetalles()).singleElement().satisfies((ErrorDetalleDto detalle) ->
                            assertThat(detalle.getMensaje()).contains("posterior a la fecha de finalización"));
                });
    }

    @Test
    void actualizar_etapaQueIniciaAntesDeTerminarLaPrevia_lanzaFechasInconsistentes() {
        EtapaPreinversion perfil = etapaConFechas(TipoEtapaPreinversion.PERFIL, "2025-01-01", "2025-01-31");
        EtapaPreinversion incompleta = etapa(TipoEtapaPreinversion.PREFACTIBILIDAD);
        incompleta.setFechaInicio(LocalDate.of(2024, 1, 1));
        EtapaPreinversion sinInicio = etapa(TipoEtapaPreinversion.FACTIBILIDAD);
        sinInicio.setFechaFin(LocalDate.of(2024, 1, 1));
        EtapaPreinversion diseno = etapaConFechas(TipoEtapaPreinversion.DISENO, "2025-01-15", "2025-02-15");
        EtapaPreinversion ejecucion = etapaConFechas(TipoEtapaPreinversion.EJECUCION, "2025-02-16", "2025-12-31");
        when(etapaRepository.findByProyectoId(ID_PROYECTO))
                .thenReturn(List.of(ejecucion, diseno, sinInicio, incompleta, perfil));
        ActualizarEtapasRequestDto request = new ActualizarEtapasRequestDto();

        assertThatThrownBy(() -> registro.actualizar(ID_PROYECTO, request))
                .isInstanceOfSatisfying(ValidacionNegocioException.class, (ValidacionNegocioException ex) ->
                        assertThat(ex.getDetalles()).singleElement().satisfies((ErrorDetalleDto detalle) -> {
                            assertThat(detalle.getCampo()).isEqualTo("DISENO");
                            assertThat(detalle.getMensaje()).isEqualTo(
                                    "No puede iniciar antes de que finalice la etapa PERFIL.");
                        }));
    }

    @Test
    void sincronizar_bloqueandoEmitidas_marcaSoloLasEmitidasFueraDeLaSeleccion() {
        EtapaPreinversion emitidaFuera = etapa(TipoEtapaPreinversion.FACTIBILIDAD);
        emitidaFuera.setTieneOpinionTecnica(true);
        EtapaPreinversion noEmitidaFuera = etapa(TipoEtapaPreinversion.PREFACTIBILIDAD);
        EtapaPreinversion emitidaDentro = etapa(TipoEtapaPreinversion.PERFIL);
        emitidaDentro.setTieneOpinionTecnica(true);
        when(etapaRepository.findByProyectoId(ID_PROYECTO))
                .thenReturn(List.of(emitidaFuera, noEmitidaFuera, emitidaDentro));
        when(etapaRepository.findByProyectoIdAndTipoEtapa(ID_PROYECTO, TipoEtapaPreinversion.PERFIL))
                .thenReturn(Optional.of(emitidaDentro));

        registro.sincronizarBloqueandoEmitidas(proyecto, List.of(TipoEtapaPreinversion.PERFIL));

        assertThat(emitidaFuera.getBloqueadaPorModificacion()).isTrue();
        assertThat(noEmitidaFuera.getBloqueadaPorModificacion()).isFalse();
        assertThat(emitidaDentro.getBloqueadaPorModificacion()).isFalse();
        verify(etapaRepository).save(emitidaFuera);
        verify(etapaRepository, times(1)).save(any());
    }

    @Test
    void sincronizar_etapaNoHabilitadaPorDefecto_seCreaDeshabilitada() {
        registro.sincronizar(proyecto, List.of(TipoEtapaPreinversion.FACTIBILIDAD));

        ArgumentCaptor<EtapaPreinversion> guardada = ArgumentCaptor.forClass(EtapaPreinversion.class);
        verify(etapaRepository).save(guardada.capture());
        assertThat(guardada.getValue().getHabilitadoParaRegistro()).isFalse();
        verify(etapaRepository, never()).findByProyectoId(any());
    }

    private EtapaPreinversion etapa(TipoEtapaPreinversion tipo) {
        return EtapaPreinversion.builder().id((long) tipo.ordinal() + 1).proyecto(proyecto).tipoEtapa(tipo).build();
    }

    private EtapaPreinversion etapaConFechas(TipoEtapaPreinversion tipo, String inicio, String fin) {
        EtapaPreinversion etapa = etapa(tipo);
        etapa.setFechaInicio(LocalDate.parse(inicio));
        etapa.setFechaFin(LocalDate.parse(fin));
        return etapa;
    }

    private static EtapaRegistroRequestDto item(NombreEtapaDto nombre, Double costo, String inicio, String fin) {
        return new EtapaRegistroRequestDto().nombreEtapa(nombre).costo(costo).fechaInicio(inicio).fechaFin(fin);
    }
}
