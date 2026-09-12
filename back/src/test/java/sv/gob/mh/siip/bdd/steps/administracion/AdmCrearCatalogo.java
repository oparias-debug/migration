package sv.gob.mh.siip.bdd.steps.administracion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CatalogoFixtures;
import sv.gob.mh.siip.bdd.support.ContextoCatalogoBdd;
import sv.gob.mh.siip.bdd.support.ContextoCatalogoBdd.ModoCondicion;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.administracion.dto.CampoDefinicionDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogoResponseDto;
import sv.gob.mh.siip.model.administracion.dto.CrearCatalogoRequestDto;
import sv.gob.mh.siip.model.administracion.dto.EstadoVigenciaDto;
import sv.gob.mh.siip.model.administracion.dto.TipoCampoDto;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.administracion.service.CatalogoService;

/**
 * CU-ADM-01-crear-catalogo.feature. La autenticacion/autorizacion del Administrador de Catalogos,
 * el actor no autorizado y el rechazo generico por falta de autorizacion se reutilizan desde
 * AdmActualizarCatalogo; el paso "{string}" del esquema de vigencia por defecto se reutiliza desde
 * AdmBuscarCatalogoPorNombre (Cucumber exige una unica definicion por texto).
 */
public class AdmCrearCatalogo {

    private final CatalogoRepository catalogoRepository;
    private final CatalogoService catalogoService;
    private final ContextoCatalogoBdd contextoCatalogo;
    private final ContextoValidacionBdd contextoValidacion;

    private CrearCatalogoRequestDto ultimaSolicitud;
    private CatalogoResponseDto ultimoResultado;

    public AdmCrearCatalogo(CatalogoRepository catalogoRepository, CatalogoService catalogoService,
            ContextoCatalogoBdd contextoCatalogo, ContextoValidacionBdd contextoValidacion) {
        this.catalogoRepository = catalogoRepository;
        this.catalogoService = catalogoService;
        this.contextoCatalogo = contextoCatalogo;
        this.contextoValidacion = contextoValidacion;
    }

    @Dado("^define código, nombre y una lista de campos que incluye al menos un campo marcado como KEY, sin nombres repetidos$")
    public void define_codigo_nombre_y_campos_con_al_menos_un_key_sin_nombres_repetidos() {
        ultimaSolicitud = new CrearCatalogoRequestDto("CAT-" + CatalogoFixtures.nuevoSufijo(),
                "Catálogo de prueba BDD",
                List.of(new CampoDefinicionDto("codigoInterno", TipoCampoDto.STRING, true),
                        new CampoDefinicionDto("descripcion", TipoCampoDto.STRING, false)));
    }

    @Cuando("^solicita crear el catálogo$")
    public void solicita_crear_el_catalogo() {
        try {
            ultimoResultado = catalogoService.crear(ultimaSolicitud);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Entonces("^el sistema crea el catálogo$")
    public void el_sistema_crea_el_catalogo() {
        assertThat(ultimoResultado).isNotNull();
        assertThat(ultimoResultado.getCodigo()).isEqualTo(ultimaSolicitud.getCodigo());
    }

    @Entonces("^lo agrega al catalogMaster$")
    public void lo_agrega_al_catalog_master() {
        assertThat(catalogoRepository.existsByCodigo(ultimaSolicitud.getCodigo())).isTrue();
    }

    @Dado("^no define ningún campo para el catálogo$")
    public void no_define_ningun_campo_para_el_catalogo() {
        ultimaSolicitud = new CrearCatalogoRequestDto("CAT-" + CatalogoFixtures.nuevoSufijo(),
                "Catálogo de prueba BDD", List.of());
    }

    @Entonces("^el sistema rechaza la creación por no existir al menos un campo definido$")
    public void el_sistema_rechaza_la_creacion_por_no_existir_al_menos_un_campo_definido() {
        assertThat(contextoValidacion.getUltimaExcepcion()).isInstanceOf(ValidacionNegocioException.class);
    }

    @Dado("^define campos para el catálogo pero ninguno está marcado como KEY$")
    public void define_campos_para_el_catalogo_pero_ninguno_esta_marcado_como_key() {
        ultimaSolicitud = new CrearCatalogoRequestDto("CAT-" + CatalogoFixtures.nuevoSufijo(),
                "Catálogo de prueba BDD", List.of(new CampoDefinicionDto("descripcion", TipoCampoDto.STRING, false)));
    }

    @Entonces("^el sistema rechaza la creación por no existir al menos un campo KEY$")
    public void el_sistema_rechaza_la_creacion_por_no_existir_al_menos_un_campo_key() {
        assertThat(contextoValidacion.getUltimaExcepcion()).isInstanceOf(ValidacionNegocioException.class);
    }

    @Dado("^define dos campos con el mismo nombre$")
    public void define_dos_campos_con_el_mismo_nombre() {
        ultimaSolicitud = new CrearCatalogoRequestDto("CAT-" + CatalogoFixtures.nuevoSufijo(),
                "Catálogo de prueba BDD",
                List.of(new CampoDefinicionDto("repetido", TipoCampoDto.STRING, true),
                        new CampoDefinicionDto("repetido", TipoCampoDto.STRING, false)));
    }

    @Entonces("^el sistema rechaza la creación por nombres de campo repetidos$")
    public void el_sistema_rechaza_la_creacion_por_nombres_de_campo_repetidos() {
        assertThat(contextoValidacion.getUltimaExcepcion()).isInstanceOf(ValidacionNegocioException.class);
    }

    @Dado("^indica como catálogo padre un código que no existe previamente$")
    public void indica_como_catalogo_padre_un_codigo_que_no_existe_previamente() {
        ultimaSolicitud = new CrearCatalogoRequestDto("CAT-" + CatalogoFixtures.nuevoSufijo(),
                "Catálogo de prueba BDD", List.of(new CampoDefinicionDto("codigoInterno", TipoCampoDto.STRING, true)));
        ultimaSolicitud.setCatalogoPadreCodigo("NOEXISTE-" + CatalogoFixtures.nuevoSufijo());
    }

    @Entonces("^el sistema rechaza la creación del catálogo hijo$")
    public void el_sistema_rechaza_la_creacion_del_catalogo_hijo() {
        assertThat(contextoValidacion.getUltimaExcepcion()).isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Dado("^crea un catálogo válido con al menos un campo KEY$")
    public void crea_un_catalogo_valido_con_al_menos_un_campo_key() {
        contextoCatalogo.setModoCondicion(ModoCondicion.VIGENCIA_CATALOGO);
    }

    @Entonces("^el catálogo queda en estado \"([^\"]*)\"$")
    public void el_catalogo_queda_en_estado(String estadoEsperado) {
        assertThat(contextoCatalogo.getCatalogoResultado().getEstado())
                .isEqualTo(EstadoVigenciaDto.fromValue(estadoEsperado));
    }

    @Cuando("^intenta crear un catálogo$")
    public void intenta_crear_un_catalogo() {
        CrearCatalogoRequestDto solicitud = new CrearCatalogoRequestDto("CAT-" + CatalogoFixtures.nuevoSufijo(),
                "Catálogo de prueba BDD", List.of(new CampoDefinicionDto("codigoInterno", TipoCampoDto.STRING, true)));
        try {
            catalogoService.crear(solicitud);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }
}
