package sv.gob.mh.siip.bdd.steps.administracion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CatalogoFixtures;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.model.administracion.domain.CampoDefinicion;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.domain.Registro;
import sv.gob.mh.siip.model.administracion.dto.ActualizarRegistroRequestDto;
import sv.gob.mh.siip.model.administracion.dto.RegistroResponseDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoVigencia;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.administracion.repository.RegistroRepository;
import sv.gob.mh.siip.model.administracion.service.RegistroService;

/**
 * CU-ADM-01-actualizar-registro.feature. La autenticacion/autorizacion del Administrador de
 * Catalogos, el actor no autorizado y el rechazo generico por falta de autorizacion se reutilizan
 * desde AdmActualizarCatalogo (Cucumber exige una unica definicion por texto).
 *
 * "el sistema reporta un error" se define aqui y lo reutiliza sin redefinir
 * CU-ADM-01-buscar-registro-por-clave.feature.
 */
public class AdmActualizarRegistro {

    private final CatalogoRepository catalogoRepository;
    private final RegistroRepository registroRepository;
    private final RegistroService registroService;
    private final ContextoValidacionBdd contextoValidacion;

    private Catalogo catalogoActual;
    private Registro registroActual;
    private RegistroResponseDto ultimoResultado;

    public AdmActualizarRegistro(CatalogoRepository catalogoRepository, RegistroRepository registroRepository,
            RegistroService registroService, ContextoValidacionBdd contextoValidacion) {
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
        this.registroService = registroService;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^selecciona un registro existente$")
    public void selecciona_un_registro_existente() {
        catalogoActual = catalogoRepository.save(
                CatalogoFixtures.nuevoCatalogo("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de prueba BDD"));
        registroActual = nuevoRegistroPersistido(catalogoActual, "K1", "Descripción original (BDD)");
    }

    @Cuando("^modifica el valor de uno o más campos no-KEY$")
    public void modifica_el_valor_de_uno_o_mas_campos_no_key() {
        String nombreCampoNoKey = nombreCampoNoKey(catalogoActual);
        ActualizarRegistroRequestDto solicitud = new ActualizarRegistroRequestDto()
                .valores(Map.of(nombreCampoNoKey, "Descripción actualizada (BDD)"));
        ultimoResultado = registroService.actualizar(catalogoActual.getCodigo(), registroActual.getClave(), solicitud);
    }

    @Entonces("^el sistema aplica los cambios$")
    public void el_sistema_aplica_los_cambios() {
        String nombreCampoNoKey = nombreCampoNoKey(catalogoActual);
        assertThat(ultimoResultado.getValores()).containsEntry(nombreCampoNoKey, "Descripción actualizada (BDD)");
    }

    @Cuando("^intenta modificar el valor del campo KEY$")
    public void intenta_modificar_el_valor_del_campo_key() {
        String nombreCampoKey = nombreCampoKey(catalogoActual);
        ActualizarRegistroRequestDto solicitud = new ActualizarRegistroRequestDto()
                .valores(Map.of(nombreCampoKey, "K1-modificado"));
        try {
            registroService.actualizar(catalogoActual.getCodigo(), registroActual.getClave(), solicitud);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Entonces("^el sistema reporta un error$")
    public void el_sistema_reporta_un_error() {
        assertThat(contextoValidacion.getUltimaExcepcion()).isNotNull();
    }

    @Entonces("^no aplica la modificación del campo KEY$")
    public void no_aplica_la_modificacion_del_campo_key() {
        Registro recargado = registroRepository.findById(registroActual.getId()).orElseThrow();
        assertThat(recargado.getClave()).isEqualTo("K1");
    }

    @Cuando("^intenta actualizar un registro existente$")
    public void intenta_actualizar_un_registro_existente() {
        Catalogo catalogo = catalogoRepository.save(
                CatalogoFixtures.nuevoCatalogo("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de prueba BDD"));
        Registro registro = nuevoRegistroPersistido(catalogo, "K1", "Descripción (BDD)");
        String nombreCampoNoKey = nombreCampoNoKey(catalogo);
        ActualizarRegistroRequestDto solicitud = new ActualizarRegistroRequestDto()
                .valores(Map.of(nombreCampoNoKey, "Intento no autorizado (BDD)"));
        try {
            registroService.actualizar(catalogo.getCodigo(), registro.getClave(), solicitud);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    private Registro nuevoRegistroPersistido(Catalogo catalogo, String clave, String descripcion) {
        Registro registro = Registro.builder()
                .catalogo(catalogo)
                .clave(clave)
                .estado(EstadoVigencia.ACTIVE)
                .build();
        registro.getValores().put(nombreCampoKey(catalogo), clave);
        registro.getValores().put(nombreCampoNoKey(catalogo), descripcion);
        return registroRepository.save(registro);
    }

    private static String nombreCampoKey(Catalogo catalogo) {
        return catalogo.getCampos().stream().filter(CampoDefinicion::isEsKey).findFirst()
                .map(CampoDefinicion::getNombre).orElseThrow();
    }

    private static String nombreCampoNoKey(Catalogo catalogo) {
        return catalogo.getCampos().stream().filter(campo -> !campo.isEsKey()).findFirst()
                .map(CampoDefinicion::getNombre).orElseThrow();
    }
}
