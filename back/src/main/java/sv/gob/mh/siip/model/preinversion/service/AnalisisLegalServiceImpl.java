package sv.gob.mh.siip.model.preinversion.service;



import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.AnalisisLegal;
import sv.gob.mh.siip.model.preinversion.domain.AnalsisGestionesLegalesRequeridas;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisLegalDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisLegalRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaAnalisisLegalRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.AnalisisLegalRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de la interfaz AnalisisLegalService para el CU-PRE-16.
 * Gestiona la persistencia, consulta y cálculo automático del análisis legal de proyectos.
 *
 * @author Luis Medrano
 * @since 2026-09
 */
@Service
@Transactional
public class AnalisisLegalServiceImpl implements AnalisisLegalService {

    private static final String MSG_NO_EXISTE = " no existe.";

    private final AnalisisLegalRepository analisisLegalRepository;
    private final ProyectoRepository proyectoRepository;
    private final ActorContexto actorContexto;

    public AnalisisLegalServiceImpl(AnalisisLegalRepository analisisLegalRepository,
                                    ProyectoRepository proyectoRepository,
                                    ActorContexto actorContexto) {
        this.analisisLegalRepository = analisisLegalRepository;
        this.proyectoRepository = proyectoRepository;
        this.actorContexto = actorContexto;
    }

    /**
     * {@inheritDoc}
     *
     * @author Luis Medrano
     */
    @Override
    @Transactional(readOnly = true)
    public AnalisisLegalDto obtenerAnalisisLegal(Long idProyecto) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE);
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);

        AnalisisLegal analisisLegal = analisisLegalRepository.findByProyectoId(idProyecto)
                .orElse(null);

        if (analisisLegal == null) {
            AnalisisLegalDto dto = new AnalisisLegalDto();
            dto.setIdProyecto(idProyecto);
            dto.setRequiereAnalisisLegal(null);
            dto.setFilas(new ArrayList<>());
            dto.setTotalCostoEntregables(0.0);
            return dto;
        }

        return mapToDto(analisisLegal);
    }

    /**
     * {@inheritDoc}
     *
     * @author Luis Medrano
     */
    @Override
    @Transactional
    public AnalisisLegalDto guardarAnalisisLegal(Long idProyecto, AnalisisLegalRequestDto analisisLegalRequestDto) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE);
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);

        // Usamos directamente el objeto 'proyecto' obtenido por buscarProyecto sin consultarlo otra vez
        AnalisisLegal analisisLegal = analisisLegalRepository.findByProyectoId(idProyecto)
                .orElseGet(() -> AnalisisLegal.builder()
                        .proyecto(proyecto)
                        .filas(new ArrayList<>())
                        .build());

        // Asignamos el valor del booleano indicador
        Boolean requiereAnalisis = analisisLegalRequestDto.getRequiereAnalisisLegal();
        analisisLegal.setRequiereAnalisisLegal(requiereAnalisis);

        // Limpieza de filas para aplicar el reemplazo completo (orphanRemoval = true)
        analisisLegal.getFilas().clear();

        // Si requiere análisis es true y vienen filas, las procesamos.
        if (Boolean.TRUE.equals(requiereAnalisis) && analisisLegalRequestDto.getFilas() != null) {
            List<AnalsisGestionesLegalesRequeridas> nuevasFilas = analisisLegalRequestDto.getFilas().stream()
                    .map(filaDto -> AnalsisGestionesLegalesRequeridas.builder()
                            .analisisLegal(analisisLegal)
                            .analisisGestionLegalRequerida(filaDto.getAnalisisGestionLegalRequerida())
                            .entregable(filaDto.getEntregable())
                            .costoEntregable(filaDto.getCostoEntregable())
                            .build())
                    .toList();

            analisisLegal.getFilas().addAll(new ArrayList<>(nuevasFilas));
        }

        AnalisisLegal savedEntity = analisisLegalRepository.save(analisisLegal);
        return mapToDto(savedEntity);
    }

    /**
     * Mapea la entidad AnalisisLegal a su respectivo DTO de lectura, calculando el total de costos.
     *
     * @param entity Entidad de dominio.
     * @return AnalisisLegalDto mapeado.
     * @author Luis Medrano
     */
    private AnalisisLegalDto mapToDto(AnalisisLegal entity) {
        List<FilaAnalisisLegalRequestDto> filasDto = entity.getFilas().stream()
                .map(fila -> {
                    FilaAnalisisLegalRequestDto dto = new FilaAnalisisLegalRequestDto();
                    dto.setAnalisisGestionLegalRequerida(fila.getAnalisisGestionLegalRequerida());
                    dto.setEntregable(fila.getEntregable());
                    dto.setCostoEntregable(fila.getCostoEntregable());
                    return dto;
                })
                .toList();

        double totalCosto = entity.getFilas().stream()
                .mapToDouble(f -> f.getCostoEntregable() != null ? f.getCostoEntregable() : 0.0)
                .sum();

        AnalisisLegalDto dto = new AnalisisLegalDto();
        dto.setIdProyecto(entity.getProyecto().getId());
        dto.setRequiereAnalisisLegal(entity.getRequiereAnalisisLegal());
        dto.setFilas(filasDto);
        dto.setTotalCostoEntregables(totalCosto);

        return dto;
    }

    private Proyecto buscarProyecto(Long idProyecto) {
        return proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException("El proyecto " + idProyecto + MSG_NO_EXISTE));
    }

    /** RN01/RN02: mismo criterio que el resto de la serie CU-PRE-06 a CU-PRE-14. */
    private void exigirAlcanceUnidadEjecutora(Usuario actor, Proyecto proyecto) {
        if (actor.getUnidadEjecutora() != null
                && !actor.getUnidadEjecutora().getId().equals(proyecto.getUnidadEjecutora().getId())) {
            throw new AccesoDenegadoException(
                    "El proyecto no pertenece a una Unidad Ejecutora dentro de las credenciales del actor.");
        }
    }
}