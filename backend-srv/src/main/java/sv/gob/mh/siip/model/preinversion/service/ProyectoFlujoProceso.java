package sv.gob.mh.siip.model.preinversion.service;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Component;

/**
 * Instancia Flowable del ciclo de vida del proyecto ({@code Proceso_SIIF.bpmn20.xml}), identificada
 * por el id del proyecto como business key: arranque al registrarlo, avance de la tarea en
 * elaboración al solicitar el CUP y cancelación.
 */
@Component
public class ProyectoFlujoProceso {

    /** Id del proceso {@code Proceso_SIIF.bpmn20.xml} que modela el ciclo de vida del proyecto. */
    private static final String PROCESS_DEFINITION_KEY = "proceso_ciclo_vida_proyecto_siip";

    private final RuntimeService runtimeService;
    private final TaskService taskService;

    public ProyectoFlujoProceso(RuntimeService runtimeService, TaskService taskService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
    }

    /** Arranca la instancia de proceso del proyecto recién registrado. */
    public void iniciar(Long idProyecto) {
        runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION_KEY, String.valueOf(idProyecto));
    }

    /**
     * Completa la tarea activa del proceso Flowable asociada al proyecto (arrancada en
     * {@link #iniciar(Long)}). Si no existe ninguna (dato creado fuera del flujo real, por ejemplo
     * en pruebas), no hay nada que avanzar.
     */
    public void completarTareaEnElaboracion(Long idProyecto) {
        Task tarea = taskService.createTaskQuery()
                .processInstanceBusinessKey(String.valueOf(idProyecto))
                .singleResult();
        if (tarea != null) {
            taskService.complete(tarea.getId());
        }
    }

    /**
     * Cancela la instancia de proceso Flowable del proyecto, si existe (ver
     * {@link #completarTareaEnElaboracion(Long)}).
     */
    public void cancelar(Long idProyecto, String motivo) {
        ProcessInstance instancia = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(String.valueOf(idProyecto))
                .singleResult();
        if (instancia != null) {
            runtimeService.deleteProcessInstance(instancia.getId(), motivo);
        }
    }
}
