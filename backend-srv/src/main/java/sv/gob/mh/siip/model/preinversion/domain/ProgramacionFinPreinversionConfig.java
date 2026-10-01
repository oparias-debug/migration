package sv.gob.mh.siip.model.preinversion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Configuración de períodos de CU-PRE-22.1 para un proyecto. */
@Entity
@Table(name = "PROGRAMACION_FIN_PREINV_CONFIG")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProgramacionFinPreinversionConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prog_fin_preinv_config_seq")
    @SequenceGenerator(name = "prog_fin_preinv_config_seq", sequenceName = "PROG_FIN_PREINV_CONFIG_SEQ",
            allocationSize = 1)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PROYECTO", nullable = false, unique = true)
    private Proyecto proyecto;
    @Column(name = "PERIODOS_A_PROGRAMAR", nullable = false)
    private Integer periodosAProgramar;
}
