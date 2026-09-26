package sv.gob.mh.siip.model.programacion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Clasificador DGICP de sectores de actividad, subordinado a Macrosector. CU-PRO-06. */
@Entity
@Table(name = "SECTOR_ACTIVIDAD")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class SectorActividad {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sector_actividad_seq")
    @SequenceGenerator(name = "sector_actividad_seq", sequenceName = "SECTOR_ACTIVIDAD_SEQ", allocationSize = 1)
    @Column(name = "ID_SECTOR_ACTIVIDAD")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_MACROSECTOR", nullable = false)
    private MacroSector macrosector;

    /**
     * No existe un código corto oficial para sectores (Anexo C.5): se usa
     * "{@code <macrosector>::<sector>}" para garantizar unicidad entre macrosectores distintos que
     * pudieran compartir el nombre de un sector.
     */
    @Column(name = "CODIGO", nullable = false, length = 100, unique = true)
    private String codigo;

    @Column(name = "NOMBRE", nullable = false, length = 150)
    private String nombre;
}
