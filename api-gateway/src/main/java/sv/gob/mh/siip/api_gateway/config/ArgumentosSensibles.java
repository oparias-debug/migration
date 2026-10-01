package sv.gob.mh.siip.api_gateway.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca un controlador (o un método) cuyos argumentos llevan credenciales: contraseñas, refresh
 * tokens. {@link GatewayAuditoriaAspect} registra la llamada, pero no sus argumentos.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ArgumentosSensibles {
}
