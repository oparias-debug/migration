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

import sv.gob.mh.siip.model.preinversion.enums.TipoCatalogoEspecificar;
import sv.gob.mh.siip.model.preinversion.enums.TipoEspecificar;

/**
 * Criterio del catálogo de elegibilidad (CU-ADM-02/CU-PRE-25): dimensión + criterio + tipo de
 * "Especificar". El propio contrato marca ⚠️ que la tabla completa (6 dimensiones/20 criterios/
 * 100 puntos) no tiene pertenencia confirmada a este CU; se implementa con datos de prueba.
 */
@Entity
@Table(name = "CRITERIO_ELEGIBILIDAD")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class CriterioElegibilidad {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "criterio_elegibilidad_seq")
    @SequenceGenerator(name = "criterio_elegibilidad_seq", sequenceName = "CRITERIO_ELEGIBILIDAD_SEQ",
            allocationSize = 1)
    @Column(name = "ID_CRITERIO_ELEGIBILIDAD")
    private Long id;

    @NotBlank
    @Column(name = "CODIGO", nullable = false, length = 50, unique = true)
    private String codigo;

    @NotBlank
    @Column(name = "DIMENSION", nullable = false, length = 250)
    private String dimension;

    @NotBlank
    @Column(name = "CRITERIO", nullable = false, length = 500)
    private String criterio;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_ESPECIFICAR", nullable = false, length = 20)
    private TipoEspecificar tipoEspecificar;

    /** No nulo únicamente si tipoEspecificar = CATALOGO. */
    @Enumerated(EnumType.STRING)
    @Column(name = "CATALOGO_ESPECIFICAR", length = 30)
    private TipoCatalogoEspecificar catalogoEspecificar;

    /** RN08 — algunos criterios con tipoEspecificar = CATALOGO permiten seleccionar más de un elemento. */
    @Column(name = "PERMITE_SELECCION_MULTIPLE")
    private Boolean permiteSeleccionMultiple;
}
