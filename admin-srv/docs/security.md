# 5. Pilares del marco — autenticación y autorización

Dos capas, con responsables distintos:

| Capa | Quién la resuelve | Qué exige del código |
|---|---|---|
| **Autenticación**: ¿quién es? | Spring Security como *resource server* OAuth2 contra Keycloak, realm `MHINTERNO` | Nada por endpoint: **todo exige JWT** salvo lo listado como `permitAll` en `SecurityConfig` |
| **Autorización**: ¿puede hacer esto? | `@PermissionsAllowed` (aspecto AOP del proyecto), que consulta a `authorization-service` | La anotación con operación y recurso, y que el permiso exista en el autorizador |

Hoy los endpoints del servicio (CU-ADM-01 y CU-ADM-04) se autorizan por rol de realm con
`@PreAuthorize`; `@PermissionsAllowed` queda disponible en el marco, pero ningún endpoint lo usa
todavía.

---

## 1. Autenticación

### Qué hace la plataforma

El ConfigMap trae `SECURITY_URL_KEYCLOAK` (el host del Keycloak) y `SECURITY_REALM`; con
ellos el `application.yml` forma el `issuer-uri` y el `jwk-set-uri` del realm, y Spring
valida firma, expiración y emisor de cada JWT. Los roles del usuario se leen del claim
`groups` (`jwtAuthConverter` en `SecurityConfig`). Este servicio no usa el secreto del
cliente OIDC: sólo valida tokens.

### Qué haces tú

Nada para exigir autenticación: `SecurityConfig` cierra todo con `anyRequest().authenticated()`.
Lo que hay que declarar es lo **público**:

```java
// SecurityConfig.java — ya incluye actuator health/info, swagger y el hello de ejemplo
.requestMatchers("/api/v1/catalogos/publico/**").permitAll()
```

Para leer la identidad en un controller:

```java
@GetMapping("/perfil")
public PerfilDto perfil(@AuthenticationPrincipal Jwt jwt, Authentication auth) {
    return new PerfilDto(jwt.getSubject(), auth.getAuthorities(), jwt.getClaimAsString("email"));
}
```

Sin token válido la respuesta es `401`.

### Cómo obtener un token para probar

El cliente del marco es público, así que se usa el flujo por contraseña. El emisor es el
`authentication-service` del ambiente (fachada del Keycloak); el path lleva `realm` en
singular:

```bash
curl -s -X POST "https://authentication-service-mh-dev-dinafi-usi-frmk.apps.<dominio>/oidc/realm/MHINTERNO/protocol/openid-connect/token" \
  -d "grant_type=password" -d "client_id=<cliente>" \
  -d "username=<usuario>" -d "password=<password>" | jq -r .access_token
```

```bash
# 200 con el rol ADMINISTRADOR o ADMINISTRADOR_DE_CATALOGOS; 403 sin él; 401 sin token
curl -H "Authorization: Bearer $TOKEN" https://<route>/api/v1/catalogos
```

---

## 2. Autorización con `@PermissionsAllowed`

### Qué hace el módulo

`PermissionsAllowedInterceptor` es un `@Aspect` sobre `@annotation(PermissionsAllowed)`. Por
cada `@Permission` llama a

```text
GET {AUTHZ_SERVICE_URL}/verify-groups
    ?groupIds=<usuario>,<grupos del claim groups>
    &componentId=<service.name>
    &resourcePath=<path>
    &operationName=<operation>
```

y lanza `AccessDeniedException` (`403`) si no se cumple. Con `requireAll = false` (default)
basta uno; con `true` deben cumplirse todos. Al ser AOP, funciona sobre cualquier bean de
Spring (controllers y servicios) sin configuración adicional.

### Qué haces tú

```java
import sv.gob.mh.infrastructure.config.authz.Permission;
import sv.gob.mh.infrastructure.config.authz.PermissionsAllowed;

@GetMapping("/expedientes/{id}")
@PermissionsAllowed(value = {
    @Permission(code = "consultar expediente",
                operation = "VIEW",
                path = "expedientes-consulta")
})
public ExpedienteDto consultar(@PathVariable Long id) { ... }
```

| Atributo | Qué es | Viaja al autorizador |
|---|---|---|
| `code` | Descripción legible del permiso | No; es documentación |
| `operation` | Operación del catálogo del autorizador: `VIEW`, `CREATE`, `UPDATE`, `DELETE`, `EXECUTE`, `ALLOW` (otras se dan de alta) | Sí |
| `path` | Código del recurso, tal cual está registrado en el autorizador | Sí |

Convención de `path`: `<sistema>.<módulo>.<recurso>`, en minúsculas y sin `/`. La herencia la
da el árbol de recursos del autorizador (un permiso sobre el padre aplica a los hijos salvo
denegación explícita).

### Registrar el permiso en el autorizador

1. En `autorizacion-ui` del ambiente, dentro del componente
   `dgicp-siip-admin-srv` (el `service.name`),
   crear el recurso con código igual al `path`.
2. Asignar al grupo (o usuario) la operación sobre ese recurso con efecto **permitir**.
3. Repetir en cada ambiente, o llevarlo con la promoción de configuración.

Los grupos del JWT se registran **sin la barra inicial** (`/Contribuyentes` →
`Contribuyentes`). También se puede otorgar a un usuario concreto usando su `username` como
grupo.

### Cómo comprobar

```bash
# con un usuario que tiene el permiso: 200
curl -s -o /dev/null -w "%{http_code}\n" -H "Authorization: Bearer $TOKEN" https://<route>/<endpoint con @PermissionsAllowed>
# con un usuario sin el permiso: 403
# sin token: 401
```

Un `403` con el permiso recién creado suele ser caché del autorizador; esperar o pedir el
reinicio de `authorization-service` en dev.

---

## 3. Pruebas unitarias con seguridad

`src/test/resources/application.yml` apunta el `issuer-uri` a un mock y la base a H2, así que
las pruebas arrancan sin Keycloak ni Oracle. Para simular un usuario se usa el
post-procesador `jwt()` de `spring-security-test`, como en `CatalogosSeguridadTest`:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ExpedienteControllerTest {

    @Autowired MockMvc mockMvc;

    @Test
    void consultaAutenticada() throws Exception {
        mockMvc.perform(get("/api/v1/expedientes/1")
                .with(jwt().jwt(j -> j.subject("usuario1").claim("groups", List.of("Contribuyentes")))))
            .andExpect(status().isOk());
    }

    @Test
    void sinToken() throws Exception {
        mockMvc.perform(get("/api/v1/expedientes/1")).andExpect(status().isUnauthorized());
    }
}
```

`@PermissionsAllowed` en pruebas unitarias no llega al autorizador real; la verificación de
permisos se prueba con Karate contra un ambiente desplegado ([testing.md](testing.md)).

---

## 4. Reglas

| Regla | Por qué |
|---|---|
| Permisos con `@PermissionsAllowed`, no con `@PreAuthorize("hasRole(…)")` ni comprobando el claim a mano | Los permisos se administran en el autorizador sin redesplegar |
| Lo público se declara explícitamente en `SecurityConfig` | Todo lo demás exige JWT por defecto |
| No escribir el JWT en logs | El logger remoto saca los mensajes del pod |
| TLS siempre validado; sin `trust-all` fuera de desarrollo | Producción usa certificados propios; ver [tls-certificates.md](tls-certificates.md) |

---

## 5. Si algo falla

| Síntoma | Causa | Qué hacer |
|---|---|---|
| `401` con token válido | `SECURITY_URL_KEYCLOAK` con `https://` o realm distinto al del emisor; token expirado | Comparar `iss` del token con el `issuer-uri` efectivo |
| `403` siempre | El recurso no existe en el autorizador **para este `service.name`**, o el grupo se registró con `/` | Revisar en `autorizacion-ui` |
| `403` con mensaje de conexión | `AUTHZ_SERVICE_URL` incorrecta o autorizador caído; el módulo responde denegado ante error | Revisar el ConfigMap y la salud de `authorization-service` |
| El arranque falla al resolver el issuer | Keycloak inalcanzable (sin VPN en local) | Ver [local-development.md](local-development.md) §2.4 |
