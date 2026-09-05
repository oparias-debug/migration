package sv.gob.mh.siip.model.preinversion.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import sv.gob.mh.siip.model.common.domain.Auditable;
import sv.gob.mh.siip.model.preinversion.enums.FuenteFinanciamiento;

/**
 * Ficha de proyectos de emergencia (Anexo A.4), 1:1 con {@link Proyecto}. Solo aplica a proyectos
 * con {@code esProyectoEmergencia = true} (CU-PRE-01). CU-PRE-3.5.
 */
@Entity
@Table(name = "FICHA_EMERGENCIA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false, of = "id")
public class FichaEmergencia extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ficha_emergencia_seq")
    @SequenceGenerator(name = "ficha_emergencia_seq", sequenceName = "FICHA_EMERGENCIA_SEQ", allocationSize = 1)
    @Column(name = "ID_FICHA_EMERGENCIA")
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false, unique = true)
    private Proyecto proyecto;

    @Column(name = "PLANTEAMIENTO_PROBLEMA", length = 2000)
    private String planteamientoProblema;

    @Column(name = "OBJETIVO_GENERAL", length = 2000)
    private String objetivoGeneral;

    @Column(name = "DESCRIPCION_PROYECTO", length = 2000)
    private String descripcionProyecto;

    /** Código de producto de {@code GET /catalogos/productos-indicadores} seleccionado en el formulario. */
    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "FICHA_EMERGENCIA_PRODUCTO", joinColumns = @JoinColumn(name = "ID_FICHA_EMERGENCIA"))
    @Column(name = "CODIGO_PRODUCTO", length = 30)
    private List<String> productos = new ArrayList<>();

    /** Tomado de {@code GET /catalogos/ubicaciones-geograficas}; "Departamento" se deriva del distrito. */
    @Column(name = "DISTRITO", length = 100)
    private String distrito;

    @Column(name = "LATITUD")
    private Double latitud;

    @Column(name = "LONGITUD")
    private Double longitud;

    @Column(name = "DIRECCION_ESPECIFICA", length = 500)
    private String direccionEspecifica;

    @Column(name = "POBLACION_OBJETIVO", length = 500)
    private String poblacionObjetivo;

    @Column(name = "INVERSION_ESTIMADA")
    private Double inversionEstimada;

    @Column(name = "ARCHIVO_PRESUPUESTO_URL", length = 500)
    private String archivoPresupuestoUrl;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "FICHA_EMERGENCIA_COMPONENTE_COSTO", joinColumns = @JoinColumn(name = "ID_FICHA_EMERGENCIA"))
    private List<ComponenteCostoEmergencia> componentesCosto = new ArrayList<>();

    @Column(name = "COSTOS_OPERACION")
    private Double costosOperacion;

    @Column(name = "COSTOS_MANTENIMIENTO")
    private Double costosMantenimiento;

    /** Catálogo cerrado de 7 valores definido en CU-PRE-17.openapi.yaml, reutilizado aquí. */
    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "FICHA_EMERGENCIA_FUENTE_FINANC", joinColumns = @JoinColumn(name = "ID_FICHA_EMERGENCIA"))
    @Enumerated(EnumType.STRING)
    @Column(name = "CODIGO_FUENTE", length = 20)
    private List<FuenteFinanciamiento> fuentesFinanciamiento = new ArrayList<>();

    /**
     * "Fuente de Recursos" — catálogo pendiente de definición del negocio (ver
     * CU-PRE-03.5.openapi.yaml y CU-PRE-17.openapi.yaml); se mantiene como texto libre.
     */
    @Column(name = "FUENTE_RECURSOS", length = 200)
    private String fuenteRecursos;

    @Column(name = "ARCHIVO_PROGRAMACION_URL", length = 500)
    private String archivoProgramacionUrl;
}
