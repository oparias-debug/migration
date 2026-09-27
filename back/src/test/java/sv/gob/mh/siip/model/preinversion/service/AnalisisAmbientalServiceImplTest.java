package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.AnalisisAmbiental;
import sv.gob.mh.siip.model.preinversion.domain.ImpactosAmbientales;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisAmbientalDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisAmbientalRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaImpactoAmbientalRequestDto;
import sv.gob.mh.siip.model.preinversion.mapper.AnalisisAmbientalMapper;
import sv.gob.mh.siip.model.preinversion.repository.AnalisisAmbientalRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

class AnalisisAmbientalServiceImplTest {

    private static final Long ID_PROYECTO = 1L;
    private static final Long ID_UNIDAD = 5L;

    private final AnalisisAmbientalRepository repository = mock(AnalisisAmbientalRepository.class);
    private final AnalisisAmbientalMapper mapper = mock(AnalisisAmbientalMapper.class);
    private final ProyectoRepository proyectoRepository = mock(ProyectoRepository.class);
    @SuppressWarnings("unchecked")
    private final ObjectProvider<AnalisisAmbientalService> selfProvider = mock(ObjectProvider.class);
    private final AnalisisAmbientalService self = mock(AnalisisAmbientalService.class);
    private final ActorContexto actorContexto = mock(ActorContexto.class);

    private final AnalisisAmbientalServiceImpl service = new AnalisisAmbientalServiceImpl(repository, mapper,
            proyectoRepository, selfProvider, actorContexto);

    private final Proyecto proyecto = Proyecto.builder().id(ID_PROYECTO)
            .unidadEjecutora(UnidadEjecutora.builder().id(ID_UNIDAD).build()).build();

    private void prepararActor(Long idUnidadActor) {
        UnidadEjecutora unidad = idUnidadActor == null ? null : UnidadEjecutora.builder().id(idUnidadActor).build();
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP))
                .thenReturn(Usuario.builder().unidadEjecutora(unidad).build());
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));
    }

    private void prepararGuardado(AnalisisAmbiental existente) {
        prepararActor(ID_UNIDAD);
        when(repository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.of(existente));
        when(selfProvider.getObject()).thenReturn(self);
    }

    @Test
    void obtener_proyectoInexistente_lanzaRecursoNoEncontrado() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP)).thenReturn(Usuario.builder().build());
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerAnalisisAmbiental(ID_PROYECTO))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void obtener_actorDeOtraUnidad_lanzaAccesoDenegado() {
        prepararActor(99L);

        assertThatThrownBy(() -> service.obtenerAnalisisAmbiental(ID_PROYECTO))
                .isInstanceOf(AccesoDenegadoException.class);
    }

    @Test
    void obtener_sinAnalisisRegistrado_lanzaExcepcion() {
        prepararActor(ID_UNIDAD);
        when(repository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerAnalisisAmbiental(ID_PROYECTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("no existe");
    }

    @Test
    void obtener_sinImpactosAmbientales_devuelveFilasVaciasYTotalCero() {
        prepararActor(null);
        AnalisisAmbiental analisis = AnalisisAmbiental.builder().tieneImpactosAmbientales(false).build();
        when(repository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.of(analisis));

        AnalisisAmbientalDto dto = service.obtenerAnalisisAmbiental(ID_PROYECTO);

        assertThat(dto.getIdProyecto()).isEqualTo(ID_PROYECTO);
        assertThat(dto.getTieneImpactosAmbientales()).isFalse();
        assertThat(dto.getFilas()).isEmpty();
        assertThat(dto.getTotalCostoMedidasGestion()).isZero();
    }

    @Test
    void obtener_conImpactosPeroListaNula_devuelveFilasVacias() {
        prepararActor(null);
        AnalisisAmbiental analisis = AnalisisAmbiental.builder().tieneImpactosAmbientales(true).build();
        analisis.setImpactosAmbientales(null);
        when(repository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.of(analisis));

        AnalisisAmbientalDto dto = service.obtenerAnalisisAmbiental(ID_PROYECTO);

        assertThat(dto.getFilas()).isEmpty();
        assertThat(dto.getTotalCostoMedidasGestion()).isZero();
    }

    @Test
    void obtener_conImpactos_sumaCostosIgnorandoNulos() {
        prepararActor(null);
        ImpactosAmbientales conCosto = new ImpactosAmbientales();
        ImpactosAmbientales sinCosto = new ImpactosAmbientales();
        AnalisisAmbiental analisis = AnalisisAmbiental.builder().tieneImpactosAmbientales(true)
                .impactosAmbientales(new ArrayList<>(List.of(conCosto, sinCosto))).build();
        when(repository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.of(analisis));
        when(mapper.toFilaDto(conCosto)).thenReturn(new FilaImpactoAmbientalRequestDto().costoMedidaGestion(250d));
        when(mapper.toFilaDto(sinCosto)).thenReturn(new FilaImpactoAmbientalRequestDto());

        AnalisisAmbientalDto dto = service.obtenerAnalisisAmbiental(ID_PROYECTO);

        assertThat(dto.getFilas()).hasSize(2);
        assertThat(dto.getTotalCostoMedidasGestion()).isEqualTo(250d);
    }

    @Test
    void guardar_analisisConListaNulaYSinFilas_inicializaListaVacia() {
        AnalisisAmbiental existente = new AnalisisAmbiental();
        existente.setImpactosAmbientales(null);
        prepararGuardado(existente);
        AnalisisAmbientalDto esperado = new AnalisisAmbientalDto();
        when(self.obtenerAnalisisAmbiental(ID_PROYECTO)).thenReturn(esperado);
        AnalisisAmbientalRequestDto request = new AnalisisAmbientalRequestDto().tieneImpactosAmbientales(true)
                .filas(null);

        assertThat(service.guardarAnalisisAmbiental(ID_PROYECTO, request)).isSameAs(esperado);
        assertThat(existente.getImpactosAmbientales()).isEmpty();
        assertThat(existente.getTieneImpactosAmbientales()).isTrue();
        verify(repository).save(existente);
    }

    @Test
    void guardar_conImpactosYFilasVacias_noAgregaImpactos() {
        AnalisisAmbiental existente = AnalisisAmbiental.builder()
                .impactosAmbientales(new ArrayList<>(List.of(new ImpactosAmbientales()))).build();
        prepararGuardado(existente);
        AnalisisAmbientalRequestDto request = new AnalisisAmbientalRequestDto().tieneImpactosAmbientales(true)
                .filas(new ArrayList<>());

        service.guardarAnalisisAmbiental(ID_PROYECTO, request);

        assertThat(existente.getImpactosAmbientales()).isEmpty();
    }

    @Test
    void guardar_conImpactosYFilas_asociaCadaImpactoAlAnalisis() {
        AnalisisAmbiental existente = new AnalisisAmbiental();
        prepararGuardado(existente);
        FilaImpactoAmbientalRequestDto fila = new FilaImpactoAmbientalRequestDto().costoMedidaGestion(10d);
        ImpactosAmbientales impacto = new ImpactosAmbientales();
        when(mapper.toFilaEntity(fila)).thenReturn(impacto);
        AnalisisAmbientalRequestDto request = new AnalisisAmbientalRequestDto().tieneImpactosAmbientales(true)
                .addFilasItem(fila);

        service.guardarAnalisisAmbiental(ID_PROYECTO, request);

        assertThat(existente.getImpactosAmbientales()).containsExactly(impacto);
        assertThat(impacto.getAnalisisAmbiental()).isSameAs(existente);
    }

    @Test
    void guardar_sinImpactosConFilas_descartaLasFilas() {
        AnalisisAmbiental existente = new AnalisisAmbiental();
        prepararGuardado(existente);
        AnalisisAmbientalRequestDto request = new AnalisisAmbientalRequestDto().tieneImpactosAmbientales(false)
                .addFilasItem(new FilaImpactoAmbientalRequestDto());

        service.guardarAnalisisAmbiental(ID_PROYECTO, request);

        assertThat(existente.getTieneImpactosAmbientales()).isFalse();
        assertThat(existente.getImpactosAmbientales()).isEmpty();
    }

    @Test
    void guardar_sinAnalisisPrevio_creaUnoConReferenciaAlProyecto() {
        prepararActor(ID_UNIDAD);
        when(repository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.empty());
        when(proyectoRepository.getReferenceById(ID_PROYECTO)).thenReturn(proyecto);
        when(selfProvider.getObject()).thenReturn(self);
        AnalisisAmbientalRequestDto request = new AnalisisAmbientalRequestDto().tieneImpactosAmbientales(false);

        service.guardarAnalisisAmbiental(ID_PROYECTO, request);

        verify(proyectoRepository).getReferenceById(ID_PROYECTO);
    }
}
