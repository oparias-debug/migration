package sv.gob.mh.domain.model.catalogo;

import java.util.Comparator;

import sv.gob.mh.shared.enums.TipoCampo;

/**
 * Definición de un campo (FIELD o KEY) de un {@link Catalogo}. La {@code posicion} determina el
 * orden de los campos y el "primer campo no KEY" del Field Set por defecto (RN-07 a); puede faltar
 * en catálogos creados antes de que el contrato la exigiera.
 */
public class CampoDefinicion {

    /** Orden de los campos: por {@code posicion}, nulls al final. */
    public static final Comparator<CampoDefinicion> POR_POSICION = Comparator
            .comparing(CampoDefinicion::getPosicion, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(CampoDefinicion::getId, Comparator.nullsLast(Comparator.naturalOrder()));

    private final Long id;
    private final String nombre;
    private final boolean esKey;
    private final Integer posicion;
    private final DefinicionTipo definicion;

    public CampoDefinicion(Long id, String nombre, boolean esKey, Integer posicion, DefinicionTipo definicion) {
        this.id = id;
        this.nombre = nombre;
        this.esKey = esKey;
        this.posicion = posicion;
        this.definicion = definicion;
    }

    /** Campo pedido, que conserva el id del campo existente con el mismo nombre ({@code null} si es nuevo). */
    static CampoDefinicion desde(NuevoCampo campo, Long idExistente) {
        return new CampoDefinicion(idExistente, campo.nombre(), campo.esKey(), campo.posicion(), campo.definicion());
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoCampo getTipo() {
        return definicion.tipo();
    }

    public DefinicionTipo getDefinicion() {
        return definicion;
    }

    public boolean isEsKey() {
        return esKey;
    }

    public Integer getPosicion() {
        return posicion;
    }
}
