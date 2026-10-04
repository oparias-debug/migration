package sv.gob.mh.shared.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * POJO que representa los datos de un log que se envía al servicio remoto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogData {
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    @JsonProperty("level")
    private String level;
    
    @JsonProperty("logger")
    private String logger;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("thread")
    private String thread;
    
    @JsonProperty("exception")
    private String exception;
    
    @JsonProperty("exception_message")
    private String exceptionMessage;
    
    @JsonProperty("class")
    private String className;
    
    @JsonProperty("method")
    private String method;
    
    @JsonProperty("line")
    private Integer line;
    
    // Constructor por defecto necesario para Jackson
    public LogData() {
    }
    
    // Constructor completo
    public LogData(String timestamp, String level, String logger, String message, String thread) {
        this.timestamp = timestamp;
        this.level = level;
        this.logger = logger;
        this.message = message;
        this.thread = thread;
    }
    
    // Builder pattern para facilitar la construcción
    
    // Método factory para crear un builder
    /**
     * Punto de entrada del constructor fluido.
     *
     * @return un constructor nuevo, con el instante y el hilo ya puestos
     */
    public static LogDataBuilder builder() {
        return new LogDataBuilder();
    }
    
    // Getters y Setters
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getLevel() {
        return level;
    }
    
    public void setLevel(String level) {
        this.level = level;
    }
    
    public String getLogger() {
        return logger;
    }
    
    public void setLogger(String logger) {
        this.logger = logger;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getThread() {
        return thread;
    }
    
    public void setThread(String thread) {
        this.thread = thread;
    }
    
    public String getException() {
        return exception;
    }
    
    public void setException(String exception) {
        this.exception = exception;
    }
    
    public String getExceptionMessage() {
        return exceptionMessage;
    }
    
    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }
    
    public String getClassName() {
        return className;
    }
    
    public void setClassName(String className) {
        this.className = className;
    }
    
    public String getMethod() {
        return method;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    public Integer getLine() {
        return line;
    }
    
    public void setLine(Integer line) {
        this.line = line;
    }
    
    @Override
    public String toString() {
        return "LogData{" +
                "timestamp='" + timestamp + '\'' +
                ", level='" + level + '\'' +
                ", logger='" + logger + '\'' +
                ", message='" + message + '\'' +
                ", thread='" + thread + '\'' +
                ", exception='" + exception + '\'' +
                ", exceptionMessage='" + exceptionMessage + '\'' +
                ", className='" + className + '\'' +
                ", method='" + method + '\'' +
                ", line=" + line +
                '}';
    }
}
