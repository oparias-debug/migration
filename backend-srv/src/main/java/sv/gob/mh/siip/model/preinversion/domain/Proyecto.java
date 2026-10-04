package sv.gob.mh.siip.model.preinversion.domain;

import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.common.domain.Auditable;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidad raiz del ciclo de inversion publica: proyecto, programa o estudio general.
 * CU-PRE-01 (Registro y Solicitud de CUP), CU-PRE-01.5 (Revision y Emision de CUP),
 * CU-PRE-02 (Bandeja de Preinversion), CU-PRE-03 (Captura de Proyectos).
 */
// Auditoría de la entidad (docs/audit-logging.md); nombre completo porque extiende
// common.domain.Auditable (columnas de creación/modificación), que se llama igual.
@sv.gob.mh.infrastructure.config.audit.Auditable
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

    @Builder.Default
    @Embedded
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private ProyectoMedidas medidas = new ProyectoMedidas();

    @Builder.Default
    @Embedded
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private ProyectoEmergencia emergencia = new ProyectoEmergencia();

    @Builder.Default
    @Embedded
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private ProyectoAlineacionPlanes alineacionPlanes = new ProyectoAlineacionPlanes();

    /** Campo "Descripción del proyecto". */
    @NotBlank
    @Column(name = "DESCRIPCION_PROYECTO", nullable = false, length = 1000)
    private String descripcionProyecto;

    /**
     * Unidad Ejecutora Co-ejecutora, asignada por el Coordinador SYMP desde la Ficha de
     * información general (CU-PRE-03.5, RN16). Único campo genuinamente editable de esa ficha.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UNIDAD_EJECUTORA_COEJECUTOR")
    private UnidadEjecutora unidadEjecutoraCoEjecutora;

    @Builder.Default
    @Embedded
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private ProyectoAlternativas alternativas = new ProyectoAlternativas();

    // Los grupos embebidos conservan columnas y getters/setters planos de Proyecto (mappers, servicios
    // y tests no cambian). Hibernate deja el embebido en null cuando todas sus columnas son null, por
    // eso cada acceso pasa por un helper que lo crea si hace falta.

    private ProyectoMedidas medidas() {
        if (medidas == null) {
            medidas = new ProyectoMedidas();
        }
        return medidas;
    }

    private ProyectoEmergencia emergencia() {
        if (emergencia == null) {
            emergencia = new ProyectoEmergencia();
        }
        return emergencia;
    }

    private ProyectoAlineacionPlanes alineacionPlanes() {
        if (alineacionPlanes == null) {
            alineacionPlanes = new ProyectoAlineacionPlanes();
        }
        return alineacionPlanes;
    }

    private ProyectoAlternativas alternativas() {
        if (alternativas == null) {
            alternativas = new ProyectoAlternativas();
        }
        return alternativas;
    }

    public List<String> getMedidasGrd() {
        return medidas().getMedidasGrd();
    }

    public void setMedidasGrd(List<String> medidasGrd) {
        medidas().setMedidasGrd(medidasGrd);
    }

    public List<String> getMedidasGrc() {
        return medidas().getMedidasGrc();
    }

    public void setMedidasGrc(List<String> medidasGrc) {
        medidas().setMedidasGrc(medidasGrc);
    }

    public List<String> getMedidasAcc() {
        return medidas().getMedidasAcc();
    }

    public void setMedidasAcc(List<String> medidasAcc) {
        medidas().setMedidasAcc(medidasAcc);
    }

    public Boolean getEsProyectoEmergencia() {
        return emergencia().getEsProyectoEmergencia();
    }

    public void setEsProyectoEmergencia(Boolean esProyectoEmergencia) {
        emergencia().setEsProyectoEmergencia(esProyectoEmergencia);
    }

    public String getTipoEvento() {
        return emergencia().getTipoEvento();
    }

    public void setTipoEvento(String tipoEvento) {
        emergencia().setTipoEvento(tipoEvento);
    }

    public String getNumeroDecretoLegislativo() {
        return emergencia().getNumeroDecretoLegislativo();
    }

    public void setNumeroDecretoLegislativo(String numeroDecretoLegislativo) {
        emergencia().setNumeroDecretoLegislativo(numeroDecretoLegislativo);
    }

    public EjePlanGobierno getEjePlanGobierno() {
        return alineacionPlanes().getEjePlanGobierno();
    }

    public void setEjePlanGobierno(EjePlanGobierno ejePlanGobierno) {
        alineacionPlanes().setEjePlanGobierno(ejePlanGobierno);
    }

    public PlanSectorialRegional getPlanSectorialRegional() {
        return alineacionPlanes().getPlanSectorialRegional();
    }

    public void setPlanSectorialRegional(PlanSectorialRegional planSectorialRegional) {
        alineacionPlanes().setPlanSectorialRegional(planSectorialRegional);
    }

    public String getJustificacionAlternativasSolucion() {
        return alternativas().getJustificacionAlternativasSolucion();
    }

    public void setJustificacionAlternativasSolucion(String justificacionAlternativasSolucion) {
        alternativas().setJustificacionAlternativasSolucion(justificacionAlternativasSolucion);
    }

    public LocalDateTime getFechaUltimoGuardadoAlternativasSolucion() {
        return alternativas().getFechaUltimoGuardadoAlternativasSolucion();
    }

    public void setFechaUltimoGuardadoAlternativasSolucion(LocalDateTime fecha) {
        alternativas().setFechaUltimoGuardadoAlternativasSolucion(fecha);
    }
}
