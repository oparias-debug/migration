package sv.gob.mh.siip.model.preinversion.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Campos comunes de las habilitaciones de modificación fuera del período de elaboración, por
 * Unidad Ejecutora y año. Ver {@link HabilitacionModificacionPap} (CU-PRE-30) y
 * {@link HabilitacionModificacionMetasPap} (CU-PRE-31).
 */
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder(builderMethodName = "")
public class HabilitacionModificacionPapBase {

    @NotNull
    @Column(name = "ID_UNIDAD_EJECUTORA", nullable = false)
    private Long idUnidadEjecutora;

    @NotNull
    @Column(name = "ANIO", nullable = false)
    private Integer anio;

    @NotNull
    @Column(name = "FECHA_HABILITACION", nullable = false)
    private LocalDateTime fechaHabilitacion;
}
