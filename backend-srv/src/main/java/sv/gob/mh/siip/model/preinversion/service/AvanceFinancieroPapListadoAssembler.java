package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import sv.gob.mh.siip.model.preinversion.domain.AvanceFinancieroCuatrimestral;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.FuenteFinanciamientoEtapaPap;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralFinanciera;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceFinancieroPAPResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.EstudioFilaAvancePAPDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;

/**
 * CU-PRE-32 "Avance Financiero Cuatrimestral del PAP": armado de las filas del listado (Anexo A.1),
 * una por fuente de financiamiento de cada etapa, tanto para la respuesta paginada de "Buscar" como
 * para el reporte (Anexo A.6).
 */
final class AvanceFinancieroPapListadoAssembler {

    private final AvanceFinancieroPapCalculos calculos;

    AvanceFinancieroPapListadoAssembler(AvanceFinancieroPapCalculos calculos) {
        this.calculos = calculos;
    }

    /**
     * RN-A.a/RN-B.a: "Buscar" filtra por Unidad Ejecutora y Año a nivel de datos (solo estudios
     * activos en la Programación Financiera de ese ejercicio, CU-PRE-30). El Período no excluye
     * filas: determina qué cuatrimestre se muestra/acumula en cada columna (RN-E).
     */
    AvanceFinancieroPAPResponseDto listarPagina(Long unidadFiltro, int anio, Cuatrimestre periodo,
            Pageable pageable) {
        Page<FuenteFinanciamientoEtapaPap> resultado = calculos.buscarActivasEnAnio(unidadFiltro, anio, pageable);
        List<EstudioFilaAvancePAPDto> contenido = construirFilas(resultado.getContent(), anio, periodo);
        return new AvanceFinancieroPAPResponseDto(unidadFiltro, anio, AvancePapSoporte.dtoDe(periodo), contenido,
                AvancePapSoporte.paginacion(resultado));
    }

    /** Anexo A.6: "la misma tabla de datos del Anexo A.1" -> mismo filtro por año (RN-B.a), sin paginar. */
    List<EstudioFilaAvancePAPDto> filasActivasEnAnio(Long idUnidadEjecutora, Integer anio, Cuatrimestre periodo) {
        List<FuenteFinanciamientoEtapaPap> fuentes = calculos
                .buscarActivasEnAnio(idUnidadEjecutora, anio, Pageable.unpaged()).getContent();
        return construirFilas(fuentes, anio, periodo);
    }

    private List<EstudioFilaAvancePAPDto> construirFilas(List<FuenteFinanciamientoEtapaPap> fuentes, Integer anio,
            Cuatrimestre periodo) {
        return fuentes.stream()
                .filter(fuente -> !calculos.etapaFinalizada(fuente.getEtapaPreinversion(), anio))
                .map(fuente -> construirFilaListaDto(fuente, anio, periodo))
                .toList();
    }

    private EstudioFilaAvancePAPDto construirFilaListaDto(FuenteFinanciamientoEtapaPap fuente, Integer anio,
            Cuatrimestre periodo) {
        EtapaPreinversion etapa = fuente.getEtapaPreinversion();
        Proyecto proyecto = etapa.getProyecto();
        ProgCuatrimestralFinanciera prog = calculos.programacionDelAnio(fuente.getId(), anio);
        BigDecimal programadoDelPeriodo = AvanceFinancieroPapCalculos.montoProgramadoCuatrimestre(prog, periodo);
        BigDecimal programadoAnual = AvanceFinancieroPapCalculos.montoProgramadoAnual(prog);
        BigDecimal programadoAlPeriodo = AvanceFinancieroPapCalculos.montoProgramadoAlPeriodo(prog, periodo);

        BigDecimal ejecutadoAnterior = calculos.ejecutadoAniosAnteriores(fuente.getId(), anio);
        BigDecimal ejecutadoAlPeriodo = calculos.ejecutadoEnAnioHastaPeriodo(fuente.getId(), anio, periodo);
        AvanceFinancieroCuatrimestral avanceDelPeriodo = calculos.avanceDelPeriodo(prog, periodo);
        BigDecimal ejecutadoDelPeriodo = AvanceFinancieroPapCalculos.montoEjecutado(avanceDelPeriodo);

        EstudioFilaAvancePAPDto dto = new EstudioFilaAvancePAPDto(proyecto.getCup(), proyecto.getNombre(),
                NombreEtapaDto.valueOf(etapa.getTipoEtapa().name()))
                .costoEtapa(etapa.getCosto())
                .fuenteFinanciamiento(AvanceFinancieroPapCalculos.fuenteFinanciamientoDto(fuente))
                .ejecutadoAniosAnteriores(AvanceFinancieroPapCalculos.positivoONulo(ejecutadoAnterior))
                .avanceAnualProgramado(programadoAnual.doubleValue())
                .avanceAnualEjecutadoMonto(ejecutadoAlPeriodo.doubleValue())
                .avanceAlCuatrimestreProgramado(programadoAlPeriodo.doubleValue())
                .avanceAlCuatrimestreEjecutadoMonto(ejecutadoAlPeriodo.doubleValue())
                .avanceDelCuatrimestreProgramado(programadoDelPeriodo.doubleValue())
                .avanceDelCuatrimestreEjecutadoMonto(ejecutadoDelPeriodo.doubleValue())
                .observaciones(AvanceFinancieroPapCalculos.observaciones(avanceDelPeriodo));

        if (programadoAnual.compareTo(BigDecimal.ZERO) > 0) {
            dto.avanceAnualEjecutadoPorcentaje(AvanceFinancieroPapCalculos.porcentaje(ejecutadoAlPeriodo,
                    programadoAnual));
        }
        if (programadoAlPeriodo.compareTo(BigDecimal.ZERO) > 0) {
            dto.avanceAlCuatrimestreEjecutadoPorcentaje(AvanceFinancieroPapCalculos.porcentaje(ejecutadoAlPeriodo,
                    programadoAlPeriodo));
        }
        if (programadoDelPeriodo.compareTo(BigDecimal.ZERO) > 0) {
            dto.avanceDelCuatrimestrePorcentaje(AvanceFinancieroPapCalculos.porcentaje(ejecutadoDelPeriodo,
                    programadoDelPeriodo));
        }
        if (etapa.getCosto() != null) {
            dto.alertaExcesoProgramado(calculos.alertaExcesoEtapa(etapa, anio, periodo));
        }
        return dto;
    }
}
