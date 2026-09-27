package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import sv.gob.mh.siip.model.preinversion.domain.SolicitudPreinversion;
import sv.gob.mh.siip.model.preinversion.dto.EstadoArchivoSolicitudDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.PaginacionMetadataDto;
import sv.gob.mh.siip.model.preinversion.dto.SolicitudActivaItemDto;
import sv.gob.mh.siip.model.preinversion.dto.SolicitudArchivadaItemDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoSolicitudDto;
import sv.gob.mh.siip.model.preinversion.mapper.ProyectoMapper;

/**
 * Filas de las tablas de la Bandeja de Preinversión (CU-PRE-02): el estado visible de una solicitud
 * activa es el del proyecto; el de una archivada, el de la propia solicitud.
 */
@Component
public class BandejaSolicitudEnsamblador {

    private final ProyectoMapper mapper;

    public BandejaSolicitudEnsamblador(ProyectoMapper mapper) {
        this.mapper = mapper;
    }

    /** Fila de "Solicitudes Activas". */
    public SolicitudActivaItemDto activa(SolicitudPreinversion s) {
        return new SolicitudActivaItemDto().idSolicitud(s.getId()).idProyecto(s.getProyecto().getId())
                .unidadEjecutora(mapper.toResumen(s.getProyecto().getUnidadEjecutora()))
                .tipoSolicitud(TipoSolicitudDto.valueOf(s.getTipoSolicitud().name()))
                .cup(s.getProyecto().getCup()).nombreProyecto(s.getProyecto().getNombre())
                .fechaSolicitud(mapper.map(s.getFechaSolicitud()))
                .estado(EstadoProyectoDto.valueOf(s.getProyecto().getEstado().name()))
                .asignadoA(mapper.toResumen(s.getTecnicoAsignado()));
    }

    /** Fila del reporte de solicitudes archivadas. */
    public SolicitudArchivadaItemDto archivada(SolicitudPreinversion s) {
        return new SolicitudArchivadaItemDto().idSolicitud(s.getId()).idProyecto(s.getProyecto().getId())
                .unidadEjecutora(mapper.toResumen(s.getProyecto().getUnidadEjecutora()))
                .tipoSolicitud(TipoSolicitudDto.valueOf(s.getTipoSolicitud().name()))
                .cup(s.getProyecto().getCup()).nombreProyecto(s.getProyecto().getNombre())
                .fechaSolicitud(mapper.map(s.getFechaSolicitud())).estadoSolicitud(EstadoArchivoSolicitudDto.ARCHIVADA)
                .fechaArchivo(mapper.map(s.getFechaArchivo()));
    }

    /** Metadatos de paginación de cualquier página de la bandeja. */
    public PaginacionMetadataDto metadata(Page<?> page) {
        return new PaginacionMetadataDto().pagina(page.getNumber()).tamanio(page.getSize())
                .totalElementos(page.getTotalElements()).totalPaginas(page.getTotalPages());
    }
}
