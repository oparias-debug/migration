package sv.gob.mh.siip.model.preinversion.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Calificación que el Viabilizador registra para un criterio de elegibilidad de un proyecto
 * (CU-PRE-25, Anexo A.1): columna "¿Aplica?" y la información complementaria de "Especificar",
 * como texto libre o como códigos de opción del catálogo del criterio (RN08). Hay a lo sumo una por
 * proyecto y criterio; cada "Guardar" la reemplaza (FA01).
 */
@Entity
@Table(name = "CALIFICACION_CRITERIO_ELEGIBILIDAD",
    uniqueConstraints = @UniqueConstraint(name = "UK_CALIF_ELEG_PROYECTO_CRITERIO",
        columnNames = {"ID_PROYECTO", "ID_CRITERIO_ELEGIBILIDAD"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class CalificacionCriterioElegibilidad {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "calificacion_criterio_eleg_seq")
  @SequenceGenerator(name = "calificacion_criterio_eleg_seq", sequenceName = "CALIFICACION_CRITERIO_ELEG_SEQ",
      allocationSize = 1)
  @Column(name = "ID_CALIFICACION_CRITERIO_ELEG")
  private Long id;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "ID_PROYECTO", nullable = false)
  private Proyecto proyecto;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "ID_CRITERIO_ELEGIBILIDAD", nullable = false)
  private CriterioElegibilidad criterio;

  /** Columna "¿Aplica?": el proyecto contribuye al criterio. */
  @NotNull
  @Column(name = "APLICA", nullable = false)
  private Boolean aplica;

  /** "Especificar" de un criterio de texto libre; nulo si no aplica o si el criterio es de catálogo. */
  @Column(name = "ESPECIFICAR_TEXTO", length = 2000)
  private String especificarTexto;

  /** "Especificar" de un criterio de catálogo: códigos de las opciones seleccionadas, en orden. */
  @ElementCollection
  @CollectionTable(name = "CALIFICACION_ELEG_OPCION",
      joinColumns = @JoinColumn(name = "ID_CALIFICACION_CRITERIO_ELEG"))
  @OrderColumn(name = "ORDEN")
  @Column(name = "CODIGO_OPCION", nullable = false, length = 50)
  @Builder.Default
  private List<String> codigosOpcion = new ArrayList<>();
}
