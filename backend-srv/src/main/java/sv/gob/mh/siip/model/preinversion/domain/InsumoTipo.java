package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Catálogo "Insumo Tipo" con Factor de Corrección (CU-ADM-02), usado por CU-PRE-17/18 para
 * calcular precios sociales a partir de precios de mercado. El propio CU aclara: "Este catálogo
 * estará sujeto a actualización por parte de la DGICP" — administrado, sin código corto oficial
 * documentado, por lo que el nombre hace de código (mismo criterio que EjeTematico/Anexo C.6).
 */
@Entity
@Table(name = "INSUMO_TIPO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = false, of = "id")
public class InsumoTipo extends CatalogoConFactorCorreccion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "insumo_tipo_seq")
    @SequenceGenerator(name = "insumo_tipo_seq", sequenceName = "INSUMO_TIPO_SEQ", allocationSize = 1)
    @Column(name = "ID_INSUMO_TIPO")
    private Long id;
}
