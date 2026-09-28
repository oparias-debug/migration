package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.databind.ObjectMapper;

import sv.gob.mh.siip.model.common.service.OpenApiDtoMapper;
import sv.gob.mh.siip.model.preinversion.domain.ActividadOm;
import sv.gob.mh.siip.model.preinversion.om.dto.ActividadDto;
import sv.gob.mh.siip.model.preinversion.om.dto.ActividadRequestDto;
import sv.gob.mh.siip.model.preinversion.om.dto.ConfigurarPresupuestoOMRequestDto;
import sv.gob.mh.siip.model.preinversion.om.dto.PresupuestoOMDto;
import sv.gob.mh.siip.model.preinversion.om.dto.TipoCostoTablaDto;

class PresupuestoOmApiServiceTest {

    private static final Long ID_PROYECTO = 7L;
    private static final Long ID_ACTIVIDAD = 11L;
    private static final String COSTOS_OPERACION = "costosOperacion";
    private static final String COSTOS_MANTENIMIENTO = "costosMantenimiento";
    private static final String ACTIVIDADES = "actividades";
    private static final String LIMPIEZA = "Limpieza";

    private final PresupuestoOmService servicioDominio = mock(PresupuestoOmService.class);
    private final PresupuestoOmApiService servicio = new PresupuestoOmApiService(servicioDominio,
            new OpenApiDtoMapper(new ObjectMapper()));

    private static Map<String, Object> actividad(Long id, String nombre) {
        return Map.of("idActividad", id, "numero", 1, "nombreActividad", nombre, "insumos", List.of());
    }

    private void prepararRegistro(TipoCostoTablaDto tabla, Map<String, Object> presupuesto) {
        when(servicioDominio.registrarActividad(eq(ID_PROYECTO), eq(tabla.getValue()), anyMap()))
                .thenReturn(ActividadOm.builder().id(ID_ACTIVIDAD).build());
        when(servicioDominio.obtener(ID_PROYECTO)).thenReturn(presupuesto);
    }

    @Test
    void obtenerConvierteLaFormaContractualCompletaAlDtoGenerado() {
        when(servicioDominio.obtener(ID_PROYECTO)).thenReturn(Map.of(
                "idProyecto", ID_PROYECTO,
                "tipoCosto", "OPERACION",
                "vidaUtil", 2,
                "tasaCrecimientoCostos", 3D,
                COSTOS_OPERACION, Map.of(
                        ACTIVIDADES, List.of(Map.of(
                                "idActividad", ID_ACTIVIDAD,
                                "numero", 1,
                                "insumos", List.of(),
                                "totalPeriodo1PrecioMercado", 100D)),
                        "totalPorPeriodoPrecioMercado", Map.of("porPeriodo", List.of(100D, 103D), "total", 205D),
                        "totalPorPeriodoPrecioAjustado", Map.of("porPeriodo", List.of(120D, 123.6D), "total", 245D)),
                "totalInversionPrecioMercado", Map.of("porPeriodo", List.of(100D, 103D), "total", 205D),
                "totalInversionPrecioAjustado", Map.of("porPeriodo", List.of(120D, 123.6D), "total", 245D)));

        PresupuestoOMDto resultado = servicio.obtener(ID_PROYECTO);

        assertThat(resultado.getIdProyecto()).isEqualTo(ID_PROYECTO);
        assertThat(resultado.getTipoCosto().getValue()).isEqualTo("OPERACION");
        assertThat(resultado.getCostosOperacion().getTotalPorPeriodoPrecioMercado().getPorPeriodo())
                .containsExactly(100D, 103D);
        assertThat(resultado.getCostosOperacion().getTotalPorPeriodoPrecioMercado().getTotal()).isEqualTo(205D);
        assertThat(resultado.getCostosMantenimiento()).isNull();
        assertThat(resultado.getTotalInversionPrecioAjustado().getTotal()).isEqualTo(245D);
    }

    @Test
    void configurar_traduceElRequestAMapaYDevuelveElPresupuesto() {
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        when(servicioDominio.configurar(eq(ID_PROYECTO), captor.capture()))
                .thenReturn(Map.of("idProyecto", ID_PROYECTO, "vidaUtil", 5));

        PresupuestoOMDto resultado = servicio.configurar(ID_PROYECTO,
                new ConfigurarPresupuestoOMRequestDto().vidaUtil(5).tasaCrecimientoCostos(2D));

        assertThat(captor.getValue()).containsEntry("vidaUtil", 5).containsEntry("tasaCrecimientoCostos", 2D);
        assertThat(resultado.getIdProyecto()).isEqualTo(ID_PROYECTO);
        assertThat(resultado.getVidaUtil()).isEqualTo(5);
    }

    @Test
    void guardar_devuelveElPresupuestoGuardado() {
        when(servicioDominio.guardar(ID_PROYECTO)).thenReturn(Map.of("idProyecto", ID_PROYECTO));

        assertThat(servicio.guardar(ID_PROYECTO).getIdProyecto()).isEqualTo(ID_PROYECTO);
    }

    @Test
    void eliminar_delegaConElValorDeLaTabla() {
        servicio.eliminar(ID_PROYECTO, TipoCostoTablaDto.MANTENIMIENTO, ID_ACTIVIDAD);

        verify(servicioDominio).eliminarActividad(ID_PROYECTO, "MANTENIMIENTO", ID_ACTIVIDAD);
    }

    @Test
    void registrar_enOperacion_devuelveLaActividadRegistradaDeEsaTabla() {
        prepararRegistro(TipoCostoTablaDto.OPERACION, Map.of(COSTOS_OPERACION, Map.of(ACTIVIDADES,
                List.of("no es un mapa", actividad(99L, "Otra"), actividad(ID_ACTIVIDAD, LIMPIEZA)))));

        ActividadDto resultado = servicio.registrar(ID_PROYECTO, TipoCostoTablaDto.OPERACION,
                new ActividadRequestDto().nombreActividad(LIMPIEZA));

        assertThat(resultado.getIdActividad()).isEqualTo(ID_ACTIVIDAD);
        assertThat(resultado.getNombreActividad()).isEqualTo(LIMPIEZA);
    }

    @Test
    void registrar_enMantenimiento_buscaEnCostosDeMantenimiento() {
        prepararRegistro(TipoCostoTablaDto.MANTENIMIENTO, Map.of(
                COSTOS_OPERACION, Map.of(ACTIVIDADES, List.of(actividad(ID_ACTIVIDAD, "Operación"))),
                COSTOS_MANTENIMIENTO, Map.of(ACTIVIDADES, List.of(actividad(ID_ACTIVIDAD, "Pintura")))));

        ActividadDto resultado = servicio.registrar(ID_PROYECTO, TipoCostoTablaDto.MANTENIMIENTO,
                new ActividadRequestDto().nombreActividad("Pintura"));

        assertThat(resultado.getNombreActividad()).isEqualTo("Pintura");
    }

    @Test
    void registrar_actividadAusenteEnElPresupuesto_lanzaIllegalState() {
        prepararRegistro(TipoCostoTablaDto.OPERACION,
                Map.of(COSTOS_OPERACION, Map.of(ACTIVIDADES, List.of(actividad(99L, "Otra")))));
        ActividadRequestDto request = new ActividadRequestDto().nombreActividad(LIMPIEZA);

        assertThatThrownBy(() -> servicio.registrar(ID_PROYECTO, TipoCostoTablaDto.OPERACION, request))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void registrar_tablaSinListaDeActividades_lanzaIllegalState() {
        prepararRegistro(TipoCostoTablaDto.OPERACION, Map.of(COSTOS_OPERACION, Map.of(ACTIVIDADES, "vacío")));
        ActividadRequestDto request = new ActividadRequestDto().nombreActividad(LIMPIEZA);

        assertThatThrownBy(() -> servicio.registrar(ID_PROYECTO, TipoCostoTablaDto.OPERACION, request))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void registrar_sinTablaEnElPresupuesto_lanzaIllegalState() {
        prepararRegistro(TipoCostoTablaDto.MANTENIMIENTO, Map.of(COSTOS_MANTENIMIENTO, "sin datos"));
        ActividadRequestDto request = new ActividadRequestDto().nombreActividad(LIMPIEZA);

        assertThatThrownBy(() -> servicio.registrar(ID_PROYECTO, TipoCostoTablaDto.MANTENIMIENTO, request))
                .isInstanceOf(IllegalStateException.class);
    }
}
