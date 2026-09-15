package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.administracion.dto.ParametroResumenDto;
import sv.gob.mh.siip.model.administracion.mapper.CatalogosAdministracionMapper;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.repository.ParametroRepository;
import sv.gob.mh.siip.security.ActorContexto;

@Service
@Transactional(readOnly = true)
public class CatalogoBeneficiosServiceImpl implements CatalogoBeneficiosService {

    private final ParametroRepository parametroRepository;
    private final CatalogosAdministracionMapper mapper;
    private final ActorContexto actorContexto;

    public CatalogoBeneficiosServiceImpl(ParametroRepository parametroRepository,
            CatalogosAdministracionMapper mapper, ActorContexto actorContexto) {
        this.parametroRepository = parametroRepository;
        this.mapper = mapper;
        this.actorContexto = actorContexto;
    }

    @Override
    public List<ParametroResumenDto> listarParametrosBeneficio() {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        return parametroRepository.findAllByOrderByNombreAsc().stream().map(mapper::toResumen).toList();
    }
}
