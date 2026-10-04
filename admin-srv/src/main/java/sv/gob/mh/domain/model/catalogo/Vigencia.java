package sv.gob.mh.domain.model.catalogo;

import java.time.LocalDate;
import java.time.ZoneId;

import sv.gob.mh.shared.enums.EstadoVigencia;

/**
 * Reglas de vigencia (RN-12, RN-15, RN-16) comunes a catálogos y registros, evaluadas a la fecha
 * de El Salvador. La TO DATE es la fecha que sigue al ':' en la producción {@code valid}.
 */
public final class Vigencia {

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private Vigencia() {
    }

    public static LocalDate hoy() {
        return LocalDate.now(ZONA_EL_SALVADOR);
    }

    /** RN-12b y RN-16: una TO DATE igual a hoy o pasada deja el elemento INACTIVE. */
    public static boolean vencido(LocalDate toDate) {
        return toDate != null && !toDate.isAfter(hoy());
    }

    /** RN-15 y RN-16: estado vigente hoy; INACTIVE si así se marcó o si su TO DATE ya llegó. */
    public static EstadoVigencia estadoEfectivo(EstadoVigencia estado, LocalDate toDate) {
        return estado == EstadoVigencia.INACTIVE || vencido(toDate) ? EstadoVigencia.INACTIVE : EstadoVigencia.ACTIVE;
    }

    /** E-09: la FROM DATE no puede ser posterior a la TO DATE. */
    public static void validarRango(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw ErroresCatalogo.vigenciaInvalida();
        }
    }

    /**
     * RN-12: TO DATE con que queda un elemento inactivado. Una TO DATE actual o pasada se conserva
     * (12b); sin ella, o con una futura, la inactivación es a la fecha actual (12a).
     */
    public static LocalDate fechaInactivacion(LocalDate toDate) {
        return vencido(toDate) ? toDate : hoy();
    }

    /** E-16: al reactivar, la TO DATE debe quedar vacía o futura. */
    public static void validarFechaReactivacion(LocalDate toDate) {
        if (vencido(toDate)) {
            throw ErroresCatalogo.fechaVigenciaVencida();
        }
    }

    /**
     * TO DATE de un elemento inactivado en cascada (RN-06, RN-14): la fecha de la inactivación,
     * salvo que ya tuviera una anterior.
     */
    public static LocalDate fechaHastaEnCascada(LocalDate actual, LocalDate fechaInactivacion) {
        return actual != null && !actual.isAfter(fechaInactivacion) ? actual : fechaInactivacion;
    }
}
