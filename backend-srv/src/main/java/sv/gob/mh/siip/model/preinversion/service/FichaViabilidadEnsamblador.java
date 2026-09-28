package sv.gob.mh.siip.model.preinversion.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.preinversion.domain.DescripcionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Identificacion;
import sv.gob.mh.siip.model.preinversion.dto.FichaViabilidadResponseDto;
import sv.gob.mh.siip.model.preinversion.repository.DescripcionTecnicaRepository;
import sv.gob.mh.siip.model.preinversion.repository.IdentificacionRepository;

/**
 * Arma la parte de consulta de la ficha de Viabilidad (CU-PRE-24, Anexo A.1 / Anexo B.1): los campos
 * que se muestran sin edición y que provienen de otros casos de uso.
 *
 * <p>Lee directamente los datos guardados de cada CU de origen, sin pasar por sus servicios, porque
 * esos servicios exigen roles de formulación (Técnico URP/PRE) y el Viabilizador también consulta la
 * ficha. Un CU de origen que el proyecto aún no haya registrado deja su campo vacío. Los productos y
 * la población, el presupuesto y los indicadores los resuelven {@link FichaViabilidadProductos},
 * {@link FichaViabilidadPresupuesto} y {@link FichaViabilidadIndicadores}.
 */
@Component
@Transactional(readOnly = true)
public class FichaViabilidadEnsamblador {

    private final IdentificacionRepository identificaciones;
    private final DescripcionTecnicaRepository descripciones;
    private final FichaViabilidadProductos productos;
    private final FichaViabilidadPresupuesto presupuesto;
    private final FichaViabilidadIndicadores indicadores;

    public FichaViabilidadEnsamblador(IdentificacionRepository identificaciones,
            DescripcionTecnicaRepository descripciones,
            FichaViabilidadProductos productos,
            FichaViabilidadPresupuesto presupuesto,
            FichaViabilidadIndicadores indicadores) {
        this.identificaciones = identificaciones;
        this.descripciones = descripciones;
        this.productos = productos;
        this.presupuesto = presupuesto;
        this.indicadores = indicadores;
    }

    /**
     * Completa en la ficha los campos de consulta (Objetivo General a Indicadores de evaluación).
     *
     * @param ficha respuesta a completar
     * @param idProyecto identificador del proyecto
     */
    public void completarCamposDeConsulta(FichaViabilidadResponseDto ficha, Long idProyecto) {
        ficha.setObjetivoGeneral(identificaciones.findByProyectoId(idProyecto)
                .map(Identificacion::getObjetivoGeneral).orElse(null));
        ficha.setDescripcion(descripciones.findByProyectoId(idProyecto)
                .map(DescripcionTecnica::getDescripcion).orElse(null));
        ficha.setProductos(productos.productos(idProyecto));
        ficha.setPoblacionObjetivo(productos.poblacionObjetivo(idProyecto));
        presupuesto.completar(ficha, idProyecto);
        ficha.setIndicadoresEvaluacion(indicadores.indicadoresEvaluacion(idProyecto));
    }
}
