package sv.gob.mh.siip.bdd.steps.administracion;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.ZoneId;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import sv.gob.mh.siip.bdd.support.CatalogoFixtures;
import sv.gob.mh.siip.bdd.support.ContextoCatalogoBdd;
import sv.gob.mh.siip.bdd.support.ContextoValidacionBdd;
import sv.gob.mh.siip.model.administracion.domain.Catalogo;
import sv.gob.mh.siip.model.administracion.dto.ActiveStatusDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogDescriptorsUpdateRequestDto;
import sv.gob.mh.siip.model.administracion.dto.CatalogDto;
import sv.gob.mh.siip.model.administracion.repository.CatalogoRepository;
import sv.gob.mh.siip.model.administracion.service.CatalogoService;

/**
 * CU-ADM-01-actualizar-descriptores-catalogo.feature. "un catálogo existente" se reutiliza sin
 * redefinir desde {@link AdmComun} (tambien lo usan inactivar-catalogo.feature y
 * crear-registro.feature).
 */
public class AdmActualizarDescriptoresCatalogo {

    private static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private final CatalogoRepository catalogoRepository;
    private final CatalogoService catalogoService;
    private final ContextoCatalogoBdd contextoCatalogo;
    private final ContextoValidacionBdd contextoValidacion;
    private final ObjectMapper objectMapper;

    private String descriptorActualizado;
    private String parentEsperado;
    private CatalogDto ultimoResultado;

    public AdmActualizarDescriptoresCatalogo(CatalogoRepository catalogoRepository, CatalogoService catalogoService,
            ContextoCatalogoBdd contextoCatalogo, ContextoValidacionBdd contextoValidacion, ObjectMapper objectMapper) {
        this.catalogoRepository = catalogoRepository;
        this.catalogoService = catalogoService;
        this.contextoCatalogo = contextoCatalogo;
        this.contextoValidacion = contextoValidacion;
        this.objectMapper = objectMapper;
    }

    @Cuando("^actualizo su descriptor \"([^\"]*)\"$")
    public void actualizo_su_descriptor(String descriptor) {
        descriptorActualizado = descriptor;
        Catalogo catalogoActual = contextoCatalogo.getCatalogoActual();
        CatalogDescriptorsUpdateRequestDto solicitud = new CatalogDescriptorsUpdateRequestDto();
        switch (descriptor) {
            case "name" -> solicitud.setName("Nombre actualizado (BDD)");
            case "parent" -> {
                Catalogo padre = catalogoRepository.save(
                        CatalogoFixtures.nuevoCatalogo("CAT-PADRE-" + CatalogoFixtures.nuevoSufijo(), "Catálogo padre (BDD)"));
                parentEsperado = padre.getCodigo();
                solicitud.setParent(parentEsperado);
            }
            case "active" -> solicitud.setActive(ActiveStatusDto.INACTIVE);
            case "valid" -> {
                solicitud.setFromDate(LocalDate.now(ZONA_EL_SALVADOR).minusDays(10));
                solicitud.setToDate(LocalDate.now(ZONA_EL_SALVADOR).plusDays(10));
            }
            default -> throw new IllegalArgumentException("Descriptor no soportado por el step BDD: " + descriptor);
        }
        ultimoResultado = catalogoService.actualizarDescriptores(catalogoActual.getCodigo(), solicitud);
    }

    @Entonces("^el cambio se guarda correctamente$")
    public void el_cambio_se_guarda_correctamente() {
        assertThat(ultimoResultado).isNotNull();
        switch (descriptorActualizado) {
            case "name" -> assertThat(ultimoResultado.getName()).isEqualTo("Nombre actualizado (BDD)");
            case "parent" -> assertThat(ultimoResultado.getParent()).isEqualTo(parentEsperado);
            case "active" -> assertThat(ultimoResultado.getActive()).isEqualTo(ActiveStatusDto.INACTIVE);
            case "valid" -> assertThat(ultimoResultado.getFromDate()).isNotNull().isNotEqualTo(ultimoResultado.getToDate());
            default -> throw new IllegalArgumentException("Descriptor no soportado por el step BDD: " + descriptorActualizado);
        }
    }

    /**
     * CatalogDescriptorsUpdateRequestDto no tiene (ni puede tener) un setter de "code": el
     * esquema lo excluye deliberadamente con additionalProperties=false (Regla 17 "a nivel de
     * esquema", ver Javadoc del propio DTO generado). Se ejercita esa restriccion con el mismo
     * ObjectMapper de Spring que usaría el controlador al deserializar el body HTTP: un intento de
     * incluir "code" en el JSON debe fallar antes de llegar al servicio.
     */
    @Cuando("^intento modificar su código$")
    public void intento_modificar_su_codigo() {
        String json = "{\"code\":\"OTRO-CODIGO-BDD\",\"name\":\"Intento con otro código (BDD)\"}";
        try {
            objectMapper.readValue(json, CatalogDescriptorsUpdateRequestDto.class);
            contextoValidacion.setUltimaExcepcion(null);
        } catch (Exception ex) {
            contextoValidacion.setUltimaExcepcion(new RuntimeException(ex));
        }
    }
}
