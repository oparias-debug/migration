package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import sv.gob.mh.siip.exception.NoAutenticadoException;
import sv.gob.mh.siip.model.preinversion.dto.MedidaCatalogoDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoMedidaCatalogoDto;

/** Catálogo de referencia del botón "Ver descripción de categorías" (Anexos C.1, C.1.5, C.2). */
public interface MedidaCatalogoService {

    /**
     * Lista las medidas del catálogo (GRD, GRC o ACC) del tipo indicado, ordenadas por código.
     *
     * @throws NoAutenticadoException si no hay actor autenticado.
     */
    List<MedidaCatalogoDto> listar(TipoMedidaCatalogoDto tipo);
}
