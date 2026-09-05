package sv.gob.mh.siip.model.preinversion.domain;

import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.common.domain.Auditable;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad raiz del ciclo de inversion publica: proyecto, programa o estudio general.
 * CU-PRE-01 (Registro y Solicitud de CUP), CU-PRE-01.5 (Revision y Emision de CUP),
 * CU-PRE-02 (Bandeja de Preinversion), CU-PRE-03 (Captura de Proyectos).
 */
@Entity
@Table(name = "PROYECTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false, of = "id")
public class Proyecto extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proyecto_seq")
    @SequenceGenerator(name = "proyecto_seq", sequenceName = "PROYECTO_SEQ", allocationSize = 1)
    @Column(name = "ID_PROYECTO")
    private Long id;

    /** Codigo Unico de Proyecto, 5 digitos, asignado en CU-PRE-01.5. Nulo mientras esta en tramite. */
    @Column(name = "CUP", length = 5, unique = true)
    private String cup;

    @NotBlank
    @Column(name = "NOMBRE", nullable = false, length = 300)
    private String nombre;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "INICIATIVA_INVERSION", nullable = false, length = 20)
    private IniciativaInversion iniciativaInversion;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_UNIDAD_EJECUTORA", nullable = false)
    private UnidadEjecutora unidadEjecutora;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_INSTITUCION", nullable = false)
    private Institucion institucion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 40)
    private EstadoProyecto estado;

    @NotNull
    @Column(name = "FECHA_INGRESO", nullable = false)
    private LocalDateTime fechaIngreso;

    @Column(name = "FECHA_CUP_ASIGNADO")
    private LocalDateTime fechaCupAsignado;

    @Column(name = "ACTIVO", nullable = false)
    private Boolean activo;

    /** Campo "Monto Estimado de Inversión" de la pantalla "Nuevo registro". */
    @NotNull
    @Column(name = "MONTO_ESTIMADO_INVERSION", nullable = false)
    private Double montoEstimadoInversion;

    /**
     * Sector seleccionado (catálogo Anexo C.5, "Macrosectores y sectores"). Reutiliza el
     * catálogo DGICP ya modelado por el módulo programacion (SectorActividad -&gt; MacroSector);
     * el Macrosector se deriva de este Sector y no se guarda por separado en Proyecto.
     */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_SECTOR", nullable = false)
    private SectorActividad sector;

    /** Eje temático seleccionado (catálogo Anexo C.6). */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_EJE_TEMATICO", nullable = false)
    private EjeTematico ejeTematico;

    /** Categorías de GRD seleccionadas (catálogo Anexo C.1). Condicional. */
    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "PROYECTO_MEDIDA_GRD", joinColumns = @JoinColumn(name = "ID_PROYECTO"))
    @Column(name = "CODIGO", length = 50)
    private List<String> medidasGrd = new ArrayList<>();

    /** Categorías de GRC seleccionadas (catálogo Anexo C.1.5). Condicional. */
    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "PROYECTO_MEDIDA_GRC", joinColumns = @JoinColumn(name = "ID_PROYECTO"))
    @Column(name = "CODIGO", length = 50)
    private List<String> medidasGrc = new ArrayList<>();

    /** Categorías de ACC seleccionadas (catálogo Anexo C.2). Condicional. */
    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "PROYECTO_MEDIDA_ACC", joinColumns = @JoinColumn(name = "ID_PROYECTO"))
    @Column(name = "CODIGO", length = 50)
    private List<String> medidasAcc = new ArrayList<>();

    /** Campo "Proyecto de emergencia". */
    @Column(name = "ES_PROYECTO_EMERGENCIA")
    private Boolean esProyectoEmergencia;

    /** Obligatorio si esProyectoEmergencia = true. */
    @Column(name = "TIPO_EVENTO", length = 100)
    private String tipoEvento;

    /** Campo "N° de DL". Obligatorio si esProyectoEmergencia = true. */
    @Column(name = "NUMERO_DECRETO_LEGISLATIVO", length = 50)
    private String numeroDecretoLegislativo;

    /** Eje del Plan Cuscatlán (catálogo Anexo C.3). Condicional. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_EJE_PLAN_GOBIERNO")
    private EjePlanGobierno ejePlanGobierno;

    /** Plan Sectorial/Regional (catálogo Anexo C.4). Condicional. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PLAN_SECTORIAL_REGIONAL")
    private PlanSectorialRegional planSectorialRegional;

    /** Campo "Descripción del proyecto". */
    @NotBlank
    @Column(name = "DESCRIPCION_PROYECTO", nullable = false, length = 1000)
    private String descripcionProyecto;

    /**
     * Unidad Ejecutora Co-ejecutora, asignada por el Coordinador SYMP desde la Ficha de
     * información general (CU-PRE-3.5, RN16). Único campo genuinamente editable de esa ficha.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UNIDAD_EJECUTORA_COEJECUTOR")
    private UnidadEjecutora unidadEjecutoraCoEjecutora;
}
