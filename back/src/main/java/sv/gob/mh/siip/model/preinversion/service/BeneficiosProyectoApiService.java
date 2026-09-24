package sv.gob.mh.siip.model.preinversion.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import sv.gob.mh.siip.model.common.service.OpenApiDtoMapper;
import sv.gob.mh.siip.model.preinversion.beneficios.dto.BeneficioDto;
import sv.gob.mh.siip.model.preinversion.beneficios.dto.BeneficioRequestDto;
import sv.gob.mh.siip.model.preinversion.beneficios.dto.BeneficiosDelProyectoDto;
import sv.gob.mh.siip.model.preinversion.beneficios.dto.GuardarBeneficiosProyectoRequestDto;

/** Adaptador de aplicación del contrato OpenAPI de CU-PRE-20. */
@Service
public class BeneficiosProyectoApiService {

    private final BeneficiosProyectoService service;
    private final OpenApiDtoMapper mapper;

    public BeneficiosProyectoApiService(BeneficiosProyectoService service, OpenApiDtoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    public BeneficiosDelProyectoDto obtener(Long idProyecto) {
        return presupuesto(service.obtenerBeneficios(idProyecto));
    }

    public BeneficioDto registrar(Long idProyecto, BeneficioRequestDto request) {
        return mapper.aDto(service.registrarBeneficio(idProyecto, mapper.aMapa(request)), BeneficioDto.class);
    }

    public void eliminar(Long idProyecto, Long idBeneficio) {
        service.eliminarBeneficio(idProyecto, idBeneficio);
    }

    public BeneficiosDelProyectoDto guardar(Long idProyecto, GuardarBeneficiosProyectoRequestDto request) {
        return presupuesto(service.guardarConfiguracion(idProyecto, mapper.aMapa(request)));
    }

    private BeneficiosDelProyectoDto presupuesto(Map<String, Object> respuesta) {
        return mapper.aDto(respuesta, BeneficiosDelProyectoDto.class);
    }
}
