package sv.gob.mh.siip.model.preinversion.domain;

import sv.gob.mh.siip.model.preinversion.enums.PosicionInteresado;
import sv.gob.mh.siip.model.preinversion.enums.TipoInteresado;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/** Analisis de interesados del proyecto. CU-PRE-06. */
@Entity
@Table(name = "INTERESADO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Interesado {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "interesado_seq")
    @SequenceGenerator(name = "interesado_seq", sequenceName = "INTERESADO_SEQ", allocationSize = 1)
    @Column(name = "ID_INTERESADO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotBlank
    @Column(name = "NOMBRE", nullable = false, length = 250)
    private String nombre;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_INTERESADO", nullable = false, length = 30)
    private TipoInteresado tipoInteresado;

    @Column(name = "ROL_INTERES", length = 500)
    private String rolInteres;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "POSICION", nullable = false, length = 20)
    private PosicionInteresado posicion;
}
