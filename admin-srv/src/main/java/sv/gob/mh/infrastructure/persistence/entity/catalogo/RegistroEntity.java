package sv.gob.mh.infrastructure.persistence.entity.catalogo;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import sv.gob.mh.shared.enums.EstadoVigencia;

/** Tabla REGISTRO_CATALOGO: registros de un catálogo, con sus valores en REGISTRO_VALOR. */
@Entity
@Table(name = "REGISTRO_CATALOGO", uniqueConstraints = @UniqueConstraint(columnNames = { "ID_CATALOGO", "CLAVE" }))
public class RegistroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "registro_catalogo_seq")
    @SequenceGenerator(name = "registro_catalogo_seq", sequenceName = "REGISTRO_CATALOGO_SEQ", allocationSize = 1)
    @Column(name = "ID_REGISTRO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CATALOGO", nullable = false)
    private CatalogoEntity catalogo;

    @Column(name = "CLAVE", nullable = false, length = 255)
    private String clave;

    /** Registro del catálogo padre al que se enlaza (RN-05); {@code null} si el catálogo no tiene padre. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_REGISTRO_PADRE")
    private RegistroEntity registroPadre;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoVigencia estado;

    @Column(name = "FECHA_DESDE")
    private LocalDate fechaDesde;

    @Column(name = "FECHA_HASTA")
    private LocalDate fechaHasta;

    @ElementCollection
    @CollectionTable(name = "REGISTRO_VALOR", joinColumns = @JoinColumn(name = "ID_REGISTRO"))
    @MapKeyColumn(name = "NOMBRE_CAMPO")
    @Column(name = "VALOR", length = 4000)
    private Map<String, String> valores = new HashMap<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CatalogoEntity getCatalogo() {
        return catalogo;
    }

    public void setCatalogo(CatalogoEntity catalogo) {
        this.catalogo = catalogo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public RegistroEntity getRegistroPadre() {
        return registroPadre;
    }

    public void setRegistroPadre(RegistroEntity registroPadre) {
        this.registroPadre = registroPadre;
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

    /** @return los valores, sin permitir modificarlos por fuera de la entidad */
    public Map<String, String> getValores() {
        return Collections.unmodifiableMap(valores);
    }

    public void reemplazarValores(Map<String, String> nuevos) {
        valores.clear();
        valores.putAll(nuevos);
    }
}
