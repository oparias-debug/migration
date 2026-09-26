package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Catalogo de Ejes del Plan Cuscatlán (Anexo C.3), fuente del listado seleccionable del
 * campo "Línea/Eje del Plan de Gobierno" de la pantalla "Nuevo registro". Condicional.
 */
@Entity
@Table(name = "EJE_PLAN_GOBIERNO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class EjePlanGobierno {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eje_plan_gobierno_seq")
    @SequenceGenerator(name = "eje_plan_gobierno_seq", sequenceName = "EJE_PLAN_GOBIERNO_SEQ", allocationSize = 1)
    @Column(name = "ID_EJE_PLAN_GOBIERNO")
    private Long id;

    @NotBlank
    @Column(name = "CODIGO", nullable = false, length = 20, unique = true)
    private String codigo;

    @NotBlank
    @Column(name = "NOMBRE", nullable = false, length = 250)
    private String nombre;

    /** Indica si el eje está sujeto a actualización periódica (Anexo C.3). */
    @Column(name = "SUJETO_ACTUALIZACION")
    private Boolean sujetoActualizacion;

    @Column(name = "ACTIVO", nullable = false)
    private Boolean activo;
}
