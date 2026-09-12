package sv.gob.mh.siip.model.administracion.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
import sv.gob.mh.siip.model.administracion.enums.EstadoVigencia;

/** Catalogo maestro del catalogMaster (CU-ADM-01): codigo, nombre, padre opcional, vigencia y sus campos (FIELD/KEY). */
@Entity
@Table(name = "CATALOGO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Catalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "catalogo_seq")
    @SequenceGenerator(name = "catalogo_seq", sequenceName = "CATALOGO_SEQ", allocationSize = 1)
    @Column(name = "ID_CATALOGO")
    private Long id;

    @Column(name = "CODIGO", nullable = false, unique = true, length = 100)
    private String codigo;

    @Column(name = "NOMBRE", nullable = false, length = 300)
    private String nombre;

    /** Codigo del catalogo padre (PARENT), sin relacion JPA: solo referencia por codigo de negocio. */
    @Column(name = "CATALOGO_PADRE_CODIGO", length = 100)
    private String catalogoPadreCodigo;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoVigencia estado;

    @Column(name = "FECHA_DESDE")
    private LocalDate fechaDesde;

    @Column(name = "FECHA_HASTA")
    private LocalDate fechaHasta;

    @Builder.Default
    @OneToMany(mappedBy = "catalogo", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<CampoDefinicion> campos = new ArrayList<>();
}
