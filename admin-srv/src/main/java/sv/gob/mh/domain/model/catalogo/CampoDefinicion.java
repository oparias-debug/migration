package sv.gob.mh.domain.model.catalogo;

import java.util.Comparator;

import sv.gob.mh.shared.enums.TipoCampo;

/**
 * Definición de un campo (FIELD o KEY) de un {@link Catalogo}. La {@code posicion} (desde 1) la da
 * el orden del arreglo {@code fields} del contrato y determina el "primer campo no KEY" de las
 * Reglas 4 y 5; puede faltar en catálogos creados antes de que el contrato la exigiera.
 */
public class CampoDefinicion {

    /** Orden de los campos (Reglas 4 y 5): por {@code posicion}, nulls al final. */
    public static final Comparator<CampoDefinicion> POR_POSICION = Comparator
            .comparing(CampoDefinicion::getPosicion, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(CampoDefinicion::getId, Comparator.nullsLast(Comparator.naturalOrder()));

    private final Long id;
    private final String nombre;
    private final TipoCampo tipo;
    private final boolean esKey;
    private final Integer posicion;

    public CampoDefinicion(Long id, String nombre, TipoCampo tipo, boolean esKey, Integer posicion) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.esKey = esKey;
        this.posicion = posicion;
    }

    /** Campo nuevo, aún sin persistir. El contrato no expone el tipo: cada campo es STRING. */
    static CampoDefinicion nuevo(NuevoCampo campo, int posicion) {
        return new CampoDefinicion(null, campo.nombre(), TipoCampo.STRING, campo.esKey(), posicion);
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoCampo getTipo() {
        return tipo;
    }

    public boolean isEsKey() {
        return esKey;
    }

    public Integer getPosicion() {
        return posicion;
    }
}
