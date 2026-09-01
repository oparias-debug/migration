package sv.gob.mh.siip.model.common.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "DEPARTAMENTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "departamento_seq")
    @SequenceGenerator(name = "departamento_seq", sequenceName = "DEPARTAMENTO_SEQ", allocationSize = 1)
    @Column(name = "ID_DEPARTAMENTO")
    private Long id;

    @Column(name = "CODIGO", nullable = false, length = 10, unique = true)
    private String codigo;

    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;
}
