package sv.gob.mh.siip.model.preinversion.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import sv.gob.mh.siip.exception.FormatoArchivoNoSoportadoException;

/**
 * Almacenamiento en disco de los árboles de problemas y de objetivos (CU-PRE-04): valida el formato
 * PDF, guarda, abre y elimina el archivo de cada proyecto.
 *
 * <p>No valida roles ni el estado del proyecto: eso lo hace {@link IdentificacionArboles} antes de
 * invocarlo.
 */
@Component
public class IdentificacionArchivos {

    private final String directorioBase;

    public IdentificacionArchivos(
            @Value("${siip.archivos.directorio-base:${java.io.tmpdir}/siip-archivos}") String directorioBase) {
        this.directorioBase = directorioBase;
    }

    /**
     * @param archivo archivo recibido
     * @throws FormatoArchivoNoSoportadoException si no es un PDF (por tipo de contenido o extensión)
     */
    public void validarFormatoPdf(MultipartFile archivo) {
        String contentType = archivo.getContentType();
        String nombre = archivo.getOriginalFilename();
        boolean esPdf = "application/pdf".equalsIgnoreCase(contentType)
                || (nombre != null && nombre.toLowerCase(Locale.ROOT).endsWith(".pdf"));
        if (!esPdf) {
            throw new FormatoArchivoNoSoportadoException("El archivo debe estar en formato PDF/A.");
        }
    }

    /**
     * Guarda el archivo con un nombre fijo por proyecto: cargar uno nuevo reemplaza al anterior
     * (RNB-1/RNB-2).
     *
     * @param idProyecto identificador del proyecto
     * @param archivoEnDisco nombre fijo del archivo en disco
     * @param archivo archivo recibido
     * @return la ruta en disco donde quedó guardado
     */
    public String guardar(Long idProyecto, String archivoEnDisco, MultipartFile archivo) {
        var ruta = Path.of(directorioBase, "identificacion", String.valueOf(idProyecto), archivoEnDisco);
        try {
            Files.createDirectories(ruta.getParent());
            archivo.transferTo(ruta);
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo almacenar el archivo cargado.", ex);
        }
        return ruta.toString();
    }

    /**
     * @param ruta ruta en disco del archivo
     * @return el contenido del archivo, o vacío si ya no existe en disco
     */
    public Optional<Resource> abrir(String ruta) {
        Resource recurso = new FileSystemResource(ruta);
        return recurso.exists() ? Optional.of(recurso) : Optional.empty();
    }

    /**
     * @param ruta ruta en disco del archivo; si ya no existe, no hace nada
     */
    public void eliminar(String ruta) {
        try {
            Files.deleteIfExists(Path.of(ruta));
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo eliminar el archivo almacenado.", ex);
        }
    }
}
