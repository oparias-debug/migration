package sv.gob.mh.siip.model.administracion.domain;

import java.time.LocalDate;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.administracion.enums.EstadoVigencia;

/**
 * Registro de un {@link Catalogo}: valor de clave (KEY) y del resto de campos definidos. Los
 * valores se guardan como texto (el OpenAPI los declara {@code additionalProperties: true}, sin
 * escenario de CU-ADM-01 que dependa de conservar el tipo original).
 */
@Entity
@Table(name = "REGISTRO_CATALOGO", uniqueConstraints = @UniqueConstraint(columnNames = { "ID_CATALOGO", "CLAVE" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Registro {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "registro_catalogo_seq")
    @SequenceGenerator(name = "registro_catalogo_seq", sequenceName = "REGISTRO_CATALOGO_SEQ", allocationSize = 1)
    @Column(name = "ID_REGISTRO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CATALOGO", nullable = false)
    private Catalogo catalogo;

    @Column(name = "CLAVE", nullable = false, length = 200)
    private String clave;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoVigencia estado;

    @Column(name = "FECHA_DESDE")
    private LocalDate fechaDesde;

    @Column(name = "FECHA_HASTA")
    private LocalDate fechaHasta;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "REGISTRO_VALOR", joinColumns = @JoinColumn(name = "ID_REGISTRO"))
    @MapKeyColumn(name = "NOMBRE_CAMPO")
    @Column(name = "VALOR", length = 1000)
    private Map<String, String> valores = new HashMap<>();
}
