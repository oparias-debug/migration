package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.AnalisisRiesgo;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.RiesgosDesastresInminentes;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisRiesgoDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisRiesgoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.CalificacionRiesgoDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaRiesgoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ImpactoRiesgoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProbabilidadDto;
import sv.gob.mh.siip.model.preinversion.mapper.AnalisisRiesgoMapper;
import sv.gob.mh.siip.model.preinversion.repository.AnalisisRiesgoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

class AnalisisRiesgoServiceImplTest {

    private static final Long ID_PROYECTO = 1L;
    private static final Long ID_UNIDAD = 5L;

    private final AnalisisRiesgoRepository analisisRiesgoRepository = mock(AnalisisRiesgoRepository.class);
    private final ProyectoRepository proyectoRepository = mock(ProyectoRepository.class);
    private final AnalisisRiesgoMapper mapper = mock(AnalisisRiesgoMapper.class);
    private final ActorContexto actorContexto = mock(ActorContexto.class);

    private final AnalisisRiesgoServiceImpl service = new AnalisisRiesgoServiceImpl(analisisRiesgoRepository,
            proyectoRepository, mapper, actorContexto);

    private final Proyecto proyecto = Proyecto.builder().id(ID_PROYECTO)
            .unidadEjecutora(UnidadEjecutora.builder().id(ID_UNIDAD).build()).build();

    private static Usuario actorDeUnidad(Long idUnidad) {
        UnidadEjecutora unidad = idUnidad == null ? null : UnidadEjecutora.builder().id(idUnidad).build();
        return Usuario.builder().unidadEjecutora(unidad).build();
    }

    private void prepararGuardar() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(actorDeUnidad(ID_UNIDAD));
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));
        when(analisisRiesgoRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.empty());
        when(analisisRiesgoRepository.save(any(AnalisisRiesgo.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private AnalisisRiesgo guardarYCapturar(AnalisisRiesgoRequestDto request) {
        service.guardarAnalisisRiesgo(ID_PROYECTO, request);
        ArgumentCaptor<AnalisisRiesgo> captor = ArgumentCaptor.forClass(AnalisisRiesgo.class);
        verify(analisisRiesgoRepository).save(captor.capture());
        return captor.getValue();
    }

    private void prepararAvanzar(AnalisisRiesgo analisis) {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(actorDeUnidad(null));
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));
        when(analisisRiesgoRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.ofNullable(analisis));
    }

    private static AnalisisRiesgo analisisConFila(CalificacionRiesgoDto calificacion, String accion, Double costo) {
        RiesgosDesastresInminentes fila = RiesgosDesastresInminentes.builder()
                .calificacionRiesgo(calificacion).accionMitigacion(accion).costoAccionMitigacion(costo).build();
        return AnalisisRiesgo.builder().filas(new ArrayList<>(List.of(fila))).build();
    }

    @Test
    void obtener_actorDeLaMismaUnidadSinAnalisis_devuelveAnalisisPorDefecto() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE))
                .thenReturn(actorDeUnidad(ID_UNIDAD));
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));
        when(analisisRiesgoRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.empty());
        AnalisisRiesgoDto esperado = new AnalisisRiesgoDto();
        ArgumentCaptor<AnalisisRiesgo> captor = ArgumentCaptor.forClass(AnalisisRiesgo.class);
        when(mapper.toDto(captor.capture())).thenReturn(esperado);

        assertThat(service.obtenerAnalisisRiesgo(ID_PROYECTO)).isSameAs(esperado);
        assertThat(captor.getValue().getTieneRiesgosDesastres()).isFalse();
        assertThat(captor.getValue().getTotalAccionesMitigacion()).isZero();
        assertThat(captor.getValue().getProyecto()).isSameAs(proyecto);
    }

    @Test
    void obtener_actorDeOtraUnidad_lanzaAccesoDenegado() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE))
                .thenReturn(actorDeUnidad(99L));
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));

        assertThatThrownBy(() -> service.obtenerAnalisisRiesgo(ID_PROYECTO))
                .isInstanceOf(AccesoDenegadoException.class);
    }

    @Test
    void guardar_proyectoInexistente_lanzaRecursoNoEncontrado() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(actorDeUnidad(null));
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.empty());
        AnalisisRiesgoRequestDto request = new AnalisisRiesgoRequestDto();

        assertThatThrownBy(() -> service.guardarAnalisisRiesgo(ID_PROYECTO, request))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void guardar_conRiesgosPeroSinFilas_guardaSinFilasYTotalCero() {
        prepararGuardar();

        AnalisisRiesgo guardado = guardarYCapturar(new AnalisisRiesgoRequestDto().tieneRiesgosDesastres(true)
                .filas(null));

        assertThat(guardado.getFilas()).isEmpty();
        assertThat(guardado.getTotalAccionesMitigacion()).isZero();
    }

    @Test
    void guardar_sinRiesgosConFilas_descartaLasFilas() {
        prepararGuardar();
        AnalisisRiesgoRequestDto request = new AnalisisRiesgoRequestDto().tieneRiesgosDesastres(false)
                .addFilasItem(new FilaRiesgoRequestDto().costoAccionMitigacion(10d));

        AnalisisRiesgo guardado = guardarYCapturar(request);

        assertThat(guardado.getTieneRiesgosDesastres()).isFalse();
        assertThat(guardado.getFilas()).isEmpty();
        assertThat(guardado.getTotalAccionesMitigacion()).isZero();
    }

    @Test
    void guardar_filasSinProbabilidadOImpacto_dejanCalificacionNulaYSumanCostosRedondeados() {
        prepararGuardar();
        AnalisisRiesgoRequestDto request = new AnalisisRiesgoRequestDto().tieneRiesgosDesastres(true)
                .addFilasItem(new FilaRiesgoRequestDto().impactoRiesgo(ImpactoRiesgoDto.ALTO)
                        .costoAccionMitigacion(10.126))
                .addFilasItem(new FilaRiesgoRequestDto().probabilidad(ProbabilidadDto.PROBABLE)
                        .costoAccionMitigacion(5.0))
                .addFilasItem(new FilaRiesgoRequestDto().probabilidad(ProbabilidadDto.PROBABLE)
                        .impactoRiesgo(ImpactoRiesgoDto.BAJO));

        AnalisisRiesgo guardado = guardarYCapturar(request);

        assertThat(guardado.getFilas()).extracting(RiesgosDesastresInminentes::getCalificacionRiesgo)
                .containsExactly(null, null, CalificacionRiesgoDto.BAJO);
        assertThat(guardado.getFilas()).allMatch(f -> f.getAnalisisRiesgo() == guardado);
        assertThat(guardado.getTotalAccionesMitigacion()).isEqualTo(15.13);
    }

    @ParameterizedTest
    @CsvSource({
        "IMPROBABLE, EXTREMO, MEDIO",
        "IMPROBABLE, ALTO, BAJO",
        "PROBABLE, EXTREMO, ALTO",
        "PROBABLE, ALTO, MEDIO",
        "PROBABLE, MODERADO, MEDIO",
        "PROBABLE, INSIGNIFICANTE, BAJO",
        "MUY_PROBABLE, EXTREMO, MUY_ALTO",
        "MUY_PROBABLE, ALTO, ALTO",
        "MUY_PROBABLE, MODERADO, ALTO",
        "MUY_PROBABLE, BAJO, MEDIO",
        "MUY_PROBABLE, INSIGNIFICANTE, BAJO",
        "CASI_SEGURO, EXTREMO, MUY_ALTO",
        "CASI_SEGURO, ALTO, MUY_ALTO",
        "CASI_SEGURO, MODERADO, ALTO",
        "CASI_SEGURO, BAJO, MEDIO",
        "CASI_SEGURO, INSIGNIFICANTE, BAJO"
    })
    void guardar_aplicaMatrizDeCalificacionAnexoC1(ProbabilidadDto probabilidad, ImpactoRiesgoDto impacto,
            CalificacionRiesgoDto esperada) {
        prepararGuardar();
        AnalisisRiesgoRequestDto request = new AnalisisRiesgoRequestDto().tieneRiesgosDesastres(true)
                .addFilasItem(new FilaRiesgoRequestDto().probabilidad(probabilidad).impactoRiesgo(impacto));

        AnalisisRiesgo guardado = guardarYCapturar(request);

        assertThat(guardado.getFilas().get(0).getCalificacionRiesgo()).isEqualTo(esperada);
    }

    @Test
    void avanzar_sinAnalisisRegistrado_lanzaRecursoNoEncontrado() {
        prepararAvanzar(null);

        assertThatThrownBy(() -> service.avanzarAAnalisisLegal(ID_PROYECTO))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void avanzar_analisisConFilasNulas_devuelveElDto() {
        AnalisisRiesgo analisis = AnalisisRiesgo.builder().filas(null).build();
        prepararAvanzar(analisis);
        AnalisisRiesgoDto esperado = new AnalisisRiesgoDto();
        when(mapper.toDto(analisis)).thenReturn(esperado);

        assertThat(service.avanzarAAnalisisLegal(ID_PROYECTO)).isSameAs(esperado);
    }

    @Test
    void avanzar_riesgoMuyAltoConAccionEnBlanco_lanzaValidacionNegocio() {
        prepararAvanzar(analisisConFila(CalificacionRiesgoDto.MUY_ALTO, "  ", 10d));

        assertThatThrownBy(() -> service.avanzarAAnalisisLegal(ID_PROYECTO))
                .isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    void avanzar_riesgoAltoSinAccion_lanzaValidacionNegocio() {
        prepararAvanzar(analisisConFila(CalificacionRiesgoDto.ALTO, null, 10d));

        assertThatThrownBy(() -> service.avanzarAAnalisisLegal(ID_PROYECTO))
                .isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    void avanzar_riesgoAltoSinCosto_lanzaValidacionNegocio() {
        prepararAvanzar(analisisConFila(CalificacionRiesgoDto.ALTO, "Reforzar taludes", null));

        assertThatThrownBy(() -> service.avanzarAAnalisisLegal(ID_PROYECTO))
                .isInstanceOf(ValidacionNegocioException.class);
    }

    @Test
    void avanzar_riesgoMuyAltoCompletoYRiesgoMedioIncompleto_devuelveElDto() {
        AnalisisRiesgo analisis = analisisConFila(CalificacionRiesgoDto.MUY_ALTO, "Reforzar taludes", 10d);
        analisis.getFilas().add(RiesgosDesastresInminentes.builder()
                .calificacionRiesgo(CalificacionRiesgoDto.MEDIO).build());
        prepararAvanzar(analisis);
        AnalisisRiesgoDto esperado = new AnalisisRiesgoDto();
        when(mapper.toDto(analisis)).thenReturn(esperado);

        assertThat(service.avanzarAAnalisisLegal(ID_PROYECTO)).isSameAs(esperado);
    }
}
