package sv.gob.mh.siip.model.preinversion.domain;

import sv.gob.mh.siip.model.common.domain.Auditable;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Fila de la tabla "Registro de Etapas" (Anexo A.1) de la Ruta de Preinversión de un proyecto.
 * CU-PRE-3.5. Una fila por combinación (proyecto, tipoEtapa) — ver {@code UK_ETAPA_PROYECTO_TIPO}.
 */
@Entity
@Table(name = "ETAPA_PREINVERSION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false, of = "id")
public class EtapaPreinversion extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "etapa_preinversion_seq")
    @SequenceGenerator(name = "etapa_preinversion_seq", sequenceName = "ETAPA_PREINVERSION_SEQ", allocationSize = 1)
    @Column(name = "ID_ETAPA_PREINVERSION")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_ETAPA", nullable = false, length = 30)
    private TipoEtapaPreinversion tipoEtapa;

    @NotNull
    @Column(name = "FECHA_SELECCION", nullable = false)
    private LocalDateTime fechaSeleccion;

    @Column(name = "JUSTIFICACION", length = 2000)
    private String justificacion;

    /**
     * Campo "Costo de la etapa" (RN04, obligatorio para guardar). RN05/RN11: para EJECUCION el
     * valor lo sobrescribe el Sistema automáticamente (Presupuesto de inversión u Opinión Técnica
     * más reciente); cualquier valor enviado por el cliente para esa etapa se ignora.
     */
    @Column(name = "COSTO")
    private Double costo;

    /**
     * Campo "Fecha estimada de inicio". Texto libre, no {@code LocalDate}: el propio CU tiene 3
     * formatos en conflicto sin resolver (RN04 "dd/mm/aaaa", Anexo B.1 "MM/AA", mockup "mm/aaaa").
     */
    @Column(name = "FECHA_INICIO", length = 20)
    private String fechaInicio;

    /** Campo "Fecha estimada de finalización". Mismo criterio que {@link #fechaInicio}. */
    @Column(name = "FECHA_FIN", length = 20)
    private String fechaFin;

    /**
     * Si el botón de navegación de esta etapa hacia identificación/formulación/evaluación/
     * programación está habilitado. PERFIL y EJECUCION lo tienen en {@code true} desde su
     * creación (RN09); las demás pasan a {@code true} la primera vez que se guardan sus fechas.
     */
    @Column(name = "HABILITADO_PARA_REGISTRO", nullable = false)
    @Builder.Default
    private Boolean habilitadoParaRegistro = false;

    /** Si ya se emitió Opinión Técnica para esta etapa (CU-PRE-26). */
    @Column(name = "TIENE_OPINION_TECNICA", nullable = false)
    @Builder.Default
    private Boolean tieneOpinionTecnica = false;

    /**
     * RN13: true si esta etapa ya tenía Opinión Técnica y una modificación de ruta posterior
     * requiere pasar nuevamente por ella. No se pierde su información, pero queda de solo lectura
     * hasta obtener Opinión Técnica nuevamente.
     */
    @Column(name = "BLOQUEADA_POR_MODIFICACION", nullable = false)
    @Builder.Default
    private Boolean bloqueadaPorModificacion = false;
}
