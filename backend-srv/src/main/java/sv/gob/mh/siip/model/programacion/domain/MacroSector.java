package sv.gob.mh.siip.model.programacion.domain;

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

/** Clasificador DGICP de macrosectores. CU-PRO-05, CU-PRO-06. */
@Entity
@Table(name = "MACROSECTOR")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class MacroSector {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "macrosector_seq")
    @SequenceGenerator(name = "macrosector_seq", sequenceName = "MACROSECTOR_SEQ", allocationSize = 1)
    @Column(name = "ID_MACROSECTOR")
    private Long id;

    /** No existe un código corto oficial para macrosectores (Anexo C.5): el nombre es el código. */
    @Column(name = "CODIGO", nullable = false, length = 100, unique = true)
    private String codigo;

    @Column(name = "NOMBRE", nullable = false, length = 150)
    private String nombre;
}
