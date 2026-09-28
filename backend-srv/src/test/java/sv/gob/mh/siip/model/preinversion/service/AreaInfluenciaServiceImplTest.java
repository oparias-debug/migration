package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.Departamento;
import sv.gob.mh.siip.model.common.domain.Municipio;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.MunicipioRepository;
import sv.gob.mh.siip.model.preinversion.domain.AnalisisPoblacion;
import sv.gob.mh.siip.model.preinversion.domain.CeldaUbicacionPoblacion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaDto;
import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaFilaDto;
import sv.gob.mh.siip.model.preinversion.repository.AnalisisPoblacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.AreaInfluenciaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

class AreaInfluenciaServiceImplTest {

    private static final Long ID_PROYECTO = 1L;

    private final ProyectoRepository proyectoRepository = mock(ProyectoRepository.class);
    private final AreaInfluenciaRepository areaInfluenciaRepository = mock(AreaInfluenciaRepository.class);
    private final AnalisisPoblacionRepository analisisPoblacionRepository = mock(AnalisisPoblacionRepository.class);
    private final MunicipioRepository municipioRepository = mock(MunicipioRepository.class);
    private final ActorContexto actorContexto = mock(ActorContexto.class);

    private final AreaInfluenciaServiceImpl service = new AreaInfluenciaServiceImpl(proyectoRepository,
            areaInfluenciaRepository, analisisPoblacionRepository, municipioRepository, actorContexto);

    private final Proyecto proyecto = Proyecto.builder().id(ID_PROYECTO)
            .unidadEjecutora(UnidadEjecutora.builder().id(5L).build()).build();

    private final Municipio centro = Municipio.builder().codigo("0614").nombre("San Salvador Centro")
            .departamento(Departamento.builder().nombre("San Salvador").region("Central").build()).build();

    private void prepararAutocompletar(AnalisisPoblacion analisis) {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(Usuario.builder().build());
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));
        when(analisisPoblacionRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.ofNullable(analisis));
        when(municipioRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(centro));
    }

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

    @Test
    void autocompletar_sinAnalisisDePoblacion_devuelveFilasVacias() {
        prepararAutocompletar(null);

        AreaInfluenciaDto dto = service.autocompletarDesdePoblacionObjetivo(ID_PROYECTO);

        assertThat(dto.getIdProyecto()).isEqualTo(ID_PROYECTO);
        assertThat(dto.getFilas()).isEmpty();
    }

    @Test
    void autocompletar_omiteUbicacionesVaciasYResuelveDistritoPorNombre() {
        AnalisisPoblacion analisis = AnalisisPoblacion.builder().ubicacionesObjetivo(List.of(
                new CeldaUbicacionPoblacion(null, 10),
                new CeldaUbicacionPoblacion("  ", 20),
                new CeldaUbicacionPoblacion("san salvador centro", 30),
                new CeldaUbicacionPoblacion("0614", 40))).build();
        prepararAutocompletar(analisis);

        AreaInfluenciaDto dto = service.autocompletarDesdePoblacionObjetivo(ID_PROYECTO);

        assertThat(dto.getFilas()).extracting(AreaInfluenciaFilaDto::getDistrito)
                .containsExactly("San Salvador Centro", "San Salvador Centro");
        AreaInfluenciaFilaDto fila = dto.getFilas().get(0);
        assertThat(fila.getRegion()).isEqualTo("Central");
        assertThat(fila.getDepartamento()).isEqualTo("San Salvador");
        assertThat(fila.getDistrito()).isEqualTo("San Salvador Centro");
        assertThat(fila.getUbicacionEspecifica()).isEqualTo("san salvador centro");
    }

    @Test
    void autocompletar_distritoInexistente_lanzaRecursoNoEncontrado() {
        AnalisisPoblacion analisis = AnalisisPoblacion.builder()
                .ubicacionesObjetivo(List.of(new CeldaUbicacionPoblacion("Distrito desconocido", 10))).build();
        prepararAutocompletar(analisis);

        assertThatThrownBy(() -> service.autocompletarDesdePoblacionObjetivo(ID_PROYECTO))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Distrito desconocido");
    }
}
