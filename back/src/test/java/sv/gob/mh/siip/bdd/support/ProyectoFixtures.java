package sv.gob.mh.siip.bdd.support;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;

/**
 * Builders reutilizados por los steps BDD de persistencia de los dominios que
 * dependen de Proyecto (ejecucion, oym, preinversion) para no repetir la
 * construcción de la cadena Institución → Unidad Ejecutora → Proyecto en
 * cada clase de steps. No son step definitions: es un helper de test plano.
 */
public final class ProyectoFixtures {

    // CUP real es un consecutivo de 5 dígitos desde 10000 (RN 2.8.c, ver
    // ProyectoServiceImpl.siguienteCup()); acá solo se necesita un valor único por
    // proyecto de prueba dentro de la misma ejecución de la suite, no el consecutivo real.
    // Arranca en 50000 (lejos del rango que siguienteCup() genera de verdad) para no
    // colisionar con el CUP real que asigna el escenario de CU-PRE-01.5-emitir-cup.feature
    // si ese insert no queda revertido al terminar su escenario. Un contador atómico evita,
    // además, las colisiones que un hash truncado del sufijo aleatorio sí produce (ver
    // incidente de "Unique index or primary key violation" en PROYECTO.CUP).
    private static final AtomicInteger CUP_SEQ = new AtomicInteger(50000);

    private ProyectoFixtures() {
    }

    public static String nuevoCup() {
        return String.valueOf(CUP_SEQ.getAndIncrement());
    }

    public static Institucion nuevaInstitucion(String codigo, String nombre) {
        return Institucion.builder()
                .codigo(codigo)
                .nombre(nombre)
                .activo(true)
                .build();
    }

    public static UnidadEjecutora nuevaUnidadEjecutora(String codigo, String nombre, Institucion institucion) {
        return UnidadEjecutora.builder()
                .codigo(codigo)
                .nombre(nombre)
                .institucion(institucion)
                .activo(true)
                .build();
    }

    public static MacroSector nuevoMacrosector(String codigo, String nombre) {
        return MacroSector.builder().codigo(codigo).nombre(nombre).build();
    }

    public static SectorActividad nuevoSector(String codigo, String nombre, MacroSector macrosector) {
        return SectorActividad.builder().codigo(codigo).nombre(nombre).macrosector(macrosector).build();
    }

    public static EjeTematico nuevoEjeTematico(String codigo, String nombre) {
        return EjeTematico.builder().codigo(codigo).nombre(nombre).activo(true).build();
    }

    public static Proyecto nuevoProyecto(String nombre, EstadoProyecto estado, UnidadEjecutora unidadEjecutora,
            Institucion institucion, SectorActividad sector, EjeTematico ejeTematico) {
        return Proyecto.builder()
                .nombre(nombre)
                .iniciativaInversion(IniciativaInversion.PROYECTO)
                .unidadEjecutora(unidadEjecutora)
                .institucion(institucion)
                .estado(estado)
                .fechaIngreso(LocalDateTime.now())
                .activo(true)
                .montoEstimadoInversion(1000.0)
                .sector(sector)
                .ejeTematico(ejeTematico)
                .descripcionProyecto("Descripcion generada para pruebas BDD.")
                .build();
    }
}
