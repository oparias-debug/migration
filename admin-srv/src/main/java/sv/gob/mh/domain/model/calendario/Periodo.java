package sv.gob.mh.domain.model.calendario;

import java.time.LocalDate;

import sv.gob.mh.shared.enums.TipoPeriodo;

/**
 * Período LABORAL o NO_LABORAL de un {@link Calendario}, definido mediante una {@link Recurrencia}.
 * Su código es único dentro del calendario (RN15); solo cambia a través del calendario.
 */
public class Periodo {

    private final Long id;
    private String codigo;
    private String nombre;
    private TipoPeriodo tipo;
    private Recurrencia recurrencia;

    public Periodo(Long id, String codigo, String nombre, TipoPeriodo tipo, Recurrencia recurrencia) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.recurrencia = recurrencia;
    }

    void redefinir(String codigo, String nombre, TipoPeriodo tipo, Recurrencia recurrencia) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.recurrencia = recurrencia;
    }

    public boolean incluye(LocalDate fecha) {
        return recurrencia.incluye(fecha);
    }

    public boolean esLaboral() {
        return tipo == TipoPeriodo.LABORAL;
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoPeriodo getTipo() {
        return tipo;
    }

    public Recurrencia getRecurrencia() {
        return recurrencia;
    }
}
