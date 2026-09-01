package sv.gob.mh.siip.model.preinversion.domain;

import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/** Etapa de preinversion seleccionada para el proyecto. CU-PRE-3.5. */
@Entity
@Table(name = "ETAPA_PREINVERSION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class EtapaPreinversion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "etapa_preinversion_seq")
    @SequenceGenerator(name = "etapa_preinversion_seq", sequenceName = "ETAPA_PREINVERSION_SEQ", allocationSize = 1)
    @Column(name = "ID_ETAPA_PREINVERSION")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_ETAPA", nullable = false, length = 30)
    private TipoEtapaPreinversion tipoEtapa;

    @NotNull
    @Column(name = "FECHA_SELECCION", nullable = false)
    private LocalDateTime fechaSeleccion;

    @Column(name = "JUSTIFICACION", length = 2000)
    private String justificacion;

    @Column(name = "FECHA_CREACION")
    private LocalDateTime fechaCreacion;

    @Column(name = "USUARIO_CREACION", length = 100)
    private String usuarioCreacion;
}
