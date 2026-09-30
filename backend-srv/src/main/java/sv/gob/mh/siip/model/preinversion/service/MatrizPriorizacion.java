package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.preinversion.domain.CalificacionSubcriterioPriorizacion;
import sv.gob.mh.siip.model.preinversion.domain.CriterioPriorizacion;
import sv.gob.mh.siip.model.preinversion.domain.PriorizacionProyecto;
import sv.gob.mh.siip.model.preinversion.domain.SubcriterioPriorizacion;
import sv.gob.mh.siip.model.preinversion.enums.TramoPriorizacion;
import sv.gob.mh.siip.model.preinversion.enums.ValorCalificacion;
import sv.gob.mh.siip.model.preinversion.repository.CalificacionSubcriterioPriorizacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.CriterioPriorizacionRepository;

/**
 * Matriz multicriterio de un proyecto (CU-PRE-26.5, Anexo A.1): los criterios y subcriterios del catálogo
 * de la DGI (CU-ADM-02) con las calificaciones registradas.
 */
@Component
@Transactional(readOnly = true)
public class MatrizPriorizacion {

    /**
     * @param criterios criterios del catálogo, en orden, con sus subcriterios
     * @param calificaciones calificaciones registradas, por id de subcriterio
     */
    public record Matriz(List<CriterioPriorizacion> criterios,
            Map<Long, CalificacionSubcriterioPriorizacion> calificaciones) {

        /**
         * @param tramo tramo de la calificación
         * @return los subcriterios que se califican en el tramo (RN01)
         */
        public List<SubcriterioPriorizacion> subcriterios(TramoPriorizacion tramo) {
            return criterios.stream()
                    .filter((CriterioPriorizacion c) -> tramo.incluye(c.getNumeroCriterio()))
                    .flatMap((CriterioPriorizacion c) -> c.getSubcriterios().stream())
                    .toList();
        }

        /**
         * @param subcriterio subcriterio del catálogo
         * @return su calificación, o {@code null} si aún no se calificó
         */
        public ValorCalificacion valor(SubcriterioPriorizacion subcriterio) {
            CalificacionSubcriterioPriorizacion calificacion = calificaciones.get(subcriterio.getId());
            return calificacion == null ? null : calificacion.getValor();
        }

        /** @return puntajes y "Prioridad del proyecto" con las calificaciones registradas (RN12 a RN14) */
        public CalculoPriorizacion.Resultado calcular() {
            return CalculoPriorizacion.calcular(criterios.stream()
                    .map((CriterioPriorizacion c) -> new CalculoPriorizacion.Criterio(c.getNumeroCriterio(),
                            c.getPonderacionCriterio(), c.getSubcriterios().stream()
                                    .map((SubcriterioPriorizacion s) -> new CalculoPriorizacion.Subcriterio(
                                            s.getNumero(), s.getPonderacionSubcriterio(), valor(s)))
                                    .toList()))
                    .toList());
        }
    }

    private final CriterioPriorizacionRepository criterios;
    private final CalificacionSubcriterioPriorizacionRepository calificaciones;

    public MatrizPriorizacion(CriterioPriorizacionRepository criterios,
            CalificacionSubcriterioPriorizacionRepository calificaciones) {
        this.criterios = criterios;
        this.calificaciones = calificaciones;
    }

    /**
     * @param priorizacion calificación del proyecto; {@code null} si aún no se empezó
     * @return la matriz con el catálogo y las calificaciones registradas
     */
    public Matriz cargar(PriorizacionProyecto priorizacion) {
        List<CriterioPriorizacion> catalogo = criterios.findAllByOrderByNumeroCriterioAsc();
        catalogo.forEach((CriterioPriorizacion c) -> c.getSubcriterios().size());
        boolean sinCalificar = priorizacion == null || priorizacion.getId() == null;
        Map<Long, CalificacionSubcriterioPriorizacion> registradas = sinCalificar ? Map.of()
                : calificaciones.findByPriorizacionId(priorizacion.getId()).stream()
                        .collect(Collectors.toMap((CalificacionSubcriterioPriorizacion c) -> c.getSubcriterio().getId(),
                                Function.identity()));
        return new Matriz(catalogo, registradas);
    }
}
