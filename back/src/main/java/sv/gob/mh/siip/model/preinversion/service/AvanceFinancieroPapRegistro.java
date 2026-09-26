package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.util.List;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.AvanceFinancieroCuatrimestral;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.FuenteFinanciamientoEtapaPap;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralFinanciera;
import sv.gob.mh.siip.model.preinversion.dto.EtapaAvanceRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaAvanceFuenteRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.repository.AvanceFinancieroCuatrimestralRepository;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;

/**
 * CU-PRE-32 "Avance Financiero Cuatrimestral del PAP": registro del monto ejecutado del
 * cuatrimestre por etapa y fuente de financiamiento, con la validación del tope anual programado.
 */
final class AvanceFinancieroPapRegistro {

    private static final String MENSAJE_LIMITE_CUATRIMESTRE =
            "Error. El monto del avance del cuatrimestre no debe superar el monto anual programado";
    private static final String MENSAJE_NO_EXISTE = "El estudio, la etapa o la fuente no existen.";

    private final AvancePapConsultas consultas;
    private final AvanceFinancieroPapCalculos calculos;
    private final FuenteFinanciamientoEtapaPapRepository fuenteRepository;
    private final ProgCuatrimestralFinancieraRepository progRepository;
    private final AvanceFinancieroCuatrimestralRepository avanceRepository;

    AvanceFinancieroPapRegistro(AvancePapConsultas consultas, AvanceFinancieroPapCalculos calculos,
            FuenteFinanciamientoEtapaPapRepository fuenteRepository,
            ProgCuatrimestralFinancieraRepository progRepository,
            AvanceFinancieroCuatrimestralRepository avanceRepository) {
        this.consultas = consultas;
        this.calculos = calculos;
        this.fuenteRepository = fuenteRepository;
        this.progRepository = progRepository;
        this.avanceRepository = avanceRepository;
    }

    void guardar(Long idProyecto, List<EtapaAvanceRequestDto> etapas, Integer anio, Cuatrimestre periodo) {
        for (EtapaAvanceRequestDto etapaRequest : AvancePapSoporte.nullSafe(etapas)) {
            EtapaPreinversion etapa = consultas.buscarEtapa(idProyecto, etapaRequest.getEtapa().name(),
                    MENSAJE_NO_EXISTE);
            for (FilaAvanceFuenteRequestDto fila : AvancePapSoporte.nullSafe(etapaRequest.getFuentes())) {
                guardarFila(etapa, fila, anio, periodo);
            }
        }
    }

    private void guardarFila(EtapaPreinversion etapa, FilaAvanceFuenteRequestDto fila, Integer anio,
            Cuatrimestre periodo) {
        FuenteFinanciamientoEtapaPap fuente = fuenteRepository.findById(fila.getIdFuente())
                .filter(f -> f.getEtapaPreinversion().getId().equals(etapa.getId()))
                .orElseThrow(() -> new RecursoNoEncontradoException(MENSAJE_NO_EXISTE));
        ProgCuatrimestralFinanciera programacion = progRepository.findByFuenteIdAndAnio(fuente.getId(), anio)
                .orElseGet(() -> progRepository.save(
                        ProgCuatrimestralFinanciera.builder().fuente(fuente).anio(anio).build()));

        BigDecimal montoEjecutado = BigDecimal.valueOf(fila.getMontoEjecutadoCuatrimestre());
        BigDecimal ejecutadoAnualProgramado = programacion.totalProgramadoAnio();
        BigDecimal ejecutadoPrevioDelAnio = periodo == Cuatrimestre.CUATRIMESTRE_I
                ? BigDecimal.ZERO
                : calculos.ejecutadoEnAnioHastaPeriodo(fuente.getId(), anio,
                        Cuatrimestre.deNumero(periodo.numero() - 1));
        if (ejecutadoPrevioDelAnio.add(montoEjecutado).compareTo(ejecutadoAnualProgramado) > 0) {
            throw new ValidacionNegocioException("MONTO_SUPERA_PROGRAMADO_ANUAL", MENSAJE_LIMITE_CUATRIMESTRE, null);
        }

        AvanceFinancieroCuatrimestral avance = avanceRepository
                .findByProgramacionIdAndCuatrimestre(programacion.getId(), periodo)
                .orElseGet(() -> AvanceFinancieroCuatrimestral.builder()
                        .programacion(programacion)
                        .cuatrimestre(periodo)
                        .build());
        avance.setMontoEjecutado(montoEjecutado);
        avance.setObservaciones(fila.getObservacionesCuatrimestre());
        avance.setFechaRegistro(AvancePapSoporte.ahora());
        avanceRepository.save(avance);
    }
}
