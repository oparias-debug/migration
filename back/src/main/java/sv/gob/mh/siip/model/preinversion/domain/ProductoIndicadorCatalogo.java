package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entrada del catálogo de Productos e Indicadores (Anexo C.6), fuente del listado seleccionable
 * de "Producto" en la Ficha de proyectos de emergencia. Sin datos oficiales en el repositorio
 * (tabla vacía; ver {@code schema_preinversion.sql}). CU-PRE-3.5.
 */
@Entity
@Table(name = "PRODUCTO_INDICADOR")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ProductoIndicadorCatalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "producto_indicador_seq")
    @SequenceGenerator(name = "producto_indicador_seq", sequenceName = "PRODUCTO_INDICADOR_SEQ", allocationSize = 1)
    @Column(name = "ID_PRODUCTO_INDICADOR")
    private Long id;

    @NotBlank
    @Column(name = "CODIGO_PRODUCTO", nullable = false, length = 30)
    private String codigoProducto;

    @NotBlank
    @Column(name = "PRODUCTO", nullable = false, length = 300)
    private String producto;

    @Column(name = "DESCRIPCION_PRODUCTO", length = 1000)
    private String descripcionProducto;

    @NotBlank
    @Column(name = "CODIGO_INDICADOR", nullable = false, length = 30)
    private String codigoIndicador;

    @NotBlank
    @Column(name = "INDICADOR", nullable = false, length = 300)
    private String indicador;

    @NotBlank
    @Column(name = "UNIDAD_MEDIDA", nullable = false, length = 100)
    private String unidadMedida;

    @NotNull
    @Column(name = "ES_INDICADOR_PRINCIPAL", nullable = false)
    private Boolean esIndicadorPrincipal;
}
