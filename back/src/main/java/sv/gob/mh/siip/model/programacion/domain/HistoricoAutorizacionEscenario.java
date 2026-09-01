package sv.gob.mh.siip.model.programacion.domain;

import sv.gob.mh.siip.model.programacion.enums.EtapaAutorizacionEscenario;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/** Historico de etapas de autorizacion de un escenario. CU-PRO-10. */
@Entity
@Table(name = "HISTORICO_AUTORIZACION_ESCENARIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class HistoricoAutorizacionEscenario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hist_autoriz_escenario_seq")
    @SequenceGenerator(name = "hist_autoriz_escenario_seq", sequenceName = "HIST_AUTORIZ_ESCENARIO_SEQ", allocationSize = 1)
    @Column(name = "ID_HISTORICO_AUTORIZACION")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_ESCENARIO", nullable = false)
    private EscenarioCortoPlazo escenario;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ETAPA_AUTORIZACION", nullable = false, length = 30)
    private EtapaAutorizacionEscenario etapaAutorizacion;

    @NotNull
    @Column(name = "FECHA", nullable = false)
    private LocalDateTime fecha;
}
