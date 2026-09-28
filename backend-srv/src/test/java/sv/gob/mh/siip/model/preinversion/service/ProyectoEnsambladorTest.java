package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.preinversion.domain.MedidaCatalogo;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.MedidaCatalogoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.enums.TipoMedidaCatalogo;
import sv.gob.mh.siip.model.preinversion.mapper.ProyectoMapper;
import sv.gob.mh.siip.model.preinversion.repository.ComentarioSolicitudRepository;
import sv.gob.mh.siip.model.preinversion.repository.MedidaCatalogoRepository;

class ProyectoEnsambladorTest {

    private ComentarioSolicitudRepository comentarioRepository;
    private MedidaCatalogoRepository medidaCatalogoRepository;
    private ProyectoEnsamblador ensamblador;

    @BeforeEach
    void setUp() {
        comentarioRepository = mock(ComentarioSolicitudRepository.class);
        medidaCatalogoRepository = mock(MedidaCatalogoRepository.class);
        ensamblador = new ProyectoEnsamblador(comentarioRepository, medidaCatalogoRepository,
                Mappers.getMapper(ProyectoMapper.class));
        when(comentarioRepository.findBySolicitudProyectoIdOrderByFechaComentarioAsc(1L)).thenReturn(List.of());
    }

    @Test
    void toDto_resuelveLasMedidasGuardadasContraSuCatalogo() {
        Proyecto entidad = proyecto();
        entidad.setMedidasGrd(List.of("GRD-1"));
        when(medidaCatalogoRepository.findByTipoAndCodigoInOrderByCodigo(TipoMedidaCatalogo.GRD, List.of("GRD-1")))
                .thenReturn(List.of(MedidaCatalogo.builder().tipo(TipoMedidaCatalogo.GRD).codigo("GRD-1")
                        .descripcion("Descripcion GRD").build()));

        ProyectoDto dto = ensamblador.toDto(entidad);

        assertThat(dto.getIdProyecto()).isEqualTo(1L);
        assertThat(dto.getMedidasGrd()).extracting(MedidaCatalogoDto::getCodigo, MedidaCatalogoDto::getDescripcion)
                .containsExactly(tuple("GRD-1", "Descripcion GRD"));
        assertThat(dto.getMedidasGrc()).isEmpty();
        assertThat(dto.getMedidasAcc()).isEmpty();
        verify(comentarioRepository).findBySolicitudProyectoIdOrderByFechaComentarioAsc(1L);
    }

    @Test
    void toDto_noConsultaElCatalogo_sinMedidas() {
        Proyecto entidad = proyecto();
        entidad.setMedidasGrd(null);
        entidad.setMedidasGrc(List.of());
        entidad.setMedidasAcc(null);

        ProyectoDto dto = ensamblador.toDto(entidad);

        assertThat(dto.getMedidasGrd()).isEmpty();
        assertThat(dto.getMedidasGrc()).isEmpty();
        verifyNoInteractions(medidaCatalogoRepository);
    }

    private static Proyecto proyecto() {
        return Proyecto.builder().id(1L).nombre("Proyecto")
                .unidadEjecutora(UnidadEjecutora.builder().id(10L).nombre("UE 1").build())
                .iniciativaInversion(IniciativaInversion.PROYECTO).estado(EstadoProyecto.EN_REGISTRO).build();
    }
}
