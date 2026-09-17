package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import sv.gob.mh.siip.model.preinversion.domain.BeneficioProyecto;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.MontoPorPeriodoDto;
import sv.gob.mh.siip.model.preinversion.dto.PresupuestoDto;
import sv.gob.mh.siip.model.preinversion.repository.BeneficioProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.BeneficiosProyectoConfiguracionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

class FlujoCajaIndicadoresServiceTest {
    private ProyectoRepository proyectos;
    private BeneficioProyectoRepository beneficios;
    private BeneficiosProyectoConfiguracionRepository configuracionBeneficios;
    private PresupuestoInversionService presupuestoInversion;
    private PresupuestoOmService presupuestoOm;
    private FlujoCajaIndicadoresService service;

    @BeforeEach
    void preparar() {
        proyectos = mock(ProyectoRepository.class);
        beneficios = mock(BeneficioProyectoRepository.class);
        configuracionBeneficios = mock(BeneficiosProyectoConfiguracionRepository.class);
        presupuestoInversion = mock(PresupuestoInversionService.class);
        presupuestoOm = mock(PresupuestoOmService.class);
        service = new FlujoCajaIndicadoresService(proyectos, beneficios, configuracionBeneficios, presupuestoInversion, presupuestoOm, mock(ActorContexto.class));

        when(proyectos.findById(9L)).thenReturn(Optional.of(Proyecto.builder().id(9L).build()));
        when(presupuestoOm.vidaUtil(9L)).thenReturn(0);
        when(presupuestoOm.costosPorTipo(9L, PresupuestoOmService.TIPO_COSTO_OPERACION))
                .thenReturn(new PresupuestoOmService.CostosPorTipo(List.of(), List.of(), List.of()));
        when(presupuestoOm.costosPorTipo(9L, PresupuestoOmService.TIPO_COSTO_MANTENIMIENTO))
                .thenReturn(new PresupuestoOmService.CostosPorTipo(List.of(), List.of(), List.of()));
        when(configuracionBeneficios.findByProyectoId(9L)).thenReturn(Optional.empty());
    }

    @Test
    void ubicaInversionEnPeriodoCeroYCalculaIndicadoresDescontandoDesdeEsePeriodo() {
        when(presupuestoInversion.obtener(9L)).thenReturn(
                new PresupuestoDto(9L, List.of(), new MontoPorPeriodoDto(List.of(1000D), 1000D), List.of(), 0D));
        when(beneficios.findByProyectoId(9L)).thenReturn(List.of(BeneficioProyecto.builder()
                .tipoIngreso("MANUAL").factorCorreccion(1.1D).montosPrecioMercadoPorPeriodo(List.of(1200D)).build()));

        Map<String, Object> resultado = service.obtenerIndicadores(9L);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> ajustado = (List<Map<String, Object>>) resultado.get("periodosPrecioAjustado");
        assertThat(ajustado).extracting(p -> p.get("periodo")).containsExactly(0, 1);
        assertThat(ajustado.get(0)).containsEntry("costosInversion", 1000D).containsEntry("totalCostos", 1000D)
                .containsEntry("beneficios", 0D).containsEntry("flujoNetoCaja", -1000D);
        assertThat(ajustado.get(1)).containsEntry("costosInversion", 0D).containsEntry("beneficios", 1320D)
                .containsEntry("totalBeneficios", 1320D).containsEntry("flujoNetoCaja", 1320D);

        // RN04: la versión a precios de mercado no aplica el factor de corrección (1.1) del beneficio.
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> mercado = (List<Map<String, Object>>) resultado.get("periodosPrecioMercado");
        assertThat(mercado.get(1)).containsEntry("beneficios", 1200D).containsEntry("totalBeneficios", 1200D)
                .containsEntry("flujoNetoCaja", 1200D);

        assertThat(resultado).containsEntry("vans",178.57);
        assertThat(resultado).containsEntry("vacs",1000D);
        assertThat(resultado).containsEntry("rbc",1.18);
        assertThat(resultado).containsEntry("cae",1120D);
        assertThat((Double) resultado.get("tirs")).isCloseTo(32D, within(0.01));
        assertThat(resultado.get("caeIndeterminado")).isEqualTo(false);
    }

    @Test
    void costosDeOperacionYMantenimientoSeLeenDePresupuestoOmService() {
        when(presupuestoInversion.obtener(9L)).thenReturn(
                new PresupuestoDto(9L, List.of(), new MontoPorPeriodoDto(List.of(), 0D), List.of(), 0D));
        when(beneficios.findByProyectoId(9L)).thenReturn(List.of());
        when(presupuestoOm.vidaUtil(9L)).thenReturn(3);
        when(presupuestoOm.costosPorTipo(9L, PresupuestoOmService.TIPO_COSTO_OPERACION)).thenReturn(
                new PresupuestoOmService.CostosPorTipo(List.of(), List.of(100D, 110D, 121D), List.of(100D, 110D, 121D)));
        when(presupuestoOm.costosPorTipo(9L, PresupuestoOmService.TIPO_COSTO_MANTENIMIENTO)).thenReturn(
                new PresupuestoOmService.CostosPorTipo(List.of(), List.of(50D, 55D, 60.5D), List.of(50D, 55D, 60.5D)));

        Map<String, Object> resultado = service.obtenerIndicadores(9L);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> periodos = (List<Map<String, Object>>) resultado.get("periodosPrecioAjustado");
        assertThat(periodos).extracting(p -> p.get("periodo")).containsExactly(0, 1, 2, 3);
        assertThat(periodos.get(0)).containsEntry("costosOperacion", 0D).containsEntry("costosMantenimiento", 0D);
        assertThat(periodos.get(1)).containsEntry("costosOperacion", 100D).containsEntry("costosMantenimiento", 50D).containsEntry("totalCostos", 150D);
        assertThat(periodos.get(2)).containsEntry("costosOperacion", 110D).containsEntry("costosMantenimiento", 55D).containsEntry("totalCostos", 165D);
        assertThat(periodos.get(3)).containsEntry("costosOperacion", 121D).containsEntry("costosMantenimiento", 60.5D).containsEntry("totalCostos", 181.5D);
    }

    @Test
    void proyectoSinDatosDaIndicadoresIndeterminados() {
        when(presupuestoInversion.obtener(9L)).thenReturn(
                new PresupuestoDto(9L, List.of(), new MontoPorPeriodoDto(List.of(), 0D), List.of(), 0D));
        when(beneficios.findByProyectoId(9L)).thenReturn(List.of());

        Map<String, Object> resultado = service.obtenerIndicadores(9L);

        assertThat((List<?>) resultado.get("periodosPrecioAjustado")).isEmpty();
        assertThat((List<?>) resultado.get("periodosPrecioMercado")).isEmpty();
        assertThat(resultado.get("tirs")).isNull();
        assertThat(resultado.get("tirsIndeterminado")).isEqualTo(true);
        assertThat(resultado.get("rbc")).isNull();
        assertThat(resultado.get("cae")).isNull();
        assertThat(resultado.get("caeIndeterminado")).isEqualTo(true);
    }
}
