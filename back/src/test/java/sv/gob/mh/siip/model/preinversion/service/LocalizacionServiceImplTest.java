package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import sv.gob.mh.siip.model.preinversion.domain.Localizacion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaDto;
import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaFilaDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaLocalizacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.LocalizacionDto;
import sv.gob.mh.siip.model.preinversion.dto.LocalizacionRequestDto;
import sv.gob.mh.siip.model.preinversion.mapper.LocalizacionMapper;
import sv.gob.mh.siip.model.preinversion.repository.LocalizacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

class LocalizacionServiceImplTest {

    private static final Long ID_PROYECTO = 1L;
    private static final Long ID_UNIDAD = 5L;

    private final LocalizacionRepository localizacionRepository = mock(LocalizacionRepository.class);
    private final ProyectoRepository proyectoRepository = mock(ProyectoRepository.class);
    private final LocalizacionMapper mapper = mock(LocalizacionMapper.class);
    private final MunicipioRepository municipioRepository = mock(MunicipioRepository.class);
    private final ActorContexto actorContexto = mock(ActorContexto.class);
    private final AreaInfluenciaService areaInfluenciaService = mock(AreaInfluenciaService.class);

    private final LocalizacionServiceImpl service = new LocalizacionServiceImpl(localizacionRepository,
            proyectoRepository, mapper, municipioRepository, actorContexto, areaInfluenciaService);

    private final Proyecto proyecto = Proyecto.builder().id(ID_PROYECTO)
            .unidadEjecutora(UnidadEjecutora.builder().id(ID_UNIDAD).build()).build();

    private final Departamento sanSalvador = Departamento.builder().nombre("San Salvador").build();
    private final Municipio centro = Municipio.builder().codigo("0614").nombre("San Salvador Centro")
            .departamento(sanSalvador).build();
    private final Municipio sinDepartamento = Municipio.builder().codigo("0999").nombre("Sin departamento")
            .build();

    private void prepararAutocompletar(Long idUnidadActor) {
        UnidadEjecutora unidad = idUnidadActor == null ? null : UnidadEjecutora.builder().id(idUnidadActor).build();
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP))
                .thenReturn(Usuario.builder().unidadEjecutora(unidad).build());
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));
    }

    @Test
    void obtener_repositorioDevuelveNull_devuelveFilasVacias() {
        when(localizacionRepository.findAllByProyectoId(ID_PROYECTO)).thenReturn(null);

        LocalizacionDto dto = service.obtenerLocalizacion(ID_PROYECTO);

        assertThat(dto.getIdProyecto()).isEqualTo(ID_PROYECTO);
        assertThat(dto.getFilas()).isEmpty();
    }

    @Test
    void obtener_sinLocalizaciones_devuelveFilasVacias() {
        when(localizacionRepository.findAllByProyectoId(ID_PROYECTO)).thenReturn(List.of());

        assertThat(service.obtenerLocalizacion(ID_PROYECTO).getFilas()).isEmpty();
    }

    @Test
    void obtener_conLocalizaciones_mapeaCadaFila() {
        Localizacion entidad = new Localizacion();
        FilaLocalizacionRequestDto fila = new FilaLocalizacionRequestDto().distrito("San Salvador Centro");
        when(localizacionRepository.findAllByProyectoId(ID_PROYECTO)).thenReturn(List.of(entidad));
        when(mapper.toFilaDto(entidad)).thenReturn(fila);

        assertThat(service.obtenerLocalizacion(ID_PROYECTO).getFilas()).containsExactly(fila);
    }

    @Test
    void guardar_sinFilas_borraLasPreviasYDevuelveListaVacia() {
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.empty());

        LocalizacionDto dto = service.guardarLocalizacion(ID_PROYECTO, new LocalizacionRequestDto().filas(null));

        verify(localizacionRepository).deleteByProyectoId(ID_PROYECTO);
        verify(localizacionRepository, never()).save(any());
        assertThat(dto.getFilas()).isEmpty();
    }

    @Test
    void guardar_conFilas_resuelveMunicipioYDepartamentoPorCodigoONombre() {
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));
        when(municipioRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(sinDepartamento, centro));
        FilaLocalizacionRequestDto porNombre = new FilaLocalizacionRequestDto().distrito("san salvador centro");
        FilaLocalizacionRequestDto sinDepto = new FilaLocalizacionRequestDto().distrito("0999");
        FilaLocalizacionRequestDto sinDistrito = new FilaLocalizacionRequestDto();
        Localizacion entidadPorNombre = new Localizacion();
        Localizacion entidadSinDepto = new Localizacion();
        Localizacion entidadSinDistrito = new Localizacion();
        when(mapper.toEntity(ID_PROYECTO, porNombre)).thenReturn(entidadPorNombre);
        when(mapper.toEntity(ID_PROYECTO, sinDepto)).thenReturn(entidadSinDepto);
        when(mapper.toEntity(ID_PROYECTO, sinDistrito)).thenReturn(entidadSinDistrito);
        LocalizacionRequestDto request = new LocalizacionRequestDto()
                .filas(List.of(porNombre, sinDepto, sinDistrito));

        LocalizacionDto dto = service.guardarLocalizacion(ID_PROYECTO, request);

        assertThat(entidadPorNombre.getMunicipio()).isSameAs(centro);
        assertThat(entidadPorNombre.getDepartamento()).isSameAs(sanSalvador);
        assertThat(entidadSinDepto.getMunicipio()).isSameAs(sinDepartamento);
        assertThat(entidadSinDepto.getDepartamento()).isNull();
        assertThat(entidadSinDistrito.getMunicipio()).isNull();
        verify(localizacionRepository, times(3)).save(any(Localizacion.class));
        assertThat(dto.getFilas()).hasSize(3);
    }

    @Test
    void guardar_distritoInexistente_lanzaRecursoNoEncontrado() {
        when(municipioRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(centro));
        FilaLocalizacionRequestDto fila = new FilaLocalizacionRequestDto().distrito("Atlántida");
        when(mapper.toEntity(eq(ID_PROYECTO), any(FilaLocalizacionRequestDto.class))).thenReturn(new Localizacion());
        LocalizacionRequestDto request = new LocalizacionRequestDto().filas(List.of(fila));

        assertThatThrownBy(() -> service.guardarLocalizacion(ID_PROYECTO, request))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("Atlántida");
    }

    @Test
    void autocompletar_proyectoInexistente_lanzaRecursoNoEncontrado() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(Usuario.builder().build());
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.autocompletarLocalizacionDesdeAreaInfluencia(ID_PROYECTO))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void autocompletar_actorDeOtraUnidad_lanzaAccesoDenegado() {
        prepararAutocompletar(99L);

        assertThatThrownBy(() -> service.autocompletarLocalizacionDesdeAreaInfluencia(ID_PROYECTO))
                .isInstanceOf(AccesoDenegadoException.class);
    }

    @Test
    void autocompletar_sinAreaInfluencia_devuelveFilasVacias() {
        prepararAutocompletar(ID_UNIDAD);
        when(areaInfluenciaService.autocompletarDesdePoblacionObjetivo(ID_PROYECTO)).thenReturn(null);

        LocalizacionDto dto = service.autocompletarLocalizacionDesdeAreaInfluencia(ID_PROYECTO);

        assertThat(dto.getIdProyecto()).isEqualTo(ID_PROYECTO);
        assertThat(dto.getFilas()).isEmpty();
    }

    @Test
    void autocompletar_areaInfluenciaSinFilas_devuelveFilasVacias() {
        prepararAutocompletar(null);
        when(areaInfluenciaService.autocompletarDesdePoblacionObjetivo(ID_PROYECTO))
                .thenReturn(new AreaInfluenciaDto().filas(List.of()));

        assertThat(service.autocompletarLocalizacionDesdeAreaInfluencia(ID_PROYECTO).getFilas()).isEmpty();
    }

    @Test
    void autocompletar_conFilas_omiteLasQueNoTienenDepartamentoYResuelveDistrito() {
        prepararAutocompletar(null);
        when(municipioRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(centro));
        AreaInfluenciaFilaDto valida = new AreaInfluenciaFilaDto().departamento("San Salvador").distrito("0614")
                .ubicacionEspecifica("Colonia Escalón");
        AreaInfluenciaFilaDto sinDepartamento = new AreaInfluenciaFilaDto().distrito("0614");
        AreaInfluenciaFilaDto departamentoEnBlanco = new AreaInfluenciaFilaDto().departamento(" ").distrito("0614");
        when(areaInfluenciaService.autocompletarDesdePoblacionObjetivo(ID_PROYECTO))
                .thenReturn(new AreaInfluenciaDto().filas(List.of(valida, sinDepartamento, departamentoEnBlanco)));

        LocalizacionDto dto = service.autocompletarLocalizacionDesdeAreaInfluencia(ID_PROYECTO);

        assertThat(dto.getFilas()).hasSize(1);
        FilaLocalizacionRequestDto fila = dto.getFilas().get(0);
        assertThat(fila.getDepartamento()).isEqualTo("San Salvador");
        assertThat(fila.getDistrito()).isEqualTo("San Salvador Centro");
        assertThat(fila.getDireccionEspecifica()).isEqualTo("Colonia Escalón");
        assertThat(fila.getCoordenadas()).isNull();
    }
}
