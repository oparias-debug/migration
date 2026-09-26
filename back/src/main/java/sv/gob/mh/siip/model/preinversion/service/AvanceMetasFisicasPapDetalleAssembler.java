package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.List;

import sv.gob.mh.siip.model.preinversion.domain.AvanceCuatriMetaFisica;
import sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceCuatrimestreMetaAnteriorDto;
import sv.gob.mh.siip.model.preinversion.dto.AvanceMetasEstudioDto;
import sv.gob.mh.siip.model.preinversion.dto.EtapaAvanceMetasDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;

/**
 * CU-PRE-33 "Avance Cuatrimestral por Metas Físicas del PAP": armado del detalle del avance de un
 * estudio, una fila por etapa con su meta física, acumulados, Estado (RN-B.c) y avances de los
 * cuatrimestres anteriores (RN-G).
 */
final class AvanceMetasFisicasPapDetalleAssembler {

    private static final double META_TOTAL = 1.0D;

    private final AvancePapConsultas consultas;
    private final EtapaMetaFisicaPapRepository etapaMetaRepository;
    private final AvanceMetasFisicasPapCalculos calculos;

    AvanceMetasFisicasPapDetalleAssembler(AvancePapConsultas consultas,
            EtapaMetaFisicaPapRepository etapaMetaRepository, AvanceMetasFisicasPapCalculos calculos) {
        this.consultas = consultas;
        this.etapaMetaRepository = etapaMetaRepository;
        this.calculos = calculos;
    }

    AvanceMetasEstudioDto construirEstudioDto(Proyecto proyecto, Integer anio, Cuatrimestre periodo) {
        List<EtapaAvanceMetasDto> etapas = consultas.etapasOrdenadas(proyecto.getId()).stream()
                .map(etapa -> construirEtapaDto(etapa, anio, periodo))
                .toList();
        return new AvanceMetasEstudioDto(proyecto.getCup(), proyecto.getNombre(), etapas);
    }

    private EtapaAvanceMetasDto construirEtapaDto(EtapaPreinversion etapa, Integer anio, Cuatrimestre periodo) {
        EtapaMetaFisicaPap etapaMeta = etapaMetaRepository.findByEtapaPreinversionId(etapa.getId()).orElse(null);
        AvanceMetasFisicasPapCalculos.Datos datos = calculos.calcularDatos(etapaMeta, anio, periodo);

        return new EtapaAvanceMetasDto(NombreEtapaDto.valueOf(etapa.getTipoEtapa().name()),
                datos.ejecutadoDelPeriodo().doubleValue())
                .meta(META_TOTAL)
                .entregable((etapaMeta != null && etapaMeta.getEntregable() != null)
                        ? etapaMeta.getEntregable().name()
                        : null)
                .ejecutadoAniosAnteriores(datos.ejecutadoAnteriorONulo())
                .programadoEnElAnio(datos.programadoAnual().doubleValue())
                .ejecutadoEnElAnio(datos.ejecutadoAlPeriodo().doubleValue())
                .programadoAlCuatrimestre(datos.programadoAlPeriodo().doubleValue())
                .ejecutadoAlCuatrimestre(datos.ejecutadoAlPeriodo().doubleValue())
                .programadoDelCuatrimestre(datos.programadoDelPeriodo().doubleValue())
                .totalMetaEjecutada(datos.totalMetaEjecutada().doubleValue())
                .estado(datos.estadoDto())
                .avancesCuatrimestresAnteriores(
                        etapaMeta != null
                                ? avancesCuatrimestresAnteriores(etapaMeta.getId(), anio, periodo)
                                : List.of());
    }

    /** RN-G: vacío en Cuatrimestre I; lo ejecutado en los cuatrimestres previos al consultado en los demás casos. */
    private List<AvanceCuatrimestreMetaAnteriorDto> avancesCuatrimestresAnteriores(Long idEtapaMetaFisica,
            Integer anio, Cuatrimestre periodo) {
        List<AvanceCuatrimestreMetaAnteriorDto> resultado = new ArrayList<>();
        for (AvanceCuatriMetaFisica avance : calculos.avancesDeEtapaMeta(idEtapaMetaFisica)) {
            if (avance.getProgramacionMeta().getAnio().equals(anio)
                    && avance.getCuatrimestre().ordinal() < periodo.ordinal()) {
                resultado.add(new AvanceCuatrimestreMetaAnteriorDto(AvancePapSoporte.dtoDe(avance.getCuatrimestre()),
                        avance.getAvanceCuatrimestre().doubleValue()));
            }
        }
        resultado.sort((a, b) -> a.getPeriodo().ordinal() - b.getPeriodo().ordinal());
        return resultado;
    }
}
