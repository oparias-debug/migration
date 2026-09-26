package sv.gob.mh.siip.model.preinversion.domain;

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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;

/**
 * Avance financiero de un cuatrimestre para una {@link ProgCuatrimestralFinanciera} (fuente + año)
 * (Anexo A.5, "Avance del Cuatrimestre"). CU-PRE-32. Una fila por combinación (programacion,
 * cuatrimestre); el resto de campos de lectura de {@code FilaAvanceFuente} (acumulados, porcentajes,
 * alertaExcesoProgramado) se calculan a partir de estas filas y del histórico de la misma fuente
 * (RN-E, RN-F), igual que {@link ProgCuatrimestralFinanciera} nunca persiste sus propios porcentajes.
 */
@Entity
@Table(name = "AVANCE_FINANCIERO_CUATRIMESTRAL",
       uniqueConstraints = @UniqueConstraint(
               name = "UK_AVANCE_FIN_CUATRI",
               columnNames = {"ID_PROG_CUATRI_FIN", "CUATRIMESTRE"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class AvanceFinancieroCuatrimestral {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "avance_fin_cuatri_seq")
    @SequenceGenerator(name = "avance_fin_cuatri_seq", sequenceName = "AVANCE_FIN_CUATRI_SEQ", allocationSize = 1)
    @Column(name = "ID_AVANCE_FIN_CUATRI")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROG_CUATRI_FIN", nullable = false)
    private ProgCuatrimestralFinanciera programacion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "CUATRIMESTRE", nullable = false, length = 20)
    private Cuatrimestre cuatrimestre;

    @NotNull
    @Column(name = "MONTO_EJECUTADO", nullable = false, precision = 18, scale = 2)
    private BigDecimal montoEjecutado;

    @Column(name = "OBSERVACIONES", length = 2000)
    private String observaciones;

    @NotNull
    @Column(name = "FECHA_REGISTRO", nullable = false)
    private LocalDateTime fechaRegistro;
}
