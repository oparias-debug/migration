package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/** Identificacion del problema central y arbol de problemas/objetivos. CU-PRE-04. */
@Entity
@Table(name = "IDENTIFICACION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Identificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "identificacion_seq")
    @SequenceGenerator(name = "identificacion_seq", sequenceName = "IDENTIFICACION_SEQ", allocationSize = 1)
    @Column(name = "ID_IDENTIFICACION")
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false, unique = true)
    private Proyecto proyecto;

    @NotBlank
    @Column(name = "PROBLEMA_CENTRAL", nullable = false, length = 2000)
    private String problemaCentral;

    @Lob
    @Column(name = "CAUSAS")
    private String causas;

    @Lob
    @Column(name = "EFECTOS")
    private String efectos;

    @Column(name = "OBJETIVO_GENERAL", length = 2000)
    private String objetivoGeneral;

    @Column(name = "ARCHIVO_ARBOL_PROBLEMAS", length = 500)
    private String archivoArbolProblemas;

    @Column(name = "ARCHIVO_ARBOL_OBJETIVOS", length = 500)
    private String archivoArbolObjetivos;

    @OneToMany(mappedBy = "identificacion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private java.util.List<ObjetivoEspecifico> objetivosEspecificos = new java.util.ArrayList<>();

    @Column(name = "FECHA_CREACION")
    private LocalDateTime fechaCreacion;
    @Column(name = "USUARIO_CREACION", length = 100)
    private String usuarioCreacion;
    @Column(name = "FECHA_MODIFICACION")
    private LocalDateTime fechaModificacion;
    @Column(name = "USUARIO_MODIFICACION", length = 100)
    private String usuarioModificacion;
}
