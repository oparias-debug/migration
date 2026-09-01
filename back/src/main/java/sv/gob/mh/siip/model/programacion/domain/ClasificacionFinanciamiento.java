package sv.gob.mh.siip.model.programacion.domain;

import sv.gob.mh.siip.model.programacion.enums.TipoClasificacionFinanciamiento;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;

/** Clasificacion de financiamiento de una etapa del proyecto dentro del PRIPME. CU-PRO-01. */
@Entity
@Table(name = "CLASIFICACION_FINANCIAMIENTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ClasificacionFinanciamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clasificacion_fin_seq")
    @SequenceGenerator(name = "clasificacion_fin_seq", sequenceName = "CLASIFICACION_FIN_SEQ", allocationSize = 1)
    @Column(name = "ID_CLASIFICACION_FIN")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PRIPME", nullable = false)
    private Pripme pripme;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ETAPA_PREINVERSION")
    private EtapaPreinversion etapaPreinversion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_CLASIFICACION", nullable = false, length = 30)
    private TipoClasificacionFinanciamiento tipoClasificacion;
}
