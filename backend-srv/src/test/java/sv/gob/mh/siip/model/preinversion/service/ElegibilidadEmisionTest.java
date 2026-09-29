package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.CalificacionCriterioElegibilidad;
import sv.gob.mh.siip.model.preinversion.domain.CriterioElegibilidad;
import sv.gob.mh.siip.model.preinversion.domain.Elegibilidad;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.repository.CalificacionCriterioElegibilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.ElegibilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;

class ElegibilidadEmisionTest {

  private ElegibilidadRepository elegibilidades;
  private CalificacionCriterioElegibilidadRepository calificaciones;
  private UsuarioRepository usuarios;
  private NotificacionService notificaciones;
  private ElegibilidadEmision emision;
  private Proyecto proyecto;
  private Usuario viabilizador;

  @BeforeEach
  void setUp() {
    elegibilidades = mock(ElegibilidadRepository.class);
    calificaciones = mock(CalificacionCriterioElegibilidadRepository.class);
    usuarios = mock(UsuarioRepository.class);
    notificaciones = mock(NotificacionService.class);
    emision = new ElegibilidadEmision(mock(ProyectoRepository.class), elegibilidades, calificaciones, usuarios,
        notificaciones, mock(FiltrosPosterioresViabilidad.class));
    proyecto = Proyecto.builder().id(1L).estado(EstadoProyecto.VIABLE)
        .unidadEjecutora(UnidadEjecutora.builder().id(3L).build()).build();
    viabilizador = Usuario.builder().id(9L).rol(RolUsuario.VIABILIZADOR).build();
  }

  @Test
  void emitir_primeraVezSinCriteriosQueApliquen_dejaCriteriosCumplidosVacio() {
    when(calificaciones.findByProyectoId(1L)).thenReturn(List.of(calificacion("ELEG-01", 1, false)));

    emision.emitir(new ElegibilidadContexto(viabilizador, proyecto, null, true));

    ArgumentCaptor<Elegibilidad> guardada = ArgumentCaptor.forClass(Elegibilidad.class);
    verify(elegibilidades).save(guardada.capture());
    assertThat(guardada.getValue().getCriteriosCumplidos()).isNull();
    assertThat(proyecto.getEstado()).isEqualTo(EstadoProyecto.ELEGIBLE);
    verify(usuarios).findByRolAndUnidadEjecutora_IdAndActivoTrue(RolUsuario.TECNICO_URP, 3L);
    verify(notificaciones).notificarEmisionElegibilidad(eq(proyecto), anyList());
    verify(notificaciones, never()).notificarObservacionesElegibilidadAtendidas(any(), anyList());
  }

  @Test
  void emitir_recortaLosCriteriosCumplidosALaLongitudDeLaColumna() {
    List<CalificacionCriterioElegibilidad> muchas = IntStream.range(0, 300)
        .mapToObj(i -> calificacion("CRITERIO-%04d".formatted(i), i, true))
        .toList();
    when(calificaciones.findByProyectoId(1L)).thenReturn(muchas);

    emision.emitir(new ElegibilidadContexto(viabilizador, proyecto, null, true));

    ArgumentCaptor<Elegibilidad> guardada = ArgumentCaptor.forClass(Elegibilidad.class);
    verify(elegibilidades).save(guardada.capture());
    assertThat(guardada.getValue().getCriteriosCumplidos())
        .hasSize(ElegibilidadEmision.LONGITUD_CRITERIOS_CUMPLIDOS)
        .startsWith("CRITERIO-0000,CRITERIO-0001");
  }

  private static CalificacionCriterioElegibilidad calificacion(String codigo, int orden, boolean aplica) {
    CriterioElegibilidad criterio = CriterioElegibilidad.builder().id((long) orden + 1).codigo(codigo)
        .numeroDimension(1).orden(orden).build();
    return CalificacionCriterioElegibilidad.builder().criterio(criterio).aplica(aplica).build();
  }
}
