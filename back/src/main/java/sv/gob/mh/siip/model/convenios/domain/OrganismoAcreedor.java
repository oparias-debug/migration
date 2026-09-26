package sv.gob.mh.siip.model.convenios.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Catalogo de organismos acreedores. CU-MPD-01. */
@Entity
@Table(name = "ORGANISMO_ACREEDOR")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class OrganismoAcreedor {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organismo_acreedor_seq")
    @SequenceGenerator(name = "organismo_acreedor_seq", sequenceName = "ORGANISMO_ACREEDOR_SEQ", allocationSize = 1)
    @Column(name = "ID_ORGANISMO_ACREEDOR")
    private Long id;

    @Column(name = "CODIGO", nullable = false, length = 20, unique = true)
    private String codigo;

    @Column(name = "NOMBRE", nullable = false, length = 250)
    private String nombre;

    @Column(name = "ACTIVO", nullable = false)
    private Boolean activo;
}
