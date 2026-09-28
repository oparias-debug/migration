package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Priorizacion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.BancoProyectosResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoBancoItemDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.PriorizacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoCapturaRepository;
import sv.gob.mh.siip.security.ActorContexto;

class BancoProyectosServiceImplTest {

    private final ProyectoCapturaRepository proyectoRepository = mock(ProyectoCapturaRepository.class);
    private final EtapaPreinversionRepository etapaRepository = mock(EtapaPreinversionRepository.class);
    private final PriorizacionRepository priorizacionRepository = mock(PriorizacionRepository.class);
    private final ActorContexto actorContexto = mock(ActorContexto.class);

    private final BancoProyectosServiceImpl service = new BancoProyectosServiceImpl(proyectoRepository,
            etapaRepository, priorizacionRepository, actorContexto);

    @BeforeEach
    void actorTecnicoPre() {
        when(actorContexto.exigirRol(anySet()))
                .thenReturn(Usuario.builder().rol(RolUsuario.TECNICO_PRE).build());
    }

    @SuppressWarnings("unchecked")
    private Pageable paginaConsultada() {
        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(proyectoRepository).findAll(any(Specification.class), pageable.capture());
        return pageable.getValue();
    }

    @Test
    @SuppressWarnings("unchecked")
    void listar_sinPaginacion_usaPrimeraPaginaYTamanioPorDefecto() {
        when(proyectoRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        BancoProyectosResponseDto respuesta = service.listar(null, null, null, null);

        assertThat(respuesta.getContenido()).isEmpty();
        Pageable pagina = paginaConsultada();
        assertThat(pagina.getPageNumber()).isZero();
        assertThat(pagina.getPageSize()).isEqualTo(20);
    }

    @Test
    @SuppressWarnings("unchecked")
    void listar_paginacionInvalidaYBusquedaEnBlanco_usaValoresPorDefecto() {
        when(proyectoRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        service.listar(5L, "   ", -1, 0);

        Pageable pagina = paginaConsultada();
        assertThat(pagina.getPageNumber()).isZero();
        assertThat(pagina.getPageSize()).isEqualTo(20);
    }

    @Test
    @SuppressWarnings("unchecked")
    void listar_muestraLaEtapaMasAvanzadaYLaPriorizacionMasReciente() {
        Proyecto proyecto = Proyecto.builder().id(1L).cup("08040").nombre("Proyecto A")
                .estado(EstadoProyecto.VIABLE).montoEstimadoInversion(1500D).build();
        Proyecto sinEtapas = Proyecto.builder().id(2L).cup("08041").nombre("Proyecto B")
                .estado(EstadoProyecto.PRIORIZADO).build();
        when(proyectoRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(proyecto, sinEtapas)));
        when(etapaRepository.findByProyectoIdIn(List.of(1L, 2L))).thenReturn(List.of(
                EtapaPreinversion.builder().proyecto(proyecto).tipoEtapa(TipoEtapaPreinversion.PERFIL).build(),
                EtapaPreinversion.builder().proyecto(proyecto).tipoEtapa(TipoEtapaPreinversion.FACTIBILIDAD).build(),
                EtapaPreinversion.builder().proyecto(proyecto).tipoEtapa(TipoEtapaPreinversion.PREFACTIBILIDAD)
                        .build()));
        when(priorizacionRepository.findByProyectoIdInOrderByAnioDescCuatrimestreDescFechaPriorizacionDesc(
                List.of(1L, 2L))).thenReturn(List.of(
                        Priorizacion.builder().proyecto(proyecto).puntaje(new BigDecimal("87.5")).build(),
                        Priorizacion.builder().proyecto(proyecto).puntaje(new BigDecimal("60")).build()));

        BancoProyectosResponseDto respuesta = service.listar(null, "0804", 0, 10);

        assertThat(respuesta.getContenido()).hasSize(2);
        ProyectoBancoItemDto item = respuesta.getContenido().getFirst();
        assertThat(item.getEtapa()).isEqualTo(TipoEtapaPreinversion.FACTIBILIDAD.getEtiquetaUi());
        assertThat(item.getPrioridad()).isEqualTo(87.5D);
        assertThat(item.getInversionEstimada()).isEqualTo(1500D);
        ProyectoBancoItemDto sinDatos = respuesta.getContenido().get(1);
        assertThat(sinDatos.getEtapa()).isNull();
        assertThat(sinDatos.getPrioridad()).isNull();
    }
}
