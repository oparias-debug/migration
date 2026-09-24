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
import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.preinversion.domain.ActividadOm;
import sv.gob.mh.siip.model.preinversion.domain.InsumoActividad;
import sv.gob.mh.siip.model.preinversion.domain.InsumoTipo;
import sv.gob.mh.siip.model.preinversion.domain.PresupuestoOmConfiguracion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
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
    private ActorContexto actor;
    private PresupuestoOmService service;

    @BeforeEach
    void preparar() {
        proyectos = mock(ProyectoRepository.class);
        configuraciones = mock(PresupuestoOmConfiguracionRepository.class);
        actividades = mock(ActividadOmRepository.class);
        insumosTipo = mock(InsumoTipoRepository.class);
        actor = mock(ActorContexto.class);
        Usuario usuario = Usuario.builder().rol(RolUsuario.TECNICO_URP).build();
        // Consulta como Técnico PRE (usuario interno, RN09) para poder verificar los precios ajustados.
        when(actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE))
                .thenReturn(Usuario.builder().rol(RolUsuario.TECNICO_PRE).build());
        when(actor.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(usuario);
        when(proyectos.findById(7L)).thenReturn(Optional.of(Proyecto.builder().id(7L).build()));
        service = new PresupuestoOmService(proyectos, configuraciones, actividades, insumosTipo, actor);
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
                .tipoCosto("O_M").vidaUtil(3).tasaCrecimientoCostos(3D).build()));
        when(actividades.findByProyectoId(7L)).thenReturn(List.of(
                actividadCon("OPERACION", "Vigilancia", 100D, 1.2D),
                actividadCon("MANTENIMIENTO", "Pintura", 50D, 1D)));

        Map<String, Object> resultado = service.obtener(7L);

        @SuppressWarnings("unchecked")
        Map<String, Object> operacion = (Map<String, Object>) resultado.get("costosOperacion");
        Map<String, Object> totalMercado = (Map<String, Object>) operacion.get("totalPorPeriodoPrecioMercado");
        assertThat((List<Double>) totalMercado.get("porPeriodo")).containsExactly(100D, 103D, 106.09D);
        Map<String, Object> totalAjustado = (Map<String, Object>) operacion.get("totalPorPeriodoPrecioAjustado");
        assertThat((List<Double>) totalAjustado.get("porPeriodo")).containsExactly(120D, 123.6D, 127.31D);

        @SuppressWarnings("unchecked")
        Map<String, Object> mantenimiento = (Map<String, Object>) resultado.get("costosMantenimiento");
        Map<String, Object> totalMantenimiento = (Map<String, Object>) mantenimiento.get("totalPorPeriodoPrecioMercado");
        assertThat((List<Double>) totalMantenimiento.get("porPeriodo")).containsExactly(50D, 51.5D, 53.04D);
        Map<String, Object> totalInversion = (Map<String, Object>) resultado.get("totalInversionPrecioMercado");
        assertThat(totalInversion).containsEntry("porPeriodo", List.of(150D, 154.5D, 159.13D))
                .containsEntry("total", 465D);
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
        when(configuraciones.findByProyectoId(7L)).thenReturn(Optional.empty());
        when(configuraciones.save(any(PresupuestoOmConfiguracion.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Map<String, Object> resultado = service.configurar(7L, Map.of("tipoCosto", "NO_APLICA"));

        assertThat(resultado).containsEntry("idProyecto", 7L)
                .doesNotContainKeys("costosOperacion", "costosMantenimiento",
                        "totalInversionPrecioMercado", "totalInversionPrecioAjustado");
    }

    @Test
    void configurarRechazaTasaFueraDelRangoDeCeroATresPorciento() {
        Map<String, Object> datos = Map.of("tipoCosto", "OPERACION", "vidaUtil", 2, "tasaCrecimientoCostos", 3.01D);

        assertThatThrownBy(() -> service.configurar(7L, datos)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    void obtenerRechazaProyectoFueraDelAlcanceDeLaUnidadEjecutora() {
        Usuario usuarioOtraUnidad = Usuario.builder().rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(UnidadEjecutora.builder().id(10L).build()).build();
        when(actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE)).thenReturn(usuarioOtraUnidad);
        when(proyectos.findById(7L)).thenReturn(Optional.of(Proyecto.builder().id(7L)
                .unidadEjecutora(UnidadEjecutora.builder().id(20L).build()).build()));

        assertThatThrownBy(() -> service.obtener(7L)).isInstanceOf(AccesoDenegadoException.class);
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
