package sv.gob.mh.siip.model.preinversion.domain;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/** Alternativas de solucion evaluadas para el proyecto. CU-PRE-05. */
@Entity
@Table(name = "ALTERNATIVA_SOLUCION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class AlternativaSolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "alternativa_solucion_seq")
    @SequenceGenerator(name = "alternativa_solucion_seq", sequenceName = "ALTERNATIVA_SOLUCION_SEQ", allocationSize = 1)
    @Column(name = "ID_ALTERNATIVA_SOLUCION")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotBlank
    @Column(name = "DESCRIPCION", nullable = false, length = 2000)
    private String descripcion;

    @Column(name = "SELECCIONADA", nullable = false)
    private Boolean seleccionada;

    @Column(name = "JUSTIFICACION", length = 2000)
    private String justificacion;
}
