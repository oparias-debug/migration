package sv.gob.mh.infrastructure.persistence.entity.catalogo;

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
 * Tabla CAMPO_DEFINICION: campos (FIELD/KEY) de un catálogo. La lista de valores de un campo ENUM
 * tiene tabla propia pero ningún escenario de CU-ADM-01 la usa todavía: el modelo de dominio no la
 * expone y el adaptador la conserva tal cual.
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

    @ElementCollection
    @CollectionTable(name = "CAMPO_DEFINICION_VALOR_ENUM", joinColumns = @JoinColumn(name = "ID_CAMPO_DEFINICION"))
    @OrderColumn(name = "ORDEN")
    @Column(name = "VALOR", length = 200)
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

    /** @return los valores del campo ENUM, sin permitir modificarlos por fuera de la entidad */
    public List<String> getValoresEnum() {
        return Collections.unmodifiableList(valoresEnum);
    }
}
