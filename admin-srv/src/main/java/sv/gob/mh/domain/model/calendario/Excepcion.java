package sv.gob.mh.domain.model.calendario;

import java.time.LocalDate;

import sv.gob.mh.shared.enums.TipoExcepcion;

/**
 * Reclasificación puntual de una fecha dentro de un {@link Calendario}, con prioridad sobre sus
 * períodos (RN01). Hay a lo sumo una por fecha; solo cambia a través del calendario.
 */
public class Excepcion {

    private final Long id;
    private LocalDate fecha;
    private TipoExcepcion tipo;
    private String descripcion;

    public Excepcion(Long id, LocalDate fecha, TipoExcepcion tipo, String descripcion) {
        this.id = id;
        this.fecha = fecha;
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    void redefinir(LocalDate fecha, TipoExcepcion tipo, String descripcion) {
        this.fecha = fecha;
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public TipoExcepcion getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
