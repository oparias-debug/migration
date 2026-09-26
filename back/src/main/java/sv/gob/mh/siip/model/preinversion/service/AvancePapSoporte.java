package sv.gob.mh.siip.model.preinversion.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.ZoneId;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.dto.CuatrimestreDto;
import sv.gob.mh.siip.model.preinversion.dto.PaginacionMetadataDto;
import sv.gob.mh.siip.model.preinversion.enums.Cuatrimestre;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * Funciones puras compartidas por el avance cuatrimestral del PAP, CU-PRE-32 (financiero) y
 * CU-PRE-33 (metas físicas): cuatrimestre vigente, roles de consulta, filtros por defecto,
 * paginación y montos programados por cuatrimestre.
 */
final class AvancePapSoporte {

    public static final ZoneId ZONA_EL_SALVADOR = ZoneId.of("America/El_Salvador");

    private static final int TAMANIO_PAGINA_POR_DEFECTO = 20;
    private static final int MES_FIN_PRIMER_CUATRIMESTRE = 4;
    private static final int MES_FIN_SEGUNDO_CUATRIMESTRE = 8;

    private static final RolUsuario[] ROLES_CONSULTA = {
            RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE, RolUsuario.COORDINADOR_PRE,
            RolUsuario.COORDINADOR_PROGRAMACION, RolUsuario.TECNICO_PROG, RolUsuario.JEFE_DGI, RolUsuario.SUBJEFE_DGI
    };

    /**
     * Actores internos de la DGICP, únicos que ven los "Comentarios al reporte DGICP": CU-PRE-32
     * Anexo A.6 ("Comentarios al reporte financiero DGICP", mismo criterio que CU-PRE-30 Anexo A.8) y
     * CU-PRE-33 RN-A.b/RN-D.a. El Técnico URP no los ve.
     */
    private static final RolUsuario[] ROLES_DGICP_INTERNOS = {
            RolUsuario.TECNICO_PRE, RolUsuario.COORDINADOR_PRE, RolUsuario.COORDINADOR_PROGRAMACION,
            RolUsuario.TECNICO_PROG, RolUsuario.JEFE_DGI, RolUsuario.SUBJEFE_DGI
    };

    private AvancePapSoporte() {
    }

    /** Exige alguno de los roles de consulta del avance del PAP y devuelve el actor. */
    static Usuario exigirRolConsulta(ActorContexto actorContexto) {
        return actorContexto.exigirRol(ROLES_CONSULTA);
    }

    /** Exige el rol Técnico URP (único que registra el avance del cuatrimestre) y devuelve el actor. */
    static Usuario exigirTecnicoUrp(ActorContexto actorContexto) {
        return actorContexto.exigirRol(RolUsuario.TECNICO_URP);
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

    /** Período consultado; sin período, el cuatrimestre vigente (RN-A.a). */
    static Cuatrimestre periodoOVigente(CuatrimestreDto periodo) {
        return periodo != null ? Cuatrimestre.valueOf(periodo.name()) : cuatrimestreVigente();
    }

    /** RN-A.a: "el cuatrimestre vigente" según el mes actual (I: ene-abr, II: may-ago, III: sep-dic). */
    static Cuatrimestre cuatrimestreVigente() {
        Month mes = LocalDateTime.now(ZONA_EL_SALVADOR).getMonth();
        if (mes.getValue() <= MES_FIN_PRIMER_CUATRIMESTRE) {
            return Cuatrimestre.CUATRIMESTRE_I;
        }
        return mes.getValue() <= MES_FIN_SEGUNDO_CUATRIMESTRE
                ? Cuatrimestre.CUATRIMESTRE_II : Cuatrimestre.CUATRIMESTRE_III;
    }

    static CuatrimestreDto dtoDe(Cuatrimestre periodo) {
        return CuatrimestreDto.valueOf(periodo.name());
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

    static boolean esActorInternoDgicp(Usuario actor) {
        for (RolUsuario rol : ROLES_DGICP_INTERNOS) {
            if (rol == actor.getRol()) {
                return true;
            }
        }
        return false;
    }

    /** Programado del cuatrimestre "periodo" a partir de los tres montos cuatrimestrales de la programación. */
    static BigDecimal programadoDelCuatrimestre(Cuatrimestre periodo, BigDecimal cuatrimestre1,
            BigDecimal cuatrimestre2, BigDecimal cuatrimestre3) {
        return switch (periodo) {
            case CUATRIMESTRE_I -> cuatrimestre1;
            case CUATRIMESTRE_II -> cuatrimestre2;
            case CUATRIMESTRE_III -> cuatrimestre3;
        };
    }

    /** Programado acumulado desde el Cuatrimestre I hasta "hastaPeriodo" (inclusive). */
    static BigDecimal programadoAlCuatrimestre(Cuatrimestre hastaPeriodo, BigDecimal cuatrimestre1,
            BigDecimal cuatrimestre2, BigDecimal cuatrimestre3) {
        BigDecimal total = BigDecimal.ZERO;
        for (Cuatrimestre c : Cuatrimestre.values()) {
            if (c.ordinal() <= hastaPeriodo.ordinal()) {
                total = total.add(programadoDelCuatrimestre(c, cuatrimestre1, cuatrimestre2, cuatrimestre3));
            }
        }
        return total;
    }

    static LocalDateTime ahora() {
        return LocalDateTime.now(ZONA_EL_SALVADOR);
    }

    static <T> List<T> nullSafe(List<T> lista) {
        return lista != null ? lista : List.of();
    }
}
