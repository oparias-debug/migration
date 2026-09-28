package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.model.preinversion.dto.AnalisisMercadoDto;
import sv.gob.mh.siip.model.preinversion.dto.AnalisisMercadoRequestDto;

public interface AnalisisMercadoService {

    AnalisisMercadoDto obtener(Long idProyecto);

    AnalisisMercadoDto guardar(Long idProyecto, AnalisisMercadoRequestDto request);
}
