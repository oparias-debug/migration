package sv.gob.mh.siip.config.devseed;

import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import sv.gob.mh.siip.model.preinversion.domain.InsumoTipo;
import sv.gob.mh.siip.model.preinversion.domain.TipoCosto;
import sv.gob.mh.siip.model.preinversion.domain.UnidadMedida;
import sv.gob.mh.siip.model.preinversion.enums.TipoUnidadMedida;
import sv.gob.mh.siip.model.preinversion.repository.InsumoTipoRepository;
import sv.gob.mh.siip.model.preinversion.repository.TipoCostoRepository;
import sv.gob.mh.siip.model.preinversion.repository.UnidadMedidaRepository;

/**
 * Catálogos de costos y medidas consumidos por CU-PRE-11 (Descripción Técnica) y CU-PRE-17/18
 * (Presupuesto):
 * <ul>
 * <li>"Insumo Tipo" (con Factor de Corrección): el documento de CU-PRE-17 lo referencia por nombre
 * pero no transcribe su contenido, así que son datos de prueba razonables, no el catálogo oficial.</li>
 * <li>"Unidad de Medida" (CU-ADM-02): catálogo oficial, Anexo D.1 de CU-PRE-11 (= Anexo C.1 de
 * CU-PRE-09).</li>
 * <li>"Tipo de Costos" (Anexo C.2 de CU-PRE-3.5): catálogo oficial; es también el del campo
 * "Componente" de CU-PRE-11 (Anexo C.1), según el contrato CU-PRE-11.openapi.yaml.</li>
 * </ul>
 *
 * <p>Los valores viven en {@code data/seed/} (ver {@link CsvSeed}); esta clase solo los carga, sin
 * duplicar los que ya existen.
 */
@Component
@Profile("dev")
@Order(25)
public class CatalogoPresupuestoDevSeeder implements DevSeeder {

    public static final String CSV_INSUMOS_TIPO = "insumos-tipo.csv";
    public static final String CSV_UNIDADES_MEDIDA = "unidades-medida.csv";
    public static final String CSV_TIPOS_COSTO = "tipos-costo.csv";

    private final InsumoTipoRepository insumoTipoRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;
    private final TipoCostoRepository tipoCostoRepository;

    public CatalogoPresupuestoDevSeeder(InsumoTipoRepository insumoTipoRepository,
            UnidadMedidaRepository unidadMedidaRepository,
            TipoCostoRepository tipoCostoRepository) {
        this.insumoTipoRepository = insumoTipoRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
        this.tipoCostoRepository = tipoCostoRepository;
    }

    @Override
    public void seed() {
        for (Map<String, String> fila : CsvSeed.leer(CSV_INSUMOS_TIPO)) {
            String nombre = fila.get("nombre");
            if (insumoTipoRepository.findByCodigo(nombre).isEmpty()) {
                insumoTipoRepository.save(InsumoTipo.builder()
                        .codigo(nombre)
                        .nombre(nombre)
                        .factorCorreccion(Double.valueOf(fila.get("factor_correccion")))
                        .build());
            }
        }

        for (Map<String, String> fila : CsvSeed.leer(CSV_UNIDADES_MEDIDA)) {
            String categoria = fila.get("categoria");
            String unidadMedida = fila.get("unidad_medida");
            if (unidadMedidaRepository.findByCategoriaAndNombre(categoria, unidadMedida).isEmpty()) {
                unidadMedidaRepository.save(UnidadMedida.builder()
                        .tipo(TipoUnidadMedida.valueOf(fila.get("tipo")))
                        .categoria(categoria)
                        .nombre(unidadMedida)
                        .descripcion(fila.get("descripcion"))
                        .build());
            }
        }

        for (Map<String, String> fila : CsvSeed.leer(CSV_TIPOS_COSTO)) {
            String codigo = fila.get("codigo");
            if (tipoCostoRepository.findByCodigo(codigo).isEmpty()) {
                tipoCostoRepository.save(TipoCosto.builder().codigo(codigo).nombre(fila.get("nombre")).build());
            }
        }
    }
}
