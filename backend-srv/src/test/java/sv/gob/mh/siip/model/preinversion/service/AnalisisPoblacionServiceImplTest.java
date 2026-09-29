package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.AnalisisPoblacion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisPoblacionDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisPoblacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.CeldaUbicacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaPoblacionRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.AnalisisPoblacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

class AnalisisPoblacionServiceImplTest {

    private static final Long ID_PROYECTO = 1L;

    private final ProyectoRepository proyectoRepository = mock(ProyectoRepository.class);
    private final AnalisisPoblacionRepository analisisPoblacionRepository = mock(AnalisisPoblacionRepository.class);
    private final ActorContexto actorContexto = mock(ActorContexto.class);

    private final AnalisisPoblacionServiceImpl service = new AnalisisPoblacionServiceImpl(proyectoRepository,
            analisisPoblacionRepository, actorContexto);

    private final Proyecto proyecto = Proyecto.builder().id(ID_PROYECTO)
            .unidadEjecutora(UnidadEjecutora.builder().id(5L).build()).build();

    @Test
    void obtener_proyectoInexistente_lanzaRecursoNoEncontrado() {
        when(actorContexto.exigir()).thenReturn(Usuario.builder().build());
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener(ID_PROYECTO)).isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void obtener_actorDeOtraUnidad_lanzaAccesoDenegado() {
        when(actorContexto.exigir())
                .thenReturn(Usuario.builder().unidadEjecutora(UnidadEjecutora.builder().id(99L).build()).build());
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));

        assertThatThrownBy(() -> service.obtener(ID_PROYECTO)).isInstanceOf(AccesoDenegadoException.class);
    }

    @BeforeEach
    void prepararGuardado() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(Usuario.builder().build());
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));
        when(analisisPoblacionRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.empty());
        when(analisisPoblacionRepository.save(any(AnalisisPoblacion.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void guardar_completo_calculaEsperaYPorcentajes() {
        AnalisisPoblacionDto dto = service.guardar(ID_PROYECTO, request(
                fila(celda("San Salvador", 100), celda("Soyapango", 50)),
                fila(celda("Distrito A", 80), celda("Distrito B", 40)),
                fila(celda("Comunidad A", 30), celda("Comunidad B", 10))));

        assertThat(dto.getPoblacionEnEspera().getTotalNumeroPersonas()).isEqualTo(80);
        assertThat(dto.getPoblacionObjetivo().getUbicaciones().get(0).getPorcentaje()).isEqualTo(37.5);
    }

    @Test
    void guardar_filasSinUbicaciones_rechazaPorCamposPendientes() {
        AnalisisPoblacionRequestDto request = new AnalisisPoblacionRequestDto()
                .poblacionReferencia(new FilaPoblacionRequestDto().ubicaciones(null))
                .poblacionAfectada(new FilaPoblacionRequestDto().descripcion("Habitantes").ubicaciones(null));

        assertThatThrownBy(() -> service.guardar(ID_PROYECTO, request))
                .isInstanceOfSatisfying(ValidacionNegocioException.class, ex -> {
                    assertThat(ex.getCodigo()).isEqualTo("CAMPOS_OBLIGATORIOS_PENDIENTES");
                    assertThat(campos(ex)).containsExactly("poblacionReferencia.ubicaciones",
                            "poblacionAfectada.ubicaciones", "poblacionObjetivo.ubicaciones");
                });
        verify(analisisPoblacionRepository, never()).save(any());
    }

    @Test
    void guardar_celdasIncompletas_reportaCadaCeldaPendiente() {
        // La "Ubicación" de Referencia no es obligatoria (Anexo B.1); la de Afectada y Objetivo sí.
        AnalisisPoblacionRequestDto request = request(
                fila(celda(null, null)),
                fila(celda(" ", 80)),
                fila(celda("Comunidad A", null)));

        assertThatThrownBy(() -> service.guardar(ID_PROYECTO, request))
                .isInstanceOfSatisfying(ValidacionNegocioException.class, ex -> {
                    assertThat(ex.getCodigo()).isEqualTo("CAMPOS_OBLIGATORIOS_PENDIENTES");
                    assertThat(campos(ex)).containsExactly("poblacionReferencia.ubicaciones[0].numeroPersonas",
                            "poblacionAfectada.ubicaciones[0].ubicacion",
                            "poblacionObjetivo.ubicaciones[0].numeroPersonas");
                });
    }

    @Test
    void guardar_numeroNegativo_rechaza() {
        AnalisisPoblacionRequestDto request = request(
                fila(celda("San Salvador", 100)),
                fila(celda("Distrito A", 80)),
                fila(celda("Comunidad A", -5)));

        assertThatThrownBy(() -> service.guardar(ID_PROYECTO, request))
                .isInstanceOfSatisfying(ValidacionNegocioException.class, ex -> {
                    assertThat(ex.getCodigo()).isEqualTo("NUMERO_PERSONAS_NEGATIVO");
                    assertThat(campos(ex)).containsExactly("poblacionObjetivo.ubicaciones[0].numeroPersonas");
                });
    }

    @Test
    void guardar_afectadaConMasUbicacionesQueReferencia_rechazaAunqueCadaCeldaSeaMenor() {
        // Sin esta regla, 80 + 80 de Afectada superaba en total los 100 de Referencia sin error.
        AnalisisPoblacionRequestDto request = request(
                fila(celda("San Salvador", 100)),
                fila(celda("Distrito A", 80), celda("Distrito B", 80)),
                fila(celda("Comunidad A", 30), celda("Comunidad B", 30)));

        assertThatThrownBy(() -> service.guardar(ID_PROYECTO, request))
                .isInstanceOfSatisfying(ValidacionNegocioException.class,
                        ex -> assertThat(ex.getCodigo()).isEqualTo("UBICACIONES_NO_COINCIDEN"));
    }

    @Test
    void guardar_objetivoConMasUbicacionesQueAfectada_rechazaEnLugarDeEsperaNegativa() {
        AnalisisPoblacionRequestDto request = request(
                fila(celda("San Salvador", 100), celda("Soyapango", 50)),
                fila(celda("Distrito A", 80)),
                fila(celda("Comunidad A", 30), celda("Comunidad B", 20)));

        assertThatThrownBy(() -> service.guardar(ID_PROYECTO, request))
                .isInstanceOfSatisfying(ValidacionNegocioException.class,
                        ex -> assertThat(ex.getCodigo()).isEqualTo("UBICACIONES_NO_COINCIDEN"));
        verify(analisisPoblacionRepository, never()).save(any());
    }

    private static AnalisisPoblacionRequestDto request(FilaPoblacionRequestDto referencia,
            FilaPoblacionRequestDto afectada, FilaPoblacionRequestDto objetivo) {
        return new AnalisisPoblacionRequestDto().poblacionReferencia(referencia).poblacionAfectada(afectada)
                .poblacionObjetivo(objetivo);
    }

    private static FilaPoblacionRequestDto fila(CeldaUbicacionRequestDto... celdas) {
        return new FilaPoblacionRequestDto().ubicaciones(Arrays.asList(celdas));
    }

    private static CeldaUbicacionRequestDto celda(String ubicacion, Integer numeroPersonas) {
        return new CeldaUbicacionRequestDto().ubicacion(ubicacion).numeroPersonas(numeroPersonas);
    }

    private static List<String> campos(ValidacionNegocioException ex) {
        return ex.getDetalles().stream().map(ErrorDetalleDto::getCampo).toList();
    }
}
