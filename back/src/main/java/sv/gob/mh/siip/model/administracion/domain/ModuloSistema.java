package sv.gob.mh.siip.model.administracion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** [SUPUESTO] Catalogo de modulos funcionales del SIIP. */
@Entity
@Table(name = "MODULO_SISTEMA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ModuloSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "modulo_sistema_seq")
    @SequenceGenerator(name = "modulo_sistema_seq", sequenceName = "MODULO_SISTEMA_SEQ", allocationSize = 1)
    @Column(name = "ID_MODULO_SISTEMA")
    private Long id;

    @Column(name = "CODIGO", nullable = false, length = 20, unique = true)
    private String codigo;

    @Column(name = "NOMBRE", nullable = false, length = 150)
    private String nombre;
}
