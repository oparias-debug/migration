package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.BeneficioProyecto;
import sv.gob.mh.siip.model.preinversion.domain.BeneficiosProyectoConfiguracion;
import sv.gob.mh.siip.model.preinversion.domain.Parametro;
import sv.gob.mh.siip.model.preinversion.domain.PresupuestoOmConfiguracion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.repository.BeneficioProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.BeneficiosProyectoConfiguracionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ParametroRepository;
import sv.gob.mh.siip.model.preinversion.repository.PresupuestoOmConfiguracionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

class BeneficiosProyectoServiceTest {
    private ProyectoRepository proyectos;
    private BeneficioProyectoRepository beneficios;
    private BeneficiosProyectoConfiguracionRepository configuraciones;
    private PresupuestoOmConfiguracionRepository presupuestoOm;
    private ParametroRepository parametros;
    private ActorContexto actor;
    private BeneficiosProyectoService service;

    @BeforeEach
    void preparar() {
        proyectos = mock(ProyectoRepository.class);
        beneficios = mock(BeneficioProyectoRepository.class);
        configuraciones = mock(BeneficiosProyectoConfiguracionRepository.class);
        presupuestoOm = mock(PresupuestoOmConfiguracionRepository.class);
        parametros = mock(ParametroRepository.class);
        actor = mock(ActorContexto.class);
        Usuario usuario = Usuario.builder().rol(RolUsuario.TECNICO_URP)
                .institucion(Institucion.builder().codigo("MH-DGICP").build()).build();
        when(actor.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(usuario);
        when(actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE)).thenReturn(usuario);
        service = new BeneficiosProyectoService(proyectos, beneficios, configuraciones,
                presupuestoOm, parametros, actor);
    }

    @Test
    void beneficioAutomaticoProyectaYAjustaSegunElEjemploDocumentado() {
        when(proyectos.findById(8L)).thenReturn(Optional.of(Proyecto.builder().id(8L).build()));
        when(beneficios.save(any(BeneficioProyecto.class))).thenAnswer(i -> i.getArgument(0));
        when(presupuestoOm.findByProyectoId(8L)).thenReturn(Optional.of(PresupuestoOmConfiguracion.builder().vidaUtil(3).build()));
        when(parametros.findByCodigo("P01")).thenReturn(Optional.of(Parametro.builder().codigo("P01")
                .nombre("Parámetro de prueba").factorCorreccion(1.1D).build()));

        Map<String, Object> respuesta = service.registrarBeneficio(8L,
                Map.of("tipoBeneficio", "BENEFICIOS_DIRECTOS", "parametro", "P01", "tipoIngreso", "AUTOMATICO",
                        "montoPeriodo1", 800D, "tasaCrecimientoProyectado", 5D, "factorCorreccion", 1.1D));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> periodos = (List<Map<String, Object>>) respuesta.get("montosPorPeriodo");

        assertThat(periodos).extracting(p -> p.get("montoPrecioMercado")).containsExactly(800D, 840D, 882D);
        // RN09: el Técnico URP no es usuario interno, no recibe precios ajustados.
        assertThat(periodos).extracting(p -> p.get("montoPrecioAjustado")).containsOnlyNulls();
    }

    @Test
    void tecnicoPreRecibeFlujosTotalesRedondeadosAMultiplosDeCincoSegunRn04() {
        when(actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE))
                .thenReturn(Usuario.builder().rol(RolUsuario.TECNICO_PRE).build());
        when(proyectos.findById(8L)).thenReturn(Optional.of(Proyecto.builder().id(8L).build()));
        when(presupuestoOm.findByProyectoId(8L)).thenReturn(Optional.of(PresupuestoOmConfiguracion.builder().vidaUtil(3).build()));
        when(configuraciones.findByProyectoId(8L)).thenReturn(Optional.empty());
        when(beneficios.findByProyectoId(8L)).thenReturn(List.of(BeneficioProyecto.builder()
                .tipoBeneficio("BENEFICIOS_DIRECTOS").parametro("P01").tipoIngreso("AUTOMATICO")
                .montoPeriodo1(800D).tasaCrecimientoProyectado(5D).factorCorreccion(1.1D).build()));

        Map<String, Object> respuesta = service.obtenerBeneficios(8L);

        assertThat(respuesta).containsEntry("flujoBeneficiosPrecioMercadoPorPeriodo", List.of(800D, 840D, 885D))
                .containsEntry("flujoBeneficiosPrecioAjustadoPorPeriodo", List.of(880D, 925D, 975D));
    }

    @Test
    void beneficioManualRechazaMasMontosQueLaVidaUtil() {
        when(proyectos.findById(8L)).thenReturn(Optional.of(Proyecto.builder().id(8L).build()));
        when(presupuestoOm.findByProyectoId(8L)).thenReturn(Optional.of(PresupuestoOmConfiguracion.builder().vidaUtil(2).build()));
        when(parametros.findByCodigo("P01")).thenReturn(Optional.of(Parametro.builder().codigo("P01")
                .factorCorreccion(1D).build()));
        Map<String, Object> request = Map.of("tipoBeneficio", "BENEFICIOS_DIRECTOS", "parametro", "P01",
                "tipoIngreso", "MANUAL", "montosPrecioMercadoPorPeriodo", List.of(1D, 2D, 3D));

        assertThatThrownBy(() -> service.registrarBeneficio(8L, request))
                .isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    void consultaNoFallaSiLaVidaUtilSeRedujoDespuesDeRegistrarUnBeneficioManual() {
        when(proyectos.findById(8L)).thenReturn(Optional.of(Proyecto.builder().id(8L).build()));
        when(presupuestoOm.findByProyectoId(8L)).thenReturn(Optional.of(PresupuestoOmConfiguracion.builder().vidaUtil(2).build()));
        when(configuraciones.findByProyectoId(8L)).thenReturn(Optional.empty());
        when(beneficios.findByProyectoId(8L)).thenReturn(List.of(BeneficioProyecto.builder()
                .tipoBeneficio("BENEFICIOS_DIRECTOS").parametro("P01").tipoIngreso("MANUAL").factorCorreccion(1D)
                .montosPrecioMercadoPorPeriodo(new ArrayList<>(List.of(10D, 20D, 30D))).build()));

        Map<String, Object> respuesta = service.obtenerBeneficios(8L);

        assertThat(respuesta).containsEntry("flujoBeneficiosPrecioMercadoPorPeriodo", List.of(10D, 20D));
    }

    @Test
    void beneficioManualRequiereAlMenosUnMonto() {
        when(proyectos.findById(8L)).thenReturn(Optional.of(Proyecto.builder().id(8L).build()));
        Map<String, Object> request = Map.of("tipoBeneficio", "BENEFICIOS_DIRECTOS", "parametro", "P01",
                "tipoIngreso", "MANUAL", "montosPrecioMercadoPorPeriodo", List.of());
        assertThatThrownBy(() -> service.registrarBeneficio(8L, request))
                .isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    void obtenerRechazaProyectoFueraDelAlcanceDeLaUnidadEjecutora() {
        when(actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE)).thenReturn(Usuario.builder()
                .rol(RolUsuario.TECNICO_URP).unidadEjecutora(UnidadEjecutora.builder().id(1L).build()).build());
        when(proyectos.findById(8L)).thenReturn(Optional.of(Proyecto.builder().id(8L)
                .unidadEjecutora(UnidadEjecutora.builder().id(2L).build()).build()));

        assertThatThrownBy(() -> service.obtenerBeneficios(8L)).isInstanceOf(AccesoDenegadoException.class);
    }

    @Test
    void tecnicoPreConsultaBeneficiosDeOtraUnidadEjecutora() {
        when(actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE)).thenReturn(Usuario.builder()
                .rol(RolUsuario.TECNICO_PRE).unidadEjecutora(UnidadEjecutora.builder().id(1L).build()).build());
        when(proyectos.findById(8L)).thenReturn(Optional.of(Proyecto.builder().id(8L)
                .unidadEjecutora(UnidadEjecutora.builder().id(2L).build()).build()));
        when(beneficios.findByProyectoId(8L)).thenReturn(List.of());
        when(presupuestoOm.findByProyectoId(8L)).thenReturn(Optional.empty());
        when(configuraciones.findByProyectoId(8L)).thenReturn(Optional.empty());

        Map<String, Object> respuesta = service.obtenerBeneficios(8L);

        assertThat(respuesta).containsEntry("idProyecto", 8L)
                .containsEntry("flujoBeneficiosPrecioMercadoPorPeriodo", List.of());
    }

    @Test
    void sinVidaUtilConfiguradaDevuelveCeroPeriodos() {
        when(proyectos.findById(8L)).thenReturn(Optional.of(Proyecto.builder().id(8L).build()));
        when(beneficios.findByProyectoId(8L)).thenReturn(List.of());
        when(presupuestoOm.findByProyectoId(8L)).thenReturn(Optional.empty());
        when(configuraciones.findByProyectoId(8L)).thenReturn(Optional.empty());

        Map<String, Object> respuesta = service.obtenerBeneficios(8L);

        assertThat(respuesta).containsEntry("vidaUtil", null)
                .containsEntry("flujoBeneficiosPrecioMercadoPorPeriodo", List.of());
    }

    @Test
    void tecnicoUrpNoRecibePreciosAjustadosNiFactorCorreccionAunqueSeaDelMinisterio() {
        when(actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE)).thenReturn(Usuario.builder()
                .rol(RolUsuario.TECNICO_URP)
                .institucion(Institucion.builder().codigo("MH-DGICP").build()).build());
        when(proyectos.findById(8L)).thenReturn(Optional.of(Proyecto.builder().id(8L).build()));
        when(beneficios.findByProyectoId(8L)).thenReturn(List.of());
        when(presupuestoOm.findByProyectoId(8L)).thenReturn(Optional.empty());
        when(configuraciones.findByProyectoId(8L)).thenReturn(Optional.empty());

        Map<String, Object> respuesta = service.obtenerBeneficios(8L);

        assertThat(respuesta).containsEntry("flujoBeneficiosPrecioAjustadoPorPeriodo", null)
                .containsEntry("fcTipoBien", null)
                .containsEntry("valorRescateAjustado", null);
    }

    @Test
    void tecnicoPreRecibePreciosAjustadosYValorRescateAjustado() {
        when(actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE)).thenReturn(Usuario.builder()
                .rol(RolUsuario.TECNICO_PRE).build());
        when(proyectos.findById(8L)).thenReturn(Optional.of(Proyecto.builder().id(8L).build()));
        when(beneficios.findByProyectoId(8L)).thenReturn(List.of());
        when(presupuestoOm.findByProyectoId(8L))
                .thenReturn(Optional.of(PresupuestoOmConfiguracion.builder().vidaUtil(1).build()));
        when(configuraciones.findByProyectoId(8L)).thenReturn(Optional.of(
                BeneficiosProyectoConfiguracion.builder().valorRescate(500D).tipoBien("EDIFICIOS")
                        .factorCorreccionTipoBien(1D).build()));

        Map<String, Object> respuesta = service.obtenerBeneficios(8L);

        assertThat(respuesta).containsEntry("flujoBeneficiosPrecioAjustadoPorPeriodo", List.of(0D))
                .containsEntry("fcTipoBien", 1D)
                .containsEntry("valorRescateAjustado", 500D);
    }

    @Test
    void guardarConfiguracionPersisteElValorRescateYTipoBien() {
        Proyecto proyecto = Proyecto.builder().id(8L).build();
        when(proyectos.findById(8L)).thenReturn(Optional.of(proyecto));
        when(configuraciones.findByProyectoId(8L)).thenReturn(Optional.empty());
        when(beneficios.findByProyectoId(8L)).thenReturn(List.of());
        when(presupuestoOm.findByProyectoId(8L)).thenReturn(Optional.empty());

        service.guardarConfiguracion(8L, Map.of("valorRescate", 500D, "tipoBien", "EDIFICIOS"));

        verify(configuraciones).save(any(BeneficiosProyectoConfiguracion.class));
    }

    @Test
    void guardarConfiguracionRechazaTipoBienFueraDelContrato() {
        when(proyectos.findById(8L)).thenReturn(Optional.of(Proyecto.builder().id(8L).build()));
        Map<String, Object> solicitud = Map.of("valorRescate", 500D, "tipoBien", "EDIFICIO");

        assertThatThrownBy(() -> service.guardarConfiguracion(8L, solicitud))
                .isInstanceOf(ValidacionNegocioException.class);
    }
}
