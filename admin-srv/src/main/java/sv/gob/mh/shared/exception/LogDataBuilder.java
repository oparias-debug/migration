package sv.gob.mh.shared.exception;

import java.time.LocalDateTime;

/**
 * Constructor de {@link LogData}.
 *
 * <p>Vive fuera de {@code LogData} a propósito: como clase anidada superaba de largo el tamaño
 * que admiten las reglas del Ministerio, y separarlo deja además el registro —que es un simple
 * contenedor de datos— sin lógica dentro.</p>
 *
 * <p>Rellena por su cuenta el instante y el hilo, que es lo que casi nadie recuerda poner y lo
 * primero que se busca al leer un log.</p>
 */
public class LogDataBuilder {

    private final LogData logData;

    public LogDataBuilder() {
        this.logData = new LogData();
        this.logData.setTimestamp(LocalDateTime.now().toString());
        this.logData.setThread(Thread.currentThread().getName());
    }

    public LogDataBuilder level(String level) {
        this.logData.setLevel(level);
        return this;
    }

    public LogDataBuilder logger(String logger) {
        this.logData.setLogger(logger);
        return this;
    }

    public LogDataBuilder message(String message) {
        this.logData.setMessage(message);
        return this;
    }

    /**
     * Añade la excepción y <b>dónde ocurrió</b>: clase, método y línea del primer marco de la
     * pila. Sin eso, en el otro extremo queda un mensaje sin sitio donde mirar.
     *
     * @param throwable la excepción, o {@code null} si el registro no viene de un error
     * @return este mismo constructor
     */
    public LogDataBuilder exception(Throwable throwable) {
        if (throwable != null) {
            this.logData.setException(throwable.getClass().getName());
            this.logData.setExceptionMessage(throwable.getMessage());

            StackTraceElement[] elements = throwable.getStackTrace();
            if (elements.length > 0) {
                this.logData.setClassName(elements[0].getClassName());
                this.logData.setMethod(elements[0].getMethodName());
                this.logData.setLine(elements[0].getLineNumber());
            }
        }
        return this;
    }

    public LogDataBuilder thread(String thread) {
        this.logData.setThread(thread);
        return this;
    }

    public LogDataBuilder timestamp(String timestamp) {
        this.logData.setTimestamp(timestamp);
        return this;
    }

    public LogData build() {
        return this.logData;
    }
}
