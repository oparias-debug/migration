package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Datos de emergencia de la pantalla "Nuevo registro", embebidos en la tabla PROYECTO. */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class ProyectoEmergencia {

    /** Campo "Proyecto de emergencia". */
    @Column(name = "ES_PROYECTO_EMERGENCIA")
    private Boolean esProyectoEmergencia;

    /** Obligatorio si esProyectoEmergencia = true. */
    @Column(name = "TIPO_EVENTO", length = 100)
    private String tipoEvento;

    /** Campo "N° de DL". Obligatorio si esProyectoEmergencia = true. */
    @Column(name = "NUMERO_DECRETO_LEGISLATIVO", length = 50)
    private String numeroDecretoLegislativo;
}
