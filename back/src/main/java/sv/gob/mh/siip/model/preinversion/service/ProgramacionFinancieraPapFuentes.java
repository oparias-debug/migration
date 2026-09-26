package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.List;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.FuenteFinanciamientoEtapaPap;
import sv.gob.mh.siip.model.preinversion.domain.ProgCuatrimestralFinanciera;
import sv.gob.mh.siip.model.preinversion.dto.FilaFuenteProgramacionRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.FuenteFinanciamientoDto;
import sv.gob.mh.siip.model.preinversion.enums.FuenteFinanciamiento;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;

/**
 * CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión": escritura de las fuentes de
 * financiamiento de una etapa y de su programación del año (alta, actualización y eliminación, RN-B.a y
 * RN-D).
 */
final class ProgramacionFinancieraPapFuentes {

    private static final String MENSAJE_FUENTE_NO_EXISTE = "El estudio, la etapa o la fuente no existen.";

    private final FuenteFinanciamientoEtapaPapRepository fuenteRepository;
    private final ProgCuatrimestralFinancieraRepository progRepository;
    private final ProgramacionFinancieraPapCalculos calculos;

    ProgramacionFinancieraPapFuentes(FuenteFinanciamientoEtapaPapRepository fuenteRepository,
            ProgCuatrimestralFinancieraRepository progRepository) {
        this.fuenteRepository = fuenteRepository;
        this.progRepository = progRepository;
        this.calculos = new ProgramacionFinancieraPapCalculos(fuenteRepository, progRepository);
    }

    /** Guarda una fila (fuente de financiamiento) de la etapa y su programación cuatrimestral del año. */
    void guardarFila(EtapaPreinversion etapa, FilaFuenteProgramacionRequestDto fila, Integer anio) {
        FuenteFinanciamientoEtapaPap fuente;
        Long idFuente = fila.getIdFuente();
        if (idFuente != null) {
            fuente = fuenteRepository.findById(idFuente)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "La fuente de financiamiento indicada no existe."));
            boolean bloqueada = calculos.tieneEjecucionAnterior(fuente.getId(), anio);
            if (!bloqueada) {
                aplicarDatosFuente(fuente, fila);
            }
            // RN-B.a: en filas ya existentes de un estudio de arrastre, se ignora cualquier cambio
            // a fuenteFinanciamiento/fuenteRecursos/convenios (sin código de error especifico).
        } else {
            fuente = FuenteFinanciamientoEtapaPap.builder().etapaPreinversion(etapa).build();
            aplicarDatosFuente(fuente, fila);
        }
        FuenteFinanciamientoEtapaPap fuenteGuardada = fuenteRepository.save(fuente);

        ProgCuatrimestralFinanciera prog = progRepository.findByFuenteIdAndAnio(fuenteGuardada.getId(), anio)
                .orElseGet(() -> ProgCuatrimestralFinanciera.builder().fuente(fuenteGuardada).anio(anio).build());
        prog.setMontoCuatrimestre1(ProgramacionPapSoporte.bd(fila.getMontoCuatrimestre1()));
        prog.setMontoCuatrimestre2(ProgramacionPapSoporte.bd(fila.getMontoCuatrimestre2()));
        prog.setMontoCuatrimestre3(ProgramacionPapSoporte.bd(fila.getMontoCuatrimestre3()));
        progRepository.save(prog);
    }

    /**
     * Elimina todas las fuentes del estudio y su programación ("Desactivar código"), salvo que el
     * estudio sea de arrastre.
     */
    void eliminarDelEstudio(Long idProyecto, Integer anio) {
        if (calculos.esArrastre(idProyecto, anio)) {
            // [SUPUESTO] RN-D: sin un módulo de ejecución financiera/física real, se asume que
            // cualquier estudio de arrastre tiene programación pendiente de completar.
            throw new ConflictoEstadoException("ESTUDIO_ARRASTRE_INCOMPLETO",
                    "El estudio es de arrastre y no completó el 100% de lo programado física o financieramente"
                            + " en periodos anteriores.");
        }

        List<FuenteFinanciamientoEtapaPap> fuentes = fuenteRepository.findByEtapaPreinversionProyectoId(idProyecto);
        eliminarFuentes(fuentes);
    }

    /** Elimina todas las fuentes de la etapa y su programación, salvo que alguna tenga ejecución anterior. */
    void eliminarDeEtapa(Long idEtapaPreinversion, Integer anio) {
        List<FuenteFinanciamientoEtapaPap> fuentes = fuenteRepository.findByEtapaPreinversionId(idEtapaPreinversion);
        rechazarSiTieneEjecucionAnterior(fuentes, anio);
        eliminarFuentes(fuentes);
    }

    /** Elimina la fuente "idFuente" de la etapa y su programación, salvo que tenga ejecución anterior. */
    void eliminarFuenteDeEtapa(Long idFuente, Long idEtapaPreinversion, Integer anio) {
        FuenteFinanciamientoEtapaPap fuente = fuenteRepository.findById(idFuente)
                .filter(f -> f.getEtapaPreinversion().getId().equals(idEtapaPreinversion))
                .orElseThrow(() -> new RecursoNoEncontradoException(MENSAJE_FUENTE_NO_EXISTE));

        rechazarSiTieneEjecucionAnterior(List.of(fuente), anio);
        eliminarFuentes(List.of(fuente));
    }

    private static void aplicarDatosFuente(FuenteFinanciamientoEtapaPap fuente, FilaFuenteProgramacionRequestDto fila) {
        FuenteFinanciamientoDto fuenteFinanciamiento = fila.getFuenteFinanciamiento();
        fuente.setFuenteFinanciamiento(
                fuenteFinanciamiento != null ? FuenteFinanciamiento.valueOf(fuenteFinanciamiento.name()) : null);
        fuente.setFuenteRecursos(fila.getFuenteRecursos());
        fuente.setConvenios(fila.getConvenios() != null ? new ArrayList<>(fila.getConvenios()) : new ArrayList<>());
    }

    private void rechazarSiTieneEjecucionAnterior(List<FuenteFinanciamientoEtapaPap> fuentes, Integer anio) {
        for (FuenteFinanciamientoEtapaPap fuente : fuentes) {
            if (calculos.tieneEjecucionAnterior(fuente.getId(), anio)) {
                throw new ConflictoEstadoException("EJECUCION_ANIOS_ANTERIORES",
                        "Etapa y/o Fuente de Financiamiento no puede ser eliminado,"
                                + " existe ejecución en años anteriores.");
            }
        }
    }

    private void eliminarFuentes(List<FuenteFinanciamientoEtapaPap> fuentes) {
        for (FuenteFinanciamientoEtapaPap fuente : fuentes) {
            progRepository.deleteByFuenteId(fuente.getId());
        }
        fuenteRepository.deleteAll(fuentes);
    }
}
