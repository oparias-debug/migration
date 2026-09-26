package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Campos comunes de los catálogos administrados con Factor de Corrección (CU-ADM-02): código,
 * nombre y factor. Ver {@link Parametro} (CU-PRE-20) e {@link InsumoTipo} (CU-PRE-17/18).
 */
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder(builderMethodName = "")
public class CatalogoConFactorCorreccion {

    @NotBlank
    @Column(name = "CODIGO", nullable = false, length = 100, unique = true)
    private String codigo;

    @NotBlank
    @Column(name = "NOMBRE", nullable = false, length = 250)
    private String nombre;

    @NotNull
    @Column(name = "FACTOR_CORRECCION", nullable = false)
    private Double factorCorreccion;
}
