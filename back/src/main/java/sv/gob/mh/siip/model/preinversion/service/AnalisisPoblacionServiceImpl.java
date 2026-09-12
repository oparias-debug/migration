package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.AnalisisPoblacion;
import sv.gob.mh.siip.model.preinversion.domain.CeldaUbicacionPoblacion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisPoblacionDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisPoblacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.CeldaUbicacionDto;
import sv.gob.mh.siip.model.preinversion.dto.CeldaUbicacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaPoblacionDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaPoblacionRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.AnalisisPoblacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

@Service
@Transactional
public class AnalisisPoblacionServiceImpl implements AnalisisPoblacionService {

    private static final String CODIGO_AFECTADA_MAYOR_REFERENCIA = "POBLACION_AFECTADA_MAYOR_QUE_REFERENCIA";
    private static final String CODIGO_OBJETIVO_MAYOR_AFECTADA = "POBLACION_OBJETIVO_MAYOR_QUE_AFECTADA";

    private final ProyectoRepository proyectoRepository;
    private final AnalisisPoblacionRepository analisisPoblacionRepository;
    private final ActorContexto actorContexto;

    public AnalisisPoblacionServiceImpl(ProyectoRepository proyectoRepository,
            AnalisisPoblacionRepository analisisPoblacionRepository,
            ActorContexto actorContexto) {
        this.proyectoRepository = proyectoRepository;
        this.analisisPoblacionRepository = analisisPoblacionRepository;
        this.actorContexto = actorContexto;
    }

    @Override
    @Transactional(readOnly = true)
    public AnalisisPoblacionDto obtener(Long idProyecto) {
        Usuario actor = actorContexto.exigir();
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);

        AnalisisPoblacion entidad = analisisPoblacionRepository.findByProyectoId(idProyecto).orElse(null);
        return construirDto(proyecto, entidad);
    }

    @Override
    public AnalisisPoblacionDto guardar(Long idProyecto, AnalisisPoblacionRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);

        List<CeldaUbicacionRequestDto> ubicacionesReferencia = ubicacionesDe(request.getPoblacionReferencia());
        List<CeldaUbicacionRequestDto> ubicacionesAfectada = ubicacionesDe(request.getPoblacionAfectada());
        List<CeldaUbicacionRequestDto> ubicacionesObjetivo = ubicacionesDe(request.getPoblacionObjetivo());

        exigirNoExcede(ubicacionesReferencia, ubicacionesAfectada, CODIGO_AFECTADA_MAYOR_REFERENCIA,
                "La cantidad de población afectada no puede ser mayor que la registrada en la población de referencia.");
        exigirNoExcede(ubicacionesAfectada, ubicacionesObjetivo, CODIGO_OBJETIVO_MAYOR_AFECTADA,
                "El número de personas de la Población Objetivo no puede superar el número de personas de la Población Afectada.");

        AnalisisPoblacion entidad = analisisPoblacionRepository.findByProyectoId(idProyecto)
                .orElseGet(() -> AnalisisPoblacion.builder().proyecto(proyecto).build());

        // RN06: la descripcion de "Poblacion de Referencia" siempre se ignora.
        entidad.setDescripcionAfectada(descripcionDe(request.getPoblacionAfectada()));
        entidad.setDescripcionObjetivo(descripcionDe(request.getPoblacionObjetivo()));
        entidad.setUbicacionesReferencia(mapearCeldas(ubicacionesReferencia));
        entidad.setUbicacionesAfectada(mapearCeldas(ubicacionesAfectada));
        entidad.setUbicacionesObjetivo(mapearCeldas(ubicacionesObjetivo));

        entidad = analisisPoblacionRepository.save(entidad);
        return construirDto(proyecto, entidad);
    }

    private Proyecto buscarProyecto(Long idProyecto) {
        return proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException("El proyecto " + idProyecto + " no existe."));
    }

    /** Igual que en MatrizInteresadosServiceImpl/AlternativaSolucionServiceImpl: RN01/RN02. */
    private void exigirAlcanceUnidadEjecutora(Usuario actor, Proyecto proyecto) {
        if (actor.getUnidadEjecutora() != null
                && !actor.getUnidadEjecutora().getId().equals(proyecto.getUnidadEjecutora().getId())) {
            throw new AccesoDenegadoException(
                    "El proyecto no pertenece a una Unidad Ejecutora dentro de las credenciales del actor.");
        }
    }

    private List<CeldaUbicacionRequestDto> ubicacionesDe(FilaPoblacionRequestDto fila) {
        if (fila == null || fila.getUbicaciones() == null) {
            return List.of();
        }
        return fila.getUbicaciones();
    }

    private String descripcionDe(FilaPoblacionRequestDto fila) {
        return fila == null ? null : fila.getDescripcion();
    }

    /**
     * FA-03: compara, posicion a posicion (misma ubicacion), que {@code mayor} no exceda a
     * {@code menor}. Solo se valida cuando ambos valores de la pareja estan presentes: un valor
     * faltante es responsabilidad visual de RN07, no de esta regla de negocio.
     */
    private void exigirNoExcede(List<CeldaUbicacionRequestDto> menor, List<CeldaUbicacionRequestDto> mayor,
            String codigo, String mensaje) {
        int tamanio = Math.max(menor.size(), mayor.size());
        for (int i = 0; i < tamanio; i++) {
            Integer numeroMenor = numeroPersonasEn(menor, i);
            Integer numeroMayor = numeroPersonasEn(mayor, i);
            if (numeroMenor != null && numeroMayor != null && numeroMayor > numeroMenor) {
                throw new ValidacionNegocioException(codigo, mensaje, null);
            }
        }
    }

    private Integer numeroPersonasEn(List<CeldaUbicacionRequestDto> celdas, int indice) {
        return indice < celdas.size() ? celdas.get(indice).getNumeroPersonas() : null;
    }

    private List<CeldaUbicacionPoblacion> mapearCeldas(List<CeldaUbicacionRequestDto> celdas) {
        List<CeldaUbicacionPoblacion> resultado = new ArrayList<>();
        for (CeldaUbicacionRequestDto celda : celdas) {
            resultado.add(new CeldaUbicacionPoblacion(celda.getUbicacion(), celda.getNumeroPersonas()));
        }
        return resultado;
    }

    private AnalisisPoblacionDto construirDto(Proyecto proyecto, AnalisisPoblacion entidad) {
        List<CeldaUbicacionPoblacion> referencia = entidad != null ? entidad.getUbicacionesReferencia() : List.of();
        List<CeldaUbicacionPoblacion> afectada = entidad != null ? entidad.getUbicacionesAfectada() : List.of();
        List<CeldaUbicacionPoblacion> objetivo = entidad != null ? entidad.getUbicacionesObjetivo() : List.of();
        String descripcionAfectada = entidad != null ? entidad.getDescripcionAfectada() : null;
        String descripcionObjetivo = entidad != null ? entidad.getDescripcionObjetivo() : null;

        return new AnalisisPoblacionDto()
                .idProyecto(proyecto.getId())
                .poblacionReferencia(construirFilaReferencia(referencia))
                .poblacionAfectada(construirFilaConstante100(afectada, descripcionAfectada))
                .poblacionObjetivo(construirFilaObjetivo(objetivo, afectada, descripcionObjetivo))
                .poblacionEnEspera(construirFilaEspera(afectada, objetivo));
    }

    /** RN04: "Población de Referencia" nunca tiene porcentaje, ni por celda ni en el total. */
    private FilaPoblacionDto construirFilaReferencia(List<CeldaUbicacionPoblacion> celdas) {
        List<CeldaUbicacionDto> ubicaciones = celdas.stream()
                .map(celda -> new CeldaUbicacionDto().ubicacion(celda.getUbicacion())
                        .numeroPersonas(celda.getNumeroPersonas()).porcentaje(null))
                .toList();
        return new FilaPoblacionDto()
                .descripcion(null)
                .ubicaciones(ubicaciones)
                .totalNumeroPersonas(sumar(celdas))
                .totalPorcentaje(null);
    }

    /** "Población Afectada": porcentaje constante en 100, por celda y en el total. */
    private FilaPoblacionDto construirFilaConstante100(List<CeldaUbicacionPoblacion> celdas, String descripcion) {
        List<CeldaUbicacionDto> ubicaciones = celdas.stream()
                .map(celda -> new CeldaUbicacionDto().ubicacion(celda.getUbicacion())
                        .numeroPersonas(celda.getNumeroPersonas()).porcentaje(100.0))
                .toList();
        return new FilaPoblacionDto()
                .descripcion(descripcion)
                .ubicaciones(ubicaciones)
                .totalNumeroPersonas(sumar(celdas))
                .totalPorcentaje(100.0);
    }

    /** "Población Objetivo": porcentaje = (Objetivo / Afectada) × 100, por celda y en el total. */
    private FilaPoblacionDto construirFilaObjetivo(List<CeldaUbicacionPoblacion> objetivo,
            List<CeldaUbicacionPoblacion> afectada, String descripcion) {
        List<CeldaUbicacionDto> ubicaciones = new ArrayList<>();
        for (int i = 0; i < objetivo.size(); i++) {
            CeldaUbicacionPoblacion celdaObjetivo = objetivo.get(i);
            Integer numeroAfectada = i < afectada.size() ? afectada.get(i).getNumeroPersonas() : null;
            ubicaciones.add(new CeldaUbicacionDto().ubicacion(celdaObjetivo.getUbicacion())
                    .numeroPersonas(celdaObjetivo.getNumeroPersonas())
                    .porcentaje(calcularPorcentaje(celdaObjetivo.getNumeroPersonas(), numeroAfectada)));
        }
        Integer totalObjetivo = sumar(objetivo);
        Integer totalAfectada = sumar(afectada);
        return new FilaPoblacionDto()
                .descripcion(descripcion)
                .ubicaciones(ubicaciones)
                .totalNumeroPersonas(totalObjetivo)
                .totalPorcentaje(calcularPorcentaje(totalObjetivo, totalAfectada));
    }

    /**
     * "Población en Espera": nunca se persiste, 100% calculada (RN05: ubicación siempre nula). N°
     * de personas = Afectada − Objetivo; % = %Afectada (100) − %Objetivo.
     */
    private FilaPoblacionDto construirFilaEspera(List<CeldaUbicacionPoblacion> afectada,
            List<CeldaUbicacionPoblacion> objetivo) {
        int tamanio = Math.max(afectada.size(), objetivo.size());
        List<CeldaUbicacionDto> ubicaciones = new ArrayList<>();
        for (int i = 0; i < tamanio; i++) {
            Integer numeroAfectada = i < afectada.size() ? afectada.get(i).getNumeroPersonas() : null;
            Integer numeroObjetivo = i < objetivo.size() ? objetivo.get(i).getNumeroPersonas() : null;
            Double porcentajeObjetivo = calcularPorcentaje(numeroObjetivo, numeroAfectada);
            ubicaciones.add(new CeldaUbicacionDto().ubicacion(null)
                    .numeroPersonas(valorOCero(numeroAfectada) - valorOCero(numeroObjetivo))
                    .porcentaje(porcentajeObjetivo == null ? null : 100.0 - porcentajeObjetivo));
        }
        Integer totalAfectada = sumar(afectada);
        Integer totalObjetivo = sumar(objetivo);
        Double totalPorcentajeObjetivo = calcularPorcentaje(totalObjetivo, totalAfectada);
        return new FilaPoblacionDto()
                .descripcion(null)
                .ubicaciones(ubicaciones)
                .totalNumeroPersonas(totalAfectada - totalObjetivo)
                .totalPorcentaje(totalPorcentajeObjetivo == null ? null : 100.0 - totalPorcentajeObjetivo);
    }

    private Double calcularPorcentaje(Integer numerador, Integer denominador) {
        if (denominador == null || denominador == 0) {
            return null;
        }
        return 100.0 * valorOCero(numerador) / denominador;
    }

    private int valorOCero(Integer valor) {
        return valor == null ? 0 : valor;
    }

    private int sumar(List<CeldaUbicacionPoblacion> celdas) {
        return celdas.stream().mapToInt(celda -> valorOCero(celda.getNumeroPersonas())).sum();
    }
}
