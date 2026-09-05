package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "OBJETIVO_ESPECIFICO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ObjetivoEspecifico {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "objetivo_especifico_seq")
    @SequenceGenerator(name = "objetivo_especifico_seq", sequenceName = "OBJETIVO_ESPECIFICO_SEQ", allocationSize = 1)
    @Column(name = "ID_OBJETIVO_ESPECIFICO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_IDENTIFICACION", nullable = false)
    private Identificacion identificacion;

    /** RNC: hasta 500 caracteres (CU-PRE-04); no es obligatorio a nivel de servidor. */
    @Column(name = "DESCRIPCION", length = 500)
    private String descripcion;

    @Column(name = "ORDEN")
    private Integer orden;
}
