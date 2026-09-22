package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.AnalisisRiesgo;
import sv.gob.mh.siip.model.preinversion.domain.RiesgosDesastresInminentes;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.*;
import sv.gob.mh.siip.model.preinversion.mapper.AnalisisRiesgoMapper;
import sv.gob.mh.siip.model.preinversion.repository.AnalisisRiesgoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Implementación de la lógica de negocio para el módulo de Análisis de Riesgo (CU-PRE-15).
 * Gestiona el almacenamiento maestro-detalle, el recálculo automático de la matriz de riesgos (Anexo C.1)
 * y la validación de la regla de negocio RN06.
 *
 * @author Luis Medrano
 * @version 1.0
 * @since 2026-09-20
 */
@Service
@Transactional
public class AnalisisRiesgoServiceImpl implements AnalisisRiesgoService {

    private final AnalisisRiesgoRepository analisisRiesgoRepository;
    private final ProyectoRepository proyectoRepository;
    private final AnalisisRiesgoMapper analisisRiesgoMapper;
    private final ActorContexto actorContexto;

    public AnalisisRiesgoServiceImpl(AnalisisRiesgoRepository analisisRiesgoRepository, ProyectoRepository proyectoRepository,
            AnalisisRiesgoMapper analisisRiesgoMapper, ActorContexto actorContexto) {
        this.analisisRiesgoRepository = analisisRiesgoRepository;
        this.proyectoRepository = proyectoRepository;
        this.analisisRiesgoMapper = analisisRiesgoMapper;
        this.actorContexto = actorContexto;
    }

    /**
     * {@inheritDoc}
     *
     * @author Luis Medrano
     */
    @Override
    @Transactional(readOnly = true)
    public AnalisisRiesgoDto obtenerAnalisisRiesgo(Long idProyecto) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE);
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);

        AnalisisRiesgo analisis = analisisRiesgoRepository.findByProyectoId(idProyecto)
                .orElseGet(() -> AnalisisRiesgo.builder()
                        .proyecto(proyecto)
                        .tieneRiesgosDesastres(false) // Asegurar valores por defecto no nulos si aplica
                        .totalAccionesMitigacion(0.0)
                        .build());

        return analisisRiesgoMapper.toDto(analisis);
    }

    /**
     * {@inheritDoc}
     *
     * @author Luis Medrano
     */
    @Override
    @Transactional
    public AnalisisRiesgoDto guardarAnalisisRiesgo(Long idProyecto, AnalisisRiesgoRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);

        AnalisisRiesgo analisis = analisisRiesgoRepository.findByProyectoId(idProyecto)
                .orElseGet(() -> AnalisisRiesgo.builder()
                        .proyecto(proyecto)
                        .build());

        analisis.setTieneRiesgosDesastres(request.getTieneRiesgosDesastres());
        analisis.getFilas().clear();

        double costoTotalAcumulado = 0.0;

        if (request.getFilas() != null && Boolean.TRUE.equals(request.getTieneRiesgosDesastres())) {
            for (FilaRiesgoRequestDto dtoReq : request.getFilas()) {
                CalificacionRiesgoDto calificacionCalculada = calcularCalificacionMatrizC(
                        dtoReq.getProbabilidad(),
                        dtoReq.getImpactoRiesgo()
                );

                RiesgosDesastresInminentes fila = RiesgosDesastresInminentes.builder()
                        .descripcionRiesgo(dtoReq.getDescripcionRiesgo())
                        .probabilidad(dtoReq.getProbabilidad())
                        .impactoRiesgo(dtoReq.getImpactoRiesgo())
                        .calificacionRiesgo(calificacionCalculada)
                        .accionMitigacion(dtoReq.getAccionMitigacion())
                        .costoAccionMitigacion(dtoReq.getCostoAccionMitigacion())
                        .build();

                analisis.getFilas().add(fila);
                fila.setAnalisisRiesgo(analisis);

                Double costo = dtoReq.getCostoAccionMitigacion();
                if (costo != null) {
                    costoTotalAcumulado += costo;
                }
            }
        }

        analisis.setTotalAccionesMitigacion(redondear(costoTotalAcumulado));
        AnalisisRiesgo guardado = analisisRiesgoRepository.save(analisis);

        return analisisRiesgoMapper.toDto(guardado);
    }

    /**
     * {@inheritDoc}
     *
     * @author Luis Medrano
     */
    @Override
    @Transactional
    public AnalisisRiesgoDto avanzarAAnalisisLegal(Long idProyecto) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);

        AnalisisRiesgo analisis = analisisRiesgoRepository.findByProyectoId(idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Análisis de riesgo no encontrado para el proyecto: " + idProyecto));

        if (analisis.getFilas() != null) {
            for (RiesgosDesastresInminentes fila : analisis.getFilas()) {
                boolean esAltoOMuyAlto = fila.getCalificacionRiesgo() == CalificacionRiesgoDto.ALTO
                        || fila.getCalificacionRiesgo() == CalificacionRiesgoDto.MUY_ALTO;

                if (esAltoOMuyAlto) {
                    boolean accionIncompleta = fila.getAccionMitigacion() == null || fila.getAccionMitigacion().isBlank();
                    boolean costoIncompleto = fila.getCostoAccionMitigacion() == null;

                    if (accionIncompleta || costoIncompleto) {
                        throw new ValidacionNegocioException("VALIDACION_NEGOCIO", "Se requiere completar los campos obligatorios", null);
                    }
                }
            }
        }

        return analisisRiesgoMapper.toDto(analisis);
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

    /**
     * Aplica la matriz de combinación de celdas del Anexo C.1 para determinar
     * la calificación del riesgo de forma automática mediante descomposición de métodos.
     * Sin probabilidad e impacto no hay calificación calculable (queda null, no "Bajo").
     */
    private CalificacionRiesgoDto calcularCalificacionMatrizC(ProbabilidadDto prob, ImpactoRiesgoDto impacto) {
        if (prob == null || impacto == null) {
            return null;
        }

        return switch (prob) {
            case IMPROBABLE -> evaluarImprobable(impacto);
            case PROBABLE -> evaluarProbable(impacto);
            case MUY_PROBABLE -> evaluarMuyProbable(impacto);
            case CASI_SEGURO -> evaluarCasiSeguro(impacto);
        };
    }

    private CalificacionRiesgoDto evaluarImprobable(ImpactoRiesgoDto impacto) {
        return impacto == ImpactoRiesgoDto.EXTREMO ? CalificacionRiesgoDto.MEDIO : CalificacionRiesgoDto.BAJO;
    }

    private CalificacionRiesgoDto evaluarProbable(ImpactoRiesgoDto impacto) {
        if (impacto == ImpactoRiesgoDto.EXTREMO) return CalificacionRiesgoDto.ALTO;
        if (impacto == ImpactoRiesgoDto.ALTO || impacto == ImpactoRiesgoDto.MODERADO) return CalificacionRiesgoDto.MEDIO;
        return CalificacionRiesgoDto.BAJO;
    }

    private CalificacionRiesgoDto evaluarMuyProbable(ImpactoRiesgoDto impacto) {
        if (impacto == ImpactoRiesgoDto.EXTREMO) return CalificacionRiesgoDto.MUY_ALTO;
        if (impacto == ImpactoRiesgoDto.ALTO || impacto == ImpactoRiesgoDto.MODERADO) return CalificacionRiesgoDto.ALTO;
        if (impacto == ImpactoRiesgoDto.BAJO) return CalificacionRiesgoDto.MEDIO;
        return CalificacionRiesgoDto.BAJO;
    }

    private CalificacionRiesgoDto evaluarCasiSeguro(ImpactoRiesgoDto impacto) {
        if (impacto == ImpactoRiesgoDto.EXTREMO || impacto == ImpactoRiesgoDto.ALTO) return CalificacionRiesgoDto.MUY_ALTO;
        if (impacto == ImpactoRiesgoDto.MODERADO) return CalificacionRiesgoDto.ALTO;
        if (impacto == ImpactoRiesgoDto.BAJO) return CalificacionRiesgoDto.MEDIO;
        return CalificacionRiesgoDto.BAJO;
    }

    

    /**
     * Utilidad para redondear montos monetarios a dos decimales de manera segura.
     */
    private double redondear(double valor) {
        return BigDecimal.valueOf(valor)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}