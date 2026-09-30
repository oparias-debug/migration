package sv.gob.mh.siip.model.preinversion.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;

/**
 * Etapas de la Ruta de Preinversión (CU-PRE-3.5) sobre las que se gestiona la Opinión Técnica
 * (CU-PRE-26, Anexo B.1 "Etapa actual" y "Etapa futura").
 *
 * <p>La OT se emite sobre el estudio de la etapa actual, que queda marcada con
 * {@code tieneOpinionTecnica}; la etapa futura es la siguiente de la ruta, a la que la OT habilita el
 * paso (Anexo A2 a y e: "Opinión Técnica ... para la etapa de [Etapa Futura]").
 */
@Component
@Transactional
public class EtapasOpinionTecnica {

    /**
     * @param actual etapa que se gestiona; {@code null} si el proyecto no tiene etapas registradas
     * @param futura etapa siguiente de la ruta; {@code null} si la actual es la última
     */
    public record Etapas(TipoEtapaPreinversion actual, TipoEtapaPreinversion futura) {

        /** RN11: la OT para la etapa de Ejecución deja el proyecto disponible en Captura de Proyectos. */
        public boolean habilitaEjecucion() {
            return futura == TipoEtapaPreinversion.EJECUCION || actual == TipoEtapaPreinversion.EJECUCION;
        }
    }

    private final EtapaPreinversionRepository etapaRepository;

    public EtapasOpinionTecnica(EtapaPreinversionRepository etapaRepository) {
        this.etapaRepository = etapaRepository;
    }

    /**
     * Opinión Técnica por primera vez: la primera etapa de la ruta que todavía no tiene OT.
     *
     * @param idProyecto identificador del proyecto
     * @return las etapas de la gestión
     */
    @Transactional(readOnly = true)
    public Etapas paraOpinionTecnica(Long idProyecto) {
        List<EtapaPreinversion> ruta = vigentes(idProyecto);
        for (int i = 0; i < ruta.size(); i++) {
            if (!Boolean.TRUE.equals(ruta.get(i).getTieneOpinionTecnica())) {
                return etapas(ruta, i);
            }
        }
        return ruta.isEmpty() ? new Etapas(null, null) : etapas(ruta, ruta.size() - 1);
    }

    /**
     * Actualización de OT (FA04 paso 4.4): la etapa más avanzada que ya tiene OT.
     *
     * @param idProyecto identificador del proyecto
     * @return las etapas de la gestión, o vacío si ninguna etapa tiene OT previa
     */
    @Transactional(readOnly = true)
    public Optional<Etapas> paraActualizacion(Long idProyecto) {
        List<EtapaPreinversion> ruta = vigentes(idProyecto);
        for (int i = ruta.size() - 1; i >= 0; i--) {
            if (Boolean.TRUE.equals(ruta.get(i).getTieneOpinionTecnica())) {
                return Optional.of(etapas(ruta, i));
            }
        }
        return Optional.empty();
    }

    /**
     * Registra que la etapa ya tiene Opinión Técnica (lo consulta CU-PRE-3.5, RN13).
     *
     * @param idProyecto identificador del proyecto
     * @param etapa etapa sobre la que se emitió la OT; {@code null} no hace nada
     */
    public void marcarEmitida(Long idProyecto, TipoEtapaPreinversion etapa) {
        if (etapa == null) {
            return;
        }
        etapaRepository.findByProyectoIdAndTipoEtapa(idProyecto, etapa).ifPresent((EtapaPreinversion e) -> {
            e.setTieneOpinionTecnica(true);
            etapaRepository.save(e);
        });
    }

    /** Etapas en orden de ruta, sin las que una modificación de la ruta dejó fuera (RN13 de CU-PRE-3.5). */
    private List<EtapaPreinversion> vigentes(Long idProyecto) {
        return etapaRepository.findByProyectoId(idProyecto).stream()
                .sorted(Comparator.comparing(EtapaPreinversion::getTipoEtapa))
                .filter(e -> !Boolean.TRUE.equals(e.getBloqueadaPorModificacion()))
                .toList();
    }

    private static Etapas etapas(List<EtapaPreinversion> ruta, int indice) {
        TipoEtapaPreinversion futura = indice + 1 < ruta.size() ? ruta.get(indice + 1).getTipoEtapa() : null;
        return new Etapas(ruta.get(indice).getTipoEtapa(), futura);
    }
}
