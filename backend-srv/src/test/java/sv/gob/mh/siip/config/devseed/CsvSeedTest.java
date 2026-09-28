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
        assertThat(CsvSeed.leer(CatalogoPresupuestoDevSeeder.CSV_UNIDADES_MEDIDA)).hasSize(10)
                .allSatisfy(f -> assertThat(f.get("tipo")).isNotNull());
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
        assertThat(CsvSeed.leer(CatalogoConsolidadoDevSeeder.CSV_CRITERIOS_ELEGIBILIDAD)).hasSize(6)
                .allSatisfy(f -> assertThat(TipoEspecificar.valueOf(f.get("tipo_especificar"))).isNotNull());
        assertThat(CsvSeed.leer(CatalogoConsolidadoDevSeeder.CSV_ENTRADAS_ESPECIFICAR)).hasSize(8)
                .allSatisfy(f -> assertThat(TipoCatalogoEspecificar.valueOf(f.get("tipo"))).isNotNull());
    }
}
