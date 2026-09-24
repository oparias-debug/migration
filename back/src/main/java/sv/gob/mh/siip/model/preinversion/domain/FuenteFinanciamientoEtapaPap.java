package sv.gob.mh.siip.model.preinversion.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import sv.gob.mh.siip.model.preinversion.enums.FuenteFinanciamiento;

/**
 * Fila de fuente de financiamiento registrada para la Programación Financiera Cuatrimestral del
 * PAP (Anexo A.2) de una etapa. CU-PRE-30. Es el {@code idFuente} de los DTOs generados y persiste
 * a través de los años (RN-B.a: en un estudio de arrastre, la información seleccionada aquí se
 * bloquea y se muestra tal cual en el ejercicio siguiente); el monto programado por cuatrimestre,
 * que sí cambia año a año, vive en {@link ProgCuatrimestralFinanciera}.
 */
@Entity
@Table(name = "FUENTE_FINANCIAMIENTO_ETAPA_PAP")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class FuenteFinanciamientoEtapaPap {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fuente_fin_etapa_pap_seq")
    @SequenceGenerator(name = "fuente_fin_etapa_pap_seq", sequenceName = "FUENTE_FIN_ETAPA_PAP_SEQ", allocationSize = 1)
    @Column(name = "ID_FUENTE")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_ETAPA_PREINVERSION", nullable = false)
    private EtapaPreinversion etapaPreinversion;

    @Enumerated(EnumType.STRING)
    @Column(name = "FUENTE_FINANCIAMIENTO", length = 30)
    private FuenteFinanciamiento fuenteFinanciamiento;

    @Column(name = "FUENTE_RECURSOS", length = 200)
    private String fuenteRecursos;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "FUENTE_FIN_ETAPA_PAP_CONVENIO", joinColumns = @JoinColumn(name = "ID_FUENTE"))
    @Column(name = "CONVENIO", length = 200)
    private List<String> convenios = new ArrayList<>();
}
