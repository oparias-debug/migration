package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.Componente;
import sv.gob.mh.siip.model.preinversion.domain.MacroactividadPresupuesto;
import sv.gob.mh.siip.model.preinversion.domain.PresupuestoProyecto;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ConfigurarPeriodosEjecucionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FuenteFinanciamientoDto;
import sv.gob.mh.siip.model.preinversion.dto.FuentesFinanciamientoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.MacroactividadInsumoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.MacroactividadRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.PresupuestoDto;
import sv.gob.mh.siip.model.preinversion.enums.FuenteFinanciamiento;
import sv.gob.mh.siip.model.preinversion.repository.ComponenteRepository;
import sv.gob.mh.siip.model.preinversion.repository.MacroactividadPresupuestoRepository;
import sv.gob.mh.siip.model.preinversion.repository.PresupuestoProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

class PresupuestoInversionServiceTest {
  private ProyectoRepository proyectos;
  private ComponenteRepository componentes;
  private PresupuestoProyectoRepository presupuestos;
  private MacroactividadPresupuestoRepository macros;
  private ActorContexto actor;
  private PresupuestoInversionService service;
  private Proyecto proyecto;
  private PresupuestoProyecto presupuesto;

  @BeforeEach
  void setup() {
    proyectos = mock(ProyectoRepository.class);
    componentes = mock(ComponenteRepository.class);
    presupuestos = mock(PresupuestoProyectoRepository.class);
    macros = mock(MacroactividadPresupuestoRepository.class);
    actor = mock(ActorContexto.class);
    service = new PresupuestoInversionService(proyectos, componentes, presupuestos, macros, actor,
        new ObjectMapper());
    proyecto = Proyecto.builder().id(1L).build();
    presupuesto = PresupuestoProyecto.builder().id(2L).proyecto(proyecto).periodosEstimados(3).build();
    when(proyectos.findById(1L)).thenReturn(Optional.of(proyecto));
    when(presupuestos.findByProyectoId(1L)).thenReturn(Optional.of(presupuesto));
    when(macros.findByPresupuestoIdOrderByNumeroProductoAscIdAsc(2L)).thenReturn(List.of());
    when(componentes.findByProyectoIdOrderByIdAsc(1L)).thenReturn(List.of(
        Componente.builder().id(10L).proyecto(proyecto).nombre("TC-1").codigoProducto("P1").build(),
        Componente.builder().id(11L).proyecto(proyecto).nombre("TC-2").codigoProducto("P2").build()));
  }

  @Test
  void obtienePresupuestoExistenteConProductos() {
    PresupuestoDto response = service.obtener(1L);

    assertThat(response.getIdProyecto()).isEqualTo(1L);
    assertThat(response.getPeriodosEstimados()).isEqualTo(3);
    assertThat(response.getProductos()).hasSize(2);
    verify(macros).findByPresupuestoIdOrderByNumeroProductoAscIdAsc(2L);
  }

  @Test
  void obtienePresupuestoLoCreaCuandoNoExiste() {
    when(presupuestos.findByProyectoId(1L)).thenReturn(Optional.empty());
    when(presupuestos.save(any(PresupuestoProyecto.class))).thenReturn(presupuesto);

    PresupuestoDto response = service.obtener(1L);

    assertThat(response).isNotNull();
    verify(presupuestos).save(any(PresupuestoProyecto.class));
  }

  @Test
  void periodosActualizaCantidadValida() {
    PresupuestoDto response = service.periodos(1L, new ConfigurarPeriodosEjecucionRequestDto(6));

    assertThat(presupuesto.getPeriodosEstimados()).isEqualTo(6);
    assertThat(response.getPeriodosEstimados()).isEqualTo(6);
  }

  @Test
  void periodosRechazaValorNuloONegativo() {
    ConfigurarPeriodosEjecucionRequestDto periodosNulos = new ConfigurarPeriodosEjecucionRequestDto(null);
    ConfigurarPeriodosEjecucionRequestDto periodosNegativos = new ConfigurarPeriodosEjecucionRequestDto(-1);

    assertThatThrownBy(() -> service.periodos(1L, periodosNulos))
        .isInstanceOf(ValidacionNegocioException.class);
    assertThatThrownBy(() -> service.periodos(1L, periodosNegativos))
        .isInstanceOf(ValidacionNegocioException.class);
  }

  @Test
  void registraMacroactividadConInsumosYCalculaTotales() {
    MacroactividadRequestDto request = new MacroactividadRequestDto("  Macro  ")
        .insumos(List.of(new MacroactividadInsumoRequestDto("PERSONAL")
        .costosPorPeriodo(java.util.Arrays.asList(10.0, null, 5.0))));
    when(macros.save(any(MacroactividadPresupuesto.class))).thenAnswer(invocation -> {
      MacroactividadPresupuesto saved = invocation.getArgument(0);
      saved.setId(8L);
      return saved;
    });
    when(macros.countByPresupuestoIdAndNumeroProducto(2L, 1)).thenReturn(1L);

    var response = service.registrar(1L, 1, request);

    assertThat(response.getNombreMacroactividad()).isEqualTo("Macro");
    assertThat(response.getNumero()).isEqualTo("1.1");
    assertThat(response.getTotalPeriodoPrecioMercado()).containsExactly(10.0, 0.0, 5.0);
    verify(macros).save(any(MacroactividadPresupuesto.class));
  }

  @Test
  void registraMacroactividadRechazaInsumosSinCosto() {
    MacroactividadRequestDto request = new MacroactividadRequestDto("Macro")
        .insumos(List.of(new MacroactividadInsumoRequestDto("PERSONAL").costosPorPeriodo(List.of())));

    assertThatThrownBy(() -> service.registrar(1L, 1, request))
        .isInstanceOf(ValidacionNegocioException.class);
    verify(macros, never()).save(any());
  }

  @Test
  void guardaPresupuestoRechazaProductoSinMacroactividad() {
    when(macros.countByPresupuestoIdAndNumeroProducto(2L, 1)).thenReturn(0L);

    assertThatThrownBy(() -> service.guardar(1L))
        .isInstanceOf(ValidacionNegocioException.class);
  }

  @Test
  void guardaPresupuestoAceptaMacroactividadPorCadaProducto() {
    when(macros.countByPresupuestoIdAndNumeroProducto(2L, 1)).thenReturn(1L);
    when(macros.countByPresupuestoIdAndNumeroProducto(2L, 2)).thenReturn(2L);

    assertThat(service.guardar(1L).getIdProyecto()).isEqualTo(1L);
  }

  @Test
  void obtieneFuentesYGuardaFuentesNormalizadas() {
    presupuesto.setFuentesFinanciamiento(List.of(FuenteFinanciamiento.FONDO_GENERAL));
    presupuesto.setFuenteRecursos(" anterior ");

    assertThat(service.fuentes(1L).getFuenteRecursos()).isEqualTo(" anterior ");
    FuentesFinanciamientoRequestDto request = new FuentesFinanciamientoRequestDto()
        .fuentesFinanciamiento(List.of(FuenteFinanciamientoDto.DONACIONES))
        .fuenteRecursos("  presupuesto nacional  ");

    var response = service.guardarFuentes(1L, request);

    assertThat(presupuesto.getFuenteRecursos()).isEqualTo("presupuesto nacional");
    assertThat(response.getFuentesFinanciamiento()).containsExactly(FuenteFinanciamientoDto.DONACIONES);
    verify(presupuestos).save(presupuesto);
  }

  @Test
  void fuentesNoRequiereFichaDeEmergenciaYGuardarFuentesRechazaDatosInvalidos() {
    // Proyecto normal (sin FichaEmergencia, CU-PRE-3.5): antes respondía 404 "No existe ficha de
    // proyecto" para cualquier proyecto que no fuera de emergencia; ya no debe fallar.
    assertThat(service.fuentes(1L)).isNotNull();

    FuentesFinanciamientoRequestDto requestVacio = new FuentesFinanciamientoRequestDto();
    assertThatThrownBy(() -> service.guardarFuentes(1L, requestVacio))
        .isInstanceOf(ValidacionNegocioException.class);

    FuentesFinanciamientoRequestDto requestSinFuenteRecursos = new FuentesFinanciamientoRequestDto()
        .fuentesFinanciamiento(List.of(FuenteFinanciamientoDto.OTROS));
    assertThatThrownBy(() -> service.guardarFuentes(1L, requestSinFuenteRecursos))
        .isInstanceOf(ValidacionNegocioException.class);
  }
}
