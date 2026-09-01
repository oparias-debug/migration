package sv.gob.mh.siip.model.administracion.domain;

import jakarta.persistence.*;
import lombok.*;

/** [SUPUESTO] Catalogo de sistemas externos integrados (SIAF, etc.). Infiere "CU-ITF-01 Interfaz con SIAF". */
@Entity
@Table(name = "SISTEMA_EXTERNO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class SistemaExterno {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sistema_externo_seq")
    @SequenceGenerator(name = "sistema_externo_seq", sequenceName = "SISTEMA_EXTERNO_SEQ", allocationSize = 1)
    @Column(name = "ID_SISTEMA_EXTERNO")
    private Long id;

    @Column(name = "CODIGO", nullable = false, length = 20, unique = true)
    private String codigo;

    @Column(name = "NOMBRE", nullable = false, length = 150)
    private String nombre;

    @Column(name = "ACTIVO", nullable = false)
    private Boolean activo;
}
