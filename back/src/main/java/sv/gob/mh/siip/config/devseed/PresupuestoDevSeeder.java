package sv.gob.mh.siip.config.devseed;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.repository.InstitucionRepository;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.preinversion.domain.Componente;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.domain.FichaEmergencia;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.FuenteFinanciamiento;
import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.repository.ComponenteRepository;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.FichaEmergenciaRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;

/**
 * Proyecto de emergencia con Ficha de emergencia (CU-PRE-03.5, Anexo A.4) ya registrada, y con dos
 * filas de Descripción Técnica (CU-PRE-11, {@link Componente}) para tener "productos" con los que
 * probar CU-PRE-17 (Presupuesto de Inversión) sin registrar ambas pantallas a mano.
 * {@code fuentesFinanciamiento}/{@code fuenteRecursos} de {@code FichaEmergencia} son datos propios
 * de CU-PRE-03.5: CU-PRE-17 tiene sus propios campos homónimos en {@code PresupuestoProyecto}, que
 * cualquier proyecto puede completar sin necesitar una Ficha de emergencia.
 */
@Component
@Profile("dev")
@Order(40)
public class PresupuestoDevSeeder implements DevSeeder {

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");
    private static final String NOMBRE_PROYECTO = "Proyecto de prueba (Presupuesto CU-PRE-17)";

    private final ProyectoRepository proyectoRepository;
    private final FichaEmergenciaRepository fichaEmergenciaRepository;
    private final ComponenteRepository componenteRepository;
    private final InstitucionRepository institucionRepository;
    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;

    public PresupuestoDevSeeder(ProyectoRepository proyectoRepository,
            FichaEmergenciaRepository fichaEmergenciaRepository, ComponenteRepository componenteRepository,
            InstitucionRepository institucionRepository, UnidadEjecutoraRepository unidadEjecutoraRepository,
            SectorActividadRepository sectorActividadRepository, EjeTematicoRepository ejeTematicoRepository) {
        this.proyectoRepository = proyectoRepository;
        this.fichaEmergenciaRepository = fichaEmergenciaRepository;
        this.componenteRepository = componenteRepository;
        this.institucionRepository = institucionRepository;
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
    }

    @Override
    public void seed() {
        if (!proyectoRepository.findByNombreContainingIgnoreCase(NOMBRE_PROYECTO).isEmpty()) {
            return;
        }

        Institucion institucion = institucionRepository.findByCodigo("MH-DGICP").orElseThrow(
                () -> new IllegalStateException("Falta el seed de Institución MH-DGICP (UsuarioDevSeeder)."));
        UnidadEjecutora unidadEjecutora = unidadEjecutoraRepository.findByCodigo("URP-01").orElseThrow(
                () -> new IllegalStateException("Falta el seed de Unidad Ejecutora URP-01 (UsuarioDevSeeder)."));
        SectorActividad sector = sectorActividadRepository.findByCodigo("Desarrollo Social::Educación y cultura")
                .orElseThrow(() -> new IllegalStateException(
                        "Falta el seed del sector 'Educación y cultura' (CatalogoProyectoDevSeeder)."));
        EjeTematico ejeTematico = ejeTematicoRepository
                .findByCodigo("Infraestructura Educativa (Construcción y Mejoramiento)")
                .orElseThrow(() -> new IllegalStateException(
                        "Falta el seed del eje temático 'Infraestructura Educativa' (CatalogoProyectoDevSeeder)."));

        Proyecto proyecto = Proyecto.builder()
                .nombre(NOMBRE_PROYECTO)
                .iniciativaInversion(IniciativaInversion.PROYECTO)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .estado(EstadoProyecto.CUP_ASIGNADO)
                .cup(siguienteCup())
                .fechaIngreso(LocalDateTime.now(ZONA_EL_SALVADOR))
                .fechaCupAsignado(LocalDateTime.now(ZONA_EL_SALVADOR))
                .activo(true)
                .montoEstimadoInversion(250000.0)
                .sector(sector)
                .ejeTematico(ejeTematico)
                .esProyectoEmergencia(true)
                .descripcionProyecto("Proyecto de prueba sembrado para pruebas locales de CU-PRE-17 (Presupuesto).")
                .build();
        proyecto = proyectoRepository.save(proyecto);

        fichaEmergenciaRepository.save(FichaEmergencia.builder()
                .proyecto(proyecto)
                .planteamientoProblema("Planteamiento del problema de prueba (BDD/dev).")
                .objetivoGeneral("Objetivo general de prueba (BDD/dev).")
                .descripcionProyecto("Descripción de prueba para el módulo de Presupuesto (CU-PRE-17).")
                .productos(List.of("P-01", "P-02"))
                .distrito("San Salvador Centro")
                .poblacionObjetivo("Población de prueba (BDD/dev).")
                .inversionEstimada(250000.0)
                .fuentesFinanciamiento(List.of(FuenteFinanciamiento.FONDO_GENERAL))
                .fuenteRecursos("Fondo General de la Nación (prueba)")
                .build());

        // Filas de Descripción Técnica (CU-PRE-11): son las que alimentan "productos" en
        // GET /proyectos/{id}/presupuesto (RN16, solo lectura ahí) — mismos códigos que
        // FichaEmergencia.productos, arriba, solo por consistencia entre ambos seeds de prueba.
        componenteRepository.save(Componente.builder()
                .proyecto(proyecto)
                .nombre("TC-EQUIPAMIENTO")
                .descripcion("Equipamiento de prueba (BDD/dev).")
                .codigoProducto("P-01")
                .cantidad(1.0)
                .unidadMedida("Unidad")
                .build());
        componenteRepository.save(Componente.builder()
                .proyecto(proyecto)
                .nombre("TC-EQUIPAMIENTO")
                .descripcion("Equipamiento de prueba (BDD/dev).")
                .codigoProducto("P-02")
                .cantidad(1.0)
                .unidadMedida("Unidad")
                .build());
    }

    /** CU-PRE-01.5, RN 2.8.c: siguiente CUP consecutivo de 5 dígitos, partiendo de 10000 — misma
     *  regla que {@code ProyectoServiceImpl.siguienteCup()} / {@code ProyectoDevSeeder}. */
    private String siguienteCup() {
        int siguiente = proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc()
                .map(p -> Integer.parseInt(p.getCup()) + 1)
                .orElse(10000);
        return String.format("%05d", siguiente);
    }
}
