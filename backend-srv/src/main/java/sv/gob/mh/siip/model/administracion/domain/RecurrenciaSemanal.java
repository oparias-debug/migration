package sv.gob.mh.siip.model.administracion.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumSet;
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

/** Recurrencia SEMANAL: rango entre fechaInicio y fechaFin restringido a ciertos dias de la semana (CU-ADM-04). */
@Entity
@Table(name = "RECURRENCIA_SEMANAL")
@PrimaryKeyJoinColumn(name = "ID_RECURRENCIA")
@DiscriminatorValue("SEMANAL")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class RecurrenciaSemanal extends Recurrencia {

    @Column(name = "FECHA_INICIO", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "FECHA_FIN", nullable = false)
    private LocalDate fechaFin;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "RECURRENCIA_SEMANAL_DIA", joinColumns = @JoinColumn(name = "RECURRENCIA_ID"))
    @Enumerated(EnumType.STRING)
    @Column(name = "DIA_SEMANA", nullable = false, length = 15)
    private Set<DayOfWeek> diasDeLaSemana = EnumSet.noneOf(DayOfWeek.class);
}
