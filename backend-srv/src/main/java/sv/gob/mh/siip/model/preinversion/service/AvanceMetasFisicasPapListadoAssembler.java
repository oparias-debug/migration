package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import sv.gob.mh.siip.model.preinversion.domain.AvanceCuatriMetaFisica;
import sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralMetaFisica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceMetasFisicasPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioFilaAvanceMetasDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;

/**
 * CU-PRE-33 "Avance Cuatrimestral por Metas Físicas del PAP": armado de las filas del listado, una
 * por etapa con meta física, tanto para la respuesta paginada como para el reporte.
 */
final class AvanceMetasFisicasPapListadoAssembler {

    private static final double META_TOTAL = 1.0D;

    private final EtapaMetaFisicaPapRepository etapaMetaRepository;
    private final AvanceMetasFisicasPapCalculos calculos;

    AvanceMetasFisicasPapListadoAssembler(EtapaMetaFisicaPapRepository etapaMetaRepository,
            AvanceMetasFisicasPapCalculos calculos) {
        this.etapaMetaRepository = etapaMetaRepository;
        this.calculos = calculos;
    }

    AvanceMetasFisicasPAPResponseDto listarPagina(Long unidadFiltro, int anio, Cuatrimestre periodo,
            Pageable pageable) {
        Page<EtapaMetaFisicaPap> resultado = etapaMetaRepository.buscar(unidadFiltro, pageable);
        List<EstudioFilaAvanceMetasDto> contenido = resultado.getContent().stream()
                .map(etapaMeta -> construirFilaListaDto(etapaMeta, anio, periodo))
                .toList();
        return new AvanceMetasFisicasPAPResponseDto(unidadFiltro, anio, AvancePapSoporte.dtoDe(periodo), contenido,
                AvancePapSoporte.paginacion(resultado));
    }

    /** Filas del reporte: todas las etapas con meta física activas de la unidad ejecutora, ordenadas por CUP. */
    List<EstudioFilaAvanceMetasDto> filasDeUnidad(Long unidadFiltro, Integer anio, Cuatrimestre periodo) {
        List<EtapaMetaFisicaPap> etapasMeta = etapaMetaRepository
            .findByEtapaPreinversion_Proyecto_UnidadEjecutora_IdAndActivoTrueOrderByEtapaPreinversion_Proyecto_CupAsc(
                        unidadFiltro);
        return etapasMeta.stream()
                .map(etapaMeta -> construirFilaListaDto(etapaMeta, anio, periodo))
                .toList();
    }

    private EstudioFilaAvanceMetasDto construirFilaListaDto(EtapaMetaFisicaPap etapaMeta, Integer anio,
            Cuatrimestre periodo) {
        EtapaPreinversion etapa = etapaMeta.getEtapaPreinversion();
        Proyecto proyecto = etapa.getProyecto();
        AvanceMetasFisicasPapCalculos.Datos datos = calculos.calcularDatos(etapaMeta, anio, periodo);
        ProgCuatrimestralMetaFisica prog = calculos.programacionDelAnio(etapaMeta.getId(), anio);
        AvanceCuatriMetaFisica avanceDelPeriodo = calculos.avanceDelPeriodo(prog, periodo);

        return new EstudioFilaAvanceMetasDto(proyecto.getCup(), proyecto.getNombre(),
                NombreEtapaDto.valueOf(etapa.getTipoEtapa().name()))
                .meta(META_TOTAL)
                .entregable(etapaMeta.getEntregable() != null ? etapaMeta.getEntregable().name() : null)
                .ejecutadoAniosAnteriores(datos.ejecutadoAnteriorONulo())
                .programadoEnElAnio(datos.programadoAnual().doubleValue())
                .ejecutadoEnElAnio(datos.ejecutadoAlPeriodo().doubleValue())
                .programadoAlCuatrimestre(datos.programadoAlPeriodo().doubleValue())
                .ejecutadoAlCuatrimestre(datos.ejecutadoAlPeriodo().doubleValue())
                .programadoDelCuatrimestre(datos.programadoDelPeriodo().doubleValue())
                .ejecutadoDelCuatrimestre(datos.ejecutadoDelPeriodo().doubleValue())
                .totalMetaEjecutada(datos.totalMetaEjecutada().doubleValue())
                .observaciones(avanceDelPeriodo != null ? avanceDelPeriodo.getObservaciones() : null)
                .estado(datos.estadoDto());
    }
}
