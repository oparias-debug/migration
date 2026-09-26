package sv.gob.mh.siip.model.administracion.service;

import sv.gob.mh.siip.model.administracion.dto.CalendarioDto;
import sv.gob.mh.siip.model.administracion.dto.CambiarEstadoCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CrearCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.EditarDefinicionCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.ExcepcionDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoInputDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoLaboralDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoNoLaboralDto;
import sv.gob.mh.siip.model.administracion.dto.RegistrarExcepcionRequestDto;

/**
 * CU-ADM-04: gestion de calendarios laborales (alta, CalendarItems, cambio de estado y edicion). Las
 * consultas estan en {@link CalendarioConsultaService}.
 */
public interface CalendarioService {

    CalendarioDto crear(CrearCalendarioRequestDto request);

    PeriodoLaboralDto agregarPeriodoLaboral(String codigoCalendario, PeriodoInputDto request);

    PeriodoNoLaboralDto agregarPeriodoNoLaboral(String codigoCalendario, PeriodoInputDto request);

    ExcepcionDto registrarExcepcion(String codigoCalendario, RegistrarExcepcionRequestDto request);

    CalendarioDto cambiarEstado(String codigoCalendario, CambiarEstadoCalendarioRequestDto request);

    CalendarioDto editarDefinicion(String codigoCalendario, EditarDefinicionCalendarioRequestDto request);
}
