package sv.gob.mh.siip.bdd.steps.administracion.calendario;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CalendarioFixtures;
import sv.gob.mh.siip.model.administracion.dto.CalendarioResumenDto;
import sv.gob.mh.siip.model.administracion.dto.EstadoCalendarioDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoCalendario;
import sv.gob.mh.siip.model.administracion.repository.CalendarioRepository;
import sv.gob.mh.siip.model.administracion.service.CalendarioService;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/** CU-ADM-04-13-listar-calendarios.feature. */
public class Adm04ListarCalendarios {

    private final UsuarioRepository usuarioRepository;
    private final CalendarioRepository calendarioRepository;
    private final CalendarioService calendarioService;

    private List<CalendarioResumenDto> ultimoResultado;

    // Codigos reales (distintos de los literales del .feature) de los dos calendarios creados por el
    // Dado: ver nota en Adm04ComunCalendario sobre por que los escenarios no pueden compartir codigos
    // literales, indexados por su literal de origen para que el Entonces los pueda recuperar.
    private final Map<String, String> codigosReales = new HashMap<>();

    public Adm04ListarCalendarios(UsuarioRepository usuarioRepository, CalendarioRepository calendarioRepository,
            CalendarioService calendarioService) {
        this.usuarioRepository = usuarioRepository;
        this.calendarioRepository = calendarioRepository;
        this.calendarioService = calendarioService;
    }

    @Dado("^que existen los calendarios \"([^\"]*)\" \\(([^)]*)\\) y \"([^\"]*)\" \\(([^)]*)\\)$")
    public void que_existen_los_calendarios(String codigo1, String estado1, String codigo2, String estado2) {
        crearCalendario(codigo1, estado1);
        crearCalendario(codigo2, estado2);
    }

    private void crearCalendario(String codigoLiteral, String estado) {
        String codigoReal = codigoLiteral + "-" + CalendarioFixtures.nuevoSufijo();
        codigosReales.put(codigoLiteral, codigoReal);
        String nombreUsuario = "admin.calendario.bdd." + CalendarioFixtures.nuevoSufijo();
        var administrador = usuarioRepository.save(CalendarioFixtures.nuevoAdministradorCalendario(nombreUsuario));
        calendarioRepository.save(CalendarioFixtures.nuevoCalendario(codigoReal, CalendarioFixtures.INICIO_CALENDARIO,
                CalendarioFixtures.FIN_CALENDARIO, EstadoCalendario.valueOf(estado), administrador));
    }

    @Cuando("^cualquier usuario consulta la lista de calendarios registrados$")
    public void cualquier_usuario_consulta_la_lista_de_calendarios_registrados() {
        ultimoResultado = calendarioService.listar();
    }

    @Entonces("^el sistema devuelve una lista que incluye el código, nombre y estado de \"([^\"]*)\" y de \"([^\"]*)\"$")
    public void el_sistema_devuelve_una_lista_que_incluye_los_calendarios(String codigo1Literal,
            String codigo2Literal) {
        String codigo1 = codigosReales.get(codigo1Literal);
        String codigo2 = codigosReales.get(codigo2Literal);
        assertThat(ultimoResultado).isNotNull();
        assertThat(ultimoResultado).extracting(CalendarioResumenDto::getCodigo).contains(codigo1, codigo2);
        assertThat(ultimoResultado).filteredOn(r -> r.getCodigo().equals(codigo1))
                .extracting(CalendarioResumenDto::getEstado).containsExactly(EstadoCalendarioDto.ACTIVO);
        assertThat(ultimoResultado).filteredOn(r -> r.getCodigo().equals(codigo2))
                .extracting(CalendarioResumenDto::getEstado).containsExactly(EstadoCalendarioDto.INACTIVO);
    }
}
