package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import org.springframework.stereotype.Component;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.RutaPreinversion;
import sv.gob.mh.siip.model.preinversion.dto.ComplejidadProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.CriteriosCalificacionDto;
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
        Proyecto proyecto = proyectos.buscar(idProyecto);
        if (proyecto.getIniciativaInversion() != IniciativaInversion.PROYECTO) {
            throw new ConflictoEstadoException(
                    "El botón \"Ruta de Preinversión\" está desactivado para proyectos que no son de iniciativa"
                            + " PROYECTO (RN07/RN08).");
        }
        return new RutaPreinversionSugeridaDto()
                .criterios(criterios)
                .etapasSugeridas(calcularEtapasSugeridas(criterios).stream()
                        .map(SeleccionEtapasRuta::aNombreEtapaDto)
                        .toList());
    }

    /** Persiste la ruta calculada y traslada sus etapas a Registro de Etapas. */
    public RutaPreinversionDto aceptar(Long idProyecto, CriteriosCalificacionDto criterios) {
        Proyecto proyecto = proyectos.buscar(idProyecto);
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

        registro.sincronizar(proyecto, etapas, false);

        return construirRutaDto(proyecto);
    }

    /**
     * Modifica manualmente la ruta (RN03). RN13: una etapa ya emitida que queda fuera de la nueva
     * selección se marca bloqueadaPorModificacion en vez de rechazar la operación completa.
     */
    public RutaPreinversionDto modificar(Long idProyecto, ModificarRutaPreinversionRequestDto request) {
        Proyecto proyecto = proyectos.buscar(idProyecto);

        List<TipoEtapaPreinversion> nuevaSeleccion = request.getEtapas().stream()
                .map((NombreEtapaDto etapa) -> TipoEtapaPreinversion.valueOf(etapa.name()))
                .toList();

        RutaPreinversion ruta = obtenerOCrearRuta(proyecto);
        ruta.setFueModificada(true);
        ruta.setJustificacionUltimaModificacion(request.getJustificacion());
        rutaPreinversionRepository.save(ruta);

        registro.sincronizar(proyecto, nuevaSeleccion, true);

        return construirRutaDto(proyecto);
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
