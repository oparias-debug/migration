package sv.gob.mh.siip.model.ejecucion.domain;

import sv.gob.mh.siip.model.ejecucion.enums.TipoObservacionEjecucion;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.common.domain.Usuario;

import java.time.LocalDateTime;

/** Observacion del Tecnico SYMP sobre presupuesto/provision reportados. CU-EJE-01. */
@Entity
@Table(name = "OBSERVACION_EJECUCION_FINANCIERA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ObservacionEjecucionFinanciera {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "obs_ejec_fin_seq")
    @SequenceGenerator(name = "obs_ejec_fin_seq", sequenceName = "OBS_EJEC_FIN_SEQ", allocationSize = 1)
    @Column(name = "ID_OBSERVACION_EJEC_FIN")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_EJECUCION_FIN_MENSUAL", nullable = false)
    private EjecucionFinancieraMensual ejecucionFinancieraMensual;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_AUTOR", nullable = false)
    private Usuario autor;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_OBSERVACION", nullable = false, length = 20)
    private TipoObservacionEjecucion tipoObservacion;

    @NotBlank
    @Column(name = "TEXTO", nullable = false, length = 2000)
    private String texto;

    @NotNull
    @Column(name = "FECHA", nullable = false)
    private LocalDateTime fecha;
}
