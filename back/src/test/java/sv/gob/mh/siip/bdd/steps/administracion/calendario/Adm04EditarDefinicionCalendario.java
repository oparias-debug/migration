package sv.gob.mh.siip.bdd.steps.administracion.calendario;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CalendarioFixtures;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.dto.CalendarItemDto;
import sv.gob.mh.siip.model.administracion.dto.CalendarItemInputDto;
import sv.gob.mh.siip.model.administracion.dto.CalendarioDto;
import sv.gob.mh.siip.model.administracion.dto.EditarDefinicionCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.dto.ExcepcionDto;
import sv.gob.mh.siip.model.administracion.dto.ExcepcionInputDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoLaboralDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoLaboralInputDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoNoLaboralDto;
import sv.gob.mh.siip.model.administracion.dto.PeriodoNoLaboralInputDto;
import sv.gob.mh.siip.model.administracion.dto.RecurrenciaUnaVezDto;
import sv.gob.mh.siip.model.administracion.enums.TipoPeriodo;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.repository.PeriodoRepository;
import sv.gob.mh.siip.model.administracion.service.CalendarioService;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/** CU-ADM-04-14-editar-definicion-calendario.feature. */
public class Adm04EditarDefinicionCalendario {

    private final UsuarioRepository usuarioRepository;
    private final CalendarioRepository calendarioRepository;
    private final PeriodoRepository periodoRepository;
    private final CalendarioService calendarioService;
    private final ContextoValidacionBdd contextoValidacion;

    // Real codigo del calendario creado por el Dado (distinto del literal del .feature): ver nota en
    // Adm04ComunCalendario sobre por que los escenarios no pueden compartir codigos literales.
    private String codigoCalendarioReal;
    private CalendarioDto definicionAntes;
    private CalendarioDto ultimoResultado;
    private String ultimaAccion;
    private String ultimoCodigoItem;

    public Adm04EditarDefinicionCalendario(UsuarioRepository usuarioRepository,
            CalendarioRepository calendarioRepository, PeriodoRepository periodoRepository,
            CalendarioService calendarioService, ContextoValidacionBdd contextoValidacion) {
        this.usuarioRepository = usuarioRepository;
        this.calendarioRepository = calendarioRepository;
        this.periodoRepository = periodoRepository;
        this.calendarioService = calendarioService;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^que existe un calendario ACTIVO con código \"([^\"]*)\" con un período LABORAL \"([^\"]*)\"$")
    public void que_existe_un_calendario_activo_con_un_periodo_laboral(String codigoCalendarioLiteral,
            String codigoPeriodo) {
        codigoCalendarioReal = codigoCalendarioLiteral + "-" + CalendarioFixtures.nuevoSufijo();
        String nombreUsuario = "admin.calendario.bdd." + CalendarioFixtures.nuevoSufijo();
        var administrador = usuarioRepository.save(CalendarioFixtures.nuevoAdministradorCalendario(nombreUsuario));
        Calendario calendario = CalendarioFixtures.nuevoCalendario(codigoCalendarioReal, administrador);
        CalendarioFixtures.agregarPeriodo(calendario, codigoPeriodo, TipoPeriodo.LABORAL,
                CalendarioFixtures.recurrenciaUnaVez(LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 20)));
        calendarioRepository.save(calendario);
        CalendarioFixtures.autenticarComo(nombreUsuario);
    }

    @Dado("^que el actor tiene el rol \"([^\"]*)\" y recupera el calendario \"([^\"]*)\" para editarlo$")
    public void que_el_actor_tiene_el_rol_y_recupera_el_calendario_para_editarlo(String rol,
            String codigoCalendarioLiteral) {
        String nombreUsuario = "actor.calendario.bdd." + CalendarioFixtures.nuevoSufijo();
        usuarioRepository.save(CalendarioFixtures.nuevoUsuarioConRol(nombreUsuario, RolUsuario.valueOf(rol)));
        CalendarioFixtures.autenticarComo(nombreUsuario);
        definicionAntes = calendarioService.recuperarDefinicion(codigoCalendarioReal);
    }

    @Cuando("^el actor \"([^\"]*)\" el CalendarItem \"([^\"]*)\" del calendario \"([^\"]*)\"$")
    public void el_actor_aplica_una_accion_sobre_el_calendaritem(String accion, String itemTexto,
            String codigoCalendarioLiteral) {
        String codigoItem = itemTexto.split(" ")[0].trim();
        ultimaAccion = accion;
        ultimoCodigoItem = codigoItem;

        EditarDefinicionCalendarioRequestDto request = new EditarDefinicionCalendarioRequestDto();
        for (CalendarItemDto item : definicionAntes.getItems()) {
            boolean esElItemDeLaAccion = coincideCodigo(item, codigoItem);
            if (esElItemDeLaAccion && "elimina".equals(accion)) {
                continue;
            }
            if (esElItemDeLaAccion && "edita".equals(accion) && item instanceof PeriodoLaboralDto laboral) {
                request.addItemsItem(new PeriodoLaboralInputDto().id(laboral.getId()).tipoItem("LABORAL")
                        .codigo(laboral.getCodigo()).nombre(laboral.getNombre() + " editado")
                        .recurrencia(laboral.getRecurrencia()));
                continue;
            }
            request.addItemsItem(aInputSinCambios(item));
        }
        if ("adiciona".equals(accion)) {
            request.addItemsItem(nuevoPeriodoNoLaboralInput(codigoItem));
        }

        ultimoResultado = calendarioService.editarDefinicion(codigoCalendarioReal, request);
    }

    @Cuando("^el actor intenta adicionar un CalendarItem con código \"([^\"]*)\" al calendario \"([^\"]*)\"$")
    public void el_actor_intenta_adicionar_un_calendaritem_con_codigo_duplicado(String codigo,
            String codigoCalendarioLiteral) {
        intentarEditar(codigoCalendarioReal, nuevoPeriodoNoLaboralInput(codigo));
    }

    @Cuando("^el actor edita el período \"([^\"]*)\" del calendario \"([^\"]*)\" con fecha de inicio posterior a la fecha de fin$")
    public void el_actor_edita_el_periodo_con_fechas_invertidas(String codigoPeriodo,
            String codigoCalendarioLiteral) {
        Long id = periodoRepository.findByCalendario_CodigoAndCodigo(codigoCalendarioReal, codigoPeriodo)
                .orElseThrow().getId();
        RecurrenciaUnaVezDto recurrencia = new RecurrenciaUnaVezDto().tipo("UNA_VEZ")
                .fechaInicio(LocalDate.of(2026, 6, 30)).fechaFin(LocalDate.of(2026, 1, 5));
        intentarEditar(codigoCalendarioReal, new PeriodoLaboralInputDto().id(id).tipoItem("LABORAL")
                .codigo(codigoPeriodo).nombre("Período editado BDD").recurrencia(recurrencia));
    }

    @Cuando("^el actor edita el período \"([^\"]*)\" del calendario \"([^\"]*)\" con fechas fuera del rango del calendario$")
    public void el_actor_edita_el_periodo_fuera_de_rango(String codigoPeriodo, String codigoCalendarioLiteral) {
        Long id = periodoRepository.findByCalendario_CodigoAndCodigo(codigoCalendarioReal, codigoPeriodo)
                .orElseThrow().getId();
        RecurrenciaUnaVezDto recurrencia = new RecurrenciaUnaVezDto().tipo("UNA_VEZ")
                .fechaInicio(LocalDate.of(2025, 12, 1)).fechaFin(LocalDate.of(2026, 1, 15));
        intentarEditar(codigoCalendarioReal, new PeriodoLaboralInputDto().id(id).tipoItem("LABORAL")
                .codigo(codigoPeriodo).nombre("Período editado BDD").recurrencia(recurrencia));
    }

    @Cuando("^el actor intenta recuperar para edición el calendario \"([^\"]*)\"$")
    public void el_actor_intenta_recuperar_para_edicion_un_calendario_inexistente(
            String codigoCalendarioLiteral) {
        try {
            calendarioService.recuperarDefinicion(codigoCalendarioLiteral);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Cuando("^el actor intenta editar la definición del calendario \"([^\"]*)\"$")
    public void el_actor_intenta_editar_la_definicion_sin_permisos(String codigoCalendarioLiteral) {
        intentarEditar(codigoCalendarioReal, nuevoPeriodoNoLaboralInput("NOLAB-" + CalendarioFixtures.nuevoSufijo()));
    }

    private void intentarEditar(String codigoCalendario, CalendarItemInputDto item) {
        EditarDefinicionCalendarioRequestDto request = new EditarDefinicionCalendarioRequestDto();
        request.addItemsItem(item);
        try {
            ultimoResultado = calendarioService.editarDefinicion(codigoCalendario, request);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    private boolean coincideCodigo(CalendarItemDto item, String codigo) {
        if (item instanceof PeriodoLaboralDto p) {
            return p.getCodigo().equals(codigo);
        }
        if (item instanceof PeriodoNoLaboralDto p) {
            return p.getCodigo().equals(codigo);
        }
        return false;
    }

    private CalendarItemInputDto aInputSinCambios(CalendarItemDto item) {
        if (item instanceof PeriodoLaboralDto p) {
            return new PeriodoLaboralInputDto().id(p.getId()).tipoItem("LABORAL").codigo(p.getCodigo())
                    .nombre(p.getNombre()).recurrencia(p.getRecurrencia());
        }
        if (item instanceof PeriodoNoLaboralDto p) {
            return new PeriodoNoLaboralInputDto().id(p.getId()).tipoItem("NO_LABORAL").codigo(p.getCodigo())
                    .nombre(p.getNombre()).recurrencia(p.getRecurrencia());
        }
        ExcepcionDto e = (ExcepcionDto) item;
        return new ExcepcionInputDto().id(e.getId()).tipoItem("EXCEPCION").fecha(e.getFecha()).tipo(e.getTipo())
                .descripcion(e.getDescripcion());
    }

    private CalendarItemInputDto nuevoPeriodoNoLaboralInput(String codigo) {
        RecurrenciaUnaVezDto recurrencia = new RecurrenciaUnaVezDto().tipo("UNA_VEZ")
                .fechaInicio(CalendarioFixtures.INICIO_CALENDARIO)
                .fechaFin(CalendarioFixtures.INICIO_CALENDARIO.plusDays(10));
        return new PeriodoNoLaboralInputDto().tipoItem("NO_LABORAL").codigo(codigo).nombre("Período agregado BDD")
                .recurrencia(recurrencia);
    }

    @Entonces("^el cambio queda reflejado en la definición del calendario \"([^\"]*)\"$")
    public void el_cambio_queda_reflejado_en_la_definicion(String codigoCalendario) {
        assertThat(ultimoResultado).isNotNull();
        switch (ultimaAccion) {
            case "elimina" -> assertThat(ultimoResultado.getItems())
                    .noneMatch(item -> coincideCodigo(item, ultimoCodigoItem));
            case "edita" -> assertThat(ultimoResultado.getItems())
                    .anyMatch(item -> item instanceof PeriodoLaboralDto p && p.getCodigo().equals(ultimoCodigoItem)
                            && p.getNombre().endsWith("editado"));
            case "adiciona" -> assertThat(ultimoResultado.getItems())
                    .anyMatch(item -> item instanceof PeriodoNoLaboralDto p && p.getCodigo().equals(ultimoCodigoItem));
            default -> throw new IllegalStateException("Acción no soportada: " + ultimaAccion);
        }
    }
}
