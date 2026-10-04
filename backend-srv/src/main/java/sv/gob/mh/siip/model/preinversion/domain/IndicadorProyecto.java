package sv.gob.mh.siip.model.preinversion.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.preinversion.enums.TipoIndicadorProyecto;

/** Indicador de resultado o de producto registrado para un proyecto en CU-PRE-23. */
@Entity
@Table(name = "INDICADOR_PROYECTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class IndicadorProyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "indicador_proyecto_seq")
    @SequenceGenerator(name = "indicador_proyecto_seq", sequenceName = "INDICADOR_PROYECTO_SEQ", allocationSize = 1)
    @Column(name = "ID_INDICADOR_PROYECTO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 20)
    private TipoIndicadorProyecto tipo;

    /** Fila de Descripción Técnica (CU-PRE-11) cuando el tipo es PRODUCTO. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COMPONENTE")
    private Componente componente;

    @Column(name = "CODIGO", nullable = false, length = 100)
    private String codigo;

    @Column(name = "NOMBRE", nullable = false, length = 300)
    private String nombre;

    @Column(name = "DESCRIPCION", length = 1000)
    private String descripcion;

    @Column(name = "UNIDAD_MEDIDA", length = 100)
    private String unidadMedida;

    @Column(name = "META_GLOBAL")
    private Double metaGlobal;

    @Column(name = "META_ES_ACUMULATIVA")
    private Boolean metaEsAcumulativa;

    @Column(name = "ES_INDICADOR_PRINCIPAL")
    private Boolean esIndicadorPrincipal;

    @ElementCollection
    @CollectionTable(name = "INDICADOR_PROYECTO_META_PERIODO",
            joinColumns = @JoinColumn(name = "ID_INDICADOR_PROYECTO"))
    @Column(name = "META")
    @Builder.Default
    private List<Double> metasPorPeriodo = new ArrayList<>();
}
