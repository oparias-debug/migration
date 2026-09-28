package sv.gob.mh.siip.model.preinversion.service;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import sv.gob.mh.siip.model.preinversion.domain.Componente;
import sv.gob.mh.siip.model.preinversion.domain.ProductoIndicadorCatalogo;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.UnidadMedida;
import sv.gob.mh.siip.model.preinversion.dto.FilaDescripcionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaDescripcionTecnicaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoSeleccionadoDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoCostoResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoUnidadMedidaDto;
import sv.gob.mh.siip.model.preinversion.dto.UnidadMedidaResumenDto;
import sv.gob.mh.siip.model.preinversion.mapper.DescripcionTecnicaMapper;
import sv.gob.mh.siip.model.preinversion.repository.ProductoIndicadorCatalogoRepository;
import sv.gob.mh.siip.model.preinversion.repository.UnidadMedidaRepository;

/**
 * Filas (componentes) de la Descripción Técnica (CU-PRE-11): conversión de cada {@link Componente}
 * a su fila de respuesta, resolviendo "Producto" (catálogo C.6) y "Unidad de Medida" (CU-ADM-02), y
 * construcción de los componentes a persistir a partir de las filas de la solicitud.
 *
 * @author Luis Medrano
 * @version 1.0
 */
@Component
public class DescripcionTecnicaFilas {

    /** Código del tipo de costo asignado a la fila cuando el mapeo no resuelve el componente. */
    private static final String CODIGO_TIPO_COSTO_POR_DEFECTO = "3";

    private final DescripcionTecnicaMapper descripcionTecnicaMapper;
    private final ProductoIndicadorCatalogoRepository productoIndicadorCatalogoRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;

    /**
     * Constructor para la inyección de dependencias.
     *
     * @param descripcionTecnicaMapper Componente Mapper para transformaciones DTO-Entidad.
     * @param productoIndicadorCatalogoRepository Resuelve el nombre de "Producto" (catálogo C.6) a partir de
     *        su código.
     * @param unidadMedidaRepository Resuelve "Unidad de Medida" (CU-ADM-02) a partir de su nombre/código.
     */
    public DescripcionTecnicaFilas(
            DescripcionTecnicaMapper descripcionTecnicaMapper,
            ProductoIndicadorCatalogoRepository productoIndicadorCatalogoRepository,
            UnidadMedidaRepository unidadMedidaRepository) {
        this.descripcionTecnicaMapper = descripcionTecnicaMapper;
        this.productoIndicadorCatalogoRepository = productoIndicadorCatalogoRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
    }

    /**
     * Convierte un componente registrado en su fila de respuesta.
     *
     * @param comp componente del proyecto
     * @return la fila con producto y unidad de medida resueltos contra sus catálogos
     */
    public FilaDescripcionTecnicaDto toFila(Componente comp) {
        FilaDescripcionTecnicaDto fila = descripcionTecnicaMapper.toFilaDto(comp);

        if (fila.getComponente() == null) {
            TipoCostoResumenDto tipoCosto = new TipoCostoResumenDto();
            tipoCosto.setCodigo(CODIGO_TIPO_COSTO_POR_DEFECTO);
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
     * Construye los componentes a persistir para el proyecto a partir de las filas de la solicitud.
     *
     * @param filas filas enviadas en la solicitud
     * @param proyecto proyecto al que pertenecen
     * @return los componentes (no persistidos)
     */
    public List<Componente> toComponentes(Collection<FilaDescripcionTecnicaRequestDto> filas, Proyecto proyecto) {
        return filas.stream()
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
     * {@code TipoCostoResumenDto} en este mismo componente.
     */
    private UnidadMedidaResumenDto resolverUnidadMedida(String nombre) {
        return unidadMedidaRepository.findFirstByNombre(nombre)
                .map(DescripcionTecnicaFilas::toUnidadMedidaResumenDto).orElse(null);
    }

    private static UnidadMedidaResumenDto toUnidadMedidaResumenDto(UnidadMedida u) {
        return new UnidadMedidaResumenDto().tipo(TipoUnidadMedidaDto.valueOf(u.getTipo().name()))
                .categoria(u.getCategoria()).unidadMedida(u.getNombre()).descripcion(u.getDescripcion());
    }
}
