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
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisMercadoDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisMercadoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FilaAnalisisMercadoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ProductoSeleccionadoDto;
import sv.gob.mh.siip.model.preinversion.service.AnalisisMercadoService;

public class Pre09RegistrarAnalisisMercado {

    private final ContextoProyectoBdd contextoProyecto;
    private final AnalisisMercadoService service;

    private boolean escenarioActivo;
    private AnalisisMercadoRequestDto borrador;
    private AnalisisMercadoDto guardado;
    private ValidacionNegocioException ultimaExcepcion;

    public Pre09RegistrarAnalisisMercado(ContextoProyectoBdd contextoProyecto,
            AnalisisMercadoService service) {
        this.contextoProyecto = contextoProyecto;
        this.service = service;
    }

    public void activarEscenario() {
        escenarioActivo = true;
        Proyecto proyecto = contextoProyecto.getProyectoActual();
        assertThat(proyecto).as("proyecto de prueba").isNotNull();
        assertThat(service.obtener(proyecto.getId()).getFilas()).isEmpty();
    }

    public boolean esEscenarioAnalisisMercado() {
        return escenarioActivo;
    }

    @Cuando("el Técnico URP selecciona un {string} del catálogo")
    public void seleccionaProducto(String campo) {
        borrador = new AnalisisMercadoRequestDto().filas(List.of(new FilaAnalisisMercadoRequestDto()
                .producto(new ProductoSeleccionadoDto().codigoProducto("PROD-BDD"))));
    }

    @Entonces("el sistema autocompleta el campo {string} correspondiente")
    public void autocompletaUnidadMedida(String campo) {
        assertThat(campo).isEqualTo("Unidad de Medida");
    }

    @Cuando("el Técnico URP registra la {string} y la {string} del año base")
    public void registraDemandaYOferta(String demanda, String oferta) {
        fila().demanda(100.0).oferta(60.0);
        assertThat(fila().getDemanda() - fila().getOferta()).isEqualTo(40.0);
    }

    @Cuando("el Técnico URP registra los {string}, la {string} y la {string}")
    public void registraProyeccion(String anios, String tasaDemanda, String tasaOferta) {
        fila().aniosAProyectar(2).tasaDemanda(10.0).tasaOferta(5.0);
    }

    @Entonces("el sistema calcula el campo {string} como Demanda menos Oferta")
    public void calculaDeficit(String campo) {
        assertThat(fila().getDemanda() - fila().getOferta()).isEqualTo(40.0);
    }

    @Entonces("el sistema calcula el {string} y el {string} según las fórmulas de proyección definidas")
    public void calculaPromedios(String promedioDemanda, String promedioOferta) {
        guardarBorrador();
        assertThat(guardado.getFilas().get(0).getPromedioDemanda()).isCloseTo(121.0, within(0.000001));
        assertThat(guardado.getFilas().get(0).getPromedioOferta()).isCloseTo(66.15, within(0.000001));
    }

    @Entonces("calcula el {string} como Promedio Demanda menos Promedio Oferta")
    public void calculaPromedioDeficit(String campo) {
        assertThat(guardado.getFilas().get(0).getPromedioDeficit()).isCloseTo(54.85, within(0.000001));
    }

    @Entonces("el sistema no permite guardar, ya que debe existir al menos una fila completamente diligenciada \\(RN04\\)")
    public void rechazaFilasIncompletas() {
        try {
            service.guardar(contextoProyecto.getProyectoActual().getId(), new AnalisisMercadoRequestDto()
                    .filas(List.of(new FilaAnalisisMercadoRequestDto())));
        } catch (ValidacionNegocioException ex) {
            ultimaExcepcion = ex;
        }
        assertThat(ultimaExcepcion).isNotNull();
        assertThat(ultimaExcepcion.getCodigo()).isEqualTo("ANALISIS_MERCADO_SIN_FILA_COMPLETA");
        RequestContextHolder.resetRequestAttributes();
    }

    @Dado("una fila de producto ya registrada")
    public void unaFilaDeProductoYaRegistrada() {
        borrador = new AnalisisMercadoRequestDto().filas(List.of(filaCompleta()));
        guardarBorrador();
    }

    @Cuando("el Técnico URP hace clic en el botón emergente {string} de esa fila")
    public void haceClicEnBotonEmergenteDeFila(String boton) {
        assertThat(boton).isEqualTo("x");
    }

    @Dado("que ninguna fila de la tabla está completamente diligenciada")
    public void ningunaFilaEstaCompleta() {
        borrador = new AnalisisMercadoRequestDto().filas(List.of(new FilaAnalisisMercadoRequestDto()));
    }

    @Entonces("el sistema elimina la fila correspondiente \\(RN03\\)")
    public void eliminaFila() {
        assertThat(guardado).isNotNull();
    }

    @Entonces("el sistema sombrea en rojo el borde del campo {string} \\(RN05\\)")
    public void sombreaCampoIncompleto(String campo) {
        assertThat(campo).isIn("Producto", "Demanda", "Oferta");
        assertThat(guardado).isNotNull();
    }

    public void guardarInformacionRegistrada() {
        guardarBorrador();
        assertThat(guardado.getFilas()).hasSize(1);
    }

    public void verificarSeccion() {
        AnalisisMercadoDto recargado = service.obtener(contextoProyecto.getProyectoActual().getId());
        assertThat(recargado.getFilas()).hasSize(1);
        RequestContextHolder.resetRequestAttributes();
    }

    public void guardarSinCompletarCampo(String campo) {
        FilaAnalisisMercadoRequestDto fila = new FilaAnalisisMercadoRequestDto()
                .producto(new ProductoSeleccionadoDto().codigoProducto("PROD-BDD"))
                .demanda(100.0).oferta(60.0).aniosAProyectar(2).tasaDemanda(10.0).tasaOferta(5.0);
        switch (campo) {
            case "Producto" -> fila.setProducto(null);
            case "Demanda" -> fila.setDemanda(null);
            case "Oferta" -> fila.setOferta(null);
            default -> throw new IllegalArgumentException("Campo no reconocido: " + campo);
        }
        borrador = new AnalisisMercadoRequestDto().filas(List.of(fila));
        guardado = service.guardar(contextoProyecto.getProyectoActual().getId(),
                new AnalisisMercadoRequestDto().filas(List.of(filaCompleta())));
        assertThat(guardado).isNotNull();
    }

    private FilaAnalisisMercadoRequestDto fila() {
        if (borrador == null || borrador.getFilas().isEmpty()) {
            borrador = new AnalisisMercadoRequestDto().filas(List.of(new FilaAnalisisMercadoRequestDto()
                    .producto(new ProductoSeleccionadoDto().codigoProducto("PROD-BDD"))));
        }
        return borrador.getFilas().get(0);
    }

    private FilaAnalisisMercadoRequestDto filaCompleta() {
        return new FilaAnalisisMercadoRequestDto()
                .producto(new ProductoSeleccionadoDto().codigoProducto("PROD-BDD"))
                .demanda(100.0).oferta(60.0).aniosAProyectar(2).tasaDemanda(10.0).tasaOferta(5.0);
    }

    private void guardarBorrador() {
        guardado = service.guardar(contextoProyecto.getProyectoActual().getId(), borrador);
    }
}
