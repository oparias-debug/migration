package sv.gob.mh.siip.bdd.steps.administracion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import sv.gob.mh.siip.bdd.support.CatalogoFixtures;
import sv.gob.mh.siip.bdd.support.ContextoCatalogoBdd;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.administracion.dto.CatalogCreateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogFieldDto;
import sv.gob.mh.siip.model.administracion.dto.FieldQualifierDto;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.administracion.service.CatalogoService;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;

/** CU-ADM-01-crear-catalogo.feature. */
public class AdmCrearCatalogo {

    private final UsuarioRepository usuarioRepository;
    private final CatalogoRepository catalogoRepository;
    private final CatalogoService catalogoService;
    private final ContextoCatalogoBdd contextoCatalogo;
    private final ContextoValidacionBdd contextoValidacion;
    private final Validator validator;

    private CatalogCreateRequestDto ultimaSolicitud;
    private CatalogDto ultimoResultado;

    public AdmCrearCatalogo(UsuarioRepository usuarioRepository, CatalogoRepository catalogoRepository,
            CatalogoService catalogoService, ContextoCatalogoBdd contextoCatalogo,
            ContextoValidacionBdd contextoValidacion, Validator validator) {
        this.usuarioRepository = usuarioRepository;
        this.catalogoRepository = catalogoRepository;
        this.catalogoService = catalogoService;
        this.contextoCatalogo = contextoCatalogo;
        this.contextoValidacion = contextoValidacion;
        this.validator = validator;
    }

    @Dado("^que indico código, nombre y al menos un campo con calificador KEY$")
    public void que_indico_codigo_nombre_y_campo_key() {
        CatalogoFixtures.autenticarNuevoAdministrador(usuarioRepository);
        ultimaSolicitud = new CatalogCreateRequestDto("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de prueba BDD",
                List.of(new CatalogFieldDto("codigoInterno", FieldQualifierDto.KEY)));
    }

    @Cuando("^confirmo la creación del catálogo$")
    public void confirmo_la_creacion_del_catalogo() {
        ultimoResultado = catalogoService.crear(ultimaSolicitud);
    }

    @Entonces("^el catálogo se crea correctamente$")
    public void el_catalogo_se_crea_correctamente() {
        assertThat(ultimoResultado).isNotNull();
        assertThat(ultimoResultado.getCode()).isEqualTo(ultimaSolicitud.getCode());
        assertThat(catalogoRepository.existsByCodigo(ultimaSolicitud.getCode())).isTrue();
    }

    @Dado("^que defino campos para el catálogo pero ninguno tiene calificador KEY$")
    public void que_defino_campos_sin_key() {
        CatalogoFixtures.autenticarNuevoAdministrador(usuarioRepository);
        ultimaSolicitud = new CatalogCreateRequestDto("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de prueba BDD",
                List.of(new CatalogFieldDto("descripcion", FieldQualifierDto.FIELD)));
    }

    @Dado("^que dos o más campos del catálogo tienen el mismo nombre$")
    public void que_dos_o_mas_campos_tienen_el_mismo_nombre() {
        CatalogoFixtures.autenticarNuevoAdministrador(usuarioRepository);
        ultimaSolicitud = new CatalogCreateRequestDto("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de prueba BDD",
                List.of(new CatalogFieldDto("repetido", FieldQualifierDto.KEY),
                        new CatalogFieldDto("repetido", FieldQualifierDto.FIELD)));
    }

    @Dado("^que no indico ningún campo para el catálogo$")
    public void que_no_indico_ningun_campo() {
        CatalogoFixtures.autenticarNuevoAdministrador(usuarioRepository);
        ultimaSolicitud = new CatalogCreateRequestDto("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de prueba BDD",
                List.of());
    }

    /**
     * A diferencia de {@link #intento_crear_el_catalogo()}, no invoca el servicio: la Regla 18
     * (al menos un campo) la exige {@code @Size(min = 1)} en {@code CatalogCreateRequestDto}, así
     * que en producción el request nunca llega a {@code CatalogoServiceImpl} sin campos — lo
     * rechaza el {@code @Valid} del controlador. Este step reproduce esa validación de borde.
     */
    @Cuando("^intento crear el catálogo sin ningún campo definido$")
    public void intento_crear_el_catalogo_sin_ningun_campo_definido() {
        Set<ConstraintViolation<CatalogCreateRequestDto>> violaciones = validator.validate(ultimaSolicitud);
        contextoValidacion.setUltimaExcepcion(violaciones.isEmpty() ? null
                : new ValidacionNegocioException("CAMPOS_REQUERIDOS", "Debe existir al menos un campo definido.", null));
    }

    @Cuando("^intento crear el catálogo$")
    public void intento_crear_el_catalogo() {
        try {
            ultimoResultado = catalogoService.crear(ultimaSolicitud);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }

    @Dado("^que no indico fechas de vigencia al crear el catálogo$")
    public void que_no_indico_fechas_de_vigencia() {
        CatalogoFixtures.autenticarNuevoAdministrador(usuarioRepository);
        ultimaSolicitud = new CatalogCreateRequestDto("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de prueba BDD",
                List.of(new CatalogFieldDto("codigoInterno", FieldQualifierDto.KEY)));
        ultimaSolicitud.setFromDate(null);
        ultimaSolicitud.setToDate(null);
    }

    @Cuando("^el catálogo se crea$")
    public void el_catalogo_se_crea() {
        ultimoResultado = catalogoService.crear(ultimaSolicitud);
        contextoCatalogo.setActiveResultado(ultimoResultado.getActive());
    }

    @Dado("^que indico un catálogo PARENT para el nuevo catálogo$")
    public void que_indico_un_catalogo_parent() {
        CatalogoFixtures.autenticarNuevoAdministrador(usuarioRepository);
        ultimaSolicitud = new CatalogCreateRequestDto("CAT-" + CatalogoFixtures.nuevoSufijo(), "Catálogo de prueba BDD",
                List.of(new CatalogFieldDto("codigoInterno", FieldQualifierDto.KEY)));
        ultimaSolicitud.setParent("NOEXISTE-" + CatalogoFixtures.nuevoSufijo());
    }

    @Cuando("^el código del catálogo padre no existe en el catalogMaster$")
    public void el_codigo_del_catalogo_padre_no_existe() {
        try {
            ultimoResultado = catalogoService.crear(ultimaSolicitud);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (RuntimeException ex) {
            contextoValidacion.setUltimaExcepcion(ex);
        }
    }
}
