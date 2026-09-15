package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.administracion.dto.InsumoTipoResumenDto;
import sv.gob.mh.siip.model.administracion.dto.UnidadMedidaResumenDto;
import sv.gob.mh.siip.model.administracion.mapper.CatalogosAdministracionMapper;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.repository.InsumoTipoRepository;
import sv.gob.mh.siip.model.preinversion.repository.UnidadMedidaRepository;
import sv.gob.mh.siip.security.ActorContexto;

@Service
@Transactional(readOnly = true)
public class CatalogoPresupuestoServiceImpl implements CatalogoPresupuestoService {

    private final InsumoTipoRepository insumoTipoRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;
    private final CatalogosAdministracionMapper mapper;
    private final ActorContexto actorContexto;

    public CatalogoPresupuestoServiceImpl(InsumoTipoRepository insumoTipoRepository,
            UnidadMedidaRepository unidadMedidaRepository, CatalogosAdministracionMapper mapper,
            ActorContexto actorContexto) {
        this.insumoTipoRepository = insumoTipoRepository;
        this.unidadMedidaRepository = unidadMedidaRepository;
        this.mapper = mapper;
        this.actorContexto = actorContexto;
    }

    @Override
    public List<InsumoTipoResumenDto> listarInsumosTipo() {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        return insumoTipoRepository.findAllByOrderByNombreAsc().stream().map(mapper::toResumen).toList();
    }

    @Override
    public List<UnidadMedidaResumenDto> listarUnidadesMedida() {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        return unidadMedidaRepository.findAllByOrderByCategoriaAscNombreAsc().stream().map(mapper::toResumen)
                .toList();
    }
}
