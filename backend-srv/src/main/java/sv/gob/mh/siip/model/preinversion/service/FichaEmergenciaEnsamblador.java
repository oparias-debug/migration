package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import sv.gob.mh.siip.model.common.domain.Municipio;
import sv.gob.mh.siip.model.common.repository.MunicipioRepository;
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
 * proyecto y de la ficha guardada, resolviendo el nombre de cada producto desde el catálogo y el
 * departamento a partir del distrito (Anexo C.5).
 */
@Component
public class FichaEmergenciaEnsamblador {

    private final ProductoIndicadorCatalogoRepository productoIndicadorCatalogoRepository;
    private final MunicipioRepository municipioRepository;

    public FichaEmergenciaEnsamblador(ProductoIndicadorCatalogoRepository productoIndicadorCatalogoRepository,
            MunicipioRepository municipioRepository) {
        this.productoIndicadorCatalogoRepository = productoIndicadorCatalogoRepository;
        this.municipioRepository = municipioRepository;
    }

    /**
     * Ficha del proyecto de emergencia. Sin ficha guardada ({@code null}) lleva los datos del
     * proyecto, las etapas actual/futura y, precargados desde CU-PRE-01 para que el Técnico URP los
     * edite (Anexo B.1), la descripción y la inversión estimada. "Objetivo general" no se precarga:
     * el proyecto no guarda un objetivo en CU-PRE-01.
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
            return dto
                    .descripcionProyecto(proyecto.getDescripcionProyecto())
                    .inversionEstimada(proyecto.getMontoEstimadoInversion());
        }

        double totalComponentesCosto = ficha.getComponentesCosto().stream()
                .mapToDouble(ComponenteCostoEmergencia::getCosto)
                .sum();

        return dto
                .planteamientoProblema(ficha.getPlanteamientoProblema())
                .objetivoGeneral(ficha.getObjetivoGeneral())
                .descripcionProyecto(ficha.getDescripcionProyecto())
                .productos(resolverProductos(ficha.getProductos()))
                .departamento(derivarDepartamento(ficha.getDistrito()))
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

    /**
     * Departamento del distrito según el catálogo de ubicaciones geográficas (Anexo C.5): "Nivel
     * nacional" es su propio departamento, "{departamento} - Nivel departamental" lleva el nombre
     * delante y el resto se busca por nombre. Devuelve {@code null} si el distrito no está en el
     * catálogo o si su nombre se repite en varios departamentos (p. ej. "San Lorenzo"): la ficha
     * guarda el distrito por nombre, no por código (no existe una entidad Distrito).
     */
    String derivarDepartamento(String distrito) {
        if (distrito == null || distrito.isBlank()) {
            return null;
        }
        String nombre = distrito.strip();
        if (nombre.equalsIgnoreCase(CatalogosSeleccionEtapasServiceImpl.NIVEL_NACIONAL)) {
            return CatalogosSeleccionEtapasServiceImpl.NIVEL_NACIONAL;
        }
        return departamentoDelDistrito(nombre);
    }

    private String departamentoDelDistrito(String nombre) {
        String sufijo = CatalogosSeleccionEtapasServiceImpl.SUFIJO_NIVEL_DEPARTAMENTAL.toLowerCase(Locale.ROOT);
        if (nombre.toLowerCase(Locale.ROOT).endsWith(sufijo)) {
            return nombre.substring(0, nombre.length() - sufijo.length()).strip();
        }
        List<String> departamentos = municipioRepository.findByNombreIgnoreCase(nombre).stream()
                .map((Municipio municipio) -> municipio.getDepartamento().getNombre())
                .distinct()
                .toList();
        return departamentos.size() == 1 ? departamentos.get(0) : null;
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
