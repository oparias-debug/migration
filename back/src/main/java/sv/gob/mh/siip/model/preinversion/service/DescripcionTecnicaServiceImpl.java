package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.gob.mh.siip.model.preinversion.domain.Componente;
import sv.gob.mh.siip.model.preinversion.domain.DescripcionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.ProductoIndicadorCatalogo;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.UnidadMedida;
import sv.gob.mh.siip.model.preinversion.dto.DescripcionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.DescripcionTecnicaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaDescripcionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaDescripcionTecnicaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoSeleccionadoDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoCostoResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoUnidadMedidaDto;
import sv.gob.mh.siip.model.preinversion.dto.UnidadMedidaResumenDto;
import sv.gob.mh.siip.model.preinversion.mapper.DescripcionTecnicaMapper;
import sv.gob.mh.siip.model.preinversion.repository.ComponenteRepository;
import sv.gob.mh.siip.model.preinversion.repository.DescripcionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.OpinionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProductoIndicadorCatalogoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.UnidadMedidaRepository;
import org.springframework.context.annotation.Lazy;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.security.ActorContexto;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Implementación de la capa de servicio de negocio para el CU-PRE-11 (Descripción Técnica).
 * Gestiona el autocompletado en memoria de reglas de negocio (RN03, RN04), la carga en modo consulta
 * para el rol {@code TECNICO_PRE} y la persistencia relacional transaccional mediante el patrón
 * "replace-all" de hijos al guardar.
 *
 * @author Luis Medrano
 * @version 1.0
 */
@Service
public class DescripcionTecnicaServiceImpl implements DescripcionTecnicaService {

    private final DescripcionTecnicaRepository descripcionTecnicaRepository;
    private final ProyectoRepository proyectoRepository;
    private final ComponenteRepository componenteRepository;
    private final DescripcionTecnicaMapper descripcionTecnicaMapper;
    private final OpinionTecnicaRepository opinionTecnicaRepository;
    private final ProductoIndicadorCatalogoRepository productoIndicadorCatalogoRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;
    private final DescripcionTecnicaService self;
    private final ActorContexto actorContexto;



    /**
     * Constructor para la inyección de dependencias requeridas por la implementación del servicio.
     *
     * @param descripcionTecnicaRepository Repositorio JPA para la cabecera de Descripción Técnica.
     * @param proyectoRepository Repositorio JPA para entidades de Proyecto.
     * @param componenteRepository Repositorio JPA para las filas de Componentes.
     * @param descripcionTecnicaMapper Componente Mapper para transformaciones DTO-Entidad.
     * @param productoIndicadorCatalogoRepository Resuelve el nombre de "Producto" (catálogo C.6) a partir de
     *        su código.
     * @param unidadMedidaRepository Resuelve "Unidad de Medida" (CU-ADM-02) a partir de su nombre/código.
     * @param actorContexto Resuelve el actor autenticado y valida su rol (x-roles del contrato OpenAPI).
     */
    public DescripcionTecnicaServiceImpl(
            DescripcionTecnicaRepository descripcionTecnicaRepository,
            ProyectoRepository proyectoRepository,
            ComponenteRepository componenteRepository,
            DescripcionTecnicaMapper descripcionTecnicaMapper,
            OpinionTecnicaRepository opinionTecnicaRepository,
            ProductoIndicadorCatalogoRepository productoIndicadorCatalogoRepository,
            UnidadMedidaRepository unidadMedidaRepository,
            @Lazy DescripcionTecnicaService self,
            ActorContexto actorContexto) {
        this.descripcionTecnicaRepository = descripcionTecnicaRepository;
        this.proyectoRepository = proyectoRepository;
        this.componenteRepository = componenteRepository;
        this.descripcionTecnicaMapper = descripcionTecnicaMapper;
        this.opinionTecnicaRepository = opinionTecnicaRepository;
        this.productoIndicadorCatalogoRepository = productoIndicadorCatalogoRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
        this.self = self;
        this.actorContexto = actorContexto;
    }

    /**
     * {@inheritDoc}
     *
     * Ejecuta una transacción de solo lectura. Recupera la cabecera registrada o crea
     * un objeto volátil en memoria autocompletando la descripción (RN03). Asimismo, retorna
     * las filas asociadas registradas.
     */
    @Override
    @Transactional(readOnly = true)
    public DescripcionTecnicaDto obtenerDescripcionTecnica(Long idProyecto) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE);

        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new NoSuchElementException("Proyecto no encontrado con ID: " + idProyecto));

        // 1. Obtener entidad persistida o resolver autocompletado jerárquico
        // (RN03: Opinión Técnica / O.T. > Proyecto)
        DescripcionTecnica entity = descripcionTecnicaRepository.findByProyectoId(idProyecto)
                .orElseGet(() -> crearDescripcionAutocompletada(proyecto, idProyecto));

        DescripcionTecnicaDto dto = descripcionTecnicaMapper.toDto(entity);
        dto.setIdProyecto(idProyecto);

        // Mapeo explícito a la propiedad del DTO de respuesta
        if (entity.getDescripcion() != null && !entity.getDescripcion().isBlank()) {
            dto.setDescripcionProyecto(entity.getDescripcion());
        } else {
            dto.setDescripcionProyecto(resolverDescripcionAutocompletada(proyecto, idProyecto));
        }

        // 2. Mapear componentes/filas si existen
        List<Componente> componentes = componenteRepository.findByProyectoId(idProyecto);

        if (!componentes.isEmpty()) {
            List<FilaDescripcionTecnicaDto> filas = componentes.stream()
                    .map(this::toFilaDescripcionTecnicaDto)
                    .toList();

            dto.setFilas(filas);
        }

        return dto;
    }

    /** Crea la cabecera volátil (no persistida) con la descripción autocompletada según RN03. */
    private DescripcionTecnica crearDescripcionAutocompletada(Proyecto proyecto, Long idProyecto) {
        DescripcionTecnica nueva = new DescripcionTecnica();
        nueva.setProyecto(proyecto);
        nueva.setDescripcion(resolverDescripcionAutocompletada(proyecto, idProyecto));
        return nueva;
    }

    /**
     * Fallback RN03: si hay O.T. (OpinionTecnica) se toman sus observaciones, y en caso contrario
     * la descripción del proyecto.
     */
    private String resolverDescripcionAutocompletada(Proyecto proyecto, Long idProyecto) {
        return opinionTecnicaRepository
                .findFirstByProyectoIdOrderByFechaEmisionDesc(idProyecto)
                .map(OpinionTecnica::getObservaciones)
                .orElseGet(proyecto::getDescripcionProyecto);
    }

    private FilaDescripcionTecnicaDto toFilaDescripcionTecnicaDto(Componente comp) {
        FilaDescripcionTecnicaDto fila = descripcionTecnicaMapper.toFilaDto(comp);

        if (fila.getComponente() == null) {
            TipoCostoResumenDto tipoCosto = new TipoCostoResumenDto();
            tipoCosto.setCodigo("3");
            tipoCosto.setNombre(comp.getNombre());
            fila.setComponente(tipoCosto);
        }

        fila.setDescripcionProducto(comp.getDescripcion());
        if (comp.getCodigoProducto() != null) {
            fila.setProducto(new ProductoSeleccionadoDto()
                    .codigoProducto(comp.getCodigoProducto())
                    .producto(resolverNombreProducto(comp.getCodigoProducto())));
        }
        if (comp.getUnidadMedida() != null) {
            fila.setUnidadMedida(resolverUnidadMedida(comp.getUnidadMedida()));
        }
        return fila;
    }

    /**
     * {@inheritDoc}
     *
     * Ejecuta una transacción de escritura. Crea o actualiza la cabecera {@link DescripcionTecnica},
     * purga la lista anterior de entidades {@link Componente} pertenecientes al proyecto
     * y persiste las nuevas filas enviadas en el DTO de entrada.
     */
    @Override
    @Transactional
    public DescripcionTecnicaDto guardarDescripcionTecnica(Long idProyecto, DescripcionTecnicaRequestDto requestDto) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);

        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new NoSuchElementException("Proyecto no encontrado con ID: " + idProyecto));
        EdicionFormulacion.exigirEditable(proyecto);

        // 1. Guardar o actualizar cabecera
        DescripcionTecnica entity = descripcionTecnicaRepository.findByProyectoId(idProyecto)
                .map((DescripcionTecnica existente) -> {
                    descripcionTecnicaMapper.updateEntityFromDto(requestDto, existente);
                    if (requestDto.getDescripcionProyecto() != null) {
                        existente.setDescripcion(requestDto.getDescripcionProyecto());
                    }
                    return existente;
                })
                .orElseGet(() -> {
                    DescripcionTecnica nueva = descripcionTecnicaMapper.toEntity(requestDto);
                    nueva.setProyecto(proyecto);
                    nueva.setDescripcion(
                            requestDto.getDescripcionProyecto() != null
                                    ? requestDto.getDescripcionProyecto()
                                    : proyecto.getDescripcionProyecto()
                    );
                    return nueva;
                });

        descripcionTecnicaRepository.save(entity);

        // 2. Aplicar estrategia "replace-all" para el listado de componentes
        componenteRepository.deleteByProyectoId(idProyecto);

        if (requestDto.getFilas() != null && !requestDto.getFilas().isEmpty()) {
            List<Componente> nuevosComponentes = requestDto.getFilas().stream()
                    .map((FilaDescripcionTecnicaRequestDto filaDto) -> {
                        Componente comp = descripcionTecnicaMapper.toComponenteEntity(filaDto);
                        comp.setProyecto(proyecto);
                        // Asegura el mapeo explicito de la propiedad 'componente' (String) del RequestDto
                        // al campo obligatorio 'nombre' (NotBlank) de la Entidad
                        comp.setNombre(filaDto.getComponente());
                        comp.setDescripcion(filaDto.getDescripcionProducto());
                        return comp;
                    })
                    .toList();
            componenteRepository.saveAll(nuevosComponentes);
        }

        return self.obtenerDescripcionTecnica(idProyecto);
    }

    /** Resuelve el nombre de "Producto" (catálogo C.6) a partir de su código; {@code null} si no existe. */
    private String resolverNombreProducto(String codigoProducto) {
        return productoIndicadorCatalogoRepository.findByCodigoProductoIn(List.of(codigoProducto)).stream()
                .findFirst().map(ProductoIndicadorCatalogo::getProducto).orElse(null);
    }

    /**
     * Resuelve "Unidad de Medida" (CU-ADM-02) a partir de su nombre. Construye directamente el DTO
     * del paquete {@code preinversion.dto}
     * (y no {@link sv.gob.mh.siip.model.administracion.dto.UnidadMedidaResumenDto},
     * usado por {@code CatalogosAdministracionMapper}): el generador de OpenAPI produce una clase
     * distinta por cada modelPackage que referencia el esquema, así que ambas son incompatibles en
     * tiempo de compilación pese a tener la misma forma — mismo criterio ya aplicado a
     * {@code TipoCostoResumenDto} en este mismo servicio.
     */
    private UnidadMedidaResumenDto resolverUnidadMedida(String nombre) {
        return unidadMedidaRepository.findFirstByNombre(nombre)
                .map(DescripcionTecnicaServiceImpl::toUnidadMedidaResumenDto).orElse(null);
    }

    private static UnidadMedidaResumenDto toUnidadMedidaResumenDto(UnidadMedida u) {
        return new UnidadMedidaResumenDto().tipo(TipoUnidadMedidaDto.valueOf(u.getTipo().name()))
                .categoria(u.getCategoria()).unidadMedida(u.getNombre()).descripcion(u.getDescripcion());
    }
}
