package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.EtapaMetaFisicaPap;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.repository.EtapaMetaFisicaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.FuenteFinanciamientoEtapaPapRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgCuatrimestralFinancieraRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-PRE-30 "Programación Cuatrimestral Financiera de la Preinversión": desactivar un estudio y
 * eliminar una etapa o una fuente de financiamiento de su programación (RN-D), con el control de
 * roles y la sincronización con la Programación de Metas Físicas (SF-4/SF-5 de CU-PRE-31).
 */
final class ProgramacionFinancieraPapEliminacion {

    private static final String MENSAJE_FUENTE_NO_EXISTE = "El estudio, la etapa o la fuente no existen.";

    private final ActorContexto actorContexto;
    private final EtapaMetaFisicaPapRepository etapaMetaFisicaPapRepository;
    private final ProgramacionPapConsultas consultas;
    private final ProgramacionFinancieraPapPlazo plazo;
    private final ProgramacionFinancieraPapFuentes fuentes;

    /**
     * Recibe ya construidos la búsqueda de estudios y etapas ({@code consultas}) y el control del período
     * de ingreso ({@code plazo}), que el servicio comparte con la habilitación fuera de plazo.
     */
    ProgramacionFinancieraPapEliminacion(ActorContexto actorContexto, ProgramacionPapConsultas consultas,
            ProgramacionFinancieraPapPlazo plazo, FuenteFinanciamientoEtapaPapRepository fuenteRepository,
            ProgCuatrimestralFinancieraRepository progRepository,
            EtapaMetaFisicaPapRepository etapaMetaFisicaPapRepository) {
        this.actorContexto = actorContexto;
        this.etapaMetaFisicaPapRepository = etapaMetaFisicaPapRepository;
        this.consultas = consultas;
        this.plazo = plazo;
        this.fuentes = new ProgramacionFinancieraPapFuentes(fuenteRepository, progRepository);
    }

    void desactivarEstudio(String cup, Integer anio) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = consultas.buscarEstudio(cup);
        plazo.verificarPeriodoAbierto(proyecto.getUnidadEjecutora().getId(), anio);

        fuentes.eliminarDelEstudio(proyecto.getId(), anio);
        // SF-4 (CU-PRE-31): al desactivar el código, se desactiva también en la Programación de
        // Metas Físicas.
        sincronizarDesactivacionMetasFisicas(proyecto.getId());
    }

    void eliminarEtapaProgramacion(String cup, NombreEtapaDto etapaDto, Integer anio) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = consultas.buscarEstudio(cup);
        EtapaPreinversion etapa = consultas.buscarEtapa(proyecto.getId(), etapaDto,
                "El estudio o la etapa no existen.");

        fuentes.eliminarDeEtapa(etapa.getId(), anio);
        // SF-5 (CU-PRE-31): al eliminar la etapa, se desactiva también en la Programación de Metas
        // Físicas.
        sincronizarEliminacionEtapaMetasFisicas(etapa.getId());
    }

    void eliminarFuenteFinanciamiento(String cup, NombreEtapaDto etapaDto, Long idFuente, Integer anio) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto proyecto = consultas.buscarEstudio(cup);
        EtapaPreinversion etapa = consultas.buscarEtapa(proyecto.getId(), etapaDto, MENSAJE_FUENTE_NO_EXISTE);

        fuentes.eliminarFuenteDeEtapa(idFuente, etapa.getId(), anio);
    }

    /**
     * SF-4 (CU-PRE-31): sincroniza la desactivación de un código con la Programación de Metas
     * Físicas. El documento dice "automáticamente se desactivará": se marca cada meta física como
     * inactiva (se conserva el registro y su programación cuatrimestral), no se borra.
     */
    private void sincronizarDesactivacionMetasFisicas(Long idProyecto) {
        List<EtapaMetaFisicaPap> etapasMeta = etapaMetaFisicaPapRepository
                .findByEtapaPreinversionProyectoId(idProyecto);
        etapasMeta.forEach(etapaMeta -> etapaMeta.setActivo(Boolean.FALSE));
        etapaMetaFisicaPapRepository.saveAll(etapasMeta);
    }

    /**
     * SF-5 (CU-PRE-31): al eliminar la etapa en CU-PRE-30, "automáticamente se desactivará en la
     * programación por Metas Físicas" — mismo criterio que SF-4 (marca inactiva, sin borrar).
     */
    private void sincronizarEliminacionEtapaMetasFisicas(Long idEtapaPreinversion) {
        etapaMetaFisicaPapRepository.findByEtapaPreinversionId(idEtapaPreinversion)
                .ifPresent((EtapaMetaFisicaPap etapaMeta) -> {
                    etapaMeta.setActivo(Boolean.FALSE);
                    etapaMetaFisicaPapRepository.save(etapaMeta);
                });
    }
}
