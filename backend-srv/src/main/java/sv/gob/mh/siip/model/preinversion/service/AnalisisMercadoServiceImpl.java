package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.AnalisisMercado;
import sv.gob.mh.siip.model.preinversion.domain.FilaAnalisisMercado;
import sv.gob.mh.siip.model.preinversion.domain.ProductoIndicadorCatalogo;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisMercadoDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisMercadoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaAnalisisMercadoRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.AnalisisMercadoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProductoIndicadorCatalogoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-PRE-09 "Análisis de Mercado". Déficit y promedios se calculan siempre en el servidor (Anexo B.1).
 * Las filas incompletas se admiten (RN05 es solo el sombreado del cliente) mientras haya al menos una
 * completa (RN04); las filas totalmente vacías se descartan.
 */
@Service
@Transactional
public class AnalisisMercadoServiceImpl implements AnalisisMercadoService {

    private final ProyectoRepository proyectoRepository;
    private final AnalisisMercadoRepository analisisMercadoRepository;
    private final ProductoIndicadorCatalogoRepository productoRepository;
    private final ActorContexto actorContexto;

    public AnalisisMercadoServiceImpl(ProyectoRepository proyectoRepository,
            AnalisisMercadoRepository analisisMercadoRepository,
            ProductoIndicadorCatalogoRepository productoRepository,
            ActorContexto actorContexto) {
        this.proyectoRepository = proyectoRepository;
        this.analisisMercadoRepository = analisisMercadoRepository;
        this.productoRepository = productoRepository;
        this.actorContexto = actorContexto;
    }

    @Override
    @Transactional(readOnly = true)
    public AnalisisMercadoDto obtener(Long idProyecto) {
        Usuario actor = actorContexto.exigir();
        Proyecto proyecto = buscarProyecto(idProyecto);
        ViabilidadAcceso.exigirAlcanceUnidadEjecutora(actor, proyecto);
        AnalisisMercado analisis = analisisMercadoRepository.findByProyectoId(idProyecto).orElse(null);
        return CalculoAnalisisMercado.respuesta(proyecto.getId(), analisis == null ? List.of() : analisis.getFilas());
    }

    @Override
    public AnalisisMercadoDto guardar(Long idProyecto, AnalisisMercadoRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarProyecto(idProyecto);
        ViabilidadAcceso.exigirAlcanceUnidadEjecutora(actor, proyecto);
        EdicionFormulacion.exigirEditable(proyecto);

        List<FilaAnalisisMercadoRequestDto> filas = ValidacionAnalisisMercado.filasDiligenciadas(request);
        Map<String, ProductoIndicadorCatalogo> catalogo = productosDelCatalogo(filas);
        ValidacionAnalisisMercado.exigirProductosDelCatalogo(filas, catalogo);
        ValidacionAnalisisMercado.exigirRangos(filas);
        ValidacionAnalisisMercado.exigirProductosSinRepetir(filas);

        AnalisisMercado analisis = analisisMercadoRepository.findByProyectoId(idProyecto)
                .orElseGet(() -> AnalisisMercado.builder().proyecto(proyecto).build());
        analisis.setFilas(new ArrayList<>(filas.stream().map(fila -> mapearFila(fila, catalogo)).toList()));
        analisis = analisisMercadoRepository.save(analisis);
        return CalculoAnalisisMercado.respuesta(proyecto.getId(), analisis.getFilas());
    }

    /**
     * RN07: los productos provienen del Catálogo de Productos e Indicadores (CU-PRE-03.5); de él salen
     * el nombre y la unidad de medida (Anexo B.1).
     */
    private Map<String, ProductoIndicadorCatalogo> productosDelCatalogo(List<FilaAnalisisMercadoRequestDto> filas) {
        List<String> codigos = filas.stream().map(ValidacionAnalisisMercado::codigoProducto)
                .filter(Objects::nonNull).distinct().toList();
        Map<String, ProductoIndicadorCatalogo> catalogo = new HashMap<>();
        if (!codigos.isEmpty()) {
            // Un producto aparece una vez por indicador; nombre y unidad de medida son los mismos.
            productoRepository.findByCodigoProductoIn(codigos)
                    .forEach(producto -> catalogo.putIfAbsent(producto.getCodigoProducto(), producto));
        }
        return catalogo;
    }

    private static FilaAnalisisMercado mapearFila(FilaAnalisisMercadoRequestDto fila,
            Map<String, ProductoIndicadorCatalogo> catalogo) {
        String codigo = ValidacionAnalisisMercado.codigoProducto(fila);
        ProductoIndicadorCatalogo producto = codigo == null ? null : catalogo.get(codigo);
        return FilaAnalisisMercado.builder()
                .codigoProducto(codigo)
                .producto(producto == null ? null : producto.getProducto())
                .unidadMedida(producto == null ? null : producto.getUnidadMedida())
                .demanda(fila.getDemanda())
                .oferta(fila.getOferta())
                .aniosAProyectar(fila.getAniosAProyectar())
                .tasaDemanda(fila.getTasaDemanda())
                .tasaOferta(fila.getTasaOferta())
                .build();
    }

    private Proyecto buscarProyecto(Long idProyecto) {
        return proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException(ViabilidadAcceso.PROYECTO_NO_ENCONTRADO,
                        "El proyecto " + idProyecto + " no existe."));
    }
}
