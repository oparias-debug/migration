package sv.gob.mh.siip.bpm.listeners;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adjunto (evento "take") a las transiciones de {@code Proceso_SIIF.bpmn20.xml} que representan un
 * cambio real de "Estado del Proyecto" (catálogo RN04). Placeholder: solo deja constancia en el log
 * de la transición; persistir un historial real de versiones del proyecto queda pendiente de diseño
 * en una tarea aparte.
 */
public class VersionSnapshotListener implements ExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(VersionSnapshotListener.class);

    @Override
    public void notify(DelegateExecution execution) {
        logger.info("[VersionSnapshot] businessKey={} actividad={} evento={}", execution.getProcessInstanceBusinessKey(),
                execution.getCurrentActivityId(), execution.getEventName());
    }
}
