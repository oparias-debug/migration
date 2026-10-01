package sv.gob.mh.infrastructure.persistence.entity.calendario;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import sv.gob.mh.shared.enums.TipoExcepcion;

/** Tabla EXCEPCION_CALENDARIO: reclasificación puntual de una fecha de un calendario (CU-ADM-04). */
@Entity
@Table(name = "EXCEPCION_CALENDARIO", uniqueConstraints = @UniqueConstraint(
        name = "UK_EXCEPCION_CALENDARIO_FECHA", columnNames = { "ID_CALENDARIO", "FECHA" }))
public class ExcepcionCalendarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "excepcion_calendario_seq")
    @SequenceGenerator(name = "excepcion_calendario_seq", sequenceName = "EXCEPCION_CALENDARIO_SEQ",
            allocationSize = 1)
    @Column(name = "ID_EXCEPCION")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CALENDARIO", nullable = false)
    private CalendarioEntity calendario;

    @Column(name = "FECHA", nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 20)
    private TipoExcepcion tipo;

    @Column(name = "DESCRIPCION", length = 500)
    private String descripcion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CalendarioEntity getCalendario() {
        return calendario;
    }

    public void setCalendario(CalendarioEntity calendario) {
        this.calendario = calendario;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public TipoExcepcion getTipo() {
        return tipo;
    }

    public void setTipo(TipoExcepcion tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
