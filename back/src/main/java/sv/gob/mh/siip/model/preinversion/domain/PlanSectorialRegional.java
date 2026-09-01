package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Catalogo de Planes Sectoriales/Regionales (Anexo C.4), fuente del listado seleccionable
 * del campo "Plan Sectorial/Regional al que contribuye" de la pantalla "Nuevo registro".
 * Condicional.
 */
@Entity
@Table(name = "PLAN_SECTORIAL_REGIONAL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class PlanSectorialRegional {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "plan_sectorial_regional_seq")
    @SequenceGenerator(name = "plan_sectorial_regional_seq", sequenceName = "PLAN_SECTORIAL_REGIONAL_SEQ", allocationSize = 1)
    @Column(name = "ID_PLAN_SECTORIAL_REGIONAL")
    private Long id;

    /** No existe un código corto oficial para planes sectoriales/regionales (Anexo C.4): el
        nombre es el código. */
    @NotBlank
    @Column(name = "CODIGO", nullable = false, length = 250, unique = true)
    private String codigo;

    @NotBlank
    @Column(name = "NOMBRE", nullable = false, length = 250)
    private String nombre;

    /** Sector al que está asociado el plan (referencia descriptiva del Anexo C.4, no es FK
        contra el catálogo de sectores porque no siempre coincide con un sector único). */
    @Column(name = "SECTOR_ASOCIADO", length = 250)
    private String sectorAsociado;

    /** Indica si el plan está sujeto a actualización periódica (Anexo C.4). */
    @Column(name = "SUJETO_ACTUALIZACION")
    private Boolean sujetoActualizacion;

    @Column(name = "ACTIVO", nullable = false)
    private Boolean activo;
}
