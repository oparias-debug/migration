package sv.gob.mh.siip.model.preinversion.domain;

import sv.gob.mh.siip.model.common.domain.Auditable;
import sv.gob.mh.siip.model.preinversion.enums.ComplejidadProyecto;
import sv.gob.mh.siip.model.preinversion.enums.TamanioProyecto;
import sv.gob.mh.siip.model.preinversion.enums.TipoCapital;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Estado de la Ruta de Preinversión de un proyecto (Anexo A.2): los 3 criterios calificados y si
 * la ruta vigente proviene de una modificación manual. 1:1 con {@link Proyecto}. CU-PRE-3.5.
 */
@Entity
@Table(name = "RUTA_PREINVERSION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false, of = "id")
public class RutaPreinversion extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ruta_preinversion_seq")
    @SequenceGenerator(name = "ruta_preinversion_seq", sequenceName = "RUTA_PREINVERSION_SEQ", allocationSize = 1)
    @Column(name = "ID_RUTA_PREINVERSION")
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false, unique = true)
    private Proyecto proyecto;

    /** Nulo si el proyecto no es de iniciativa PROYECTO (RN07/RN08 no usan calificación de criterios). */
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_CAPITAL", length = 30)
    private TipoCapital tipoCapital;

    @Enumerated(EnumType.STRING)
    @Column(name = "TAMANIO_PROYECTO", length = 20)
    private TamanioProyecto tamanioProyecto;

    @Enumerated(EnumType.STRING)
    @Column(name = "COMPLEJIDAD", length = 30)
    private ComplejidadProyecto complejidad;

    /** true si la ruta vigente proviene de {@code modificarRutaPreinversion}, no de la sugerencia original. */
    @Column(name = "FUE_MODIFICADA", nullable = false)
    @Builder.Default
    private Boolean fueModificada = false;

    @Column(name = "JUSTIFICACION_ULTIMA_MODIFICACION", length = 2000)
    private String justificacionUltimaModificacion;
}
