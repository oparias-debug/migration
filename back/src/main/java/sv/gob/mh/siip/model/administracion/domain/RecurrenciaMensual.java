package sv.gob.mh.siip.model.administracion.domain;

import java.time.Month;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Recurrencia MENSUAL: conjunto de dias del mes aplicado sobre un conjunto de meses (CU-ADM-04, RN11). */
@Entity
@Table(name = "RECURRENCIA_MENSUAL")
@PrimaryKeyJoinColumn(name = "ID_RECURRENCIA")
@DiscriminatorValue("MENSUAL")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class RecurrenciaMensual extends Recurrencia {

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "RECURRENCIA_MENSUAL_DIA", joinColumns = @JoinColumn(name = "RECURRENCIA_ID"))
    @Column(name = "DIA_MES", nullable = false)
    private Set<Integer> diasDelMes = new LinkedHashSet<>();

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "RECURRENCIA_MENSUAL_MES", joinColumns = @JoinColumn(name = "RECURRENCIA_ID"))
    @Enumerated(EnumType.STRING)
    @Column(name = "MES", nullable = false, length = 10)
    private Set<Month> meses = EnumSet.noneOf(Month.class);
}
