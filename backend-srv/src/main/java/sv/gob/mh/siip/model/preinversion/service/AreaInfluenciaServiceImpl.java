package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Municipio;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.MunicipioRepository;
import sv.gob.mh.siip.model.preinversion.domain.AnalisisPoblacion;
import sv.gob.mh.siip.model.preinversion.domain.AreaInfluencia;
import sv.gob.mh.siip.model.preinversion.domain.CeldaUbicacionPoblacion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaDto;
import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaFilaDto;
import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaFilaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ErrorDetalleDto;
import sv.gob.mh.siip.model.preinversion.repository.AnalisisPoblacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.AreaInfluenciaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-PRE-08 "Área de Influencia".
 *
 * <p>Decisiones de negocio sobre lo que el CU deja abierto: Región, Departamento y Distrito son editables
 * (RN07 prevalece sobre RN08), y el guardado admite campos pendientes (RN05 es solo el sombreado del
 * cliente), por lo que una fila puede quedar sin "Ubicación específica".
 */
@Service
@Transactional
public class AreaInfluenciaServiceImpl implements AreaInfluenciaService {

    public static final String CODIGO_DISTRITO_INVALIDO = "DISTRITO_INVALIDO";
    public static final String CODIGO_FILA_DUPLICADA = "FILA_DUPLICADA";

    private final ProyectoRepository proyectoRepository;
    private final AreaInfluenciaRepository areaInfluenciaRepository;
    private final AnalisisPoblacionRepository analisisPoblacionRepository;
    private final MunicipioRepository municipioRepository;
    private final ActorContexto actorContexto;

    public AreaInfluenciaServiceImpl(ProyectoRepository proyectoRepository,
            AreaInfluenciaRepository areaInfluenciaRepository,
            AnalisisPoblacionRepository analisisPoblacionRepository,
            MunicipioRepository municipioRepository,
            ActorContexto actorContexto) {
        this.proyectoRepository = proyectoRepository;
        this.areaInfluenciaRepository = areaInfluenciaRepository;
        this.analisisPoblacionRepository = analisisPoblacionRepository;
        this.municipioRepository = municipioRepository;
        this.actorContexto = actorContexto;
    }

    @Override
    @Transactional(readOnly = true)
    public AreaInfluenciaDto obtener(Long idProyecto) {
        Usuario actor = actorContexto.exigir();
        Proyecto proyecto = buscarProyecto(idProyecto);
        ViabilidadAcceso.exigirAlcanceUnidadEjecutora(actor, proyecto);
        return construirDto(proyecto, areaInfluenciaRepository.findByProyectoIdOrderByIdAsc(idProyecto));
    }

    /**
     * Reemplaza las filas del área de influencia. Cada distrito debe identificarse sin ambigüedad en el
     * catálogo y no se admiten filas repetidas; si algo no es válido no se guarda nada.
     */
    @Override
    public AreaInfluenciaDto guardar(Long idProyecto, AreaInfluenciaRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarProyecto(idProyecto);
        ViabilidadAcceso.exigirAlcanceUnidadEjecutora(actor, proyecto);
        EdicionFormulacion.exigirEditable(proyecto);

        List<AreaInfluenciaFilaRequestDto> filas = Optional.ofNullable(request)
                .map(AreaInfluenciaRequestDto::getFilas)
                .orElse(List.of());
        List<AreaInfluencia> nuevas = validarFilas(proyecto, filas);

        areaInfluenciaRepository.deleteAll(areaInfluenciaRepository.findByProyectoIdOrderByIdAsc(idProyecto));
        areaInfluenciaRepository.saveAll(nuevas);
        return construirDto(proyecto, areaInfluenciaRepository.findByProyectoIdOrderByIdAsc(idProyecto));
    }

    /**
     * FA-03 / RN07: propone una fila por cada ubicación de la Población Objetivo (CU-PRE-07), sin guardar.
     * Esa ubicación es texto libre: si identifica un distrito del catálogo se completan Región,
     * Departamento y Distrito; si no (p. ej. "Comunidad Río Mar"), quedan vacíos para que el Técnico URP
     * elija el distrito. En ambos casos el texto se propone como "Ubicación específica".
     */
    @Override
    public AreaInfluenciaDto autocompletarDesdePoblacionObjetivo(Long idProyecto) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarProyecto(idProyecto);
        ViabilidadAcceso.exigirAlcanceUnidadEjecutora(actor, proyecto);
        EdicionFormulacion.exigirEditable(proyecto);

        List<AreaInfluenciaFilaDto> filas = analisisPoblacionRepository.findByProyectoId(idProyecto)
                .map(AnalisisPoblacion::getUbicacionesObjetivo)
                .orElse(List.of())
                .stream()
                .map(CeldaUbicacionPoblacion::getUbicacion)
                .filter(ubicacion -> ubicacion != null && !ubicacion.isBlank())
                .map(String::strip)
                .map(this::filaAutocompletada)
                .toList();
        return new AreaInfluenciaDto().idProyecto(proyecto.getId()).filas(filas);
    }

    private AreaInfluenciaFilaDto filaAutocompletada(String ubicacion) {
        AreaInfluenciaFilaDto fila = new AreaInfluenciaFilaDto().ubicacionEspecifica(ubicacion);
        List<Municipio> candidatos = candidatos(ubicacion);
        if (candidatos.size() == 1) {
            Municipio municipio = candidatos.get(0);
            fila.region(municipio.getDepartamento().getRegion())
                    .departamento(municipio.getDepartamento().getNombre())
                    .distrito(municipio.getNombre());
        }
        return fila;
    }

    /** Resuelve el distrito de cada fila y rechaza filas repetidas, reportando todas las filas inválidas. */
    private List<AreaInfluencia> validarFilas(Proyecto proyecto, List<AreaInfluenciaFilaRequestDto> filas) {
        List<ErrorDetalleDto> invalidas = new ArrayList<>();
        List<AreaInfluencia> nuevas = new ArrayList<>();
        for (int i = 0; i < filas.size(); i++) {
            AreaInfluenciaFilaRequestDto fila = filas.get(i);
            String campo = "filas[" + i + "].distrito";
            String distrito = fila.getDistrito() == null ? "" : fila.getDistrito().strip();
            List<Municipio> candidatos = distrito.isEmpty() ? List.of() : candidatos(distrito);
            if (distrito.isEmpty()) {
                invalidas.add(detalle(campo, "Seleccione un distrito."));
            } else if (candidatos.isEmpty()) {
                invalidas.add(detalle(campo, "El distrito " + distrito + " no existe en el catálogo geográfico."));
            } else if (candidatos.size() > 1) {
                invalidas.add(detalle(campo, "El nombre " + distrito + " corresponde a varios distritos ("
                        + departamentos(candidatos) + "); identifíquelo por su código."));
            } else {
                Municipio municipio = candidatos.get(0);
                nuevas.add(AreaInfluencia.builder()
                        .proyecto(proyecto)
                        .departamento(municipio.getDepartamento())
                        .municipio(municipio)
                        .descripcion(textoOVacio(fila.getUbicacionEspecifica()))
                        .build());
            }
        }
        if (!invalidas.isEmpty()) {
            throw new ValidacionNegocioException(CODIGO_DISTRITO_INVALIDO,
                    "Hay distritos que no se pueden identificar en el catálogo geográfico.", invalidas);
        }
        exigirSinDuplicados(nuevas);
        return nuevas;
    }

    /** Dos filas con el mismo distrito y la misma "Ubicación específica" registran lo mismo. */
    private static void exigirSinDuplicados(List<AreaInfluencia> filas) {
        Set<String> vistas = new HashSet<>();
        List<ErrorDetalleDto> duplicadas = new ArrayList<>();
        for (int i = 0; i < filas.size(); i++) {
            AreaInfluencia fila = filas.get(i);
            String descripcion = fila.getDescripcion() == null ? "" : fila.getDescripcion().toLowerCase(Locale.ROOT);
            if (!vistas.add(fila.getMunicipio().getCodigo() + "|" + descripcion)) {
                duplicadas.add(detalle("filas[" + i + "]",
                        "Fila repetida: mismo distrito y misma ubicación específica."));
            }
        }
        if (!duplicadas.isEmpty()) {
            throw new ValidacionNegocioException(CODIGO_FILA_DUPLICADA, "El área de influencia tiene filas repetidas.",
                    duplicadas);
        }
    }

    /**
     * Distritos que corresponden al texto: el código, que es único, tiene prioridad; si no, el nombre,
     * que puede repetirse en varios departamentos.
     */
    private List<Municipio> candidatos(String distrito) {
        return municipioRepository.findByCodigoIgnoreCase(distrito)
                .map(List::of)
                .orElseGet(() -> municipioRepository.findByNombreIgnoreCase(distrito));
    }

    private static String departamentos(List<Municipio> municipios) {
        return municipios.stream()
                .map(municipio -> municipio.getDepartamento().getNombre())
                .collect(Collectors.joining(", "));
    }

    private static String textoOVacio(String texto) {
        return texto == null || texto.isBlank() ? null : texto.strip();
    }

    private static ErrorDetalleDto detalle(String campo, String mensaje) {
        return new ErrorDetalleDto().campo(campo).mensaje(mensaje);
    }

    private Proyecto buscarProyecto(Long idProyecto) {
        return proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException(ViabilidadAcceso.PROYECTO_NO_ENCONTRADO,
                        "El proyecto " + idProyecto + " no existe."));
    }

    private static AreaInfluenciaDto construirDto(Proyecto proyecto, List<AreaInfluencia> filas) {
        List<AreaInfluenciaFilaDto> dtoFilas = filas.stream()
                .map(fila -> new AreaInfluenciaFilaDto()
                        .region(fila.getDepartamento().getRegion())
                        .departamento(fila.getDepartamento().getNombre())
                        .distrito(fila.getMunicipio().getNombre())
                        .ubicacionEspecifica(fila.getDescripcion()))
                .toList();
        return new AreaInfluenciaDto().idProyecto(proyecto.getId()).filas(dtoFilas);
    }
}
