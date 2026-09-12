package sv.gob.mh.siip.model.administracion.service;

import java.util.List;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.NoAutenticadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.administracion.dto.ActualizarCatalogoRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogoHijoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.CrearCatalogoRequestDto;
import sv.gob.mh.siip.model.administracion.dto.ExistenciaCatalogoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.InactivacionRequestDto;

/** CU-ADM-01: administracion de catalogos maestros (catalogMaster). */
public interface CatalogoService {

    /**
     * @throws NoAutenticadoException actor no autenticado (401)
     * @throws AccesoDenegadoException actor sin el rol requerido (403)
     * @throws ValidacionNegocioException sin campos (regla 17), sin campo KEY (regla 2), o nombres repetidos (regla 3)
     * @throws RecursoNoEncontradoException el catalogo padre indicado no existe
     */
    CatalogoResponseDto crear(CrearCatalogoRequestDto request);

    /** @throws NoAutenticadoException, AccesoDenegadoException ver {@link #crear} */
    ExistenciaCatalogoResponseDto buscarPorNombre(String nombre);

    /** @throws NoAutenticadoException, AccesoDenegadoException ver {@link #crear} */
    List<CatalogoHijoResponseDto> buscarHijos(String codigoCatalogo);

    /**
     * @throws RecursoNoEncontradoException el catalogo indicado no existe
     * @throws ConflictoEstadoException se intento modificar los campos de un catalogo que ya tiene registros (regla 18)
     */
    CatalogoResponseDto actualizar(String codigoCatalogo, ActualizarCatalogoRequestDto request);

    /**
     * @throws RecursoNoEncontradoException el catalogo indicado no existe
     * @throws ValidacionNegocioException la fecha toDate provista es futura (regla 8b)
     */
    CatalogoResponseDto inactivar(String codigoCatalogo, InactivacionRequestDto request);
}
