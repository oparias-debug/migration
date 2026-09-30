package sv.gob.mh.siip.model.preinversion.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.preinversion.enums.ResultadoOpinionTecnica;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.enums.TipoSolicitudOpinionTecnica;

/**
 * Gestión de Opinión Técnica del proyecto (CU-PRE-26): una por cada solicitud o ronda de revisión de
 * la DGICP, desde "Solicitar OT" hasta que el Técnico PRE envía comentarios o emite la OT favorable.
 *
 * <p>
 * {@link #resultado} y {@link #fechaEmision} quedan nulos mientras la gestión está en curso. Al
 * cerrarse registran lo que CU-PRE-11, CU-PRE-24 y CU-PRE-25 consultan: {@code OBSERVADO} con la fecha
 * de envío de los comentarios (FA03) o {@code FAVORABLE} con la fecha de emisión (FA01). Si el Técnico
 * URP ajusta el proyecto, la siguiente revisión abre una gestión nueva, así que cada devolución queda
 * registrada por separado (RN15).
 */
@Entity
@Table(name = "OPINION_TECNICA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class OpinionTecnica {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "opinion_tecnica_seq")
    @SequenceGenerator(name = "opinion_tecnica_seq", sequenceName = "OPINION_TECNICA_SEQ", allocationSize = 1)
    @Column(name = "ID_OPINION_TECNICA")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    /** Resultado de la gestión; {@code null} mientras está en curso. */
    @Enumerated(EnumType.STRING)
    @Column(name = "RESULTADO", length = 20)
    private ResultadoOpinionTecnica resultado;

    /** Envío de comentarios (OBSERVADO) o emisión de la OT (FAVORABLE); {@code null} en curso. */
    @Column(name = "FECHA_EMISION")
    private LocalDateTime fechaEmision;

    @Column(name = "OBSERVACIONES", length = 2000)
    private String observaciones;

    /** Técnico PRE asignado por el Coordinador PRE (RN07 b); {@code null} hasta la asignación. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TECNICO_RESPONSABLE")
    private Usuario tecnicoResponsable;

    @NotNull
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_SOLICITUD", nullable = false, length = 20)
    private TipoSolicitudOpinionTecnica tipoSolicitud = TipoSolicitudOpinionTecnica.OPINION_TECNICA;

    /** Primera gestión de OT del proyecto: solo en ella se muestra "Comentarios Elegibilidad" (RN 12). */
    @NotNull
    @Builder.Default
    @Column(name = "PRIMERA_GESTION", nullable = false)
    private Boolean primeraGestion = true;

    /** Solicitud de la Bandeja de Preinversión (CU-PRE-02) por la que el Coordinador PRE asigna el caso. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SOLICITUD")
    private SolicitudPreinversion solicitud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SOLICITANTE")
    private Usuario solicitante;

    /** Clic en "Solicitar OT" o confirmación de la Actualización de OT (Anexo B.1 "Fecha de solicitud"). */
    @Column(name = "FECHA_SOLICITUD")
    private LocalDateTime fechaSolicitud;

    @Column(name = "FECHA_ASIGNACION")
    private LocalDateTime fechaAsignacion;

    /** Etapa que se gestiona (Anexo B.1 "Etapa actual"). */
    @Enumerated(EnumType.STRING)
    @Column(name = "ETAPA_ACTUAL", length = 20)
    private TipoEtapaPreinversion etapaActual;

    /** Etapa siguiente de la Ruta de Preinversión (Anexo B.1 "Etapa futura"); {@code null} si no hay. */
    @Enumerated(EnumType.STRING)
    @Column(name = "ETAPA_FUTURA", length = 20)
    private TipoEtapaPreinversion etapaFutura;

    /** Conclusiones del Técnico PRE y su "Visto bueno OT" (FA01 pasos 1.1–1.4). */
    @Valid
    @Embedded
    @Builder.Default
    private ConclusionesOpinionTecnica revisionConclusiones = new ConclusionesOpinionTecnica();

    /** Plazo para atender los comentarios (RN08, RN09); leerlo con {@link #getPlazoComentarios()}. */
    @Embedded
    private PlazoComentariosOpinionTecnica plazoComentarios;

    /** Última fecha de envío de ajustes del Técnico URP (Anexo B.1 "Fecha de ajustes"). */
    @Column(name = "FECHA_AJUSTES")
    private LocalDateTime fechaAjustes;

    /** Archivo por vencimiento del plazo de atención de observaciones (RN09). */
    @Column(name = "FECHA_ARCHIVO")
    private LocalDateTime fechaArchivo;

    /** "N° de nota de OT" (Anexo B.1). */
    @Column(name = "NUMERO_NOTA_OT", length = 50)
    private String numeroNotaOt;

    /** "Inversión estimada" de CU-PRE-17 al emitir la OT favorable, para el Histórico (Anexo A1.5). */
    @Column(name = "INVERSION_ESTIMADA", precision = 18, scale = 2)
    private BigDecimal inversionEstimada;

    /**
     * Hibernate carga un {@code @Embedded} con todas sus columnas nulas como {@code null}; aquí se
     * sustituye por uno vacío y conectado a la entidad, para que leerlo y modificarlo siempre funcione.
     *
     * @return el plazo de atención de comentarios, nunca {@code null}
     */
    public PlazoComentariosOpinionTecnica getPlazoComentarios() {
        if (plazoComentarios == null) {
            plazoComentarios = new PlazoComentariosOpinionTecnica();
        }
        return plazoComentarios;
    }

    /** @return si la DGICP todavía no envió comentarios ni emitió la OT, y no se archivó */
    public boolean estaEnCurso() {
        return resultado == null && fechaArchivo == null;
    }

    /** @return si el Técnico PRE envió comentarios y la gestión espera los ajustes (FA03) */
    public boolean estaObservada() {
        return resultado == ResultadoOpinionTecnica.OBSERVADO && fechaArchivo == null;
    }

    /** @return si se emitió la OT favorable (FA01) */
    public boolean esFavorable() {
        return resultado == ResultadoOpinionTecnica.FAVORABLE;
    }

    /** @return si venció el plazo de atención de observaciones y la gestión se archivó (RN09) */
    public boolean estaArchivada() {
        return fechaArchivo != null;
    }
}
