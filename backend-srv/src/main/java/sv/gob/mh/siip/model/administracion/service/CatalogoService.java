package sv.gob.mh.siip.model.administracion.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.NoAutenticadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.administracion.dto.CatalogCreateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogDescriptorsUpdateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogExistenceResponseDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogFieldsUpdateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogSummaryDto;
import sv.gob.mh.siip.model.administracion.dto.InactivationRequestDto;

/** CU-ADM-01: administracion de catalogos maestros (catalogMaster), tag Catalog del contrato. */
public interface CatalogoService {

    /**
     * @throws NoAutenticadoException actor no autenticado (401)
     * @throws AccesoDenegadoException actor sin el rol requerido (403)
     * @throws ValidacionNegocioException sin campos (regla 18), sin campo KEY (regla 2), o nombres repetidos (regla 3)
     * @throws RecursoNoEncontradoException el catalogo padre indicado no existe
     */
    CatalogDto crear(CatalogCreateRequestDto request);

    /** @throws NoAutenticadoException, AccesoDenegadoException ver {@link #crear} */
    Page<CatalogDto> listar(Pageable pageable);

    /** @throws NoAutenticadoException, AccesoDenegadoException ver {@link #crear} */
    CatalogExistenceResponseDto verificarExistencia(String nombre);

    /** @throws RecursoNoEncontradoException el catalogo indicado no existe (regla 21) */
    CatalogDto consultar(String codigoCatalogo);

    /**
     * @throws RecursoNoEncontradoException el catalogo indicado, o el padre indicado, no existe
     * @throws ValidacionNegocioException el payload no trae ningun descriptor a actualizar
     */
    CatalogDto actualizarDescriptores(String codigoCatalogo, CatalogDescriptorsUpdateRequestDto request);

    /** @throws ValidacionNegocioException siempre: un catalogo no puede eliminarse (regla 10) */
    void eliminar(String codigoCatalogo);

    /**
     * @throws RecursoNoEncontradoException el catalogo indicado no existe
     * @throws ConflictoEstadoException el catalogo ya tiene registros (regla 19)
     * @throws ValidacionNegocioException sin campo KEY (regla 2) o nombres repetidos (regla 3)
     */
    CatalogDto actualizarCampos(String codigoCatalogo, CatalogFieldsUpdateRequestDto request);

    /**
     * @throws RecursoNoEncontradoException el catalogo indicado no existe
     * @throws ValidacionNegocioException la fecha toDate provista es futura (reglas 9b/14)
     */
    CatalogDto inactivar(String codigoCatalogo, InactivationRequestDto request);

    /** @throws RecursoNoEncontradoException el catalogo (padre) indicado no existe */
    List<CatalogSummaryDto> consultarHijos(String codigoCatalogo);
}
