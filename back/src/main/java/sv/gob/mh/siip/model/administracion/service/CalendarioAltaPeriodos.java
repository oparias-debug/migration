package sv.gob.mh.siip.model.administracion.service;

import sv.gob.mh.siip.exception.ConflictoEstadoException;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.domain.Periodo;
import sv.gob.mh.siip.model.administracion.domain.Recurrencia;
import sv.gob.mh.siip.model.administracion.dto.PeriodoInputDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoLaboralDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoNoLaboralDto;
import sv.gob.mh.siip.model.administracion.enums.TipoPeriodo;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.repository.PeriodoRepository;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-ADM-04 (Gestion de Calendarios): alta de periodos LABORAL y NO_LABORAL en un calendario (RN08, RN10,
 * RN15), restringida a ADMINISTRADOR o ADMINISTRADOR_CALENDARIO (RN12).
 */
final class CalendarioAltaPeriodos {

    private final ActorContexto actorContexto;
    private final CalendarioRepository calendarioRepository;
    private final PeriodoRepository periodoRepository;
    private final CalendarioBusqueda busqueda;

    CalendarioAltaPeriodos(ActorContexto actorContexto, CalendarioRepository calendarioRepository,
            PeriodoRepository periodoRepository, CalendarioBusqueda busqueda) {
        this.actorContexto = actorContexto;
        this.calendarioRepository = calendarioRepository;
        this.periodoRepository = periodoRepository;
        this.busqueda = busqueda;
    }

    PeriodoLaboralDto agregarPeriodoLaboral(String codigoCalendario, PeriodoInputDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR, RolUsuario.ADMINISTRADOR_CALENDARIO);
        Periodo periodo = agregarPeriodo(codigoCalendario, request, TipoPeriodo.LABORAL);
        return CalendarioDtoAssembler.aPeriodoLaboralDto(periodo,
                busqueda.obtenerCalendario(codigoCalendario).getEstado());
    }

    PeriodoNoLaboralDto agregarPeriodoNoLaboral(String codigoCalendario, PeriodoInputDto request) {
        actorContexto.exigirRol(RolUsuario.ADMINISTRADOR, RolUsuario.ADMINISTRADOR_CALENDARIO);
        Periodo periodo = agregarPeriodo(codigoCalendario, request, TipoPeriodo.NO_LABORAL);
        return CalendarioDtoAssembler.aPeriodoNoLaboralDto(periodo,
                busqueda.obtenerCalendario(codigoCalendario).getEstado());
    }

    private Periodo agregarPeriodo(String codigoCalendario, PeriodoInputDto request, TipoPeriodo tipo) {
        Calendario calendario = busqueda.obtenerCalendario(codigoCalendario);
        if (periodoRepository.existsByCalendario_CodigoAndCodigo(codigoCalendario, request.getCodigo())) {
            throw new ConflictoEstadoException("Ya existe un período con ese código dentro del calendario.");
        }

        Recurrencia recurrencia = CalendarioRecurrenciaFactory.aRecurrencia(request.getRecurrencia(), calendario);
        Periodo periodo = Periodo.builder()
                .codigo(request.getCodigo())
                .nombre(request.getNombre())
                .tipo(tipo)
                .calendario(calendario)
                .recurrencia(recurrencia)
                .build();
        calendario.getPeriodos().add(periodo);
        // Igual que en registrarExcepcion: save() cascada un merge, que crea una copia gestionada
        // nueva para el periodo recien agregado (id se asigna a esa copia, no a esta variable
        // local); se recupera del resultado de save() por codigo, unico dentro del calendario
        // (ver existsByCalendario_CodigoAndCodigo arriba).
        Calendario calendarioGuardado = calendarioRepository.save(calendario);
        return calendarioGuardado.getPeriodos().stream()
                .filter(p -> p.getCodigo().equals(request.getCodigo()))
                .findFirst()
                .orElseThrow();
    }
}
