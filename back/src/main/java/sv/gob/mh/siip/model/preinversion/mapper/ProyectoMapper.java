package sv.gob.mh.siip.model.preinversion.mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.ComentarioSolicitud;
import sv.gob.mh.siip.model.preinversion.domain.EjePlanGobierno;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.PlanSectorialRegional;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ComentarioSolicitudDto;
import sv.gob.mh.siip.model.preinversion.dto.EjePlanGobiernoResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.EjeTematicoResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.InstitucionResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.MacrosectorResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.PlanSectorialRegionalResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoListItemDto;
import sv.gob.mh.siip.model.preinversion.dto.SectorResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.UnidadEjecutoraResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.UsuarioResumenDto;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;

/** Traduce las entidades de dominio de CU-PRE-01 a los DTOs generados desde preinversion.yaml. */
@Mapper(componentModel = "spring")
public interface ProyectoMapper {

    ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    // MapStruct no puede convertirlos automaticamente a List<MedidaCatalogoDto> (objetos
    // resueltos con descripcion), asi que ProyectoServiceImpl los resuelve a mano contra
    // MedidaCatalogoRepository despues de llamar a este metodo.
    @Mapping(target = "idProyecto", source = "entidad.id")
    @Mapping(target = "revisionPre", source = "revisionPre")
    @Mapping(target = "medidasGrd", ignore = true)
    @Mapping(target = "medidasGrc", ignore = true)
    @Mapping(target = "medidasAcc", ignore = true)
    ProyectoDto toDto(Proyecto entidad, List<ComentarioSolicitud> revisionPre);

    @Mapping(target = "idProyecto", source = "id")
    ProyectoListItemDto toListItem(Proyecto entidad);

    @Mapping(target = "idInstitucion", source = "id")
    InstitucionResumenDto toResumen(Institucion institucion);

    @Mapping(target = "idUnidadEjecutora", source = "id")
    UnidadEjecutoraResumenDto toResumen(UnidadEjecutora unidadEjecutora);

    @Mapping(target = "idUsuario", source = "id")
    UsuarioResumenDto toResumen(Usuario usuario);

    @Mapping(target = "idComentario", source = "id")
    ComentarioSolicitudDto toDto(ComentarioSolicitud comentario);

    @Mapping(target = "idMacrosector", source = "id")
    MacrosectorResumenDto toResumen(MacroSector macrosector);

    @Mapping(target = "idSector", source = "id")
    SectorResumenDto toResumen(SectorActividad sector);

    @Mapping(target = "idEjeTematico", source = "id")
    EjeTematicoResumenDto toResumen(EjeTematico ejeTematico);

    @Mapping(target = "idEjePlanGobierno", source = "id")
    EjePlanGobiernoResumenDto toResumen(EjePlanGobierno ejePlanGobierno);

    @Mapping(target = "idPlanSectorialRegional", source = "id")
    PlanSectorialRegionalResumenDto toResumen(PlanSectorialRegional planSectorialRegional);

    default OffsetDateTime map(LocalDateTime fecha) {
        return fecha == null ? null : fecha.atZone(ZONA_EL_SALVADOR).toOffsetDateTime();
    }
}
