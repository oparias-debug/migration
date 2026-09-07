package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/** Alternativas de solucion evaluadas para el proyecto (fila de "Registro de Alternativas"). CU-PRE-05. */
@Entity
@Table(name = "ALTERNATIVA_SOLUCION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class AlternativaSolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "alternativa_solucion_seq")
    @SequenceGenerator(name = "alternativa_solucion_seq", sequenceName = "ALTERNATIVA_SOLUCION_SEQ", allocationSize = 1)
    @Column(name = "ID_ALTERNATIVA_SOLUCION")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    /** Ninguno de los campos de fila es obligatorio a nivel de servidor (CU-PRE-05). */
    @Column(name = "NOMBRE_ALTERNATIVA", length = 300)
    private String nombreAlternativa;

    @Column(name = "MONTO_ALTERNATIVA")
    private Double montoAlternativa;

    /** Sin Tipo/Formato/límite de caracteres documentado en el Anexo B.1: no se inventa un límite. */
    @Lob
    @Column(name = "DESCRIPCION_ALTERNATIVA")
    private String descripcionAlternativa;

    /** RN2-6: como máximo una fila puede tener {@code true}. */
    @Column(name = "SELECCIONADA")
    private Boolean seleccionada;

    /** Preserva el orden de las filas entre guardados (mismo criterio que ObjetivoEspecifico en CU-PRE-04). */
    @Column(name = "ORDEN")
    private Integer orden;
}
