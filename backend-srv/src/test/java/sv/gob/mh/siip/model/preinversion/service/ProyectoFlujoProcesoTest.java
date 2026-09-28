package sv.gob.mh.siip.model.preinversion.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProyectoFlujoProcesoTest {

    private RuntimeService runtimeService;
    private TaskService taskService;
    private TaskQuery taskQuery;
    private ProcessInstanceQuery processInstanceQuery;
    private ProyectoFlujoProceso flujoProceso;

    @BeforeEach
    void setUp() {
        runtimeService = mock(RuntimeService.class);
        taskService = mock(TaskService.class);
        taskQuery = mock(TaskQuery.class);
        processInstanceQuery = mock(ProcessInstanceQuery.class);
        when(taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.processInstanceBusinessKey(any())).thenReturn(taskQuery);
        when(runtimeService.createProcessInstanceQuery()).thenReturn(processInstanceQuery);
        when(processInstanceQuery.processInstanceBusinessKey(any())).thenReturn(processInstanceQuery);
        flujoProceso = new ProyectoFlujoProceso(runtimeService, taskService);
    }

    @Test
    void iniciar_arrancaElProcesoConElIdDelProyectoComoBusinessKey() {
        flujoProceso.iniciar(7L);

        verify(runtimeService).startProcessInstanceByKey("proceso_ciclo_vida_proyecto_siip", "7");
    }

    @Test
    void completarTareaEnElaboracion_completaLaTareaActiva() {
        Task tarea = mock(Task.class);
        when(tarea.getId()).thenReturn("tarea-1");
        when(taskQuery.singleResult()).thenReturn(tarea);

        flujoProceso.completarTareaEnElaboracion(7L);

        verify(taskQuery).processInstanceBusinessKey("7");
        verify(taskService).complete("tarea-1");
    }

    @Test
    void completarTareaEnElaboracion_noHaceNada_sinTareaActiva() {
        when(taskQuery.singleResult()).thenReturn(null);

        flujoProceso.completarTareaEnElaboracion(7L);

        verify(taskService, never()).complete(any());
    }

    @Test
    void cancelar_eliminaLaInstanciaConElMotivo() {
        ProcessInstance instancia = mock(ProcessInstance.class);
        when(instancia.getId()).thenReturn("instancia-1");
        when(processInstanceQuery.singleResult()).thenReturn(instancia);

        flujoProceso.cancelar(7L, "motivo");

        verify(processInstanceQuery).processInstanceBusinessKey("7");
        verify(runtimeService).deleteProcessInstance("instancia-1", "motivo");
    }

    @Test
    void cancelar_noHaceNada_sinInstancia() {
        when(processInstanceQuery.singleResult()).thenReturn(null);

        flujoProceso.cancelar(7L, "motivo");

        verify(runtimeService, never()).deleteProcessInstance(any(), any());
    }
}
