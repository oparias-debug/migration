package sv.gob.mh.siip.model.preinversion.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.Identificacion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ArchivoAdjuntoResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.IdentificacionDto;
import sv.gob.mh.siip.model.preinversion.dto.IdentificacionRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.IdentificacionRepository;
import sv.gob.mh.siip.model.preinversion.service.IdentificacionArboles.TipoArbol;

/**
 * CU-PRE-04 "Identificación": el control de acceso vive en {@link IdentificacionAcceso}, los árboles
 * de problemas y de objetivos en {@link IdentificacionArboles} y el traslado a/desde DTO en
 * {@link IdentificacionEnsamblador}; esta clase mantiene el control transaccional.
 */
@Service
@Transactional
public class IdentificacionServiceImpl implements IdentificacionService {

    private final IdentificacionRepository identificacionRepository;
    private final IdentificacionAcceso acceso;
    private final IdentificacionArboles arboles;
    private final IdentificacionEnsamblador ensamblador;

    public IdentificacionServiceImpl(IdentificacionRepository identificacionRepository,
            IdentificacionAcceso acceso,
            IdentificacionArboles arboles,
            IdentificacionEnsamblador ensamblador) {
        this.identificacionRepository = identificacionRepository;
        this.acceso = acceso;
        this.arboles = arboles;
        this.ensamblador = ensamblador;
    }

    @Override
    @Transactional(readOnly = true)
    public IdentificacionDto obtener(Long idProyecto) {
        Usuario actor = acceso.exigirActor();
        var proyecto = acceso.proyectoVisible(actor, idProyecto);

        Optional<Identificacion> entidad = identificacionRepository.findByProyectoId(idProyecto);
        exigirGuardadoPrevio(actor, entidad.isPresent(), idProyecto);
        return ensamblador.aDto(proyecto, entidad.orElse(null));
    }

    private static void exigirGuardadoPrevio(Usuario actor, boolean guardado, Long idProyecto) {
        if (!guardado && actor.getRol() != RolUsuario.TECNICO_URP) {
            // RNA-2/RNA-3: para cualquier otro actor, sin al menos un guardado previo el recurso no existe.
            throw new RecursoNoEncontradoException(
                    "La informacion de identificacion del proyecto " + idProyecto + " todavia no se ha guardado.");
        }
    }

    @Override
    public IdentificacionDto guardar(Long idProyecto, IdentificacionRequestDto request) {
        var proyecto = acceso.proyectoEditable(idProyecto);

        Identificacion entidad = identificacionRepository.findByProyectoId(proyecto.getId())
                .orElseGet(() -> Identificacion.builder().proyecto(proyecto).build());
        ensamblador.aplicar(entidad, request);

        entidad = identificacionRepository.save(entidad);
        return ensamblador.aDto(proyecto, entidad);
    }

    @Override
    public ArchivoAdjuntoResumenDto cargarArbolProblemas(Long idProyecto, MultipartFile archivo) {
        return arboles.cargar(idProyecto, archivo, TipoArbol.PROBLEMAS);
    }

    @Override
    @Transactional(readOnly = true)
    public ArchivoDescargado descargarArbolProblemas(Long idProyecto) {
        return arboles.descargar(idProyecto, TipoArbol.PROBLEMAS);
    }

    @Override
    public void eliminarArbolProblemas(Long idProyecto) {
        arboles.eliminar(idProyecto, TipoArbol.PROBLEMAS);
    }

    @Override
    public ArchivoAdjuntoResumenDto cargarArbolObjetivos(Long idProyecto, MultipartFile archivo) {
        return arboles.cargar(idProyecto, archivo, TipoArbol.OBJETIVOS);
    }

    @Override
    @Transactional(readOnly = true)
    public ArchivoDescargado descargarArbolObjetivos(Long idProyecto) {
        return arboles.descargar(idProyecto, TipoArbol.OBJETIVOS);
    }

    @Override
    public void eliminarArbolObjetivos(Long idProyecto) {
        arboles.eliminar(idProyecto, TipoArbol.OBJETIVOS);
    }
}
