package sv.gob.mh.siip.model.preinversion.domain;

import sv.gob.mh.siip.model.preinversion.enums.NivelInfluencia;
import sv.gob.mh.siip.model.preinversion.enums.NivelInteres;
import sv.gob.mh.siip.model.preinversion.enums.TipoInteresado;

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

/** Fila de la "Matriz de gestion de interesados" (Anexo A.1). CU-PRE-06. */
@Entity
@Table(name = "INTERESADO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Interesado {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "interesado_seq")
    @SequenceGenerator(name = "interesado_seq", sequenceName = "INTERESADO_SEQ", allocationSize = 1)
    @Column(name = "ID_INTERESADO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    /** Ninguno de los campos de fila es obligatorio a nivel de servidor (CU-PRE-06, RN06). */
    @Column(name = "NOMBRE_INTERESADO", length = 300)
    private String nombreInteresado;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", length = 20)
    private TipoInteresado tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "NIVEL_INFLUENCIA", length = 10)
    private NivelInfluencia nivelInfluencia;

    @Enumerated(EnumType.STRING)
    @Column(name = "NIVEL_INTERES", length = 10)
    private NivelInteres nivelInteres;

    @Column(name = "ESTRATEGIA_GESTION")
    private String estrategiaGestion;

    /** Preserva el orden de las filas entre guardados (mismo criterio que AlternativaSolucion). */
    @Column(name = "ORDEN")
    private Integer orden;
}
