package sv.gob.mh.siip.model.preinversion.service;

import sv.gob.mh.siip.model.preinversion.dto.HabilitarModificacionesFueraPlazoRequestDto;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;

/**
 * Programación Financiera Cuatrimestral del PAP (CU-PRE-30): ajustes sobre la programación ya registrada,
 * fuera del flujo normal de registro: bajas (desactivar un estudio, eliminar una etapa o una fuente, RN-D)
 * y habilitación de modificaciones fuera de plazo (RN-A.b).
 */
public interface ProgramacionFinancieraPapAjusteService {

    void desactivarEstudio(String cup, Integer anio);

    void eliminarEtapaProgramacion(String cup, NombreEtapaDto etapa, Integer anio);

    void eliminarFuenteFinanciamiento(String cup, NombreEtapaDto etapa, Long idFuente, Integer anio);

    void habilitarModificacionesFueraPlazo(HabilitarModificacionesFueraPlazoRequestDto request);
}
