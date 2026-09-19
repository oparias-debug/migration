package sv.gob.mh.siip.bdd.steps.administracion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CatalogoFixtures;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.domain.Registro;
import sv.gob.mh.siip.model.administracion.dto.CatalogDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogFieldDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogFieldsUpdateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.FieldQualifierDto;
import sv.gob.mh.siip.model.administracion.enums.EstadoVigencia;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.administracion.repository.RegistroRepository;
import sv.gob.mh.siip.model.administracion.service.CatalogoService;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/** CU-ADM-01-actualizar-campos-catalogo.feature. */
public class AdmActualizarCamposCatalogo {

    private final UsuarioRepository usuarioRepository;
    private final CatalogoRepository catalogoRepository;
    private final RegistroRepository registroRepository;
    private final CatalogoService catalogoService;
    private final ContextoValidacionBdd contextoValidacion;

    private Catalogo catalogoActual;
    private CatalogDto ultimoResultado;

    public AdmActualizarCamposCatalogo(UsuarioRepository usuarioRepository, CatalogoRepository catalogoRepository,
            RegistroRepository registroRepository, CatalogoService catalogoService,
            ContextoValidacionBdd contextoValidacion) {
        this.usuarioRepository = usuarioRepository;
        this.catalogoRepository = catalogoRepository;
        this.registroRepository = registroRepository;
        this.catalogoService = catalogoService;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^un catálogo que no contiene registros$")
    public void un_catalogo_que_no_contiene_registros() {
        CatalogoFixtures.autenticarNuevoAdministrador(usuarioRepository);
        catalogoActual = catalogoRepository.save(
                CatalogoFixtures.nuevoCatalogo("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de prueba BDD"));
    }

    @Cuando("^modifico sus campos manteniendo nombres únicos entre ellos$")
    public void modifico_sus_campos_manteniendo_nombres_unicos() {
        CatalogFieldsUpdateRequestDto solicitud = new CatalogFieldsUpdateRequestDto(List.of(
                new CatalogFieldDto("codigoInterno", FieldQualifierDto.KEY),
                new CatalogFieldDto("descripcionActualizada", FieldQualifierDto.FIELD)));
        ultimoResultado = catalogoService.actualizarCampos(catalogoActual.getCodigo(), solicitud);
    }

    @Entonces("^el sistema permite la actualización conforme a la Regla 3$")
    public void el_sistema_permite_la_actualizacion() {
        assertThat(ultimoResultado.getFields()).extracting(CatalogFieldDto::getName)
                .containsExactly("codigoInterno", "descripcionActualizada");
    }

    @Dado("^un catálogo que sí contiene registros$")
    public void un_catalogo_que_si_contiene_registros() {
        CatalogoFixtures.autenticarNuevoAdministrador(usuarioRepository);
        catalogoActual = catalogoRepository.save(
                CatalogoFixtures.nuevoCatalogo("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de prueba BDD"));
        Registro registro = Registro.builder().catalogo(catalogoActual).clave("K1").estado(EstadoVigencia.ACTIVE).build();
        registro.getValores().put(CatalogoFixtures.nombreCampoKey(catalogoActual), "K1");
        registroRepository.save(registro);
    }

    @Cuando("^intento modificar sus campos$")
    public void intento_modificar_sus_campos() {
        CatalogFieldsUpdateRequestDto solicitud = new CatalogFieldsUpdateRequestDto(
                List.of(new CatalogFieldDto("otroCampo", FieldQualifierDto.KEY)));
        try {
            catalogoService.actualizarCampos(catalogoActual.getCodigo(), solicitud);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Dado("^un catálogo sin registros cuya modificación de campos elimina todos los campos KEY$")
    public void un_catalogo_sin_registros_cuya_modificacion_elimina_todos_los_key() {
        CatalogoFixtures.autenticarNuevoAdministrador(usuarioRepository);
        catalogoActual = catalogoRepository.save(
                CatalogoFixtures.nuevoCatalogo("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de prueba BDD"));
    }

    @Cuando("^intento guardar esa modificación$")
    public void intento_guardar_esa_modificacion() {
        CatalogFieldsUpdateRequestDto solicitud = new CatalogFieldsUpdateRequestDto(
                List.of(new CatalogFieldDto("soloTexto", FieldQualifierDto.FIELD)));
        try {
            catalogoService.actualizarCampos(catalogoActual.getCodigo(), solicitud);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }
}
