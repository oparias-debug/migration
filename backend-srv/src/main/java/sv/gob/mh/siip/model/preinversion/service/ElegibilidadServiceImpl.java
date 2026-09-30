package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.preinversion.dto.EmitirElegibilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaElegibilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarCalificacionElegibilidadRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.GuardarCalificacionElegibilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.RespuestaCriterioElegibilidadDto;
import sv.gob.mh.siip.model.preinversion.repository.CalificacionCriterioElegibilidadRepository;

/**
 * Implementación de CU-PRE-25 "Elegibilidad".
 *
 * <p>
 * Orquesta los colaboradores del caso de uso: {@link ElegibilidadAcceso} abre
 * cada operación y
 * deriva si la ficha está habilitada, {@link ElegibilidadCalificacion} valida y
 * guarda la
 * calificación, {@link ElegibilidadEmision} emite la Elegibilidad y
 * {@link CriteriosFichaElegibilidad}
 * provee los criterios vigentes de la ficha.
 */
@Service
@Transactional
public class ElegibilidadServiceImpl implements ElegibilidadService {

    private final ElegibilidadAcceso acceso;
    private final CriteriosFichaElegibilidad criterios;
    private final CalificacionCriterioElegibilidadRepository calificaciones;
    private final ElegibilidadCalificacion calificacion;
    private final ElegibilidadEmision emision;

    public ElegibilidadServiceImpl(ElegibilidadAcceso acceso,
            CriteriosFichaElegibilidad criterios,
            CalificacionCriterioElegibilidadRepository calificaciones,
            ElegibilidadCalificacion calificacion,
            ElegibilidadEmision emision) {
        this.acceso = acceso;
        this.criterios = criterios;
        this.calificaciones = calificaciones;
        this.calificacion = calificacion;
        this.emision = emision;
    }

    @Override
    @Transactional(readOnly = true)
    public FichaElegibilidadResponseDto consultarFicha(Long idProyecto) {
        ElegibilidadContexto contexto = acceso.paraConsulta(idProyecto);
        return ElegibilidadRespuestas.ficha(contexto, criterios.cargar(), calificaciones.findByProyectoId(idProyecto));
    }

    @Override
    public GuardarCalificacionElegibilidadResponseDto guardarCalificacion(Long idProyecto,
            GuardarCalificacionElegibilidadRequestDto request) {
        ElegibilidadContexto contexto = acceso.paraViabilizador(idProyecto);
        CriteriosVigentesElegibilidad vigentes = criterios.cargar();
        List<RespuestaCriterioElegibilidadDto> respuestas = calificacion.guardar(contexto, request).stream()
                .map(c -> ElegibilidadRespuestas.respuesta(c, vigentes))
                .toList();
        return new GuardarCalificacionElegibilidadResponseDto(new ArrayList<>(respuestas), contexto.acciones());
    }

    @Override
    public EmitirElegibilidadResponseDto emitirElegibilidad(Long idProyecto) {
        ElegibilidadContexto contexto = acceso.paraViabilizador(idProyecto);
        emision.emitir(contexto);
        return new EmitirElegibilidadResponseDto(contexto.proyecto().getId(),
                contexto.proyecto().getEstado().getEtiquetaUi());
    }
}
