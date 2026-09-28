package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.administracion.enums.EstadoCalendarioEvento;
import sv.gob.mh.siip.model.administracion.enums.TipoEventoCalendario;
import sv.gob.mh.siip.model.administracion.repository.CalendarioEventoRepository;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;

/**
 * Lecturas comunes del avance cuatrimestral del PAP, CU-PRE-32 (financiero) y CU-PRE-33 (metas
 * físicas): el estudio por CUP, sus etapas y la verificación de que el período de ingreso del
 * calendario (evento EJECUCION_PAP) siga abierto.
 */
final class AvancePapConsultas {

    private static final String MENSAJE_ESTUDIO_NO_EXISTE = "El estudio (CUP + año + período) no existe.";

    private final ProyectoRepository proyectoRepository;
    private final EtapaPreinversionRepository etapaPreinversionRepository;
    private final CalendarioEventoRepository calendarioEventoRepository;

    AvancePapConsultas(ProyectoRepository proyectoRepository, EtapaPreinversionRepository etapaPreinversionRepository,
            CalendarioEventoRepository calendarioEventoRepository) {
        this.proyectoRepository = proyectoRepository;
        this.etapaPreinversionRepository = etapaPreinversionRepository;
        this.calendarioEventoRepository = calendarioEventoRepository;
    }

    Proyecto buscarEstudio(String cup) {
        Proyecto proyecto = proyectoRepository.findByCup(cup)
                .orElseThrow(() -> new RecursoNoEncontradoException(MENSAJE_ESTUDIO_NO_EXISTE));
        if (etapaPreinversionRepository.findByProyectoId(proyecto.getId()).isEmpty()) {
            throw new RecursoNoEncontradoException(MENSAJE_ESTUDIO_NO_EXISTE);
        }
        return proyecto;
    }

    List<EtapaPreinversion> etapasOrdenadas(Long idProyecto) {
        return etapaPreinversionRepository.findByProyectoId(idProyecto).stream()
                .sorted((a, b) -> Integer.compare(a.getTipoEtapa().ordinal(), b.getTipoEtapa().ordinal()))
                .toList();
    }

    /** Etapa "nombreEtapa" del proyecto; si no existe lanza {@link RecursoNoEncontradoException} con "mensaje". */
    EtapaPreinversion buscarEtapa(Long idProyecto, String nombreEtapa, String mensaje) {
        return etapaPreinversionRepository
                .findByProyectoIdAndTipoEtapa(idProyecto, TipoEtapaPreinversion.valueOf(nombreEtapa))
                .orElseThrow(() -> new RecursoNoEncontradoException(mensaje));
    }

    /**
     * RN-A.b. [SUPUESTO] Sin evento EJECUCION_PAP configurado para el año/cuatrimestre se asume el
     * período abierto (la especificación no define ese caso; mismo criterio que CU-PRE-30).
     */
    void verificarPeriodoAbierto(Integer anio, Cuatrimestre periodo) {
        boolean abierto = calendarioEventoRepository
                .findByTipoEventoAndAnioAndCuatrimestre(TipoEventoCalendario.EJECUCION_PAP, anio, periodo.numero())
                .map(evento -> evento.getEstado() == EstadoCalendarioEvento.ABIERTO)
                .orElse(true);
        if (!abierto) {
            throw new ConflictoEstadoException("PERIODO_CERRADO", "Periodo de ingreso de información ha finalizado.");
        }
    }
}
