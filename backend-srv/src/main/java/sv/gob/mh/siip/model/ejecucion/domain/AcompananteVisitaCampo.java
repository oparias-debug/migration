package sv.gob.mh.siip.model.ejecucion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Persona que acompano la visita de campo. CU-EJE-06. */
@Entity
@Table(name = "ACOMPANANTE_VISITA_CAMPO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class AcompananteVisitaCampo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "acompanante_visita_seq")
    @SequenceGenerator(name = "acompanante_visita_seq", sequenceName = "ACOMPANANTE_VISITA_SEQ", allocationSize = 1)
    @Column(name = "ID_ACOMPANANTE")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_INFORME_VISITA", nullable = false)
    private InformeVisitaCampo informeVisitaCampo;

    @Column(name = "INSTITUCION", length = 250)
    private String institucion;

    @NotBlank
    @Column(name = "NOMBRE", nullable = false, length = 250)
    private String nombre;

    @Column(name = "CARGO", length = 150)
    private String cargo;
}
