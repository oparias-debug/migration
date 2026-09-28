package sv.gob.mh.siip.model.administracion.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.NoAutenticadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.administracion.dto.CatalogRecordCreateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogRecordDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogRecordUpdateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.InactivationRequestDto;

/** CU-ADM-01: administracion de registros pertenecientes a un catalogo, tag CatalogRecord del contrato. */
public interface RegistroService {

    /**
     * @throws NoAutenticadoException actor no autenticado (401)
     * @throws AccesoDenegadoException actor sin el rol requerido (403)
     * @throws RecursoNoEncontradoException el catalogo indicado no existe
     * @throws ValidacionNegocioException falta el valor de algun campo definido, incluyendo el KEY (reglas 1, 8)
     */
    CatalogRecordDto crear(String codigoCatalogo, CatalogRecordCreateRequestDto request);

    /** @throws RecursoNoEncontradoException el catalogo indicado no existe */
    Page<CatalogRecordDto> buscarLista(String codigoCatalogo, List<String> campos, Pageable pageable);

    /**
     * @throws RecursoNoEncontradoException el catalogo o el registro indicado no existe, o algun
     *     campo solicitado no existe en el catalogo (regla 4)
     */
    CatalogRecordDto buscarPorClave(String codigoCatalogo, String key, List<String> campos);

    /**
     * @throws RecursoNoEncontradoException el catalogo o el registro indicado no existe
     * @throws ValidacionNegocioException se intento modificar el valor del campo KEY (regla 16)
     */
    CatalogRecordDto actualizar(String codigoCatalogo, String key, CatalogRecordUpdateRequestDto request);

    /** @throws ValidacionNegocioException siempre: un registro no puede eliminarse, solo inactivarse (regla 11) */
    void eliminar(String codigoCatalogo, String key);

    /**
     * @throws RecursoNoEncontradoException el catalogo o el registro indicado no existe
     * @throws ValidacionNegocioException la fecha toDate provista es futura (reglas 9b/14)
     */
    CatalogRecordDto inactivar(String codigoCatalogo, String key, InactivationRequestDto request);
}
