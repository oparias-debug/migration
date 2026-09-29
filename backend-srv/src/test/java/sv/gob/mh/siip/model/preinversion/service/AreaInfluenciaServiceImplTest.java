package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Departamento;
import sv.gob.mh.siip.model.common.domain.Municipio;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.MunicipioRepository;
import sv.gob.mh.siip.model.preinversion.domain.AnalisisPoblacion;
import sv.gob.mh.siip.model.preinversion.domain.AreaInfluencia;
import sv.gob.mh.siip.model.preinversion.domain.CeldaUbicacionPoblacion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaDto;
import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaFilaDto;
import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaFilaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
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

    private final Municipio centro = municipio("0614", "San Salvador Centro", "San Salvador", "Central");
    // "San Lorenzo" existe en Ahuachapán y en San Vicente (Anexo C.1).
    private final Municipio sanLorenzoAhuachapan = municipio("0107", "San Lorenzo", "Ahuachapán", "Occidental");
    private final Municipio sanLorenzoSanVicente = municipio("1004", "San Lorenzo", "San Vicente", "Central");

    private static Municipio municipio(String codigo, String nombre, String departamento, String region) {
        return Municipio.builder().codigo(codigo).nombre(nombre)
                .departamento(Departamento.builder().nombre(departamento).region(region).build()).build();
    }

    private void prepararCatalogo() {
        when(municipioRepository.findByCodigoIgnoreCase("0614")).thenReturn(Optional.of(centro));
        when(municipioRepository.findByCodigoIgnoreCase("1004")).thenReturn(Optional.of(sanLorenzoSanVicente));
        when(municipioRepository.findByNombreIgnoreCase("san salvador centro")).thenReturn(List.of(centro));
        when(municipioRepository.findByNombreIgnoreCase("San Salvador Centro")).thenReturn(List.of(centro));
        when(municipioRepository.findByNombreIgnoreCase("San Lorenzo"))
                .thenReturn(List.of(sanLorenzoAhuachapan, sanLorenzoSanVicente));
    }

    private void prepararTecnicoUrp() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(Usuario.builder().build());
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));
        prepararCatalogo();
    }

    private void prepararAutocompletar(AnalisisPoblacion analisis) {
        prepararTecnicoUrp();
        when(analisisPoblacionRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.ofNullable(analisis));
    }

    @Test
    void obtener_proyectoInexistente_lanzaRecursoNoEncontradoConCodigo() {
        when(actorContexto.exigir()).thenReturn(Usuario.builder().build());
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener(ID_PROYECTO))
                .isInstanceOfSatisfying(RecursoNoEncontradoException.class,
                        ex -> assertThat(ex.getCodigo()).isEqualTo(ViabilidadAcceso.PROYECTO_NO_ENCONTRADO));
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
    void autocompletar_omiteUbicacionesVaciasYResuelveDistritoPorNombreOCodigo() {
        prepararAutocompletar(AnalisisPoblacion.builder().ubicacionesObjetivo(List.of(
                new CeldaUbicacionPoblacion(null, 10),
                new CeldaUbicacionPoblacion("  ", 20),
                new CeldaUbicacionPoblacion("san salvador centro", 30),
                new CeldaUbicacionPoblacion("0614", 40))).build());

        AreaInfluenciaDto dto = service.autocompletarDesdePoblacionObjetivo(ID_PROYECTO);

        assertThat(dto.getFilas()).extracting(AreaInfluenciaFilaDto::getDistrito)
                .containsExactly("San Salvador Centro", "San Salvador Centro");
        AreaInfluenciaFilaDto fila = dto.getFilas().get(0);
        assertThat(fila.getRegion()).isEqualTo("Central");
        assertThat(fila.getDepartamento()).isEqualTo("San Salvador");
        assertThat(fila.getUbicacionEspecifica()).isEqualTo("san salvador centro");
    }

    @Test
    void autocompletar_ubicacionQueNoEsDistrito_laProponeComoUbicacionEspecificaSinFallar() {
        // Mockup de CU-PRE-07: la Población Objetivo se registra como texto libre ("Comunidad Río Mar").
        prepararAutocompletar(AnalisisPoblacion.builder().ubicacionesObjetivo(List.of(
                new CeldaUbicacionPoblacion("Comunidad Río Mar", 8450),
                new CeldaUbicacionPoblacion("San Salvador Centro", 100))).build());

        AreaInfluenciaDto dto = service.autocompletarDesdePoblacionObjetivo(ID_PROYECTO);

        assertThat(dto.getFilas()).hasSize(2);
        AreaInfluenciaFilaDto libre = dto.getFilas().get(0);
        assertThat(libre.getUbicacionEspecifica()).isEqualTo("Comunidad Río Mar");
        assertThat(libre.getDistrito()).isNull();
        assertThat(libre.getDepartamento()).isNull();
        assertThat(libre.getRegion()).isNull();
        assertThat(dto.getFilas().get(1).getDistrito()).isEqualTo("San Salvador Centro");
    }

    @Test
    void autocompletar_nombreDeDistritoRepetido_dejaElDistritoVacioParaQueLoElijaElTecnico() {
        prepararAutocompletar(AnalisisPoblacion.builder().ubicacionesObjetivo(List.of(
                new CeldaUbicacionPoblacion("San Lorenzo", 10))).build());

        AreaInfluenciaFilaDto fila = service.autocompletarDesdePoblacionObjetivo(ID_PROYECTO).getFilas().get(0);

        assertThat(fila.getDistrito()).isNull();
        assertThat(fila.getUbicacionEspecifica()).isEqualTo("San Lorenzo");
    }

    @Test
    void guardar_distritoPorCodigoYPorNombreUnico_guardaConSuDepartamento() {
        prepararTecnicoUrp();

        service.guardar(ID_PROYECTO, request(fila("1004", "Cantón El Rosario"), fila("San Salvador Centro", "Col. Escalón")));

        List<AreaInfluencia> guardadas = guardadas();
        assertThat(guardadas).extracting(a -> a.getDepartamento().getNombre())
                .containsExactly("San Vicente", "San Salvador");
    }

    @Test
    void guardar_filaSinUbicacionEspecifica_laGuardaIgual() {
        // Decisión de negocio: RN05 solo sombrea en el cliente; el guardado incompleto se admite.
        prepararTecnicoUrp();

        service.guardar(ID_PROYECTO, request(fila("0614", "   ")));

        assertThat(guardadas()).singleElement().satisfies(a -> assertThat(a.getDescripcion()).isNull());
    }

    @Test
    void guardar_nombreRepetidoEnVariosDepartamentos_rechazaSinBorrarLoGuardado() {
        prepararTecnicoUrp();

        assertThatThrownBy(() -> service.guardar(ID_PROYECTO, request(fila("San Lorenzo", "Centro"))))
                .isInstanceOfSatisfying(ValidacionNegocioException.class, ex -> {
                    assertThat(ex.getCodigo()).isEqualTo(AreaInfluenciaServiceImpl.CODIGO_DISTRITO_INVALIDO);
                    assertThat(ex.getDetalles()).singleElement().satisfies(d -> {
                        assertThat(d.getCampo()).isEqualTo("filas[0].distrito");
                        assertThat(d.getMensaje()).contains("Ahuachapán", "San Vicente");
                    });
                });
        verify(areaInfluenciaRepository, never()).deleteAll(anyIterable());
    }

    @Test
    void guardar_distritosVaciosOInexistentes_reportaCadaFila() {
        prepararTecnicoUrp();

        assertThatThrownBy(() -> service.guardar(ID_PROYECTO,
                request(fila(" ", "A"), fila("Distrito desconocido", "B"), fila("0614", "C"))))
                .isInstanceOfSatisfying(ValidacionNegocioException.class, ex -> {
                    assertThat(ex.getCodigo()).isEqualTo(AreaInfluenciaServiceImpl.CODIGO_DISTRITO_INVALIDO);
                    assertThat(ex.getDetalles()).extracting(ErrorDetalleDto::getCampo)
                            .containsExactly("filas[0].distrito", "filas[1].distrito");
                });
    }

    @Test
    void guardar_filasRepetidas_rechaza() {
        prepararTecnicoUrp();

        assertThatThrownBy(() -> service.guardar(ID_PROYECTO,
                request(fila("0614", "Col. Escalón"), fila("San Salvador Centro", "col. escalón "))))
                .isInstanceOfSatisfying(ValidacionNegocioException.class, ex -> {
                    assertThat(ex.getCodigo()).isEqualTo(AreaInfluenciaServiceImpl.CODIGO_FILA_DUPLICADA);
                    assertThat(ex.getDetalles()).extracting(ErrorDetalleDto::getCampo).containsExactly("filas[1]");
                });
        verify(areaInfluenciaRepository, never()).saveAll(any());
    }

    private static AreaInfluenciaRequestDto request(AreaInfluenciaFilaRequestDto... filas) {
        return new AreaInfluenciaRequestDto().filas(List.of(filas));
    }

    private static AreaInfluenciaFilaRequestDto fila(String distrito, String ubicacionEspecifica) {
        return new AreaInfluenciaFilaRequestDto().distrito(distrito).ubicacionEspecifica(ubicacionEspecifica);
    }

    @SuppressWarnings("unchecked")
    private List<AreaInfluencia> guardadas() {
        ArgumentCaptor<List<AreaInfluencia>> captor = ArgumentCaptor.forClass(List.class);
        verify(areaInfluenciaRepository).saveAll(captor.capture());
        return captor.getValue();
    }
}
