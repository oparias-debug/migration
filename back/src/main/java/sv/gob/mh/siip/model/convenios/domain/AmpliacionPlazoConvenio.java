package sv.gob.mh.siip.model.convenios.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Ampliacion de plazo del ultimo desembolso del convenio. CU-MPD-03. */
@Entity
@Table(name = "AMPLIACION_PLAZO_CONVENIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class AmpliacionPlazoConvenio {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ampliacion_plazo_seq")
    @SequenceGenerator(name = "ampliacion_plazo_seq", sequenceName = "AMPLIACION_PLAZO_SEQ", allocationSize = 1)
    @Column(name = "ID_AMPLIACION_PLAZO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CONVENIO", nullable = false)
    private Convenio convenio;

    @NotNull
    @Column(name = "FECHA_AMPLIACION_DESEMBOLSO", nullable = false)
    private LocalDate fechaAmpliacionDesembolso;

    @Column(name = "PERIODO_AJUSTADO_CALCULADO", length = 100)
    private String periodoAjustadoCalculado;

    @Column(name = "DOCUMENTO_AUTORIZACION", length = 500)
    private String documentoAutorizacion;

    @NotNull
    @Column(name = "FECHA_REGISTRO", nullable = false)
    private LocalDateTime fechaRegistro;
}
