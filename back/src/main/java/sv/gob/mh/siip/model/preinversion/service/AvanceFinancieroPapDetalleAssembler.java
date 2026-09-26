package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import sv.gob.mh.siip.model.preinversion.domain.AvanceFinancieroCuatrimestral;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.FuenteFinanciamientoEtapaPap;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralFinanciera;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceCuatrimestreAnteriorDto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceEstudioDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaAvanceDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaAvanceFuenteDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;

/**
 * CU-PRE-32 "Avance Financiero Cuatrimestral del PAP": armado del detalle del avance de un estudio
 * (etapas no finalizadas y, por cada una, sus filas por fuente de financiamiento).
 */
final class AvanceFinancieroPapDetalleAssembler {

    private final AvancePapConsultas consultas;
    private final AvanceFinancieroPapCalculos calculos;

    AvanceFinancieroPapDetalleAssembler(AvancePapConsultas consultas, AvanceFinancieroPapCalculos calculos) {
        this.consultas = consultas;
        this.calculos = calculos;
    }

    AvanceEstudioDto construirEstudioDto(Proyecto proyecto, Integer anio, Cuatrimestre periodo) {
        List<EtapaAvanceDto> etapas = consultas.etapasOrdenadas(proyecto.getId()).stream()
                .filter(etapa -> !calculos.etapaFinalizada(etapa, anio))
                .map(etapa -> construirEtapaDto(etapa, anio, periodo))
                .toList();
        return new AvanceEstudioDto(proyecto.getCup(), proyecto.getNombre(), etapas);
    }

    private EtapaAvanceDto construirEtapaDto(EtapaPreinversion etapa, Integer anio, Cuatrimestre periodo) {
        List<FuenteFinanciamientoEtapaPap> fuentesEtapa = calculos.fuentesDeEtapa(etapa.getId());
        Boolean alertaExceso = etapa.getCosto() != null ? calculos.alertaExcesoEtapa(etapa, anio, periodo) : null;
        List<FilaAvanceFuenteDto> fuentes = fuentesEtapa.stream()
                .map(fuente -> construirFilaDto(fuente, alertaExceso, anio, periodo))
                .toList();
        BigDecimal ejecutadoAnterior = calculos.ejecutadoAniosAnterioresEtapa(fuentesEtapa, anio);
        return new EtapaAvanceDto(NombreEtapaDto.valueOf(etapa.getTipoEtapa().name()), fuentes)
                .costoEtapa(etapa.getCosto())
                .ejecutadoAniosAnteriores(AvanceFinancieroPapCalculos.positivoONulo(ejecutadoAnterior));
    }

    private FilaAvanceFuenteDto construirFilaDto(FuenteFinanciamientoEtapaPap fuente, Boolean alertaExcesoEtapa,
            Integer anio, Cuatrimestre periodo) {
        ProgCuatrimestralFinanciera prog = calculos.programacionDelAnio(fuente.getId(), anio);
        BigDecimal programadoDelPeriodo = AvanceFinancieroPapCalculos.montoProgramadoCuatrimestre(prog, periodo);
        BigDecimal programadoAnual = AvanceFinancieroPapCalculos.montoProgramadoAnual(prog);
        BigDecimal programadoAlPeriodo = AvanceFinancieroPapCalculos.montoProgramadoAlPeriodo(prog, periodo);

        BigDecimal ejecutadoAlPeriodo = calculos.ejecutadoEnAnioHastaPeriodo(fuente.getId(), anio, periodo);
        AvanceFinancieroCuatrimestral avanceDelPeriodo = calculos.avanceDelPeriodo(prog, periodo);
        BigDecimal ejecutadoDelPeriodo = AvanceFinancieroPapCalculos.montoEjecutado(avanceDelPeriodo);

        FilaAvanceFuenteDto dto = new FilaAvanceFuenteDto(fuente.getId(), ejecutadoDelPeriodo.doubleValue())
                .fuenteFinanciamiento(AvanceFinancieroPapCalculos.fuenteFinanciamientoDto(fuente))
                .montoProgramadoCuatrimestre(programadoDelPeriodo.doubleValue())
                .observacionesCuatrimestre(AvanceFinancieroPapCalculos.observaciones(avanceDelPeriodo))
                .avanceAnualProgramado(programadoAnual.doubleValue())
                .avanceAnualEjecutadoMonto(ejecutadoAlPeriodo.doubleValue())
                .avanceAlCuatrimestreProgramado(programadoAlPeriodo.doubleValue())
                .avanceAlCuatrimestreEjecutadoMonto(ejecutadoAlPeriodo.doubleValue())
                .avancesCuatrimestresAnteriores(avancesCuatrimestresAnteriores(fuente.getId(), anio, periodo));

        if (programadoAnual.compareTo(BigDecimal.ZERO) > 0) {
            dto.avanceAnualEjecutadoPorcentaje(AvanceFinancieroPapCalculos.porcentaje(ejecutadoAlPeriodo,
                    programadoAnual));
        }
        if (programadoAlPeriodo.compareTo(BigDecimal.ZERO) > 0) {
            dto.avanceAlCuatrimestreEjecutadoPorcentaje(AvanceFinancieroPapCalculos.porcentaje(ejecutadoAlPeriodo,
                    programadoAlPeriodo));
        }
        if (programadoDelPeriodo.compareTo(BigDecimal.ZERO) > 0) {
            dto.porcentajeEjecutadoCuatrimestre(AvanceFinancieroPapCalculos.porcentaje(ejecutadoDelPeriodo,
                    programadoDelPeriodo));
        }
        if (alertaExcesoEtapa != null) {
            dto.alertaExcesoProgramado(alertaExcesoEtapa);
        }
        return dto;
    }

    /** RN-F: vacío en Cuatrimestre I; lo ejecutado en los cuatrimestres previos al consultado en los demás casos. */
    private List<AvanceCuatrimestreAnteriorDto> avancesCuatrimestresAnteriores(Long idFuente, Integer anio,
            Cuatrimestre periodo) {
        List<AvanceCuatrimestreAnteriorDto> resultado = new ArrayList<>();
        for (AvanceFinancieroCuatrimestral avance : calculos.avancesDeFuente(idFuente)) {
            if (avance.getProgramacion().getAnio().equals(anio)
                    && avance.getCuatrimestre().ordinal() < periodo.ordinal()) {
                resultado.add(new AvanceCuatrimestreAnteriorDto(
                        AvancePapSoporte.dtoDe(avance.getCuatrimestre()),
                        avance.getMontoEjecutado().doubleValue()));
            }
        }
        resultado.sort((a, b) -> a.getPeriodo().ordinal() - b.getPeriodo().ordinal());
        return resultado;
    }
}
