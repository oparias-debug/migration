package sv.gob.mh.api.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sv.gob.mh.infrastructure.config.authz.Permission;
import sv.gob.mh.infrastructure.config.authz.PermissionsAllowed;

/**
 * Ejemplo vivo del esquema de seguridad del marco, en cuatro niveles.
 *
 * <p>Cada endpoint sube un escalón: sin nada, con sesión, con permiso de lectura y con permiso
 * de borrado. Los dos últimos son los que de verdad enseñan autorización, porque con el padrón
 * de ejemplo cargado <b>dos usuarios autenticados obtienen respuestas distintas del mismo
 * endpoint</b>: el grupo administrador puede borrar y el de consulta recibe {@code 403}.</p>
 *
 * <p>El padrón que hace falta está en {@code authz/ejemplo-authz.json} y se carga con una sola
 * llamada; los detalles, en {@code docs/autorizacion.md}. Sin cargarlo, los dos endpoints con
 * {@link PermissionsAllowed} responden {@code 403} a todo el mundo, que es el comportamiento
 * correcto: el marco falla cerrado.</p>
 *
 * <p>Aquí la autorización no la resuelve una librería externa sino la copia vendorizada que
 * vive en {@code infrastructure/config/authz}: un aspecto AOP que lee la anotación y consulta
 * al {@code authorization-service}. El contrato es el mismo que en las plantillas Quarkus.</p>
 *
 * <p>Es de ejemplo y se borra en cuanto el servicio tenga sus propios recursos.</p>
 */
@RestController
@RequestMapping("/api/v1/demo/security")
public class SecurityController {

    /** Clave con la que se publica la identidad del usuario en las respuestas. */
    private static final String CLAVE_IDENTITY = "identity";

    /** Recurso del padrón que representa la consulta de expedientes. */
    private static final String RECURSO_CONSULTA = "expedientes-consulta";

    /** Recurso del padrón que representa el alta y la baja de expedientes. */
    private static final String RECURSO_REGISTRO = "expedientes-registro";

    /**
     * Nivel 1 — recurso público: responde sin exigir nada.
     *
     * <p>El acceso anónimo se declara además en {@code SecurityConfig}, que es quien decide
     * qué rutas quedan fuera del filtro de autenticación.</p>
     *
     * @return mensaje de bienvenida
     */
    @GetMapping(value = "/publico", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> publico() {
        return ResponseEntity.ok(Map.of("mensaje", "Recurso público: no exige autenticación."));
    }

    /**
     * Nivel 2 — sólo autenticación: devuelve los claims del token recibido.
     *
     * <p>Útil para comprobar que el token llega completo y ver qué publica Keycloak para el
     * usuario. En particular el claim {@code groups}, que es sobre el que se conceden los
     * permisos.</p>
     *
     * @param jwt token de la petición en curso
     * @return los claims del token y la identidad resuelta
     */
    @GetMapping(value = "/autenticado", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> autenticado(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> respuesta = new LinkedHashMap<>(jwt.getClaims());
        respuesta.put(CLAVE_IDENTITY, jwt.getSubject());
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Nivel 3 — autenticación y permiso de lectura sobre {@code expedientes-consulta}.
     *
     * <p>El {@code code} es documentación: en la llamada al servicio de autorización sólo viajan
     * la operación y el path, así que lo que decide es la pareja
     * {@code (VIEW, expedientes-consulta)}. El path es el <b>código del recurso</b> tal y como
     * está registrado en el componente: un segmento simple, sin barras ni puntos con
     * significado. La jerarquía la da el {@code parentCode} del padrón, no el texto del path.</p>
     *
     * @param jwt token de la petición en curso
     * @return la identidad que superó la comprobación y el expediente de ejemplo
     */
    @GetMapping(value = "/expedientes", produces = MediaType.APPLICATION_JSON_VALUE)
    @PermissionsAllowed(@Permission(
        code = "consultar-expedientes",
        operation = "VIEW",
        path = RECURSO_CONSULTA))
    public ResponseEntity<Map<String, Object>> consultarExpedientes(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(Map.of(
            CLAVE_IDENTITY, jwt.getSubject(),
            "expedientes", List.of(
                Map.of("id", 1, "descripcion", "Expediente de ejemplo"))));
    }

    /**
     * Nivel 4 — permiso de borrado sobre {@code expedientes-registro}.
     *
     * <p>Este es el que demuestra la autorización de verdad. Con el padrón de ejemplo cargado,
     * el grupo administrador tiene {@code DELETE} en {@code effect = 1} y el de consulta lo
     * tiene en {@code effect = 0}: <b>los dos están autenticados y sólo uno puede borrar</b>.
     * La denegación del hijo gana sobre el permiso heredado del padre.</p>
     *
     * @param id  identificador del expediente a eliminar
     * @param jwt token de la petición en curso
     * @return confirmación del borrado
     */
    @DeleteMapping(value = "/expedientes/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PermissionsAllowed(@Permission(
        code = "eliminar-expediente",
        operation = "DELETE",
        path = RECURSO_REGISTRO))
    public ResponseEntity<Map<String, Object>> eliminarExpediente(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(Map.of(
            CLAVE_IDENTITY, jwt.getSubject(),
            "eliminado", id));
    }
}
