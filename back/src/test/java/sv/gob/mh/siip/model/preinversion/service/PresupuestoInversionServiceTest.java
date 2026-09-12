package sv.gob.mh.siip.model.preinversion.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.*;
import sv.gob.mh.siip.model.preinversion.repository.*;
import sv.gob.mh.siip.security.ActorContexto;

class PresupuestoInversionServiceTest {
  private ProyectoRepository proyectos; private FichaEmergenciaRepository fichas;
  private PresupuestoInversionService service;
  @BeforeEach void setup(){
    proyectos=mock(ProyectoRepository.class); fichas=mock(FichaEmergenciaRepository.class);
    PresupuestoProyectoRepository presupuestos=mock(PresupuestoProyectoRepository.class);
    MacroactividadPresupuestoRepository macros=mock(MacroactividadPresupuestoRepository.class);
    ActorContexto actor=mock(ActorContexto.class);
    service=new PresupuestoInversionService(proyectos,fichas,presupuestos,macros,actor,new ObjectMapper());
  }
  @Test void rechazaPeriodosNegativos(){
    var request = new ConfigurarPeriodosEjecucionRequestDto(-1);
    assertThrows(ValidacionNegocioException.class, () -> service.periodos(1L,request));
  }
  @Test void rechazaMacroactividadSinNombre(){
    when(proyectos.findById(1L)).thenReturn(Optional.of(new Proyecto()));
    var request = new MacroactividadRequestDto(" ");
    assertThrows(ValidacionNegocioException.class, () -> service.registrar(1L,1,request));
  }
  @Test void rechazaFuentesVacias(){
    when(fichas.findByProyectoId(1L)).thenReturn(Optional.empty());
    var request = new FuentesFinanciamientoRequestDto();
    assertThrows(RuntimeException.class, () -> service.guardarFuentes(1L,request));
  }
}
