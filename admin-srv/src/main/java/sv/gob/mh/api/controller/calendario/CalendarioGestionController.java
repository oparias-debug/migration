package sv.gob.mh.api.controller.calendario;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.api.dto.calendario.CalendarioDto;
import sv.gob.mh.api.dto.calendario.CambiarEstadoCalendarioRequestDto;
import sv.gob.mh.api.dto.calendario.CrearCalendarioRequestDto;
import sv.gob.mh.api.dto.calendario.EditarDefinicionCalendarioRequestDto;
import sv.gob.mh.api.dto.calendario.ExcepcionDto;
import sv.gob.mh.api.dto.calendario.PeriodoInputDto;
import sv.gob.mh.api.dto.calendario.PeriodoLaboralDto;
import sv.gob.mh.api.dto.calendario.PeriodoNoLaboralDto;
import sv.gob.mh.api.dto.calendario.RegistrarExcepcionRequestDto;

/**
 * CU-ADM-04 (Gestión de Calendarios), tag "Calendarios - Gestión": alta, edición y cambio de
 * estado de calendarios y sus CalendarItems, restringidos a ADMINISTRADOR o
 * ADMINISTRADOR_CALENDARIO (RN12, 403 SIN_PERMISOS). Las rutas las declara la interfaz generada
 * del contrato y se montan bajo {@value #BASE}; los errores los traduce
 * {@code CalendariosManejadorErrores}. Las consultas están en {@link CalendarioConsultasController}.
 *
 * <p>Las operaciones las atienden {@link EscrituraCalendarios} y {@link EscrituraItemsCalendario},
 * que traducen el contrato a los handlers (CQRS de la plantilla).</p>
 */
@RestController
@RequestMapping(CalendarioGestionController.BASE)
@PreAuthorize(CalendarioGestionController.ADMINISTRA_CALENDARIOS)
public class CalendarioGestionController implements CalendariosGestinApi {

    public static final String BASE = "/api/v1";

    /** x-roles del contrato, que en Keycloak son roles de realm. */
    public static final String ADMINISTRA_CALENDARIOS = "hasAnyRole('ADMINISTRADOR', 'ADMINISTRADOR_CALENDARIO')";

    private final EscrituraCalendarios calendarios;
    private final EscrituraItemsCalendario items;

    public CalendarioGestionController(EscrituraCalendarios calendarios, EscrituraItemsCalendario items) {
        this.calendarios = calendarios;
        this.items = items;
    }

    @Override
    public ResponseEntity<CalendarioDto> crearCalendario(CrearCalendarioRequestDto crearCalendarioRequestDto) {
        return calendarios.crear(crearCalendarioRequestDto);
    }

    @Override
    public ResponseEntity<PeriodoLaboralDto> agregarPeriodoLaboral(String codigoCalendario,
            PeriodoInputDto periodoInputDto) {
        return items.agregarPeriodoLaboral(codigoCalendario, periodoInputDto);
    }

    @Override
    public ResponseEntity<PeriodoNoLaboralDto> agregarPeriodoNoLaboral(String codigoCalendario,
            PeriodoInputDto periodoInputDto) {
        return items.agregarPeriodoNoLaboral(codigoCalendario, periodoInputDto);
    }

    @Override
    public ResponseEntity<ExcepcionDto> registrarExcepcion(String codigoCalendario,
            RegistrarExcepcionRequestDto registrarExcepcionRequestDto) {
        return items.registrarExcepcion(codigoCalendario, registrarExcepcionRequestDto);
    }

    @Override
    public ResponseEntity<CalendarioDto> cambiarEstadoCalendario(String codigoCalendario,
            CambiarEstadoCalendarioRequestDto cambiarEstadoCalendarioRequestDto) {
        return calendarios.cambiarEstado(codigoCalendario, cambiarEstadoCalendarioRequestDto);
    }

    @Override
    public ResponseEntity<CalendarioDto> editarDefinicionCalendario(String codigoCalendario,
            EditarDefinicionCalendarioRequestDto editarDefinicionCalendarioRequestDto) {
        return calendarios.editarDefinicion(codigoCalendario, editarDefinicionCalendarioRequestDto);
    }
}
