package sv.gob.mh.siip.model.preinversion.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sv.gob.mh.siip.model.common.domain.Usuario;

/**
 * "Conclusiones" del Técnico PRE y el "Visto bueno OT" del Coordinador PRE sobre ellas (CU-PRE-26 FA01
 * pasos 1.1–1.4). El visto bueno aprueba un texto concreto: si las conclusiones cambian, deja de valer.
 */
@Embeddable
@Getter
@NoArgsConstructor
public class ConclusionesOpinionTecnica {

    /** Conclusiones del Técnico PRE; guardarlas habilita el "Visto bueno OT". */
    @Column(name = "CONCLUSIONES", length = 2000)
    private String texto;

    @NotNull
    @Column(name = "VISTO_BUENO", nullable = false)
    private Boolean vistoBueno = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COORDINADOR_VISTO_BUENO")
    private Usuario coordinadorVistoBueno;

    @Column(name = "FECHA_VISTO_BUENO")
    private LocalDateTime fechaVistoBueno;

    /** @param texto conclusiones ya registradas, sin visto bueno */
    public ConclusionesOpinionTecnica(String texto) {
        this.texto = texto;
    }

    /** @return si hay conclusiones guardadas */
    public boolean estanRegistradas() {
        return texto != null && !texto.isBlank();
    }

    /** @return si el Coordinador PRE dio el visto bueno a las conclusiones actuales */
    public boolean tieneVistoBueno() {
        return Boolean.TRUE.equals(vistoBueno);
    }

    /**
     * Registra las conclusiones; si el texto cambia, anula el visto bueno que tuvieran.
     *
     * @param nuevoTexto conclusiones ya validadas
     * @return si el texto cambió
     */
    public boolean registrar(String nuevoTexto) {
        if (nuevoTexto.equals(texto)) {
            return false;
        }
        texto = nuevoTexto;
        vistoBueno = false;
        coordinadorVistoBueno = null;
        fechaVistoBueno = null;
        return true;
    }

    /**
     * @param coordinador Coordinador PRE que da el visto bueno
     * @param fecha momento del visto bueno
     */
    public void darVistoBueno(Usuario coordinador, LocalDateTime fecha) {
        vistoBueno = true;
        coordinadorVistoBueno = coordinador;
        fechaVistoBueno = fecha;
    }
}
