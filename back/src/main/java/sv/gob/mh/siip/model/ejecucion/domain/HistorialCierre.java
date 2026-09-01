package sv.gob.mh.siip.model.ejecucion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

import sv.gob.mh.siip.model.common.domain.Usuario;

/** Trazabilidad cronologica de todos los ajustes realizados durante el cierre del proyecto. CU-EJE-07. */
@Entity
@Table(name = "HISTORIAL_CIERRE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class HistorialCierre {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "historial_cierre_seq")
    @SequenceGenerator(name = "historial_cierre_seq", sequenceName = "HISTORIAL_CIERRE_SEQ", allocationSize = 1)
    @Column(name = "ID_HISTORIAL_CIERRE")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CIERRE_PROYECTO", nullable = false)
    private CierreProyecto cierreProyecto;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private Usuario usuario;

    @NotNull
    @Column(name = "FECHA_HORA", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "OBSERVACIONES", length = 2000)
    private String observaciones;
}
