package sv.gob.mh.siip.bdd.steps.administracion;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.ZoneId;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CatalogoFixtures;
import sv.gob.mh.siip.bdd.support.ContextoCatalogoBdd;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.domain.Registro;
import sv.gob.mh.siip.model.administracion.dto.ActiveStatusDto;
import sv.gob.mh.siip.model.administracion.dto.InactivationRequestDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoVigencia;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.administracion.repository.RegistroRepository;
import sv.gob.mh.siip.model.administracion.service.CatalogoService;
import sv.gob.mh.siip.model.administracion.service.RegistroService;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import org.springframework.data.domain.PageRequest;

/**
 * Steps de CU-ADM-01 (administracion de catalogos) cuyo texto es literalmente identico en varios
 * .feature del caso de uso (Cucumber exige una unica definicion por texto en el classpath de
 * test):
 * <ul>
 * <li>"un catálogo existente": actualizar-descriptores-catalogo, inactivar-catalogo,
 * crear-registro.</li>
 * <li>"un registro existente": actualizar-registro, inactivar-registro.</li>
 * <li>"fijo su ACTIVE en INACTIVE" / "fijo su TO DATE en la fecha actual o en una fecha pasada":
 * inactivar-catalogo e inactivar-registro comparten el verbo de inactivacion; el destino
 * (catalogo o registro) se decide por si {@link ContextoCatalogoBdd#getRegistroActual()} esta
 * definido.</li>
 * <li>"realizo la búsqueda": buscar-lista-registros y buscar-registro-por-clave; el destino se
 * decide por si hay una {@link ContextoCatalogoBdd#getClaveConsultada()} definida.</li>
 * <li>los 4 resultados de error/rechazo genericos ("... conforme a la Regla N", "el sistema
 * reporta un error", "el sistema rechaza la operación") reutilizados por casi todas las demas.</li>
 * <li>"su estado queda ACTIVE conforme a la Regla 13": crear-catalogo y crear-registro.</li>
 * </ul>
 */
public class AdmComun {

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private final UsuarioRepository usuarioRepository;
    private final CatalogoRepository catalogoRepository;
    private final RegistroRepository registroRepository;
    private final CatalogoService catalogoService;
    private final RegistroService registroService;
    private final ContextoCatalogoBdd contextoCatalogo;
    private final ContextoValidacionBdd contextoValidacion;

    public AdmComun(UsuarioRepository usuarioRepository, CatalogoRepository catalogoRepository,
            RegistroRepository registroRepository, CatalogoService catalogoService, RegistroService registroService,
            ContextoCatalogoBdd contextoCatalogo, ContextoValidacionBdd contextoValidacion) {
        this.usuarioRepository = usuarioRepository;
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
        this.catalogoService = catalogoService;
        this.registroService = registroService;
        this.contextoCatalogo = contextoCatalogo;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^un catálogo existente$")
    public void un_catalogo_existente() {
        CatalogoFixtures.autenticarNuevoAdministrador(usuarioRepository);
        Catalogo catalogo = catalogoRepository
                .save(CatalogoFixtures.nuevoCatalogo("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de prueba BDD"));
        contextoCatalogo.setCatalogoActual(catalogo);
    }

    @Dado("^un registro existente$")
    public void un_registro_existente() {
        CatalogoFixtures.autenticarNuevoAdministrador(usuarioRepository);
        Catalogo catalogo = catalogoRepository
                .save(CatalogoFixtures.nuevoCatalogo("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de prueba BDD"));
        Registro registro = Registro.builder().catalogo(catalogo).clave("K1").estado(EstadoVigencia.ACTIVE).build();
        registro.getValores().put(CatalogoFixtures.nombreCampoKey(catalogo), "K1");
        registro.getValores().put(CatalogoFixtures.nombreCampoNoKey(catalogo), "Descripción original (BDD)");
        contextoCatalogo.setCatalogoActual(catalogo);
        contextoCatalogo.setRegistroActual(registroRepository.save(registro));
    }

    @Cuando("^fijo su ACTIVE en INACTIVE$")
    public void fijo_su_active_en_inactive() {
        inactivar(new InactivationRequestDto().active(ActiveStatusDto.INACTIVE));
    }

    @Cuando("^fijo su TO DATE en la fecha actual o en una fecha pasada$")
    public void fijo_su_to_date_en_fecha_actual_o_pasada() {
        inactivar(new InactivationRequestDto().toDate(LocalDate.now(ZONA_EL_SALVADOR).minusDays(1)));
    }

    private void inactivar(InactivationRequestDto solicitud) {
        Catalogo catalogo = contextoCatalogo.getCatalogoActual();
        if (contextoCatalogo.getRegistroActual() != null) {
            contextoCatalogo.setRegistroResultado(
                    registroService.inactivar(catalogo.getCodigo(), contextoCatalogo.getRegistroActual().getClave(),
                            solicitud));
        } else {
            contextoCatalogo.setCatalogoResultado(catalogoService.inactivar(catalogo.getCodigo(), solicitud));
        }
    }

    @Cuando("^realizo la búsqueda$")
    public void realizo_la_busqueda() {
        Catalogo catalogo = contextoCatalogo.getCatalogoActual();
        try {
            if (contextoCatalogo.getClaveConsultada() != null) {
                contextoCatalogo.setRegistroResultado(registroService.buscarPorClave(catalogo.getCodigo(),
                        contextoCatalogo.getClaveConsultada(), contextoCatalogo.getCamposSolicitados()));
            } else {
                contextoCatalogo.setResultadoLista(registroService.buscarLista(catalogo.getCodigo(),
                        contextoCatalogo.getCamposSolicitados(), PageRequest.of(0, 20)));
            }
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Entonces("^su estado queda ACTIVE conforme a la Regla 13$")
    public void su_estado_queda_active_conforme_a_la_regla_13() {
        assertThat(contextoCatalogo.getActiveResultado()).isEqualTo(ActiveStatusDto.ACTIVE);
    }

    @Entonces("^el sistema rechaza la operación conforme a la Regla \\d+$")
    public void el_sistema_rechaza_la_operacion_conforme_a_una_regla() {
        assertThat(contextoValidacion.getUltimaExcepcion()).isNotNull();
    }

    @Entonces("^el sistema reporta un error conforme a la Regla \\d+$")
    public void el_sistema_reporta_un_error_conforme_a_una_regla() {
        assertThat(contextoValidacion.getUltimaExcepcion()).isNotNull();
    }

    @Entonces("^el sistema reporta un error$")
    public void el_sistema_reporta_un_error() {
        assertThat(contextoValidacion.getUltimaExcepcion()).isNotNull();
    }

    @Entonces("^el sistema rechaza la operación$")
    public void el_sistema_rechaza_la_operacion() {
        assertThat(contextoValidacion.getUltimaExcepcion()).isNotNull();
    }

    @Entonces("^el sistema rechaza la operación y solo permite inactivarlo conforme a la Regla \\d+$")
    public void el_sistema_rechaza_la_operacion_y_solo_permite_inactivarlo() {
        assertThat(contextoValidacion.getUltimaExcepcion()).isNotNull();
    }
}
