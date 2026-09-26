package sv.gob.mh.siip.model.programacion.domain;

import sv.gob.mh.siip.model.programacion.enums.TipoCambioComparativo;

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
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;

/** Clasificacion de cambios entre escenario base y contrapropuesta. CU-PRO-09, CU-PRO-11. */
@Entity
@Table(name = "COMPARATIVO_ESCENARIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ComparativoEscenario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comparativo_escenario_seq")
    @SequenceGenerator(
            name = "comparativo_escenario_seq",
            sequenceName = "COMPARATIVO_ESCENARIO_SEQ",
            allocationSize = 1)
    @Column(name = "ID_COMPARATIVO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CONTRAPROPUESTA", nullable = false)
    private ContrapropuestaInstitucional contrapropuesta;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_CAMBIO", nullable = false, length = 20)
    private TipoCambioComparativo tipoCambio;
}
