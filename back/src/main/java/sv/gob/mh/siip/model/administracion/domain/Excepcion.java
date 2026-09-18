package sv.gob.mh.siip.model.administracion.domain;

import java.time.LocalDate;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.administracion.enums.TipoExcepcion;

/** Reclasificacion puntual de una fecha dentro de un {@link Calendario}, con prioridad sobre sus periodos (CU-ADM-04). */
@Entity
@Table(name = "EXCEPCION", uniqueConstraints = @UniqueConstraint(name = "UK_EXCEPCION_CALENDARIO_FECHA",
        columnNames = { "CALENDARIO_ID", "FECHA" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Excepcion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "excepcion_seq")
    @SequenceGenerator(name = "excepcion_seq", sequenceName = "EXCEPCION_SEQ", allocationSize = 1)
    @Column(name = "ID_EXCEPCION")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CALENDARIO_ID", nullable = false)
    private Calendario calendario;

    @Column(name = "FECHA", nullable = false)
    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 20)
    private TipoExcepcion tipo;

    @Column(name = "DESCRIPCION", length = 500)
    private String descripcion;
}
