# 10. Pruebas funcionales y de seguridad

Qué pruebas trae el servicio, cómo se corren en local, cuáles corre el ciclo solo y cómo
pedir las que se corren bajo demanda. La parte de seguridad está al final: es lo que el
ciclo revisa en cada cambio.

---

## 1. Lo que trae el repositorio

```text
src/test/
├── java/sv/gob/mh/
│   ├── ApplicationTest.java                        # el contexto arranca
│   ├── SecurityControllerTest.java                 # @SpringBootTest + MockMvc + jwt()
│   ├── api/controller/SecurityControllerUnitTest.java
│   ├── infrastructure/config/OpenApiConfigTest.java
│   └── pruebas/{integration,smoke,stress}/runners/ # runners Karate por tipo
├── pruebas/
│   ├── integration/features/  health/, security/   # Karate contra un servicio desplegado
│   ├── integration/oauth2-token-helper.feature      # obtiene el token (contraseña o cliente)
│   ├── smoke/features/                              # disponibilidad rápida
│   ├── stress/features/                             # carga: load, spike, single-request
│   └── run-tests.sh                                 # lo usa el ciclo
└── resources/
    ├── application.yml                              # H2 e issuer en mock para unitarias
    └── karate-config.js                             # ambiente, URLs y credenciales de Karate
```

| Tipo | Herramienta | Perfil Maven | Contra qué corre |
|---|---|---|---|
| Unitarias | JUnit 5, `@SpringBootTest`, MockMvc, `spring-security-test` | (por defecto) | La aplicación en memoria, con H2 |
| Integración | Karate | `-Pintegration-tests` | Un servicio desplegado |
| Smoke | Karate | `-Psmoke-tests` | Un servicio desplegado |
| Carga | Karate en paralelo | `-Pstress-tests` | Un servicio desplegado |

---

## 2. Unitarias

```bash
./mvnw test                               # todas
./mvnw test -Dtest=SecurityControllerTest # una clase
./mvnw verify -Preport                    # con cobertura JaCoCo: target/site/jacoco/index.html
```

`src/test/resources/application.yml` usa H2 en memoria y un `issuer-uri` de mock, así que
las unitarias no necesitan VPN ni credenciales. Para simular usuarios se usa `jwt()`
([security.md](security.md) §3).

Al añadir un endpoint, añadir al menos: el caso feliz, el `401` sin token y el `400` de
validación. La cobertura la mide SonarQube en cada revisión.

---

## 3. Integración, smoke y carga en local

Corren contra un servicio **desplegado**; en local, contra el que tengas levantado con
`spring-boot:run` o Podman, o contra dev con VPN.

```bash
# contra el servicio local
./mvnw verify -Pintegration-tests -Dkarate.env=dev

# contra un ambiente: la URL y el emisor de token van por -DargLine, no por -D a secas,
# porque surefire bifurca la JVM y los -D de la línea de comandos no llegan a las pruebas
./mvnw verify -Pintegration-tests -Dkarate.env=qa \
  -DargLine="-Dbase.url=https://siipsafi-srv-mh-qa-dgicp-siip2.apps.<dominio> \
             -Doauth2.token.url=https://authentication-service-mh-qa-dinafi-usi-devhub.apps.<dominio>/oidc/realm/MHINTERNO/protocol/openid-connect/token \
             -Doauth2.username=<usuario> -Doauth2.password=<password> -Doauth2.client.id=<cliente>"

./mvnw verify -Psmoke-tests -Dkarate.env=dev
./mvnw verify -Pstress-tests -Dkarate.env=dev -DargLine="-Dbase.url=http://localhost:8080"
```

Reportes en `target/karate-reports/karate-summary.html`.

Sin credenciales, Karate corre los escenarios públicos y los de `401`, y marca los
autenticados como no verificados. El helper `oauth2-token-helper.feature` elige el flujo
por lo que recibe: usuario y contraseña → `password`; secreto de cliente →
`client_credentials`. El cliente del marco es público, así que hoy el flujo es por
contraseña.

### Escribir una prueba nueva

Un `.feature` en `src/test/pruebas/integration/features/<módulo>/`; el runner
`IntegrationTestRunner` los recoge todos. Plantilla mínima:

```gherkin
Feature: Expedientes

  Background:
    * url baseUrl
    * def token = call read('classpath:pruebas/integration/oauth2-token-helper.feature')
    * header Authorization = 'Bearer ' + token.accessToken

  Scenario: consultar un expediente existente
    Given path 'api/v1/expedientes/1'
    When method get
    Then status 200
    And match response.id == 1
```

### Concurrencia y umbral de las de carga

| Qué | Dónde se fija | Valor |
|---|---|---|
| Hilos concurrentes | Parámetro `STRESS_THREADS` de la pipeline | 10 por defecto |
| Tiempo de respuesta máximo | `assert responseTime < 2000` en los propios features | 2 s |

---

## 4. Lo que corre el ciclo solo

### 4.1 En cada revisión (CR)

Las unitarias, con el mismo `mvn` que en local. Si fallan, `Verified -1`. Además SonarQube,
que cuenta la cobertura.

### 4.2 Tras cada despliegue en dev

El CI ejecuta `karate-verify` **después de desplegar**, con el perfil `integration-tests`.
La tarea espera a que el servicio esté listo (hasta 900 s) antes de probar. Consulta por
defecto `/q/health/ready`; en Spring la readiness es `/actuator/health/readiness`, así que
pide al equipo de plataforma que fije el `HEALTH_PATH` del servicio o el veredicto será
`UNVERIFIED` por espera vencida.

**No bloquea el ciclo**: informa. El veredicto está en el log del PipelineRun.

---

## 5. Bajo demanda, empujando a una rama

Tres pipelines que se lanzan por el **nombre de la rama**. El ambiente sale del sufijo:

| Rama | Qué ejecuta |
|---|---|
| `pruebas-funcional-<ambiente>` | perfil `integration-tests` |
| `pruebas-stress-<ambiente>` | perfil `stress-tests` |
| `pruebas-zap-<ambiente>` | análisis dinámico de seguridad (OWASP ZAP) contra el servicio desplegado |

`<ambiente>` es `dev`, `qa`, `test` o `preprod`. Por ejemplo:

```bash
git push origin HEAD:refs/heads/pruebas-stress-qa
```

La URL del servicio y el emisor del token salen del bloque `verificacion` del
`values.yaml` **de la rama de ese ambiente** en el repositorio `-config`. No hay que
pasarlos a mano.

### 5.1 Dónde queda el informe

Las tres publican su informe HTML en la documentación del proyecto en Tuleap, con nombre
`<tipo>-<dirección>-<servicio>-<ambiente>-<ejecución>.html`.

### 5.2 Qué significa cada veredicto

| Veredicto | Significa |
|---|---|
| `OK` | Se ejecutó y pasó |
| `FAILED` | Se ejecutó y falló. Hay algo que mirar en el servicio |
| `HALLAZGOS` | ZAP encontró algo por encima del umbral |
| `UNVERIFIED` | **No se pudo probar**. NO es un aprobado |
| `SKIPPED` | El chart no declara `verificacion.baseUrl` para ese ambiente |

**Un `UNVERIFIED` no se puede leer como "está bien".** El resumen del log dice qué faltó.

### 5.3 Lo único que hay que configurar

La credencial con la que las pruebas piden el token. Vive en el Secret `karate-auth` del
namespace `*-cicloci` de la dirección, con una clave por aplicación y ambiente:

```text
<aplicación>.<ambiente>.clientId
<aplicación>.<ambiente>.username
<aplicación>.<ambiente>.password
```

Sin ella corren los escenarios públicos y los de 401, y el veredicto es `UNVERIFIED`. Pedir
al equipo de plataforma que registre la entrada de
`dgicp-siip2-siipsafi-srv` para cada ambiente.

---

## 6. Seguridad en el ciclo

| Cuándo | Control | Qué busca | Efecto |
|---|---|---|---|
| CR | SonarQube (perfil de seguridad del Ministerio) | Vulnerabilidades y *hotspots* en el código | Gate en la revisión |
| CR | `secret-scan` (gitleaks) | Credenciales versionadas | Comentario en la revisión |
| CR | `trivy-scan` | CVE en las dependencias del `pom.xml` | Comentario por severidad |
| CI | `acs-image-check` (Advanced Cluster Security) | CVE en la imagen construida, incluida la base | **Bloquea** si supera el umbral |
| CI | `trusted-artifact-signer` | Firma la imagen | Sólo se despliegan imágenes firmadas |
| Bajo demanda | `pruebas-zap-<ambiente>` | Pruebas dinámicas (DAST) | Informe en Tuleap |
| Siempre | Chart | Contenedor sin privilegios, sin escalada, sin capacidades | |

Qué hacer con un hallazgo:

- **Secreto versionado**: retirarlo, rotarlo y reescribir el commit. Borrarlo en un commit
  nuevo no basta: sigue en el historial.
- **CVE en dependencia**: subir la versión en el `pom.xml` (o el `spring-boot-starter-parent`).
- **CVE en la imagen base**: la mantiene la plataforma; reportarlo.
- **Hotspot de Sonar**: preferir el arreglo en código; es el único que sobrevive a que la
  línea se mueva.

---

## 7. Si algo falla

| Síntoma | Causa | Qué hacer |
|---|---|---|
| `401` en todos los escenarios autenticados | Sin credencial, o usuario sin acceso al cliente | Pasar `-Doauth2.username`/`password` por `-DargLine`; en el ciclo, la entrada en `karate-auth` |
| `Public client not allowed to retrieve service account` | Se intentó `client_credentials` con el cliente público | Usar usuario y contraseña |
| `Connection refused` / timeout | `base.url` incorrecta o el servicio no está arriba | `curl <base.url>/actuator/health` |
| Los `-D` no llegan a las pruebas | Se pasaron sin `-DargLine` | Ver §3 |
| `karate-verify` en `UNVERIFIED` | Credencial ausente, o espera de readiness vencida (`HEALTH_PATH`) | §4.2 y §5.3 |
