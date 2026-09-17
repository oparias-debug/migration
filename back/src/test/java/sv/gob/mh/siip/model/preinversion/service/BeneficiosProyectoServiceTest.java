package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.BeneficioProyecto;
import sv.gob.mh.siip.model.preinversion.domain.PresupuestoOmConfiguracion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.repository.BeneficioProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.BeneficiosProyectoConfiguracionRepository;
import sv.gob.mh.siip.model.preinversion.repository.PresupuestoOmConfiguracionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

class BeneficiosProyectoServiceTest {
    private ProyectoRepository proyectos;
    private BeneficioProyectoRepository beneficios;
    private PresupuestoOmConfiguracionRepository presupuestoOm;
    private BeneficiosProyectoService service;

    @BeforeEach
    void preparar() {
        proyectos = mock(ProyectoRepository.class); beneficios = mock(BeneficioProyectoRepository.class);
        presupuestoOm = mock(PresupuestoOmConfiguracionRepository.class);
        service = new BeneficiosProyectoService(proyectos, beneficios, mock(BeneficiosProyectoConfiguracionRepository.class), presupuestoOm, mock(ActorContexto.class));
    }

    @Test
    void beneficioAutomaticoProyectaYAjustaSegunElEjemploDocumentado() {
        when(proyectos.findById(8L)).thenReturn(Optional.of(Proyecto.builder().id(8L).build()));
        when(beneficios.save(any(BeneficioProyecto.class))).thenAnswer(i -> i.getArgument(0));
        when(presupuestoOm.findByProyectoId(8L)).thenReturn(Optional.of(PresupuestoOmConfiguracion.builder().vidaUtil(3).build()));

        Map<String, Object> respuesta = service.registrarBeneficio(8L, Map.of("tipoBeneficio", "BENEFICIOS_DIRECTOS", "parametro", "P01", "tipoIngreso", "AUTOMATICO", "montoPeriodo1", 800D, "tasaCrecimientoProyectado", 5D, "factorCorreccion", 1.1D));
        @SuppressWarnings("unchecked") List<Map<String, Object>> periodos = (List<Map<String, Object>>) respuesta.get("montosPorPeriodo");

        assertThat(periodos).extracting(p -> p.get("montoPrecioMercado")).containsExactly(800D, 840D, 882D);
        assertThat(periodos).extracting(p -> p.get("montoPrecioAjustado")).containsExactly(880D, 924D, 970.2D);
    }

    @Test
    void beneficioManualRequiereAlMenosUnMonto() {
        Map<String, Object> request = Map.of("tipoBeneficio", "BENEFICIOS_DIRECTOS", "parametro", "P01", "tipoIngreso", "MANUAL", "montosPrecioMercadoPorPeriodo", List.of());
        assertThatThrownBy(() -> service.registrarBeneficio(8L,request ))
                .isInstanceOf(ValidacionNegocioException.class);
    }
}
