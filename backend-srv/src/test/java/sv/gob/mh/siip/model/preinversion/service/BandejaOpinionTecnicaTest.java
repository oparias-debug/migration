package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.enums.EstadoSolicitud;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud;
import sv.gob.mh.siip.model.preinversion.repository.SolicitudPreinversionRepository;

/** Pruebas unitarias de {@link BandejaOpinionTecnica} (CU-PRE-26 con la Bandeja de CU-PRE-02). */
class BandejaOpinionTecnicaTest {

    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 9, 28, 10, 0);

    private SolicitudPreinversionRepository solicitudes;
    private BandejaOpinionTecnica bandeja;
    private SolicitudPreinversion solicitud;
    private OpinionTecnica gestion;

    @BeforeEach
    void setUp() {
        solicitudes = mock(SolicitudPreinversionRepository.class);
        when(solicitudes.save(any())).thenAnswer(i -> i.getArgument(0));
        bandeja = new BandejaOpinionTecnica(solicitudes);
        solicitud = SolicitudPreinversion.builder().id(3L).estado(EstadoSolicitud.REGISTRADA).build();
        gestion = OpinionTecnica.builder().id(4L).solicitud(solicitud).build();
    }

    @Test
    void abreUnaSolicitudDeOpinionTecnicaRegistrada() {
        SolicitudPreinversion abierta = bandeja.abrir(Proyecto.builder().id(1L).build(),
                Usuario.builder().nombreUsuario("urp").build(), AHORA);

        assertThat(abierta.getTipoSolicitud()).isEqualTo(TipoSolicitud.OPINION_TECNICA);
        assertThat(abierta.getEstado()).isEqualTo(EstadoSolicitud.REGISTRADA);
        assertThat(abierta.getUsuarioCreacion()).isEqualTo("urp");
    }

    @Test
    void laSolicitudSigueElEstadoDeLaGestion() {
        Usuario tecnico = Usuario.builder().id(7L).build();
        gestion.setTecnicoResponsable(tecnico);
        gestion.setFechaAsignacion(AHORA);

        bandeja.asignar(gestion);
        assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.ASIGNADA);
        assertThat(solicitud.getTecnicoAsignado()).isSameAs(tecnico);

        bandeja.observar(gestion);
        assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.OBSERVADA);

        bandeja.aprobar(gestion);
        assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.APROBADA);

        gestion.setFechaArchivo(AHORA);
        bandeja.archivar(gestion);
        assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitud.ARCHIVADA);
        assertThat(solicitud.getFechaArchivo()).isEqualTo(AHORA);
    }

    @Test
    void unaSolicitudYaArchivadaNoSeVuelveAArchivar() {
        solicitud.setEstado(EstadoSolicitud.ARCHIVADA);

        bandeja.archivar(gestion);

        verify(solicitudes, never()).save(any());
    }

    @Test
    void unaGestionSinSolicitudEnLaBandejaNoHaceNada() {
        OpinionTecnica sinSolicitud = OpinionTecnica.builder().id(5L).build();

        bandeja.asignar(sinSolicitud);
        bandeja.observar(sinSolicitud);
        bandeja.aprobar(sinSolicitud);
        bandeja.archivar(sinSolicitud);

        verify(solicitudes, never()).save(any());
    }
}
