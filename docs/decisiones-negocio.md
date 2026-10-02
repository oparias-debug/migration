# Decisiones de negocio

Decisiones confirmadas por negocio que resuelven ambigüedades o contradicciones de los documentos
de casos de uso. Prevalecen sobre el texto del CU y sobre resoluciones anteriores cuando las
contradicen. Cada decisión indica dónde está implementada.

| ID | Fecha | Tema | CU afectados |
|----|-------|------|--------------|
| DN-01 | 2026-09-24 | Definición de "Usuario interno" / "usuario central" | CU-PRE-17, CU-PRE-18, CU-PRE-20, CU-PRE-21 |
| DN-02 | 2026-09-24 | Alcance del redondeo RN04 en CU-PRE-20 | CU-PRE-20 |
| DN-03 | 2026-10-01 | Proyectos de emergencia sin Ruta de Preinversión | CU-PRE-03.5, CU-PRE-24 |

---

## DN-01 — "Usuario interno" es cualquier rol distinto de Técnico URP

**Contexto.** Varios CU restringen la visibilidad de los precios ajustados, el FC y el valor de
rescate ajustado a "usuarios internos", "usuarios centrales" o "actores internos". RQ-C-03 los
había definido como "cualquier usuario del Ministerio de Hacienda, independientemente de su rol",
pero el sistema no tiene un dato confiable para saber si un usuario pertenece al Ministerio. Cada
CU terminó aplicando un criterio distinto:

- CU-PRE-18: usuario sin Institución asignada.
- CU-PRE-20: Institución cuyo código empieza con `MH-`. Con los usuarios de desarrollo, esto hacía
  que el Técnico URP (adscrito a `MH-DGICP`) viera los precios ajustados y el Técnico PRE no, es
  decir, lo contrario de lo esperado.

**Decisión.** Un usuario es interno si su rol **no** es `TECNICO_URP`. La Institución y la Unidad
Ejecutora del usuario no se tienen en cuenta. Reemplaza a RQ-C-03 como criterio de implementación.

**Consecuencias.**

- El Técnico URP nunca recibe precios ajustados, FC ni valor de rescate ajustado. El servidor
  devuelve esos campos en `null`, incluso en la respuesta de sus propias operaciones de registro.
- El Técnico PRE y cualquier otro rol sí los reciben.
- "Usuarios internos", "usuarios centrales" y "actores internos" son el mismo grupo.

**Implementación.** Un único criterio en `ActorContexto.esUsuarioInterno(Usuario)`
(`backend-srv/src/main/java/sv/gob/mh/siip/security/ActorContexto.java`), que ya usan
`PresupuestoOmService` (CU-PRE-18) y `BeneficiosProyectoService` (CU-PRE-20). CU-PRE-17 y CU-PRE-21
todavía no ocultan campos por este motivo; cuando lo hagan, deben usar el mismo método.

---

## DN-02 — RN04 de CU-PRE-20 se aplica solo a los totales

**Contexto.** RN04 de CU-PRE-20 pide redondear hacia arriba, a múltiplos de 5 o de 10, "las
casillas de la tabla Montos por período del Anexo A.2". El ejemplo del mismo documento (mockup
A.2, reproducido en la BDD) muestra valores por período sin redondear: 882.00 a precios de mercado
y 970.20 a precios ajustados. Con RN04 aplicado a cada casilla serían 885 y 975.

**Decisión.** El redondeo se aplica solo a las filas de totales por período de la pantalla
"Beneficios del proyecto" (Anexo A.1): "Flujo de beneficios (precios de mercado)" y "Flujo de
beneficios (precios ajustados)". Es el mismo criterio que usa CU-PRE-18 para sus totales. Los
montos por período de cada beneficio conservan 2 decimales.

**Ejemplos.**

| Total calculado | Total mostrado |
|-----------------|----------------|
| 1,750,427.58 | 1,750,430.00 |
| 1,750,423.58 | 1,750,425.00 |
| 882.00 | 885.00 |

**Implementación.** `BeneficiosProyectoService.respuesta` redondea
`flujoBeneficiosPrecioMercadoPorPeriodo` y `flujoBeneficiosPrecioAjustadoPorPeriodo`. Lo cubren los
escenarios BDD de RN04 en `CU-PRE-20-guardar-avanzar.feature`.

---

## DN-03 — Un proyecto de emergencia no tiene Ruta de Preinversión

**Contexto.** CU-PRE-03.5 lleva al proyecto de emergencia por un camino propio: en Registro de
Etapas solo muestra "Perfil" (FA-05, paso 5.1), y al guardar la Ficha de proyectos de emergencia
(Anexo A.4) lo remite a Viabilidad (paso 5.5). Pero el CU no dice si ese proyecto puede, además,
generar, aceptar o modificar una Ruta de Preinversión como un proyecto normal. El sistema no lo
impedía: el aislamiento dependía solo de lo que mostraba la pantalla. Se preguntó a negocio el 2026-09-21.

**Decisión.** Un proyecto de emergencia no entra a la ruta normal de preinversión. Al guardar la
Ficha de proyectos de emergencia pasa directamente a Viabilidad (CU-PRE-24).

**Consecuencias.**

- Para un proyecto con `esProyectoEmergencia = true`, generar, aceptar y modificar la Ruta de
  Preinversión responden 409 con código `PROYECTO_EMERGENCIA_SIN_RUTA`.
- Su Registro de Etapas sigue mostrando solo "Perfil", que es el acceso a la ficha.

**Implementación.** `SeleccionEtapasRuta.exigirQueNoSeaDeEmergencia`
(`backend-srv/src/main/java/sv/gob/mh/siip/model/preinversion/service/SeleccionEtapasRuta.java`),
llamado desde `generar`, `aceptar` y `modificar`. Lo cubre el escenario "Un proyecto de emergencia no
tiene Ruta de Preinversión (DN-03)" de `CU-PRE-3.5-registrar-ficha-emergencia.feature`.
