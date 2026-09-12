package sv.gob.mh.siip.model.administracion.domain;

import java.util.ArrayList;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.administracion.enums.TipoCampo;

/**
 * Definicion de un campo (FIELD o KEY) de un {@link Catalogo}. La restriccion de tipo libre
 * (restriccionTipo, additionalProperties del OpenAPI) no se persiste: ningun escenario de
 * CU-ADM-01 ejercita su contenido.
 */
@Entity
@Table(name = "CAMPO_DEFINICION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class CampoDefinicion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "campo_definicion_seq")
    @SequenceGenerator(name = "campo_definicion_seq", sequenceName = "CAMPO_DEFINICION_SEQ", allocationSize = 1)
    @Column(name = "ID_CAMPO_DEFINICION")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CATALOGO", nullable = false)
    private Catalogo catalogo;

    @Column(name = "NOMBRE", nullable = false, length = 150)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 20)
    private TipoCampo tipo;

    @Column(name = "ES_KEY", nullable = false)
    private boolean esKey;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "CAMPO_DEFINICION_VALOR_ENUM", joinColumns = @JoinColumn(name = "ID_CAMPO_DEFINICION"))
    @OrderColumn(name = "ORDEN")
    @Column(name = "VALOR", length = 200)
    private List<String> valoresEnum = new ArrayList<>();
}
