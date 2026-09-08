package sv.gob.mh.siip.model.preinversion.repository;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.EstadoProyecto;
import sv.gob.mh.siip.model.preinversion.enums.IniciativaInversion;

/**
 * Repositorio de acceso a datos para la entidad {@link Proyecto}.
 *
 * @author Luis Medrano
 * @see Proyecto
 * @see JpaRepository
 * @see JpaSpecificationExecutor
 */
@Repository
public interface ProyectoCapturaRepository
        extends JpaRepository<Proyecto, Long>, JpaSpecificationExecutor<Proyecto> {

    /**
     * Especificaciones para la construcción de consultas dinámicas sobre {@link Proyecto}.
     *
     * @author Luis Medrano
     */
    interface Specs {

        /** Nombre del campo del identificador en {@link Proyecto}. */
        String FIELD_ID = "id";

        /** Nombre del campo del código único de proyecto en {@link Proyecto}. */
        String FIELD_CUP = "cup";

        /** Nombre del campo de denominación del proyecto en {@link Proyecto}. */
        String FIELD_NOMBRE = "nombre";

        /** Nombre del campo de estado activo en {@link Proyecto}. */
        String FIELD_ACTIVO = "activo";

        /** Nombre del campo de la unidad ejecutora asociada en {@link Proyecto}. */
        String FIELD_UNIDAD_EJECUTORA = "unidadEjecutora";

        /** Nombre del campo de tipo de iniciativa en {@link Proyecto}. */
        String FIELD_INICIATIVA = "iniciativaInversion";

        /** Nombre del campo de estado del proyecto en {@link Proyecto}. */
        String FIELD_ESTADO = "estado";

        /**
         * Aplica un FETCH JOIN sobre la relación con {@link sv.gob.mh.siip.model.common.domain.UnidadEjecutora}
         * para optimizar el rendimiento y evitar el problema de N+1 consultas durante la serialización DTO.
         * <p>
         * Se omite el FETCH cuando Hibernate ejecuta la consulta de conteo para la paginación ({@code SELECT COUNT}).
         *
         * @return {@link Specification} con la instrucción de carga anticipada.
         */
        static Specification<Proyecto> fetchUnidadEjecutora() {
            return (root, query, cb) -> {
                Class<?> resultType = query.getResultType();
                if (Long.class.equals(resultType) || boolean.class.equals(resultType)) {
                    return null;
                }
                root.fetch(FIELD_UNIDAD_EJECUTORA, JoinType.LEFT);
                return null;
            };
        }

        /**
         * Filtra entidades válidas que contengan un CUP no nulo y tengan estado activo.
         *
         * @return {@link Specification} con la condición lógica.
         */
        static Specification<Proyecto> esValidoParaCaptura() {
            return (root, query, cb) -> cb.and(
                    cb.isNotNull(root.get(FIELD_CUP)),
                    cb.isTrue(root.get(FIELD_ACTIVO))
            );
        }

        /**
         * Filtra proyectos por el identificador de la unidad ejecutora.
         *
         * @param idUnidadEjecutora identificador de la unidad ejecutora.
         * @return {@link Specification} correspondiente.
         */
        static Specification<Proyecto> byUnidadEjecutora(Long idUnidadEjecutora) {
            return (root, query, cb) -> idUnidadEjecutora == null ? null :
                    cb.equal(root.get(FIELD_UNIDAD_EJECUTORA).get(FIELD_ID), idUnidadEjecutora);
        }

        /**
         * Realiza una búsqueda coincidencial sobre CUP, nombre del proyecto o nombre de la unidad ejecutora.
         *
         * @param busqueda término o cadena de texto ingresada.
         * @return {@link Specification} que agrupa la búsqueda.
         */
        static Specification<Proyecto> byBusquedaGeneral(String busqueda) {
            return (root, query, cb) -> {
                if (busqueda == null || busqueda.isBlank()) {
                    return null;
                }
                String term = "%" + busqueda.toLowerCase().trim() + "%";
                return cb.or(
                        cb.like(cb.lower(root.get(FIELD_CUP)), term),
                        cb.like(cb.lower(root.get(FIELD_NOMBRE)), term),
                        cb.like(cb.lower(root.get(FIELD_UNIDAD_EJECUTORA).get(FIELD_NOMBRE)), term)
                );
            };
        }

        /**
         * Filtra por coincidencia parcial en el parámetro CUP.
         *
         * @param cup código único de proyecto.
         * @return {@link Specification} correspondiente.
         */
        static Specification<Proyecto> byCup(String cup) {
            return (root, query, cb) -> (cup == null || cup.isBlank()) ? null :
                    cb.like(cb.lower(root.get(FIELD_CUP)), "%" + cup.toLowerCase().trim() + "%");
        }

        /**
         * Filtra por coincidencia parcial en el nombre del proyecto.
         *
         * @param nombre término de búsqueda para la denominación.
         * @return {@link Specification} correspondiente.
         */
        static Specification<Proyecto> byNombre(String nombre) {
            return (root, query, cb) -> (nombre == null || nombre.isBlank()) ? null :
                    cb.like(cb.lower(root.get(FIELD_NOMBRE)), "%" + nombre.toLowerCase().trim() + "%");
        }

        /**
         * Filtra por el código de la iniciativa de inversión.
         *
         * @param iniciativaValue valor textual del enumerado de iniciativa.
         * @return {@link Specification} correspondiente.
         */
        static Specification<Proyecto> byIniciativa(String iniciativaValue) {
            return (root, query, cb) -> {
                if (iniciativaValue == null || iniciativaValue.isBlank()) {
                    return null;
                }
                try {
                    IniciativaInversion enumValue = IniciativaInversion.valueOf(iniciativaValue);
                    return cb.equal(root.get(FIELD_INICIATIVA), enumValue);
                } catch (IllegalArgumentException _) {
                    return cb.disjunction(); // Si el String no coincide con ningún Enum válido, fuerza resultado vacío seguro
                }
            };
        }

        /**
         * Filtra por el código del estado del proyecto.
         *
         * @param estadoValue valor textual del enumerado de estado.
         * @return {@link Specification} correspondiente.
         */
        static Specification<Proyecto> byEstado(String estadoValue) {
            return (root, query, cb) -> {
                if (estadoValue == null || estadoValue.isBlank()) {
                    return null;
                }
                try {
                    EstadoProyecto enumValue = EstadoProyecto.valueOf(estadoValue);
                    return cb.equal(root.get(FIELD_ESTADO), enumValue);
                } catch (IllegalArgumentException _) {
                    return cb.disjunction(); // Si el String no coincide con ningún Enum válido, fuerza resultado vacío seguro
                }
            };
        }
    }
}