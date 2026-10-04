package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDateTime;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.preinversion.domain.Identificacion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ArchivoAdjuntoResumenDto;
import sv.gob.mh.siip.model.preinversion.repository.IdentificacionRepository;

/**
 * Árboles de problemas y de objetivos de la sección "Identificación" (CU-PRE-04): carga, descarga y
 * eliminación del PDF de cada árbol y de su referencia en el registro de identificación. La carga y
 * la eliminación quedan reservadas al Técnico URP con la formulación habilitada.
 */
@Component
public class IdentificacionArboles {

    private static final String MENSAJE_SIN_ARCHIVO_ARBOL = "No hay ningun archivo cargado en el arbol de ";

    /** Nombre fijo del archivo en disco por proyecto/árbol: cargar uno nuevo reemplaza al anterior (RNB-1/RNB-2). */
    public enum TipoArbol {
        PROBLEMAS("arbol-problemas.pdf", "problemas"),
        OBJETIVOS("arbol-objetivos.pdf", "objetivos");

        private final String archivoEnDisco;
        private final String etiqueta;

        TipoArbol(String archivoEnDisco, String etiqueta) {
            this.archivoEnDisco = archivoEnDisco;
            this.etiqueta = etiqueta;
        }
    }

    private final IdentificacionAcceso acceso;
    private final IdentificacionRepository identificacionRepository;
    private final IdentificacionArchivos archivos;
    private final IdentificacionEnsamblador ensamblador;

    public IdentificacionArboles(IdentificacionAcceso acceso, IdentificacionRepository identificacionRepository,
            IdentificacionArchivos archivos, IdentificacionEnsamblador ensamblador) {
        this.acceso = acceso;
        this.identificacionRepository = identificacionRepository;
        this.archivos = archivos;
        this.ensamblador = ensamblador;
    }

    /**
     * @param idProyecto identificador del proyecto
     * @param archivo PDF del árbol
     * @param tipo árbol que se carga
     * @return nombre original y fecha de carga del archivo
     */
    public ArchivoAdjuntoResumenDto cargar(Long idProyecto, MultipartFile archivo, TipoArbol tipo) {
        var proyecto = acceso.proyectoEditable(idProyecto);
        archivos.validarFormatoPdf(archivo);

        Identificacion entidad = identificacionRepository.findByProyectoId(proyecto.getId())
                .orElseGet(() -> Identificacion.builder().proyecto(proyecto).build());
        String ruta = archivos.guardar(idProyecto, tipo.archivoEnDisco, archivo);

        String nombreOriginal = archivo.getOriginalFilename() != null ? archivo.getOriginalFilename()
                : tipo.archivoEnDisco;
        LocalDateTime ahora = IdentificacionEnsamblador.ahora();
        if (tipo == TipoArbol.PROBLEMAS) {
            entidad.setNombreArchivoArbolProblemas(nombreOriginal);
            entidad.setRutaArchivoArbolProblemas(ruta);
            entidad.setFechaCargaArbolProblemas(ahora);
        } else {
            entidad.setNombreArchivoArbolObjetivos(nombreOriginal);
            entidad.setRutaArchivoArbolObjetivos(ruta);
            entidad.setFechaCargaArbolObjetivos(ahora);
        }
        identificacionRepository.save(entidad);

        return ensamblador.resumenArchivo(nombreOriginal, ahora);
    }

    /**
     * @param idProyecto identificador del proyecto
     * @param tipo árbol que se descarga
     * @return contenido y nombre original del archivo
     * @throws RecursoNoEncontradoException si el árbol no tiene archivo cargado o ya no está en disco
     */
    public ArchivoDescargado descargar(Long idProyecto, TipoArbol tipo) {
        acceso.proyectoConsultable(idProyecto);

        Identificacion entidad = identificacionRepository.findByProyectoId(idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        MENSAJE_SIN_ARCHIVO_ARBOL + tipo.etiqueta + "."));
        String ruta = rutaArchivo(entidad, tipo);
        if (ruta == null) {
            throw new RecursoNoEncontradoException(
                    MENSAJE_SIN_ARCHIVO_ARBOL + tipo.etiqueta + ".");
        }
        Resource recurso = archivos.abrir(ruta)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        MENSAJE_SIN_ARCHIVO_ARBOL + tipo.etiqueta + "."));
        String nombre = tipo == TipoArbol.PROBLEMAS ? entidad.getNombreArchivoArbolProblemas()
                : entidad.getNombreArchivoArbolObjetivos();
        return new ArchivoDescargado(recurso, nombre);
    }

    /**
     * @param idProyecto identificador del proyecto
     * @param tipo árbol cuyo archivo se elimina
     * @throws RecursoNoEncontradoException si el árbol no tiene archivo cargado
     */
    public void eliminar(Long idProyecto, TipoArbol tipo) {
        acceso.proyectoEditable(idProyecto);

        Identificacion entidad = identificacionRepository.findByProyectoId(idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        MENSAJE_SIN_ARCHIVO_ARBOL + tipo.etiqueta + " para eliminar."));
        String ruta = rutaArchivo(entidad, tipo);
        if (ruta == null) {
            throw new RecursoNoEncontradoException(
                    MENSAJE_SIN_ARCHIVO_ARBOL + tipo.etiqueta + " para eliminar.");
        }
        archivos.eliminar(ruta);

        if (tipo == TipoArbol.PROBLEMAS) {
            entidad.setNombreArchivoArbolProblemas(null);
            entidad.setRutaArchivoArbolProblemas(null);
            entidad.setFechaCargaArbolProblemas(null);
        } else {
            entidad.setNombreArchivoArbolObjetivos(null);
            entidad.setRutaArchivoArbolObjetivos(null);
            entidad.setFechaCargaArbolObjetivos(null);
        }
        identificacionRepository.save(entidad);
    }

    private static String rutaArchivo(Identificacion entidad, TipoArbol tipo) {
        return tipo == TipoArbol.PROBLEMAS ? entidad.getRutaArchivoArbolProblemas()
                : entidad.getRutaArchivoArbolObjetivos();
    }
}
