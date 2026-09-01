package sv.gob.mh.siip.model.ejecucion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import sv.gob.mh.siip.model.common.domain.Usuario;

/** Informe de visita de campo a un proyecto. CU-EJE-06. */
@Entity
@Table(name = "INFORME_VISITA_CAMPO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class InformeVisitaCampo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "informe_visita_campo_seq")
    @SequenceGenerator(name = "informe_visita_campo_seq", sequenceName = "INFORME_VISITA_CAMPO_SEQ", allocationSize = 1)
    @Column(name = "ID_INFORME_VISITA")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_TECNICO_RESPONSABLE", nullable = false)
    private Usuario tecnicoResponsable;

    @NotNull
    @Column(name = "FECHA_VISITA", nullable = false)
    private LocalDate fechaVisita;

    @Column(name = "AVANCE_OBSERVADO", length = 2000)
    private String avanceObservado;

    @Lob
    @Column(name = "COMENTARIOS")
    private String comentarios;

    @Lob
    @Column(name = "CONCLUSIONES")
    private String conclusiones;

    @Column(name = "FECHA_CREACION")
    private LocalDateTime fechaCreacion;
    @Column(name = "USUARIO_CREACION", length = 100)
    private String usuarioCreacion;

    @OneToMany(mappedBy = "informeVisitaCampo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private java.util.List<AcompananteVisitaCampo> acompanantes = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "informeVisitaCampo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private java.util.List<ArchivoAdjuntoVisitaCampo> archivosAdjuntos = new java.util.ArrayList<>();
}
