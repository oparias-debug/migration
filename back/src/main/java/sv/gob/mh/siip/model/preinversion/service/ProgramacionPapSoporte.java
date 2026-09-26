package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.dto.NombreEtapaDto;
import sv.gob.mh.siip.model.preinversion.dto.PaginacionMetadataDto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * Funciones puras compartidas por la programación cuatrimestral del PAP, CU-PRE-30 (financiera) y
 * CU-PRE-31 (metas físicas): roles de consulta, unidad ejecutora y año efectivos, paginación, fecha
 * actual en El Salvador, agrupación de la solicitud por etapa y conversión de montos.
 */
final class ProgramacionPapSoporte {

    public static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private static final int TAMANIO_PAGINA_POR_DEFECTO = 20;

    /** Actores que consultan la Programación Financiera del PAP (CU-PRE-30). */
    private static final RolUsuario[] ROLES_CONSULTA_FINANCIERA = {
            RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE, RolUsuario.COORDINADOR_PRE,
            RolUsuario.COORDINADOR_PROGRAMACION, RolUsuario.TECNICO_PROG, RolUsuario.JEFE_DGI, RolUsuario.SUBJEFE_DGI
    };

    /** Actores que consultan la Programación de Metas Físicas del PAP (CU-PRE-31). */
    private static final RolUsuario[] ROLES_CONSULTA_METAS = {
            RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE, RolUsuario.COORDINADOR_PRE,
            RolUsuario.COORDINADOR_PROGRAMACION, RolUsuario.TECNICO_PROG, RolUsuario.JEFE_DGI,
            RolUsuario.SUBJEFE_DGI, RolUsuario.TECNICO_SYMP, RolUsuario.COORDINADOR_SYMP
    };

    /** RN-C (CU-PRE-31): actores internos de la DGICP, únicos que pueden ver "Comentarios al reporte DGICP". */
    private static final RolUsuario[] ROLES_DGICP_INTERNOS = {
            RolUsuario.TECNICO_PRE, RolUsuario.COORDINADOR_PRE, RolUsuario.COORDINADOR_PROGRAMACION,
            RolUsuario.TECNICO_PROG, RolUsuario.JEFE_DGI, RolUsuario.SUBJEFE_DGI,
            RolUsuario.TECNICO_SYMP, RolUsuario.COORDINADOR_SYMP
    };

    private ProgramacionPapSoporte() {
    }

    /** Exige alguno de los roles de consulta de la Programación Financiera (CU-PRE-30) y devuelve el actor. */
    static Usuario exigirRolConsultaFinanciera(ActorContexto actorContexto) {
        return actorContexto.exigirRol(ROLES_CONSULTA_FINANCIERA);
    }

    /** Exige alguno de los roles de consulta de la Programación de Metas Físicas (CU-PRE-31) y devuelve el actor. */
    static Usuario exigirRolConsultaMetas(ActorContexto actorContexto) {
        return actorContexto.exigirRol(ROLES_CONSULTA_METAS);
    }

    static boolean esActorInternoDgicp(Usuario actor) {
        for (RolUsuario rol : ROLES_DGICP_INTERNOS) {
            if (rol == actor.getRol()) {
                return true;
            }
        }
        return false;
    }

    /** El Técnico URP queda restringido a su propia unidad ejecutora; los demás actores usan el parámetro. */
    static Long unidadEjecutoraEfectiva(Usuario actor, Long idUnidadEjecutora) {
        return actor.getRol() == RolUsuario.TECNICO_URP
                ? actor.getUnidadEjecutora().getId()
                : idUnidadEjecutora;
    }

    /** Año consultado; sin año, el año en curso en El Salvador. */
    static int anioOActual(Integer anio) {
        return anio != null ? anio : Year.now(ZONA_EL_SALVADOR).getValue();
    }

    static LocalDateTime ahora() {
        return LocalDateTime.now(ZONA_EL_SALVADOR);
    }

    /**
     * Página solicitada; valores ausentes o inválidos se reemplazan por la primera página o el
     * tamaño por defecto.
     */
    static Pageable paginaSolicitada(Integer pagina, Integer tamanio) {
        return PageRequest.of(
                (pagina != null && pagina >= 0) ? pagina : 0,
                (tamanio != null && tamanio > 0) ? tamanio : TAMANIO_PAGINA_POR_DEFECTO);
    }

    static PaginacionMetadataDto paginacion(Page<?> resultado) {
        return new PaginacionMetadataDto()
                .pagina(resultado.getNumber())
                .tamanio(resultado.getSize())
                .totalElementos(resultado.getTotalElements())
                .totalPaginas(resultado.getTotalPages());
    }

    /** Agrupa los elementos de la solicitud por el tipo de etapa que indica cada uno ("etapaDe"). */
    static <T> Map<TipoEtapaPreinversion, T> construirMapaPorEtapa(List<T> etapas,
            Function<T, NombreEtapaDto> etapaDe) {
        Map<TipoEtapaPreinversion, T> porEtapa = new EnumMap<>(TipoEtapaPreinversion.class);
        for (T item : nullSafe(etapas)) {
            porEtapa.put(TipoEtapaPreinversion.valueOf(etapaDe.apply(item).name()), item);
        }
        return porEtapa;
    }

    static BigDecimal bd(Double valor) {
        return valor != null ? BigDecimal.valueOf(valor) : BigDecimal.ZERO;
    }

    /** El valor como {@code Double} si es mayor que cero; {@code null} si es nulo, cero o negativo. */
    static Double positivoONulo(BigDecimal valor) {
        return (valor != null && valor.compareTo(BigDecimal.ZERO) > 0) ? valor.doubleValue() : null;
    }

    static <T> List<T> nullSafe(List<T> lista) {
        return lista != null ? lista : List.of();
    }
}
