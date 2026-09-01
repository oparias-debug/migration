package sv.gob.mh.siip.config.devseed;

import java.util.LinkedHashMap;
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
 */
@Component
@Profile("dev")
@Order(20)
public class CatalogoProyectoDevSeeder implements DevSeeder {

    private static final String MEDIO_AMBIENTE = "Medio Ambiente";

    /**
     * Catálogo de Sectores y Macrosectores (Anexo C.5 de CU-PRE-01; referenciado parcialmente,
     * como ejemplos no exhaustivos, en CU-PRO-05, CU-PRO-06 y CU-PRO-10). Clave = nombre del
     * macrosector, valor = nombres de sus sectores.
     */
    private static final Map<String, List<String>> SECTORES_POR_MACROSECTOR = new LinkedHashMap<>();
    static {
        SECTORES_POR_MACROSECTOR.put("Desarrollo Social", List.of(
                "Previsión social", "Deporte y recreación", "Vivienda", MEDIO_AMBIENTE,
                "Asistencia social", "Agua potable y alcantarillado", "Multisectorial", "Salud",
                "Desarrollo urbano y comunal", "Educación y cultura"));
        SECTORES_POR_MACROSECTOR.put("Desarrollo Económico", List.of(
                "Energía", "Industria/comercio y turismo", "Silvoagropecuario", "Comunicación",
                "Transporte y almacenaje"));
        SECTORES_POR_MACROSECTOR.put("Seguridad Pública y Justicia", List.of("Seguridad", "Justicia"));
    }

    /**
     * Catálogo de Ejes del Plan de Gobierno / Plan Cuscatlán (Anexo C.3 de CU-PRE-01;
     * reutilizado en el Anexo C.2 de CU-PRE-25). Clave = código, valor = nombre.
     */
    private static final Map<String, String> EJES_PLAN_GOBIERNO = new LinkedHashMap<>();
    static {
        EJES_PLAN_GOBIERNO.put("EJE_1", "Eje 1: Carreteras");
        EJES_PLAN_GOBIERNO.put("EJE_2", "Eje 2: Transporte");
        EJES_PLAN_GOBIERNO.put("EJE_3", "Eje 3: Infraestructura de salud y educación");
        EJES_PLAN_GOBIERNO.put("EJE_4", "Eje 4: Puertos, aeropuertos y aduanas");
        EJES_PLAN_GOBIERNO.put("EJE_5", "Eje 5: Agua potable y saneamiento");
        EJES_PLAN_GOBIERNO.put("EJE_6", "Eje 6: Vivienda y desarrollo urbano");
        EJES_PLAN_GOBIERNO.put("EJE_7", "Eje 7: Infraestructura penitenciaria");
        EJES_PLAN_GOBIERNO.put("EJE_8", "Eje 8: Asocios públicos-privados");
        EJES_PLAN_GOBIERNO.put("EJE_9", "Eje 9: Energía");
    }

    /**
     * Catálogo de Planes Sectoriales/Regionales (Anexo C.4 de CU-PRE-01). No hay código corto
     * oficial: el nombre es el código. Clave = código/nombre, valor = sector asociado. Todos
     * los planes de este anexo están sujetos a actualización periódica.
     */
    private static final Map<String, String> PLANES_SECTORIALES = new LinkedHashMap<>();
    static {
        PLANES_SECTORIALES.put("Plan Control Territorial", "Seguridad");
        PLANES_SECTORIALES.put("Plan Nacional de Turismo - 2030", "Turismo");
        PLANES_SECTORIALES.put("Plan Sectorial de Educación 2022-2030", "Educación");
        PLANES_SECTORIALES.put(
                "Planes Sectoriales para la implementación de las Contribuciones Nacionalmente "
                        + "Determinadas de El Salvador",
                MEDIO_AMBIENTE);
        PLANES_SECTORIALES.put("Plan Nacional de Cambio Climático 2022-2026", MEDIO_AMBIENTE);
        PLANES_SECTORIALES.put("Plan Nacional para la Gestión Integral de Residuos", MEDIO_AMBIENTE);
        PLANES_SECTORIALES.put("Política Crecer Juntos 2020-2030", "Salud/Educación");
    }

    /** Catálogo de medidas de Gestión de Riesgo de Desastres, GRD (Anexo C.1 de CU-PRE-01;
        reutilizado en CU-PRE-25 Anexo C.4). Clave = código, valor = descripción. */
    private static final Map<String, String> MEDIDAS_GRD = new LinkedHashMap<>();
    static {
        MEDIDAS_GRD.put("1", "Reducción del riesgo existente");
        MEDIDAS_GRD.put("2", "Prospectivos para evitar nuevos riesgos y generación de conocimiento");
        MEDIDAS_GRD.put("3", "Preparación");
        MEDIDAS_GRD.put("4", "Respuesta y Recuperación");
    }

    /** Catálogo de medidas de Gestión de Riesgo Climático, GRC (Anexo C.1.5 de CU-PRE-01). */
    private static final Map<String, String> MEDIDAS_GRC = new LinkedHashMap<>();
    static {
        MEDIDAS_GRC.put("1", "Prevención");
        MEDIDAS_GRC.put("2", "Preparación");
        MEDIDAS_GRC.put("3", "Gestión de desastres");
    }

    /** Catálogo de medidas de Adaptación al Cambio Climático, ACC (Anexo C.2 de CU-PRE-01;
        reutilizado en CU-PRE-25 Anexo C.5). */
    private static final Map<String, String> MEDIDAS_ACC = new LinkedHashMap<>();
    static {
        MEDIDAS_ACC.put("1", "Mitigación");
        MEDIDAS_ACC.put("2", "Adaptación");
    }

    /** Catálogo de Ejes Temáticos (Anexo C.6 de CU-PRE-01). Igual que macrosectores/sectores, no
     * hay código corto oficial: el nombre es el código. */
    private static final List<String> EJES_TEMATICOS = List.of(
            "Infraestructura Educativa (Construcción y Mejoramiento)",
            "Equipamiento, Tecnología y Fortalecimiento Pedagógico en Centros Escolares",
            "Educación Superior e Investigación",
            "Construcción y Mejoramiento de Infraestructura de Salud",
            "Construcción y Mejoramiento de Infraestructura Vial",
            "Transporte Público y Movilidad Urbana",
            "Infraestructura Turística",
            "Espacios Públicos y Desarrollo Urbano",
            "Gestión Ambiental y Restauración",
            "Infraestructura para Gestión de Riesgo y Adaptación Climática",
            "Infraestructura Agrícola y Seguridad Alimentaria",
            "Generación, Transmisión o Distribución de Energía",
            "Infraestructura Aeroportuaria o Portuaria",
            "Conectividad y Comunicaciones",
            "Infraestructura y Servicios para Grupos Vulnerables",
            "Equipamiento y Formación de Capital Humano",
            "Seguridad Ciudadana y Convivencia Comunitaria",
            "Fortalecimiento y Equipamiento Institucional",
            "Vivienda y Mejoramiento Habitacional",
            "Sistemas de Agua y Saneamiento Básico",
            "Infraestructura Cultural y Patrimonio",
            "Infraestructura Deportiva y Recreativa");

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

        sembrarMedidas(TipoMedidaCatalogo.GRD, MEDIDAS_GRD);
        sembrarMedidas(TipoMedidaCatalogo.GRC, MEDIDAS_GRC);
        sembrarMedidas(TipoMedidaCatalogo.ACC, MEDIDAS_ACC);
    }

    private void sembrarSectoresYMacrosectores() {
        SECTORES_POR_MACROSECTOR.forEach((nombreMacrosector, sectores) -> {
            MacroSector macrosector = macroSectorRepository.findByCodigo(nombreMacrosector)
                    .orElseGet(() -> macroSectorRepository.save(
                            MacroSector.builder().codigo(nombreMacrosector).nombre(nombreMacrosector).build()));

            for (String nombreSector : sectores) {
                String codigoSector = nombreMacrosector + "::" + nombreSector;
                if (sectorActividadRepository.findByCodigo(codigoSector).isEmpty()) {
                    sectorActividadRepository.save(SectorActividad.builder().macrosector(macrosector)
                            .codigo(codigoSector).nombre(nombreSector).build());
                }
            }
        });
    }

    private void sembrarEjesTematicos() {
        for (String nombre : EJES_TEMATICOS) {
            if (ejeTematicoRepository.findByCodigo(nombre).isEmpty()) {
                ejeTematicoRepository.save(EjeTematico.builder().codigo(nombre).nombre(nombre).activo(true).build());
            }
        }
    }

    private void sembrarEjesPlanGobierno() {
        EJES_PLAN_GOBIERNO.forEach((codigo, nombre) -> {
            if (ejePlanGobiernoRepository.findByCodigo(codigo).isEmpty()) {
                ejePlanGobiernoRepository.save(EjePlanGobierno.builder().codigo(codigo).nombre(nombre)
                        .sujetoActualizacion(true).activo(true).build());
            }
        });
    }

    private void sembrarPlanesSectoriales() {
        PLANES_SECTORIALES.forEach((nombre, sectorAsociado) -> {
            if (planSectorialRegionalRepository.findByCodigo(nombre).isEmpty()) {
                planSectorialRegionalRepository.save(PlanSectorialRegional.builder().codigo(nombre).nombre(nombre)
                        .sectorAsociado(sectorAsociado).sujetoActualizacion(true).activo(true).build());
            }
        });
    }

    private void sembrarMedidas(TipoMedidaCatalogo tipo, Map<String, String> medidas) {
        List<MedidaCatalogo> existentes = medidaCatalogoRepository.findByTipoOrderByCodigo(tipo);
        medidas.forEach((codigo, descripcion) -> {
            boolean existe = existentes.stream().anyMatch(m -> codigo.equals(m.getCodigo()));
            if (!existe) {
                medidaCatalogoRepository
                        .save(MedidaCatalogo.builder().tipo(tipo).codigo(codigo).descripcion(descripcion).build());
            }
        });
    }
}
