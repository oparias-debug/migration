package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.preinversion.domain.CriterioPriorizacion;
import sv.gob.mh.siip.model.preinversion.domain.SubcriterioPriorizacion;
import sv.gob.mh.siip.model.preinversion.dto.CategoriaPriorizacionDto;
import sv.gob.mh.siip.model.preinversion.dto.CriterioPriorizacionDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoTramoCalificacionDto;
import sv.gob.mh.siip.model.preinversion.dto.PriorizacionResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.PuntajeCriterioDto;
import sv.gob.mh.siip.model.preinversion.dto.ResultadoPriorizacionDto;
import sv.gob.mh.siip.model.preinversion.dto.SubcriterioPriorizacionDto;
import sv.gob.mh.siip.model.preinversion.dto.TramoCalificacionPriorizacionDto;
import sv.gob.mh.siip.model.preinversion.dto.ValorCalificacionSubcriterioDto;
import sv.gob.mh.siip.model.preinversion.enums.TramoPriorizacion;
import sv.gob.mh.siip.model.preinversion.enums.ValorCalificacion;

/**
 * Conversión a DTO de la priorización (CU-PRE-26.5): la matriz multicriterio (Anexo A.1) con sus
 * puntajes y, cuando la calificación está completa, la "Prioridad del proyecto" (Anexo A.2).
 */
@Component
@Transactional(readOnly = true)
public class PriorizacionEnsamblador {

    private final MatrizPriorizacion matriz;
    private final InterpretacionPriorizacion interpretacion;

    public PriorizacionEnsamblador(MatrizPriorizacion matriz, InterpretacionPriorizacion interpretacion) {
        this.matriz = matriz;
        this.interpretacion = interpretacion;
    }

    /**
     * @param contexto contexto de la operación
     * @return la priorización para el actor del contexto
     */
    public PriorizacionResponseDto pantalla(PriorizacionContexto contexto) {
        MatrizPriorizacion.Matriz actual = matriz.cargar(contexto.priorizacion());
        CalculoPriorizacion.Resultado calculo = actual.calcular();
        List<CriterioPriorizacionDto> criterios = new ArrayList<>();
        for (CriterioPriorizacion criterio : actual.criterios()) {
            criterios.add(criterio(criterio, actual, calculo));
        }
        PriorizacionResponseDto dto = new PriorizacionResponseDto(contexto.proyecto().getId(), criterios,
                tramo(contexto, TramoPriorizacion.PRE), tramo(contexto, TramoPriorizacion.SYMP),
                contexto.criteriosCalificables(), contexto.acciones());
        if (contexto.priorizacion() != null && contexto.priorizacion().estaCompleta()) {
            dto.setResultado(resultado(actual.criterios(), calculo));
        }
        return dto;
    }

    private static CriterioPriorizacionDto criterio(CriterioPriorizacion criterio, MatrizPriorizacion.Matriz actual,
            CalculoPriorizacion.Resultado calculo) {
        List<SubcriterioPriorizacionDto> subcriterios = new ArrayList<>();
        for (SubcriterioPriorizacion subcriterio : criterio.getSubcriterios()) {
            CalculoPriorizacion.SubcriterioCalculado calculado = calculo.subcriterio(subcriterio.getNumero());
            SubcriterioPriorizacionDto dto = new SubcriterioPriorizacionDto(subcriterio.getNumero(),
                    subcriterio.getNombre(), subcriterio.getPonderacionSubcriterio());
            dto.setDescripcionRequerimientoInformacion(subcriterio.getDescripcionRequerimiento());
            dto.setPonderacionSubcriterioAplicada(calculado.ponderacionAplicada());
            ValorCalificacion valor = actual.valor(subcriterio);
            dto.setCalificacion(valor == null ? null : ValorCalificacionSubcriterioDto.valueOf(valor.name()));
            dto.setPuntaje(numero(calculado.puntaje()));
            subcriterios.add(dto);
        }
        CriterioPriorizacionDto dto = new CriterioPriorizacionDto(criterio.getNumeroCriterio(),
                criterio.getNombreCriterio(), criterio.getPonderacionCriterio(), subcriterios);
        dto.setPonderacionCriterioAplicada(calculo.criterio(criterio.getNumeroCriterio()).ponderacionAplicada());
        return dto;
    }

    /** Anexo A.2: puntaje de cada criterio, "Prioridad del proyecto" y su rango de interpretación (RN09). */
    private ResultadoPriorizacionDto resultado(List<CriterioPriorizacion> criterios,
            CalculoPriorizacion.Resultado calculo) {
        List<PuntajeCriterioDto> puntajes = new ArrayList<>();
        for (CriterioPriorizacion criterio : criterios) {
            PuntajeCriterioDto puntaje = new PuntajeCriterioDto(criterio.getNumeroCriterio(),
                    criterio.getNombreCriterio());
            puntaje.setPuntaje(numero(calculo.criterio(criterio.getNumeroCriterio()).puntaje()));
            puntajes.add(puntaje);
        }
        InterpretacionPriorizacion.Interpretacion rango = interpretacion.interpretar(calculo.prioridad());
        return new ResultadoPriorizacionDto(puntajes, calculo.prioridad().doubleValue(),
                rango == null ? null : CategoriaPriorizacionDto.valueOf(rango.categoria().name()),
                rango == null ? null : rango.implicacion(), true);
    }

    private static TramoCalificacionPriorizacionDto tramo(PriorizacionContexto contexto, TramoPriorizacion tramo) {
        return new TramoCalificacionPriorizacionDto(
                EstadoTramoCalificacionDto.valueOf(contexto.tramo(tramo).getEstado().name()),
                contexto.calificable(tramo));
    }

    private static Double numero(BigDecimal valor) {
        return valor == null ? null : valor.doubleValue();
    }
}
