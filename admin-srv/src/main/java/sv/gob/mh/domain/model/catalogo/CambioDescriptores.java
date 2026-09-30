package sv.gob.mh.domain.model.catalogo;

import java.time.LocalDate;
import java.util.Set;

import sv.gob.mh.shared.enums.EstadoVigencia;
import sv.gob.mh.shared.exception.ErrorCatalogoException;

/**
 * Descriptores a actualizar de un catálogo (HU-ADM-01-05). {@code informados} distingue un
 * descriptor ausente (queda igual) de uno informado con {@code null}: {@code parent: null}
 * desvincula el catálogo de su padre (Regla 22).
 */
public record CambioDescriptores(Set<Descriptor> informados, String nombre, String padre, EstadoVigencia estado,
        LocalDate fechaDesde, LocalDate fechaHasta) {

    public enum Descriptor {
        NOMBRE,
        PADRE,
        ESTADO,
        FECHA_DESDE,
        FECHA_HASTA
    }

    public CambioDescriptores {
        informados = Set.copyOf(informados);
    }

    public boolean informa(Descriptor descriptor) {
        return informados.contains(descriptor);
    }

    /** Al menos un descriptor, y el nombre, si viene, no nulo. */
    public void validar() {
        if (informados.isEmpty()) {
            throw ErrorCatalogoException.solicitudInvalida("Debe indicar al menos un descriptor a actualizar.");
        }
        if (informa(Descriptor.NOMBRE) && nombre == null) {
            throw ErrorCatalogoException.solicitudInvalida("El nombre del catálogo no puede ser nulo.");
        }
    }
}
