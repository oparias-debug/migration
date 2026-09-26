package sv.gob.mh.siip.config.devseed;

import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import sv.gob.mh.siip.model.preinversion.domain.InsumoTipo;
import sv.gob.mh.siip.model.preinversion.domain.UnidadMedida;
import sv.gob.mh.siip.model.preinversion.enums.TipoUnidadMedida;
import sv.gob.mh.siip.model.preinversion.repository.InsumoTipoRepository;
import sv.gob.mh.siip.model.preinversion.repository.UnidadMedidaRepository;

/**
 * Catálogos "Insumo Tipo" (con Factor de Corrección) y "Unidad de Medida" (CU-ADM-02, Anexos
 * D.1/C.1 de CU-PRE-09), consumidos por CU-PRE-17/18 (Presupuesto). A diferencia de
 * {@link CatalogoProyectoDevSeeder}, el documento fuente de CU-PRE-17
 * (UC-PRE-17-Presupuesto_de_Inversion.md) referencia ambos catálogos por nombre pero nunca
 * transcribe su contenido real (ninguna fila detectada en "Catálogos Detectados", a diferencia
 * del Anexo F de CU-PRE-03.5): los valores son datos de prueba razonables, no el catálogo
 * oficial de la DGICP.
 *
 * <p>Los valores viven en {@code data/seed/insumos-tipo.csv} y {@code data/seed/unidades-medida.csv}
 * (ver {@link CsvSeed}); esta clase solo los carga.
 */
@Component
@Profile("dev")
@Order(25)
public class CatalogoPresupuestoDevSeeder implements DevSeeder {

    public static final String CSV_INSUMOS_TIPO = "insumos-tipo.csv";
    public static final String CSV_UNIDADES_MEDIDA = "unidades-medida.csv";

    private final InsumoTipoRepository insumoTipoRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;

    public CatalogoPresupuestoDevSeeder(InsumoTipoRepository insumoTipoRepository,
            UnidadMedidaRepository unidadMedidaRepository) {
        this.insumoTipoRepository = insumoTipoRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
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
    }
}
