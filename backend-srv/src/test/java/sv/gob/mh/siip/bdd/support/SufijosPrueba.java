package sv.gob.mh.siip.bdd.support;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Sufijos únicos y determinísticos para los códigos de los datos de prueba (institución, unidad
 * ejecutora, sector, etc.), que van a columnas únicas y se siembran en cada escenario. Reemplaza a
 * {@code UUID.randomUUID()} (Sonar java:S5977: los tests deben usar datos fijos): el contador no se
 * revierte con el rollback entre escenarios, así que nunca repite un valor dentro de la misma JVM.
 */
public final class SufijosPrueba {

    private static final AtomicLong SECUENCIA = new AtomicLong();

    private SufijosPrueba() {
    }

    /**
     * @param largo cantidad de caracteres del sufijo (respeta el largo de columna del código)
     * @return el siguiente número de la secuencia, relleno con ceros a {@code largo} caracteres
     */
    public static String nuevo(int largo) {
        String numero = Long.toString(SECUENCIA.incrementAndGet());
        if (numero.length() > largo) {
            throw new IllegalStateException("Se agotaron los sufijos de " + largo + " caracteres");
        }
        return "0".repeat(largo - numero.length()) + numero;
    }
}
