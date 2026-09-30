package sv.gob.mh.siip.model.preinversion.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Plazo de 5 días hábiles que tiene la institución para atender los comentarios de la DGICP (CU-PRE-26
 * RN08, RN09; Anexo A2 c y f). Sus fechas quedan nulas hasta que el Técnico PRE envía comentarios.
 */
@Embeddable
@Getter
@NoArgsConstructor
public class PlazoComentariosOpinionTecnica {

    /** Fin del plazo para atender los comentarios. */
    @Column(name = "FECHA_FIN_PLAZO")
    private LocalDate fechaFin;

    /** Envío del correo de advertencia de dos días antes del vencimiento. */
    @Column(name = "FECHA_ALERTA_PLAZO")
    private LocalDateTime fechaAlerta;

    /** @param fechaFin fin del plazo, sin advertencia enviada */
    public PlazoComentariosOpinionTecnica(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    /** @return si el plazo empezó a correr, es decir, si se enviaron comentarios */
    public boolean estaIniciado() {
        return fechaFin != null;
    }

    /** @return si ya se envió la advertencia de vencimiento (RN08: una sola) */
    public boolean alertaEnviada() {
        return fechaAlerta != null;
    }

    /** @param fin fin del plazo que inicia el envío de comentarios */
    public void iniciar(LocalDate fin) {
        fechaFin = fin;
    }

    /** @param fecha momento en que se envió la advertencia */
    public void registrarAlerta(LocalDateTime fecha) {
        fechaAlerta = fecha;
    }
}
