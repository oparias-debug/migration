package sv.gob.mh.siip.model.administracion.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.administracion.dto.CalendarioDto;
import sv.gob.mh.siip.model.administracion.dto.CambiarEstadoCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CrearCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.EditarDefinicionCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.ExcepcionDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoInputDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoLaboralDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoNoLaboralDto;
import sv.gob.mh.siip.model.administracion.dto.RegistrarExcepcionRequestDto;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.repository.PeriodoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-ADM-04 (Gestion de Calendarios): gestion de calendarios, restringida a ADMINISTRADOR o
 * ADMINISTRADOR_CALENDARIO (RN12). La logica vive en {@link CalendarioAltas} (alta del calendario y
 * registro de excepciones), {@link CalendarioAltaPeriodos} (alta de periodos) y {@link CalendarioEdicion}
 * (cambio de estado y edicion de la definicion); esta clase mantiene el control transaccional. Las
 * consultas estan en {@link CalendarioConsultaServiceImpl}.
 */
@Service
@Transactional
public class CalendarioServiceImpl implements CalendarioService {

    private final CalendarioAltas altas;
    private final CalendarioAltaPeriodos altaPeriodos;
    private final CalendarioEdicion edicion;

    public CalendarioServiceImpl(CalendarioRepository calendarioRepository, PeriodoRepository periodoRepository,
            ActorContexto actorContexto) {
        CalendarioBusqueda busqueda = new CalendarioBusqueda(calendarioRepository, periodoRepository);
        this.altas = new CalendarioAltas(actorContexto, calendarioRepository, busqueda);
        this.altaPeriodos = new CalendarioAltaPeriodos(actorContexto, calendarioRepository, periodoRepository,
                busqueda);
        this.edicion = new CalendarioEdicion(actorContexto, calendarioRepository, busqueda);
    }

    @Override
    public CalendarioDto crear(CrearCalendarioRequestDto request) {
        return altas.crear(request);
    }

    @Override
    public PeriodoLaboralDto agregarPeriodoLaboral(String codigoCalendario, PeriodoInputDto request) {
        return altaPeriodos.agregarPeriodoLaboral(codigoCalendario, request);
    }

    @Override
    public PeriodoNoLaboralDto agregarPeriodoNoLaboral(String codigoCalendario, PeriodoInputDto request) {
        return altaPeriodos.agregarPeriodoNoLaboral(codigoCalendario, request);
    }

    @Override
    public ExcepcionDto registrarExcepcion(String codigoCalendario, RegistrarExcepcionRequestDto request) {
        return altas.registrarExcepcion(codigoCalendario, request);
    }

    @Override
    public CalendarioDto cambiarEstado(String codigoCalendario, CambiarEstadoCalendarioRequestDto request) {
        return edicion.cambiarEstado(codigoCalendario, request);
    }

    @Override
    public CalendarioDto editarDefinicion(String codigoCalendario, EditarDefinicionCalendarioRequestDto request) {
        return edicion.editarDefinicion(codigoCalendario, request);
    }
}
