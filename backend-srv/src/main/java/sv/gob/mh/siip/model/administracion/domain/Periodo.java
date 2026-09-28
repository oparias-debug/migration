package sv.gob.mh.siip.model.administracion.domain;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.administracion.enums.TipoPeriodo;

/** Periodo LABORAL o NO_LABORAL de un {@link Calendario}, definido mediante una {@link Recurrencia} (CU-ADM-04). */
@Entity
@Table(name = "PERIODO", uniqueConstraints = @UniqueConstraint(name = "UK_PERIODO_CALENDARIO_CODIGO",
        columnNames = { "CALENDARIO_ID", "CODIGO" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Periodo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "periodo_seq")
    @SequenceGenerator(name = "periodo_seq", sequenceName = "PERIODO_SEQ", allocationSize = 1)
    @Column(name = "ID_PERIODO")
    private Long id;

    /** Codigo del periodo, unico dentro del calendario (RN15). */
    @Column(name = "CODIGO", nullable = false, length = 100)
    private String codigo;

    @Column(name = "NOMBRE", nullable = false, length = 300)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 20)
    private TipoPeriodo tipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CALENDARIO_ID", nullable = false)
    private Calendario calendario;

    /**
     * EAGER deliberado (no LAZY): Recurrencia es una jerarquia JOINED (RecurrenciaUnaVez/Semanal/
     * Mensual) y CalendarioServiceImpl distingue el subtipo concreto con "instanceof" directo sobre
     * este campo (ver perteneceARecurrencia/calcularDuracionDias/finDelPeriodo/aRecurrenciaDto). Un
     * proxy Hibernate LAZY sobre una asociacion polimorfica no resuelve el subtipo real sin forzar
     * antes su inicializacion, por lo que esos "instanceof" fallan silenciosamente (retornan false)
     * cuando el Periodo se relee en una consulta separada de aquella que lo creo, rompiendo RN02,
     * RN04, RN05, RN09 y RN11 en cualquier lectura posterior a la creacion.
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "RECURRENCIA_ID", nullable = false, unique = true)
    private Recurrencia recurrencia;
}
