package sv.gob.mh.siip.model.preinversion.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.PresupuestoProyecto;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ConfigurarPeriodosEjecucionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FuentesFinanciamientoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.MacroactividadDto;
import sv.gob.mh.siip.model.preinversion.dto.MacroactividadRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.PresupuestoDto;
import sv.gob.mh.siip.model.preinversion.repository.PresupuestoProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * Presupuesto de Inversión (CU-PRE-20): periodos de ejecución, macroactividades
 * por producto y fuentes de financiamiento. Delega el registro de
 * macroactividades en {@link PresupuestoInversionMacroactividades}, las fuentes
 * en {@link PresupuestoInversionFuentes} y el cálculo del presupuesto en
 * {@link PresupuestoInversionEnsamblador}.
 */
@Service
@Transactional
public class PresupuestoInversionService {

    private final ProyectoRepository proyectos;
    private final PresupuestoProyectoRepository presupuestos;
    private final ActorContexto actor;
    private final PresupuestoInversionMacroactividades macroactividades;
    private final PresupuestoInversionEnsamblador ensamblador;
    private final CostoEtapaEjecucion costoEjecucion;

    public PresupuestoInversionService(ProyectoRepository p, PresupuestoProyectoRepository pr, ActorContexto a,
            PresupuestoInversionMacroactividades m, PresupuestoInversionEnsamblador e, CostoEtapaEjecucion c) {
        proyectos = p;
        presupuestos = pr;
        actor = a;
        macroactividades = m;
        ensamblador = e;
        costoEjecucion = c;
    }

    public PresupuestoDto obtener(Long id) {
        actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE);
        return ensamblador.dto(buscar(id), obtenerOCrear(buscar(id)));
    }

    /**
     * Presupuesto del proyecto en modo consulta, para los CU que lo muestran
     * sin editarlo (p.ej. la ficha de CU-PRE-24 "Viabilidad"). A diferencia de
     * {@link #obtener(Long)}, no exige rol (el CU que lo invoca aplica sus
     * propias credenciales) y no crea el presupuesto si todavía no existe.
     *
     * @param id identificador del proyecto
     * @return el presupuesto calculado, o vacío si el proyecto aún no tiene
     * presupuesto registrado
     */
    @Transactional(readOnly = true)
    public Optional<PresupuestoDto> consultarSoloLectura(Long id) {
        return presupuestos.findByProyectoId(id).map(p -> ensamblador.dto(buscar(id), p));
    }

    public PresupuestoDto periodos(Long id, ConfigurarPeriodosEjecucionRequestDto req) {
        actor.exigirRol(RolUsuario.TECNICO_URP);
        PresupuestoProyecto p = obtenerOCrear(buscarEditable(id));
        p.setPeriodosEstimados(req.getPeriodosEstimados());
        return conCostoEjecucion(id, ensamblador.dto(buscar(id), p));
    }

    public MacroactividadDto registrar(Long id, Integer producto, MacroactividadRequestDto req) {
        actor.exigirRol(RolUsuario.TECNICO_URP);
        var proyecto = buscarEditable(id);
        PresupuestoInversionMacroactividades.validar(req);
        PresupuestoProyecto p = obtenerOCrear(proyecto);
        MacroactividadDto registrada = macroactividades.registrar(p, producto, req);
        conCostoEjecucion(id, ensamblador.dto(proyecto, p));
        return registrada;
    }

    public PresupuestoDto guardar(Long id) {
        actor.exigirRol(RolUsuario.TECNICO_URP);
        var proyecto = buscar(id);
        PresupuestoProyecto p = obtenerOCrear(proyecto);
        macroactividades.exigirPorProducto(p, ensamblador.contarProductos(id));
        return conCostoEjecucion(id, ensamblador.dto(proyecto, p));
    }

    public FuentesFinanciamientoRequestDto fuentes(Long id) {
        actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE);
        return PresupuestoInversionFuentes.dto(obtenerOCrear(buscar(id)));
    }

    public FuentesFinanciamientoRequestDto guardarFuentes(Long id, FuentesFinanciamientoRequestDto req) {
        actor.exigirRol(RolUsuario.TECNICO_URP);
        PresupuestoInversionFuentes.validar(req);
        PresupuestoProyecto p = obtenerOCrear(buscar(id));
        PresupuestoInversionFuentes.aplicar(p, req);
        presupuestos.save(p);
        return PresupuestoInversionFuentes.dto(p);
    }

    /** RN05/RN22 de CU-PRE-03.5: el total del presupuesto es el costo de la etapa de Ejecución. */
    private PresupuestoDto conCostoEjecucion(Long id, PresupuestoDto presupuesto) {
        costoEjecucion.actualizar(id, presupuesto);
        return presupuesto;
    }

    private Proyecto buscar(Long id) {
        return proyectos.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Proyecto no encontrado"));
    }

    /**
     * Como {@link #buscar(Long)}, pero rechaza la operacion si la formulacion
     * esta bloqueada (CU-PRE-24 RN04).
     */
    private Proyecto buscarEditable(Long id) {
        var proyecto = buscar(id);
        EdicionFormulacion.exigirEditable(proyecto);
        return proyecto;
    }

    private PresupuestoProyecto obtenerOCrear(Proyecto proyecto) {
        return presupuestos.findByProyectoId(proyecto.getId())
                .orElseGet(() -> presupuestos.save(PresupuestoProyecto.builder().proyecto(proyecto).build()));
    }
}
