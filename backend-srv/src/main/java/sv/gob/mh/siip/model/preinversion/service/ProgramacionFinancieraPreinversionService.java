package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.common.domain.Usuario;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.EtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.ProgramacionFinPreinversionConfig;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.programacion.dto.ConfigurarPeriodosProgramacionPreinversionRequestDto;
import sv.gob.mh.siip.model.preinversion.programacion.dto.FilaProgramacionEtapaRequestDto;
import sv.gob.mh.siip.model.preinversion.programacion.dto.ProgramacionFinancieraPreinversionDto;
import sv.gob.mh.siip.model.preinversion.programacion.dto.ProgramacionFinancieraPreinversionRequestDto;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgramacionFinPreinversionConfigRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgramacionFinPreinversionDetalleRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * Lógica de CU-PRE-22.1: programación financiera por etapa y período. Las validaciones, la escritura de
 * detalles y el armado de la respuesta se delegan en colaboradores del mismo paquete.
 */
@Service
@Transactional
public class ProgramacionFinancieraPreinversionService {
    private final ProyectoRepository proyectos;
    private final EtapaPreinversionRepository etapas;
    private final ProgramacionFinPreinversionConfigRepository configuraciones;
    private final ProgramacionFinPreinversionDetalleRepository detalles;
    private final ActorContexto actor;
    private final ProgramacionFinancieraPreinversionPersistencia persistencia;

    public ProgramacionFinancieraPreinversionService(ProyectoRepository proyectos, EtapaPreinversionRepository etapas,
            ProgramacionFinPreinversionConfigRepository configuraciones,
            ProgramacionFinPreinversionDetalleRepository detalles, ActorContexto actor) {
        this.proyectos = proyectos;
        this.etapas = etapas;
        this.configuraciones = configuraciones;
        this.detalles = detalles;
        this.actor = actor;
        this.persistencia = new ProgramacionFinancieraPreinversionPersistencia(etapas, detalles);
    }

    public ProgramacionFinancieraPreinversionDto obtener(Long idProyecto) {
        var usuario = actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE);
        var proyecto = proyecto(idProyecto);
        if (usuario.getRol() == RolUsuario.TECNICO_URP) {
            ProgramacionFinancieraPreinversionValidaciones.exigirAlcanceUnidadEjecutora(usuario, proyecto);
        }
        return respuesta(proyecto);
    }

    public ProgramacionFinancieraPreinversionDto configurarPeriodos(Long idProyecto,
            ConfigurarPeriodosProgramacionPreinversionRequestDto request) {
        var usuario = actor.exigirRol(RolUsuario.TECNICO_URP);
        var proyecto = proyecto(idProyecto);
        ProgramacionFinancieraPreinversionValidaciones.exigirAlcanceUnidadEjecutora(usuario, proyecto);
        int periodos = ProgramacionFinancieraPreinversionValidaciones.periodosValidos(
                request == null ? null : request.getPeriodosAProgramar());
        ProgramacionFinPreinversionConfig config = configuraciones.findByProyectoId(idProyecto)
                .orElseGet(() -> ProgramacionFinPreinversionConfig.builder().proyecto(proyecto).build());
        config.setPeriodosAProgramar(periodos);
        configuraciones.save(config);
        // Al reducir períodos, los detalles fuera del nuevo horizonte no pueden seguir
        // afectando el costo que CU-PRE-03.5 muestra para la etapa.
        persistencia.depurarYRecalcular(proyecto, etapasProgramables(proyecto), periodos);
        return respuesta(proyecto);
    }

    public ProgramacionFinancieraPreinversionDto guardar(Long idProyecto,
            ProgramacionFinancieraPreinversionRequestDto request) {
        var usuario = actor.exigirRol(RolUsuario.TECNICO_URP);
        var proyecto = proyecto(idProyecto);
        ProgramacionFinancieraPreinversionValidaciones.exigirAlcanceUnidadEjecutora(usuario, proyecto);
        int periodos = periodosConfigurados(idProyecto);
        List<EtapaPreinversion> etapasProgramables = etapasProgramables(proyecto);
        List<FilaProgramacionEtapaRequestDto> filas = ProgramacionFinancieraPreinversionValidaciones
                .filasValidas(request, etapasProgramables, periodos);
        persistencia.reemplazar(proyecto, etapasProgramables, filas);
        return respuesta(proyecto);
    }

    private ProgramacionFinancieraPreinversionDto respuesta(Proyecto proyecto) {
        int periodos = configuraciones.findByProyectoId(proyecto.getId())
                .map(ProgramacionFinPreinversionConfig::getPeriodosAProgramar)
                .filter((Integer periodosConfigurados) -> periodosConfigurados > 0)
                .orElse(0);
        return ProgramacionFinancieraPreinversionAssembler.armar(proyecto.getId(), periodos,
                detalles.findByProyectoId(proyecto.getId()), etapasProgramables(proyecto));
    }

    private Proyecto proyecto(Long idProyecto) {
        return proyectos.findById(idProyecto)
                .orElseThrow(() -> new RecursoNoEncontradoException("Proyecto no encontrado"));
    }

    private int periodosConfigurados(Long idProyecto) {
        return configuraciones.findByProyectoId(idProyecto)
                .map(ProgramacionFinPreinversionConfig::getPeriodosAProgramar)
                .filter((Integer periodos) -> periodos > 0)
                .orElseThrow(() -> ProgramacionFinancieraPreinversionValidaciones
                        .validacion("Debe configurar al menos un período antes de guardar."));
    }

    private List<EtapaPreinversion> etapasProgramables(Proyecto proyecto) {
        return etapas.findByProyectoId(proyecto.getId()).stream()
                .filter((EtapaPreinversion etapa) -> etapa.getTipoEtapa() != TipoEtapaPreinversion.EJECUCION)
                .toList();
    }
}
