package sv.gob.mh.api.dto.catalogo;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * {@link CatalogDescriptorsUpdateRequestDto} que recuerda qué propiedades vinieron en el JSON,
 * incluidas las que vinieron con {@code null}. El DTO generado (openApiNullable=false) no
 * distingue "ausente" de "null", y el contrato sí: {@code parent: null} desvincula el catálogo de
 * su padre, mientras que omitir {@code parent} lo deja igual (Regla 22). Jackson la instancia en
 * lugar del DTO por el mixin de {@code CatalogosJacksonConfig}.
 */
@JsonDeserialize
public class DescriptoresCatalogoInformados extends CatalogDescriptorsUpdateRequestDto {

    public static final String PROPIEDAD_NAME = "name";
    public static final String PROPIEDAD_PARENT = "parent";
    public static final String PROPIEDAD_ACTIVE = "active";
    public static final String PROPIEDAD_FROM_DATE = "fromDate";
    public static final String PROPIEDAD_TO_DATE = "toDate";

    @JsonIgnore
    private final Set<String> informadas = new HashSet<>();

    /**
     * Propiedades presentes en la solicitud. Para un DTO construido en código (no deserializado)
     * se infiere de las propiedades no nulas.
     */
    public static Set<String> informadas(CatalogDescriptorsUpdateRequestDto request) {
        if (request instanceof DescriptoresCatalogoInformados informados) {
            return Set.copyOf(informados.informadas);
        }
        Set<String> noNulas = new HashSet<>();
        agregarSiNoNula(noNulas, PROPIEDAD_NAME, request.getName());
        agregarSiNoNula(noNulas, PROPIEDAD_PARENT, request.getParent());
        agregarSiNoNula(noNulas, PROPIEDAD_ACTIVE, request.getActive());
        agregarSiNoNula(noNulas, PROPIEDAD_FROM_DATE, request.getFromDate());
        agregarSiNoNula(noNulas, PROPIEDAD_TO_DATE, request.getToDate());
        return noNulas;
    }

    private static void agregarSiNoNula(Set<String> propiedades, String nombre, Object valor) {
        if (valor != null) {
            propiedades.add(nombre);
        }
    }

    @Override
    public void setName(@Nullable String name) {
        informadas.add(PROPIEDAD_NAME);
        super.setName(name);
    }

    @Override
    public void setParent(@Nullable String parent) {
        informadas.add(PROPIEDAD_PARENT);
        super.setParent(parent);
    }

    @Override
    public void setActive(@Nullable ActiveStatusDto active) {
        informadas.add(PROPIEDAD_ACTIVE);
        super.setActive(active);
    }

    @Override
    public void setFromDate(@Nullable LocalDate fromDate) {
        informadas.add(PROPIEDAD_FROM_DATE);
        super.setFromDate(fromDate);
    }

    @Override
    public void setToDate(@Nullable LocalDate toDate) {
        informadas.add(PROPIEDAD_TO_DATE);
        super.setToDate(toDate);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
