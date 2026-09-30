package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.preinversion.domain.PriorizacionProyecto;
import sv.gob.mh.siip.model.preinversion.dto.CalificacionPriorizacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.PriorizacionResponseDto;
import sv.gob.mh.siip.model.preinversion.enums.TramoPriorizacion;

/**
 * Implementación de CU-PRE-26.5 "Priorización": {@link PriorizacionAcceso} abre cada operación,
 * {@link PriorizacionCalificacion} registra la calificación de los Técnicos, {@link PriorizacionRevision}
 * la de los Coordinadores y {@link PriorizacionEnsamblador} arma la respuesta.
 */
@Service
@Transactional
public class PriorizacionServiceImpl implements PriorizacionService {

    private final PriorizacionAcceso acceso;
    private final PriorizacionCalificacion calificacion;
    private final PriorizacionRevision revision;
    private final PriorizacionEnsamblador ensamblador;

    public PriorizacionServiceImpl(PriorizacionAcceso acceso,
            PriorizacionCalificacion calificacion,
            PriorizacionRevision revision,
            PriorizacionEnsamblador ensamblador) {
        this.acceso = acceso;
        this.calificacion = calificacion;
        this.revision = revision;
        this.ensamblador = ensamblador;
    }

    @Override
    @Transactional(readOnly = true)
    public PriorizacionResponseDto obtener(Long idProyecto) {
        return ensamblador.pantalla(acceso.paraConsulta(idProyecto));
    }

    @Override
    public PriorizacionResponseDto guardar(Long idProyecto, TramoPriorizacion tramo,
            CalificacionPriorizacionRequestDto request) {
        PriorizacionContexto contexto = acceso.para(idProyecto, tramo.getTecnico());
        return pantalla(contexto, calificacion.guardar(contexto, tramo, request));
    }

    @Override
    public PriorizacionResponseDto calificar(Long idProyecto, TramoPriorizacion tramo,
            CalificacionPriorizacionRequestDto request) {
        PriorizacionContexto contexto = acceso.para(idProyecto, tramo.getTecnico());
        return pantalla(contexto, calificacion.calificar(contexto, tramo, request));
    }

    @Override
    public PriorizacionResponseDto revisar(Long idProyecto, TramoPriorizacion tramo) {
        PriorizacionContexto contexto = acceso.para(idProyecto, tramo.getCoordinador());
        return pantalla(contexto, revision.revisar(contexto, tramo));
    }

    @Override
    public PriorizacionResponseDto habilitarAjustes(Long idProyecto, TramoPriorizacion tramo) {
        PriorizacionContexto contexto = acceso.para(idProyecto, tramo.getCoordinador());
        revision.habilitarAjustes(contexto, tramo);
        return ensamblador.pantalla(contexto);
    }

    /** La respuesta tras un cambio: las acciones se derivan del estado resultante. */
    private PriorizacionResponseDto pantalla(PriorizacionContexto contexto, PriorizacionProyecto priorizacion) {
        return ensamblador.pantalla(new PriorizacionContexto(contexto.actor(), contexto.proyecto(),
                contexto.opinionTecnica(), priorizacion));
    }
}
