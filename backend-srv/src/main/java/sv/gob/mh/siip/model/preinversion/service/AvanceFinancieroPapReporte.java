package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import sv.gob.mh.siip.model.common.domain.Institucion;
import sv.gob.mh.siip.model.common.domain.UnidadEjecutora;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.repository.UnidadEjecutoraRepository;
import sv.gob.mh.siip.model.preinversion.domain.RevisionAvancePap;
import sv.gob.mh.siip.model.preinversion.dto.EstudioFilaAvancePAPDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.model.preinversion.repository.RevisionAvancePapRepository;

/**
 * CU-PRE-32 "Avance Financiero Cuatrimestral del PAP", Anexo A.6: encabezado (Institución
 * Ejecutora y comentarios DGICP) y generación del reporte en PDF o Excel a partir de las filas del
 * listado.
 */
final class AvanceFinancieroPapReporte {

    private final UnidadEjecutoraRepository unidadEjecutoraRepository;
    private final RevisionAvancePapRepository revisionRepository;

    AvanceFinancieroPapReporte(UnidadEjecutoraRepository unidadEjecutoraRepository,
            RevisionAvancePapRepository revisionRepository) {
        this.unidadEjecutoraRepository = unidadEjecutoraRepository;
        this.revisionRepository = revisionRepository;
    }

    Resource generar(Usuario actor, Long idUnidadEjecutora, Integer anio, Cuatrimestre periodo,
            List<EstudioFilaAvancePAPDto> filas, String formato) {
        ReporteAvanceFinancieroPapGenerator.Encabezado encabezado = new ReporteAvanceFinancieroPapGenerator.Encabezado(
                nombreInstitucionEjecutora(idUnidadEjecutora), anio, periodo,
                AvancePapSoporte.esActorInternoDgicp(actor),
                AvancePapSoporte.esActorInternoDgicp(actor)
                        ? comentarioReporteFinancieroDgicp(idUnidadEjecutora, anio, periodo)
                        : null);
        byte[] contenido = "PDF".equalsIgnoreCase(formato)
                ? ReporteAvanceFinancieroPapGenerator.generarPdf(encabezado, filas)
                : ReporteAvanceFinancieroPapGenerator.generarExcel(encabezado, filas);
        return new ByteArrayResource(contenido);
    }

    /** Anexo A.6 "Institución Ejecutora": nombre de la Institución a la que pertenece la Unidad Ejecutora. */
    private String nombreInstitucionEjecutora(Long idUnidadEjecutora) {
        if (idUnidadEjecutora == null) {
            return "";
        }
        return unidadEjecutoraRepository.findById(idUnidadEjecutora)
                .map(UnidadEjecutora::getInstitucion)
                .map(Institucion::getNombre)
                .orElse("");
    }

    /**
     * Anexo A.6 "Comentarios al reporte financiero DGICP": se registran en la revisión única del
     * avance del PAP (CU-PRE-33 SF-3, {@link RevisionAvancePap}) para la Unidad Ejecutora/Año/Período.
     */
    private String comentarioReporteFinancieroDgicp(Long idUnidadEjecutora, Integer anio, Cuatrimestre periodo) {
        return revisionRepository.findByIdUnidadEjecutoraAndAnioAndPeriodo(idUnidadEjecutora, anio, periodo)
                .map(RevisionAvancePap::getComentarioReporteFinancieroDgicp)
                .orElse(null);
    }
}
