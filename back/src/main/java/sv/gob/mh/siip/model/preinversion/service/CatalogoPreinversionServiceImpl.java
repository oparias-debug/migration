package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.preinversion.dto.EjePlanGobiernoResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.EjeTematicoResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.PlanSectorialRegionalResumenDto;
import sv.gob.mh.siip.model.preinversion.dto.SectorResumenDto;
import sv.gob.mh.siip.model.preinversion.mapper.ProyectoMapper;
import sv.gob.mh.siip.model.preinversion.repository.EjePlanGobiernoRepository;
import sv.gob.mh.siip.model.preinversion.repository.EjeTematicoRepository;
import sv.gob.mh.siip.model.preinversion.repository.PlanSectorialRegionalRepository;
import sv.gob.mh.siip.model.programacion.repository.SectorActividadRepository;
import sv.gob.mh.siip.security.ActorContexto;

@Service
@Transactional(readOnly = true)
public class CatalogoPreinversionServiceImpl implements CatalogoPreinversionService {

    private final SectorActividadRepository sectorActividadRepository;
    private final EjeTematicoRepository ejeTematicoRepository;
    private final EjePlanGobiernoRepository ejePlanGobiernoRepository;
    private final PlanSectorialRegionalRepository planSectorialRegionalRepository;
    private final ProyectoMapper mapper;
    private final ActorContexto actorContexto;

    public CatalogoPreinversionServiceImpl(SectorActividadRepository sectorActividadRepository,
            EjeTematicoRepository ejeTematicoRepository,
            EjePlanGobiernoRepository ejePlanGobiernoRepository,
            PlanSectorialRegionalRepository planSectorialRegionalRepository,
            ProyectoMapper mapper,
            ActorContexto actorContexto) {
        this.sectorActividadRepository = sectorActividadRepository;
        this.ejeTematicoRepository = ejeTematicoRepository;
        this.ejePlanGobiernoRepository = ejePlanGobiernoRepository;
        this.planSectorialRegionalRepository = planSectorialRegionalRepository;
        this.mapper = mapper;
        this.actorContexto = actorContexto;
    }

    @Override
    public List<SectorResumenDto> listarSectores() {
        actorContexto.exigir();
        return sectorActividadRepository.findAllByOrderByNombreAsc().stream().map(mapper::toResumen).toList();
    }

    @Override
    public List<EjeTematicoResumenDto> listarEjesTematicos() {
        actorContexto.exigir();
        return ejeTematicoRepository.findByActivoTrueOrderByNombre().stream().map(mapper::toResumen).toList();
    }

    @Override
    public List<EjePlanGobiernoResumenDto> listarEjesPlanGobierno() {
        actorContexto.exigir();
        return ejePlanGobiernoRepository.findByActivoTrueOrderByNombre().stream().map(mapper::toResumen).toList();
    }

    @Override
    public List<PlanSectorialRegionalResumenDto> listarPlanesSectoriales() {
        actorContexto.exigir();
        return planSectorialRegionalRepository.findByActivoTrueOrderByNombre().stream().map(mapper::toResumen)
                .toList();
    }
}
