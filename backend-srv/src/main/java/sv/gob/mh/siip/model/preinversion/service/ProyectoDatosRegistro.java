package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.LongFunction;

import org.springframework.stereotype.Component;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.ProyectoRequestDto;
import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.repository.EjePlanGobiernoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.PlanSectorialRegionalRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * Datos del registro de un proyecto (CU-PRE-01): copia los campos de la petición a la entidad,
 * resolviendo los identificadores de catálogo (sector, eje temático, eje del plan de gobierno,
 * plan sectorial/regional) y la Unidad Ejecutora a la que se reasigna el proyecto.
 */
@Component
public class ProyectoDatosRegistro {

    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final EjePlanGobiernoRepository ejePlanGobiernoRepository;
    private final PlanSectorialRegionalRepository planSectorialRegionalRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;

    public ProyectoDatosRegistro(SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository,
            EjePlanGobiernoRepository ejePlanGobiernoRepository,
            PlanSectorialRegionalRepository planSectorialRegionalRepository,
            UnidadEjecutoraRepository unidadEjecutoraRepository) {
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.ejePlanGobiernoRepository = ejePlanGobiernoRepository;
        this.planSectorialRegionalRepository = planSectorialRegionalRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
    }

    /**
     * Proyecto nuevo en estado En Registro con los datos de la petición, adscrito a la Unidad
     * Ejecutora del Técnico URP que lo registra (todavía sin persistir).
     *
     * @throws ValidacionNegocioException si algún catálogo no existe o falta un dato de emergencia.
     */
    public Proyecto nuevo(Usuario actor, ProyectoRequestDto request) {
        var entidad = ProyectoReglas.nuevoEnRegistro(actor);
        aplicarRequest(entidad, request);
        ProyectoReglas.validarReglaEmergencia(entidad);
        return entidad;
    }

    /**
     * Actualiza los datos de un proyecto todavía editable (todavía sin persistir).
     *
     * @throws sv.gob.mh.siip.exception.ConflictoEstadoException si el proyecto no es editable.
     * @throws ValidacionNegocioException si algún catálogo no existe o falta un dato de emergencia.
     */
    public void actualizar(Proyecto entidad, ProyectoRequestDto request) {
        ProyectoReglas.exigirEstadoEditable(entidad);
        aplicarRequest(entidad, request);
        ProyectoReglas.validarReglaEmergencia(entidad);
    }

    /**
     * Reasigna el proyecto a otra Unidad Ejecutora, y con ella a su Institución.
     *
     * @throws RecursoNoEncontradoException si la Unidad Ejecutora no existe.
     */
    public void asignarUnidadEjecutora(Proyecto entidad, Long idUnidadEjecutora) {
        UnidadEjecutora nuevaUnidadEjecutora = unidadEjecutoraRepository.findById(idUnidadEjecutora)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "La Unidad Ejecutora " + idUnidadEjecutora + " no existe."));
        entidad.setUnidadEjecutora(nuevaUnidadEjecutora);
        entidad.setInstitucion(nuevaUnidadEjecutora.getInstitucion());
    }

    private void aplicarRequest(Proyecto entidad, ProyectoRequestDto request) {
        List<ErrorDetalleDto> detalles = new ArrayList<>();

        entidad.setIniciativaInversion(IniciativaInversion.valueOf(request.getIniciativaInversion().name()));
        entidad.setNombre(request.getNombre());
        entidad.setMontoEstimadoInversion(request.getMontoEstimadoInversion());
        entidad.setSector(resolverCatalogoRequerido(sectorActividadRepository::findById, request.getIdSector(),
                "idSector", detalles));
        entidad.setEjeTematico(resolverCatalogoRequerido(ejeTematicoRepository::findById, request.getIdEjeTematico(),
                "idEjeTematico", detalles));
        entidad.setMedidasGrd(new ArrayList<>(request.getMedidasGrd() == null ? List.of() : request.getMedidasGrd()));
        entidad.setMedidasGrc(new ArrayList<>(request.getMedidasGrc() == null ? List.of() : request.getMedidasGrc()));
        entidad.setMedidasAcc(new ArrayList<>(request.getMedidasAcc() == null ? List.of() : request.getMedidasAcc()));
        entidad.setEsProyectoEmergencia(request.getEsProyectoEmergencia());
        entidad.setTipoEvento(request.getTipoEvento());
        entidad.setNumeroDecretoLegislativo(request.getNumeroDecretoLegislativo());
        entidad.setEjePlanGobierno(resolverCatalogoOpcional(ejePlanGobiernoRepository::findById,
                request.getIdEjePlanGobierno(), "idEjePlanGobierno", detalles));
        entidad.setPlanSectorialRegional(resolverCatalogoOpcional(planSectorialRegionalRepository::findById,
                request.getIdPlanSectorialRegional(), "idPlanSectorialRegional", detalles));
        entidad.setDescripcionProyecto(request.getDescripcionProyecto());

        if (!detalles.isEmpty()) {
            throw new ValidacionNegocioException("Existen campos de catálogo con identificadores inválidos.",
                    detalles);
        }
    }

    /** Resuelve un id de catálogo obligatorio (idSector/idEjeTematico); agrega un detalle si no existe. */
    private static <T> T resolverCatalogoRequerido(LongFunction<Optional<T>> buscador, Long id,
            String campo, List<ErrorDetalleDto> detalles) {
        if (id == null) {
            return null;
        }
        return buscador.apply(id)
                .orElseGet(() -> {
                    detalles.add(new ErrorDetalleDto().campo(campo).mensaje("El catálogo referenciado no existe."));
                    return null;
                });
    }

    /** Resuelve un id de catálogo condicional (ejePlanGobierno/planSectorialRegional); null es válido. */
    private static <T> T resolverCatalogoOpcional(LongFunction<Optional<T>> buscador, Long id,
            String campo, List<ErrorDetalleDto> detalles) {
        if (id == null) {
            return null;
        }
        return resolverCatalogoRequerido(buscador, id, campo, detalles);
    }
}
