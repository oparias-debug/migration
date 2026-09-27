package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.AlternativaSolucion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.RegistroAlternativasDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistroAlternativasRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.AlternativaSolucionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

class AlternativaSolucionServiceImplTest {

    private static final Long ID_PROYECTO = 1L;
    private static final Long ID_UNIDAD = 5L;

    private final ProyectoRepository proyectoRepository = mock(ProyectoRepository.class);
    private final AlternativaSolucionRepository alternativaRepository = mock(AlternativaSolucionRepository.class);
    private final ActorContexto actorContexto = mock(ActorContexto.class);

    private final AlternativaSolucionServiceImpl service = new AlternativaSolucionServiceImpl(proyectoRepository,
            alternativaRepository, actorContexto);

    private final Proyecto proyecto = Proyecto.builder().id(ID_PROYECTO)
            .unidadEjecutora(UnidadEjecutora.builder().id(ID_UNIDAD).build()).build();

    private static Usuario actor(RolUsuario rol, Long idUnidad) {
        UnidadEjecutora unidad = idUnidad == null ? null : UnidadEjecutora.builder().id(idUnidad).build();
        return Usuario.builder().rol(rol).unidadEjecutora(unidad).build();
    }

    private void prepararAvanzar(List<AlternativaSolucion> alternativas) {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(actor(RolUsuario.TECNICO_URP, ID_UNIDAD));
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));
        when(alternativaRepository.findByProyectoIdOrderByOrdenAsc(ID_PROYECTO)).thenReturn(alternativas);
    }

    @Test
    void obtener_proyectoInexistente_lanzaRecursoNoEncontrado() {
        when(actorContexto.exigir()).thenReturn(actor(RolUsuario.TECNICO_URP, null));
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener(ID_PROYECTO)).isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void obtener_actorDeOtraUnidad_lanzaAccesoDenegado() {
        when(actorContexto.exigir()).thenReturn(actor(RolUsuario.TECNICO_URP, 99L));
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));

        assertThatThrownBy(() -> service.obtener(ID_PROYECTO)).isInstanceOf(AccesoDenegadoException.class);
    }

    @Test
    void obtener_sinGuardadoPrevioYActorNoTecnicoUrp_lanzaRecursoNoEncontrado() {
        when(actorContexto.exigir()).thenReturn(actor(RolUsuario.TECNICO_PRE, null));
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));

        assertThatThrownBy(() -> service.obtener(ID_PROYECTO))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("todavia no se ha guardado");
    }

    @Test
    void obtener_conGuardadoPrevioYActorNoTecnicoUrp_devuelveAlternativas() {
        proyecto.setFechaUltimoGuardadoAlternativasSolucion(LocalDateTime.of(2026, 9, 1, 10, 0));
        when(actorContexto.exigir()).thenReturn(actor(RolUsuario.TECNICO_PRE, ID_UNIDAD));
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));
        when(alternativaRepository.findByProyectoIdOrderByOrdenAsc(ID_PROYECTO))
                .thenReturn(List.of(AlternativaSolucion.builder().nombreAlternativa("Puente").build()));

        RegistroAlternativasDto dto = service.obtener(ID_PROYECTO);

        assertThat(dto.getAlternativas()).hasSize(1);
        assertThat(dto.getAlternativas().get(0).getNombreAlternativa()).isEqualTo("Puente");
        assertThat(dto.getFechaUltimoGuardado()).isNotNull();
    }

    @Test
    void guardar_sinAlternativas_guardaListaVaciaYJustificacion() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(actor(RolUsuario.TECNICO_URP, null));
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));
        when(proyectoRepository.save(proyecto)).thenReturn(proyecto);
        when(alternativaRepository.saveAll(any())).thenReturn(List.of());
        RegistroAlternativasRequestDto request = new RegistroAlternativasRequestDto().alternativas(null)
                .justificacion("Única alternativa viable");

        RegistroAlternativasDto dto = service.guardar(ID_PROYECTO, request);

        assertThat(dto.getAlternativas()).isEmpty();
        assertThat(dto.getJustificacion()).isEqualTo("Única alternativa viable");
        assertThat(proyecto.getFechaUltimoGuardadoAlternativasSolucion()).isNotNull();
    }

    @Test
    void avanzar_sinAlternativas_lanzaValidacionNegocio() {
        prepararAvanzar(List.of());

        assertThatThrownBy(() -> service.avanzarAAnalisisInteresados(ID_PROYECTO))
                .isInstanceOf(ValidacionNegocioException.class);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void avanzar_unaAlternativaSinJustificacion_lanzaValidacionNegocio(String justificacion) {
        proyecto.setJustificacionAlternativasSolucion(justificacion);
        prepararAvanzar(List.of(new AlternativaSolucion()));

        assertThatThrownBy(() -> service.avanzarAAnalisisInteresados(ID_PROYECTO))
                .isInstanceOf(ValidacionNegocioException.class)
                .hasMessageContaining("Justificación");
    }

    @Test
    void avanzar_unaAlternativaConJustificacion_devuelveElRegistro() {
        proyecto.setJustificacionAlternativasSolucion("Es la de menor costo");
        prepararAvanzar(List.of(new AlternativaSolucion()));

        RegistroAlternativasDto dto = service.avanzarAAnalisisInteresados(ID_PROYECTO);

        assertThat(dto.getAlternativas()).hasSize(1);
        assertThat(dto.getJustificacion()).isEqualTo("Es la de menor costo");
    }

    @Test
    void avanzar_variasAlternativasSinJustificacion_devuelveElRegistro() {
        prepararAvanzar(List.of(new AlternativaSolucion(), new AlternativaSolucion()));

        assertThat(service.avanzarAAnalisisInteresados(ID_PROYECTO).getAlternativas()).hasSize(2);
    }
}
