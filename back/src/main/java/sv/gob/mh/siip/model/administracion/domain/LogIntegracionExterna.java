package sv.gob.mh.siip.model.administracion.domain;

import java.time.LocalDateTime;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sv.gob.mh.siip.model.administracion.enums.EstadoIntegracion;

/**
 * [SUPUESTO] Bitacora de envios/respuestas hacia sistemas externos. Infiere "CU-ITF-01
 * Interfaz con SIAF", mencionado unicamente en el WBS (modulo M-15), sin ficha de caso de uso.
 */
@Entity
@Table(name = "LOG_INTEGRACION_EXTERNA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class LogIntegracionExterna {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "log_integracion_seq")
    @SequenceGenerator(name = "log_integracion_seq", sequenceName = "LOG_INTEGRACION_SEQ", allocationSize = 1)
    @Column(name = "ID_LOG_INTEGRACION")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_SISTEMA_EXTERNO", nullable = false)
    private SistemaExterno sistemaExterno;

    @NotBlank
    @Column(name = "TIPO_OPERACION", nullable = false, length = 100)
    private String tipoOperacion;

    @Column(name = "ENTIDAD_RELACIONADA", length = 100)
    private String entidadRelacionada;

    @Column(name = "ID_ENTIDAD_RELACIONADA")
    private Long idEntidadRelacionada;

    @NotNull
    @Column(name = "FECHA_ENVIO", nullable = false)
    private LocalDateTime fechaEnvio;

    @Column(name = "FECHA_RESPUESTA")
    private LocalDateTime fechaRespuesta;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoIntegracion estado;

    @Column(name = "MENSAJE_ERROR", length = 2000)
    private String mensajeError;

    @Column(name = "PAYLOAD_REFERENCIA", length = 500)
    private String payloadReferencia;
}
