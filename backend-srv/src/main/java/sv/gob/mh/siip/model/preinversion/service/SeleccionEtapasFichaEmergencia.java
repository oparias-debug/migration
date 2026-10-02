package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.ComponenteCostoEmergencia;
import sv.gob.mh.siip.model.preinversion.domain.FichaEmergencia;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ComponenteCostoDto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaEmergenciaDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaEmergenciaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FuenteFinanciamientoDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoSeleccionadoDto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.FuenteFinanciamiento;
import sv.gob.mh.siip.model.preinversion.repository.FichaEmergenciaRepository;

/**
 * Ficha de proyectos de emergencia de CU-PRE-03.5 (Anexo A.4): consulta y
 * registro; al registrarla completa, el proyecto se remite a Viabilidad.
 */
@Component
public class SeleccionEtapasFichaEmergencia {

    private static final String CAMPO_OBLIGATORIO = "*Campo obligatorio";

    /**
     * Estados desde los que se puede registrar la ficha y remitir el proyecto a Viabilidad: el
     * proyecto tiene CUP y todavía no llegó a Viabilidad, o Viabilidad lo devolvió (OBSERVADO). En
     * cualquier otro estado, guardar haría retroceder el proyecto a EN_VIABILIDAD.
     */
    private static final Set<EstadoProyecto> ESTADOS_REGISTRABLES = EnumSet.of(EstadoProyecto.CUP_ASIGNADO,
            EstadoProyecto.EN_FORMULACION, EstadoProyecto.PROYECTO_FORMULADO, EstadoProyecto.OBSERVADO);

    private final SeleccionEtapasProyectos proyectos;
    private final FichaEmergenciaRepository fichaEmergenciaRepository;
    private final FichaEmergenciaEnsamblador ensamblador;

    public SeleccionEtapasFichaEmergencia(SeleccionEtapasProyectos proyectos,
            FichaEmergenciaRepository fichaEmergenciaRepository, FichaEmergenciaEnsamblador ensamblador) {
        this.proyectos = proyectos;
        this.fichaEmergenciaRepository = fichaEmergenciaRepository;
        this.ensamblador = ensamblador;
    }

    /**
     * Ficha del proyecto de emergencia indicado (vacía si aún no se registró).
     */
    public FichaEmergenciaDto obtener(Long idProyecto) {
        Proyecto proyecto = proyectos.buscarDeEmergencia(idProyecto);
        FichaEmergencia ficha = fichaEmergenciaRepository.findByProyectoId(idProyecto).orElse(null);
        return ensamblador.construir(proyecto, ficha);
    }

    /**
     * Registra la ficha y remite el proyecto a Viabilidad.
     *
     * @throws ConflictoEstadoException si el estado del proyecto no admite registrar la ficha (ver
     * {@link #ESTADOS_REGISTRABLES})
     * @throws ValidacionNegocioException si faltan campos obligatorios
     * ("Existen campos sin diligenciar").
     */
    public FichaEmergenciaDto registrar(Long idProyecto, FichaEmergenciaRequestDto request) {
        Proyecto proyecto = proyectos.buscarDeEmergencia(idProyecto);
        exigirEstadoRegistrable(proyecto);
        validarObligatorios(request);

        FichaEmergencia ficha = fichaEmergenciaRepository.findByProyectoId(idProyecto)
                .orElseGet(() -> FichaEmergencia.builder().proyecto(proyecto).build());
        copiarDatos(request, ficha);
        fichaEmergenciaRepository.save(ficha);

        // FA-05, paso 5.5: al guardar con exito, el proyecto se remite a Viabilidad (CU-PRE-24).
        proyecto.setEstado(EstadoProyecto.EN_VIABILIDAD);
        proyectos.guardar(proyecto);

        return ensamblador.construir(proyecto, ficha);
    }

    private static void exigirEstadoRegistrable(Proyecto proyecto) {
        if (!ESTADOS_REGISTRABLES.contains(proyecto.getEstado())) {
            String estado = proyecto.getEstado() == null ? "sin estado" : proyecto.getEstado().getEtiquetaUi();
            throw new ConflictoEstadoException("FICHA_EMERGENCIA_NO_EDITABLE",
                    "La Ficha de proyectos de emergencia no se puede registrar en el estado actual del proyecto ("
                            + estado + ").");
        }
    }

    private static void validarObligatorios(FichaEmergenciaRequestDto request) {
        List<ErrorDetalleDto> detalles = new ArrayList<>();
        exigirTexto(request.getPlanteamientoProblema(), "planteamientoProblema", detalles);
        if (request.getProductos().isEmpty()) {
            detalles.add(new ErrorDetalleDto().campo("productos").mensaje(CAMPO_OBLIGATORIO));
        }
        exigirTexto(request.getDistrito(), "distrito", detalles);
        exigirTexto(request.getPoblacionObjetivo(), "poblacionObjetivo", detalles);
        if (!detalles.isEmpty()) {
            throw new ValidacionNegocioException("Existen campos sin diligenciar", detalles);
        }
    }

    private static void exigirTexto(String valor, String campo, List<ErrorDetalleDto> detalles) {
        if (valor == null || valor.isBlank()) {
            detalles.add(new ErrorDetalleDto().campo(campo).mensaje(CAMPO_OBLIGATORIO));
        }
    }

    private static void copiarDatos(FichaEmergenciaRequestDto request, FichaEmergencia ficha) {
        ficha.setPlanteamientoProblema(request.getPlanteamientoProblema());
        ficha.setObjetivoGeneral(request.getObjetivoGeneral());
        ficha.setDescripcionProyecto(request.getDescripcionProyecto());
        ficha.setProductos(request.getProductos().stream().map(ProductoSeleccionadoDto::getCodigoProducto).toList());
        ficha.setDistrito(request.getDistrito());
        ficha.setLatitud(request.getLatitud());
        ficha.setLongitud(request.getLongitud());
        ficha.setDireccionEspecifica(request.getDireccionEspecifica());
        ficha.setPoblacionObjetivo(request.getPoblacionObjetivo());
        ficha.setInversionEstimada(request.getInversionEstimada());
        ficha.setArchivoPresupuestoUrl(request.getArchivoPresupuestoUrl());
        ficha.setComponentesCosto(request.getComponentesCosto().stream()
                .map((ComponenteCostoDto componente)
                        -> new ComponenteCostoEmergencia(componente.getTipoCosto(), componente.getCosto()))
                .toList());
        ficha.setCostosOperacion(request.getCostosOperacion());
        ficha.setCostosMantenimiento(request.getCostosMantenimiento());
        ficha.setFuentesFinanciamiento(request.getFuentesFinanciamiento().stream()
                .map((FuenteFinanciamientoDto fuente) -> FuenteFinanciamiento.valueOf(fuente.name()))
                .toList());
        ficha.setFuenteRecursos(request.getFuenteRecursos());
        ficha.setArchivoProgramacionUrl(request.getArchivoProgramacionUrl());
    }
}
