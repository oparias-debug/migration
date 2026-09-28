package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.core.io.Resource;

import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.preinversion.dto.AvanceFinancieroPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.repository.AvanceFinancieroCuatrimestralRepository;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;
import sv.gob.mh.siip.model.preinversion.repository.RevisionAvancePapRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-PRE-32 "Avance Financiero Cuatrimestral del PAP": listado "Buscar" (Anexo A.1) y su reporte
 * (Anexo A.6), con el control de roles y los filtros por defecto (Unidad Ejecutora, Año, Período).
 */
final class AvanceFinancieroPapListado {

    private final ActorContexto actorContexto;
    private final AvanceFinancieroPapListadoAssembler listadoAssembler;
    private final AvanceFinancieroPapReporte reporte;

    AvanceFinancieroPapListado(ActorContexto actorContexto, FuenteFinanciamientoEtapaPapRepository fuenteRepository,
            ProgCuatrimestralFinancieraRepository progRepository,
            AvanceFinancieroCuatrimestralRepository avanceRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository, RevisionAvancePapRepository revisionRepository) {
        this.actorContexto = actorContexto;
        this.listadoAssembler = new AvanceFinancieroPapListadoAssembler(
                new AvanceFinancieroPapCalculos(fuenteRepository, progRepository, avanceRepository));
        this.reporte = new AvanceFinancieroPapReporte(unidadEjecutoraRepository, revisionRepository);
    }

    AvanceFinancieroPAPResponseDto listar(Long idUnidadEjecutora, Integer anio, CuatrimestreDto periodo,
            Integer pagina, Integer tamanio) {
        Usuario actor = AvancePapSoporte.exigirRolConsulta(actorContexto);
        Long unidadFiltro = AvancePapSoporte.unidadEjecutoraEfectiva(actor, idUnidadEjecutora);
        int anioEfectivo = AvancePapSoporte.anioOActual(anio);
        Cuatrimestre periodoEfectivo = AvancePapSoporte.periodoOVigente(periodo);
        return listadoAssembler.listarPagina(unidadFiltro, anioEfectivo, periodoEfectivo,
                AvancePapSoporte.paginaSolicitada(pagina, tamanio));
    }

    Resource generarReporte(Long idUnidadEjecutora, Integer anio, CuatrimestreDto periodo, String formato) {
        Usuario actor = AvancePapSoporte.exigirRolConsulta(actorContexto);
        Cuatrimestre periodoEfectivo = Cuatrimestre.valueOf(periodo.name());
        return reporte.generar(actor, idUnidadEjecutora, anio, periodoEfectivo,
                listadoAssembler.filasActivasEnAnio(idUnidadEjecutora, anio, periodoEfectivo), formato);
    }
}
