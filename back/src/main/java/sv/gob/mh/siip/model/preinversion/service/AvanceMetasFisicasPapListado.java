package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.dto.AvanceMetasFisicasPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioFilaAvanceMetasDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.repository.AvanceCuatriMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralMetaFisicaRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-PRE-33 "Avance Cuatrimestral por Metas Físicas del PAP": listado del avance por Unidad
 * Ejecutora, Año y Período, y su reporte en PDF o Excel, con el control de roles.
 */
final class AvanceMetasFisicasPapListado {

    private final ActorContexto actorContexto;
    private final AvanceMetasFisicasPapListadoAssembler listadoAssembler;

    AvanceMetasFisicasPapListado(ActorContexto actorContexto, EtapaMetaFisicaPapRepository etapaMetaRepository,
            ProgCuatrimestralMetaFisicaRepository progRepository, AvanceCuatriMetaFisicaRepository avanceRepository) {
        this.actorContexto = actorContexto;
        this.listadoAssembler = new AvanceMetasFisicasPapListadoAssembler(etapaMetaRepository,
                new AvanceMetasFisicasPapCalculos(progRepository, avanceRepository));
    }

    AvanceMetasFisicasPAPResponseDto listar(Long idUnidadEjecutora, Integer anio, CuatrimestreDto periodo,
            Integer pagina, Integer tamanio) {
        Usuario actor = AvancePapSoporte.exigirRolConsulta(actorContexto);
        Long unidadFiltro = AvancePapSoporte.unidadEjecutoraEfectiva(actor, idUnidadEjecutora);
        int anioEfectivo = AvancePapSoporte.anioOActual(anio);
        Cuatrimestre periodoEfectivo = AvancePapSoporte.periodoOVigente(periodo);
        return listadoAssembler.listarPagina(unidadFiltro, anioEfectivo, periodoEfectivo,
                AvancePapSoporte.paginaSolicitada(pagina, tamanio));
    }

    Resource generarReporteAvanceMetas(Long idUnidadEjecutora, Integer anio, CuatrimestreDto periodo,
            String formato) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE);
        // Mismo criterio que listar(): el Técnico URP solo puede generar el reporte de su propia unidad
        // ejecutora; el parámetro recibido solo se respeta para el Técnico PRE.
        Long unidadFiltro = AvancePapSoporte.unidadEjecutoraEfectiva(actor, idUnidadEjecutora);
        Cuatrimestre periodoEfectivo = Cuatrimestre.valueOf(periodo.name());
        List<EstudioFilaAvanceMetasDto> filas = listadoAssembler.filasDeUnidad(unidadFiltro, anio, periodoEfectivo);

        byte[] contenido = "PDF".equalsIgnoreCase(formato)
                ? ReporteAvanceMetasFisicasPapGenerator.generarPdf(unidadFiltro, anio, periodoEfectivo, filas)
                : ReporteAvanceMetasFisicasPapGenerator.generarExcel(unidadFiltro, anio, periodoEfectivo, filas);
        return new ByteArrayResource(contenido);
    }
}
