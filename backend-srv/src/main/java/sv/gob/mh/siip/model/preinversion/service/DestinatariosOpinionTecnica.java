package sv.gob.mh.siip.model.preinversion.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.common.repository.UsuarioRepository;
import sv.gob.mh.siip.model.preinversion.domain.OpinionTecnica;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.domain.RevisionViabilidad;
import sv.gob.mh.siip.model.preinversion.repository.RevisionViabilidadRepository;

/** Destinatarios de los correos de CU-PRE-26 "Opinión Técnica" (RN07; Anexo A2). */
@Component
public class DestinatariosOpinionTecnica {

    private final UsuarioRepository usuarios;
    private final RevisionViabilidadRepository revisiones;

    public DestinatariosOpinionTecnica(UsuarioRepository usuarios, RevisionViabilidadRepository revisiones) {
        this.usuarios = usuarios;
        this.revisiones = revisiones;
    }

    /** @return los Técnicos URP activos de la Unidad Ejecutora del proyecto (la "Institución") */
    public List<Usuario> tecnicosUrp(Proyecto proyecto) {
        if (proyecto.getUnidadEjecutora() == null) {
            return List.of();
        }
        return usuarios.findByRolAndUnidadEjecutora_IdAndActivoTrue(RolUsuario.TECNICO_URP,
                proyecto.getUnidadEjecutora().getId());
    }

    /**
     * @param idTecnicoPre usuario a asignar
     * @return el usuario, si es un Técnico PRE activo (RN07 b)
     * @throws RecursoNoEncontradoException (404) si no existe o no es un Técnico PRE activo
     */
    public Usuario tecnicoPreActivo(Long idTecnicoPre) {
        return usuarios.findById(idTecnicoPre)
                .filter((Usuario u) -> u.getRol() == RolUsuario.TECNICO_PRE && Boolean.TRUE.equals(u.getActivo()))
                .orElseThrow(() -> new RecursoNoEncontradoException("Técnico PRE no encontrado."));
    }

    /** @return los Coordinadores PRE activos */
    public List<Usuario> coordinadoresPre() {
        return usuarios.findByRolAndActivoTrue(RolUsuario.COORDINADOR_PRE);
    }

    /** @return el Técnico PRE asignado a la gestión o, si todavía no hay, todos los activos */
    public List<Usuario> tecnicosPre(OpinionTecnica gestion) {
        return gestion.getTecnicoResponsable() != null ? List.of(gestion.getTecnicoResponsable())
                : usuarios.findByRolAndActivoTrue(RolUsuario.TECNICO_PRE);
    }

    /**
     * El Viabilizador del proyecto: el que revisó por última vez su Viabilidad (CU-PRE-24). Si ninguno la
     * revisó todavía o ya no está activo, todos los Viabilizadores activos.
     *
     * @param proyecto proyecto
     * @return los Viabilizadores a notificar
     */
    public List<Usuario> viabilizadores(Proyecto proyecto) {
        List<RevisionViabilidad> historial = revisiones.findByProyectoIdOrderByNumeroAsc(proyecto.getId());
        for (int i = historial.size() - 1; i >= 0; i--) {
            Usuario viabilizador = historial.get(i).getViabilizador();
            if (viabilizador != null) {
                return Boolean.TRUE.equals(viabilizador.getActivo()) ? List.of(viabilizador)
                        : usuarios.findByRolAndActivoTrue(RolUsuario.VIABILIZADOR);
            }
        }
        return usuarios.findByRolAndActivoTrue(RolUsuario.VIABILIZADOR);
    }

    /** RN07 c, RN08, RN09: la institución (Técnico URP) y el Viabilizador del proyecto. */
    public List<Usuario> institucionYViabilizadores(Proyecto proyecto) {
        return unir(tecnicosUrp(proyecto), viabilizadores(proyecto));
    }

    /** RN07 d: el Técnico PRE y el Coordinador PRE. */
    public List<Usuario> dgicp(OpinionTecnica gestion) {
        return unir(tecnicosPre(gestion), coordinadoresPre());
    }

    /** RN07 e: todos los actores del CU. */
    public List<Usuario> todos(Proyecto proyecto, OpinionTecnica gestion) {
        return unir(institucionYViabilizadores(proyecto), dgicp(gestion));
    }

    /** Une las listas sin repetir usuarios, en el orden recibido. */
    static List<Usuario> unir(List<Usuario> primeros, List<Usuario> segundos) {
        Map<Long, Usuario> porId = Stream.concat(primeros.stream(), segundos.stream())
                .collect(Collectors.toMap(Usuario::getId, Function.identity(), (Usuario a, Usuario b) -> a,
                        LinkedHashMap::new));
        return new ArrayList<>(porId.values());
    }
}
