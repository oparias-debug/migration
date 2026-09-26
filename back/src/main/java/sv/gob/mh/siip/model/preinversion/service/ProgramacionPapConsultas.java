package sv.gob.mh.siip.model.preinversion.service;

import java.util.Comparator;
import java.util.List;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;

/**
 * Lecturas comunes de la programación cuatrimestral del PAP, CU-PRE-30 (financiera) y CU-PRE-31
 * (metas físicas): el proyecto o estudio por CUP y sus etapas de preinversión.
 */
final class ProgramacionPapConsultas {

    private static final String MENSAJE_ESTUDIO_NO_EXISTE = "El estudio (CUP + año) no existe.";

    private final ProyectoRepository proyectoRepository;
    private final EtapaPreinversionRepository etapaPreinversionRepository;

    ProgramacionPapConsultas(ProyectoRepository proyectoRepository,
            EtapaPreinversionRepository etapaPreinversionRepository) {
        this.proyectoRepository = proyectoRepository;
        this.etapaPreinversionRepository = etapaPreinversionRepository;
    }

    /** Proyecto por CUP (CU-PRE-30 "Agregar nuevo estudio"), tenga o no etapas de preinversión. */
    Proyecto buscarProyecto(String cup) {
        return proyectoRepository.findByCup(cup)
                .orElseThrow(() -> new RecursoNoEncontradoException("El CUP indicado no existe."));
    }

    /** Estudio por CUP: el proyecto debe existir y tener al menos una etapa de preinversión. */
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
                .sorted(Comparator.comparingInt(etapa -> etapa.getTipoEtapa().ordinal()))
                .toList();
    }

    /** Etapa "etapa" del proyecto; si no existe lanza {@link RecursoNoEncontradoException} con "mensaje". */
    EtapaPreinversion buscarEtapa(Long idProyecto, NombreEtapaDto etapa, String mensaje) {
        return etapaPreinversionRepository
                .findByProyectoIdAndTipoEtapa(idProyecto, TipoEtapaPreinversion.valueOf(etapa.name()))
                .orElseThrow(() -> new RecursoNoEncontradoException(mensaje));
    }
}
