package sv.gob.mh.siip.model.convenios.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.convenios.enums.EstadoCondicionPrevia;

/** Condicion previa de desembolso del convenio, con numeracion correlativa. CU-MPD-01, CU-MPD-04. */
@Entity
@Table(name = "CONDICION_PREVIA", uniqueConstraints = @UniqueConstraint(name = "UK_CONDICION_PREVIA_NUM", columnNames = {"ID_CONVENIO", "NUMERO"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class CondicionPrevia {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "condicion_previa_seq")
    @SequenceGenerator(name = "condicion_previa_seq", sequenceName = "CONDICION_PREVIA_SEQ", allocationSize = 1)
    @Column(name = "ID_CONDICION_PREVIA")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CONVENIO", nullable = false)
    private Convenio convenio;

    @NotNull
    @Column(name = "NUMERO", nullable = false)
    private Integer numero;

    @NotBlank
    @Column(name = "DESCRIPCION", nullable = false, length = 2000)
    private String descripcion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoCondicionPrevia estado;

    @Column(name = "FECHA_CUMPLIMIENTO")
    private LocalDate fechaCumplimiento;

    @Column(name = "COMENTARIOS", length = 2000)
    private String comentarios;

    @NotNull
    @Column(name = "FINALIZADA", nullable = false)
    private Boolean finalizada;
}
