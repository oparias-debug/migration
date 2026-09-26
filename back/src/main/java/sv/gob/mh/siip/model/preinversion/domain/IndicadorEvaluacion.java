package sv.gob.mh.siip.model.preinversion.domain;

import sv.gob.mh.siip.model.preinversion.enums.TipoIndicador;

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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Indicadores de evaluacion socioeconomica/financiera del proyecto. CU-PRE-21, CU-PRE-23. */
@Entity
@Table(name = "INDICADOR_EVALUACION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class IndicadorEvaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "indicador_evaluacion_seq")
    @SequenceGenerator(name = "indicador_evaluacion_seq", sequenceName = "INDICADOR_EVALUACION_SEQ", allocationSize = 1)
    @Column(name = "ID_INDICADOR_EVALUACION")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false)
    private Proyecto proyecto;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_INDICADOR", nullable = false, length = 30)
    private TipoIndicador tipoIndicador;

    @NotNull
    @Column(name = "VALOR", nullable = false, precision = 18, scale = 4)
    private BigDecimal valor;

    @NotNull
    @Column(name = "FECHA_CALCULO", nullable = false)
    private LocalDateTime fechaCalculo;
}
