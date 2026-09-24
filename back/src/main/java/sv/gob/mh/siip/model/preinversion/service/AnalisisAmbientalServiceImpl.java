package sv.gob.mh.siip.model.preinversion.service;


import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.AnalisisAmbiental;
import sv.gob.mh.siip.model.preinversion.domain.ImpactosAmbientales;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisAmbientalDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisAmbientalRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaImpactoAmbientalRequestDto;
import sv.gob.mh.siip.model.preinversion.mapper.AnalisisAmbientalMapper;
import sv.gob.mh.siip.model.preinversion.repository.AnalisisAmbientalRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de la lógica de negocio para el Análisis Ambiental y Permisos Requeridos (CU-PRE-14).
 *
 * @author Luis Medrano
 * @version 1.0
 */
@Service
@Transactional
public class AnalisisAmbientalServiceImpl implements AnalisisAmbientalService {

    private final AnalisisAmbientalRepository analisisAmbientalRepository;
    private final AnalisisAmbientalMapper analisisAmbientalMapper;
    private final ProyectoRepository proyectoRepository;
    private final ObjectProvider<AnalisisAmbientalService> selfProvider;
    private final ActorContexto actorContexto;

    public AnalisisAmbientalServiceImpl(
            AnalisisAmbientalRepository analisisAmbientalRepository,
            AnalisisAmbientalMapper analisisAmbientalMapper,
            ProyectoRepository proyectoRepository,
            ObjectProvider<AnalisisAmbientalService> selfProvider, ActorContexto actorContexto) {
        this.analisisAmbientalRepository = analisisAmbientalRepository;
        this.analisisAmbientalMapper = analisisAmbientalMapper;
        this.proyectoRepository = proyectoRepository;
        this.selfProvider = selfProvider;
        this.actorContexto = actorContexto;
    }

    // Nota: @RequiredArgsConstructor de Lombok ya genera el constructor,
    // pero si lo querés explícito lo dejamos sin problema.

    @Override
    @Transactional(readOnly = true)
    public AnalisisAmbientalDto obtenerAnalisisAmbiental(Long idProyecto) {

        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);

        // 1. Buscamos la cabecera (AnalisisAmbiental) única para el proyecto
        AnalisisAmbiental analisis = analisisAmbientalRepository.findByProyectoId(idProyecto)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("El análisis ambiental no existe para el proyecto seleccionado"));

        // 2. Mapeamos la cabecera al DTO principal
        AnalisisAmbientalDto dto = new AnalisisAmbientalDto();
        dto.setIdProyecto(idProyecto);
        dto.setTieneImpactosAmbientales(analisis.getTieneImpactosAmbientales());

        // 3. Validamos si hay impactos ambientales para mapear las filas de forma segura
        if (Boolean.TRUE.equals(analisis.getTieneImpactosAmbientales())) {
            List<FilaImpactoAmbientalRequestDto> filasDto = new ArrayList<>();
            if (analisis.getImpactosAmbientales() != null) {
                filasDto = analisis.getImpactosAmbientales().stream()
                        .map(impacto -> analisisAmbientalMapper.toFilaDto(impacto)) // <--- Actualizado a toFilaDto
                        .collect(Collectors.toList());
            }
            dto.setFilas(filasDto);

            // 4. Cálculo dinámico del costo total al vuelo (Cero persistencia de basura)
            double totalCosto = filasDto.stream()
                    .mapToDouble(f -> f.getCostoMedidaGestion() != null ? f.getCostoMedidaGestion() : 0.0)
                    .sum();
            dto.setTotalCostoMedidasGestion(totalCosto);

        } else {
            // Si no hay impactos ambientales, inicializamos el array vacío y el total a 0.0
            dto.setFilas(new ArrayList<>());
            dto.setTotalCostoMedidasGestion(0.0);
        }

        return dto;
    }

    @Override
    @Transactional
    public AnalisisAmbientalDto guardarAnalisisAmbiental(Long idProyecto, AnalisisAmbientalRequestDto requestDto) {

        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);

        // 1. Buscamos si ya existe la cabecera previa para actualizarla, o creamos una nueva
        AnalisisAmbiental analisis = analisisAmbientalRepository.findByProyectoId(idProyecto)
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    AnalisisAmbiental nuevo = new AnalisisAmbiental();
                    Proyecto proyectoProxy  = proyectoRepository.getReferenceById(idProyecto);
                    nuevo.setProyecto(proyectoProxy );
                    return nuevo;
                });

        boolean tieneImpactos = Boolean.TRUE.equals(requestDto.getTieneImpactosAmbientales());
        analisis.setTieneImpactosAmbientales(tieneImpactos);

        if (analisis.getImpactosAmbientales() == null) {
            analisis.setImpactosAmbientales(new ArrayList<>());
        }

        // 2. Manejo seguro de la colección para evitar bloqueos en el PersistentBag
        analisis.getImpactosAmbientales().clear();

        // Forzamos que Hibernate procese la limpieza de huérfanos antes de insertar los nuevos
        analisisAmbientalRepository.saveAndFlush(analisis);

        if (tieneImpactos && requestDto.getFilas() != null && !requestDto.getFilas().isEmpty()) {
            List<ImpactosAmbientales> nuevosImpactos = requestDto.getFilas().stream()
                    .map(filaDto -> {
                        ImpactosAmbientales impacto = analisisAmbientalMapper.toFilaEntity(filaDto);
                        impacto.setAnalisisAmbiental(analisis);
                        return impacto;
                    })
                    .toList(); // <-- Aquí cambias el collect(Collectors.toList()) por .toList()

            analisis.getImpactosAmbientales().addAll(nuevosImpactos);
        }

        // 3. Guardado final de la cabecera con sus nuevos hijos ya sincronizados
        analisisAmbientalRepository.save(analisis);

        // 4. Retornamos el DTO consolidado
        return selfProvider.getObject().obtenerAnalisisAmbiental(idProyecto);
    }
    private Proyecto buscarProyecto(Long idProyecto) {
        return proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException("El proyecto " + idProyecto + " no existe."));
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