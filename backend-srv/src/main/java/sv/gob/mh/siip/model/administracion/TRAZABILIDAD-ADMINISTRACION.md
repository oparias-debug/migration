# Trazabilidad — Módulo Administración e Interfaces (SIIP)

## ⚠️ Advertencia — módulo 100% especulativo

A diferencia de los 5 módulos anteriores (Preinversión, Programación, Ejecución y Seguimiento,
Operación y Mantenimiento, Convenios), **no existe ningún caso de uso documentado** para
Administración (`CU-ADM-xx`) ni Interfaces (`CU-ITF-xx`) en el corpus `use_cases-unificado.md`
recibido (61 casos de uso, ninguno con esos prefijos).

Este esquema se construyó **infiriendo** necesidades a partir de:
- El nombre de los módulos en el WBS: "M-00 — Administración" y "M-15 — Interfaces".
- Referencias sueltas a `CU-ADM-01`, `CU-ADM-02`, `CU-ADM-04`, `CU-ADM-13` y `CU-ITF-01` que
  aparecen mencionadas *dentro* de otros casos de uso ya procesados, sin que existan como
  documentos propios.
- Patrones repetidos en los 5 módulos ya entregados (roles de usuario, calendario de
  apertura/cierre, historiales de auditoría) que sugieren una necesidad transversal.

**No se debe implementar este módulo sin validación previa del equipo de negocio.** Si en algún
momento consigues las fichas reales de estos casos de uso, se debe repetir el proceso de análisis
(Pasos 1-4 del agente) igual que con los otros módulos, y probablemente se descartará o ajustará
buena parte de lo propuesto aquí.

## Tabla de trazabilidad (con origen inferido, no un CU real)

| Entidad JPA | Tabla Oracle | Inferido de |
|---|---|---|
| `Rol` | `ROL` | Mención de "CU-ADM-01 Starter de programación y seguridad" en el WBS |
| `ModuloSistema` | `MODULO_SISTEMA` | Necesidad de agrupar `Permiso` |
| `Permiso` | `PERMISO` | Mención de "seguridad" en CU-ADM-01 |
| `RolPermiso` | `ROL_PERMISO` | Necesidad de matriz rol-permiso |
| `SesionUsuario` | `SESION_USUARIO` | Práctica estándar de trazabilidad de acceso, no mencionada explícitamente |
| `ParametroSistema` | `PARAMETRO_SISTEMA` | CU-PRE-01 RN4 (3 meses + 5 días hábiles hardcodeado); mención de "CU-ADM-02 Catálogos de tablas básicas" |
| `CalendarioEvento` | `CALENDARIO_EVENTO` | Referencias sueltas a "CU-ADM-04" en CU-PRE-01, CU-EJE-01/05/10, CU-PRO-07/21/25 |
| `LogAuditoria` | `LOG_AUDITORIA` | Menciones sueltas de "Respaldo/Histórico de información" en varios CU (CU-PRO-21, CU-EJE-05/10) |
| `SistemaExterno` | `SISTEMA_EXTERNO` | Mención de "CU-ITF-01 Interfaz con SIAF" en el WBS (M-15) |
| `LogIntegracionExterna` | `LOG_INTEGRACION_EXTERNA` | Igual que arriba |

## Decisiones de diseño y conflictos con módulos ya entregados

1. **`Rol` (tabla) vs. `RolUsuario` (enum, módulo Preinversión)**: son dos representaciones
   distintas del mismo concepto. `RolUsuario` ya está en producción conceptual en `Usuario.rol`
   en 5 módulos. **No se modificó** `Usuario` para apuntar a `Rol` (evitar romper lo ya
   entregado). Si se decide adoptar `Rol` como catálogo real, el camino recomendado es:
   - Migrar los valores del enum a filas de `ROL`.
   - Cambiar `Usuario.rol` de `@Enumerated(EnumType.STRING)` a `@ManyToOne Rol`.
   - Esto es un cambio de ruptura (breaking change) para los 5 módulos ya generados.

2. **`CalendarioEvento` vs. `PeriodoProgramacionPripme` / `PeriodoProgramacionPaip` /
   `PeriodoProgramacionPap`** (ya creados en Programación y Ejecución): son conceptualmente
   la misma cosa (ventanas de apertura/cierre) modeladas de forma independiente porque en su
   momento no existía este catálogo central. **No se consolidaron** por la misma razón que el
   punto anterior. Recomendación: si se valida `CalendarioEvento`, migrar esas 3 tablas para
   que referencien `CALENDARIO_EVENTO` en vez de tener su propia estructura de año/período/estado.

3. **`LogAuditoria` vs. `HistorialCierre`** (módulo Ejecución): `HistorialCierre` es específico
   del proceso de cierre de proyecto y tiene su propia semántica de negocio (usuario, fecha,
   observaciones). `LogAuditoria` es una bitácora CRUD genérica de más bajo nivel. Pueden
   coexistir sin conflicto — no es necesario elegir entre ellas.

4. **`Rol` para autorización de excepción DGICP** (pendiente desde el módulo Operación y
   Mantenimiento, CU-OYM-01 RN08): con este catálogo de `Rol`/`Permiso` configurable, ya sería
   posible crear un rol `DGICP_JEFATURA` sin tocar el enum `RolUsuario`. Esto resuelve
   parcialmente ese supuesto pendiente, aunque requiere decidir el punto 1 primero.

## Qué falta para cerrar este módulo con confianza

- Las fichas reales de `CU-ADM-01`, `CU-ADM-02`, `CU-ADM-04`, `CU-ADM-13`.
- La ficha real de `CU-ITF-01` (Interfaz con SIAF) — sin ella, `SistemaExterno` y
  `LogIntegracionExterna` son solo un placeholder genérico de bitácora de integración,
  sin conocer los campos reales que SIAF espera intercambiar (montos, códigos presupuestarios,
  frecuencia de sincronización, dirección del flujo de datos, etc.).
