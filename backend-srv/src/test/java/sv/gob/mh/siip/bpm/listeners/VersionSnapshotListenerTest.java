package sv.gob.mh.siip.bpm.listeners;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.flowable.engine.delegate.DelegateExecution;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

class VersionSnapshotListenerTest {

    private final VersionSnapshotListener listener = new VersionSnapshotListener();
    private final Logger logger = (Logger) LoggerFactory.getLogger(VersionSnapshotListener.class);
    private final ListAppender<ILoggingEvent> logAppender = new ListAppender<>();

    @BeforeEach
    void setUp() {
        logAppender.start();
        logger.addAppender(logAppender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(logAppender);
        logAppender.stop();
    }

    @Test
    void notify_registraLaTransicionConBusinessKeyActividadYEvento() {
        DelegateExecution execution = mock(DelegateExecution.class);
        when(execution.getProcessInstanceBusinessKey()).thenReturn("101");
        when(execution.getCurrentActivityId()).thenReturn("Flow_EnviarDgicp");
        when(execution.getEventName()).thenReturn("take");

        listener.notify(execution);

        assertThat(logAppender.list).singleElement()
                .extracting(ILoggingEvent::getFormattedMessage)
                .isEqualTo("[VersionSnapshot] businessKey=101 actividad=Flow_EnviarDgicp evento=take");
    }
}
