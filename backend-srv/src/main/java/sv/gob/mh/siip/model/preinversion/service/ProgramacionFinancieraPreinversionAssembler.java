package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.ProgramacionFinPreinversionDetalle;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.programacion.dto.FilaProgramacionEtapaDto;
import sv.gob.mh.siip.model.preinversion.programacion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.programacion.dto.ProgramacionFinancieraPreinversionDto;

/**
 * CU-PRE-22.1 "Programación Financiera de Preinversión": arma la respuesta con una fila por etapa
 * programable, los montos de cada período configurado y los totales por período y general.
 */
final class ProgramacionFinancieraPreinversionAssembler {

    private ProgramacionFinancieraPreinversionAssembler() {
    }

    /**
     * Arma la programación del proyecto. Los detalles de períodos posteriores al horizonte configurado se
     * ignoran.
     */
    static ProgramacionFinancieraPreinversionDto armar(Long idProyecto, int periodos,
            Collection<ProgramacionFinPreinversionDetalle> detalles, Collection<EtapaPreinversion> etapas) {
        Map<TipoEtapaPreinversion, List<ProgramacionFinPreinversionDetalle>> porEtapa =
                new EnumMap<>(TipoEtapaPreinversion.class);
        for (ProgramacionFinPreinversionDetalle detalle : detalles) {
            porEtapa.computeIfAbsent(detalle.getEtapa(), (TipoEtapaPreinversion clave) -> new ArrayList<>())
                    .add(detalle);
        }
        List<BigDecimal> totales = new ArrayList<>(Collections.nCopies(periodos, BigDecimal.ZERO));
        List<FilaProgramacionEtapaDto> filas = new ArrayList<>();
        for (EtapaPreinversion etapa : etapas) {
            List<Double> montos = montosPorPeriodo(porEtapa.getOrDefault(etapa.getTipoEtapa(), List.of()), periodos);
            BigDecimal total = BigDecimal.ZERO;
            for (var i = 0; i < periodos; i++) {
                BigDecimal monto = ProgramacionFinancieraPreinversionMontos.monetario(montos.get(i));
                totales.set(i, totales.get(i).add(monto));
                total = total.add(monto);
            }
            double totalRedondeado = ProgramacionFinancieraPreinversionMontos.redondeado(total);
            filas.add(new FilaProgramacionEtapaDto(NombreEtapaDto.valueOf(etapa.getTipoEtapa().name()), montos,
                    totalRedondeado).costoEtapa(totalRedondeado));
        }
        List<Double> totalesRedondeados = totales.stream()
                .map(ProgramacionFinancieraPreinversionMontos::redondeado)
                .toList();
        BigDecimal totalGeneral = totales.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ProgramacionFinancieraPreinversionDto(idProyecto, filas).periodosAProgramar(periodos)
                .totalGeneralPorPeriodo(totalesRedondeados)
                .totalGeneral(ProgramacionFinancieraPreinversionMontos.redondeado(totalGeneral));
    }

    private static List<Double> montosPorPeriodo(Collection<ProgramacionFinPreinversionDetalle> detalles,
            int periodos) {
        List<Double> montos = new ArrayList<>(Collections.nCopies(periodos, 0D));
        for (ProgramacionFinPreinversionDetalle detalle : detalles) {
            if (detalle.getPeriodo() <= periodos) {
                montos.set(detalle.getPeriodo() - 1, detalle.getMonto().doubleValue());
            }
        }
        return montos;
    }
}
