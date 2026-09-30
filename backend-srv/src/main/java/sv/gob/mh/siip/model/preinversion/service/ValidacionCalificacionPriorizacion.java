package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sv.gob.mh.siip.exception.ReglaNegocioException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.CriterioPriorizacion;
import sv.gob.mh.siip.model.preinversion.domain.SubcriterioPriorizacion;
import sv.gob.mh.siip.model.preinversion.dto.CalificacionSubcriterioRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.enums.TramoPriorizacion;

/** Validaciones de la calificación de un tramo de la matriz multicriterio (CU-PRE-26.5, RN01 y RN04). */
final class ValidacionCalificacionPriorizacion {

    public static final String SUBCRITERIO_FUERA_DE_ALCANCE = "SUBCRITERIO_FUERA_DE_ALCANCE";
    public static final String CALIFICACION_INCOMPLETA = "CALIFICACION_INCOMPLETA";

    /** Mensaje literal de RN04. */
    public static final String MENSAJE_CALIFICACION_INCOMPLETA =
            "Error. Debe seleccionar un puntaje para cada subcriterio";

    private ValidacionCalificacionPriorizacion() {
    }

    /**
     * Los subcriterios deben existir, no repetirse y ser del tramo del Técnico (RN01).
     *
     * @param tramo tramo que se califica
     * @param matriz matriz del proyecto
     * @param recibidas calificaciones de la solicitud
     */
    static void exigirDelTramo(TramoPriorizacion tramo, MatrizPriorizacion.Matriz matriz,
            List<CalificacionSubcriterioRequestDto> recibidas) {
        Set<String> delCatalogo = new HashSet<>();
        for (CriterioPriorizacion criterio : matriz.criterios()) {
            criterio.getSubcriterios().forEach((SubcriterioPriorizacion s) -> delCatalogo.add(s.getNumero()));
        }
        Set<String> propios = new HashSet<>();
        matriz.subcriterios(tramo).forEach((SubcriterioPriorizacion s) -> propios.add(s.getNumero()));
        Set<String> vistos = new HashSet<>();
        List<ErrorDetalleDto> invalidos = new ArrayList<>();
        List<ErrorDetalleDto> ajenos = new ArrayList<>();
        for (CalificacionSubcriterioRequestDto recibida : recibidas) {
            String numero = recibida.getSubcriterioNumero();
            boolean valido = delCatalogo.contains(numero) && vistos.add(numero);
            if (!valido) {
                invalidos.add(detalle(numero, "Subcriterio inexistente o repetido."));
            }
            if (valido && !propios.contains(numero)) {
                ajenos.add(detalle(numero, "Subcriterio fuera del alcance del Técnico (" + tramo.getDescripcion()
                        + ")."));
            }
        }
        if (!invalidos.isEmpty()) {
            throw new ValidacionNegocioException(DocumentosViabilidad.SOLICITUD_INVALIDA,
                    "Las calificaciones no corresponden a los subcriterios de la matriz.", invalidos);
        }
        if (!ajenos.isEmpty()) {
            throw new ReglaNegocioException(SUBCRITERIO_FUERA_DE_ALCANCE,
                    "El usuario no puede calificar subcriterios de este criterio.", ajenos);
        }
    }

    /**
     * RN04: cada subcriterio del tramo debe tener un puntaje para calificar la priorización.
     *
     * @param tramo tramo que se califica
     * @param matriz matriz del proyecto con las calificaciones registradas
     */
    static void exigirCompleta(TramoPriorizacion tramo, MatrizPriorizacion.Matriz matriz) {
        List<ErrorDetalleDto> faltantes = new ArrayList<>();
        for (SubcriterioPriorizacion subcriterio : matriz.subcriterios(tramo)) {
            if (matriz.valor(subcriterio) == null) {
                faltantes.add(detalle(subcriterio.getNumero(), "Subcriterio sin puntaje."));
            }
        }
        if (!faltantes.isEmpty()) {
            throw new ReglaNegocioException(CALIFICACION_INCOMPLETA, MENSAJE_CALIFICACION_INCOMPLETA, faltantes);
        }
    }

    private static ErrorDetalleDto detalle(String numero, String mensaje) {
        return new ErrorDetalleDto().campo("calificaciones[" + numero + "]").mensaje(mensaje);
    }
}
