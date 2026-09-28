package sv.gob.mh.siip.model.administracion.service;

import java.util.HashSet;
import java.util.Set;

import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.dto.CalendarItemInputDto;
import sv.gob.mh.siip.model.administracion.dto.CalendarioDto;
import sv.gob.mh.siip.model.administracion.dto.CambiarEstadoCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.EditarDefinicionCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.ExcepcionInputDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoCalendario;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-ADM-04 (Gestion de Calendarios): cambio de estado de un calendario y edicion de su definicion completa
 * (RN23), restringidos a ADMINISTRADOR o ADMINISTRADOR_CALENDARIO (RN12).
 */
final class CalendarioEdicion {

    private final ActorContexto actorContexto;
    private final CalendarioRepository calendarioRepository;
    private final CalendarioBusqueda busqueda;

    CalendarioEdicion(ActorContexto actorContexto, CalendarioRepository calendarioRepository,
            CalendarioBusqueda busqueda) {
        this.actorContexto = actorContexto;
        this.calendarioRepository = calendarioRepository;
        this.busqueda = busqueda;
    }

    CalendarioDto cambiarEstado(String codigoCalendario, CambiarEstadoCalendarioRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR, RolUsuario.ADMINISTRADOR_CALENDARIO);
        Calendario calendario = busqueda.obtenerCalendario(codigoCalendario);
        calendario.setEstado(EstadoCalendario.valueOf(request.getEstado().name()));
        return CalendarioDtoAssembler.aCalendarioDto(calendarioRepository.save(calendario));
    }

    CalendarioDto editarDefinicion(String codigoCalendario, EditarDefinicionCalendarioRequestDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR, RolUsuario.ADMINISTRADOR_CALENDARIO);
        Calendario calendario = busqueda.obtenerCalendario(codigoCalendario);

        Set<Long> periodosConservados = new HashSet<>();
        Set<Long> excepcionesConservadas = new HashSet<>();
        for (CalendarItemInputDto item : request.getItems()) {
            if (item instanceof ExcepcionInputDto excepcionInput) {
                excepcionesConservadas.add(
                        CalendarioEdicionItems.aplicarExcepcionInput(calendario, excepcionInput).getId());
            } else {
                periodosConservados.add(CalendarioEdicionItems.aplicarPeriodoInput(calendario, item).getId());
            }
        }
        calendario.getPeriodos().removeIf(periodo -> !periodosConservados.contains(periodo.getId()));
        calendario.getExcepciones().removeIf(excepcion -> !excepcionesConservadas.contains(excepcion.getId()));

        return CalendarioDtoAssembler.aCalendarioDto(calendarioRepository.save(calendario));
    }
}
