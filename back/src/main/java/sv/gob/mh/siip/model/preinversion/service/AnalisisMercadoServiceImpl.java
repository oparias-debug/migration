package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.AccesoDenegadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.AnalisisMercado;
import sv.gob.mh.siip.model.preinversion.domain.FilaAnalisisMercado;
import sv.gob.mh.siip.model.preinversion.domain.ProductoIndicadorCatalogo;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisMercadoDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisMercadoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaAnalisisMercadoDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaAnalisisMercadoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoSeleccionadoDto;
import sv.gob.mh.siip.model.preinversion.repository.AnalisisMercadoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProductoIndicadorCatalogoRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

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
        exigirAlcanceUnidadEjecutora(actor, proyecto);
        AnalisisMercado analisis = analisisMercadoRepository.findByProyectoId(idProyecto).orElse(null);
        return construirDto(proyecto, analisis == null ? List.of() : analisis.getFilas());
    }

    @Override
    public AnalisisMercadoDto guardar(Long idProyecto, AnalisisMercadoRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = buscarProyecto(idProyecto);
        exigirAlcanceUnidadEjecutora(actor, proyecto);

        List<FilaAnalisisMercadoRequestDto> filas = request == null || request.getFilas() == null
                ? List.of() : request.getFilas();
        if (filas.stream().noneMatch(this::estaCompleta)) {
            throw new ValidacionNegocioException("ANALISIS_MERCADO_SIN_FILA_COMPLETA",
                    "Debe existir al menos una fila de análisis de mercado completamente diligenciada.", null);
        }

        AnalisisMercado analisis = analisisMercadoRepository.findByProyectoId(idProyecto)
                .orElseGet(() -> AnalisisMercado.builder().proyecto(proyecto).build());
        analisis.setFilas(new ArrayList<>(filas.stream().map(this::mapearFila).toList()));
        analisis = analisisMercadoRepository.save(analisis);
        return construirDto(proyecto, analisis.getFilas());
    }

    private boolean estaCompleta(FilaAnalisisMercadoRequestDto fila) {
        ProductoSeleccionadoDto producto = fila == null ? null : fila.getProducto();
        return producto != null
            && producto.getCodigoProducto() != null
            && !producto.getCodigoProducto().isBlank()
                && fila.getDemanda() != null && fila.getOferta() != null
                && fila.getAniosAProyectar() != null && fila.getTasaDemanda() != null
                && fila.getTasaOferta() != null;
    }

    private FilaAnalisisMercado mapearFila(FilaAnalisisMercadoRequestDto fila) {
        String codigo = fila.getProducto() == null ? null : fila.getProducto().getCodigoProducto();
        ProductoIndicadorCatalogo producto = buscarProducto(codigo);
        return FilaAnalisisMercado.builder()
                .codigoProducto(codigo)
                .producto(producto == null ? nombreEnviado(fila) : producto.getProducto())
                .unidadMedida(producto == null ? null : producto.getUnidadMedida())
                .demanda(fila.getDemanda())
                .oferta(fila.getOferta())
                .aniosAProyectar(fila.getAniosAProyectar())
                .tasaDemanda(fila.getTasaDemanda())
                .tasaOferta(fila.getTasaOferta())
                .build();
    }

    private String nombreEnviado(FilaAnalisisMercadoRequestDto fila) {
        ProductoSeleccionadoDto producto = fila.getProducto();
        return producto == null ? null : producto.getProducto();
    }

    private ProductoIndicadorCatalogo buscarProducto(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return null;
        }
        return productoRepository.findByCodigoProductoIn(List.of(codigo)).stream().findFirst().orElse(null);
    }

    private AnalisisMercadoDto construirDto(Proyecto proyecto, List<FilaAnalisisMercado> filas) {
        List<FilaAnalisisMercadoDto> resultado = new ArrayList<>();
        for (FilaAnalisisMercado fila : filas) {
            ProductoSeleccionadoDto producto = new ProductoSeleccionadoDto()
                    .codigoProducto(fila.getCodigoProducto()).producto(fila.getProducto());
            Double deficit = calcularDeficit(fila.getDemanda(), fila.getOferta());
            Double promedioDemanda = proyectar(fila.getDemanda(), fila.getTasaDemanda(), fila.getAniosAProyectar());
            Double promedioOferta = proyectar(fila.getOferta(), fila.getTasaOferta(), fila.getAniosAProyectar());
            resultado.add(new FilaAnalisisMercadoDto()
                    .producto(producto)
                    .unidadMedida(fila.getUnidadMedida())
                    .demanda(fila.getDemanda())
                    .oferta(fila.getOferta())
                    .deficit(deficit)
                    .aniosAProyectar(fila.getAniosAProyectar())
                    .tasaDemanda(fila.getTasaDemanda())
                    .tasaOferta(fila.getTasaOferta())
                    .promedioDemanda(promedioDemanda)
                    .promedioOferta(promedioOferta)
                    .promedioDeficit(calcularDeficit(promedioDemanda, promedioOferta)));
        }
        return new AnalisisMercadoDto().idProyecto(proyecto.getId()).filas(resultado);
    }

    private Double calcularDeficit(Double demanda, Double oferta) {
        return demanda == null || oferta == null ? null : demanda - oferta;
    }

    private Double proyectar(Double base, Double tasa, Integer anios) {
        return base == null || tasa == null || anios == null
                ? null : base * Math.pow(1.0 + tasa / 100.0, anios);
    }

    private Proyecto buscarProyecto(Long idProyecto) {
        return proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException("El proyecto " + idProyecto + " no existe."));
    }

    private void exigirAlcanceUnidadEjecutora(Usuario actor, Proyecto proyecto) {
        if (actor.getUnidadEjecutora() != null
                && !actor.getUnidadEjecutora().getId().equals(proyecto.getUnidadEjecutora().getId())) {
            throw new AccesoDenegadoException(
                    "El proyecto no pertenece a una Unidad Ejecutora dentro de las credenciales del actor.");
        }
    }
}
