package sv.gob.mh.siip.config.devseed;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import sv.gob.mh.siip.model.preinversion.domain.EjePlanGobierno;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.MedidaCatalogo;
import sv.gob.mh.siip.model.preinversion.domain.PlanSectorialRegional;
import sv.gob.mh.siip.model.preinversion.enums.TipoMedidaCatalogo;
import sv.gob.mh.siip.model.preinversion.repository.EjePlanGobiernoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.MedidaCatalogoRepository;
import sv.gob.mh.siip.model.preinversion.repository.PlanSectorialRegionalRepository;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.MacroSectorRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * Catálogos seleccionables de la pantalla "Nuevo registro" (Anexos C.1/C.1.5/C.2 medidas de
 * GRD/GRC/ACC, C.3 eje del Plan de Gobierno, C.4 plan sectorial/regional, C.5 macrosectores y
 * sectores, C.6 eje temático). Todos estos anexos ya tienen su catálogo oficial completo
 * sembrado aquí, sin datos de prueba inventados.
 *
 * <p>Los valores viven en los CSV de {@code data/seed/} (ver las constantes {@code CSV_*} y
 * {@link CsvSeed}); esta clase solo los carga.
 */
@Component
@Profile("dev")
@Order(20)
public class CatalogoProyectoDevSeeder implements DevSeeder {

    public static final String CSV_SECTORES = "sectores.csv";
    public static final String CSV_EJES_TEMATICOS = "ejes-tematicos.csv";
    public static final String CSV_EJES_PLAN_GOBIERNO = "ejes-plan-gobierno.csv";
    public static final String CSV_PLANES_SECTORIALES = "planes-sectoriales.csv";
    public static final String CSV_MEDIDAS = "medidas.csv";

    private static final String COLUMNA_NOMBRE = "nombre";
    private static final String COLUMNA_CODIGO = "codigo";

    private final MacroSectorRepository macroSectorRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final EjePlanGobiernoRepository ejePlanGobiernoRepository;
    private final PlanSectorialRegionalRepository planSectorialRegionalRepository;
    private final MedidaCatalogoRepository medidaCatalogoRepository;

    public CatalogoProyectoDevSeeder(MacroSectorRepository macroSectorRepository,
            SectorActividadRepository sectorActividadRepository, EjeTematicoRepository ejeTematicoRepository,
            EjePlanGobiernoRepository ejePlanGobiernoRepository,
            PlanSectorialRegionalRepository planSectorialRegionalRepository,
            MedidaCatalogoRepository medidaCatalogoRepository) {
        this.macroSectorRepository = macroSectorRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.ejePlanGobiernoRepository = ejePlanGobiernoRepository;
        this.planSectorialRegionalRepository = planSectorialRegionalRepository;
        this.medidaCatalogoRepository = medidaCatalogoRepository;
    }

    @Override
    public void seed() {
        sembrarSectoresYMacrosectores();
        sembrarEjesTematicos();
        sembrarEjesPlanGobierno();
        sembrarPlanesSectoriales();
        sembrarMedidas();
    }

    private void sembrarSectoresYMacrosectores() {
        Map<String, MacroSector> macrosectores = new HashMap<>();
        for (Map<String, String> fila : CsvSeed.leer(CSV_SECTORES)) {
            String nombreMacrosector = fila.get("macrosector");
            String nombreSector = fila.get("sector");
            MacroSector macrosector = macrosectores.computeIfAbsent(nombreMacrosector,
                    (String nombre) -> macroSectorRepository.findByCodigo(nombre)
                            .orElseGet(() -> macroSectorRepository.save(
                                    MacroSector.builder().codigo(nombre).nombre(nombre).build())));

            String codigoSector = nombreMacrosector + "::" + nombreSector;
            if (sectorActividadRepository.findByCodigo(codigoSector).isEmpty()) {
                sectorActividadRepository.save(SectorActividad.builder().macrosector(macrosector)
                        .codigo(codigoSector).nombre(nombreSector).build());
            }
        }
    }

    private void sembrarEjesTematicos() {
        for (Map<String, String> fila : CsvSeed.leer(CSV_EJES_TEMATICOS)) {
            String nombre = fila.get(COLUMNA_NOMBRE);
            if (ejeTematicoRepository.findByCodigo(nombre).isEmpty()) {
                ejeTematicoRepository.save(EjeTematico.builder().codigo(nombre).nombre(nombre).activo(true).build());
            }
        }
    }

    private void sembrarEjesPlanGobierno() {
        for (Map<String, String> fila : CsvSeed.leer(CSV_EJES_PLAN_GOBIERNO)) {
            String codigo = fila.get(COLUMNA_CODIGO);
            if (ejePlanGobiernoRepository.findByCodigo(codigo).isEmpty()) {
                ejePlanGobiernoRepository.save(EjePlanGobierno.builder().codigo(codigo).nombre(fila.get(COLUMNA_NOMBRE))
                        .sujetoActualizacion(true).activo(true).build());
            }
        }
    }

    private void sembrarPlanesSectoriales() {
        for (Map<String, String> fila : CsvSeed.leer(CSV_PLANES_SECTORIALES)) {
            String nombre = fila.get(COLUMNA_NOMBRE);
            if (planSectorialRegionalRepository.findByCodigo(nombre).isEmpty()) {
                planSectorialRegionalRepository.save(PlanSectorialRegional.builder().codigo(nombre).nombre(nombre)
                        .sectorAsociado(fila.get("sector_asociado")).sujetoActualizacion(true).activo(true).build());
            }
        }
    }

    private void sembrarMedidas() {
        Map<TipoMedidaCatalogo, List<MedidaCatalogo>> existentesPorTipo = new EnumMap<>(TipoMedidaCatalogo.class);
        for (Map<String, String> fila : CsvSeed.leer(CSV_MEDIDAS)) {
            TipoMedidaCatalogo tipo = TipoMedidaCatalogo.valueOf(fila.get("tipo"));
            String codigo = fila.get(COLUMNA_CODIGO);
            List<MedidaCatalogo> existentes = existentesPorTipo.computeIfAbsent(tipo,
                    medidaCatalogoRepository::findByTipoOrderByCodigo);
            boolean existe = existentes.stream().anyMatch(m -> codigo.equals(m.getCodigo()));
            if (!existe) {
                medidaCatalogoRepository.save(MedidaCatalogo.builder().tipo(tipo).codigo(codigo)
                        .descripcion(fila.get("descripcion")).build());
            }
        }
    }
}
