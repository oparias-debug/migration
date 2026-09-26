package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;

import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap;
import sv.gob.mh.siip.model.preinversion.domain.RevisionProgramacionPap;
import sv.gob.mh.siip.model.preinversion.dto.EstudioFilaMetasFisicasDto;
import sv.gob.mh.siip.model.preinversion.dto.ProgramacionMetasFisicasPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.RevisionProgramacionPapRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-PRE-31 "Programación Cuatrimestral de Metas Físicas de la Preinversión": listado de la
 * programación física por Unidad Ejecutora y Año (Anexo A.1), y su reporte en PDF o Excel, con el
 * control de roles y los "Comentarios al reporte DGICP" solo para actores internos (RN-C).
 */
final class ProgramacionMetasFisicasPapListado {

    private final ActorContexto actorContexto;
    private final EtapaMetaFisicaPapRepository etapaMetaRepository;
    private final RevisionProgramacionPapRepository revisionRepository;
    private final ProgramacionMetasFisicasPapListadoAssembler listadoAssembler;

    ProgramacionMetasFisicasPapListado(ActorContexto actorContexto, EtapaMetaFisicaPapRepository etapaMetaRepository,
            ProgCuatrimestralMetaFisicaRepository progRepository,
            RevisionProgramacionPapRepository revisionRepository) {
        this.actorContexto = actorContexto;
        this.etapaMetaRepository = etapaMetaRepository;
        this.revisionRepository = revisionRepository;
        this.listadoAssembler = new ProgramacionMetasFisicasPapListadoAssembler(etapaMetaRepository, progRepository);
    }

    ProgramacionMetasFisicasPAPResponseDto listar(Long idUnidadEjecutora, Integer anio, Integer pagina,
            Integer tamanio) {
        Usuario actor = ProgramacionPapSoporte.exigirRolConsultaMetas(actorContexto);
        Long unidadFiltro = ProgramacionPapSoporte.unidadEjecutoraEfectiva(actor, idUnidadEjecutora);
        int anioEfectivo = ProgramacionPapSoporte.anioOActual(anio);

        Page<EtapaMetaFisicaPap> resultado = etapaMetaRepository.buscar(unidadFiltro,
                ProgramacionPapSoporte.paginaSolicitada(pagina, tamanio));

        String comentarios = ProgramacionPapSoporte.esActorInternoDgicp(actor)
                ? comentariosReporteMetasFisicas(unidadFiltro, anioEfectivo) : null;
        List<EstudioFilaMetasFisicasDto> contenido = resultado.getContent().stream()
                .map(etapaMeta -> listadoAssembler.construirFilaListaDto(etapaMeta, anioEfectivo, comentarios))
                .toList();

        return new ProgramacionMetasFisicasPAPResponseDto(unidadFiltro, anioEfectivo, contenido,
                ProgramacionPapSoporte.paginacion(resultado));
    }

    Resource generarReporteMetasFisicas(Long idUnidadEjecutora, Integer anio, String formato) {
        Usuario actor = ProgramacionPapSoporte.exigirRolConsultaMetas(actorContexto);
        List<EtapaMetaFisicaPap> etapasMeta = etapaMetaRepository
            .findByEtapaPreinversion_Proyecto_UnidadEjecutora_IdAndActivoTrueOrderByEtapaPreinversion_Proyecto_CupAsc(
                        idUnidadEjecutora);
        String comentarios = ProgramacionPapSoporte.esActorInternoDgicp(actor)
                ? comentariosReporteMetasFisicas(idUnidadEjecutora, anio) : null;
        List<EstudioFilaMetasFisicasDto> filas = etapasMeta.stream()
                .map(etapaMeta -> listadoAssembler.construirFilaListaDto(etapaMeta, anio, comentarios))
                .toList();

        byte[] contenido = "PDF".equalsIgnoreCase(formato)
                ? ReporteProgramacionMetasFisicasPapGenerator.generarPdf(idUnidadEjecutora, anio, filas)
                : ReporteProgramacionMetasFisicasPapGenerator.generarExcel(idUnidadEjecutora, anio, filas);
        return new ByteArrayResource(contenido);
    }

    private String comentariosReporteMetasFisicas(Long idUnidadEjecutora, Integer anio) {
        return revisionRepository.findByIdUnidadEjecutoraAndAnio(idUnidadEjecutora, anio)
                .map(RevisionProgramacionPap::getComentariosReporteMetasFisicasDgicp)
                .orElse(null);
    }
}
