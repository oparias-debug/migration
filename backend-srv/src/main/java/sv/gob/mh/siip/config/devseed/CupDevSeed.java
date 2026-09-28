package sv.gob.mh.siip.config.devseed;

import java.util.Locale;

import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;

/**
 * CU-PRE-01.5, RN 2.8.c: siguiente CUP consecutivo de 5 dígitos, partiendo de 10000 — misma
 * regla que {@code GeneradorCup} (que no se reutiliza porque asigna el CUP en su propia
 * transacción). Compartido por {@link ProyectoDevSeeder} y {@link PresupuestoDevSeeder}.
 */
final class CupDevSeed {

    private static final int CUP_INICIAL = 10000;
    private static final String FORMATO_CUP = "%05d";

    private CupDevSeed() {
    }

    static String siguiente(ProyectoRepository proyectoRepository) {
        int siguiente = proyectoRepository.findFirstByCupIsNotNullOrderByCupDesc()
                .map(p -> Integer.parseInt(p.getCup()) + 1)
                .orElse(CUP_INICIAL);
        return String.format(Locale.ROOT, FORMATO_CUP, siguiente);
    }
}
