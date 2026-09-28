package sv.gob.mh.siip.model.preinversion.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Categorías de GRD, GRC y ACC seleccionadas en "Nuevo registro". Condicionales. */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class ProyectoMedidas {

    /** Categorías de GRD seleccionadas (catálogo Anexo C.1). */
    @ElementCollection
    @CollectionTable(name = "PROYECTO_MEDIDA_GRD", joinColumns = @JoinColumn(name = "ID_PROYECTO"))
    @Column(name = "CODIGO", length = 50)
    private List<String> medidasGrd = new ArrayList<>();

    /** Categorías de GRC seleccionadas (catálogo Anexo C.1.5). */
    @ElementCollection
    @CollectionTable(name = "PROYECTO_MEDIDA_GRC", joinColumns = @JoinColumn(name = "ID_PROYECTO"))
    @Column(name = "CODIGO", length = 50)
    private List<String> medidasGrc = new ArrayList<>();

    /** Categorías de ACC seleccionadas (catálogo Anexo C.2). */
    @ElementCollection
    @CollectionTable(name = "PROYECTO_MEDIDA_ACC", joinColumns = @JoinColumn(name = "ID_PROYECTO"))
    @Column(name = "CODIGO", length = 50)
    private List<String> medidasAcc = new ArrayList<>();
}
