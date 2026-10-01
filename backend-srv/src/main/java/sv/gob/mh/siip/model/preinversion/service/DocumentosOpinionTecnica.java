package sv.gob.mh.siip.model.preinversion.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import sv.gob.mh.siip.exception.ReglaNegocioException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.DocumentoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoDocumentoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.repository.DocumentoOpinionTecnicaRepository;

/**
 * Almacenamiento de las notas de una gestión de Opinión Técnica (CU-PRE-26): "Nota de solicitud de
 * OT" (RN04) y "Nota de OT" (FA01 paso 1.5). Mismo directorio base que los documentos de CU-PRE-24.
 */
@Component
@Transactional
public class DocumentosOpinionTecnica {

    public static final String NOTA_SOLICITUD_OT_REQUERIDA = "NOTA_SOLICITUD_OT_REQUERIDA";

    /** Solo se conserva en disco una extensión alfanumérica corta (ASCII); cualquier otra se descarta. */
    private static final int LONGITUD_MAXIMA_EXTENSION = 10;

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private final DocumentoOpinionTecnicaRepository repositorio;
    private final String directorioBase;

    public DocumentosOpinionTecnica(DocumentoOpinionTecnicaRepository repositorio,
            @Value("${siip.archivos.directorio-base:${java.io.tmpdir}/siip-archivos}") String directorioBase) {
        this.repositorio = repositorio;
        this.directorioBase = directorioBase;
    }

    /**
     * RN04: el botón "Solicitar OT" se activa con la "Nota de solicitud de OT" anexada.
     *
     * @param archivo archivo recibido
     * @throws ReglaNegocioException (422) si no llegó la nota
     */
    public static void exigirNotaSolicitud(MultipartFile archivo) {
        if (!tieneContenido(archivo)) {
            throw new ReglaNegocioException(NOTA_SOLICITUD_OT_REQUERIDA,
                    "Debe anexar la Nota de solicitud de OT para solicitar la Opinión Técnica.",
                    List.of(new ErrorDetalleDto().campo("notaSolicitudOt").mensaje("Archivo requerido.")));
        }
    }

    /**
     * @param archivo archivo recibido
     * @return si llegó un archivo con contenido
     */
    public static boolean tieneContenido(MultipartFile archivo) {
        return archivo != null && !archivo.isEmpty();
    }

    /**
     * Guarda una nota de la gestión. Cada tipo es único por gestión, así que reemplaza a la vigente.
     *
     * @param gestion gestión de OT dueña del documento
     * @param tipoDocumento tipo de nota
     * @param archivo archivo recibido, con contenido
     * @param usuarioCarga actor que la carga
     * @return el documento registrado
     * @throws ValidacionNegocioException (400) si el archivo no tiene nombre
     */
    public DocumentoOpinionTecnica cargar(OpinionTecnica gestion, TipoDocumentoOpinionTecnica tipoDocumento,
            MultipartFile archivo, Usuario usuarioCarga) {
        String nombre = nombreLimpio(archivo, campo(tipoDocumento));
        repositorio.findFirstByOpinionTecnicaIdAndTipoDocumento(gestion.getId(), tipoDocumento)
                .ifPresent(this::eliminar);
        Path ruta = Path.of(directorioBase, "opinion-tecnica", String.valueOf(gestion.getProyecto().getId()),
                tipoDocumento.name().toLowerCase(Locale.ROOT) + "-" + UUID.randomUUID() + sufijoExtension(nombre));
        guardarBytes(ruta, archivo);
        return repositorio.save(DocumentoOpinionTecnica.builder()
                .opinionTecnica(gestion)
                .tipoDocumento(tipoDocumento)
                .nombreArchivo(nombre)
                .rutaArchivo(ruta.toString())
                .fechaCarga(LocalDateTime.now(ZONA_EL_SALVADOR))
                .usuarioCarga(usuarioCarga)
                .build());
    }

    /**
     * @param idGestion identificador de la gestión
     * @return las notas de la gestión en el orden en que se cargaron
     */
    @Transactional(readOnly = true)
    public List<DocumentoOpinionTecnica> listar(Long idGestion) {
        return repositorio.findByOpinionTecnicaIdOrderByIdAsc(idGestion);
    }

    /** Nombre de la parte multipart en el contrato, para el detalle de los errores. */
    private static String campo(TipoDocumentoOpinionTecnica tipoDocumento) {
        return tipoDocumento == TipoDocumentoOpinionTecnica.NOTA_SOLICITUD_OT ? "notaSolicitudOt" : "notaOt";
    }

    private static String nombreLimpio(MultipartFile archivo, String campo) {
        String nombre = archivo.getOriginalFilename();
        Path soloNombre = nombre == null ? null : Path.of(nombre.replace('\\', '/')).getFileName();
        String limpio = soloNombre == null ? "" : soloNombre.toString();
        if (limpio.isBlank()) {
            String mensaje = "El archivo debe tener nombre.";
            throw new ValidacionNegocioException(DocumentosViabilidad.SOLICITUD_INVALIDA, mensaje,
                    List.of(new ErrorDetalleDto().campo(campo).mensaje(mensaje)));
        }
        return limpio;
    }

    private void eliminar(DocumentoOpinionTecnica documento) {
        try {
            Files.deleteIfExists(Path.of(documento.getRutaArchivo()));
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo eliminar la nota reemplazada.", ex);
        }
        repositorio.delete(documento);
    }

    private static void guardarBytes(Path ruta, MultipartFile archivo) {
        try {
            Files.createDirectories(ruta.getParent());
            archivo.transferTo(ruta);
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo almacenar el archivo cargado.", ex);
        }
    }

    private static String sufijoExtension(String nombre) {
        int punto = nombre.lastIndexOf('.');
        String extension = punto < 0 ? "" : nombre.substring(punto + 1).toLowerCase(Locale.ROOT);
        return extensionSegura(extension) ? ("." + extension) : "";
    }

    private static boolean extensionSegura(String extension) {
        return !extension.isEmpty() && extension.length() <= LONGITUD_MAXIMA_EXTENSION
                && extension.chars().allMatch((int c) -> (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9'));
    }
}
