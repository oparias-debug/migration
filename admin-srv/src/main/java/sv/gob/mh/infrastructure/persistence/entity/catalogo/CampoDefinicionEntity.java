package sv.gob.mh.infrastructure.persistence.entity.catalogo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import jakarta.persistence.OrderColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import sv.gob.mh.shared.enums.TipoCampo;

/**
 * Tabla CAMPO_DEFINICION: campos (FIELD/KEY) de un catálogo con su tipo y la restricción del tipo
 * (RN-04): mínimo y máximo de NUMERIC, longitud de STRING, rango de FECHA y, en tabla propia, los
 * valores de ENUM. DDL en {@code sql/V001} y {@code sql/V004}.
 */
@Entity
@Table(name = "CAMPO_DEFINICION")
public class CampoDefinicionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "campo_definicion_seq")
    @SequenceGenerator(name = "campo_definicion_seq", sequenceName = "CAMPO_DEFINICION_SEQ", allocationSize = 1)
    @Column(name = "ID_CAMPO_DEFINICION")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CATALOGO", nullable = false)
    private CatalogoEntity catalogo;

    @Column(name = "NOMBRE", nullable = false, length = 150)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 20)
    private TipoCampo tipo;

    @Column(name = "ES_KEY", nullable = false)
    private boolean esKey;

    @Column(name = "POSICION")
    private Integer posicion;

    @Column(name = "VALOR_MINIMO", precision = 38, scale = 10)
    private BigDecimal valorMinimo;

    @Column(name = "VALOR_MAXIMO", precision = 38, scale = 10)
    private BigDecimal valorMaximo;

    @Column(name = "LONGITUD_MAXIMA")
    private Integer longitudMaxima;

    @Column(name = "FECHA_MINIMA")
    private LocalDate fechaMinima;

    @Column(name = "FECHA_MAXIMA")
    private LocalDate fechaMaxima;

    @ElementCollection
    @CollectionTable(name = "CAMPO_DEFINICION_VALOR_ENUM", joinColumns = @JoinColumn(name = "ID_CAMPO_DEFINICION"))
    @OrderColumn(name = "ORDEN")
    @Column(name = "VALOR", length = 255)
    private List<String> valoresEnum = new ArrayList<>();

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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoCampo getTipo() {
        return tipo;
    }

    public void setTipo(TipoCampo tipo) {
        this.tipo = tipo;
    }

    public boolean isEsKey() {
        return esKey;
    }

    public void setEsKey(boolean esKey) {
        this.esKey = esKey;
    }

    public Integer getPosicion() {
        return posicion;
    }

    public void setPosicion(Integer posicion) {
        this.posicion = posicion;
    }

    public BigDecimal getValorMinimo() {
        return valorMinimo;
    }

    public void setValorMinimo(BigDecimal valorMinimo) {
        this.valorMinimo = valorMinimo;
    }

    public BigDecimal getValorMaximo() {
        return valorMaximo;
    }

    public void setValorMaximo(BigDecimal valorMaximo) {
        this.valorMaximo = valorMaximo;
    }

    public Integer getLongitudMaxima() {
        return longitudMaxima;
    }

    public void setLongitudMaxima(Integer longitudMaxima) {
        this.longitudMaxima = longitudMaxima;
    }

    public LocalDate getFechaMinima() {
        return fechaMinima;
    }

    public void setFechaMinima(LocalDate fechaMinima) {
        this.fechaMinima = fechaMinima;
    }

    public LocalDate getFechaMaxima() {
        return fechaMaxima;
    }

    public void setFechaMaxima(LocalDate fechaMaxima) {
        this.fechaMaxima = fechaMaxima;
    }

    /** @return los valores del campo ENUM, sin permitir modificarlos por fuera de la entidad */
    public List<String> getValoresEnum() {
        return Collections.unmodifiableList(valoresEnum);
    }

    /** Reemplaza los valores en la misma colección, para que Hibernate sincronice la tabla. */
    public void reemplazarValoresEnum(List<String> nuevos) {
        valoresEnum.clear();
        valoresEnum.addAll(nuevos);
    }
}
