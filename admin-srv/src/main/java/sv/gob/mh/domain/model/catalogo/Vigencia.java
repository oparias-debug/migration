package sv.gob.mh.domain.model.catalogo;

import java.time.LocalDate;
import java.time.ZoneId;

import sv.gob.mh.shared.enums.EstadoVigencia;
import sv.gob.mh.shared.exception.ErrorCatalogoException;

/** Reglas de vigencia (8-14) comunes a catálogos y registros, evaluadas a la fecha de El Salvador. */
public final class Vigencia {

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private Vigencia() {
    }

    public static LocalDate hoy() {
        return LocalDate.now(ZONA_EL_SALVADOR);
    }

    /** Regla 14: una {@code toDate} igual a hoy o pasada deja el elemento INACTIVE. */
    public static boolean vencido(LocalDate toDate) {
        return toDate != null && !toDate.isAfter(hoy());
    }

    /** Reglas 13 y 14: ACTIVE por defecto, salvo que se pida INACTIVE o la {@code toDate} ya haya llegado. */
    public static EstadoVigencia estadoInicial(EstadoVigencia estadoSolicitado, LocalDate toDate) {
        if (vencido(toDate) || estadoSolicitado == EstadoVigencia.INACTIVE) {
            return EstadoVigencia.INACTIVE;
        }
        return EstadoVigencia.ACTIVE;
    }

    /** Estado vigente hoy: INACTIVE si así se marcó o si su {@code toDate} ya llegó (Regla 14). */
    public static EstadoVigencia estadoEfectivo(EstadoVigencia estado, LocalDate toDate) {
        return estado == EstadoVigencia.INACTIVE || vencido(toDate) ? EstadoVigencia.INACTIVE : EstadoVigencia.ACTIVE;
    }

    /**
     * Regla 9: sin {@code toDate} la inactivación es a la fecha actual (9a); con {@code toDate},
     * esta debe ser la actual o una pasada (9b).
     */
    public static LocalDate fechaInactivacion(LocalDate toDate) {
        if (toDate == null) {
            return hoy();
        }
        if (toDate.isAfter(hoy())) {
            throw ErrorCatalogoException.reglaNegocio("FECHA_INACTIVACION_FUTURA",
                    "La fecha de inactivación debe ser la fecha actual o una fecha pasada.", "toDate",
                    toDate.toString());
        }
        return toDate;
    }
}
