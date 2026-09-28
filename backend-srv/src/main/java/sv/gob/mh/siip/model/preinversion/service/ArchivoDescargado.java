package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.core.io.Resource;

/** Resultado de descargar el árbol de problemas/objetivos (CU-PRE-04): contenido + nombre original. */
public record ArchivoDescargado(Resource recurso, String nombreArchivo) {
}
