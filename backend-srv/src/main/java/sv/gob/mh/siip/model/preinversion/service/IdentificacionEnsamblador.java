package sv.gob.mh.siip.model.preinversion.service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import sv.gob.mh.siip.model.preinversion.domain.Identificacion;
import sv.gob.mh.siip.model.preinversion.domain.ObjetivoEspecifico;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ArchivoAdjuntoResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.IdentificacionDto;
import sv.gob.mh.siip.model.preinversion.dto.IdentificacionRequestDto;
import sv.gob.mh.siip.model.preinversion.mapper.ProyectoMapper;

/**
 * Traslado de datos de la sección "Identificación" (CU-PRE-04): aplica al registro lo capturado en
 * el formulario y arma el DTO de respuesta con las fechas en la zona horaria de El Salvador.
 */
@Component
public class IdentificacionEnsamblador {

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private final ProyectoMapper proyectoMapper;

    public IdentificacionEnsamblador(ProyectoMapper proyectoMapper) {
        this.proyectoMapper = proyectoMapper;
    }

    /**
     * @return fecha y hora actuales en la zona horaria de El Salvador
     */
    public static LocalDateTime ahora() {
        return LocalDateTime.now(ZONA_EL_SALVADOR);
    }

    /**
     * Copia al registro los textos y objetivos específicos del formulario (en el orden recibido) y
     * marca la fecha del último guardado.
     *
     * @param entidad registro de identificación del proyecto
     * @param request datos capturados
     */
    public void aplicar(Identificacion entidad, IdentificacionRequestDto request) {
        entidad.setAntecedentes(request.getAntecedentes());
        entidad.setProblemaCentral(request.getProblemaCentral());
        entidad.setObjetivoGeneral(request.getObjetivoGeneral());
        reemplazarObjetivosEspecificos(entidad, request.getObjetivosEspecificos());
        entidad.setFechaUltimoGuardado(ahora());
    }

    /**
     * @param proyecto proyecto consultado
     * @param entidad registro de identificación, o {@code null} si todavía no se ha guardado
     * @return la identificación del proyecto; sin registro, solo con los datos del proyecto
     */
    public IdentificacionDto aDto(Proyecto proyecto, Identificacion entidad) {
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
            dto.setArchivoArbolProblemas(resumenArchivo(entidad.getNombreArchivoArbolProblemas(),
                    entidad.getFechaCargaArbolProblemas()));
        }
        if (entidad.getNombreArchivoArbolObjetivos() != null) {
            dto.setArchivoArbolObjetivos(resumenArchivo(entidad.getNombreArchivoArbolObjetivos(),
                    entidad.getFechaCargaArbolObjetivos()));
        }
        dto.setFechaUltimoGuardado(map(entidad.getFechaUltimoGuardado()));
        return dto;
    }

    /**
     * @param nombreArchivo nombre original del archivo cargado
     * @param fechaCarga fecha de carga, en la zona horaria de El Salvador
     * @return el resumen del archivo adjunto
     */
    public ArchivoAdjuntoResumenDto resumenArchivo(String nombreArchivo, LocalDateTime fechaCarga) {
        return new ArchivoAdjuntoResumenDto(nombreArchivo, map(fechaCarga));
    }

    private static void reemplazarObjetivosEspecificos(Identificacion entidad, List<String> nuevosObjetivos) {
        entidad.getObjetivosEspecificos().clear();
        List<String> valores = nuevosObjetivos == null ? List.of() : nuevosObjetivos;
        var orden = 0;
        for (String descripcion : valores) {
            entidad.getObjetivosEspecificos().add(ObjetivoEspecifico.builder()
                    .identificacion(entidad)
                    .descripcion(descripcion)
                    .orden(orden)
                    .build());
            orden++;
        }
    }

    private static OffsetDateTime map(LocalDateTime fecha) {
        return fecha == null ? null : fecha.atZone(ZONA_EL_SALVADOR).toOffsetDateTime();
    }
}
