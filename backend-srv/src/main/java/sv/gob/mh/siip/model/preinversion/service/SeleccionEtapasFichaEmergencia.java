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

    /** Margen para comparar montos en dólares (medio centavo). */
    private static final double TOLERANCIA_MONTO = 0.005D;

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
     * ("Existen campos sin diligenciar") o si el Total de los componentes de costo no es igual a la
     * Inversión estimada (Anexo B.1, código {@code TOTAL_COMPONENTES_DISTINTO_INVERSION}).
     */
    public FichaEmergenciaDto registrar(Long idProyecto, FichaEmergenciaRequestDto request) {
        Proyecto proyecto = proyectos.buscarDeEmergencia(idProyecto);
        exigirEstadoRegistrable(proyecto);
        validarObligatorios(request);
        validarTotalComponentes(request);

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

    /**
     * Anexo B.1, campo "Total": la suma de los componentes de costo "debe ser igual al campo
     * Inversión Estimada". Solo se exige cuando se registró al menos un componente.
     */
    private static void validarTotalComponentes(FichaEmergenciaRequestDto request) {
        if (request.getComponentesCosto().isEmpty()) {
            return;
        }
        double total = request.getComponentesCosto().stream()
                .mapToDouble(ComponenteCostoDto::getCosto)
                .sum();
        Double inversionEstimada = request.getInversionEstimada();
        if (inversionEstimada == null || Math.abs(total - inversionEstimada) > TOLERANCIA_MONTO) {
            throw new ValidacionNegocioException("TOTAL_COMPONENTES_DISTINTO_INVERSION",
                    "El Total de los componentes de costo debe ser igual a la Inversión estimada.",
                    List.of(new ErrorDetalleDto().campo("componentesCosto")
                            .mensaje("El Total (" + total + ") no es igual a la Inversión estimada ("
                                    + (inversionEstimada == null ? "sin dato" : inversionEstimada) + ").")));
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
        // Los archivos solo se reemplazan si el cliente manda una referencia nueva: si no la manda
        // (no hay endpoint de carga), se conserva la guardada.
        if (request.getArchivoPresupuestoUrl() != null) {
            ficha.setArchivoPresupuestoUrl(request.getArchivoPresupuestoUrl());
        }
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
        if (request.getArchivoProgramacionUrl() != null) {
            ficha.setArchivoProgramacionUrl(request.getArchivoProgramacionUrl());
        }
    }
}
