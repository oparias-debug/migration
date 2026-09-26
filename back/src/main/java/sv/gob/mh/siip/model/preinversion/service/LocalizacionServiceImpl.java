package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.Municipio;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.MunicipioRepository;
import sv.gob.mh.siip.model.preinversion.domain.Localizacion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaDto;
import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaFilaDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaLocalizacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.LocalizacionDto;
import sv.gob.mh.siip.model.preinversion.dto.LocalizacionRequestDto;
import sv.gob.mh.siip.model.preinversion.mapper.LocalizacionMapper;
import sv.gob.mh.siip.model.preinversion.repository.LocalizacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

import java.util.ArrayList;
import java.util.List;


/**
 * Implementación del servicio para la gestión de la localización del proyecto (CU-PRE-12).
 * Orquesta la lógica de negocio para consultar, registrar, guardar y autocompletar
 * las filas de localización geográfica.
 *
 * @author Luis Medrano
 */
@Service
@Transactional
public class LocalizacionServiceImpl implements LocalizacionService {

    private final LocalizacionRepository localizacionRepository;
    private final ProyectoRepository proyectoRepository;
    private final LocalizacionMapper localizacionMapper;
    private final MunicipioRepository municipioRepository;
    private final ActorContexto actorContexto;
    private final AreaInfluenciaService areaInfluenciaService;


    /**
     * Constructor para la inyección de dependencias del servicio de localización.
     *
     * @param localizacionRepository Repositorio de persistencia para la entidad Localización.
     * @param localizacionMapper Mapper MapStruct para transformaciones entre entidad y DTOs.
     */
    public LocalizacionServiceImpl(LocalizacionRepository localizacionRepository,
                                   ProyectoRepository proyectoRepository,
                                   LocalizacionMapper localizacionMapper,
                                   MunicipioRepository municipioRepository,
                                   ActorContexto actorContexto,
                                   AreaInfluenciaService areaInfluenciaService) {
        this.localizacionRepository = localizacionRepository;
        this.proyectoRepository = proyectoRepository;
        this.localizacionMapper = localizacionMapper;
        this.municipioRepository = municipioRepository;
        this.actorContexto = actorContexto;
        this.areaInfluenciaService = areaInfluenciaService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public LocalizacionDto obtenerLocalizacion(Long idProyecto) {
        List<Localizacion> entidades = localizacionRepository.findAllByProyectoId(idProyecto);

        LocalizacionDto dto = new LocalizacionDto();
        dto.setIdProyecto(idProyecto);

        List<FilaLocalizacionRequestDto> filas = (entidades == null || entidades.isEmpty()) ?
                new ArrayList<>() :
                entidades.stream()
                        .map(localizacionMapper::toFilaDto)
                        .toList();

        dto.setFilas(filas);

        // Nota: Si el estatus del terreno se maneja en otra entidad/tabla,
        // se consultaría y asignaría a dto.setPropiedadTerreno(...) aquí.

        return dto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public LocalizacionDto guardarLocalizacion(Long idProyecto, LocalizacionRequestDto localizacionRequestDto) {
        // CU-PRE-24 RN04: con la formulación bloqueada no se admiten cambios.
        proyectoRepository.findById(idProyecto).ifPresent(EdicionFormulacion::exigirEditable);

        // Reemplazo completo de filas para el proyecto
        localizacionRepository.deleteByProyectoId(idProyecto);

        List<FilaLocalizacionRequestDto> filasRequest = localizacionRequestDto.getFilas();
        if (filasRequest != null) {
            for (FilaLocalizacionRequestDto filaDto : filasRequest) {
                Localizacion entidad = localizacionMapper.toEntity(idProyecto, filaDto);

                if (filaDto.getDistrito() != null) {
                    Municipio muni = buscarMunicipio(filaDto.getDistrito());
                    entidad.setMunicipio(muni);

                    // Extraemos y seteamos el departamento que ya trae el municipio por su relación @ManyToOne
                    if (muni != null && muni.getDepartamento() != null) {
                        entidad.setDepartamento(muni.getDepartamento());
                    }
                }

                localizacionRepository.save(entidad);
            }
        }

        LocalizacionDto responseDto = new LocalizacionDto();
        responseDto.setIdProyecto(idProyecto);
        responseDto.setFilas(filasRequest != null ? filasRequest : new ArrayList<>());

        return responseDto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public LocalizacionDto autocompletarLocalizacionDesdeAreaInfluencia(Long idProyecto) {
        // 1. Validación de seguridad y roles (Técnico URP)
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);
        EdicionFormulacion.exigirEditable(proyecto);

        // 2. Consulta de el área influencia registrada previamente (CU-PRE-08)
        AreaInfluenciaDto areaInfluencia = areaInfluenciaService.autocompletarDesdePoblacionObjetivo(proyecto.getId());

        if (areaInfluencia == null || areaInfluencia.getFilas().isEmpty()) {
            return construirDtoVacio(proyecto);
        }

        // 3. Mapeo funcional de distritos/ubicaciones hacia las filas de localización (RN03 / FA-03)
        List<FilaLocalizacionRequestDto> filas = areaInfluencia.getFilas().stream()
                .filter(area -> area.getDepartamento() != null && !area.getDepartamento().isBlank())
                .map(this::construirFilaAutocompletada)
                .toList();

        return construirDto(proyecto, filas);
    }

    private FilaLocalizacionRequestDto construirFilaAutocompletada(AreaInfluenciaFilaDto area) {
        Municipio municipio = buscarMunicipio(area.getDistrito());

        return new FilaLocalizacionRequestDto()
                .departamento(municipio.getDepartamento().getNombre())
                .distrito(municipio.getNombre())
                .direccionEspecifica(area.getUbicacionEspecifica())
                // Ajusta aquí si la celda trae las coordenadas mapeadas a CoordenadasDto
                .coordenadas(null);
    }

    private Proyecto buscarProyecto(Long idProyecto) {
        return proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException("El proyecto " + idProyecto + " no existe."));
    }

    private static void exigirAlcanceUnidadEjecutora(Usuario actor, Proyecto proyecto) {
        if (actor.getUnidadEjecutora() != null
                && !actor.getUnidadEjecutora().getId().equals(proyecto.getUnidadEjecutora().getId())) {
            throw new AccesoDenegadoException(
                    "El proyecto no pertenece a una Unidad Ejecutora dentro de las credenciales del actor.");
        }
    }

    private static LocalizacionDto construirDto(Proyecto proyecto, List<FilaLocalizacionRequestDto> filas) {
        List<FilaLocalizacionRequestDto> dtoFilas = filas.stream()
                .map(fila -> new FilaLocalizacionRequestDto()
                        .departamento(fila.getDepartamento())
                        .distrito(fila.getDistrito())
                        .direccionEspecifica(fila.getDireccionEspecifica())
                        .coordenadas(fila.getCoordenadas()))
                .toList();

        return new LocalizacionDto()
                .idProyecto(proyecto.getId())
                .filas(dtoFilas);
    }



    private Municipio buscarMunicipio(String distrito) {
        return municipioRepository.findAllByOrderByNombreAsc().stream()
                .filter(municipio -> municipio.getCodigo().equalsIgnoreCase(distrito)
                        || municipio.getNombre().equalsIgnoreCase(distrito))
                .findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El distrito " + distrito + " no existe en el catalogo geografico."));
    }

    private static LocalizacionDto construirDtoVacio(Proyecto proyecto) {
        return new LocalizacionDto()
                .idProyecto(proyecto.getId())
                .filas(new ArrayList<>());
    }
}
