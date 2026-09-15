package sv.gob.mh.siip.config.devseed;

import java.util.LinkedHashMap;
import java.util.List;
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
 * del Anexo F de CU-PRE-3.5): los valores de aquí son datos de prueba razonables, no el catálogo
 * oficial de la DGICP.
 */
@Component
@Profile("dev")
@Order(25)
public class CatalogoPresupuestoDevSeeder implements DevSeeder {

    /** Insumo -> Factor de Corrección. Datos de prueba, no el catálogo oficial (ver javadoc de clase). */
    private static final Map<String, Double> INSUMOS_TIPO = new LinkedHashMap<>();
    static {
        INSUMOS_TIPO.put("Mano de obra calificada", 0.86);
        INSUMOS_TIPO.put("Mano de obra no calificada", 0.62);
        INSUMOS_TIPO.put("Materiales de construcción (origen nacional)", 0.92);
        INSUMOS_TIPO.put("Materiales de construcción (origen importado)", 0.83);
        INSUMOS_TIPO.put("Maquinaria y equipo (origen nacional)", 0.90);
        INSUMOS_TIPO.put("Maquinaria y equipo (origen importado)", 0.80);
        INSUMOS_TIPO.put("Combustibles y lubricantes", 0.75);
        INSUMOS_TIPO.put("Consultoría y servicios profesionales", 1.00);
    }

    /** Unidad de medida: tipo, categoría, unidad, descripción. Datos de prueba (ver javadoc de clase). */
    private record FilaUnidadMedida(TipoUnidadMedida tipo, String categoria, String unidadMedida, String descripcion) {
    }

    private static final List<FilaUnidadMedida> UNIDADES_MEDIDA = List.of(
            new FilaUnidadMedida(TipoUnidadMedida.MIXTA, "Longitud", "Metro lineal", null),
            new FilaUnidadMedida(TipoUnidadMedida.BIEN, "Superficie", "Metro cuadrado", null),
            new FilaUnidadMedida(TipoUnidadMedida.BIEN, "Volumen", "Metro cúbico", null),
            new FilaUnidadMedida(TipoUnidadMedida.BIEN, "Peso", "Kilogramo", null),
            new FilaUnidadMedida(TipoUnidadMedida.BIEN, "Volumen", "Litro", null),
            new FilaUnidadMedida(TipoUnidadMedida.MIXTA, "Cantidad", "Unidad", null),
            new FilaUnidadMedida(TipoUnidadMedida.SERVICIO, "Tiempo", "Hora", null),
            new FilaUnidadMedida(TipoUnidadMedida.SERVICIO, "Tiempo", "Mes", null),
            new FilaUnidadMedida(TipoUnidadMedida.SERVICIO, "Alcance", "Global",
                    "Todo el alcance del contrato/servicio, sin desagregar por unidad física."),
            new FilaUnidadMedida(TipoUnidadMedida.MIXTA, "Cantidad", "Lote", null));

    private final InsumoTipoRepository insumoTipoRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;

    public CatalogoPresupuestoDevSeeder(InsumoTipoRepository insumoTipoRepository,
            UnidadMedidaRepository unidadMedidaRepository) {
        this.insumoTipoRepository = insumoTipoRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
    }

    @Override
    public void seed() {
        INSUMOS_TIPO.forEach((nombre, factorCorreccion) -> {
            if (insumoTipoRepository.findByCodigo(nombre).isEmpty()) {
                insumoTipoRepository.save(InsumoTipo.builder()
                        .codigo(nombre)
                        .nombre(nombre)
                        .factorCorreccion(factorCorreccion)
                        .build());
            }
        });

        for (FilaUnidadMedida fila : UNIDADES_MEDIDA) {
            if (unidadMedidaRepository.findByCategoriaAndNombre(fila.categoria(), fila.unidadMedida())
                    .isEmpty()) {
                unidadMedidaRepository.save(UnidadMedida.builder()
                        .tipo(fila.tipo())
                        .categoria(fila.categoria())
                        .nombre(fila.unidadMedida())
                        .descripcion(fila.descripcion())
                        .build());
            }
        }
    }
}
