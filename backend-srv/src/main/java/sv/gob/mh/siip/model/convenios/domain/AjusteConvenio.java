package sv.gob.mh.siip.model.convenios.domain;
import java.time.LocalDateTime;

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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.convenios.enums.TipoAjusteConvenio;

/** Ajuste (adenda/reestructuracion) sobre un convenio. CU-MPD-03. */
@Entity
@Table(name = "AJUSTE_CONVENIO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class AjusteConvenio {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ajuste_convenio_seq")
    @SequenceGenerator(name = "ajuste_convenio_seq", sequenceName = "AJUSTE_CONVENIO_SEQ", allocationSize = 1)
    @Column(name = "ID_AJUSTE_CONVENIO")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CONVENIO", nullable = false)
    private Convenio convenio;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_AJUSTE", nullable = false, length = 20)
    private TipoAjusteConvenio tipoAjuste;

    @Column(name = "DOCUMENTO_AUTORIZACION", length = 500)
    private String documentoAutorizacion;

    @Column(name = "JUSTIFICACION", length = 2000)
    private String justificacion;

    @NotNull
    @Column(name = "FECHA_AJUSTE", nullable = false)
    private LocalDateTime fechaAjuste;

    @OneToMany(mappedBy = "ajusteConvenio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private java.util.List<ComponenteConvenioAjuste> componentesAjustados = new java.util.ArrayList<>();
}
