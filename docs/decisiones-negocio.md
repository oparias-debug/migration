# Decisiones de negocio

Decisiones confirmadas por negocio que resuelven ambigüedades o contradicciones de los documentos
de casos de uso. Prevalecen sobre el texto del CU y sobre resoluciones anteriores cuando las
contradicen. Cada decisión indica dónde está implementada.

| ID | Fecha | Tema | CU afectados |
|----|-------|------|--------------|
| DN-01 | 2026-09-24 | Definición de "Usuario interno" / "usuario central" | CU-PRE-17, CU-PRE-18, CU-PRE-20, CU-PRE-21 |
| DN-02 | 2026-09-24 | Alcance del redondeo RN04 en CU-PRE-20 | CU-PRE-20 |

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
(`back/src/main/java/sv/gob/mh/siip/security/ActorContexto.java`), que ya usan
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
