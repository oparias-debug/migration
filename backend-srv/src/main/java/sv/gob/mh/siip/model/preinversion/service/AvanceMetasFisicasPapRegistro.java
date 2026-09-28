package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.util.List;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.AvanceCuatriMetaFisica;
import sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralMetaFisica;
import sv.gob.mh.siip.model.preinversion.dto.EtapaAvanceMetasRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.repository.AvanceCuatriMetaFisicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralMetaFisicaRepository;

/**
 * CU-PRE-33 "Avance Cuatrimestral por Metas Físicas del PAP": registro del avance porcentual del
 * cuatrimestre por etapa, con las validaciones del tope anual programado (RN-C.b) y del 100% de la
 * meta del estudio (RN-B.d).
 */
final class AvanceMetasFisicasPapRegistro {

    private static final String MENSAJE_LIMITE_100 = "El porcentaje total registrado supera el 100%";
    private static final String MENSAJE_NO_EXISTE = "El estudio o la etapa no existen.";

    private final AvancePapConsultas consultas;
    private final AvanceMetasFisicasPapCalculos calculos;
    private final EtapaMetaFisicaPapRepository etapaMetaRepository;
    private final ProgCuatrimestralMetaFisicaRepository progRepository;
    private final AvanceCuatriMetaFisicaRepository avanceRepository;

    AvanceMetasFisicasPapRegistro(AvancePapConsultas consultas, AvanceMetasFisicasPapCalculos calculos,
            EtapaMetaFisicaPapRepository etapaMetaRepository, ProgCuatrimestralMetaFisicaRepository progRepository,
            AvanceCuatriMetaFisicaRepository avanceRepository) {
        this.consultas = consultas;
        this.calculos = calculos;
        this.etapaMetaRepository = etapaMetaRepository;
        this.progRepository = progRepository;
        this.avanceRepository = avanceRepository;
    }

    void guardar(Long idProyecto, List<EtapaAvanceMetasRequestDto> etapas, Integer anio, Cuatrimestre periodo) {
        for (EtapaAvanceMetasRequestDto etapaRequest : AvancePapSoporte.nullSafe(etapas)) {
            EtapaPreinversion etapa = consultas.buscarEtapa(idProyecto, etapaRequest.getEtapa().name(),
                    MENSAJE_NO_EXISTE);
            guardarFila(etapa, etapaRequest, anio, periodo);
        }
    }

    private void guardarFila(EtapaPreinversion etapa, EtapaAvanceMetasRequestDto etapaRequest, Integer anio,
            Cuatrimestre periodo) {
        EtapaMetaFisicaPap etapaMeta = etapaMetaRepository.findByEtapaPreinversionId(etapa.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException(MENSAJE_NO_EXISTE));
        ProgCuatrimestralMetaFisica programacion = progRepository
                .findByEtapaMetaFisicaIdAndAnio(etapaMeta.getId(), anio)
                .orElseGet(() -> progRepository.save(
                        ProgCuatrimestralMetaFisica.builder().etapaMetaFisica(etapaMeta).anio(anio).build()));

        BigDecimal avanceCuatrimestre = BigDecimal.valueOf(etapaRequest.getAvanceCuatrimestre());
        BigDecimal ejecutadoPrevioDelAnio = periodo == Cuatrimestre.CUATRIMESTRE_I
                ? BigDecimal.ZERO
                : calculos.ejecutadoEnAnioHastaPeriodo(etapaMeta.getId(), anio,
                        Cuatrimestre.deNumero(periodo.numero() - 1));
        BigDecimal ejecutadoEnElAnio = ejecutadoPrevioDelAnio.add(avanceCuatrimestre);

        // RN-C.b: el avance del cuatrimestre no debe superar el "Programado en el Año" de la
        // programación de metas físicas (CU-PRE-31; AAP de RN-F). Se valida el acumulado del año
        // (cuatrimestres previos + el que se registra), igual que CU-PRE-32 con el monto anual
        // programado. RN-B.b: los estudios con "programado del cuatrimestre" = 0 siempre pueden
        // reportar su avance, por lo que quedan fuera de este tope (sigue aplicando RN-B.d).
        boolean exentoRnBb = AvanceMetasFisicasPapCalculos.programadoCuatrimestre(programacion, periodo).signum() == 0;
        if (!exentoRnBb && ejecutadoEnElAnio.compareTo(programacion.totalProgramadoAnio()) > 0) {
            throw new ValidacionNegocioException("PORCENTAJE_SUPERA_PROGRAMADO_ANUAL", MENSAJE_LIMITE_100, null);
        }
        // RN-B.d: el acumulado (Ejecutado años anteriores + Ejecutado en el año) no puede superar el 100%.
        BigDecimal ejecutadoAnterior = calculos.ejecutadoAniosAnteriores(etapaMeta.getId(), anio);
        if (ejecutadoAnterior.add(ejecutadoEnElAnio).compareTo(AvanceMetasFisicasPapCalculos.CIEN) > 0) {
            throw new ValidacionNegocioException("PORCENTAJE_SUPERA_100", MENSAJE_LIMITE_100, null);
        }

        AvanceCuatriMetaFisica avance = avanceRepository
                .findByProgramacionMetaIdAndCuatrimestre(programacion.getId(), periodo)
                .orElseGet(() -> AvanceCuatriMetaFisica.builder()
                        .programacionMeta(programacion)
                        .cuatrimestre(periodo)
                        .build());
        avance.setAvanceCuatrimestre(avanceCuatrimestre);
        avance.setObservaciones(etapaRequest.getObservacionesCuatrimestre());
        avance.setFechaRegistro(AvancePapSoporte.ahora());
        avanceRepository.save(avance);
    }
}
