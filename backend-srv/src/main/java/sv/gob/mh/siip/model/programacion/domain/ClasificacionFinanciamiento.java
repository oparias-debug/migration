package sv.gob.mh.siip.model.programacion.domain;

import sv.gob.mh.siip.model.programacion.enums.TipoClasificacionFinanciamiento;

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
