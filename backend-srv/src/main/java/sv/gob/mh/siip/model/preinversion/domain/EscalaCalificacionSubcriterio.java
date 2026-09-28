package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import sv.gob.mh.siip.model.preinversion.enums.ValorCalificacion;

/**
 * Fila de la Escala de Calificación de un subcriterio de priorización (CU-PRE-26.5, botón "Ver
 * Escala de Calificación", RN08, Anexo C). No usa FK contra {@link SubcriterioPriorizacion}
 * (mismo criterio que {@link MedidaCatalogo}): el código del subcriterio solo se valida por
 * existencia al consultar, no se referencia con integridad relacional.
 */
@Entity
@Table(name = "ESCALA_CALIFICACION_SUBCRITERIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class EscalaCalificacionSubcriterio {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "escala_calificacion_subcriterio_seq")
    @SequenceGenerator(name = "escala_calificacion_subcriterio_seq",
            sequenceName = "ESCALA_CALIFICACION_SUBCRITERIO_SEQ", allocationSize = 1)
    @Column(name = "ID_ESCALA_CALIFICACION")
    private Long id;

    @NotBlank
    @Column(name = "CODIGO_SUBCRITERIO", nullable = false, length = 50)
    private String codigoSubcriterio;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "VALOR", nullable = false, length = 10)
    private ValorCalificacion valor;

    @NotBlank
    @Column(name = "DESCRIPCION", nullable = false, length = 500)
    private String descripcion;
}
