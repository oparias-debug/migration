package sv.gob.mh.siip.model.preinversion.domain;

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
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "OBJETIVO_ESPECIFICO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class ObjetivoEspecifico {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "objetivo_especifico_seq")
    @SequenceGenerator(name = "objetivo_especifico_seq", sequenceName = "OBJETIVO_ESPECIFICO_SEQ", allocationSize = 1)
    @Column(name = "ID_OBJETIVO_ESPECIFICO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_IDENTIFICACION", nullable = false)
    private Identificacion identificacion;

    /** RNC: hasta 500 caracteres (CU-PRE-04); no es obligatorio a nivel de servidor. */
    @Column(name = "DESCRIPCION", length = 500)
    private String descripcion;

    @Column(name = "ORDEN")
    private Integer orden;
}
