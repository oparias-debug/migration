package sv.gob.mh.siip.model.preinversion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.dto.EstadoArchivoSolicitudDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.PaginacionMetadataDto;
import sv.gob.mh.siip.model.preinversion.dto.SolicitudActivaItemDto;
import sv.gob.mh.siip.model.preinversion.dto.SolicitudArchivadaItemDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoSolicitudDto;
import sv.gob.mh.siip.model.preinversion.dto.UnidadEjecutoraResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.UsuarioResumenDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoSolicitud;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitud;
import sv.gob.mh.siip.model.preinversion.mapper.ProyectoMapper;

class BandejaSolicitudEnsambladorTest {

    private static final LocalDateTime FECHA_SOLICITUD = LocalDateTime.of(2025, 1, 10, 8, 0);
    private static final LocalDateTime FECHA_ARCHIVO = LocalDateTime.of(2025, 2, 1, 9, 0);

    private final ProyectoMapper mapper = mock(ProyectoMapper.class);
    private final BandejaSolicitudEnsamblador ensamblador = new BandejaSolicitudEnsamblador(mapper);

    private final UnidadEjecutora unidad = UnidadEjecutora.builder().id(2L).build();
    private final Usuario tecnico = Usuario.builder().id(3L).build();
    private final SolicitudPreinversion solicitud = SolicitudPreinversion.builder().id(1L)
            .proyecto(Proyecto.builder().id(4L).cup("CUP-4").nombre("Escuela").unidadEjecutora(unidad)
                    .estado(EstadoProyecto.ENVIADO_DGICP_REGISTRO).build())
            .tipoSolicitud(TipoSolicitud.CUP).estado(EstadoSolicitud.ARCHIVADA).tecnicoAsignado(tecnico)
            .fechaSolicitud(FECHA_SOLICITUD).fechaArchivo(FECHA_ARCHIVO).build();

    @Test
    void activa_tomaElEstadoDelProyectoYElTecnicoAsignado() {
        UnidadEjecutoraResumenDto resumenUnidad = new UnidadEjecutoraResumenDto();
        UsuarioResumenDto resumenTecnico = new UsuarioResumenDto();
        OffsetDateTime fecha = FECHA_SOLICITUD.atOffset(ZoneOffset.UTC);
        when(mapper.toResumen(unidad)).thenReturn(resumenUnidad);
        when(mapper.toResumen(tecnico)).thenReturn(resumenTecnico);
        when(mapper.map(FECHA_SOLICITUD)).thenReturn(fecha);

        SolicitudActivaItemDto item = ensamblador.activa(solicitud);

        assertThat(item.getIdSolicitud()).isEqualTo(1L);
        assertThat(item.getIdProyecto()).isEqualTo(4L);
        assertThat(item.getUnidadEjecutora()).isSameAs(resumenUnidad);
        assertThat(item.getTipoSolicitud()).isEqualTo(TipoSolicitudDto.CUP);
        assertThat(item.getCup()).isEqualTo("CUP-4");
        assertThat(item.getNombreProyecto()).isEqualTo("Escuela");
        assertThat(item.getFechaSolicitud()).isEqualTo(fecha);
        assertThat(item.getEstado()).isEqualTo(EstadoProyectoDto.ENVIADO_DGICP_REGISTRO);
        assertThat(item.getAsignadoA()).isSameAs(resumenTecnico);
    }

    @Test
    void archivada_tomaElEstadoYLaFechaDeArchivoDeLaSolicitud() {
        OffsetDateTime fechaArchivo = FECHA_ARCHIVO.atOffset(ZoneOffset.UTC);
        when(mapper.map(FECHA_ARCHIVO)).thenReturn(fechaArchivo);

        SolicitudArchivadaItemDto item = ensamblador.archivada(solicitud);

        assertThat(item.getIdSolicitud()).isEqualTo(1L);
        assertThat(item.getCup()).isEqualTo("CUP-4");
        assertThat(item.getTipoSolicitud()).isEqualTo(TipoSolicitudDto.CUP);
        assertThat(item.getEstadoSolicitud()).isEqualTo(EstadoArchivoSolicitudDto.ARCHIVADA);
        assertThat(item.getFechaArchivo()).isEqualTo(fechaArchivo);
    }

    @Test
    void metadata_copiaLosDatosDeLaPagina() {
        PaginacionMetadataDto metadata = ensamblador.metadata(
                new PageImpl<>(List.of("a", "b"), PageRequest.of(1, 2), 5));

        assertThat(metadata.getPagina()).isEqualTo(1);
        assertThat(metadata.getTamanio()).isEqualTo(2);
        assertThat(metadata.getTotalElementos()).isEqualTo(5);
        assertThat(metadata.getTotalPaginas()).isEqualTo(3);
    }
}
