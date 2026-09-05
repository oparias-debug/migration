package sv.gob.mh.siip.model.preinversion.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.FormatoArchivoNoSoportadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.Identificacion;
import sv.gob.mh.siip.model.preinversion.domain.ObjetivoEspecifico;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ArchivoAdjuntoResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.IdentificacionDto;
import sv.gob.mh.siip.model.preinversion.dto.IdentificacionRequestDto;
import sv.gob.mh.siip.model.preinversion.mapper.ProyectoMapper;
import sv.gob.mh.siip.model.preinversion.repository.IdentificacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

@Service
@Transactional
public class IdentificacionServiceImpl implements IdentificacionService {

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    /** Nombre fijo del archivo en disco por proyecto/árbol: cargar uno nuevo reemplaza al anterior (RNB-1/RNB-2). */
    private enum TipoArbol {
        PROBLEMAS("arbol-problemas.pdf", "problemas"),
        OBJETIVOS("arbol-objetivos.pdf", "objetivos");

        private final String archivoEnDisco;
        private final String etiqueta;

        TipoArbol(String archivoEnDisco, String etiqueta) {
            this.archivoEnDisco = archivoEnDisco;
            this.etiqueta = etiqueta;
        }
    }

    private final ProyectoRepository proyectoRepository;
    private final IdentificacionRepository identificacionRepository;
    private final ActorContexto actorContexto;
    private final ProyectoMapper proyectoMapper;
    private final String directorioBase;

    public IdentificacionServiceImpl(ProyectoRepository proyectoRepository,
            IdentificacionRepository identificacionRepository,
            ActorContexto actorContexto,
            ProyectoMapper proyectoMapper,
            @Value("${siip.archivos.directorio-base:${java.io.tmpdir}/siip-archivos}") String directorioBase) {
        this.proyectoRepository = proyectoRepository;
        this.identificacionRepository = identificacionRepository;
        this.actorContexto = actorContexto;
        this.proyectoMapper = proyectoMapper;
        this.directorioBase = directorioBase;
    }

    @Override
    @Transactional(readOnly = true)
    public IdentificacionDto obtener(Long idProyecto) {
        Usuario actor = actorContexto.exigir();
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);

        Optional<Identificacion> entidad = identificacionRepository.findByProyectoId(idProyecto);
        if (entidad.isEmpty() && actor.getRol() != RolUsuario.TECNICO_URP) {
            // RNA-2/RNA-3: para cualquier otro actor, sin al menos un guardado previo el recurso no existe.
            throw new RecursoNoEncontradoException(
                    "La informacion de identificacion del proyecto " + idProyecto + " todavia no se ha guardado.");
        }
        return construirDto(proyecto, entidad.orElse(null));
    }

    @Override
    public IdentificacionDto guardar(Long idProyecto, IdentificacionRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);

        Identificacion entidad = obtenerOCrearEntidad(proyecto);
        entidad.setAntecedentes(request.getAntecedentes());
        entidad.setProblemaCentral(request.getProblemaCentral());
        entidad.setObjetivoGeneral(request.getObjetivoGeneral());
        reemplazarObjetivosEspecificos(entidad, request.getObjetivosEspecificos());
        entidad.setFechaUltimoGuardado(LocalDateTime.now(ZONA_EL_SALVADOR));

        entidad = identificacionRepository.save(entidad);
        return construirDto(proyecto, entidad);
    }

    @Override
    public ArchivoAdjuntoResumenDto cargarArbolProblemas(Long idProyecto, MultipartFile archivo) {
        return cargarArbol(idProyecto, archivo, TipoArbol.PROBLEMAS);
    }

    @Override
    @Transactional(readOnly = true)
    public ArchivoDescargado descargarArbolProblemas(Long idProyecto) {
        return descargarArbol(idProyecto, TipoArbol.PROBLEMAS);
    }

    @Override
    public void eliminarArbolProblemas(Long idProyecto) {
        eliminarArbol(idProyecto, TipoArbol.PROBLEMAS);
    }

    @Override
    public ArchivoAdjuntoResumenDto cargarArbolObjetivos(Long idProyecto, MultipartFile archivo) {
        return cargarArbol(idProyecto, archivo, TipoArbol.OBJETIVOS);
    }

    @Override
    @Transactional(readOnly = true)
    public ArchivoDescargado descargarArbolObjetivos(Long idProyecto) {
        return descargarArbol(idProyecto, TipoArbol.OBJETIVOS);
    }

    @Override
    public void eliminarArbolObjetivos(Long idProyecto) {
        eliminarArbol(idProyecto, TipoArbol.OBJETIVOS);
    }

    private ArchivoAdjuntoResumenDto cargarArbol(Long idProyecto, MultipartFile archivo, TipoArbol tipo) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);
        validarFormatoPdf(archivo);

        Identificacion entidad = obtenerOCrearEntidad(proyecto);
        Path ruta = resolverRutaArchivo(idProyecto, tipo);
        guardarBytes(ruta, archivo);

        String nombreOriginal = archivo.getOriginalFilename() != null ? archivo.getOriginalFilename()
                : tipo.archivoEnDisco;
        LocalDateTime ahora = LocalDateTime.now(ZONA_EL_SALVADOR);
        if (tipo == TipoArbol.PROBLEMAS) {
            entidad.setNombreArchivoArbolProblemas(nombreOriginal);
            entidad.setRutaArchivoArbolProblemas(ruta.toString());
            entidad.setFechaCargaArbolProblemas(ahora);
        } else {
            entidad.setNombreArchivoArbolObjetivos(nombreOriginal);
            entidad.setRutaArchivoArbolObjetivos(ruta.toString());
            entidad.setFechaCargaArbolObjetivos(ahora);
        }
        identificacionRepository.save(entidad);

        return new ArchivoAdjuntoResumenDto(nombreOriginal, map(ahora));
    }

    private ArchivoDescargado descargarArbol(Long idProyecto, TipoArbol tipo) {
        Usuario actor = actorContexto.exigir();
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);

        Identificacion entidad = identificacionRepository.findByProyectoId(idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No hay ningun archivo cargado en el arbol de " + tipo.etiqueta + "."));
        String ruta = tipo == TipoArbol.PROBLEMAS ? entidad.getRutaArchivoArbolProblemas()
                : entidad.getRutaArchivoArbolObjetivos();
        if (ruta == null) {
            throw new RecursoNoEncontradoException(
                    "No hay ningun archivo cargado en el arbol de " + tipo.etiqueta + ".");
        }
        Resource recurso = new FileSystemResource(ruta);
        if (!recurso.exists()) {
            throw new RecursoNoEncontradoException(
                    "No hay ningun archivo cargado en el arbol de " + tipo.etiqueta + ".");
        }
        String nombre = tipo == TipoArbol.PROBLEMAS ? entidad.getNombreArchivoArbolProblemas()
                : entidad.getNombreArchivoArbolObjetivos();
        return new ArchivoDescargado(recurso, nombre);
    }

    private void eliminarArbol(Long idProyecto, TipoArbol tipo) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);

        Identificacion entidad = identificacionRepository.findByProyectoId(idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No hay ningun archivo cargado en el arbol de " + tipo.etiqueta + " para eliminar."));
        String ruta = tipo == TipoArbol.PROBLEMAS ? entidad.getRutaArchivoArbolProblemas()
                : entidad.getRutaArchivoArbolObjetivos();
        if (ruta == null) {
            throw new RecursoNoEncontradoException(
                    "No hay ningun archivo cargado en el arbol de " + tipo.etiqueta + " para eliminar.");
        }
        eliminarBytes(ruta);

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

    private Proyecto buscarProyecto(Long idProyecto) {
        return proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException("El proyecto " + idProyecto + " no existe."));
    }

    private Identificacion obtenerOCrearEntidad(Proyecto proyecto) {
        return identificacionRepository.findByProyectoId(proyecto.getId())
                .orElseGet(() -> Identificacion.builder().proyecto(proyecto).build());
    }

    /**
     * RNA-1 (Técnico URP), RNA-2 (sin restricción para Técnico PRE) y RNA-3 (Usuarios
     * Internos/Externos, según credenciales): igual que en {@code ProyectoServiceImpl}, solo se
     * acota por Unidad Ejecutora cuando el actor autenticado tiene una asignada.
     */
    private void exigirAlcanceUnidadEjecutora(Usuario actor, Proyecto proyecto) {
        if (actor.getUnidadEjecutora() != null
                && !actor.getUnidadEjecutora().getId().equals(proyecto.getUnidadEjecutora().getId())) {
            throw new AccesoDenegadoException(
                    "El proyecto no pertenece a una Unidad Ejecutora dentro de las credenciales del actor.");
        }
    }

    private void reemplazarObjetivosEspecificos(Identificacion entidad, List<String> nuevosObjetivos) {
        entidad.getObjetivosEspecificos().clear();
        List<String> valores = nuevosObjetivos == null ? List.of() : nuevosObjetivos;
        int orden = 0;
        for (String descripcion : valores) {
            entidad.getObjetivosEspecificos().add(ObjetivoEspecifico.builder()
                    .identificacion(entidad)
                    .descripcion(descripcion)
                    .orden(orden++)
                    .build());
        }
    }

    private void validarFormatoPdf(MultipartFile archivo) {
        String contentType = archivo.getContentType();
        String nombre = archivo.getOriginalFilename();
        boolean esPdf = "application/pdf".equalsIgnoreCase(contentType)
                || (nombre != null && nombre.toLowerCase(Locale.ROOT).endsWith(".pdf"));
        if (!esPdf) {
            throw new FormatoArchivoNoSoportadoException("El archivo debe estar en formato PDF/A.");
        }
    }

    private Path resolverRutaArchivo(Long idProyecto, TipoArbol tipo) {
        return Path.of(directorioBase, "identificacion", String.valueOf(idProyecto), tipo.archivoEnDisco);
    }

    private void guardarBytes(Path ruta, MultipartFile archivo) {
        try {
            Files.createDirectories(ruta.getParent());
            archivo.transferTo(ruta);
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo almacenar el archivo cargado.", ex);
        }
    }

    private void eliminarBytes(String ruta) {
        try {
            Files.deleteIfExists(Path.of(ruta));
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo eliminar el archivo almacenado.", ex);
        }
    }

    private IdentificacionDto construirDto(Proyecto proyecto, Identificacion entidad) {
        IdentificacionDto dto = new IdentificacionDto()
                .idProyecto(proyecto.getId())
                .unidadEjecutora(proyectoMapper.toResumen(proyecto.getUnidadEjecutora()))
                .nombreProyecto(proyecto.getNombre())
                .cup(proyecto.getCup())
                .objetivosEspecificos(List.of());

        if (entidad == null) {
            return dto;
        }

        dto.setAntecedentes(entidad.getAntecedentes());
        dto.setProblemaCentral(entidad.getProblemaCentral());
        dto.setObjetivoGeneral(entidad.getObjetivoGeneral());
        dto.setObjetivosEspecificos(entidad.getObjetivosEspecificos().stream()
                .sorted(Comparator.comparing(ObjetivoEspecifico::getOrden, Comparator.nullsLast(Integer::compareTo)))
                .map(ObjetivoEspecifico::getDescripcion)
                .toList());
        if (entidad.getNombreArchivoArbolProblemas() != null) {
            dto.setArchivoArbolProblemas(new ArchivoAdjuntoResumenDto(entidad.getNombreArchivoArbolProblemas(),
                    map(entidad.getFechaCargaArbolProblemas())));
        }
        if (entidad.getNombreArchivoArbolObjetivos() != null) {
            dto.setArchivoArbolObjetivos(new ArchivoAdjuntoResumenDto(entidad.getNombreArchivoArbolObjetivos(),
                    map(entidad.getFechaCargaArbolObjetivos())));
        }
        dto.setFechaUltimoGuardado(map(entidad.getFechaUltimoGuardado()));
        return dto;
    }

    private OffsetDateTime map(LocalDateTime fecha) {
        return fecha == null ? null : fecha.atZone(ZONA_EL_SALVADOR).toOffsetDateTime();
    }
}
