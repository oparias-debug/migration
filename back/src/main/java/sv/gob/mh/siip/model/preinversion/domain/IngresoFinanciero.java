package sv.gob.mh.siip.model.preinversion.domain;
import java.util.*;
import jakarta.persistence.*;
import lombok.*;
@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IngresoFinanciero {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false) private Proyecto proyecto;
 private String nombreIngreso;
 @ElementCollection @Builder.Default private List<Double> montosPorPeriodo=new ArrayList<>();
}
