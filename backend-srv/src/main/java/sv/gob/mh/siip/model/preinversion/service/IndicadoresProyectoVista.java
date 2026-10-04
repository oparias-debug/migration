package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import org.springframework.stereotype.Component;

import sv.gob.mh.siip.model.preinversion.domain.Componente;
import sv.gob.mh.siip.model.preinversion.domain.Identificacion;
import sv.gob.mh.siip.model.preinversion.domain.IndicadorProyecto;
import sv.gob.mh.siip.model.preinversion.domain.PresupuestoProyecto;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoPresupuestoDto;
import static sv.gob.mh.siip.model.preinversion.enums.TipoIndicadorProyecto.RESULTADO;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.IndicadorProductoDto;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.IndicadorResultadoDto;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.IndicadoresProyectoDto;
import sv.gob.mh.siip.model.preinversion.indicadores.dto.ProductoIndicadoresDto;
import sv.gob.mh.siip.model.preinversion.repository.ComponenteRepository;
import sv.gob.mh.siip.model.preinversion.repository.IdentificacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.IndicadorProyectoRepository;
import sv.gob.mh.siip.model.preinversion.repository.PresupuestoProyectoRepository;
import static sv.gob.mh.siip.model.preinversion.service.IndicadoresProyectoReglas.mismaCantidad;
import static sv.gob.mh.siip.model.preinversion.service.IndicadoresProyectoReglas.sumar;

/** Arma la pantalla de CU-PRE-23: indicadores, objetivo general y peso relativo de cada producto. */
@Component
class IndicadoresProyectoVista {
    private final ComponenteRepository componentes;
    private final IndicadorProyectoRepository indicadores;
    private final IdentificacionRepository identificaciones;
    private final PresupuestoProyectoRepository presupuestos;
    private final PresupuestoInversionEnsamblador presupuestoEnsamblador;

    IndicadoresProyectoVista(ComponenteRepository componentes, IndicadorProyectoRepository indicadores,
            IdentificacionRepository identificaciones, PresupuestoProyectoRepository presupuestos,
            PresupuestoInversionEnsamblador presupuestoEnsamblador) {
        this.componentes = componentes;
        this.indicadores = indicadores;
        this.identificaciones = identificaciones;
        this.presupuestos = presupuestos;
        this.presupuestoEnsamblador = presupuestoEnsamblador;
    }

    IndicadoresProyectoDto respuesta(Proyecto proyecto) {
        List<Componente> componentesProyecto = componentes.findByProyectoIdOrderByIdAsc(proyecto.getId());
        List<ProductoIndicadoresDto> productos = componentesProyecto.stream().map((Componente componente) -> {
            List<IndicadorProductoDto> filas = indicadores.findByComponenteId(componente.getId()).stream()
                    .map((IndicadorProyecto indicador) -> producto(indicador, componente)).toList();
            return new ProductoIndicadoresDto(componente.getId(), nombreProducto(componente), componente.getCantidad(),
                    filas).unidadMedida(componente.getUnidadMedida())
                    .pesoRelativoProducto(pesoRelativo(proyecto, componente, componentesProyecto));
        }).toList();
        String objetivo = identificaciones.findByProyectoId(proyecto.getId()).map(Identificacion::getObjetivoGeneral)
                .orElse(null);
        List<IndicadorResultadoDto> resultados = indicadores.findByProyectoIdAndTipo(proyecto.getId(), RESULTADO)
                .stream().map(this::resultado).toList();
        return new IndicadoresProyectoDto(proyecto.getId(), resultados, productos).objetivoGeneral(objetivo);
    }

    IndicadorResultadoDto resultado(IndicadorProyecto indicador) {
        return new IndicadorResultadoDto(indicador.getId(), indicador.getNombre()).codigo(indicador.getCodigo())
                .descripcionIndicador(indicador.getDescripcion()).unidadMedida(indicador.getUnidadMedida())
                .metaGlobal(indicador.getMetaGlobal());
    }

    IndicadorProductoDto producto(IndicadorProyecto indicador, Componente componente) {
        List<Double> metas = indicador.getMetasPorPeriodo() == null ? List.of() : indicador.getMetasPorPeriodo();
        boolean cubierta = componente.getCantidad() != null
                && indicadores.findByComponenteId(componente.getId()).stream().anyMatch(
                        (IndicadorProyecto fila) -> mismaCantidad(fila.getMetaGlobal(), componente.getCantidad()));
        return new IndicadorProductoDto(indicador.getId(), indicador.getNombre(), metas, sumar(metas), !cubierta)
                .codigo(indicador.getCodigo()).descripcionIndicador(indicador.getDescripcion())
                .unidadMedida(indicador.getUnidadMedida()).esIndicadorPrincipal(indicador.getEsIndicadorPrincipal())
                .metaGlobal(indicador.getMetaGlobal()).metaEsAcumulativa(indicador.getMetaEsAcumulativa());
    }

    private Double pesoRelativo(Proyecto proyecto, Componente componente, List<Componente> componentesProyecto) {
        return presupuestos.findByProyectoId(proyecto.getId())
                .map((PresupuestoProyecto presupuesto) -> calcularPeso(presupuesto, componente, componentesProyecto))
                .orElse(null);
    }

    private Double calcularPeso(PresupuestoProyecto presupuesto, Componente componente,
            List<Componente> componentesProyecto) {
        var presupuestoDto = presupuestoEnsamblador.dto(componente.getProyecto(), presupuesto);
        Double total = presupuestoDto.getInversionEstimadaPreciosMercado().getTotal();
        if (total == null || total == 0D) {
            return null;
        }
        int numeroProducto = componentesProyecto.indexOf(componente) + 1;
        return presupuestoDto.getProductos().stream()
                .filter((ProductoPresupuestoDto producto) -> producto.getNumero() == numeroProducto).findFirst()
                .map((ProductoPresupuestoDto producto) -> producto.getCostoProductoTotal() / total).orElse(null);
    }

    private static String nombreProducto(Componente componente) {
        return componente.getDescripcion() == null || componente.getDescripcion().isBlank()
                ? componente.getCodigoProducto()
                : componente.getDescripcion();
    }
}
