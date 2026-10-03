package sv.gob.mh.siip.model.preinversion.service;

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
import org.mockito.AdditionalAnswers;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.Componente;
import sv.gob.mh.siip.model.preinversion.domain.IndicadorProyecto;
import sv.gob.mh.siip.model.preinversion.domain.ProductoIndicadorCatalogo;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.IndicadorProductoRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.ComponenteRepository;
import sv.gob.mh.siip.model.preinversion.repository.IdentificacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.IndicadorProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.IndicadorResultadoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProductoIndicadorCatalogoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.PresupuestoProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/** Reglas de negocio críticas de CU-PRE-23 contra los productos de CU-PRE-11. */
class IndicadoresProyectoServiceTest {
    private ProyectoRepository proyectos;
    private ComponenteRepository componentes;
    private IndicadorProyectoRepository indicadores;
    private ProductoIndicadorCatalogoRepository catalogoProductos;
    private ActorContexto actor;
    private IndicadoresProyectoService service;

    @BeforeEach
    void preparar() {
        proyectos = mock(ProyectoRepository.class);
        componentes = mock(ComponenteRepository.class);
        indicadores = mock(IndicadorProyectoRepository.class);
        catalogoProductos = mock(ProductoIndicadorCatalogoRepository.class);
        actor = mock(ActorContexto.class);
        when(actor.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(Usuario.builder().rol(RolUsuario.TECNICO_URP).build());
        service = new IndicadoresProyectoService(proyectos, componentes, indicadores,
                mock(IndicadorResultadoRepository.class), catalogoProductos, mock(IdentificacionRepository.class),
                mock(PresupuestoProyectoRepository.class), mock(PresupuestoInversionEnsamblador.class), actor);
    }

    @Test
    void rechazaIndicadorParaComponenteDeOtroProyecto() {
        Proyecto proyecto = Proyecto.builder().id(7L).build();
        when(proyectos.findById(7L)).thenReturn(Optional.of(proyecto));
        when(componentes.findById(4L)).thenReturn(Optional.of(Componente.builder()
                .id(4L).proyecto(Proyecto.builder().id(9L).build()).codigoProducto("P-01").cantidad(10D).build()));

        assertThatThrownBy(() -> service.registrarProducto(7L, 4L, solicitud(10D, List.of(10D))))
                .isInstanceOf(RecursoNoEncontradoException.class);
        verify(indicadores, never()).save(any());
    }

    @Test
    void rechazaCuandoLaSumaDePeriodosNoCoincideConMetaGlobal() {
        Proyecto proyecto = Proyecto.builder().id(7L).build();
        Componente componente = Componente.builder().id(4L).proyecto(proyecto).codigoProducto("P-01").cantidad(10D).build();
        when(proyectos.findById(7L)).thenReturn(Optional.of(proyecto));
        when(componentes.findById(4L)).thenReturn(Optional.of(componente));
        when(catalogoProductos.findByCodigoProductoAndCodigoIndicador("P-01", "I-01"))
                .thenReturn(Optional.of(ProductoIndicadorCatalogo.builder().codigoProducto("P-01")
                        .codigoIndicador("I-01").indicador("Indicador").unidadMedida("Unidad")
                        .esIndicadorPrincipal(true).build()));

        assertThatThrownBy(() -> service.registrarProducto(7L, 4L, solicitud(10D, List.of(7D, 2D))))
                .isInstanceOf(ValidacionNegocioException.class).hasMessageContaining("La suma de los períodos");
        verify(indicadores, never()).save(any());
    }

    @Test
    void guardarExigeIndicadorPorCadaProductoDeCuPre11() {
        Proyecto proyecto = Proyecto.builder().id(7L).build();
        Componente componente = Componente.builder().id(4L).proyecto(proyecto).codigoProducto("P-01").cantidad(10D).build();
        when(proyectos.findById(7L)).thenReturn(Optional.of(proyecto));
        when(indicadores.findByProyectoIdAndTipo(7L, "RESULTADO"))
                .thenReturn(List.of(IndicadorProyecto.builder().id(1L).build()));
        when(componentes.findByProyectoIdOrderByIdAsc(7L)).thenReturn(List.of(componente));
        when(indicadores.findByComponenteId(4L)).thenReturn(List.of());

        assertThatThrownBy(() -> service.guardar(7L)).isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("cada producto");
    }

    private static IndicadorProductoRequestDto solicitud(double meta, List<Double> periodos) {
        return new IndicadorProductoRequestDto("I-01", meta, true).metasPorPeriodo(periodos);
    }
}
