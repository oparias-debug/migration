package sv.gob.mh.siip.bdd.support;

import java.time.LocalDateTime;

import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.preinversion.domain.EjeTematico;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.model.programacion.domain.SectorActividad;

/**
 * Builders reutilizados por los steps BDD de CU-PRE-30 (Programación Financiera Cuatrimestral del
 * PAP): un estudio con CUP asignado y sus etapas de la Ruta de Preinversión (CU-PRE-3.5, no
 * ejercitado aquí: se persisten directamente las filas de {@link EtapaPreinversion} que ese CU
 * produciría, mismo criterio que otros steps de este paquete simulan el efecto de CUs upstream no
 * ejercitados dentro del mismo escenario).
 */
public final class Pre30Fixtures {

    private Pre30Fixtures() {
    }

    public static Proyecto nuevoEstudio(ProyectoRepository proyectoRepository, UnidadEjecutora unidadEjecutora,
            Institucion institucion, SectorActividad sector, EjeTematico ejeTematico, String cup) {
        Proyecto proyecto = ProyectoFixtures.nuevoProyecto("Estudio PAP BDD " + cup, EstadoProyecto.CUP_ASIGNADO,
                unidadEjecutora, institucion, sector, ejeTematico);
        proyecto.setCup(cup);
        return proyectoRepository.save(proyecto);
    }

    public static EtapaPreinversion nuevaEtapa(EtapaPreinversionRepository etapaPreinversionRepository,
            Proyecto proyecto, TipoEtapaPreinversion tipoEtapa, Double costo) {
        return etapaPreinversionRepository.save(EtapaPreinversion.builder()
                .proyecto(proyecto)
                .tipoEtapa(tipoEtapa)
                .fechaSeleccion(LocalDateTime.now())
                .costo(costo)
                .habilitadoParaRegistro(true)
                .build());
    }
}
