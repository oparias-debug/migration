package sv.gob.mh.siip.model.preinversion.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.DocumentoViabilidad;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoDocumentoViabilidad;
import sv.gob.mh.siip.model.preinversion.repository.DocumentoViabilidadRepository;

/**
 * Documentos de soporte de la sección "Viabilidad" (CU-PRE-24, FB1 paso 1; RN02; Anexo B.1): valida
 * y guarda en disco los archivos que carga el Técnico URP y mantiene su referencia en base de datos.
 *
 * <p>No valida roles ni el estado de la gestión: eso lo hace {@link ViabilidadServiceImpl} antes de
 * invocarlo.
 */
@Component
@Transactional
public class DocumentosViabilidad {

    /** Código de error (400) de un archivo o tipo de documento inválido. */
    static final String SOLICITUD_INVALIDA = "SOLICITUD_INVALIDA";

    /** Nombre de la parte multipart del archivo, usado en el detalle de los errores de validación. */
    private static final String CAMPO_ARCHIVO = "archivo";

    /** Formatos admitidos para el Documento de Preinversión ("PDF/ o .doc", Anexo B.1). */
    private static final Set<String> EXTENSIONES_PREINVERSION = Set.of("pdf", "doc", "docx");

    /** Solo se conserva en disco una extensión alfanumérica corta; cualquier otra se descarta. */
    private static final Pattern EXTENSION_SEGURA = Pattern.compile("[a-z0-9]{1,10}");

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private final DocumentoViabilidadRepository repositorio;
    private final String directorioBase;

    public DocumentosViabilidad(DocumentoViabilidadRepository repositorio,
            @Value("${siip.archivos.directorio-base:${java.io.tmpdir}/siip-archivos}") String directorioBase) {
        this.repositorio = repositorio;
        this.directorioBase = directorioBase;
    }

    /**
     * Guarda un documento del proyecto. El Documento de Preinversión es único por proyecto, así que
     * reemplaza al vigente; los otros documentos se acumulan.
     *
     * @param proyecto proyecto dueño del documento
     * @param tipoDocumento tipo del documento
     * @param archivo archivo recibido
     * @param usuarioCarga Técnico URP que lo carga
     * @return el documento registrado
     * @throws ValidacionNegocioException (400) si el tipo o el archivo no son válidos
     */
    public DocumentoViabilidad cargar(Proyecto proyecto, TipoDocumentoViabilidad tipoDocumento, MultipartFile archivo,
            Usuario usuarioCarga) {
        String nombreOriginal = validar(tipoDocumento, archivo);
        if (tipoDocumento == TipoDocumentoViabilidad.DOCUMENTO_PREINVERSION) {
            repositorio.findFirstByProyectoIdAndTipoDocumento(proyecto.getId(), tipoDocumento).ifPresent(this::eliminar);
        }
        Path ruta = Path.of(directorioBase, "viabilidad", String.valueOf(proyecto.getId()),
                tipoDocumento.name().toLowerCase(Locale.ROOT) + "-" + UUID.randomUUID() + sufijoExtension(nombreOriginal));
        guardarBytes(ruta, archivo);
        return repositorio.save(DocumentoViabilidad.builder()
                .proyecto(proyecto)
                .tipoDocumento(tipoDocumento)
                .nombreArchivo(nombreOriginal)
                .rutaArchivo(ruta.toString())
                .fechaCarga(LocalDateTime.now(ZONA_EL_SALVADOR))
                .usuarioCarga(usuarioCarga)
                .build());
    }

    /**
     * @param idProyecto identificador del proyecto
     * @return documentos del proyecto en el orden en que se cargaron
     */
    @Transactional(readOnly = true)
    public List<DocumentoViabilidad> listar(Long idProyecto) {
        return repositorio.findByProyectoIdOrderByFechaCargaAscIdAsc(idProyecto);
    }

    /**
     * @param idProyecto identificador del proyecto
     * @return si el proyecto ya tiene cargado el Documento de Preinversión (RN02)
     */
    @Transactional(readOnly = true)
    public boolean tieneDocumentoPreinversion(Long idProyecto) {
        return repositorio.existsByProyectoIdAndTipoDocumento(idProyecto, TipoDocumentoViabilidad.DOCUMENTO_PREINVERSION);
    }

    /** Valida el tipo y el archivo recibidos y devuelve el nombre original, sin rutas. */
    private static String validar(TipoDocumentoViabilidad tipoDocumento, MultipartFile archivo) {
        if (tipoDocumento == null) {
            throw invalido("tipoDocumento", "Debe indicar el tipo de documento.");
        }
        if (archivo == null || archivo.isEmpty()) {
            throw invalido(CAMPO_ARCHIVO, "Debe adjuntar un archivo con contenido.");
        }
        String nombre = archivo.getOriginalFilename();
        if (nombre == null || nombre.isBlank()) {
            throw invalido(CAMPO_ARCHIVO, "El archivo debe tener nombre.");
        }
        Path soloNombre = Path.of(nombre.replace('\\', '/')).getFileName();
        String limpio = soloNombre == null ? "" : soloNombre.toString();
        if (limpio.isBlank()) {
            throw invalido(CAMPO_ARCHIVO, "El archivo debe tener nombre.");
        }
        if (tipoDocumento == TipoDocumentoViabilidad.DOCUMENTO_PREINVERSION
                && !EXTENSIONES_PREINVERSION.contains(extension(limpio))) {
            throw invalido(CAMPO_ARCHIVO, "El Documento de Preinversión debe estar en formato PDF o Word (.doc/.docx).");
        }
        return limpio;
    }

    private static ValidacionNegocioException invalido(String campo, String mensaje) {
        return new ValidacionNegocioException(SOLICITUD_INVALIDA, mensaje,
                List.of(new ErrorDetalleDto().campo(campo).mensaje(mensaje)));
    }

    private void eliminar(DocumentoViabilidad documento) {
        try {
            Files.deleteIfExists(Path.of(documento.getRutaArchivo()));
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo eliminar el documento reemplazado.", ex);
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

    private static String extension(String nombre) {
        int punto = nombre.lastIndexOf('.');
        return punto < 0 ? "" : nombre.substring(punto + 1).toLowerCase(Locale.ROOT);
    }

    private static String sufijoExtension(String nombre) {
        String extension = extension(nombre);
        return EXTENSION_SEGURA.matcher(extension).matches() ? "." + extension : "";
    }
}
