package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.NoAutenticadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.dto.RegistroAlternativasDto;
import sv.gob.mh.siip.model.preinversion.dto.RegistroAlternativasRequestDto;

/**
 * Reglas de negocio de CU-PRE-05 (Alternativas de Solución): sección "Registro de Alternativas"
 * de la pestaña "Identificación del proyecto". Un método por operación del contrato
 * (CU-PRE-05.openapi.yaml).
 */
public interface AlternativaSolucionService {

    /**
     * Consulta el registro de alternativas de solución (Anexo A.1). RN1-2/RN1-3: si el actor no
     * es Técnico URP y la información nunca se ha guardado, no existe un objeto que devolver.
     *
     * @throws NoAutenticadoException si no hay actor autenticado.
     * @throws AccesoDenegadoException si el proyecto no está dentro del alcance de Unidad
     *         Ejecutora del actor (RN1-1 para Técnico URP, RN1-3 para Usuarios Internos/Externos).
     * @throws RecursoNoEncontradoException si el proyecto no existe, o (para actores distintos de
     *         Técnico URP) la información todavía no se ha guardado ni una sola vez.
     */
    RegistroAlternativasDto obtener(Long idProyecto);

    /**
     * Registra y guarda las alternativas de solución (botón "Guardar", SF-1). Ningún campo es
     * obligatorio a nivel de servidor. Reemplaza por completo la lista de alternativas.
     *
     * @throws AccesoDenegadoException si el actor no es Técnico URP, o el proyecto no está en su
     *         alcance de Unidad Ejecutora (RN1-1).
     * @throws RecursoNoEncontradoException si el proyecto no existe.
     */
    RegistroAlternativasDto guardar(Long idProyecto, RegistroAlternativasRequestDto request);

    /**
     * Valida el último guardado contra RN2-4 (al menos una alternativa) y RN2-3 (justificación
     * obligatoria si hay exactamente una), en ese orden, y avanza a "Análisis de interesados"
     * (botón "Siguiente", SF-2). No modifica el estado del proyecto: es navegación entre secciones
     * de un mismo formulario.
     *
     * @throws AccesoDenegadoException si el actor no es Técnico URP.
     * @throws RecursoNoEncontradoException si el proyecto no existe.
     * @throws ValidacionNegocioException si no hay ninguna alternativa registrada (RN2-4), o hay
     *         exactamente una y la justificación está vacía (RN2-3).
     */
    RegistroAlternativasDto avanzarAAnalisisInteresados(Long idProyecto);
}
