package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;

/**
 * CU-PRE-01.5, RN 2.8.c: asigna el siguiente CUP consecutivo de 5 dígitos, partiendo de 10000.
 *
 * <p>No existe una secuencia de base de datos dedicada para el CUP: se deriva leyendo el CUP más
 * alto ya asignado y sumando 1 ({@link #siguienteCup()}), lo que no es atómico si dos emisiones
 * de CUP ocurren a la vez (ambas pueden leer "todavía no hay ninguno" y calcular el mismo
 * valor). {@link #asignar(Proyecto)} corre en su propia transacción ({@code REQUIRES_NEW}) para
 * que, si el flush choca contra la unique constraint de {@code PROYECTO.CUP}, el llamador
 * ({@code ProyectoServiceImpl.emitirCup}) pueda reintentar con un valor recalculado sin heredar
 * una transacción ya marcada para rollback (y sin arrastrar ese error a una conexión/transacción
 * que algunos motores de base de datos dejarían inutilizable tras el primer choque). Como
 * contrapartida, el CUP queda comprometido de forma independiente y previa al resto de
 * emitirCup() (cambio de estado, notificación); se acepta porque RN4 ya establece que el CUP se
 * emite una única vez en el horizonte del proyecto, así que reservarlo cuanto antes es
 * preferible a reintentar el método completo (lo que reenviaría el correo de notificación).
 */
@Component
class GeneradorCup {

    private static final int CUP_INICIAL = 10000;

    private final ProyectoRepository proyectoRepository;

    GeneradorCup(ProyectoRepository proyectoRepository) {
        this.proyectoRepository = proyectoRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Proyecto asignar(Proyecto entidad) {
        entidad.setCup(siguienteCup());
        return proyectoRepository.saveAndFlush(entidad);
    }

    private String siguienteCup() {
        int siguiente = proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc()
                .map(p -> Integer.parseInt(p.getCup()) + 1)
                .orElse(CUP_INICIAL);
        return String.format("%05d", siguiente);
    }
}
