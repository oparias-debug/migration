package sv.gob.mh.siip.model.administracion.service;

import java.util.List;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.NoAutenticadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.administracion.dto.ActualizarRegistroRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CrearRegistroRequestDto;
import sv.gob.mh.siip.model.administracion.dto.InactivacionRequestDto;
import sv.gob.mh.siip.model.administracion.dto.ListaRegistrosResponseDto;
import sv.gob.mh.siip.model.administracion.dto.RegistroResponseDto;
import sv.gob.mh.siip.model.administracion.dto.RegistroValoresResponseDto;

/** CU-ADM-01: administracion de registros pertenecientes a un catalogo. */
public interface RegistroService {

    /**
     * @throws NoAutenticadoException actor no autenticado (401)
     * @throws AccesoDenegadoException actor sin el rol requerido (403)
     * @throws RecursoNoEncontradoException el catalogo indicado no existe
     * @throws ValidacionNegocioException falta el valor de algun campo definido, incluyendo el KEY (regla 7)
     * @throws ConflictoEstadoException el valor del campo KEY provisto ya existe en el catalogo
     */
    RegistroResponseDto crear(String codigoCatalogo, CrearRegistroRequestDto request);

    /** @throws RecursoNoEncontradoException el catalogo indicado no existe */
    ListaRegistrosResponseDto buscarLista(String codigoCatalogo, List<String> campos);

    /**
     * @throws RecursoNoEncontradoException el catalogo o el registro indicado no existe
     * @throws ValidacionNegocioException algun campo solicitado no existe en el catalogo
     */
    RegistroValoresResponseDto buscarPorClave(String codigoCatalogo, String key, List<String> campos);

    /**
     * @throws RecursoNoEncontradoException el catalogo o el registro indicado no existe
     * @throws ValidacionNegocioException se intento modificar el valor del campo KEY (regla 15)
     */
    RegistroResponseDto actualizar(String codigoCatalogo, String key, ActualizarRegistroRequestDto request);

    /**
     * @throws RecursoNoEncontradoException el catalogo o el registro indicado no existe
     * @throws ValidacionNegocioException la fecha toDate provista es futura (regla 8b)
     */
    RegistroResponseDto inactivar(String codigoCatalogo, String key, InactivacionRequestDto request);
}
