package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.preinversion.domain.Identificacion;
import sv.gob.mh.siip.model.preinversion.domain.Localizacion;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.dto.FichaViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.IndicadorEvaluacionDto;
import sv.gob.mh.siip.model.preinversion.dto.IndicadorInformeOpinionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.dto.InformeOpinionTecnicaResponseDto;
import sv.gob.mh.siip.model.preinversion.dto.LocalizacionInformeOpinionTecnicaDto;
import sv.gob.mh.siip.model.preinversion.repository.IdentificacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.LocalizacionRepository;

/**
 * "REPORTE DE OPINIÓN TÉCNICA" (Anexo A.6), que se abre desde el Histórico de OT (Anexo A1.5). Los datos
 * del proyecto se toman de sus pantallas de formulación con el mismo armado que la ficha de CU-PRE-24;
 * los de la gestión, de la OT emitida.
 */
@Component
@Transactional(readOnly = true)
public class InformeOpinionTecnicaEnsamblador {

    public static final String OPINION_TECNICA_NO_EMITIDA = "OPINION_TECNICA_NO_EMITIDA";

    /** Texto del campo "Opinión Técnica" (Anexo B.1). */
    public static final String FAVORABLE = "FAVORABLE";

    private final FichaViabilidadEnsamblador ficha;
    private final IdentificacionRepository identificaciones;
    private final LocalizacionRepository localizaciones;

    public InformeOpinionTecnicaEnsamblador(FichaViabilidadEnsamblador ficha,
            IdentificacionRepository identificaciones,
            LocalizacionRepository localizaciones) {
        this.ficha = ficha;
        this.identificaciones = identificaciones;
        this.localizaciones = localizaciones;
    }

    /**
     * @param contexto contexto de consulta de una gestión de OT
     * @return el informe de la OT emitida
     * @throws ConflictoEstadoException (409) si la gestión no es una OT favorable
     */
    public InformeOpinionTecnicaResponseDto informe(OpinionTecnicaContexto contexto) {
        OpinionTecnica gestion = contexto.gestion();
        if (!gestion.esFavorable()) {
            throw new ConflictoEstadoException(OPINION_TECNICA_NO_EMITIDA,
                    "El informe solo está disponible para una Opinión Técnica emitida.");
        }
        Long idProyecto = contexto.proyecto().getId();
        var datos = new FichaViabilidadResponseDto();
        ficha.completarCamposDeConsulta(datos, idProyecto);

        var dto = new InformeOpinionTecnicaResponseDto(gestion.getId(),
                OpinionTecnicaEnsamblador.encabezado(contexto.proyecto(), gestion), datos.getProductos(),
                localizacion(idProyecto), indicadores(datos.getIndicadoresEvaluacion()), FAVORABLE);
        dto.setNumeroNotaOt(gestion.getNumeroNotaOt());
        dto.setProblemaCentral(identificaciones.findByProyectoId(idProyecto)
                .map(Identificacion::getProblemaCentral).orElse(null));
        dto.setObjetivoGeneral(datos.getObjetivoGeneral());
        dto.setDescripcionProyecto(datos.getDescripcion());
        dto.setPoblacionObjetivo(datos.getPoblacionObjetivo());
        dto.setInversionEstimada(datos.getInversionEstimada());
        dto.setResumenPresupuesto(datos.getResumenPresupuesto());
        dto.setCostoOperacion(datos.getCostoOperacion());
        dto.setCostoMantenimiento(datos.getCostoMantenimiento());
        dto.setFuenteFinanciamiento(datos.getFuenteFinanciamiento());
        dto.setFechaSolicitud(OpinionTecnicaEnsamblador.fecha(gestion.getFechaSolicitud()));
        dto.setFechaAjustes(OpinionTecnicaEnsamblador.fecha(gestion.getFechaAjustes()));
        dto.setFechaEmisionOt(OpinionTecnicaEnsamblador.fecha(gestion.getFechaEmision()));
        dto.setConclusiones(gestion.getRevisionConclusiones().getTexto());
        return dto;
    }

    private List<LocalizacionInformeOpinionTecnicaDto> localizacion(Long idProyecto) {
        return localizaciones.findAllByProyectoId(idProyecto).stream()
                .map((Localizacion l) -> new LocalizacionInformeOpinionTecnicaDto()
                        .departamento(l.getDepartamento() == null ? null : l.getDepartamento().getNombre())
                        .municipio(l.getMunicipio() == null ? null : l.getMunicipio().getNombre())
                        .direccion(l.getDireccion()))
                .toList();
    }

    private static List<IndicadorInformeOpinionTecnicaDto> indicadores(List<IndicadorEvaluacionDto> indicadores) {
        return indicadores.stream()
                .map((IndicadorEvaluacionDto i) -> new IndicadorInformeOpinionTecnicaDto(i.getNombre())
                        .valor(i.getValor()))
                .toList();
    }
}
