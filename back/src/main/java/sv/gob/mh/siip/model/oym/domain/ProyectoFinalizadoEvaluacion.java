package sv.gob.mh.siip.model.oym.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;

import java.time.LocalDateTime;

/**
 * Seleccion de un proyecto finalizado para evaluacion ex post, y excepcion de bloqueo.
 * CU-OYM-01. RN08: una vez seleccionado y con evaluacion adjunta, el checkbox queda
 * bloqueado salvo autorizacion de excepcion por Jefatura/Subjefatura de la DGICP.
 */
@Entity
@Table(name = "PROYECTO_FINALIZADO_EVALUACION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ProyectoFinalizadoEvaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proy_fin_eval_seq")
    @SequenceGenerator(name = "proy_fin_eval_seq", sequenceName = "PROY_FIN_EVAL_SEQ", allocationSize = 1)
    @Column(name = "ID_PROY_FIN_EVAL")
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false, unique = true)
    private Proyecto proyecto;

    @NotNull
    @Column(name = "SELECCIONADO_EVALUACION", nullable = false)
    private Boolean seleccionadoEvaluacion;

    @Column(name = "FECHA_SELECCION")
    private LocalDateTime fechaSeleccion;

    /** Autorizacion de excepcion (nivel DGICP) para desmarcar la seleccion. RN08. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO_EXCEPCION")
    private Usuario usuarioExcepcion;

    @Column(name = "FECHA_EXCEPCION")
    private LocalDateTime fechaExcepcion;

    @Column(name = "MOTIVO_EXCEPCION", length = 2000)
    private String motivoExcepcion;

    // Los documentos de evaluacion ex post cuelgan de Proyecto (ver DocumentoEvaluacionExPost),
    // no de esta cabecera; se consultan via DocumentoEvaluacionExPostRepository.findByProyectoId(...).
}
