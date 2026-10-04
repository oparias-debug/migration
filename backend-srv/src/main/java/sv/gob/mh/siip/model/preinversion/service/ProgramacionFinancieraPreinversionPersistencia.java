package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.ProgramacionFinPreinversionDetalle;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.programacion.dto.FilaProgramacionEtapaRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgramacionFinPreinversionDetalleRepository;

/**
 * CU-PRE-22.1 "Programación Financiera de Preinversión": escritura de los detalles por etapa y período y
 * sincronización del costo de cada etapa que muestra CU-PRE-03.5.
 */
final class ProgramacionFinancieraPreinversionPersistencia {

    private final EtapaPreinversionRepository etapas;
    private final ProgramacionFinPreinversionDetalleRepository detalles;

    ProgramacionFinancieraPreinversionPersistencia(EtapaPreinversionRepository etapas,
            ProgramacionFinPreinversionDetalleRepository detalles) {
        this.etapas = etapas;
        this.detalles = detalles;
    }

    /**
     * Reemplaza la programación completa del proyecto por las filas ya validadas y recalcula el costo de
     * cada etapa programable.
     */
    void reemplazar(Proyecto proyecto, Collection<EtapaPreinversion> etapasProgramables,
            Collection<FilaProgramacionEtapaRequestDto> filas) {
        detalles.deleteByProyectoId(proyecto.getId());
        // Una fila eliminada por el usuario debe eliminar también su costo en CU-PRE-03.5.
        for (EtapaPreinversion etapa : etapasProgramables) {
            etapa.setCosto(0D);
            etapas.save(etapa);
        }
        for (FilaProgramacionEtapaRequestDto fila : filas) {
            var tipo = TipoEtapaPreinversion.valueOf(fila.getEtapa().name());
            BigDecimal total = BigDecimal.ZERO;
            List<Double> montos = fila.getProgramacionPorPeriodo();
            for (var indice = 0; indice < montos.size(); indice++) {
                BigDecimal monto = ProgramacionFinancieraPreinversionMontos.monetario(montos.get(indice));
                total = total.add(monto);
                detalles.save(ProgramacionFinPreinversionDetalle.builder().proyecto(proyecto).etapa(tipo)
                        .periodo(indice + 1).monto(monto).build());
            }
            EtapaPreinversion etapa = etapas.findByProyectoIdAndTipoEtapa(proyecto.getId(), tipo).orElse(null);
            if (etapa != null) {
                etapa.setCosto(ProgramacionFinancieraPreinversionMontos.redondeado(total));
                etapas.save(etapa);
            }
        }
    }

    /**
     * Elimina los detalles fuera del horizonte de períodos o de etapas que ya no son programables, y
     * recalcula el costo de cada etapa vigente con los detalles restantes.
     */
    void depurarYRecalcular(Proyecto proyecto, Collection<EtapaPreinversion> etapasProgramables, int periodos) {
        Set<TipoEtapaPreinversion> etapasVigentes = EnumSet.noneOf(TipoEtapaPreinversion.class);
        for (EtapaPreinversion etapa : etapasProgramables) {
            etapasVigentes.add(etapa.getTipoEtapa());
        }
        List<ProgramacionFinPreinversionDetalle> invalidos = detalles.findByProyectoId(proyecto.getId()).stream()
                .filter((ProgramacionFinPreinversionDetalle detalle) -> detalle.getPeriodo() == null
                        || detalle.getPeriodo() > periodos || !etapasVigentes.contains(detalle.getEtapa()))
                .toList();
        if (!invalidos.isEmpty()) {
            // También sanea datos heredados de una etapa eliminada o de EJECUCIÓN;
            // CU-PRE-22.1 solo administra etapas vigentes de preinversión.
            detalles.deleteAll(invalidos);
        }
        Map<TipoEtapaPreinversion, BigDecimal> totales = new EnumMap<>(TipoEtapaPreinversion.class);
        for (ProgramacionFinPreinversionDetalle detalle : detalles.findByProyectoId(proyecto.getId())) {
            totales.merge(detalle.getEtapa(), detalle.getMonto(), BigDecimal::add);
        }
        for (EtapaPreinversion etapa : etapasProgramables) {
            etapa.setCosto(ProgramacionFinancieraPreinversionMontos.redondeado(
                    totales.getOrDefault(etapa.getTipoEtapa(), BigDecimal.ZERO)));
            etapas.save(etapa);
        }
    }
}
