package sv.gob.mh.siip.model.administracion.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.administracion.enums.EstadoCalendario;
import sv.gob.mh.siip.model.common.domain.Usuario;

/** Calendario laboral (CU-ADM-04): periodo maestro, estado y sus periodos/excepciones asociados. */
@Entity
@Table(name = "CALENDARIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Calendario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calendario_seq")
    @SequenceGenerator(name = "calendario_seq", sequenceName = "CALENDARIO_SEQ", allocationSize = 1)
    @Column(name = "ID_CALENDARIO")
    private Long id;

    @Column(name = "CODIGO", nullable = false, unique = true, length = 100)
    private String codigo;

    @Column(name = "NOMBRE", nullable = false, length = 300)
    private String nombre;

    @Column(name = "DESCRIPCION", length = 1000)
    private String descripcion;

    /** Inicio del periodo maestro del calendario (RN08, RN10). */
    @Column(name = "FECHA_INICIO", nullable = false)
    private LocalDate fechaInicio;

    /** Fin del periodo maestro del calendario (RN08, RN10). */
    @Column(name = "FECHA_FIN", nullable = false)
    private LocalDate fechaFin;

    /** Nunca se elimina el registro: la unica transicion admitida es ACTIVO <-> INACTIVO (RN20). */
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoCalendario estado;

    /** Administrador responsable del calendario (RN12); se asigna a partir del actor autenticado. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ADMINISTRADOR_ID", nullable = false)
    private Usuario administrador;

    @Builder.Default
    @OneToMany(mappedBy = "calendario", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<Periodo> periodos = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "calendario", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fecha ASC")
    private List<Excepcion> excepciones = new ArrayList<>();
}
