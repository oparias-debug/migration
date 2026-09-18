package sv.gob.mh.siip.bdd.steps.administracion;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CalendarioFixtures;
import sv.gob.mh.siip.bdd.support.ContextoCalendarioBdd;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.model.administracion.domain.Calendario;
import sv.gob.mh.siip.model.administracion.dto.CalendarioDto;
import sv.gob.mh.siip.model.administracion.dto.EditarCalendarioRequestDto;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.service.CalendarioService;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/** CU-ADM-04-12-editar-calendario.feature. */
public class Adm04EditarCalendario {

    private final UsuarioRepository usuarioRepository;
    private final CalendarioRepository calendarioRepository;
    private final CalendarioService calendarioService;
    private final ContextoCalendarioBdd contextoCalendario;
    private final ContextoValidacionBdd contextoValidacion;

    private EditarCalendarioRequestDto ultimaSolicitud;
    private CalendarioDto ultimoResultado;

    public Adm04EditarCalendario(UsuarioRepository usuarioRepository, CalendarioRepository calendarioRepository,
            CalendarioService calendarioService, ContextoCalendarioBdd contextoCalendario,
            ContextoValidacionBdd contextoValidacion) {
        this.usuarioRepository = usuarioRepository;
        this.calendarioRepository = calendarioRepository;
        this.calendarioService = calendarioService;
        this.contextoCalendario = contextoCalendario;
        this.contextoValidacion = contextoValidacion;
    }

    @Cuando("^el actor edita su nombre y su descripción con datos válidos$")
    public void el_actor_edita_su_nombre_y_su_descripcion_con_datos_validos() {
        Calendario calendario = contextoCalendario.getCalendarioActual();
        ultimaSolicitud = new EditarCalendarioRequestDto()
                .nombre("Calendario editado (BDD)")
                .descripcion("Descripción editada (BDD)")
                .fechaInicio(calendario.getFechaInicio())
                .fechaFin(calendario.getFechaFin());
        ultimoResultado = calendarioService.editar(calendario.getCodigo(), ultimaSolicitud);
    }

    @Entonces("^el calendario queda actualizado con el nuevo nombre y la nueva descripción$")
    public void el_calendario_queda_actualizado_con_el_nuevo_nombre_y_la_nueva_descripcion() {
        assertThat(ultimoResultado).isNotNull();
        assertThat(ultimoResultado.getNombre()).isEqualTo(ultimaSolicitud.getNombre());
        assertThat(ultimoResultado.getDescripcion()).isEqualTo(ultimaSolicitud.getDescripcion());
    }

    @Cuando("^el actor edita el calendario indicando una fecha de inicio posterior a la fecha de fin$")
    public void el_actor_edita_el_calendario_indicando_una_fecha_de_inicio_posterior_a_la_fecha_de_fin() {
        Calendario calendario = contextoCalendario.getCalendarioActual();
        LocalDate fechaInicio = calendario.getFechaInicio().plusDays(10);
        LocalDate fechaFin = calendario.getFechaInicio().plusDays(1);
        try {
            calendarioService.editar(calendario.getCodigo(), new EditarCalendarioRequestDto()
                    .nombre(calendario.getNombre())
                    .fechaInicio(fechaInicio)
                    .fechaFin(fechaFin));
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Cuando("^el actor intenta editar un calendario existente$")
    public void el_actor_intenta_editar_un_calendario_existente() {
        Usuario administrador = usuarioRepository
                .save(CalendarioFixtures.nuevoAdministradorCalendario("admin.calendario.bdd." + CalendarioFixtures.nuevoSufijo()));
        Calendario calendario = calendarioRepository
                .save(CalendarioFixtures.nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(), administrador));
        try {
            calendarioService.editar(calendario.getCodigo(), new EditarCalendarioRequestDto()
                    .nombre("Calendario editado (BDD)")
                    .fechaInicio(calendario.getFechaInicio())
                    .fechaFin(calendario.getFechaFin()));
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Dado("^un calendario cuyo administrador responsable es otro usuario$")
    public void un_calendario_cuyo_administrador_responsable_es_otro_usuario() {
        Usuario administradorResponsable = usuarioRepository
                .save(CalendarioFixtures.nuevoAdministradorCalendario("admin.calendario.bdd." + CalendarioFixtures.nuevoSufijo()));
        Calendario calendario = calendarioRepository.save(
                CalendarioFixtures.nuevoCalendario("CAL-" + CalendarioFixtures.nuevoSufijo(), administradorResponsable));
        contextoCalendario.setCalendarioActual(calendario);
    }

    @Cuando("^un actor con rol ADMINISTRADOR_CALENDARIO, distinto del administrador responsable, intenta editar ese calendario$")
    public void un_actor_administrador_calendario_distinto_del_responsable_intenta_editar_ese_calendario() {
        Calendario calendario = contextoCalendario.getCalendarioActual();
        String nombreUsuario = "admin.calendario.bdd." + CalendarioFixtures.nuevoSufijo();
        usuarioRepository.save(CalendarioFixtures.nuevoAdministradorCalendario(nombreUsuario));
        CalendarioFixtures.autenticarComo(nombreUsuario);
        try {
            calendarioService.editar(calendario.getCodigo(), new EditarCalendarioRequestDto()
                    .nombre("Calendario editado (BDD)")
                    .fechaInicio(calendario.getFechaInicio())
                    .fechaFin(calendario.getFechaFin()));
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }
}
