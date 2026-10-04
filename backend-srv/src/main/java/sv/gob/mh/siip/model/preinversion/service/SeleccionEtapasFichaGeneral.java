package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.stereotype.Component;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.FichaInformacionGeneralDto;
import sv.gob.mh.siip.model.preinversion.dto.IniciativaInversionDto;
import sv.gob.mh.siip.model.preinversion.dto.SeleccionCoEjecutorRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.mapper.ProyectoMapper;

/**
 * Ficha de información general de CU-PRE-03.5 (Anexo A.3): consulta (RN14, no editable) y
 * asignación de la Unidad Ejecutora Co-ejecutora (RN16).
 */
@Component
public class SeleccionEtapasFichaGeneral {

    private final SeleccionEtapasProyectos proyectos;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final ProyectoMapper proyectoMapper;

    public SeleccionEtapasFichaGeneral(SeleccionEtapasProyectos proyectos,
            UnidadEjecutoraRepository unidadEjecutoraRepository, ProyectoMapper proyectoMapper) {
        this.proyectos = proyectos;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.proyectoMapper = proyectoMapper;
    }

    /** Ficha del proyecto indicado. */
    public FichaInformacionGeneralDto obtener(Long idProyecto) {
        return construir(proyectos.buscar(idProyecto));
    }

    /**
     * @throws RecursoNoEncontradoException si el proyecto o la Unidad Ejecutora indicada no existen.
     */
    public FichaInformacionGeneralDto seleccionarCoEjecutor(Long idProyecto, SeleccionCoEjecutorRequestDto request) {
        var proyecto = proyectos.buscar(idProyecto);
        UnidadEjecutora coEjecutor = unidadEjecutoraRepository.findById(request.getIdUnidadEjecutoraCoEjecutora())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "La Unidad Ejecutora " + request.getIdUnidadEjecutoraCoEjecutora() + " no existe."));
        proyecto.setUnidadEjecutoraCoEjecutora(coEjecutor);
        proyectos.guardar(proyecto);
        return construir(proyecto);
    }

    private FichaInformacionGeneralDto construir(Proyecto proyecto) {
        boolean esGrdGrcAcc = !proyecto.getMedidasGrd().isEmpty() || !proyecto.getMedidasGrc().isEmpty()
                || !proyecto.getMedidasAcc().isEmpty();

        FichaInformacionGeneralDto dto = new FichaInformacionGeneralDto()
                .idProyecto(proyecto.getId())
                .institucion(proyectoMapper.toResumen(proyecto.getInstitucion()))
                .unidadEjecutora(proyectoMapper.toResumen(proyecto.getUnidadEjecutora()))
                .iniciativaInversion(IniciativaInversionDto.valueOf(proyecto.getIniciativaInversion().name()))
                .nombreProyecto(proyecto.getNombre())
                .montoEstimadoInversion(proyecto.getMontoEstimadoInversion())
                .sector(proyectoMapper.toResumen(proyecto.getSector()))
                .ejeTematico(proyectoMapper.toResumen(proyecto.getEjeTematico()))
                .esProyectoGrdGrcAcc(esGrdGrcAcc)
                .esProyectoEmergencia(Boolean.TRUE.equals(proyecto.getEsProyectoEmergencia()))
                .tipoEvento(proyecto.getTipoEvento())
                .numeroDecretoLegislativo(proyecto.getNumeroDecretoLegislativo())
                .descripcionProyecto(proyecto.getDescripcionProyecto());

        if (proyecto.getUnidadEjecutoraCoEjecutora() != null) {
            dto.setCoEjecutor(proyectoMapper.toResumen(proyecto.getUnidadEjecutoraCoEjecutora()));
        }
        if (proyecto.getEjePlanGobierno() != null) {
            dto.setEjePlanGobierno(proyectoMapper.toResumen(proyecto.getEjePlanGobierno()));
        }
        if (proyecto.getPlanSectorialRegional() != null) {
            dto.setPlanSectorialRegional(proyectoMapper.toResumen(proyecto.getPlanSectorialRegional()));
        }
        // RN17: solo no nulo en etapa de Ejecucion. RN18 ("se alimenta automaticamente") no tiene
        // una fuente propia distinta de este mismo monto; se proyecta el monto estimado como valor
        // provisional mientras eso se define.
        if (proyecto.getEstado() == EstadoProyecto.EN_EJECUCION) {
            dto.setMontoAjustadoEjecucion(proyecto.getMontoEstimadoInversion());
        }
        // RN15: objetivoProyecto/montoEstimadoInversion/descripcionProyecto se actualizarian segun
        // la ultima Opinion Tecnica (CU-PRE-04/17/11); mientras esos CU no alimenten la ficha se
        // devuelven los valores originales de CU-PRE-01 (objetivoProyecto queda nulo).
        return dto;
    }
}
