package sv.gob.mh.siip.model.administracion.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/** Recurrencia UNA_VEZ: rango continuo entre fechaInicio y fechaFin (CU-ADM-04). */
@Entity
@Table(name = "RECURRENCIA_UNA_VEZ")
@PrimaryKeyJoinColumn(name = "ID_RECURRENCIA")
@DiscriminatorValue("UNA_VEZ")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class RecurrenciaUnaVez extends Recurrencia {

    @Column(name = "FECHA_INICIO", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "FECHA_FIN", nullable = false)
    private LocalDate fechaFin;
}
