package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.preinversion.enums.TipoMedidaCatalogo;
import sv.gob.mh.siip.model.preinversion.dto.MedidaCatalogoDto;
import sv.gob.mh.siip.model.preinversion.dto.TipoMedidaCatalogoDto;
import sv.gob.mh.siip.model.preinversion.repository.MedidaCatalogoRepository;
import sv.gob.mh.siip.security.ActorContexto;

@Service
@Transactional(readOnly = true)
public class MedidaCatalogoServiceImpl implements MedidaCatalogoService {

    private final MedidaCatalogoRepository medidaCatalogoRepository;
    private final ActorContexto actorContexto;

    public MedidaCatalogoServiceImpl(MedidaCatalogoRepository medidaCatalogoRepository, ActorContexto actorContexto) {
        this.medidaCatalogoRepository = medidaCatalogoRepository;
        this.actorContexto = actorContexto;
    }

    @Override
    public List<MedidaCatalogoDto> listar(TipoMedidaCatalogoDto tipo) {
        actorContexto.exigir();
        TipoMedidaCatalogo tipoDominio = TipoMedidaCatalogo.valueOf(tipo.name());
        return medidaCatalogoRepository.findByTipoOrderByCodigo(tipoDominio).stream()
                .map(m -> new MedidaCatalogoDto().codigo(m.getCodigo()).descripcion(m.getDescripcion()))
                .toList();
    }
}
