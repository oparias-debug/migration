package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.AnalisisLegal;
import sv.gob.mh.siip.model.preinversion.domain.AnalsisGestionesLegalesRequeridas;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisLegalDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisLegalRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaAnalisisLegalRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.AnalisisLegalRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

class AnalisisLegalServiceImplTest {

    private static final Long ID_PROYECTO = 1L;
    private static final Long ID_UNIDAD = 5L;

    private final AnalisisLegalRepository analisisLegalRepository = mock(AnalisisLegalRepository.class);
    private final ProyectoRepository proyectoRepository = mock(ProyectoRepository.class);
    private final ActorContexto actorContexto = mock(ActorContexto.class);

    private final AnalisisLegalServiceImpl service = new AnalisisLegalServiceImpl(analisisLegalRepository,
            proyectoRepository, actorContexto);

    private final Proyecto proyecto = Proyecto.builder().id(ID_PROYECTO)
            .unidadEjecutora(UnidadEjecutora.builder().id(ID_UNIDAD).build()).build();

    private void prepararActor(Long idUnidadActor) {
        UnidadEjecutora unidad = idUnidadActor == null ? null : UnidadEjecutora.builder().id(idUnidadActor).build();
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE))
                .thenReturn(Usuario.builder().unidadEjecutora(unidad).build());
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.of(proyecto));
    }

    private void prepararGuardadoSinAnalisisPrevio() {
        prepararActor(ID_UNIDAD);
        when(analisisLegalRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.empty());
        when(analisisLegalRepository.save(any(AnalisisLegal.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void obtener_proyectoInexistente_lanzaRecursoNoEncontrado() {
        when(actorContexto.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE))
                .thenReturn(Usuario.builder().build());
        when(proyectoRepository.findById(ID_PROYECTO)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerAnalisisLegal(ID_PROYECTO))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void obtener_actorDeOtraUnidad_lanzaAccesoDenegado() {
        prepararActor(99L);

        assertThatThrownBy(() -> service.obtenerAnalisisLegal(ID_PROYECTO))
                .isInstanceOf(AccesoDenegadoException.class);
    }

    @Test
    void obtener_sinAnalisisRegistrado_devuelveDtoVacio() {
        prepararActor(ID_UNIDAD);
        when(analisisLegalRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.empty());

        AnalisisLegalDto dto = service.obtenerAnalisisLegal(ID_PROYECTO);

        assertThat(dto.getIdProyecto()).isEqualTo(ID_PROYECTO);
        assertThat(dto.getRequiereAnalisisLegal()).isNull();
        assertThat(dto.getFilas()).isEmpty();
        assertThat(dto.getTotalCostoEntregables()).isZero();
    }

    @Test
    void obtener_conAnalisisRegistrado_mapeaFilasYTotal() {
        prepararActor(null);
        AnalsisGestionesLegalesRequeridas fila = AnalsisGestionesLegalesRequeridas.builder()
                .analisisGestionLegalRequerida("Permiso").entregable("Resolución").costoEntregable(80d).build();
        AnalisisLegal analisis = AnalisisLegal.builder().proyecto(proyecto).requiereAnalisisLegal(true)
                .filas(new ArrayList<>(List.of(fila))).build();
        when(analisisLegalRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.of(analisis));

        AnalisisLegalDto dto = service.obtenerAnalisisLegal(ID_PROYECTO);

        assertThat(dto.getRequiereAnalisisLegal()).isTrue();
        assertThat(dto.getFilas()).hasSize(1);
        assertThat(dto.getTotalCostoEntregables()).isEqualTo(80d);
    }

    @Test
    void guardar_requiereAnalisisConFilas_reemplazaFilasYCalculaTotal() {
        prepararGuardadoSinAnalisisPrevio();
        AnalisisLegalRequestDto request = new AnalisisLegalRequestDto().requiereAnalisisLegal(true)
                .addFilasItem(new FilaAnalisisLegalRequestDto().analisisGestionLegalRequerida("Permiso")
                        .entregable("Resolución").costoEntregable(100d))
                .addFilasItem(new FilaAnalisisLegalRequestDto().analisisGestionLegalRequerida("Escritura")
                        .entregable("Testimonio"));

        AnalisisLegalDto dto = service.guardarAnalisisLegal(ID_PROYECTO, request);

        assertThat(dto.getIdProyecto()).isEqualTo(ID_PROYECTO);
        assertThat(dto.getFilas()).extracting(FilaAnalisisLegalRequestDto::getEntregable)
                .containsExactly("Resolución", "Testimonio");
        assertThat(dto.getTotalCostoEntregables()).isEqualTo(100d);
    }

    @Test
    void guardar_requiereAnalisisSinFilas_guardaSinFilas() {
        prepararGuardadoSinAnalisisPrevio();
        AnalisisLegalRequestDto request = new AnalisisLegalRequestDto().requiereAnalisisLegal(true).filas(null);

        AnalisisLegalDto dto = service.guardarAnalisisLegal(ID_PROYECTO, request);

        assertThat(dto.getRequiereAnalisisLegal()).isTrue();
        assertThat(dto.getFilas()).isEmpty();
    }

    @Test
    void guardar_noRequiereAnalisis_descartaFilasPrevias() {
        prepararActor(ID_UNIDAD);
        AnalisisLegal existente = AnalisisLegal.builder().proyecto(proyecto)
                .filas(new ArrayList<>(List.of(new AnalsisGestionesLegalesRequeridas()))).build();
        when(analisisLegalRepository.findByProyectoId(ID_PROYECTO)).thenReturn(Optional.of(existente));
        when(analisisLegalRepository.save(existente)).thenReturn(existente);
        AnalisisLegalRequestDto request = new AnalisisLegalRequestDto().requiereAnalisisLegal(false)
                .addFilasItem(new FilaAnalisisLegalRequestDto().entregable("Ignorada"));

        AnalisisLegalDto dto = service.guardarAnalisisLegal(ID_PROYECTO, request);

        assertThat(dto.getRequiereAnalisisLegal()).isFalse();
        assertThat(dto.getFilas()).isEmpty();
        assertThat(existente.getFilas()).isEmpty();
    }
}
