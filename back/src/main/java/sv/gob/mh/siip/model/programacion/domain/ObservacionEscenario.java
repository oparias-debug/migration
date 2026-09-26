package sv.gob.mh.siip.model.programacion.domain;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.common.domain.Usuario;

import java.time.LocalDateTime;

/** Retroalimentacion del Jefe/Subjefe DGI sobre el escenario. CU-PRO-09. */
@Entity
@Table(name = "OBSERVACION_ESCENARIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ObservacionEscenario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "observacion_escenario_seq")
    @SequenceGenerator(
            name = "observacion_escenario_seq",
            sequenceName = "OBSERVACION_ESCENARIO_SEQ",
            allocationSize = 1)
    @Column(name = "ID_OBSERVACION_ESCENARIO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_ESCENARIO", nullable = false)
    private EscenarioCortoPlazo escenario;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_AUTOR", nullable = false)
    private Usuario autor;

    @NotBlank
    @Column(name = "TEXTO", nullable = false, length = 2000)
    private String texto;

    @NotNull
    @Column(name = "FECHA_OBSERVACION", nullable = false)
    private LocalDateTime fechaObservacion;
}
