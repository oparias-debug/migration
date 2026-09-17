package sv.gob.mh.siip.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.siip.exception.RecursoNoEncontradoException;
import sv.gob.mh.siip.exception.ValidacionNegocioException;
import sv.gob.mh.siip.model.common.enums.RolUsuario;
import sv.gob.mh.siip.model.preinversion.domain.ConfiguracionProgramacionPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.ProgramacionEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.domain.Proyecto;
import sv.gob.mh.siip.model.preinversion.enums.TipoEtapaPreinversion;
import sv.gob.mh.siip.model.preinversion.repository.ConfiguracionProgramacionPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.EtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProgramacionEtapaPreinversionRepository;
import sv.gob.mh.siip.model.preinversion.repository.ProyectoRepository;
import sv.gob.mh.siip.security.ActorContexto;

/**
 * CU-PRE-22.1: programación financiera por etapa y período.
 */
@RestController
@RequestMapping("/proyectos/{idProyecto}/programacion-financiera-preinversion")
public class ProgramacionFinancieraPreinversionController {

    private final ProyectoRepository proyectos;
    private final ConfiguracionProgramacionPreinversionRepository configuraciones;
    private final ProgramacionEtapaPreinversionRepository programaciones;
    private final EtapaPreinversionRepository etapas;
    private final ActorContexto actor;

    public ProgramacionFinancieraPreinversionController(ProyectoRepository p, ConfiguracionProgramacionPreinversionRepository c, ProgramacionEtapaPreinversionRepository pr, EtapaPreinversionRepository e, ActorContexto a) {
        proyectos = p;
        configuraciones = c;
        programaciones = pr;
        etapas = e;
        actor = a;
    }

    @GetMapping
    public Map<String, Object> obtener(@PathVariable Long idProyecto) {
        actor.exigirRol(RolUsuario.TECNICO_URP, RolUsuario.TECNICO_PRE);
        proyecto(idProyecto);
        return respuesta(idProyecto);
    }

    @PutMapping("/periodos")
    public Map<String, Object> configurarPeriodos(@PathVariable Long idProyecto, @RequestBody Map<String, Object> req) {
        actor.exigirRol(RolUsuario.TECNICO_URP);
        Proyecto p = proyecto(idProyecto);
        if (!(req.get("periodosAProgramar") instanceof Number n) || n.intValue() < 0) {
            throw new ValidacionNegocioException("Períodos inválidos", List.of());

        }
        ConfiguracionProgramacionPreinversion c = configuraciones.findByProyectoId(idProyecto).orElseGet(() -> ConfiguracionProgramacionPreinversion.builder().proyecto(p).build());
        c.setPeriodosAProgramar(n.intValue());
        configuraciones.save(c);
        return respuesta(idProyecto);
    }

    @PutMapping
    public Map<String, Object> guardar(@PathVariable Long idProyecto, @RequestBody Map<String, Object> req) {
        actor.exigirRol(RolUsuario.TECNICO_URP);
        proyecto(idProyecto);
        if (!(req.get("filas") instanceof List<?> filas)) {
            return respuesta(idProyecto);

        }
        programaciones.deleteAll(programaciones.findByProyectoId(idProyecto));
        for (Object o : filas) {
            if (!(o instanceof Map<?, ?> m) || !(m.get("etapa") instanceof String nombre)) {
                continue;

            }
            TipoEtapaPreinversion tipo;
            try {
                tipo = TipoEtapaPreinversion.valueOf(nombre);
            } catch (IllegalArgumentException ex) {
                throw new ValidacionNegocioException("Etapa inválida", List.of());
            }
            List<Double> valores = valores(m.get("programacionPorPeriodo"));
            ProgramacionEtapaPreinversion fila = programaciones.save(ProgramacionEtapaPreinversion.builder().proyecto(proyecto(idProyecto)).etapa(tipo).programacionPorPeriodo(valores).build());
            double total = total(fila.getProgramacionPorPeriodo());
            etapas.findByProyectoIdAndTipoEtapa(idProyecto, tipo).ifPresent(e -> {
                e.setCosto(total);
                etapas.save(e);
            });
        }
        return respuesta(idProyecto);
    }

    private Map<String, Object> respuesta(Long id) {
        int n = configuraciones.findByProyectoId(id).map(x -> x.getPeriodosAProgramar() == null ? 0 : x.getPeriodosAProgramar()).orElse(0);
        List<Double> totalPeriodo = ceros(n);
        List<Map<String, Object>> filas = new ArrayList<>();
        for (ProgramacionEtapaPreinversion f : programaciones.findByProyectoId(id)) {
            List<Double> valores = new ArrayList<>(f.getProgramacionPorPeriodo());
            while (valores.size() < n) {
                valores.add(null);

            }
            double total = total(valores);
            for (int i = 0; i < n; i++) {
                totalPeriodo.set(i, r(totalPeriodo.get(i) + (valores.get(i) == null ? 0 : valores.get(i))));

            }
            Map<String, Object> x = new LinkedHashMap<>();
            x.put("etapa", f.getEtapa().name());
            x.put("programacionPorPeriodo", valores);
            x.put("totalProgramacion", total);
            x.put("costoEtapa", total);
            filas.add(x);
        }
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("idProyecto", id);
        out.put("periodosAProgramar", n == 0 ? null : n);
        out.put("filas", filas);
        out.put("totalGeneralPorPeriodo", totalPeriodo);
        out.put("totalGeneral", total(totalPeriodo));
        return out;
    }

    private Proyecto proyecto(Long id) {
        return proyectos.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Proyecto no encontrado"));
    }

    private static List<Double> valores(Object o) {
        List<Double> x = new ArrayList<>();
        if (o instanceof List<?> l) {
            for (Object v : l) {
                x.add(v instanceof Number n ? n.doubleValue() : null);

            }
        }
        return x;
    }

    private static List<Double> ceros(int n) {
        List<Double> x = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            x.add(0D);

        }
        return x;
    }

    private static double total(List<Double> x) {
        return r(x.stream().filter(Objects::nonNull).mapToDouble(Double::doubleValue).sum());
    }

    private static double r(double x) {
        return Math.round(x * 100D) / 100D;
    }
}
