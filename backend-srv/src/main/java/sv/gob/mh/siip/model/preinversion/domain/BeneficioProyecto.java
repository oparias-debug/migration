package sv.gob.mh.siip.model.preinversion.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Detalle de un beneficio capturado en CU-PRE-20. */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficioProyecto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) private Proyecto proyecto;
    private String tipoBeneficio;
    private String nombreBeneficio;
    private String parametro;
    private String tipoIngreso;
    private Double montoPeriodo1;
    private Double tasaCrecimientoProyectado;
    private Double factorCorreccion;
    @ElementCollection @Builder.Default private List<Double> montosPrecioMercadoPorPeriodo = new ArrayList<>();
}
