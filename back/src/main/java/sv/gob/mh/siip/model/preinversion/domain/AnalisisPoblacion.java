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
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * "Análisis de la Población" (Anexo A.1), 1:1 con {@link Proyecto}: descripción de las filas
 * Afectada/Objetivo (RN06, bloqueada para Referencia) y las celdas de "Ubicación"/"N° de Personas"
 * (RN09) de cada una de las 3 filas editables. "Población en Espera" nunca se persiste: es 100%
 * calculada por el servicio (RN04/RN05/RN09). CU-PRE-07.
 */
@Entity
@Table(name = "ANALISIS_POBLACION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class AnalisisPoblacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "analisis_poblacion_seq")
    @SequenceGenerator(name = "analisis_poblacion_seq", sequenceName = "ANALISIS_POBLACION_SEQ", allocationSize = 1)
    @Column(name = "ID_ANALISIS_POBLACION")
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false, unique = true)
    private Proyecto proyecto;

    /** RN06: bloqueada y vacía para Referencia/Espera; cualquier valor enviado para Referencia se ignora. */
    @Column(name = "DESCRIPCION_AFECTADA", length = 2000)
    private String descripcionAfectada;

    @Column(name = "DESCRIPCION_OBJETIVO", length = 2000)
    private String descripcionObjetivo;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "ANALISIS_POBLACION_UBIC_REF", joinColumns = @JoinColumn(name = "ID_ANALISIS_POBLACION"))
    @OrderColumn(name = "ORDEN")
    private List<CeldaUbicacionPoblacion> ubicacionesReferencia = new ArrayList<>();

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "ANALISIS_POBLACION_UBIC_AFEC", joinColumns = @JoinColumn(name = "ID_ANALISIS_POBLACION"))
    @OrderColumn(name = "ORDEN")
    private List<CeldaUbicacionPoblacion> ubicacionesAfectada = new ArrayList<>();

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "ANALISIS_POBLACION_UBIC_OBJ", joinColumns = @JoinColumn(name = "ID_ANALISIS_POBLACION"))
    @OrderColumn(name = "ORDEN")
    private List<CeldaUbicacionPoblacion> ubicacionesObjetivo = new ArrayList<>();
}
