package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.NoAutenticadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.preinversion.dto.MatrizInteresadosDto;
import sv.gob.mh.siip.model.preinversion.dto.MatrizInteresadosRequestDto;

/**
 * Reglas de negocio de CU-PRE-06 (Analisis de Interesados): tabla "Matriz de gestion de
 * interesados" (Anexo A.1) de la seccion "Diagnostico de la Situacion Actual". Un metodo por
 * operacion del contrato (CU-PRE-06.openapi.yaml).
 */
public interface MatrizInteresadosService {

    /**
     * Consulta la matriz de gestion de interesados. A diferencia de CU-PRE-04/CU-PRE-05, ninguna
     * regla de este CU condiciona el acceso de Tecnico PRE a que la matriz ya se haya guardado al
     * menos una vez (RN02): siempre devuelve un objeto, con la lista de interesados vacia si nunca
     * se ha guardado.
     *
     * @throws NoAutenticadoException si no hay actor autenticado.
     * @throws AccesoDenegadoException si el proyecto no esta dentro del alcance de Unidad
     *         Ejecutora del actor (RN01/RN02).
     * @throws RecursoNoEncontradoException si el proyecto no existe.
     */
    MatrizInteresadosDto obtener(Long idProyecto);

    /**
     * Registra y guarda la matriz de gestion de interesados (boton "Guardar", FA-01). Ningun
     * campo es obligatorio a nivel de servidor (RN06). Reemplaza por completo la lista de
     * interesados vigente. RN05 (interesado repetido) no se valida en el servidor: se permite
     * guardar cualquier combinacion de filas, incluidas duplicadas exactas.
     *
     * @throws AccesoDenegadoException si el actor no es Tecnico URP, o el proyecto no esta en su
     *         alcance de Unidad Ejecutora (RN01).
     * @throws RecursoNoEncontradoException si el proyecto no existe.
     */
    MatrizInteresadosDto guardar(Long idProyecto, MatrizInteresadosRequestDto request);
}
