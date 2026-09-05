package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.*;
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

    /** RNC: hasta 3000 caracteres (limite de entrada de cliente, no de validacion del servidor). */
    @Column(name = "ANTECEDENTES", length = 3000)
    private String antecedentes;

    /** RNC: hasta 500 caracteres. Ninguno de los 4 campos del formulario es obligatorio (CU-PRE-04). */
    @Column(name = "PROBLEMA_CENTRAL", length = 500)
    private String problemaCentral;

    @Column(name = "OBJETIVO_GENERAL", length = 500)
    private String objetivoGeneral;

    @OneToMany(mappedBy = "identificacion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private java.util.List<ObjetivoEspecifico> objetivosEspecificos = new java.util.ArrayList<>();

    @Column(name = "NOMBRE_ARCHIVO_ARBOL_PROBLEMAS", length = 300)
    private String nombreArchivoArbolProblemas;

    @Column(name = "RUTA_ARCHIVO_ARBOL_PROBLEMAS", length = 500)
    private String rutaArchivoArbolProblemas;

    @Column(name = "FECHA_CARGA_ARBOL_PROBLEMAS")
    private LocalDateTime fechaCargaArbolProblemas;

    @Column(name = "NOMBRE_ARCHIVO_ARBOL_OBJETIVOS", length = 300)
    private String nombreArchivoArbolObjetivos;

    @Column(name = "RUTA_ARCHIVO_ARBOL_OBJETIVOS", length = 500)
    private String rutaArchivoArbolObjetivos;

    @Column(name = "FECHA_CARGA_ARBOL_OBJETIVOS")
    private LocalDateTime fechaCargaArbolObjetivos;

    /** Nulo si nunca se ha guardado (botón "Guardar", SF-1). Ver RNA-2/RNA-3 en CU-PRE-04.openapi.yaml. */
    @Column(name = "FECHA_ULTIMO_GUARDADO")
    private LocalDateTime fechaUltimoGuardado;

    @Column(name = "FECHA_CREACION")
    private LocalDateTime fechaCreacion;
    @Column(name = "USUARIO_CREACION", length = 100)
    private String usuarioCreacion;
    @Column(name = "FECHA_MODIFICACION")
    private LocalDateTime fechaModificacion;
    @Column(name = "USUARIO_MODIFICACION", length = 100)
    private String usuarioModificacion;
}
