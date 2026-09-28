package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Descripcion tecnica del proyecto. CU-PRE-11. */
@Entity
@Table(name = "DESCRIPCION_TECNICA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class DescripcionTecnica {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "descripcion_tecnica_seq")
    @SequenceGenerator(name = "descripcion_tecnica_seq", sequenceName = "DESCRIPCION_TECNICA_SEQ", allocationSize = 1)
    @Column(name = "ID_DESCRIPCION_TECNICA")
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false, unique = true)
    private Proyecto proyecto;

    @NotNull
    
    @Column(name = "DESCRIPCION", nullable = false)
    private String descripcion;

    
    @Column(name = "ESPECIFICACIONES")
    private String especificaciones;

    @Column(name = "VIDA_UTIL_ANIOS")
    private Integer vidaUtilAnios;
}
