package sv.gob.mh.siip.model.preinversion.domain;

import sv.gob.mh.siip.model.preinversion.enums.TipoMedidaCatalogo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Entrada del catalogo de medidas de GRD/GRC/ACC (Anexos C.1, C.1.5, C.2), mostrado en la
 * ventana emergente del boton "Ver descripción de categorías" de CU-PRE-01. Catalogo de solo
 * lectura desde la pantalla "Nuevo registro"; los campos medidasGrd/medidasGrc/medidasAcc de
 * Proyecto solo guardan el codigo seleccionado, sin FK contra esta tabla.
 */
@Entity
@Table(name = "MEDIDA_CATALOGO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class MedidaCatalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "medida_catalogo_seq")
    @SequenceGenerator(name = "medida_catalogo_seq", sequenceName = "MEDIDA_CATALOGO_SEQ", allocationSize = 1)
    @Column(name = "ID_MEDIDA_CATALOGO")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 10)
    private TipoMedidaCatalogo tipo;

    @NotBlank
    @Column(name = "CODIGO", nullable = false, length = 50)
    private String codigo;

    @NotBlank
    @Column(name = "DESCRIPCION", nullable = false, length = 500)
    private String descripcion;
}
