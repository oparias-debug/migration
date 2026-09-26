package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.preinversion.enums.CampoFichaViabilidad;

/**
 * Celda de la columna "COMENTARIOS DEL VIABILIZADOR" de la tabla "FICHA DEL PROYECTO" (CU-PRE-24,
 * Anexo A.1; FA01 pasos 1.1–1.2): el comentario que el Viabilizador registró sobre un campo.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ComentarioCampoViabilidad {

    @Enumerated(EnumType.STRING)
    @Column(name = "CAMPO", nullable = false, length = 40)
    private CampoFichaViabilidad campo;

    @Column(name = "COMENTARIO", nullable = false, length = 2000)
    private String comentario;
}
