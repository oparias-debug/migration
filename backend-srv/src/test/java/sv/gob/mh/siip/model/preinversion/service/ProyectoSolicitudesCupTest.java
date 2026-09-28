package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.ComentarioSolicitud;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoSolicitud;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud;
import sv.gob.mh.siip.model.preinversion.repository.ComentarioSolicitudRepository;
import sv.gob.mh.siip.model.preinversion.repository.SolicitudPreinversionRepository;

class ProyectoSolicitudesCupTest {

    private static final Long ID_PROYECTO = 1L;

    private SolicitudPreinversionRepository solicitudRepository;
    private ComentarioSolicitudRepository comentarioRepository;
    private ProyectoSolicitudesCup solicitudes;
    private Usuario tecnicoPre;

    @BeforeEach
    void setUp() {
        solicitudRepository = mock(SolicitudPreinversionRepository.class);
        comentarioRepository = mock(ComentarioSolicitudRepository.class);
        solicitudes = new ProyectoSolicitudesCup(solicitudRepository, comentarioRepository);
        tecnicoPre = Usuario.builder().id(200L).build();
    }

    @Test
    void exigirSinSolicitudCup_rechazaProyectoConSolicitud() {
        ultimaSolicitud(SolicitudPreinversion.builder().id(1L).build());

        assertThatThrownBy(() -> solicitudes.exigirSinSolicitudCup(ID_PROYECTO))
                .isInstanceOf(ConflictoEstadoException.class)
                .hasMessageContaining("RN 4");
    }

    @Test
    void exigirSinSolicitudCup_aceptaProyectoSinSolicitud() {
        ultimaSolicitud(null);

        assertThatCode(() -> solicitudes.exigirSinSolicitudCup(ID_PROYECTO)).doesNotThrowAnyException();
    }

    @Test
    void registrarSiNoVigente_creaSolicitudRegistrada_cuandoNoHayNinguna() {
        ultimaSolicitud(null);
        Proyecto entidad = proyecto(EstadoProyecto.ENVIADO_DGICP_REGISTRO);

        solicitudes.registrarSiNoVigente(entidad);

        ArgumentCaptor<SolicitudPreinversion> captor = ArgumentCaptor.forClass(SolicitudPreinversion.class);
        verify(solicitudRepository).save(captor.capture());
        assertThat(captor.getValue().getProyecto()).isSameAs(entidad);
        assertThat(captor.getValue().getTipoSolicitud()).isEqualTo(TipoSolicitud.CUP);
        assertThat(captor.getValue().getEstado()).isEqualTo(EstadoSolicitud.REGISTRADA);
        assertThat(captor.getValue().getFechaSolicitud()).isNotNull();
    }

    @Test
    void registrarSiNoVigente_creaSolicitud_cuandoLaUltimaEstaArchivada() {
        ultimaSolicitud(SolicitudPreinversion.builder().estado(EstadoSolicitud.ARCHIVADA).build());

        solicitudes.registrarSiNoVigente(proyecto(EstadoProyecto.ENVIADO_DGICP_REGISTRO));

        verify(solicitudRepository).save(any(SolicitudPreinversion.class));
    }

    @Test
    void registrarSiNoVigente_reutilizaLaSolicitudVigente() {
        ultimaSolicitud(SolicitudPreinversion.builder().estado(EstadoSolicitud.OBSERVADA).build());

        solicitudes.registrarSiNoVigente(proyecto(EstadoProyecto.ENVIADO_DGICP_REGISTRO));

        verify(solicitudRepository, never()).save(any());
    }

    @Test
    void vigenteParaResponder_devuelveLaUltimaSolicitud() {
        SolicitudPreinversion solicitud = SolicitudPreinversion.builder().id(5L).build();
        ultimaSolicitud(solicitud);

        assertThat(solicitudes.vigenteParaResponder(ID_PROYECTO)).isSameAs(solicitud);
    }

    @Test
    void vigenteParaResponder_rechazaProyectoSinSolicitud() {
        ultimaSolicitud(null);

        assertThatThrownBy(() -> solicitudes.vigenteParaResponder(ID_PROYECTO))
                .isInstanceOf(ConflictoEstadoException.class);
    }

    @Test
    void asignadaVigente_rechazaProyectoNoEnviado() {
        Proyecto entidad = proyecto(EstadoProyecto.EN_REGISTRO);

        assertThatThrownBy(() -> solicitudes.asignadaVigente(entidad, tecnicoPre))
                .isInstanceOf(ConflictoEstadoException.class)
                .hasMessageContaining("Enviado a DGICP");
    }

    @Test
    void asignadaVigente_rechazaProyectoSinSolicitud() {
        ultimaSolicitud(null);
        Proyecto entidad = proyecto(EstadoProyecto.ENVIADO_DGICP_REGISTRO);

        assertThatThrownBy(() -> solicitudes.asignadaVigente(entidad, tecnicoPre))
                .isInstanceOf(ConflictoEstadoException.class)
                .hasMessageContaining("vigente");
    }

    @Test
    void asignadaVigente_rechazaSolicitudArchivada() {
        ultimaSolicitud(SolicitudPreinversion.builder().estado(EstadoSolicitud.ARCHIVADA)
                .tecnicoAsignado(tecnicoPre).build());
        Proyecto entidad = proyecto(EstadoProyecto.ENVIADO_DGICP_REGISTRO);

        assertThatThrownBy(() -> solicitudes.asignadaVigente(entidad, tecnicoPre))
                .isInstanceOf(ConflictoEstadoException.class)
                .hasMessageContaining("archivada");
    }

    @Test
    void asignadaVigente_rechazaSolicitudSinTecnicoOAsignadaAOtro() {
        Proyecto entidad = proyecto(EstadoProyecto.ENVIADO_DGICP_REGISTRO);
        Usuario otroTecnico = Usuario.builder().id(201L).build();

        ultimaSolicitud(SolicitudPreinversion.builder().build());
        assertThatThrownBy(() -> solicitudes.asignadaVigente(entidad, tecnicoPre))
                .isInstanceOf(AccesoDenegadoException.class);

        ultimaSolicitud(SolicitudPreinversion.builder().tecnicoAsignado(otroTecnico).build());
        assertThatThrownBy(() -> solicitudes.asignadaVigente(entidad, tecnicoPre))
                .isInstanceOf(AccesoDenegadoException.class);
    }

    @Test
    void asignadaVigente_devuelveLaSolicitudAsignadaAlActor() {
        SolicitudPreinversion solicitud = SolicitudPreinversion.builder().estado(EstadoSolicitud.ASIGNADA)
                .tecnicoAsignado(tecnicoPre).build();
        ultimaSolicitud(solicitud);

        assertThat(solicitudes.asignadaVigente(proyecto(EstadoProyecto.ENVIADO_DGICP_REGISTRO), tecnicoPre))
                .isSameAs(solicitud);
    }

    @Test
    void comentar_guardaElComentarioDelAutor() {
        SolicitudPreinversion solicitud = SolicitudPreinversion.builder().id(5L).build();

        solicitudes.comentar(solicitud, tecnicoPre, "Texto");

        ArgumentCaptor<ComentarioSolicitud> captor = ArgumentCaptor.forClass(ComentarioSolicitud.class);
        verify(comentarioRepository).save(captor.capture());
        assertThat(captor.getValue().getSolicitud()).isSameAs(solicitud);
        assertThat(captor.getValue().getAutor()).isSameAs(tecnicoPre);
        assertThat(captor.getValue().getTexto()).isEqualTo("Texto");
        assertThat(captor.getValue().getFechaComentario()).isNotNull();
    }

    @Test
    void cambiarEstado_actualizaYGuardaLaSolicitud() {
        SolicitudPreinversion solicitud = SolicitudPreinversion.builder().estado(EstadoSolicitud.ASIGNADA).build();

        solicitudes.cambiarEstado(solicitud, EstadoSolicitud.APROBADA);

        assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.APROBADA);
        verify(solicitudRepository).save(solicitud);
    }

    private void ultimaSolicitud(SolicitudPreinversion solicitud) {
        when(solicitudRepository.findFirstByProyectoIdAndTipoSolicitudOrderByFechaSolicitudDesc(ID_PROYECTO,
                TipoSolicitud.CUP)).thenReturn(Optional.ofNullable(solicitud));
    }

    private static Proyecto proyecto(EstadoProyecto estado) {
        return Proyecto.builder().id(ID_PROYECTO).estado(estado).build();
    }
}
