# Pendientes

Lista única de lo que falta resolver en SIIP, agrupada por quién lo destraba. Cada punto indica
de dónde sale (para ir al detalle) y si ya estaba anotado en otro documento o se detectó al
armar esta lista (**nuevo**).

Al cerrar un punto: marcarlo `[x]`, anotar la fecha y cómo se resolvió, y actualizar el
documento de origen si lo hay.

Última revisión: 2026-09-27.

## Índice

1. [Despliegue en la entidad (infra / DBA)](#1-despliegue-en-la-entidad-infra--dba)
2. [Repositorios y estructura](#2-repositorios-y-estructura)
3. [Preguntas de negocio](#3-preguntas-de-negocio)
4. [Casos de uso sin implementar](#4-casos-de-uso-sin-implementar)
5. [Motor de procesos (Flowable)](#5-motor-de-procesos-flowable)
6. [Front](#6-front)
7. [Calidad, pipeline y deuda técnica](#7-calidad-pipeline-y-deuda-técnica)

---

## 1. Despliegue en la entidad (infra / DBA)

Bloquean el primer despliegue real de `backend-srv`. Detalle en `backend-srv-config/README.md`.

- [ ] **P-01 · DDL del esquema de base de datos.** Con `JPA_DDL_AUTO=validate` y
  `FLOWABLE_DB_SCHEMA_UPDATE=false`, las tablas tienen que existir antes de desplegar, creadas
  con el usuario dueño del esquema (el `*_POOL` no tiene permisos de DDL). No hay migraciones.
  Decidir: scripts DDL entregados al DBA o Flyway/Liquibase. Si se eligen scripts, definir
  cómo se generan y cómo se mantienen al día con cada cambio de entidad JPA.
  *Destraba:* equipo + DBA.
- [ ] **P-02 · Esquema de Flowable en Oracle.** Un usuario/esquema separado, con grants
  cruzados hacia el `*_POOL`. Confirmar además si Flowable necesita `databaseTablePrefix` para
  leer un esquema distinto al del usuario de conexión.
  *Destraba:* DBA.
- [ ] **P-03 · Completar los valores `<...>` de los overlays.** `DB_SCHEMA`,
  `FLOWABLE_DB_SCHEMA` y `GATEWAY_URL` en todos los clusters, y `DB_URL` de tst, ppd y
  ocp-dev-dga. *Destraba:* infra + DBA.
- [ ] **P-04 · Route pública de `backend-srv`.** `route.enabled: true` lo publica fuera del
  cluster, pero `backend-srv` no tiene seguridad propia: confía en el header `X-Usuario` que
  pone api-gateway. Con la Route abierta cualquiera puede llamarlo sin token y hacerse pasar
  por otro usuario. Hoy la usa `verificacion.baseUrl` (karate-verify). Decidir: deshabilitar,
  restringir, o verificar a través de api-gateway. *Destraba:* infra + seguridad.
- [ ] **P-05 · Recursos y probes.** `limits.memory: 512Mi` y la `startupProbe` (~65 s) son los
  de la plantilla; Spring Boot + Hibernate + Flowable puede superar ambos. Medir en el primer
  despliegue. *Destraba:* equipo.
- [ ] **P-06 · Despliegue de api-gateway en la entidad (nuevo).** No existe repo Gerrit ni
  `api-gateway-config`. Cuando exista, debe fijar `BACKEND_SRV_URL=http://backend-srv` (Service,
  puerto 80), las variables de Keycloak del ambiente y su propia Route. Hasta entonces
  `GATEWAY_URL` de `backend-srv` no tiene valor real. *Destraba:* equipo + infra.
- [ ] **P-07 · Keycloak y realm de la entidad (nuevo).** api-gateway valida contra el realm
  local `siip-api`; el chart de la plantilla apunta a `MHINTERNO`. Falta definir con qué
  realm, cliente y roles va a autenticar SIIP en la entidad, y si los 13 roles de
  `realm-export.json` existen allí. *Destraba:* infra + seguridad.
- [ ] **P-08 · Pilares del marco DINAFI (nuevo).** `backend-srv` no integra
  configuración externa, autorización por permisos, auditoría remota ni logger remoto (audita
  en su propia base). Confirmar con la entidad si eso es aceptable o si alguno es obligatorio.
  *Destraba:* entidad.

## 2. Repositorios y estructura

- [ ] **P-12 · Restos de la plantilla y del nombre viejo (nuevo).**
  - `backend-srv/hs_err_pid45360.log` (volcado de un crash de la JVM) está versionado en el
    repo de la entidad: borrarlo y agregar `hs_err_pid*.log` al `.gitignore`.
  - `backend-srv/HELP.md` y `api-gateway/HELP.md` son el archivo genérico de Spring Initializr.
  - `Chart.yaml` dice `description: A Helm chart for Kubernetes` y `appVersion: "1.16.0"`.
  - `.vscode/launch.json` usa `"projectName": "back"`, y la configuración "BackApplication"
    usa variables que la app ya no lee (`DB_SERVER`, `DB_NAME`).
  - Comentario `back:8081` en `front/src/api/preinversionApi.ts:43`.
  *Destraba:* equipo.
- [ ] **P-36 · Copias de contratos y `.feature` desfasadas entre `backend-srv` y `front` (nuevo).**
  Detectado al cerrar P-35:
  - `.yaml` distintos: `CU-PRE-18` y `CU-PRE-20` (el back corrigió el de CU-PRE-20 el 27-sep).
  - `.feature` distintos: `CU-PRE-02-desarchivar-solicitud`, `CU-PRE-14`, `CU-PRE-15`, `CU-PRE-16`.
  - 12 `.feature` de `front/features/` no existen con ese nombre en el back (CU-ADM-04 ×3,
    CU-PRE-3.5 ×2, CU-PRE-20, 24, 29, 30, 31, 32, 33): revisar si el back los renombró o si faltan.
  Definir cuál es la versión buena, copiarla y regenerar el cliente. *Destraba:* equipo.

## 3. Preguntas de negocio

Preguntas enviadas a negocio y todavía sin respuesta. Las respuestas van a
`docs/decisiones-negocio.md` como una DN nueva.

- [ ] **P-13 · CU-PRE-18, 20, 21, 21.5, 22.1 y 23.** 21 preguntas (fórmulas de proyección, VAN
  y TIR, factor de corrección, roles, catálogos en conflicto). Detalle en
  `docs/CU-PRE-18-23-preguntas-negocio.md`. Las 5 características de CU-PRE-18 siguen en
  `@wip` a la espera de estas respuestas.
- [ ] **P-14 · Proyectos de emergencia → Viabilidad.** Preguntas enviadas el 2026-09-21:
  1. ¿El botón "Enviar a viabilidad" reemplaza el envío automático actual al guardar la ficha,
     o solo lo hace visible?
  2. ¿El pedido incluye la pantalla y el servicio de Viabilidad (CU-PRE-24)?
  3. ¿Hay que impedir que un proyecto de emergencia entre a la ruta normal de preinversión, o
     alcanza con el aislamiento actual (`listarEtapas()`)?
  4. ¿Se refleja en el BPMN con una rama propia?

  Hoy el botón aparece desactivado en `FichaEmergenciaPage.tsx` porque CU-PRE-03.5 no expone
  el envío. CU-PRE-24 ya tiene contrato y `ViabilidadController`: confirmar si eso responde la
  pregunta 2.
- [ ] **P-15 · Conflictos C2, C5 y C7 del BPMN.** `Proceso_SIIF.bpmn20.xml` los marca como no
  documentados: C5, sin botón ni pantalla para "Solicitar Viabilidad"; C2, "En elegibilidad"
  sin entrada ni salida propia en CU-PRE-25; C7, transición "Avanzar a Ejecución" no confirmada.
- [ ] **P-16 · Tablas de CU-PRE-24.** Los Anexos A.4 y A.5 de CU-PRE-17 que usa CU-PRE-24 no
  tienen estructura definida (`CU-PRE-24.openapi.yaml`, líneas 424 y 431).
- [ ] **P-17 · Contenido de CU-ADM-02.** Un endpoint de catálogos está bloqueado hasta que
  negocio confirme cuál versión del anexo Excel está vigente (subcriterios y ponderaciones).
- [ ] **P-18 · Escenarios que no se pudieron escribir.** 66 comentarios "⚠️ Escenario
  pendiente" en los `.feature` de 24 CU (CU-ADM-04, y de CU-PRE-01 a CU-PRE-33). La mayoría
  son textos de ayuda "?" o mensajes de validación que el CU no transcribe; otros son reglas
  sin dato verificable. Conviene revisarlos, agruparlos por tipo y pedirle a negocio en un
  solo envío lo que falte. Listado:
  `grep -rn "Escenario pendiente" backend-srv/src/test/resources/features`.

## 4. Casos de uso sin implementar

- [ ] **P-19 · CU con documento pero sin contrato OpenAPI:** CU-PRE-21, CU-PRE-21.5 y
  CU-PRE-22.1 (bloqueados en parte por P-13).
- [ ] **P-20 · CU referenciados sin documento en `docs/casos-de-uso/`:** CU-PRE-3.6 (Ficha
  del proyecto), CU-PRE-23, CU-PRE-25 (Elegibilidad), CU-PRE-26 (Opinión Técnica), CU-PRE-26.5
  (Priorización / Post-OT). Tampoco están en `docs/` los documentos de CU-ADM-02 a 04 ni de
  CU-PRE-16, aunque sus contratos existen.
- [ ] **P-21 · Ficha del proyecto (CU-PRE-3.6).** Sin endpoint ni servicio. Tiene en `@wip`
  3 escenarios de CU-PRE-29 (ver y descargar la ficha en PDF y Excel).
- [ ] **P-22 · Revisar el `@wip` de CU-PRE-30 "avanzar a programación de metas" (nuevo).** El
  comentario dice que CU-PRE-31 no está implementado, pero ya existe `CU-PRE-31.openapi.yaml`.
  Ver si el escenario ya se puede activar.
- [ ] **P-23 · Opinión Técnica en el front.** `OpinionTecnicaEntradaPage` muestra "todavía no
  está disponible".
- [ ] **P-24 · Módulos sin pantalla.** Priorización, PRIPME, PAIP, actualizaciones de
  ejecución, seguimiento (proyectos, PAP, PAIP), convenios, reportes y seguridad:
  `PLACEHOLDER_PATHS` en `front/src/App.tsx`.

## 5. Motor de procesos (Flowable)

- [ ] **P-25 · Alcance de Flowable.** Decidido posponerlo a la implementación de CU-PRE-3.5.
  Hoy Flowable solo registra: nada lee sus tareas ni sus `candidateGroups`, y el proceso se
  cancela al emitir el CUP. Decidir si pasa a ser el motor real de bandejas y tareas o se queda
  solo en el tramo de CU-PRE-01. Mientras tanto, no conectar más transiciones.
- [ ] **P-26 · Listener `etapaEnCurso`.** Si se adopta el BPMN por etapas
  (`docs/casos-de-uso/1 - Preinversion/DRAFT-Proceso_SIIF-formulacion-por-etapas.bpmn20.xml`),
  hace falta el listener que setea `etapaEnCurso` para `gw_retorno_elegibilidad`. No existe en
  `bpm/listeners/`. Depende de P-25.
- [ ] **P-27 · `sf15_reenviar_ot` del proceso desplegado.** Hoy vuelve directo a
  `UT_RevisionOT` sin pasar por Formulación. El borrador por etapas lo corrige, pero el proceso
  real no. Decidir si se ajusta.

## 6. Front

- [ ] **P-28 · Migración a la plantilla del MH (`siip2/frontend-ui`).** Piloto CU-PRE-01
  escrito, pero **nunca compilado ni probado** (hace falta acceso al Nexus del MH para
  `shared-lib-react`). Luego: registrar el recurso `preinversion-proyectos` en
  authorization-service, conectar `apiPreinversionUrl` al backend real, migrar el resto de los
  módulos y decidir cuándo se retira `siip/front`.
- [ ] **P-29 · Puente del pipeline (`scripts.build`).** El `echo pipeline-mh-rama-react-next…`
  se queda hasta que la plataforma acepte Vite en la rama React de `nodejs-dependency-test`.
- [ ] **P-30 · Otros pendientes de `front/README.md`.** Cliente generado sin chequeo en CR,
  carpeta de salida `dist/` frente a `out/`, conteo de pruebas en Sonar (falta reporter de
  Vitest), unos 290 hallazgos de stylelint, `npm run lint` roto (falta ESLint) e íconos desde
  CDN (impiden una CSP `'self'`).

## 7. Calidad, pipeline y deuda técnica

- [ ] **P-31 · Contratos duplicados a mano.** Los `.feature` y `.openapi.yaml` se copian a mano
  entre `backend-srv` y `front`, sin chequeo automático. Con los repos separados el riesgo de
  que se desincronicen crece. Evaluar un chequeo en CR o una fuente única.
- [ ] **P-32 · Reglas de Sonar a confirmar con el MH.** Los últimos commits de `backend-srv`
  alternan mayúsculas y minúsculas en SQL por una regla ("regla confusa de sonarqube revisar
  con el MH"). Hay cambios sin commit en `sonar/reglas-entidad.json`.
- [ ] **P-33 · Imágenes solo `amd64`.** El tester usa Mac; si Rosetta da problemas, publicar
  multi-arquitectura (`docker buildx --platform linux/amd64,linux/arm64`).
