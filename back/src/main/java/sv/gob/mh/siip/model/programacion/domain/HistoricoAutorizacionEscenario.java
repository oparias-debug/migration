package sv.gob.mh.siip.model.programacion.domain;

import sv.gob.mh.siip.model.programacion.enums.EtapaAutorizacionEscenario;

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
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @SequenceGenerator(
            name = "hist_autoriz_escenario_seq",
            sequenceName = "HIST_AUTORIZ_ESCENARIO_SEQ",
            allocationSize = 1)
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
