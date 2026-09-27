package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import org.springframework.stereotype.Component;

import sv.gob.mh.siip.model.preinversion.domain.ComponenteCostoEmergencia;
import sv.gob.mh.siip.model.preinversion.domain.FichaEmergencia;
import sv.gob.mh.siip.model.preinversion.domain.ProductoIndicadorCatalogo;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ComponenteCostoDto;
import sv.gob.mh.siip.model.preinversion.dto.FichaEmergenciaDto;
import sv.gob.mh.siip.model.preinversion.dto.FuenteFinanciamientoDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoSeleccionadoDto;
import sv.gob.mh.siip.model.preinversion.enums.FuenteFinanciamiento;
import sv.gob.mh.siip.model.preinversion.repository.ProductoIndicadorCatalogoRepository;

/**
 * Arma la respuesta de la Ficha de proyectos de emergencia de CU-PRE-03.5 (Anexo A.4) a partir del
 * proyecto y de la ficha guardada, resolviendo el nombre de cada producto desde el catálogo.
 */
@Component
public class FichaEmergenciaEnsamblador {

    private final ProductoIndicadorCatalogoRepository productoIndicadorCatalogoRepository;

    public FichaEmergenciaEnsamblador(ProductoIndicadorCatalogoRepository productoIndicadorCatalogoRepository) {
        this.productoIndicadorCatalogoRepository = productoIndicadorCatalogoRepository;
    }

    /**
     * Ficha del proyecto de emergencia; sin ficha guardada ({@code null}) solo lleva los datos del
     * proyecto y las etapas actual/futura.
     */
    public FichaEmergenciaDto construir(Proyecto proyecto, FichaEmergencia ficha) {
        FichaEmergenciaDto dto = new FichaEmergenciaDto()
                .cup(proyecto.getCup())
                .nombreProyecto(proyecto.getNombre())
                .etapaActual(NombreEtapaDto.PERFIL)
                .etapaFutura(NombreEtapaDto.EJECUCION)
                .numeroDecretoLegislativo(proyecto.getNumeroDecretoLegislativo())
                .tipoEvento(proyecto.getTipoEvento());

        if (ficha == null) {
            return dto;
        }

        double totalComponentesCosto = ficha.getComponentesCosto().stream()
                .mapToDouble(ComponenteCostoEmergencia::getCosto)
                .sum();

        return dto
                .planteamientoProblema(ficha.getPlanteamientoProblema())
                .objetivoGeneral(ficha.getObjetivoGeneral())
                .descripcionProyecto(ficha.getDescripcionProyecto())
                .productos(resolverProductos(ficha.getProductos()))
                .departamento(null)
                .distrito(ficha.getDistrito())
                .latitud(ficha.getLatitud())
                .longitud(ficha.getLongitud())
                .direccionEspecifica(ficha.getDireccionEspecifica())
                .poblacionObjetivo(ficha.getPoblacionObjetivo())
                .inversionEstimada(ficha.getInversionEstimada())
                .archivoPresupuestoUrl(ficha.getArchivoPresupuestoUrl())
                .componentesCosto(ficha.getComponentesCosto().stream()
                        .map(FichaEmergenciaEnsamblador::aComponenteCostoDto)
                        .toList())
                .totalComponentesCosto(ficha.getComponentesCosto().isEmpty() ? null : totalComponentesCosto)
                .costosOperacion(ficha.getCostosOperacion())
                .costosMantenimiento(ficha.getCostosMantenimiento())
                .fuentesFinanciamiento(ficha.getFuentesFinanciamiento().stream()
                        .map((FuenteFinanciamiento fuente) -> FuenteFinanciamientoDto.valueOf(fuente.name()))
                        .toList())
                .fuenteRecursos(ficha.getFuenteRecursos())
                .archivoProgramacionUrl(ficha.getArchivoProgramacionUrl());
    }

    private List<ProductoSeleccionadoDto> resolverProductos(List<String> codigosProducto) {
        if (codigosProducto.isEmpty()) {
            return List.of();
        }
        List<ProductoIndicadorCatalogo> catalogo =
                productoIndicadorCatalogoRepository.findByCodigoProductoIn(codigosProducto);
        return codigosProducto.stream()
                .map((String codigo) -> aProductoSeleccionado(codigo, catalogo))
                .toList();
    }

    private static ProductoSeleccionadoDto aProductoSeleccionado(String codigo,
            List<ProductoIndicadorCatalogo> catalogo) {
        ProductoSeleccionadoDto dto = new ProductoSeleccionadoDto().codigoProducto(codigo);
        catalogo.stream()
                .filter((ProductoIndicadorCatalogo producto) -> producto.getCodigoProducto().equals(codigo))
                .findFirst()
                .ifPresent((ProductoIndicadorCatalogo producto) -> dto.setProducto(producto.getProducto()));
        return dto;
    }

    private static ComponenteCostoDto aComponenteCostoDto(ComponenteCostoEmergencia componente) {
        return new ComponenteCostoDto().tipoCosto(componente.getTipoCosto()).costo(componente.getCosto());
    }
}
