package sv.gob.mh.siip.model.preinversion.enums;

import sv.gob.mh.siip.model.common.enums.RolUsuario;

/**
 * Tramos de la calificación de la matriz multicriterio (CU-PRE-26.5, RN01): el Técnico PRE califica los
 * criterios 1 a 4 y el Técnico SYMP el criterio 5; cada tramo lo revisa su Coordinador.
 */
public enum TramoPriorizacion {

    PRE(RolUsuario.TECNICO_PRE, RolUsuario.COORDINADOR_PRE, "criterios 1, 2, 3 y 4"),
    SYMP(RolUsuario.TECNICO_SYMP, RolUsuario.COORDINADOR_SYMP, "criterio 5");

    /** Único criterio que califica el Técnico SYMP (RN01). */
    public static final int CRITERIO_SYMP = 5;

    private final RolUsuario tecnico;
    private final RolUsuario coordinador;
    private final String descripcion;

    TramoPriorizacion(RolUsuario tecnico, RolUsuario coordinador, String descripcion) {
        this.tecnico = tecnico;
        this.coordinador = coordinador;
        this.descripcion = descripcion;
    }

    /** @return rol que califica el tramo */
    public RolUsuario getTecnico() {
        return tecnico;
    }

    /** @return rol que revisa el tramo y habilita sus ajustes (RN10, RN11) */
    public RolUsuario getCoordinador() {
        return coordinador;
    }

    /** @return los criterios del tramo, para los mensajes */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param numeroCriterio número del criterio (1 a 5)
     * @return si el criterio se califica en este tramo
     */
    public boolean incluye(int numeroCriterio) {
        return (numeroCriterio == CRITERIO_SYMP) == (this == SYMP);
    }
}
