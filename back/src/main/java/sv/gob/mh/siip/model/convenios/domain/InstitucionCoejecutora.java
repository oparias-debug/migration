package sv.gob.mh.siip.model.convenios.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.common.domain.Institucion;

/** Institucion coejecutora adicional de un convenio (N:M). CU-MPD-01, RN09. */
@Entity
@Table(name = "INSTITUCION_COEJECUTORA", uniqueConstraints = @UniqueConstraint(name = "UK_INST_COEJECUTORA", columnNames = {"ID_CONVENIO", "ID_INSTITUCION"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class InstitucionCoejecutora {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inst_coejecutora_seq")
    @SequenceGenerator(name = "inst_coejecutora_seq", sequenceName = "INST_COEJECUTORA_SEQ", allocationSize = 1)
    @Column(name = "ID_INST_COEJECUTORA")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CONVENIO", nullable = false)
    private Convenio convenio;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_INSTITUCION", nullable = false)
    private Institucion institucion;
}
