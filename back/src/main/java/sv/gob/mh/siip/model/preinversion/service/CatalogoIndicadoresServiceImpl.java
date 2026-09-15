package sv.gob.mh.siip.model.preinversion.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sv.gob.mh.siip.model.administracion.dto.IndicadorResultadoResumenDto;
import sv.gob.mh.siip.model.administracion.mapper.CatalogosAdministracionMapper;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.repository.IndicadorResultadoRepository;
import sv.gob.mh.siip.security.ActorContexto;

@Service
@Transactional(readOnly = true)
public class CatalogoIndicadoresServiceImpl implements CatalogoIndicadoresService {

    private final IndicadorResultadoRepository indicadorResultadoRepository;
    private final CatalogosAdministracionMapper mapper;
    private final ActorContexto actorContexto;

    public CatalogoIndicadoresServiceImpl(IndicadorResultadoRepository indicadorResultadoRepository,
            CatalogosAdministracionMapper mapper, ActorContexto actorContexto) {
        this.indicadorResultadoRepository = indicadorResultadoRepository;
        this.mapper = mapper;
        this.actorContexto = actorContexto;
    }

    /** FA01, paso 1.2: "el sistema permitirá filtrar por palabra los indicadores" — filtra por
     *  nombre o descripción, mismo criterio que {@code listarUbicacionesGeograficas}. */
    @Override
    public List<IndicadorResultadoResumenDto> listarIndicadoresResultado(String busqueda) {
        actorContexto.exigirRol(RolUsuario.TECNICO_URP);
        var indicadores = indicadorResultadoRepository.findAllByOrderByNombreAsc();
        if (busqueda != null && !busqueda.isBlank()) {
            String palabraClave = busqueda.toLowerCase();
            indicadores = indicadores.stream()
                    .filter(i -> i.getNombre().toLowerCase().contains(palabraClave)
                            || (i.getDescripcion() != null && i.getDescripcion().toLowerCase().contains(palabraClave)))
                    .toList();
        }
        return indicadores.stream().map(mapper::toResumen).toList();
    }
}
