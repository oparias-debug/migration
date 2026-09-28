package sv.gob.mh.siip.model.preinversion.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import sv.gob.mh.siip.model.common.service.OpenApiDtoMapper;
import sv.gob.mh.siip.model.preinversion.domain.ActividadOm;
import sv.gob.mh.siip.model.preinversion.om.dto.ActividadDto;
import sv.gob.mh.siip.model.preinversion.om.dto.ActividadRequestDto;
import sv.gob.mh.siip.model.preinversion.om.dto.ConfigurarPresupuestoOMRequestDto;
import sv.gob.mh.siip.model.preinversion.om.dto.PresupuestoOMDto;
import sv.gob.mh.siip.model.preinversion.om.dto.TipoCostoTablaDto;

/** Adaptador de aplicación: traduce el contrato OpenAPI sin cargar esa responsabilidad al controller. */
@Service
public class PresupuestoOmApiService {

    private final PresupuestoOmService service;
    private final OpenApiDtoMapper mapper;

    public PresupuestoOmApiService(PresupuestoOmService service, OpenApiDtoMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    public PresupuestoOMDto obtener(Long idProyecto) {
        return presupuesto(service.obtener(idProyecto));
    }

    public PresupuestoOMDto configurar(Long idProyecto, ConfigurarPresupuestoOMRequestDto request) {
        return presupuesto(service.configurar(idProyecto, mapper.aMapa(request)));
    }

    public ActividadDto registrar(Long idProyecto, TipoCostoTablaDto tipoCostoTabla, ActividadRequestDto request) {
        ActividadOm actividad = service.registrarActividad(idProyecto, tipoCostoTabla.getValue(),
                mapper.aMapa(request));
        return actividadRegistrada(service.obtener(idProyecto), tipoCostoTabla, actividad.getId());
    }

    public void eliminar(Long idProyecto, TipoCostoTablaDto tipoCostoTabla, Long idActividad) {
        service.eliminarActividad(idProyecto, tipoCostoTabla.getValue(), idActividad);
    }

    public PresupuestoOMDto guardar(Long idProyecto) {
        return presupuesto(service.guardar(idProyecto));
    }

    private PresupuestoOMDto presupuesto(Map<String, Object> respuesta) {
        return mapper.aDto(respuesta, PresupuestoOMDto.class);
    }

    private ActividadDto actividadRegistrada(Map<String, Object> respuesta, TipoCostoTablaDto tipoCostoTabla,
            Long idActividad) {
        String tabla = tipoCostoTabla == TipoCostoTablaDto.OPERACION ? "costosOperacion" : "costosMantenimiento";
        if (respuesta.get(tabla) instanceof Map<?, ?> costos && costos.get("actividades") instanceof List<?> lista) {
            for (Object item : lista) {
                if (item instanceof Map<?, ?> actividad && idActividad.equals(actividad.get("idActividad"))) {
                    return mapper.aDto(comoMapaDeTexto(actividad), ActividadDto.class);
                }
            }
        }
        throw new IllegalStateException("La actividad registrada no está disponible en el presupuesto del proyecto.");
    }

    private static Map<String, Object> comoMapaDeTexto(Map<?, ?> valores) {
        Map<String, Object> resultado = new LinkedHashMap<>();
        valores.forEach((clave, valor) -> resultado.put((String) clave, valor));
        return resultado;
    }
}
