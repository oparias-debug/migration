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
import sv.gob.mh.siip.model.preinversion.enums.TipoUnidadMedida;

/**
 * Catálogo de unidades de medida (CU-ADM-02, Anexo D.1), "idéntico en contenido" al del Anexo
 * C.1 de CU-PRE-09 "Análisis de Mercado" (donde hoy viaja denormalizado dentro de
 * ProductoIndicador/ProductoSeleccionado, sin catálogo propio) — ver nota en
 * CU-ADM-02-catalogos.openapi.yaml.
 */
@Entity
@Table(name = "UNIDAD_MEDIDA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class UnidadMedida {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unidad_medida_seq")
    @SequenceGenerator(name = "unidad_medida_seq", sequenceName = "UNIDAD_MEDIDA_SEQ", allocationSize = 1)
    @Column(name = "ID_UNIDAD_MEDIDA")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 10)
    private TipoUnidadMedida tipo;

    @NotBlank
    @Column(name = "CATEGORIA", nullable = false, length = 100)
    private String categoria;

    @NotBlank
    @Column(name = "UNIDAD_MEDIDA", nullable = false, length = 100)
    private String nombre;

    @Column(name = "DESCRIPCION", length = 500)
    private String descripcion;
}
