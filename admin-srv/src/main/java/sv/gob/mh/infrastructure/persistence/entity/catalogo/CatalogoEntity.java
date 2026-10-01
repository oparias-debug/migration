package sv.gob.mh.infrastructure.persistence.entity.catalogo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import sv.gob.mh.shared.enums.EstadoVigencia;

/** Tabla CATALOGO: catálogos del catalogMaster (CU-ADM-01). DDL en {@code sql/V001} y {@code sql/V003}. */
@Entity
@Table(name = "CATALOGO")
public class CatalogoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "catalogo_seq")
    @SequenceGenerator(name = "catalogo_seq", sequenceName = "CATALOGO_SEQ", allocationSize = 1)
    @Column(name = "ID_CATALOGO")
    private Long id;

    @Column(name = "CODIGO", nullable = false, unique = true, length = 100)
    private String codigo;

    @Column(name = "NOMBRE", nullable = false, length = 300)
    private String nombre;

    /** Catálogo padre (Regla 15), enlazado por su id; {@code null} si no tiene. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CATALOGO_PADRE")
    private CatalogoEntity catalogoPadre;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoVigencia estado;

    @Column(name = "FECHA_DESDE")
    private LocalDate fechaDesde;

    @Column(name = "FECHA_HASTA")
    private LocalDate fechaHasta;

    @OneToMany(mappedBy = "catalogo", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<CampoDefinicionEntity> campos = new ArrayList<>();

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

    public CatalogoEntity getCatalogoPadre() {
        return catalogoPadre;
    }

    public void setCatalogoPadre(CatalogoEntity catalogoPadre) {
        this.catalogoPadre = catalogoPadre;
    }

    /** Código del catálogo padre, que es como lo identifican el dominio y el contrato. */
    public String getCatalogoPadreCodigo() {
        return catalogoPadre == null ? null : catalogoPadre.getCodigo();
    }

    public EstadoVigencia getEstado() {
        return estado;
    }

    public void setEstado(EstadoVigencia estado) {
        this.estado = estado;
    }

    public LocalDate getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(LocalDate fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public LocalDate getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(LocalDate fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    /** @return los campos, sin permitir modificarlos por fuera de la entidad */
    public List<CampoDefinicionEntity> getCampos() {
        return Collections.unmodifiableList(campos);
    }

    public void agregarCampo(CampoDefinicionEntity campo) {
        campos.add(campo);
    }

    /** Reemplaza los campos en la misma colección, para que Hibernate elimine los que salen (orphanRemoval). */
    public void reemplazarCampos(List<CampoDefinicionEntity> nuevos) {
        campos.clear();
        campos.addAll(nuevos);
    }
}
