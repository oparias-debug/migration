package sv.gob.mh.siip.config.devseed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import sv.gob.mh.siip.model.preinversion.enums.TipoCatalogoEspecificar;
import sv.gob.mh.siip.model.preinversion.enums.TipoEspecificar;
import sv.gob.mh.siip.model.preinversion.enums.TipoMedidaCatalogo;
import sv.gob.mh.siip.model.preinversion.enums.TipoUnidadMedida;
import sv.gob.mh.siip.model.preinversion.enums.ValorCalificacion;

class CsvSeedTest {

    @Test
    void leer_ignoraComentariosYLineasVacias_yDevuelveNullEnCeldasVacias() {
        List<Map<String, String>> filas = CsvSeed.leer("prueba-valido.csv");

        assertThat(filas).hasSize(2);
        assertThat(filas.get(0)).containsEntry("codigo", "A").containsEntry("descripcion", "Con espacios");
        assertThat(filas.get(1)).containsEntry("codigo", "B").containsEntry("descripcion", null);
    }

    @Test
    void leer_fallaConNumeroDeLinea_cuandoUnaFilaTieneOtraCantidadDeColumnas() {
        assertThatThrownBy(() -> CsvSeed.leer("prueba-columnas.csv"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("prueba-columnas.csv línea 2");
    }

    @Test
    void leer_fallaConElNombreDelArchivo_cuandoNoExiste() {
        assertThatThrownBy(() -> CsvSeed.leer("no-existe.csv"))
                .isInstanceOf(UncheckedIOException.class)
                .hasMessageContaining("data/seed/no-existe.csv");
    }

    @Test
    void losCsvDelSeederDePresupuestoSeLeenCompletos() {
        assertThat(CsvSeed.leer(CatalogoPresupuestoDevSeeder.CSV_INSUMOS_TIPO)).hasSize(8)
                .allSatisfy(f -> assertThat(f.get("factor_correccion")).isNotNull());
        // Anexo D.1 de CU-PRE-11: 29 bienes, 5 servicios y 5 mixtas.
        assertThat(CsvSeed.leer(CatalogoPresupuestoDevSeeder.CSV_UNIDADES_MEDIDA)).hasSize(39)
                .allSatisfy(f -> {
                    assertThat(TipoUnidadMedida.valueOf(f.get("tipo"))).isNotNull();
                    assertThat(f.get("descripcion")).isNotNull();
                });
        // Anexo C.2 de CU-PRE-3.5, que incluye los 7 componentes del Anexo C.1 de CU-PRE-11.
        List<Map<String, String>> tiposCosto = CsvSeed.leer(CatalogoPresupuestoDevSeeder.CSV_TIPOS_COSTO);
        assertThat(tiposCosto).hasSize(12)
                .allSatisfy(f -> assertThat(f.get("codigo")).startsWith("TC-").hasSizeLessThanOrEqualTo(20));
        assertThat(tiposCosto).extracting(f -> f.get("nombre")).contains("Infraestructura", "Equipamiento",
                "Capacitaciones", "Administración", "Consultorías", "Terrenos", "Otros", "Ambiental");
        assertThat(tiposCosto).extracting(f -> f.get("codigo")).doesNotHaveDuplicates().contains("TC-EQUIPAMIENTO");
    }

    @Test
    void losCsvDelSeederDeEtapasSeLeenCompletos() {
        // Anexo C.5 de CU-PRE-3.5: 262 distritos, sin las filas generadas "Nivel departamental"/"Nivel nacional".
        List<Map<String, String>> ubicaciones = CsvSeed.leer(CatalogoEtapasDevSeeder.CSV_UBICACIONES_GEOGRAFICAS);
        assertThat(ubicaciones).hasSize(262);
        assertThat(ubicaciones).extracting(f -> f.get("codigo_distrito")).doesNotHaveDuplicates()
                .allSatisfy(c -> assertThat(c).hasSizeLessThanOrEqualTo(10));
        assertThat(ubicaciones.stream().map(f -> f.get("departamento")).distinct()).hasSize(14);
        // Anexo C.6: 3 filas oficiales y 6 de prueba (P-xx), un indicador por fila.
        List<Map<String, String>> productos = CsvSeed.leer(CatalogoEtapasDevSeeder.CSV_PRODUCTOS_INDICADORES);
        assertThat(productos).hasSize(9);
        assertThat(productos).extracting(f -> f.get("codigo_indicador")).doesNotHaveDuplicates();
        assertThat(productos).allSatisfy(f -> assertThat(f.get("es_indicador_principal")).isIn("true", "false"));
    }

    @Test
    void losCsvDelSeederDeProyectoSeLeenCompletos() {
        assertThat(CsvSeed.leer(CatalogoProyectoDevSeeder.CSV_SECTORES)).hasSize(17);
        assertThat(CsvSeed.leer(CatalogoProyectoDevSeeder.CSV_EJES_TEMATICOS)).hasSize(22);
        assertThat(CsvSeed.leer(CatalogoProyectoDevSeeder.CSV_EJES_PLAN_GOBIERNO)).hasSize(9);
        assertThat(CsvSeed.leer(CatalogoProyectoDevSeeder.CSV_PLANES_SECTORIALES)).hasSize(7);
        assertThat(CsvSeed.leer(CatalogoProyectoDevSeeder.CSV_MEDIDAS)).hasSize(9)
                .allSatisfy(f -> assertThat(TipoMedidaCatalogo.valueOf(f.get("tipo"))).isNotNull());
    }

  @Test
  void losCsvDelSeederConsolidadoSeLeenCompletosYConValoresValidos() {
    assertThat(CsvSeed.leer(CatalogoConsolidadoDevSeeder.CSV_PARAMETROS)).hasSize(4);
    assertThat(CsvSeed.leer(CatalogoConsolidadoDevSeeder.CSV_INDICADORES_RESULTADO)).hasSize(4);
    assertThat(CsvSeed.leer(CatalogoConsolidadoDevSeeder.CSV_RANGOS_INTERPRETACION)).hasSize(4);
    assertThat(CsvSeed.leer(CatalogoConsolidadoDevSeeder.CSV_CRITERIOS_PRIORIZACION)).hasSize(4);
    assertThat(CsvSeed.leer(CatalogoConsolidadoDevSeeder.CSV_SUBCRITERIOS_PRIORIZACION)).hasSize(7);
    assertThat(CsvSeed.leer(CatalogoConsolidadoDevSeeder.CSV_ESCALA_CALIFICACION)).hasSize(7)
        .allSatisfy(f -> assertThat(ValorCalificacion.valueOf(f.get("valor"))).isNotNull());
    assertThat(CsvSeed.leer(CatalogoConsolidadoDevSeeder.CSV_CRITERIOS_ELEGIBILIDAD)).hasSize(4)
        .allSatisfy(f -> assertThat(TipoEspecificar.valueOf(f.get("tipo_especificar"))).isNotNull());
    assertThat(CsvSeed.leer(CatalogoConsolidadoDevSeeder.CSV_ENTRADAS_ESPECIFICAR)).hasSize(8)
        .allSatisfy(f -> assertThat(TipoCatalogoEspecificar.valueOf(f.get("tipo"))).isNotNull());
  }
}
