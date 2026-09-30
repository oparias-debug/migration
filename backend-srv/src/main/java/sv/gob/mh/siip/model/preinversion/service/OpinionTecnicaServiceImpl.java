package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.dto.ComentariosDgicpRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.ConclusionesOpinionTecnicaRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.EmisionOpinionTecnicaFavorableResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.EnvioComentariosDgicpResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.InformeOpinionTecnicaResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.EstadoProyectoDto;
import sv.gob.mh.siip.model.preinversion.dto.JustificacionesInstitucionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.OpinionTecnicaResponseDto;

/**
 * Implementación de {@link OpinionTecnicaService}.
 *
 * <p>Orquesta los colaboradores del caso de uso: {@link OpinionTecnicaAcceso} abre cada operación,
 * {@link OpinionTecnicaRevision} registra y envía los comentarios DGICP, {@link OpinionTecnicaAjustes}
 * registra las justificaciones de la institución, {@link OpinionTecnicaEmision} da el visto bueno y emite
 * la OT, y {@link OpinionTecnicaEnsamblador} arma la pantalla.
 */
@Service
@Transactional
public class OpinionTecnicaServiceImpl implements OpinionTecnicaService {

    private final OpinionTecnicaAcceso acceso;
    private final OpinionTecnicaRevision revision;
    private final OpinionTecnicaAjustes ajustes;
    private final OpinionTecnicaEmision emision;
    private final OpinionTecnicaEnsamblador ensamblador;
    private final InformeOpinionTecnicaEnsamblador informe;

    public OpinionTecnicaServiceImpl(OpinionTecnicaAcceso acceso,
            OpinionTecnicaRevision revision,
            OpinionTecnicaAjustes ajustes,
            OpinionTecnicaEmision emision,
            OpinionTecnicaEnsamblador ensamblador,
            InformeOpinionTecnicaEnsamblador informe) {
        this.acceso = acceso;
        this.revision = revision;
        this.ajustes = ajustes;
        this.emision = emision;
        this.ensamblador = ensamblador;
        this.informe = informe;
    }

    @Override
    @Transactional(readOnly = true)
    public OpinionTecnicaResponseDto obtener(Long idProyecto, Long idGestion) {
        return ensamblador.pantalla(acceso.gestionParaConsulta(idProyecto, idGestion));
    }

    @Override
    public OpinionTecnicaResponseDto guardarComentarios(Long idProyecto, Long idGestion,
            ComentariosDgicpRequestDto request) {
        OpinionTecnicaContexto contexto = acceso.gestionParaDgicp(idProyecto, idGestion);
        revision.guardarComentarios(contexto, request);
        return pantallaActual(contexto);
    }

    @Override
    public EnvioComentariosDgicpResponseDto enviarComentarios(Long idProyecto, Long idGestion,
            ComentariosDgicpRequestDto request) {
        OpinionTecnicaContexto contexto = acceso.gestionParaDgicp(idProyecto, idGestion);
        return revision.enviarComentarios(contexto, request).respuesta(idGestion, contexto.proyecto().getEstado());
    }

    @Override
    public OpinionTecnicaResponseDto guardarJustificaciones(Long idProyecto, Long idGestion,
            JustificacionesInstitucionRequestDto request) {
        OpinionTecnicaContexto contexto = acceso.gestionParaJustificacion(idProyecto, idGestion);
        ajustes.guardarJustificaciones(contexto, request);
        return pantallaActual(contexto);
    }

    @Override
    public OpinionTecnicaResponseDto guardarConclusiones(Long idProyecto, Long idGestion,
            ConclusionesOpinionTecnicaRequestDto request) {
        OpinionTecnicaContexto contexto = acceso.gestionParaDgicp(idProyecto, idGestion);
        revision.guardarConclusiones(contexto, request.getConclusiones());
        return pantallaActual(contexto);
    }

    @Override
    public OpinionTecnicaResponseDto darVistoBueno(Long idProyecto, Long idGestion) {
        OpinionTecnicaContexto contexto = acceso.paraGestion(idProyecto, idGestion, RolUsuario.COORDINADOR_PRE);
        emision.darVistoBueno(contexto);
        return pantallaActual(contexto);
    }

    @Override
    public EmisionOpinionTecnicaFavorableResponseDto emitirFavorable(Long idProyecto, Long idGestion,
            MultipartFile notaOt, String numeroNotaOt) {
        OpinionTecnicaContexto contexto = acceso.paraGestion(idProyecto, idGestion, RolUsuario.TECNICO_PRE);
        boolean disponibleEnCaptura = emision.emitirFavorable(contexto, notaOt, numeroNotaOt);
        return new EmisionOpinionTecnicaFavorableResponseDto(idGestion,
                EstadoProyectoDto.valueOf(contexto.proyecto().getEstado().name()),
                contexto.gestion().getFechaEmision().toLocalDate(), disponibleEnCaptura);
    }

    @Override
    @Transactional(readOnly = true)
    public InformeOpinionTecnicaResponseDto obtenerInforme(Long idProyecto, Long idGestion) {
        return informe.informe(acceso.gestionParaConsulta(idProyecto, idGestion));
    }

    /** La pantalla tras un cambio: las acciones se derivan otra vez del estado resultante. */
    private OpinionTecnicaResponseDto pantallaActual(OpinionTecnicaContexto contexto) {
        return ensamblador.pantalla(acceso.actualizar(contexto.actor(), contexto.proyecto(), contexto.gestion()));
    }
}
