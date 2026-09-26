package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;
import java.util.Locale;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;

import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.FuenteFinanciamientoEtapaPap;
import sv.gob.mh.siip.model.preinversion.dto.EstudioFilaListaPAPDto;
import sv.gob.mh.siip.model.preinversion.dto.ProgramacionFinancieraPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión": listado de la programación
 * por Unidad Ejecutora y Año (Anexo A.1), y su reporte en PDF o Excel, con el control de roles.
 */
final class ProgramacionFinancieraPapListado {

    private final ActorContexto actorContexto;
    private final FuenteFinanciamientoEtapaPapRepository fuenteRepository;
    private final ProgramacionFinancieraPapListadoAssembler listadoAssembler;

    ProgramacionFinancieraPapListado(ActorContexto actorContexto,
            FuenteFinanciamientoEtapaPapRepository fuenteRepository,
            ProgCuatrimestralFinancieraRepository progRepository) {
        this.actorContexto = actorContexto;
        this.fuenteRepository = fuenteRepository;
        this.listadoAssembler = new ProgramacionFinancieraPapListadoAssembler(fuenteRepository, progRepository);
    }

    ProgramacionFinancieraPAPResponseDto listar(Long idUnidadEjecutora, Integer anio, String busqueda,
            Integer pagina, Integer tamanio) {
        Usuario actor = ProgramacionPapSoporte.exigirRolConsultaFinanciera(actorContexto);
        Long unidadFiltro = ProgramacionPapSoporte.unidadEjecutoraEfectiva(actor, idUnidadEjecutora);
        int anioEfectivo = ProgramacionPapSoporte.anioOActual(anio);
        String termino = terminoBusqueda(busqueda);

        Page<FuenteFinanciamientoEtapaPap> resultado = fuenteRepository.buscar(unidadFiltro, termino,
                ProgramacionPapSoporte.paginaSolicitada(pagina, tamanio));

        List<EstudioFilaListaPAPDto> contenido = resultado.getContent().stream()
                .map(fuente -> listadoAssembler.construirFilaListaDto(fuente, anioEfectivo))
                .toList();

        return new ProgramacionFinancieraPAPResponseDto(unidadFiltro, anioEfectivo, contenido,
                ProgramacionPapSoporte.paginacion(resultado));
    }

    Resource generarReporte(Long idUnidadEjecutora, Integer anio, String formato) {
        ProgramacionPapSoporte.exigirRolConsultaFinanciera(actorContexto);
        List<FuenteFinanciamientoEtapaPap> fuentes = fuenteRepository
                .findByEtapaPreinversion_Proyecto_UnidadEjecutora_IdOrderByEtapaPreinversion_Proyecto_CupAsc(
                        idUnidadEjecutora);
        List<EstudioFilaListaPAPDto> filas = fuentes.stream()
                .map(fuente -> listadoAssembler.construirFilaListaDto(fuente, anio))
                .toList();

        byte[] contenido = "PDF".equalsIgnoreCase(formato)
                ? ReporteProgramacionPapGenerator.generarPdf(idUnidadEjecutora, anio, filas)
                : ReporteProgramacionPapGenerator.generarExcel(idUnidadEjecutora, anio, filas);
        return new ByteArrayResource(contenido);
    }

    /** Término de búsqueda para LIKE (en minúsculas y entre comodines); sin búsqueda, {@code null}. */
    private static String terminoBusqueda(String busqueda) {
        return (busqueda == null || busqueda.isBlank())
                ? null
                : ("%" + busqueda.trim().toLowerCase(Locale.ROOT) + "%");
    }
}
