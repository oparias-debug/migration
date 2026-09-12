package sv.gob.mh.siip.model.administracion.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import sv.gob.mh.siip.model.administracion.dto.EjePlanGobiernoResumenDto;
import sv.gob.mh.siip.model.administracion.dto.EjeTematicoResumenDto;
import sv.gob.mh.siip.model.administracion.dto.MacrosectorResumenDto;
import sv.gob.mh.siip.model.administracion.dto.PlanSectorialRegionalResumenDto;
import sv.gob.mh.siip.model.administracion.dto.SectorResumenDto;
import sv.gob.mh.siip.model.administracion.dto.UsuarioResumenDto;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.EjePlanGobierno;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.PlanSectorialRegional;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;

/**
 * Traduce las entidades de dominio a los DTOs de catálogo generados desde
 * CU-ADM-02-catalogos.openapi.yaml. Análogo a {@link sv.gob.mh.siip.model.preinversion.mapper.ProyectoMapper}
 * pero apuntando al modelPackage administracion.dto, ya que estos catálogos se sirven ahora bajo
 * las Api generadas del dominio "administracion" tras la reorganización de los contratos OpenAPI.
 */
@Mapper(componentModel = "spring")
public interface CatalogosAdministracionMapper {

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

    @Mapping(target = "idUsuario", source = "id")
    UsuarioResumenDto toResumen(Usuario usuario);
}
