package sv.gob.mh.siip.bdd.steps.administracion;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.ZoneId;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CatalogoFixtures;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.domain.Registro;
import sv.gob.mh.siip.model.administracion.dto.CatalogoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.EstadoVigenciaDto;
import sv.gob.mh.siip.model.administracion.dto.InactivacionRequestDto;
import sv.gob.mh.siip.model.administracion.dto.RegistroResponseDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoVigencia;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.administracion.repository.RegistroRepository;
import sv.gob.mh.siip.model.administracion.service.CatalogoService;
import sv.gob.mh.siip.model.administracion.service.RegistroService;

/**
 * CU-ADM-01-inactivar-catalogo-o-registro.feature. La autenticacion/autorizacion del
 * Administrador de Catalogos, el actor no autorizado y el rechazo generico por falta de
 * autorizacion se reutilizan desde AdmActualizarCatalogo (Cucumber exige una unica definicion por
 * texto).
 */
public class AdmInactivarCatalogoORegistro {

    private static final String CATALOGO = "catálogo";
    // CatalogoServiceImpl.calcularEstadoVigencia asigna/compara la TO DATE con
    // LocalDate.now(ZONA_EL_SALVADOR), no con el huso horario por defecto de la JVM: usar el mismo
    // huso aquí evita un falso negativo/positivo cuando ambos husos ya no coinciden en el día.
    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private final CatalogoRepository catalogoRepository;
    private final RegistroRepository registroRepository;
    private final CatalogoService catalogoService;
    private final RegistroService registroService;
    private final ContextoValidacionBdd contextoValidacion;

    private String objetivoActual;
    private Catalogo catalogoActual;
    private Registro registroActual;
    private CatalogoResponseDto catalogoResultado;
    private RegistroResponseDto registroResultado;

    public AdmInactivarCatalogoORegistro(CatalogoRepository catalogoRepository, RegistroRepository registroRepository,
            CatalogoService catalogoService, RegistroService registroService,
            ContextoValidacionBdd contextoValidacion) {
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
        this.catalogoService = catalogoService;
        this.registroService = registroService;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^selecciona \"([^\"]*)\" existente en estado ACTIVE$")
    public void selecciona_objetivo_existente_en_estado_active(String objetivo) {
        objetivoActual = objetivo;
        catalogoActual = catalogoRepository.save(
                CatalogoFixtures.nuevoCatalogo("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de prueba BDD"));
        if (!CATALOGO.equals(objetivo)) {
            Registro registro = Registro.builder().catalogo(catalogoActual).clave("K1").estado(EstadoVigencia.ACTIVE)
                    .build();
            registro.getValores().put(catalogoActual.getCampos().get(0).getNombre(), "K1");
            registroActual = registroRepository.save(registro);
        }
    }

    @Cuando("^cambia el flag ACTIVE a INACTIVE$")
    public void cambia_el_flag_active_a_inactive() {
        inactivar(null);
    }

    @Cuando("^establece la TO DATE en la fecha actual o en una fecha pasada$")
    public void establece_la_to_date_en_la_fecha_actual_o_en_una_fecha_pasada() {
        inactivar(LocalDate.now(ZONA_EL_SALVADOR).minusDays(1));
    }

    @Entonces("^el sistema asigna automáticamente la TO DATE con la fecha actual$")
    public void el_sistema_asigna_automaticamente_la_to_date_con_la_fecha_actual() {
        LocalDate fechaHasta = CATALOGO.equals(objetivoActual) ? catalogoResultado.getVigencia().getFechaHasta()
                : registroResultado.getVigencia().getFechaHasta();
        assertThat(fechaHasta).isEqualTo(LocalDate.now(ZONA_EL_SALVADOR));
    }

    @Entonces("^el \"([^\"]*)\" queda marcado como INACTIVE$")
    public void el_objetivo_queda_marcado_como_inactive(String objetivo) {
        EstadoVigenciaDto estado = CATALOGO.equals(objetivo) ? catalogoResultado.getEstado()
                : registroResultado.getEstado();
        assertThat(estado).isEqualTo(EstadoVigenciaDto.INACTIVE);
    }

    @Entonces("^el \"([^\"]*)\" permanece almacenado, sin ser eliminado$")
    public void el_objetivo_permanece_almacenado_sin_ser_eliminado(String objetivo) {
        if (CATALOGO.equals(objetivo)) {
            assertThat(catalogoRepository.existsByCodigo(catalogoActual.getCodigo())).isTrue();
        } else {
            assertThat(registroRepository.existsByCatalogo_CodigoAndClave(catalogoActual.getCodigo(),
                    registroActual.getClave())).isTrue();
        }
    }

    @Cuando("^intenta inactivar un catálogo o un registro$")
    public void intenta_inactivar_un_catalogo_o_un_registro() {
        Catalogo catalogo = catalogoRepository.save(
                CatalogoFixtures.nuevoCatalogo("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de prueba BDD"));
        try {
            catalogoService.inactivar(catalogo.getCodigo(), null);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    private void inactivar(LocalDate toDate) {
        InactivacionRequestDto solicitud = toDate == null ? null : new InactivacionRequestDto().toDate(toDate);
        if (CATALOGO.equals(objetivoActual)) {
            catalogoResultado = catalogoService.inactivar(catalogoActual.getCodigo(), solicitud);
        } else {
            registroResultado = registroService.inactivar(catalogoActual.getCodigo(), registroActual.getClave(), solicitud);
        }
    }
}
