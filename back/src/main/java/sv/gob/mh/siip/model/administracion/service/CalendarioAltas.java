package sv.gob.mh.siip.model.administracion.service;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.exception.InconsistenciaFechaException;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.domain.Excepcion;
import sv.gob.mh.siip.model.administracion.dto.CalendarioDto;
import sv.gob.mh.siip.model.administracion.dto.CrearCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.ExcepcionDto;
import sv.gob.mh.siip.model.administracion.dto.RegistrarExcepcionRequestDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoCalendario;
import sv.gob.mh.siip.model.administracion.enums.TipoExcepcion;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-ADM-04 (Gestion de Calendarios): alta de un calendario y registro de una excepcion sobre una fecha
 * (RN10), restringidos a ADMINISTRADOR o ADMINISTRADOR_CALENDARIO (RN12).
 */
final class CalendarioAltas {

    private final ActorContexto actorContexto;
    private final CalendarioRepository calendarioRepository;
    private final CalendarioBusqueda busqueda;

    CalendarioAltas(ActorContexto actorContexto, CalendarioRepository calendarioRepository,
            CalendarioBusqueda busqueda) {
        this.actorContexto = actorContexto;
        this.calendarioRepository = calendarioRepository;
        this.busqueda = busqueda;
    }

    CalendarioDto crear(CrearCalendarioRequestDto request) {
        Usuario actor = actorContexto.exigirRol(RolUsuario.ADMINISTRADOR, RolUsuario.ADMINISTRADOR_CALENDARIO);

        if (calendarioRepository.existsByCodigo(request.getCodigo())) {
            throw new ConflictoEstadoException("Ya existe un calendario con el código indicado.");
        }
        if (request.getFechaInicio().isAfter(request.getFechaFin())) {
            throw new InconsistenciaFechaException("CALENDARIO_RANGO_INVALIDO",
                    "La fecha de inicio del calendario es posterior a la fecha de fin.");
        }

        Calendario calendario = Calendario.builder()
                .codigo(request.getCodigo())
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .fechaInicio(request.getFechaInicio())
                .fechaFin(request.getFechaFin())
                .estado(EstadoCalendario.valueOf(request.getEstado().name()))
                .administrador(actor)
                .build();

        return CalendarioDtoAssembler.aCalendarioDto(calendarioRepository.save(calendario));
    }

    ExcepcionDto registrarExcepcion(String codigoCalendario, RegistrarExcepcionRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR, RolUsuario.ADMINISTRADOR_CALENDARIO);
        Calendario calendario = busqueda.obtenerCalendario(codigoCalendario);
        CalendarioRecurrenciaFactory.exigirFechaEnmarcadaEnCalendario(request.getFecha(), calendario);

        Excepcion excepcion = Excepcion.builder()
                .calendario(calendario)
                .fecha(request.getFecha())
                .tipo(TipoExcepcion.valueOf(request.getTipo().name()))
                .descripcion(request.getDescripcion())
                .build();
        calendario.getExcepciones().add(excepcion);
        // save() invoca EntityManager.merge() (calendario ya tiene id): al cascadear, merge crea
        // una copia gestionada nueva para cada hijo recien agregado y es esa copia -no la
        // variable local 'excepcion'- la que recibe el id generado. Se recupera del resultado de
        // save() por fecha (UK_EXCEPCION_CALENDARIO_FECHA garantiza que es unica dentro del
        // calendario).
        Calendario calendarioGuardado = calendarioRepository.save(calendario);
        Excepcion excepcionGuardada = calendarioGuardado.getExcepciones().stream()
                .filter(e -> e.getFecha().equals(request.getFecha()))
                .findFirst()
                .orElseThrow();
        return CalendarioDtoAssembler.aExcepcionDto(excepcionGuardada, calendarioGuardado.getEstado());
    }
}
