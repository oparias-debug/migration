package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.ProgramacionFinPreinversionDetalle;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.programacion.dto.ConfigurarPeriodosProgramacionPreinversionRequestDto;
import sv.gob.mh.siip.model.preinversion.programacion.dto.FilaProgramacionEtapaRequestDto;
import sv.gob.mh.siip.model.preinversion.programacion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.programacion.dto.ProgramacionFinancieraPreinversionRequestDto;
import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.ProgramacionFinPreinversionConfig;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgramacionFinPreinversionConfigRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgramacionFinPreinversionDetalleRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

class ProgramacionFinancieraPreinversionServiceTest {
    private ProyectoRepository proyectos;
    private EtapaPreinversionRepository etapas;
    private ProgramacionFinPreinversionConfigRepository configuraciones;
    private ProgramacionFinPreinversionDetalleRepository detalles;
    private ActorContexto actor;
    private ProgramacionFinancieraPreinversionService service;

    @BeforeEach
    void preparar() {
        proyectos = mock(ProyectoRepository.class);
        etapas = mock(EtapaPreinversionRepository.class);
        configuraciones = mock(ProgramacionFinPreinversionConfigRepository.class);
        detalles = mock(ProgramacionFinPreinversionDetalleRepository.class);
        actor = mock(ActorContexto.class);
        when(actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE))
                .thenReturn(Usuario.builder().rol(RolUsuario.TECNICO_PRE).build());
        service = new ProgramacionFinancieraPreinversionService(proyectos, etapas, configuraciones, detalles, actor);
    }

    @Test
    void tecnicoPreConsultaEtapasDeOtraUnidadYObtieneTotalesPorPeriodo() {
        Proyecto proyecto = Proyecto.builder().id(8L).build();
        when(proyectos.findById(8L)).thenReturn(Optional.of(proyecto));
        when(configuraciones.findByProyectoId(8L)).thenReturn(Optional.empty());
        when(detalles.findByProyectoId(8L)).thenReturn(List.of());
        when(etapas.findByProyectoId(8L)).thenReturn(List.of(EtapaPreinversion.builder()
                .tipoEtapa(TipoEtapaPreinversion.PERFIL).build()));

        var respuesta = service.obtener(8L);

        assertThat(respuesta.getIdProyecto()).isEqualTo(8L);
        assertThat(respuesta.getFilas()).hasSize(1);
        assertThat(respuesta.getTotalGeneralPorPeriodo()).isEmpty();
    }

    @Test
    void tecnicoUrpGuardaMontosYActualizaCostoDeLaEtapa() {
        Proyecto proyecto = Proyecto.builder().id(8L).build();
        EtapaPreinversion etapa = EtapaPreinversion.builder().tipoEtapa(TipoEtapaPreinversion.PERFIL).build();
        when(actor.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
        when(proyectos.findById(8L)).thenReturn(Optional.of(proyecto));
        when(etapas.findByProyectoIdAndTipoEtapa(8L, TipoEtapaPreinversion.PERFIL)).thenReturn(Optional.of(etapa));
        when(configuraciones.findByProyectoId(8L)).thenReturn(Optional.of(ProgramacionFinPreinversionConfig.builder()
                .proyecto(proyecto).periodosAProgramar(2).build()));
        when(detalles.findByProyectoId(8L)).thenReturn(List.of());
        when(etapas.findByProyectoId(8L)).thenReturn(List.of(etapa));

        FilaProgramacionEtapaRequestDto fila = new FilaProgramacionEtapaRequestDto(NombreEtapaDto.PERFIL,
                List.of(100D, 50D));
        ProgramacionFinancieraPreinversionRequestDto solicitud = new ProgramacionFinancieraPreinversionRequestDto()
                .filas(List.of(fila));

        var respuesta = service.guardar(8L, solicitud);

        assertThat(etapa.getCosto()).isEqualTo(150D);
        assertThat(respuesta.getFilas()).hasSize(1);
        verify(detalles).deleteByProyectoId(8L);
        verify(detalles, times(2)).save(org.mockito.ArgumentMatchers.any(ProgramacionFinPreinversionDetalle.class));
        verify(etapas, times(2)).save(etapa);
    }

    @Test
    void alOmitirUnaEtapaLaProgramacionEliminaSusDetallesYReiniciaSuCosto() {
        Proyecto proyecto = Proyecto.builder().id(8L).build();
        EtapaPreinversion perfil = EtapaPreinversion.builder().tipoEtapa(TipoEtapaPreinversion.PERFIL).costo(80D).build();
        EtapaPreinversion diseno = EtapaPreinversion.builder().tipoEtapa(TipoEtapaPreinversion.DISENO).costo(90D).build();
        prepararGuardado(proyecto, List.of(perfil, diseno), 2);
        when(etapas.findByProyectoIdAndTipoEtapa(8L, TipoEtapaPreinversion.PERFIL)).thenReturn(Optional.of(perfil));

        service.guardar(8L, solicitud(NombreEtapaDto.PERFIL, List.of(10D, 20D)));

        assertThat(perfil.getCosto()).isEqualTo(30D);
        assertThat(diseno.getCosto()).isZero();
        verify(detalles).deleteByProyectoId(8L);
    }

    @Test
    void rechazaEtapaEjecucionAntesDeBorrarLaProgramacionAnterior() {
        Proyecto proyecto = Proyecto.builder().id(8L).build();
        EtapaPreinversion ejecucion = EtapaPreinversion.builder().tipoEtapa(TipoEtapaPreinversion.EJECUCION).build();
        prepararGuardado(proyecto, List.of(ejecucion), 2);

        ProgramacionFinancieraPreinversionRequestDto solicitud = solicitud(NombreEtapaDto.EJECUCION, List.of(10D, 20D));

        assertThatThrownBy(() -> service.guardar(8L, solicitud))
                .isInstanceOf(ValidacionNegocioException.class);

        verify(detalles, never()).deleteByProyectoId(8L);
    }

    @Test
    void rechazaEtapaQueNoPerteneceALaRutaAntesDeBorrarLaProgramacionAnterior() {
        Proyecto proyecto = Proyecto.builder().id(8L).build();
        EtapaPreinversion perfil = EtapaPreinversion.builder().tipoEtapa(TipoEtapaPreinversion.PERFIL).build();
        prepararGuardado(proyecto, List.of(perfil), 2);

        ProgramacionFinancieraPreinversionRequestDto solicitud = solicitud(NombreEtapaDto.DISENO, List.of(10D, 20D));

        assertThatThrownBy(() -> service.guardar(8L, solicitud))
                .isInstanceOf(ValidacionNegocioException.class);

        verify(detalles, never()).deleteByProyectoId(8L);
    }

    @Test
    void rechazaMontosFueraDelHorizonteONegativosAntesDePersistir() {
        Proyecto proyecto = Proyecto.builder().id(8L).build();
        EtapaPreinversion perfil = EtapaPreinversion.builder().tipoEtapa(TipoEtapaPreinversion.PERFIL).build();
        prepararGuardado(proyecto, List.of(perfil), 2);

        ProgramacionFinancieraPreinversionRequestDto solicitud = solicitud(NombreEtapaDto.PERFIL, List.of(5D, -1D, 8D));

        assertThatThrownBy(() -> service.guardar(8L, solicitud))
                .isInstanceOf(ValidacionNegocioException.class);

        verify(detalles, never()).deleteByProyectoId(8L);
    }

    @Test
    void rechazaGuardarSinPeriodosConfigurados() {
        Proyecto proyecto = Proyecto.builder().id(8L).build();
        when(actor.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
        when(proyectos.findById(8L)).thenReturn(Optional.of(proyecto));
        when(configuraciones.findByProyectoId(8L)).thenReturn(Optional.of(ProgramacionFinPreinversionConfig.builder()
                .proyecto(proyecto).periodosAProgramar(2).build()));

        ProgramacionFinancieraPreinversionRequestDto solicitud = new ProgramacionFinancieraPreinversionRequestDto()
                .filas(List.of());

        assertThatThrownBy(() -> service.guardar(8L, solicitud))
                .isInstanceOf(ValidacionNegocioException.class);
        verify(detalles, never()).deleteByProyectoId(8L);
    }

    @Test
    void alReducirPeriodosEliminaDetallesFueraDelHorizonteYRecalculaCosto() {
        Proyecto proyecto = Proyecto.builder().id(8L).build();
        EtapaPreinversion perfil = EtapaPreinversion.builder().tipoEtapa(TipoEtapaPreinversion.PERFIL).costo(300D).build();
        ProgramacionFinPreinversionConfig config = ProgramacionFinPreinversionConfig.builder()
                .proyecto(proyecto).periodosAProgramar(3).build();
        ProgramacionFinPreinversionDetalle detalleValido = ProgramacionFinPreinversionDetalle.builder()
                .proyecto(proyecto).etapa(TipoEtapaPreinversion.PERFIL).periodo(1).monto(new BigDecimal("100.00"))
                .build();
        ProgramacionFinPreinversionDetalle detalleFueraDeRango = ProgramacionFinPreinversionDetalle.builder()
                .proyecto(proyecto).etapa(TipoEtapaPreinversion.PERFIL).periodo(3).monto(new BigDecimal("200.00"))
                .build();
        when(actor.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
        when(proyectos.findById(8L)).thenReturn(Optional.of(proyecto));
        when(configuraciones.findByProyectoId(8L)).thenReturn(Optional.of(config));
        when(detalles.findByProyectoId(8L)).thenReturn(List.of(detalleValido, detalleFueraDeRango), List.of(detalleValido));
        when(etapas.findByProyectoId(8L)).thenReturn(List.of(perfil));

        service.configurarPeriodos(8L, new ConfigurarPeriodosProgramacionPreinversionRequestDto(1));

        assertThat(perfil.getCosto()).isEqualTo(100D);
        verify(detalles).deleteAll(List.of(detalleFueraDeRango));
    }

    @Test
    void redondeaCadaMontoMonetarioAntesDePersistirYTotalizar() {
        Proyecto proyecto = Proyecto.builder().id(8L).build();
        EtapaPreinversion perfil = EtapaPreinversion.builder().tipoEtapa(TipoEtapaPreinversion.PERFIL).build();
        prepararGuardado(proyecto, List.of(perfil), 2);
        when(etapas.findByProyectoIdAndTipoEtapa(8L, TipoEtapaPreinversion.PERFIL)).thenReturn(Optional.of(perfil));
        ArgumentCaptor<ProgramacionFinPreinversionDetalle> detallesGuardados = ArgumentCaptor
                .forClass(ProgramacionFinPreinversionDetalle.class);

        service.guardar(8L, solicitud(NombreEtapaDto.PERFIL, List.of(10.005D, 20.004D)));

        verify(detalles, times(2)).save(detallesGuardados.capture());
        assertThat(detallesGuardados.getAllValues()).extracting(ProgramacionFinPreinversionDetalle::getMonto)
                .containsExactly(new BigDecimal("10.01"), new BigDecimal("20.00"));
        assertThat(perfil.getCosto()).isEqualTo(30.01D);
    }

    @Test
    void tecnicoUrpNoPuedeConsultarProyectoDeOtraUnidadEjecutora() {
        UnidadEjecutora unidadActor = UnidadEjecutora.builder().id(1L).build();
        UnidadEjecutora unidadProyecto = UnidadEjecutora.builder().id(2L).build();
        when(actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE)).thenReturn(
                Usuario.builder().rol(RolUsuario.TECNICO_URP).unidadEjecutora(unidadActor).build());
        when(proyectos.findById(8L)).thenReturn(Optional.of(Proyecto.builder().id(8L).unidadEjecutora(unidadProyecto).build()));

        assertThatThrownBy(() -> service.obtener(8L)).isInstanceOf(AccesoDenegadoException.class);
    }

    @Test
    void tecnicoUrpConsultaProyectoDeSuUnidadIgnorandoConfiguracionSinPeriodos() {
        UnidadEjecutora unidad = UnidadEjecutora.builder().id(1L).build();
        when(actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE)).thenReturn(
                Usuario.builder().rol(RolUsuario.TECNICO_URP).unidadEjecutora(unidad).build());
        Proyecto proyecto = Proyecto.builder().id(8L).unidadEjecutora(unidad).build();
        when(proyectos.findById(8L)).thenReturn(Optional.of(proyecto));
        when(configuraciones.findByProyectoId(8L)).thenReturn(Optional.of(ProgramacionFinPreinversionConfig.builder()
                .proyecto(proyecto).periodosAProgramar(0).build()));
        when(detalles.findByProyectoId(8L)).thenReturn(List.of());
        when(etapas.findByProyectoId(8L)).thenReturn(List.of(
                EtapaPreinversion.builder().tipoEtapa(TipoEtapaPreinversion.PERFIL).build(),
                EtapaPreinversion.builder().tipoEtapa(TipoEtapaPreinversion.EJECUCION).build()));

        var respuesta = service.obtener(8L);

        assertThat(respuesta.getPeriodosAProgramar()).isZero();
        assertThat(respuesta.getFilas()).hasSize(1);
    }

    @Test
    void rechazaConsultarProyectoInexistente() {
        when(proyectos.findById(8L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener(8L)).isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void configurarPeriodosSinSolicitudRechazaAntesDeGuardarConfiguracion() {
        Proyecto proyecto = Proyecto.builder().id(8L).build();
        when(actor.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
        when(proyectos.findById(8L)).thenReturn(Optional.of(proyecto));

        assertThatThrownBy(() -> service.configurarPeriodos(8L, null)).isInstanceOf(ValidacionNegocioException.class);
        verify(configuraciones, never()).save(any(ProgramacionFinPreinversionConfig.class));
    }

    @Test
    void configurarPeriodosCreaLaConfiguracionCuandoNoExiste() {
        Proyecto proyecto = Proyecto.builder().id(8L).build();
        when(actor.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
        when(proyectos.findById(8L)).thenReturn(Optional.of(proyecto));
        when(configuraciones.findByProyectoId(8L)).thenReturn(Optional.empty());
        when(detalles.findByProyectoId(8L)).thenReturn(List.of());
        when(etapas.findByProyectoId(8L)).thenReturn(List.of());
        ArgumentCaptor<ProgramacionFinPreinversionConfig> guardada = ArgumentCaptor
                .forClass(ProgramacionFinPreinversionConfig.class);

        service.configurarPeriodos(8L, new ConfigurarPeriodosProgramacionPreinversionRequestDto(4));

        verify(configuraciones).save(guardada.capture());
        assertThat(guardada.getValue().getProyecto()).isSameAs(proyecto);
        assertThat(guardada.getValue().getPeriodosAProgramar()).isEqualTo(4);
    }

    @Test
    void rechazaGuardarCuandoLaConfiguracionTieneCeroPeriodos() {
        Proyecto proyecto = Proyecto.builder().id(8L).build();
        prepararGuardado(proyecto, List.of(), 0);
        ProgramacionFinancieraPreinversionRequestDto solicitud = solicitud(NombreEtapaDto.PERFIL, List.of(1D));

        assertThatThrownBy(() -> service.guardar(8L, solicitud))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessage("Debe configurar al menos un período antes de guardar.");
        verify(detalles, never()).deleteByProyectoId(8L);
    }

    private void prepararGuardado(Proyecto proyecto, List<EtapaPreinversion> etapasProyecto, int periodos) {
        when(actor.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
        when(proyectos.findById(8L)).thenReturn(Optional.of(proyecto));
        when(configuraciones.findByProyectoId(8L)).thenReturn(Optional.of(ProgramacionFinPreinversionConfig.builder()
                .proyecto(proyecto).periodosAProgramar(periodos).build()));
        when(etapas.findByProyectoId(8L)).thenReturn(etapasProyecto);
        when(detalles.findByProyectoId(8L)).thenReturn(List.of());
    }

    private static ProgramacionFinancieraPreinversionRequestDto solicitud(NombreEtapaDto etapa, List<Double> montos) {
        return new ProgramacionFinancieraPreinversionRequestDto().filas(List.of(
                new FilaProgramacionEtapaRequestDto(etapa, montos)));
    }
}
