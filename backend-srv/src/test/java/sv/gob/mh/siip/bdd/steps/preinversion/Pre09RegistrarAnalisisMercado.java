package sv.gob.mh.siip.bdd.steps.preinversion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import java.util.List;

import org.springframework.web.context.request.RequestContextHolder;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.ContextoProyectoBdd;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.preinversion.domain.ProductoIndicadorCatalogo;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisMercadoDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisMercadoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaAnalisisMercadoDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaAnalisisMercadoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoSeleccionadoDto;
import sv.gob.mh.siip.model.preinversion.repository.ProductoIndicadorCatalogoRepository;
import sv.gob.mh.siip.model.preinversion.service.AnalisisMercadoService;

public class Pre09RegistrarAnalisisMercado {

    /** Productos del Catálogo de Productos e Indicadores (RN07) que usan los escenarios. */
    private static final String PRODUCTO = "PROD-BDD";
    private static final String OTRO_PRODUCTO = "PROD-BDD-2";
    private static final String UNIDAD_MEDIDA = "Unidad BDD";

    private final ContextoProyectoBdd contextoProyecto;
    private final AnalisisMercadoService service;
    private final ProductoIndicadorCatalogoRepository catalogo;

    private boolean escenarioActivo;
    private AnalisisMercadoRequestDto borrador;
    private AnalisisMercadoDto guardado;
    private ValidacionNegocioException ultimaExcepcion;
    private String campoPendiente;

    public Pre09RegistrarAnalisisMercado(ContextoProyectoBdd contextoProyecto,
            AnalisisMercadoService service,
            ProductoIndicadorCatalogoRepository catalogo) {
        this.contextoProyecto = contextoProyecto;
        this.service = service;
        this.catalogo = catalogo;
    }

    public void activarEscenario() {
        escenarioActivo = true;
        Proyecto proyecto = contextoProyecto.getProyectoActual();
        assertThat(proyecto).as("proyecto de prueba").isNotNull();
        assertThat(service.obtener(proyecto.getId()).getFilas()).isEmpty();
        registrarEnCatalogo(PRODUCTO);
        registrarEnCatalogo(OTRO_PRODUCTO);
    }

    public boolean esEscenarioAnalisisMercado() {
        return escenarioActivo;
    }

    @Cuando("el Técnico URP selecciona un {string} del catálogo")
    public void seleccionaProducto(String campo) {
        borrador = new AnalisisMercadoRequestDto().filas(List.of(new FilaAnalisisMercadoRequestDto()
                .producto(new ProductoSeleccionadoDto().codigoProducto(PRODUCTO))));
    }

    @Entonces("el sistema autocompleta el campo {string} correspondiente")
    public void autocompletaUnidadMedida(String campo) {
        // La unidad se resuelve desde el catálogo al guardar; se verifica en "guarda la información registrada".
        assertThat(campo).isEqualTo("Unidad de Medida");
    }

    @Cuando("el Técnico URP registra la {string} y la {string} del año base")
    public void registraDemandaYOferta(String demanda, String oferta) {
        fila().demanda(100.0).oferta(60.0);
    }

    @Cuando("el Técnico URP registra los {string}, la {string} y la {string}")
    public void registraProyeccion(String anios, String tasaDemanda, String tasaOferta) {
        fila().aniosAProyectar(2).tasaDemanda(10.0).tasaOferta(5.0);
    }

    @Entonces("el sistema calcula el campo {string} como Demanda menos Oferta")
    public void calculaDeficit(String campo) {
        // Aún faltan años y tasas (la fila no está completa, RN04): el Déficit calculado por el servidor
        // se verifica al guardar, en el paso de los promedios.
        assertThat(campo).isEqualTo("Déficit");
    }

    @Entonces("el sistema calcula el {string} y el {string} según las fórmulas de proyección definidas")
    public void calculaPromedios(String promedioDemanda, String promedioOferta) {
        guardarBorrador();
        assertThat(guardado.getFilas().get(0).getDeficit()).isEqualTo(40.0);
        // Anexo B.1: promedio del año base y los años proyectados, (100 + 110 + 121) / 3 y (60 + 63 + 66,15) / 3.
        assertThat(guardado.getFilas().get(0).getPromedioDemanda()).isCloseTo(110.333333, within(0.000001));
        assertThat(guardado.getFilas().get(0).getPromedioOferta()).isCloseTo(63.05, within(0.000001));
    }

    @Entonces("calcula el {string} como Promedio Demanda menos Promedio Oferta")
    public void calculaPromedioDeficit(String campo) {
        assertThat(guardado.getFilas().get(0).getPromedioDeficit()).isCloseTo(47.283333, within(0.000001));
    }

    @Entonces("el sistema no permite guardar, ya que debe existir al menos una fila completamente diligenciada \\(RN04\\)")
    public void rechazaFilasIncompletas() {
        capturarRechazo(borrador);
        assertThat(ultimaExcepcion.getCodigo()).isEqualTo("ANALISIS_MERCADO_SIN_FILA_COMPLETA");
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("que el Técnico URP selecciona un producto que no pertenece al catálogo")
    public void seleccionaProductoFueraDelCatalogo() {
        assertThat(catalogo.findByCodigoProductoIn(List.of("PROD-INEXISTENTE"))).isEmpty();
        borrador = new AnalisisMercadoRequestDto().filas(List.of(filaCompleta(PRODUCTO), filaCompleta("PROD-INEXISTENTE")));
    }

    @Entonces("el sistema no permite guardar, ya que los productos provienen del catálogo de productos e indicadores \\(RN07\\)")
    public void rechazaProductoFueraDelCatalogo() {
        capturarRechazo(borrador);
        assertThat(ultimaExcepcion.getCodigo()).isEqualTo("PRODUCTO_NO_EN_CATALOGO");
        assertThat(service.obtener(contextoProyecto.getProyectoActual().getId()).getFilas()).isEmpty();
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("una fila de producto ya registrada")
    public void unaFilaDeProductoYaRegistrada() {
        borrador = new AnalisisMercadoRequestDto().filas(List.of(filaCompleta(PRODUCTO)));
        guardarBorrador();
    }

    @Cuando("el Técnico URP hace clic en el botón emergente {string} de esa fila")
    public void haceClicEnBotonEmergenteDeFila(String boton) {
        assertThat(boton).isEqualTo("x");
    }

    @Dado("que ninguna fila de la tabla está completamente diligenciada")
    public void ningunaFilaEstaCompleta() {
        borrador = new AnalisisMercadoRequestDto().filas(List.of(new FilaAnalisisMercadoRequestDto()
                .producto(new ProductoSeleccionadoDto().codigoProducto(PRODUCTO)).demanda(100.0)));
    }

    @Entonces("el sistema elimina la fila correspondiente \\(RN03\\)")
    public void eliminaFila() {
        // RN03: eliminar la fila ocurre en el cliente; el siguiente "Guardar" envía la tabla sin ella.
        guardado = service.guardar(contextoProyecto.getProyectoActual().getId(),
                new AnalisisMercadoRequestDto().filas(List.of(filaCompleta(OTRO_PRODUCTO))));
        assertThat(guardado.getFilas()).extracting(f -> f.getProducto().getCodigoProducto())
                .containsExactly(OTRO_PRODUCTO);
    }

    @Entonces("el sistema sombrea en rojo el borde del campo {string} \\(RN05\\)")
    public void sombreaCampoIncompleto(String campo) {
        // RN05 es solo visual: el servidor guarda la fila incompleta (hay otra completa, RN04) y el cliente
        // sombrea el campo que quedó vacío.
        assertThat(campo).isEqualTo(campoPendiente);
        AnalisisMercadoDto recargado = service.obtener(contextoProyecto.getProyectoActual().getId());
        assertThat(recargado.getFilas()).hasSize(2);
        FilaAnalisisMercadoDto incompleta = recargado.getFilas().get(1);
        switch (campo) {
            case "Producto" -> assertThat(incompleta.getProducto().getCodigoProducto()).isNull();
            case "Demanda" -> assertThat(incompleta.getDemanda()).isNull();
            case "Oferta" -> assertThat(incompleta.getOferta()).isNull();
            default -> throw new IllegalArgumentException("Campo no reconocido: " + campo);
        }
        RequestContextHolder.resetRequestAttributes();
    }

    public void guardarInformacionRegistrada() {
        guardarBorrador();
        assertThat(guardado.getFilas()).singleElement()
                .satisfies(fila -> assertThat(fila.getUnidadMedida()).isEqualTo(UNIDAD_MEDIDA));
    }

    public void verificarSeccion() {
        AnalisisMercadoDto recargado = service.obtener(contextoProyecto.getProyectoActual().getId());
        assertThat(recargado.getFilas()).hasSize(1);
        RequestContextHolder.resetRequestAttributes();
    }

    /** Guarda una fila completa y otra a la que le falta el campo indicado (RN05). */
    public void guardarSinCompletarCampo(String campo) {
        FilaAnalisisMercadoRequestDto incompleta = filaCompleta(OTRO_PRODUCTO);
        switch (campo) {
            case "Producto" -> incompleta.setProducto(null);
            case "Demanda" -> incompleta.setDemanda(null);
            case "Oferta" -> incompleta.setOferta(null);
            default -> throw new IllegalArgumentException("Campo no reconocido: " + campo);
        }
        campoPendiente = campo;
        guardado = service.guardar(contextoProyecto.getProyectoActual().getId(),
                new AnalisisMercadoRequestDto().filas(List.of(filaCompleta(PRODUCTO), incompleta)));
    }

    private FilaAnalisisMercadoRequestDto fila() {
        if (borrador == null || borrador.getFilas().isEmpty()) {
            borrador = new AnalisisMercadoRequestDto().filas(List.of(new FilaAnalisisMercadoRequestDto()
                    .producto(new ProductoSeleccionadoDto().codigoProducto(PRODUCTO))));
        }
        return borrador.getFilas().get(0);
    }

    private static FilaAnalisisMercadoRequestDto filaCompleta(String codigoProducto) {
        return new FilaAnalisisMercadoRequestDto()
                .producto(new ProductoSeleccionadoDto().codigoProducto(codigoProducto))
                .demanda(100.0).oferta(60.0).aniosAProyectar(2).tasaDemanda(10.0).tasaOferta(5.0);
    }

    private void guardarBorrador() {
        guardado = service.guardar(contextoProyecto.getProyectoActual().getId(), borrador);
    }

    private void capturarRechazo(AnalisisMercadoRequestDto request) {
        ultimaExcepcion = null;
        try {
            service.guardar(contextoProyecto.getProyectoActual().getId(), request);
        } catch (ValidacionNegocioException ex) {
            ultimaExcepcion = ex;
        }
        assertThat(ultimaExcepcion).isNotNull();
    }

    private void registrarEnCatalogo(String codigo) {
        if (catalogo.findByCodigoProductoIn(List.of(codigo)).isEmpty()) {
            catalogo.save(ProductoIndicadorCatalogo.builder()
                    .codigoProducto(codigo).producto("Producto " + codigo)
                    .codigoIndicador("IND-" + codigo).indicador("Indicador " + codigo)
                    .unidadMedida(UNIDAD_MEDIDA).esIndicadorPrincipal(true).build());
        }
    }
}
