package sv.gob.mh.siip.model.convenios.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;

import java.math.BigDecimal;

/** Proyecto vinculado al convenio, con monto comprometido y bandera de Programa Principal. CU-MPD-04. */
@Entity
@Table(name = "PROYECTO_CONVENIO", uniqueConstraints = @UniqueConstraint(name = "UK_PROYECTO_CONVENIO", columnNames = {"ID_CONVENIO", "ID_PROYECTO"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ProyectoConvenio {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyecto_convenio_seq")
    @SequenceGenerator(name = "proyecto_convenio_seq", sequenceName = "PROYECTO_CONVENIO_SEQ", allocationSize = 1)
    @Column(name = "ID_PROYECTO_CONVENIO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CONVENIO", nullable = false)
    private Convenio convenio;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotNull
    @Column(name = "MONTO_COMPROMETIDO", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoComprometido;

    @NotNull
    @Column(name = "ES_PROGRAMA_PRINCIPAL", nullable = false)
    private Boolean esProgramaPrincipal;
}
