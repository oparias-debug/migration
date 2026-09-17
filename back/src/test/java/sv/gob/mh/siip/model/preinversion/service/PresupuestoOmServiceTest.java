package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.ActividadOm;
import sv.gob.mh.siip.model.preinversion.domain.InsumoActividad;
import sv.gob.mh.siip.model.preinversion.domain.InsumoTipo;
import sv.gob.mh.siip.model.preinversion.domain.PresupuestoOmConfiguracion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.repository.ActividadOmRepository;
import sv.gob.mh.siip.model.preinversion.repository.InsumoTipoRepository;
import sv.gob.mh.siip.model.preinversion.repository.PresupuestoOmConfiguracionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

class PresupuestoOmServiceTest {
    private ProyectoRepository proyectos;
    private PresupuestoOmConfiguracionRepository configuraciones;
    private ActividadOmRepository actividades;
    private InsumoTipoRepository insumosTipo;
    private PresupuestoOmService service;

    @BeforeEach
    void preparar() {
        proyectos = mock(ProyectoRepository.class);
        configuraciones = mock(PresupuestoOmConfiguracionRepository.class);
        actividades = mock(ActividadOmRepository.class);
        insumosTipo = mock(InsumoTipoRepository.class);
        service = new PresupuestoOmService(proyectos, configuraciones, actividades, insumosTipo, mock(ActorContexto.class));
    }

    private static ActividadOm actividadCon(String tipoCostoTabla, String nombre, double costo, double factorCorreccion) {
        return ActividadOm.builder().tipoCostoTabla(tipoCostoTabla).nombreActividad(nombre)
                .insumos(List.of(InsumoActividad.builder().insumoTipoCodigo("X").insumoTipoNombre("X")
                        .costoPeriodo1PrecioMercado(costo).factorCorreccion(factorCorreccion).build()))
                .build();
    }

    @Test
    void obtenerProyectaCostosDeOperacionYMantenimientoPorSeparadoSegunRn06() {
        when(configuraciones.findByProyectoId(7L)).thenReturn(Optional.of(PresupuestoOmConfiguracion.builder()
                .vidaUtil(3).tasaCrecimientoCostos(10D).build()));
        when(actividades.findByProyectoId(7L)).thenReturn(List.of(
                actividadCon("OPERACION", "Vigilancia", 100D, 1.2D),
                actividadCon("MANTENIMIENTO", "Pintura", 50D, 1D)));

        Map<String, Object> resultado = service.obtener(7L);

        @SuppressWarnings("unchecked")
        Map<String, Object> operacion = (Map<String, Object>) resultado.get("operacion");
        assertThat((List<Double>) operacion.get("totalPorPeriodoPrecioMercado")).containsExactly(100D, 110D, 121D);
        assertThat((List<Double>) operacion.get("totalPorPeriodoPrecioAjustado")).containsExactly(120D, 132D, 145.2D);

        @SuppressWarnings("unchecked")
        Map<String, Object> mantenimiento = (Map<String, Object>) resultado.get("mantenimiento");
        assertThat((List<Double>) mantenimiento.get("totalPorPeriodoPrecioMercado")).containsExactly(50D, 55D, 60.5D);
    }

    @Test
    void costosPorTipoUsadoPorCuPre21DevuelveSoloLasActividadesDeEsaTabla() {
        when(configuraciones.findByProyectoId(7L)).thenReturn(Optional.of(PresupuestoOmConfiguracion.builder().vidaUtil(1).build()));
        when(actividades.findByProyectoId(7L)).thenReturn(List.of(
                actividadCon("OPERACION", "Vigilancia", 100D, 1D),
                actividadCon("MANTENIMIENTO", "Pintura", 50D, 1D)));

        PresupuestoOmService.CostosPorTipo operacion = service.costosPorTipo(7L, PresupuestoOmService.TIPO_COSTO_OPERACION);

        assertThat(operacion.actividades()).extracting(ActividadOm::getNombreActividad).containsExactly("Vigilancia");
        assertThat(operacion.mercado()).containsExactly(100D);
    }

    @Test
    void configurarRechazaTipoDeCostoInvalido() {
        Map<String, Object> datos = Map.of("tipoCosto", "INVALIDO");

        assertThatThrownBy(() -> service.configurar(7L, datos)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    void configurarExigeVidaUtilYTasaCuandoAplicaUnTipoDeCosto() {
        Map<String, Object> datos = Map.of("tipoCosto", "OPERACION");

        assertThatThrownBy(() -> service.configurar(7L, datos)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    void configurarPermiteNoAplicaSinVidaUtilNiTasa() {
        Proyecto proyecto = Proyecto.builder().id(7L).build();
        when(proyectos.findById(7L)).thenReturn(Optional.of(proyecto));
        when(configuraciones.findByProyectoId(7L)).thenReturn(Optional.empty());
        when(configuraciones.save(any(PresupuestoOmConfiguracion.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Map<String, Object> resultado = service.configurar(7L, Map.of("tipoCosto", "NO_APLICA"));

        assertThat(resultado.get("idProyecto")).isEqualTo(7L);
    }

    @Test
    void registroRechazaActividadSinInsumos() {
        Map<String, Object> datos = Map.of("nombreActividad", "Seguro");

        assertThatThrownBy(() -> service.registrarActividad(7L, "OPERACION", datos))
                .isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    void registroRechazaInsumoTipoDesconocido() {
        when(insumosTipo.findByCodigo("Inexistente")).thenReturn(Optional.empty());
        Map<String, Object> datos = Map.of("nombreActividad", "Seguro",
                "insumos", List.of(Map.of("insumoTipoCodigo", "Inexistente", "costoPeriodo1PrecioMercado", 100D)));

        assertThatThrownBy(() -> service.registrarActividad(7L, "OPERACION", datos))
                .isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    void registroSumaLosInsumosAplicandoElFactorDeCorreccionDeCadaUno() {
        Proyecto proyecto = Proyecto.builder().id(7L).build();
        when(proyectos.findById(7L)).thenReturn(Optional.of(proyecto));
        when(actividades.save(any(ActividadOm.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
        when(insumosTipo.findByCodigo("Mano de obra calificada")).thenReturn(Optional.of(
                InsumoTipo.builder().codigo("Mano de obra calificada").nombre("Mano de obra calificada").factorCorreccion(0.86D).build()));
        when(insumosTipo.findByCodigo("Consultoría y servicios profesionales")).thenReturn(Optional.of(
                InsumoTipo.builder().codigo("Consultoría y servicios profesionales").nombre("Consultoría y servicios profesionales").factorCorreccion(1D).build()));

        ActividadOm actividad = service.registrarActividad(7L, "OPERACION", Map.of(
                "nombreActividad", "Seguro",
                "insumos", List.of(
                        Map.of("insumoTipoCodigo", "Mano de obra calificada", "costoPeriodo1PrecioMercado", 500D),
                        Map.of("insumoTipoCodigo", "Consultoría y servicios profesionales", "costoPeriodo1PrecioMercado", 100D))));

        assertThat(actividad.getCostoPeriodo1PrecioMercado()).isEqualTo(600D);
        assertThat(actividad.getCostoPeriodo1PrecioAjustado()).isEqualTo(530D);
        assertThat(actividad.getTipoCostoTabla()).isEqualTo("OPERACION");
    }
}
