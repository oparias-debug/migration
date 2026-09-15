package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.model.administracion.dto.CriterioPriorizacionResumenDto;
import sv.gob.mh.siip.model.administracion.dto.EscalaCalificacionValorDto;
import sv.gob.mh.siip.model.administracion.dto.RangoInterpretacionDto;
import sv.gob.mh.siip.model.administracion.mapper.CatalogosAdministracionMapper;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.repository.CriterioPriorizacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.EscalaCalificacionSubcriterioRepository;
import sv.gob.mh.siip.model.preinversion.repository.RangoInterpretacionPriorizacionRepository;
import sv.gob.mh.siip.model.preinversion.repository.SubcriterioPriorizacionRepository;
import sv.gob.mh.siip.security.ActorContexto;

@Service
@Transactional(readOnly = true)
public class CatalogoPriorizacionServiceImpl implements CatalogoPriorizacionService {

    private static final RolUsuario[] ROLES_PRIORIZACION = { RolUsuario.TECNICO_PRE, RolUsuario.TECNICO_SYMP,
            RolUsuario.COORDINADOR_PRE, RolUsuario.COORDINADOR_SYMP, RolUsuario.SUBJEFE_DGI, RolUsuario.JEFE_DGI };

    private final CriterioPriorizacionRepository criterioPriorizacionRepository;
    private final SubcriterioPriorizacionRepository subcriterioPriorizacionRepository;
    private final EscalaCalificacionSubcriterioRepository escalaCalificacionSubcriterioRepository;
    private final RangoInterpretacionPriorizacionRepository rangoInterpretacionPriorizacionRepository;
    private final CatalogosAdministracionMapper mapper;
    private final ActorContexto actorContexto;

    public CatalogoPriorizacionServiceImpl(CriterioPriorizacionRepository criterioPriorizacionRepository,
            SubcriterioPriorizacionRepository subcriterioPriorizacionRepository,
            EscalaCalificacionSubcriterioRepository escalaCalificacionSubcriterioRepository,
            RangoInterpretacionPriorizacionRepository rangoInterpretacionPriorizacionRepository,
            CatalogosAdministracionMapper mapper, ActorContexto actorContexto) {
        this.criterioPriorizacionRepository = criterioPriorizacionRepository;
        this.subcriterioPriorizacionRepository = subcriterioPriorizacionRepository;
        this.escalaCalificacionSubcriterioRepository = escalaCalificacionSubcriterioRepository;
        this.rangoInterpretacionPriorizacionRepository = rangoInterpretacionPriorizacionRepository;
        this.mapper = mapper;
        this.actorContexto = actorContexto;
    }

    @Override
    public List<CriterioPriorizacionResumenDto> listarCriteriosPriorizacion() {
        actorContexto.exigirRol(ROLES_PRIORIZACION);
        return criterioPriorizacionRepository.findAllByOrderByNumeroCriterioAsc().stream().map(mapper::toResumen)
                .toList();
    }

    @Override
    public List<EscalaCalificacionValorDto> listarEscalaCalificacionSubcriterio(String codigoSubcriterio) {
        actorContexto.exigirRol(ROLES_PRIORIZACION);
        subcriterioPriorizacionRepository.findByCodigo(codigoSubcriterio)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el subcriterio '" + codigoSubcriterio + "'"));
        return escalaCalificacionSubcriterioRepository.findByCodigoSubcriterio(codigoSubcriterio).stream()
                .map(mapper::toResumen).toList();
    }

    @Override
    public List<RangoInterpretacionDto> listarRangosInterpretacionPriorizacion() {
        actorContexto.exigirRol(ROLES_PRIORIZACION);
        return rangoInterpretacionPriorizacionRepository.findAllByOrderByPuntajeMinimoAsc().stream()
                .map(mapper::toResumen).toList();
    }
}
