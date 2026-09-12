package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.NoAutenticadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisPoblacionDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisPoblacionRequestDto;

/**
 * Reglas de negocio de CU-PRE-07 (Población Objetivo): tabla "Análisis de la Población" (Anexo
 * A.1). Un metodo por operacion del contrato (CU-PRE-07.openapi.yaml).
 */
public interface AnalisisPoblacionService {

    /**
     * Consulta el analisis de la poblacion. Igual que CU-PRE-06: ninguna regla de este CU
     * condiciona el acceso de Tecnico PRE a que la informacion ya se haya guardado al menos una
     * vez: siempre devuelve un objeto, con filas vacias (incluida "Poblacion en Espera") si nunca
     * se ha guardado.
     *
     * @throws NoAutenticadoException si no hay actor autenticado.
     * @throws AccesoDenegadoException si el proyecto no esta dentro del alcance de Unidad
     *         Ejecutora del actor (RN01/RN02).
     * @throws RecursoNoEncontradoException si el proyecto no existe.
     */
    AnalisisPoblacionDto obtener(Long idProyecto);

    /**
     * Registra y guarda el analisis de la poblacion (boton "Guardar", FA-01/FA-03). RN07 (bordes
     * rojos por campos pendientes) es puramente visual: ningun campo es obligatorio a nivel de
     * servidor. La descripcion enviada para "Poblacion de Referencia" se ignora siempre (RN06).
     * Reemplaza por completo las ubicaciones vigentes de cada fila. "Poblacion en Espera" se
     * calcula enteramente en la respuesta (RN04/RN05/RN09), nunca se persiste.
     *
     * @throws AccesoDenegadoException si el actor no es Tecnico URP, o el proyecto no esta en su
     *         alcance de Unidad Ejecutora (RN01).
     * @throws RecursoNoEncontradoException si el proyecto no existe.
     * @throws ValidacionNegocioException (FA-03) si, en una misma posicion de ubicacion, la
     *         Poblacion Afectada supera a la de Referencia
     *         ({@code POBLACION_AFECTADA_MAYOR_QUE_REFERENCIA}) o la Poblacion Objetivo supera a
     *         la Afectada ({@code POBLACION_OBJETIVO_MAYOR_QUE_AFECTADA}). Solo se compara cuando
     *         ambos valores de la pareja estan presentes (un valor faltante es responsabilidad
     *         visual de RN07, no de esta validacion).
     */
    AnalisisPoblacionDto guardar(Long idProyecto, AnalisisPoblacionRequestDto request);
}
