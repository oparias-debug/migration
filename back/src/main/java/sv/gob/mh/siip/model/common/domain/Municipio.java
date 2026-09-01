package sv.gob.mh.siip.model.common.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MUNICIPIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Municipio {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "municipio_seq")
    @SequenceGenerator(name = "municipio_seq", sequenceName = "MUNICIPIO_SEQ", allocationSize = 1)
    @Column(name = "ID_MUNICIPIO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_DEPARTAMENTO", nullable = false)
    private Departamento departamento;

    @Column(name = "CODIGO", nullable = false, length = 10, unique = true)
    private String codigo;

    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;
}
