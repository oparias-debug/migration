package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.administracion.dto.CriterioElegibilidadResumenDto;
import sv.gob.mh.siip.model.administracion.dto.EntradaCatalogoEspecificarDto;
import sv.gob.mh.siip.model.administracion.dto.TipoCatalogoEspecificarDto;
import sv.gob.mh.siip.model.administracion.mapper.CatalogosAdministracionMapper;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.enums.TipoCatalogoEspecificar;
import sv.gob.mh.siip.model.preinversion.repository.CriterioElegibilidadRepository;
import sv.gob.mh.siip.model.preinversion.repository.EntradaCatalogoEspecificarRepository;
import sv.gob.mh.siip.security.ActorContexto;

@Service
@Transactional(readOnly = true)
public class CatalogoElegibilidadServiceImpl implements CatalogoElegibilidadService {

    private final CriterioElegibilidadRepository criterioElegibilidadRepository;
    private final EntradaCatalogoEspecificarRepository entradaCatalogoEspecificarRepository;
    private final CatalogosAdministracionMapper mapper;
    private final ActorContexto actorContexto;

    public CatalogoElegibilidadServiceImpl(CriterioElegibilidadRepository criterioElegibilidadRepository,
            EntradaCatalogoEspecificarRepository entradaCatalogoEspecificarRepository,
            CatalogosAdministracionMapper mapper, ActorContexto actorContexto) {
        this.criterioElegibilidadRepository = criterioElegibilidadRepository;
        this.entradaCatalogoEspecificarRepository = entradaCatalogoEspecificarRepository;
        this.mapper = mapper;
        this.actorContexto = actorContexto;
    }

    @Override
    public List<CriterioElegibilidadResumenDto> listarCriteriosElegibilidad() {
        actorContexto.exigirRol(RolUsuario.VIABILIZADOR);
        return criterioElegibilidadRepository.findAllByOrderByCodigoAsc().stream().map(mapper::toResumen).toList();
    }

    @Override
    public List<EntradaCatalogoEspecificarDto> listarCatalogoEspecificarElegibilidad(
            TipoCatalogoEspecificarDto tipo) {
        actorContexto.exigirRol(RolUsuario.VIABILIZADOR);
        TipoCatalogoEspecificar tipoDominio = TipoCatalogoEspecificar.valueOf(tipo.name());
        return entradaCatalogoEspecificarRepository.findByTipoOrderByCodigoAsc(tipoDominio).stream()
                .map(mapper::toResumen).toList();
    }
}
