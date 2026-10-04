package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.RutaPreinversion;
import sv.gob.mh.siip.model.preinversion.dto.ComplejidadProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.CriteriosCalificacionDto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.dto.ModificarRutaPreinversionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.RutaPreinversionDto;
import sv.gob.mh.siip.model.preinversion.dto.RutaPreinversionSugeridaDto;
import sv.gob.mh.siip.model.preinversion.dto.TamanioProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoCapitalDto;
import sv.gob.mh.siip.model.preinversion.enums.ComplejidadProyecto;
import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.enums.TamanioProyecto;
import sv.gob.mh.siip.model.preinversion.enums.TipoCapital;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.RutaPreinversionRepository;

/**
 * Ruta de Preinversión de CU-PRE-03.5 (Anexo A.2): cálculo de la ruta sugerida según los criterios
 * de calificación (RN10, Anexo B.2), aceptación y modificación manual de la ruta.
 */
@Component
public class SeleccionEtapasRuta {

    /**
     * Ruta con Diseño, sin Prefactibilidad/Factibilidad (Anexo B.2: "Perfil con Diseño Básico"/"Perfil + Diseño").
     */
    private static final List<TipoEtapaPreinversion> RUTA_PERFIL_DISENO = List.of(TipoEtapaPreinversion.PERFIL,
            TipoEtapaPreinversion.DISENO, TipoEtapaPreinversion.EJECUCION);

    /** Ruta completa (Anexo B.2: "Perfil + Prefactibilidad + Factibilidad + Diseño"). */
    private static final List<TipoEtapaPreinversion> RUTA_COMPLETA = List.of(TipoEtapaPreinversion.PERFIL,
            TipoEtapaPreinversion.PREFACTIBILIDAD, TipoEtapaPreinversion.FACTIBILIDAD,
            TipoEtapaPreinversion.DISENO, TipoEtapaPreinversion.EJECUCION);

    /** RN07/RN08: Programa/Estudio General usan siempre PERFIL+EJECUCION, preseleccionadas, sin criterios. */
    private static final List<TipoEtapaPreinversion> RUTA_PROGRAMA_ESTUDIO = List.of(TipoEtapaPreinversion.PERFIL,
            TipoEtapaPreinversion.EJECUCION);

    private final RutaPreinversionRepository rutaPreinversionRepository;
    private final SeleccionEtapasProyectos proyectos;
    private final SeleccionEtapasRegistro registro;

    public SeleccionEtapasRuta(RutaPreinversionRepository rutaPreinversionRepository,
            SeleccionEtapasProyectos proyectos, SeleccionEtapasRegistro registro) {
        this.rutaPreinversionRepository = rutaPreinversionRepository;
        this.proyectos = proyectos;
        this.registro = registro;
    }

    /** Estado actual de la ruta del proyecto. */
    public RutaPreinversionDto obtener(Long idProyecto) {
        return construirRutaDto(proyectos.buscar(idProyecto));
    }

    /** Calcula, sin persistir, las etapas sugeridas (solo para iniciativa PROYECTO, RN07/RN08). */
    public RutaPreinversionSugeridaDto generar(Long idProyecto, CriteriosCalificacionDto criterios) {
        var proyecto = proyectos.buscar(idProyecto);
        exigirQueNoSeaDeEmergencia(proyecto);
        exigirIniciativaProyecto(proyecto);
        return new RutaPreinversionSugeridaDto()
                .criterios(criterios)
                .etapasSugeridas(calcularEtapasSugeridas(criterios).stream()
                        .map(SeleccionEtapasRuta::aNombreEtapaDto)
                        .toList());
    }

    /**
     * Persiste la ruta calculada y deja en Registro de Etapas solo sus etapas (ver
     * {@link SeleccionEtapasRegistro#reemplazarSeleccion}).
     */
    public RutaPreinversionDto aceptar(Long idProyecto, CriteriosCalificacionDto criterios) {
        var proyecto = proyectos.buscar(idProyecto);
        exigirQueNoSeaDeEmergencia(proyecto);
        boolean esProyecto = proyecto.getIniciativaInversion() == IniciativaInversion.PROYECTO;

        List<TipoEtapaPreinversion> etapas = esProyecto ? calcularEtapasSugeridas(criterios) : RUTA_PROGRAMA_ESTUDIO;

        RutaPreinversion ruta = obtenerOCrearRuta(proyecto);
        if (esProyecto) {
            ruta.setTipoCapital(TipoCapital.valueOf(criterios.getTipoCapital().name()));
            ruta.setTamanioProyecto(TamanioProyecto.valueOf(criterios.getTamanioProyecto().name()));
            ruta.setComplejidad(ComplejidadProyecto.valueOf(criterios.getComplejidad().name()));
        }
        ruta.setFueModificada(false);
        ruta.setJustificacionUltimaModificacion(null);
        rutaPreinversionRepository.save(ruta);

        registro.reemplazarSeleccion(proyecto, etapas);

        return construirRutaDto(proyecto);
    }

    /**
     * Modifica manualmente la ruta (RN03). Las etapas que quedan fuera se eliminan; RN13: una etapa
     * ya emitida se marca bloqueadaPorModificacion en vez de eliminarse (ver
     * {@link SeleccionEtapasRegistro#reemplazarSeleccion}).
     */
    public RutaPreinversionDto modificar(Long idProyecto, ModificarRutaPreinversionRequestDto request) {
        var proyecto = proyectos.buscar(idProyecto);
        exigirQueNoSeaDeEmergencia(proyecto);
        exigirIniciativaProyecto(proyecto);
        validarModificacion(request);

        List<TipoEtapaPreinversion> nuevaSeleccion = request.getEtapas().stream()
                .map((NombreEtapaDto etapa) -> TipoEtapaPreinversion.valueOf(etapa.name()))
                .toList();

        RutaPreinversion ruta = obtenerOCrearRuta(proyecto);
        ruta.setFueModificada(true);
        ruta.setJustificacionUltimaModificacion(request.getJustificacion());
        rutaPreinversionRepository.save(ruta);

        registro.reemplazarSeleccion(proyecto, nuevaSeleccion);

        return construirRutaDto(proyecto);
    }

    /**
     * DN-03: un proyecto de emergencia no tiene Ruta de Preinversión. Pasa de la Ficha de proyectos
     * de emergencia (Anexo A.4) directamente a Viabilidad (CU-PRE-24).
     */
    private static void exigirQueNoSeaDeEmergencia(Proyecto proyecto) {
        if (Boolean.TRUE.equals(proyecto.getEsProyectoEmergencia())) {
            throw new ConflictoEstadoException("PROYECTO_EMERGENCIA_SIN_RUTA",
                    "Los proyectos de emergencia no tienen Ruta de Preinversión: pasan de la Ficha de proyectos"
                            + " de emergencia directamente a Viabilidad (CU-PRE-24).");
        }
    }

    /** RN07/RN08: Programa y Estudio General tienen siempre la ruta Perfil + Ejecución. */
    private static void exigirIniciativaProyecto(Proyecto proyecto) {
        if (proyecto.getIniciativaInversion() != IniciativaInversion.PROYECTO) {
            throw new ConflictoEstadoException(
                    "El botón \"Ruta de Preinversión\" está desactivado para proyectos que no son de iniciativa"
                            + " PROYECTO (RN07/RN08).");
        }
    }

    /**
     * RN03: la justificación es obligatoria. RN02: Perfil y Ejecución son obligatorias en la nueva
     * ruta.
     */
    private static void validarModificacion(ModificarRutaPreinversionRequestDto request) {
        List<ErrorDetalleDto> detalles = new ArrayList<>();
        if (request.getJustificacion() == null || request.getJustificacion().isBlank()) {
            detalles.add(new ErrorDetalleDto().campo("justificacion").mensaje("*Campo obligatorio"));
        }
        List<NombreEtapaDto> etapas = request.getEtapas() == null ? List.of() : request.getEtapas();
        if (!etapas.contains(NombreEtapaDto.PERFIL) || !etapas.contains(NombreEtapaDto.EJECUCION)) {
            detalles.add(new ErrorDetalleDto().campo("etapas")
                    .mensaje("La ruta debe incluir las etapas Perfil y Ejecución (RN02)."));
        }
        if (!detalles.isEmpty()) {
            throw new ValidacionNegocioException("RUTA_MODIFICADA_INVALIDA",
                    "La modificación de la Ruta de Preinversión no es válida.", detalles);
        }
    }

    private RutaPreinversion obtenerOCrearRuta(Proyecto proyecto) {
        return rutaPreinversionRepository.findByProyectoId(proyecto.getId())
                .orElseGet(() -> RutaPreinversion.builder().proyecto(proyecto).build());
    }

    /**
     * RN10 (Anexo B.2, matriz confirmada v1.3): solo el "Tipo de capital" y, cuando es Capital
     * Físico, el "Tamaño" y la "Complejidad" alteran la ruta sugerida; Capital Humano/
     * Institucional/Otros capitales siempre sugieren únicamente Perfil, sin importar Tamaño o
     * Complejidad. Ejecución no aparece en la matriz porque, junto con Perfil, está siempre
     * habilitada por defecto (RN09); se agrega aquí para que la sugerencia refleje la ruta
     * completa que terminará en Registro de Etapas.
     */
    private static List<TipoEtapaPreinversion> calcularEtapasSugeridas(CriteriosCalificacionDto criterios) {
        if (criterios.getTipoCapital() != TipoCapitalDto.CAPITAL_FISICO) {
            return RUTA_PROGRAMA_ESTUDIO;
        }
        boolean rutaPerfilDiseno = criterios.getTamanioProyecto() == TamanioProyectoDto.PEQUENIO
                || (criterios.getTamanioProyecto() == TamanioProyectoDto.MEDIANO
                        && criterios.getComplejidad() == ComplejidadProyectoDto.BAJA);
        return rutaPerfilDiseno ? RUTA_PERFIL_DISENO : RUTA_COMPLETA;
    }

    private RutaPreinversionDto construirRutaDto(Proyecto proyecto) {
        RutaPreinversion ruta = rutaPreinversionRepository.findByProyectoId(proyecto.getId()).orElse(null);
        List<NombreEtapaDto> etapasAceptadas = registro.enOrdenDeRuta(proyecto.getId()).stream()
                .map((EtapaPreinversion etapa) -> aNombreEtapaDto(etapa.getTipoEtapa()))
                .toList();

        RutaPreinversionDto dto = new RutaPreinversionDto()
                .idProyecto(proyecto.getId())
                .etapasAceptadas(etapasAceptadas)
                .fueModificada(ruta != null && Boolean.TRUE.equals(ruta.getFueModificada()));

        if (ruta != null) {
            dto.setJustificacionUltimaModificacion(ruta.getJustificacionUltimaModificacion());
            dto.setCriterios(criteriosDe(ruta));
        }
        return dto;
    }

    /** Criterios con que se aceptó la ruta; {@code null} si falta alguno (ruta de Programa/Estudio General). */
    private static CriteriosCalificacionDto criteriosDe(RutaPreinversion ruta) {
        if (ruta.getTipoCapital() == null || ruta.getTamanioProyecto() == null || ruta.getComplejidad() == null) {
            return null;
        }
        return new CriteriosCalificacionDto()
                .tipoCapital(TipoCapitalDto.valueOf(ruta.getTipoCapital().name()))
                .tamanioProyecto(TamanioProyectoDto.valueOf(ruta.getTamanioProyecto().name()))
                .complejidad(ComplejidadProyectoDto.valueOf(ruta.getComplejidad().name()));
    }

    private static NombreEtapaDto aNombreEtapaDto(TipoEtapaPreinversion tipoEtapa) {
        return NombreEtapaDto.valueOf(tipoEtapa.name());
    }
}
