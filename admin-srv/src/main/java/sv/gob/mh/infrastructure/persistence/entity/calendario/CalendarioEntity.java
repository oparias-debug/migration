package sv.gob.mh.infrastructure.persistence.entity.calendario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import sv.gob.mh.shared.enums.EstadoCalendario;

/** Tabla CALENDARIO: calendarios laborales (CU-ADM-04). DDL en {@code sql/V002__crear_tablas_calendario.sql}. */
@Entity
@Table(name = "CALENDARIO")
public class CalendarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calendario_seq")
    @SequenceGenerator(name = "calendario_seq", sequenceName = "CALENDARIO_SEQ", allocationSize = 1)
    @Column(name = "ID_CALENDARIO")
    private Long id;

    @Column(name = "CODIGO", nullable = false, unique = true, length = 100)
    private String codigo;

    @Column(name = "NOMBRE", nullable = false, length = 300)
    private String nombre;

    @Column(name = "DESCRIPCION", length = 1000)
    private String descripcion;

    @Column(name = "FECHA_INICIO", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "FECHA_FIN", nullable = false)
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoCalendario estado;

    /** preferred_username del administrador que lo creó (RN12); sin FK: los usuarios viven en Keycloak. */
    @Column(name = "ADMINISTRADOR", nullable = false, length = 100)
    private String administrador;

    @OneToMany(mappedBy = "calendario", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<PeriodoCalendarioEntity> periodos = new ArrayList<>();

    @OneToMany(mappedBy = "calendario", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fecha ASC")
    private List<ExcepcionCalendarioEntity> excepciones = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public EstadoCalendario getEstado() {
        return estado;
    }

    public void setEstado(EstadoCalendario estado) {
        this.estado = estado;
    }

    public String getAdministrador() {
        return administrador;
    }

    public void setAdministrador(String administrador) {
        this.administrador = administrador;
    }

    /** @return los períodos, sin permitir modificarlos por fuera de la entidad */
    public List<PeriodoCalendarioEntity> getPeriodos() {
        return Collections.unmodifiableList(periodos);
    }

    /** Reemplaza los períodos en la misma colección, para que Hibernate elimine los que salen (orphanRemoval). */
    public void reemplazarPeriodos(List<PeriodoCalendarioEntity> nuevos) {
        periodos.clear();
        periodos.addAll(nuevos);
    }

    /** @return las excepciones, sin permitir modificarlas por fuera de la entidad */
    public List<ExcepcionCalendarioEntity> getExcepciones() {
        return Collections.unmodifiableList(excepciones);
    }

    /** Reemplaza las excepciones en la misma colección, para que Hibernate elimine las que salen (orphanRemoval). */
    public void reemplazarExcepciones(List<ExcepcionCalendarioEntity> nuevas) {
        excepciones.clear();
        excepciones.addAll(nuevas);
    }
}
