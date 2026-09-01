package sv.gob.mh.siip.model.common.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "FUENTE_FINANCIAMIENTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class FuenteFinanciamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fuente_financiamiento_seq")
    @SequenceGenerator(name = "fuente_financiamiento_seq", sequenceName = "FUENTE_FINANCIAMIENTO_SEQ", allocationSize = 1)
    @Column(name = "ID_FUENTE_FINANCIAMIENTO")
    private Long id;

    @Column(name = "CODIGO", nullable = false, length = 20, unique = true)
    private String codigo;

    @Column(name = "NOMBRE", nullable = false, length = 200)
    private String nombre;

    @Column(name = "TIPO", length = 40)
    private String tipo;

    @Column(name = "ACTIVO", nullable = false)
    private Boolean activo;
}
