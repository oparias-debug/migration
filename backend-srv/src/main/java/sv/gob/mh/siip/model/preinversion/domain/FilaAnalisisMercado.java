package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilaAnalisisMercado {

    @Column(name = "CODIGO_PRODUCTO", length = 30)
    private String codigoProducto;

    @Column(name = "DEMANDA")
    private Double demanda;

    @Column(name = "OFERTA")
    private Double oferta;

    @Column(name = "ANIOS_A_PROYECTAR")
    private Integer aniosAProyectar;

    @Column(name = "TASA_DEMANDA")
    private Double tasaDemanda;

    @Column(name = "TASA_OFERTA")
    private Double tasaOferta;

    @Column(name = "PRODUCTO", length = 300)
    private String producto;

    @Column(name = "UNIDAD_MEDIDA", length = 100)
    private String unidadMedida;
}
