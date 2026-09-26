package sv.gob.mh.siip.model.programacion.domain;

import sv.gob.mh.siip.model.programacion.enums.EstadoEscenario;

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
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.common.domain.Institucion;

import java.time.LocalDateTime;

/** Escenario unico de techo institucional de corto plazo. CU-PRO-08, CU-PRO-09. */
@Entity
@Table(name = "ESCENARIO_CORTO_PLAZO",
       uniqueConstraints = @UniqueConstraint(
               name = "UK_ESCENARIO_CP_INST_ANIO",
               columnNames = {"ID_INSTITUCION", "ANIO"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class EscenarioCortoPlazo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "escenario_cp_seq")
    @SequenceGenerator(name = "escenario_cp_seq", sequenceName = "ESCENARIO_CP_SEQ", allocationSize = 1)
    @Column(name = "ID_ESCENARIO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_INSTITUCION", nullable = false)
    private Institucion institucion;

    @NotNull
    @Column(name = "ANIO", nullable = false)
    private Integer anio;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 30)
    private EstadoEscenario estado;

    @NotNull
    @Column(name = "FECHA_GENERACION", nullable = false)
    private LocalDateTime fechaGeneracion;

    @Column(name = "FECHA_ENVIO_REVISION")
    private LocalDateTime fechaEnvioRevision;

    @Column(name = "FECHA_VALIDACION")
    private LocalDateTime fechaValidacion;
}
