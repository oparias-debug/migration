package sv.gob.mh.siip.bdd.support;

import java.time.LocalDateTime;

import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.programacion.domain.MacroSector;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;

/**
 * Builders reutilizados por los steps BDD de persistencia de los dominios que
 * dependen de Proyecto (ejecucion, oym, preinversion) para no repetir la
 * construcción de la cadena Institución → Unidad Ejecutora → Proyecto en
 * cada clase de steps. No son step definitions: es un helper de test plano.
 */
public final class ProyectoFixtures {

    private ProyectoFixtures() {
    }

    // Antes, un AtomicInteger estático generaba el CUP de prueba, independiente
    // del CUP real que ProyectoServiceImpl.siguienteCup() calcula como MAX(cup)+1.
    // Esos dos generadores comparten la misma columna única y, cuando un escenario
    // que siembra un Proyecto con este fixture también ejercita el flujo real de
    // emitir CUP (CU-PRE-01.5-emitir-cup.feature) en la misma transacción, el
    // contador estático no se revierte con el rollback de @Transactional entre
    // escenarios aunque los inserts sí, y ambos generadores terminan calculando el
    // mismo próximo número (incidente real: "Unique index or primary key violation"
    // en PROYECTO.CUP). Recalcular aquí el mismo MAX(cup)+1 que usa el código real
    // los mantiene en la misma secuencia monótona sin importar el orden de
    // ejecución ni los rollbacks entre escenarios.
    public static String nuevoCup(ProyectoRepository proyectos) {
        int siguiente = proyectos.findFirstByCupIsNotNullOrderByCupDesc()
                .map(p -> Integer.parseInt(p.getCup()) + 1)
                .orElse(10000);
        return String.format("%05d", siguiente);
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
