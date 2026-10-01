package sv.gob.mh.infrastructure.persistence.entity.calendario;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
import sv.gob.mh.shared.enums.TipoPeriodo;

/**
 * Tabla PERIODO_CALENDARIO: período LABORAL/NO_LABORAL de un calendario (CU-ADM-04) con su
 * recurrencia. UNA_VEZ y SEMANAL usan FECHA_INICIO/FECHA_FIN; SEMANAL además sus días de la semana y
 * MENSUAL sus días del mes y meses, cada conjunto en su tabla.
 */
@Entity
@Table(name = "PERIODO_CALENDARIO", uniqueConstraints = @UniqueConstraint(
        name = "UK_PERIODO_CALENDARIO_CODIGO", columnNames = { "ID_CALENDARIO", "CODIGO" }))
public class PeriodoCalendarioEntity {

    /** Forma de la recurrencia que define las fechas del período. */
    public enum TipoRecurrencia {
        UNA_VEZ,
        SEMANAL,
        MENSUAL
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "periodo_calendario_seq")
    @SequenceGenerator(name = "periodo_calendario_seq", sequenceName = "PERIODO_CALENDARIO_SEQ",
            allocationSize = 1)
    @Column(name = "ID_PERIODO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CALENDARIO", nullable = false)
    private CalendarioEntity calendario;

    @Column(name = "CODIGO", nullable = false, length = 100)
    private String codigo;

    @Column(name = "NOMBRE", nullable = false, length = 300)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 20)
    private TipoPeriodo tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_RECURRENCIA", nullable = false, length = 20)
    private TipoRecurrencia tipoRecurrencia;

    @Column(name = "FECHA_INICIO")
    private LocalDate fechaInicio;

    @Column(name = "FECHA_FIN")
    private LocalDate fechaFin;

    @ElementCollection
    @CollectionTable(name = "PERIODO_CALENDARIO_DIA_SEMANA", joinColumns = @JoinColumn(name = "ID_PERIODO"))
    @Enumerated(EnumType.STRING)
    @Column(name = "DIA_SEMANA", nullable = false, length = 15)
    private Set<DayOfWeek> diasSemana = EnumSet.noneOf(DayOfWeek.class);

    @ElementCollection
    @CollectionTable(name = "PERIODO_CALENDARIO_DIA_MES", joinColumns = @JoinColumn(name = "ID_PERIODO"))
    @Column(name = "DIA_MES", nullable = false)
    private Set<Integer> diasMes = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "PERIODO_CALENDARIO_MES", joinColumns = @JoinColumn(name = "ID_PERIODO"))
    @Enumerated(EnumType.STRING)
    @Column(name = "MES", nullable = false, length = 10)
    private Set<Month> meses = EnumSet.noneOf(Month.class);

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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoPeriodo getTipo() {
        return tipo;
    }

    public void setTipo(TipoPeriodo tipo) {
        this.tipo = tipo;
    }

    public TipoRecurrencia getTipoRecurrencia() {
        return tipoRecurrencia;
    }

    public void setTipoRecurrencia(TipoRecurrencia tipoRecurrencia) {
        this.tipoRecurrencia = tipoRecurrencia;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Set<DayOfWeek> getDiasSemana() {
        return Collections.unmodifiableSet(diasSemana);
    }

    /**
     * Reemplaza el contenido en la misma colección, que es la que Hibernate sincroniza; si no cambia,
     * no la toca, para que no la reescriba.
     */
    public void reemplazarDiasSemana(Collection<DayOfWeek> nuevos) {
        if (diasSemana.equals(Set.copyOf(nuevos))) {
            return;
        }
        diasSemana.clear();
        diasSemana.addAll(nuevos);
    }

    public Set<Integer> getDiasMes() {
        return Collections.unmodifiableSet(diasMes);
    }

    public void reemplazarDiasMes(Collection<Integer> nuevos) {
        if (diasMes.equals(Set.copyOf(nuevos))) {
            return;
        }
        diasMes.clear();
        diasMes.addAll(nuevos);
    }

    public Set<Month> getMeses() {
        return Collections.unmodifiableSet(meses);
    }

    public void reemplazarMeses(Collection<Month> nuevos) {
        if (meses.equals(Set.copyOf(nuevos))) {
            return;
        }
        meses.clear();
        meses.addAll(nuevos);
    }
}
