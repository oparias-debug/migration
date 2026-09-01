package sv.gob.mh.siip.model.common.domain;

import jakarta.persistence.*;
import lombok.*;

/** Catalogo de instituciones del Sector Publico. CU-PRE-01 (catalogo). */
@Entity
@Table(name = "INSTITUCION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false, of = "id")
public class Institucion extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "institucion_seq")
    @SequenceGenerator(name = "institucion_seq", sequenceName = "INSTITUCION_SEQ", allocationSize = 1)
    @Column(name = "ID_INSTITUCION")
    private Long id;

    @Column(name = "CODIGO", nullable = false, length = 20, unique = true)
    private String codigo;

    @Column(name = "NOMBRE", nullable = false, length = 250)
    private String nombre;

    @Column(name = "MACROSECTOR", length = 100)
    private String macrosector;

    @Column(name = "SECTOR_ACTIVIDAD", length = 100)
    private String sectorActividad;

    @Column(name = "ACTIVO", nullable = false)
    private Boolean activo;
}
