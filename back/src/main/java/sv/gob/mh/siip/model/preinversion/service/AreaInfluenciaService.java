package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaDto;
import sv.gob.mh.siip.model.preinversion.dto.AreaInfluenciaRequestDto;

public interface AreaInfluenciaService {

    AreaInfluenciaDto obtener(Long idProyecto);

    AreaInfluenciaDto guardar(Long idProyecto, AreaInfluenciaRequestDto request);

    AreaInfluenciaDto autocompletarDesdePoblacionObjetivo(Long idProyecto);
}