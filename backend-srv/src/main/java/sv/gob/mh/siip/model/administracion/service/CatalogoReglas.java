package sv.gob.mh.siip.model.administracion.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;

import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.administracion.domain.CampoDefinicion;
import sv.gob.mh.siip.model.administracion.dto.ActiveStatusDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoVigencia;

/**
 * CU-ADM-01 (Gestion de Catalogos): reglas compartidas por catalogos y registros, el orden de despliegue
 * de los campos (Reglas 4 y 5) y la vigencia (Reglas 9b, 13 y 14).
 */
final class CatalogoReglas {

    /** Orden de despliegue de un CatalogField (Reglas 4 y 5): por `posicion`, nulls al final. */
    public static final Comparator<CampoDefinicion> POR_POSICION = Comparator
            .comparing(CampoDefinicion::getPosicion, Comparator.nullsLast(Comparator.naturalOrder()));

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private CatalogoReglas() {
    }

    /**
     * Regla 9b/14: sin toDate se asigna la fecha actual (9a); con toDate, debe ser
     * actual o pasada.
     */
    static LocalDate resolverToDate(LocalDate toDate) {
        LocalDate hoy = LocalDate.now(ZONA_EL_SALVADOR);
        if (toDate == null) {
            return hoy;
        }
        if (toDate.isAfter(hoy)) {
            throw new ValidacionNegocioException("TO_DATE_FUTURA",
                    "La fecha toDate provista es futura y no corresponde a una inactivación.", null);
        }
        return toDate;
    }

    /** Regla 13: sin active explicito, ACTIVE salvo que fromDate/toDate ya lo dejen en el pasado. */
    static EstadoVigencia estadoSolicitadoOCalculado(ActiveStatusDto activeSolicitado, LocalDate fechaHasta) {
        return activeSolicitado != null ? EstadoVigencia.valueOf(activeSolicitado.getValue())
                : calcularEstadoVigencia(fechaHasta);
    }

    static EstadoVigencia calcularEstadoVigencia(LocalDate fechaHasta) {
        return (fechaHasta != null && fechaHasta.isBefore(LocalDate.now(ZONA_EL_SALVADOR))) ? EstadoVigencia.INACTIVE
                : EstadoVigencia.ACTIVE;
    }
}
