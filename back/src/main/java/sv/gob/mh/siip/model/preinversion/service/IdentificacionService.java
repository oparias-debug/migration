package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.web.multipart.MultipartFile;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.FormatoArchivoNoSoportadoException;
import sv.gob.mh.siip.exception.NoAutenticadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.preinversion.dto.ArchivoAdjuntoResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.IdentificacionDto;
import sv.gob.mh.siip.model.preinversion.dto.IdentificacionRequestDto;

/**
 * Reglas de negocio de CU-PRE-04 (Identificación): pestaña "Identificación del proyecto" (Anexo
 * A.1) y sus árboles de problemas/objetivos adjuntos. Un método por operación del contrato
 * (CU-PRE-04.openapi.yaml).
 */
public interface IdentificacionService {

    /**
     * Consulta la información de identificación del proyecto (Anexo A.1). RNA-2/RNA-3: si el actor
     * no es Técnico URP y la información nunca se ha guardado, no existe un objeto que devolver.
     *
     * @throws NoAutenticadoException si no hay actor autenticado.
     * @throws AccesoDenegadoException si el proyecto no está dentro del alcance de Unidad Ejecutora
     *         del actor (RNA-1 para Técnico URP, RNA-3 para Usuarios Internos/Externos).
     * @throws RecursoNoEncontradoException si el proyecto no existe, o (para actores distintos de
     *         Técnico URP) la información todavía no se ha guardado ni una sola vez.
     */
    IdentificacionDto obtener(Long idProyecto);

    /**
     * Registra y guarda la información de identificación (botón "Guardar", SF-1). Ningún campo es
     * obligatorio a nivel de servidor. Reemplaza por completo la lista de objetivos específicos.
     *
     * @throws AccesoDenegadoException si el actor no es Técnico URP, o el proyecto no está en su
     *         alcance de Unidad Ejecutora (RNA-1).
     * @throws RecursoNoEncontradoException si el proyecto no existe.
     */
    IdentificacionDto guardar(Long idProyecto, IdentificacionRequestDto request);

    /**
     * Carga o reemplaza el archivo del árbol de problemas (RNB-1).
     *
     * @throws AccesoDenegadoException si el actor no es Técnico URP.
     * @throws RecursoNoEncontradoException si el proyecto no existe.
     * @throws FormatoArchivoNoSoportadoException si el archivo no está en formato PDF/A.
     */
    ArchivoAdjuntoResumenDto cargarArbolProblemas(Long idProyecto, MultipartFile archivo);

    /**
     * Descarga el archivo del árbol de problemas.
     *
     * @throws RecursoNoEncontradoException si el proyecto no existe, o no hay archivo cargado.
     */
    ArchivoDescargado descargarArbolProblemas(Long idProyecto);

    /**
     * Elimina el archivo cargado del árbol de problemas (RNB-1).
     *
     * @throws AccesoDenegadoException si el actor no es Técnico URP.
     * @throws RecursoNoEncontradoException si el proyecto no existe, o no hay archivo para eliminar.
     */
    void eliminarArbolProblemas(Long idProyecto);

    /** Carga o reemplaza el archivo del árbol de objetivos (RNB-2). Mismas reglas que RNB-1. */
    ArchivoAdjuntoResumenDto cargarArbolObjetivos(Long idProyecto, MultipartFile archivo);

    /** Descarga el archivo del árbol de objetivos. Mismas reglas que el árbol de problemas. */
    ArchivoDescargado descargarArbolObjetivos(Long idProyecto);

    /** Elimina el archivo cargado del árbol de objetivos (RNB-2). Mismas reglas que RNB-1. */
    void eliminarArbolObjetivos(Long idProyecto);
}
