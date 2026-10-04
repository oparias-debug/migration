package sv.gob.mh.siip.config.devseed;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import sv.gob.mh.siip.model.common.domain.Departamento;
import sv.gob.mh.siip.model.common.domain.Municipio;
import sv.gob.mh.siip.model.common.repository.DepartamentoRepository;
import sv.gob.mh.siip.model.common.repository.MunicipioRepository;
import sv.gob.mh.siip.model.preinversion.domain.ProductoIndicadorCatalogo;
import sv.gob.mh.siip.model.preinversion.repository.ProductoIndicadorCatalogoRepository;

/**
 * Catálogos de CU-PRE-3.5 "Selección y registro de etapas" que usan los CU de formulación:
 * <ul>
 * <li>Ubicaciones geográficas (Anexo C.5, = Anexo C.1 de CU-PRE-07/08): departamentos con su región y
 * sus distritos. Catálogo oficial; los códigos no lo son (ver el CSV).</li>
 * <li>Productos e Indicadores (Anexo C.6): las filas oficiales que transcribe el anexo más productos de
 * prueba marcados como tales (códigos {@code P-xx}), que usan {@link PresupuestoDevSeeder} y los
 * ejemplos de CU-PRE-09 y CU-PRE-11.</li>
 * </ul>
 *
 * <p>Los valores viven en {@code data/seed/} (ver {@link CsvSeed}); esta clase solo los carga, sin
 * duplicar los que ya existen. Se ejecuta antes que los seeders de proyectos y presupuesto, que
 * referencian distritos y productos.
 */
@Component
@Profile("dev")
@Order(21)
public class CatalogoEtapasDevSeeder implements DevSeeder {

    public static final String CSV_UBICACIONES_GEOGRAFICAS = "ubicaciones-geograficas.csv";
    public static final String CSV_PRODUCTOS_INDICADORES = "productos-indicadores.csv";

    private final DepartamentoRepository departamentoRepository;
    private final MunicipioRepository municipioRepository;
    private final ProductoIndicadorCatalogoRepository productoRepository;

    public CatalogoEtapasDevSeeder(DepartamentoRepository departamentoRepository,
            MunicipioRepository municipioRepository,
            ProductoIndicadorCatalogoRepository productoRepository) {
        this.departamentoRepository = departamentoRepository;
        this.municipioRepository = municipioRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public void seed() {
        sembrarUbicaciones();
        sembrarProductos();
    }

    private void sembrarUbicaciones() {
        Map<String, Departamento> departamentos = new HashMap<>();
        for (Map<String, String> fila : CsvSeed.leer(CSV_UBICACIONES_GEOGRAFICAS)) {
            var departamento = departamentos.computeIfAbsent(fila.get("codigo_departamento"),
                    (String codigo) -> departamentoRepository.findByCodigo(codigo)
                            .orElseGet(() -> departamentoRepository.save(Departamento.builder()
                                    .codigo(codigo)
                                    .nombre(fila.get("departamento"))
                                    .region(fila.get("region"))
                                    .build())));
            String codigoDistrito = fila.get("codigo_distrito");
            if (municipioRepository.findByCodigoIgnoreCase(codigoDistrito).isEmpty()) {
                municipioRepository.save(Municipio.builder()
                        .departamento(departamento)
                        .codigo(codigoDistrito)
                        .nombre(fila.get("distrito"))
                        .build());
            }
        }
    }

    private void sembrarProductos() {
        for (Map<String, String> fila : CsvSeed.leer(CSV_PRODUCTOS_INDICADORES)) {
            String codigoIndicador = fila.get("codigo_indicador");
            if (!productoRepository.existsByCodigoIndicador(codigoIndicador)) {
                productoRepository.save(ProductoIndicadorCatalogo.builder()
                        .codigoProducto(fila.get("codigo_producto"))
                        .producto(fila.get("producto"))
                        .descripcionProducto(fila.get("descripcion"))
                        .codigoIndicador(codigoIndicador)
                        .indicador(fila.get("indicador"))
                        .unidadMedida(fila.get("unidad_medida"))
                        .esIndicadorPrincipal(Boolean.valueOf(fila.get("es_indicador_principal")))
                        .build());
            }
        }
    }
}
