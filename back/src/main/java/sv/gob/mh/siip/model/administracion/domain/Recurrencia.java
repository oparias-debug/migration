package sv.gob.mh.siip.model.administracion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Recurrencia que define el rango efectivo de un {@link Periodo} (CU-ADM-04, RN08-RN11):
 * {@link RecurrenciaUnaVez}, {@link RecurrenciaSemanal} o {@link RecurrenciaMensual}.
 */
@Entity
@Table(name = "RECURRENCIA")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "TIPO_RECURRENCIA", discriminatorType = DiscriminatorType.STRING, length = 20)
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public abstract class Recurrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recurrencia_seq")
    @SequenceGenerator(name = "recurrencia_seq", sequenceName = "RECURRENCIA_SEQ", allocationSize = 1)
    @Column(name = "ID_RECURRENCIA")
    private Long id;
}
