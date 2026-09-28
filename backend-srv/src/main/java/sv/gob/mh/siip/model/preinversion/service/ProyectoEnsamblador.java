package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import org.springframework.stereotype.Component;

import sv.gob.mh.siip.model.preinversion.domain.ComentarioSolicitud;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.MedidaCatalogoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoMedidaCatalogo;
import sv.gob.mh.siip.model.preinversion.mapper.ProyectoMapper;
import sv.gob.mh.siip.model.preinversion.repository.ComentarioSolicitudRepository;
import sv.gob.mh.siip.model.preinversion.repository.MedidaCatalogoRepository;

/**
 * Ensambla el {@link ProyectoDto} de detalle (CU-PRE-01): el mapeo de la entidad, los comentarios
 * de la revisión PRE y las medidas GRD/GRC/ACC resueltas contra su catálogo.
 */
@Component
public class ProyectoEnsamblador {

    private final ComentarioSolicitudRepository comentarioRepository;
    private final MedidaCatalogoRepository medidaCatalogoRepository;
    private final ProyectoMapper mapper;

    public ProyectoEnsamblador(ComentarioSolicitudRepository comentarioRepository,
            MedidaCatalogoRepository medidaCatalogoRepository, ProyectoMapper mapper) {
        this.comentarioRepository = comentarioRepository;
        this.medidaCatalogoRepository = medidaCatalogoRepository;
        this.mapper = mapper;
    }

    /** DTO de detalle del proyecto, con su revisión PRE y sus medidas resueltas. */
    public ProyectoDto toDto(Proyecto entidad) {
        List<ComentarioSolicitud> revisionPre = comentarioRepository
                .findBySolicitudProyectoIdOrderByFechaComentarioAsc(entidad.getId());
        ProyectoDto dto = mapper.toDto(entidad, revisionPre);
        dto.setMedidasGrd(resolverMedidas(TipoMedidaCatalogo.GRD, entidad.getMedidasGrd()));
        dto.setMedidasGrc(resolverMedidas(TipoMedidaCatalogo.GRC, entidad.getMedidasGrc()));
        dto.setMedidasAcc(resolverMedidas(TipoMedidaCatalogo.ACC, entidad.getMedidasAcc()));
        return dto;
    }

    /** Resuelve los codigos guardados en Proyecto.medidasGrd/Grc/Acc a las entradas de catálogo. */
    private List<MedidaCatalogoDto> resolverMedidas(TipoMedidaCatalogo tipo, List<String> codigos) {
        if (codigos == null || codigos.isEmpty()) {
            return List.of();
        }
        return medidaCatalogoRepository.findByTipoAndCodigoInOrderByCodigo(tipo, codigos).stream()
                .map(m -> new MedidaCatalogoDto().codigo(m.getCodigo()).descripcion(m.getDescripcion()))
                .toList();
    }
}
