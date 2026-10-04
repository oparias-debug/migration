package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.stereotype.Component;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;

/**
 * Localiza el proyecto sobre el que operan las pantallas de CU-PRE-03.5 (Selección y Registro de
 * Etapas), con el mismo mensaje de "no existe" para todas ellas.
 */
@Component
public class SeleccionEtapasProyectos {

    private static final String PREFIJO_PROYECTO = "El proyecto ";

    private final ProyectoRepository proyectoRepository;

    public SeleccionEtapasProyectos(ProyectoRepository proyectoRepository) {
        this.proyectoRepository = proyectoRepository;
    }

    /**
     * @throws RecursoNoEncontradoException si el proyecto no existe.
     */
    public Proyecto buscar(Long idProyecto) {
        return proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException(PREFIJO_PROYECTO + idProyecto + " no existe."));
    }

    /**
     * @throws RecursoNoEncontradoException si el proyecto no existe o no está categorizado como de
     *         emergencia.
     */
    public Proyecto buscarDeEmergencia(Long idProyecto) {
        var proyecto = buscar(idProyecto);
        if (!Boolean.TRUE.equals(proyecto.getEsProyectoEmergencia())) {
            throw new RecursoNoEncontradoException(PREFIJO_PROYECTO + idProyecto
                    + " no existe o no está categorizado como de emergencia.");
        }
        return proyecto;
    }

    /** Persiste los cambios del proyecto (Co-ejecutor, remisión a Viabilidad). */
    public void guardar(Proyecto proyecto) {
        proyectoRepository.save(proyecto);
    }
}
