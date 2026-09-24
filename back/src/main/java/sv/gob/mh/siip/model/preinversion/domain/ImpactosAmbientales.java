package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import lombok.*;
import sv.gob.mh.siip.model.preinversion.enums.CategoriaAmbiental;

@Entity
@Table(name = "impactos_ambientales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImpactosAmbientales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el Padre (AnalisisAmbiental)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_analisis_ambiental", nullable = false)
    private AnalisisAmbiental analisisAmbiental;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria_ambiental", nullable = true)
    private CategoriaAmbiental categoriaAmbiental;

    @Column(name = "medio", nullable = true)
    private String medio;

    @Column(name = "impacto", length = 500, nullable = true)
    private String impacto;

    @Column(name = "tipo_impacto", nullable = true)
    private String tipoImpacto;

    @Column(name = "magnitud", nullable = true)
    private String magnitud;

    @Column(name = "duracion", nullable = true)
    private String duracion;

    @Column(name = "reversibilidad", nullable = true)
    private String reversibilidad;

    @Column(name = "medida_gestion", length = 1000, nullable = true)
    private String medidaGestion;

    @Column(name = "costo_medida_gestion", nullable = true)
    private Double costoMedidaGestion;

    @Column(name = "permisos_requeridos", length = 500, nullable = true)
    private String permisosRequeridos;

    @Column(name = "observaciones", length = 1000, nullable = true)
    private String observaciones;
}