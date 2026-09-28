package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
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

    @Test
    void configurarRechazaTipoDeCostoNulo() {
        Map<String, Object> datos = new HashMap<>();
        datos.put("tipoCosto", null);

        assertThatThrownBy(() -> service.configurar(7L, datos)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    void configurarExigeTasaAunqueVengaLaVidaUtil() {
        Map<String, Object> datos = Map.of("tipoCosto", "MANTENIMIENTO", "vidaUtil", 2);

        assertThatThrownBy(() -> service.configurar(7L, datos)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    void configurarRechazaTasaNegativa() {
        Map<String, Object> datos = Map.of("tipoCosto", "OPERACION", "vidaUtil", 2, "tasaCrecimientoCostos", -0.5D);

        assertThatThrownBy(() -> service.configurar(7L, datos)).isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    void configurarActualizaLaConfiguracionExistenteConVidaUtilYTasa() {
        PresupuestoOmConfiguracion existente = PresupuestoOmConfiguracion.builder().tipoCosto("NO_APLICA").build();
        when(configuraciones.findByProyectoId(7L)).thenReturn(Optional.of(existente));
        when(actividades.findByProyectoId(7L)).thenReturn(List.of());

        Map<String, Object> resultado = service.configurar(7L,
                Map.of("tipoCosto", "OPERACION", "vidaUtil", 2, "tasaCrecimientoCostos", 1.5D));

        assertThat(existente.getTipoCosto()).isEqualTo("OPERACION");
        assertThat(existente.getVidaUtil()).isEqualTo(2);
        assertThat(existente.getTasaCrecimientoCostos()).isEqualTo(1.5D);
        verify(configuraciones).save(existente);
        // El Técnico URP no es usuario interno: no recibe precios ajustados (RN09).
        assertThat(resultado).containsKeys("costosOperacion", "totalInversionPrecioMercado")
                .doesNotContainKeys("costosMantenimiento", "totalInversionPrecioAjustado");
    }

    @Test
    void registroRechazaNombreDeActividadNuloOEnBlanco() {
        Map<String, Object> sinNombre = Map.of("insumos", List.of());
        Map<String, Object> nombreEnBlanco = Map.of("nombreActividad", "  ");

        assertThatThrownBy(() -> service.registrarActividad(7L, "OPERACION", sinNombre))
                .isInstanceOf(ValidacionNegocioException.class).hasMessage("Actividad inválida");
        assertThatThrownBy(() -> service.registrarActividad(7L, "OPERACION", nombreEnBlanco))
                .isInstanceOf(ValidacionNegocioException.class).hasMessage("Actividad inválida");
    }

    @Test
    void registroIgnoraInsumosIncompletosYExigeAlMenosUnoValido() {
        Map<String, Object> datos = Map.of("nombreActividad", "Seguro", "insumos", List.of(
                "no es un mapa",
                Map.of("insumoTipoCodigo", 15, "costoPeriodo1PrecioMercado", 100D),
                Map.of("insumoTipoCodigo", " ", "costoPeriodo1PrecioMercado", 100D),
                Map.of("insumoTipoCodigo", "Mano de obra", "costoPeriodo1PrecioMercado", "cien")));

        assertThatThrownBy(() -> service.registrarActividad(7L, "OPERACION", datos))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessage("Debe registrar el costo de al menos un insumo");
    }

    @Test
    void eliminarActividadInexistenteLanzaRecursoNoEncontrado() {
        when(actividades.findByIdAndProyectoId(3L, 7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.eliminarActividad(7L, "OPERACION", 3L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void eliminarActividadDeOtraTablaLanzaRecursoNoEncontrado() {
        ActividadOm actividad = actividadCon("MANTENIMIENTO", "Pintura", 50D, 1D);
        when(actividades.findByIdAndProyectoId(3L, 7L)).thenReturn(Optional.of(actividad));

        assertThatThrownBy(() -> service.eliminarActividad(7L, "OPERACION", 3L))
                .isInstanceOf(RecursoNoEncontradoException.class);
        verify(actividades, never()).delete(any());
    }

    @Test
    void eliminarActividadDeLaTablaIndicadaLaBorra() {
        ActividadOm actividad = actividadCon("OPERACION", "Vigilancia", 50D, 1D);
        when(actividades.findByIdAndProyectoId(3L, 7L)).thenReturn(Optional.of(actividad));

        service.eliminarActividad(7L, "operacion", 3L);

        verify(actividades).delete(actividad);
    }

    @Test
    void guardarSinConfiguracionDevuelveSoloElIdDelProyecto() {
        when(configuraciones.findByProyectoId(7L)).thenReturn(Optional.empty());

        assertThat(service.guardar(7L)).containsOnlyKeys("idProyecto");
    }

    @Test
    void guardarConConfiguracionSinTipoDeCostoDevuelveSoloElIdDelProyecto() {
        when(configuraciones.findByProyectoId(7L)).thenReturn(Optional.of(PresupuestoOmConfiguracion.builder()
                .vidaUtil(2).build()));

        assertThat(service.guardar(7L)).containsOnlyKeys("idProyecto");
    }

    @Test
    void obtenerConNoAplicaNoIncluyeTablasNiTotales() {
        when(configuraciones.findByProyectoId(7L)).thenReturn(Optional.of(PresupuestoOmConfiguracion.builder()
                .tipoCosto("NO_APLICA").build()));
        when(actividades.findByProyectoId(7L)).thenReturn(List.of());

        assertThat(service.obtener(7L)).containsOnlyKeys("idProyecto", "tipoCosto", "vidaUtil",
                "tasaCrecimientoCostos");
    }

    @Test
    void obtenerConSoloMantenimientoYSinTasaProyectaCostoConstante() {
        when(configuraciones.findByProyectoId(7L)).thenReturn(Optional.of(PresupuestoOmConfiguracion.builder()
                .tipoCosto("MANTENIMIENTO").vidaUtil(2).build()));
        when(actividades.findByProyectoId(7L)).thenReturn(List.of(actividadCon("MANTENIMIENTO", "Pintura", 50D, 1D)));

        Map<String, Object> resultado = service.obtener(7L);

        assertThat(resultado).doesNotContainKey("costosOperacion").containsKey("costosMantenimiento")
                .containsEntry("totalInversionPrecioMercado", Map.of("porPeriodo", List.of(50D, 50D), "total", 100D));
    }

    @Test
    void vidaUtilDevuelveCeroSinConfiguracionOSinValor() {
        when(configuraciones.findByProyectoId(7L)).thenReturn(Optional.empty());
        when(configuraciones.findByProyectoId(8L)).thenReturn(Optional.of(new PresupuestoOmConfiguracion()));
        when(configuraciones.findByProyectoId(9L))
                .thenReturn(Optional.of(PresupuestoOmConfiguracion.builder().vidaUtil(4).build()));

        assertThat(service.vidaUtil(7L)).isZero();
        assertThat(service.vidaUtil(8L)).isZero();
        assertThat(service.vidaUtil(9L)).isEqualTo(4);
    }

    @Test
    void costosPorTipoSinConfiguracionDevuelveListasVacias() {
        when(configuraciones.findByProyectoId(7L)).thenReturn(Optional.empty());
        when(actividades.findByProyectoId(7L)).thenReturn(List.of(actividadCon("OPERACION", "Vigilancia", 10D, 1D)));

        PresupuestoOmService.CostosPorTipo costos =
                service.costosPorTipo(7L, PresupuestoOmService.TIPO_COSTO_OPERACION);

        assertThat(costos.actividades()).hasSize(1);
        assertThat(costos.mercado()).isEmpty();
        assertThat(costos.ajustado()).isEmpty();
    }

    @Test
    void obtenerRechazaProyectoSinUnidadEjecutoraParaActorConUnidad() {
        Usuario usuarioConUnidad = Usuario.builder().rol(RolUsuario.TECNICO_PRE)
                .unidadEjecutora(UnidadEjecutora.builder().id(10L).build()).build();
        when(actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE)).thenReturn(usuarioConUnidad);

        assertThatThrownBy(() -> service.obtener(7L)).isInstanceOf(AccesoDenegadoException.class);
    }

    @Test
    void guardarComoTecnicoUrpDeLaMismaUnidadOmiteLosPreciosAjustados() {
        UnidadEjecutora unidad = UnidadEjecutora.builder().id(10L).build();
        when(actor.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(Usuario.builder().rol(RolUsuario.TECNICO_URP)
                .unidadEjecutora(unidad).build());
        when(proyectos.findById(7L)).thenReturn(Optional.of(Proyecto.builder().id(7L).unidadEjecutora(unidad).build()));
        when(configuraciones.findByProyectoId(7L)).thenReturn(Optional.of(PresupuestoOmConfiguracion.builder()
                .tipoCosto("OPERACION").vidaUtil(1).tasaCrecimientoCostos(0D).build()));
        when(actividades.findByProyectoId(7L)).thenReturn(List.of(actividadCon("OPERACION", "Vigilancia", 100D, 1.2D)));

        Map<String, Object> resultado = service.guardar(7L);

        @SuppressWarnings("unchecked")
        Map<String, Object> operacion = (Map<String, Object>) resultado.get("costosOperacion");
        @SuppressWarnings("unchecked")
        Map<String, Object> actividad = ((List<Map<String, Object>>) operacion.get("actividades")).get(0);
        @SuppressWarnings("unchecked")
        Map<String, Object> insumo = ((List<Map<String, Object>>) actividad.get("insumos")).get(0);
        assertThat(actividad).containsEntry("totalPeriodo1PrecioMercado", 100D)
                .doesNotContainKey("totalPeriodo1PrecioAjustado");
        assertThat(insumo).containsEntry("costoPeriodo1PrecioMercado", 100D)
                .doesNotContainKey("costoPeriodo1PrecioAjustado");
        assertThat(operacion).doesNotContainKey("totalPorPeriodoPrecioAjustado");
    }

    @Test
    void obtenerProyectoInexistenteLanzaRecursoNoEncontrado() {
        when(proyectos.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener(7L)).isInstanceOf(RecursoNoEncontradoException.class);
    }
}
